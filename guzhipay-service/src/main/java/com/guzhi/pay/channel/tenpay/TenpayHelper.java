/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.tenpay;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TimeHelper;

/**
 * 财付通帮助类.
 * 
 * @author administrator
 * 
 */
public class TenpayHelper {
    private static final Logger LOG = LoggerFactory.getLogger(TenpayHelper.class);

    private final static String TRADE_STATE_SUCCESS = "0";
    private final static String RET_CODE_SUCCESS = "0";

    /**
     * 生成财付通签名。签名字符串生成规则如下:
     * <ul>
     * <li>参数值不编码</li>
     * <li>参数名Ascii正排序</li>
     * <li>参数值非空</li>
     * </ul>
     * 
     * @param dataMap
     * @param secretKey
     * @return
     */
    public static String generateSign(Map<String, String> dataMap, String secretKey) {
        String toBeSigned = StringHelper.assembleResqStr(dataMap, null, true, false) + "&key=" + secretKey;
        return MD5Utils.getMD5(toBeSigned).toUpperCase();
    }

    /**
     * 根据财付通查询响应更新订单.
     */
    public static void updatePayOrderByQuery(PayOrder payOrder, String responseString) {
        Map<String, String> dataMap = transXml2Map(responseString);
        updatePayOrder(payOrder, dataMap);
    }

    /**
     * 根据财付通前台跳转更新订单.
     */
    public static void updatePayOrderByReturn(PayOrder payOrder, Map<String, String> dataMap) {
        updatePayOrder(payOrder, dataMap);
    }

    /**
     * 根据财付通通知更新订单.
     * 注意还有到财付通回查订单.
     * 
     * @param payOrder
     * @param dataMap
     */
    public static void updatePayOrderByNotify(PayOrder payOrder, Map<String, String> dataMap) {
        Map<String, String> verifyRequestMap = new HashMap<String, String>();
        verifyRequestMap.put(TenpayConsts.KEY_SIGN_TYPE, "MD5");
        verifyRequestMap.put(TenpayConsts.KEY_SERVICE_VERSION, "1.0");
        verifyRequestMap.put(TenpayConsts.KEY_INPUT_CHARSET, "UTF-8");
        verifyRequestMap.put(TenpayConsts.KEY_SIGN_KEY_INDEX, "1");
        verifyRequestMap.put(TenpayConsts.KEY_PARTNER, payOrder.getChAccountId());
        verifyRequestMap.put(TenpayConsts.KEY_NOTIFY_ID, dataMap.get(TenpayConsts.KEY_NOTIFY_ID));
        String sign = generateSign(verifyRequestMap, payOrder.getAppChInfo().getChPayKeyMd5());
        verifyRequestMap.put(TenpayConsts.KEY_SIGN, sign);
        String verifyNotifyUrl = TenpayConsts.ADDR_TENPAYGATE_VERIFYNOTIFY + "?"
                + StringHelper.assembleResqStr(dataMap, Consts.CHARSET_UTF8);
        String verifyResponse = HttpClientHelper.sendRequest(verifyNotifyUrl);
        Map<String, String> verifyResponseMap = transXml2Map(verifyResponse);
        updatePayOrder(payOrder, verifyResponseMap);
    }

    private static void updatePayOrder(PayOrder payOrder, Map<String, String> dataMap) {
        String responseSign = dataMap.remove(TenpayConsts.KEY_SIGN);
        String sign = generateSign(dataMap, payOrder.getAppChInfo().getChPayKeyMd5());

        // 校验签名
        if (StringUtils.isBlank(responseSign)) {
            LOG.info("[query.tenpay] get an empty sign from tenpay,request fail,orderId:{}", payOrder.getChOrderId());
            payOrder.setStatusMsg(MapUtils.getString(dataMap, TenpayConsts.KEY_RETMSG));
            return;
        }
        // 成功订单则直接返回
        if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode())) {
            return;
        }
        if (!responseSign.equals(sign)) {
            LOG.error("[query.tenpay] check sign error,sign:{},response sign:{},orderId:{}", sign, responseSign,
                    payOrder.getChOrderId());
            throw new PayException(Consts.SC.CHANNEL_ERROR, "invalid sign,orderId:" + payOrder.getChOrderId());
        }
        if (TRADE_STATE_SUCCESS.equals(dataMap.get(TenpayConsts.KEY_TRADE_STATE))) {
            // 如果通知订单为成功状态，那么校验金额
            int responseAmount = MapUtils.getIntValue(dataMap, TenpayConsts.KEY_TOTAL_FEE);
            int orderAmount = Integer.valueOf(StringHelper.getAmount(payOrder.getAmount()));
            if (responseAmount < orderAmount) {
                throw new PayException(Consts.SC.CHANNEL_ERROR, "unmatched tenpay amount,expected amount:"
                        + orderAmount + ",response amount:" + responseAmount);
            }
            if (!RET_CODE_SUCCESS.equals(MapUtils.getString(dataMap, TenpayConsts.KEY_RETCODE, "1"))) {
                throw new PayException(Consts.SC.CHANNEL_ERROR, "错误的返回码:ret_code"
                        + dataMap.get(TenpayConsts.KEY_RETCODE));
            }
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setStatusMsg("财付通:支付成功");
            payOrder.setChDealId(dataMap.get(TenpayConsts.KEY_TRANSACTION_ID));
            payOrder.setBankDealId(MapUtils.getString(dataMap, TenpayConsts.KEY_BANK_BILLNO, ""));
            payOrder.setChDealTime(MapUtils.getString(dataMap, TenpayConsts.KEY_TIME_END, TimeHelper.get(8, new Date())));
        } else {
            payOrder.setStatusMsg(MapUtils.getString(dataMap, TenpayConsts.KEY_RETMSG, "财付通：支付异常"));
        }
    }

    /**
     * 将xml字符串转化为map.<br>
     * 约定：只适用于财付通渠道信息处理.
     * <ul>
     * <li>所有数据都保存在一级节点</li>
     * </ul>
     * 
     * @param xmlContent
     * @return
     * @throws ParserConfigurationException
     */
    private static Map<String, String> transXml2Map(String xmlContent) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xmlContent);
        } catch (DocumentException e) {
            LOG.error("[tenpay.parse] parse xml content fail,{}", xmlContent, e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, "parse xml fail.");
        }
        Element rootElt = doc.getRootElement();
        Iterator<Element> iterators = rootElt.elementIterator();
        Map<String, String> dataMap = new HashMap<String, String>();
        while (iterators.hasNext()) {
            Element element = iterators.next();
            dataMap.put(element.getName(), element.getText());
        }
        return dataMap;
    }
}