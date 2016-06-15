/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.szf;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * @author 
 * 
 */
public abstract class AbstractSzfAdapter extends AbstractChannelIF {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSzfAdapter.class);
    private static final String VERSION = "3";
    private static final String VERIFYTYPE = "1";
    private static final DecimalFormat decimalFormat = new DecimalFormat("0");

    public abstract String getCardTypeCombine();

    public abstract void createPayTask(PayOrder order);

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        AppChInfo appChInfo = payOrder.getAppChInfo();
        Map<String, String> request = new HashMap<String, String>();
        // 生成chOrderId
        String chOrderId = payOrder.getChOrderId();
        if (StringUtils.isBlank(chOrderId)) {
            chOrderId = OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId());
            payOrder.setChOrderId(chOrderId);
        }
        request.put(SzfConsts.KEY_VERSION, VERSION);
        request.put(SzfConsts.KEY_MERID, appChInfo.getChAccountId());
        // {@link com.guzhi.pay.helper.StringHelper}
        // getAmount()已经封装了DecimalFormat。可直接用。
        request.put(SzfConsts.KEY_PAYMONEY, decimalFormat.format(payOrder.getAmount().doubleValue() * 100) + "");
        request.put(SzfConsts.KEY_ORDERID, chOrderId);
        request.put(SzfConsts.KEY_RETURNURL, UrlHelper.removeLastSep(getguzhiPayNotify()) + SzfConsts.ADDR_guzhiPay_NOTIFY);
        request.put(SzfConsts.KEY_CARDINFO, SzfHelper.assembleCardInfo(payOrder));
        request.put(SzfConsts.KEY_PRIVATEFIELD, payOrder.getAppOrderId());
        request.put(SzfConsts.KEY_VERIFYTYPE, VERIFYTYPE);
        // 日志中不能存在卡密码
        payOrder.setCardPass("");
        LOG.info("[AbstractSzfAdapter.pay] with PayOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        String signMsg = SzfHelper.genPaySign(request, appChInfo.getChPayKeyMd5());
        request.put(SzfConsts.KEY_MD5STRING, signMsg);
        request.put(SzfConsts.KEY_CARDTYPECOMBINE, getCardTypeCombine());
        String payUrl = SzfConsts.ADDR_SZF_PAY + StringHelper.assembleResqStr(request);
        LOG.info("[AbstractSzfAdapter.pay] with payUrl:{}", payUrl, TraceHelper.getTrace(payOrder));
        {
            payOrder.setPayUrl(payUrl);
            payOrder.setStatusCode(Consts.SC.PENDING);
            payOrder.setStatusMsg("等待神州付通知");
            createPayTask(payOrder);
        }
        // String respStr = HttpClientHelper.sendRequest(payUrl,
        // Consts.CHARSET_UTF8);
        // SzfHelper.updatePayOrderByPay(payOrder, respStr);
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        LOG.info("[AbstractSzfAdapter.query] with PayOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        AppChInfo appChInfo = payOrder.getAppChInfo();
        Map<String, String> request = new HashMap<String, String>();
        request.put(SzfConsts.KEY_VERSION, VERSION);
        request.put(SzfConsts.KEY_MERID, appChInfo.getChAccountId());
        request.put(SzfConsts.KEY_ORDERID, payOrder.getChOrderId());
        String signMsg = SzfHelper.genQuerySign(request, appChInfo.getChPayKeyMd5());
        request.put(SzfConsts.KEY_MD5STRING, signMsg);
        String queryUrl = SzfConsts.ADDR_SZF_QUERY + StringHelper.assembleResqStr(request);
        LOG.info("[AbstractSzfAdapter.query] with queryUrl:{}", queryUrl, TraceHelper.getTrace(payOrder));
        String respStr = HttpClientHelper.sendRequest(queryUrl, Consts.CHARSET_UTF8);
        SzfHelper.updateQueryOrderByPay(payOrder, respStr);
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }
}
