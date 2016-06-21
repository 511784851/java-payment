/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.qihu;

import java.io.ByteArrayInputStream;
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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.channel.zfb.ZfbConsts;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;

/**
 * @author administrator
 * 
 */
public class QihuHelper {

    private static final Logger LOG = LoggerFactory.getLogger(QihuHelper.class);
    private static final String EQ = "=";
    private static final String AMP = "&";

    /**
     * 根据奇虎加密要求生成加密字符串:
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
            if (StringUtils.isBlank(request.get(key)) || ZfbConsts.KEY_SIGN.equals(key)) {
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
     * 根据异步（NotifyUrl）中的信息，组装PayOrder
     */
    public static PayOrder assemblePayOrder(DomainResource resource, Map<String, String> params) {
        String chOrderId = params.get(QihuConsts.KEY_MER_TRADE_CODE);
        PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
        // validate sign
        String signType = params.get(QihuConsts.KEY_SIGN_TYPE);
        String signInReq = params.get(QihuConsts.KEY_SIGN);
        String signExpect = genSign(params, payOrder.getAppChInfo().getChPayKeyMd5());
        if (!QihuConsts.VALUE_SIGN_TYPE.equals(signType) || !signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched! signType=%s, signInReq=%s, signExpect=%s",
                    signType, signInReq, signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        return payOrder;
    }

    /**
     * 处理来自奇虎的支付结果（异步通知）
     * 
     * @param notifyRequest
     * @return
     */
    public static void updatePayOrderByReturnNotify(PayOrder payOrder, Map<String, String> params) {
        LOG.info("[qihu_in] pay results, params={}, payOrder={}", params, payOrder, TraceHelper.getTrace(payOrder));
        // 提取信息
        String tradeStatus = StringUtils.trim(params.get(QihuConsts.KEY_BANK_PAY_FLAG));
        String chDealId = StringUtils.trim(params.get(QihuConsts.KEY_INNER_TRADE_CODE));
        String bankId = StringUtils.trim(params.get(QihuConsts.KEY_BANK_CODE));
        String bankDealId = StringUtils.trim(params.get(QihuConsts.KEY_BANK_TRADE_CODE));
        String bankDealTime = StringUtils.trim(params.get(QihuConsts.KEY_PAY_TIME));
        // 更新结果前先作业务级的安全校验
        validatePayOrderStatusForUpdate(payOrder);
        // 更新结果
        payOrder.setBankId(bankId);
        payOrder.setChDealId(chDealId);
        payOrder.setBankDealId(bankDealId);
        payOrder.setBankDealTime(bankDealTime);
        payOrder.setStatusCode(translatePayResult(tradeStatus));
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            payOrder.appendMsg("，奇虎通知：支付成功");
        } else if (Consts.SC.FAIL.equalsIgnoreCase(payOrder.getStatusCode())) {
            payOrder.appendMsg("，奇虎通知：支付失败");
        } else if (Consts.SC.PENDING.equalsIgnoreCase(payOrder.getStatusCode())) {
            payOrder.appendMsg("，奇虎通知：等待支付");
        } else {
            payOrder.appendMsg("，奇虎通知：未知状态");
        }
        payOrder.setLastUpdateTime(TimeHelper.get(8, new Date()));
        return;
    }

