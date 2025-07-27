package com.example.zlmediakit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Camera2Activity extends Activity {
    private static final String TAG = "Camera2Activity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    // UI组件
    private TextureView mTextureView;
    private Button mBtnScaleBig;
    private Button mBtnScaleSmall;
    private Button mBtnStart;
    private Button mBtnFlash;
    private Button mBtntakepic;
    
    // Camera2 相关
    private String mCameraId;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    private Size mPreviewSize;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private CameraManager mCameraManager;
    private CameraCharacteristics mCharacteristics;
    
    // 编码器相关
    private H264Encoder mH264Encoder;
    private Surface mEncoderSurface;
    private boolean mIsRecording = false;
    private boolean mDestroyting = false;
    private boolean mIsFrontCamera = false;
    
    // 自动对焦相关
    private boolean mManualFocusEngaged = false;
    private int mSensorOrientation;
    private Rect mActiveArraySize;
    private boolean mFlashLightEnabled = false;
    private static final float LOW_LIGHT_THRESHOLD = 0.3f;

    private float mCurrentZoom = 1.0f; // 当前缩放比例，默认1
    private static final float MAX_ZOOM = 4.0f; // 最大缩放4倍
    private static final float MIN_ZOOM = 1.0f; // 最小缩放1倍

    private Handler mDelayHandler = new Handler();
    private Runnable mDelayRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        
        // 设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        initViews();
        checkPermissions();
       
    }

    private void initViews() {
        mTextureView = findViewById(R.id.texture_view);
        mBtnScaleBig = findViewById(R.id.btn_scale_big);
        mBtnScaleSmall = findViewById(R.id.btn_scale_small);
        mBtnStart = findViewById(R.id.btn_start_record);
        mBtnFlash = findViewById(R.id.btn_flash);
        mBtntakepic= findViewById(R.id.btn_takepic);
        
        // 设置TextureView监听器
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        
        // 设置触摸监听实现点击对焦
        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handleTouchFocus(event);
                    return true;
                }
                return false;
            }
        });

        
        // 切换摄像头按钮
        mBtnScaleBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomIn();
            }
        });

        mBtnScaleSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOut();
            }
        });

        //startRecordingWithDelay();
        // 开始/停止录制按钮
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRecording) {
                    stopRecording();
                } else {
                    startRecording();
                    //startRecordingWithDelay();
                }
            }
        });

        mBtnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlashLightEnabled = !mFlashLightEnabled;
                setFlashLight(mFlashLightEnabled);
                mBtnStart.setText(mFlashLightEnabled ? "关闭补光" : "开启补光");
            }
        });

        mBtntakepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            ZLMediaKitClient client = new ZLMediaKitClient(
                                    "http://127.0.0.1:8080",
                                    "nXduZZCDcoxyHzNVY0CeRASDYPQII5lG"
                            );
                            String firstIp = "";
                            String requestIp;
                            HttpShortConnection     httpClient = new HttpShortConnection();
                            try {
                                // 获取第一个客户端IP
                                firstIp = client.getFirstExternalClientIp();
                                System.out.println("第一个客户端IP: " + firstIp);
                            } catch (IOException e) {
                                System.err.println("请求失败: " + e.getMessage());
                            }

                            if(!firstIp.isEmpty())
                            {
                                // 初始化HTTP客户端
                                requestIp = "http://" + firstIp + ":8081";
                                Log.e(TAG, "Request requestIp:" + requestIp);
                            }else {
                                return;
                            }

                            // 准备请求数据
                            String jsonBody = "{\"eventtype\":\"TAKEPIC\",\"value\":\"1\"}";
                            // 发送请求
                            httpClient.sendJsonRequest(requestIp, jsonBody, new HttpShortConnection.ResponseCallback() {
                                            @Override
                                            public void onSuccess(String response) {
                                                Log.i(TAG, "请求成功: " + response);
                                            }

                                            @Override
                                            public void onFailure(String error) {
                                                Log.e(TAG, "请求失败: " + error);
                                            }
                                        });
                                Log.d(TAG, "mBtntakepic take pic");
                            }
                    }).start();
            }
        });
    }

    // 添加延时开始录制的方法
    private void startRecordingWithDelay() {
        // 创建延时任务
        mDelayRunnable = new Runnable() {
            @Override
            public void run() {
                startRecording(); // 实际开始录制
            }
        };

        // 延时2000ms执行
        mDelayHandler.postDelayed(mDelayRunnable, 1000);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown keyCode:" + keyCode+",event:"+event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                // 可能是旋钮正转
                zoomIn();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // 可能是旋钮反转
                zoomOut();
                return true;
            // 其他特定设备的按键码
            case KeyEvent.KEYCODE_STEM_1:
                // 某些设备的专用旋钮按键
                //handleStemEvent(event);
                return true;
            case KeyEvent.KEYCODE_STEM_2:
                // 另一个专用旋钮按键
                //handleStemEvent(event);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    private boolean isZoomSupported() {
        if (mCharacteristics == null) {
            return false;
        }
        Float maxZoom = mCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
        return maxZoom != null && maxZoom >= 1.0f;
    }

    /**
     * 获取当前缩放比例
     */
    public float getCurrentZoom() {
        return mCurrentZoom;
    }

    /**
     * 放大2倍
     */
    public void zoomIn() {
        float targetZoom = mCurrentZoom * 2;
        if (targetZoom > MAX_ZOOM) {
            Log.d(TAG, "已达到最大缩放倍数: " + MAX_ZOOM);
            return;
        }
        setZoom(targetZoom);
    }

     /**
     * 缩小2倍
     */
    public void zoomOut() {
        float targetZoom = mCurrentZoom / 2;
        if (targetZoom < MIN_ZOOM) {
            Log.d(TAG, "已达到最小缩放倍数: " + MIN_ZOOM);
            return;
        }
        setZoom(targetZoom);
    }

    /**
     * 设置具体缩放级别
     * @param zoomLevel 缩放级别，范围1.0-4.0
     */
    private void setZoom(float zoomLevel) {
        if (!isZoomSupported()) {
            Log.w(TAG, "该设备不支持缩放");
            return;
        }
        
        if (mPreviewRequestBuilder == null || mCaptureSession == null || mActiveArraySize == null) {
            Log.w(TAG, "相机未初始化完成，无法缩放");
            return;
        }
        
        // 限制缩放范围
        zoomLevel = Math.max(MIN_ZOOM, Math.min(zoomLevel, MAX_ZOOM));
        
        // 计算缩放区域（中心缩放）
        int centerX = mActiveArraySize.width() / 2;
        int centerY = mActiveArraySize.height() / 2;
        int deltaX = (int)((0.5f * mActiveArraySize.width()) / zoomLevel);
        int deltaY = (int)((0.5f * mActiveArraySize.height()) / zoomLevel);
        
        Rect zoomRect = new Rect(
            Math.max(0, centerX - deltaX),
            Math.max(0, centerY - deltaY),
            Math.min(mActiveArraySize.width(), centerX + deltaX),
            Math.min(mActiveArraySize.height(), centerY + deltaY)
        );
        
        try {
            // 设置缩放区域
            mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect);
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mBackgroundHandler);
            
            // 更新当前缩放比例
            mCurrentZoom = zoomLevel;
            Log.d(TAG, "缩放设置成功，当前比例: " + mCurrentZoom);
        } catch (CameraAccessException e) {
            Log.e(TAG, "设置缩放失败", e);
        }
    }
    
    /**
     * 重置为原始大小(1倍)
     */
    public void resetZoom() {
        setZoom(1.0f);
    }

    /**
     * 打开或关闭补光灯
     * @param enable true 打开补光灯，false 关闭补光灯
     */
    private void setFlashLight(boolean enable) {
        if (mCameraDevice == null || mPreviewRequestBuilder == null || mCaptureSession == null) {
            Log.e(TAG, "相机未初始化，无法控制补光灯");
            return;
        }

        try {
            // 检查设备是否支持闪光灯
            Boolean flashAvailable = mCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (flashAvailable == null || !flashAvailable) {
                Toast.makeText(this, "设备不支持闪光灯", Toast.LENGTH_SHORT).show();
                return;
            }

            // 设置闪光灯模式
            if (enable) {
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            } else {
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            }

            // 应用设置
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mBackgroundHandler);
            
            Log.d(TAG, "补光灯状态: " + (enable ? "开启" : "关闭"));

        } catch (CameraAccessException e) {
            Log.e(TAG, "控制补光灯失败", e);
            Toast.makeText(this, "控制补光灯失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissions() {
        List<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.CAMERA);
        permissionList.add(Manifest.permission.RECORD_AUDIO);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissionList.add(Manifest.permission.READ_MEDIA_VIDEO);
        } else {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        
        String[] permissions = permissionList.toArray(new String[0]);
        
        boolean needRequest = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                needRequest = true;
                break;
            }
        }
        
        if (needRequest) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            setupCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                setupCamera();
            } else {
                Toast.makeText(this, "需要相机和存储权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void setupCamera() {
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        
        try {
            // 获取可用的摄像头列表
            String[] cameraIds = mCameraManager.getCameraIdList();
            
            // 选择默认摄像头（后置）
            for (String cameraId : cameraIds) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);

                // 检查对焦能力
                int[] afModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
                if (afModes == null || afModes.length == 0) {
                    Log.d(TAG, "摄像头 " + cameraId + " 不支持自动对焦");
                    continue;
                }

                boolean hasContinuousVideo = false;
                for (int mode : afModes) {
                    if (mode == CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_VIDEO) {
                        hasContinuousVideo = true;
                        break;
                    }
                }

//                if (!hasContinuousVideo) {
//                    Log.d(TAG, "摄像头 " + cameraId + " 不支持CONTINUOUS_VIDEO对焦模式");
//                    continue;
//                }
                
                 // 检查闪光灯支持
                Boolean flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (flashAvailable == null || !flashAvailable) {
                    Log.d(TAG, "摄像头 " + cameraId + " 不支持闪光灯");
                    continue;
                }

                if (!mIsFrontCamera && facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    mCameraId = cameraId;
                    mCharacteristics = characteristics;
                    break;
                } else if (mIsFrontCamera && facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    mCameraId = cameraId;
                    mCharacteristics = characteristics;
                    break;
                }
            }
            
            // 获取摄像头支持的配置
            StreamConfigurationMap map = mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map != null) {
                // 选择合适的预览尺寸
                Size[] outputSizes = map.getOutputSizes(SurfaceTexture.class);
                mPreviewSize = chooseOptimalSize(outputSizes, 1920, 1080);
                Log.d(TAG, "选择的预览尺寸: " + mPreviewSize.getWidth() + "x" + mPreviewSize.getHeight());
            }
            
            // 获取传感器方向和有效区域
            mSensorOrientation = mCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            mActiveArraySize = mCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            
        } catch (CameraAccessException e) {
            Log.e(TAG, "设置相机失败", e);
        }
    }

    private Size chooseOptimalSize(Size[] choices, int targetWidth, int targetHeight) {
        List<Size> suitable = new ArrayList<>();
        
        // 首先尝试找到目标尺寸
        for (Size option : choices) {
            if (option.getWidth() == targetWidth && option.getHeight() == targetHeight) {
                return option;
            }
            // 收集所有16:9比例的尺寸
            if (option.getWidth() * 9 == option.getHeight() * 16) {
                suitable.add(option);
            }
        }
        
        // 如果没有找到目标尺寸，选择最接近的16:9尺寸
        if (!suitable.isEmpty()) {
            return Collections.max(suitable, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                            (long) rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        
        // 如果还是没有，返回最大的尺寸
        return choices[0];
    }

    // TextureView监听器
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    private void openCamera() {
        if (mCameraId == null) {
            Log.e(TAG, "Camera ID is null");
            return;
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        try {
            mCameraManager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "打开相机失败", e);
        }
    }

    // 相机状态回调
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
            finish();
        }
    };

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            
            // 设置默认缓冲区大小
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            
            // 创建预览Surface
            Surface previewSurface = new Surface(texture);
            
            // 创建Surface列表
            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(previewSurface);
            
            // 如果正在录制，添加编码器Surface
            if (mIsRecording && mEncoderSurface != null) {
                surfaces.add(mEncoderSurface);
            }
            
            // 创建预览请求构建器
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(previewSurface);
            
            // 如果正在录制，也添加编码器Surface到预览请求
            if (mIsRecording && mEncoderSurface != null) {
                mPreviewRequestBuilder.addTarget(mEncoderSurface);
            }
            
            // 设置自动对焦模式
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            
            // 创建相机捕获会话
            mCameraDevice.createCaptureSession(surfaces,
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if (mCameraDevice == null) {
                                return;
                            }
                            
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // 设置自动对焦、自动曝光、自动白平衡
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE,
                                        CameraMetadata.CONTROL_MODE_AUTO);
                                
                                // 构建预览请求
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                
                                // 开始预览
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        null, mBackgroundHandler);
                                
                            } catch (CameraAccessException e) {
                                Log.e(TAG, "创建预览失败", e);
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(Camera2Activity.this, "配置失败", Toast.LENGTH_SHORT).show();
                        }
                    }, null);
            
        } catch (CameraAccessException e) {
            Log.e(TAG, "创建预览会话失败", e);
        }
    }

    private void handleTouchFocus(MotionEvent event) {
        if (mCameraDevice == null || mCharacteristics == null || mActiveArraySize == null) {
            Log.w(TAG, "handleTouchFocus: camera or characteristics not ready");
            return;
        }
        try {
            mManualFocusEngaged = true;
            float x = event.getX();
            float y = event.getY();
            Log.d(TAG, "handleTouchFocus: touch at (" + x + ", " + y + ")");
            MeteringRectangle focusAreaTouch = getFocusArea(x, y);
            Log.d(TAG, "handleTouchFocus: focusAreaTouch=" + focusAreaTouch.toString());
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_OFF);
            Log.d(TAG, "handleTouchFocus: send CANCEL trigger");
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            if (isMeteringAreaAFSupported()) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS,
                        new MeteringRectangle[]{focusAreaTouch});
                Log.d(TAG, "handleTouchFocus: set CONTROL_AF_REGIONS");
            }
            if (isMeteringAreaAESupported()) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS,
                        new MeteringRectangle[]{focusAreaTouch});
                Log.d(TAG, "handleTouchFocus: set CONTROL_AE_REGIONS");
            }
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_AUTO);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            Log.d(TAG, "handleTouchFocus: send START trigger");
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "触摸对焦失败", e);
        }
    }

    private MeteringRectangle getFocusArea(float x, float y) {
        // 计算对焦区域大小（使用传感器尺寸的1/8）
        int areaSize = 200;
        
        // 将触摸坐标转换为传感器坐标
        float[] pointF = new float[]{x, y};
        
        // 获取TextureView的尺寸
        int viewWidth = mTextureView.getWidth();
        int viewHeight = mTextureView.getHeight();
        
        // 转换坐标到传感器坐标系
        int sensorX = (int) ((pointF[0] / viewWidth) * mActiveArraySize.width());
        int sensorY = (int) ((pointF[1] / viewHeight) * mActiveArraySize.height());
        
        // 确保对焦区域在有效范围内
        int left = Math.max(0, sensorX - areaSize / 2);
        int top = Math.max(0, sensorY - areaSize / 2);
        int right = Math.min(mActiveArraySize.width(), left + areaSize);
        int bottom = Math.min(mActiveArraySize.height(), top + areaSize);
        
        Log.d(TAG, "getFocusArea: sensorX=" + sensorX + ", sensorY=" + sensorY + ", left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);
        return new MeteringRectangle(left, top, right - left, bottom - top, 1000);
    }

    private boolean isMeteringAreaAFSupported() {
        Integer maxRegions = mCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
        return maxRegions != null && maxRegions > 0;
    }

    private boolean isMeteringAreaAESupported() {
        Integer maxRegions = mCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
        return maxRegions != null && maxRegions > 0;
    }

    // 捕获回调
    private final CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            Log.d(TAG, "onCaptureCompleted: manualFocusEngaged=" + mManualFocusEngaged);
            if (mManualFocusEngaged) {
                mManualFocusEngaged = false;
                Log.d(TAG, "onCaptureCompleted: restoring CONTINUOUS_VIDEO AF mode in 3s");
                mBackgroundHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
                            Log.d(TAG, "onCaptureCompleted: setRepeatingRequest CONTINUOUS_VIDEO");
                            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(),
                                    null, mBackgroundHandler);
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "恢复自动对焦失败", e);
                        }
                    }
                }, 3000);
            }
        }
    };

    private void switchCamera() {
        closeCamera();
        mIsFrontCamera = !mIsFrontCamera;
        setupCamera();
        
        if (mTextureView.isAvailable()) {
            openCamera();
        }
    }

    private void startRecording() {
        if (mCameraDevice == null) {
            Toast.makeText(this, "相机未初始化", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Camera2Activity.this, "RTSP推流已启动", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                @Override
                public void onStreamStopped() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Camera2Activity.this, "RTSP推流已停止", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                @Override
                public void onStreamError(String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Camera2Activity.this, "推流错误: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };
            
            // 初始化H264编码器
            mH264Encoder = new H264Encoder(mPreviewSize.getWidth(), mPreviewSize.getHeight(),
                    outputFile.getAbsolutePath(), rtspUrl, streamCallback);
            
            // 启动编码器并获取Surface
            mEncoderSurface = mH264Encoder.start();
            
            mIsRecording = true;
            mBtnStart.setText("停止录制");
            //mBtnSwitch.setEnabled(false);
            
            // 重新创建相机会话，包含编码器Surface
            createCameraPreviewSession();
            
            Toast.makeText(this, "开始录制和推流", Toast.LENGTH_SHORT).show();
            
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
        
        if (mEncoderSurface != null) {
            mEncoderSurface.release();
            mEncoderSurface = null;
        }
        
        mIsRecording = false;
        mBtnStart.setText("开始录制");
        //mBtnSwitch.setEnabled(true);
        
        // 重新创建相机会话，移除编码器Surface
        if(!mDestroyting) {
            createCameraPreviewSession();
        }
        
        Toast.makeText(this, "录制结束", Toast.LENGTH_SHORT).show();
    }

    private File createOutputFile() throws IOException {
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dir = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "ZLMediaKit");
        } else {
            dir = new File(Environment.getExternalStorageDirectory(), "ZLMediaKit");
        }
        
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("无法创建输出目录: " + dir.getAbsolutePath());
        }
        
        String fileName = "camera2_" + System.currentTimeMillis() + ".h264";
        File file = new File(dir, fileName);
        
        Log.d(TAG, "输出文件: " + file.getAbsolutePath());
        return file;
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        
        if (mTextureView.isAvailable()) {
            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         mDestroyting = true;
         Toast.makeText(this, "当前界面不允许退出", Toast.LENGTH_SHORT).show();
         if (mIsRecording) {
             stopRecording();
         }
    }
}
