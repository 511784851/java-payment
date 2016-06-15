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
import com.guzhi.pay.domain.UserAccountLimit;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.traderule.TradeRuleConsts;

/**
 * @author Administrator
 *         黑名单验证抽象类
 */
public abstract class AbstractBlackRule implements RuleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractBlackRule.class);
    private static final int BLACK_NUMBER = 2;
    @Autowired
    private DomainResource domainResource;

    public Map<String, String> validator(Map<String, String> params) {
        Map<String, String> result = new HashMap<String, String>();
        result.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        String startTime = TimeHelper.nDayDate(-30, TimeHelper.TIME_FORMATE);
        String endTime = TimeHelper.get(8, new Date());
        String chId = params.get(TradeRuleConsts.CHID);
        if (StringUtils.isBlank(chId)) {
            chId = TradeRuleConsts.gb;
        }
        String account = params.get(getAccountParam());
        if (StringUtils.isBlank(account)) {
            return result;
        }
        // 最近一个月存在黑名单记录，则拒绝充值
        UserAccountLimit userAccount = domainResource.getUserAccount(account, chId, UserAccountLimit.TYPE_BLACK,
                startTime, endTime);
        if (userAccount != null) {
            LOG.info("[AbstractBlackRule.validator] account:{} ,chId:{} exist in the UserAccountLimit", account, chId,
                    TraceHelper.getTrace(params));
            result.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            result.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_000);
            return result;
        }
        // 在黑名单记录中存在两次记录，则永远拒绝充值
        int total = domainResource.getBlackAccountNumber(account, chId, UserAccountLimit.TYPE_BLACK);
        if (total >= BLACK_NUMBER) {
            LOG.info("[AbstractBlackRule.validator] account:{} ,chId:{} exist two times in  the UserAccountLimit",
                    account, chId, TraceHelper.getTrace(params));
            result.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            result.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_000);
            return result;
        }
        return result;
    }

    /**
     * 获取账户参数名字
     * 
     * @return
     */
    public abstract String getAccountParam();
}
