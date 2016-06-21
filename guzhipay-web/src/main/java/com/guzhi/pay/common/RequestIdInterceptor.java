/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.common;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

//import com.yy.darkseer.utils.ThreadRequestId;

/**
 * @author administrator
 * 
 */
public class RequestIdInterceptor extends HandlerInterceptorAdapter {

    // private static Random random = new Random(1000000000);
    private static final Logger logger = LoggerFactory.getLogger(RequestIdInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logger.info("[tpay] url:{}", request.getRequestURI(), "ds:stat:1");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, Object obj,
            ModelAndView modelandview) throws Exception {
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setLenient(false);
        for (int i = 0; i < 1000; i++) {
            // System.out.println(sdf.format(new Date()));
            // System.out.println(TimeHelper.get(8, new Date()) +
            // String.valueOf(Math.abs(random.nextInt())));
        }

    }
}