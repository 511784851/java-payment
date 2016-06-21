package com.guzhi.pay.channel.zfb;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.SecureHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;

/**
 * 支付宝帮助类
 * 
 * @author jgnan
 * @author administrator 2013-04-02
 */
public class ZfbHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ZfbHelper.class);
    private static final String EQ = "=";
    private static final String AMP = "&";
    private static final String QUOTE = "\"";
    private static final String EMPTYSTRING = "";

    // private static final SAXReader READER = new SAXReader(false);

    /**
     * 根据支付宝加密要求生成加密字符串:
     * 假设请求中有: _input_charset=utf8, body=test, service=user_direct_pay,
     * quantity=1, sign_type=MD5
     * 需要首先把请求(除了sign和sign_type)参数按照首字母排序,然后通过&连接，如果没有值则不要进行这个步骤！！
     * params="_input_charset=utf8&body=test&quantity=1&service=user_direct_pay"
     * 然后直接在后面加上加密密钥，转成md5 md5(params + key);
     * 
     * @param request
     * @param signKey
     * @return
     */
    public static String genSign(Map<String, String> request, String signKey) {
        StringBuilder result = new StringBuilder();
        List<String> sortedKeys = new ArrayList<String>(request.keySet());
        Collections.shuffle(sortedKeys);
        Collections.sort(sortedKeys, new AlphabetCompartor<String>());
        for (String key : sortedKeys) {
            if (StringUtils.isBlank(request.get(key)) || ZfbConsts.KEY_SIGN.equals(key)
                    || ZfbConsts.KEY_SIGN_TYPE.equals(key)) {
                continue;
            }
            result.append(key).append(EQ).append(request.get(key)).append(AMP);
        }
        if (result.length() < 1)
            return null;

        result.deleteCharAt(result.length() - 1); // remove last &
        result.append(signKey);
        byte[] bytes = result.toString().getBytes(Charset.forName(Consts.CHARSET_UTF8));
        return DigestUtils.md5DigestAsHex(bytes).toLowerCase();
    }

    /**
     * The sign target parameters should be built according to the original
     * order.
     * 
     * @param request
     * @param signKey
     * @author administrator
     * @return
     */
    public static String genSignForNotify(Map<String, String> request, String signKey) {
        StringBuilder result = new StringBuilder();
        for (String key : request.keySet()) {
            if (StringUtils.isBlank(request.get(key)) || ZfbConsts.KEY_SIGN.equals(key)
                    || ZfbConsts.KEY_SIGN_TYPE.equals(key)) {
                continue;
            }
            result.append(key).append(EQ).append(request.get(key)).append(AMP);
        }
        if (result.length() < 1)
            return null;
        result.deleteCharAt(result.length() - 1); // remove last &
        result.append(signKey);
        byte[] bytes = result.toString().getBytes(Charset.forName(Consts.CHARSET_UTF8));
        return DigestUtils.md5DigestAsHex(bytes).toLowerCase();
    }

    /**
     * 组装支付结果的URL（同步返回和异步通知均用这个方法），用于调用业务方
     */
    public static String assemblePayResultQueryStr(PayOrder payOrder) {

        AppInfo appInfo = payOrder.getAppInfo();

        Map<String, String> params = new HashMap<String, String>();
        payOrder.setPayUrl("");
        String data = JsonHelper.payOrderToRespJson(payOrder);
        params.put(Consts.Http.PARAM_DATA, data);
        params.put(Consts.Http.PARAM_APP_ID, payOrder.getAppId());
        params.put(Consts.Http.PARAM_SIGN, SecureHelper.genMd5Sign(appInfo.getKey(), data));

        return assembleQueryStr(params);
    }

    /**
     * 组装查询字符串
     * 
     * @param map 请求参数
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String assembleQueryStr(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            if (StringUtils.isBlank(map.get(key))) {
                continue;
            }

            try {
                String ecodedStr = URLEncoder.encode(map.get(key), Consts.CHARSET_UTF8);
                builder.append(key).append(EQ).append(ecodedStr).append(AMP);
            } catch (UnsupportedEncodingException e) {
                LOG.warn("get exception when creating query string, key={}, value={}", key, map.get(key), e);
            }
        }

        String queryStr = builder.toString().replaceAll("&$", ""); // remove
                                                                   // last "&"
        LOG.debug("assembled query string, result={}, map={}", queryStr, map);
        return queryStr;
    }

    /**
     * 根据同步（ReturnUrl）或异步（NotifyUrl）中的信息，组装PayOrder
     */
    public static PayOrder assemblePayOrder(DomainResource resource, Map<String, String> params) {
        String notifyType = params.get(ZfbConsts.KEY_NOTIFY_TYPE);
        String chOrderId = null;
        String appId = null;
        String appOrderId = null;
        if (ZfbConsts.NOTIFY_TYPE_REFUND.equalsIgnoreCase(notifyType)) {
            String resultDetails = params.get(ZfbConsts.KEY_RESULT_DETAILS);
            if (!StringUtils.isBlank(resultDetails)) {
                String chDealId = resultDetails.split("\\^")[0];
                PayOrder payOrder = resource.getPayOrder(chDealId);
                chOrderId = payOrder.getChOrderId();
                appId = payOrder.getAppId();
                appOrderId = payOrder.getAppOrderId();
            }
        } else {
            chOrderId = params.get(ZfbConsts.KEY_OUT_TRADE_NO);
            appId = OrderIdHelper.getAppId(chOrderId);
            appOrderId = OrderIdHelper.getAppOrderId(chOrderId);
        }
        // validates key info
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appOrderId)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "appId or appOrderId empty! chOrderId=" + chOrderId);
        }

        // check if info exist
        AppInfo appInfo = resource.getAppInfo(appId);
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        String payMethod = payOrder.getPayMethod();
        String chId = (payOrder == null) ? null : payOrder.getChId();
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, chId, payMethod);
        AppChInfo appChInfo = appChInfos.get(0);
        // 因教育线的退款未提交gate参数，暂时去掉appchinfos判断，后续如果不同支付方式密码不一样会存在问题
        // if (appInfo == null || payOrder == null ||
        // CollectionUtils.size(appChInfos) != 1) {
        if (appInfo == null || payOrder == null) {
            String msg = "appInfo/payOrder/appChInfo not found, or get more than one appChInfo!";
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg + " chOrderId=" + chOrderId + ", payOrder=" + payOrder);
        }

        // validate sign
        String signType = params.get(ZfbConsts.KEY_SIGN_TYPE);
        String signInReq = params.get(ZfbConsts.KEY_SIGN);
        String signExpect = genSign(params, appChInfo.getChPayKeyMd5());
        if (!ZfbConsts.ZFB_SIGN_TYPE_MD5.equals(signType) || !signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched! signType=%s, signInReq=%s, signExpect=%s",
                    signType, signInReq, signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }

        // validate notify_id
        String notifyVerifyUrlPattern = ZfbConsts.ADDR_NOTIFY_VERIFY_PATTERN;
        String notifyId = params.get(ZfbConsts.KEY_NOTIFY_ID);
        if (StringUtils.isBlank(notifyId)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "notify_id is empty, which should not happen!");
        }
        String verifyUrl = String.format(notifyVerifyUrlPattern, appChInfo.getChAccountId(), notifyId);
        String respStr = HttpClientHelper.sendRequest(verifyUrl, ZfbConsts.CHARSET_UTF8);
        if (!new Boolean(respStr)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "notify_id verify failed! verifyUrl=" + verifyUrl);
        }

        // assemble PayOrder
        payOrder.setAppInfo(appInfo);
        payOrder.setAppChInfo(appChInfo);
        return payOrder;
    }

    /**
     * 翻译支付类型代号
     * 
     * @param payMethod 支付类型代号
     * @return
     *         （待定义） - 便捷支付(motoPay)
     *         Balance - 余额支付(directPay)
     *         Gate - 银行支付(bankPay)
     *         其它 - 余额支付(directPay)
     */
    public static String translatePayMethod(String payMethod) {
        // String payMethod = PAYMETHOD_BANK_PAY_VALUE;
        String chPayMethod = ZfbConsts.PAYMETHOD_DIRECT_PAY_VALUE;
        if (Consts.PayMethod.GATE.equalsIgnoreCase(payMethod)) {
            chPayMethod = ZfbConsts.PAYMETHOD_BANK_PAY_VALUE;
        } else if (Consts.PayMethod.BALANCE.equalsIgnoreCase(payMethod)) {
            chPayMethod = ZfbConsts.PAYMETHOD_DIRECT_PAY_VALUE;
        }
        return chPayMethod;
    }

    /*
     * 按字母顺序由小到大排序
     */
    private static class AlphabetCompartor<T> implements Comparator<T> {

        public AlphabetCompartor() {
        }

        @Override
        public int compare(T o1, T o2) {
            int result = 0;
            if (o1 == o2) {
                return 0;
            }
            if (o1 == null) {
                result = -1;
            } else if (o2 == null) {
                result = 1;
            } else {
                result = o1.toString().compareTo(o2.toString());
            }
            return result;
        }
    }

    /**
     * 从响应中解析出防钓鱼时间戳
     */
    public static String parseQueryTimestampResponse(String respStr) {
        LOG.info("parsing query timestamp result, respStr={}", respStr, "ds:trace:0");

        if (StringUtils.isBlank(respStr)) {
            return null;
        }

        Document document = null;
        SAXReader reader = new SAXReader(false);
        try {
            document = reader.read(new ByteArrayInputStream(respStr.replaceFirst("utf8", "utf-8").getBytes()));
            Node isSuccessNode = document.selectSingleNode(ZfbConsts.QUERY_IS_SUCCESS_NODE);
            if (isSuccessNode == null || !ZfbConsts.SUCCESS_RESULT_CODE.equals(isSuccessNode.getStringValue())) {
                LOG.info("query timestamp failed, the 'is_success' node is not as expected");
                return null;
            }

            Node resultNode = document.selectSingleNode(ZfbConsts.QUERY_TIMESTAMP_RESULT_NODE);
            if (resultNode == null) {
                Node errorCodeNode = document.selectSingleNode(ZfbConsts.QUERY_ERROR_NODE);
                String errorCode = errorCodeNode != null ? errorCodeNode.getStringValue() : ZfbConsts.UNKNOWN_ERROR;
                LOG.info("query timestamp with fail result: {}", errorCode);
                return null;
            }
            return resultNode.getStringValue();
        } catch (DocumentException e) {
            LOG.error("get exception when parsing query timestamp response:" + respStr, e);
        } finally {
            if (document != null)
                document.clearContent();
        }
        return null;
    }

    /**
     * 根据查询结果更新PayOrder，支付在查询接口中返回的内容是xml（HttpBody）
     * 
     * @return true 结果有更新 false 没有更新
     */
    public static void updatePayOrderByQuery(PayOrder payOrder, String respStr) {
        LOG.info("[zfb_in] query results, respStr={}, payOrder={}", respStr, payOrder, TraceHelper.getTrace(payOrder));
        Map<String, String> paramsMap = getMapFromXml(respStr);
        // 处理请求失败的情况（是查询请求本身失败，而非支付失败）
        if (!ZfbConsts.SUCCESS_RESULT_CODE.equals(paramsMap.get(ZfbConsts.ZFB_QUERY_IS_SUCCESS))) {
            LOG.error("[zfb_in] order query request failed, isSuccessValue={}, errorValue={}, respStr{}",
                    paramsMap.get(ZfbConsts.ZFB_QUERY_IS_SUCCESS), paramsMap.get(ZfbConsts.ZFB_QUERY_ERROR), respStr,
                    "ds:trace:" + payOrder.getAppOrderId());
            return;
        }
        // 如果是退款，并且退款成功，则直接返回
        String refundStatus = paramsMap.get(ZfbConsts.KEY_REFUND_STATUS);
        if (!StringUtils.isBlank(refundStatus)) {
            if (Consts.SC.REFUND_SUCCESS.equalsIgnoreCase(refundStatus)) {
                payOrder.setStatusCode(Consts.SC.REFUND_SUCCESS);
            } else if (ZfbConsts.WAIT_ALIPAY_REFUND.equalsIgnoreCase(refundStatus)) {
                payOrder.setStatusCode(Consts.SC.REFUND_PENDING);
            } else {
                payOrder.setStatusCode(refundStatus);
            }
            return;
        }
        String tradeStatusValue = paramsMap.get(ZfbConsts.QUERY_TRADE_STATUS_NODE);
        // 交易结果为关闭（不成功）
        if (ZfbConsts.TRADE_CLOSED.equals(tradeStatusValue)) {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg("支付宝查询：支付关闭");
            return;
        }
        // 处理中间结果
        if (!(ZfbConsts.TRADE_SUCCESS.equals(tradeStatusValue) || ZfbConsts.TRADE_FINISHED.equals(tradeStatusValue))) {
            payOrder.setStatusMsg("支付宝查询：支付中");
            return;
        }
        // validate sign
        String signType = paramsMap.get(ZfbConsts.KEY_SIGN_TYPE);
        String signInReq = paramsMap.get(ZfbConsts.KEY_SIGN);
        String signExpect = genSign(getTradeMap(paramsMap), payOrder.getAppChInfo().getChPayKeyMd5());
        if (!ZfbConsts.ZFB_SIGN_TYPE_MD5.equals(signType) || !signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched! signType=%s, signInReq=%s, signExpect=%s",
                    signType, signInReq, signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        String chDealId = paramsMap.get(ZfbConsts.QUERY_TRADE_NO_NODE);
        String amount = paramsMap.get(ZfbConsts.QUERY_TOTAL_FEE_NODE);
        String chDealTime = paramsMap.get(ZfbConsts.QUERY_GMT_CREATE_NODE);
        String bankDealTime = paramsMap.get(ZfbConsts.QUERY_GMT_PAYMENT_NODE);
        String sellerId = paramsMap.get(ZfbConsts.QUERY_SELLER_ID_NODE);
        // 更新结果前先作业务级的安全校验
        validatePayOrderStatusForUpdate(payOrder);
        validatePayOrderOldValues(payOrder, amount, sellerId);

        // 更新结果
        payOrder.setStatusCode(Consts.SC.SUCCESS);
        payOrder.setChDealId(chDealId);
        payOrder.setChDealTime(chDealTime.replaceAll("[-: ]", ""));
        payOrder.setBankDealTime(bankDealTime.replaceAll("[-: ]", ""));
        payOrder.setChFee(new BigDecimal(payOrder.getAmount().longValue() * 5 / 1000)); // 暂时支付宝的渠道费按千分之五算

    }

    /**
     * 获取查询结果需要签名的Map
     * 
     * @param param
     * @return
     */
    public static Map<String, String> getTradeMap(Map<String, String> param) {
        Map<String, String> result = new HashMap<String, String>();
        for (Iterator<Map.Entry<String, String>> it = param.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            if ((ZfbConsts.ZFB_QUERY_IS_SUCCESS.equals(key)) || (ZfbConsts.KEY_INPUT_CHARSET.equals(key))
                    || (ZfbConsts.KEY_SERVICE.equals(key)) || (ZfbConsts.KEY_PARTNER.equals(key))
                    || (ZfbConsts.KEY_SIGN.equals(key)) || (ZfbConsts.KEY_SIGN_TYPE.equals(key))) {
                continue;
            }
            result.put(key, entry.getValue());
        }
        return result;
    }

    /**
     * 处理支付宝的查询页面结果
     * 
     * @param respStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getMapFromXml(String respStr) {
        Document document = null;
        Map<String, String> paramsMap = new HashMap<String, String>();
        SAXReader reader = new SAXReader(false);
        try {
            // document = READER.read(new
            // ByteArrayInputStream(respStr.replaceFirst("utf8",
            // "utf-8").getBytes()));
            document = reader.read(new ByteArrayInputStream(respStr.getBytes()));
            Element root = document.getRootElement();//
            for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
                Element element = (Element) i.next();
                if (element.getName().equals(ZfbConsts.ZFB_QUERY_RUQUEST)
                        || element.getName().equals(ZfbConsts.ZFB_QUERY_RESPONSE)) {
                    continue;
                } else {
                    paramsMap.put(element.getName(), element.getText());
                }
            }
            Element request = root.element(ZfbConsts.ZFB_QUERY_RUQUEST);
            if (request != null) {
                for (Iterator<Element> iterator = request.elementIterator(); iterator.hasNext();) {
                    Element temp = (Element) iterator.next();
                    paramsMap.put(temp.attributeValue(ZfbConsts.ZFB_QUERY_NAME), temp.getText());
                }
            }
            if (root.element(ZfbConsts.ZFB_QUERY_RESPONSE) != null) {
                Element response = root.element(ZfbConsts.ZFB_QUERY_RESPONSE).element(ZfbConsts.ZFB_QUERY_TRADE);
                if (response != null) {
                    for (Iterator<Element> iterator = response.elementIterator(); iterator.hasNext();) {
                        Element temp = (Element) iterator.next();
                        paramsMap.put(temp.getName(), temp.getText());
                    }
                }
            }
        } catch (DocumentException e) {
            LOG.error("[zfb_in]parse response with exception!", e);
            /*
             * TODO log the abstract about the bad response. As known, sometime
             * Alipay will
             * return a 500 error here and we should trigger a notification in
             * this case
             */
        } finally {
            if (document != null) {
                document.clearContent();
            }
        }
        return paramsMap;
    }

    /**
     * 处理来自支付宝的支付结果（同步跳转或者异步通知）
     * 
     * @param notifyRequest
     * @return
     */
    public static void updatePayOrderByReturnNotify(PayOrder payOrder, Map<String, String> params) {
        LOG.info("[zfb_in] pay results, params={}, payOrder={}", params, payOrder, TraceHelper.getTrace(payOrder));

        // 提取信息
        // String payType =
        // StringUtils.trim(params.get(ZfbConstants.KEY_PAYMENT_TYPE));
        // String ext1 =
        // StringUtils.trim(params.get(ZfbConstants.KEY_EXTRA_COMMON_PARAM));
        String tradeStatus = StringUtils.trim(params.get(ZfbConsts.KEY_TRADE_STATUS));
        String chDealId = StringUtils.trim(params.get(ZfbConsts.KEY_TRADE_NO));
        // chDealTime由渠道创建时间改为支付时间
        String chDealTime = StringUtils.trim(params.get(ZfbConsts.KEY_GMT_PAYMENT));
        String bankId = StringUtils.trim(params.get(ZfbConsts.KEY_OUT_CHANNEL_INST)); // TODO:
                                                                                      // use
                                                                                      // userPayCh?
        String bankDealId = StringUtils.trim(params.get(ZfbConsts.KEY_BANK_SEQ_NO)); // TODO:
                                                                                     // use
                                                                                     // userPayChDealId?
        String bankDealTime = StringUtils.trim(params.get(ZfbConsts.KEY_GMT_PAYMENT)); // TODO:
                                                                                       // use
                                                                                       // userPayTime?
        String totalFee = StringUtils.trim(params.get(ZfbConsts.KEY_TOTAL_FEE));
        String sellerId = StringUtils.trim(params.get(ZfbConsts.KEY_SELLER_ID));
        String errCode = StringUtils.trim(params.get(ZfbConsts.KEY_ERROR_CODE));
        String notifyTime = StringUtils.trim(params.get(ZfbConsts.KEY_NOTIFY_TIME));
        String notifyType = params.get(ZfbConsts.KEY_NOTIFY_TYPE);
        if (Consts.SC.REFUND_SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return;
        }
        if (ZfbConsts.NOTIFY_TYPE_REFUND.equalsIgnoreCase(notifyType)) {
            String resultDetails = params.get(ZfbConsts.KEY_RESULT_DETAILS);
            if (!StringUtils.isBlank(resultDetails)) {
                if (ZfbConsts.NOTIFY_HANDLE_RESULT_SUCCESS.equalsIgnoreCase(resultDetails.split("\\^")[2]))
                    ;
                {
                    tradeStatus = Consts.SC.REFUND_SUCCESS;
                }
            }
        } else {
            // 更新结果前先作业务级的安全校验
            // validatePayOrderStatusForUpdate(payOrder);
            validatePayOrderOldValues(payOrder, totalFee, sellerId);
            // 更新结果
            payOrder.setBankId(bankId);
            payOrder.setChDealId(chDealId);
            payOrder.setBankDealId(bankDealId);
            payOrder.setChDealTime(translateTimeFormat(chDealTime));
            payOrder.setBankDealTime(translateTimeFormat(bankDealTime));
        }
        payOrder.setStatusCode(translatePayResult(tradeStatus));
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            payOrder.setStatusMsg("支付宝：支付完成");
        } else if (Consts.SC.PENDING.equalsIgnoreCase(payOrder.getStatusCode())) {
            payOrder.setStatusMsg("支付宝：支付中");
        } else if (Consts.SC.REFUND_PENDING.equalsIgnoreCase(payOrder.getStatusCode())) {
            payOrder.setStatusMsg("支付宝：等待退款");
        } else if (Consts.SC.REFUND_SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            payOrder.setStatusMsg("支付宝：退款完成");
        }
        payOrder.setLastUpdateTime(translateTimeFormat(notifyTime));
        return;
    }

    private static String translateTimeFormat(String time) {
        if (StringUtils.isBlank(time)) {
            return TimeHelper.get(8, new Date());
        }
        return time.replaceAll("[-: ]", "");
    }

    private static void validatePayOrderOldValues(PayOrder payOrder, String totalFee, String sellerId) {
        BigDecimal amount = payOrder.getAmount();
        String chAccountId = payOrder.getAppChInfo().getChAccountId();
        boolean chAccountIdNotMatch = StringUtils.isNotBlank(sellerId) && !chAccountId.equals(sellerId);
        int amountCompare = amount.compareTo(new BigDecimal(totalFee));
        boolean amountNotMatch = NumberUtils.isNumber(totalFee) && (amountCompare != 0);
        if (chAccountIdNotMatch || amountNotMatch) {
            String msg = String.format("chAccountId or amount not matched with old value! '%s'<?>'%s', '%s'<?>'%s'",
                    chAccountId, sellerId, amount, totalFee);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg, payOrder);
        }
    }

    private static void validatePayOrderStatusForUpdate(PayOrder payOrder) {
        String statusCode = payOrder.getStatusCode();
        if (Consts.SC.SUCCESS.equals(statusCode) || Consts.SC.FAIL.equals(statusCode)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "Order statusCode is final, should not update!", payOrder);
        }
    }

    private static String translatePayResult(String tradeStatus) {
        String result = Consts.SC.UNKNOWN;
        if (StringUtils.isBlank(tradeStatus)) {
            return result;
        }

        if (ZfbConsts.TRADE_PENDING.equals(tradeStatus) || ZfbConsts.WAIT_BUYER_PAY.equals(tradeStatus)) {
            result = Consts.SC.PENDING;
        } else if (ZfbConsts.TRADE_SUCCESS.equals(tradeStatus) || ZfbConsts.TRADE_FINISHED.equals(tradeStatus)) {
            result = Consts.SC.SUCCESS;
        } else if (ZfbConsts.WAIT_ALIPAY_REFUND.equalsIgnoreCase(tradeStatus)) {
            result = Consts.SC.REFUND_PENDING;
        } else if (Consts.SC.REFUND_SUCCESS.equalsIgnoreCase(tradeStatus)) {
            result = Consts.SC.REFUND_SUCCESS;
        } else if (ZfbConsts.TRADE_CLOSED.equals(tradeStatus) || ZfbConsts.TRADE_HAS_CLOSED.equals(tradeStatus)) {
            // 1）在指定时间段内未支付时关闭的交易。2）在交易完成全额退款成功时关闭的交易
            result = Consts.SC.FAIL;
        }
        return result;
    }

    /**
     * 退款操作处理
     * 
     * @param payOrder
     * @param respStr
     */
    public static void updatePayOrderByRefund(PayOrder payOrder, String respStr) {
        LOG.info("[zfb_in] refund results, respStr={}, payOrder={}", respStr, payOrder,
                "ds:trace:" + payOrder.getAppOrderId());
        // 解析退款返回的xml文件内容
        // Map<String, String> paramsMap = getMapFromXml(respStr);
        // if(ZfbConsts.SUCCESS_RESULT_CODE.equals(paramsMap.get(ZfbConsts.ZFB_QUERY_IS_SUCCESS))){
        // payOrder.setStatusCode(Consts.SC.REFUND_SUCCESS);
        // }else
        // if(ZfbConsts.FAIL_RESULT_CODE.equals(paramsMap.get(ZfbConsts.ZFB_QUERY_IS_SUCCESS))){
        // payOrder.setStatusCode(Consts.SC.REFUND_FAIL);
        // }else
        // if(ZfbConsts.P_RESULT_CODE.equals(paramsMap.get(ZfbConsts.ZFB_QUERY_IS_SUCCESS))){
        // payOrder.setStatusCode(Consts.SC.REFUND_PENDING);
        // }
        // 暂时都定为退款请求中
        payOrder.setStatusCode(Consts.SC.REFUND_PENDING);
    }

    /**
     * 组装支付宝手机网页即时到账接口的授权接口的req_data参数
     * 
     * @param param
     * @return
     */
    public static String assembleWapAuthorizeData(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append(ZfbConsts.Wap.XML1).append(ZfbConsts.Wap.DIRECT_TRADE_CREATE_REQ).append(ZfbConsts.Wap.XML2);
        for (Iterator<Map.Entry<String, String>> it = param.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(ZfbConsts.Wap.XML1).append(key).append(ZfbConsts.Wap.XML2).append(value)
                    .append(ZfbConsts.Wap.XML3).append(key).append(ZfbConsts.Wap.XML2);
        }
        sb.append(ZfbConsts.Wap.XML3).append(ZfbConsts.Wap.DIRECT_TRADE_CREATE_REQ).append(ZfbConsts.Wap.XML2);
        return sb.toString();
    }

    /**
     * 组装支付宝手机网页即时到账接口的交易接口的req_data参数
     * 
     * @param param
     * @return
     */
    public static String assembleWapTransData(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append(ZfbConsts.Wap.XML1).append(ZfbConsts.Wap.AUTH_AND_EXECUTE_REQ).append(ZfbConsts.Wap.XML2);
        for (Iterator<Map.Entry<String, String>> it = param.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(ZfbConsts.Wap.XML1).append(key).append(ZfbConsts.Wap.XML2).append(value)
                    .append(ZfbConsts.Wap.XML3).append(key).append(ZfbConsts.Wap.XML2);
        }
        sb.append(ZfbConsts.Wap.XML3).append(ZfbConsts.Wap.AUTH_AND_EXECUTE_REQ).append(ZfbConsts.Wap.XML2);
        return sb.toString();
    }

    /**
     * 把wap授权接口返回的参数解析并返回toekn
     * 
     * @param respStr
     * @return
     */
    public static String getWapAuthorizeMap(PayOrder payOrder, String respStr) {
        LOG.info("parsing wapAuthorize result payOrder={}, respStr={}", payOrder, respStr,
                TraceHelper.getTrace(payOrder));
        Map<String, String> respMap = new HashMap<String, String>();
        String[] result = respStr.split(ZfbConsts.AMP);
        for (String s : result) {
            if (s.contains(ZfbConsts.Wap.RES_DATA)) {
                respMap.put(ZfbConsts.Wap.RES_DATA, s.substring(9));
            } else if (s.contains(ZfbConsts.Wap.RES_ERROR)) {
                respMap.put(ZfbConsts.Wap.RES_ERROR, s.substring(10));
            } else {
                String[] temp = s.split(ZfbConsts.EQ);
                respMap.put(temp[0], temp[1]);
            }
        }
        if (StringUtils.isBlank(respMap.get(ZfbConsts.Wap.RES_DATA))) {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg(respMap.get(ZfbConsts.Wap.RES_DATA));
            return "";
        }
        // 请求失败的请况下，不签名,所以签名参数延后校验
        String authorizeRespSign = respMap.get(ZfbConsts.KEY_SIGN);
        String authorizeRespSignMsg = ZfbHelper.genSign(respMap, payOrder.getAppChInfo().getChPayKeyMd5());
        String signType = respMap.get(ZfbConsts.Wap.SEC_ID);
        if (!authorizeRespSign.equals(authorizeRespSignMsg)) {
            String msg = String.format("sign: unsupported type or unmatched! signType=%s, signInReq=%s, signExpect=%s",
                    signType, authorizeRespSign, authorizeRespSignMsg);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        Map<String, String> tokenMap = getMapFromResDataXml(respMap.get(ZfbConsts.Wap.RES_DATA));
        return tokenMap.get(ZfbConsts.Wap.REQUEST_TOKEN);
    }

    /**
     * 根据返回xml 转换成map
     * 
     * @param respStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getMapFromResDataXml(String respStr) {
        Document document = null;
        Map<String, String> paramsMap = new HashMap<String, String>();
        SAXReader reader = new SAXReader(false);
        try {
            document = reader.read(new ByteArrayInputStream(respStr.getBytes()));
            Element root = document.getRootElement();
            for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
                Element element = (Element) i.next();
                paramsMap.put(element.getName(), element.getText());
            }
        } catch (DocumentException e) {
            LOG.error("[zfb_in]parse response with exception!", e);
        } finally {
            if (document != null) {
                document.clearContent();
            }
        }
        return paramsMap;
    }

    /**
     * 根据手机支付同步（NotifyUrl）中的信息，组装PayOrder
     */
    public static PayOrder assembleSynPayWapOrder(DomainResource resource, Map<String, String> params) {
        LOG.info("parsing assembleSynPayWapOrder result params={}", params, "ds:trace:0");
        String chOrderId = params.get(ZfbConsts.KEY_OUT_TRADE_NO);
        ;
        String appId = null;
        String appOrderId = null;
        appId = OrderIdHelper.getAppId(chOrderId);
        appOrderId = OrderIdHelper.getAppOrderId(chOrderId);
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appOrderId)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "appId or appOrderId empty! chOrderId=" + chOrderId);
        }
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        String signInReq = params.get(ZfbConsts.KEY_SIGN);
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, payOrder.getChId(), payOrder.getPayMethod());
        AppChInfo appChInfo = appChInfos.get(0);
        payOrder.setAppChInfo(appChInfo);
        AppInfo appInfo = resource.getAppInfo(appId);
        payOrder.setAppInfo(appInfo);
        String signExpect = genSign(params, payOrder.getAppChInfo().getChPayKeyMd5());
        if (!signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s", signInReq,
                    signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return payOrder;
        }
        if (ZfbConsts.NOTIFY_HANDLE_RESULT_SUCCESS.equalsIgnoreCase(params.get(ZfbConsts.Wap.RESULT))) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        }
        payOrder.setChDealId(params.get(ZfbConsts.KEY_TRADE_NO));
        return payOrder;
    }

    /**
     * 根据手机支付异步（NotifyUrl）中的信息，组装PayOrder
     */
    public static PayOrder assembleAsynPayWapOrder(DomainResource resource, Map<String, String> params) {
        LOG.info("parsing assembleAsynPayWapOrder result params={}", params, "ds:trace:0");
        Map<String, String> notfiyDataMap = getMapFromNotfiyDataXml(params.get(ZfbConsts.Wap.NOTFIY_DATA));
        String chOrderId = notfiyDataMap.get(ZfbConsts.KEY_OUT_TRADE_NO);
        String appId = null;
        String appOrderId = null;
        appId = OrderIdHelper.getAppId(chOrderId);
        appOrderId = OrderIdHelper.getAppOrderId(chOrderId);
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appOrderId)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "appId or appOrderId empty! chOrderId=" + chOrderId);
        }
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        String signType = params.get(ZfbConsts.Wap.SEC_ID);
        String signInReq = params.get(ZfbConsts.KEY_SIGN);
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, payOrder.getChId(), payOrder.getPayMethod());
        AppChInfo appChInfo = appChInfos.get(0);
        payOrder.setAppChInfo(appChInfo);
        String signExpect = genSignForNotify(params, payOrder.getAppChInfo().getChPayKeyMd5());

        if (!ZfbConsts.ZFB_SIGN_TYPE_MD5.equals(signType) || !signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched! signType=%s, signInReq=%s, signExpect=%s",
                    signType, signInReq, signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return payOrder;
        }
        String tradeStatus = notfiyDataMap.get(ZfbConsts.KEY_TRADE_STATUS);
        // add a status to trigger a task
        if (ZfbConsts.TRADE_SUCCESS.equalsIgnoreCase(tradeStatus)
                || ZfbConsts.TRADE_FINISHED.equalsIgnoreCase(tradeStatus)) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
        } else if (ZfbConsts.TRADE_PENDING.equalsIgnoreCase(tradeStatus)
                || ZfbConsts.WAIT_BUYER_PAY.equalsIgnoreCase(tradeStatus)) {
            payOrder.setStatusCode(Consts.SC.PENDING);
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        payOrder.setStatusMsg(notfiyDataMap.get(ZfbConsts.KEY_TRADE_STATUS));
        payOrder.setChDealId(notfiyDataMap.get(ZfbConsts.KEY_TRADE_NO));
        String notifyTime = StringUtils.trim(notfiyDataMap.get(ZfbConsts.KEY_NOTIFY_TIME));
        payOrder.setLastUpdateTime(translateTimeFormat(notifyTime));
        String chDealTime = StringUtils.trim(notfiyDataMap.get(ZfbConsts.KEY_GMT_PAYMENT));
        LOG.info("chDealTime:{},apporderid:{},notfiyDataMap:{}", chDealTime, payOrder.getAppOrderId(), notfiyDataMap);
        payOrder.setChDealTime(translateTimeFormat(chDealTime));
        return payOrder;
    }

    /**
     * 支付宝快捷支付（wapapp）的通知组装订单
     */
    public static PayOrder assembleAsynPayWapAppOrder(DomainResource resource, Map<String, String> params) {
        LOG.info("[zfbwapapp_in.notify] parsing assembleAsynPayWapAppOrder result params={}", params);
        Map<String, String> notfiyDataMap = params;
        String chOrderId = notfiyDataMap.get(ZfbConsts.KEY_OUT_TRADE_NO);
        String appId = null;
        String appOrderId = null;
        appId = OrderIdHelper.getAppId(chOrderId);
        appOrderId = OrderIdHelper.getAppOrderId(chOrderId);
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appOrderId)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "appId or appOrderId empty! chOrderId=" + chOrderId);
        }
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        String signInReq = params.get(ZfbConsts.KEY_SIGN);
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, payOrder.getChId(), payOrder.getPayMethod());
        AppChInfo appChInfo = appChInfos.get(0);
        payOrder.setAppChInfo(appChInfo);
        String publicKeyPath = "";
        if (payOrder.getAppChInfo() != null) {
            publicKeyPath = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(),
                    ZfbConsts.WapApp.PUBLIC_KEY_PATH);
        }
        if (StringUtils.isBlank(publicKeyPath)) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "get no zfbwapapp_public_file_path in additionalInfo.");
        }
        String verifyData = getNotifySignTargetForWapApp(params);
        LOG.debug("[zfbwapapp_in.notify] assemble a sign string,target string:{}", verifyData);
        boolean verified = false;
        try {
            verified = RSAEncrypt.checkSign(verifyData, signInReq, publicKeyPath);
        } catch (Exception e) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "RSASignature.doCheck false");
        }
        if (!verified) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "RSASignature.doCheck false");
        }

        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return payOrder;
        }
        String tradeStatus = notfiyDataMap.get(ZfbConsts.KEY_TRADE_STATUS);
        // add a status to trigger a task
        if (ZfbConsts.TRADE_SUCCESS.equalsIgnoreCase(tradeStatus)
                || ZfbConsts.TRADE_FINISHED.equalsIgnoreCase(tradeStatus)) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
        } else if (ZfbConsts.TRADE_PENDING.equalsIgnoreCase(tradeStatus)
                || ZfbConsts.WAIT_BUYER_PAY.equalsIgnoreCase(tradeStatus)) {
            payOrder.setStatusCode(Consts.SC.PENDING);
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        payOrder.setStatusMsg(notfiyDataMap.get(ZfbConsts.KEY_TRADE_STATUS));
        payOrder.setChDealId(notfiyDataMap.get(ZfbConsts.KEY_TRADE_NO));
        String notifyTime = StringUtils.trim(notfiyDataMap.get(ZfbConsts.WapApp.KEY_NOTIFY_REG_TIME));
        payOrder.setChDealTime(translateTimeFormat(notifyTime));
        payOrder.setLastUpdateTime(translateTimeFormat(notifyTime));
        LOG.info("[zfbwapapp_in.notify] change payorder status, payorder:{}", payOrder);
        return payOrder;
    }

    /**
     * 根据快捷支付手机端的支付宝签名字符串来更新订单状态.
     * 
     * @param resource
     * @param params
     * @return
     */
    public static PayOrder assembleSynPayWapAppOrder(DomainResource resource, Map<String, String> params) {
        String resultStatus = params.get(ZfbConsts.WapApp.KEY_RESULT_STATUS);
        String memo = params.get(ZfbConsts.WapApp.KEY_MEMO);
        String result = params.get(ZfbConsts.WapApp.KEY_RESULT);
        LOG.info("[zfbwapapp_in.checkSign] resultStatus:{},memo:{},result:{}", resultStatus, memo, result);
        String signTargetData = ZfbHelper.getSynSignTargetString(result);
        Map<String, String> resultMap = getKeyValueMap(result);
        String chSign = resultMap.get(ZfbConsts.KEY_SIGN);
        String chOrderId = resultMap.get(ZfbConsts.KEY_OUT_TRADE_NO);
        String successFlag = resultMap.get(ZfbConsts.WapApp.KEY_SUCCESS);
        String appId = OrderIdHelper.getAppId(chOrderId);
        String appOrderId = OrderIdHelper.getAppOrderId(chOrderId);
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, payOrder.getChId(), payOrder.getPayMethod());
        AppChInfo appChInfo = appChInfos.get(0);
        payOrder.setAppChInfo(appChInfo);
        // 验证支付宝签名，异常则不修改订单状态，直接返回。
        try {
            if (!checkSynPayWapAppSign(payOrder, signTargetData, chSign)) {
                LOG.warn("[zfbwapapp_in.checkSign] failed,because of invalid zfb sign,signTargetData:{},chSign:{}",
                        signTargetData, chSign);
                return payOrder;
            }
        } catch (PayException e) {
            return payOrder;
        }
        // 如果订单是成功状态，直接返回。
        if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode())) {
            return payOrder;
        }
        if (ZfbConsts.WapApp.SUCCESS_FLAG.equals(successFlag)
                && ZfbConsts.WapApp.RESULT_STATUS_SUCCESS.equals(resultStatus)) {
            LOG.info("[zfbwapapp_in.checkSign] set success status,appid:{},orderid:{}", payOrder.getAppId(),
                    payOrder.getAppOrderId());
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
            payOrder.setStatusMsg("支付宝客户端：支付成功");
        } else if (ZfbConsts.WapApp.RESULT_STATUS_PENDING.equals(resultStatus)) {
            LOG.info("[zfbwapapp_in.checkSign] set pending status,appid:{},orderid:{}", payOrder.getAppId(),
                    payOrder.getAppOrderId());
            payOrder.setStatusCode(Consts.SC.PENDING);
            payOrder.setStatusMsg("支付宝客户端：支付中");
        } else {
            LOG.info("[zfbwapapp_in.checkSign] set fail status,appid:{},orderid:{}", payOrder.getAppId(),
                    payOrder.getAppOrderId());
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg("支付宝客户端：支付失败");
        }
        return payOrder;
    }

    /**
     * 校验支付宝快捷支付，客户端返回的参数
     * 
     * @param payOrder
     * @param data
     * @param sign
     * @return
     */
    public static boolean checkSynPayWapAppSign(PayOrder payOrder, String data, String sign) {
        String publicKeyPath = "";
        if (payOrder.getAppChInfo() != null) {
            publicKeyPath = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(),
                    ZfbConsts.WapApp.PUBLIC_KEY_PATH);
        }
        if (StringUtils.isBlank(publicKeyPath)) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "get no zfbwapapp_public_file_path in additionalInfo.");
        }
        return RSAEncrypt.checkSign(data, sign, publicKeyPath);
    }

    /**
     * 根据返回通知返回的结果 xml 转换成map
     * 
     * @param respStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getMapFromNotfiyDataXml(String respStr) {
        Document document = null;
        Map<String, String> paramsMap = new HashMap<String, String>();
        SAXReader reader = new SAXReader(false);
        try {
            document = reader.read(new ByteArrayInputStream(respStr.getBytes()));
            Element root = document.getRootElement();
            for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
                Element element = (Element) i.next();
                paramsMap.put(element.getName(), element.getText());
            }
        } catch (DocumentException e) {
            LOG.error("[zfb_in]parse response with exception!", e);
        } finally {
            if (document != null) {
                document.clearContent();
            }
        }
        return paramsMap;
    }

    /**
     * 获取商品名称
     * 
     * @param order
     * @return
     */
    public static String getSubjectName(PayOrder order) {
        if (StringUtils.isBlank(order.getProdName())) {
            return Consts.SUBJECT_NAME;
        } else {
            return order.getProdName();
        }
    }

    /**
     * 组装移动快捷支付的客户端支付请求内容
     * 
     * @param requestMap
     * @return
     */
    public static String assemblePayContentForAppWap(Map<String, String> requestMap) {
        StringBuffer sb = new StringBuffer();
        for (String key : requestMap.keySet()) {
            if (StringUtils.isBlank(requestMap.get(key))) {
                continue;
            }
            // 要对notify_url参数和sign参数进行URLEncode操作，其他参数不用，特别注意包含“@”的seller_id参数不能参与编码
            try {
                if (ZfbConsts.WapApp.KEY_NOTIFY_URL.equals(key) || ZfbConsts.WapApp.KEY_SIGN.equals(key)) {
                    String ecodedStr = URLEncoder.encode(requestMap.get(key), ZfbConsts.CHARSET_UTF8);
                    sb.append(key).append(EQ).append(QUOTE).append(ecodedStr).append(QUOTE).append(AMP);
                } else {
                    sb.append(key).append(EQ).append(QUOTE).append(requestMap.get(key)).append(QUOTE).append(AMP);
                }
            } catch (UnsupportedEncodingException e) {
                LOG.warn("[zfbwapapp_in.pay] not supported charset.");
            }
        }
        return sb.toString().replaceAll("&$", "");
    }

    /**
     * 生成要签名的字符串<br>
     * 用于支付宝快捷支付（即wapapp）<br>
     * 
     * @param request
     * @return
     */
    public static String getNotifySignTargetForWapApp(Map<String, String> request) {
        StringBuilder result = new StringBuilder();
        List<String> sortedKeys = new ArrayList<String>(request.keySet());
        Collections.shuffle(sortedKeys);
        Collections.sort(sortedKeys, new AlphabetCompartor<String>());
        for (String key : sortedKeys) {
            if (StringUtils.isBlank(request.get(key)) || ZfbConsts.KEY_SIGN.equals(key)
                    || ZfbConsts.KEY_SIGN_TYPE.equals(key)) {
                continue;
            }
            result.append(key).append(EQ).append(request.get(key)).append(AMP);
        }
        if (result.length() < 1)
            return null;

        result.deleteCharAt(result.length() - 1); // remove last &
        return result.toString();
    }

    /**
     * 从业务参数中获取&sign_type前的字符串。
     * 
     * @param src
     * @return
     */
    private static String getSynSignTargetString(String src) {
        if (StringUtils.isBlank(src) || src.indexOf(AMP + ZfbConsts.KEY_SIGN_TYPE) == -1) {
            return EMPTYSTRING;
        }
        return src.substring(0, src.indexOf(AMP + ZfbConsts.KEY_SIGN_TYPE));
    }

    /**
     * 从字符串中取出指定key对应的value。
     * 
     * @param src
     * @param key
     * @return
     */
    private static Map<String, String> getKeyValueMap(String src) {
        Map<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isBlank(src)) {
            return resultMap;
        }
        String[] args = src.split(AMP);
        for (int i = 0; i < args.length; i++) {
            if (StringUtils.isBlank(args[i]) || !args[i].contains(EQ)) {
                continue;
            }
            int index = args[i].indexOf(EQ);
            String key = args[i].substring(0, index);
            String value = args[i].substring(index + 1, args[i].length());
            if (value.length() >= 2) {
                resultMap.put(key, value.substring(1, value.length() - 1));
            } else {
                resultMap.put(key, "");
            }
        }
        return resultMap;
    }
}