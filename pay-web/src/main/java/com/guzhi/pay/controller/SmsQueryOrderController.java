/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.guzhi.pay.channel.sms.SmsHelper;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpUtils;
import com.guzhi.pay.util.PayOrderUtil;

/**
 * @author administrator
 * 
 */

@Controller
public class SmsQueryOrderController {
    @Autowired
    private DomainResource resource;

    private static final Logger logger = LoggerFactory.getLogger(SmsQueryOrderController.class);

    /**
     * sms的订单查询确认
     * 
     */
    // @ResponseBody
    @ModelAttribute
    @RequestMapping(value = "/ch/sms/queryOrder")
    public String query(HttpServletRequest request, HttpServletResponse response) {
        String result = null;
        try {
            result = handlerRequest(request);
        } catch (Throwable t) {
            logger.error("exception when handling ReturnUrl!", t);
            result = SmsHelper.genResp(result);
        }
        PayOrderUtil.outPutStr(response, result);
        return null;
    }

    /**
     * 验证订单是否合法
     * 
     * @param request
     * @return
     */
    private String handlerRequest(HttpServletRequest request) {
        Map<String, String> params = HttpUtils.getParameterMap(request);
        logger.info("[SmsQueryOrderController.query]  map={}", HttpUtils.map2String(params));
        String result = SmsHelper.genResp("");
        try {
            String validSmsOrder = SmsHelper.assembeSmsOrder(params, resource);
            result = SmsHelper.genResp(validSmsOrder);
            logger.info("SmsQueryOrderController result:{}", result);
        } catch (PayException t) {
            logger.error("PayException error statuCode:{},statusMsg:{}", t.getStatusCode(), t.getStatusMsg());
            result = SmsHelper.genResp("");
        } catch (Throwable t) {
            logger.error("exception when handling ReturnUrl!", t);
            result = SmsHelper.genResp("");
        }
        return result;
    }
}
