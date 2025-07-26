package com.example.zlmediakit;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ZLMediaKitClient {
    private final OkHttpClient httpClient;
    private final String serverUrl;
    private final String apiSecret;

    public ZLMediaKitClient(String serverUrl, String apiSecret) {
        this.serverUrl = serverUrl.endsWith("/") ?
                serverUrl : serverUrl + "/";
        this.apiSecret = apiSecret;

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取第一个非本地连接的客户端IP
     * @return 第一个非127.0.0.1的peer_ip，如果没有则返回null
     */
    public String getFirstExternalClientIp() throws IOException {
        List<String> ips = getAllClientIps();
        for (String ip : ips) {
            if (!"127.0.0.1".equals(ip)) {
                return ip;
            }
        }
        return null;
    }

    /**
     * 获取所有非本地客户端IP列表
     */
    public List<String> getAllExternalClientIps() throws IOException {
        List<String> allIps = getAllClientIps();
        List<String> externalIps = new ArrayList<>();

        for (String ip : allIps) {
            if (!"127.0.0.1".equals(ip)) {
                externalIps.add(ip);
            }
        }
        return externalIps;
    }

    /**
     * 获取所有客户端IP列表（原始数据，包含127.0.0.1）
     */
    private List<String> getAllClientIps() throws IOException {
        String apiUrl = serverUrl + "index/api/getAllSession";

        Request request = new Request.Builder()
                .url(apiUrl + "?secret=" + apiSecret)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败，HTTP状态码: " + response.code());
            }

            String responseBody = Objects.requireNonNull(response.body()).string();
            return parseAllClientIps(responseBody);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析所有peer_ip（原始数据）
     */
    private List<String> parseAllClientIps(String jsonResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonResponse);

        if (json.getInt("code") != 0) {
            throw new RuntimeException("API返回错误: " + json.optString("msg"));
        }

        List<String> ips = new ArrayList<>();
        JSONArray data = json.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            ips.add(data.getJSONObject(i).getString("peer_ip"));
        }

        return ips;
    }
}