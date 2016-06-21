/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.szf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;

/**
 * 通过神州付渠道使用神州行充值卡。
 * 
 * @author administrator
 * 
 */
@Service("szfSzxAdapter")
public class SzfSzxAdapter extends AbstractSzfAdapter {
    @Autowired
    DomainResource resource;
    private static final Logger LOG = LoggerFactory.getLogger(SzfSzxAdapter.class);
    private static final String CARDTYPECOMBINE = "0";

    public String getCardTypeCombine() {
        return CARDTYPECOMBINE;
    }

    /**
     * 创建神州付异步充值任务。
     * 
     * @param payOrder
     */
    public void createPayTask(PayOrder payOrder) {
        payOrder.setAsyncPayTaskType(Task.TYPE_PAY_SZFCARD);
    }
}
