package com.guzhi.pay.helper;

/**
 * 目前主要用来帮助记录请求来源的IP地址。
 * 
 * @author administrator
 */
public class ThreadHelper {

    private static ThreadLocal<String> chIp = new ThreadLocal<String>();
    private static ThreadLocal<String> appIp = new ThreadLocal<String>();

    public static void setChIp(String ip) {
        chIp.set(ip);
    }

    public static String getChIp() {
        return chIp.get();
    }

    public static void setAppIp(String ip) {
        appIp.set(ip);
    }

    public static String getAppIp() {
        return appIp.get();
    }

    public static void cleanupIpRecords() {
        chIp.remove();
        appIp.remove();
    }

}
