/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.paypal;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.UserAccountLimit;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.traderule.TradeRuleConsts;
import com.guzhi.pay.traderule.TradeRuleUtils;

/**
 * @author Administrator
 * 
 */
public class PaypalHelper {
    private static final String VERSION = "98.0";
    private static final Logger LOG = LoggerFactory.getLogger(PaypalHelper.class);
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    /**
     * 异步通知订单状态
     * 
     * @param resource
     * @param params
     * @return
     */
    public static PayOrder assembleAsynPayOrder(DomainResource resource, Map<String, String> params) {
        String txnId = params.get(PaypalConsts.TXN_ID);
        PayOrder payOrder = resource.getPayOrder(txnId);
        String status = params.get(PaypalConsts.PAYMENT_STATUS);
        if (payOrder == null) {
            String parentTxnId = params.get(PaypalConsts.PARENT_TXN_ID);
            payOrder = resource.getPayOrder(parentTxnId);
            LOG.error("IPN error params={}", params, TraceHelper.getTrace(payOrder));
            LOG.error("IPN error params={}", params, "ds:alarm:black account");
            return payOrder;
        }
        if (PaypalConsts.COMPLETED.equalsIgnoreCase(status)) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        } else if (PaypalConsts.PENDING.equalsIgnoreCase(status)) {
            payOrder.setStatusCode(Consts.SC.PENDING);
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        return payOrder;
    }

    /**
     * 查询订单状态
     * 
     * @param payOrder
     * @param respStr
     * @return
     */
    public static PayOrder updatePayOrderByQuery(PayOrder payOrder, String respStr) {
        LOG.info("[paypal_query] updatePayOrderByPay results, respStr={}, payOrder={}", respStr, payOrder,
                TraceHelper.getTrace(payOrder));
        Map<String, String> respMap = getRespMap(respStr);
        String status = respMap.get(PaypalConsts.PAYMENTSTATUS);
        if (PaypalConsts.COMPLETED.equalsIgnoreCase(status)) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
        } else if (PaypalConsts.PENDING.equalsIgnoreCase(status)) {
            payOrder.setStatusCode(Consts.SC.PENDING);
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        return payOrder;
    }

