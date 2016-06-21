/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.domain;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * 支付订单类，主要信息都在这里。<br>
 * 为避免文档重复，主要的说明请见接口文档（yypay/docs），这里仅作必要补充。 <br>
 * 返回和业务通知结构体不进行验证，只验证请求结构体
 * 
 * @author administrator
 * @author administrator
 */
@JsonInclude(Include.NON_NULL)
public class PayOrder {
    // appId is in TReq level, not in the data json string
    // @JsonView({ PayReqView.class, QueryReqView.class, RespView.class })
    @NotBlank(groups = { PayReqVal.class, QueryReqVal.class }, message = "appId must not be blank.")
    private String appId;

    @NotBlank(groups = { PayReqVal.class, QueryReqVal.class, RefundReqVal.class }, message = "appOrderId must not be blank.")
    @JsonView({ QueryReqView.class, PayReqView.class, RespView.class, RefundReqView.class })
    private String appOrderId;

    @NotBlank(groups = { RefundReqVal.class }, message = "chId must not be blank.")
    @JsonView({ PayReqView.class, RespView.class, RefundReqView.class })
    private String chId;

    @NotBlank(groups = { PayReqVal.class }, message = "appOrderTime must not be blank.")
    @JsonView({ PayReqView.class, RespView.class })
    private String appOrderTime;

    @NotBlank(groups = { PayReqVal.class }, message = "payMethod must not be blank.")
    @JsonView({ PayReqView.class, RespView.class })
    private String payMethod;

    // @NotBlank(groups = { PayReqView.class, RefundReqVal.class}, message =
    // "amount must not be blank.")
    @JsonView({ PayReqView.class, RespView.class, RefundReqView.class })
    private BigDecimal amount;

    @JsonView({ PayReqView.class, RespView.class })
    private String bankId;

    @NotBlank(groups = { RefundReqVal.class }, message = "chDealId must not be blank.")
    @JsonView({ RespView.class, RefundReqView.class })
    private String chDealId;

    @NotBlank(groups = { RefundReqVal.class }, message = "appRefundTime must not be blank.")
    @JsonView({ RefundReqView.class })
    private String appRefundTime;

    @NotBlank(groups = { RefundReqVal.class }, message = "refundAmount must not be blank.")
    @JsonView({ RefundReqView.class })
    private String refundAmount;

    @NotBlank(groups = { RefundReqVal.class }, message = "refundDesc must not be blank.")
    @JsonView({ RefundReqView.class })
    private String refundDesc;

    @JsonView({ RefundReqView.class })
    private String orphanRefund;

    // 返回结构体不做验证
    @JsonView({ RespView.class })
    private String statusCode;

    @JsonView({ RespView.class })
    private String statusMsg;

    @JsonView({ RespView.class })
    private String chDealTime;

    @JsonView({ RespView.class })
    private String chOrderId;

    @JsonView({ RespView.class })
    private String chAccountId; // 冗余性字段，协助记录信息

    @JsonView({ RespView.class })
    private BigDecimal chFee;

    @JsonView({ RespView.class })
    private String bankDealId;

    @JsonView({ RespView.class })
    private String bankDealTime;

    @JsonView({ RespView.class })
    private String submitTime = TimeHelper.getFormattedTime();

    /**
     * @see #setLastUpdateTime(String)
     */
    @JsonView({ RespView.class })
    private String lastUpdateTime = TimeHelper.getFormattedTime();

    @JsonView({ RespView.class })
    private String payUrl;

    @JsonView({ PayReqView.class })
    private String returnUrl;

    @JsonView({ PayReqView.class })
    private String notifyUrl;

    @JsonView({ PayReqView.class, RefundReqView.class })
    private String userIp;

    @JsonView({ PayReqView.class, RespView.class })
    private String userId;

    @JsonView({ PayReqView.class })
    private String userName;

    @JsonView({ PayReqView.class })
    private String userContact;

    @JsonView({ PayReqView.class })
    private String userAddiInfo;

    @JsonView({ PayReqView.class })
    private String prodId;

    @JsonView({ PayReqView.class })
    private String prodName;

    @JsonView({ PayReqView.class })
    private String prodDesc;

    @JsonView({ PayReqView.class })
    private String prodAddiInfo;

    @JsonView({ PayReqView.class })
    private String cardNum;

    @JsonView({ PayReqView.class })
    private String cardPass;

