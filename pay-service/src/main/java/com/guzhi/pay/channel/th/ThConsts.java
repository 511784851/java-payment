/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.th;

/**
 * 天宏常量
 * 
 * @author administrator
 * 
 */
public interface ThConsts {

    String ADD_PAY = "http://port.27399.com/gateway/ECard_PayGate.do";
    String ADD_QUERY = "http://port.27399.com/gateway/ECard_QueryGate_Single.do";

    static final String SPLIT_SYMBOL = "~";

    static final String USERNAME = "username";

    static final String PRODUCTID = "productid";

    static final String CKNUM = "cknum";

    static final String KAOHAO = "kahao";

    static final String MIMA = "mima";

    static final String BUYNUM = "buynum";

    static final String ORDERNUM = "orderNum";

    static final String RETURNURL = "returnUrl";

    static final String MD5TOSELF = "md5toself";

    static final String ADDR_guzhiPay_NOTIFY = "/ch/notify/thYkt.do";

    static final String STATE = "state";

    static final String CODE = "code";

    static final String SUCCESS = "1";

    static final String CARD_ERROR = "-6";

    static final String ITEMS = "items";

    static final String ITEM = "item";

    static final String PAYDATE_Q = "payDate";

    static final String MD5KEY = "md5Key";

    static final String MSG = "msg";

    static final String MONEY = "money";

    static final String USERNAME_Q = "userName";

    static final String ORDERNUM_Q = "orderNum";

    static final String BUYNUM_Q = "buyNum";

    String MD5ERRORMSG = "MD5验证错误";

    String PAYERRORMSG = "卡密错误或卡密失效";

}
