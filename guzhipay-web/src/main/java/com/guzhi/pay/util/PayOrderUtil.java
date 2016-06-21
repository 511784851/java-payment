/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.SecureHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.ThreadHelper;
import com.guzhi.pay.helper.ValidateHelper;

/**
 * @author Administrator
 * 
 */
public class PayOrderUtil {
    private static Logger log = LoggerFactory.getLogger(PayOrderUtil.class);
    private static final BigDecimal LIMIT_AMOUNT = new BigDecimal("200000");
    private static final String QUESTION_MARK = "?";
    private static final String EQUAL = "=";
    private static final String AMP = "&";
    private static final String ALIPAY = "alipay.com/gateway.do";
    private static final String URL_ALIPAY = "?_input_charset=utf-8";
    private static final String RATE = "rate";

    @SuppressWarnings("rawtypes")
    public static PayOrder assemblePayOrder(DomainResource resource, String data, String sign, String appId,
            Class viewClass, Class validateGroup, boolean assembleAppChInfo) {

        // validates Fields
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(data) || StringUtils.isBlank(sign)) {
            throw new PayException(Consts.SC.REQ_ERROR, "appId/sign/data empty in request! TReq=" + sign + " " + appId
                    + " " + data);
        }

        // validate appInfo
        AppInfo appInfo = resource.getAppInfo(appId);
        if (appInfo == null || Consts.Status.INVALID.equalsIgnoreCase(appInfo.getStatus())) {
            throw new PayException(Consts.SC.APP_INFO_ERROR, "appInfo inexist/invalid, appId=" + appId);
        }

        // validate App Ip Address
        String reqIp = ThreadHelper.getAppIp();
        String ipWhitelist = appInfo.getIpWhitelist();
        log.info("[assemblePayOrder] ip whitelist:{},reqIp:{}", ipWhitelist, reqIp);
        if (!StringUtils.isBlank(ipWhitelist) && !ipWhitelist.contains(reqIp)) {
            throw new PayException(Consts.SC.APP_INFO_ERROR, "the request IP in not in white list, reqIp=" + reqIp
                    + ",ipWhitelist=" + ipWhitelist);
        }

        // validate sign
        SecureHelper.verifyMd5Sign(appInfo.getKey(), sign, data);

        // assemble PayOrder
        PayOrder payOrder = JsonHelper.reqJsonToPayOrder(data, viewClass);
        payOrder.setAppId(appId);
        payOrder.setAppInfo(appInfo);
        payOrder.setAppIp(ThreadHelper.getAppIp());
        if (payOrder.getAmount() != null && payOrder.getAmount().compareTo(LIMIT_AMOUNT) == 1) {
            log.warn("[assemblePayOrder] it pays too much,payorder:{}", payOrder);
            throw new PayException(Consts.SC.DATA_ERROR, "amount error", payOrder);
        }
        if (payOrder.getYyAmount() != null && payOrder.getYyAmount().compareTo(LIMIT_AMOUNT) == 1) {
            log.warn("[assemblePayOrder] it pays too much YYB,payorder:{}", payOrder);
            throw new PayException(Consts.SC.DATA_ERROR, "yyamount error", payOrder);
        }
        BigDecimal amount = payOrder.getAmount();
        BigDecimal yyAmount = payOrder.getYyAmount();

        // if ((null != amount) && (null != yyAmount)) {
        // double scale = amount.doubleValue() / yyAmount.doubleValue();
        // if (scale < 1) {
        // throw new PayException(Consts.SC.DATA_ERROR, "yyamount error",
        // payOrder);
        // }
        // }
        // 如果payMethod为空，bankId不为空，则默认payMethod为Gate
        if (StringUtils.isBlank(payOrder.getPayMethod()) && StringUtils.isNotBlank(payOrder.getBankId())) {
            payOrder.setPayMethod(Consts.PayMethod.GATE);
        }

        // 不需要组装appChInfo的情况：查询会依赖从DB中找到的信息，退款是异步的操作
        if (assembleAppChInfo) {
            List<AppChInfo> appChInfos = StringUtils.isNotBlank(payOrder.getBankId())
                    && Consts.PayMethod.GATE.equalsIgnoreCase(payOrder.getPayMethod()) ? resource.getAppChInfo(appId,
                    payOrder.getChId(), payOrder.getPayMethod(), payOrder.getBankId()) : resource.getAppChInfo(appId,
                    payOrder.getChId(), payOrder.getPayMethod());
            AppChInfo appChInfo = siftAppChInfo(appChInfos);
            if (appChInfo == null || Consts.Status.INVALID.equalsIgnoreCase(appChInfo.getStatus())) {
                throw new PayException(Consts.SC.CHANNEL_INFO_ERROR, "appChInfo inexist/invalid", payOrder);
            }

            payOrder.setAppChInfo(appChInfo);
            payOrder.setChAccountId(appChInfo.getChAccountId());
            payOrder.setChId(appChInfo.getChId()); // 有些情况，请求中只指定了PayMethod，需要记录使用了哪个ChId
            payOrder.setPayMethod(appChInfo.getPayMethod());
        }

