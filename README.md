1、/Users/olaola/Desktop/ola/opensource/ffmpeg/ffmpeg  -f avfoundation -framerate 30 -video_size 1280x720 -i "0" -c:v libx264 -preset ultrafast -tune zerolatency -g 60 -b:v 1000k  -f rtsp rtsp://127.0.0.1:554/live/camera

C:\Users\yangjun\Desktop\work\rtspffmpeg\ffmpeg-7.0.2-full_build-shared\ffmpeg-7.0.2-full_build-shared\bin>C:\Users\yangjun\Desktop\work\rtspffmpeg\ffmpeg-7.0.2-full_build-shared\ffmpeg-7.0.2-full_build-shared\bin\ffmpeg.exe -stream_loop -1 -re -i C:\Users\yangjun\Desktop\work\rtspffmpeg\ffmpeg-7.0.2-full_build-shared\ffmpeg-7.0.2-full_build-shared\bin\local_output.h264   -c:v copy -c:a copy -f rtsp -rtsp_transport tcp "rtsp://192.168.31.127:554/live/camera"


//flv 播放
http://127.0.0.1:800/live/camera.live.flv

//请求地址
http://192.168.31.127:800/index/api/addStreamProxy?vhost=http://192.168.31.127/&app=live&stream=camera&url=rtsp://192.168.31.127/live/camera&secret=oAvNC2gxlrXJcU1EM31OU6Y54AS270mS


ws://127.0.0.1:800/live/camera.live.flv

https://192.168.31.127/webrtc/?app=live&stream=camera&type=play

https://192.168.31.127/index/api/webrtc?app=live&stream=camera&type=play  http 请求


/Users/olaola/Desktop/ola/opensource/ffmpeg/ffmpeg -f avfoundation -framerate 30 -video_size 1280x720 -i "0" -c:v libx265 -preset ultrafast -tune zerolatency -x265-params "keyint=60:min-keyint=60:bframes=0" -pix_fmt yuv420p -b:v 10000k -f rtsp rtsp://127.0.0.1:554/live/camera


webrtc rtc over tcp h265编码
/Users/olaola/Desktop/ola/opensource/ffmpeg/ffmpeg -f avfoundation -framerate 30 -video_size 1280x720 -i "0" -c:v libx265 -preset ultrafast -tune zerolatency -x265-params "keyint=60:min-keyint=60:bframes=0" -pix_fmt yuv420p -b:v 10000k -f flv rtmp://192.168.82.22/live/camera2

此时rtsp拉流失败，黑屏


rtsp 推流
webrtc 无法拉流



2/4 机器
8.210.187.185  root/152620828a


scp -r root@8.210.187.185:/root/webrtc/rtc/src/out/Release/apks ./

https://blog.csdn.net/rosyrays1/article/details/148355150 webrtc 最新版本android aar编译

chrome://webrtc-internals 查看数据

rtsp/rtmp direct_Proxy = 0;避免转发，直接推流,解决h265无法推流问题


webrtc 编译时候出错,需要去除这个
then
   echo Running depot tools as root is sad.
-  exit
+ # exit
 fi

 rtsp://192.168.1.35:8554/live/vrcamera1  拉流mediaserver

cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit_副本/nacktest 
cd /Users/olaola/Desktop/ola/opensource/ZLMediaKit_副本/build
cmake ..
make -j8
