/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.domain;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author administrator
 */
public class Task {
    private String appId;
    private String appOrderId;
    private String type;
    private String flag;
    private Date nextTime;
    private String chId;// TaskDispatcher需要它来选择线程池
    private String payMethod;// TaskDispatcher需要它来选择线程池
    private int retryTimes;// 任务重试的次数
    private CountDownLatch cdl;// 用于获知线程执行信息

    public CountDownLatch getCdl() {
        return cdl;
    }

    public void setCdl(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    /**
     * 查询任务
     */
    public static final String TYPE_QUERY = "q";
    /**
     * 通知任务
     */
    public static final String TYPE_NOTIFY = "n";
    /**
     * 空闲的任务
     */
    public static final String FLAG_IDLE = "i";
    /**
     * 被占用的任务
     */
    public static final String FLAG_OCCUPIED = "o";
    /**
     * 退款
     */
    public static final String TYPE_REFUND = "refund";
    /**
     * yy币充值
     */
    public static final String TYPE_ADD_YY = "ayy";
    /**
     * appl充值
     */
    public static final String TYPE_PAY_APPLE = "verApp";

    /**
     * 视频保证金充值
     */
    public static final String TYPE_ADD_DEPOSIT = "adeposit";

    /**
     * 渠道保证金充值
     */
    public static final String TYPE_ADD_CHANNEL_DEPOSIT = "achdeposit";

    /**
     * 天宏一卡通充值
     */
    public static final String TYPE_PAY_THYKT = "payThykt";

    /**
     * 骏网骏卡
     */
    public static final String TYPE_PAY_JWJK = "jwJk";

    /**
     * 易宝卡类充值
     */
    public static final String TYPE_PAY_YEEPAYCARD = "payYbCard";

    /**
     * 快钱神州行充值
     */
    public static final String TYPE_PAY_KQSZX = "payKqSzx";

    /**
     * 神州付卡类充值
     */
    public static final String TYPE_PAY_SZFCARD = "paySzfCard";
    /**
     * 异步充值
     */
    public static final String TYPE_PAY_ASYNC = "payAsync";

    public Task() {

    }

    public Task(String appId, String appOrderId, String type, String chId, String payMethod) {
        super();
        this.appId = appId;
        this.appOrderId = appOrderId;
        this.type = type;
        this.flag = FLAG_IDLE;
        this.chId = chId;
        this.payMethod = payMethod;
    }

    @Override
    public String toString() {
        return "Task [appId=" + appId + ", appOrderId=" + appOrderId + ", type=" + type + ", flag=" + flag
                + ", nextTime=" + nextTime + ", chId=" + chId + ", payMethod=" + payMethod + ", retryTimes="
                + retryTimes + "]";
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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

}
