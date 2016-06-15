package com.guzhi.pay.channel.zfb;

/**
 * 支付宝参数常量Key
 * 
 * @author zhaoming@chinaduo.com 2012-6-8 下午03:13:29
 * @author administrator
 */
public interface ZfbConsts {
    static final int MAX_OUT_TRADE_NO_LEN = 64;
    static final String CHARSET_UTF8 = "utf-8";

    /** 地址（URL），作为可配置项更合理，考虑到实际上基本几年都不会有变化，暂时先写这里。后续可考虑改进 */
    static final String ADDR_GATEWAY = "https://mapi.alipay.com/gateway.do";
    // 支付宝手机网页即时到接口地址
    static final String ADD_WAP_PAY = "http://wappaygw.alipay.com/service/rest.htm?";
    static final String ADDR_guzhiPay_NOTIFY = "/ch/notify/zfb.do";
    static final String ADDR_guzhiPay_RETURN = "/ch/return/zfb.do";
    static final String ADDR_WAP_guzhiPay_NOTIFY = "/ch/notify/zfbwap.do";
    static final String ADDR_WAP_guzhiPay_RETURN = "/ch/return/zfbwap.do";
    static final String ADDR_WAP_APP_guzhiPay_NOTIFY = "/ch/notify/zfbwapapp.do";
    static final String ADDR_NOTIFY_VERIFY_PATTERN = ADDR_GATEWAY + "?service=notify_verify&partner=%s&notify_id=%s";

    /** 接口名称 */
    static final String KEY_SERVICE = "service";
    /** 合作伙伴ID */
    static final String KEY_PARTNER = "partner";
    /** 通知 URL */
    static final String KEY_NOTIFY_URL = "notify_url";
    /** 返回 URL */
    static final String KEY_RETURN_URL = "return_url";
    /** 代理商ID */
    static final String KEY_AGENT = "agent";
    /** 阐述编码字符集 */
    static final String KEY_INPUT_CHARSET = "_input_charset";

    /** 支付类型 */
    static final String KEY_PAYMENT_TYPE = "payment_type";
    /** 网银流水号 */
    static final String KEY_BANK_SEQ_NO = "bank_seq_no";
    /** 实际使用的银行渠道 */
    static final String KEY_OUT_CHANNEL_INST = "out_channel_inst";

    /** 错误代号 */
    static final String KEY_ERROR_CODE = "error_code";

    /** 商户回传参数 */
    static final String KEY_EXTRA_COMMON_PARAM = "extra_common_param";

    // TODO 通知
    /** 通知类型 */
    static final String KEY_NOTIFY_TYPE = "notify_type";
    /** 通知ID */
    static final String KEY_NOTIFY_ID = "notify_id";
    /** 通知时间 */
    static final String KEY_NOTIFY_TIME = "notify_time";

    // TODO 通知验证
    /** 通知任务 ID */
    static final String KEY_MSG_ID = "msg_id";
    /** 交易卖家Id */
    static final String KEY_SELLER_ID = "seller_id";
    /** 交易卖家Email */
    static final String KEY_SELLER_EMAIL = "seller_email";
    /** 支付宝订单号 */
    static final String KEY_TRADE_NO = "trade_no";
    /** 默认银行代号 */
    static final String KEY_DEFAULT_BANK = "defaultbank";
    /** 调用者IP */
    static final String KEY_EXTER_INVOKE_IP = "exter_invoke_ip";
    /** 时间戳 */
    static final String KEY_TIMESTAMP = "anti_phishing_key";

    // TODO 业务参数
    /** 商品名称 */
    static final String KEY_SUBJECT = "subject";
    /** 商品描述 */
    static final String KEY_BODY = "body";
    /** 商户系统交易号 */
    static final String KEY_OUT_TRADE_NO = "out_trade_no";
    /** 交易结果 */
    static final String KEY_TRADE_STATUS = "trade_status";
    /** 商品单价 */
    static final String KEY_PRICE = "price";
    /** 购买数量 */
    static final String KEY_QUANTITY = "quantity";
    /** 商品展示网址 */
    static final String KEY_SHOW_URL = "show_url";
    /** 总价 */
    static final String KEY_TOTAL_FEE = "total_fee";
    /** 默认登录 */
    static final String KEY_DEFAULT_LOGIN = "default_login";
    /** 支付方式 */
    static final String KEY_PAY_METHOD = "paymethod";
    /** 创建订单时间 */
    static final String KEY_GMT_CREATE = "gmt_create";
    /** 支付订单时间 */
    static final String KEY_GMT_PAYMENT = "gmt_payment";

