package com.example.zlmediakit;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
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
import android.view.WindowManager;
import android.view.MotionEvent;
import android.hardware.Camera.Area;


public class CameraActivity extends Activity implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {
    private static final String TAG = "CameraActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private TextureView mTextureView;
    private Camera mCamera;
    private boolean mIsPreviewRunning = false;
    private boolean mIsFrontCamera = false;
    
    private H264Encoder mH264Encoder;
    private Button mBtnSwitch;
    private Button mBtnStart;
    private boolean mIsRecording = false;
    
    // 预览参数
    private int mPreviewWidth = 1920;
    private int mPreviewHeight = 1080;
    private int mFrameRate = 30;

    // 添加Surface相关
    private SurfaceTexture mSurfaceTexture;
    private Surface mCameraSurface;
    private Surface mEncoderSurface;

    private boolean mIsAutoFocusSupported = false;
    private Camera.AutoFocusCallback mAutoFocusCallback;
    private Handler mAutoFocusHandler;
    private Runnable mAutoFocusRunnable;
    private int mMaxNumFocusAreas;
    private int mMaxNumMeteringAreas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        
        // 设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d(TAG, "屏幕常亮已启用");


        Log.d(TAG, "CameraActivity onCreate");
        initViews();
        
        // 延迟一下再申请权限，确保界面完全加载
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermissions();
            }
        }, 500);

        // 自动开始录制
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mIsRecording) {
                    //startRecording();
                }
            }
        }, 3000); // 延迟3秒启动
    }
    
    private void initViews() {
        mTextureView = findViewById(R.id.texture_view);
        mBtnSwitch = findViewById(R.id.btn_switch_camera);
        mBtnStart = findViewById(R.id.btn_start_record);
        
        mTextureView.setSurfaceTextureListener(this);
         // 添加触摸监听以实现点击对焦
        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && mCamera != null && mIsAutoFocusSupported) {
                    handleTouchFocus(event);
                    return true;
                }
                return false;
            }
        });

        
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

    private void handleTouchFocus(MotionEvent event) {
        if (mCamera == null || !mIsAutoFocusSupported) return;
        
        try {
            // 停止自动对焦
            stopAutoFocus();
            
            // 获取触摸点坐标并转换为对焦区域
            float x = event.getX();
            float y = event.getY();
            
            Log.d(TAG, "原始点击坐标: (" + x + ", " + y + ")");
            Log.d(TAG, "TextureView尺寸: " + mTextureView.getWidth() + "x" + mTextureView.getHeight());
            
            Rect focusRect = calculateTapArea(x, y, 1f);
            Rect meteringRect = calculateTapArea(x, y, 1.5f);
            
            Log.d(TAG, "计算后的对焦区域: " + focusRect.toString());
            Log.d(TAG, "计算后的测光区域: " + meteringRect.toString());
            
            Camera.Parameters parameters = mCamera.getParameters();
            
            // 设置对焦区域
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<>();
                focusAreas.add(new Camera.Area(focusRect, 1000));
                parameters.setFocusAreas(focusAreas);
                Log.d(TAG, "设置对焦区域: " + focusAreas.toString());
            }
            
            // 设置测光区域
            if (parameters.getMaxNumMeteringAreas() > 0) {
                List<Camera.Area> meteringAreas = new ArrayList<>();
                meteringAreas.add(new Camera.Area(meteringRect, 1000));
                parameters.setMeteringAreas(meteringAreas);
                Log.d(TAG, "设置测光区域: " + meteringAreas.toString());
            }
            
            // 设置对焦模式为自动
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(parameters);
            
            // 启动对焦
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    Log.d(TAG, "触摸对焦完成: " + (success ? "成功" : "失败"));
                    // 对焦完成后恢复自动对焦
                    if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    } else if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }
                    parameters.setFocusAreas(null);
                    parameters.setMeteringAreas(null);
                    camera.setParameters(parameters);
                    
                    // 重新启动自动对焦
                    startAutoFocus();
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "触摸对焦失败", e);
            // 失败后也尝试恢复自动对焦
            startAutoFocus();
        }
    }

    
    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(200 * coefficient).intValue();
        
        // 修正后的坐标转换逻辑
        // 将屏幕坐标映射到相机坐标系统[-1000,1000]
        // 注意屏幕和相机的坐标系可能不一致
        
        // 首先，将触摸点坐标从屏幕坐标系转换为比例值(0-1)
        float normalizedX = x / mTextureView.getWidth();
        float normalizedY = y / mTextureView.getHeight();
        
        Log.d(TAG, "归一化坐标: (" + normalizedX + ", " + normalizedY + ")");
        
        // 然后，将比例值映射到[-1000,1000]坐标系
        int centerX = (int) ((normalizedX * 2000) - 1000);
        int centerY = (int) ((normalizedY * 2000) - 1000);
        
        Log.d(TAG, "比例映射坐标: (" + centerX + ", " + centerY + ")");
        
        // 处理相机旋转(手机是竖屏，但相机是横向的)
        // 对于后置摄像头(正常情况下为90度旋转)
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mIsFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK, info);
        
        int adjustedX = centerX;
        int adjustedY = centerY;
        
        // 根据设备方向和相机方向调整触摸点
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        
        // 计算需要补偿的角度
        int totalRotation = 0;
        if (mIsFrontCamera) {
            totalRotation = (info.orientation + degrees) % 360;
            totalRotation = (360 - totalRotation) % 360;  // 前置摄像头需要镜像
        } else {
            totalRotation = (info.orientation - degrees + 360) % 360;
        }
        
        Log.d(TAG, "设备旋转: " + degrees + "度, 相机方向: " + info.orientation + 
                "度, 总旋转: " + totalRotation + "度");
        
        // 根据总旋转角度调整坐标
        if (totalRotation == 90) {
            adjustedX = centerY;
            adjustedY = -centerX;
        } else if (totalRotation == 180) {
            adjustedX = -centerX;
            adjustedY = -centerY;
        } else if (totalRotation == 270) {
            adjustedX = -centerY;
            adjustedY = centerX;
        }
        
        Log.d(TAG, "旋转调整后的坐标: (" + adjustedX + ", " + adjustedY + ")");
        
        // 确保坐标在有效范围内
        adjustedX = Math.max(-1000, Math.min(adjustedX, 1000));
        adjustedY = Math.max(-1000, Math.min(adjustedY, 1000));
        
        // 计算区域边界
        int left = adjustedX - areaSize / 2;
        int right = adjustedX + areaSize / 2;
        int top = adjustedY - areaSize / 2;
        int bottom = adjustedY + areaSize / 2;
        
        // 确保区域不超出边界
        left = Math.max(-1000, left);
        top = Math.max(-1000, top);
        right = Math.min(1000, right);
        bottom = Math.min(1000, bottom);
        
        Rect result = new Rect(left, top, right, bottom);
        Log.d(TAG, "最终对焦区域: " + result.toString());
        return result;
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
            
   

             // 检查设备是否支持区域对焦
            mMaxNumFocusAreas = parameters.getMaxNumFocusAreas();
            mMaxNumMeteringAreas = parameters.getMaxNumMeteringAreas();
            Log.d(TAG, "最大对焦区域数: " + mMaxNumFocusAreas + ", 最大测光区域数: " + mMaxNumMeteringAreas);

            // 设置对焦模式
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                mIsAutoFocusSupported = true;
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mIsAutoFocusSupported = true;
            } else {
                mIsAutoFocusSupported = false;
            }



            // 检查设备是否支持闪光灯
