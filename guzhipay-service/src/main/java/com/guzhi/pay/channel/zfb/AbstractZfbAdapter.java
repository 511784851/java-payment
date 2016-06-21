package com.guzhi.pay.channel.zfb;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 支付宝
 * 
 * @author jiangnan@chinaduo.com
 * @author duanyuenfeng 2013-02-22
 * @author administrator 2013-04-02
 */
public class AbstractZfbAdapter extends AbstractChannelIF {

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private static final Logger LOG = LoggerFactory.getLogger(AbstractZfbAdapter.class);
    private static final String CH_ACCOUNT_Id = "2088001403805263";

    @Override
    public String status() {
        // throw new RuntimeException("not implemented yet");
        return "success";
    }

    @Override
    public PayOrder pay(PayOrder order) {
        LOG.info("[AbstractZfbAdapter.pay] start paying.payOrder:{}", order, TraceHelper.getTrace(order));

        // basic info
        AppChInfo appChInfo = order.getAppChInfo();
        String timeStampKey = queryTimeStamp(appChInfo); // 防钓鱼时间戳

        // gen chOrderId
        String chOrderId = order.getChOrderId();
        if (StringUtils.isBlank(chOrderId)) {
            chOrderId = OrderIdHelper.genChOrderId(order.getAppId(), order.getAppOrderId());
            order.setChOrderId(chOrderId);
        }

        // assemble pay info
        Map<String, String> request = new HashMap<String, String>();
        request.put(ZfbConsts.KEY_SERVICE, ZfbConsts.SERVICE_DIRECT_PAY_BY_USER); // 服务名
        request.put(ZfbConsts.KEY_INPUT_CHARSET, ZfbConsts.CHARSET_UTF8);
        request.put(ZfbConsts.KEY_PARTNER, appChInfo.getChAccountId()); // 商家ID
        // request.put(ZfbConsts.KEY_NOTIFY_URL, ZfbConsts.ADDR_YYPAY_NOTIFY);
        request.put(ZfbConsts.KEY_NOTIFY_URL, UrlHelper.removeLastSep(getPayNotify()) + ZfbConsts.ADDR_YYPAY_NOTIFY);
        request.put(ZfbConsts.KEY_OUT_TRADE_NO, chOrderId); // 商户订单号
        request.put(ZfbConsts.KEY_SUBJECT, ZfbHelper.getSubjectName(order)); // 名称
        request.put(ZfbConsts.KEY_BODY, "Desc: " + order.getProdDesc()); // 商品描述
        request.put(ZfbConsts.KEY_PAYMENT_TYPE, ZfbConsts.PAYMENT_TYPE_BUY_PRODUCT); // 商品类型
        request.put(ZfbConsts.KEY_TOTAL_FEE, decimalFormat.format(order.getAmount()));
        request.put(ZfbConsts.KEY_PAY_METHOD, ZfbHelper.translatePayMethod(order.getPayMethod())); // 支付方式
        request.put(ZfbConsts.KEY_SELLER_EMAIL, appChInfo.getChAccountName()); // 卖家账号
        request.put(ZfbConsts.KEY_TIMESTAMP, timeStampKey);
        // 因为 存在代理或移动运营商改ip，最后先不传
        // request.put(ZfbConsts.KEY_EXTER_INVOKE_IP, order.getUserIp());
        if (CH_ACCOUNT_Id.equals(order.getAppChInfo().getChAccountId())) {
            request.put(ZfbConsts.KEY_EXTER_INVOKE_IP, order.getUserIp());
        }
        if (StringUtils.isNotBlank(order.getBankId()) && !ZfbConsts.BANK_ALIPAY.equals(order.getBankId())) {
            request.put(ZfbConsts.KEY_DEFAULT_BANK, order.getBankId()); // TODO:
                                                                        // need
                                                                        // translate?
        }
        if (StringUtils.isNotBlank(order.getReturnUrl())) {
            request.put(ZfbConsts.KEY_RETURN_URL, UrlHelper.removeLastSep(getPayNotify())
                    + ZfbConsts.ADDR_YYPAY_RETURN);
        }

        // gen sign
        String signMsg = ZfbHelper.genSign(request, appChInfo.getChPayKeyMd5());
        request.put(ZfbConsts.KEY_SIGN, signMsg);
        request.put(ZfbConsts.KEY_SIGN_TYPE, ZfbConsts.ZFB_SIGN_TYPE_MD5);

        // gen payUrl
        String payUrl = UrlHelper.removeLastSep(ZfbConsts.ADDR_GATEWAY) + "?" + ZfbHelper.assembleQueryStr(request);
        order.setPayUrl(payUrl);
        order.setStatusCode(Consts.SC.PENDING);
        order.setStatusMsg("等待用户支付，或等待支付宝通知");

        LOG.info("[AbstractZfbAdapter.pay] create pay url successfully.payOrder: {}", order,
                TraceHelper.getTrace(order));
        return order;
    }

