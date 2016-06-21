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

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.UserAccountLimit;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.traderule.TradeRuleConsts;

/**
 * @author administrator
 *         一个payapl账号在一天内对应的ip如果在3个或者3个以上，则不允许充值（不考虑订单的状态）
 *         一个payapl账号在一天内对应的ip如果在5个或者5个以上，则进入黑名单（不考虑订单的状态）
 */
public class PaypalCorpIpRule implements RuleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PaypalCorpIpRule.class);
    private static int prohibitLimit = 2;
    private static int blackLimit = 4;
    @Autowired
    private DomainResource domainResource;

    @Override
    public Map<String, String> validator(Map<String, String> params) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        boolean returnFlag = false;
        String ip = params.get(TradeRuleConsts.IP);
        String yyuid = params.get(TradeRuleConsts.YYUID);
        String account = params.get(TradeRuleConsts.PAYERID);
        String startTime = TimeHelper.get(8, TimeHelper.alterHour(new Date(), -24));
        String endTime = TimeHelper.get(8, new Date());
        if (StringUtils.isBlank(ip)) {
            return resultMap;
        }
        String chId = params.get(TradeRuleConsts.CHID);
        int result = domainResource.getPaypalCrospIp(ip, chId, startTime, endTime, account);
        if (result >= prohibitLimit) {
            LOG.info("[PaypalCorpIpRule.validator] Paypal:{},yyuid:{} corresponding {} ip, ip:{},refuse request",
                    account, yyuid, result, ip, TraceHelper.getTrace(params));
            returnFlag = true;
        }

        if (result >= blackLimit) {
            LOG.info("[PaypalCorpIpRule.validator] Paypal:{},yyuid:{} corresponding {} ip, ip:{},add to black",
                    account, yyuid, ip, TraceHelper.getTrace(params));
            returnFlag = true;
            // add this paypal to black
            UserAccountLimit userAccountLimit = assemblerUserAccountLimit(account, endTime);
            LOG.info("the black add payerId:{}", account);
            domainResource.createUserAccountLimit(userAccountLimit);
        }

        // TODO 为了传递订单的状态以及statusMsg
        if (returnFlag) {
            params.put(TradeRuleConsts.STATUS, Consts.FAIL);
            params.put(TradeRuleConsts.STATUS_MSG, "[PaypalCorpIpRule] corresponding ips:" + result);
            resultMap.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            resultMap.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_106);
        }
        return resultMap;
    }

    /**
     * 组装userAccountLimit对象
     * 
     * @param account
     * @param endTime
     * @return
     */
    private UserAccountLimit assemblerUserAccountLimit(String account, String endTime) {
        UserAccountLimit userAccountLimit = new UserAccountLimit();
        userAccountLimit.setAccount(account);
        userAccountLimit.setChId(Consts.Channel.PAYPAL);
        userAccountLimit.setLastUpdateTime(endTime);
        userAccountLimit.setStatus(TradeRuleConsts.STATUS_VAILID);
        userAccountLimit.setType(TradeRuleConsts.BLACK);
        userAccountLimit.setCause("one paypal corresponding too much in one day " + endTime);
        return userAccountLimit;
    }
}
