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
 * @author Administrator
 *         金额校验
 *         每次充值限额=int(充值额度/1000)*100+100，单位：美元
 *         充值额度=历史充值总额-过去24小时内的充值金额，（只计算PayPal渠道的充值额度）
 */
public class PayAmountLimitRule implements RuleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PayAmountLimitRule.class);
    @Autowired
    private DomainResource domainResource;

    @Override
    public Map<String, String> validator(Map<String, String> params) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        String gbuid = params.get(TradeRuleConsts.gbUID);
        String chId = params.get(TradeRuleConsts.CHID);
        String payAmount = params.get(TradeRuleConsts.AMOUNT);
        if (StringUtils.isBlank(gbuid) || StringUtils.isBlank(chId) || StringUtils.isBlank(payAmount)) {
            return resultMap;
        }
        String endtime = TimeHelper.nDayDate(-7, TimeHelper.TIME_FORMATE);
        int payTimes = domainResource.getPayTimesByEndTime(gbuid, chId, endtime);
        // 一个星期之前没有充值过,最高金额为30美金
        if (payTimes == 0) {
            if (Double.parseDouble(payAmount) > 30) {
                LOG.info("[PayAmountLimitRule.validator] pay gbuid:{},ammout is {},limit amount is {}", gbuid,
                        payAmount, "30", TraceHelper.getTrace(params));
                resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
                resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_100);
                return resultMap;
            }
            return resultMap;
        } else {
            // 对于以及充值过的用户,依照旧的规则
            String totalAmount = domainResource.getHisTotalAmount(gbuid, chId);
            if (StringUtils.isBlank(totalAmount)) {
                totalAmount = TradeRuleConsts.ZERO;
            }
            String startTime = TimeHelper.nDayDate(-1, TimeHelper.TIME_FORMATE);
            String endTime = TimeHelper.get(8, new Date());
            String recentDaytotalAmount = domainResource.getHisTotalAmountByTime(gbuid, chId, startTime, endTime);
            if (StringUtils.isBlank(recentDaytotalAmount)) {
                recentDaytotalAmount = TradeRuleConsts.ZERO;
            }
            int amount = (int) ((Double.parseDouble(totalAmount) - Double.parseDouble(recentDaytotalAmount)) / 1000 * 100 + 100);
            if (Double.parseDouble(payAmount) > amount) {
                LOG.info("[PayAmountLimitRule.validator]pay gbuid:{},ammout is {},limit amount is {}", gbuid,
                        payAmount, amount, TraceHelper.getTrace(params));
                resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
                resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_101);
                return resultMap;
            } else {
                return resultMap;
            }
        }
    }

    public DomainResource getDomainResource() {
        return domainResource;
    }

    public void setDomainResource(DomainResource domainResource) {
        this.domainResource = domainResource;
    }

}
