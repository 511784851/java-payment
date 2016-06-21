/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.scene;

import java.util.Map;

/**
 * @author Administrator
 * 
 */
public interface SceneValidator {
    /**
     * 遍历各个场景中的规则
     * 
     * @param params
     * @return
     */
    public Map<String, String> sceneValidator(Map<String, String> params);
}
