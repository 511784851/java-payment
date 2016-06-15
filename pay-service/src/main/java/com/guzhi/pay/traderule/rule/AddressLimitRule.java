/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.rule;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.paypal.PaypalConsts;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.traderule.TradeRuleConsts;

/**
 * 中国大陆不能充值
 * 
 * @author administrator
 * 
 */
public class AddressLimitRule implements RuleValidator {
    private static final Logger LOG = LoggerFactory.getLogger(IpCorpPaypalRule.class);

    @Override
    public Map<String, String> validator(Map<String, String> params) {
        Map<String, String> result = new HashMap<String, String>();
        result.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        String address = params.get(TradeRuleConsts.ADDRESS);
        String countryCode = JsonHelper.fromJson(address, PaypalConsts.ADDRESS_SHIPTOCOUNTRYCODE);
        LOG.info("[AddressLimitRule.validator] countrycode;{}", countryCode, TraceHelper.getTrace(params));
        if (PaypalConsts.COUNTRY_CODE_0.equalsIgnoreCase(countryCode)
                || PaypalConsts.COUNTRY_CODE_1.equalsIgnoreCase(countryCode)) {
            LOG.info("[AddressLimitRule.validator] exist risk countrycode;{}", countryCode,
                    TraceHelper.getTrace(params));
            result.put(TradeRuleConsts.FLAG, TradeRuleConsts.TRUE);
            result.put(TradeRuleConsts.TRADE_ERROR_CODE, TradeRuleConsts.ERROR_CODE_001);
            return result;
        }
        return result;
    }
}
