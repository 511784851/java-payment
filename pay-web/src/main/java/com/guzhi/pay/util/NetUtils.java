/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * referenced IpFilterInterceptor
 * 
 * @author 
 * 
 */
public class NetUtils {
    private static final String HEADER_X_REAL_IP = "X-Real-IP";
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

    public static String getRequestorIp(HttpServletRequest request) {
        // 两层的Nginx转发中（web专区），这个字段的第一个值才是最接近客户端的IP
        String forwardIps = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (StringUtils.isNotBlank(forwardIps)) {
            return forwardIps.split(",")[0].trim();
        }

        // 一层的Nginx转发中（旧生产环境），这个字段记录的是最接近客户端的IP
        // 两层的Nginx转发中（web专区），这个字段记录的是第一层入口的Nginx的地址
        String realIp = request.getHeader(HEADER_X_REAL_IP);
        if (StringUtils.isNotBlank(realIp)) {
            return realIp;
        }

        // 一层和两层的Nginx转发中，这个字段拿到的是转发给它的Nginx地址（因常在同一台机，故常是127.0.0.1）
        return request.getRemoteAddr();
    }

    /**
     * 获取具体的网卡ip
     * 
     * @return
     */
    public static String getIP() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        boolean isWin = false;
        if (os.startsWith("win") || os.startsWith("Win")) {
            isWin = true;
        }
        try {
            String s = "";
            if (isWin) {
                s = InetAddress.getLocalHost().getHostAddress();
            } else {
                NetworkInterface ni = NetworkInterface.getByName("eth0");
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();
                    s = ip.getHostAddress();
                }
            }
            if (s == null || s.equals("")) {
                return "";
            }
            return s;
        } catch (Exception e) {
            return "";
        }
    }
}
