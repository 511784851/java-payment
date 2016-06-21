/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.szf;

/**
 * 神州付 常量
 * 
 * @author Administrator
 * 
 */
public interface SzfConsts {

    static final String ADDR_YYPAY_RETURN = "http://api.pay.yy.com/ch/return/szfSzx";

    // static final String ADDR_YYPAY_NOTIFY =
    // "http://api.payplf.yy.com/ch/notify/szfSzx.do";

    static final String ADDR_YYPAY_NOTIFY = "/ch/notify/szfSzx.do";

    static final String DES_KEY = "desKey";

    static final String ADDR_SZF_PAY = "http://pay3.shenzhoufu.com/interface/version3/serverconnszx/entry-noxml.aspx?";

    static final String ADDR_SZF_QUERY = "http://pay3.shenzhoufu.com/interface/version3/query/entry.aspx?";

    // 版本号
    static final String KEY_VERSION = "version";
    // 商户 ID
    static final String KEY_MERID = "merId";
    // 订单金额
    static final String KEY_PAYMONEY = "payMoney";
    // 订单号
    static final String KEY_ORDERID = "orderId";
    // 服务器返回地址
    static final String KEY_RETURNURL = "returnUrl";
    // 充值卡加密信息
    static final String KEY_CARDINFO = "cardInfo";
    // 商户私有数据
    static final String KEY_PRIVATEFIELD = "privateField";
    // 数据校验方式
    static final String KEY_VERIFYTYPE = "verifyType";
    // 充值卡类
    static final String KEY_CARDTYPECOMBINE = "cardTypeCombine";
    // MD5 校验串
    static final String KEY_MD5STRING = "md5String";
    // 用户名
    static final String KEY_MERUSERNAME = "merUserName";
    // 用户邮箱
    static final String KEY_MERUSERMAIL = "merUserMail";

    static final String CONNECT = "@";

    // PAY_开头为充值同步返回的状态码及描述
    static final String PAY_MD5FAIL = "101";

    static final String PAY_MD5FAIL_DES = "md5验证失败";

    static final String PAY_REPEATORDER = "102";

    static final String PAY_REPEATORDER_DES = "订单号重复";

    static final String PAY_BADUSER = "103";

    static final String PAY_BADUSER_DES = "恶意用户";

    static final String PAY_CARDERROR = "104";

    static final String PAY_CARDERROR_DES = "序列号，密码简单验证失败或之前曾提交过的卡密已验证失败";

    static final String PAY_PASWHANDLE = "105";

    static final String PAY_PASWHANDLE_DES = "密码正在处理中";

    static final String PAY_SYSBUSY = "106";

    static final String PAY_SYSBUSY_DES = "系统繁忙，暂停提交";

    static final String PAY_NSF = "107";

    static final String PAY_NSF_DES = "多次充值时卡内余额不足";

    static final String PAY_DESERROR = "109";

    static final String PAY_DESERROR_DES = "des解密失败";

    static final String PAY_AUTHFAIL = "201";

    static final String PAY_AUTHFAIL_DES = "证书验证失败";

    static final String PAY_INSERTDBFAIL = "501";

    static final String PAY_INSERTDEFAIL_DES = "插入数据库失败";

    static final String PAY_INSERTDBFAIL2 = "502";

    static final String PAY_INSERTDBFAIL2_DES = "插入数据库失败";

    static final String PAY_SUCCESS = "200";

    static final String PAY_SUCCESS_DES = "请求成功，神州付收单（非订单状态为成功）";

    static final String PAY_ARGSERROR = "902";

    static final String PAY_ARGSERROR_DES = "商户参数不全";

    static final String PAY_MERNOTEXIST = "903";

    static final String PAY_MERNOTEXIST_DES = "商户ID不存在";

    static final String PAY_MERNOTACTIVATE = "904";

    static final String PAY_MERNOTACTIVATE_DES = "商户没有激活";

    static final String PAY_MERNOTAUTH = "905";

    static final String PAY_MERNOTAUTH_DES = "商户没有使用该接口的权限";

    static final String PAY_MERNOTKEY = "906";

    static final String PAY_MERNOTKEY_DES = "商户没有设置密钥（privateKey）";

    static final String PAY_MERNOTDESKEY = "907";

    static final String PAY_MERNOTDESKEY_DES = "商户没有设置DES密钥";

    static final String PAY_ORDERDONE = "908";

    static final String PAY_ORDERDONE_DES = "该笔订单已经处理完成（订单状态已经为确定的状态：成功或者失败）";

    static final String PAY_URLNOTQUAL = "910";

    static final String PAY_URLNOTQUAL_DES = "服务器返回地址，不符合规范";

    static final String PAY_ORDERIDNOTQUAL = "911";

    static final String PAY_ORDERIDNOTQUAL_DES = "订单号，不符合规范";

    static final String PAY_ILLEGALORDER = "912";

    static final String PAY_ILLEGALORDER_DES = "非法订单";

    static final String PAY_CARDNOTSUPPORT = "913";

    static final String PAY_CARDNOTSUPPORT_DES = "该地方卡暂时不支持";

    static final String PAY_AMOUNTERROR = "914";

    static final String PAY_AMOUNTERROR_DES = "金额非法";

    static final String PAY_TOTALAMOUNTERROR = "915";

    static final String PAY_TOTALAMOUNTERROR_DES = "卡面额非法";

    static final String PAY_MERNOTSUPPORTCARD = "916";

    static final String PAY_MERNOTSUPPORTCART_DES = "商户不支持该充值卡";

    static final String PAY_ARGSNOTFORMAL = "917";

