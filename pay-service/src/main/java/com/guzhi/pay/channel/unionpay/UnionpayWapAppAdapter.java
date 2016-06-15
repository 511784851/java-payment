/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.unionpay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.HttpRetryHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * @author 
 * 
 */
@Service("unionpayWapappAdapter")
public class UnionpayWapAppAdapter extends AbstractChannelIF {
    private final static Logger LOG = LoggerFactory.getLogger(UnionpayWapAppAdapter.class);
    private final static SimpleDateFormat sdf = new SimpleDateFormat("gbgbMMddHHmmss");

    @Value("${unionpayWapAppUrl}")
    private String unionpayWapAppUrl;

    public String getUnionpayWapAppUrl() {
        return unionpayWapAppUrl;
    }

    public void setUnionpayWapAppUrl(String unionpayWapAppUrl) {
        this.unionpayWapAppUrl = unionpayWapAppUrl;
    }

    @Override
    public String status() {
        return null;
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> req = new HashMap<String, String>();
        req.put(Constants.KEY_VERSION, Constants.VERSION);
        req.put(Constants.KEY_CHARSET, Constants.CHARSET_UTF8);
        req.put(Constants.KEY_TRANSTYPE, Constants.TRANSTYPE);
        req.put(Constants.KEY_MERID, payOrder.getChAccountId());
        req.put(Constants.KEY_BACKENDURL, getguzhiPayNotify() + Constants.APP_NOTIFYURL);
        // 银联暂时不提供前台通知的功能
        req.put(Constants.KEY_FRONTENDURL, getguzhiPayNotify() + Constants.APP_RETURNURL);
        req.put(Constants.KEY_ORDERDESCRIPTION, payOrder.getProdName());// 订单描述(可选)
        req.put(Constants.KEY_ORDERTIME, sdf.format(new Date()));// 交易开始日期时间gbgbMMddHHmmss
        req.put(Constants.KEY_ORDERNUMBER, payOrder.getChOrderId());
        req.put(Constants.KEY_ORDERAMOUNT, UnionpayWapHelper.generateCentAmountString(payOrder.getAmount()));
        String requestBody = UnionpayWapHelper.buildRequestBodyForApp(req, payOrder.getAppChInfo().getChPayKeyMd5());
        // String respString = HttpClientHelper.sendRequest(
        // UrlHelper.removeLastSep(getUnionpayWapAppUrl() +
        // Constants.APP_PAY_ADDR), requestBody,
        // Consts.CHARSET_UTF8, Consts.CHARSET_UTF8);
        // 使用重试
        String respString = HttpRetryHelper.sendRequest(
                UrlHelper.removeLastSep(getUnionpayWapAppUrl() + Constants.APP_PAY_ADDR), requestBody, 500);

        LOG.info("[pay] get response from unionpay,response:{},orderid:{}", respString, payOrder.getChOrderId());
        Map<String, String> params = UnionpayWapHelper.assembleResponseMapForApp(respString, payOrder.getAppChInfo()
                .getChPayKeyMd5());
        if (!params.containsKey(Constants.KEY_TN)) {
            LOG.info("[pay] get empty tn from unionpay,appid:{},apporderid{},params:{}", payOrder.getAppId(),
                    payOrder.getAppOrderId(), params);
            throw new PayException(Consts.SC.INTERNAL_ERROR, "get empty tn from unionpay.");
        }
        String payUrl = params.get(Constants.KEY_TN);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setPayUrl(payUrl);
        payOrder.setStatusMsg("等待用户支付或银联通知");
        LOG.info("[pay] get payurl success for unionpay wapapp,payOrder:{}", payOrder);
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        Map<String, String> req = new HashMap<String, String>();
        req.put(Constants.KEY_VERSION, Constants.VERSION);
        req.put(Constants.KEY_CHARSET, Constants.CHARSET_UTF8);
        req.put(Constants.KEY_TRANSTYPE, Constants.TRANSTYPE);
        req.put(Constants.KEY_MERID, payOrder.getChAccountId());
        req.put(Constants.KEY_ORDERTIME, sdf.format(new Date()));// 交易开始日期时间gbgbMMddHHmmss
        req.put(Constants.KEY_ORDERNUMBER, payOrder.getChOrderId());
        String requestBody = UnionpayWapHelper.buildRequestBodyForApp(req, payOrder.getAppChInfo().getChPayKeyMd5());
        LOG.info("[query] send request to unionpay,orderid:{},requestBody:{}", payOrder.getChOrderId(), requestBody);
        String respString = HttpClientHelper.sendRequest(
                UrlHelper.removeLastSep(getUnionpayWapAppUrl() + Constants.APP_QUERY_ADDR), requestBody,
                Consts.CHARSET_UTF8, Consts.CHARSET_UTF8);
        LOG.info("[query] get response from unionpay,orderid:{},responseBody:{}", payOrder.getChOrderId(), respString);
        Map<String, String> params = UnionpayWapHelper.stringToMap(respString, "&");
        UnionpayWapHelper.assembleAppPayOrder(payOrder, params);
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        // TODO Auto-generated method stub
        return null;
    }

}