        String rate;
        if (payOrder.getAppChInfo() != null && StringUtils.isNotBlank(payOrder.getAppChInfo().getAdditionalInfo())) {
            rate = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), RATE);
        } else {
            rate = "";
        }
        // 校验amount的yyamount的值
        if (!AmountUtils.checkAmountRate(amount, yyAmount, rate)) {
            throw new PayException(Consts.SC.DATA_ERROR, "yyamount error", payOrder);
        }

        // validate payOrderFields
        ValidateHelper.validatePayOrderFields(payOrder, validateGroup);

        return payOrder;
    }

    /**
     * 根据权重筛选出一个渠道
     * 
     * @param appChInfos
     * @return
     */
    public static AppChInfo siftAppChInfo(List<AppChInfo> appChInfos) {
        log.debug("select(sift) one appChInfo, appChInfos={}", appChInfos);

        // 简单返回
        if (CollectionUtils.isEmpty(appChInfos)) {
            return null;
        }
        int size = appChInfos.size();
        if (size == 1) {
            return appChInfos.get(0);
        }

        // 根据权重筛选
        double sum = 0;
        for (AppChInfo a : appChInfos) {
            sum += a.getChWeight();
        }
        if (sum == 0) {
            return appChInfos.get(RandomUtils.nextInt(size));
        }

        List<AppChInfo> list = new ArrayList<AppChInfo>();
        for (AppChInfo a : appChInfos) {
            int pct = (int) (a.getChWeight() / sum * 100);
            for (int i = 0; i < pct; i++) {
                list.add(a);
            }
        }
        Collections.shuffle(list);
        return list.get(RandomUtils.nextInt(list.size()));
    }

    /**
     * 异常时组装异常错误
     * 
     * @param t
     * @param payOrderInParam
     * @return
     */
    public static String getErrorResp(Map<String, String> params, Throwable t, PayOrder payOrderInParam) {
        PayOrder payOrderForResp = null;
        PayException payException = null;

        // 尽量得到原始的PayOrder（注意抛出异常时，可能连基本的PayOrder都没有组装出来）
        if (t instanceof PayException) { // 优先使用异常抛出时所以包含的PayOrder
            payException = (PayException) t;
            payOrderForResp = payException.getPayOrder();
        }

        // 其次使用方法参数中的PayOrder 或 新建一个
        if (payOrderForResp == null) {
            payOrderForResp = (payOrderInParam == null) ? new PayOrder() : payOrderInParam;
        }

        // 组装信息到PayOrder（注意这里设置的信息不会进入数据库！）
        payOrderForResp.setAppId(params.get(Consts.APPID));
        payOrderForResp.setStatusCode(Consts.SC.INTERNAL_ERROR);
        Throwable cause = t;
        String statusMsg = "Get exception: " + (cause == null ? "" : cause.getMessage());
        while (cause != null && cause.getCause() != null) {
            cause = cause.getCause();
            statusMsg = statusMsg + " > " + cause.getMessage();
        }
        payOrderForResp.setStatusMsg(statusMsg);

        if (payException != null) {
            payOrderForResp.setStatusCode(payException.getStatusCode());
            payOrderForResp.setStatusMsg(payException.getStatusMsg());
        }

        // 组装Response
        String data = JsonHelper.payOrderToRespJson(payOrderForResp);
        String sign = "AppInfoNull";

        if (payOrderForResp.getAppInfo() != null) {
            // 注意这里不应再调用resource查找appId，否则可能直接抛出异常给Thrift
            sign = SecureHelper.genMd5Sign(payOrderForResp.getAppInfo().getKey(), data);
        }
        String result = "appId=" + payOrderForResp.getAppId() + "&sign=" + sign + "&data="
                + StringHelper.encode(data, Consts.CHARSET_UTF8);
        log.info("return error to app:={}", result);
        return result;
    }

    /**
     * 组装正常的错误
     * payOrder必须包含appInfo信息
     * 
     * @param t
     * @param payOrderInParam
     * @return
     */
    public static String getResp(PayOrder payOrder) {
        String data = JsonHelper.payOrderToRespJson(payOrder);
        String sign = SecureHelper.genMd5Sign(payOrder.getAppInfo().getKey(), data);
        String result = "appId=" + payOrder.getAppId() + "&sign=" + sign + "&data="
                + StringHelper.encode(data, Consts.CHARSET_UTF8);
        log.info("return to app:={}", result);
        return result;
    }

    /**
     * 输出结果
     * 
     * @param response
     * @param str
     */
    public static void outPutStr(HttpServletResponse response, String str) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(str);
            pw.flush();
        } catch (IOException e) {
            log.error("outPut str error:{}", e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

    }

    public static Map<String, String> assemblePayUrlMap(PayOrder payOrder) {
        Map<String, String> payUrlMap = new HashMap<String, String>();
        String payUrl = payOrder.getPayUrl();
        String[] payUrlComponentArray = StringUtils.split(payUrl, QUESTION_MARK);
        if (payUrlComponentArray[0].indexOf(ALIPAY) != -1) {
            payUrlComponentArray[0] = payUrlComponentArray[0] + URL_ALIPAY;
        }
        payUrlMap.put("payUrl", payUrlComponentArray[0]);
        if (payUrlComponentArray.length < 2) {
            return payUrlMap;
        }
        if (payUrlComponentArray.length > 2) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "支付平台：不支持直接跳转的payUrl形式");
        }
        String[] kvs = StringUtils.split(payUrlComponentArray[1], AMP);
        for (String kv : kvs) {
            String key = kv.substring(0, kv.indexOf(EQUAL));
            String value = kv.substring(kv.indexOf(EQUAL) + 1);
            if (StringUtils.isNotBlank(value)) {
                payUrlMap.put(key, URLDecoder.decode(value));
            } else {
                payUrlMap.put(key, "");
            }
        }
        return payUrlMap;
    }

    /**
     * 
     * @param resource
     * @param appId
     * @param appOrderId
     */
    public static PayOrder assemblePayOrder(DomainResource resource, String appId, String appOrderId) {
        PayOrder payOrderInDb = resource.getPayOrder(appId, appOrderId);
        if (payOrderInDb == null) {
            return null;
        }
        AppInfo appInfo = resource.getAppInfo(payOrderInDb.getAppId());
        payOrderInDb.setAppInfo(appInfo);
        return payOrderInDb;
    }
}
