/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.channel.broadbrand.BroadbandHelper;
import com.guzhi.pay.channel.jw.JwHelper;
import com.guzhi.pay.channel.kq.KqHelper;
import com.guzhi.pay.channel.szf.SzfHelper;
import com.guzhi.pay.channel.th.ThYktHelper;
import com.guzhi.pay.channel.vpay.VpayHelper;
import com.guzhi.pay.channel.yeepay.YeePayHelper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.OrderIdHelper;

/**
 * @author administrator
 *         异步充值定时任务<br>
 *         适用于同步返回充值最终结果的渠道<br>
 *         为了更快地返回结果给业务方，不用同步等待渠道的支付结果<br>
 *         第三方渠道的服务质量差别很大，如果使用同步的方式，将会导致支付平台的服务质量下降。下面举例说明：<br>
 *         1)支付报文丢失，支付失败，而业务完全具备成功支付的条件，<br>
 *         2)支付请求响应时间不定，如果超过一定时间，可能会被异常终止。<br>
 *         当前支持异步充值任务的渠道包括
 *         <ol>
 *         <li>骏网骏卡</li>
 *         <li>天宏一卡通</li>
 *         <li>易宝卡类</li>
 *         <li>神州付卡类</li>
 *         <li>快钱神州行</li>
 *         <li>新泛联天下通</li>
 *         </ol>
 */
public class AsynPayTaskRunnable implements Runnable {
    private Task task;
    private DomainResource resource;
    private static final Logger logger = LoggerFactory.getLogger(AsynPayTaskRunnable.class);

    public AsynPayTaskRunnable(Task task, DomainResource resource) {
        this.task = task;
        this.resource = resource;
    }

