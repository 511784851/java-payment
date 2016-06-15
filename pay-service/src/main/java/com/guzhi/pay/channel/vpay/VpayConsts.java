/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.vpay;

/**
 * 盈华讯方常量
 * 
 * @author 
 * 
 */
public interface VpayConsts {
    /**
     * 商户代码(n5)
     */
    String KEY_SP = "sp";
    /**
     * 商户订单号(_an1...30)
     */
    String KEY_OD = "od";
    /**
     * 订单金额(n1...3)，单位为元
     */
    String KEY_MZ = "mz";
    /**
     * MD5值(n32)，md532(sp+od+sppwd+mz+spreq+spsuc+mob)
     */
    String KEY_MD5 = "md5";
    /**
     * 商户网站请求地址
     */
    String KEY_SPREQ = "spreq";
    /**
     * 商户显示成功充值地址
     */
    String KEY_SPSUC = "spsuc";
    /**
     * 用户自定义字段，非汉字
     */
    String KEY_SPZDY = "spzdy";
    /**
     * 手机号码(n11)
     */
    String KEY_MOB = "mob";
    /**
     * 用户ID(c1...50)
     */
    String KEY_UID = "uid";
    String PAY_SUCCESS_MSG = "yhxfsucc";
    String PAY_FAIL_MSG = "yhxffail";

    /**
     * App版本短信支付请求地址
     */
    // String GATE_URL = "http://ydzf.vnetone.com/Default_app.aspx";
    String GATE_URL = "http://ydzf.vnetone.com/Default_sdk.aspx";

    /**
     * PC端短信支付请求地址
     */
    String PC_GATE_URL = "http://ydzf.vnetone.com/Default_mo.aspx";

    /***************************** Important **************************************/
    /**
     * It is never used in our application,and it should be configured by
     * channel staff.
     * This url is used by chId:Vpay,paymethod:sms.
     */
    String NOTIFY_URL = "/ch/notify/vpaySms";

    /***************************** Important **************************************/
    /**
     * It is never used in our application,and it should be configured by
     * channel staff.
     * This url is used by chId:Vpay,paymethod:pcsms.
     */
    String PC_NOTIFY_URL = "/ch/notify/vpay-pc-sms.do";

    String SPREQ = "http://pay.guzhi.com/";
    String SPSUC = "http://pay.guzhi.com/";
    String SPZDY = "duowan";

    String KEY_SPID = "spid";
    String KEY_OID = "oid";
    String KEY_SPORDER = "sporder";
    String KEY_ZDY = "zdy";
    String KEY_SPUID = "spuid";
    String NOTIFY_SUCCESS_MSG = "okydzf";
    String NOTIFY_FAIL_MSG = "failydzf";
    String SP_VERSION = "vpay1001";
    String VB_NOTIFY = "/ch/notify/vpay-tel.do";
    String VB_ADDR = "http://s2.vnetone.com/Default.aspx";
    String VPAYTEL_FRONT = "/ch/front/vpay-tel.do";

    String DEFAULT_SP_REQ = "http://www.gb.com";

    // SP在扣费前调用的param
    String KEY_URLCODE = "urlcode";
    String KEY_SP_VERSION = "spversion";
    String KEY_SP_ID = "spid";
    String KEY_SP_NAME = "spname";
    String KEY_SP_ORDERID = "spoid";
    String KEY_USER_ID = "userid";
    String KEY_USER_IP = "userip";
    String KEY_MONEY = "money";
    // 自定义字段,会返回
    String KEY_SP_CUSTOM = "spcustom";
    String KEY_SP_REQ = "spreq";
    String KEY_NOTIFY_URL = "sprec";
    String KEY_SP_MD5 = "spmd5";

    String KEY_gbUID = "gbuid";
    // 返回商户网站，用户重新下单的地址
    String KEY_PROD_URL = "vbProdUrl";

    String V1 = "v1";

    String V2 = "v2";

    String V3 = "v3";

    String V6 = "v6";

    String V7 = "v7";

    String V10 = "v10";

    String V4 = "v4";

    /**
     * App移动短信支付返回值key列表
     */
    String TEL = "tel";

    String CONTENT = "content";

    String DES = "des";
}