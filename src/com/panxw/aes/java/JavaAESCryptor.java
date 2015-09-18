package com.panxw.aes.java;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.panxw.aes.jni.AESCryptor;

//使用256位长度密钥需查看的链接·
//http://czj4451.iteye.com/blog/1986483
//http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
//http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
public class JavaAESCryptor {

    private static final String TEST_DATA = "kLNyk5O9jj0kG/lqskHCLs7HQttQjqMwNToSXGVs7WraXf0bVpBA1vaE+30Mz2wu/6dmmU6mHOVAye+w9zrgZswPAjqEtU8nAa7+z5RDeil/5kBEEnV/IVO+Xry6YO4AL6xuHm/6k32zn6C8R2ZCvNL/vvUbk49YH5MEyCU/9See8Y8hqM9jPTmGc9+izcIjZtkMnC1PfShwvgdtE5gkkBqVJx20bnjyzEEPIb3dxt/DlhmbnpBeC6GWzCHjzvdLcC3mfYHoP6+A1r+oXjDxGFfKIDgtwaUZfzAKhlpsx9gOn7e2CaC85Nyu2Xy1vjTBlJiwN1EPvI87nQrWWqOBDyRRzhlbc+f2pEfZ6yIQKXnR7QKLKptxnD3jcKuH5r2l82b1Q3OSFTCYRCzYtA/CYbdJq4gRxx8bFwSeqmxtYy0=kLNyk5O9jj0kG/lqskHCLs7HQttQjqMwNToSXGVs7WraXf0bVpBA1vaE+30Mz2wu/6dmmU6mHOVAye+w9zrgZswPAjqEtU8nAa7+z5RDeil/5kBEEnV/IVO+Xry6YO4AL6xuHm/6k32zn6C8R2ZCvNL/vvUbk49YH5MEyCU/9See8Y8hqM9jPTmGc9+izcIjZtkMnC1PfShwvgdtE5gkkBqVJx20bnjyzEEPIb3dxt/DlhmbnpBeC6GWzCHjzvdLcC3mfYHoP6+A1r+oXjDxGFfKIDgtwaUZfzAKhlpsx9gOn7e2CaC85Nyu2Xy1vjTBlJiwN1EPvI87nQrWWqOBDyRRzhlbc+f2pEfZ6yIQKXnR7QKLKptxnD3jcKuH5r2l82b1Q3OSFTCYRCzYtA/CYbdJq4gRxx8bFwSeqmxtYy0=";

    // 测试
    public static void main(String[] args) throws Exception {
        // 加密
        System.out.println("加密前：" + TEST_DATA);
        byte[] encryptResult = encrypt(TEST_DATA.getBytes("UTF-8"));
        System.out.println("加密后："+AESCryptor.bytes2HexStr(encryptResult));

        // 解密
        byte[] decryptResult = decrypt(encryptResult);

        System.out.println("解密后：" + new String(decryptResult));
    }

    // iv同C语言中iv
    private static byte ivBytes[] = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04,
            0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

    // keyBytes同C语言中key
    private static byte keyBytes[] = new byte[] { 0x60, 0x3d, (byte) 0xeb,
            0x10, 0x15, (byte) 0xca, 0x71, (byte) 0xbe, 0x2b, 0x73,
            (byte) 0xae, (byte) 0xf0, (byte) 0x85, 0x7d, 0x77, (byte) 0x81,
            0x1f, 0x35, 0x2c, 0x07, 0x3b, 0x61, 0x08, (byte) 0xd7, 0x2d,
            (byte) 0x98, 0x10, (byte) 0xa3, 0x09, 0x14, (byte) 0xdf,
            (byte) 0xf4 };

    /**
     * 加密
     * 
     * @param content
     *            需要加密的内容
     * @param password
     *            加密密码
     * @return
     */
    public static byte[] encrypt(byte[] content) {
        return docrypt(content, keyBytes, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     * 
     * @param content
     *            待解密内容
     * @param password
     *            解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] content) {
        return docrypt(content, keyBytes, Cipher.DECRYPT_MODE);
    }

    public static byte[] docrypt(byte[] content, byte[] keyBytes, int mode) {
        try {
            // KeyGenerator kgen = KeyGenerator.getInstance("AES");
            // kgen.init(128, new SecureRandom(keyBytes));
            // SecretKey secretKey = kgen.generateKey();
            // byte[] enCodeFormat = secretKey.getEncoded();

            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES"); // keyBytes32个字节，256位，
                                                                    // 与C语言中的key一致
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
            final IvParameterSpec iv = new IvParameterSpec(ivBytes);

            cipher.init(mode, key, iv);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
