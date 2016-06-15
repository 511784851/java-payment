/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.rule;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.traderule.TradeRuleConsts;

/**
 * 2小时充值成功笔数不能大于3笔
 * 24小时充值成功笔数不能大于5笔
 * 
 * @author administrator
 * 
 */
public class TimeInnerOrderRule implements RuleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(TimeInnerOrderRule.class);
    private static final String INIT_TOTAL_AMOUNT = "0";
    @Autowired
    private DomainResource domainResource;

    @Override
    public Map<String, String> validator(Map<String, String> params) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        String startTime = TimeHelper.get(8, TimeHelper.alterHour(new Date(), -2));
        String endTime = TimeHelper.get(8, new Date());
        String account = params.get(TradeRuleConsts.PAYERID);
        String gbuid = params.get(TradeRuleConsts.gbUID);
        String payAmount = params.get(TradeRuleConsts.AMOUNT);
        if (StringUtils.isBlank(account)) {
            return resultMap;
        }
        int twoHourInner = domainResource.getAccountNumberByTime(account, startTime, endTime);
        // 2小时3笔以内
        if (twoHourInner >= 3) {
            LOG.info(
                    "[TimeInnerOrderRule.validator] pay too much in 2 hour,account:{}, gbuid:{},pay time:{} ,times: {}",
                    account, gbuid, endTime, twoHourInner, TraceHelper.getTrace(params));
            resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_102);
            return resultMap;
        }
        startTime = TimeHelper.get(8, TimeHelper.alterHour(new Date(), -24));
        int hourInner = domainResource.getAccountNumberByTime(account, startTime, endTime);
        // 24小时10笔以内
        if (hourInner >= 5) {
            LOG.info(
                    "[TimeInnerOrderRule.validator] pay too much in 24 hour,account:{},gbuid:{},pay time:{} ,times: {}",
                    account, gbuid, endTime, hourInner, TraceHelper.getTrace(params));
            resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_103);
            return resultMap;
        }
        String recentDaytotalAmount = domainResource.getTotalAmountByTime(account, startTime, endTime);
        if (StringUtils.isBlank(recentDaytotalAmount)) {
            recentDaytotalAmount = INIT_TOTAL_AMOUNT;
        }
        // 24小时总额度400美金
        if (Double.parseDouble(recentDaytotalAmount) + Double.parseDouble(payAmount) > 400) {
            LOG.info(
                    "[TimeInnerOrderRule.validator] pay limit ,account:{},gbuid:{},24 hours totalAmount:{},payAmount:{}",
                    account, gbuid, recentDaytotalAmount, payAmount, TraceHelper.getTrace(params));
            resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_107);
            return resultMap;
        }
        return resultMap;
    }
}
