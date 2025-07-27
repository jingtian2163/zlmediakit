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
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.e(TAG, "Request sendJsonRequest: " + request.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + e.getMessage());
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code: " + response);
                    }
                    String responseData = response.body().string();
                    callback.onSuccess(responseData);
                } catch (Exception e) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }
}