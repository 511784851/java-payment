/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.gb;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.thrift.ybpay.PaymentResult;

/**
 * @author administrator
 * 
 */
public class GbHelper {

    private static final Logger LOG = LoggerFactory.getLogger(gbHelper.class);

    @SuppressWarnings("unchecked")
    public static void updatePayOrderByQuery(PayOrder payOrder, String respStr) {
        LOG.info("[gbHelper] updatePayOrderByQuery results, respStr={}, payOrder={}", respStr, payOrder,
                TraceHelper.getTrace(payOrder));
        if (StringUtils.isEmpty(respStr)) {
            payOrder.setStatusCode(Consts.SC.CHANNEL_ERROR);
            payOrder.appendMsg("query return null");
            return;
        }
        Map<String, Object> respMap = JsonHelper.fromJson(respStr, Map.class);
        if (respMap == null || respMap.get(gbBalanceConsts.CODE) == null) {
            payOrder.setStatusCode(Consts.SC.CHANNEL_ERROR);
            payOrder.appendMsg("query return null");
            return;
        }
        try {
            int code = Integer.valueOf((String) respMap.get(gbBalanceConsts.CODE));
            if (code == gbBalanceConsts.SUCCESS) {
                payOrder.setStatusCode(Consts.SC.SUCCESS);
                payOrder.appendMsg("updatePayOrderByQuery OK");
                return;
            }
            if (code == gbBalanceConsts.PENDING) {
                payOrder.setStatusCode(Consts.SC.PENDING);
                payOrder.appendMsg(" waiting user check");
                return;
            } else {
                payOrder.setStatusCode(Consts.SC.FAIL);
                payOrder.appendMsg((String) respMap.get(gbBalanceConsts.INFO));
                return;
            }

        } catch (Throwable e) {
            payOrder.setStatusCode(Consts.SC.CHANNEL_ERROR);
            payOrder.appendMsg(" query return data illegal");
            return;
        }
    }

