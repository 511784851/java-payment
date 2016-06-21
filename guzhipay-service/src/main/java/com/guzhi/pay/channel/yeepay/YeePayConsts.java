/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.yeepay;

/**
 * 易宝网关支付参数
 * 参考文档《易宝支付产品(HTML版)通用接口文档 v3.0》
 * Note:gbk
 * 
 * @author administrator
 * 
 */
public interface YeePayConsts {
    static final String BUY_CMD = "Buy";
    static final String REFUND_CMD = "RefundOrd";
    String CHARGECARDDIRECT = "ChargeCardDirect";
    String QUERYORDERDETAIL = "QueryOrdDetail";
    static final String CHARGE_CARD_QUERY = "ChargeCardQuery";

    static final String CNY = "CNY";
    static final String DEFAULT_SAF = "0";
    static final String NEED_RESPONSE = "1";
    static final String YEEPAY_PAY_URL = "https://www.yeepay.com/app-merchant-proxy/node";
    static final String YEEPAY_QUERY_URL = "https://www.yeepay.com/app-merchant-proxy/command";
    static final String YEEPAY_REFUND_URL = "https://www.yeepay.com/app-merchant-proxy/command";

    static final String ADDR_YEEPAY_GATE_NOTIFY = "/ch/notify/yeepay-gate.do";
    static final String ADDR_YEEPAY_CARD_NOTIFY = "/ch/notify/yeepay-card.do";

    String YEEPAY_CARD_PAY_URL = "https://www.yeepay.com/app-merchant-proxy/command.action";

    // hard code,to be transformed to configuration files or database.
    static final String YEEPAY_MERCHAND_ID = "";
    // never used,provided in database.
    static final String YEEPAY_MERCHAND_PW = "";

    static final String P0_CMD = "p0_Cmd";

    static final String P1_MERID = "p1_MerId";

    static final String P2_ORDER = "p2_Order";

    static final String P3_AMT = "p3_Amt";

    static final String P4_CUR = "p4_Cur";

    static final String P5_PID = "p5_Pid";

    static final String P6_PCAT = "p6_Pcat";

    static final String P7_PDESC = "p7_Pdesc";

    static final String P8_URL = "p8_Url";

    static final String P9_SAF = "p9_SAF";

    static final String PA_MP = "pa_MP";

    static final String PD_FRPID = "pd_FrpId";

    static final String PR_NEEDRESPONSE = "pr_NeedResponse";

    static final String HMAC = "hmac";

    static final String GBK = "gbk";

    static final String SUCCESS = "success";

    static final String CANCELED = "CANCELED";

    static final String INIT = "INIT";

    static final String PB_TRXID = "pb_TrxId";

    static final String PA7_CARDAMT = "pa7_cardAmt";

    static final String PA8_CARDNO = "pa8_cardNo";

    static final String PA9_CARDPWD = "pa9_cardPwd";

    static final String CARD_REQ_SUCCESS = "1";

    static final String R0_CMD = "r0_Cmd";

    static final String R1_CODE = "r1_Code";

    static final String R6_ORDER = "r6_Order";

    static final String RQ_RETURNMSG = "rq_ReturnMsg";

    static final String NOTIFY_HANDLE_RESULT_FAILED = "failed";

    static final String P4_VERFYAMT = "p4_verifyAmt";

    static final String PZ_USERID = "pz_userId";

    static final String PZ1_USERREGTIME = "pz1_userRegTime";

    static final String P4_FRPID = "p4_FrpId";

    static final String P5_CARDNO = "p5_CardNo";

    static final String P6_CONFIRMAMOUNT = "p6_confirmAmount";

    static final String P7_REALAMOUNT = "p7_realAmount";

    static final String P8_CARDSTATUS = "p8_cardStatus";

    static final String P9_MP = "p9_MP";

    static final String PB_BALANCEAMT = "pb_BalanceAmt";

