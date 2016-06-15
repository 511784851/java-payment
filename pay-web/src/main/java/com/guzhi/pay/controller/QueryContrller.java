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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guzhi.pay.business.PayService;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.PayOrder.QueryReqVal;
import com.guzhi.pay.domain.PayOrder.QueryReqView;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpUtils;
import com.guzhi.pay.util.PayOrderUtil;

/**
 * @author administrator
 * 
 */
@Controller
public class QueryContrller {
    private static final Logger logger = LoggerFactory.getLogger(QueryContrller.class);
    @Resource
    private PayService payService;
    @Resource
    private DomainResource resource;

    /**
     * 支付请求
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public String query(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = HttpUtils.getParameterMap(request);
        logger.info("query params:{}", params);
        PayOrder payOrder = null;
        try {
            payOrder = PayOrderUtil.assemblePayOrder(resource, params.get(Consts.DATA), params.get(Consts.SIGN),
                    params.get(Consts.APPID), QueryReqView.class, QueryReqVal.class, false);
            logger.info("[query] before enter adapter payorder={}", payOrder);
            payOrder = payService.query(payOrder);
            logger.info("[query] finish, payorder={}", payOrder);
            return PayOrderUtil.getResp(payOrder);
        } catch (Throwable t) {
            logger.error("[QueryController] Throwable message={}", t.getMessage(), t);
            if (t instanceof PayException) {
                PayException payException = (PayException) t;
                if (Consts.SC.CONN_ERROR.equalsIgnoreCase(payException.getStatusCode())
                        || Consts.SC.CHANNEL_ERROR.equalsIgnoreCase(payException.getStatusCode())) {
                    payOrder = PayOrderUtil.assemblePayOrder(resource, payOrder.getAppId(), payOrder.getAppOrderId());
                    return PayOrderUtil.getResp(payOrder);
                }
            }
            return PayOrderUtil.getErrorResp(params, t, payOrder);
        }
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
