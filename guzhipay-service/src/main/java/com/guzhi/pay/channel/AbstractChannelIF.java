/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.OrderIdHelper;

/**
 * @author Administrator
 * 
 */
public abstract class AbstractChannelIF implements ChannelIF {

    @Autowired
    protected DomainResource resource;
    // 增加通知常量
    @Value("${payNotify}")
    protected String payNotify;

    public String getPayNotify() {
        return payNotify;
    }

    public void setPayNotify(String payNotify) {
        this.payNotify = payNotify;
    }

    /**
     * 生成chOrderId
     * 
     * @param payOrder
     * @return
     */
    public String getChOrderId(PayOrder payOrder) {
        String chOrderId = payOrder.getChOrderId();
        if (StringUtils.isBlank(chOrderId)) {
            chOrderId = OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId());
        }
        return chOrderId;
    }

}
