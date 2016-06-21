/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.domain;

import com.guzhi.pay.helper.TimeHelper;

/**
 * @author administrator
 * 
 */
public class SmsOrder {
    /** 手机号 */
    private String phone;

    /** 验证码 (商品号+验证码) */
    private String validCode;

    /** 关联order表中的chOrderId */
    private String chOrderId;

    private String creatTime = TimeHelper.getFormattedTime();;

    private String lastUpdateTime = TimeHelper.getFormattedTime();

    /** 同payOrder的状态一样 */
    private String statusCode;

    /** 本订单的说明情况 */
    private String statusMsg;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

    public String getChOrderId() {
        return chOrderId;
    }

    public void setChOrderId(String chOrderId) {
        this.chOrderId = chOrderId;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
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

    @Override
    public String toString() {
        return "SmsOrder [phone=" + phone + ", validCode=" + validCode + ", chOrderId=" + chOrderId + ", creatTime="
                + creatTime + ", lastUpdateTime=" + lastUpdateTime + ", statusCode=" + statusCode + ", statusMsg="
                + statusMsg + "]";
    }

}