    static final String PC_BALANCEACT = "pc_BalanceAct";

    static final String CARD_PAY_SUCCESS = "0";

    static final String SZX = "SZX";

    static final String UNICOM = "UNICOM";

    static final String RB_PAYSTATUS = "rb_PayStatus";

    /**
     * gate notify parameters.
     */
    static final String R2_TRXID = "r2_TrxId";
    static final String R3_AMT = "r3_Amt";
    static final String R4_CUR = "r4_Cur";
    static final String R5_PID = "r5_Pid";
    static final String R7_UID = "r7_Uid";
    static final String R8_MP = "r8_MP";
    static final String R9_BTYPE = "r9_BType";
    static final String RB_BANKID = "rb_BankId";
    static final String RO_BANKORDERID = "ro_BankOrderId";
    static final String RP_PAYDATE = "rp_PayDate";
    static final String RQ_CARDNO = "rq_CardNo";
    static final String RU_TRXTIME = "ru_Trxtime";

    /**
     * notify parameter values
     */
    String PAY_SUCCESS_MSG = "支付成功";
    String PAY_FAILED_MSG = "支付失败";

    String NOTIFY_SUCCESS_CODE = "1";

    /**
     * Notify报文中r9_BType的值
     * 为“1”: 浏览器重定向;
     * 为“2”: 服务器点对点通讯.
     */
    String RETURNTYPE = "1";
    String NOTIFYTYPE = "2";

    /**
     * 易宝卡密通知报文中r1_code字段的取值
     */
    String NOTIFY_R1_CODE_SUCCESS = "1";
    String NOTIFY_R1_CODE_FAIL = "2";

    /**
     * 
     * 卡密提交状态返回码
     * 
     */
    public interface CardPayRespCode {
        // success status
        String CODE_1 = "1";
        String MSG_1 = "提交成功，等待易宝与运营商处理订单";// 原本为“提交成功”，为避免用户产生误会，将其改为“提交成功，等待易宝与运营商处理订单”

        // fail status
        String CODE_n1 = "-1";
        String MSG_n1 = "签名较验失败或未知错误";
        String CODE_2 = "2";
        String MSG_2 = "卡密成功处理过或者提交卡号过于频繁";
        String CODE_5 = "5";
        String MSG_5 = "卡数量过多，目前最多支持10张卡";
        String CODE_11 = "11";
        String MSG_11 = "订单号重复";
        String CODE_21 = "21";
        String MSG_21 = "请求失败，请稍后再试";
        String CODE_66 = "66";
        String MSG_66 = "支付金额有误";
        String CODE_95 = "95";
        String MSG_95 = "支付方式未开通";
        String CODE_112 = "112";
        String MSG_112 = "业务状态不可用，未开通此类卡业务";
        String CODE_8001 = "8001";
        String MSG_8001 = "卡面额组填写错误";
        String CODE_8002 = "8002";
        String MSG_8002 = "卡号密码为空或者数量不相等（使用组合支付时）";

        // custom undefined status
        String MSG_UNDEFINED = "不能识别的支付返回状态";
    }

    /**
     * 卡密通知状态码
     * 
     */
    public interface CardPayNotifyStatus {
        // success status
        String CODE_0 = "0";
        String MSG_0 = "销卡成功，订单成功";

