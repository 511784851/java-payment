/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.PayOrder;

/**
 * @author Administrator
 * 
 */
public class StringHelper {
    private static final Logger LOG = LoggerFactory.getLogger(StringHelper.class);
    private static final String EQ = "=";
    private static final String AMP = "&";
    private final static String[] str = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "q", "w", "e", "r", "t",
            "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m" };
    private static final DecimalFormat decimalFormat = new DecimalFormat("0");

    /**
     * 拼接字符串 A=XXX@B=XXX
     * 
     * @param param
     * @return
     */
    public static String assembleResqStr(Map<String, String> param) {
        return assembleResqStr(param, "utf8");
    }

    public static String assembleResqStr(Map<String, String> param, String charsetName) {
        StringBuilder builder = new StringBuilder();
        for (String key : param.keySet()) {
            if (StringUtils.isBlank(param.get(key))) {
                continue;
            }
            try {
                String ecodedStr = URLEncoder.encode(param.get(key), charsetName);
                builder.append(key).append(EQ).append(ecodedStr).append(AMP);
            } catch (UnsupportedEncodingException e) {
                LOG.warn("get exception when creating resq string, key={}, value={}", key, param.get(key), e);
            }
        }
        String payStr = builder.toString().replaceAll("&$", ""); // remove last
                                                                 // "&"
        LOG.debug("assembled resq string, result={}, map={}", payStr, param);
        return payStr;
    }

    /**
     * 拼接字符串形成key1=value1&key2=value2.<br>
     * 
     * @param param 参数集合
     * @param charsetName 字符编码集
     * @param sort 是否需要排序
     * @param blankable 是否允许空值
     * @return
     */
    public static String assembleResqStr(Map<String, String> param, String charsetName, boolean sort, boolean blankable) {
        List<String> keys = new ArrayList<String>(param.keySet());
        if (sort) {
            Collections.sort(keys);
        }
        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            if (!blankable && StringUtils.isBlank(param.get(key))) {
                continue;
            }
            if (StringUtils.isBlank(charsetName)) {
                builder.append(key).append(EQ).append(param.get(key)).append(AMP);
            } else {
                try {
                    String ecodedStr = URLEncoder.encode(param.get(key), charsetName);
                    builder.append(key).append(EQ).append(ecodedStr).append(AMP);
                } catch (UnsupportedEncodingException e) {
                    LOG.warn("get exception when creating resq string, key={}, value={}", key, param.get(key), e);
                }
            }
        }
        String payStr = builder.toString().replaceAll("&$", ""); // remove last
        LOG.debug("assembled resq string, result={}, map={}", payStr, param);
        return payStr;
    }

    /**
     * 对字符串解码
     * 
     * @param str
     * @param enc
     * @return
     */
    public static String decode(String str, String dec) {
        String decodeRespStr = "";
        try {
            decodeRespStr = URLDecoder.decode(str, dec);
        } catch (UnsupportedEncodingException e) {
            LOG.error("decode with error:{}", e.getMessage());
        }
        return decodeRespStr;
    }

    /**
     * 对字符串encode
     * 
     * @param str
     * @param enc
     * @return
     */
    public static String encode(String str, String enc) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            str = URLEncoder.encode(str, enc);
        } catch (UnsupportedEncodingException e) {
            LOG.error("data encoder fail", e);
        }
        return str;
    }

    public static String getRandomCharStr(int length) {
        String s = "";
        for (int i = 0; i < length; i++) {
            int a = (int) (Math.random() * 36);
            s += str[a];
        }
        return s;
    }

    /**
     * 截取statusMsg的长度
     * 
     * @param statusMsg
     * @return
     */
    public static String getStatusMsg(String statusMsg) {
        String newStatusMsg = "";
        if (StringUtils.isNotBlank(statusMsg) && (statusMsg.length() > 990)) {
            newStatusMsg = statusMsg.substring(0, 990);
            LOG.info("statusMsg is too long statusMsg:{}", statusMsg);
        }
        return newStatusMsg;
    }

    /**
     * 判断字符串是否为Null,如果为null,返回空串
     * 
     * @param str
     * @return
     */
    public static String getNotNullString(String str) {
        return StringUtils.isEmpty(str) ? "" : str;
    }

    /**
     * 把金额单位变成分并且无小数
     * 
     * @param amount
     * @return
     */
    public static String getAmount(BigDecimal amount) {
        return String.valueOf(decimalFormat.format(amount.doubleValue() * 100));
    }

    /**
     * 组装支付结果的URL（同步返回和异步通知均用这个方法），用于调用业务方
     */
    public static String assemblePayResultQueryStr(PayOrder payOrder) {

        AppInfo appInfo = payOrder.getAppInfo();

        Map<String, String> params = new HashMap<String, String>();
        payOrder.setPayUrl("");
        String data = JsonHelper.payOrderToRespJson(payOrder);
        params.put(Consts.Http.PARAM_DATA, data);
        params.put(Consts.Http.PARAM_APP_ID, payOrder.getAppId());
        params.put(Consts.Http.PARAM_SIGN, SecureHelper.genMd5Sign(appInfo.getKey(), data));

        return assembleResqStr(params);
    }

    /**
     * 对字符串encode
     * 
     * @param str
     * @return
     */
    public static String encodeStr(String str) {
        return encode(str, Consts.CHARSET_UTF8);
    }
}
