package com.example.zlmediakit;

/**
 * 事件类型枚举
 */
public enum EventType {
    TAKEPIC("TAKEPIC", "拍照"),
    STARTRECORDMP4("STARTRECORDMP4", "开始录制MP4"),
    STOPRECORDMP4("STOPRECORDMP4", "停止录制MP4");

    private final String eventName;
    private final String displayName;

    EventType(String eventName, String displayName) {
        this.eventName = eventName;
        this.displayName = displayName;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 生成JSON请求体
     * @param value 事件值
     * @return JSON字符串
     */
    public String toJsonBody(String value) {
        return "{\"eventtype\":\"" + eventName + "\",\"value\":\"" + value + "\"}";
    }

    /**
     * 生成JSON请求体（默认值为"1"）
     * @return JSON字符串
     */
    public String toJsonBody() {
        return toJsonBody("1");
    }
}
