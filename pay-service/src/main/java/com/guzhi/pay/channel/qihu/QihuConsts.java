/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.qihu;

/**
 * @author administrator
 * 
 */
public interface QihuConsts {

    static final String CHARSET_UTF8 = "UTF-8";
    static final String FORMAT_XML = "xml";
    static final String PAY_TIMEOUT_SET = "10";
    static final String ADDR_guzhiPay_NOTIFY = "/ch/notify/qihu.do";
    static final String ADDR_guzhiPay_RETURN = "/ch/return/qihu.do";

    // TODO
    static final String ADDR_GATEWAY_PAY = "https://api.360pay.cn/gateway/do";
    static final String ADDR_GATEWAY_QUERY = "http://query.mpay.360.cn/trans/get";
    static final String ADDR_GATEWAY_RETURN = "http://mpay.360.cn/noReturn/return";

    // key
    String KEY_MER_CODE = "mer_code";
    String KEY_MER_TRADE_CODE = "mer_trade_code";
    String KEY_TRANS_SERVICE = "trans_service";
    String KEY_INPUT_CHA = "input_cha";
    String KEY_SIGN_TYPE = "sign_type";
    String KEY_SIGN = "sign";
    String KEY_NOTIFY_URL = "notify_url";
    String KEY_RETURN_URL = "return_url";
    String KEY_PRODUCT_NAME = "product_name";
    String KEY_REC_AMOUNT = "rec_amount";
    String KEY_PRODUCT_DESCT = "product_desc";
    String KEY_CLIENT_IP = "client_ip";
    String KEY_TIMEOUT_SET = "timeout_set";
    String KEY_BANK_CODE = "bank_code";
    String KEY_MER_ORDER_TIME = "mer_order_time";

    // value　const
    String VAlUE_TRANS_SERVICE = "direct_pay";
    String VALUE_SIGN_TYPE = "MD5";

    // notify const
    /** 回调处理成功时返回的结果 */
    static final String NOTIFY_HANDLE_RESULT_SUCCESS = "success";
    /** 回调处理失败时返回的结果 */
    static final String NOTIFY_HANDLE_RESULT_FAILED = "failed";
    /** 奇虎的流水号 */
    String KEY_INNER_TRADE_CODE = "inner_trade_code";
    /** 银行的流水号 */
    String KEY_BANK_TRADE_CODE = "bank_trade_code";
    /** 总体的状态 */
    String KEY_BANK_PAY_FLAG = "bank_pay_flag";
    /** 银行的处理时间 */
    String KEY_PAY_TIME = "pay_time";

    // query
    String KEY_OUT_FORMAT = "out_format";
    // query response
    String KEY_RESULT_CODE = "result_code";
    String KEY_RESULT_MSG = "result_msg";
    String KEY_RECORD = "record";
    String KEY_TRANS_STATUS = "trans_status";
    /** 银行的处理时间 */
    String KEY_PAY_RET_TIME = "pay_ret_time";

    // query value
    String KEY_QUERY_RESULT_SUCCESS = "0000";

    // 状态
    static final String TRADE_SUCCESS = "success";
    static final String TRADE_FAIL = "failed";
    static final String QUERY_RESULT_SUCCESS = "S";
    static final String QUERY_RESULT_PENDING = "W";
    static final String QUERY_RESULT_FAIL = "F";

}
