/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.guzhi.pay.channel.unicom.UnicomSmsAdapter;
import com.guzhi.pay.channel.zfb.ZfbHelper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpUtils;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.task.TaskHelper;
import com.guzhi.pay.util.PayOrderUtil;

/**
 * 业务帮助类。
 * 
 * @author administrator
 * 
 */
@Controller
@RequestMapping("/ch/util")
public class UtilController {
    private static final Logger LOG = LoggerFactory.getLogger(UtilController.class);
    @Autowired
    private DomainResource resource;

    /**
     * 协助支付宝移动快捷支付（即wapapp）客户端校验支付宝同步响应的签名。
     * 请求的参数包括appid,sign,data<br>
     * data为json格式串，包括chId,payMethod,resultStatus,memo,result,形如<br>
     * service="mobile.securitypay.pay"&partner="2088111050324223"&
     * _input_charset="utf-8"&notify_url=
     * "https%3A%2F%2Fpayplf-tpay-test.yy.com%2Fch%2Fnotify%2Fzfbwapapp.do"
     * &out_trade_no
     * ="101201311020701362000"&subject="欢聚支付平台测试商品"&payment_type="1"
     * &seller_id="payplf@yy.com"
     * &total_fee="0.01"&success="true"&sign_type="RSA"&sign=
     * "XlQGJ8cXrpEWaztxPEM7HwZDpV+GStHIz+LHUGbIAWAkKIwS82olG5wREYnF8Gi+8L8ipC5KNpI5pDjz5kDBu84ej0CEznndDYX6AgZ6M0v/9oS71DWENqPvM1d79+dk9KV+AEZwoY7QsO7/rWUYp89hfmT/aXPQ9w/MTtghuoM="
     * 
     * @param request
     * @param response
     */
    @RequestMapping("/checkSign")
    public void checkZfbWapAppSign(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = HttpUtils.getParameterMap(request);
        LOG.info("[zfbwapapp_in.checkZfbWapAppSign] get data from app client,params:{}", params);
        String appId = params.get(Consts.APPID);
        String data = params.get(Consts.DATA);
        // String sign = params.get(Consts.SIGN);
        Map<String, String> dataMap = JsonHelper.fromJson(data, Map.class);
        LOG.debug("[zfbwapapp_in.checkZfbWapAppSign] get data from app client,dataMap:{}", dataMap);
        // 客户端的签名在gate实现，后期再和gate联调
        // try {
        // AppInfo appInfo = resource.getAppInfo(appId);
        // if (appInfo != null) {
        // SecureHelper.verifyMd5Sign(appInfo.getKey(), sign, data);
        // }
        // } catch (Exception e) {
        // LOG.warn("[zfbwapapp_in.checkZfbWapAppSign] fail,because of error
        // sign.data:{},sign:{}",
        // data, sign);
        // HttpUtils.niceResponse(response, ZFB_WAP_APP_RESPONSE_FAIL);
        // }
        PayOrder payOrder = ZfbHelper.assembleSynPayWapAppOrder(resource, dataMap);
        if (payOrder != null) {
            resource.updatePayOrder(payOrder);
        }
        AppInfo appInfo = resource.getAppInfo(appId);
        payOrder.setAppInfo(appInfo);
        String result = PayOrderUtil.getResp(payOrder);
        LOG.info("[zfbwapapp_in.checkZfbWapAppSign] return to app,result:{}", result);
        PayOrderUtil.outPutStr(response, result);
        TaskHelper.createAfterPaySuccessTask(resource, payOrder);
    }

    @Autowired
    private UnicomSmsAdapter unicomSmsAdapter;

    @RequestMapping("/checkUnicomSmsVerifyCode")
    public void checkUnicomSmsVerifyCode(@RequestParam String appId, @RequestParam String data,
            HttpServletRequest request, HttpServletResponse response) {
        @SuppressWarnings("unchecked")
        Map<String, String> dataMap = JsonHelper.fromJson(data, Map.class);
        LOG.info("[checkUnicomSmsVerifyCode] appId:{},dataMap:{}", appId, dataMap);

        String verifyCode = dataMap.get("verifyCode");
        String appOrderId = dataMap.get(Consts.APP_ORDER_ID);

        PayOrder order = null;

        try {
            if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(appOrderId)) {
                throw new PayException(Consts.SC.DATA_ERROR, "verifyCode or appOrderId is blank");
            }
            order = resource.getPayOrder(appId, appOrderId);

            decorate(order);

            order = unicomSmsAdapter.confirmPay(order, verifyCode);

            resource.updatePayOrder(order);
            TaskHelper.createAfterPaySuccessTask(resource, order);
        } catch (Exception e) {
            LOG.error("[checkUnicomSmsVerifyCode] error", e);
            if (order == null) {
                order = new PayOrder();
                order.setAppId(appId);
                order.setAppInfo(resource.getAppInfo(appId));
            }
            if (e instanceof PayException) {
                PayException pe = (PayException) e;
                order.setStatusCode(pe.getStatusCode());
                order.setStatusMsg(pe.getStatusMsg());
            } else {
                order.setStatusCode(Consts.SC.CHANNEL_ERROR);
                order.setStatusMsg("支付失败");
            }
        }
        String result = PayOrderUtil.getResp(order);
        PayOrderUtil.outPutStr(response, result);
    }

    private void decorate(PayOrder order) {
        AppInfo appInfo = resource.getAppInfo(order.getAppId());
        order.setAppInfo(appInfo);
        AppChInfo appChInfo = PayOrderUtil
                .siftAppChInfo(resource.getAppChInfo(order.getAppId(), order.getChId(), order.getPayMethod()));
        order.setAppChInfo(appChInfo);
    }
}