    /**
     * 
     * @param resource
     * @param params
     * @return
     */
    public static PayOrder assemblePaypalToken(DomainResource resource, Map<String, String> params) {
        String token = params.get(PaypalConsts.TOKEN.toLowerCase());
        PayOrder payOrder = resource.getPayOrder(token + PaypalConsts.TOKEN);
        if (payOrder == null) {
            return null;
        }
        List<AppChInfo> appChInfos = resource.getAppChInfo(payOrder.getAppId(), payOrder.getChId(),
                payOrder.getPayMethod());
        AppInfo appInfo = resource.getAppInfo(payOrder.getAppId());
        if (appInfo == null || CollectionUtils.size(appChInfos) != 1) {
            String msg = "appInfo/appChInfo not found, or get more than one appChInfo!";
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg + " payOrder=" + payOrder);
        }
        AppChInfo appChInfo = appChInfos.get(0);
        payOrder.setAppChInfo(appChInfo);
        payOrder.setAppInfo(appInfo);
        Map<String, String> request = new HashMap<String, String>();
        request.put(PaypalConsts.USER, appChInfo.getChAccountId());
        request.put(PaypalConsts.PWD, appChInfo.getChPayKeyMd5());
        request.put(PaypalConsts.SIGNATURE,
                JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), PaypalConsts.APPCHINFO_SIGNATURE));
        request.put(PaypalConsts.VERSION, VERSION);
        request.put(PaypalConsts.METHOD, PaypalConsts.METHOD_GETEXPRESSCHECKOUTDETAILS);
        request.put(PaypalConsts.TOKEN, token);
        String repUrl = PaypalBalanceAdapter.ADDR_PAYPAL_PURVIES + StringHelper.assembleResqStr(request);
        LOG.debug("[PaypalBalanceAdapter.GetExpressCheckoutDetails] with repUrl:{}", repUrl,
                TraceHelper.getTrace(payOrder));
        String respStr = HttpClientHelper.sendRequest(repUrl, Consts.CHARSET_UTF8);
        LOG.info("[PaypalBalanceAdapter.GetExpressCheckoutDetails] with respStr:{}", respStr,
                TraceHelper.getTrace(payOrder));
        Map<String, String> paramMap = getRespMap(StringHelper.decode(respStr, Consts.CHARSET_UTF8));
        // 判断是否存在风险
        Map<String, String> validatorMap = validatePaypalDetail(payOrder, paramMap);
        String flag = validatorMap.get(TradeRuleConsts.FLAG);
        if (TradeRuleConsts.TRUE.equalsIgnoreCase(flag)) {
            payOrder.setStatusCode(Consts.SC.RISK_ERROR);
            payOrder.setStatusMsg("[paypal.validatePaypalDetail] exist risk, errorcode="
                    + validatorMap.get(TradeRuleConsts.TRADE_ERROR_CODE));
            LOG.warn("[PaypalBalanceAdapter.assemblePaypalToken] validatePaypalDetail fail,errorcode:{}",
                    validatorMap.get(TradeRuleConsts.TRADE_ERROR_CODE), TraceHelper.getTrace(payOrder));
            LOG.warn("[PaypalBalanceAdapter.assemblePaypalToken] validatePaypalDetail fail,payOrder:{}",
                    "ds:alarm:paypal存在风控");
        } else if (Consts.SC.SUCCESS_NOTIFY.equalsIgnoreCase(paramMap.get(PaypalConsts.ACK))) {
            request.put(PaypalConsts.METHOD, PaypalConsts.METHOD_DOEXPRESSCHECKOUTPAYMENT);
            request.put(PaypalConsts.PAYMENTREQUEST_0_PAYMENTACTION, PaypalConsts.PAYMENTACTION_SALE);
            request.put(PaypalConsts.PAYERID, paramMap.get(PaypalConsts.PAYERID));
            request.put(PaypalConsts.PAYMENTREQUEST_0_AMT, decimalFormat.format(payOrder.getAmount()));
            request.put(PaypalConsts.PAYMENTREQUEST_0_NOTIFYURL, PaypalBalanceAdapter.ADDR_PAYPAL_NOTIFYURL);
            String resultUrl = PaypalBalanceAdapter.ADDR_PAYPAL_PURVIES + StringHelper.assembleResqStr(request);
            LOG.debug("[PaypalBalanceAdapter.DoExpressCheckoutPayment] with resultUrl:{}", repUrl,
                    TraceHelper.getTrace(payOrder));
            String resultStr = HttpClientHelper.sendRequest(resultUrl, Consts.CHARSET_UTF8);
            LOG.info("[PaypalBalanceAdapter.DoExpressCheckoutPayment] with resultStr:{}", resultStr,
                    TraceHelper.getTrace(payOrder));
            Map<String, String> resultMap = getRespMap(StringHelper.decode(resultStr, Consts.CHARSET_UTF8));
            if (Consts.SC.SUCCESS_NOTIFY.equalsIgnoreCase(resultMap.get(PaypalConsts.ACK))) {
                String status = resultMap.get(PaypalConsts.PAYMENTINFO_0_PAYMENTSTATUS);
                if (PaypalConsts.COMPLETED.equalsIgnoreCase(status) || PaypalConsts.COMPLETED_FUNDS_HELD.equals(status)) {
                    payOrder.setStatusCode(Consts.SC.SUCCESS);
                    payOrder.setChDealTime(TimeHelper.get(8, new Date()));
                    payOrder.setChDealId(resultMap.get(PaypalConsts.PAYMENTREQUEST_0_TRANSACTIONID));
                    return payOrder;
                } else if (PaypalConsts.PENDING.equalsIgnoreCase(status)) {
                    payOrder.setStatusCode(Consts.SC.PENDING);
                    payOrder.setChDealId(resultMap.get(PaypalConsts.PAYMENTREQUEST_0_TRANSACTIONID));
                    return payOrder;
                } else {
                    payOrder.appendMsg("[doExpressCheckoutPayment] status:" + status);
                }
            } else {
                payOrder.appendMsg("[doExpressCheckoutPayment] result:" + resultMap.get(PaypalConsts.ACK));
            }
        } else {
            payOrder.appendMsg("[GetExpressCheckoutDetails] result:" + paramMap.get(PaypalConsts.ACK));
        }
        // 其他情况都是fail
        payOrder.setStatusCode(Consts.SC.FAIL);
        return payOrder;
    }

    /**
     * 
     * @param resource
     * @param params
     * @return
     */
    public static PayOrder assemblePaypalCancel(DomainResource resource, Map<String, String> params) {
        String token = params.get(PaypalConsts.TOKEN.toLowerCase());
        PayOrder payOrder = resource.getPayOrder(token + PaypalConsts.TOKEN);
        if (payOrder == null) {
            return null;
        }
        payOrder.appendMsg("[PaypalCancel]");
        payOrder.setStatusCode(Consts.SC.FAIL);
        return payOrder;
    }

    /**
     * 根据SetExpressCheckout方法获取token
     * 
     * @param payOrder
     * @param respStr
     * @return
     */
    public static PayOrder updatePayOrderByPay(PayOrder payOrder, String respStr) {
        LOG.info("[paypal_in] updatePayOrderByPay results, respStr={}, payOrder={}", respStr, payOrder,
                TraceHelper.getTrace(payOrder));
        Map<String, String> respMap = getRespMap(respStr);
        if (Consts.SC.SUCCESS_NOTIFY.equalsIgnoreCase(respMap.get(PaypalConsts.ACK))) {
            String token = respMap.get(PaypalConsts.TOKEN);
            if (!StringUtils.isBlank(token)) {
                String payUrl = PaypalBalanceAdapter.ADDR_PAYPAL_PAY + PaypalConsts.AMP
                        + PaypalConsts.TOKEN.toLowerCase() + PaypalConsts.EQ + token;
                payOrder.setPayUrl(payUrl);
                payOrder.setStatusCode(Consts.SC.PENDING);
                payOrder.setChDealId(token + PaypalConsts.TOKEN);
                String newExt = JsonHelper.putJson(payOrder.getExt(), PaypalConsts.TOKEN, token);
                payOrder.setExt(newExt);
                payOrder.setStatusMsg(Consts.PENDING_DES);
                return payOrder;
            }
        } else {
            payOrder.appendMsg("[getToken]:" + respMap.get(PaypalConsts.ACK));
        }
        payOrder.setStatusCode(Consts.SC.FAIL);
        payOrder.setStatusMsg(Consts.FAIL_DES);
        return payOrder;
    }

    /**
     * 把返回字符串转换成Map
     * 
     * @param respStr
     * @return
     */
    private static Map<String, String> getRespMap(String respStr) {
        Map<String, String> respMap = new HashMap<String, String>();
        if (StringUtils.isBlank(respStr)) {
            return respMap;
        }
        String[] values = respStr.split(PaypalConsts.AMP);
        for (String s : values) {
            String[] value = s.split(PaypalConsts.EQ);
            if (value.length == 2) {
                respMap.put(value[0], value[1]);
            } else if (value.length == 1) {
                respMap.put(value[0], "");
            }
        }
        return respMap;
    }

    /**
     * 判断此笔交易请求是否存在风险
     * 
     * @param payOrder
     * @return
     */
    public static Map<String, String> validateTransValidate(PayOrder payOrder) {
        Map<String, String> paramMap = new HashMap<String, String>();
        String yyuid = getYyuid(payOrder);
        paramMap.put(TradeRuleConsts.YYUID, yyuid);
        paramMap.put(TradeRuleConsts.CHID, payOrder.getChId());
        paramMap.put(TradeRuleConsts.AMOUNT, payOrder.getAmount() + "");
        paramMap.put(TradeRuleConsts.IP, payOrder.getUserIp());
        // for trace
        paramMap.put(TradeRuleConsts.APPID, payOrder.getAppId());
        paramMap.put(TradeRuleConsts.APPORDERID, payOrder.getAppOrderId());
        return TradeRuleUtils.dispatch(TradeRuleConsts.PAYFORPAYPAL, paramMap);
    }

    /**
     * 从paypal获取详细信息判断交易是否存在风险
     * 
     * @param payOrder
     * @return
     */
    public static Map<String, String> validatePaypalDetail(PayOrder payOrder, Map<String, String> paramMap) {
        Map<String, String> validateMap = new HashMap<String, String>();
        validateMap.put(TradeRuleConsts.YYUID, getYyuid(payOrder));
        validateMap.put(TradeRuleConsts.CHID, payOrder.getChId());
        validateMap.put(TradeRuleConsts.AMOUNT, payOrder.getAmount() + "");
        validateMap.put(TradeRuleConsts.PAYERSTATUS, paramMap.get("PAYERSTATUS"));
        validateMap.put(TradeRuleConsts.PAYERID, paramMap.get("PAYERID"));
        validateMap.put(TradeRuleConsts.IP, payOrder.getUserIp());
        // for trace
        validateMap.put(TradeRuleConsts.APPID, payOrder.getAppId());
        validateMap.put(TradeRuleConsts.APPORDERID, payOrder.getAppOrderId());
        // for db address
        Map<String, String> addressMap = new HashMap<String, String>();
        putToMap(addressMap, PaypalConsts.ADDRESS_SHIPTOCOUNTRYCODE,
                paramMap.get(PaypalConsts.ADDRESS_SHIPTOCOUNTRYCODE));
        putToMap(addressMap, PaypalConsts.ADDRESS_SHIPTOSTATE, paramMap.get(PaypalConsts.ADDRESS_SHIPTOSTATE));
        putToMap(addressMap, PaypalConsts.ADDRESS_SHIPTOCITY, paramMap.get(PaypalConsts.ADDRESS_SHIPTOCITY));
        putToMap(addressMap, PaypalConsts.ADDRESS_SHIPTOSTREE, paramMap.get(PaypalConsts.ADDRESS_SHIPTOSTREE));
        putToMap(addressMap, PaypalConsts.ADDRESS_SHIPTOSTREE2, paramMap.get(PaypalConsts.ADDRESS_SHIPTOSTREE2));
        putToMap(addressMap, PaypalConsts.ADDRESS_SHIPTOZIP, paramMap.get(PaypalConsts.ADDRESS_SHIPTOZIP));
        putToMap(addressMap, PaypalConsts.ADDRESS_SHIPTOPHONENUM, paramMap.get(PaypalConsts.ADDRESS_SHIPTOPHONENUM));
        putToMap(addressMap, PaypalConsts.ADDRESS_SHIPTOSTATE, paramMap.get(PaypalConsts.ADDRESS_SHIPTOSTATE));
        validateMap.put(TradeRuleConsts.ADDRESS, JsonHelper.toJson(addressMap));

        // for db ext(name,EMAIL)
        Map<String, String> extMap = new HashMap<String, String>();
        putToMap(extMap, PaypalConsts.SALUTATION, paramMap.get(PaypalConsts.SALUTATION));
        putToMap(extMap, PaypalConsts.FIRSTNAME, paramMap.get(PaypalConsts.FIRSTNAME));
        putToMap(extMap, PaypalConsts.MIDDLENAME, paramMap.get(PaypalConsts.MIDDLENAME));
        putToMap(extMap, PaypalConsts.LASTNAME, paramMap.get(PaypalConsts.LASTNAME));
        putToMap(extMap, PaypalConsts.EMAIL, paramMap.get(PaypalConsts.EMAIL));
        validateMap.put(TradeRuleConsts.EXT, JsonHelper.toJson(extMap));

        return TradeRuleUtils.dispatch(TradeRuleConsts.PAYPALDETAIIL, validateMap);
    }

    /**
     * 获取yyuid
     * 
     * @param payOrder
     * @return
     */
    private static String getYyuid(PayOrder payOrder) {
        @SuppressWarnings("unchecked")
        Map<String, String> userIdMap = JsonHelper.fromJson(payOrder.getUserId(), Map.class);
        if (userIdMap == null) {
            throw new PayException(Consts.SC.DATA_ERROR, "yyuid is empty");
        }
        String yyuid = userIdMap.get(TradeRuleConsts.YYUID);
        if (StringUtils.isBlank(yyuid)) {
            throw new PayException(Consts.SC.DATA_ERROR, "yyuid is empty");
        }
        return yyuid;
    }

    /**
     * 交易是否被冻结
     * 
     * @param params
     * @param payOrder
     * @return
     */
    public static void assembleUserAccountLimit(DomainResource resource, Map<String, String> params, PayOrder payOrder) {
        String status = params.get(PaypalConsts.PAYMENT_STATUS);
        String payerId = params.get(PaypalConsts.PAYER_ID);
        if ((PaypalConsts.REVERSED.equalsIgnoreCase(status))
                || (PaypalConsts.PENDING.equalsIgnoreCase(status) && PaypalConsts.PENDING_REASON
                        .equalsIgnoreCase(params.get(PaypalConsts.KEY_PENDING_REASON)))) {
            UserAccountLimit userAccountLimit = new UserAccountLimit();
            userAccountLimit.setAccount(payerId);
            userAccountLimit.setChId(payOrder.getChId());
            userAccountLimit.setLastUpdateTime(TimeHelper.get(8, new Date()));
            userAccountLimit.setStatus(TradeRuleConsts.STATUS_VAILID);
            userAccountLimit.setType(TradeRuleConsts.BLACK);
            userAccountLimit.setCause("the order is frozen");
            LOG.info("the black add payerId:{}", payerId, TraceHelper.getTrace(payOrder));
            resource.createUserAccountLimit(userAccountLimit);
            String yyuid = getYyuid(payOrder);
            userAccountLimit.setAccount(yyuid);
            LOG.info("the black add yyuid:{}", yyuid, TraceHelper.getTrace(payOrder));
            resource.createUserAccountLimit(userAccountLimit);
        }
    }

    private static void putToMap(Map<String, String> map, String key, String value) {
        // TODO 只允许200个字符,只允许放4个字段
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        if (map.size() >= 4) {
            return;
        }
        map.put(key, value);
    }
}
