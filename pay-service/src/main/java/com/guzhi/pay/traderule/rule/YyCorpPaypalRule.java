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
 *         一个gb 账号只能对应一个1个paypal账号
 */
public class gbCorpPaypalRule implements RuleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(gbCorpPaypalRule.class);
    private static int limit = 1;
    @Autowired
    private DomainResource domainResource;

    @Override
    public Map<String, String> validator(Map<String, String> params) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        String gbuid = params.get(TradeRuleConsts.gbUID);
        String account = params.get(TradeRuleConsts.PAYERID);
        if (StringUtils.isBlank(gbuid)) {
            return resultMap;
        }
        String chId = params.get(TradeRuleConsts.CHID);
        String startTime = TimeHelper.nDayDate(-30, TimeHelper.TIME_FORMATE);
        String endTime = TimeHelper.get(8, new Date());
        int result = domainResource.getgbCrospPaypal(gbuid, chId, startTime, endTime, account);
        if (result >= limit) {
            LOG.info("[gbCorpPaypalRule.validator]gbuid:{} ,account:{},corresponding mutip paypal account", gbuid,
                    account, TraceHelper.getTrace(params));
            resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_105);
            return resultMap;
        }
        return resultMap;
    }

}
