/**
 * Copyright © 2011 guzhi.com [多玩游戏]
 */
package com.guzhi.pay.channel.kq;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.channel.kq.axis.GatewayOrderDetail;
import com.guzhi.pay.channel.kq.axis.GatewayOrderQueryResponse;
import com.guzhi.pay.channel.kq.util.Pkipair;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.Accounts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpRetryHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TimeHelper;

/**
 * 支付接口的帮助类。方便重用或阅读。
 * 
 * @author yangpeng
 * @author administrator 2013-03-12
 */
public class KqHelper {

    private static final Logger LOG = LoggerFactory.getLogger(KqHelper.class);

    /**
     * 把GatewayOrderQueryResponse转化为PayOrder
     * 
     * @param response
     * @return
     */
    public static Accounts convert(GatewayOrderQueryResponse queryResponse, List<PayOrder> originalPayorders) {
        Accounts payAccountsResult = new Accounts();
        if (StringUtils.isNotBlank(queryResponse.getCurrentPage())) {
            payAccountsResult.setCurrentPage(Integer.parseInt(queryResponse.getCurrentPage()));
        }
        if (StringUtils.isNotBlank(queryResponse.getPageCount())) {
            payAccountsResult.setPageCount(Integer.parseInt(queryResponse.getPageCount()));
        }
        if (StringUtils.isNotBlank(queryResponse.getPageSize())) {
            payAccountsResult.setPageSize(Integer.parseInt(queryResponse.getPageSize()));
        }
        if (StringUtils.isNotBlank(queryResponse.getRecordCount())) {
            payAccountsResult.setRecordCount(Integer.parseInt(queryResponse.getRecordCount()));
        }
        payAccountsResult.setErrorCode(queryResponse.getErrCode());
        if (StringUtils.isBlank(queryResponse.getErrCode()) && queryResponse.getOrders() != null) {
            GatewayOrderDetail[] orders = queryResponse.getOrders();
            // 目前只有单笔查询，所以加个唯一存在判断，如果需要批量查询，可在这里放开。
            if (orders.length != 1) {
                throw new PayException(Consts.SC.INTERNAL_ERROR, "[convert] query response get multi orders.");
            }
            List<PayOrder> results = new ArrayList<PayOrder>();
            for (GatewayOrderDetail detail : orders) {
                PayOrder result = assemblePayOrder(detail, queryResponse,
                        getPayOrder(originalPayorders, detail.getOrderId()));
                results.add(result);
            }
            payAccountsResult.setResults(results);
        }

        // 空数据的话，统一返回正确。
        if (KqConsts.ERROR_CODE_31001.equals(payAccountsResult.getErrorCode())
                || KqConsts.ERROR_CODE_31002.equals(payAccountsResult.getErrorCode())) {
            payAccountsResult.setResults(originalPayorders);
            payAccountsResult.setErrorCode(KqConsts.ERROR_CODE_EMPTY);
        }
        return payAccountsResult;
    }

