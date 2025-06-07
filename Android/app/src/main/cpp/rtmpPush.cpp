#include "rtmpPush.h"
#include <android/log.h>

#define LOG_TAG "RtspPusher"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using namespace std;
using namespace toolkit;
using namespace mediakit;

static RtspPusherWrapper* instance = nullptr;

RtspPusherWrapper* RtspPusherWrapper::getInstance() {
    if (!instance) {
        instance = new RtspPusherWrapper();
    }
    return instance;
}

RtspPusherWrapper::RtspPusherWrapper()
        : _is_pushing(false) {
    // 初始化日志系统
    Logger::Instance().add(std::make_shared<ConsoleChannel>());
    Logger::Instance().setWriter(std::make_shared<AsyncLogWriter>());

    // 创建事件轮询器
    _poller = EventPollerPool::Instance().getPoller();
}

RtspPusherWrapper::~RtspPusherWrapper() {
    stop();
}

bool RtspPusherWrapper::initPusher(const std::string& url) {
    if (_is_pushing) {
        return false;
    }

    _push_url = url;

    // 创建媒体源
    MediaTuple tuple;
    tuple.app = "live";
    tuple.stream = "stream";

    // 创建媒体源
    _media_source = std::make_shared<RtspMediaSourceHelper>(tuple);

    // 创建推流器
    _pusher = std::make_shared<MyRtspPusher>(_poller, _media_source);

    // 设置推流结果回调
    _pusher->setOnPublished([this](const SockException &ex) {
        if (!ex) {
            LOGI("推流成功");
            _is_pushing = true;
        } else {
            LOGE("推流失败: %s", ex.what());
            _is_pushing = false;
        }
    });

    // 设置断开回调
    _pusher->setOnShutdown([this](const SockException &ex) {
        LOGE("推流中断: %s", ex.what());
        _is_pushing = false;
    });

    // 开始推流
    _pusher->publish(_push_url);
    return true;
}

void RtspPusherWrapper::pushVideoFrame(uint8_t* data, int length, int64_t timestamp) {
    if (!_is_pushing) {
        return;
    }

    // 创建H264帧
    auto frame = std::make_shared<H264Frame>();

    // 设置帧类型
//    if (data[4] == 0x67) {
//        frame->setType(H264Frame::NAL_SPS);
//    } else if (data[4] == 0x68) {
//        frame->setType(H264Frame::NAL_PPS);
//    } else if (data[4] == 0x65) {
//        frame->setType(H264Frame::NAL_IDR);
//    } else {
//        frame->setType(H264Frame::NAL_B_P);
//    }

    // 设置数据和时间戳
    frame->_buffer.assign((char*)data, length);
    frame->_dts = timestamp;
    frame->_pts = timestamp;

    // 输入帧
    _media_source->inputFrame(frame);
}

void RtspPusherWrapper::stop() {
    if (_pusher) {
        _pusher->teardown();
        _pusher = nullptr;
    }
    _is_pushing = false;
}

void RtspPusherWrapper::release() {
    stop();
    if (instance) {
        delete instance;
        instance = nullptr;
    }
}

// 添加测试方法
void RtspPusherWrapper::testPushIFrame() {
    _is_pushing = true;
     if (!_is_pushing) {
         LOGE("推流未初始化");
         return;
     }

    // 构造一个模拟的H264 I帧
    // SPS
    uint8_t sps[] = {
        0x00, 0x00, 0x00, 0x01, 0x67, 0x42, 0x00, 0x1f, 
        0x96, 0x54, 0x0b, 0x24, 0x28, 0xc8, 0x00, 0x00, 
        0x03, 0x00, 0x04, 0x00, 0x00, 0x03, 0x00, 0xf0, 
        0x3c, 0x60, 0xc9, 0x20
    };
    
    // PPS
    uint8_t pps[] = {
        0x00, 0x00, 0x00, 0x01, 0x68, 0xce, 0x38, 0x80
    };
    
    // IDR帧
    uint8_t idr[] = {
        0x00, 0x00, 0x00, 0x01, 0x65, 0x88, 0x84, 0x00,
        0x10, 0xff, 0xfe, 0xf6, 0xf0, 0xfe, 0x05, 0x36,
        0x56, 0x04, 0x50, 0x96, 0x7b, 0x3f, 0x53, 0xe1
    };

    // 获取当前时间戳
    int64_t now = getCurrentMillisecond();

    // 推送SPS
    auto sps_frame = std::make_shared<H264Frame>();
    sps_frame->_buffer.assign((char*)sps, sizeof(sps));
    sps_frame->_dts = now;
    sps_frame->_pts = now;
    sps_frame->_prefix_size = 4; // NALU起始码长度
    _media_source->inputFrame(sps_frame);

    // 推送PPS
    auto pps_frame = std::make_shared<H264Frame>();
    pps_frame->_buffer.assign((char*)pps, sizeof(pps));
    pps_frame->_dts = now;
    pps_frame->_pts = now;
    pps_frame->_prefix_size = 4;
    _media_source->inputFrame(pps_frame);

    // 推送IDR帧
    auto idr_frame = std::make_shared<H264Frame>();
    idr_frame->_buffer.assign((char*)idr, sizeof(idr));
    idr_frame->_dts = now;
    idr_frame->_pts = now;
    idr_frame->_prefix_size = 4;
    _media_source->inputFrame(idr_frame);

    LOGI("测试帧推送完成");
}

// 获取当前毫秒时间戳
int64_t RtspPusherWrapper::getCurrentMillisecond() {
    return toolkit::getCurrentMillisecond();
}

//// JNI方法实现
//extern "C" {
//
//static RtspPusherWrapper* getPusher(JNIEnv* env, jobject thiz) {
//    return RtspPusherWrapper::getInstance();
//}
//
//JNIEXPORT jboolean JNICALL
//Java_com_example_rtsppusher_RtspPusher_native_1init(JNIEnv* env, jobject thiz, jstring url) {
//    const char* str = env->GetStringUTFChars(url, nullptr);
//    bool ret = getPusher(env, thiz)->initPusher(str);
//    env->ReleaseStringUTFChars(url, str);
//    return ret;
//}
//
//JNIEXPORT void JNICALL
//Java_com_example_rtsppusher_RtspPusher_native_1pushVideoFrame(JNIEnv* env, jobject thiz,
//        jbyteArray data, jint length, jlong timestamp) {
//jbyte* buffer = env->GetByteArrayElements(data, nullptr);
//getPusher(env, thiz)->pushVideoFrame((uint8_t*)buffer, length, timestamp);
//env->ReleaseByteArrayElements(data, buffer, JNI_ABORT);
//}
//
//JNIEXPORT void JNICALL
//Java_com_example_rtsppusher_RtspPusher_native_1stop(JNIEnv* env, jobject thiz) {
//getPusher(env, thiz)->stop();
//}
//
//JNIEXPORT void JNICALL
//Java_com_example_rtsppusher_RtspPusher_native_1release(JNIEnv* env, jobject thiz) {
//getPusher(env, thiz)->release();
//}
//
//} // extern "C"