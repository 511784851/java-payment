/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.gb;

/**
 * gbpay的一些接口常量
 * 
 * @author administrator
 * 
 */
public interface GbBalanceConsts {

    static final String ADDR_guzhiPay_gbNOTIFY = "/ch/notify/gb.do";
    static final String ADDR_guzhiPay_QUERY_ORDERURL = "/ch/gb/queryOrder.do";

    // G币的类型
    int MONEY_TYPE_GB = 10;

    // 保证金
    int MONEY_TYPE_DEPOSIT = 12;

    // 频道保证金
    int MONEY_TYPE_CHANNEL_DEPOSIT = 16;

    // 存入到appChInfo中的addtionalInfo字段为
    final String PRODUCT = "product";
    final String ENCRYPT_ADD_gb_KEY = "encryptAddgbKey";
    // 存入userId的字段
    final String gbUID = "gbuid";

    // 存入订单表的prodAddiInfo字段
    final String gbCHANNELID = "gbchannelId";

    final String PASSWORD = "username";
    final String ORDER_ID = "orderId";
    final String TIME = "time";
    final String SIGN = "sign";
    final String TIMESTAMP = "timestamp";
    String CONFIRM = "confirm";
    String URLTYPE = "urlType";
    String DEDUCTSETTINGS = "deductSettings";
    String T = "t";
    String VER = "ver";
    String URLKEY = "urlKey";
    String ADDR_MOBILE_AUTH = "http://inf.pay.guzhi.com/payment/mobileAuthCallback.action";

    /******* 重要，在渠道Additional中需要配置 *****/
    /** 用于向G币中心发送“确认是否再提示”的key. *****/
    /****************************************/
    String CONFIRMKEY = "confirmKey";

    final String CODE = "code";
    final String INFO = "info";

    final int SUCCESS = 1;
    // 重复订单,也是成功的另一种形式
    final int SUCCESS_ORDER_REPEAT = -18;
    final int PENDING = -30;
    final String SUCCESS_CODE = "1";

    final int UNKNOWN_ERROR = -100;

    // gbQueryOrder addgb时,gb系统调用我们的查询订单接口的固定结果
    final int QUERY_SUCCESS = 1;
    final int QUERY_SIGN_ERROR = -11;
    final int QUERY_ORDER_NOT_EXIST = -22;
    final int QUERY_UNKNOWN_ERROR = -100;

    // gbQueryOrder addgb时,gb系统调用我们的查询订单接口返回的字段
    final String QUERY_R_CODE = "code";
    final String QUERY_R_MESSAGE = "message";
    final String QUERY_R_gbUID = "gbuid";
    final String QUERY_R_AMOUNT = "amount";
    final String ADD_gb = "addgb";
    final String ADD_gb_SUCCESS = "ADD_gb_SUCCESS";
    final String ADD_gb_FAIL = "ADD_gb_FAIL";
    // 增加保证金相关参数
    final String ADD_DEPOSIT = "adddeposit";
    final String ADD_DEPOSIT_SUCCESS = "add_deposit_success";
    final String ADD_DEPOSIT_FAILED = "add_deposit_failed";

    // 增加频道保证金相关参数
    final String ADD_CHANNEL_DEPOSIT = "addchdeposit";
    final String ADD_CHANNEL_DEPOSIT_SUCCESS = "add_channel_deposit_success";
    final String ADD_CHANNEL_DEPOSIT_FAILED = "add_channel_deposit_failed";

    final String DEFAULT_USERIP = "127.0.0.1";
}