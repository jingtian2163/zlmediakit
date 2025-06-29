package com.zlmediakit.jni;

import java.util.Map;

public class ZLMediaKit {
    static public native boolean startServer(String sd_path);
    public static native void startServerWithConfig(Map<String, Object> config);
    //static public native boolean stopServer();
    static {
        System.loadLibrary("zlmediakit_jni");
    }
}
