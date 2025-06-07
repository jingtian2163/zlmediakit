#ifndef RTSP_PUSHER_H
#define RTSP_PUSHER_H

#include <jni.h>
#include <string>
#include <memory>
#include "Rtsp/RtspPusher.h"
#include "Rtsp/RtspMediaSource.h"
#include "Thread/semaphore.h"
#include "Util/logger.h"
#include "ext-codec/H264.h"

class RtspMediaSourceHelper : public mediakit::RtspMediaSource {
public:
    using Ptr = std::shared_ptr<RtspMediaSourceHelper>;

    RtspMediaSourceHelper(const mediakit::MediaTuple& tuple)
            : RtspMediaSource(tuple) {
        // 初始化SDP
        _sdp = "v=0\r\n"
               "o=- 0 0 IN IP4 127.0.0.1\r\n"
               "s=H264 Video Stream\r\n"
               "c=IN IP4 0.0.0.0\r\n"
               "t=0 0\r\n"
               "m=video 0 RTP/AVP 96\r\n"
               "a=rtpmap:96 H264/90000\r\n"
               "a=control:trackID=0\r\n";
    }

    void inputFrame(const mediakit::Frame::Ptr &frame) {
        // 创建RTP包
        auto rtp = mediakit::RtpPacket::create();
        rtp->type = frame->getTrackType();
        rtp->sample_rate = 90000; // H264使用90000采样率
        rtp->ntp_stamp = frame->dts();

        // 设置RTP头
        auto header = rtp->getHeader();
        header->pt = 96; // H264的payload type
        header->ssrc = htonl(0x12345678);
        header->seq = htons(getSeqence(frame->getTrackType()));
        header->stamp = htonl(frame->dts() * 90); // 转换为90kHz时钟

        // 拷贝数据
        memcpy(rtp->getPayload(), frame->data(), frame->size());

        // 写入RTP包
        onWrite(rtp, frame->keyFrame());
    }
};

class MyRtspPusher : public mediakit::RtspPusher {
public:
    MyRtspPusher(const toolkit::EventPoller::Ptr &poller, const mediakit::RtspMediaSource::Ptr &src)
            : RtspPusher(poller, src) {}

protected:
    void onPublishResult(const toolkit::SockException &ex) override {
        if(_publishCB) _publishCB(ex);
    }

    void onShutdown(const toolkit::SockException &ex) override {
        if(_shutdownCB) _shutdownCB(ex);
    }

public:
    using Event = std::function<void(const toolkit::SockException &ex)>;
    void setOnPublished(const Event &cb) { _publishCB = cb; }
    void setOnShutdown(const Event &cb) { _shutdownCB = cb; }

private:
    Event _publishCB;
    Event _shutdownCB;
};

class RtspPusherWrapper {
public:
    static RtspPusherWrapper* getInstance();
    bool initPusher(const std::string& url);
    void pushVideoFrame(uint8_t* data, int length, int64_t timestamp);
    void stop();
    void release();

    void testPushIFrame();
    int64_t getCurrentMillisecond() ;

private:
    RtspPusherWrapper();
    ~RtspPusherWrapper();

    std::shared_ptr<MyRtspPusher> _pusher;
    RtspMediaSourceHelper::Ptr _media_source;
    bool _is_pushing;
    std::string _push_url;
    toolkit::EventPoller::Ptr _poller;
};

#endif // RTSP_PUSHER_H