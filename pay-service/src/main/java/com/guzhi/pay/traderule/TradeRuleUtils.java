/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.traderule.scene.SceneValidator;

/**
 * @author Administrator
 *         风控入口类
 */
public class TradeRuleUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TradeRuleUtils.class);
    private static Map<String, SceneValidator> sceneValidatorMap = new HashMap<String, SceneValidator>();

    /**
     * 风控入口
     * 
     * @param scene
     * @param params
     * @return
     */
    public static Map<String, String> dispatch(String scene, Map<String, String> params) {
        LOG.info("TradeRuleUtils scene:{}, params:{}", scene, params);
        SceneValidator sceneValidator = sceneValidatorMap.get(scene);
        if (sceneValidator == null) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(TradeRuleConsts.FLAG, TradeRuleConsts.FALSE);
            return result;
        }
        return sceneValidator.sceneValidator(params);
    }

    public static Map<String, SceneValidator> getSceneValidatorMap() {
        return sceneValidatorMap;
    }

    public static void setSceneValidatorMap(Map<String, SceneValidator> sceneValidatorMap) {
        TradeRuleUtils.sceneValidatorMap = sceneValidatorMap;
    }

}
