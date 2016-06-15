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
 * 业务方的基本信息
 * 
 * @author administrator
 */
public class AppInfo {
    public static final String COMMON_APPID = "000"; // 通用渠道通过使用这个AppId来存储渠道信息

    private String appId;
    private String appName;
    private String status;
    private String ipWhitelist;
    private String key;
    private String passwdKey;
    // 0 代表非自营业务，1代表自营业务
    private String type;

    @Override
    public String toString() {
        String warnMsg = "(some sensitive info are not printeed!) ";
        return warnMsg + "AppInfo [appId=" + appId + ", appName=" + appName + ", status=" + status + "]";
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpWhitelist() {
        return ipWhitelist;
    }

    public void setIpWhitelist(String ipWhitelist) {
        this.ipWhitelist = ipWhitelist;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPasswdKey() {
        return passwdKey;
    }

    public void setPasswdKey(String passwdKey) {
        this.passwdKey = passwdKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
