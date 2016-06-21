/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.th;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 天宏一卡通充值方式
 * 
 * @author administrator
 * 
 */
@Service("thYktAdapter")
public class ThYktAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(ThYktAdapter.class);

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        LOG.info("[ThYktAdapter] with PayOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        AppChInfo appChInfo = payOrder.getAppChInfo();
        Map<String, String> request = new HashMap<String, String>();
        // 生成chOrderId
        String chOrderId = payOrder.getChOrderId();
        if (StringUtils.isBlank(chOrderId)) {
            chOrderId = OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId());
            payOrder.setChOrderId(chOrderId);
        }
        if (StringUtils.isBlank(payOrder.getCardNum()) || StringUtils.isBlank(payOrder.getCardPass())) {
            throw new PayException(Consts.SC.DATA_ERROR, "支付平台：天宏卡一卡通卡密不能为空");
        }
        String pass = DESEncrypt.decryptByAES(payOrder.getAppInfo().getPasswdKey(), payOrder.getCardPass());
        request.put(ThConsts.USERNAME, appChInfo.getChAccountId());
        request.put(ThConsts.PRODUCTID,
                JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), ThConsts.PRODUCTID));
        // 天宏卡都是大写的卡号，现在先强制转换
        String[] kahaos = payOrder.getCardNum().toUpperCase().split(ThConsts.SPLIT_SYMBOL);
        String[] mimas = pass.split(ThConsts.SPLIT_SYMBOL);
        // 日志不应该记录卡号与卡密
        // LOG.info("pass:{},mimas:{}", pass, mimas);
        ThYktHelper.validateKahaoAndMima(kahaos, mimas);
        request.put(ThConsts.CKNUM, String.valueOf(kahaos.length));
        // 循环加入
        for (int i = 0; i < kahaos.length; i++) {
            request.put(ThConsts.KAOHAO + String.valueOf(i + 1), kahaos[i]);
            request.put(ThConsts.MIMA + String.valueOf(i + 1), MD5Utils.getMD5(mimas[i]).toUpperCase());
        }
        request.put(ThConsts.BUYNUM, payOrder.getAmount().intValue() + "");
        request.put(ThConsts.ORDERNUM, chOrderId);
        // TODO 天宏那边有点怪，想encode再进行签名(目前不需要returnUrl,所以没有特殊字符,不需要encode)
        request.put(ThConsts.MD5TOSELF, ThYktHelper.genPaySign(request, appChInfo.getChPayKeyMd5()));
        String payUrl = UrlHelper.addQuestionMark(ThConsts.ADD_PAY) + StringHelper.assembleResqStr(request);
        LOG.info("[ThYktAdapter] with payUrl:{}", payUrl, TraceHelper.getTrace(payOrder));
        payOrder.setPayUrl(payUrl);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setStatusMsg("等待天宏通知");
        // 异步充值
        payOrder.setAsyncPayTaskType(Task.TYPE_PAY_THYKT);

        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        LOG.info("[ThYktAdapter] with query:{}", payOrder, TraceHelper.getTrace(payOrder));
        AppChInfo appChInfo = payOrder.getAppChInfo();
        Map<String, String> request = new HashMap<String, String>();
        request.put(ThConsts.USERNAME_Q, appChInfo.getChAccountId());
        request.put(ThConsts.ORDERNUM_Q, payOrder.getChOrderId());
        request.put(ThConsts.MD5TOSELF, ThYktHelper.getQuerySign(request, appChInfo.getChPayKeyMd5()));
        String queryUrl = UrlHelper.addQuestionMark(ThConsts.ADD_QUERY) + StringHelper.assembleResqStr(request);
        LOG.info("[ThYktAdapter] with queryUrl:{}", queryUrl, TraceHelper.getTrace(payOrder));
        String respStr = HttpClientHelper.sendRequest(queryUrl, Consts.CHARSET_UTF8);
        ThYktHelper.updatePayOrderByQuery(payOrder, respStr);
        LOG.info("[ThYktAdapter] with after query PayOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

}
