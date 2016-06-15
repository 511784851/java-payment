/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.kq.card;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.channel.kq.AbstractKqAdatper;
import com.guzhi.pay.channel.kq.KqConsts;
import com.guzhi.pay.channel.kq.KqHelper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 快钱卡密支付方式
 * 
 * @author administrator
 * @author administrator 2013-03-12
 */
public abstract class KqCardAdapter extends AbstractKqAdatper implements ChannelIF {

    private static final Logger LOG = LoggerFactory.getLogger(KqCardAdapter.class);

    public abstract String getPayType();

    public abstract String getBossType();

    public abstract String getAmountFlag();

    public abstract void assembleCardInfo(Map<String, String> request, PayOrder order);

    public abstract void updatePayOrder(String payUrl, PayOrder order);

    @Override
    public PayOrder pay(PayOrder order) {

        LOG.info("[KqCardAdapter.pay] with PayOrder:{}", order);

        Map<String, String> request = new HashMap<String, String>();
        // 协议参数
        request.put(KqConsts.KEY_INPUTCHARSET, KqConsts.KQ_INPUT_CHARSET_UTF8);
        request.put(KqConsts.KEY_BGURL, UrlHelper.removeLastSep(getguzhiPayNotify()) + KqConsts.ADDR_guzhiPay_CARD_NOTIFY);
        request.put(KqConsts.KEY_PAGEURL, UrlHelper.removeLastSep(getguzhiPayNotify()) + KqConsts.ADDR_guzhiPay_CARD_RETURN);
        request.put(KqConsts.KEY_VERSION, KqConsts.KQ_VERSION_2);
        request.put(KqConsts.KEY_LANGUAGE, KqConsts.KQ_LANGUAGE_CHN);
        request.put(KqConsts.KEY_SIGNTYPE, KqConsts.KQ_SIGN_TYPE_MD5);
        // 买卖双方信息参数
        AppChInfo appChInfo = order.getAppChInfo();
        request.put(KqConsts.KEY_MERCHANTACCTID, appChInfo.getChAccountId());
        request.put(KqConsts.KEY_PAYERNAME, StringHelper.encodeStr(order.getUserName()));
        request.put(KqConsts.KEY_PAYERCONTACTTYPE, "");
        request.put(KqConsts.KEY_PAYERCONTACT, "");
        // 业务参数
        String chOrderId = getChOrderId(order);
        order.setChOrderId(chOrderId);
        request.put(KqConsts.KEY_ORDERID, chOrderId);
        request.put(KqConsts.KEY_ORDERAMOUNT, StringHelper.getAmount(order.getAmount()));
        request.put(KqConsts.KEY_PAYTYPE, getPayType());
        assembleCardInfo(request, order);
        request.put(KqConsts.KEY_FULL_AMOUNT_FLAG, getAmountFlag());
        request.put(KqConsts.KEY_ORDERTIME, order.getSubmitTime());
        request.put(KqConsts.KEY_PRODUCTNAME, StringHelper.encodeStr(order.getProdName()));
        request.put(KqConsts.KEY_PRODUCTNUM, String.valueOf(1));
        // 先固定死,或许以后需要增加单价和数量这两个参数;
        // request.put(KqConsts.KEY_PRODUCTID,
        // StringHelper.encodeStr(order.getProdId()));
        request.put(KqConsts.KEY_PRODUCTDESC, StringHelper.encodeStr(order.getProdDesc()));
        request.put(KqConsts.KEY_EXT1, "");
        request.put(KqConsts.KEY_EXT2, "");
        request.put(KqConsts.KEY_BOSS_TYPE, getBossType());
        // 签名
        String sign = KqHelper.genPaySignForCard(request, appChInfo.getChPayKeyMd5());
        request.put(KqConsts.KEY_SIGN_MSG, sign);
        String payUrl = UrlHelper.addQuestionMark(getguzhiPayKqAddr() + KqConsts.ADDR_CARD_PAY)
                + StringHelper.assembleResqStr(request);
        order.setPayUrl(payUrl);
        order.setStatusCode(Consts.SC.PENDING);
        order.setStatusMsg("等待快钱通知");
        if (Consts.PayMethod.SZX.equalsIgnoreCase(order.getPayMethod())) {
            order.setAsyncPayTaskType(Task.TYPE_PAY_KQSZX);
        }
        // updatePayOrder(payUrl, order);
        return order;
    }

    @Override
    public String getQueryAddress() {
        return getguzhiPayKqAddr() + KqConsts.ADDR_WEBSERVICE_QUERY;
    }
}
