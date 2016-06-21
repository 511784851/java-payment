/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.zfb;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 
 * 支付宝无线快捷支付
 * 参考文档：移动快捷支付应用集成接入包 支付接口 版本号：1.1
 * 
 * @author administrator
 * 
 */
@Service("zfbWapappAdapter")
public class ZfbWapAppAdapter extends AbstractZfbAdapter implements ChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(ZfbWapAppAdapter.class);

    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public PayOrder pay(PayOrder payOrder) {
        LOG.info("[zfbwapapp_in.pay] start paying,payOrder:{}", payOrder, "ds:trace:" + payOrder.getAppOrderId());
        payOrder.setChOrderId(getChOrderId(payOrder));

        Map<String, String> requestMap = new LinkedHashMap<String, String>();
        requestMap.put(ZfbConsts.KEY_SERVICE, ZfbConsts.WapApp.SERVICE_MOBILE_SECURITYPAY);
        requestMap.put(ZfbConsts.WapApp.KEY_PARTNER, payOrder.getAppChInfo().getChAccountId());
        requestMap.put(ZfbConsts.KEY_INPUT_CHARSET, ZfbConsts.CHARSET_UTF8);
        requestMap.put(ZfbConsts.WapApp.KEY_NOTIFY_URL, UrlHelper.removeLastSep(getPayNotify())
                + ZfbConsts.ADDR_WAP_APP_YYPAY_NOTIFY);
        requestMap.put(ZfbConsts.WapApp.KEY_OUT_TRADE_NO, payOrder.getChOrderId());
        requestMap.put(ZfbConsts.KEY_SUBJECT, ZfbHelper.getSubjectName(payOrder));
        requestMap.put(ZfbConsts.KEY_PAYMENT_TYPE, ZfbConsts.PAYMENT_TYPE_BUY_PRODUCT);
        requestMap.put(ZfbConsts.KEY_SELLER_ID, payOrder.getAppChInfo().getChAccountName());
        requestMap.put(ZfbConsts.KEY_TOTAL_FEE, decimalFormat.format(payOrder.getAmount()));
        String target = ZfbHelper.assemblePayContentForAppWap(requestMap);
        LOG.info("[zfbwapapp_in.pay] sign target string:{}", target);
        String privateKeyPath = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(),
                ZfbConsts.WapApp.PRIVATE_KEY_PATH);
        String password = payOrder.getAppChInfo().getChPayKeyMd5();
        String sign = RSAEncrypt.sign(target, privateKeyPath, password);
        requestMap.put(ZfbConsts.KEY_SIGN_TYPE, ZfbConsts.WapApp.DEFAULT_SIGN_TYPE);
        requestMap.put(ZfbConsts.WapApp.KEY_SIGN, sign);
        payOrder.setPayUrl(ZfbHelper.assemblePayContentForAppWap(requestMap));
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setStatusMsg("等待用户支付，或等待支付宝通知");
        LOG.info("[zfbwapapp_in.pay] payurl created successfully,payOrder:{}", payOrder);
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        LOG.info("[zfbwapapp.query] with PayOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        return super.query(payOrder);
    }

}
