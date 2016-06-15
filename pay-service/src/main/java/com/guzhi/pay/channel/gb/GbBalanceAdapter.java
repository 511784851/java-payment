/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.gb;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HMacSHA1;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.ThrifeUtils;
import com.guzhi.pay.helper.TimeCostHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;
import com.guzhi.pay.thrift.ThriftConfig;
import com.guzhi.pay.thrift.ybpay.AddMoneyRequest;
import com.guzhi.pay.thrift.ybpay.AddgbChannelMoneyRequest;
import com.guzhi.pay.thrift.ybpay.OriginalOrder;
import com.guzhi.pay.thrift.ybpay.PayMoneyRequest2;
import com.guzhi.pay.thrift.ybpay.PaymentResult;
import com.guzhi.pay.thrift.ybpay.PaymentService;

/**
 * @author administrator
 * @author 2013/08/27
 * 
 */
@SuppressWarnings("unchecked")
@Service("gbBalanceAdapter")
public class GbBalanceAdapter extends AbstractChannelIF {

    private static final Logger LOG = LoggerFactory.getLogger(GbBalanceAdapter.class);

    // G币的主动查询地址(guzhiPay-->gb)
    @Value("${gbQueryUrl}")
    protected String gbQueryUrl;

    @Autowired(required = false)
    @Qualifier("gbpayConfig")
    private ThriftConfig gbpayConfig;

