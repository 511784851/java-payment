/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guzhi.pay.traderule.TradeRuleConsts;
import com.guzhi.pay.traderule.rule.RuleValidator;

/**
 * @author Administrator
 * 
 */
public abstract class AbstractSceneValidator implements SceneValidator {
    /**
     * 遍历各个场景的使用规则
     */
    @Override
    public Map<String, String> sceneValidator(Map<String, String> params) {
        Map<String, String> result = new HashMap<String, String>();
        result.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
        beforeSceneValidator(params);
        List<RuleValidator> ruleValidatorList = getRuleList();
        for (RuleValidator ruleValidator : ruleValidatorList) {
            Map<String, String> validatorMap = ruleValidator.validator(params);
            String flag = validatorMap.get(TradeRuleConsts.FLAG);
            if (TradeRuleConsts.TRUE.equalsIgnoreCase(flag)) {
                return validatorMap;
            }
        }
        // 风控的规则都过了,可update交易记录.
        afterSceneValidator(params);
        return result;
    }

    /**
     * 获取场景中的规则列表
     * 
     * @return
     */
    public abstract List<RuleValidator> getRuleList();

    /**
     * 遍历规则前的动作
     * 
     * @param params
     */
    public abstract void beforeSceneValidator(Map<String, String> params);

    /**
     * 遍历规则后的动作
     * 
     * @param params
     */
    public abstract void afterSceneValidator(Map<String, String> params);
}
