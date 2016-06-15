/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.business;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.DomainResource;

/**
 * @author administrator
 * 
 */
@Service
public class ChannelManageServier {
    @Autowired
    private DomainResource resource;

    public List<AppChInfo> getAppChInfos() {
        return resource.getAppChInfos();
    }

    public int createAppChInfo(AppChInfo appChInfo) {
        return resource.createAppChInfo(appChInfo);
    }

    public int updateAppChInfo(AppChInfo appChInfo) {
        return resource.updateAppChInfo(appChInfo);
    }

    public int deleteAppChInfo(String appId, String chId, String payMethod) {
        return resource.deleteAppChInfo(appId, chId, payMethod);
    }

    public AppChInfo getAppChInfo(String appId, String chId, String payMethod) {
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, chId, payMethod);
        if (CollectionUtils.size(appChInfos) != 1) {
            return null;
        }
        return appChInfos.get(0);
    }

}
