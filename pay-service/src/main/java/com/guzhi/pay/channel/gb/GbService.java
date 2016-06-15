/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.gb;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HMacSHA1;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.ThrifeUtils;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.thrift.ThriftConfig;
import com.guzhi.pay.thrift.ybpay.OriginalOrder;
import com.guzhi.pay.thrift.ybpay.PayMoneyRequest2;
import com.guzhi.pay.thrift.ybpay.PaymentResult;
import com.guzhi.pay.thrift.ybpay.PaymentService;
import com.guzhi.pay.thrift.ybpay.ReverseOrderRequest;

/**
 * @author administrator
 * 
 *         update by
 *         reference gate
 */
@Service("gbService")
public class GbService {
    private static final Logger LOG = LoggerFactory.getLogger(gbService.class);
    private static final int SUCCESS = 1, REPEAT_ORDER = -18, ORDER_REVERSED = -23, NEED_CONFIRM = -30;

    @Autowired(required = false)
    @Qualifier("gbpayConfig")
    private ThriftConfig gbpayConfig;

    public void payMoney(PayOrder order, AppChInfo gbAppChInfo, String callbackUrl) {
        long gbuid = Long.valueOf(JsonHelper.fromJson(order.getUserId(), Consts.gbUID));
        PaymentResult paymentResult = payMoney(gbuid, order.getAmount(), order.getChOrderId(), order.getUserIp(),
                order.getProdName(), "购买" + order.getProdDesc(), order.getProdId(), gbAppChInfo.getChAccountId(),
                gbAppChInfo.getChPayKeyMd5(), callbackUrl);
        if (null == paymentResult) {
            order.setStatusCode(Consts.SC.CHANNEL_ERROR);
            order.setStatusMsg("G币中心异常");
            return;
        }
        if (SUCCESS == paymentResult.code) {
            order.setStatusCode(Consts.SC.SUCCESS);
            order.setStatusMsg("消费G币成功");
            order.setChDealTime(TimeHelper.get(8, new Date()));
            return;
        }
        String confirmKey = JsonHelper.fromJson(gbAppChInfo.getAdditionalInfo(), gbBalanceConsts.CONFIRMKEY);
        // 如果需要认证，那么支付网关将会向G币中心发送认证请求，获取认证信息并取出有效值返回给业务方.
        if (NEED_CONFIRM == paymentResult.code && StringUtils.isNotBlank(confirmKey)) {
            order.setStatusCode(Consts.SC.PENDING);
            order.setStatusMsg("等待支付");
            Map map = gbHelper.requestForAuthMap(order, paymentResult.info, confirmKey);
            order.setPayUrl(JsonHelper.toJson(map));
            return;
        }
        order.setStatusCode(Consts.SC.FAIL);
        order.setStatusMsg(paymentResult.info);
    }

    /**
     * 扣除多玩币, 不需要传入 appId 和 sign
     * 
     * @param gbuid
     * @param gbChannelId gb频道ID，兼容旧接口的参数，可以填0
     * @param payMoney
     * @param orderId
     * @param userIp
     * @return 如果返回PaymentResult 则表示成功或者需要弹窗验证，否则抛出异常
     */
    @SuppressWarnings("unchecked")
    public PaymentResult payMoney(Long gbuid, BigDecimal payMoney, String orderId, String userIp, String goodsName,
            String description, String prodId, String gbProd, String gbKey, String callbackUrl) {
        long verifyStart = System.currentTimeMillis();
        ThriftClientWrapper<PaymentService.Iface> client = null;
        PayMoneyRequest2 req = new PayMoneyRequest2();
        OriginalOrder originalOrder = new OriginalOrder();
        originalOrder.setOrderId(orderId);
        originalOrder.setMoney(payMoney.toPlainString());
        originalOrder.setMoneyType(gbBalanceConsts.MONEY_TYPE_gb);
        req.setProduct(gbProd);
        req.setCallbackAddr(callbackUrl);
        req.setgbuid(gbuid);
        req.setgbChannelId(0);
        req.setMoney(payMoney.toPlainString());
        req.setMoneyType(gbBalanceConsts.MONEY_TYPE_gb);
        req.setOrderId(orderId);
        req.setUserIp(userIp);
        req.setPayTime(TimeHelper.getFormattedTime());
        req.setGoodsName(goodsName);
        req.setDescription(description);
        req.setConsumeTargetProduct(prodId);

        String str = "" + req.getgbuid() + req.getgbChannelId() + req.getMoneyType() + req.getMoney()
                + req.getOrderId().toLowerCase() + req.getProduct().toLowerCase() + req.getPayTime() + req.getUserIp()
                + gbKey;

        String sign = MD5Utils.getMD5(str);
        req.setSign(sign);
        try {
            client = gbpayConfig.getFactory().createClient();
            PaymentResult result = client.getClient().payMoneyWithOriginalOrder(req, originalOrder);
            LOG.info("[payMoneyWithOriginalOrder] request:{}, result:{}", req, result);
            return result;
        } catch (Exception e) {
            LOG.error("[payMoneyWithOriginalOrder] orderId:" + orderId + " error.", e);
            throw new PayException(
                    e instanceof PayException ? ((PayException) e).getStatusCode() : Consts.SC.CHANNEL_ERROR,
                    e.getMessage());
        } finally {
            ThrifeUtils.close(client);
            LOG.info("[payMoneyWithOriginalOrder] orderId:{}, timeCost:{}ms", orderId,
                    System.currentTimeMillis() - verifyStart);
        }
    }

