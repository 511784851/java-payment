/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.DomainResource;

/**
 * @author xiaoweiteng
 * 
 */
@Service
public class AppInfoManageServier {
    @Autowired
    private DomainResource resource;

    public List<AppInfo> getAppInfos() {
        return resource.getAppInfos();
    }

    public int createAppInfo(AppInfo appInfo) {
        return resource.createAppInfo(appInfo);
    }

    public int updateAppInfo(AppInfo appInfo) {
        return resource.updateAppInfo(appInfo);
    }

    public int deleteAppInfo(String appId) {
        return resource.deleteAppInfo(appId);
    }

    public AppInfo getAppInfo(String appId) {
        return resource.getAppInfo(appId);
    }

}
