/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.unionpay;

/**
 * 参考《银联无卡支付商户接入规范.docx》
 * 第0.1.0版
 * 
 * @author 
 * 
 */
public interface Constants {
    String NOTIFYURL = "/ch/notify/unionPayWap.do";
    String RETURNURL = "/ch/return/unionPayWap.do";

    String KEY_APPLICATION = "application";
    String KEY_VERSION = "version";
    String KEY_SENDTIME = "sendTime";
    String KEY_SENDSEQID = "sendSeqId";
    String KEY_MERCHANTNAME = "merchantName";
    String KEY_MERCHANTID = "merchantId";
    String KEY_MERCHANTORDERID = "merchantOrderId";
    String KEY_MERCHANTORDERTIME = "merchantOrderTime";
    String KEY_MERCHANTORDERAMT = "merchantOrderAmt";
    String KEY_MERCHANTORDERCURRENCY = "merchantOrderCurrency";
    String KEY_MERCHANTORDERDESC = "merchantOrderDesc";
    String KEY_TRANSTIMEOUT = "transTimeout";
    String KEY_TRANSTYPE = "transType";
    String KEY_GWTYPE = "gwType";
    String KEY_GWINVOKECMD = "gwInvokeCmd";
    String KEY_FRONTURL = "frontUrl";
    String KEY_BACKURL = "backUrl";
    String KEY_MERCHANTUSERID = "merchantUserId";
    String KEY_MOBILENUM = "monbileNum";
    String KEY_USERNAME = "userName";
    String KEY_IDTYPE = "idType";
    String KEY_IDNUM = "idNum";
    String KEY_CARDNUM = "cardNum";
    String KEY_MSGEXT = "msgExt";
    String KEY_MISC = "misc";
    String KEY_RESPCODE = "respCode";
    String KEY_RESPDESC = "respDesc";

    String PAY_REQ_APPLICATION = "MGw.Req";
    String PAY_RSP_APPLICATION = "MGw.Rsp";
    String VERSION = "1.0.0";
    String TRANSTYPE = "01";
    String GWTYPE = "01";

    String NOTIFY_REQ_APPLICATION = "MTransNotify.Req";
    String NOTIFY_RSP_APPLICATION = "MTransNotify.Rsp";
    String NOTIFY_RSP_SUCCESS_CODE = "0000";
    String NOTIFY_RSP_SUCCESS_DESC = "pay_success";

    String MERCHANT_ORDER_CURRENCY = "156";

    String DESKEY = "desKey";

    String GWINVOKECMD = "gwInvokeCmd";

    String UPBP = "upbp";

    String ERROR = "0";

    String QUERY_APPLICATION = "MTransInfo.Req";

    String RESPCODE = "respCode";

    String QUERY_RESULT = "queryResult";

    String CUPSRESPCODE = "cupsRespCode";

    String CUPSQID = "cupsQid";

    String CUPSRESPDESC = "cupsRespDesc";

    String CPUSTRACETIME = "cupsTraceTime";

    // 预订单接口处理成功
    String PREPARE_SUCCESS_MSG = "银联预订单处理成功，等待用户支付或银联通知";// 银联接收预订单请求之后，正常返回为“处理成功”，为防止理解偏差，改为当前描述。

    String APP_PAY_ADDR = "/gateway/merchant/trade";
    String APP_QUERY_ADDR = "/gateway/merchant/query";
    String APP_NOTIFYURL = "/ch/notify/unionPayWapApp.do";
    String APP_RETURNURL = "/ch/return/unionPayWapApp.do";

    // app使用的参数
    String KEY_CHARSET = "charset";
    String KEY_MERID = "merId";
    String KEY_BACKENDURL = "backEndUrl";
    String KEY_FRONTENDURL = "frontEndUrl";
    String KEY_ORDERDESCRIPTION = "orderDescription";
    String KEY_ORDERTIME = "orderTime";
    String KEY_ORDERNUMBER = "orderNumber";
    String KEY_ORDERAMOUNT = "orderAmount";

    String KEY_TN = "tn";
    String KEY_SIGNATURE = "signature";
    String KEY_SIGNMETHOD = "signMethod";
    String KEY_SETTLEAMOUNT = "settleAmount";
    String KEY_SETTLEDATE = "settleDate";
    String KEY_QN = "qn";
    String KEY_TRANSSTATUS = "transStatus";
    // String KEY_RESPCODE = "respCode";

    String APP_NOTIFY_SUCCESS_TRANSSTATUS = "00";
    String CHARSET_UTF8 = "UTF-8";
    String MD5 = "MD5";

