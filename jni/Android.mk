
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := hellojni
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := hellojni.c
LOCAL_C_INCLUDES :=$(JNI_H_INCLUDE) 
LOCAL_LDLIBS    := -llog  #more...

LOCAL_SHARED_LIBRARIES := liblog libcutils
LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)