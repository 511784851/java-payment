/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.kq.card;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.channel.kq.KqConsts;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.DESEncrypt;

/**
 * 快钱神州卡支付方式
 * 
 * @author administrator
 * @author administrator 2013-03-12
 */
@Service("kqSzxAdapter")
public class KqSzxAdapter extends KqCardAdapter implements ChannelIF {

    private static final Logger LOG = LoggerFactory.getLogger(KqSzxAdapter.class);

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void assembleCardInfo(Map<String, String> request, PayOrder order) {
        if (StringUtils.isBlank(order.getCardNum()) || StringUtils.isBlank(order.getCardPass())) {
            throw new PayException(Consts.SC.DATA_ERROR, "支付平台：快钱神州行卡密不能为空");
        }

        request.put(KqConsts.KEY_CARD_NUM, order.getCardNum());
        request.put(KqConsts.KEY_CARD_PWD,
                DESEncrypt.decryptByAES(order.getAppInfo().getPasswdKey(), order.getCardPass()));
    }

    @Override
    public String getBossType() {
        return KqConsts.KQ_SZX_BOSS_TYPE;
    }

    @Override
    public String getPayType() {
        return KqConsts.KQ_SZX_CARD_PAY_TYPE;
    }

    @Override
    public void updatePayOrder(String payUrl, PayOrder order) {
        {
            order.setPayUrl(payUrl);
            order.setStatusCode(Consts.SC.PENDING);
            order.setStatusMsg("等待快钱通知");
        }
        // String respStr = HttpClientHelper.sendRequest(payUrl,
        // Consts.CHARSET_UTF8);
        // LOG.info("[KqSzxAdapter.updatePayOrder] with respStr: {}", respStr,
        // TraceHelper.getTrace(order));
        // KqHelper.assemblePayCardPayOrder(order, respStr);
        // LOG.info("[KqSzxAdapter.updatePayOrder] return PayOrder: {}", order,
        // TraceHelper.getTrace(order));
    }

    @Override
    public String getAmountFlag() {
        return KqConsts.KQ_FULL_AMOUNT_FLAG_TRUE;
    }

}