    /**
     * 
     * App支付时CUPS的响应状态。
     * 
     */
    public interface AppCupsStatus {
        // 成功的情况
        String CUPS_CODE_00 = "00";
        String CUPS_MSG_00 = "操作成功";
        // 异常的情况
        String CUPS_CODE_01 = "01";
        String CUPS_MSG_01 = "请求报文错误";
        String CUPS_CODE_02 = "02";
        String CUPS_MSG_02 = "签名验证失败";
        String CUPS_CODE_03 = "03";
        String CUPS_MSG_03 = "交易失败，详情请咨询";
        String CUPS_CODE_04 = "04";
        String CUPS_MSG_04 = "会话超时";
        String CUPS_CODE_11 = "11";
        String CUPS_MSG_11 = "订单未支付";
        String CUPS_CODE_21 = "21";
        String CUPS_MSG_21 = "无效订单";
        String CUPS_CODE_22 = "22";
        String CUPS_MSG_22 = "重复支付";
        String CUPS_CODE_23 = "23";
        String CUPS_MSG_23 = "请您确认输入的卡号与所选的银行与卡类型相符合";
        String CUPS_CODE_24 = "24";
        String CUPS_MSG_24 = "请您确认手机号是否填写正确";
        String CUPS_CODE_25 = "25";
        String CUPS_MSG_25 = "请确认您银行卡的有效期是否填写正确";
        String CUPS_CODE_26 = "26";
        String CUPS_MSG_26 = "请您确认身份证件号是否填写正确";
        String CUPS_CODE_27 = "27";
        String CUPS_MSG_27 = "贵银行卡未开通银联无卡业务，请到银行柜台开通";
        String CUPS_CODE_28 = "28";
        String CUPS_MSG_28 = "非常抱歉，目前本系统不支持该银行卡交易,请换其他银行";
        String CUPS_CODE_31 = "31";
        String CUPS_MSG_31 = "查找原始交易失败";
        String CUPS_CODE_32 = "32";
        String CUPS_MSG_32 = "交易无效或无法完成";
        String CUPS_CODE_33 = "33";
        String CUPS_MSG_33 = "原始金额错误";
        String CUPS_CODE_41 = "41";
        String CUPS_MSG_41 = "交易受限";
        String CUPS_CODE_42 = "42";
        String CUPS_MSG_42 = "交易金额超限";
        String CUPS_CODE_51 = "51";
        String CUPS_MSG_51 = "短信验证码错误";
        String CUPS_CODE_52 = "52";
        String CUPS_MSG_52 = "您的短信发送过于频繁，请稍候再试";
        String CUPS_CODE_53 = "53";
        String CUPS_MSG_53 = "您输入的短信验证码与手机号不匹配，请检查手机号或验证";
        String CUPS_CODE_61 = "61";
        String CUPS_MSG_61 = "处理超时，请重试";
        String CUPS_CODE_91 = "91";
        String CUPS_MSG_91 = "CUPS";
        String CUPS_CODE_92 = "92";
        String CUPS_MSG_92 = "多渠道应答异常";
    }

    public interface CupsStatus {
        // 通知报文成功情况
        String CUPS_CODE_00 = "00";
        String CUPS_MSG_00 = "支付成功";
        // 通知报文异常情况
        String CUPS_CODE_01 = "01";
        String CUPS_MSG_01 = "交易异常，支付失败。详情请咨询95516";
        String CUPS_CODE_02 = "02";
        String CUPS_MSG_02 = "您输入的卡号无效，请确认后输入";
        String CUPS_CODE_03 = "03";
        String CUPS_MSG_03 = "发卡银行不支持，支付失败";
        String CUPS_CODE_06 = "06";
        String CUPS_MSG_06 = "您的卡已经过期，请使用其他卡支付";
        String CUPS_CODE_11 = "11";
        String CUPS_MSG_11 = "您卡上的余额不足";
        String CUPS_CODE_14 = "14";
        String CUPS_MSG_14 = "您的卡已过期或者是您输入的有效期不正确，支付失败";
        String CUPS_CODE_15 = "15";
        String CUPS_MSG_15 = "您输入的银行卡密码有误，支付失败";
        String CUPS_CODE_20 = "20";
        String CUPS_MSG_20 = "您输入的转入卡卡号有误，支付失败";
        String CUPS_CODE_21 = "21";
        String CUPS_MSG_21 = "您输入的手机号或CVN2有误，支付失败";
        String CUPS_CODE_25 = "25";
        String CUPS_MSG_25 = "原始交易查找失败";
        String CUPS_CODE_30 = "30";
        String CUPS_MSG_30 = "报文格式错误";
        String CUPS_CODE_36 = "36";
        String CUPS_MSG_36 = "交易金额超过网上银行交易金额限制，支付失败";
        String CUPS_CODE_39 = "39";
        String CUPS_MSG_39 = "您已连续多次输入错误密码";
        String CUPS_CODE_40 = "40";
        String CUPS_MSG_40 = "请与您的银行联系";
        String CUPS_CODE_41 = "41";
        String CUPS_MSG_41 = "您的银行不支持认证支付，请选择快捷支付";
        String CUPS_CODE_42 = "42";
        String CUPS_MSG_42 = "您的银行不支持普通支付，请选择快捷支付";
        String CUPS_CODE_56 = "56";
        String CUPS_MSG_56 = "交易受限";
        String CUPS_CODE_71 = "71";
        String CUPS_MSG_71 = "交易无效，无法完成，支付失败";
        String CUPS_CODE_80 = "80";
        String CUPS_MSG_80 = "内部错误";
        String CUPS_CODE_81 = "81";
        String CUPS_MSG_81 = "可疑报文";
        String CUPS_CODE_82 = "82";
        String CUPS_MSG_82 = "验签失败";
        String CUPS_CODE_83 = "83";
        String CUPS_MSG_83 = "超时";
        String CUPS_CODE_84 = "84";
        String CUPS_MSG_84 = "订单不存在";
        String CUPS_CODE_94 = "94";
        String CUPS_MSG_94 = "重复交易";
    }

    public interface QueryResult {
        String QUERYRESULT_CODE_0 = "0";
        String QUERYRESULT_MSG_0 = "成功";
        String QUERYRESULT_CODE_1 = "1";
        String QUERYRESULT_MSG_1 = "失败";
        String QUERYRESULT_CODE_2 = "2";
        String QUERYRESULT_MSG_2 = "处理中";
        String QUERYRESULT_CODE_3 = "3";
        String QUERYRESULT_MSG_3 = "无此交易";
    }
}