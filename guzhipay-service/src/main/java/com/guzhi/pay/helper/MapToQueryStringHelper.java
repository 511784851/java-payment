/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.guzhi.pay.common.Consts;

/**
 * Map数据转为query string，value默认使用utf-8进行编码
 * 
 * @author administrator
 * 
 */
public class MapToQueryStringHelper {
    public static String convert(Map<String, String> params) {
        return convert(params, Consts.CHARSET_UTF8);
    }

    public static String convert(Map<String, String> params, String encoding) {
        Entry<String, String> entry;
        StringBuffer queryString = new StringBuffer();
        try {
            for (Iterator<Entry<String, String>> iter = params.entrySet().iterator(); iter.hasNext();) {
                entry = iter.next();
                queryString.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encoding));
                if (iter.hasNext()) {
                    queryString.append("&");
                }
            }
        } catch (UnsupportedEncodingException e) {
            // don't care
        }
        return queryString.toString();
    }
}
