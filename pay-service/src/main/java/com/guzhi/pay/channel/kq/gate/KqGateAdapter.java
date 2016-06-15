/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.kq.gate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.channel.kq.AbstractKqAdatper;
import com.guzhi.pay.channel.kq.KqConsts;
import com.guzhi.pay.channel.kq.KqHelper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 快钱网关支付方式
 * 当前数据库中的商户号是处理过的（即添加了01后缀的）
 * 
 * @author administrator
 * @author administrator 2013-03-12
 */
@Service("kqGateAdapter")
public class KqGateAdapter extends AbstractKqAdatper implements ChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(KqGateAdapter.class);
    private static final String TIME_STAMP_QUERY_URL = "timestampQueryUrl";

    @Override
    public PayOrder pay(PayOrder order) {
        LOG.info("[KqGateAdapter.pay] with PayOrder:{}", order);
        Map<String, String> request = new HashMap<String, String>();
        // 协议参数
        request.put(KqConsts.KEY_INPUTCHARSET, KqConsts.KQ_INPUT_CHARSET_UTF8);
        request.put(KqConsts.KEY_BGURL, UrlHelper.removeLastSep(getguzhiPayNotify()) + KqConsts.ADDR_guzhiPay_NOTIFY); // UrlHelper.removeLastSep("http://172.19.51.71")
        request.put(KqConsts.KEY_PAGEURL, UrlHelper.removeLastSep(getguzhiPayNotify()) + KqConsts.ADDR_guzhiPay_RETURN);
        request.put(KqConsts.KEY_VERSION, KqConsts.KQ_VERSION_2);
        request.put(KqConsts.KEY_LANGUAGE, KqConsts.KQ_LANGUAGE_CHN);
        request.put(KqConsts.KEY_SIGNTYPE, KqConsts.KQ_SIGN_TYPE_PKI);
        AppChInfo appChInfo = order.getAppChInfo();
        request.put(KqConsts.KEY_MERCHANTACCTID, appChInfo.getChAccountId());
        request.put(KqConsts.KEY_PAYERNAME, order.getUserName());
        request.put(KqConsts.KEY_PAYERCONTACTTYPE, "");
        request.put(KqConsts.KEY_PAYERCONTACT, "");
        request.put(KqConsts.KEY_PAYERIP, order.getUserIp());
        // 业务参数
        order.setChOrderId(getChOrderId(order));
        request.put(KqConsts.KEY_ORDERID, order.getChOrderId());
        request.put(KqConsts.KEY_ORDERAMOUNT, StringHelper.getAmount(order.getAmount()));
        request.put(KqConsts.KEY_ORDERTIME, String.valueOf(order.getSubmitTime()));
        request.put(KqConsts.KEY_ORDERTIMESTAMP, String.valueOf(getOrderTimeStamp(appChInfo)));
        request.put(KqConsts.KEY_PRODUCTNAME, order.getProdName());
        request.put(KqConsts.KEY_PRODUCTNUM, String.valueOf(1));
        // 先固定死,或许以后需要增加单价和数量这两个参数;
        request.put(KqConsts.KEY_PRODUCTID, order.getProdId());
        request.put(KqConsts.KEY_PRODUCTDESC, order.getProdDesc());
        request.put(KqConsts.KEY_EXT1, "");
        request.put(KqConsts.KEY_EXT2, "");
        request.put(KqConsts.KEY_PAYTYPE, KqConsts.KQ_PAY_TYPE_BANK);
        if (StringUtils.isNotBlank(order.getBankId())) {
            request.put(KqConsts.KEY_BANKID, order.getBankId());
        }
        request.put(KqConsts.KEY_REDOFLAG, KqConsts.KQ_REDO_FLAG_NOT_REPEAT);
        request.put(KqConsts.KEY_PID, ""); // appChInfo.getChId()
        String privateKeyFilePath = JsonHelper.fromJson(order.getAppChInfo().getAdditionalInfo(),
                KqConsts.KEY_PRIVATE_KEY_FILE_PATH);
        // 签名
        request.put(KqConsts.KEY_SIGN_MSG,
                KqHelper.genPaySign(request, privateKeyFilePath, order.getAppChInfo().getChPayKeyMd5()));
        // 构建pay url
        String payUrl = UrlHelper.removeLastSep(getguzhiPayKqAddr() + KqConsts.ADDR_PAY) + "?"
                + UrlHelper.assembleQueryStr(request); // MapToQueryStringHelper.convert(request);
        order.setPayUrl(payUrl.toString());
        order.setStatusCode(Consts.SC.PENDING);
        order.setStatusMsg("等待用户支付，或等待快钱通知");
        LOG.info("[KqGateAdapter.pay] return PayOrder: {}", order);
        return order;// TODO: audit log?
    }

    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

    private String getOrderTimeStamp(AppChInfo appChInfo) {
        // 获取时间戳
        String timeStampQueryUrl = KqConsts.DEFAULT_TIME_STAMPE_QUERY_URL;
        @SuppressWarnings("unchecked")
        Map<String, String> additionalInfoMaps = JsonHelper.fromJson(appChInfo.getAdditionalInfo(), Map.class);
        if (additionalInfoMaps != null && additionalInfoMaps.containsKey(TIME_STAMP_QUERY_URL)) {
            timeStampQueryUrl = additionalInfoMaps.get(TIME_STAMP_QUERY_URL);
        }
        String orderTimestamp = KqHelper.getOrderTimestamp(timeStampQueryUrl);
        return orderTimestamp;
    }

    @Override
    public String getQueryAddress() {
        return getguzhiPayKqAddr() + KqConsts.ADDR_QUERY;
    }
}
