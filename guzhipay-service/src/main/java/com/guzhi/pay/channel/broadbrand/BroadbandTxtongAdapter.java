/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.broadbrand;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 新泛联天下通
 * 参考《游戏卡供货接口说明》开发，版本v1.0
 * 具体见文件:游戏卡供货接口(4).docx
 * 
 * @author administrator
 * 
 */
@Service("broadbandTxtongAdapter")
public class BroadbandTxtongAdapter extends AbstractChannelIF {

    @Value("${broadbandTxtongPayAddr}")
    private String broadBandTxtongPayAddr;

    @Value("${broadbandTxtongQueryAddr}")
    private String broadBandTxtongQueryAddr;

    @Resource
    private DomainResource resource;

    private static final Logger LOG = LoggerFactory.getLogger(BroadbandTxtongAdapter.class);

    @Override
    public String status() {
        return null;
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> requestMap = new LinkedHashMap<String, String>();
        requestMap.put(BroadbandConsts.KEY_CARD_NUM, payOrder.getCardNum());
        String cardPass = DESEncrypt.decryptByAES(payOrder.getAppInfo().getPasswdKey(), payOrder.getCardPass());
        String desKey = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), BroadbandConsts.DESKEY);
        String password = BroadbandHelper.generateHexPassword(cardPass, desKey);
        requestMap.put(BroadbandConsts.KEY_CARD_PWD, password);
        requestMap.put(BroadbandConsts.KEY_GAME_ID, BroadbandConsts.GAMEID);
        requestMap.put(BroadbandConsts.KEY_MCH_ID, payOrder.getAppChInfo().getChAccountId());
        requestMap.put(BroadbandConsts.KEY_MCH_ORDER_ID, payOrder.getChOrderId());
        requestMap.put(BroadbandConsts.KEY_PAR, "" + payOrder.getAmount().intValue());
        requestMap.put(BroadbandConsts.KEY_RETURN_URL, UrlHelper.removeLastSep(getPayNotify())
                + BroadbandConsts.NOTIFY_URL);
        requestMap.put(BroadbandConsts.KEY_VERSION, BroadbandConsts.VERSION);
        String sign = BroadbandHelper.getSign(requestMap, payOrder.getAppChInfo().getChPayKeyMd5());
        requestMap.put(BroadbandConsts.KEY_SIGN, sign);
        String payUrl = UrlHelper.addQuestionMark(broadBandTxtongPayAddr) + StringHelper.assembleResqStr(requestMap);
        payOrder.setPayUrl(payUrl);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setAsyncPayTaskType(Task.TYPE_PAY_ASYNC);
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        Map<String, String> requestMap = new LinkedHashMap<String, String>();
        requestMap.put(BroadbandConsts.KEY_CARD_NUM, payOrder.getCardNum());
        requestMap.put(BroadbandConsts.KEY_DATE, StringUtils.left(payOrder.getAppOrderTime(), 8));
        requestMap.put(BroadbandConsts.KEY_GAME_ID, BroadbandConsts.GAMEID);
        requestMap.put(BroadbandConsts.KEY_MCH_ID, payOrder.getChAccountId());
        requestMap.put(BroadbandConsts.KEY_MCH_ORDER_ID, payOrder.getChOrderId());
        requestMap.put(BroadbandConsts.KEY_VERSION, BroadbandConsts.VERSION);
        String sign = BroadbandHelper.getSign(requestMap, payOrder.getAppChInfo().getChPayKeyMd5());
        requestMap.put(BroadbandConsts.KEY_SIGN, sign);
        String queryUrl = UrlHelper.addQuestionMark(broadBandTxtongQueryAddr)
                + StringHelper.assembleResqStr(requestMap);
        LOG.info("[broadbandTxtong.query] send query request,orderid:{},queryurl:{}", payOrder.getChOrderId(), queryUrl);
        String respStr = HttpClientHelper.sendRequest(queryUrl);
        LOG.info("[broadbandTxtong.query] get query response,orderid:{},queryurl:{},response:{}",
                payOrder.getChOrderId(), queryUrl, respStr);
        BroadbandHelper.updatePayOrderByNotify(payOrder, respStr);
        LOG.info("[broadbandTxtong.query] after query.payOrder:{}", payOrder);
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        return payOrder;
    }
}
