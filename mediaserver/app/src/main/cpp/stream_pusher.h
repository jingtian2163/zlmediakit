#include <iostream>
extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/mathematics.h"
#include "libavutil/time.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/opt.h>
#include <libavutil/imgutils.h>
#include <libswscale/swscale.h>
#include <libavdevice/avdevice.h>
#include <stdio.h>
#include <sys/time.h>
#include <unistd.h>
#include <string.h>
}

#include <iostream>
#include <chrono>
#include <iomanip>
#include <ctime>
#include <fstream>
#include <vector>
#include <cstdint>
#include <thread>
#include <android/log.h>
#define LOG_TAG "StreamPusher"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

class StreamPusher {
public:
    StreamPusher() : m_octx(nullptr), m_reusableBuffer(nullptr), m_bufferSize(0),
        m_frameCount(0), m_startTime(0), m_targetFps(30) {
    }
    ~StreamPusher() {
        uninit();
    }

    // 获取当前时间(微秒)
    uint64_t getCurrentTime() {
        struct timeval tv;
        gettimeofday(&tv, NULL);
        return tv.tv_sec * 1000000ULL + tv.tv_usec;
    }

    // 初始化推流器
    bool init(const char* rtmpUrl, int width = 1920, int height = 1080, int fps = 30) {
        LOGI("开始初始化推流器: URL=%s, %dx%d@%dfps", rtmpUrl, width, height, fps);
        m_targetFps = fps;  // 保存目标帧率
        
        // 初始化网络
        avformat_network_init();
        LOGI("FFmpeg网络初始化完成");

        // 创建输出上下文
        int ret = avformat_alloc_output_context2(&m_octx, NULL, "rtsp", rtmpUrl);
        if (ret < 0 || !m_octx) {
            char errbuf[AV_ERROR_MAX_STRING_SIZE];
            av_strerror(ret, errbuf, sizeof(errbuf));
            LOGE("创建输出上下文失败: %s (错误码: %d)", errbuf, ret);
            return false;
        }
        LOGI("输出上下文创建成功");

        // 创建视频流
        AVStream* stream = avformat_new_stream(m_octx, NULL);
        if (!stream) {
            LOGE("创建视频流失败");
            return false;
        }
        LOGI("视频流创建成功");

        // 设置编码参数
        stream->codecpar->codec_type = AVMEDIA_TYPE_VIDEO;
        stream->codecpar->codec_id = AV_CODEC_ID_H264;
        stream->codecpar->width = width;
        stream->codecpar->height = height;
        stream->codecpar->format = AV_PIX_FMT_YUV420P;
        stream->codecpar->bit_rate = 4000000;
        stream->time_base = { 1, m_targetFps };

        // 打开输出URL
        if (!(m_octx->oformat->flags & AVFMT_NOFILE)) {
            LOGI("开始打开输出URL: %s", rtmpUrl);
            ret = avio_open(&m_octx->pb, rtmpUrl, AVIO_FLAG_WRITE);
            if (ret < 0) {
                char errbuf[AV_ERROR_MAX_STRING_SIZE];
                av_strerror(ret, errbuf, sizeof(errbuf));
                LOGE("打开输出URL失败: %s (错误码: %d)", errbuf, ret);
                return false;
            }
            LOGI("输出URL打开成功");
        }

        // 写入头部信息
        LOGI("开始写入文件头");
        ret = avformat_write_header(m_octx, NULL);
        if (ret < 0) {
            char errbuf[AV_ERROR_MAX_STRING_SIZE];
            av_strerror(ret, errbuf, sizeof(errbuf));
            LOGE("写入文件头失败: %s (错误码: %d)", errbuf, ret);
            return false;
        }
        LOGI("文件头写入成功");

        // 初始化时间戳
        m_startTime = av_gettime();
        m_frameCount = 0;

        // 初始化可重用buffer
        m_bufferSize = 1024 * 1024; // 1MB初始大小
        m_reusableBuffer = (uint8_t*)av_malloc(m_bufferSize);
        if (!m_reusableBuffer) {
            return false;
        }

        return true;
    }

