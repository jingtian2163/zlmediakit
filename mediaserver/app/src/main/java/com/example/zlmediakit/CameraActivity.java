package com.example.zlmediakit;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "CameraActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private boolean mIsPreviewRunning = false;
    private boolean mIsFrontCamera = false;
    
    private H264Encoder mH264Encoder;
    private Button mBtnSwitch;
    private Button mBtnStart;
    private boolean mIsRecording = false;
    
    // 预览参数
    private int mPreviewWidth = 1280;
    private int mPreviewHeight = 720;
    private int mFrameRate = 30;

    // 添加帧计数器
    private long mFrameCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        
        Log.d(TAG, "CameraActivity onCreate");
        initViews();
        
        // 延迟一下再申请权限，确保界面完全加载
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermissions();
            }
        }, 500);
    }
    
    private void initViews() {
        mSurfaceView = findViewById(R.id.surface_view);
        mBtnSwitch = findViewById(R.id.btn_switch_camera);
        mBtnStart = findViewById(R.id.btn_start_record);
        
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        mBtnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRecording) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });
    }
    
    private void checkPermissions() {
        // 根据Android版本动态申请权限
        List<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.CAMERA);
        permissionList.add(Manifest.permission.RECORD_AUDIO);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用新的媒体权限
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissionList.add(Manifest.permission.READ_MEDIA_VIDEO);
        } else {
            // Android 12及以下使用旧的存储权限
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        
        String[] permissions = permissionList.toArray(new String[0]);
        
        boolean needRequest = false;
        StringBuilder missingPermissions = new StringBuilder();
        
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            Log.d(TAG, "检查权限 " + permission + ", 结果: " + (result == PackageManager.PERMISSION_GRANTED ? "已授予" : "未授予"));
            if (result != PackageManager.PERMISSION_GRANTED) {
                needRequest = true;
                missingPermissions.append(permission).append(" ");
            }
        }
        
        if (needRequest) {
            Log.d(TAG, "需要申请权限: " + missingPermissions.toString());
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "所有权限已授予，开始初始化相机");
            initCamera();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        Log.d(TAG, "权限申请结果回调, requestCode: " + requestCode);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < permissions.length; i++) {
                boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                result.append(permissions[i]).append(": ").append(granted ? "已授予" : "被拒绝").append("\n");
                if (!granted) {
                    allGranted = false;
                }
            }
            
            Log.d(TAG, "权限申请结果:\n" + result.toString());
            
            if (allGranted) {
                Log.d(TAG, "所有权限已授予，开始初始化相机");
                Toast.makeText(this, "权限已授予，正在启动相机...", Toast.LENGTH_SHORT).show();
                initCamera();
                // 自动开始推流
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mIsRecording) {
                            startRecording();
                        }
                    }
                }, 1000); // 延迟1秒启动
            } else {
                Log.e(TAG, "权限被拒绝");
                Toast.makeText(this, "需要摄像头和存储权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    private void initCamera() {
        try {
            openCamera(mIsFrontCamera);
        } catch (Exception e) {
            Log.e(TAG, "初始化摄像头失败", e);
            Toast.makeText(this, "摄像头初始化失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openCamera(boolean isFront) {
        releaseCamera();
        
        try {
            int cameraId = isFront ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
            int cameraIndex = getCameraIndex(cameraId);
            
            if (cameraIndex == -1) {
                Toast.makeText(this, "未找到摄像头", Toast.LENGTH_SHORT).show();
                return;
            }
            
            mCamera = Camera.open(cameraIndex);
            setupCameraParameters();
            
        } catch (Exception e) {
            Log.e(TAG, "打开摄像头失败", e);
            Toast.makeText(this, "摄像头打开失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    private int getCameraIndex(int facing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return i;
            }
        }
        return -1;
    }
    
    private void setupCameraParameters() {
        if (mCamera == null) return;
        
        try {
            // 设置摄像头显示方向为90度
            mCamera.setDisplayOrientation(90);
            Log.d(TAG, "摄像头显示方向设置为90度");
            
            Camera.Parameters parameters = mCamera.getParameters();
            
            // 设置预览尺寸
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            Camera.Size bestSize = getBestPreviewSize(previewSizes);
            if (bestSize != null) {
                mPreviewWidth = bestSize.width;
                mPreviewHeight = bestSize.height;
                parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
                Log.d(TAG, "设置预览尺寸: " + mPreviewWidth + "x" + mPreviewHeight);
            }
            
            // 设置帧率
            List<int[]> fpsRanges = parameters.getSupportedPreviewFpsRange();
            if (fpsRanges != null && !fpsRanges.isEmpty()) {
                int[] bestFpsRange = getBestFpsRange(fpsRanges);
                parameters.setPreviewFpsRange(bestFpsRange[0], bestFpsRange[1]);
                Log.d(TAG, "设置帧率范围: " + bestFpsRange[0] + "-" + bestFpsRange[1]);
            }
            
            // 设置对焦模式
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            
            // 设置预览格式为NV21
            parameters.setPreviewFormat(android.graphics.ImageFormat.NV21);
            
            mCamera.setParameters(parameters);
            
            Log.d(TAG, "摄像头参数设置完成");
            
            // 如果Surface已经准备好，立即开始预览
            if (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid()) {
                Log.d(TAG, "Surface已准备好，开始预览");
                startPreview();
            } else {
                Log.d(TAG, "Surface尚未准备好，等待surfaceChanged回调");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "设置摄像头参数失败", e);
        }
    }
    
    private Camera.Size getBestPreviewSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        for (Camera.Size size : sizes) {
            if (size.width == 1280 && size.height == 720) {
                return size;
            }
            if (size.width == 1920 && size.height == 1080) {
                bestSize = size;
            } else if (bestSize == null || 
                      (size.width * size.height > bestSize.width * bestSize.height)) {
                bestSize = size;
            }
        }
        return bestSize;
    }
    
    private int[] getBestFpsRange(List<int[]> fpsRanges) {
        int[] bestRange = fpsRanges.get(0);
        for (int[] range : fpsRanges) {
            if (range[1] >= 30000) { // 30fps
                return range;
            }
            if (range[1] > bestRange[1]) {
                bestRange = range;
            }
        }
        return bestRange;
    }
    
    private void switchCamera() {
        if (mIsRecording) {
            Toast.makeText(this, "录制中不能切换摄像头", Toast.LENGTH_SHORT).show();
            return;
        }
        
        mIsFrontCamera = !mIsFrontCamera;
        openCamera(mIsFrontCamera);
        
        if (mSurfaceHolder.getSurface().isValid()) {
            startPreview();
        }
    }
    
    private void startRecording() {
        if (mCamera == null) {
            Toast.makeText(this, "摄像头未初始化", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            // 创建输出文件
            File outputFile = createOutputFile();
            
            // RTSP推流地址
            //String rtspUrl = "rtmp://127.0.0.1:1935/live/vrcamera1";
            String rtspUrl = "rtsp://127.0.0.1:8554/live/vrcamera1";
            
            // 创建流回调接口
            H264Encoder.StreamCallback streamCallback = new H264Encoder.StreamCallback() {
                @Override
                public void onStreamStarted() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "RTSP推流已启动", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "RTSP推流已启动");
                        }
                    });
                }
                
                @Override
                public void onStreamStopped() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "RTSP推流已停止", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "RTSP推流已停止");
                        }
                    });
                }
                
                @Override
                public void onStreamError(String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "推流错误: " + error, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "推流错误: " + error);
                        }
                    });
                }
            };
            
            // 初始化H264编码器，支持文件录制和RTSP推流
            mH264Encoder = new H264Encoder(mPreviewWidth, mPreviewHeight, 
                    outputFile.getAbsolutePath(), rtspUrl, streamCallback);
            mH264Encoder.start();
            
            mIsRecording = true;
            mBtnStart.setText("停止录制");
            mBtnSwitch.setEnabled(false);
            
            String message = String.format("开始录制\n文件: %s\n推流: %s", 
                    outputFile.getName(), rtspUrl);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.d(TAG, "开始录制和推流: " + rtspUrl);
            
        } catch (Exception e) {
            Log.e(TAG, "开始录制失败", e);
            Toast.makeText(this, "录制失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void stopRecording() {
        if (mH264Encoder != null) {
            mH264Encoder.stop();
            mH264Encoder = null;
        }
        
        mIsRecording = false;
        mBtnStart.setText("开始录制");
        mBtnSwitch.setEnabled(true);
        
        Toast.makeText(this, "录制结束", Toast.LENGTH_SHORT).show();
    }
    
    private File createOutputFile() throws IOException {
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+使用应用专属目录
            dir = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "ZLMediaKit");
        } else {
            // Android 10之前使用外部存储
            dir = new File(Environment.getExternalStorageDirectory(), "ZLMediaKit");
        }
        
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("无法创建输出目录: " + dir.getAbsolutePath());
        }
        
        String fileName = "camera_" + System.currentTimeMillis() + ".h264";
        File file = new File(dir, fileName);
        
        Log.d(TAG, "输出文件: " + file.getAbsolutePath());
        return file;
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: " + width + "x" + height);
        
        // 只有相机已经初始化才启动预览
        if (mCamera != null) {
            startPreview();
        } else {
            Log.d(TAG, "surfaceChanged: 相机尚未初始化，等待相机初始化完成");
        }
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        stopPreview();
    }
    
    private void startPreview() {
        Log.d(TAG, "startPreview: 开始尝试启动预览");
        
        if (mCamera == null) {
            Log.e(TAG, "startPreview: 相机为空，无法启动预览");
            return;
        }
        
        if (mIsPreviewRunning) {
            Log.d(TAG, "startPreview: 预览已经在运行中");
            return;
        }
        
        if (mSurfaceHolder == null) {
            Log.e(TAG, "startPreview: SurfaceHolder为空，无法启动预览");
            return;
        }
        
        try {
            Log.d(TAG, "startPreview: 设置预览显示");
            mCamera.setPreviewDisplay(mSurfaceHolder);
            
            Log.d(TAG, "startPreview: 设置预览回调");
            mCamera.setPreviewCallback(this);
            
            Log.d(TAG, "startPreview: 开始预览");
            mCamera.startPreview();
            mIsPreviewRunning = true;
            
            Log.d(TAG, "预览启动成功！");
            
        } catch (IOException e) {
            Log.e(TAG, "设置预览显示失败", e);
        } catch (RuntimeException e) {
            Log.e(TAG, "启动预览运行时异常", e);
        } catch (Exception e) {
            Log.e(TAG, "启动预览失败", e);
        }
    }
    
    private void stopPreview() {
        if (mCamera != null && mIsPreviewRunning) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mIsPreviewRunning = false;
            
            Log.d(TAG, "预览停止");
        }
    }
    
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mFrameCount++;
        
        // 每30帧打印一次状态（大约1秒）
        if (mFrameCount % 100 == 1) {
            Log.d(TAG, String.format("onPreviewFrame: 接收到第%d帧，数据大小=%d字节, 录制状态=%s", 
                    mFrameCount, data != null ? data.length : 0, mIsRecording ? "录制中" : "未录制"));
        }
        
        if (mIsRecording && mH264Encoder != null) {
            if (data != null && data.length > 0) {
                // 前10帧打印前20个字节用于调试
                if (mFrameCount <= 10) {
                    StringBuilder hexStart = new StringBuilder();
                    for (int i = 0; i < Math.min(20, data.length); i++) {
                        hexStart.append(String.format("%02X ", data[i] & 0xFF));
                    }
                    Log.d(TAG, String.format("Camera frame %d: size=%d, first 20 bytes: %s", 
                            mFrameCount, data.length, hexStart.toString()));
                }
                
                // 将YUV数据传递给编码器
                mH264Encoder.onFrameAvailable(data);
            } else {
                Log.w(TAG, "Camera frame " + mFrameCount + " is null or empty");
            }
        }
    }
    
    private void releaseCamera() {
        stopPreview();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
        releaseCamera();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        stopPreview();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (mSurfaceHolder.getSurface().isValid()) {
            startPreview();
        }
    }
} 