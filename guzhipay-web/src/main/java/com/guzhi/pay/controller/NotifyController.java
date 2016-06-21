/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.channel.broadbrand.BroadbandConsts;
import com.guzhi.pay.channel.broadbrand.BroadbandHelper;
import com.guzhi.pay.channel.jw.JwConsts;
import com.guzhi.pay.channel.jw.JwHelper;
import com.guzhi.pay.channel.kq.KqConsts;
import com.guzhi.pay.channel.kq.KqHelper;
import com.guzhi.pay.channel.lkl.LklHelper;
import com.guzhi.pay.channel.paypal.PaypalHelper;
import com.guzhi.pay.channel.qihu.QihuConsts;
import com.guzhi.pay.channel.qihu.QihuHelper;
import com.guzhi.pay.channel.sms.SmsHelper;
import com.guzhi.pay.channel.szf.SzfHelper;
import com.guzhi.pay.channel.tenpay.TenpayConsts;
import com.guzhi.pay.channel.tenpay.TenpayHelper;
import com.guzhi.pay.channel.unionpay.Constants;
import com.guzhi.pay.channel.unionpay.UnionpayWapHelper;
import com.guzhi.pay.channel.vpay.VpayConsts;
import com.guzhi.pay.channel.vpay.VpayHelper;
import com.guzhi.pay.channel.yeepay.YeePayConsts;
import com.guzhi.pay.channel.yeepay.YeePayHelper;
import com.guzhi.pay.channel.yeepay.gate.YeePayGateHelper;
import com.guzhi.pay.channel.zfb.ZfbConsts;
import com.guzhi.pay.channel.zfb.ZfbHelper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpUtils;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.UrlHelper;
import com.guzhi.pay.util.PayOrderUtil;

/**
 * 接收支付结果通知（NotifyUrl）
 * 
 * @author administrator
 * @author administrator
 */
@Controller
@RequestMapping("/ch/notify")
public class NotifyController {
    @Autowired
    private DomainResource resource;
    // @Autowired
    // private TaskThreadPool defaultTaskThreadPool;

    private static final Logger logger = LoggerFactory.getLogger(NotifyController.class);

