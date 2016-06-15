/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.jw;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.TimeHelper;

/**
 * 骏网一卡通帮助类
 * 
 * @author administrator
 * 
 */
public class JwHelper {
    private static final Logger LOG = LoggerFactory.getLogger(JwHelper.class);

    /**
     * 一卡通卡号密码组装
     * 一卡通卡号密码|最多支持3张一卡通,格式为 卡号1=密码1,卡号2=密码2,卡号3=密码3），必填,双方协商的对称加密,
     * 使用3DES加密,合作方不能保存记录卡密 卡号卡密均为16位数字)
     * 
     * @param payOrder
     * @return
     */

    public static String assembleCardData(PayOrder payOrder) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(payOrder.getCardNum()) || StringUtils.isBlank(payOrder.getCardPass())) {
            throw new PayException(Consts.SC.DATA_ERROR, "支付平台：骏卡卡密不能为空");
        }
        String[] cards = payOrder.getCardNum().split(JwConsts.CARD_SPLIT);
        String[] passwds = DESEncrypt.decryptByAES(payOrder.getAppInfo().getPasswdKey(), payOrder.getCardPass()).split(
                JwConsts.CARD_SPLIT);

        // 卡号支付时卡号和密码必须对称
        if (cards.length != passwds.length) {
            throw new PayException(Consts.SC.DATA_FORMAT_ERROR, " cards=" + payOrder.getCardNum() + ",  passwds ="
                    + payOrder.getCardPass() + ", payOrder=" + payOrder);
        }
        if (cards.length > 3) {
            throw new PayException(Consts.SC.DATA_ERROR, "支付平台：骏卡最多只支持3张卡密组合支付");
        }
        for (int i = 0, n = cards.length; i < n; i++) {
            sb.append(cards[i]);
            sb.append(JwConsts.EQ);
            sb.append(passwds[i]);
            sb.append(JwConsts.CARD_DATA_SPLIT);
        }
        String result = sb.toString();
        return DESEncrypt.encryptBy3DES(result.substring(0, result.length() - 1), JwConsts.THREE_DES_KEY);
    }

    /**
     * 签名
     * 数字签名（32位的md5加密,加密后转换成小写）
     * 需要加密的串：agent_id=***&bill_id=***&bill_time=***&card_data=***&pay_jpoint=**
     * *&time_stamp=***|||md5Key
     * 
     * @param request
     * @param signKey
     * @return
     */
    public static String genPaySign(Map<String, String> request, String signKey) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(JwConsts.KEY_AGENT_ID, request.get(JwConsts.KEY_AGENT_ID));
        map.put(JwConsts.KEY_BILL_ID, request.get(JwConsts.KEY_BILL_ID));
        map.put(JwConsts.KEY_BILL_TIME, request.get(JwConsts.KEY_BILL_TIME));
        map.put(JwConsts.KEY_CARD_DATA, request.get(JwConsts.KEY_CARD_DATA));
        map.put(JwConsts.KEY_PAY_JPOINT, request.get(JwConsts.KEY_PAY_JPOINT));
        map.put(JwConsts.KEY_TIME_STAMP, request.get(JwConsts.KEY_TIME_STAMP));
        return DESEncrypt.getMD5(Help.getStrByMap(map, true) + JwConsts.CONNECT + signKey).toLowerCase();
    }

    /**
     * 根据返回字符串填充payorder
     * 
     * @param payOrder
     * @param respStr
     * @return
     */
    public static void updatePayOrderByPay(PayOrder payOrder, String respStr, String signKey) {
        LOG.info("[jw_in] updatePayOrderByPay results, respStr={}, payOrder={}", respStr, payOrder, "ds:trace:"
                + payOrder.getAppOrderId());
        Map<String, String> respMap = Help.getMapByRespStr(respStr);
        String signMsg = getRespSign(respMap, signKey);
        String signInReq = respMap.get(JwConsts.KEY_SIGN);
        if (!signMsg.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s", signInReq,
                    signMsg);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        if (respMap.get(JwConsts.KEY_RET_CODE).equals(JwConsts.SUCCESS_RESULT_CODE)) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        } else if (JwConsts.CARD_ERROR_RESULT_CODE.equalsIgnoreCase(respMap.get(JwConsts.KEY_RET_CODE))) {
            // 当返回的ret_Code为97时，认为是卡密有错，具体的信息是“一卡通不存在或已经使用完了”或“该卡其他产品的专用充值卡，不能充值本产品！”
            LOG.info("[updatePayOrderByPay] jw get a wrong cardnum and cardpass,orderid:{}", payOrder.getAppOrderId());
            payOrder.setStatusCode(Consts.SC.CARD_ERROR);
        } else if (JwConsts.ReturnResult.RETCODE_CODE_1.equalsIgnoreCase(respMap.get(JwConsts.KEY_RET_CODE))) {
            // 当返回的ret_Code为1时，按照文档，对应的msg是“输入参数有误”，同时收到的msg是“卡号或密码不正确”
            LOG.info("[updatePayOrderByPay] jw get a wrong cardnum and cardpass,returncode=1,orderid:{}",
                    payOrder.getAppOrderId());
            payOrder.setStatusCode(Consts.SC.CARD_ERROR);
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        payOrder.setStatusMsg("骏网：" + translateRetCode(respMap.get(JwConsts.KEY_RET_CODE)));
    }

    /**
     * 对pay 返回串进行签名
     * 数字签名的组成
     * ret_code=***&agent_id=***&bill_id=***&jnet_bill_no=***&card_use_data=***&
     * real_jpoint=***|||md5Key
     * 
     * @param respMap
     * @param signKey
     * @return
     */
    public static String getRespSign(Map<String, String> respMap, String signKey) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(JwConsts.KEY_RET_CODE, respMap.get(JwConsts.KEY_RET_CODE));
        map.put(JwConsts.KEY_AGENT_ID, respMap.get(JwConsts.KEY_AGENT_ID));
        map.put(JwConsts.KEY_BILL_ID, respMap.get(JwConsts.KEY_BILL_ID));
        map.put(JwConsts.KEY_JNET_BILL_NO, respMap.get(JwConsts.KEY_JNET_BILL_NO));
        map.put(JwConsts.KEY_CARD_USE_DATA, respMap.get(JwConsts.KEY_CARD_USE_DATA));
        map.put(JwConsts.KEY_REAL_JPOINT, respMap.get(JwConsts.KEY_REAL_JPOINT));
        return DESEncrypt.getMD5(Help.getStrByMap(map, true) + JwConsts.CONNECT + signKey).toLowerCase();
    }

    /**
     * 查询签名
     * 数字签名（32位的md5加密,加密后转换成小写）
     * 需要加密的串：agent_id=***&bill_id=***&time_stamp=***|||md5Key
     * 
     * @param request
     * @param signKey
     * @return
     */
    public static String genQuerySign(Map<String, String> request, String signKey) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(JwConsts.KEY_AGENT_ID, request.get(JwConsts.KEY_AGENT_ID));
        map.put(JwConsts.KEY_BILL_ID, request.get(JwConsts.KEY_BILL_ID));
        map.put(JwConsts.KEY_TIME_STAMP, request.get(JwConsts.KEY_TIME_STAMP));
        return DESEncrypt.getMD5(Help.getStrByMap(map, true) + JwConsts.CONNECT + signKey).toLowerCase();
    }

    /**
     * 根据返回字符串填充payorder
     * 
     * @param payOrder
     * @param respStr
     * @return
     */
    public static void updatePayOrderByQuery(PayOrder payOrder, String respStr) {
        LOG.info("[jw_in] updatePayOrderByQuery results, respStr={}, payOrder={}", respStr, payOrder, "ds:trace:"
                + payOrder.getAppOrderId());
        updatePayOrderByPay(payOrder, respStr, payOrder.getAppChInfo().getChPayKeyMd5());
    }

    /**
     * 根据同步（ReturnUrl）或异步（NotifyUrl）中的信息，组装PayOrder
     * 
     * @param resource
     * @param params
     */
    public static PayOrder assemblePayOrder(DomainResource resource, Map<String, String> params) {
        LOG.info("[jw_in] assemblePayOrder results, params={}", params, "ds:trace:0");
        String chOrderId = params.get(JwConsts.KEY_BILL_ID);
        PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
        // 如果订单状态已经成功直接返回
        if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode())) {
            return payOrder;
        }
        // 如果订单为卡密校验失败，那么直接返回
        if (Consts.SC.CARD_ERROR.equals(payOrder.getStatusCode())) {
            return payOrder;
        }
        String signInReq = params.get(JwConsts.KEY_SIGN);
        String signExpect = getAsyNotifySign(params, payOrder.getAppChInfo().getChPayKeyMd5());
        if (!signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s", signInReq,
                    signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        if (JwConsts.SUCCESS.equals(params.get(JwConsts.KEY_BILL_STATUS))) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setStatusMsg("骏网：支付成功");
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        } else if (JwConsts.FAIL.equals(params.get(JwConsts.KEY_BILL_STATUS))) {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg("骏网：支付失败");
        } else {
            payOrder.setStatusCode(Consts.SC.UNKNOWN);
        }

        payOrder.setChDealId(params.get(JwConsts.KEY_JNET_BILL_NO));
        return payOrder;
    }

    private static String getAsyNotifySign(Map<String, String> params, String signKey) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(JwConsts.KEY_AGENT_ID, params.get(JwConsts.KEY_AGENT_ID));
        map.put(JwConsts.KEY_BILL_ID, params.get(JwConsts.KEY_BILL_ID));
        map.put(JwConsts.KEY_JNET_BILL_NO, params.get(JwConsts.KEY_JNET_BILL_NO));
        map.put(JwConsts.KEY_BILL_STATUS, params.get(JwConsts.KEY_BILL_STATUS));
        map.put(JwConsts.KEY_REAL_AMT, params.get(JwConsts.KEY_REAL_AMT));
        return DESEncrypt.getMD5(Help.getStrByMap(map, true) + JwConsts.CONNECT + signKey).toLowerCase();
    }

    private static String translateRetCode(String code) {
        if (JwConsts.ReturnResult.RETCODE_CODE_0.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_0;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_1.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_1;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_2.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_2;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_3.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_3;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_4.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_4;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_5.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_5;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_6.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_6;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_7.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_7;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_8.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_8;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_9.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_9;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_10.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_10;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_11.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_11;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_13.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_13;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_14.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_14;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_97.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_97;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_98.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_98;
        } else if (JwConsts.ReturnResult.RETCODE_CODE_99.equalsIgnoreCase(code)) {
            return JwConsts.ReturnResult.RETCODE_MSG_99;
        }
        return "支付平台：未知错误，retCode：" + code;
    }
}