    /**
     * 查询G币消费订单状态.
     */
    public PayOrder quergbbOrder(PayOrder payOrder, String passport, AppChInfo gbChInfo) {
        try {
            PaymentResult result = getPayMoneyResult(passport, payOrder.getAppId() + "-" + payOrder.getAppOrderId(),
                    gbChInfo.getChAccountId(), gbChInfo.getChPayKeyMd5());
            if (SUCCESS == result.code) {
                payOrder.setStatusCode(Consts.SC.SUCCESS);
                payOrder.setStatusMsg("G币查询：" + "支付成功");
            } else if (NEED_CONFIRM == result.code) {
                payOrder.setStatusMsg("G币查询：" + "需要用户认证");
            } else {
                payOrder.setStatusCode(Consts.SC.FAIL);
                payOrder.setStatusCode("G币查询：" + StringUtils.substring(result.info, 0, 80));
            }
        } catch (Exception e) {
            payOrder.setStatusMsg("G币查询：" + "请求异常");
        }
        return payOrder;
    }

    /**
     * 获取订单状态
     * 
     * @param passport
     * @param orderId
     * @return
     */
    @SuppressWarnings("unchecked")
    public PaymentResult getPayMoneyResult(String passport, String orderId, String gbProd, String gbKey) {
        long verifyStart = System.currentTimeMillis();
        ThriftClientWrapper<PaymentService.Iface> client = null;
        String sign = MD5Utils.getMD5(StringUtils.defaultString(passport).toLowerCase() + gbProd.toLowerCase()
                + StringUtils.defaultString(orderId).toLowerCase() + gbKey);
        try {
            client = gbpayConfig.getFactory().createClient();
            PaymentResult result = client.getClient().getPayMoneyResult(passport, gbProd, orderId, sign);
            LOG.info("[getPayMoneyResult] passport:{}, orderId:{}, sign:{}, result:{}", passport, orderId, sign,
                    result);
            // 如果为成功或者为需要授权的订单，则返回结果
            if (SUCCESS == result.code || NEED_CONFIRM == result.code) {
                return result;
            }
            // 其他状态
            throw new PayException(Consts.SC.FAIL, result.info);
        } catch (Exception e) {
            LOG.error("[getPayMoneyResult] orderId:" + orderId + " error.", e);
            throw new PayException(
                    e instanceof PayException ? ((PayException) e).getStatusCode() : Consts.SC.CHANNEL_ERROR,
                    e.getMessage());
        } finally {
            ThrifeUtils.close(client);
            LOG.info("[getPayMoneyResult] orderId:{}, timeCost:{}ms", orderId,
                    System.currentTimeMillis() - verifyStart);
        }
    }

    /**
     * 冲正G币
     * 如果冲正成功，则返回true，否则返回false
     * 如果是通信异常，则抛出异常
     * 
     * 冲正接口加多一次重试
     * 
     * @param gbuid
     * @param orderId
     * @param money
     * @param description
     * @return
     */
    public boolean reversePayMoney(long gbuid, String orderId, BigDecimal money, String description, String gbProd,
            String gbKey) {
        try {
            return reversePayMoneyHelper(gbuid, orderId, money, description, gbProd, gbKey);
        } catch (PayException e) {
            if (Consts.SC.CHANNEL_ERROR.equalsIgnoreCase(e.getStatusCode())) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e1) {
                }
                return reversePayMoneyHelper(gbuid, orderId, money, description, gbProd, gbKey);
            } else {
                throw e;
            }
        }
    }

    /**
     * 冲正G币
     * </br>
     * 如果冲正成功，则返回true，否则返回false
     * </br>
     * 如果是通信异常，则抛出异常
     * 
     * @param gbuid
     * @param orderId
     * @param money
     * @param description
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean reversePayMoneyHelper(long gbuid, String orderId, BigDecimal money, String description,
            String gbProd, String gbKey) {
        long verifyStart = System.currentTimeMillis();
        ThriftClientWrapper<PaymentService.Iface> client = null;
        ReverseOrderRequest req = new ReverseOrderRequest();
        req.setgbuid(gbuid);
        req.setProduct(gbProd);
        req.setProductOrderId(orderId);
        req.setMoney(String.valueOf(money.floatValue()));
        req.setMoneyType(gbBalanceConsts.MONEY_TYPE_gb);
        req.setTime(TimeHelper.getFormattedTime());
        req.setDescription(description);

        String sign = HMacSHA1.getSignature(gbProd.toLowerCase() + req.getProductOrderId() + req.getgbuid()
                + gbBalanceConsts.MONEY_TYPE_gb + req.getMoney() + req.getTime() + req.getDescription(), gbKey);
        req.setSign(sign);

        try {
            client = gbpayConfig.getFactory().createClient();
            PaymentResult result = client.getClient().reversePayMoney(req);

            LOG.info("[reversePayMoney] request:{}, result:{}", req, result);
            // 成功和重复订单都是成功
            if (result.code == SUCCESS || result.code == ORDER_REVERSED) {
                return true;
            }
            throw new PayException(Consts.SC.FAIL, result.info);
        } catch (Exception e) {
            LOG.error("[reversePayMoney] orderId:" + orderId + ", error.", e);
            throw new PayException(
                    e instanceof PayException ? ((PayException) e).getStatusCode() : Consts.SC.CHANNEL_ERROR,
                    e.getMessage());
        } finally {
            ThrifeUtils.close(client);
            LOG.info("[reversePayMoney] orderId:{}, timeCost:{}ms", orderId, System.currentTimeMillis() - verifyStart);
        }
    }
}