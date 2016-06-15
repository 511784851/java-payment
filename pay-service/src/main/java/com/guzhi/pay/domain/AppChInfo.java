/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.domain;

/**
 * 业务方的对应渠道的基本信息
 * 
 * @author administrator
 */
public class AppChInfo {
    private String appId;
    private String status;
    private String chId;
    private String payMethod;
    private String chName;
    private Integer chWeight; // 注意：权重的计算是在payMethod相同的情况下进行的
    private String chAccountId;
    private String chAccountName;
    private String chPayKeyMd5;
    private String chAccountsKeyMd5;
    private String additionalInfo;

    @Override
    public String toString() {
        String warnMsg = "(some sensitive info are not printeed!) ";
        return warnMsg + "AppChInfo [appId=" + appId + ", status=" + status + ", chId=" + chId + ", payMethod="
                + payMethod + ", chName=" + chName + ", chWeight=" + chWeight + "]";
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public Integer getChWeight() {
        return chWeight;
    }

    public void setChWeight(Integer chWeight) {
        this.chWeight = chWeight;
    }

    public String getChAccountId() {
        return chAccountId;
    }

    public void setChAccountId(String chAccountId) {
        this.chAccountId = chAccountId;
    }

    public String getChPayKeyMd5() {
        return chPayKeyMd5;
    }

    public void setChPayKeyMd5(String chPayKeyMd5) {
        this.chPayKeyMd5 = chPayKeyMd5;
    }

    public String getChAccountsKeyMd5() {
        return chAccountsKeyMd5;
    }

    public void setChAccountsKeyMd5(String chAccountsKeyMd5) {
        this.chAccountsKeyMd5 = chAccountsKeyMd5;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getChAccountName() {
        return chAccountName;
    }

    public void setChAccountName(String chAccountName) {
        this.chAccountName = chAccountName;
    }
}