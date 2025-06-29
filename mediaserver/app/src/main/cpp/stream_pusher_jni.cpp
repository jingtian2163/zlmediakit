#include <jni.h>
#include <string>
#include <android/log.h>
#include "stream_pusher.h"

#undef LOG_TAG
#define LOG_TAG "StreamPusherJNI"
#undef LOGI
#undef LOGE
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

// 创建推流器实例
JNIEXPORT jlong JNICALL
Java_com_example_zlmediakit_H264Encoder_nativeCreatePusher(JNIEnv *env, jobject /* this */) {
    LOGI("nativeCreatePusher: 开始创建推流器实例");
    
    try {
        StreamPusher* pusher = new StreamPusher();
        if (pusher) {
            LOGI("nativeCreatePusher: 推流器实例创建成功, 地址: %p", pusher);
            return reinterpret_cast<jlong>(pusher);
        } else {
            LOGE("nativeCreatePusher: 推流器实例创建失败");
            return 0;
        }
    } catch (const std::exception& e) {
        LOGE("nativeCreatePusher: 异常: %s", e.what());
        return 0;
    }
}

// 初始化推流器
JNIEXPORT jboolean JNICALL
Java_com_example_zlmediakit_H264Encoder_nativeInitPusher(JNIEnv *env, jobject /* this */, 
                                                         jlong pusherPtr, jstring rtspUrl, 
                                                         jint width, jint height, jint fps) {
    LOGI("nativeInitPusher: 开始初始化推流器");
    
    if (pusherPtr == 0) {
        LOGE("nativeInitPusher: 推流器指针为空");
        return JNI_FALSE;
    }
    
    StreamPusher* pusher = reinterpret_cast<StreamPusher*>(pusherPtr);
    if (!pusher) {
        LOGE("nativeInitPusher: 无效的推流器指针");
        return JNI_FALSE;
    }
    
    // 转换Java字符串为C++字符串
    const char* urlCStr = env->GetStringUTFChars(rtspUrl, 0);
    if (!urlCStr) {
        LOGE("nativeInitPusher: 获取URL字符串失败");
        return JNI_FALSE;
    }
    
    std::string url(urlCStr);
    env->ReleaseStringUTFChars(rtspUrl, urlCStr);
    
    LOGI("nativeInitPusher: URL=%s, 分辨率=%dx%d, 帧率=%d", 
         url.c_str(), (int)width, (int)height, (int)fps);
    
    // 调用推流器初始化
    bool result = pusher->init(url.c_str(), (int)width, (int)height, (int)fps);
    
    if (result) {
        LOGI("nativeInitPusher: 推流器初始化成功");
    } else {
        LOGE("nativeInitPusher: 推流器初始化失败");
    }
    
    return result ? JNI_TRUE : JNI_FALSE;
}

// 推送视频帧
JNIEXPORT jboolean JNICALL
Java_com_example_zlmediakit_H264Encoder_nativePushFrame(JNIEnv *env, jobject /* this */, 
                                                        jlong pusherPtr, jbyteArray data, 
                                                        jint size, jboolean isKeyFrame) {
    if (pusherPtr == 0) {
        LOGE("nativePushFrame: 推流器指针为空");
        return JNI_FALSE;
    }
    
    StreamPusher* pusher = reinterpret_cast<StreamPusher*>(pusherPtr);
    if (!pusher) {
        LOGE("nativePushFrame: 无效的推流器指针");
        return JNI_FALSE;
    }
    
    if (!data || size <= 0) {
        LOGE("nativePushFrame: 无效的数据参数, size=%d", (int)size);
        return JNI_FALSE;
    }
    
    // 获取Java字节数组数据
    jbyte* dataPtr = env->GetByteArrayElements(data, 0);
    if (!dataPtr) {
        LOGE("nativePushFrame: 获取字节数组数据失败");
        return JNI_FALSE;
    }
    
    // 推送帧数据
    bool result = pusher->pushFrame(reinterpret_cast<const uint8_t*>(dataPtr), 
                                   (int)size, (bool)isKeyFrame);
    
    // 释放Java字节数组
    env->ReleaseByteArrayElements(data, dataPtr, JNI_ABORT);
    
    if (!result) {
        LOGE("nativePushFrame: 推送帧失败 (大小:%d, 关键帧:%s)", 
             (int)size, isKeyFrame ? "是" : "否");
    }
    
    return result ? JNI_TRUE : JNI_FALSE;
}

// 销毁推流器实例
JNIEXPORT void JNICALL
Java_com_example_zlmediakit_H264Encoder_nativeDestroyPusher(JNIEnv *env, jobject /* this */, 
                                                            jlong pusherPtr) {
    LOGI("nativeDestroyPusher: 开始销毁推流器实例");
    
    if (pusherPtr == 0) {
        LOGE("nativeDestroyPusher: 推流器指针为空");
        return;
    }
    
    StreamPusher* pusher = reinterpret_cast<StreamPusher*>(pusherPtr);
    if (pusher) {
        LOGI("nativeDestroyPusher: 销毁推流器实例, 地址: %p", pusher);
        delete pusher;
    } else {
        LOGE("nativeDestroyPusher: 无效的推流器指针");
    }
}

} // extern "C" 