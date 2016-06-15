/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.common;

/**
 * 一些常量
 * 
 * @author administrator
 * 
 */
public interface Consts {
    /**
     * 状态码
     */
    interface SC {
        /** 支付成功 */
        String SUCCESS = "CODE_SUCCESS";
        /** 等待中 */
        String PENDING = "CODE_PENDING";
        /** 支付失败 */
        String FAIL = "CODE_FAIL";
        /** 支付状态未知（异常情况，应该消除） */
        String UNKNOWN = "CODE_UNKNOWN";
        /** 卡密错误 **/
        String CARD_ERROR = "CODE_CARD_ERROR";
        /** 请求错误 */
        String REQ_ERROR = "CODE_REQ_ERROR";
        /** 安全错误 */
        String SECURE_ERROR = "CODE_SECURE_ERROR";
        /** 业务信息错误（如业务不存在，禁用等） */
        String APP_INFO_ERROR = "CODE_APP_INFO_ERROR";
        /** 渠道信息错误（如渠道不存在，禁用等） */
        String CHANNEL_INFO_ERROR = "CODE_CHANNEL_INFO_ERROR";
        /** 外部网络连接错误 */
        String CHANNEL_CONN_ERROR = "CODE_CHANNEL_CONN_ERROR";
        /** 第三方错误 */
        String CHANNEL_ERROR = "CODE_CHANNEL_ERROR";
        /** 数据错误(如数据签名不正确，必选区域为空等) */
        String DATA_ERROR = "CODE_DATA_ERROR";
        /** 数据格式错误 */
        String DATA_FORMAT_ERROR = "CODE_DATA_FORMAT_ERROR";
        /** 内部错误 */
        String INTERNAL_ERROR = "CODE_INTERNAL_ERROR";
        /** 未知错误 */
        String UNKNOWN_ERROR = "CODE_UNKNOWN_ERROR";
        /** 对外连接出错 **/
        String CONN_ERROR = "CODE_CONN_ERROR";

        /** 成功通知 */
        String SUCCESS_NOTIFY = "success";
        /** 订单不存在 */
        String ORDER_NOT_EXIST = "CODE_ORDER_NOT_EXIST";
        /** 退款成功 */
        String REFUND_SUCCESS = "REFUND_SUCCESS";
        /** 退款失败 */
        String REFUND_FAIL = "REFUND_FAIL";
        /** 退款等待中 */
        String REFUND_PENDING = "REFUND_PENDING";
        /** 风险控制 */
        String RISK_ERROR = "RISK_ERROR";

    }

    /**
     * 状态信息
     */
    interface Status {
        String VALID = "valid";
        String INVALID = "invalid";
    }

    /**
     * 支付渠道
     */
    interface Channel {
        /** 支付宝 alipay */
        String ZFB = "Zfb";
        /** 快钱 99bill */
        String KQ = "Kq";
        // 骏网
        String JW = "Jw";
        // 神州付
        String SZF = "Szf";
        String PAYPAL = "paypal";
        // gb,做为本系统内部使用,不公开给第三方业务线.只有gb,balance
        String gb = "gb";
        // 苹果渠道
        String APPLE = "Apple";
        // 360奇虎的渠道
        String QIHU = "Qihu";
        // Sms的渠道
        String SMS = "Sms";
        // 固话VB的渠道
        String VB = "Vb";
        // 盈华讯方Vpay
        String VPAY = "Vpay";
        // 拉卡拉
        String LKLA = "Lkl";
        // 天宏
        String TH = "Th";
        // 易宝
        String YEEPAY = "YeePay";
        // 银联
        String UNIONPAY = "Unionpay";
        // mock渠道
        String MOCK = "mock";
        // 泛联
        String BROADBAND = "Broadband";
    }

