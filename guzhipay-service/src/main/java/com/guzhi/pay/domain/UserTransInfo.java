/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.domain;

import java.math.BigDecimal;

import com.guzhi.pay.helper.TimeHelper;

/**
 * @author Administrator
 *         用户信息记录表
 */
public class UserTransInfo {
    private String appId;
    private String appOrderId;
    // YY账号
    private String yyuid;
    // 第三方账号，如果有多，可以用json格式存储
    private String account;
    // 支付时间
    private String payTime;
    // 请求IP
    private String ip;
    // 地址
    private String address;
    // 扩展字段
    private String ext;
    // 金额
    private BigDecimal amount;
    // 渠道
    private String chId;

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    // 订单状态(失败,成功)
    private String status;
    // 订单说明
    private String statusMsg;
    // 上次更新时间
    private String lastUpdateTime = TimeHelper.getFormattedTime();

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppOrderId() {
        return appOrderId;
    }

    public void setAppOrderId(String appOrderId) {
        this.appOrderId = appOrderId;
    }

    public String getYyuid() {
        return yyuid;
    }

    public void setYyuid(String yyuid) {
        this.yyuid = yyuid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public void appendMsg(String appendStatusMsg) {
        this.statusMsg = (statusMsg == null ? "" : statusMsg) + appendStatusMsg;
    }

}