    /**
     * 默认的接收地址，不应该走到这个类来。
     */
    @RequestMapping(value = "/")
    @ResponseBody
    public String defaultNotify() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String pathInfo = request.getPathInfo();
        String errorInfo = "channel notify with wrong url, pathInfo=" + pathInfo;
        logger.error(errorInfo);
        return errorInfo;
    }

    /**
     * 支付宝的异步通知（NotifyUrl）
     */
    @RequestMapping(value = "/zfb")
    @ResponseBody
    public String zfbNotify(HttpServletRequest request, HttpServletResponse response) {
        // TODO: Audit log

        String result = ZfbConsts.NOTIFY_HANDLE_RESULT_SUCCESS;
        try {
            // create payOrder

            // Map<String, String> params = request.getParameterMap();
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("zfb notify map={}", HttpUtils.map2String(params));
            PayOrder payOrder = ZfbHelper.assemblePayOrder(resource, params);
            logger.info("[zfbNotify] assembled payOrder={}", payOrder);

            // update payOrder in DB
            ZfbHelper.updatePayOrderByReturnNotify(payOrder, params);

            handleNotifyOrder(payOrder);
        } catch (Throwable t) {
            logger.warn("[zfbNotify]exception when handling ReturnUrl!", t);
            result = ZfbConsts.NOTIFY_HANDLE_RESULT_FAILED;
        }
        return result;
    }

    /**
     * 神州付神州行异步通知（NotifyUrl）
     */
    @RequestMapping(value = "/szfSzx")
    @ResponseBody
    public String szfSzxNotify(HttpServletRequest request, HttpServletResponse response) {

        String result = "";
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("szfSzx notify map={}", HttpUtils.map2String(params));
            // 根据回调参数组装payorder
            PayOrder payOrder = SzfHelper.assemblePayOrder(resource, params);
            logger.info("[szfSzxNotify] assembled payOrder={}", payOrder);

            handleNotifyOrder(payOrder);

            result = payOrder.getChOrderId();
        } catch (Throwable t) {
            logger.warn("[szfSzxNotify]exception when handling ReturnUrl!", t);
        }
        return result;
    }

    /**
     * 骏网一卡通异步通知（NotifyUrl）
     */
    @RequestMapping(value = "/jwjk")
    @ResponseBody
    public String jwJkNotify(HttpServletRequest request, HttpServletResponse response) {

        String result = "";
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("jwjk notify map={}", HttpUtils.map2String(params));
            // 根据回调参数组装payorder
            PayOrder payOrder = JwHelper.assemblePayOrder(resource, params);
            logger.info("[jwJkNotify] assembled payOrder={}", payOrder);

            handleNotifyOrder(payOrder);

            result = JwConsts.OK;
        } catch (Throwable t) {
            logger.warn("[jwJkNotify]exception when handling ReturnUrl!", t);
        }
        return result;
    }

    /**
     * 支付宝手机充值的异步通知（NotifyUrl）
     */
    @RequestMapping(value = "/zfbwap")
    @ResponseBody
    public String zfbWapNotify(HttpServletRequest request, HttpServletResponse response) {
        String result = ZfbConsts.NOTIFY_HANDLE_RESULT_SUCCESS;
        try {
            Map<String, String> params = HttpUtils.getParameterMapForNotify(request);
            logger.info("zfbwap notify map={}", HttpUtils.map2String(params));
            PayOrder payOrder = ZfbHelper.assembleAsynPayWapOrder(resource, params);
            logger.info("[zfbWapNotify] assembled  payOrder={}", payOrder);

            handleNotifyOrder(payOrder);

        } catch (Throwable t) {
            logger.warn("[zfbWapNotify]exception when handling ReturnUrl!", t);
            result = ZfbConsts.NOTIFY_HANDLE_RESULT_FAILED;
        }
        return result;
    }

    /**
     * 支付宝手机App充值的异步通知（NotifyUrl）
     */
    @RequestMapping(value = "/zfbwapapp")
    @ResponseBody
    public String zfbWapAppNotify(HttpServletRequest request, HttpServletResponse response) {
        String result = ZfbConsts.NOTIFY_HANDLE_RESULT_SUCCESS;
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("zfbWapAppNotify notify map={}", HttpUtils.map2String(params));
            PayOrder payOrder = ZfbHelper.assembleAsynPayWapAppOrder(resource, params);
            logger.info("[zfbWapAppNotify] assembled  payOrder={}", payOrder);

            handleNotifyOrder(payOrder);

        } catch (Throwable t) {
            logger.warn("[zfbWapAppNotify]exception when handling ReturnUrl!", t);
            result = ZfbConsts.NOTIFY_HANDLE_RESULT_FAILED;
        }
        return result;
    }

    /**
     * paypal返回token
     */
    @RequestMapping(value = "/paypalToken")
    @ResponseBody
    public String paypalTokenNotify(HttpServletRequest request, HttpServletResponse response) {

        String result = "";
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("paypalToken notify map={}", HttpUtils.map2String(params));
            PayOrder payOrder = PaypalHelper.assemblePaypalToken(resource, params);
            logger.info("[paypalTokenNotify] assembled  payOrder={}", payOrder);

            handleNotifyOrder(payOrder);

            String returnUrl = payOrder.getReturnUrl();
            if (!StringUtils.isBlank(returnUrl)) {
                String redirectUrlQueryStr = ZfbHelper.assemblePayResultQueryStr(payOrder);
                result = UrlHelper.addQuestionMark(returnUrl) + redirectUrlQueryStr;
                logger.info("paypal redirect={} ", result);
                response.sendRedirect(result);
                return null;
            }
            // 无需回调业务线
            // createTask(payOrder);
        } catch (Throwable t) {
            logger.warn("[paypalTokenNotify]exception when handling ReturnUrl!", t);
            result = ZfbConsts.NOTIFY_HANDLE_RESULT_FAILED;
        }
        return result;
    }

    /**
     * paypal 在用户取消订单的情况下回调
     */
    @RequestMapping(value = "/paypalCancel")
    @ResponseBody
    public String paypalCancelNotify(HttpServletRequest request, HttpServletResponse response) {

        // TODO 需要问对方协议
        String result = "paypal cancel success";
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("paypalToken paypalCancel map={}", HttpUtils.map2String(params));
            PayOrder payOrder = PaypalHelper.assemblePaypalCancel(resource, params);
            logger.info("[paypalCancelNotify] assembled  payOrder={}", payOrder);

            handleNotifyOrder(payOrder);

            // TODO 以后会根据返回的returnUrl
            // String returnUrl = payOrder.getReturnUrl();
            // if (!StringUtils.isBlank(returnUrl)) {
            // String redirectUrlQueryStr =
            // ZfbHelper.assemblePayResultQueryStr(payOrder);
            // // result = "redirect:" + returnUrl + "?" + redirectUrlQueryStr;
            // result = UrlHelper.addQuestionMark(returnUrl) +
            // redirectUrlQueryStr;
            // logger.info("paypal redirect={} ", result);
            // response.sendRedirect(result);
            // return null;
            // }
            // 无需回调业务线
            // createTask(payOrder);
        } catch (Throwable t) {
            logger.warn("[paypalTokenNotify]exception when handling ReturnUrl!", t);
            result = ZfbConsts.NOTIFY_HANDLE_RESULT_FAILED;
        }
        return result;
    }

    /**
     * paypal的异步通知（NotifyUrl）
     */
    @RequestMapping(value = "/paypalBalance")
    @ResponseBody
    public String paypalBalanceNotify(HttpServletRequest request, HttpServletResponse response) {
        String result = "";
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("paypalBalance notify map={}", HttpUtils.map2String(params));
            PayOrder payOrder = PaypalHelper.assembleAsynPayOrder(resource, params);
            logger.info("[paypalBalanceNotify] assembled  payOrder={}", payOrder);

            handleNotifyOrder(payOrder);
            // 判断是否需要加入黑名单
            PaypalHelper.assembleUserAccountLimit(resource, params, payOrder);
        } catch (Throwable t) {
            logger.warn("[paypalBalanceNotify]exception when handling ReturnUrl!", t);
            result = ZfbConsts.NOTIFY_HANDLE_RESULT_FAILED;
        }
        return result;
    }

    /**
     * 快钱的异步通知（NotifyUrl）
     */

    @RequestMapping(value = "/kq")
    @ResponseBody
    public String kqNotify(HttpServletRequest request, HttpServletResponse response) {
        String failResult = KqConsts.NOTIFY_RESULT_FAIL;
        String result = failResult;
        Map<String, String> params = HttpUtils.getParameterMap(request);
        logger.info("[kqNotify] request map:{}", HttpUtils.map2String(params));
        try {
            // create payOrder
            String domailUrl = getReqDomainUrl(request);
            logger.info("[kqNotify] before change domainUrl protocal,domainUrl:{}", domailUrl);
            // url协议转换
            domailUrl = translate2Https(domailUrl);
            logger.info("[kqNotify] after change domainUrl protocal,domainUrl:{}", domailUrl);
            PayOrder payOrder = KqHelper.assemblePayOrder(resource, params, false);
            logger.info("[kqNotify] assembled payOrder={}", payOrder);

            handleNotifyOrder(payOrder);

            result = KqHelper.assembleNotifySuccessUrl(domailUrl);
            logger.info("[kqNotify] response notify.orderid:{},result:{}",
                    OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId()), result);
        } catch (Throwable t) {
            logger.warn("exception when handling ReturnUrl,params:{}!", params, t);
        }
        return result;
    }

    /**
     * 快钱的异步通知（NotifyUrl）
     */
    @RequestMapping(value = "/kq/card")
    @ResponseBody
    public String kqCardNotify(HttpServletRequest request, HttpServletResponse response) {
        String failResult = KqConsts.NOTIFY_RESULT_FAIL;
        String result = failResult;
        Map<String, String> params = HttpUtils.getParameterMap(request);
        logger.info("[kqCardNotify] request map:{}", HttpUtils.map2String(params));
        try {
            String domailUrl = getReqDomainUrl(request);
            logger.info("[kqCardNotify] before change domainUrl,domainUrl:{}", domailUrl);
            // url协议转换
            domailUrl = translate2Https(domailUrl);
            logger.info("[kqCardNotify] after change domainUrl,domainUrl:{}", domailUrl);
            // create payOrder
            PayOrder payOrder = KqHelper.assemblePayOrder(resource, params, true);
            logger.info("[kqCardNotify] assembled payOrder:{}", payOrder);

            handleNotifyOrder(payOrder);

            result = KqHelper.assembleNotifySuccessUrl(domailUrl, true);
            logger.info("[kqCardNotify] response result:{},orderid:{}", result,
                    OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId()));
        } catch (Throwable t) {
            logger.warn("[kqCardNotify] exception when handling ReturnUrl,params:{}", params, t);
            result = failResult;
        }
        return result;
    }

    /**
     * 奇虎的异步通知（NotifyUrl）
     */
    @RequestMapping(value = "/qihu")
    @ResponseBody
    public String qihuNotify(HttpServletRequest request, HttpServletResponse response) {

        String result = QihuConsts.NOTIFY_HANDLE_RESULT_SUCCESS;
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("[qihuNotify] notify map={}", HttpUtils.map2String(params));
            PayOrder payOrder = QihuHelper.assemblePayOrder(resource, params);
            logger.info("[qihuNotify] assembled payOrder={}", payOrder);

            // update payOrder in DB
            QihuHelper.updatePayOrderByReturnNotify(payOrder, params);

            handleNotifyOrder(payOrder);

        } catch (Throwable t) {
            logger.warn("[qihuNotify]exception when handling ReturnUrl!", t);
            result = QihuConsts.NOTIFY_HANDLE_RESULT_FAILED;
        }
        PayOrderUtil.outPutStr(response, result);
        return null;
    }

    /**
     * 短信支付异步 通知（NotifyUrl）
     */
    @RequestMapping(value = "/smsyd")
    @ResponseBody
    public String smsNotify(HttpServletRequest request, HttpServletResponse response) {
        String result = SmsHelper.genResp("");
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("[smsNotify] map={}", HttpUtils.map2String(params));
            PayOrder payOrder = SmsHelper.assembeSmsOrderByNotify(params, resource);

            handleNotifyOrder(payOrder);

            result = SmsHelper.genResp(SmsHelper.getNotifyReturnMsg(params, payOrder));
            logger.info("smsNotify notify result:{},appid:{},apporderid:{}", result, payOrder.getAppId(),
                    payOrder.getAppOrderId());
        } catch (PayException t) {
            logger.error("PayException error statuCode:{},statusMsg:{}", t.getStatusCode(), t.getStatusMsg());
            result = SmsHelper.genResp("");
        } catch (Throwable t) {
            logger.error("exception when handling ReturnUrl!", t);
            result = SmsHelper.genResp("");
        }
        return result;
    }

    /**
     * 盈华讯方电话支付V币的异步通知（NotifyUrl）
     */
    @RequestMapping(value = "/vpay-tel")
    @ResponseBody
    public String vpayTelNotify(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = HttpUtils.getParameterMap(request);
        logger.info("[vpayTelNotify] map={}", HttpUtils.map2String(params));
        try {
            PayOrder payOrder = VpayHelper.assembleTelPayOrderByNotify(params, resource);

            handleNotifyOrder(payOrder);

            response.addHeader("Data-Received", "ok_vpay8");

            // 前台通知和后台通知使用相同的地址。
            String returnUrl = payOrder.getReturnUrl();
            if (StringUtils.isBlank(returnUrl)) {
                logger.warn("[vpayTelNotify] returnUrl for payorder is empty,can't redirect! apporderid={}",
                        payOrder.getAppOrderId());
                return null;
            }
            String redirectUrl = returnUrl + "?" + StringHelper.assemblePayResultQueryStr(payOrder);
            logger.info("[vpayTelNotify] return :{}", redirectUrl);
            response.sendRedirect(redirectUrl);

        } catch (PayException t) {
            logger.error("PayException error params:{},statuCode:{},statusMsg:{}", params, t.getStatusCode(),
                    t.getStatusMsg());
        } catch (Throwable t) {
            logger.error("exception when handling ReturnUrl!", t);
        }
        return "";
    }

    /**
     * 盈华讯方短信支付Notify处理。
     * Note:该地址不能更新,参考{@link com.guzhi.pay.channel.vpay.VpayConsts}。
     */
    @RequestMapping("/vpaySms")
    @ResponseBody
    public String vpaySmsNotify(HttpServletRequest req, HttpServletResponse resp) {

        Map<String, String> requestMap = HttpUtils.getParameterMap(req);
        logger.info("[vpaySmsWithNotify] get notify request parameters:{}", requestMap);
        String result = VpayConsts.NOTIFY_SUCCESS_MSG;
        try {
            PayOrder payOrder = VpayHelper.assembleSmsPayOrderByNotify(requestMap, resource);

            handleNotifyOrder(payOrder);

        } catch (Throwable e) {
            logger.warn("[vpaySmsNotify] exception when handling ReturnUrl!", e);
            result = VpayConsts.NOTIFY_FAIL_MSG;
        }
        return result;
    }

    /**
     * 盈华讯方PC端短信支付通知处理。
     * 处理逻辑同盈华讯方短信支付一样，下面对这段看似冗余的代码进行说明。
     * 移动短信支付和PC端短信支付都有各自独立使用的接口，应该被看作两个不同的渠道，前面的支付请求流程与通知流程都区分开来比较合适。
     * 
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/vpay-pc-sms")
    @ResponseBody
    public String vpayPcsmsNotify(HttpServletRequest req, HttpServletResponse resp) {

        Map<String, String> requestMap = HttpUtils.getParameterMap(req);
        logger.info("[vpayPcsmsNotify] get notify request parameters:{}", requestMap);
        String result = VpayConsts.NOTIFY_SUCCESS_MSG;
        try {
            PayOrder payOrder = VpayHelper.assembleSmsPayOrderByNotify(requestMap, resource);

            handleNotifyOrder(payOrder);

        } catch (Throwable e) {
            logger.warn("[vpayPcsmsNotify] exception when handling ReturnUrl!", e);
            result = VpayConsts.NOTIFY_FAIL_MSG;
        }
        return result;
    }

    /**
     * 易宝网关的异步通知和同步通知（NotifyURL和returnURL）
     * Note：易宝的notifyURL和returnURL相同。
     */
    @RequestMapping(value = "/yeepay-gate")
    @ResponseBody
    public String yeepayNotify(HttpServletRequest request, HttpServletResponse response) {
        String result = YeePayConsts.SUCCESS;
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("yeepay notify,map={}", HttpUtils.map2String(params));
            PayOrder payOrder = YeePayGateHelper.assemblePayOrderByNotify(resource, params);
            logger.info("[yeepayNotify] assembled payOrder={}", payOrder);

            handleNotifyOrder(payOrder);

            // If redirect parameter was set by yeepay,we redirect here.
            if (YeePayGateHelper.isReturnTypeRequest(params)) {
                if (StringUtils.isNotBlank(payOrder.getReturnUrl())) {
                    String fullReturnUrl = payOrder.getReturnUrl();
                    if (payOrder.getReturnUrl().contains("?")) {
                        fullReturnUrl += "&";
                    } else {
                        fullReturnUrl += "?";
                    }
                    fullReturnUrl += StringHelper.assemblePayResultQueryStr(payOrder);
                    logger.info("[yeepayNotify] redirect,orderId:{},url:{}", payOrder.getAppOrderId(), fullReturnUrl);
                    response.sendRedirect(fullReturnUrl);
                    response.flushBuffer();
                }
            }
            // else {
            // // create async task to notify
            // createTask(payOrder);
            // }
        } catch (Throwable t) {
            logger.warn("[yeepayNotify] exception when handling NotifyUrl!", t);
            result = YeePayConsts.NOTIFY_HANDLE_RESULT_FAILED;
        }
        return result;
    }

    /**
     * 处理易宝卡密支付的异步通知
     */
    @RequestMapping(value = "/yeepay-card")
    @ResponseBody
    public String yeepayCardNotify(HttpServletRequest request, HttpServletResponse response) {
        String result = YeePayConsts.SUCCESS;
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("[yeepayCardNotify] yeepay notify map={}", HttpUtils.map2String(params));
            PayOrder payOrder = YeePayHelper.assemblePayOrderByNotify(resource, params);
            logger.info("[yeepayCardNotify] assembled payOrder={}", payOrder);

            handleNotifyOrder(payOrder);

        } catch (Throwable t) {
            logger.warn("[yeepayCardNotify] exception when handling notify url!", t);
            result = YeePayConsts.NOTIFY_HANDLE_RESULT_FAILED;
        }
        return result;
    }

    @RequestMapping("/unionPayWap")
    @ResponseBody
    public String unionPayNotify(HttpServletRequest req, HttpServletResponse resp) {
        String result = "";
        try {
            Map<String, String> requestMap = UnionpayWapHelper.generateNotifyRequestMap(req, resp);
            if (null == requestMap || requestMap.isEmpty()) {
                logger.info("[unionPayNotify] error,because of empty required request data.");
                return null;
            }
            if (null != requestMap && !requestMap.isEmpty()) {
                logger.info("[unionPayNotify] get notify from unionpay.requestMap:{}", requestMap,
                        "ds:trace:" + requestMap.get(Constants.KEY_MERCHANTORDERID));
            }
            PayOrder payOrder = UnionpayWapHelper.assemblePayorderByNotify(resource, requestMap);

            handleNotifyOrder(payOrder);

            result = UnionpayWapHelper.getNotifyResponse(requestMap);
        } catch (Throwable t) {
            logger.warn("[unionPayNotify] exception when handling notify url!", t);
        }
        return result;
    }

    @RequestMapping(value = "/unionPayWapApp")
    @ResponseBody
    public String unionPayWapAppNotify(HttpServletRequest req, HttpServletResponse resp) {
        Map<String, String> params = HttpUtils.getParameterMap(req);
        logger.info("[unionPayWapApp_notify] get notify,params:{}", params);
        String result = "unknow";
        try {
            String chOrderId = params.get(Constants.KEY_ORDERNUMBER);
            if (StringUtils.isBlank(chOrderId)) {
                throw new PayException(Consts.SC.DATA_ERROR, "unexcepted parameter with out orderNumber");
            }
            PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
            UnionpayWapHelper.assembleAppPayOrder(payOrder, params);

            handleNotifyOrder(payOrder);

        } catch (Throwable t) {
            logger.warn("[unionPayWapApp_notify] exception when handling notify url.params:{}", params, t);
            // 银联服务器在收到商户服务器响应状态码为200时，认为通知成功，其他均为通知失败.
            resp.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
        }
        return result;
    }

    @RequestMapping("/lklBalance")
    @ResponseBody
    public String lklBalanceNotify(HttpServletRequest req, HttpServletResponse resp) {
        Map<String, String> requestMap = HttpUtils.getParameterMap(req);
        logger.info("[lklBalanceWithNotify] get notify request parameters:{}", requestMap);
        String result = "";
        try {
            PayOrder payOrder = LklHelper.updatePayOrderWithNotify(requestMap, resource);

            handleNotifyOrder(payOrder);

            result = LklHelper.returnNotify();
        } catch (Throwable e) {
            logger.warn("[lklBalanceWithNotify] exception when handling ReturnUrl!", e);
        }
        return result;
    }

    /**
     * Mock渠道通知。
     */
    @RequestMapping(value = "/mock")
    @ResponseBody
    public String mockNotify(@RequestParam String appId, @RequestParam String appOrderId, @RequestParam String pay) {

        logger.info("[mockNotify] appId:{},appOrderId:{},pay:{}", appId, appOrderId, pay);

        PayOrder order = resource.getPayOrder(appId, appOrderId);
        if (order == null) {
            return "fail";
        }
        // 做安全校验: 非mock渠道不通过
        if (!Consts.Channel.MOCK.equalsIgnoreCase(order.getChId())) {
            return "fail";
        }
        // 做安全校验: 1000元以上的不通过
        if (BigDecimal.valueOf(1000l).compareTo(order.getAmount()) < 0) {
            return "fail";
        }
        order.setStatusCode("success".equals(pay) ? Consts.SC.SUCCESS : Consts.SC.FAIL);
        order.setStatusMsg("mock支付: " + pay);

        handleNotifyOrder(order);

        if (StringUtils.isNotBlank(order.getReturnUrl())) {
            order = resource.getPayOrder(appId, appOrderId);
            order.setAppInfo(resource.getAppInfo(appId));
            String returnMsg = HttpUtils.spliceUrl(order.getReturnUrl(), PayOrderUtil.getResp(order));
            return "success" + returnMsg;
        } else {
            return "success";
        }

    }

    @RequestMapping(value = "/broadband-txtong")
    @ResponseBody
    public String broadbandTxtongNotify(HttpServletRequest req, HttpServletResponse resp) {
        String requestString = req.getQueryString();
        logger.info("[broadbandTxtong.notify] get notify request parameters:{}", requestString);
        String result = "";
        try {
            String chOrderId = req.getParameter(BroadbandConsts.KEY_MCH_ORDER_ID);
            PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
            BroadbandHelper.updatePayOrderByNotify(payOrder, requestString);
            handleNotifyOrder(payOrder);
            result = BroadbandConsts.NOTIFYSUCCESS;
        } catch (Throwable e) {
            logger.warn("[broadbandTxtong.notify] exception when handling ReturnUrl!", e);
        }
        return result;
    }

    @RequestMapping(value = "/tenpay")
    @ResponseBody
    public String tenpayNotify(HttpServletRequest req, HttpServletResponse resp) {
        Map<String, String> dataMap = HttpUtils.getParameterMap(req);
        logger.info("[tenpay.notify] get notify request parameters:{}", dataMap);
        String result = "fail";
        try {
            String chOrderId = req.getParameter(TenpayConsts.KEY_OUT_TRADE_NO);
            PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
            TenpayHelper.updatePayOrderByNotify(payOrder, dataMap);
            handleNotifyOrder(payOrder);
            result = TenpayConsts.NOTIFYSUCCESS;
        } catch (Throwable e) {
            logger.warn("[tenpay.notify] exception when handling ReturnUrl!", e);
        }
        return result;
    }

    private void handleNotifyOrder(PayOrder notifyOrder) {

        PayOrder oriOrder = resource.getPayOrder(notifyOrder.getAppId(), notifyOrder.getAppOrderId());

        if (oriOrder == null) {
            logger.info("[handleNotifyOrder] notifyOrder not exsits! appId:{},appOrderId:{}", notifyOrder.getAppId(),
                    notifyOrder.getAppOrderId());
            return;
        }
        // 已经是成功的订单，don't care
        if (Consts.SC.SUCCESS.equalsIgnoreCase(oriOrder.getStatusCode())) {
            logger.info("[handleNotifyOrder] oriOrder is code_success, don't care notifyOrder! ");
            return;
        }
        // 如果通知状态与数据库状态一样，也忽略
        if (StringUtils.equalsIgnoreCase(notifyOrder.getStatusCode(), oriOrder.getStatusCode())) {
            logger.info("[handleNotifyOrder] oriOrder.statusCode equals with notifyOrder, don't care ");
            return;
        }
        resource.updatePayOrder(notifyOrder);
        createTask(notifyOrder);
    }

    /**
     * 创建通知任务
     * 
     * @param payOrder
     */
    private void createTask(PayOrder payOrder) {
        // 需要做YB相关的定时任务
        String taskType = "";
        Task task = null;
        // 当yyoper 参数不为空，并且订单状态是成功时才充YY币或者保证金
        if ((!StringUtils.isEmpty(payOrder.getYyOper()))
                && (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode()))) {
            // add yb
            if (Consts.YbOper.ADD.equals(payOrder.getYyOper())) {
                taskType = Task.TYPE_ADD_YY;
                task = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), taskType);
            } else if (Consts.YbOper.ADD_DEPOSIT.equals(payOrder.getYyOper())) {
                // add deposit
                taskType = Task.TYPE_ADD_DEPOSIT;
                task = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), taskType);
            } else if (Consts.YbOper.ADD_CHANNEL_DEPOSIT.equals(payOrder.getYyOper())) {
                // add channel deposit
                taskType = Task.TYPE_ADD_CHANNEL_DEPOSIT;
                task = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), taskType);
            }
            // notify
        } else {
            taskType = Task.TYPE_NOTIFY;
            task = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), taskType);
        }
        if (task == null && !StringUtils.isEmpty(taskType)) {
            logger.info("[createTask],taskType:" + taskType);
            resource.createTask(new Task(payOrder.getAppId(), payOrder.getAppOrderId(), taskType, payOrder.getChId(),
                    payOrder.getPayMethod()));
        }
        logger.info("[createTask],appid:{},apporderid:{}, tasktype:{}", payOrder.getAppId(), payOrder.getAppOrderId(),
                taskType);
    }

    /**
     * 获取请求域名
     * 
     * @param request
     * @return
     */
    private String getReqDomainUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String domainUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
        return domainUrl;
    }

    /**
     * 由于web专区ngnix的转发，获取到的请求都是http协议，这里需要将其更新为https协议。
     * 
     * @param url
     * @return
     */
    private String translate2Https(String url) {
        if (StringUtils.isNotBlank(url)) {
            return url.replaceFirst("^http:", "https:");
        }
        return url;
    }
}
