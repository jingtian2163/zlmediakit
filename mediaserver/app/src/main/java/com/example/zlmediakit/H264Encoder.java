package com.example.zlmediakit;

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

public class H264Encoder {
    private static final String TAG = "H264Encoder";
    private static final String MIME_TYPE = "video/avc";
    private static final int IFRAME_INTERVAL = 2; // 2秒GOP
    private static final int BITRATE = 4000000; // 10Mbps
    private static final int FRAMERATE = 30;
    
    // 加载native库
    static {
        try {
            System.loadLibrary("zlmediakit_jni");
        } catch (UnsatisfiedLinkError e) {
            // 库未找到时的容错处理
            android.util.Log.w("H264Encoder", "Native library not found: " + e.getMessage());
        }
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
    }
    
    // 启用RTSP推流的构造函数
    public H264Encoder(int width, int height, String outputFile, String rtspUrl, StreamCallback callback) {
        mWidth = width;
        mHeight = height;
        mOutputFile = outputFile;
        mRtspUrl = rtspUrl;
        mStreamCallback = callback;
        mBufferInfo = new MediaCodec.BufferInfo();
        mEnableStreaming = (rtspUrl != null && !rtspUrl.isEmpty());
    }
    
    public Surface start() throws IOException {
        Log.d(TAG, "Starting H264 encoder: " + mWidth + "x" + mHeight);
        
        // 创建输出文件流
        mFileOutputStream = new BufferedOutputStream(new FileOutputStream(mOutputFile));
        
        // 创建MediaFormat
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        
        // 设置颜色格式 - Surface编码使用COLOR_FormatSurface
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        
        // 设置基本编码参数
        format.setInteger(MediaFormat.KEY_BIT_RATE, BITRATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMERATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        
        // 设置编码器配置为baseline profile（无B帧）
        format.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
        format.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel31);
        
        // 设置颜色空间和传输特性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            format.setInteger(MediaFormat.KEY_COLOR_STANDARD, MediaFormat.COLOR_STANDARD_BT709);
            format.setInteger(MediaFormat.KEY_COLOR_TRANSFER, MediaFormat.COLOR_TRANSFER_SDR_VIDEO);
            format.setInteger(MediaFormat.KEY_COLOR_RANGE, MediaFormat.COLOR_RANGE_LIMITED);
        }
        
        Log.d(TAG, "MediaFormat配置: " + format.toString());
        
        // 创建编码器
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        
        // 获取输入Surface - 这是关键！
        mInputSurface = mEncoder.createInputSurface();
        
        mEncoder.start();
        mIsRunning = true;
        
        // 初始化推流器
        if (mEnableStreaming) {
            mPusherPtr = nativeCreatePusher();
            if (mPusherPtr != 0) {
                boolean initSuccess = nativeInitPusher(mPusherPtr, mRtspUrl, mWidth, mHeight, FRAMERATE);
                if (initSuccess) {
                    Log.d(TAG, "RTSP推流器初始化成功: " + mRtspUrl + " (尺寸: " + mWidth + "x" + mHeight + ")");
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
        
        Log.d(TAG, "H264 encoder started successfully, returning Surface");
        
        // 返回输入Surface供相机使用
        return mInputSurface;
    }
    
    private void encodeLoop() {
        while (mIsRunning) {
            try {
                // 处理编码器输出
                drainEncoder(false);
                
                // 短暂休眠避免过度占用CPU
                Thread.sleep(10);
                
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                Log.e(TAG, "Error in encode loop: " + e.getMessage());
            }
        }
        
        // 结束编码
        drainEncoder(true);
    }
    
    private void drainEncoder(boolean endOfStream) {
        if (endOfStream) {
            Log.d(TAG, "Sending EOS to encoder");
            mEncoder.signalEndOfInputStream();
        }
        
        while (true) {
            int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 10000);
            
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
                    
                    // 处理编码数据
                    processEncodedData(data);
                } else {
                    Log.w(TAG, "Encoder output buffer size is 0");
                }
                
                mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d(TAG, "End of stream reached");
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
                        // 保存SPS，但不进行推流
                        if (mSps == null && data.length > 4) {
                            mSps = data.clone();
                            Log.d(TAG, "SPS saved from encoder output, length: " + mSps.length);
                        }
                        return;
                    case 8:
                        frameType = "PPS";
                        // 保存PPS，但不进行推流
                        if (mPps == null && data.length > 4) {
                            mPps = data.clone();
                            Log.d(TAG, "PPS saved from encoder output, length: " + mPps.length);
                        }
                        return;
                }
            }
            
            // 跳过太小的数据包
            if (data.length <= 10) {
                Log.w(TAG, "Skipping too small frame: " + data.length + " bytes");
                return;
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
                
                Log.d(TAG, String.format("Frame %d: %s frame (SPS+PPS+I), size=%d, keyFrame=%s",
                        mFrameCount, frameType, outputData.length, isKeyFrame));
                
            } else {
                // P帧或其他帧直接使用原数据
                outputData = data;
                
                if (mFrameCount <= 10) {
                    Log.d(TAG, String.format("Frame %d: %s frame, size=%d, keyFrame=%s",
                            mFrameCount, frameType, outputData.length, isKeyFrame));
                }
            }
            
            // 再次检查最终输出数据的大小
            if (outputData.length <= 10) {
                Log.w(TAG, "Final output data too small, skipping: " + outputData.length + " bytes");
                return;
            }
            
            // 输出前5帧的前30字节十六进制数据
            if (mFrameCount <= 5) {
                int printLength = Math.min(30, outputData.length);
                StringBuilder hexString = new StringBuilder();
                for (int i = 0; i < printLength; i++) {
                    hexString.append(String.format("%02X ", outputData[i] & 0xFF));
                }
                Log.d(TAG, String.format("Frame %d hex data: %s", mFrameCount, hexString.toString()));
            }
            
            // 写入文件
            // if (mFileOutputStream != null) {
            //     mFileOutputStream.write(outputData);
            //     mFileOutputStream.flush();
            // }
            
            // 推送到RTSP流
            if (mEnableStreaming && mPusherPtr != 0 && outputData.length > 15) {
                boolean pushSuccess = nativePushFrame(mPusherPtr, outputData, outputData.length, isKeyFrame);
                if (!pushSuccess && mFrameCount <= 10) {
                    Log.w(TAG, "RTSP推流失败 - 帧大小: " + outputData.length);
                } else if (mFrameCount <= 10) {
                    Log.d(TAG, "RTSP推流成功 - 帧大小: " + outputData.length + ", 类型: " + frameType);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing encoded data: " + e.getMessage());
        }
    }
    
    public void stop() {
        Log.d(TAG, "Stopping H264 encoder");
        
        mIsRunning = false;
        
        if (mEncoderThread != null) {
            try {
                mEncoderThread.join(3000);
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
        
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
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
    
    /**
     * 获取编码器的输入Surface
     */
    public Surface getInputSurface() {
        return mInputSurface;
    }
} 