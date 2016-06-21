/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.sms;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 移动的Sms支付
 * 
 * @author administrator
 * 
 */
@Service("smsYdAdapter")
public class SmsYdAdapter extends AbstractChannelIF {
    private static final DecimalFormat formatGoods = new DecimalFormat("000");
    private static final String SPLIT = "#";
    private static final Logger LOG = LoggerFactory.getLogger(SmsYdAdapter.class);
    private static final String VERSION = "3.0";

    @Autowired
    private DomainResource resource;
    // 移动的sp的签名key
    @Value("${smsReceiveAddress}")
    private String smsReceiveAddress;
    @Value("${smsYdQueryAddress}")
    private String smsYdQueryAddress;

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder order) {
        LOG.info("[SmsYdAdapter.pay] start paying.payOrder:{}", order);
        order.setChOrderId(getChOrderId(order));
        AppChInfo appChInfo = order.getAppChInfo();
        String chAdditionalInfo = appChInfo.getAdditionalInfo();
        String subUser = JsonHelper.fromJson(chAdditionalInfo, SmsConsts.KEY_SUB_USER);
        String smsKey = JsonHelper.fromJson(chAdditionalInfo, SmsConsts.KEY_SMS_KEY);
        // 以角为单位
        String smsCode = formatGoods.format(order.getAmount().doubleValue() * 10) + SPLIT
                + StringHelper.getRandomCharStr(SmsConsts.randomCharLength);
        LOG.info("[SmsYdAdapter.pay] orderAmount:{} ,smsCode:{}", order.getAmount(), smsCode);
        String phone = JsonHelper.fromJson(order.getUserContact(), SmsConsts.KEY_TEL);
        String passport = JsonHelper.fromJson(order.getUserId(), Consts.YYUID);
        // 给用户发送短信
        String returnMsg = SmsHelper.send(phone, passport, subUser, smsCode, smsKey, smsReceiveAddress);
        SmsHelper.assembePayOrderByPay(returnMsg, order, phone, smsCode, resource);
        return order;
    }

    @Override
    public PayOrder query(PayOrder order) {
        LOG.info("[SmsYdAdapter.query] start query.order:{}", order);
        String phone = JsonHelper.fromJson(order.getUserContact(), SmsConsts.KEY_TEL);
        String goodsId = JsonHelper.fromJson(order.getExt(), SmsConsts.GOODS_ID);
        String merDate = JsonHelper.fromJson(order.getExt(), SmsConsts.MER_DATE);
        // 如果在ext字段中没有goodsId参数，那么再到prodAddiInfo中查找
        if (StringUtils.isBlank(goodsId)) {
            goodsId = JsonHelper.fromJson(order.getProdAddiInfo(), SmsConsts.GOODS_ID);
        }
        // 目前设置的goodsId，只有4种，020,100,200,300，分别对应4种金额，2元，10元，20元，30元，正好对应金额值（单位为角）
        if (StringUtils.isBlank(goodsId)) {
            goodsId = formatGoods.format(order.getAmount().doubleValue() * 10);
        }
        if (StringUtils.isBlank(merDate) && StringUtils.isNotBlank(order.getAppOrderTime())) {
            merDate = StringUtils.substring(order.getAppOrderTime(), 0, 8);
        }
        Map<String, String> request = new LinkedHashMap<String, String>();
        request.put(SmsConsts.MER_ID, order.getAppChInfo().getChAccountId());
        request.put(SmsConsts.GOODS_ID, goodsId);
        request.put(SmsConsts.ORDER_ID, order.getChOrderId());
        request.put(SmsConsts.MER_DATE, merDate);
        request.put(SmsConsts.MOBILE_ID, phone);
        request.put(SmsConsts.VERSION, VERSION);
        String sign = SmsHelper.getQuerySign(order, request);
        request.put(SmsConsts.SIGN, sign);
        String queryUrl = UrlHelper.addQuestionMark(smsYdQueryAddress) + StringHelper.assembleResqStr(request);
        LOG.info("[SmsYdAdapter.query] with queryUrl:{}", queryUrl);
        String respStr = HttpClientHelper.sendRequest(queryUrl, "GBK");
        LOG.info("[SmsYdAdapter.respStr] with queryUrl:{},respStr:{}", queryUrl, respStr);
        SmsHelper.updateByQuerySmsYd(order, respStr, resource);
        return order;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

    public String getSmsReceiveAddress() {
        return smsReceiveAddress;
    }

    public void setSmsReceiveAddress(String smsReceiveAddress) {
        this.smsReceiveAddress = smsReceiveAddress;
    }

    public String getSmsYdQueryAddress() {
        return smsYdQueryAddress;
    }

    public void setSmsYdQueryAddress(String smsYdQueryAddress) {
        this.smsYdQueryAddress = smsYdQueryAddress;
    }

}
