#include <jni.h>
#include <string>
#include "Util/logger.h"
#include "Thread/semaphore.h"
#include "Common/config.h"
#include "Player/MediaPlayer.h"
#include "Extension/Frame.h"

using namespace std;
using namespace toolkit;
using namespace mediakit;

#define JNI_API(retType, funName, ...) extern "C"  JNIEXPORT retType Java_com_zlmediakit_jni_ZLMediaKit_##funName(JNIEnv* env, jclass cls,##__VA_ARGS__)
extern int start_main(int argc,char *argv[]);
// 全局变量
static std::shared_ptr<std::thread> g_server_thread = nullptr;
static bool g_is_running = false;
static std::mutex g_mutex;
static pthread_t g_thread_id = 0;

// 工具函数
string stringFromJstring(JNIEnv *env, jstring jstr) {
    if (!env || !jstr) {
        return "";
    }
    const char *field_char = env->GetStringUTFChars(jstr, 0);
    string ret(field_char, env->GetStringUTFLength(jstr));
    env->ReleaseStringUTFChars(jstr, field_char);
    return ret;
}

// JNI 加载
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    Logger::Instance().add(std::make_shared<ConsoleChannel>());
    Logger::Instance().setWriter(std::make_shared<AsyncLogWriter>());
    return JNI_VERSION_1_6;
}

// JNI 卸载
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_thread_id) {
        pthread_kill(g_thread_id, SIGINT);
        g_thread_id = 0;
    }
    if (g_server_thread) {
        g_is_running = false;
        if (g_server_thread->joinable()) {
            g_server_thread->join();
        }
        g_server_thread = nullptr;
    }
}
extern "C" {

extern "C" JNIEXPORT void JNICALL
Java_com_zlmediakit_jni_ZLMediaKit_startServerWithConfig(
        JNIEnv *env, jclass clazz, jobject config) {

    jclass mapClass = env->GetObjectClass(config);
    jmethodID getMethod = env->GetMethodID(mapClass, "get",
                                           "(Ljava/lang/Object;)Ljava/lang/Object;");

    // 获取PEM文件路径
    jstring pemPath = (jstring)env->CallObjectMethod(config, getMethod,
                                                     env->NewStringUTF("ssl.cert_file"));

    const char *pemFile = env->GetStringUTFChars(pemPath, NULL);

    try {
        // 使用同一个PEM文件初始化SSL
        //SSL_Ctx_Init(pemFile, pemFile);

        // 设置日志
        Logger::Instance().add(std::make_shared<ConsoleChannel>());
        Logger::Instance().setWriter(std::make_shared<AsyncLogWriter>());

        // 加载配置
        string configPath = string(pemFile).substr(0,
                                                   string(pemFile).find_last_of('/'));


        // 设置端口
        uint16_t rtspPort = mINI::Instance()["rtsp"].as<uint16_t>();
        uint16_t rtmpPort = mINI::Instance()["rtmp"].as<uint16_t>();
        uint16_t httpPort = mINI::Instance()["http"].as<uint16_t>();

    } catch (exception &ex) {
        InfoL << "Server startup failed:" << ex.what();
    }

    env->ReleaseStringUTFChars(pemPath, pemFile);
}
    
// 启动服务器
JNIEXPORT jboolean JNICALL
Java_com_zlmediakit_jni_ZLMediaKit_startServer(JNIEnv* env, jclass cls, jstring ini_dir) {
    std::lock_guard<std::mutex> lock(g_mutex);

    // 检查服务器是否已经在运行
    if (g_is_running) {
        WarnL << "Server is already running";
        return false;
    }

    string sd_path = stringFromJstring(env, ini_dir);
    string ini_file = sd_path + "/zlmediakit.ini";
    string pem_file = sd_path + "/zlmediakit.pem";

    DebugL << "sd_path:" << sd_path;
    DebugL << "ini file:" << ini_file;

    g_is_running = true;
    g_server_thread = std::make_shared<std::thread>([sd_path, ini_file, pem_file]() {
        g_thread_id = pthread_self();
        try {
            // 配置服务器参数
            mINI::Instance()[Http::kRootPath] = sd_path + "/httpRoot";
            //mINI::Instance()[Protocol::kMP4SavePath] = sd_path + "/httpRoot";
            //mINI::Instance()[Protocol::kHlsSavePath] = sd_path + "/httpRoot";

            // 服务器配置
            mINI::Instance()["http.enable_hls"] = 0;
            mINI::Instance()["http.port"] = 8080;
            mINI::Instance()["http.sslport"] = 8443;
            mINI::Instance()["rtsp.port"] = 8554;
            mINI::Instance()["rtsp.sslport"] = 8332;
            mINI::Instance()["general.enableVhost"] = 0;
            mINI::Instance()["rtsp.lowLatency"] = 1;
            mINI::Instance()["rtsp.rtpMaxSize"] = 1024;

            // 替换hook地址
            for (auto &pr: mINI::Instance()) {
                replace(pr.second, "https://127.0.0.1/", "http://127.0.0.1:8080/");
            }

            mINI::Instance()["hook.enable"] = 0;
            mINI::Instance()["api.apiDebug"] = 1;

            // 创建定时器
            auto poller = EventPollerPool::Instance().getPoller();

            // 启动服务器
            int argc = 5;
            const char *argv[] = {"", "-c", ini_file.data(), "-s", pem_file.data()};
            start_main(argc, (char **) argv);

        } catch (std::exception &ex) {
            WarnL << "Server start failed: " << ex.what();
            g_is_running = false;
        }
    });

    return true;
}

// 停止服务器
JNIEXPORT jboolean JNICALL Java_com_zlmediakit_jni_ZLMediaKit_stopServer(JNIEnv* env, jclass cls) {
    std::lock_guard<std::mutex> lock(g_mutex);

    if (!g_is_running) {
        WarnL << "Server is not running";
        return false;
    }

    if (g_thread_id) {
        pthread_kill(g_thread_id, SIGINT);
        g_thread_id = 0;
    }

    if (g_server_thread) {
        g_is_running = false;
        if (g_server_thread->joinable()) {
            g_server_thread->join();
        }
        g_server_thread = nullptr;
    }

    return true;
}
}