    @JsonView({ PayReqView.class })
    private String cardTotalAmount;

    @JsonView({ PayReqView.class })
    private String autoRedirect;

    @JsonView({ PayReqView.class })
    private String yyOper;

    @JsonView({ PayReqView.class })
    private BigDecimal yyAmount;

    @JsonView({ PayReqView.class })
    private String unit;

    @JsonView({ RespView.class })
    private String smsCode;

    @JsonIgnore
    private String chIp;

    @JsonIgnore
    private String appIp;

    @JsonIgnore
    private AppInfo appInfo;
    @JsonIgnore
    private AppChInfo appChInfo;

    @JsonView({ PayReqView.class, RespView.class, RefundReqView.class })
    private String ext;

    @JsonView({ PayReqView.class })
    private String category;
    /**
     * 异步充值Task类型
     */
    @JsonIgnore
    private String asyncPayTaskType;

    public String getAsyncPayTaskType() {
        return asyncPayTaskType;
    }

    public void setAsyncPayTaskType(String asyncPayTaskType) {
        this.asyncPayTaskType = asyncPayTaskType;
    }

    /**
     * 用于Jackson定制转换（Jackson的@JsonView需要通过任意的类对象指定View）<br>
     * 此类用于标识在将Pay(支付)请求中的Json串转换为PayOrder时，需要进行映射的字段
     */
    public static class PayReqView {
    }

    /**
     * 用于Jackson定制转换（Jackson的@JsonView需要通过任意的类对象指定View）<br>
     * 此类用于标识在将Query(查询)请求中的Json串转换为PayOrder时，需要进行映射的字段
     */
    public static class QueryReqView {
    }

    /**
     * 用于Jackson定制转换（Jackson的@JsonView需要通过任意的类对象指定View）<br>
     * 此类用于标识在将Query(查询)请求中的Json串转换为PayOrder时，需要进行映射的字段
     */
    public static class RefundReqView {
    }

    /**
     * 用于Jackson定制转换（Jackson的@JsonView需要通过任意的类对象指定View）<br>
     * 此类用于标识在将PayOrder转换为响应中的Json串时，需要进行映射的字段 <br>
     * 如支付返回、查询返回、业务通知
     */
    public static class RespView {
    }

    /**
     * 用于数据校验（JSP303），此类用于支付请求的校验
     */
    public static interface PayReqVal {
    }

    /**
     * 用于数据校验（JSP303），此类用于查询请求的校验
     */
    public static interface QueryReqVal {
    }

    /**
     * 用于数据校验（JSP303），此类用于退款请求的校验
     */
    public static interface RefundReqVal {
    }

    /**
     * 用于数据校验（JSP303），此类用于对账请求的校验<br>
     */
    public static interface AccountsReqVal {
    }

