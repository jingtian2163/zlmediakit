project(ZLMediaKit)
cmake_minimum_required(VERSION 3.1.3)
#使能c++11
set(CMAKE_CXX_STANDARD 11)

#设置生成的so动态库最后输出的路径
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/libs_export/${ANDROID_ABI})
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/libs_export/${ANDROID_ABI})
set(EXECUTABLE_OUTPUT_PATH ${CMAKE_CURRENT_SOURCE_DIR}/libs_export/${ANDROID_ABI}/binary)
set(OPENSSL_ROOT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}")

#由于openssl库编译时未指定-fPIC，到时github action ci编译失败，先屏蔽掉
set(OPENSSL_INCLUDE_DIR "${OPENSSL_ROOT_DIR}/include")
set(OPENSSL_CRYPTO_LIBRARY "${OPENSSL_ROOT_DIR}/libcrypto.a")
set(OPENSSL_SSL_LIBRARY "${OPENSSL_ROOT_DIR}/libssl.a")

# 设置 SRTP库文件目录
set(SRTP_ROOT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI}")
set(SRTP_INCLUDE_DIRS "${SRTP_ROOT_DIR}/include/srtp2")
set(SRTP_LIBRARIES "${SRTP_ROOT_DIR}/libsrtp2.a")
# 4. 设置strip选项
set(CMAKE_STRIP "${ANDROID_TOOLCHAIN_PREFIX}strip")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -s")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -s")
# 9. 设置release编译
set(CMAKE_BUILD_TYPE Release)
#设置工程源码根目录
set(JNI_Root ${CMAKE_CURRENT_SOURCE_DIR})
set(ZLMediaKit_Root ${CMAKE_CURRENT_SOURCE_DIR}/../../../../../)

#添加主工程cmake
add_subdirectory(${ZLMediaKit_Root} ${EXECUTABLE_OUTPUT_PATH})

#设置include
include_directories(${JNI_Root})
include_directories(${ZLMediaKit_Root}/src)
include_directories(${ZLMediaKit_Root}/srt)
include_directories(${ZLMediaKit_Root}/webrtc)
include_directories(${ZLMediaKit_Root}/server)
include_directories(${ZLMediaKit_Root}/3rdpart)
include_directories(${ZLMediaKit_Root}/3rdpart/media-server)
include_directories(${ZLMediaKit_Root}/3rdpart/ZLToolKit/src)
#mkapi
include_directories(${ZLMediaKit_Root}/api)
include_directories(${ZLMediaKit_Root}/api/include)
include_directories(${ZLMediaKit_Root}/api/source)
# 包含 SRTP 头文件路径
include_directories(${SRTP_INCLUDE_DIRS})

# 设置 FFmpeg 库文件目录
set(FFMPEG_ROOT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/libffmpeg/${ANDROID_ABI}")
set(FFMPEG_INCLUDE_DIRS "${FFMPEG_ROOT_DIR}/include")
set(FFMPEG_LIB_DIR "${FFMPEG_ROOT_DIR}/lib")

# 包含 FFmpeg 头文件路径
include_directories(${FFMPEG_INCLUDE_DIRS})

# 预构建 FFmpeg 库
add_library(ffmpeg_avcodec SHARED IMPORTED)
set_target_properties(ffmpeg_avcodec PROPERTIES IMPORTED_LOCATION ${FFMPEG_LIB_DIR}/libavcodec.so)

add_library(ffmpeg_avformat SHARED IMPORTED)
set_target_properties(ffmpeg_avformat PROPERTIES IMPORTED_LOCATION ${FFMPEG_LIB_DIR}/libavformat.so)

add_library(ffmpeg_avutil SHARED IMPORTED)
set_target_properties(ffmpeg_avutil PROPERTIES IMPORTED_LOCATION ${FFMPEG_LIB_DIR}/libavutil.so)

add_library(ffmpeg_swscale SHARED IMPORTED)
set_target_properties(ffmpeg_swscale PROPERTIES IMPORTED_LOCATION ${FFMPEG_LIB_DIR}/libswscale.so)

add_library(ffmpeg_swresample SHARED IMPORTED)
set_target_properties(ffmpeg_swresample PROPERTIES IMPORTED_LOCATION ${FFMPEG_LIB_DIR}/libswresample.so)

add_library(ffmpeg_avfilter SHARED IMPORTED)
set_target_properties(ffmpeg_avfilter PROPERTIES IMPORTED_LOCATION ${FFMPEG_LIB_DIR}/libavfilter.so)

add_library(ffmpeg_avdevice SHARED IMPORTED)
set_target_properties(ffmpeg_avdevice PROPERTIES IMPORTED_LOCATION ${FFMPEG_LIB_DIR}/libavdevice.so)

add_library(ffmpeg_postproc SHARED IMPORTED)
set_target_properties(ffmpeg_postproc PROPERTIES IMPORTED_LOCATION ${FFMPEG_LIB_DIR}/libpostproc.so)

#收集源代码添加动态库
file(GLOB JNI_src_list ${JNI_Root}/*.cpp ${JNI_Root}/*.h ${ZLMediaKit_Root}/api/include/*.h ${ZLMediaKit_Root}/api/source/*.cpp ${OPENSSL_INCLUDE_DIR}/*.h ${SRTP_INCLUDE_DIRS}/*.h )
add_library(zlmediakit_jni SHARED ${JNI_src_list})

#链接
target_link_libraries(zlmediakit_jni
        -Wl,--start-group
        log
        z
        ${MK_LINK_LIBRARIES}
        # FFmpeg 库
        ffmpeg_avformat
        ffmpeg_avcodec
        ffmpeg_avutil
        ffmpeg_swscale
        ffmpeg_swresample
        ffmpeg_avfilter
        ffmpeg_avdevice
        ffmpeg_postproc
        -Wl,--as-needed    # 只链接实际使用的符号
        -Wl,--end-group
)