//            List<String> flashModes = parameters.getSupportedFlashModes();
//            if (flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH); // 开启常亮补光
//            } else {
//                Log.e("Camera1", "设备不支持闪光灯补光");
//            }
            
            // 设置预览格式为NV21
            parameters.setPreviewFormat(android.graphics.ImageFormat.NV21);
            
            mCamera.setParameters(parameters);
            
            Log.d(TAG, "摄像头参数设置完成");
            
            initAutoFocus();
            // 如果TextureView已经准备好，立即开始预览
            if (mTextureView.isAvailable()) {
                Log.d(TAG, "TextureView已准备好，开始预览");
                startPreview();
            } else {
                Log.d(TAG, "TextureView尚未准备好，等待onSurfaceTextureAvailable回调");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "设置摄像头参数失败", e);
        }
    }
    
    private void initAutoFocus() {
        mAutoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Log.d(TAG, "自动对焦完成: " + (success ? "成功" : "失败"));
                // 对焦完成后，延迟一段时间再次启动自动对焦
                if (mAutoFocusHandler != null) {
                    //mAutoFocusHandler.postDelayed(mAutoFocusRunnable, 3000);
                }
            }
        };
        
        mAutoFocusHandler = new Handler();
        mAutoFocusRunnable = new Runnable() {
            @Override
            public void run() {
                if (mCamera != null && mIsAutoFocusSupported && !mIsRecording) {
                    try {
                        Camera.Parameters parameters = mCamera.getParameters();
                        if (parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                            // 清除之前的对焦区域
                            parameters.setFocusAreas(null);
                            parameters.setMeteringAreas(null);
                            mCamera.setParameters(parameters);
                            
                            // 启动自动对焦
                            mCamera.autoFocus(mAutoFocusCallback);
                            Log.d(TAG, "启动自动对焦");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "自动对焦失败", e);
                    }
                }
            }
        };
    }

    private void startAutoFocus() {
        if (mAutoFocusHandler != null && mAutoFocusRunnable != null) {
            mAutoFocusHandler.post(mAutoFocusRunnable);
        }
    }

    private void stopAutoFocus() {
        if (mAutoFocusHandler != null && mAutoFocusRunnable != null) {
            mAutoFocusHandler.removeCallbacks(mAutoFocusRunnable);
        }
    }

    private Camera.Size getBestPreviewSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        for (Camera.Size size : sizes) {
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
        
        if (mTextureView.isAvailable()) {
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
            String rtspUrl = "rtsp://127.0.0.1:8554/live/vrcamera";
            
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
            
            // 启动编码器并获取Surface
            mEncoderSurface = mH264Encoder.start();
            
            // 将编码器Surface设置给相机预览
            setupCameraWithEncoderSurface();
            
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
    
    private void setupCameraWithEncoderSurface() {
        if (mCamera == null || mEncoderSurface == null) {
            Log.e(TAG, "相机或编码器Surface为空");
            return;
        }
        
        try {
            // 停止当前预览
            mCamera.stopPreview();
            
            // 尝试使用编码器Surface作为相机预览目标
            // 这种方式直接将相机输出到编码器，性能最好
            try {
                mCamera.setPreviewDisplay(null); // 清除之前的显示目标
                
                // 使用反射设置预览Surface
                java.lang.reflect.Method setPreviewSurfaceMethod = 
                    mCamera.getClass().getMethod("setPreviewSurface", Surface.class);
                setPreviewSurfaceMethod.invoke(mCamera, mEncoderSurface);
                
                Log.d(TAG, "成功设置编码器Surface为相机预览目标");
                
                // 同时设置TextureView显示（如果可能）
                if (mSurfaceTexture != null) {
                    mCamera.setPreviewTexture(mSurfaceTexture);
                }
                
            } catch (Exception e) {
                Log.w(TAG, "setPreviewSurface方法不可用，使用替代方案: " + e.getMessage());
                
                // 如果反射失败，尝试将编码器Surface转换为SurfaceHolder
                setupAlternateSurfaceMethod();
            }
            
            // 重新开始预览
            mCamera.startPreview();
            Log.d(TAG, "相机预览重新启动，包含编码器Surface");
            
        } catch (Exception e) {
            Log.e(TAG, "设置相机编码器Surface失败", e);
            
            // 回退到TextureView模式
            fallbackToTextureViewMode();
        }
    }
    
    private void setupAlternateSurfaceMethod() {
        try {
            // 尝试其他方法设置Surface
            if (mEncoderSurface != null) {
                // 创建一个SurfaceHolder的代理
                SurfaceHolder holder = new SurfaceHolder() {
                    @Override
                    public void addCallback(Callback callback) {}
                    
                    @Override
                    public void removeCallback(Callback callback) {}
                    
                    @Override
                    public boolean isCreating() { return false; }
                    
                    @Override
                    public void setType(int type) {}
                    
                    @Override
                    public void setFixedSize(int width, int height) {}
                    
                    @Override
                    public void setSizeFromLayout() {}
                    
                    @Override
                    public void setFormat(int format) {}
                    
                    @Override
                    public void setKeepScreenOn(boolean screenOn) {}
                    
                    @Override
                    public Canvas lockCanvas() { return null; }
                    
                    @Override
                    public Canvas lockCanvas(Rect dirty) { return null; }
                    
                    @Override
                    public void unlockCanvasAndPost(Canvas canvas) {}
                    
                    @Override
                    public Rect getSurfaceFrame() { return new Rect(0, 0, mPreviewWidth, mPreviewHeight); }
                    
                    @Override
                    public Surface getSurface() { return mEncoderSurface; }
                };
                
                mCamera.setPreviewDisplay(holder);
                Log.d(TAG, "使用SurfaceHolder代理设置编码器Surface");
                
            }
        } catch (Exception e) {
            Log.e(TAG, "替代Surface设置方法失败", e);
        }
    }
    
    private void fallbackToTextureViewMode() {
        Log.d(TAG, "回退到TextureView模式");
        
        try {
            if (mSurfaceTexture != null) {
                mCamera.setPreviewTexture(mSurfaceTexture);
                
                // 设置帧回调来手动编码
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        // 如果需要，这里可以实现手动编码
                        // 但这会增加CPU负担，不推荐
                        Log.v(TAG, "PreviewFrame callback - manual encoding not implemented");
                    }
                });
                
                Log.d(TAG, "设置TextureView预览成功");
            }
        } catch (Exception e) {
            Log.e(TAG, "回退到TextureView模式失败", e);
        }
    }
    
    private void stopRecording() {
        if (mH264Encoder != null) {
            mH264Encoder.stop();
            mH264Encoder = null;
        }
        
        if (mEncoderSurface != null) {
            mEncoderSurface.release();
            mEncoderSurface = null;
        }
        
        mIsRecording = false;
        mBtnStart.setText("开始录制");
        mBtnSwitch.setEnabled(true);
        
        // 重新设置相机预览为只显示模式
        if (mCamera != null && mSurfaceTexture != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, "重新设置相机预览失败", e);
            }
        }
        
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
    
    // TextureView.SurfaceTextureListener 实现
    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable: " + width + "x" + height);
        mSurfaceTexture = surface;
        
        // 如果相机已经初始化，立即开始预览
        if (mCamera != null) {
            startPreview();
        } else {
            Log.d(TAG, "onSurfaceTextureAvailable: 相机尚未初始化，等待相机初始化完成");
        }
    }
    
    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged: " + width + "x" + height);
    }
    
    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        stopPreview();
        mSurfaceTexture = null;
        return true;
    }
    
    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        // 帧更新回调，通常不需要处理
    }
    
    // 原来的SurfaceHolder.Callback方法保留以防万一
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: " + width + "x" + height);
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
        
        if (mSurfaceTexture == null) {
            Log.e(TAG, "startPreview: SurfaceTexture为空，无法启动预览");
            return;
        }
        
        try {
            Log.d(TAG, "startPreview: 设置预览纹理");
            mCamera.setPreviewTexture(mSurfaceTexture);
            
            Log.d(TAG, "startPreview: 开始预览");
            mCamera.startPreview();
            mIsPreviewRunning = true;

             // 启动自动对焦
            if (mIsAutoFocusSupported) {
                startAutoFocus();
            }
            
            Log.d(TAG, "预览启动成功！");
            
        } catch (IOException e) {
            Log.e(TAG, "设置预览纹理失败", e);
        } catch (RuntimeException e) {
            Log.e(TAG, "启动预览运行时异常", e);
        } catch (Exception e) {
            Log.e(TAG, "启动预览失败", e);
        }
    }
    
    private void stopPreview() {
        if (mCamera != null && mIsPreviewRunning) {
            mCamera.stopPreview();
            mIsPreviewRunning = false;
            
            // 停止自动对焦
            stopAutoFocus();
            Log.d(TAG, "预览停止");
        }
    }
    
    private void releaseCamera() {
        stopPreview();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        
        if (mCameraSurface != null) {
            mCameraSurface.release();
            mCameraSurface = null;
        }

        stopAutoFocus();
        mAutoFocusHandler = null;
        mAutoFocusRunnable = null;
        mAutoFocusCallback = null;
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
        if (mTextureView.isAvailable()) {
            startPreview();
        }
    }
} 