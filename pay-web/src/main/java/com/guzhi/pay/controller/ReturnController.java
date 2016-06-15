/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.channel.kq.KqHelper;
import com.guzhi.pay.channel.qihu.QihuConsts;
import com.guzhi.pay.channel.qihu.QihuHelper;
import com.guzhi.pay.channel.tenpay.TenpayConsts;
import com.guzhi.pay.channel.tenpay.TenpayHelper;
import com.guzhi.pay.channel.unionpay.Constants;
import com.guzhi.pay.channel.unionpay.UnionpayWapHelper;
import com.guzhi.pay.channel.zfb.ZfbHelper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpUtils;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 接收支付返回页面（ReturnUrl）
 * 
 * @author administrator
 */
@Controller
@RequestMapping("/ch/return")
public class ReturnController {
    @Autowired
    private DomainResource resource;
    private static final Logger logger = LoggerFactory.getLogger(ReturnController.class);

    private final static String DEFAULT_RETURN_URL = "../../return.jsp";
    private final static String DEFAULT_RETURN_ERROR_URL = "../../returnerror.jsp";

    /**
     * 默认的接收地址，不应该走到这个类来。
     */
    @RequestMapping(value = "/")
    @ResponseBody
    public String defaultReturn() {
        HttpServletRequest request = getRequest();
        String pathInfo = request.getPathInfo();
        String errorInfo = "channel return with wrong url, pathInfo=" + pathInfo;
        logger.error(errorInfo);
        return errorInfo;
    }