        // fail status
        String CODE_1 = "1";
        String MSG_1 = "销卡成功，订单失败";
        String CODE_7 = "7";
        String MSG_7 = "卡号卡密或卡面额不符合规则";
        String CODE_1002 = "1002";
        String MSG_1002 = "本张卡密您提交过于频繁，请您稍后再试";
        String CODE_1003 = "1003";
        String MSG_1003 = "不支持的卡类型（比如电信地方卡）";
        String CODE_1004 = "1004";
        String MSG_1004 = "密码错误或充值卡无效";
        String CODE_1006 = "1006";
        String MSG_1006 = "充值卡无效";
        String CODE_1007 = "1007";
        String MSG_1007 = "卡内余额不足";
        String CODE_1008 = "1008";
        String MSG_1008 = "余额卡过期（有效期1个月）";
        String CODE_1010 = "1010";
        String MSG_1010 = "此卡正在处理中";
        String CODE_10000 = "10000";
        String MSG_10000 = "未知错误";
        String CODE_2005 = "2005";
        String MSG_2005 = "此卡已使用";
        String CODE_2006 = "2006";
        String MSG_2006 = "卡密在系统处理中";
        String CODE_2007 = "2007";
        String MSG_2007 = "该卡为假卡";
        String CODE_2008 = "2008";
        String MSG_2008 = "该卡种正在维护";
        String CODE_2009 = "2009";
        String MSG_2009 = "浙江省移动维护";
        String CODE_2010 = "2010";
        String MSG_2010 = "江苏省移动维护";
        String CODE_2011 = "2011";
        String MSG_2011 = "福建省移动维护";
        String CODE_2012 = "2012";
        String MSG_2012 = "辽宁省移动维护";
        String CODE_2014 = "2014";
        String MSG_2014 = "支付系统繁忙，请重新支付";// 易宝支付技术-广东(1581758834) 17:12:21 支付失败
                                         // 这张卡 还可以使用
        String CODE_3001 = "3001";
        String MSG_3001 = "卡不存在";
        String CODE_3002 = "3002";
        String MSG_3002 = "卡已使用过";
        String CODE_3003 = "3003";
        String MSG_3003 = "卡已作废";
        String CODE_3004 = "3004";
        String MSG_3004 = "卡已冻结";
        String CODE_3005 = "3005";
        String MSG_3005 = "卡未激活";
        String CODE_3006 = "3006";
        String MSG_3006 = "密码不正确";
        String CODE_3007 = "3007";
        String MSG_3007 = "卡正在处理中";
        String CODE_3101 = "3101";
        String MSG_3101 = "系统错误";
        String CODE_3102 = "3102";
        String MSG_3102 = "卡已过期";

        // custom undefined status
        String MSG_UNDEFINED = "不能识别的通知状态码";

    }

    /**
     * 通用查询返回状态码（银行卡和电话卡）
     * 用于匹配查询响应中的字段rb_PayStatus
     */
    public interface GeneralQueryRespStatus {
        String CODE_INIT = "INIT";
        String MSG_INIT = "未支付";
        String CODE_CANCELED = "CANCELED";
        String MSG_CANCELED = "已取消";
        String CODE_SUCCESS = "SUCCESS";
        String MSG_SUCCESS = "已支付";
        // custom undefined status
        String MSG_UNDEFINED = "不能识别的查询返回状态码";
    }

    /**
     * 通用查询返回码（银行卡和电话卡）
     * 用于匹配查询响应中的字段r1_Code
     */
    public interface GeneralQueryRespCode {
        String CODE_1 = "1";
        String MSG_1 = "查询正常";
        String CODE_50 = "50";
        String MSG_50 = "订单不存在";
        // custom undefined status
        String MSG_UNDEFINED = "不能识别的查询返回码";
        String CODE_3 = "3";
        String MSG_3 = "订单正在处理中";
    }

    /**
     * 
     * 退款返回码
     * 
     */
    public interface GeneralRefundRespCode {
        // success status
        String CODE_1 = "1";
        String MSG_1 = "退款成功";
        // failed status
        String CODE_2 = "2";
        String MSG_2 = "账户状态无效";
        String CODE_7 = "7";
        String MSG_7 = "该订单不支持退款";
        String CODE_10 = "10";
        String MSG_10 = "退款金额超限";
        String CODE_18 = "18";
        String MSG_18 = "余额不足";
        String CODE_50 = "50";
        String MSG_50 = "订单不存在";

        // custom undefined status
        String MSG_UNDEFINED = "不能识别的退款返回码";
    }

}
