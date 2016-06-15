/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 成功支付结果的记录表
 * 
 * @author administrator
 * 
 */
public class PayResult {
    private String appId;
    private String appOrderId;
    private BigDecimal amount;
    private BigDecimal chFee;
    private String bankId;
    private String bankDealTime;
    private String bankDealId;
    private String statusCode;
    private String statusMsg;
    private Date updateTime;

    @Override
    public String toString() {
        return "PayResult [appId=" + appId + ", appOrderId=" + appOrderId + ", amount=" + amount + ", chFee=" + chFee
                + ", bankId=" + bankId + ", bankDealTime=" + bankDealTime + ", bankDealId=" + bankDealId
                + ", statusCode=" + statusCode + ", statusMsg=" + statusMsg + ", updateTime=" + updateTime + "]";
    }

    public String getBankDealId() {
        return bankDealId;
    }

    public void setBankDealId(String bankDealId) {
        this.bankDealId = bankDealId;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getChFee() {
        return chFee;
    }

    public void setChFee(BigDecimal chFee) {
        this.chFee = chFee;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankDealTime() {
        return bankDealTime;
    }

    public void setBankDealTime(String bankDealTime) {
        this.bankDealTime = bankDealTime;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
