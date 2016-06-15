/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.util;

import java.util.Map;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.SecureHelper;
import com.guzhi.pay.helper.StringHelper;

/**
 * @author 
 * 
 */
public class PayUtils {
    /**
     * 加密Map类型的集合.
     */
    public static String getResp(PayOrder payOrder, @SuppressWarnings("rawtypes") Map dataMap) {
        String data = JsonHelper.toJson(dataMap);
        String sign = SecureHelper.genMd5Sign(payOrder.getAppInfo().getKey(), data);
        String result = "appId=" + payOrder.getAppId() + "&sign=" + sign + "&data="
                + StringHelper.encode(data, Consts.CHARSET_UTF8);
        return result;
    }
}