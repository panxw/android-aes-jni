LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := aes_jni
LOCAL_SRC_FILES := main.c aes/aes.c

APP_OPTIM := release
OPT_CFLAGS := -O2 -fno-exceptions -fno-rtti
   
OPT_CPPFLAGS := $(OPT_CLFAGS)
  
APP_CFLAGS := $(APP_CFLAGS) $(OPT_CFLAGS)
#APP_CPPFLAGS := $(APP_CPPFLAGS) $(OPT_CPPFLAGS

include $(BUILD_SHARED_LIBRARY)