    // 推送一帧数据
    bool pushFrame(const uint8_t* data, int size, bool isKeyFrame) {
        if (!m_octx) {
            LOGE("推流器未初始化，无法推送帧");
            return false;
        }
        if (!data) {
            LOGE("数据指针为空，无法推送帧");
            return false;
        }
        if (size <= 0) {
            LOGE("数据大小无效: %d字节", size);
            return false;
        }

        // 打印前10帧的前80个字节的16进制数据
        if (m_frameCount < 10) {
            int printLength = (size > 80) ? 80 : size;
            std::string hexStr = "";
            for (int i = 0; i < printLength; i++) {
                char buf[4];
                sprintf(buf, "%02X ", data[i]);
                hexStr += buf;
                // 每16字节换行
                // if ((i + 1) % 16 == 0) {
                //     hexStr += "\n";
                // }
            }
            LOGI("第%ld帧数据 (前%d字节): 大小=%d, 关键帧=%s\n%s", 
                 (long)(m_frameCount + 1), printLength, size, isKeyFrame ? "是" : "否", hexStr.c_str());
        }

        // 确保buffer足够大
        if (size > m_bufferSize) {
            uint8_t* newBuffer = (uint8_t*)av_realloc(m_reusableBuffer, size);
            if (!newBuffer) {
                return false;
            }
            m_reusableBuffer = newBuffer;
            m_bufferSize = size;
        }

        // 复制数据到重用buffer
        memcpy(m_reusableBuffer, data, size);

        // 创建packet
        AVPacket* packet = av_packet_alloc();
        if (!packet) {
            LOGE("分配AVPacket失败，内存不足");
            return false;
        }

        // 设置packet数据
        packet->data = m_reusableBuffer;
        packet->size = size;
        packet->stream_index = 0;

        // 计算时间戳
        AVRational src_tb = { 1, m_targetFps }; // 假设60fps
        AVRational dst_tb = m_octx->streams[0]->time_base;
        //AVRational dst_tb = { 1, m_targetFps };
        //int64_t pts = av_rescale_q(m_frameCount, src_tb, dst_tb);
        int64_t pts = av_rescale_q(m_frameCount , src_tb, dst_tb);
        packet->pts = pts;
        packet->dts = pts;
        packet->duration = av_rescale_q(1, src_tb, dst_tb);
        packet->flags = isKeyFrame ? AV_PKT_FLAG_KEY : 0;

        // 控制帧率
        long long now = av_gettime() - m_startTime;
        long long pts_time = av_rescale_q(pts, dst_tb, { 1, AV_TIME_BASE });
        if (pts_time > now) {
            av_usleep(pts_time - now);
        }

        // 写入帧
        // LOGI("准备写入第%ld帧 (关键帧:%s, 大小:%d字节, PTS:%ld)", 
        //      (long)(m_frameCount + 1), isKeyFrame ? "是" : "否", size, (long)pts);
        
        int ret = av_interleaved_write_frame(m_octx, packet);
        if (ret < 0) {
            char errbuf[AV_ERROR_MAX_STRING_SIZE];
            av_strerror(ret, errbuf, sizeof(errbuf));
            LOGE("写入第%ld帧失败: %s (错误码: %d, 大小:%d, 关键帧:%s)", 
                 (long)(m_frameCount + 1), errbuf, ret, size, isKeyFrame ? "是" : "否");
            av_packet_free(&packet);
            return false;
        }
        
        //LOGI("第%ld帧写入成功", (long)(m_frameCount + 1));
        m_frameCount++;
        m_frameCountSecond++;

        // 修改打印日志的部分
        int64_t curTs = getCurrentTime();
        int64_t diff = curTs - lastStatsTime;
        if (diff  > 1000 * 1000 ) {
            auto now = std::chrono::system_clock::now();
            auto now_time = std::chrono::system_clock::to_time_t(now);
            double actual_fps = (double)m_frameCountSecond * 1000/(diff/1000);
            
            // 使用格式化时间字符串
            std::string timeStr = formatTime(now_time);
            LOGE("Time:%s, Frames:%ld, Target FPS:%d, Actual FPS:%.1f", 
                timeStr.c_str(),
                (long)m_frameCount,
                m_targetFps,
                actual_fps);
            m_frameCountSecond = 0;
            lastStatsTime= getCurrentTime();
        }
        
        av_packet_free(&packet);
        return ret >= 0;
    }

    // 清理资源
    void uninit() {
        if (m_octx) {
            av_write_trailer(m_octx);
            if (!(m_octx->oformat->flags & AVFMT_NOFILE)) {
                avio_closep(&m_octx->pb);
            }
            avformat_free_context(m_octx);
            m_octx = nullptr;
        }

        if (m_reusableBuffer) {
            av_free(m_reusableBuffer);
            m_reusableBuffer = nullptr;
        }

        m_bufferSize = 0;
        m_frameCount = 0;
    }

private:
    std::string formatTime(const time_t& time) {
        char buffer[32];
        struct tm* timeinfo = localtime(&time);
        strftime(buffer, sizeof(buffer), "%H:%M:%S", timeinfo);
        return std::string(buffer);
    }
    AVFormatContext* m_octx;
    uint8_t* m_reusableBuffer;
    int m_bufferSize;
    int64_t m_frameCount;
    int64_t m_frameCountSecond;
    int64_t m_startTime;
    int m_targetFps = 0;
    uint64_t lastStatsTime = 0;
};
