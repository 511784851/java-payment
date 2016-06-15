/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.DomainResource;

/**
 * 特殊密文获取工具。
 * 
 * @author 
 * 
 */

public class PasswordUtils {
    /**
     * 在不指定应用ID和渠道ID的情况下，查询指定账户的私密信息。
     * 
     * @param resource
     * @param chAccountId
     * @return
     */
    public static String getChPayKegbyChAccountId(DomainResource resource, String chAccountId) {
        if (StringUtils.isBlank(chAccountId)) {
            return "";
        }
        List<AppChInfo> appChInfos = resource.getAppChInfos();
        for (AppChInfo appChInfo : appChInfos) {
            if (chAccountId.equalsIgnoreCase(appChInfo.getChAccountId())) {
                return appChInfo.getChPayKeyMd5();
            }
        }
        return "";
    }
}