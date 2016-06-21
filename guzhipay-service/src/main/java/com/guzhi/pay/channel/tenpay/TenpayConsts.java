/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.tenpay;

/**
 * 财付通常量定义.
 * 
 * @author administrator
 * 
 */
public interface TenpayConsts {
    String ADDR_TENPAYGATE_PAY = "https://gw.tenpay.com/gateway/pay.htm";
    String ADDR_TENPAYGATE_QUERY = "https://gw.tenpay.com/gateway/normalorderquery.xml";
    String ADDR_TENPAYGATE_VERIFYNOTIFY = "https://gw.tenpay.com/gateway/verifynotifyid.xml";
    String ADDR_YYPAY_NOTIFY = "/ch/notify/tenpay.do";
    String ADDR_YYPAY_RETURN = "/ch/return/tenpay.do";
    String KEY_ATTACH = "attach";
    String KEY_BANK_BILLNO = "bank_billno";
    String KEY_BANK_TYPE = "bank_type";
    String KEY_BODY = "body";
    String KEY_BUYER_ALIAS = "buyer_alias";
    String KEY_BUYER_ID = "buyer_id";
    String KEY_DISCOUNT = "discount";
    String KEY_FEE_TYPE = "fee_type";
    String KEY_GOODS_TAG = "goods_tag";
    String KEY_INPUT_CHARSET = "input_charset";
    String KEY_NOTIFY_ID = "notify_id";
    String KEY_NOTIFY_URL = "notify_url";
    String KEY_OUT_TRADE_NO = "out_trade_no";
    String KEY_PARTNER = "partner";
    String KEY_PAY_INFO = "pay_info";
    String KEY_PRODUCT_FEE = "product_fee";
    String KEY_RETCODE = "retcode";
    String KEY_RETMSG = "retmsg";
    String KEY_RETURN_URL = "return_url";
    String KEY_SERVICE_VERSION = "service_version";
    String KEY_SIGN = "sign";
    String KEY_SIGN_KEY_INDEX = "sign_key_index";
    String KEY_SIGN_TYPE = "sign_type";
    String KEY_SPBILL_CREATE_IP = "spbill_create_ip";
    String KEY_TIME_END = "time_end";
    String KEY_TIME_EXPIRE = "time_expire";
    String KEY_TIME_START = "time_start";
    String KEY_TOTAL_FEE = "total_fee";
    String KEY_TRADE_MODE = "trade_mode";
    String KEY_TRADE_STATE = "trade_state";
    String KEY_TRANSACTION_ID = "transaction_id";
    String KEY_TRANSPORT_FEE = "transport_fee";
    String KEY_USE_SPBILL_NO_FLAG = "use_spbill_no_flag";
    String NOTIFYSUCCESS = "success";
}