    /**
     * 方法通过Eclipse的自动生成功能生成，建议勿手工修改。<br>
     * 其中不包含（敏感信息）：cardPass
     */
    @Override
    public String toString() {
        return "PayOrder [appId=" + appId + ", appOrderId=" + appOrderId + ", chId=" + chId + ", appOrderTime="
                + appOrderTime + ", payMethod=" + payMethod + ", amount=" + amount + ", bankId=" + bankId
                + ", chDealTime=" + chDealTime + ", appRefundTime=" + appRefundTime + ", refundAmount=" + refundAmount
                + ", refundDesc=" + refundDesc + ", orphanRefund=" + orphanRefund + ", statusCode=" + statusCode
                + ", statusMsg=" + statusMsg + ", chDealId=" + chDealId + ", chOrderId=" + chOrderId + ", chAccountId="
                + chAccountId + ", chFee=" + chFee + ", bankDealId=" + bankDealId + ", bankDealTime=" + bankDealTime
                + ", submitTime=" + submitTime + ", lastUpdateTime=" + lastUpdateTime + ", payUrl=" + payUrl
                + ", returnUrl=" + returnUrl + ", notifyUrl=" + notifyUrl + ", userIp=" + userIp + ", userId=" + userId
                + ", userName=" + userName + ", userContact=" + userContact + ", userAddiInfo=" + userAddiInfo
                + ",yyAmount=" + yyAmount + ",ext=" + ext + ", prodId=" + prodId + ", prodName=" + prodName
                + ", prodDesc=" + prodDesc + ", prodAddiInfo=" + prodAddiInfo + ", cardNum=" + cardNum + ", chIp="
                + chIp + ", cardTotalAmount=" + cardTotalAmount + ", appIp=" + appIp + ", appInfo=" + appInfo
                + ", appChInfo=" + appChInfo + ", category=" + category + "]";
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

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
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

    public String getAppOrderTime() {
        return appOrderTime;
    }

    public void setAppOrderTime(String appOrderTime) {
        this.appOrderTime = appOrderTime;
    }

    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public String getChOrderId() {
        return chOrderId;
    }

    public void setChOrderId(String chOrderId) {
        this.chOrderId = chOrderId;
    }

    public String getChDealTime() {
        return chDealTime;
    }

    public void setChDealTime(String chDealTime) {
        this.chDealTime = chDealTime;
    }

    public String getChDealId() {
        return chDealId;
    }

    public void setChDealId(String chDealId) {
        this.chDealId = chDealId;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankDealId() {
        return bankDealId;
    }

    public void setBankDealId(String bankDealId) {
        this.bankDealId = bankDealId;
    }

    public String getBankDealTime() {
        return bankDealTime;
    }

    public void setBankDealTime(String bankDealTime) {
        this.bankDealTime = bankDealTime;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
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

    // 后续需要重新修改
    public void setStatusMsg(String statusMsg) {
        if (StringUtils.isNotBlank(statusMsg) && (statusMsg.length() > 990)) {
            statusMsg = statusMsg.substring(0, 990);
        }
        this.statusMsg = statusMsg;
    }

    public void appendMsg(String appendStatusMsg) {
        this.statusMsg = (statusMsg == null ? "" : statusMsg) + appendStatusMsg;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdAddiInfo() {
        return prodAddiInfo;
    }

    public void setProdAddiInfo(String prodAddiInfo) {
        this.prodAddiInfo = prodAddiInfo;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getChAccountId() {
        return chAccountId;
    }

    public void setChAccountId(String chAccountId) {
        this.chAccountId = chAccountId;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getCardPass() {
        return cardPass;
    }

    public void setCardPass(String cardPass) {
        this.cardPass = cardPass;
    }

    public String getCardTotalAmount() {
        return cardTotalAmount;
    }

    public void setCardTotalAmount(String cardTotalAmount) {
        this.cardTotalAmount = cardTotalAmount;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * 这个字段会被用来判断是否要更新DB中的数据（保证并发下的数据安全），请尽量使用渠道方提供的信息更新的时间。
     * 
     * @param lastUpdateTime
     */
    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getUserAddiInfo() {
        return userAddiInfo;
    }

    public void setUserAddiInfo(String userAddiInfo) {
        this.userAddiInfo = userAddiInfo;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public AppChInfo getAppChInfo() {
        return appChInfo;
    }

    public void setAppChInfo(AppChInfo appChInfo) {
        this.appChInfo = appChInfo;
    }

    public String getAppIp() {
        return appIp;
    }

    public void setAppIp(String appIp) {
        this.appIp = appIp;
    }

    public String getChIp() {
        return chIp;
    }

    public void setChIp(String chIp) {
        this.chIp = chIp;
    }

    public String getAppRefundTime() {
        return appRefundTime;
    }

    public void setAppRefundTime(String appRefundTime) {
        this.appRefundTime = appRefundTime;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundDesc() {
        return refundDesc;
    }

    public void setRefundDesc(String refundDesc) {
        this.refundDesc = refundDesc;
    }

    public String getOrphanRefund() {
        return orphanRefund;
    }

    public void setOrphanRefund(String orphanRefund) {
        this.orphanRefund = orphanRefund;
    }

    public String getAutoRedirect() {
        return autoRedirect;
    }

    public void setAutoRedirect(String autoRedirect) {
        this.autoRedirect = autoRedirect;
    }

    public String getYyOper() {
        return yyOper;
    }

    public void setYyOper(String yyOper) {
        this.yyOper = yyOper;
    }

    public BigDecimal getYyAmount() {
        return yyAmount;
    }

    public void setYyAmount(BigDecimal yyAmount) {
        this.yyAmount = yyAmount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
        this.smsCode = JsonHelper.fromJson(ext, "smsCode");
    }

    // 暂时把smsCode存在ext
    public String getSmsCode() {
        return JsonHelper.fromJson(ext, "smsCode");
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
        this.ext = JsonHelper.putJson(ext, "smsCode", smsCode);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}