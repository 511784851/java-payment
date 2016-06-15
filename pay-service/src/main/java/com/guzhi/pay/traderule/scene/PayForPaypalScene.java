/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.scene;

import java.util.List;
import java.util.Map;

import com.guzhi.pay.traderule.rule.RuleValidator;

/**
 * @author Administrator
 * paypal 交易入口的控制
 */
public class PayForPaypalScene extends AbstractSceneValidator{
    private List<RuleValidator> ruleValidatorList ;

    @Override
    public List<RuleValidator> getRuleList() {
        return ruleValidatorList;
    }

    @Override
    public void beforeSceneValidator(Map<String, String> params) {
      
    }

    @Override
    public void afterSceneValidator(Map<String, String> params) {
        
    }

    public List<RuleValidator> getRuleValidatorList() {
        return ruleValidatorList;
    }

    public void setRuleValidatorList(List<RuleValidator> ruleValidatorList) {
        this.ruleValidatorList = ruleValidatorList;
    }

    

}