    private static PayOrder getPayOrder(List<PayOrder> payOrders, String chOrderId) {
        String appId = OrderIdHelper.getAppId(chOrderId);
        String appOrderId = OrderIdHelper.getAppOrderId(chOrderId);
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appOrderId)) {
            String errorMsg = String.format("query kq error,get an empty appId:%s,or appOrderId:%s", appId, appOrderId);
            LOG.error("[getPayOrder] " + errorMsg);
            throw new PayException(Consts.SC.INTERNAL_ERROR, errorMsg);
        }
        for (PayOrder payOrder : payOrders) {
            if (appId.equals(payOrder.getAppId()) && appOrderId.equals(payOrder.getAppOrderId())) {
                return payOrder;
            }
        }
        String errorMsg = String.format("query kq error,get no payOrder in payorder list,chOrderId:%s", chOrderId);
        LOG.error("[getPayOrder] {}", chOrderId);
        throw new PayException(Consts.SC.INTERNAL_ERROR, errorMsg);
    }

    /**
     * 
     * @param detail
     * @param queryResponse
     * @return
     */
    private static PayOrder assemblePayOrder(GatewayOrderDetail detail, GatewayOrderQueryResponse queryResponse,
            PayOrder payOrder) {
        // PayOrder result = new PayOrder();
        // result.setChDealId(detail.getDealId());
        // result.setChAccountId(queryResponse.getMerchantAcctId());
        // result.setChFee(BigDecimal.valueOf(detail.getFee()));
        // result.setAmount(BigDecimal.valueOf(detail.getPayAmount()));
        // result.setAppId(OrderIdHelper.getAppId(detail.getOrderId()));
        // result.setAppOrderId(OrderIdHelper.getAppOrderId(detail.getOrderId()));
        // result.setChDealTime(detail.getOrderTime());
        // result.setBankDealTime(detail.getDealTime());
        // result.setChId(Consts.Channel.KQ);
        // // FIXME 11表示电话银行卡支付，目前没有接入
        // result.setPayMethod(KqConsts.KQ_PAY_TYPE_BANK.equals(detail.getPayType())
        // ? Consts.PayMethod.GATE : detail
        // .getPayType());
        // result.setStatusCode(KqConsts.PAY_RESULT_SUCCESS.equals(detail.getPayResult())
        // ? Consts.SC.SUCCESS
        // : Consts.SC.FAIL);
        // return result;
        payOrder.setChDealId(detail.getDealId());
        payOrder.setChFee(BigDecimal.valueOf(detail.getFee()));
        payOrder.setChDealTime(detail.getOrderTime());
        payOrder.setBankDealTime(detail.getDealTime());
        payOrder.setStatusCode(KqConsts.PAY_RESULT_SUCCESS.equals(detail.getPayResult()) ? Consts.SC.SUCCESS
                : Consts.SC.FAIL);
        payOrder.setStatusMsg("快钱查询：" + (KqConsts.PAY_RESULT_SUCCESS.equals(detail.getPayResult()) ? "支付成功" : "支付失败"));
        return payOrder;
    }

    /**
     * 验证对账信息回应的签名
     * 
     * @param queryResponse
     * @param key
     */
    public static void validSign(GatewayOrderQueryResponse queryResponse, String key) {
        if (queryResponse == null) {
            return;
        }
        // 主记录验证
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(KqConsts.KEY_VERSION, queryResponse.getVersion());
        map.put(KqConsts.KEY_SIGNTYPE, String.valueOf(queryResponse.getSignType()));
        map.put(KqConsts.KEY_MERCHANTACCTID, queryResponse.getMerchantAcctId());
        map.put(KqConsts.KEY_ERRCODE, queryResponse.getErrCode());
        map.put(KqConsts.KEY_CURRENTPAGE, queryResponse.getCurrentPage());
        map.put(KqConsts.KEY_PAGECOUNT, queryResponse.getPageCount());
        map.put(KqConsts.KEY_PAGESIZE, queryResponse.getPageSize());
        map.put(KqConsts.KEY_RECORDCOUNT, queryResponse.getRecordCount());
        map.put(KqConsts.KEY_KEY, key);
        String signMsgVal = Help.getStrByMap(map, false);
        String signMsg = DigestUtils.md5DigestAsHex(signMsgVal.getBytes(Charset.forName(KqConsts.CHARSET_UTF8)))
                .toUpperCase();
        if (!signMsg.equals(queryResponse.getSignMsg())) {
            throw new PayException(Consts.SC.DATA_ERROR, "sign of GatewayOrderQueryResponse failed with: "
                    + queryResponse.getMerchantAcctId(), null, null);
        }
        // 详细记录验证
        if (queryResponse.getOrders() != null) {
            for (GatewayOrderDetail detail : queryResponse.getOrders()) {
                Map<String, String> detailMap = new LinkedHashMap<String, String>();
                detailMap.put(KqConsts.KEY_ORDERID, detail.getOrderId());
                detailMap.put(KqConsts.KEY_ORDERAMOUNT, String.valueOf(detail.getOrderAmount()));
                detailMap.put(KqConsts.KEY_ORDERTIME, detail.getOrderTime());
                detailMap.put(KqConsts.KEY_DEALTIME, detail.getDealTime());
                detailMap.put(KqConsts.KEY_PAYRESULT, detail.getPayResult());
                detailMap.put(KqConsts.KEY_PAYTYPE, detail.getPayType());
                detailMap.put(KqConsts.KEY_PAYAMOUNT, String.valueOf(detail.getPayAmount()));
                detailMap.put(KqConsts.KEY_FEE, String.valueOf(detail.getFee()));
                detailMap.put(KqConsts.KEY_DEALID, detail.getDealId());
                detailMap.put(KqConsts.KEY_KEY, key);
                signMsgVal = Help.getStrByMap(detailMap, false);
                signMsg = DigestUtils.md5DigestAsHex(signMsgVal.getBytes(Charset.forName(KqConsts.CHARSET_UTF8)))
                        .toUpperCase();
                if (!signMsg.equals(detail.getSignInfo())) {
                    throw new PayException(Consts.SC.DATA_ERROR,
                            "sign message of GatewayOrderQueryResponse failed with orderId: " + detail.getOrderId(),
                            null, null);
                }
            }
        }

    }

    private final static String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";

    /**
     * 向快钱请求获取当前交易时间戳
     * 
     * @param url
     * @return
     */
    public static String getOrderTimestamp(String url) {
        String timestamp = TimeHelper.get(8, new Date());
        if (StringUtils.isBlank(url)) {
            return timestamp;
        }
        // 200ms内未返回，则再次请求一次,内还未返回,则自己伪造一个

        String content = null;
        try {
            content = HttpRetryHelper.sendRequest(url, 200, 1000);
        } catch (Exception e) {
            content = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
        }
        int idx = content.indexOf(KqConsts.KEY_WORD);
        if (idx > 0) {
            timestamp = content.substring(idx + 11, idx + 25);
        }
        return timestamp;
    }

    public static String genPaySignForCard(Map<String, String> request, String key) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(KqConsts.KEY_INPUTCHARSET, request.get(KqConsts.KEY_INPUTCHARSET));
        map.put(KqConsts.KEY_BGURL, request.get(KqConsts.KEY_BGURL));
        map.put(KqConsts.KEY_PAGEURL, request.get(KqConsts.KEY_PAGEURL));
        map.put(KqConsts.KEY_VERSION, request.get(KqConsts.KEY_VERSION));
        map.put(KqConsts.KEY_LANGUAGE, request.get(KqConsts.KEY_LANGUAGE));
        map.put(KqConsts.KEY_SIGNTYPE, request.get(KqConsts.KEY_SIGNTYPE));
        map.put(KqConsts.KEY_MERCHANTACCTID, request.get(KqConsts.KEY_MERCHANTACCTID));
        map.put(KqConsts.KEY_PAYERNAME, request.get(KqConsts.KEY_PAYERNAME));
        map.put(KqConsts.KEY_PAYERCONTACTTYPE, request.get(KqConsts.KEY_PAYERCONTACTTYPE));
        map.put(KqConsts.KEY_PAYERCONTACT, request.get(KqConsts.KEY_PAYERCONTACT));
        map.put(KqConsts.KEY_ORDERID, request.get(KqConsts.KEY_ORDERID));
        map.put(KqConsts.KEY_ORDERAMOUNT, request.get(KqConsts.KEY_ORDERAMOUNT));
        map.put(KqConsts.KEY_PAYTYPE, request.get(KqConsts.KEY_PAYTYPE));
        map.put(KqConsts.KEY_CARD_NUM, request.get(KqConsts.KEY_CARD_NUM));
        map.put(KqConsts.KEY_CARD_PWD, request.get(KqConsts.KEY_CARD_PWD));
        map.put(KqConsts.KEY_FULL_AMOUNT_FLAG, request.get(KqConsts.KEY_FULL_AMOUNT_FLAG));
        map.put(KqConsts.KEY_ORDERTIME, request.get(KqConsts.KEY_ORDERTIME));
        map.put(KqConsts.KEY_PRODUCTNAME, request.get(KqConsts.KEY_PRODUCTNAME));
        map.put(KqConsts.KEY_PRODUCTNUM, request.get(KqConsts.KEY_PRODUCTNUM));
        map.put(KqConsts.KEY_PRODUCTID, request.get(KqConsts.KEY_PRODUCTID));
        map.put(KqConsts.KEY_PRODUCTDESC, request.get(KqConsts.KEY_PRODUCTDESC));
        map.put(KqConsts.KEY_EXT1, request.get(KqConsts.KEY_EXT1));
        map.put(KqConsts.KEY_EXT2, request.get(KqConsts.KEY_EXT2));
        map.put(KqConsts.KEY_BOSS_TYPE, request.get(KqConsts.KEY_BOSS_TYPE));
        map.put(KqConsts.KEY_KEY, key);
        // 签名原文
        String signMsgVal = Help.getStrByMap(map, false);
        String signMsg = DigestUtils.md5DigestAsHex(signMsgVal.getBytes(Charset.forName(KqConsts.CHARSET_UTF8)))
                .toUpperCase();
        return signMsg;
    }

    public static String genPaySign(Map<String, String> request, String privateKeyFilePath, String password) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(KqConsts.KEY_INPUTCHARSET, request.get(KqConsts.KEY_INPUTCHARSET));
        map.put(KqConsts.KEY_PAGEURL, request.get(KqConsts.KEY_PAGEURL));
        map.put(KqConsts.KEY_BGURL, request.get(KqConsts.KEY_BGURL));
        map.put(KqConsts.KEY_VERSION, request.get(KqConsts.KEY_VERSION));
        map.put(KqConsts.KEY_LANGUAGE, request.get(KqConsts.KEY_LANGUAGE));
        map.put(KqConsts.KEY_SIGNTYPE, request.get(KqConsts.KEY_SIGNTYPE));
        map.put(KqConsts.KEY_MERCHANTACCTID, request.get(KqConsts.KEY_MERCHANTACCTID));
        map.put(KqConsts.KEY_PAYERNAME, request.get(KqConsts.KEY_PAYERNAME));
        map.put(KqConsts.KEY_PAYERCONTACTTYPE, request.get(KqConsts.KEY_PAYERCONTACTTYPE));
        map.put(KqConsts.KEY_PAYERCONTACT, request.get(KqConsts.KEY_PAYERCONTACT));
        map.put(KqConsts.KEY_PAYERIP, request.get(KqConsts.KEY_PAYERIP));
        map.put(KqConsts.KEY_ORDERID, request.get(KqConsts.KEY_ORDERID));
        map.put(KqConsts.KEY_ORDERAMOUNT, request.get(KqConsts.KEY_ORDERAMOUNT));
        map.put(KqConsts.KEY_ORDERTIME, request.get(KqConsts.KEY_ORDERTIME));
        map.put(KqConsts.KEY_ORDERTIMESTAMP, request.get(KqConsts.KEY_ORDERTIMESTAMP));
        map.put(KqConsts.KEY_PRODUCTNAME, request.get(KqConsts.KEY_PRODUCTNAME));
        map.put(KqConsts.KEY_PRODUCTNUM, request.get(KqConsts.KEY_PRODUCTNUM));
        map.put(KqConsts.KEY_PRODUCTID, request.get(KqConsts.KEY_PRODUCTID));
        map.put(KqConsts.KEY_PRODUCTDESC, request.get(KqConsts.KEY_PRODUCTDESC));
        map.put(KqConsts.KEY_EXT1, request.get(KqConsts.KEY_EXT1));
        map.put(KqConsts.KEY_EXT2, request.get(KqConsts.KEY_EXT2));
        map.put(KqConsts.KEY_PAYTYPE, request.get(KqConsts.KEY_PAYTYPE));
        map.put(KqConsts.KEY_BANKID, request.get(KqConsts.KEY_BANKID));
        map.put(KqConsts.KEY_REDOFLAG, request.get(KqConsts.KEY_REDOFLAG));
        map.put(KqConsts.KEY_PID, request.get(KqConsts.KEY_PID));
        // 签名原文
        String signMsgVal = Help.getStrByMap(map, false);
        String signMsg = Pkipair.signMsg(signMsgVal, privateKeyFilePath, password);
        return signMsg;
    }

    private static boolean genNotifySign(Map<String, String> request, String publicKeyFilePath) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(KqConsts.KEY_MERCHANTACCTID, request.get(KqConsts.KEY_MERCHANTACCTID).trim());
        map.put(KqConsts.KEY_VERSION, request.get(KqConsts.KEY_VERSION).trim());
        map.put(KqConsts.KEY_LANGUAGE, request.get(KqConsts.KEY_LANGUAGE).trim());
        map.put(KqConsts.KEY_SIGNTYPE, request.get(KqConsts.KEY_SIGNTYPE).trim());
        map.put(KqConsts.KEY_PAY_TYPE, request.get(KqConsts.KEY_PAY_TYPE).trim());
        map.put(KqConsts.KEY_BANKID, request.get(KqConsts.KEY_BANKID).trim());
        map.put(KqConsts.KEY_ORDERID, request.get(KqConsts.KEY_ORDERID).trim());
        map.put(KqConsts.KEY_ORDERTIME, request.get(KqConsts.KEY_ORDERTIME).trim());
        map.put(KqConsts.KEY_ORDERAMOUNT, request.get(KqConsts.KEY_ORDERAMOUNT).trim());
        map.put(KqConsts.KEY_DEALID, request.get(KqConsts.KEY_DEALID).trim());
        map.put(KqConsts.KEY_BANK_DEALID, request.get(KqConsts.KEY_BANK_DEALID).trim());
        map.put(KqConsts.KEY_DEALTIME, request.get(KqConsts.KEY_DEALTIME).trim());
        map.put(KqConsts.KEY_PAYAMOUNT, request.get(KqConsts.KEY_PAYAMOUNT).trim());
        map.put(KqConsts.KEY_FEE, request.get(KqConsts.KEY_FEE).trim());
        map.put(KqConsts.KEY_EXT1, request.get(KqConsts.KEY_EXT1).trim());
        map.put(KqConsts.KEY_EXT2, request.get(KqConsts.KEY_EXT2).trim());
        map.put(KqConsts.KEY_PAYRESULT, request.get(KqConsts.KEY_PAYRESULT).trim());
        map.put(KqConsts.KEY_ERRCODE, request.get(KqConsts.KEY_ERRCODE).trim());
        String toMd5Str = Help.getStrByMap(map, false);
        boolean flag = Pkipair.enCodeByCer(toMd5Str, request.get(KqConsts.KEY_SIGN_MSG), publicKeyFilePath);
        LOG.info("kq flag:{},toMd5Str:{},sing_msg:{}", flag, toMd5Str, request.get(KqConsts.KEY_SIGN_MSG));
        return flag;
    }

    // change to private
    public static String genNotifySignForCard(Map<String, String> request, String key) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(KqConsts.KEY_MERCHANTACCTID, request.get(KqConsts.KEY_MERCHANTACCTID));
        map.put(KqConsts.KEY_VERSION, request.get(KqConsts.KEY_VERSION));
        map.put(KqConsts.KEY_LANGUAGE, request.get(KqConsts.KEY_LANGUAGE));
        map.put(KqConsts.KEY_PAY_TYPE, request.get(KqConsts.KEY_PAY_TYPE));
        map.put(KqConsts.KEY_CARD_NUM, request.get(KqConsts.KEY_CARD_NUM));
        map.put(KqConsts.KEY_CARD_PWD, request.get(KqConsts.KEY_CARD_PWD));
        map.put(KqConsts.KEY_ORDERID, request.get(KqConsts.KEY_ORDERID));
        map.put(KqConsts.KEY_ORDERAMOUNT, request.get(KqConsts.KEY_ORDERAMOUNT));
        map.put(KqConsts.KEY_DEALID, request.get(KqConsts.KEY_DEALID));
        map.put(KqConsts.KEY_ORDERTIME, request.get(KqConsts.KEY_ORDERTIME));
        map.put(KqConsts.KEY_EXT1, request.get(KqConsts.KEY_EXT1));
        map.put(KqConsts.KEY_EXT2, request.get(KqConsts.KEY_EXT2));
        map.put(KqConsts.KEY_PAYAMOUNT, request.get(KqConsts.KEY_PAYAMOUNT));
        map.put(KqConsts.KEY_BILL_ORDERTIME, request.get(KqConsts.KEY_BILL_ORDERTIME));
        map.put(KqConsts.KEY_PAYRESULT, request.get(KqConsts.KEY_PAYRESULT));
        map.put(KqConsts.KEY_SIGNTYPE, request.get(KqConsts.KEY_SIGNTYPE));
        map.put(KqConsts.KEY_BOSS_TYPE, request.get(KqConsts.KEY_BOSS_TYPE));
        map.put(KqConsts.KEY_RECEIVE_BOSSTYPE, request.get(KqConsts.KEY_RECEIVE_BOSSTYPE));
        map.put(KqConsts.KEY_RECEIVE_ACCTID, request.get(KqConsts.KEY_RECEIVE_ACCTID));
        map.put(KqConsts.KEY_KEY, key);
        String toMd5Str = Help.getStrByMap(map, false);
        String merchantSignMsg = DigestUtils.md5DigestAsHex(toMd5Str.getBytes(Charset.forName(KqConsts.CHARSET_UTF8)))
                .toUpperCase();
        return merchantSignMsg;
    }

    /**
     * 根据异步（NotifyUrl）中的信息，组装PayOrder
     */
    public static PayOrder assemblePayOrder(DomainResource resource, Map<String, String> params, boolean isCard) {
        // get appId and appOrderId
        String orderId = params.get(KqConsts.KEY_ORDERID).trim();
        PayOrder payOrder = Help.getPayOrderByNotify(resource, orderId);
        String signType = params.get(KqConsts.KEY_SIGNTYPE);
        String signExpect = params.get(KqConsts.KEY_SIGN_MSG).trim();
        String signInReq = "";
        // card支付跟银行支付不一样
        if (!isCard) {
            String publicKeyFilePath = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(),
                    KqConsts.KEY_PUBLIC_KEY_FILE_PATH);
            boolean signFlag = genNotifySign(params, publicKeyFilePath);
            if (!signFlag) {
                throw new PayException(Consts.SC.SECURE_ERROR, "sign: unsupported type or unmatched");
            }
        } else {
            signInReq = genNotifySignForCard(params, payOrder.getAppChInfo().getChPayKeyMd5());
            if (!signInReq.equals(signExpect)) {
                String msg = String.format(
                        "sign: unsupported type or unmatched! signType=%s, signInReq=%s, signExpect=%s", signType,
                        signInReq, signExpect);
                throw new PayException(Consts.SC.SECURE_ERROR, msg);
            }
        }
        if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode())) {
            return payOrder;
        }
        // 更新结果前先作业务级的安全校验
        // validatePayOrderStatusForUpdate(payOrder);
        validatePayOrderOldValues(payOrder, params.get(KqConsts.KEY_ORDERAMOUNT),
                params.get(KqConsts.KEY_MERCHANTACCTID));

        // 更新结果
        payOrder.setChDealId(params.get(KqConsts.KEY_DEALID));
        String payResult = params.get(KqConsts.KEY_PAYRESULT);
        // 在人民币网关的情况下，payResult字段只有一种有明确定义的返回值（成功为10），其他表示订单失败。
        // 在充值卡的情况下，payResult字段有两种明确定义的返回值（成功为10，失败为11），其他状态未知，为不正常状态。
        if (!isCard) {
            if (KqConsts.PAY_RESULT_SUCCESS.equalsIgnoreCase(payResult)) {
                payOrder.setStatusCode(Consts.SC.SUCCESS);
                payOrder.setStatusMsg("快钱：支付成功");
            } else {
                payOrder.setStatusCode(Consts.SC.FAIL);
                payOrder.setStatusMsg("快钱："
                        + KqStatusMsgHelper.translateBankPayResponseErrCode(params.get(KqConsts.KEY_ERRCODE)));
            }
            payOrder.setBankId(params.get(KqConsts.KEY_BANKID));
            payOrder.setBankDealId(params.get(KqConsts.KEY_BANK_DEALID));
            payOrder.setChDealTime(params.get(KqConsts.KEY_DEALTIME));
        } else {
            if (KqConsts.PAY_RESULT_SUCCESS.equalsIgnoreCase(payResult)) {
                payOrder.setStatusCode(Consts.SC.SUCCESS);
                payOrder.setStatusMsg("快钱：支付成功");
            } else if (KqConsts.PAY_RESULT_FAIL.equalsIgnoreCase(payResult)) {
                payOrder.setStatusCode(Consts.SC.FAIL);
                payOrder.setStatusMsg("快钱：支付失败");
            } else {
                payOrder.setStatusCode(Consts.SC.UNKNOWN);
                payOrder.setStatusMsg("快钱：未知的返回状态码，payResult：" + payResult);
            }
            // TODO 时候需要验证cardnum 跟 cardpass
            payOrder.setChDealTime(params.get(KqConsts.KEY_BILL_ORDERTIME));
        }
        return payOrder;
    }

    private static void validatePayOrderStatusForUpdate(PayOrder payOrder) {
        String statusCode = payOrder.getStatusCode();
        if (Consts.SC.SUCCESS.equals(statusCode) || Consts.SC.FAIL.equals(statusCode)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "Order statusCode is final, should not update!", payOrder);
        }
    }

    private static void validatePayOrderOldValues(PayOrder payOrder, String totalFee, String sellerId) {
        String chAccountId = payOrder.getAppChInfo().getChAccountId();
        boolean chAccountIdNotMatch = StringUtils.isNotBlank(sellerId) && !chAccountId.equals(sellerId);
        boolean amountNotMatch = !StringHelper.getAmount(payOrder.getAmount()).equals(totalFee);
        if (chAccountIdNotMatch || amountNotMatch) {
            String msg = String.format("chAccountId or amount not matched with old value! '%s'='%s', '%s'='%s'",
                    chAccountId, sellerId, StringHelper.getAmount(payOrder.getAmount()), totalFee);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg, payOrder);
        }
    }

    /**
     * 获取块钱的查询签名
     * 
     * @param payOrder
     * @return
     */
    public static String getQuerySign(PayOrder payOrder) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(KqConsts.KEY_INPUTCHARSET, KqConsts.KQ_INPUT_CHARSET_UTF8);
        map.put(KqConsts.KEY_VERSION, KqConsts.KQ_VERSION_2);
        map.put(KqConsts.KEY_SIGNTYPE, KqConsts.KQ_SIGN_TYPE_MD5);
        map.put(KqConsts.KEY_MERCHANTACCTID, payOrder.getAppChInfo().getChAccountId());
        map.put(KqConsts.KEY_QUERYTYPE, KqConsts.KQ_QUERY_TYPE_ORDER);
        map.put(KqConsts.KEY_QUERYMODE, KqConsts.KQ_QUERY_MODE);
        map.put(KqConsts.KEY_ORDERID, payOrder.getChOrderId());
        String password = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), KqConsts.KQ_QUERY_KEY);
        if (StringUtils.isBlank(password)) {
            password = payOrder.getAppChInfo().getChPayKeyMd5();
        }
        map.put(KqConsts.KEY_KEY, password);
        String signMsgVal = Help.getStrByMap(map, false);
        try {
            String signMsg = org.apache.commons.codec.digest.DigestUtils.md5Hex(
                    signMsgVal.getBytes(KqConsts.CHARSET_UTF8)).toUpperCase();
            return signMsg;
        } catch (UnsupportedEncodingException e) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "sign error", payOrder, e);
        }
    }

    /**
     * 组装通知成功后的url
     * 
     * @param url
     * @return
     */
    public static String assembleNotifySuccessUrl(String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(KqConsts.NOTIFY_RESULT_SUCCESS).append(KqConsts.REDIRECTURL).append(url)
                .append(KqConsts.ADDR_YYPAY_RETURN).append(KqConsts.UREDIRECTURL);
        return sb.toString();
    }

    /**
     * 组装通知成功后的url
     * 
     * @param url
     * @param isCard true:网易卡，盛大卡，神州行卡 false:银行
     * @return
     */
    public static String assembleNotifySuccessUrl(String url, boolean isCard) {
        if (isCard) {
            StringBuilder sb = new StringBuilder();
            sb.append(KqConsts.NOTIFY_RESULT_SUCCESS).append(KqConsts.REDIRECTURL).append(url)
                    .append(KqConsts.ADDR_YYPAY_CARD_RETURN).append(KqConsts.UREDIRECTURL);
            return sb.toString();
        } else {
            return assembleNotifySuccessUrl(url);
        }
    }

    /**
     * 组装returrn payorder
     * 
     * @param resource
     * @param params
     * @return
     */
    public static PayOrder assembleNotifyPayOrder(DomainResource resource, Map<String, String> params) {
        String orderId = params.get(KqConsts.KEY_ORDERID).trim();
        PayOrder payOrder = Help.getPayOrderByNotify(resource, orderId);
        String publicKeyFilePath = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(),
                KqConsts.KEY_PUBLIC_KEY_FILE_PATH);
        boolean signFlag = genNotifySign(params, publicKeyFilePath);
        if (!signFlag) {
            throw new PayException(Consts.SC.SECURE_ERROR, "sign: unsupported type or unmatched");
        }
        return payOrder;
    }

    /**
     * 组装return payOrder
     * 
     * @param resource
     * @param params
     * @param isCard true:网易卡，盛大卡，神州行卡 false:银行
     * @return
     */
    public static PayOrder assembleNotifyPayOrder(DomainResource resource, Map<String, String> params, boolean isCard) {
        if (isCard) {
            String orderId = params.get(KqConsts.KEY_ORDERID).trim();
            PayOrder payOrder = Help.getPayOrderByNotify(resource, orderId);
            String signType = params.get(KqConsts.KEY_SIGNTYPE);
            String signInReq = params.get(KqConsts.KEY_SIGN_MSG).trim();
            String SignExpect = genNotifySignForCard(params, payOrder.getAppChInfo().getChPayKeyMd5());
            if (!SignExpect.equals(signInReq)) {
                String msg = String.format(
                        "sign: unsupported type or unmatched! signType=%s, SignExpect=%s, signInReq=%s", signType,
                        SignExpect, signInReq);
                throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
            }
            return payOrder;
        } else {
            return assembleNotifyPayOrder(resource, params);
        }
    }

    /**
     * 根据请求结果对payorder 进行组装
     * 没有看到这方面的文档，有知道的补充一下吧。个人感觉，这个方法名要是能，突出体现更新订单状态的作用就好了。By An observer。
     * 
     * @param payOrder
     * @param respStr
     * @return
     */
    public static PayOrder assemblePayCardPayOrder(PayOrder payOrder, String respStr) {
        String[] results = respStr.split(Consts.AMP);
        Map<String, String> map = new HashMap<String, String>();
        for (String s : results) {
            String[] temp = s.split(Consts.EQ);
            if (temp.length == 2) {
                map.put(temp[0], temp[1]);
            }
        }
        if (KqConsts.CARD_REQ_SUCCESS.equalsIgnoreCase(map.get(KqConsts.RETURNCODE))) {
            payOrder.setStatusCode(Consts.SC.PENDING);
        } else if (KqConsts.CARD_REQ_ERROR.equalsIgnoreCase(map.get(KqConsts.RETURNCODE))) {
            LOG.info("[assemblePayCardPayOrder] kq get a wrong cardnum and cardpass,orderid:{}",
                    payOrder.getAppOrderId());
            // 当returncode为30019时，认为是卡密错误，具体的提示信息是“卡密已失效”
            payOrder.setStatusCode(Consts.SC.CARD_ERROR);
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        payOrder.setStatusMsg(KqStatusMsgHelper.translateCardPayResponseReturnCode(map.get(KqConsts.RETURNCODE)));
        return payOrder;
    }
}
