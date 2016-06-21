/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
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
 * @author administrator
 *         一个ip 一个月内只能对应2个以下的payapl账号
 */
public class IpCorpPaypalRule implements RuleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(IpCorpPaypalRule.class);
    private static int limit = 2;
    @Autowired
    private DomainResource domainResource;

    @Override
    public Map<String, String> validator(Map<String, String> params) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        String ip = params.get(TradeRuleConsts.IP);
        String yyuid = params.get(TradeRuleConsts.YYUID);
        String account = params.get(TradeRuleConsts.PAYERID);
        if (StringUtils.isBlank(ip)) {
            return resultMap;
        }
        String chId = params.get(TradeRuleConsts.CHID);
        String startTime = TimeHelper.nDayDate(-30, TimeHelper.TIME_FORMATE);
        String endTime = TimeHelper.get(8, new Date());
        int result = domainResource.getIpCrospPaypal(ip, chId, startTime, endTime, account);
        if (result >= limit) {
            LOG.info("[IpCorpPaypalRule.validator]ip:{} ,yyuid:{} corresponding mutip paypal account:{}", ip, yyuid,
                    account, TraceHelper.getTrace(params));
            resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_104);
            return resultMap;
        }
        return resultMap;
    }
}