    @Autowired
    DomainResource resource;

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    /**
     *
     */
    @Override
    public PayOrder pay(PayOrder payOrder) {
        if (Consts.gbOper.ADD.equalsIgnoreCase(payOrder.getgbOper())) {
            return addgb(payOrder, payOrder.getAppChInfo());
        } else if (Consts.gbOper.ADD_DEPOSIT.equalsIgnoreCase(payOrder.getgbOper())) {
            return addDeposit(payOrder, payOrder.getAppChInfo());
        } else if (Consts.gbOper.ADD_CHANNEL_DEPOSIT.equalsIgnoreCase(payOrder.getgbOper())) {
            return addgbChannelMoney(payOrder, payOrder.getAppChInfo());
        }
        // TODO 由于gb充值接口传递的订单号变更，下面的代码需要更改才能使用。
        ThriftClientWrapper<PaymentService.Iface> client = null;
        PayMoneyRequest2 request = new PayMoneyRequest2();
        // invariant
        String product = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), gbBalanceConsts.PRODUCT);
        String encryptKey = payOrder.getAppChInfo().getChPayKeyMd5();
        request.setMoneyType(gbBalanceConsts.MONEY_TYPE_gb);
        request.setProduct(product);
        request.setPayTime(TimeHelper.get(8, new Date()));

        // 生成chOrderId
        String chOrderId = payOrder.getChOrderId();
        if (StringUtils.isBlank(chOrderId)) {
            chOrderId = OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId());
            payOrder.setChOrderId(chOrderId);
        }
        request.setOrderId(payOrder.getChOrderId());

        Long uid = gbHelper.getUid(payOrder);
        if (uid == null || uid == 0) {
            payOrder.setStatusCode(Consts.SC.DATA_ERROR);
            payOrder.setStatusMsg("userAddiInfo中取不到gbnumber");
            return payOrder;
        }
        request.setgbuid(uid);
        request.setCallbackAddr(UrlHelper.removeLastSep(getguzhiPayNotify()) + gbBalanceConsts.ADDR_guzhiPay_gbNOTIFY);
        request.setUserIp(payOrder.getUserIp());
        request.setMoney(String.valueOf(payOrder.getAmount()));
        request.setGoodsName(payOrder.getProdName());
        request.setDescription(payOrder.getProdDesc());
        request.setAddMoneyOrderId("");
        String toBeEncrypt = "" + request.getgbuid() + request.getgbChannelId() + request.getMoneyType()
                + request.getMoney() + request.getOrderId().toLowerCase() + request.getProduct().toLowerCase()
                + request.getPayTime() + request.getUserIp() + encryptKey;
        request.setSign(MD5Utils.getMD5(toBeEncrypt));

        OriginalOrder oriOrder = new OriginalOrder(request.getOrderId(), request.getMoney(), request.getMoneyType());

        try {
            client = gbpayConfig.getFactory().createClient();
            TimeCostHelper.suspend();
            PaymentResult paymentResult = client.getClient().payMoneyWithOriginalOrder(request, oriOrder);
            TimeCostHelper.resume();
            LOG.info("[gbBalanceAdapter.pagbgbB] param[uid:{},orderid:{},gbcount:{}], result:{}", request.getgbuid(),
                    request.getOrderId(), request.getMoney(), paymentResult, TraceHelper.getTrace(payOrder));
            return gbHelper.updatePayOrderByResult(payOrder, paymentResult);

        } catch (Exception e) {
            TimeCostHelper.resume();
            LOG.error("[gbBalanceAdapter.pagbgbB] error.", e);
            payOrder.setStatusCode(Consts.SC.CHANNEL_ERROR);
            payOrder.setStatusMsg("gb支付渠道出错,请联系技术人员");
        } finally {
            ThrifeUtils.close(client);
        }

        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {

        /*
         * if (Consts.gbOper.ADD.equals(payOrder.getgbOper())) {
         * return queryAddgbStatus(payOrder, payOrder.getAppChInfo());
         * }
         */

        ThriftClientWrapper<PaymentService.Iface> client = null;

        String password = gbHelper.getPassword(payOrder);
        if (StringUtils.isEmpty(password)) {
            payOrder.setStatusCode(Consts.SC.DATA_ERROR);
            payOrder.appendMsg("userAddiInfo中取不到password");
            return payOrder;
        }

        try {
            client = gbpayConfig.getFactory().createClient();
            String product = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), gbBalanceConsts.PRODUCT);
            String encryptKey = payOrder.getAppChInfo().getChPayKeyMd5();
            String sign = MD5Utils.getMD5(password + product + payOrder.getChOrderId() + encryptKey);
            TimeCostHelper.suspend();
            PaymentResult paymentResult = client.getClient().getPayMoneyResult(password, product,
                    payOrder.getChOrderId(), sign);
            TimeCostHelper.resume();
            LOG.info("[gbBalanceAdapter.quergbgbB] param[password:{},orderid:{}], result:{}", password,
                    payOrder.getChOrderId(), paymentResult, TraceHelper.getTrace(payOrder));
            return gbHelper.updatePayOrderByResult(payOrder, paymentResult);

        } catch (Exception e) {
            TimeCostHelper.resume();
            LOG.error("[gbBalanceAdapter.quergbgbB] error.", e);
            payOrder.setStatusCode(Consts.SC.CHANNEL_ERROR);
            payOrder.setStatusMsg("gb支付渠道出错,请联系技术人员");
        } finally {
            ThrifeUtils.close(client);
        }
        return payOrder;
    }

    // 用http的方式
    public PayOrder query2(PayOrder payOrder) {

        LOG.info("[gbBalanceAdapter.query] with PayOrder:{}", payOrder);
        Map<String, String> request = new HashMap<String, String>();
        String product = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), gbBalanceConsts.PRODUCT);
        String encryptKey = payOrder.getAppChInfo().getChPayKeyMd5();

        String password = gbHelper.getPassword(payOrder);
        if (StringUtils.isEmpty(password)) {
            payOrder.setStatusCode(Consts.SC.DATA_ERROR);
            payOrder.setStatusMsg("userAddiInfo中取不到password");
            return payOrder;
        }
        request.put(gbBalanceConsts.PASSWORD, password);
        request.put(gbBalanceConsts.PRODUCT, product);
        request.put(gbBalanceConsts.ORDER_ID, payOrder.getChOrderId());
        String toBeEncrypt = password + product + payOrder.getChOrderId() + encryptKey;
        request.put(gbBalanceConsts.SIGN, MD5Utils.getMD5(toBeEncrypt));
        String queryUrl = gbQueryUrl + StringHelper.assembleResqStr(request);
        String respStr = HttpClientHelper.sendRequest(queryUrl, Consts.CHARSET_UTF8);
        gbHelper.updatePayOrderByQuery(payOrder, respStr);
        return payOrder;
    }

    private PayOrder addgbMoney(PayOrder payOrder, AppChInfo gbChInfo, int moneyType) {
        LOG.info("[gbBalanceAdapter.addgbMoney] start add gb money,userid:{}", payOrder.getUserId(),
                TraceHelper.getTrace(payOrder));
        String ext = payOrder.getExt();
        if (gbBalanceConsts.ADD_gb_SUCCESS.equalsIgnoreCase(JsonHelper.fromJson(ext, gbBalanceConsts.ADD_gb))) {
            LOG.info(
                    "[gbBalanceAdapter.addgbMoney] return,add gb operation is not neccessary,it is done earlier,userid:{},orderId:{}",
                    payOrder.getUserId(), payOrder.getAppOrderId(), TraceHelper.getTrace(payOrder));
            return payOrder;
        }
        if (gbBalanceConsts.ADD_DEPOSIT_SUCCESS
                .equalsIgnoreCase(JsonHelper.fromJson(ext, gbBalanceConsts.ADD_DEPOSIT))) {
            LOG.info(
                    "[gbBalanceAdapter.addgbMoney] return,add gb Deposit operation is not neccessary,it is done earlier,userid:{},orderId:{}",
                    payOrder.getUserId(), payOrder.getAppOrderId(), TraceHelper.getTrace(payOrder));
            return payOrder;
        }
        String newExt = payOrder.getExt();
        ThriftClientWrapper<PaymentService.Iface> client = null;

        try {
            String product = JsonHelper.fromJson(gbChInfo.getAdditionalInfo(), gbBalanceConsts.PRODUCT);
            String addgbKey = JsonHelper.fromJson(gbChInfo.getAdditionalInfo(), gbBalanceConsts.ENCRYPT_ADD_gb_KEY);
            String gbuid = JsonHelper.fromJson(payOrder.getUserId(), gbBalanceConsts.gbUID);
            // String chOrderId =
            // OrderIdHelper.genChOrderId(payOrder.getAppId(),
            // payOrder.getAppOrderId());
            String chOrderId = payOrder.getAppId() + Consts.DELIMITER + payOrder.getAppOrderId();
            String description = "增加虚拟币";
            if (moneyType == gbBalanceConsts.MONEY_TYPE_DEPOSIT) {
                description = "通过" + payOrder.getAppChInfo().getChName() + "渠道充值保证金";
            }
            if (moneyType == gbBalanceConsts.MONEY_TYPE_gb) {
                description = "通过" + payOrder.getAppChInfo().getChName() + "渠道充值G币";
            }
            AddMoneyRequest request = new AddMoneyRequest();
            request.setMoneyType(moneyType);
            // 常量
            request.setProduct(product);
            request.setAddTime(TimeHelper.get(8, new Date()));

            request.setOrderId(chOrderId);
            request.setgbuid(Long.valueOf(gbuid));
            request.setMoney(String.valueOf(payOrder.getgbAmount().doubleValue()));
            request.setUserIp(payOrder.getUserIp());
            request.setChannel(payOrder.getChId());
            request.setDescription(description);
            String queryOrderAddr = UrlHelper.removeLastSep(guzhiPayNotify)
                    + gbBalanceConsts.ADDR_guzhiPay_QUERY_ORDERURL;
            request.setQueryOrderAddr(queryOrderAddr);
            LOG.info("[gbBalanceAdapter.addgbMoney] queryOrderAddr:{}", queryOrderAddr, TraceHelper.getTrace(payOrder));
            request.setSign(
                    HMacSHA1.getSignature(request.getProduct().toLowerCase() + request.getOrderId() + request.getgbuid()
                            + request.getMoneyType() + request.getMoney() + request.getChannel() + request.getUserIp()
                            + request.getAddTime() + request.getDescription() + request.getQueryOrderAddr(), addgbKey));
            ext = payOrder.getExt();
            client = gbpayConfig.getFactory().createClient();
            TimeCostHelper.suspend();
            PaymentResult paymentResult = client.getClient().addMoney(request);
            TimeCostHelper.resume();
            LOG.info("[gbBalanceAdapter.addgbMoney] param[uid:{},orderid:{},gbcount:{}], result:{}", request.getgbuid(),
                    request.getOrderId(), request.getMoney(), paymentResult, TraceHelper.getTrace(payOrder));
            if (paymentResult != null && (paymentResult.getCode() == gbBalanceConsts.SUCCESS
                    || paymentResult.getCode() == gbBalanceConsts.SUCCESS_ORDER_REPEAT)) {
                if (moneyType == gbBalanceConsts.MONEY_TYPE_gb) {
                    newExt = JsonHelper.putJson(ext, gbBalanceConsts.ADD_gb, gbBalanceConsts.ADD_gb_SUCCESS);
                } else if (moneyType == gbBalanceConsts.MONEY_TYPE_DEPOSIT) {
                    newExt = JsonHelper.putJson(ext, gbBalanceConsts.ADD_DEPOSIT, gbBalanceConsts.ADD_DEPOSIT_SUCCESS);
                }
                if (StringUtils.isEmpty(newExt)) {
                    LOG.error("[gbBalanceAdapter.addgbMoney] success ,but set the ext param fail",
                            TraceHelper.getTrace(payOrder));
                } else {
                    payOrder.setExt(newExt);
                    return payOrder;
                }
            } else {
                LOG.error("[gbBalanceAdapter.addgbMoney] fail,uid:{},orderId:{}", request.getgbuid(),
                        request.getOrderId(), TraceHelper.getTrace(payOrder));
            }
        } catch (Exception e) {
            TimeCostHelper.resume();
            LOG.error("[gbBalanceAdapter.addgbMoney] error. uid:{},orderId:{},exception:{}", payOrder.getUserId(),
                    OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId()), e,
                    TraceHelper.getTrace(payOrder));
            String errorMsg = "[gbBalanceAdapter.addgbMoney] error. uid:" + payOrder.getUserId() + ",orderId:"
                    + payOrder.getChOrderId() + ",exception:" + e.getMessage();
            LOG.error(errorMsg, e);
        } finally {
            ThrifeUtils.close(client);
        }
        if (moneyType == gbBalanceConsts.MONEY_TYPE_gb) {
            newExt = JsonHelper.putJson(ext, gbBalanceConsts.ADD_gb, gbBalanceConsts.ADD_gb_FAIL);
        } else if (moneyType == gbBalanceConsts.MONEY_TYPE_DEPOSIT) {
            newExt = JsonHelper.putJson(ext, gbBalanceConsts.ADD_DEPOSIT, gbBalanceConsts.ADD_DEPOSIT_FAILED);
        }
        if (StringUtils.isEmpty(newExt)) {
            LOG.error("[gbBalanceAdapter.addgbMoney] fail ,and set the ext param fail too",
                    TraceHelper.getTrace(payOrder));
        }
        payOrder.setExt(newExt);
        return payOrder;
    }

    /**
     * 增加频道保证金
     * 
     * @param payOrder
     * @param gbChInfo
     * @return
     */
    private PayOrder addgbChannelMoney(PayOrder payOrder, AppChInfo gbChInfo) {
        if (gbBalanceConsts.ADD_CHANNEL_DEPOSIT_SUCCESS
                .equalsIgnoreCase(JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_CHANNEL_DEPOSIT))) {
            LOG.info("[addgbChannelMoney] not need to add channel deposit return.payOrder:{}", payOrder);
            return payOrder;
        }
        String gbuid = JsonHelper.fromJson(payOrder.getUserId(), gbBalanceConsts.gbUID);
        String gbChannelId = JsonHelper.fromJson(payOrder.getProdAddiInfo(), gbBalanceConsts.gbCHANNELID);
        String orderId = payOrder.getAppId() + Consts.DELIMITER + payOrder.getAppOrderId();
        int balanceType = gbBalanceConsts.MONEY_TYPE_CHANNEL_DEPOSIT;
        String amount = String.valueOf(payOrder.getgbAmount().doubleValue());
        // 充值中心为业务方ID取了别名，"product"和"appId"所指相同。
        String appId = JsonHelper.fromJson(gbChInfo.getAdditionalInfo(), gbBalanceConsts.PRODUCT);
        List<AppChInfo> appChInfos = resource.getAppChInfo(payOrder.getAppId(), payOrder.getChId(),
                payOrder.getPayMethod());
        String channel = payOrder.getChId();
        String description = "";
        if (CollectionUtils.isNotEmpty(appChInfos)) {
            description = "通过" + appChInfos.get(0).getChName() + "增加频道保证金";
        } else {
            description = "通过" + payOrder.getAppChInfo().getChName() + "增加频道保证金";
        }
        String userIp = StringUtils.isBlank(payOrder.getUserIp()) ? gbBalanceConsts.DEFAULT_USERIP
                : payOrder.getUserIp();
        long timestamp = System.currentTimeMillis();
        String queryOrderAddr = UrlHelper.removeLastSep(guzhiPayNotify) + gbBalanceConsts.ADDR_guzhiPay_QUERY_ORDERURL;
        String addgbKey = JsonHelper.fromJson(gbChInfo.getAdditionalInfo(), gbBalanceConsts.ENCRYPT_ADD_gb_KEY);
        String sign = HMacSHA1.getSignature(StringUtils.lowerCase(appId + gbuid + gbChannelId + orderId
                + String.valueOf(balanceType) + amount + channel + userIp + String.valueOf(timestamp) + queryOrderAddr),
                addgbKey);
        AddgbChannelMoneyRequest request = new AddgbChannelMoneyRequest();
        request.setgbuid(Long.valueOf(gbuid));
        request.setgbuid(Long.valueOf(gbuid));
        request.setgbChannelId(Long.valueOf(gbChannelId));
        request.setOrderId(orderId);
        request.setBalanceType(balanceType);
        request.setAmount(amount);
        request.setAppId(appId);
        request.setChannel(channel);
        request.setUserIp(userIp);
        request.setTimestamp(timestamp);
        request.setDescription(description);
        request.setQueryOrderAddr(queryOrderAddr);
        request.setSign(sign);
        LOG.info("[addgbChannelMoney] addgbChannelMoneyRequest request:{}", request.toString());
        String ext = payOrder.getExt();
        String newExt = payOrder.getExt();
        ThriftClientWrapper<PaymentService.Iface> client = null;
        try {
            client = gbpayConfig.getFactory().createClient();
            TimeCostHelper.suspend();
            PaymentResult paymentResult = client.getClient().addgbChannelMoney(request);
            TimeCostHelper.resume();
            LOG.info("[addgbChannelMoney] get result success,orderid:{},chid:{},paymethod:{},paymentResult:{}",
                    payOrder.getAppOrderId(), payOrder.getChId(), payOrder.getPayMethod(), paymentResult);
            int code = paymentResult.getCode();
            if (paymentResult != null && code == gbBalanceConsts.SUCCESS
                    || code == gbBalanceConsts.SUCCESS_ORDER_REPEAT) {
                LOG.info("[addgbChannelMoney] add money success,orderid:{},chid:{},paymethod:{},paymentResult:{}",
                        payOrder.getAppOrderId(), payOrder.getChId(), payOrder.getPayMethod(), paymentResult);
                newExt = JsonHelper.putJson(ext, gbBalanceConsts.ADD_CHANNEL_DEPOSIT,
                        gbBalanceConsts.ADD_CHANNEL_DEPOSIT_SUCCESS);
            } else {
                LOG.info("[addgbChannelMoney] add money fail,orderid:{},chid:{},paymethod:{},paymentResult:{}",
                        payOrder.getAppOrderId(), payOrder.getChId(), payOrder.getPayMethod(), paymentResult);
                newExt = JsonHelper.putJson(ext, gbBalanceConsts.ADD_CHANNEL_DEPOSIT,
                        gbBalanceConsts.ADD_CHANNEL_DEPOSIT_FAILED);
            }
            if (StringUtils.isBlank(newExt)) {
                LOG.error("[addgbChannelMoney] call thrift success,but set ext field fail.request:{},paymentResult:{}",
                        request, paymentResult);
            } else {
                payOrder.setExt(newExt);
                return payOrder;
            }
        } catch (TException e) {
            TimeCostHelper.resume();
            LOG.error("[addgbChannelMoney] calling thrift error,paymentRequest:{}", request);
        } catch (Exception e) {
            TimeCostHelper.resume();
            LOG.error("[addgbChannelMoney] create thrift client error,paymentRequest:{}", request);
        } finally {
            ThrifeUtils.close(client);
        }
        newExt = JsonHelper.putJson(ext, gbBalanceConsts.ADD_CHANNEL_DEPOSIT,
                gbBalanceConsts.ADD_CHANNEL_DEPOSIT_FAILED);
        LOG.info("[addgbChannelMoney] add channel deposit fail.request:{},payOrder:{}", request, payOrder);
        if (StringUtils.isNotBlank(newExt)) {
            payOrder.setExt(newExt);
        }
        return payOrder;
    }

    /**
     * 新增G币的接口
     * 
     * @param queryOrderUrl 查询订单的url
     * @param channel 支付渠道
     * @return
     */
    private PayOrder addgb(PayOrder payOrder, AppChInfo gbChInfo) {
        return addgbMoney(payOrder, gbChInfo, gbBalanceConsts.MONEY_TYPE_gb);
    }

    private PayOrder addDeposit(PayOrder payOrder, AppChInfo gbChInfo) {
        return addgbMoney(payOrder, gbChInfo, gbBalanceConsts.MONEY_TYPE_DEPOSIT);
    }

    /**
     * 查询G币的充值是否成功(还没测试)
     * 
     * @param older
     * @param queryOrderUrl 查询订单的url
     * @param channel 支付渠道
     * @return
     */
    private PayOrder queryAddgbStatus(PayOrder order, AppChInfo gbChInfo) {
        LOG.info("[gbBalanceAdapter.queryAddgbStatus] gbChInfo.getAdditionalInfo():{}", gbChInfo.getAdditionalInfo());
        LOG.info("[gbBalanceAdapter.queryAddgbStatus] order.getUserId():{}", order.getUserId());
        ThriftClientWrapper<PaymentService.Iface> client = null;
        String ext = null;
        try {
            String product = JsonHelper.fromJson(gbChInfo.getAdditionalInfo(), gbBalanceConsts.PRODUCT);
            String addgbKey = JsonHelper.fromJson(gbChInfo.getAdditionalInfo(), gbBalanceConsts.ENCRYPT_ADD_gb_KEY);
            String gbuid = JsonHelper.fromJson(order.getUserId(), gbBalanceConsts.gbUID);
            String chOrderId = OrderIdHelper.genChOrderId(order.getAppId(), order.getAppOrderId());

            LOG.info("[gbBalanceAdapter.queryAddgbStatus]  product:{},addgbKey:{},gbuid:{}", product,
                    String.format("%s******%s", addgbKey.substring(0, addgbKey.length() - 3), "!@ss"), gbuid);
            String sign = HMacSHA1.getSignature(product.toLowerCase() + chOrderId + gbuid, addgbKey);
            ext = order.getExt();
            LOG.info("[gbBalanceAdapter.addgb] appOrderId:{},ext:{}", order.getAppOrderId(), ext,
                    TraceHelper.getTrace(order));
            client = gbpayConfig.getFactory().createClient();
            TimeCostHelper.suspend();
            PaymentResult paymentResult = client.getClient().getAddMoneyResult(Long.valueOf(gbuid), product, chOrderId,
                    sign);
            TimeCostHelper.resume();
            LOG.info("[gbBalanceAdapter.addgb] param[uid:{},orderid:{}], result:{}", Long.valueOf(gbuid), chOrderId,
                    paymentResult, TraceHelper.getTrace(order));
            if (paymentResult != null && paymentResult.getCode() == 1) {
                String newExt = JsonHelper.putJson(ext, gbBalanceConsts.ADD_gb, gbBalanceConsts.ADD_gb_SUCCESS);
                if (StringUtils.isEmpty(newExt)) {
                    LOG.error("[gbBalanceAdapter.addgb] success ,but set the ext param fail",
                            TraceHelper.getTrace(order));
                } else {
                    order.setExt(newExt);
                    return order;
                }
            }
        } catch (Exception e) {
            TimeCostHelper.resume();
            LOG.error("[gbBalanceAdapter.addgb] error.", e, TraceHelper.getTrace(order));
        } finally {
            ThrifeUtils.close(client);
        }
        String newExt = JsonHelper.putJson(ext, gbBalanceConsts.ADD_gb, gbBalanceConsts.ADD_gb_FAIL);
        if (StringUtils.isEmpty(newExt)) {
            LOG.error("[gbBalanceAdapter.addgb] fail ,and set the ext param fail too", TraceHelper.getTrace(order));
        }
        order.setExt(newExt);
        return order;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }
}
