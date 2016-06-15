/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.domain;

/**
 * @author Administrator
 * 支付渠道银行对应表
 */
public class ChBank {
    //渠道
    private String chId;
    //银行代码
    private String bankId;
    //渠道银行代码
    private String code;
    //银行名字
    private String name;
    //状态
    private String status;
    //优先级
    private int priority;
    public String getChId() {
        return chId;
    }
    public void setChId(String chId) {
        this.chId = chId;
    }
    
    public String getBankId() {
        return bankId;
    }
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    
}