    /**
     * 支付方式
     */
    interface PayMethod {
        /** 网关支付 */
        String GATE = "Gate";
        /** 神州行卡支付 */
        String SZX = "Szx";
        /** 联通卡支付 */
        String LT = "Lt";
        /** 移动手机短信支付 */
        String YD = "yd";
        /** 全国电话支付V币支付 */
        String VB = "Vb";
        /** 天宏卡支付 */
        String TH = "Th";
        /** 骏网一卡通支付 */
        String JW = "Jw";
        /* 余额支付 */
        String BALANCE = "Balance";
        // wap余额支付
        String WAPBALANCE = "WapBalance";
        // 电话支付
        String TEL = "Tel";
        /** 一卡通支付 */
        String YKT = "Ykt";
        /** 骏卡支付 */
        String JK = "Jk";
        /** 短信方式 **/
        String SMS = "Sms";
        /** 无线APP **/
        String WAPAPP = "WapApp";
        /** 新泛联天下通 **/
        String TXTONG = "Txtong";
    }

    /**
     * 银行代码
     */
    interface Bank {
        /** 支付宝支付 */
        String ALIPAY = "ALIPAY";
        /** 快钱支付 */
        String KQ = "KQ";
        /** 易宝支付 */
        String YEEPAY = "YEEPAY";
    }

    /**
     * Task相关的配置
     */
    interface Task {
        /** 通知任务最多的重复次数 */
        int RETRY_MAX_TIMES = 10;
        /** 通知任务重复执行间隔基数,秒为单位 */
        int NOTIFY_RETRY_INTERVAL_SECONDS = 30;
        /** 查询任务首次执行的延迟时间,毫秒为单位 */
        int QUERY_RETRY_INIT_DELAY = 8 * 60 * 1000; // 8分钟
        /** 查询任务重复执行间隔基数,秒为单位 */
        int QUERY_RETRY_INTERVAL_SECONDS = 10;
        /** 充gb币的定时任务,秒为单位 */
        int ADD_gb_RETRY_INTERVAL_SECONDS = 30;
        /** 充保证金的定时任务时间间隔，秒为单位 */
        int ADD_DEPOSIT_RETRY_INTERVAL_SECONDS = 30;
        /** 定时任务相等的时间间隔 */
        int EQUAL_INTERVAL_SECONDS = 10;
        /** 首次异步充值延迟时间 */
        int PAY_RETRY_INIT_DELAY = 3 * 1000; // 3秒钟

    }

    /**
     * 返回或者是通知业务时中，HTTP Query String中的字段
     */
    interface Http {
        String PARAM_APP_ID = "appId";
        String PARAM_SIGN = "sign";
        String PARAM_DATA = "data";
    }

    /**
     * gb操作
     */
    interface gbOper {
        String ADD = "a";
        String SUBTRACT = "s";
        String ADD_SUBTRACT = "as";
        String ADD_DEPOSIT = "ad";
        String ADD_CHANNEL_DEPOSIT = "acd";
    }

    String SUCCESS = "success";
    String FAIL = "fail";

    String CHARSET_UTF8 = "utf-8";
    /** "Y"表示此笔支付的支付动作不在本平台发生（如gb教育） */
    String ORPHANREFUND = "Y";

    String REFUND = "Refund";

    String APPID = "appId";
    String APP_ORDER_ID = "appOrderId";
    String SIGN = "sign";
    String DATA = "data";
    String RETURNURL = "returnUrl";
    String CARDPASS = "cardPass";
    String AUTOREDIRECT = "autoRedirect";
    String TRUE = "true";
    String EQ = "=";
    String AMP = "&";
    String CONNECT = "||";
    String CHARSET_GBK = "gbk";
    String CHARSET_GB_2312 = "gb2312";
    String SUBJECT_NAME = "欢聚时代支付";
    String gbUID = "gbuid";
    String TEL = "tel";
    String DELIMITER = "-";
    String SUCCDESS_DES = "支付成功";
    String FAIL_DES = "支付失败 具体原因联系支付平台童鞋";
    String PENDING_DES = "支付中";
}
