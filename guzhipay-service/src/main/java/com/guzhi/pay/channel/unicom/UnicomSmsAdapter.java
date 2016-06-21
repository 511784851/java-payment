/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.unicom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.channel.sms.SmsConsts;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpRetryHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.TimeHelper;

/**
 * @author administrator
 * 
 */
@Service("unicomSmsAdapter")
public class UnicomSmsAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(UnicomSmsAdapter.class);

    @Value("${unicomAddress}")
    private String unicomAddress;

    @Override
    public String status() {
        return null;
    }

    /** 验证码类型 **/
    private static final String VERIFY_TYPE = "10023";
    private static final String RETURN_CODE = "returnCode";
    private static final String VERIFY_URI = "/v1/verifyCode/sendVerifyCode";
    private static final String BUY_URI = "/v1/product/buyProductWithVCode";
    private static final String VERIFY_SUCCESS_RESPONSE = "000000";
    private static final Map<String, String> RESPONSE_MAPPING = new HashMap<String, String>() {
        private static final long serialVersionUID = -8367522644846395879L;

        {
            put(null, "未知错误");
            put("000000", "成功");
            put("200001", "输入的必选参数为空");
            put("200002", "参数格式错误");
            put("100001", "系统忙");
            put("100005", "数据库系统异常");
            put("400017", "验证码类型不可用");
            put("803014", "内容状态不正常");
            put("803015", "用户状态不正常");
            put("803016", "用户已经注销");
            put("301001", "用户不存在");
            put("803018", "内容无效");
            put("770012", "验证码错误或已过时");
            put("770004", "该渠道不允许操作该产品");
        }
    };

    private String buildUrl(TreeMap<String, String> params, String uri, String md5Key) {
        StringBuffer sb = new StringBuffer();
        StringBuffer psb = new StringBuffer();
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String val = params.get(key);
            sb.append(key).append(val);
            psb.append("&").append(key).append("=").append(val);
        }
        sb.append(md5Key);
        String digest = MD5Utils.getMD5(sb.toString()).toUpperCase();
        return unicomAddress + uri + "?digest=" + digest + psb.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PayOrder pay(PayOrder order) {

        order.setChOrderId(getChOrderId(order));

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appkey", order.getAppChInfo().getChAccountsKeyMd5());
        params.put("timestamp", TimeHelper.getFormattedTime());
        params.put("callNumber", JsonHelper.fromJson(order.getUserContact(), SmsConsts.KEY_TEL));
        params.put("verifyType", VERIFY_TYPE);
        params.put("verifyParam", order.getProdId());

        String targetUrl = buildUrl(params, VERIFY_URI, order.getAppChInfo().getChPayKeyMd5());

        StringBuffer logSb = new StringBuffer("[pay] request:" + targetUrl);
        Map<String, Object> returnMap = null;
        try {
            String response = HttpRetryHelper.sendRequest(targetUrl, 5000, 10000);
            logSb.append(" response:" + response);
            returnMap = JsonHelper.fromJson(response, Map.class);
        } catch (Exception e) {
            LOG.error(logSb.toString() + " error ", e);
        }
        if (returnMap == null) {
            order.setStatusCode(Consts.SC.CONN_ERROR);
            order.setStatusMsg("连接异常");
        } else {
            if (VERIFY_SUCCESS_RESPONSE.equals(returnMap.get(RETURN_CODE))) {
                order.setStatusCode(Consts.SC.PENDING);
                order.setStatusMsg("等待输入验证码");
            } else {
                order.setStatusCode(Consts.SC.FAIL);
                order.setStatusMsg(RESPONSE_MAPPING.get(returnMap.get(RETURN_CODE)));
            }
        }
        logSb.append(" return: " + ToStringBuilder.reflectionToString(order));
        LOG.info(logSb.toString());
        return order;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        if (StringUtils.isBlank(payOrder.getStatusCode())) {
            payOrder.setStatusCode(Consts.SC.PENDING);
        }
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new PayException(Consts.SC.UNKNOWN, "unicom refund unimplment");
    }

    @SuppressWarnings("unchecked")
    public PayOrder confirmPay(PayOrder order, String verifyCode) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appkey", order.getAppChInfo().getChAccountsKeyMd5());
        params.put("timestamp", TimeHelper.getFormattedTime());
        params.put("callNumber", JsonHelper.fromJson(order.getUserContact(), SmsConsts.KEY_TEL));
        params.put("verifyCode", verifyCode);
        params.put("productId", order.getProdId());

        String targetUrl = buildUrl(params, BUY_URI, order.getAppChInfo().getChPayKeyMd5());

        StringBuffer logSb = new StringBuffer("[confirmPay] request verifyCode:" + verifyCode + " appOrderId:"
                + order.getAppOrderId());

        Map<String, Object> returnMap = null;
        try {
            String response = HttpRetryHelper.sendRequest(targetUrl, 5000, 10000);
            logSb.append(" response: " + response);
            returnMap = JsonHelper.fromJson(response, Map.class);
        } catch (Exception e) {
            LOG.error(logSb.toString() + "  error  ", e);
        }

        if (returnMap == null) {
            order.setStatusCode(Consts.SC.FAIL);
            order.setStatusMsg("扣费请求失败");
        } else {

            if (VERIFY_SUCCESS_RESPONSE.equals(returnMap.get(RETURN_CODE))) {
                order.setStatusCode(Consts.SC.SUCCESS);
                order.setStatusMsg("扣费成功");
                order.setChDealTime(TimeHelper.getFormattedTime());
                order.setChDealId(order.getChDealTime());
            } else {
                order.setStatusCode(Consts.SC.FAIL);
                order.setStatusMsg(RESPONSE_MAPPING.get(returnMap.get(RETURN_CODE)));
            }
        }
        logSb.append(" return: " + ToStringBuilder.reflectionToString(order));
        LOG.info(logSb.toString());
        return order;
    }
}
