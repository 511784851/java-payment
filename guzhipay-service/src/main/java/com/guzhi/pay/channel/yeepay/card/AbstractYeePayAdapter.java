/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.yeepay.card;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.channel.yeepay.YeePayConsts;
import com.guzhi.pay.channel.yeepay.YeePayHelper;
import com.guzhi.pay.channel.yeepay.util.DigestUtil;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 易宝组合支付。
 * 参考《非银行卡专业版(组合支付)帮助文档.chm》
 * 版本3.0
 * 
 * @author administrator
 * 
 */
public abstract class AbstractYeePayAdapter extends AbstractChannelIF {
    private static Logger LOG = LoggerFactory.getLogger(AbstractYeePayAdapter.class);

    public PayOrder pay(PayOrder payOrder) {
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put(YeePayConsts.P0_CMD, "ChargeCardDirect");
        reqParams.put(YeePayConsts.P1_MERID, payOrder.getAppChInfo().getChAccountId());
        reqParams.put(YeePayConsts.P2_ORDER, payOrder.getChOrderId());
        reqParams.put(YeePayConsts.P3_AMT, payOrder.getAmount().toString());
        reqParams.put(YeePayConsts.P4_VERFYAMT, "true");
        reqParams.put(YeePayConsts.P5_PID, StringUtils.defaultString(payOrder.getProdId(), ""));
        reqParams.put(YeePayConsts.P6_PCAT, StringUtils.defaultString(payOrder.getProdName(), ""));
        reqParams.put(YeePayConsts.P7_PDESC, StringUtils.defaultString(payOrder.getProdDesc(), ""));
        reqParams.put(YeePayConsts.P8_URL, getPayNotify() + YeePayConsts.ADDR_YEEPAY_CARD_NOTIFY);
        reqParams.put(YeePayConsts.PA7_CARDAMT, YeePayHelper.getCardTotalAmount(payOrder));
        reqParams.put(YeePayConsts.PA8_CARDNO, payOrder.getCardNum());
        reqParams.put(YeePayConsts.PA9_CARDPWD,
                DESEncrypt.decryptByAES(payOrder.getAppInfo().getPasswdKey(), payOrder.getCardPass()));
        reqParams.put(YeePayConsts.PD_FRPID, getPdFrpId());
        reqParams.put(YeePayConsts.PR_NEEDRESPONSE, YeePayConsts.NEED_RESPONSE);
        String hmac = DigestUtil.hamcSign(reqParams, payOrder.getAppChInfo().getChPayKeyMd5());
        reqParams.put(YeePayConsts.HMAC, hmac);
        String payUrl = UrlHelper.addQuestionMark(YeePayConsts.YEEPAY_CARD_PAY_URL)
                + StringHelper.assembleResqStr(reqParams, YeePayConsts.GBK);
        LOG.info("[AbstractYeePayAdapter.pay] with payUrl:{}", payUrl, TraceHelper.getTrace(payOrder));
        // 删除同步处理流程，使用异步充值任务
        {
            payOrder.setPayUrl(payUrl);
            payOrder.setStatusCode(Consts.SC.PENDING);
            payOrder.setStatusMsg("等待易宝通知");
            createPayTask(payOrder);
        }

        // String respStr = HttpClientHelper.sendRequest(payUrl,
        // YeePayConsts.GBK);
        // LOG.info("[AbstractYeePayAdapter.pay] payUrl response:{}", respStr,
        // TraceHelper.getTrace(payOrder));
        // YeePayHelper.upatePayOrderByCardPayResponse(payOrder, respStr);
        // LOG.info("[AbstractYeePayAdapter.pay] out payOrder:{}", payOrder,
        // TraceHelper.getTrace(payOrder));
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        Map<String, String> reqParams = new HashMap<String, String>();
        boolean isCard = Boolean.TRUE;
        if ("".equalsIgnoreCase(payOrder.getPayMethod())
                || Consts.PayMethod.GATE.equalsIgnoreCase(payOrder.getPayMethod())) {
            isCard = Boolean.FALSE;
        }
        if (isCard) {
            reqParams.put(YeePayConsts.P0_CMD, YeePayConsts.CHARGE_CARD_QUERY);
        } else {
            reqParams.put(YeePayConsts.P0_CMD, YeePayConsts.QUERYORDERDETAIL);
        }
        reqParams.put(YeePayConsts.P1_MERID, payOrder.getChAccountId());
        reqParams.put(YeePayConsts.P2_ORDER, payOrder.getChOrderId());

        // TODO generate original sign
        String hmac = YeePayHelper.getQuerySign(reqParams, payOrder.getAppChInfo().getChPayKeyMd5());

        reqParams.put(YeePayConsts.HMAC, hmac);
        String requestUrl = UrlHelper.addQuestionMark(YeePayConsts.YEEPAY_QUERY_URL)
                + StringHelper.assembleResqStr(reqParams);
        LOG.info("[Yibao{}.query] with requestUrl:{}", requestUrl, TraceHelper.getTrace(payOrder));
        String resp = HttpClientHelper.sendRequest(requestUrl, YeePayConsts.GBK);
        try {
            resp = URLDecoder.decode(resp, YeePayConsts.GBK);
        } catch (UnsupportedEncodingException e) {
            LOG.error("decode resp error " + e.getMessage(), e);
        }
        return YeePayHelper.updatePayOrderByQuery(resp, payOrder, isCard);
    }

    public abstract String getPdFrpId();

    public abstract void createPayTask(PayOrder payOrder);
}
