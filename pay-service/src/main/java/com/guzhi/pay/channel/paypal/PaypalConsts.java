/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.paypal;

/**
 * @author Administrator
 *         paypal常量定义
 */
public interface PaypalConsts {

    static final String ADDR_guzhiPay_PAYPALTOKEN = "/ch/notify/paypalToken.do";

    static final String ADDR_guzhiPay_PAYPALCANCEL = "/ch/notify/paypalCancel.do";

    static final String ADDR_guzhiPay_PAYPALNOTIFY = "/ch/notify/paypalBalance.do";

    static final String USER = "USER";

    static final String PWD = "PWD";

    static final String SIGNATURE = "SIGNATURE";

    static final String VERSION = "VERSION";

    static final String METHOD = "METHOD";

    static final String RETURNURL = "RETURNURL";

    static final String CANCELURL = "CANCELURL";

    static final String PAYMENTREQUEST_0_PAYMENTACTION = "PAYMENTREQUEST_0_PAYMENTACTION";

    static final String PAYMENTREQUEST_0_AMT = "PAYMENTREQUEST_0_AMT";

    static final String METHOD_SETEXPRESSCHECKOUT = "SetExpressCheckout";

    static final String AMP = "&";

    static final String EQ = "=";

    static final String ACK = "ACK";

    static final String TOKEN = "TOKEN";

    static final String METHOD_GETEXPRESSCHECKOUTDETAILS = "GetExpressCheckoutDetails";

    static final String METHOD_DOEXPRESSCHECKOUTPAYMENT = "DoExpressCheckoutPayment";

    static final String PAYERID = "PAYERID";

    static final String METHOD_GETTRANSACTIONDETAILS = "GetTransactionDetails";

    static final String TRANSACTIONID = "TRANSACTIONID";

    static final String PAYMENTSTATUS = "PAYMENTSTATUS";

    static final String COMPLETED = "Completed";

    static final String PENDING = "Pending";

    static final String PROCESSED = "Processed";

    static final String COMPLETED_FUNDS_HELD = "Completed-Funds-Held";

    static final String PAYMENT_STATUS = "payment_status";

    static final String PAYMENTACTION_SALE = "Sale";

    static final String PAYMENTREQUEST_0_TRANSACTIONID = "PAYMENTINFO_0_TRANSACTIONID";

    static final String PAYMENTINFO_0_PAYMENTSTATUS = "PAYMENTINFO_0_PAYMENTSTATUS";

    static final String PAYMENTREQUEST_0_NOTIFYURL = "PAYMENTREQUEST_0_NOTIFYURL";

    static final String TXN_ID = "txn_id";

    static final String PARENT_TXN_ID = "parent_txn_id";

    static final String PAYER_ID = "payer_id";

    static final String KEY_PENDING_REASON = "pending_reason";

    static final String PENDING_REASON = "paymentreview";

    static final String APPCHINFO_SIGNATURE = "signature";

    static final String EMAIL = "EMAIL";

    static final String SOLUTIONTYPE = "SOLUTIONTYPE";

    static final String SOLUTIONTYPEVALUE = "Mark";

    static final String LANDINGPAGE = "LANDINGPAGE";

    static final String LANDINGPAGEVALUE = "Login";

    // 地址的字段
    static final String ADDRESS_SHIPTOCOUNTRYCODE = "PAYMENTREQUEST_0_SHIPTOCOUNTRYCODE";
    static final String ADDRESS_SHIPTOSTATE = "PAYMENTREQUEST_0_SHIPTOSTATE";
    static final String ADDRESS_SHIPTOCITY = "PAYMENTREQUEST_0_SHIPTOCITY";
    static final String ADDRESS_SHIPTOSTREE = "PAYMENTREQUEST_0_SHIPTOSTREET";
    static final String ADDRESS_SHIPTOSTREE2 = "PAYMENTREQUEST_0_SHIPTOSTREET2";
    static final String ADDRESS_SHIPTOZIP = "PAYMENTREQUEST_0_SHIPTOZIP";
    static final String ADDRESS_SHIPTOPHONENUM = "PAYMENTREQUEST_0_SHIPTOPHONENUM";

    // 名字字段
    static final String SALUTATION = "SALUTATION";
    static final String FIRSTNAME = "FIRSTNAME";
    static final String MIDDLENAME = "MIDDLENAME";
    static final String LASTNAME = "LASTNAME";

    static final String REVERSED = "Reversed ";

    static final String COUNTRY_CODE_0 = "CN";

    static final String COUNTRY_CODE_1 = "C2";
}
