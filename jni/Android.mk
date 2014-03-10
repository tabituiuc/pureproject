LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := PURETest
LOCAL_SRC_FILES := PURETest.cpp

include $(BUILD_SHARED_LIBRARY)
