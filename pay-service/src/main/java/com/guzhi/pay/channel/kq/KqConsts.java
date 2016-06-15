package com.guzhi.pay.channel.kq;

/**
 * 与快钱接口通信需要用到的字符串。
 * 
 * @author yangpeng
 * @author administrator
 */
public interface KqConsts {
    String CHARSET_UTF8 = "utf-8";
    int MAX_ORDER_ID_LEN = 30;

    /** 快钱webservice地址 */
    static final String ADDR_WEBSERVICE_QUERY = "/szx_gateway/services/szxGatewayPayOrderQuery";

    /** 银行直连地址（URL） */
    static final String ADDR_PAY = "/gateway/recvMerchantInfoAction.htm"; // "https://www.99bill.com/gateway/recvMerchantInfoAction.htm";
    static final String ADDR_QUERY = "/apipay/services/gatewayOrderQuery?wsdl"; // "https://sandbox.99bill.com/apipay/services/gatewayOrderQuery";
    static final String ADDR_guzhiPay_NOTIFY = "/ch/notify/kq.do";
    static final String ADDR_guzhiPay_RETURN = "/ch/return/kq.do";
    static final String ADDR_TIMESTAMP_QUERY = "http://www.99bill.com/gateway/getOrderTimestamp.htm";

    /** 密码以及证书地址 */
    // static final String PASSWORD = "123456"; // "duowan123";
    /** 快钱非对称加密算法证书PFX文件名称 */
    // public static final String SECRET_KEY_FILE_NAME =
    // "channel/kq/gb-rsa.pfx"; // "src/main/resources/gb-rsa.pfx";
    // public static final String SECRET_KEY_FILE_NAME =
    // "/data/var/payplf-tpay.gb.com/certifications/gb-rsa.pfx";
    /** 快钱非对称加密算法证书CER文件名称 */
    // public static final String PUBLIC_KEY_FILE_NAME =
    // "channel/kq/public-rsa-gb.cer"; //
    // "src/main/resources/public-rsa-gb.cer";
    // public static final String PUBLIC_KEY_FILE_NAME =
    // "/data/var/payplf-tpay.gb.com/certifications/public-rsa-gb.cer";

    String KEY_PUBLIC_KEY_FILE_PATH = "kq_cer_file_path";
    String KEY_PRIVATE_KEY_FILE_PATH = "kq_pfx_file_path";

    /** 卡密支付URL */
    static final String ADDR_CARD_PAY = "/szxgateway/recvMerchantInfoAction.htm";
    static final String ADDR_guzhiPay_CARD_NOTIFY = "/ch/notify/kq/card.do";
    static final String ADDR_guzhiPay_CARD_RETURN = "/ch/return/kq/card.do";

    // 快钱支付结果代码
    /** 支付成功 */
    String PAY_RESULT_SUCCESS = "10";
    /** 支付失败 */
    String PAY_RESULT_FAIL = "11";
    /** 快钱的一些默认值 */
    String KQ_INPUT_CHARSET_UTF8 = "1";// 字符集 固定值：1 1代表UTF-8
    String KQ_VERSION_2 = "v2.0";// 快钱版本 固定值：v2.0注意为小写字母
    String KQ_VERSION_2_1 = "v2.1";// 快钱版本 固定值：v2.1注意为小写字母
    String KQ_LANGUAGE_CHN = "1";// 1为中文
    String KQ_SIGN_TYPE_MD5 = "1";// PKI
    String KQ_SIGN_TYPE_PKI = "4";// PKI
    String KQ_PAY_TYPE_BANK = "10";// 10：银行卡支付（网关支付页面只显示银行卡支付）
    String KQ_PAY_TYPE_DIRECT = "52";// 卡密直连支付
    String KQ_PAY_TYPE_REDIRECT = "42";// 跳转快钱页面支付
    String KQ_REDO_FLAG_NOT_REPEAT = "1";// 0表示同一订单号在没有支付成功的前提下可重复提交多次,1表示不能重复提交多次。
    String KQ_QUERY_MODE = "1";// 简单结果信息
    String KQ_QUERY_TYPE_ORDER = "0";// 按订单查
    String KQ_QUERY_TYPE_TIME = "1";// 按时间查
    // 神州行支付的默认值
    String KQ_CARD_PAY_TYPE = "52";
    String KQ_FULL_AMOUNT_FLAG_TRUE = "1";// 充值卡全额支付
    String KQ_FULL_AMOUNT_FLAG_FALSE = "0";// 充值卡非全额支付