    // TODO 公用
    /** 签名 */
    static final String KEY_SIGN = "sign";
    /** 签名类型 */
    static final String KEY_SIGN_TYPE = "sign_type";
    /** 加密方式 MD5 */
    static final String ZFB_SIGN_TYPE_MD5 = "MD5";
    /** 支付网关服务名 */
    static final String SERVICE_DIRECT_PAY_BY_USER = "create_direct_pay_by_user";
    /** 查询单个订单服务 */
    static final String SERVICE_SINGLE_TRADE_QUERY = "single_trade_query";
    /** 支付宝退款请求 */
    static final String REFUND_FASTPAY_BY_PLATFORM_PWD = "refund_fastpay_by_platform_nopwd";
    /** 获得时间戳服务 */
    static final String SERVICE_QUERY_TIMESTAMP = "query_timestamp";
    /** 支付类型 - 商品购买 */
    static final String PAYMENT_TYPE_BUY_PRODUCT = "1";
    /** 默认不使用自动登录支付宝功能 */
    static final String DEFAULT_LOGIN = "Y";

    /** 余额支付代号 */
    static final String PAYMETHOD_DIRECT_PAY_CODE = "12";
    /** 余额支付值 */
    static final String PAYMETHOD_DIRECT_PAY_VALUE = "directPay";

    /** 网银支付代号 */
    static final String PAYMETHOD_BANK_PAY_CODE = "10";
    /** 网银支付值 */
    static final String PAYMETHOD_BANK_PAY_VALUE = "bankPay";
    /** 便捷支付代号 */
    static final String PAYMETHOD_MOTO_PAY_CODE = "11";
    /** 便捷支付值 */
    static final String PAYMETHOD_MOTO_PAY_VALUE = "motoPay";

    /** 支付宝支付成功时的返回代号 */
    static final String SUCCESS_RESULT_CODE = "T";
    /** 支付宝退款失败时的返回代号 */
    static final String FAIL_RESULT_CODE = "F";
    /** 支付宝退款处理中的返回代号 */
    static final String P_RESULT_CODE = "P";
    /** 支付宝位置错误 */
    static final String UNKNOWN_ERROR = "UNKOWN_ERROR";
    /** 交易代号:支付成功 */
    static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    /** 交易代号:支付关闭 */
    static final String TRADE_CLOSED = "TRADE_CLOSED";
    /** 交易代号:支付完成 */
    static final String TRADE_FINISHED = "TRADE_FINISHED";
    /** 交易代号:支付中 */
    static final String TRADE_PENDING = "TRADE_PENDING";
    /** 订单存在 */
    static final String TRADE_NOT_EXISTS = "TRADE_NOT_EXIST";
    /** 交易代号:等待买家付款 */
    static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    /** 等待支付宝退款 */
    static final String WAIT_ALIPAY_REFUND = "WAIT_ALIPAY_REFUND";
    /** 结束退款 */
    static final String OVERED_REFUND = "OVERED_REFUND";
    /** 交易已关闭，不允许退款 */
    static final String TRADE_HAS_CLOSED = "TRADE_HAS_CLOSED";

    /** 网银支付成功返回代号 */
    static final String PAY_RESULT_SUCCESS = "10";
    /** 网银支付失败返回代号 */
    static final String PAY_RESULT_ERROR = "12";

    /** 查询是否支付成功节点XPATH查询语句 */
    static final String QUERY_IS_SUCCESS_NODE = "/alipay/is_success";
    /** 查询交易信息节点XPATH查询语句 */
    static final String QUERY_TRADE_NODE = "/alipay/response/trade";
    /** 查询错误节点XPATH查询语句 */
    static final String QUERY_ERROR_NODE = "/alipay/error";
    /** 查询交易状态节点XPATH查询语句 */
    static final String QUERY_TRADE_STATUS_NODE = "trade_status";
    /** 查询交易流水号节点XPATH查询语句 */
    static final String QUERY_TRADE_NO_NODE = "trade_no";
    /** 查询交易订单号节点XPATH查询语句 */
    static final String QUERY_OUT_TRADE_NO_NODE = "out_trade_no";
    /** 查询总金额节点XPATH查询语句 */
    static final String QUERY_TOTAL_FEE_NODE = "total_fee";
    /** 查询支付成功时间节点XPATH查询语句 */
    static final String QUERY_GMT_PAYMENT_NODE = "gmt_payment";
    /** 查询订单创建节点XPATH查询语句 */
    static final String QUERY_GMT_CREATE_NODE = "gmt_create";
    /** 查询商户号节点XPATH查询语句 */
    static final String QUERY_SELLER_ID_NODE = "seller_id";
    /** 查询支付类型节点XPATH查询语句 */
    static final String QUERY_PAYMENT_TYPE_NODE = "payment_type";
    /** 查询时间戳的结果节点TAG */
    static final String QUERY_TIMESTAMP_RESULT_NODE = "/alipay/response/timestamp/encrypt_key";

    /** 回调处理成功时返回的结果 */
    static final String NOTIFY_HANDLE_RESULT_SUCCESS = "success";
    /** 回调处理失败时返回的结果 */
    static final String NOTIFY_HANDLE_RESULT_FAILED = "failed";

    /** 选用支付宝余额支付方式 */
    static final String BANK_ALIPAY = "ALIPAY";