    /**
     * 接收来自支付宝的同步通知（ReturnUrl）。<br>
     */
    // @ResponseBody
    @RequestMapping(value = "/zfb")
    public String zfbReturn(HttpServletRequest request, HttpServletResponse response) {
        // TODO: Audit log

        String redirectUrl = null;
        PayOrder payOrder = null;
        try {
            // create payOrder
            // Map<String, String> params = request.getParameterMap();
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("return map={}", HttpUtils.map2String(params));
            payOrder = ZfbHelper.assemblePayOrder(resource, params);

            logger.info("[zfbReturn] payOrder:{}", payOrder);

            // by design, should be able to redirect
            String returnUrl = payOrder.getReturnUrl();
            if (StringUtils.isBlank(returnUrl)) {
                logger.warn("[zfbReturn]returnUrl for app is empty, can NOT redirect! params={}", params);
                return redirectUrl;
            }
            if (!Consts.SC.SUCCESS.equals(payOrder.getStatusCode()) && !Consts.SC.FAIL.equals(payOrder.getStatusCode())) {
                // update payOrder in DB
                ZfbHelper.updatePayOrderByReturnNotify(payOrder, params);
                // resource.updatePayOrder(payOrder);
            }

            // create redirect url
            String redirectUrlQueryStr = ZfbHelper.assemblePayResultQueryStr(payOrder);
            returnUrl = returnUrl + "?" + redirectUrlQueryStr;
            if (StringUtils.isBlank(redirectUrl)) {
                redirectUrl = "redirect:" + returnUrl;
            }
            response.sendRedirect(returnUrl);
            return null;
        } catch (Throwable t) {
            logger.warn("exception when handling ReturnUrl!", t);
            try {
                response.sendRedirect("../../returnerror.jsp");
                return null;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        logger.info("[zfbReturn] return:{}", redirectUrl);
        return redirectUrl;
    }

    /**
     * 接收来自支付宝的手机支付同步通知（ReturnUrl）。<br>
     */
    @RequestMapping(value = "/zfbwap")
    public String zfbWapReturn() {
        HttpServletRequest request = getRequest();
        String redirectUrl = null;
        PayOrder payOrder = null;
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("zfbwap return map={}", HttpUtils.map2String(params));
            payOrder = ZfbHelper.assembleSynPayWapOrder(resource, params);
            logger.info("[zfbWapReturn] payOrder:{}", payOrder);
            String returnUrl = payOrder.getReturnUrl();
            if (StringUtils.isBlank(returnUrl)) {
                logger.warn("[zfbWapReturn]returnUrl for app is empty, can NOT redirect! params={}", params);
                return redirectUrl;
            }
            // resource.updatePayOrder(payOrder);
            String redirectUrlQueryStr = ZfbHelper.assemblePayResultQueryStr(payOrder);
            if (StringUtils.isBlank(redirectUrl)) {
                redirectUrl = "redirect:" + returnUrl + "?" + redirectUrlQueryStr;
            }
        } catch (Throwable t) {
            logger.warn("exception when handling ReturnUrl!", t);
        }
        logger.info("[zfbWapReturn] return:{}", redirectUrl);
        return redirectUrl;
    }

    /**
     * 接收来自奇虎的同步通知（ReturnUrl）。<br>
     */
    // @ResponseBody
    @RequestMapping(value = "/qihu")
    public String qihuReturn(HttpServletRequest request, HttpServletResponse response) {
        String redirectUrl = null;
        PayOrder payOrder = null;
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("[qihuReturn] return map={}", HttpUtils.map2String(params));
            payOrder = QihuHelper.assemblePayOrder(resource, params);
            logger.info("[qihuReturn] payOrder:{}", payOrder);
            if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode()) || Consts.SC.FAIL.equals(payOrder.getStatusCode())) {
                logger.info("[qihuReturn] payOrder status is finnal,not need to update", payOrder);
            } else {
                // update payOrder in DB
                QihuHelper.updatePayOrderByReturnNotify(payOrder, params);
                // resource.updatePayOrder(payOrder);
            }
            // create redirect url
            String redirectUrlQueryStr = ZfbHelper.assemblePayResultQueryStr(payOrder);
            String returnUrl = payOrder.getReturnUrl();
            if (StringUtils.isBlank(returnUrl)) {
                logger.warn("[qihuReturn]returnUrl for app is empty, return qihu common return url! params={}", params);
                redirectUrl = "redirect:" + QihuConsts.ADDR_GATEWAY_RETURN + "?" + redirectUrlQueryStr;
            } else {
                redirectUrl = "redirect:" + returnUrl + "?" + redirectUrlQueryStr;
            }
        } catch (Throwable t) {
            logger.warn("exception when handling ReturnUrl!", t);
        }

        logger.info("[qihuReturn] return:{}", redirectUrl);
        return redirectUrl;
    }

    @RequestMapping(value = "/unionPayWap")
    public String unionPayWapReturn(HttpServletRequest req, HttpServletResponse resp) {
        String chOrderId = req.getParameter(Constants.KEY_MERCHANTORDERID);
        String sign = req.getParameter("sign");
        if (StringUtils.isBlank(chOrderId)) {
            logger.warn("[unionPayWapReturn] get a blank merchant orderID, can NOT redirect!");
            return null;
        }
        String redirectUrl = null;
        PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
        if (!UnionpayWapHelper.verifyFrontUrl(payOrder, sign)) {
            logger.warn("[unionPayWapReturn] get a sign error,chOrderId:{},sign:{}", chOrderId, sign);
            return null;
        }
        String returnUrl = payOrder.getReturnUrl();
        if (StringUtils.isBlank(returnUrl)) {
            logger.warn("[unionPayWapReturn]returnUrl for app is empty, can NOT redirect! chOrderId={}", chOrderId);
            return null;
        }
        String redirectUrlQueryStr = ZfbHelper.assemblePayResultQueryStr(payOrder);
        if (StringUtils.isBlank(redirectUrl)) {
            redirectUrl = "redirect:" + returnUrl + "?" + redirectUrlQueryStr;
        }
        logger.info("[unionPayWapReturn] return:{}", redirectUrl);
        return redirectUrl;
    }

    @RequestMapping(value = "/kq/card")
    public String kqCardReturn(HttpServletRequest request, HttpServletResponse response) {
        String redirectUrl = null;
        PayOrder payOrder = null;
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("kq card return  map={}", HttpUtils.map2String(params));
            payOrder = KqHelper.assembleNotifyPayOrder(resource, params, true);
            logger.info("[kqCardReturn] payOrder:{}", payOrder);

            // by design, should be able to redirect
            String returnUrl = payOrder.getReturnUrl();
            if (StringUtils.isBlank(returnUrl)) {
                logger.warn("[kqCardReturn]returnUrl for app is empty, can NOT redirect! params={}", params);
                response.sendRedirect("../../return.jsp");
                return null;
            }

            // create redirect url
            String redirectUrlQueryStr = StringHelper.assemblePayResultQueryStr(payOrder);
            returnUrl = UrlHelper.addQuestionMark(returnUrl) + redirectUrlQueryStr;
            if (StringUtils.isBlank(redirectUrl)) {
                redirectUrl = "redirect:" + returnUrl;
            }
            response.sendRedirect(returnUrl);
            return null;
        } catch (Throwable t) {
            logger.warn("[kqReturn]exception when handling ReturnUrl!:{}", t);
            try {
                response.sendRedirect("../../returnerror.jsp");
                return null;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        logger.info("[kqReturn] return:{}", redirectUrl);
        return redirectUrl;

    }

    @RequestMapping(value = "/kq")
    public String kqReturn(HttpServletRequest request, HttpServletResponse response) {
        String redirectUrl = null;
        PayOrder payOrder = null;
        try {
            Map<String, String> params = HttpUtils.getParameterMap(request);
            logger.info("kq return  map={}", HttpUtils.map2String(params));
            payOrder = KqHelper.assembleNotifyPayOrder(resource, params);
            logger.info("[kqReturn] payOrder:{}", payOrder);

            // by design, should be able to redirect
            String returnUrl = payOrder.getReturnUrl();
            if (StringUtils.isBlank(returnUrl)) {
                logger.warn("[kqReturn]returnUrl for app is empty, can NOT redirect! params={}", params);
                response.sendRedirect("../../return.jsp");
                return null;
            }

            // create redirect url
            String redirectUrlQueryStr = StringHelper.assemblePayResultQueryStr(payOrder);
            returnUrl = UrlHelper.addQuestionMark(returnUrl) + redirectUrlQueryStr;
            if (StringUtils.isBlank(redirectUrl)) {
                redirectUrl = "redirect:" + returnUrl;
            }
            response.sendRedirect(returnUrl);
            return null;
        } catch (Throwable t) {
            logger.warn("[kqReturn]exception when handling ReturnUrl!:{}", t);
            try {
                response.sendRedirect("../../returnerror.jsp");
                return null;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        logger.info("[kqReturn] return:{}", redirectUrl);
        return redirectUrl;

    }

    @RequestMapping(value = "/tenpay")
    public String tenpayReturn(HttpServletRequest request, HttpServletResponse response) {
        PayOrder returnOrder = null;
        Map<String, String> params = HttpUtils.getParameterMap(request);
        logger.info("[return.tenpay] return map:{}", HttpUtils.map2String(params));
        String chOrderId = params.get(TenpayConsts.KEY_OUT_TRADE_NO);
        try {
            returnOrder = Help.getPayOrderByNotify(resource, chOrderId);
            TenpayHelper.updatePayOrderByReturn(returnOrder, params);
            handleReturnOrder(returnOrder, response, null);
        } catch (Exception e) {
            logger.error("[return.tenpay] return failed,orderid:{}", chOrderId, e);
            handleReturnOrder(returnOrder, response, e);
        }
        return null;
    }

    private void handleReturnOrder(PayOrder returnOrder, HttpServletResponse response, Exception e) {
        String redirectUrl = "";
        if (e != null) {
            redirectUrl = DEFAULT_RETURN_ERROR_URL;
        } else {
            redirectUrl = StringUtils.isBlank(returnOrder.getReturnUrl()) ? DEFAULT_RETURN_URL : UrlHelper
                    .addQuestionMark(returnOrder.getReturnUrl()) + StringHelper.assemblePayResultQueryStr(returnOrder);
        }
        try {
            logger.info("[return.tenpay] return to url:{}", redirectUrl);
            response.sendRedirect(redirectUrl);
        } catch (IOException e1) {
            logger.error(e1.getMessage(), e1);
        }
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}