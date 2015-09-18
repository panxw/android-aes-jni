#include <stdio.h>
#include <jni.h>
#include <stdlib.h>
#include <android/log.h>
#include "aes/aes.h"

//CRYPT CONFIG
#define MAX_LEN (2*1024*1024)
#define ENCRYPT 0
#define DECRYPT 1
#define AES_KEY_SIZE 256
#define READ_LEN 10

#define TARGET_CLASS "com/panxw/aes/jni/AESCryptor"
#define TARGET_CRYPT "crypt"
#define TARGET_CRYPT_SIG "([BJI)[B"
#define TARGET_READ "read"
#define TARGET_READ_SIG "(Ljava/lang/String;J)[B"

//AES_IV
static unsigned char AES_IV[16] = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
		0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
//AES_KEY
static unsigned char AES_KEY[32] = { 0x60, 0x3d, 0xeb, 0x10, 0x15, 0xca, 0x71,
		0xbe, 0x2b, 0x73, 0xae, 0xf0, 0x85, 0x7d, 0x77, 0x81, 0x1f, 0x35, 0x2c,
		0x07, 0x3b, 0x61, 0x08, 0xd7, 0x2d, 0x98, 0x10, 0xa3, 0x09, 0x14, 0xdf,
		0xf4 };

/*
 * Class:     tv_fun_common_crypt_Funcrypt
 * Method:    sha1
 * Signature: (Ljava/lang/String;JI)[Ljava/lang/Object;
 */
JNIEXPORT jbyteArray JNICALL android_native_aes(JNIEnv *env, jclass clazz,
		jbyteArray jarray, jlong jtimestamp, jint jmode) {
	//check input data
	unsigned int len = (unsigned int) ((*env)->GetArrayLength(env, jarray));
	if (len <= 0 || len >= MAX_LEN) {
		return NULL;
	}

	unsigned char *data = (unsigned char*) (*env)->GetByteArrayElements(env,
			jarray, NULL);
	if (!data) {
		return NULL;
	}

	//计算填充长度，当为加密方式且长度不为16的整数倍时，则填充，与3DES填充类似(DESede/CBC/PKCS5Padding)
	unsigned int mode = (unsigned int) jmode;
	unsigned int rest_len = len % AES_BLOCK_SIZE;
	unsigned int padding_len = (
			(ENCRYPT == mode) ? (AES_BLOCK_SIZE - rest_len) : 0);
	unsigned int src_len = len + padding_len;

	//设置输入
	unsigned char *input = (unsigned char *) malloc(src_len);
	memset(input, 0, src_len);
	memcpy(input, data, len);
	if (padding_len > 0) {
		memset(input + len, (unsigned char) padding_len, padding_len);
	}
	//data不再使用
	(*env)->ReleaseByteArrayElements(env, jarray, data, 0);

	//设置输出Buffer
	unsigned char * buff = (unsigned char*) malloc(src_len);
	if (!buff) {
		free(input);
		return NULL;
	}
	memset(buff, src_len, 0);

	//set key & iv
	unsigned int key_schedule[AES_BLOCK_SIZE * 4] = { 0 }; //>=53(这里取64)
	aes_key_setup(AES_KEY, key_schedule, AES_KEY_SIZE);

	//执行加解密计算(CBC mode)
	if (mode == ENCRYPT) {
		aes_encrypt_cbc(input, src_len, buff, key_schedule, AES_KEY_SIZE,
				AES_IV);
	} else {
		aes_decrypt_cbc(input, src_len, buff, key_schedule, AES_KEY_SIZE,
				AES_IV);
	}

	//解密时计算填充长度
	if (ENCRYPT != mode) {
		unsigned char * ptr = buff;
		ptr += (src_len - 1);
		padding_len = (unsigned int) *ptr;
		if (padding_len > 0 && padding_len <= AES_BLOCK_SIZE) {
			src_len -= padding_len;
		}
		ptr = NULL;
	}

	//设置返回变量
	jbyteArray bytes = (*env)->NewByteArray(env, src_len);
	(*env)->SetByteArrayRegion(env, bytes, 0, src_len, (jbyte*) buff);

	//内存释放
	free(input);
	free(buff);

	return bytes;
}

JNIEXPORT jbyteArray JNICALL android_native_read(JNIEnv *env, jclass clazz,
		jstring jstr, jlong jtimestam) {
	char * path = (char *) (*env)->GetStringUTFChars(env, jstr, NULL);
	if (!path) {
		return NULL;
	}
	FILE *fp = fopen(path, "r"); //获取文件的指针
	if (!fp) {
		return NULL;
	}
	(*env)->ReleaseStringUTFChars(env, jstr, path);

	char pBuf[READ_LEN + 1] = { 0 };
	fread(pBuf, 1, READ_LEN, fp); //读文件
	pBuf[READ_LEN] = 0;
	fclose(fp);

	//设置返回变量
	jbyteArray bytes = (*env)->NewByteArray(env, READ_LEN);
	(*env)->SetByteArrayRegion(env, bytes, 0, READ_LEN, (jbyte*) pBuf);

	return bytes;
}

/**
 * 注册JNI
 */
static const JNINativeMethod gMethods[] = { { TARGET_CRYPT, TARGET_CRYPT_SIG,
		(void*) android_native_aes }, { TARGET_READ, TARGET_READ_SIG,
		(void*) android_native_read } };

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return -1;
	}

	jclass clazz = (*env)->FindClass(env, TARGET_CLASS);
	if (!clazz) {
		return -1;
	}
	//这里就是关键了，把本地函数和一个java类方法关联起来。不管之前是否关联过，一律把之前的替换掉！
	if ((*env)->RegisterNatives(env, clazz, gMethods,
			sizeof(gMethods) / sizeof(gMethods[0])) != JNI_OK) {
		return -1;
	}

	return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return;
	}
}
