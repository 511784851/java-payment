/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.rule;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.traderule.TradeRuleConsts;

/**
 * @author Administrator
 *         未成为paypal 认证账号不允许充值
 */
public class PaypalCerAccountRule implements RuleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PaypalCerAccountRule.class);

    @Override
    public Map<String, String> validator(Map<String, String> params) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        String status = params.get(TradeRuleConsts.PAYERSTATUS);
        if (StringUtils.isBlank(status)) {
            LOG.info("[PaypalCerAccountRule.validator] not verified,status is empty", TraceHelper.getTrace(params));
            resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_003);
            return resultMap;
        }
        if (!TradeRuleConsts.VERIFIED.equalsIgnoreCase(status)) {
            LOG.info("[PaypalCerAccountRule.validator]not verified, status:{}", status, TraceHelper.getTrace(params));
            resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_003);
            return resultMap;
        }
        return resultMap;
    }

}