    private static void validatePayOrderStatusForUpdate(PayOrder payOrder) {
        String statusCode = payOrder.getStatusCode();
        if (Consts.SC.SUCCESS.equals(statusCode) || Consts.SC.FAIL.equals(statusCode)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "Order statusCode is final, should not update!", payOrder);
        }
    }

    /**
     * 根据查询结果更新PayOrder，支付在查询接口中返回的内容是xml（HttpBody）
     * 
     * @return true 结果有更新 false 没有更新
     */
    public static void updatePayOrderByQuery(PayOrder payOrder, String respStr) {
        LOG.info("[qihu_in] query results, respStr={}, payOrder={}", respStr, payOrder, TraceHelper.getTrace(payOrder));
        Map<String, String> paramsMap = getMapFromXml(respStr);
        // 处理请求失败的情况（是查询请求本身失败，而非支付失败）
        if (!QihuConsts.KEY_QUERY_RESULT_SUCCESS.equals(paramsMap.get(QihuConsts.KEY_RESULT_CODE))) {
            LOG.error("[qihu_in] order query request failed, result_code={}, result_msg:{}, respStr{}",
                    paramsMap.get(QihuConsts.KEY_RESULT_CODE), paramsMap.get(QihuConsts.KEY_RESULT_MSG), respStr,
                    TraceHelper.getTrace(payOrder));
            return;
        }
        String tradeStatusValue = paramsMap.get(QihuConsts.KEY_TRANS_STATUS);
        // 交易结果为关闭（不成功）
        if (QihuConsts.QUERY_RESULT_FAIL.equals(tradeStatusValue)) {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg("奇虎查询：支付不成功");
            return;
        }
        // 处理中间结果
        if (QihuConsts.QUERY_RESULT_PENDING.equals(tradeStatusValue)) {
            payOrder.setStatusCode(Consts.SC.PENDING);
            payOrder.appendMsg("奇虎查询：等待支付");
            return;
        }
        // validate sign
        String signType = paramsMap.get(QihuConsts.KEY_SIGN_TYPE);
        String signInReq = paramsMap.get(QihuConsts.KEY_SIGN);
        String signExpect = genSign(getTradeMap(paramsMap), payOrder.getAppChInfo().getChPayKeyMd5());
        if (!QihuConsts.VALUE_SIGN_TYPE.equals(signType) || !signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched! signType=%s, signInReq=%s, signExpect=%s",
                    signType, signInReq, signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        String chDealId = StringUtils.trim(paramsMap.get(QihuConsts.KEY_INNER_TRADE_CODE));
        String bankDealId = StringUtils.trim(paramsMap.get(QihuConsts.KEY_BANK_TRADE_CODE));
        String bankDealTime = StringUtils.trim(paramsMap.get(QihuConsts.KEY_PAY_RET_TIME));
        // 更新结果前先作业务级的安全校验
        validatePayOrderStatusForUpdate(payOrder);

        // 更新结果
        payOrder.setStatusCode(Consts.SC.SUCCESS);
        payOrder.setChDealId(chDealId);
        payOrder.setBankDealId(bankDealId);
        payOrder.setBankDealTime(bankDealTime);

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
        try {
            document = new SAXReader(false).read(new ByteArrayInputStream(respStr.getBytes()));
            Element root = document.getRootElement();//
            for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
                Element element = (Element) i.next();
                if (element.getName().equals(QihuConsts.KEY_RECORD)) {
                    continue;
                } else {
                    paramsMap.put(element.getName(), element.getText());
                }
            }
            Element record = root.element(QihuConsts.KEY_RECORD);
            if (record != null) {
                for (Iterator<Element> iterator = record.elementIterator(); iterator.hasNext();) {
                    Element temp = (Element) iterator.next();
                    paramsMap.put(temp.getName(), temp.getText());
                }
            }
        } catch (DocumentException e) {
            LOG.error("[qihu_in]parse response with exception!", e);
        } finally {
            if (document != null) {
                document.clearContent();
            }
        }
        return paramsMap;
    }

    private static String translatePayResult(String tradeStatus) {
        String result = Consts.SC.UNKNOWN;
        if (StringUtils.isBlank(tradeStatus)) {
            return result;
        }
        if (QihuConsts.QUERY_RESULT_PENDING.equals(tradeStatus)) {
            result = Consts.SC.PENDING;
        } else if (QihuConsts.TRADE_SUCCESS.equals(tradeStatus) || QihuConsts.QUERY_RESULT_SUCCESS.equals(tradeStatus)) {
            result = Consts.SC.SUCCESS;
        } else if (QihuConsts.TRADE_FAIL.equals(tradeStatus) || QihuConsts.QUERY_RESULT_FAIL.equals(tradeStatus)) {
            result = Consts.SC.FAIL;
        }
        return result;
    }

    /**
     * 获取查询结果需要签名的Map
     * 
     * @param param
     * @return
     */
    private static Map<String, String> getTradeMap(Map<String, String> param) {
        Map<String, String> result = new HashMap<String, String>();
        for (Iterator<Map.Entry<String, String>> it = param.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            if ((QihuConsts.KEY_RESULT_CODE.equals(key)) || (QihuConsts.KEY_RESULT_MSG.equals(key))
                    || (ZfbConsts.KEY_SIGN.equals(key)) || StringUtils.isEmpty(entry.getValue())) {
                continue;
            }
            result.put(key, entry.getValue());
        }
        return result;
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

}