    // 神州行卡
    String KQ_SZX_CARD_PAY_TYPE = "52";
    String KQ_SZX_BOSS_TYPE = "0";// bossType 神州行卡
    // 盛大卡支付
    String KQ_SD_CARD_PAY_TYPE = "42";
    String KQ_SD_BOSS_TYPE = "10";// bossType 盛大卡
    // 网易卡支付
    String KQ_WY_CARD_PAY_TYPE = "42";
    String KQ_WY_BOSS_TYPE = "14";// bossType 网易卡

    /**
     * 查询时使用到的MD5密钥
     */
    String KQ_QUERY_KEY = "queryKey";

    /** 快钱的参数key值 ,参考快钱文档 */
    String KEY_INPUTCHARSET = "inputCharset";
    String KEY_PAGEURL = "pageUrl";
    String KEY_BGURL = "bgUrl";
    String KEY_VERSION = "version";
    String KEY_LANGUAGE = "language";
    String KEY_SIGNTYPE = "signType";
    String KEY_MERCHANTACCTID = "merchantAcctId";
    String KEY_PAYERNAME = "payerName";
    String KEY_PAYERCONTACTTYPE = "payerContactType";
    String KEY_PAYERCONTACT = "payerContact";
    String KEY_ORDERID = "orderId";
    String KEY_ORDERAMOUNT = "orderAmount";
    String KEY_ORDERTIME = "orderTime";
    String KEY_ORDERTIMESTAMP = "orderTimestamp";
    String KEY_PRODUCTNAME = "productName";
    String KEY_PRODUCTNUM = "productNum";
    String KEY_PRODUCTID = "productId";
    String KEY_PRODUCTDESC = "productDesc";
    String KEY_PAYERIP = "payerIP";
    String KEY_EXT1 = "ext1";
    String KEY_EXT2 = "ext2";
    String KEY_PAYTYPE = "payType";
    String KEY_BANKID = "bankId";
    String KEY_REDOFLAG = "redoFlag";
    String KEY_PID = "pid";
    String KEY_KEY = "key";
    String KEY_SIGN_MSG = "signMsg";

    // 神州行卡支付的一些常量
    String KEY_CARD_NUM = "cardNumber";
    String KEY_CARD_PWD = "cardPwd";
    String KEY_PAY_TYPE = "payType";
    String KEY_FULL_AMOUNT_FLAG = "fullAmountFlag";
    String KEY_BOSS_TYPE = "bossType";

    // 支付结果
    String KEY_DEALID = "dealId";
    String KEY_BANK_DEALID = "bankDealId";
    String KEY_DEALTIME = "dealTime";
    String KEY_PAYAMOUNT = "payAmount";
    String KEY_FEE = "fee";
    String KEY_PAYRESULT = "payResult";
    String KEY_ERRCODE = "errCode";
    String CARD_FAIL_CODE = "cardfailCode";

    String KEY_BILL_ORDERTIME = "billOrderTime";
    String KEY_RECEIVE_BOSSTYPE = "receiveBossType";
    String KEY_RECEIVE_ACCTID = "receiverAcctId";

    // 查询的字段
    String KEY_QUERYTYPE = "queryType";
    String KEY_QUERYMODE = "queryMode";
    String KEY_STARTTIME = "startTime";
    String KEY_ENDTIME = "endTime";
    String KEY_REQUESTPAGE = "requestPage";
    String KEY_CURRENTPAGE = "currentPage";
    String KEY_PAGECOUNT = "pageCount";
    String KEY_PAGESIZE = "pageSize";
    String KEY_RECORDCOUNT = "recordCount";

    // 错误代码
    String ERROR_CODE_EMPTY = "";
    String ERROR_CODE_31001 = "31001";
    String ERROR_CODE_31002 = "31002";
    /** 默认的时间戳查询地址 */
    String DEFAULT_TIME_STAMPE_QUERY_URL = "http://www.99bill.com/gateway/getOrderTimestamp.htm";

    String KEY_WORD = "<timestamp>";

    String NOTIFY_RESULT_SUCCESS = "<result>1</result>";

    String NOTIFY_RESULT_FAIL = "<result>0</result>";

    String REDIRECTURL = "<redirecturl>";

    String UREDIRECTURL = "</redirecturl>";

    String SANDBOX = "sandbox";

    String RETURNCODE = "returncode";

    String CARD_REQ_SUCCESS = "120";

    String CARD_REQ_ERROR = "30019";
}