    @Override
    public void run() {
        try {
            logger.info("[AsynPay.start] start to run task,task:{}", task);
            String appId = task.getAppId();
            String appOrderId = task.getAppOrderId();
            PayOrder payOrder = Help.getPayOrderByNotify(resource, OrderIdHelper.genChOrderId(appId, appOrderId));
            logger.debug("[AsynPay.assemble] assemble payorder,payorder:{}", payOrder);
            if (!Consts.SC.PENDING.equalsIgnoreCase(payOrder.getStatusCode())) {
                // 如果订单状态被更新过，那么删除该任务。
                deleteAsynPayTask(task);
                return;
            }
            String respStr = sendPayRequest(payOrder);
            if (StringUtils.isBlank(respStr)) {
                TaskUtils.refreshEqualInterval(resource, task, Consts.Task.EQUAL_INTERVAL_SECONDS);
                return;
            }
            updatePayOrder(payOrder, respStr);
            resource.updatePayOrder(payOrder);
            logger.info("[AsynPay.finish] after pay,PayOrder:{}", payOrder);
            // 如果订单支付请求已经提交成功，应该删除该异步充值任务本身。
            deleteAsynPayTask(task);
            // 如果订单状态被更新过，那么创建通知任务
            if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
                TaskHelper.createAfterPaySuccessTask(resource, payOrder);
            } else if (Consts.SC.PENDING.equalsIgnoreCase(payOrder.getStatusCode())) {
                return;
            } else {
                TaskHelper.createNotifyTask(resource, payOrder);
            }
        } catch (Throwable t) {
            logger.error("run AsynPayTaskRunnable error msg:" + t.getMessage() + " apporderid:" + task.getAppOrderId()
                    + " appid:" + task.getAppId() + " type:" + task.getType(), t);
            TaskUtils.refreshEqualInterval(resource, task, Consts.Task.EQUAL_INTERVAL_SECONDS);
        }
    }

    /**
     * 选择相应的helper来update payOrder
     * 
     * @param payOrder
     * @param respStr
     */
    private void updatePayOrder(PayOrder payOrder, String respStr) {
        String chId = payOrder.getChId();
        String payMethod = payOrder.getPayMethod();

        if (StringUtils.isBlank(chId) || StringUtils.isBlank(payMethod)) {
            logger.warn("[AsynPay.update]: chId or payMethod is empty:{}", respStr);
            return;
        }

        if (chId.equalsIgnoreCase(Consts.Channel.JW) && payMethod.equalsIgnoreCase(Consts.PayMethod.JK)) {
            AppChInfo appChInfo = payOrder.getAppChInfo();
            JwHelper.updatePayOrderByPay(payOrder, respStr, appChInfo.getChPayKeyMd5());
            return;
        }
        if (chId.equalsIgnoreCase(Consts.Channel.TH) && payMethod.equalsIgnoreCase(Consts.PayMethod.YKT)) {
            ThYktHelper.updatePayOrderByPay(payOrder, respStr);
            return;
        }
        if (chId.equalsIgnoreCase(Consts.Channel.YEEPAY) && payMethod.equalsIgnoreCase(Consts.PayMethod.SZX)) {
            YeePayHelper.upatePayOrderByCardPayResponse(payOrder, respStr);
            return;
        }
        if (chId.equalsIgnoreCase(Consts.Channel.YEEPAY) && payMethod.equalsIgnoreCase(Consts.PayMethod.LT)) {
            YeePayHelper.upatePayOrderByCardPayResponse(payOrder, respStr);
            return;
        }
        if (chId.equalsIgnoreCase(Consts.Channel.SZF) && payMethod.equalsIgnoreCase(Consts.PayMethod.SZX)) {
            SzfHelper.updatePayOrderByPay(payOrder, respStr);
            return;
        }
        if (chId.equalsIgnoreCase(Consts.Channel.SZF) && payMethod.equalsIgnoreCase(Consts.PayMethod.LT)) {
            SzfHelper.updatePayOrderByPay(payOrder, respStr);
            return;
        }
        if (chId.equalsIgnoreCase(Consts.Channel.KQ) && payMethod.equalsIgnoreCase(Consts.PayMethod.SZX)) {
            KqHelper.assemblePayCardPayOrder(payOrder, respStr);
            return;
        }

        if (chId.equalsIgnoreCase(Consts.Channel.VPAY) && payMethod.equalsIgnoreCase(Consts.PayMethod.SMS)) {
            VpayHelper.updateSmsPayOrderWithPayResponse(respStr, payOrder);
            return;
        }

        if (chId.equalsIgnoreCase(Consts.Channel.BROADBAND) && payMethod.equalsIgnoreCase(Consts.PayMethod.TXTONG)) {
            BroadbandHelper.updatePayOrderByPay(payOrder, respStr);
        }
    }

    private static final Logger HIIDO_LOG = LoggerFactory.getLogger("hiido_statistics");

    /**
     * 同步发送支付请求
     * 
     * @param payOrder
     */
    private String sendPayRequest(PayOrder payOrder) {
        String response = "";
        long timeStart = System.currentTimeMillis();
        try {
            response = HttpClientHelper.sendRequest(payOrder.getPayUrl(),
                    getCharsetForRequest(payOrder.getChId(), payOrder.getPayMethod()));
            logger.info("[AsynPay.sendRequest] get response,task:{},response:{}", task, response);
        } catch (Throwable e) {
            logger.error("[AsynPay.sendRequest] get exception,task:{},url={} ,e={}", task, payOrder.getPayUrl(), e);
        } finally {
            HIIDO_LOG.info("tpay;3;" + payOrder.getAppId() + ";/asynPay.do;" + (System.currentTimeMillis() - timeStart)
                    + ";;;" + payOrder.getChId() + ";" + payOrder.getPayMethod() + ";");
        }
        return response;
    }

    /**
     * 从数据库中删除异步充值任务<br>
     */
    private void deleteAsynPayTask(Task task) {
        logger.info("[AsynPay.delete] delete task,task type:{},appid:{},apporderid:{}", task.getType(),
                task.getAppId(), task.getAppOrderId());

        // 当前执行的任务肯定是异步充值任务，直接删除即可，不用判断任务类型。
        resource.deleteTask(task.getAppId(), task.getAppOrderId(), task.getType());
    }

    /**
     * 为渠道选择编码方式<br>
     * 易宝卡类支付需要使用GBK编码，其他渠道使用UTF8编码。<br>
     * 为兼容后续可能的相同渠道不同paymethod使用不同的编码，本方法包括参数paymethod。<br>
     * 
     * @param chId
     * @param paymethod
     * @return
     */
    private String getCharsetForRequest(String chId, String paymethod) {
        if (Consts.Channel.YEEPAY.equalsIgnoreCase(chId)) {
            if (Consts.PayMethod.SZX.equalsIgnoreCase(paymethod)) {
                return Consts.CHARSET_GBK;
            }
            if (Consts.PayMethod.LT.equalsIgnoreCase(paymethod)) {
                return Consts.CHARSET_GBK;
            }
        }
        return Consts.CHARSET_UTF8;
    }
}