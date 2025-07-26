package com.example.zlmediakit;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpLongConnectionClient {
    private static final String TAG = "HttpLongConnection";
    private static final int HEARTBEAT_INTERVAL = 5; // 心跳间隔(秒)
    private static final int HEARTBEAT_TIMEOUT = 10; // 心跳超时(秒)
    private static final int MAX_RETRIES = 3; // 最大重试次数

    private OkHttpClient client;
    private String serverUrl;
    private boolean isRunning = false;
    private WebSocket webSocket;
    private HeartbeatTask heartbeatTask;

    public HttpLongConnectionClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(HEARTBEAT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HEARTBEAT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(HEARTBEAT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;
        connectWebSocket();
        startHeartbeat();
    }

    public void stop() {
        isRunning = false;
        if (heartbeatTask != null) {
            heartbeatTask.stop();
        }
        if (webSocket != null) {
            webSocket.close(1000, "Normal closure");
        }
    }

    private void connectWebSocket() {
        Request request = new Request.Builder()
                .url(serverUrl.replace("http", "ws") + "/ws") // WebSocket端点
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.i(TAG, "WebSocket connected");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket connection failed", t);
                if (isRunning) {
                    // 无限重连
                    new Handler(Looper.getMainLooper()).postDelayed(() -> connectWebSocket(), 5000);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.i(TAG, "Received message: " + text);
                // 处理服务器消息
            }
        });
    }

    private void startHeartbeat() {
        heartbeatTask = new HeartbeatTask();
        new Thread(heartbeatTask).start();
    }

    public void sendRequest(String path, String jsonBody, final HttpCallback callback) {
        Request request = new Request.Builder()
                .url(serverUrl + path)
                .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                .build();

        sendWithRetry(client, request, callback, 0);
    }

    private void sendWithRetry(OkHttpClient client, Request request, HttpCallback callback, int retryCount) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (retryCount < MAX_RETRIES) {
                    Log.w(TAG, "Request failed, retrying... (" + (retryCount + 1) + ")");
                    sendWithRetry(client, request, callback, retryCount + 1);
                } else {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (retryCount < MAX_RETRIES) {
                        Log.w(TAG, "Request failed, retrying... (" + (retryCount + 1) + ")");
                        sendWithRetry(client, request, callback, retryCount + 1);
                        return;
                    }
                }
                callback.onResponse(response.body().string());
            }
        });
    }

    private class HeartbeatTask implements Runnable {
        private boolean stopped = false;

        public void stop() {
            stopped = true;
        }

        @Override
        public void run() {
            while (!stopped && isRunning) {
                try {
                    if (webSocket != null) {
                        webSocket.send("{\"type\":\"heartbeat\"}");
                        Log.d(TAG, "Heartbeat sent");
                    }
                    Thread.sleep(HEARTBEAT_INTERVAL * 1000);
                } catch (Exception e) {
                    Log.e(TAG, "Heartbeat error", e);
                }
            }
        }
    }

    public interface HttpCallback {
        void onResponse(String response);
        void onFailure(Throwable t);
    }
}