    static final String PAY_ARGSNOTFORMAL_DES = "参数格式不正确";

    static final String PAY_NETWORKERROR = "0";

    static final String PAY_NETWORKERROR_DES = "网络连接失败";

    static final String KEY_ORDERIDS = "orderIds";

    static final String KEY_RESULTFORMAT = "resultFormat";

    static final String KEY_ORDERS = "orders";

    static final String KEY_ORDER = "order";

    static final String KEY_PAYSTATUS = "payStatus";

    static final String KEY_ENDDATE = "endDate";

    static final String KEY_PAYRESULT = "payResult";
    // 成功
    static final String KEY_PAYSUCCESS = "1";
    // 失败
    static final String KEY_PAYFAIL = "0";
    // 处理中
    static final String KEY_PAYPENDDING = "2";

    static final String KEY_QUERYRESULT = "queryResult";

    static final String KEY_PAYDETAILS = "payDetails";

    static final String KEY_ERRORCODE = "errcode";

    static final String CHID = "szx";

    // NOTIFY_开头为神州付异步返回的状态码及描述
    static final String NOTIFY_SUCCESS = "200";

    static final String NOTIFY_SUCCESS_DES = "充值卡验证成功";

    static final String NOTIFY_PWDERRAMOUNTERR = "201";

    static final String NOTIFY_PWDERRAMOUNTERR_DES = "您输入的充值卡密码错误或充值卡余额不足";

    static final String NOTIFY_CARDUSED = "202";

    static final String NOTIFY_CARDUSED_DES = "您输入的充值卡已被使用";

    static final String NOTIFY_PWDILLEGAL = "203";

    static final String NOTIFY_PWDILLEGAL_DES = "您输入的充值卡密码非法";

    static final String NOTIFY_FAILTOOMUCH = "204";

    static final String NOTIFY_FAILTOOMUCH_DES = "您输入的卡号或密码错误次数过多";

    static final String NOTIFY_CARDNOTFORMAL = "205";

    static final String NOTIFY_CARDNOTFORMAL_DES = "卡号密码正则不匹配或者被禁止";

    static final String NOTIFY_REPEATSUBMIT = "206";

    static final String NOTIFY_REPEATSUBMIT_DES = "本卡之前被提交过，本次订单失败，不再继续处理";

    static final String NOTIFY_CARDNOTSUPPORT = "207";

    static final String NOTIFY_CARDNOTSUPPORT_DES = "暂不支持该充值卡";

    static final String NOTIFY_CARDNUMERROR = "208";

    static final String NOTIFY_CARDNUMERROR_DES = "您输入的充值卡卡号错误";

    static final String NOTIFY_CARDNOTACTIVATE = "209";

    static final String NOTIFY_CARDNOTACTIVATE_DES = "您输入的充值卡未激活（生成卡）";

    static final String NOTIFY_CARDINVALID = "210";

    static final String NOTIFY_CARDINVALID_DES = "您输入的充值卡已经作废（能查到有该卡，但是没卡的信息）";

    static final String NOTIFY_CARDEXPIRED = "211";

    static final String NOTIFY_CARDEXPIRED_DES = "您输入的充值卡已过期";

    static final String NOTIFY_TOTALAMOUNTERROR = "212";

    static final String NOTIFY_TOTALAMOUNTERROR_DES = "您选择的卡面额不正确";

    static final String NOTIFY_SPECIALCARD = "213";

    static final String NOTIFY_SPECIALCARD_DES = "该卡为特殊本地业务卡，系统不支持";

    static final String NOTIFY_RISECARD = "214";

    static final String NOTIFY_RISECARD_DES = "该卡为增值业务卡，系统不支持";

    static final String NOTIFY_NEWCARD = "215";

    static final String NOTIFY_NEWCARD_DES = "新生卡";

    static final String NOTIFY_SYSMAIN = "216";

    static final String NOTIFY_SYSMAIN_DES = "系统维护";

    static final String NOTIFY_INTERFACEMAIN = "217";

    static final String NOTIFY_INTERFACEMAIN_DES = "接口维护";

    static final String NOTIFY_OSYSMAIN = "218";

    static final String NOTIFY_OSYSMAIN_DES = "运营商系统维护";

    static final String NOTIFY_SYSBUSY = "219";

    static final String NOTIFY_SYSBUSY_DES = "系统忙，请稍后再试";

    static final String NOTIFY_ERRORUNKNOWN = "220";

    static final String NOTIFY_ERRORUNKNOWN_DES = "充值卡金额不足以支付本次订单";// 文档描述为“未知错误”，神州付技术建议修改为“充值卡金额不足以支付本次订单”

    static final String NOTIFY_CARDDONE = "221";

    static final String NOTIFY_CARDDONE_DES = "本卡之前被处理完毕，本次订单失败，不再继续处理";

    // QUERY_开头为神州付查询接口返回的状态码和描述
    static final String QUERY_SUCCESS = "S";

    static final String QUERY_SUCCESS_DES = "神州付已收单";// 原来是"成功"

    static final String QUERY_ARGSNOTENOUGH = "F001";

    static final String QUERY_ARGSNOTENOUGH_DES = "参数不全";

    static final String QUERY_MERIDNOTEXIST = "F002";

    static final String QUERY_MERIDNOTEXIST_DES = "商户ID不存在";

    static final String QUERY_ORDERIDNOTEXIST = "F003";

    static final String QUERY_ORDERINDOTEXIST_DES = "订单号不存在";

    static final String QUERY_MD5FAIL = "F004";

    static final String QUERY_MD5FAIL_DES = "MD5校验失败";
}
