/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.business;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.ChannelAdapterSelector;
import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.channel.gb.gbBalanceConsts;
import com.guzhi.pay.channel.zfb.ZfbConsts;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.Accounts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.ChBank;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TaskHelper;

/**
 * 支付平台逻辑层的入口
 * 
 * @author administrator
 */
@Service
public class PayService {
    @Autowired
    @Qualifier("channelAdapterSelector")
    private ChannelAdapterSelector adapterSelector;

    @Autowired
    private DomainResource resource;

    private static Executor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(10000));

    private static Logger logger = LoggerFactory.getLogger(PayService.class);
    private static final Logger HIIDO_LOG = LoggerFactory.getLogger("hiido_statistics");

    public PayOrder pay(PayOrder payOrder) {
        // existence check
        PayOrder payOrderInDb = resource.getPayOrder(payOrder.getAppId(), payOrder.getAppOrderId());
        if (payOrderInDb != null) {
            throw new PayException(Consts.SC.REQ_ERROR, "payOrder already exist in DB!", payOrder, null);
        }

        // 检查渠道的appId
        AppInfo appInfo = resource.getAppInfo(payOrder.getAppId());
        if (appInfo == null) {
            throw new PayException(Consts.SC.REQ_ERROR, "appInfo not exist in DB! appId :" + appInfo, payOrder, null);
        }
        payOrder.setAppInfo(appInfo);

        // 获取渠道对应的真实银行id
        String realBankId = "";
        if (!StringUtils.isBlank(payOrder.getBankId())) {
            realBankId = getRealBankId(payOrder.getChId(), payOrder.getBankId());
        }
        // 替换成渠道对应的银行id
        payOrder.setBankId(realBankId);

        // invoke channel
        ChannelIF adapter = adapterSelector.get(payOrder.getChId(), payOrder.getPayMethod());
        if (adapter == null) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "can not find adpater!", payOrder);
        }
        long timeStart = System.currentTimeMillis();
        try {
            payOrder = adapter.pay(payOrder);
        } finally {
            HIIDO_LOG.info("tpay;3;" + payOrder.getAppId() + ";/pay.do;" + (System.currentTimeMillis() - timeStart)
                    + ";;;" + payOrder.getChId() + ";" + payOrder.getPayMethod() + ";");
        }

        // store payOrder
        int result = resource.createPayOrder(payOrder);
        if (result != 1) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "failed to store PayOrder into DB!", payOrder);
        }

        return afterPayCompletion(payOrder);
    }

    private PayOrder afterPayCompletion(PayOrder payOrder) {
        Task asyncQueryTask = new Task(payOrder.getAppId(), payOrder.getAppOrderId(), "", payOrder.getChId(),
                payOrder.getPayMethod());
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            TaskHelper.createAfterPaySuccessTask(resource, payOrder);
            // 添加日志观察同步下单即订单成功的情况.
            logger.info("[pay] get a synchronous channel,set pending status if gb wapbalance,payOrder:{}", payOrder);
            // 保险起见，这里指针对做手机G币的订单状态做调整
            if (Consts.Channel.gb.equalsIgnoreCase(payOrder.getChId())
                    && Consts.PayMethod.WAPBALANCE.equalsIgnoreCase(payOrder.getPayMethod())) {
                payOrder.setStatusCode(Consts.SC.PENDING);
            }
            return payOrder;
        }

        // 如果需要异步充值
        if (StringUtils.isNotBlank(payOrder.getAsyncPayTaskType())) {
            createAsyncPayTask(payOrder, payOrder.getAsyncPayTaskType());
        }

        // 如果是apple 渠道,则创建充值定时任务
        if (Consts.Channel.APPLE.equalsIgnoreCase(payOrder.getChId())) {
            asyncQueryTask.setType(Task.TYPE_PAY_APPLE);
        } else {
            // add async query task
            asyncQueryTask.setType(Task.TYPE_QUERY);
            asyncQueryTask.setNextTime(new Date(System.currentTimeMillis() + Consts.Task.QUERY_RETRY_INIT_DELAY));
        }
        resource.createTask(asyncQueryTask);
        return payOrder;
    }

    /**
     * 异步充值时创建异步充值定时任务
     * 
     * @param payOrder
     * @param taskType 定时任务类型
     */
    private void createAsyncPayTask(PayOrder payOrder, String taskType) {
        logger.info("[createAsyncPayTask]: appid:{},apporderid:{},type:{}", payOrder.getAppId(),
                payOrder.getAppOrderId(), taskType);
        Task task = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), taskType);
        if (task == null && !StringUtils.isEmpty(taskType)) {
            Task newTask = new Task(payOrder.getAppId(), payOrder.getAppOrderId(), taskType, payOrder.getChId(),
                    payOrder.getPayMethod());
            resource.createTask(newTask);
        }
    }

    /**
     * 退款处理
     * 
     * @param payOrder
     * @return
     */
    public PayOrder refund(PayOrder payOrder) {
        PayOrder payOrderInDb = resource.getPayOrder(payOrder.getAppId(), payOrder.getAppOrderId());
        boolean exist = false;
        // 此笔支付的支付动作不是在支付平台发生，则先创建订单
        if ((Consts.ORPHANREFUND.equalsIgnoreCase(payOrder.getOrphanRefund())) && (payOrderInDb == null)) {
            payOrder.setStatusCode(Consts.SC.REFUND_PENDING);
            int result = resource.createPayOrder(payOrder);
            if (result != 1) {
                throw new PayException(Consts.SC.INTERNAL_ERROR, "failed to store PayOrder into DB!", payOrder);
            }
            exist = true;
        } else if (payOrderInDb == null) {// 订单不存在，直接抛异常
            throw new PayException(Consts.SC.REQ_ERROR, "payOrder not exist in DB!", payOrder, null);
        }
        if (!exist) {
            payOrderInDb.setAppInfo(payOrder.getAppInfo());
            // 目前一笔订单只支持一次退款，所以退款金额必须小于支付金额相同
            if (payOrderInDb.getAmount().compareTo(payOrder.getAmount()) == -1) {
                throw new PayException(Consts.SC.DATA_ERROR, "amount error", payOrder);
            }
            // 退款渠道必须跟支付渠道一样
            if (!payOrder.getChId().equalsIgnoreCase(payOrderInDb.getChId())) {
                throw new PayException(Consts.SC.DATA_ERROR, "chId error", payOrder);
            }
            // 如果已经退款，则直接返回退款成功 还是返回重复退款？
            if (Consts.SC.REFUND_SUCCESS.equals(payOrderInDb.getStatusCode())) {
                return payOrderInDb;
            }
            // 退款中
            if (Consts.SC.REFUND_PENDING.equals(payOrderInDb.getStatusCode())) {
                return payOrderInDb;
            }
            payOrderInDb.setAppRefundTime(payOrder.getAppRefundTime());
            payOrderInDb.setRefundAmount(payOrder.getRefundAmount());
            payOrderInDb.setRefundDesc(payOrder.getRefundDesc());
            payOrderInDb.setOrphanRefund(payOrder.getOrphanRefund());
            // 暂时不判断订单是否支付成功
            payOrderInDb.setStatusCode(Consts.SC.REFUND_PENDING);
            int result = resource.updatePayOrder(payOrderInDb);
            if (result != 1) {
                throw new PayException(Consts.SC.INTERNAL_ERROR, "failed to store PayOrder into DB!", payOrder);
            }
            payOrder = payOrderInDb;
        }
        Task asyncQueryTask = new Task(payOrder.getAppId(), payOrder.getAppOrderId(), Task.TYPE_REFUND,
                payOrder.getChId(), payOrder.getPayMethod());
        asyncQueryTask.setNextTime(new Date(System.currentTimeMillis()));
        int result = resource.createTask(asyncQueryTask);
        if (result != 1) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "refund failed ,failed to store PayOrder into DB!",
                    payOrder);
        }
        return payOrder;
    }

    public PayOrder query(PayOrder payOrder) {
        // check payOrder existence
        String appId = payOrder.getAppId();
        PayOrder payOrderInDb = resource.getPayOrder(appId, payOrder.getAppOrderId());
        if (payOrderInDb == null) {
            throw new PayException(Consts.SC.ORDER_NOT_EXIST, "payOrder not exist in DB!", payOrder);
        }

        // check appChInfo
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, payOrderInDb.getChId(), payOrderInDb.getPayMethod());
        if (CollectionUtils.size(appChInfos) != 1) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "can not find appchinfo or appchinfos > 1!", payOrder);
        }

        if (payOrder.getAppInfo() == null) {
            payOrder.setAppInfo(resource.getAppInfo(payOrder.getAppId()));
        }

        payOrderInDb.setAppInfo(payOrder.getAppInfo());
        payOrderInDb.setAppChInfo(appChInfos.get(0));

        // directly return if result is final
        String statusCode = payOrderInDb.getStatusCode();
        if (Consts.SC.FAIL.equalsIgnoreCase(statusCode) || Consts.SC.RISK_ERROR.equalsIgnoreCase(statusCode)
                || Consts.SC.CARD_ERROR.equalsIgnoreCase(statusCode)) {
            return payOrderInDb;
        }
        // 如果除了充钱没有其他充值操作则订单成功
        if (Consts.SC.SUCCESS.equalsIgnoreCase(statusCode) && StringUtils.isBlank(payOrderInDb.getgbOper())) {
            return payOrderInDb;
        }
        // 如果有冲gb 币或者冲保证金的操作时，如果充值gb币,保证金或者频道保证金都失败，则订单状态是pendding
        if (Consts.SC.SUCCESS.equalsIgnoreCase(statusCode) && StringUtils.isNotBlank(payOrderInDb.getgbOper())) {
            String gbFlag = JsonHelper.fromJson(payOrderInDb.getExt(), gbBalanceConsts.ADD_gb);
            String depositFlag = JsonHelper.fromJson(payOrderInDb.getExt(), gbBalanceConsts.ADD_DEPOSIT);
            String addchdepositFlag = JsonHelper.fromJson(payOrderInDb.getExt(), gbBalanceConsts.ADD_CHANNEL_DEPOSIT);
            if (!gbBalanceConsts.ADD_gb_SUCCESS.equalsIgnoreCase(gbFlag)
                    && !gbBalanceConsts.ADD_DEPOSIT_SUCCESS.equalsIgnoreCase(depositFlag)
                    && !gbBalanceConsts.ADD_CHANNEL_DEPOSIT_SUCCESS.equalsIgnoreCase(addchdepositFlag)) {
                payOrderInDb.setStatusCode(Consts.SC.PENDING);
            }
            return payOrderInDb;
        }

        // invoke channel
        try {
            executor.execute(new AsynCallChanncel(payOrder, payOrderInDb));
        } catch (RejectedExecutionException e) {
            logger.error("拒绝接受执行此任务,可能任务数过多", e);
        } catch (Exception e) {
            logger.error("线程池其它异常", e);
        }

        return payOrderInDb;
    }

    /**
     * 调用第三方渠道查询
     * 
     * @author zhouziqing
     * 
     */
    private class AsynCallChanncel implements Runnable {

        private PayOrder payOrder;

        private PayOrder payOrderInDb;

        public AsynCallChanncel(PayOrder payOrder, PayOrder payOrderInDb) {
            this.payOrder = payOrder;
            this.payOrderInDb = payOrderInDb;
        }

        @Override
        public void run() {
            ChannelIF adapter = null;
            
            try {
                if (StringUtils.isBlank(payOrderInDb.getPayMethod())) {
                    adapter = adapterSelector.get(payOrderInDb.getChId(), Consts.REFUND);
                } else {
                    adapter = adapterSelector.get(payOrderInDb.getChId(), payOrderInDb.getPayMethod());
                }

                if (adapter != null) {
                    long timeStart = System.currentTimeMillis();

                    try {
                        payOrder = adapter.query(payOrderInDb);
                    } finally {
                        HIIDO_LOG.info("tpay;3;" + payOrder.getAppId() + ";/query.do;"
                                + (System.currentTimeMillis() - timeStart) + ";;;" + payOrder.getChId() + ";"
                                + payOrder.getPayMethod() + ";");
                    }

                    resource.updatePayOrder(payOrder);

                    // notify app if result is final
                    createTask(payOrder);
                } else {
                    // throw new PayException(Consts.SC.INTERNAL_ERROR, "can not find adpater!", payOrder);
                    logger.error("can not find adpater!");
                }
            } catch (Exception e) {
                logger.error("异步调用第三方渠道查询时出错", e);
            }
        }
    }

    public Accounts accounts(Accounts accounts) {
        throw new PayException(Consts.SC.INTERNAL_ERROR, "Account interface not implemented yet!");
        // ChannelIF adapter = adapterSelector.get(accounts.getAppId(),
        // accounts.getPayMethod());
        // if (adapter != null) {
        // TODO need accounts?
        // return adapter.accounts(accounts);
        // }
        // return accounts;
    }

    /**
     * 真正退款处理接口
     * 
     * @return
     */
    public PayOrder realRefund(PayOrder payOrder) {
        // 选择具体的支付渠道
        ChannelIF adapter = adapterSelector.get(payOrder.getChId(), Consts.REFUND);
        if (adapter == null) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "can not find adpater!", payOrder);
        }
        List<AppChInfo> appChInfos = resource.getAppChInfo(payOrder.getAppId(), payOrder.getChId(),
                payOrder.getPayMethod());
        // 因教育线的退款未提交gate参数，暂时去掉appchinfos判断，后续如果不同支付方式密码不一样会存在问题
        // if (CollectionUtils.size(appChInfos) != 1) {
        // throw new PayException(Consts.SC.INTERNAL_ERROR,
        // "can not find appchinfo or appchinfos > 1!", payOrder);
        // }

        payOrder.setAppInfo(payOrder.getAppInfo());
        payOrder.setAppChInfo(appChInfos.get(0));

        payOrder = adapter.refund(payOrder);
        // resource.updatePayOrder(payOrder);
        // 创建退款的查询任务
        Task asyncQueryTask = new Task(payOrder.getAppId(), payOrder.getAppOrderId(), Task.TYPE_QUERY,
                payOrder.getChId(), payOrder.getPayMethod());
        asyncQueryTask.setNextTime(new Date(System.currentTimeMillis() + Consts.Task.QUERY_RETRY_INIT_DELAY));
        resource.createTask(asyncQueryTask);
        return payOrder;
    }

    /**
     * 根据渠道ID 和银行ID获取渠道对应的银行ID
     * 
     * @param chId
     * @param bankId
     * @return
     */
    private String getRealBankId(String chId, String bankId) {
        ChBank chBank = resource.getBank(chId, bankId);
        if (chBank != null) {
            return chBank.getCode();
        } else {
            throw new PayException(Consts.SC.DATA_FORMAT_ERROR, "failed to get bankId!  " + chId + "  " + bankId);
        }
    }

    /**
     * 创建通知任务
     * 
     * @param payOrder
     */
    private void createTask(PayOrder payOrder) {
        // 需要做gb相关的定时任务
        String taskType = "";
        Task task = null;
        logger.info("[createTask],add op:" + payOrder.getgbOper());
        String addgbResult = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_gb);
        // 订单状态成功且是addgb操作
        if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode()) && !StringUtils.isEmpty(payOrder.getgbOper())
                && Consts.gbOper.ADD.equals(payOrder.getgbOper())
                && !gbBalanceConsts.ADD_gb_SUCCESS.equals(addgbResult)) {
            taskType = Task.TYPE_ADD_GB;
            task = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), taskType);
        } else if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode())
                || Consts.SC.FAIL.equals(payOrder.getStatusCode())
                || Consts.SC.REFUND_SUCCESS.equals(payOrder.getStatusCode())
                || ZfbConsts.OVERED_REFUND.equalsIgnoreCase(payOrder.getStatusCode())) {
            taskType = Task.TYPE_NOTIFY;
            task = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), taskType);
        }
        if (task == null && !StringUtils.isEmpty(taskType)) {
            logger.info("[createTask],taskType:" + taskType);
            resource.createTask(new Task(payOrder.getAppId(), payOrder.getAppOrderId(), taskType, payOrder.getChId(),
                    payOrder.getPayMethod()));
        }
    }
}
