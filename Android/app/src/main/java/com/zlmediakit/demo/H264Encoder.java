package com.zlmediakit.demo;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class H264Encoder {
    private static final String TAG = "H264Encoder";
    private static final String MIME_TYPE = "video/avc";
    private static final int IFRAME_INTERVAL = 2; // 2秒GOP
    private static final int BITRATE = 2000000; // 2Mbps
    private static final int FRAMERATE = 30;
    
    // 加载native库
    static {
        System.loadLibrary("stream_pusher");
    }
    
    // JNI方法声明
    private native long nativeCreatePusher();
    private native boolean nativeInitPusher(long pusherPtr, String rtspUrl, int width, int height, int fps);
    private native boolean nativePushFrame(long pusherPtr, byte[] data, int size, boolean isKeyFrame);
    private native void nativeDestroyPusher(long pusherPtr);
    
    public interface StreamCallback {
        void onStreamStarted();
        void onStreamStopped(); 
        void onStreamError(String error);
    }
    
    private MediaCodec mEncoder;
    private Surface mInputSurface;
    private MediaCodec.BufferInfo mBufferInfo;
    
    private int mWidth;
    private int mHeight;
    private String mOutputFile;
    private BufferedOutputStream mFileOutputStream;
    private String mRtspUrl;
    private StreamCallback mStreamCallback;
    
    private boolean mIsRunning = false;
    private Thread mEncoderThread;
    private BlockingQueue<byte[]> mFrameQueue;
    
    // SPS和PPS
    private byte[] mSps;
    private byte[] mPps;
    private int mFrameCount = 0;
    
    // FFmpeg推流器句柄
    private long mPusherPtr = 0;
    private boolean mEnableStreaming = false;
    
    public H264Encoder(int width, int height, String outputFile) {
        mWidth = width;
        mHeight = height;
        mOutputFile = outputFile;
        mBufferInfo = new MediaCodec.BufferInfo();
        mFrameQueue = new LinkedBlockingQueue<>();
    }
    
    // 启用RTSP推流的构造函数
    public H264Encoder(int width, int height, String outputFile, String rtspUrl, StreamCallback callback) {
        mWidth = width;
        mHeight = height;
        mOutputFile = outputFile;
        mRtspUrl = rtspUrl;
        mStreamCallback = callback;
        mBufferInfo = new MediaCodec.BufferInfo();
        mFrameQueue = new LinkedBlockingQueue<>();
        mEnableStreaming = (rtspUrl != null && !rtspUrl.isEmpty());
    }
    
    public void start() throws IOException {
        Log.d(TAG, "Starting H264 encoder: " + mWidth + "x" + mHeight);
        
        // 创建输出文件流
        mFileOutputStream = new BufferedOutputStream(new FileOutputStream(mOutputFile));
        
        // 创建MediaFormat
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BITRATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMERATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        
        // 设置编码器配置为baseline profile（无B帧）
        format.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
        format.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel31);
        
        // 创建编码器
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mEncoder.start();
        
        mIsRunning = true;
        
        // 初始化推流器
        if (mEnableStreaming) {
            mPusherPtr = nativeCreatePusher();
            if (mPusherPtr != 0) {
                boolean initSuccess = nativeInitPusher(mPusherPtr, mRtspUrl, mWidth, mHeight, FRAMERATE);
                if (initSuccess) {
                    Log.d(TAG, "RTSP推流器初始化成功: " + mRtspUrl);
                    if (mStreamCallback != null) {
                        mStreamCallback.onStreamStarted();
                    }
                } else {
                    Log.e(TAG, "RTSP推流器初始化失败");
                    if (mStreamCallback != null) {
                        mStreamCallback.onStreamError("推流器初始化失败");
                    }
                }
            }
        }
        
        // 启动编码线程
        mEncoderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                encodeLoop();
            }
        });
        mEncoderThread.start();
        
        Log.d(TAG, "H264 encoder started successfully");
    }
    
    public void onFrameAvailable(byte[] data) {
        if (mIsRunning && data != null && data.length > 0) {
            try {
                // 检查数据是否有效（不全是0）
                boolean hasNonZeroData = false;
                for (int i = 0; i < Math.min(100, data.length); i++) {
                    if (data[i] != 0) {
                        hasNonZeroData = true;
                        break;
                    }
                }
                
                if (hasNonZeroData) {
                    mFrameQueue.offer(data.clone());
                } else {
                    Log.w(TAG, "Skipping frame with all zero data, length: " + data.length);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding frame to queue: " + e.getMessage());
            }
        }
    }
    
    private void encodeLoop() {
        while (mIsRunning) {
            try {
                // 从队列获取帧数据 - 使用take()阻塞等待，避免空轮询
                byte[] frameData = mFrameQueue.poll(10, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (frameData != null && frameData.length > 0) {
                    encodeFrame(frameData);
                }
                
                // 处理编码器输出
                drainEncoder(false);
                
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                Log.e(TAG, "Error in encode loop: " + e.getMessage());
            }
        }
        
        // 结束编码
        drainEncoder(true);
    }
    
    private void encodeFrame(byte[] frameData) {
        try {
            int inputBufferIndex = mEncoder.dequeueInputBuffer(0);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mEncoder.getInputBuffer(inputBufferIndex);
                if (inputBuffer != null) {
                    inputBuffer.clear();
                    inputBuffer.put(frameData);
                    
                    long presentationTimeUs = System.nanoTime() / 1000;
                    mEncoder.queueInputBuffer(inputBufferIndex, 0, frameData.length, presentationTimeUs, 0);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error encoding frame: " + e.getMessage());
        }
    }
    
    private void drainEncoder(boolean endOfStream) {
        if (endOfStream) {
            int inputBufferIndex = mEncoder.dequeueInputBuffer(0);
            if (inputBufferIndex >= 0) {
                mEncoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            }
        }
        
        while (true) {
            int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            
            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {
                    break;
                }
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = mEncoder.getOutputFormat();
                Log.d(TAG, "Encoder output format changed: " + newFormat);
                
                // 提取SPS和PPS
                if (newFormat.containsKey("csd-0")) {
                    ByteBuffer csd0 = newFormat.getByteBuffer("csd-0");
                    mSps = new byte[csd0.remaining()];
                    csd0.get(mSps);
                    Log.d(TAG, "SPS extracted from csd-0, length: " + mSps.length);
                }
                
                if (newFormat.containsKey("csd-1")) {
                    ByteBuffer csd1 = newFormat.getByteBuffer("csd-1");
                    mPps = new byte[csd1.remaining()];
                    csd1.get(mPps);
                    Log.d(TAG, "PPS extracted from csd-1, length: " + mPps.length);
                }
                
            } else if (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = mEncoder.getOutputBuffer(outputBufferIndex);
                
                if (mBufferInfo.size > 0) {
                    byte[] data = new byte[mBufferInfo.size];
                    outputBuffer.position(mBufferInfo.offset);
                    outputBuffer.get(data, 0, mBufferInfo.size);
                    
                    // 检查编码数据是否有效
                    if (data.length > 4) {
                        // 处理编码数据
                        processEncodedData(data);
                    } else {
                        Log.w(TAG, "Skipping invalid encoded data, size: " + data.length);
                    }
                } else {
                    Log.w(TAG, "Encoder output buffer size is 0");
                }
                
                mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            }
        }
    }
    
    private void processEncodedData(byte[] data) {
        try {
            // 检查NAL单元类型
            String frameType = "Unknown";
            boolean isKeyFrame = (mBufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0;
            
            if (data.length >= 4) {
                int nalType = data[4] & 0x1F;
                switch (nalType) {
                    case 1:
                        frameType = "P";
                        break;
                    case 5:
                        frameType = "I";
                        isKeyFrame = true;
                        break;
                    case 7:
                        frameType = "SPS";
                        return; // 跳过单独的SPS帧，不计数也不写入
                    case 8:
                        frameType = "PPS";
                        return; // 跳过单独的PPS帧，不计数也不写入
                }
            }
            
            mFrameCount++;
            
            // 处理I帧：构建 SPS + PPS + I帧 的完整数据
            byte[] outputData;
            if (isKeyFrame && frameType.equals("I") && mSps != null && mPps != null) {
                // 计算总长度：SPS + PPS + I帧
                int totalLength = mSps.length + mPps.length + data.length;
                outputData = new byte[totalLength];
                
                int offset = 0;
                // 拷贝SPS
                System.arraycopy(mSps, 0, outputData, offset, mSps.length);
                offset += mSps.length;
                
                // 拷贝PPS
                System.arraycopy(mPps, 0, outputData, offset, mPps.length);
                offset += mPps.length;
                
                // 拷贝I帧数据
                System.arraycopy(data, 0, outputData, offset, data.length);
                
//                Log.d(TAG, String.format("Frame %d: %s frame (SPS+PPS+I), size=%d, keyFrame=%s",
//                        mFrameCount, frameType, outputData.length, isKeyFrame));
                
            } else {
                // P帧或其他帧直接使用原数据
                outputData = data;
                
//                Log.d(TAG, String.format("Frame %d: %s frame, size=%d, keyFrame=%s",
//                        mFrameCount, frameType, outputData.length, isKeyFrame));
            }
            
            // 输出前10帧的前60字节十六进制数据
            if (mFrameCount <= 10) {
                int printLength = Math.min(60, outputData.length);
                StringBuilder hexString = new StringBuilder();
                for (int i = 0; i < printLength; i++) {
                    hexString.append(String.format("%02X ", outputData[i] & 0xFF));
                }
                Log.d(TAG, String.format("Frame %d hex data: %s", mFrameCount, hexString.toString()));
            }
            
            // 验证数据后再写入文件
            if (outputData.length > 4) {
                // 再次检查前几个字节确保不全是0
                boolean hasValidData = false;
                for (int i = 0; i < Math.min(10, outputData.length); i++) {
                    if (outputData[i] != 0) {
                        hasValidData = true;
                        break;
                    }
                }
                
                if (hasValidData) {
                    // 写入文件
                    if (mFileOutputStream != null) {
                        mFileOutputStream.write(outputData);
                        mFileOutputStream.flush();
                    }
                    
                    // 推送到RTSP流
                    if (mEnableStreaming && mPusherPtr != 0) {
                        boolean pushSuccess = nativePushFrame(mPusherPtr, outputData, outputData.length, isKeyFrame);
                        if (!pushSuccess) {
                            Log.w(TAG, "RTSP推流失败");
                        }
                    }
                } else {
                    Log.w(TAG, "Skipping write - output data appears to be all zeros");
                }
            } else {
                Log.w(TAG, "Skipping write - output data too small: " + outputData.length);
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Error writing encoded data: " + e.getMessage());
        }
    }
    
    public void stop() {
        Log.d(TAG, "Stopping H264 encoder");
        
        mIsRunning = false;
        
        if (mEncoderThread != null) {
            try {
                mEncoderThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (mEncoder != null) {
            try {
                mEncoder.stop();
                mEncoder.release();
                mEncoder = null;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping encoder: " + e.getMessage());
            }
        }
        
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
                mFileOutputStream = null;
            } catch (IOException e) {
                Log.e(TAG, "Error closing output stream: " + e.getMessage());
            }
        }
        
        // 清理推流器
        if (mPusherPtr != 0) {
            nativeDestroyPusher(mPusherPtr);
            mPusherPtr = 0;
            if (mStreamCallback != null) {
                mStreamCallback.onStreamStopped();
            }
        }
        
        Log.d(TAG, String.format("H264 encoder stopped. Total frames: %d, Output file: %s", 
                mFrameCount, mOutputFile));
    }
} 