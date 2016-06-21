/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.zfb;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 
 * 支付宝手机网页支付
 * 
 * @author administrator
 * 
 */
@Service("zfbWapalipayAdapter")
public class ZfbWapAlipayAdapter extends AbstractZfbAdapter implements ChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(ZfbWapAlipayAdapter.class);

    @Override
    public PayOrder pay(PayOrder payOrder) {
        LOG.info("[ZfbWapAlipayAdapter.pay] start paying,payOrder:{}", payOrder);
        String chOrderId = payOrder.getChOrderId();
        if (StringUtils.isBlank(chOrderId)) {
            chOrderId = OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId());
            payOrder.setChOrderId(chOrderId);
        }
        String token = StringUtils.EMPTY;
        try {
            token = getToken(payOrder);
        } catch (Exception e) {
            // 第一次获取失败，100ms后，重新获取
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e1) {
            }
            token = getToken(payOrder);
        }
        // 未 获取到 token 直接返回
        if (StringUtils.isBlank(token)) {
            return payOrder;
        }
        Map<String, String> request = getRequestMap(payOrder);
        request.put(ZfbConsts.KEY_SERVICE, ZfbConsts.Wap.EXECUTE_SERVER);
        request.remove(ZfbConsts.Wap.REQ_ID);
        Map<String, String> requDataMap = new HashMap<String, String>();
        requDataMap.put(ZfbConsts.Wap.REQUEST_TOKEN, token);
        request.put(ZfbConsts.Wap.REQ_DATA, ZfbHelper.assembleWapTransData(requDataMap));
        String signMsg = ZfbHelper.genSign(request, payOrder.getAppChInfo().getChPayKeyMd5());
        request.put(ZfbConsts.KEY_SIGN, signMsg);
        String payUrl = UrlHelper.removeLastSep(ZfbConsts.ADD_WAP_PAY) + ZfbHelper.assembleQueryStr(request);
        payOrder.setPayUrl(payUrl);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setStatusMsg("等待用户支付，或等待支付宝通知");
        LOG.info("[ZfbWapAlipayAdapter.pay] payurl created successfully,payOrder:{}", payOrder);
        return payOrder;
    }

    /**
     * 获取token
     * 
     * @param payOrder
     * @return
     */
    private String getToken(PayOrder payOrder) {
        Map<String, String> request = getRequestMap(payOrder);
        Map<String, String> requDataMap = new HashMap<String, String>();
        requDataMap.put(ZfbConsts.KEY_SUBJECT, payOrder.getProdName());
        requDataMap.put(ZfbConsts.KEY_OUT_TRADE_NO, payOrder.getChOrderId());
        requDataMap.put(ZfbConsts.KEY_TOTAL_FEE, payOrder.getAmount() + "");
        requDataMap.put(ZfbConsts.Wap.SELLER_ACCOUNT_NAME, payOrder.getAppChInfo().getChAccountName());
        requDataMap.put(ZfbConsts.Wap.CALL_BACK_URL, UrlHelper.removeLastSep(getPayNotify())
                + ZfbConsts.ADDR_WAP_YYPAY_RETURN);
        requDataMap.put(ZfbConsts.KEY_NOTIFY_URL, UrlHelper.removeLastSep(getPayNotify())
                + ZfbConsts.ADDR_WAP_YYPAY_NOTIFY);
        request.put(ZfbConsts.Wap.REQ_DATA, ZfbHelper.assembleWapAuthorizeData(requDataMap));
        String signMsg = ZfbHelper.genSign(request, payOrder.getAppChInfo().getChPayKeyMd5());
        request.put(ZfbConsts.KEY_SIGN, signMsg);
        String requAutorizeUrl = UrlHelper.removeLastSep(ZfbConsts.ADD_WAP_PAY) + ZfbHelper.assembleQueryStr(request);
        LOG.info("[ZfbWapAlipayAdapter.authorize] apporderid:{}  with requAutorizeUrl:{}", payOrder.getAppOrderId(),
                requAutorizeUrl);
        String respStr = HttpClientHelper.sendRequest(requAutorizeUrl, ZfbConsts.CHARSET_UTF8);
        String decodeRespStr = StringHelper.decode(respStr, Consts.CHARSET_UTF8);
        LOG.info("[ZfbWapAlipayAdapter.authorize] respStr:{}", respStr);
        String result = ZfbHelper.getWapAuthorizeMap(payOrder, decodeRespStr);
        LOG.info("[ZfbWapAlipayAdapter.authorize] token:{}", result);
        return result;
    }

    /**
     * 构造请求map
     * 
     * @param payOrder
     * @return
     */
    private Map<String, String> getRequestMap(PayOrder payOrder) {
        Map<String, String> request = new HashMap<String, String>();
        request.put(ZfbConsts.KEY_SERVICE, ZfbConsts.Wap.TOKEN_SERVER);
        request.put(ZfbConsts.Wap.FORMAT, ZfbConsts.Wap.FORMAT_VALUE);
        request.put(ZfbConsts.Wap.VERSION, ZfbConsts.Wap.VERSION_VALUE);
        request.put(ZfbConsts.KEY_PARTNER, payOrder.getAppChInfo().getChAccountId());
        // 请求号后续需要改成唯一
        request.put(ZfbConsts.Wap.REQ_ID, payOrder.getChOrderId());
        request.put(ZfbConsts.Wap.SEC_ID, ZfbConsts.ZFB_SIGN_TYPE_MD5);
        return request;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        LOG.info("[ZfbWapAlipayAdapter.query] with PayOrder:{}", payOrder);
        return super.query(payOrder);
    }

}