    /**
     * 拿防钓鱼时间戳
     */
    private String queryTimeStamp(AppChInfo appChInfo) {
        Map<String, String> param = new HashMap<String, String>();
        param.put(ZfbConsts.KEY_SERVICE, ZfbConsts.SERVICE_QUERY_TIMESTAMP); // 服务名
        param.put(ZfbConsts.KEY_PARTNER, appChInfo.getChAccountId()); // 商户ID

        String reqUrl = UrlHelper.removeLastSep(ZfbConsts.ADDR_GATEWAY) + "?" + ZfbHelper.assembleQueryStr(param);

        String result = null;

        try {
            String respStr = HttpClientHelper.sendRequest(reqUrl, ZfbConsts.CHARSET_UTF8);
            result = ZfbHelper.parseQueryTimestampResponse(respStr);
        } catch (Exception e) {
            // 第一次获取失败，100ms后，重新获取
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e1) {
            }
            String respStr = HttpClientHelper.sendRequest(reqUrl, ZfbConsts.CHARSET_UTF8);
            result = ZfbHelper.parseQueryTimestampResponse(respStr);
        }
        return result;
    }

    /**
     * 查询订单结果。<br>
     * 从支付宝提供的文档来看，找不到查询接口相关的文档了。手头仅有v1.1，怀疑（但不确定）后续是否不建议用了（待确认）
     */
    @Override
    public PayOrder query(PayOrder order) {
        LOG.info("[AbstractZfbAdapter.query] start querying payOrder:{}", order, TraceHelper.getTrace(order));
        // basic info
        AppChInfo appChInfo = order.getAppChInfo();
        String md5Key = appChInfo.getChPayKeyMd5();

        // assemble query
        Map<String, String> request = new HashMap<String, String>();
        request.put(ZfbConsts.KEY_SERVICE, ZfbConsts.SERVICE_SINGLE_TRADE_QUERY); // 服务名
        request.put(ZfbConsts.KEY_PARTNER, appChInfo.getChAccountId()); // 服务名
        request.put(ZfbConsts.KEY_INPUT_CHARSET, ZfbConsts.CHARSET_UTF8); // 字符集编码
        request.put(ZfbConsts.KEY_OUT_TRADE_NO, order.getChOrderId()); // 可改进，支付宝建议用chOrderId查询效率高些，已经有时，可以考虑使用
        request.put(ZfbConsts.KEY_TRADE_NO, order.getChDealId());

        // sign
        String signMsg = ZfbHelper.genSign(request, md5Key);
        request.put(ZfbConsts.KEY_SIGN, signMsg);
        request.put(ZfbConsts.KEY_SIGN_TYPE, ZfbConsts.ZFB_SIGN_TYPE_MD5);

        // 构造查询地址
        String reqUrl = UrlHelper.removeLastSep(ZfbConsts.ADDR_GATEWAY) + "?" + ZfbHelper.assembleQueryStr(request);
        LOG.info("[AbstractZfbAdapter.query] reqUrl:{}", reqUrl, TraceHelper.getTrace(order));
        String respStr = HttpClientHelper.sendRequest(reqUrl, ZfbConsts.CHARSET_UTF8);

        // 更新结果
        ZfbHelper.updatePayOrderByQuery(order, respStr);

        return order;
    }

    /**
     * 支付退款处理接口
     * 
     * @param payOrder
     */
    public PayOrder refund(PayOrder payOrder) {
        LOG.info("[AbstractZfbAdapter.refund] with PayOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        AppChInfo appChInfo = payOrder.getAppChInfo();
        String md5Key = appChInfo.getChPayKeyMd5();
        Map<String, String> request = new HashMap<String, String>();
        request.put(ZfbConsts.KEY_SERVICE, ZfbConsts.REFUND_FASTPAY_BY_PLATFORM_PWD); // 接口名称
        request.put(ZfbConsts.KEY_PARTNER, appChInfo.getChAccountId()); // 合作者身份ID
        request.put(ZfbConsts.KEY_INPUT_CHARSET, ZfbConsts.CHARSET_UTF8);// 字符编码
        request.put(ZfbConsts.KEY_SIGN_TYPE, ZfbConsts.ZFB_SIGN_TYPE_MD5);// 签名方式
        // request.put(ZfbConsts.KEY_NOTIFY_URL,
        // ZfbConsts.ADDR_YYPAY_NOTIFY);//异步通知商户地址
        request.put(ZfbConsts.KEY_NOTIFY_URL, UrlHelper.removeLastSep(getPayNotify()) + ZfbConsts.ADDR_YYPAY_NOTIFY);// 异步通知商户地址
        request.put(ZfbConsts.KEY_REFUND_DATE, TimeHelper.get(1, TimeHelper.str2Date(payOrder.getAppRefundTime())));// 退款请求时间
        request.put(ZfbConsts.KEY_BATCH_NO, TimeHelper.get(8, new Date()) + payOrder.getAppOrderId().substring(8));// 退款批次号
        request.put(ZfbConsts.KEY_BATCH_NUM, ZfbConsts.KEY_NUM); // 退款总笔数

        String detail_data = String.format("%s^%s^%s", payOrder.getChDealId(),
                new BigDecimal(payOrder.getRefundAmount()).setScale(2, BigDecimal.ROUND_HALF_UP),
                payOrder.getRefundDesc()); // 单笔数据集
        request.put(ZfbConsts.KEY_DETAIL_DATA, detail_data);
        String signMsg = ZfbHelper.genSign(request, md5Key);
        request.put(ZfbConsts.KEY_SIGN, signMsg);
        request.put(ZfbConsts.KEY_SIGN_TYPE, ZfbConsts.ZFB_SIGN_TYPE_MD5);
        // 构造查询地址
        String reqUrl = UrlHelper.removeLastSep(ZfbConsts.ADDR_GATEWAY) + "?" + ZfbHelper.assembleQueryStr(request);
        String respStr = HttpClientHelper.sendRequest(reqUrl, ZfbConsts.CHARSET_UTF8);
        // 更新结果
        ZfbHelper.updatePayOrderByRefund(payOrder, respStr);
        return payOrder;
    }

}
