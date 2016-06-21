/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guzhi.pay.business.AppInfoManageServier;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.util.PayOrderUtil;

/**
 * @author xiaoweiteng
 * 
 */
@Controller
@RequestMapping("/manager/app")
public class AppInfoManageController {
    private static final Logger LOG = LoggerFactory.getLogger(AppInfoManageController.class);

    @Autowired
    private AppInfoManageServier appInfoManageService;

    @RequestMapping("/getAppInfos")
    public void getAppInfos(HttpServletRequest req, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        List<AppInfo> appInfos = appInfoManageService.getAppInfos();
        String appInfosJsonString = "";
        try {
            appInfosJsonString = new ObjectMapper().writeValueAsString(appInfos);
        } catch (JsonProcessingException e) {
        }
        if (appInfosJsonString == null || appInfosJsonString.equals("") || appInfosJsonString.equals("null")) {
            PayOrderUtil.outPutStr(response, "empty");
        } else {
            PayOrderUtil.outPutStr(response, appInfosJsonString);
        }
    }

    @RequestMapping("/createAppInfo")
    public void createAppInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("[/manager/app/getAppInfos] parameters:{}", request.getParameterMap());
        response.setContentType("text/html;charset=UTF-8");
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(request.getParameter("appId"));
        appInfo.setAppName(request.getParameter("appName"));
        appInfo.setStatus(request.getParameter("status"));
        appInfo.setIpWhitelist(request.getParameter("ipWhitelist"));
        appInfo.setKey(request.getParameter("key"));
        appInfo.setPasswdKey(request.getParameter("passwdKey"));

        int result = appInfoManageService.createAppInfo(appInfo);
        if (result == 1) {
            PayOrderUtil.outPutStr(response, "create appInfo success");
        } else {
            PayOrderUtil.outPutStr(response, "create appInfo fail");
        }
    }

    @RequestMapping("/updateAppInfo")
    public void updateAppInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("[/manager/app/updateAppInfo] parameters:{}", request.getParameterMap());
        response.setContentType("text/html;charset=UTF-8");
        String appId = request.getParameter("appId");
        String appName = request.getParameter("appName");
        String status = request.getParameter("status");
        String ipWhitelist = request.getParameter("ipWhitelist");
        String key = request.getParameter("key");
        String passwdKey = request.getParameter("passwdKey");
        AppInfo appInfo = appInfoManageService.getAppInfo(appId);
        if (appId != null && !appId.isEmpty()) {
            appInfo.setAppId(appId);
        }
        appInfo.setAppName(appName);
        appInfo.setStatus(status);
        appInfo.setIpWhitelist(ipWhitelist);
        appInfo.setKey(key);
        appInfo.setPasswdKey(passwdKey);

        int result = appInfoManageService.updateAppInfo(appInfo);
        if (result == 1) {
            PayOrderUtil.outPutStr(response, "update appInfo success");
        } else {
            PayOrderUtil.outPutStr(response, "update appInfo fail");
        }
    }

    @RequestMapping("/deleteAppInfo")
    public void deleteAppInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("[/manager/app/deleteAppInfo] parameters:{}", request.getParameterMap());
        response.setContentType("text/html;charset=UTF-8");
        String appId = request.getParameter("appId");

        int result = appInfoManageService.deleteAppInfo(appId);
        if (result == 1) {
            PayOrderUtil.outPutStr(response, "delete appInfo success");
        } else {
            PayOrderUtil.outPutStr(response, "delete appInfo fail");
        }
    }

    @RequestMapping("/getAppInfo")
    public void getAppInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String appId = request.getParameter("appId");

        AppInfo appInfo = appInfoManageService.getAppInfo(appId);
        if (appInfo != null) {
            PayOrderUtil.outPutStr(response, new ObjectMapper().writeValueAsString(appInfo));
        } else {
            PayOrderUtil.outPutStr(response, "appInfo not exist");
        }
    }
}
