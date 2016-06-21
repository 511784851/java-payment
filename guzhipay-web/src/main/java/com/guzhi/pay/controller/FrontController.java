/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.guzhi.pay.helper.HttpUtils;

/**
 * 跳转控制器。
 * 
 * @author administrator
 * 
 */
@Controller
@RequestMapping(value = "/ch/front")
public class FrontController {
    private static final Logger LOG = LoggerFactory.getLogger(FrontController.class);

    /**
     * 盈华讯方电话支付跳转扭转。
     * 一般来说，参数很多，如果出现问题，
     * 可采取只传包括订单号在内的少数参数，通过查询订单数据库填充前端界面。
     * 
     * @param req
     * @param resp
     * @return
     * @throws IOException
     */
    @RequestMapping("/vpay-tel")
    public String vpayTelRedirect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("[/ch/front/vpayTel] get parameters:{}", HttpUtils.getParameterMap(req));
        return "/channel/vpaytel/pay.jsp";
    }
}