/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.guzhi.pay.helper.ThreadHelper;
import com.guzhi.pay.helper.TimeCostHelper;

/**
 * @author administrator
 * 
 */
public class HelperInterceptor extends HandlerInterceptorAdapter {
    private static final String HEADER_X_REAL_IP = "X-Real-IP";
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final Logger HIIDO_LOG = LoggerFactory.getLogger("hiido_statistics");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置IP
        ThreadHelper.setAppIp(getRequestorIp(request));
        // 记录访问时间
        String appId = request.getParameter("appId");
        if (StringUtils.isBlank(appId)) {
            // 不做统计
            return true;
        }
        String uri = request.getRequestURI();
        Map<String, Object> carrier = new HashMap<String, Object>();
        carrier.put("appId", appId);
        carrier.put("uri", uri);
        TimeCostHelper.watch(carrier);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        ThreadHelper.cleanupIpRecords();
        Map<String, Object> carrier = TimeCostHelper.getCarrier();
        if (carrier == null) {
            return;
        }
        HIIDO_LOG.info("tpay;1;" + carrier.get("appId") + ";" + carrier.get("uri") + ";" + TimeCostHelper.getTimeCost()
                + ";;;;;");
        TimeCostHelper.clean();
    }

    private String getRequestorIp(HttpServletRequest request) {
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

}
