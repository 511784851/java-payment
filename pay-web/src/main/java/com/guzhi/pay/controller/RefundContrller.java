/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guzhi.pay.business.PayService;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.PayOrder.RefundReqVal;
import com.guzhi.pay.domain.PayOrder.RefundReqView;
import com.guzhi.pay.helper.HttpUtils;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.util.PayOrderUtil;

/**
 * @author administrator
 * 
 */
@Controller
public class RefundContrller {
    private static final Logger logger = LoggerFactory.getLogger(RefundContrller.class);
    @Resource
    private PayService payService;
    @Resource
    private DomainResource resource;

    /**
     * 支付请求
     */
    @RequestMapping(value = "/refund")
    @ResponseBody
    @ModelAttribute
    public String refund(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> params = HttpUtils.getParameterMap(request);
        logger.info("refund map={}", HttpUtils.map2String(params), "ds:trace:0");
        PayOrder payOrder = null;
        try {
            payOrder = PayOrderUtil.assemblePayOrder(resource, params.get(Consts.DATA), params.get(Consts.SIGN),
                    params.get(Consts.APPID), RefundReqView.class, RefundReqVal.class, false);
            logger.info("[refund] before payorder={}", payOrder, TraceHelper.getTrace(payOrder));
            payOrder = payService.refund(payOrder);
            logger.info("[refund] after payorder={}", payOrder, TraceHelper.getTrace(payOrder));
            String data = PayOrderUtil.getResp(payOrder);
            PayOrderUtil.outPutStr(response, data);
        } catch (Throwable t) {
            logger.error("Throwable={}", t.getMessage());
            PayOrderUtil.outPutStr(response, PayOrderUtil.getErrorResp(params, t, payOrder));
        }
        return null;
    }

    public PayService getPayService() {
        return payService;
    }

    public void setPayService(PayService payService) {
        this.payService = payService;
    }

    public DomainResource getResource() {
        return resource;
    }

    public void setResource(DomainResource resource) {
        this.resource = resource;
    }

}
