/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.kq;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.client.Stub;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.channel.kq.axis.GatewayOrderQueryRequest;
import com.guzhi.pay.channel.kq.axis.GatewayOrderQueryResponse;
import com.guzhi.pay.channel.kq.axis.GatewayOrderQueryServiceLocator;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.Accounts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TimeHelper;

/**
 * 快钱支付方式
 * 
 * @author administrator
 * @author administrator 2013-03-12
 */
public abstract class AbstractKqAdatper extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractKqAdatper.class);
    private static GatewayOrderQueryServiceLocator locator = new GatewayOrderQueryServiceLocator();

    // 增加银行直连地址（URL）常量
    @Value("${payKqAddr}")
    protected String payKqAddr;

    public String getPayKqAddr() {
        return payKqAddr;
    }

    public void setPayKqAddr(String payKqAddr) {
        this.payKqAddr = payKqAddr;
    }

    @Override
    public abstract PayOrder pay(PayOrder order);

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    public abstract String getQueryAddress();

    /**
     * 卡密查询和人民币网关查询实现基本一样。
     */
    @Override
    public PayOrder query(PayOrder payOrder) {
        // 远程调用
        Accounts accountResult = getPayAccounts(payOrder);
        // FIXME 嵌套太深，请精简一下
        if (!StringUtils.isBlank(accountResult.getErrorCode())) {
            payOrder.setStatusMsg(KqStatusMsgHelper.translateCardQueryResponseErrorCode(accountResult.getErrorCode()));
            return payOrder;
        }
        if (accountResult.getResults() == null) {
            return payOrder;
        }
        // 取出最早的一条成功记录作为结果。
        PayOrder payResult = null;
        for (PayOrder element : accountResult.getResults()) {
            if (payResult == null || Long.valueOf(payResult.getChDealTime()) > Long.valueOf(element.getChDealTime())) {
                payResult = element;
            }
        }
        payResult.setAppInfo(payOrder.getAppInfo());
        payResult.setAppChInfo(payOrder.getAppChInfo());
        return payResult;
    }

    /**
     * 需增加注释
     * 
     * @param payOrder
     * @return
     */
    private Accounts getPayAccounts(PayOrder payOrder) {
        payOrder.setChOrderId(getChOrderId(payOrder));
        String signMsg = KqHelper.getQuerySign(payOrder);
        LOG.info("[KqAdapter.getPayAccounts]signMsg:{}", signMsg);
        GatewayOrderQueryRequest queryRequest = assembleGatewayOrderQueryRequest(payOrder, signMsg);
        // 远程调用
        locator.setgatewayOrderQueryEndpointAddress(getQueryAddress());
        GatewayOrderQueryResponse queryResponse = null;
        LOG.info("[KqAdapter.getPayAccounts] queryRequest: {}", ReflectionToStringBuilder.toString(queryRequest));
        StopWatch timer = TimeHelper.initTimer();
        try {
            Stub stub = (Stub) locator.getgatewayOrderQuery();
            stub.setTimeout(10000);
            queryResponse = locator.getgatewayOrderQuery().gatewayOrderQuery(queryRequest);
            LOG.info("[KqAdapter.getPayAccounts] costTime:{}, queryResponse: {}", TimeHelper.calcTime(timer),
                    ReflectionToStringBuilder.toString(queryResponse));
        } catch (Throwable e) {
            LOG.error("[KqAdapter.getPayAccounts] error, costTime:{}, queryRequest: {}", TimeHelper.calcTime(timer),
                    ReflectionToStringBuilder.toString(queryRequest));
            throw new PayException(Consts.SC.CHANNEL_ERROR, "error invoking 3pp channel", payOrder, e);
        }
        String password = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), KqConsts.KQ_QUERY_KEY);
        if (StringUtils.isBlank(password)) {
            password = payOrder.getAppChInfo().getChPayKeyMd5();
        }
        // 签名验证
        KqHelper.validSign(queryResponse, password);
        // 结果解析
        List<PayOrder> payOrders = new ArrayList<PayOrder>(1);
        payOrders.add(payOrder);
        Accounts payAccountsResult = KqHelper.convert(queryResponse, payOrders);
        payAccountsResult.setAppId(payOrder.getAppId());
        payAccountsResult.setChannelId(payOrder.getChId());
        return payAccountsResult;
    }

    /**
     * 组装GatewayOrderQueryRequest
     * 
     * @param payOrder
     * @param signMsg
     * @return
     */
    private GatewayOrderQueryRequest assembleGatewayOrderQueryRequest(PayOrder payOrder, String signMsg) {
        GatewayOrderQueryRequest queryRequest = new GatewayOrderQueryRequest();
        queryRequest.setInputCharset(KqConsts.KQ_INPUT_CHARSET_UTF8);
        queryRequest.setVersion(KqConsts.KQ_VERSION_2);
        queryRequest.setSignType(Integer.parseInt(KqConsts.KQ_SIGN_TYPE_MD5));
        queryRequest.setMerchantAcctId(payOrder.getAppChInfo().getChAccountId());
        queryRequest.setQueryType(Integer.parseInt(KqConsts.KQ_QUERY_TYPE_ORDER));
        queryRequest.setQueryMode(Integer.parseInt(KqConsts.KQ_QUERY_MODE));
        queryRequest.setOrderId(getChOrderId(payOrder));
        queryRequest.setSignMsg(signMsg);
        return queryRequest;
    }

}
