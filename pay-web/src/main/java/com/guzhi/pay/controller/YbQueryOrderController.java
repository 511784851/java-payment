/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.guzhi.pay.channel.gb.gbBalanceConsts;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HMacSHA1;
import com.guzhi.pay.helper.HttpUtils;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.util.PayOrderUtil;

/**
 * @author administrator
 * 
 */

@Controller
@SuppressWarnings({ "unchecked", "rawtypes" })
public class gbQueryOrderController {
    @Autowired
    private DomainResource resource;

    private static final Logger logger = LoggerFactory.getLogger(gbQueryOrderController.class);

    // @RequestMapping("/ch/deposit")
    // public String queryDeposit(HttpServletRequest req, HttpServletResponse
    // resp) {
    // return null;
    // }

    /**
     * 支付宝的异步通知（NotifyUrl）
     */
    // @ResponseBody
    @ModelAttribute
    @RequestMapping(value = "/ch/gb/queryOrder")
    public String query(HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        // TODO: Audit log
        logger.info("[gbQueryOrderController.query] gb check guzhiPay order");
        Map<String, String> params = HttpUtils.getParameterMap(request);
        Map result = new HashMap();
        try {
            result = handlerRequest(request, params);
        } catch (Throwable t) {
            logger.error("exception when handling ReturnUrl!", t);
            result.put(gbBalanceConsts.QUERY_R_CODE, gbBalanceConsts.QUERY_UNKNOWN_ERROR);
        }
        String choroderid = params.get(gbBalanceConsts.ORDER_ID);
        long end = System.currentTimeMillis();
        logger.info("[gbQueryOrderController.query] before time :{},choroderid:{}", end - start, choroderid);
        String responseStr = JsonHelper.toJson(result);
        PayOrderUtil.outPutStr(response, responseStr);
        long lastEnd = System.currentTimeMillis();
        logger.info("[gbQueryOrderController.query] all time :{},choroderid:{},result:{}", lastEnd - start, choroderid,
                responseStr);
        return null;
    }

    private Map handlerRequest(HttpServletRequest request, Map<String, String> params) {
        // 初始化方法结果
        Map result = new HashMap();
        result.put(gbBalanceConsts.QUERY_R_CODE, gbBalanceConsts.QUERY_SUCCESS);
        result.put(gbBalanceConsts.QUERY_R_MESSAGE, "");
        result.put(gbBalanceConsts.QUERY_R_gbUID, 0l);
        result.put(gbBalanceConsts.QUERY_R_AMOUNT, "");
        logger.info("[gbQueryOrderController.query]  map={}", HttpUtils.map2String(params));

        // 取得必须参数
        String orderId = params.get(gbBalanceConsts.ORDER_ID);
        String product = params.get(gbBalanceConsts.PRODUCT);
        String time = params.get(gbBalanceConsts.TIME);
        String expectSign = params.get(gbBalanceConsts.SIGN);
        String appId = null;
        String appOrderId = null;
        if (StringUtils.isBlank(orderId)) {
            logger.error("[gbQueryOrderController.query]appId or appOrderId empty! request={}", params, "ds:trace:"
                    + orderId);
            // 返回未知异常
            result.put(gbBalanceConsts.QUERY_R_CODE, gbBalanceConsts.QUERY_UNKNOWN_ERROR);
            return result;
        }
        String[] ids = orderId.split(Consts.DELIMITER);
        if (ids.length == 2) {
            appId = ids[0];
            appOrderId = ids[1];
        } else {
            appId = OrderIdHelper.getAppId(orderId);
            appOrderId = OrderIdHelper.getAppOrderId(orderId);
        }

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appOrderId)) {
            logger.error("[gbQueryOrderController.query]appId or appOrderId empty! request={}", params, "ds:trace:"
                    + orderId);
            // 返回未知异常
            result.put(gbBalanceConsts.QUERY_R_CODE, gbBalanceConsts.QUERY_UNKNOWN_ERROR);
            return result;
        }
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        // 订单不存在
        if (payOrder == null) {
            logger.error("[gbQueryOrderController.query]order is not exist", "ds:trace:" + orderId);
            // 返回未知异常
            result.put(gbBalanceConsts.QUERY_R_CODE, gbBalanceConsts.QUERY_UNKNOWN_ERROR);
            return result;
        }
        logger.info("[gbQueryOrderController.query]  payOrder={}", payOrder, TraceHelper.getTrace(payOrder));
        // 取到gb的渠道消息
        List<AppChInfo> appChInfos = resource.getAppChInfo(payOrder.getAppId(), Consts.Channel.gb,
                Consts.PayMethod.BALANCE);
        if (CollectionUtils.size(appChInfos) != 1) {
            String msg = "appInfo/appChInfo not found, or get more than one appChInfo!";
            logger.error(" payOrder={},msg={}", payOrder, msg, payOrder, TraceHelper.getTrace(payOrder));
            // 返回未知异常
            result.put(gbBalanceConsts.QUERY_R_CODE, gbBalanceConsts.QUERY_UNKNOWN_ERROR);
            return result;
        }
        AppChInfo gbChInfo = appChInfos.get(0);

        String toBeEncryt = orderId + product + time;
        String addgbKey = JsonHelper.fromJson(gbChInfo.getAdditionalInfo(), gbBalanceConsts.ENCRYPT_ADD_gb_KEY);
        String sign = HMacSHA1.getSignature(toBeEncryt, addgbKey);
        if (!sign.equals(expectSign)) {
            // 返回未知异常
            logger.error(" payOrder={},toBeEncryt={},sign={},expectSign={}", payOrder, toBeEncryt, sign, expectSign,
                    payOrder, TraceHelper.getTrace(payOrder));
            result.put(gbBalanceConsts.QUERY_R_CODE, gbBalanceConsts.QUERY_SIGN_ERROR);
            return result;
        }

        // TODO 需要检查Time?
        result.put(gbBalanceConsts.QUERY_R_CODE, gbBalanceConsts.QUERY_SUCCESS);
        result.put(gbBalanceConsts.QUERY_R_gbUID, JsonHelper.fromJson(payOrder.getUserId(), "gbuid"));
        result.put(gbBalanceConsts.QUERY_R_AMOUNT, String.valueOf(payOrder.getgbAmount().doubleValue()));
        return result;
    }

    public static void main(String[] args) {
        System.out.println(HMacSHA1.getSignature("112-20140410181202550351pay1397184744867", "KEY999999999"));
    }
}
