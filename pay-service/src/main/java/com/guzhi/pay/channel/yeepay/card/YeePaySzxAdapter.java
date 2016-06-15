/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.yeepay.card;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.guzhi.pay.channel.yeepay.YeePayConsts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.helper.TraceHelper;

/**
 * @author administrator
 * 
 */
@Component("yeepaySzxAdapter")
public class YeePaySzxAdapter extends AbstractYeePayAdapter {
    private static Logger LOG = LoggerFactory.getLogger(YeePaySzxAdapter.class);

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        LOG.info("[YibaoSzxAdapter.pay] in payOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        return super.pay(payOrder);
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        return super.query(payOrder);
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public String getPdFrpId() {
        return YeePayConsts.SZX;
    }

    /**
     * 创建易宝卡类异步充值任务。
     * 
     * @param payOrder
     */
    public void createPayTask(PayOrder payOrder) {
        payOrder.setAsyncPayTaskType(Task.TYPE_PAY_YEEPAYCARD);
    }
}
