/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.gb;

import javax.annotation.Resource;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.UrlHelper;
import com.guzhi.pay.thrift.udb.UdbUserInfoService;

/**
 * 移动端gb渠道.
 * 
 * @author
 * 
 */
@Service("gbWapbalanceAdapter")
public class GbWapBalanceAdapter extends AbstractChannelIF {

    private final static String DELIMITTER = "-";

    @Resource
    private gbService gbService;

    @Resource
    private UdbUserInfoService udbService;

    @Override
    public String status() {
        return null;
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        // 遗留问题，G币渠道和其他渠道的渠道订单号生成规则不一同.
        // 为了和gate系统统一，tpay在G币中心的订单生成规则为"appId"+"-"+"appOrderId".
        payOrder.setChOrderId(payOrder.getAppId() + DELIMITTER + payOrder.getAppOrderId());
        String callbackUrl = UrlHelper.removeLastSep(getguzhiPayNotify()) + gbBalanceConsts.ADDR_guzhiPay_gbNOTIFY;
        AppChInfo gbAppChInfo = payOrder.getAppChInfo();
        gbService.payMoney(payOrder, gbAppChInfo, callbackUrl);
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        String passport = JsonHelper.fromJson(payOrder.getUserId(), "passport");
        if (StringUtils.isBlank(passport)) {
            String gbuid = JsonHelper.fromJson(payOrder.getUserId(), "gbuid");
            if (StringUtils.isNotBlank(gbuid)) {
                passport = udbService.getPassportByUid(gbuid);
            }
        }
        AppChInfo gbChInfo = payOrder.getAppChInfo();
        return gbService.quergbbOrder(payOrder, passport, gbChInfo);
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        return null;
    }
}