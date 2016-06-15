/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.traderule.TradeRuleConsts;

/**
 * @author administrator
 *         只允许非中国大陆的IP付款
 */
public class IpFilterRule implements RuleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(IpFilterRule.class);
    private static String taobaoIpSearchUrl = "http://ip.taobao.com/service/getIpInfo.php?ip=";
    private static String KEY_CODE = "code";
    private static String KEY_DATA = "data";
    private static String KEY_COUNTRY_ID = "country_id";
    private static String KEY_COUNTRY = "country";
    private static String CHINA1 = "86";
    private static String CHINA2 = "CN";
    private static String CHINA3 = "中国";
    private static List<String> iPWhite = new ArrayList<String>();
    static {
        iPWhite.add("183.60.177.228");
        iPWhite.add("183.60.177.227");
    }

    @Override
    public Map<String, String> validator(Map<String, String> params) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        String ip = params.get(TradeRuleConsts.IP);
        String gbuid = params.get(TradeRuleConsts.gbUID);
        LOG.info("[IpFilterRule.validator] ip:{},gbuid:{}", ip, gbuid, TraceHelper.getTrace(params));
        if (StringUtils.isBlank(ip)) {
            return resultMap;
        }
        if (iPWhite.contains(ip)) {
            return resultMap;
        }
        boolean flag = filerIp(ip);
        if (flag) {
            LOG.error("[IpFilterRule.validator] ip:{} is in china", ip, TraceHelper.getTrace(params));
            resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_002);
            return resultMap;
        } else {
            return resultMap;
        }
    }

    @SuppressWarnings("rawtypes")
    private static boolean filerIp(String ip) {
        String url = taobaoIpSearchUrl + ip;
        String respStr = "";
        try {
            respStr = HttpClientHelper.sendRequest(url, Consts.CHARSET_UTF8);
            LOG.info("[IpFilterRule.filterIp] taobao getIpData ip:{},respStr:{}", ip, respStr);
            Map result = JsonHelper.fromJson(respStr, Map.class);
            // success
            if (!((Integer) result.get(KEY_CODE) == 0)) {
                return false;
            }
            Map data = (Map) result.get(KEY_DATA);
            if (CHINA1.equals(data.get(KEY_COUNTRY_ID)) || CHINA2.equals(data.get(KEY_COUNTRY_ID))
                    || CHINA3.equals(data.get(KEY_COUNTRY))) {
                return true;
            }
            return false;
        } catch (Throwable e) {
            LOG.warn("[IpFilterRule.filterIp] taobao getIpData exception,ip:{},gbuid:{},respStr:{}", ip, respStr, e);
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(filerIp("115.85.144.0"));
    }
}
