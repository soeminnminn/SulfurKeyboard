LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/src

# LOCAL_SRC_FILES := \
	jni/com_android_inputmethod_skeyboard_WordCorrection.cpp \
    jni/com_android_inputmethod_skeyboard_BinaryDictionary.cpp \
    jni/jni_common.cpp \
	src/dictionary.cpp \
	src/char_utils.cpp \
	src/zawgyi_correction.cpp
	
 LOCAL_SRC_FILES := \
    com_android_inputmethod_skeyboard_BinaryDictionary.cpp \
	src/dictionary.cpp \
	src/char_utils.cpp 

LOCAL_NDK_VERSION := 8
LOCAL_SDK_VERSION := 8

LOCAL_MODULE := libjni_skeyboard

include $(BUILD_SHARED_LIBRARY)
