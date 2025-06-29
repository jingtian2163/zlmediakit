package com.example.zlmediakit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.zlmediakit.databinding.ActivityMainBinding;
import com.zlmediakit.jni.ZLMediaKit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ZLMediaKit";
    private ActivityMainBinding binding;

    private static final String SSL_DIR = "ssl";
    private static final String PEM_FILE = "zlmediakit.pem";
    private static final String INI_FILE = "zlmediakit.ini";
    private static final String WWW_FILE = "www";

    private static final String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.INTERNET"
    };

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        debugPrintAssetsList(); // 添加这行来打印assets目录内容
        requestPermissions();
        initSSLCertificate();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();

            for (String permission : PERMISSIONS_STORAGE) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(permission);
                }
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[0]), 1);
            }
        }
    }

    private void debugPrintAssetsList() {
        try {
            String[] files = getAssets().list("");
            Log.d(TAG, "Assets directory contents:");
            for (String file : files) {
                Log.d(TAG, "- " + file);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to list assets", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                initSSLCertificate();
            } else {
                showToast("需要相关权限才能运行！");
            }
        }
    }

    private void initSSLCertificate() {
        try {
            // 使用应用私有目录存储证书
            File sslDir = new File(getFilesDir(), SSL_DIR);
            if (!sslDir.exists() && !sslDir.mkdirs()) {
                Log.e(TAG, "Failed to create SSL directory");
                throw new IOException("无法创建SSL目录");
            }

            // 复制 PEM 证书文件
            copyFileFromAssets(PEM_FILE, new File(sslDir, PEM_FILE));

            // 复制 INI 配置文件
            copyFileFromAssets(INI_FILE, new File(sslDir, INI_FILE));

            // 复制 www 目录
            File wwwDir = new File(sslDir, WWW_FILE);
            if (!wwwDir.exists() && !wwwDir.mkdirs()) {
                throw new IOException("无法创建www目录");
            }
            copyAssetsDir("www", wwwDir);

            // 启动服务器
            startZLMediaKit(sslDir.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "SSL certificate initialization failed", e);
            showToast("初始化失败：" + e.getMessage());
        }
    }

    // 复制单个文件
    private void copyFileFromAssets(String assetName, File outFile) throws IOException {
        if (!outFile.exists()) {
            try (InputStream in = getAssets().open(assetName);
                 OutputStream out = new FileOutputStream(outFile)) {
                Log.d(TAG, "Copying file from assets: " + assetName + " to: " + outFile.getAbsolutePath());
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
            }
        }

        if (!outFile.canRead()) {
            if (!outFile.setReadable(true, true)) {
                throw new IOException("Cannot set file readable: " + outFile.getAbsolutePath());
            }
        }
    }

    // 递归复制目录
    private void copyAssetsDir(String path, File outDir) throws IOException {
        String[] assets = getAssets().list(path);
        if (assets == null) {
            return;
        }

        for (String asset : assets) {
            String subPath = path + "/" + asset;
            String[] subAssets = getAssets().list(subPath);

            if (subAssets != null && subAssets.length > 0) {
                // 是目录
                File newDir = new File(outDir, asset);
                if (!newDir.exists() && !newDir.mkdirs()) {
                    throw new IOException("无法创建目录: " + newDir.getAbsolutePath());
                }
                copyAssetsDir(subPath, newDir);
            } else {
                // 是文件
                File outFile = new File(outDir, asset);
                copyFileFromAssets(subPath, outFile);
            }
        }
    }


    // 修改验证方法，添加更多日志
    private boolean validatePEMCertificate(File pemFile) {
        try {
            // 读取并打印文件内容（调试用）
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pemFile)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            Log.d(TAG, "PEM file content length: " + content.length());

            // 检查文件内容
            String pemContent = content.toString();
            if (!pemContent.contains("BEGIN CERTIFICATE")) {
                Log.e(TAG, "PEM file does not contain certificate");
                return false;
            }

            if (!pemContent.contains("BEGIN RSA PRIVATE KEY")) {
                Log.e(TAG, "PEM file does not contain private key");
                return false;
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to validate PEM certificate", e);
            return false;
        }
    }

    private void copyAssetsToFiles(String assetName, File outFile) throws IOException {
        try (InputStream in = getAssets().open(assetName);
             OutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        }
    }

    private void startZLMediaKit(String sslDir) {
        new Thread(() -> {
            try {
                Map<String, Object> config = new HashMap<>();
                config.put("ssl.cert_file", new File(sslDir, PEM_FILE).getAbsolutePath());
                // 使用同一个PEM文件作为证书和私钥
                config.put("ssl.key_file", new File(sslDir, PEM_FILE).getAbsolutePath());
                config.put("ssl.verify_client", false);

               // ZLMediaKit.startServerWithConfig(config);
                ZLMediaKit.startServer(sslDir);
                showToast("ZLMediaKit服务器启动成功！");
            } catch (Exception e) {
                e.printStackTrace();
                showToast("服务器启动失败：" + e.getMessage());
            }
        }).start();
    }

    private void showToast(final String message) {
        mainHandler.post(() ->
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ZLMediaKit.stopServer();
    }
}