    @SuppressWarnings("unchecked")
    public static Long getUid(PayOrder order) {
        String jsonstr = order.getUserAddiInfo();
        try {
            Map<String, String> jsonMap = JsonHelper.fromJson(jsonstr, Map.class);
            if (StringUtils.isNotEmpty((jsonMap.get("gbnumber")))) {
                return Long.valueOf(jsonMap.get("gbnumber"));
            }
        } catch (Exception e) {
            LOG.error("[gbHelper.getUid], exception ,jsonStr:{},exception message:{}", jsonstr, e.getMessage(),
                    TraceHelper.getTrace(order));
            return null;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static String getPassword(PayOrder order) {
        String jsonstr = order.getUserAddiInfo();
        try {
            Map<String, String> jsonMap = JsonHelper.fromJson(jsonstr, Map.class);
            if (StringUtils.isNotEmpty(jsonMap.get("password"))) {
                return jsonMap.get("password");
            }
        } catch (Exception e) {
            LOG.error("[gbHelper.getPassword], exception ,jsonStr:{},exception message:{}", jsonstr, e.getMessage(),
                    TraceHelper.getTrace(order));
            return null;
        }
        return null;
    }

    /**
     * 根据同步（ReturnUrl）或异步（NotifyUrl）中的信息，组装PayOrder
     * 
     * @param resource
     * @param params
     */
    public static PayOrder assemblePayOrder(DomainResource resource, Map<String, String> params) {
        String chOrderId = params.get(gbBalanceConsts.ORDER_ID);
        // 生成并校验appId和appOrderId
        String args[] = chOrderId.split("-");
        if (args == null || args.length != 2 || StringUtils.isBlank(args[0]) || StringUtils.isBlank(args[1])) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "appId or appOrderId from gb channel is empty");
        }
        String appId = args[0];
        String appOrderId = args[1];
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        // 如果订单状态已经成功直接返回
        if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode())) {
            return payOrder;
        }
        // 检查
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, payOrder.getChId(), payOrder.getPayMethod());
        if (CollectionUtils.size(appChInfos) != 1) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "can not find appchinfo or appchinfos > 1!", payOrder);
        }

        String signInReq = params.get(gbBalanceConsts.SIGN);
        String signExpect = getAsyNotifySign(params, appChInfos.get(0).getChPayKeyMd5());
        if (!signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s", signInReq,
                    signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        // 由于G币中心没有返回处理时间，支付网关定义渠道处理时间为业务方的订单提交时间
        if (gbBalanceConsts.SUCCESS_CODE.equals(params.get(gbBalanceConsts.CODE))) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setChDealTime(payOrder.getSubmitTime());
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.appendMsg(
                    "code:" + params.get(gbBalanceConsts.CODE) + " info:" + params.get(gbBalanceConsts.INFO));
        }
        return payOrder;
    }

    private static String getAsyNotifySign(Map<String, String> params, String signKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(params.get(gbBalanceConsts.PASSWORD).toLowerCase())
                .append(params.get(gbBalanceConsts.ORDER_ID).toLowerCase()).append(params.get(gbBalanceConsts.CODE))
                .append(params.get(gbBalanceConsts.TIMESTAMP)).append(signKey);
        return MD5Utils.getMD5(sb.toString()).toLowerCase();
    }

    public static PayOrder updatePayOrderByResult(PayOrder payOrder, PaymentResult paymentResult) {
        // 不需要授权的情况下
        if (paymentResult != null && paymentResult.getCode() == gbBalanceConsts.SUCCESS) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            return payOrder;
        }
        // 需要授权的情况下,返回-30,跟弹窗确认的url
        if (paymentResult != null && paymentResult.getCode() == gbBalanceConsts.PENDING) {
            payOrder.setStatusCode(Consts.SC.PENDING);
            payOrder.setPayUrl(paymentResult.getInfo());
            return payOrder;
        }
        // 其他情况,返回支付失败,msg中带有code
        payOrder.setStatusCode(Consts.SC.FAIL);
        payOrder.appendMsg("code:" + paymentResult.getCode() + " info:" + paymentResult.getInfo());
        return payOrder;
    }

    /**
     * 向G币中心发送请求，获取认证参数.
     * 
     * @param payOrder
     * @param gbAuthUrl
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> requestForAuthMap(PayOrder payOrder, String gbAuthUrl, String confirmKey) {
        // 请求openAuth的urlKey值如何生成
        String encryptResponse = HttpClientHelper.sendRequest(gbAuthUrl + "&t=7");
        LOG.info("[requestForAuthMap] success,request:{},encrypt response:{}", gbAuthUrl, encryptResponse);
        String decryptResponse;
        try {
            decryptResponse = EncryptUtils.decrypt(encryptResponse, confirmKey);
        } catch (Exception e) {
            LOG.error("[requestForAuthMap] error.", e);
            throw new PayException(Consts.SC.INTERNAL_ERROR, "解密G币中心认证数据失败");
        }
        LOG.info("[requestForAuthMap] success,request:{},decrypt response:{}", gbAuthUrl, decryptResponse);
        Map<String, Object> responseMap = JsonHelper.fromJson(decryptResponse, Map.class);
        // 如果请求成功，那么code值为1
        if (1 == MapUtils.getInteger(responseMap, gbBalanceConsts.CODE)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(gbBalanceConsts.URLKEY, MapUtils.getString(responseMap, gbBalanceConsts.URLKEY));
            map.put(gbBalanceConsts.URLTYPE, MapUtils.getString(responseMap, gbBalanceConsts.URLTYPE));
            LOG.info("[requestForAuthMap] return response:{}", map);
            return map;
        }
        throw new PayException(Consts.SC.CHANNEL_CONN_ERROR, "gb中心：获取认证key失败");
    }

    /**
     * 请求G币中心的确认接口.
     */
    @SuppressWarnings("unchecked")
    public static boolean requestAuthCallback(Map<String, String> map, String confirmKey) {
        Map<String, String> dataMap = new LinkedHashMap<String, String>();
        dataMap.put(gbBalanceConsts.CONFIRM, MapUtils.getString(map, gbBalanceConsts.CONFIRM));
        dataMap.put(gbBalanceConsts.URLTYPE, MapUtils.getString(map, gbBalanceConsts.URLTYPE));
        dataMap.put(gbBalanceConsts.DEDUCTSETTINGS, MapUtils.getString(map, gbBalanceConsts.DEDUCTSETTINGS));
        dataMap.put(gbBalanceConsts.T, "7");
        dataMap.put(gbBalanceConsts.URLKEY, MapUtils.getString(map, gbBalanceConsts.URLKEY));
        String toBeEncrypted = StringHelper.assembleResqStr(dataMap);
        try {
            String data = EncryptUtils.encrypt(toBeEncrypted, confirmKey);
            String url = gbBalanceConsts.ADDR_MOBILE_AUTH + "?data=" + URLEncoder.encode(data, "utf-8") + "&"
                    + "t=7&ver=1.2";
            String response = HttpClientHelper.sendRequest(url);
            Map<String, Object> responseMap = JsonHelper.fromJson(response, Map.class);
            // 如果请求成功，那么code值为1
            return 1 == MapUtils.getInteger(responseMap, gbBalanceConsts.CODE);
        } catch (Exception e) {
            LOG.error("[requestAuthCallback] error.", e);
            return false;
        }
    }
}