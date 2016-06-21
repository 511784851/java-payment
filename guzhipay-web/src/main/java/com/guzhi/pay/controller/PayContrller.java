/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.guzhi.pay.business.PayService;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.PayOrder.PayReqVal;
import com.guzhi.pay.domain.PayOrder.PayReqView;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpUtils;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;
import com.guzhi.pay.util.PayOrderUtil;

/**
 * 支付请求controller
 * 
 * @author administrator
 * 
 */
@Controller
public class PayContrller {
    private static final Logger logger = LoggerFactory.getLogger(PayContrller.class);
    @Resource
    private PayService payService;
    @Resource
    private DomainResource resource;

    /**
     * 默认的接收地址，不应该走到这个类来。
     */
    @RequestMapping(value = "/")
    @ResponseBody
    public String defaultNotify() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String pathInfo = request.getPathInfo();
        String errorInfo = "channel pay with wrong url, pathInfo=" + pathInfo;
        logger.error(errorInfo);
        return errorInfo;
    }

    /**
     * 支付请求
     * 
     * @throws IOException
     */
    @RequestMapping(value = "/pay")
    @ModelAttribute
    public String pay(HttpServletRequest request, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        Map<String, String> params = HttpUtils.getParameterMap(request);
        String autoRedirect = "";
        String returnUrl = "";
        PayOrder payOrder = null;
        try {
            @SuppressWarnings("unchecked")
            // 去掉密码敏感信息
            Map<String, String> paramsData = JsonHelper.fromJson(
                    StringHelper.decode(params.get(Consts.DATA), Consts.CHARSET_UTF8), Map.class);
            autoRedirect = paramsData.get(Consts.AUTOREDIRECT);
            returnUrl = paramsData.get(Consts.RETURNURL);
            paramsData.remove(Consts.CARDPASS);
            logger.info("[pay] get pay request,parameterMap:{}", paramsData);
            payOrder = PayOrderUtil.assemblePayOrder(resource, params.get(Consts.DATA), params.get(Consts.SIGN),
                    params.get(Consts.APPID), PayReqView.class, PayReqVal.class, true);
            // TODO 需要对敏感信息打印时进行过滤
            // 支付前参数
            logger.info("[pay] before enter adpter,payorder:{}", payOrder);
            payOrder = payService.pay(payOrder);
            long endTime = System.currentTimeMillis();
            logger.info("[pay] time:{},chid:{},paymethod:{},appid:{},apporderid:{}", endTime - startTime,
                    payOrder.getChId(), payOrder.getPayMethod(), payOrder.getAppId(), payOrder.getAppOrderId(),
                    TraceHelper.getTrace(payOrder));
            // 支付后参数
            logger.info("[pay] process payorder finished,about to response,payorder:{}", payOrder);
            // 银行网管支付方式需要自动跳到对应渠道所对应银行界面
            // TODO 这个if else是不是太复杂？
            if (Consts.TRUE.equalsIgnoreCase(autoRedirect)) {
                if (StringUtils.isNotBlank(payOrder.getPayUrl())
                        && (Consts.Channel.KQ.equalsIgnoreCase(payOrder.getChId())
                                || Consts.Channel.YEEPAY.equalsIgnoreCase(payOrder.getChId()) || Consts.Channel.ZFB
                                    .equalsIgnoreCase(payOrder.getChId()))
                        && (Consts.PayMethod.GATE.equalsIgnoreCase(payOrder.getPayMethod()) || Consts.PayMethod.BALANCE
                                .equalsIgnoreCase(payOrder.getPayMethod()))) {
                    Map<String, String> requestMap = PayOrderUtil.assemblePayUrlMap(payOrder);
                    request.setAttribute("trxMap", requestMap);
                    request.getRequestDispatcher("autopost.jsp").forward(request, response);
                    logger.info("[pay] forward to autopost.jsp,orderid:{}",
                            OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId()));
                    return null;
                }

                if ((StringUtils.isNotBlank(payOrder.getPayUrl()))
                        && (Consts.PayMethod.GATE.equalsIgnoreCase(payOrder.getPayMethod())
                                || Consts.PayMethod.WAPBALANCE.equalsIgnoreCase(payOrder.getPayMethod())
                                || Consts.Channel.PAYPAL.equalsIgnoreCase(payOrder.getChId()) || Consts.Channel.LKLA
                                    .equalsIgnoreCase(payOrder.getChId()))) {
                    response.sendRedirect(payOrder.getPayUrl());
                    // forward(payOrder.getPayUrl(), request, response);
                    return null;
                } else if (Consts.Channel.SZF.equalsIgnoreCase(payOrder.getChId())) {
                    String data = PayOrderUtil.getResp(payOrder);
                    PayOrderUtil.outPutStr(response, data);
                    return null;
                } else {
                    String data = PayOrderUtil.getResp(payOrder);
                    // 如果return url 不为空，则调到return url
                    if (StringUtils.isNotBlank(payOrder.getReturnUrl())) {
                        // forward(UrlHelper.addQuestionMark(payOrder.getReturnUrl())
                        // + data, request, response);
                        response.sendRedirect(UrlHelper.addQuestionMark(payOrder.getReturnUrl()) + data);
                        return null;
                    } else {
                        response.sendRedirect("error.jsp");
                        return null;
                    }
                }
            } else {
                String data = PayOrderUtil.getResp(payOrder);
                PayOrderUtil.outPutStr(response, data);
            }
        } catch (Throwable t) {
            logger.error("[pay] error  occured.message:" + t.getMessage(), t);
            // 如果是签名错误，就直接跳到错误页面
            if ((Consts.TRUE.equalsIgnoreCase(autoRedirect)) && (t instanceof PayException)) {
                String statusCode = ((PayException) t).getStatusCode();
                if (Consts.SC.SECURE_ERROR.equalsIgnoreCase(statusCode)) {
                    try {
                        response.sendRedirect("error.jsp");
                        return null;
                    } catch (IOException e) {
                        logger.error("[pay] response failed because of IOException.IOException:" + t.getMessage(), t);
                    }
                }
            }
            try {
                if (payOrder != null) {
                    payOrder.setCardPass("");
                    logger.error("[pay] error occured.message:" + t.getMessage());
                }
                String data = PayOrderUtil.getErrorResp(params, t, payOrder);
                if (Consts.TRUE.equalsIgnoreCase(autoRedirect)) {
                    if (StringUtils.isNotBlank(returnUrl)) {
                        // forward(UrlHelper.addQuestionMark(returnUrl) + data,
                        // request, response);
                        response.sendRedirect(UrlHelper.addQuestionMark(returnUrl) + data);
                        return null;
                    } else {
                        // forward("error.jsp", request, response);
                        response.sendRedirect("error.jsp");
                        return null;
                    }
                }
                PayOrderUtil.outPutStr(response, data);
            } catch (Throwable e) {
                logger.error("[pay] response failed because of IOException.IOException:" + t.getMessage());
            }
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

    // private void forward(String payUrl, HttpServletRequest request,
    // HttpServletResponse response)
    // throws ServletException, IOException {
    // request.setAttribute("payUrl", payUrl);
    // request.getRequestDispatcher("/forward.jsp").forward(request, response);
    // }
}
