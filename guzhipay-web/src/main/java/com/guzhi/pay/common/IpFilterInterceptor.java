/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.guzhi.pay.helper.ThreadHelper;
import com.guzhi.pay.util.NetUtils;

/**
 * @author administrator
 * 
 */
public class IpFilterInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = LoggerFactory.getLogger(IpFilterInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = NetUtils.getRequestorIp(request);
        log.info("[IpFilterInterceptor.getRequestorIp,ip:{}]", ip);
        ThreadHelper.setAppIp(ip);
        // if (whiteList.contains(ip)) {
        // return true;
        // } else {
        // response.setContentType("text/plain;charset=utf-8");
        // //TODO audit log?
        // response.getWriter().write("-1"); //FIXME should we return more
        // specific result?
        // return false;
        // }
        return true;
    }
}
