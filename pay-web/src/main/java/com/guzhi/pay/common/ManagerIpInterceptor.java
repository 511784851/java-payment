/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.guzhi.pay.util.NetUtils;

/**
 * 管理类专用拦截器。
 * 
 * @author 
 * 
 */
public class ManagerIpInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = LoggerFactory.getLogger(ManagerIpInterceptor.class);
    private List<String> allow;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (null == allow || allow.isEmpty()) {
            return true;
        } else {
            String requestIp = NetUtils.getRequestorIp(request);
            if (allow.contains(requestIp)) {
                log.info("[manager] a legal request from {}", requestIp);
                return true;
            } else {
                log.error("[manager] an illegal request from {}", requestIp, "ds:alarm:illegal request");
                response.getWriter().println("You are an illegal guest, and your net address may be traced.");
                response.getWriter().flush();
                return false;
            }
        }
    }

    public List<String> getAllow() {
        return allow;
    }

    public void setAllow(List<String> allow) {
        this.allow = allow;
    }
}
