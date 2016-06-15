/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.jw;

/**
 * 骏网一卡通参数常量
 * 
 * @author administrator
 * 
 */
public interface JwConsts {
    // 3des加密key
    static final String THREE_DES_KEY = "D41E7618B199417DA09DE745";

    // 骏网充值地址
    static final String ADDR_JW_PAY = "http://service.800j.com/UCard/Submit.aspx?";

    // 骏网查询地址
    static final String ADDR_JW_QUERY = "http://service.800j.com/UCard/Query.aspx?";

    static final String ADDR_guzhiPay_NOTIFY = "/ch/notify/jwjk.do";

    // 商家ID (必填) 800j注册的账户数字ID
    static final String KEY_AGENT_ID = "agent_id";

    // 商家提交的唯一订单号（必填）必须唯一6到50位格式：数字、字母、或者数字字母组合
    static final String KEY_BILL_ID = "bill_id";

    // 商户订单时间(格式为 gbyMMddHHmmss)
    static final String KEY_BILL_TIME = "bill_time";

    // 一卡通卡号密码
    static final String KEY_CARD_DATA = "card_data";

    // 支付J点
    static final String KEY_PAY_JPOINT = "pay_jpoint";

    // 提交时间戳(格式为gbyMMddHHmmss )
    static final String KEY_TIME_STAMP = "time_stamp";

    // 签名
    static final String KEY_SIGN = "sign";

    // 连接符
    static final String CONNECT = "|||";

    // 返回结果代码
    static final String KEY_RET_CODE = "ret_code";

    // 返回消息
    static final String KEY_RET_MSG = "ret_msg";

    // 成功后在汇元网产生的单据号
    static final String KEY_JNET_BILL_NO = "jnet_bill_no";

    // 一卡通使用日志
    static final String KEY_CARD_USE_DATA = "card_use_data";

    // 实际支付J点
    static final String KEY_REAL_JPOINT = "real_jpoint";

    static final String EQ = "=";

    static final String AMP = "&";

    static final String SUCCESS_RESULT_CODE = "0";

    static final String CARD_ERROR_RESULT_CODE = "97";

    static final String CARD_SPLIT = "~";

    static final String CARD_DATA_SPLIT = ",";

    static final String KEY_NOTIFY_URL = "notify_url";

    static final String KEY_BILL_STATUS = "bill_status";

    static final String KEY_REAL_AMT = "real_amt";

    static final String SUCCESS = "1";

    static final String FAIL = "-1";

    static final String UNKNOWN = "0";

    static final String OK = "OK";

    public interface ReturnResult {
        String RETCODE_CODE_0 = "0";
        String RETCODE_MSG_0 = "充值成功";
        String RETCODE_CODE_1 = "1";
        String RETCODE_MSG_1 = "卡号或密码不正确";// 按照文档是“输入参数有误”
        String RETCODE_CODE_2 = "2";
        String RETCODE_MSG_2 = "代理商ID错误 或 未开通该服务";
        String RETCODE_CODE_3 = "3";
        String RETCODE_MSG_3 = "IP验证错误";
        String RETCODE_CODE_4 = "4";
        String RETCODE_MSG_4 = "签名验证错误";
        String RETCODE_CODE_5 = "5";
        String RETCODE_MSG_5 = "重复的订单号";
        String RETCODE_CODE_6 = "6";
        String RETCODE_MSG_6 = "卡加密错误";
        String RETCODE_CODE_7 = "7";
        String RETCODE_MSG_7 = "失败";
        String RETCODE_CODE_8 = "8";
        String RETCODE_MSG_8 = "单据不存在";
        String RETCODE_CODE_9 = "9";
        String RETCODE_MSG_9 = "卡号或密码不正确";
        String RETCODE_CODE_10 = "10";
        String RETCODE_MSG_10 = "卡中余额不足";
        String RETCODE_CODE_11 = "11";
        String RETCODE_MSG_11 = "无效的订单号";
        String RETCODE_CODE_13 = "13";
        String RETCODE_MSG_13 = "无效的单据时间";
        String RETCODE_CODE_14 = "14";
        String RETCODE_MSG_14 = "无效的产品编码";
        String RETCODE_CODE_97 = "97";// 文档中为“各种原因可获取ret_msg的中文说明”
        String RETCODE_MSG_97 = "失败：一卡通不存在或已经使用完了，或该卡其他产品的专用充值卡，不能充值本产品！";
        String RETCODE_CODE_98 = "98";
        String RETCODE_MSG_98 = "接口维中";
        String RETCODE_CODE_99 = "99";
        String RETCODE_MSG_99 = "系统错误,未知（需要查询后在处理单据状态）";
    }
}
