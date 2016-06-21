/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.apple;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TimeHelper;

/**
 * @author administrator
 * 
 */
public class AppleBalanceHelper {
    private static final Logger LOG = LoggerFactory.getLogger(AppleBalanceHelper.class);

    /**
     * 构建生产认证url
     * 
     * @param payOrder
     * @return
     */
    public static String assembleProdUrl(PayOrder payOrder) {
        return AppleBalanceConsts.PRODUCVERIFYRECEIPTURl;
    }

    /**
     * 构建测试认证URL
     * 
     * @param payOrder
     * @return
     */
    public static String assembleTestUrl(PayOrder payOrder) {
        return AppleBalanceConsts.TESTVERIFYRECEIPTURl;
    }

    /**
     * 组装receipt
     * 
     * @param payOrder
     * @return
     */
    public static String getReceipt(String token) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(AppleBalanceConsts.RECEIPTDATA, token);
        String receipt = JsonHelper.toJson(paramMap);
        return receipt;
    }

    /**
     * 对验证凭证进行结果处理
     * 
     * @param payOrder
     * @param respStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static PayOrder updatePayOrderByPay(PayOrder payOrder, String respStr) {
        LOG.info("[AppleBalance] updatePayOrderByPay results, respStr={}, payOrder={}", respStr, payOrder,
                "ds:trace:" + payOrder.getAppOrderId());
        @SuppressWarnings("rawtypes")
        Map proMap = JsonHelper.fromJson(respStr, Map.class);
        String status = String.valueOf(proMap.get(AppleBalanceConsts.STATUS));
        if (AppleBalanceConsts.SUCCESS.equals(status)) {
            Map<String, String> data = (Map<String, String>) proMap.get(AppleBalanceConsts.RECEIPT);
            String quantity = data.get(AppleBalanceConsts.QUANTITY);
            String productId = data.get(AppleBalanceConsts.PRODUCT_ID);
            String souceQuantity = JsonHelper.fromJson(payOrder.getProdAddiInfo(), AppleBalanceConsts.QUANTITY);
            if ((payOrder.getProdId().equalsIgnoreCase(productId)) && (souceQuantity.equalsIgnoreCase(quantity))) {
                payOrder.setChDealTime(TimeHelper.get(8, new Date()));
                payOrder.setStatusCode(Consts.SC.SUCCESS);
                payOrder.setStatusMsg(Consts.SUCCDESS_DES);
            } else {
                String errorMsg = " [guzhipay's check] quantity or product_id is not match source_quantity:"
                        + souceQuantity + " quantity:" + quantity + " source_product_id:" + payOrder.getProdId();
                respStr = respStr + errorMsg;
                payOrder.setStatusCode(Consts.SC.FAIL);
                payOrder.setStatusMsg(errorMsg);
            }
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg(Consts.FAIL_DES);
        }
        LOG.info(respStr);
        return payOrder;
    }
}
