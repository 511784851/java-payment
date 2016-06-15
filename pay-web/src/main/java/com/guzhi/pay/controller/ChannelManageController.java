/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guzhi.pay.business.ChannelManageServier;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.util.PayOrderUtil;

/**
 * @author administrator
 * 
 */
@Controller
@RequestMapping("/manager/ch")
public class ChannelManageController {

    @Autowired
    private ChannelManageServier channelManageServier;

    @RequestMapping("/getAppChInfos")
    public void getAppChInfos(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        List<AppChInfo> appChInfos = channelManageServier.getAppChInfos();
        String appChInfosJsonString = "";
        try {
            appChInfosJsonString = new ObjectMapper().writeValueAsString(appChInfos);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (appChInfosJsonString == null || appChInfosJsonString.equals("") || appChInfosJsonString.equals("null")) {
            PayOrderUtil.outPutStr(response, "empty");
        } else {
            PayOrderUtil.outPutStr(response, appChInfosJsonString);
        }
    }

    @RequestMapping("/createAppChInfo")
    public void createAppChInfo(HttpServletResponse response) throws IOException {
        HttpServletRequest request = getRequest();
        response.setContentType("text/html;charset=UTF-8");
        AppChInfo appChInfo = new AppChInfo();
        appChInfo.setAppId(request.getParameter("appId"));
        appChInfo.setStatus(request.getParameter("status"));
        appChInfo.setChId(request.getParameter("chId"));
        appChInfo.setPayMethod(request.getParameter("payMethod"));
        appChInfo.setChName(request.getParameter("chName"));
        if (request.getParameter("chWeight") != null && !request.getParameter("chWeight").equals("")) {
            appChInfo.setChWeight(Integer.parseInt(request.getParameter("chWeight")));
        }
        appChInfo.setChAccountId(request.getParameter("chAccountId"));
        appChInfo.setChAccountName(request.getParameter("chAccountName"));
        appChInfo.setChPayKeyMd5(request.getParameter("chPayKeyMd5"));
        appChInfo.setChAccountsKeyMd5(request.getParameter("chAccountsKeyMd5"));
        appChInfo.setAdditionalInfo(request.getParameter("additionalInfo"));

        int result = channelManageServier.createAppChInfo(appChInfo);
        if (result == 1) {
            PayOrderUtil.outPutStr(response, "create appChInfo success");
        } else {
            PayOrderUtil.outPutStr(response, "create appChInfo fail");
        }
    }

    @RequestMapping("/updateAppChInfo")
    public void updateAppChInfo(HttpServletResponse response) throws IOException {
        HttpServletRequest request = getRequest();
        response.setContentType("text/html;charset=UTF-8");
        String appId = request.getParameter("appId");
        String status = request.getParameter("status");
        String chId = request.getParameter("chId");
        String payMethod = request.getParameter("payMethod");
        String chName = request.getParameter("chName");
        String chWeight = request.getParameter("chWeight");
        String chAccountId = request.getParameter("chAccountId");
        String chAccountName = request.getParameter("chAccountName");
        String chPayKeyMd5 = request.getParameter("chPayKeyMd5");
        String chAccountsKeyMd5 = request.getParameter("chAccountsKeyMd5");
        String additionalInfo = request.getParameter("additionalInfo");
        AppChInfo appChInfo = channelManageServier.getAppChInfo(appId, chId, payMethod);
        if (appChInfo != null) {
            appChInfo.setAppId(appId);
            appChInfo.setStatus(status);
            appChInfo.setChId(chId);
            appChInfo.setPayMethod(payMethod);
            appChInfo.setChName(chName);
            if (chWeight != null && !chWeight.isEmpty()) {
                appChInfo.setChWeight(Integer.parseInt(chWeight));
            }
            appChInfo.setChAccountId(chAccountId);
            appChInfo.setChAccountName(chAccountName);
            appChInfo.setChPayKeyMd5(chPayKeyMd5);
            appChInfo.setChAccountsKeyMd5(chAccountsKeyMd5);
            appChInfo.setAdditionalInfo(additionalInfo);
        }

        int result = channelManageServier.updateAppChInfo(appChInfo);
        if (result == 1) {
            PayOrderUtil.outPutStr(response, "update appChInfo success");
        } else {
            PayOrderUtil.outPutStr(response, "create appChInfo fail");
        }
    }

    @RequestMapping("/deleteAppChInfo")
    public void deleteAppChInfo(HttpServletResponse response) throws IOException {
        HttpServletRequest request = getRequest();
        response.setContentType("text/html;charset=UTF-8");
        String appId = request.getParameter("appId");
        String chId = request.getParameter("chId");
        String payMethod = request.getParameter("payMethod");

        int result = channelManageServier.deleteAppChInfo(appId, chId, payMethod);
        if (result == 1) {
            PayOrderUtil.outPutStr(response, "delete appChInfo success");
        } else {
            PayOrderUtil.outPutStr(response, "delete appChInfo fail");
        }
    }

    @RequestMapping("/getAppChInfo")
    public void getAppChInfo(HttpServletResponse response) throws IOException {
        HttpServletRequest request = getRequest();
        response.setContentType("text/html;charset=UTF-8");
        String appId = request.getParameter("appId");
        String chId = request.getParameter("chId");
        String payMethod = request.getParameter("payMethod");

        AppChInfo appChInfo = channelManageServier.getAppChInfo(appId, chId, payMethod);
        if (appChInfo != null) {
            PayOrderUtil.outPutStr(response, new ObjectMapper().writeValueAsString(appChInfo));
        } else {
            PayOrderUtil.outPutStr(response, "appChInfo not exist");
        }
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
