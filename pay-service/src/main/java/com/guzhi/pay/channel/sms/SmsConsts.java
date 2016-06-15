/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.sms;

/**
 * @author administrator
 * 
 */
public interface SmsConsts {

    /** 随机验证码的长度 */
    static final int randomCharLength = 6;
    static final String CHARSET_UTF8 = "UTF-8";
    static final String SMSURL = "http://sms.guzhi.com/send/smssending_emay.jsp";

    /** 存放在appch中smskey(用来给用户发短信的签名key) */
    String KEY_SMS_KEY = "smsKey";
    String KEY_SUB_USER = "subUser";
    String YD_PHONE_REG = "ydPhoneReg";

    // 从order中的additional字段中取出
    String KEY_TEL = "tel";
    String KEY_PASSPORT = "pass";

    // 发送短信,方法的状态码
    String SEND_SMS_SUCCESS = "1";
    String SEND_SMS_FAIL_SIGN = "-1";
    String SEND_SMS_FAIL_PARAM = "-2";
    String SEND_SMS_FAIL_DB = "-3";
    String SEND_SMS_FAIL_INTERFACE = "-4";
    String SEND_SMS_FAIL_PROJECT = "-5";
    String SEND_SMS_FAIL_PHONE = "-6";

    String SEND_SMS_SUCCESS_MSG = "成功";
    String SEND_SMS_FAIL_SIGN_MSG = "验证错误";
    String SEND_SMS_FAIL_PARAM_MSG = "参数信息不完整";
    String SEND_SMS_FAIL_DB_MSG = "信息入库失败";
    String SEND_SMS_FAIL_INTERFACE_MSG = "发送接口失败";
    String SEND_SMS_FAIL_PROJECT_MSG = "项目编号错误";
    String SEND_SMS_FAIL_PHONE_MSG = "手机号码错误";

    // SP在扣费前调用的param
    String KEY_MOBILE_ID = "MobileId";
    String KEY_PID = "Pid";
    String KEY_PINF = "Pinf";
    String KEY_ORDER_ID = "OrderId";
    String KEY_AMOUNT = "Amount";
    String KEY_MAC = "Mac";
    String KEY_SMS_DATE = "SmsDate";

    // SP在扣费前调用guzhiPay进行查询订单确认
    String CONFIRM_SUCCESS = "0";
    String CONFIRM_FAIL_ORDER_REPEAT = "1";
    String CONFIRM_FAIL_CODE_INVALID = "2";
    String CONFIRM_FAIL_SIGN_INVALID = "3";
    String CONFIRM_FAIL_OTHER = "4";

    String CONFIRM_SUCCESS_MSG = "ok";
    String CONFIRM_FAIL_ORDER_REPEAT_MSG = "order_repeat";
    String CONFIRM_FAIL_CODE_INVALID_MSG = "code_invalid";
    String CONFIRM_FAIL_SIGN_INVALID_MSG = "sign_invalid";
    String CONFIRM_FAIL_OTHER_MSG = "other_fail";

    String PHONE = "phone";
    String USERID = "userid";
    String SUBUSER = "subuser";
    String TIME = "time";
    String MAC = "mac";
    String CONTENT = "content";

    String MER_ID = "merId";

    String GOODS_ID = "goodsId";

    String GOODS_INF = "goodsInf";

    String MOBILE_ID = "mobileId";

    String AMT_TYPE = "amtType";

    String BANK_TYPE = "bankType";

    String VERSION = "version";

    String SIGN = "sign";

    String CRT_PATH = "crtPath";

    String PRI_KEY_PATH = "priKeyPath";

    String CONNECT = "|";

    String ORDER_ID = "orderId";

    String MER_DATE = "merDate";

    String AMOUNT = "amount";

    String NOTIFY_URL = "notifyUrl";

    String MER_PRIV = "merPriv";

    String EXPAND = "expand";

    String RET_CODE = "retCode";

    String RET_MSG = "retMsg";

    String NOTIFY_URL_ADDR = "/ch/notify/smsyd.do";

    String SMS_YD_SUCCESS = "0000";

    String SMS_YD_SUCCESS_DES = "联动：支付成功";

    String SMS_YD_FAIL = "1111";

    String SMS_YD_FAIL_DES = "联动：支付取消";

    String PAY_DATE = "payDate";

    String TRANS_TYPE = "transType";

    String SETTLE_DATE = "settleDate";

    String QUERY_SUCCESS = "1";

    String QUERY_FAIL = "2";

    String QUERY_INI = "0";

}
