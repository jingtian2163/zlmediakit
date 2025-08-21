package com.example.zlmediakit;
import android.util.Log;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpShortConnection {
    private static final String TAG = "HttpShortConnection";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;

    public HttpShortConnection() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public interface ResponseCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    /**
     * 发送JSON请求（短连接）
     * @param url 目标URL
     * @param jsonBody JSON请求体
     * @param callback 回调接口
     */
    public void sendJsonRequest(String url, String jsonBody, ResponseCallback callback) {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        Log.i(TAG, "=== 开始发送请求 ===");
        Log.i(TAG, "请求开始时间: " + startTime + " (" + new java.util.Date(startTime) + ")");
        Log.i(TAG, "请求URL: " + url);
        Log.i(TAG, "请求体: " + jsonBody);

        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Log.i(TAG, "Request对象创建完成: " + request.toString());

        // 记录开始执行网络请求的时间
        long executeStartTime = System.currentTimeMillis();
        Log.i(TAG, "开始执行网络请求时间: " + executeStartTime + " (距离开始: " + (executeStartTime - startTime) + "ms)");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                long failureTime = System.currentTimeMillis();
                Log.e(TAG, "=== 请求失败 ===");
                Log.e(TAG, "失败时间: " + failureTime + " (" + new java.util.Date(failureTime) + ")");
                Log.e(TAG, "总耗时: " + (failureTime - startTime) + "ms");
                Log.e(TAG, "网络请求耗时: " + (failureTime - executeStartTime) + "ms");
                Log.e(TAG, "失败原因: " + e.getMessage());
                Log.e(TAG, "异常类型: " + e.getClass().getSimpleName());
                if (e.getCause() != null) {
                    Log.e(TAG, "根本原因: " + e.getCause().getMessage());
                }
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                long responseTime = System.currentTimeMillis();
                Log.i(TAG, "=== 收到响应 ===");
                Log.i(TAG, "响应时间: " + responseTime + " (" + new java.util.Date(responseTime) + ")");
                Log.i(TAG, "总耗时: " + (responseTime - startTime) + "ms");
                Log.i(TAG, "网络请求耗时: " + (responseTime - executeStartTime) + "ms");
                Log.i(TAG, "响应码: " + response.code());
                Log.i(TAG, "响应消息: " + response.message());

                try {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "响应不成功: " + response.code() + " " + response.message());
                        throw new IOException("Unexpected code: " + response);
                    }

                    long bodyStartTime = System.currentTimeMillis();
                    Log.i(TAG, "开始读取响应体时间: " + bodyStartTime);

                    String responseData = response.body().string();

                    long bodyEndTime = System.currentTimeMillis();
                    Log.i(TAG, "响应体读取完成时间: " + bodyEndTime);
                    Log.i(TAG, "读取响应体耗时: " + (bodyEndTime - bodyStartTime) + "ms");
                    Log.i(TAG, "响应体长度: " + responseData.length() + " 字符");
                    Log.i(TAG, "响应内容: " + responseData);

                    callback.onSuccess(responseData);

                    long callbackTime = System.currentTimeMillis();
                    Log.i(TAG, "回调完成时间: " + callbackTime);
                    Log.i(TAG, "=== 请求完全结束，总耗时: " + (callbackTime - startTime) + "ms ===");

                } catch (Exception e) {
                    long errorTime = System.currentTimeMillis();
                    Log.e(TAG, "=== 处理响应时发生异常 ===");
                    Log.e(TAG, "异常时间: " + errorTime + " (总耗时: " + (errorTime - startTime) + "ms)");
                    Log.e(TAG, "异常信息: " + e.getMessage());
                    Log.e(TAG, "异常类型: " + e.getClass().getSimpleName());
                    callback.onFailure(e.getMessage());
                }
            }
        });

        Log.i(TAG, "enqueue调用完成，请求已提交到队列");
    }
}