    /** 支付是否成功 */
    static final String ZFB_QUERY_IS_SUCCESS = "is_success";

    /** 查询错误 */
    static final String ZFB_QUERY_ERROR = "error";

    static final String ZFB_QUERY_RUQUEST = "request";

    static final String ZFB_QUERY_RESPONSE = "response";

    static final String ZFB_QUERY_NAME = "name";

    static final String ZFB_QUERY_TRADE = "trade";
    /** 退款请求时间 */
    static final String KEY_REFUND_DATE = "refund_date";
    /** 退款批次号 */
    static final String KEY_BATCH_NO = "batch_no";
    /** 总笔数 */
    static final String KEY_BATCH_NUM = "batch_num";
    /** 单笔数据集 */
    static final String KEY_DETAIL_DATA = "detail_data";
    /** 退款总笔数 */
    static final String KEY_NUM = "1";
    /** 退款状态 */
    static final String KEY_REFUND_STATUS = "refund_status";
    /** 退款通知 */
    static final String NOTIFY_TYPE_REFUND = "batch_refund_notify";

    static final String KEY_RESULT_DETAILS = "result_details";

    static final String AMP = "&";

    static final String EQ = "=";

    /**
     * wap接口相关的变量
     * 
     * @author administrator
     * 
     */
    interface Wap {
        String TOKEN_SERVER = "alipay.wap.trade.create.direct";
        String EXECUTE_SERVER = "alipay.wap.auth.authAndExecute";
        String FORMAT = "format";
        String FORMAT_VALUE = "xml";
        String VERSION = "v";
        String VERSION_VALUE = "2.0";
        String REQ_ID = "req_id";
        String SEC_ID = "sec_id";
        String REQ_DATA = "req_data";
        String SELLER_ACCOUNT_NAME = "seller_account_name";
        String CALL_BACK_URL = "call_back_url";
        String DIRECT_TRADE_CREATE_REQ = "direct_trade_create_req";
        String XML1 = "<";
        String XML2 = ">";
        String XML3 = "</";
        String REQUEST_TOKEN = "request_token";
        String AUTH_AND_EXECUTE_REQ = "auth_and_execute_req";
        String RES_DATA = "res_data";
        String RES_ERROR = "res_error";
        String NOTFIY_DATA = "notify_data";
        String ADD_CALL_BACK_URL = "";
        String RESULT = "result";
    }

    interface WapApp {
        // 数据库中保存的支付平台的pkcs8私钥文件的路径名
        static final String PRIVATE_KEY_PATH = "zfbwapapp_private_file_path";
        // 数据库中保存的支付宝的公钥文件的路径名
        static final String PUBLIC_KEY_PATH = "zfbwapapp_public_file_path";

        // 商户（RSA）私钥
        public static final String RSA_PRIVATE_PATH = "channel/zfb/wapapp/5101200070003100001.pfx";
        // 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
        public static final String RSA_ALIPAY_PUBLIC_PATH = "channel/zfb/wapapp/5101200070003100001.pfx";

        /** 合作伙伴ID */
        static final String KEY_PARTNER = "partner";
        /** selle ID */
        static final String KEY_SELLER = "seller";
        /** 原payUrl中的notifyUrl */
        static final String KEY_NOTIFY_URL = "notify_url";
        /** 对支付宝系统来讲,业务线的唯一订单ID */
        static final String KEY_OUT_TRADE_NO = "out_trade_no";
        static final String SERVICE_MOBILE_SECURITYPAY = "mobile.securitypay.pay";
        static final String DEFAULT_SIGN_TYPE = "RSA";
        /**
         * 支付宝APP同步返回值中，支付成功参数名
         */
        static final String KEY_SUCCESS = "success";
        /**
         * 支付宝APP同步返回中，支付成功标识
         */
        static final String SUCCESS_FLAG = "true";
        // 服务端异步回调
        static final String KEY_SIGN = "sign";
        static final String KEY_NOTIFY_DATA = "notify_data";
        static final String KEY_ELE_ROOT = "notify";
        static final String KEY_NOTIFY_REG_TIME = "notify_reg_time";

        /**
         * 下面是支付宝返回给客户端的参数
         */
        static final String KEY_RESULT_STATUS = "resultStatus";
        static final String KEY_MEMO = "memo";
        static final String KEY_RESULT = "result";

        /**
         * 下面是支付宝返回给客户端的状态码，对应的参数名是"resultStatus"
         */
        static final String RESULT_STATUS_SUCCESS = "9000";// 订单支付成功
        static final String RESULT_STATUS_PENDING = "8000";// 正在处理中
        static final String RESULT_STATUS_FAIL = "4000";// 订单支付失败
        static final String RESULT_STATUS_CANCEL = "6001";// 用户中途取消[与支付失败不做区分]
        static final String RESULT_STATUS_CONN_ERROR = "6002";// 网络连接出错[与支付失败不做区分]

    }
}
