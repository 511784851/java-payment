/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.rule;

import java.util.Map;

/**
 * @author Administrator
 *         规则验证接口
 */
public interface RuleValidator {
    /**
     * 规则验证接口
     * 
     * @param params
     * @return
     */
    public Map<String, String> validator(Map<String, String> params);
}
