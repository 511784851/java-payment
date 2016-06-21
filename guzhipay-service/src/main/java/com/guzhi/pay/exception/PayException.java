/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.exception;

import com.guzhi.pay.domain.PayOrder;

/**
 * 整体系统尽量使用运行时异常，简化异常的继承结构，尽量通过Code、Msg来携带信息
 * 
 * @author administrator
 */
public class PayException extends RuntimeException {
    private static final long serialVersionUID = -2742295823094596726L;

    private String statusCode;
    private String statusMsg;
    private PayOrder payOrder;

    public PayException(String statusCode, String statusMsg) {
        super(statusMsg, null);
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }
    
    public PayException(String statusCode, String statusMsg, PayOrder payOrder) {
        super(statusMsg, null);
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
        this.payOrder = payOrder;
    }
    
    public PayException(String statusCode, String statusMsg, PayOrder payOrder, Throwable cause) {
        super(statusMsg, cause);
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
        this.payOrder = payOrder;
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

    public PayOrder getPayOrder() {
        return payOrder;
    }

    public void setPayOrder(PayOrder payOrder) {
        this.payOrder = payOrder;
    }
}
