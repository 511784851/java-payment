/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.yeepay;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.channel.yeepay.util.DigestUtil;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.OrderIdHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;

/**
 * @author 
 * 
 */
public class YeePayHelper {
    private static final Logger LOG = LoggerFactory.getLogger(YeePayHelper.class);

    /**
     * 根据卡密的异步通知修改订单状态。
     * 
     * @param resource
     * @param params
     * @return
     */
    public static PayOrder assemblePayOrderByNotify(DomainResource resource, Map<String, String> params) {
        String chOrderId = params.get(YeePayConsts.P2_ORDER);
        PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return payOrder;
        }
        String signInReq = params.get(YeePayConsts.HMAC);
        String[] paramArray = new String[] { StringUtils.defaultIfBlank(params.get(YeePayConsts.R0_CMD), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.R1_CODE), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.P1_MERID), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.P2_ORDER), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.P3_AMT), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.P4_FRPID), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.P5_CARDNO), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.P6_CONFIRMAMOUNT), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.P7_REALAMOUNT), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.P8_CARDSTATUS), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.P9_MP), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.PB_BALANCEAMT), ""),
                StringUtils.defaultIfBlank(params.get(YeePayConsts.PC_BALANCEACT), "") };
        String signExpect = DigestUtil.hmacSign(paramArray, payOrder.getAppChInfo().getChPayKeyMd5());
        if (!signExpect.equals(signInReq)) {
            LOG.error(
                    "[assemblePayOrderByNotify] sign: unsupported type or unmatched! orderId:{},signInReq:{}, signExpect:{}",
                    payOrder.getChOrderId(), signInReq, signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, "unmatched sign.");
        }
        String p8_cardstatus = params.get(YeePayConsts.P8_CARDSTATUS);
        String statusMsg = translateCardPayNotifyStatus(p8_cardstatus);
        // 添加卡密错误的订单状态，当p8_cardstatus为7或1004时，认为卡密有误，具体的信息是“卡号卡密或卡面额不符合规则”和“密码错误或充值卡无效”。
        if (YeePayConsts.CHARGECARDDIRECT.equalsIgnoreCase(params.get(YeePayConsts.R0_CMD))
                && (YeePayConsts.CardPayNotifyStatus.CODE_1004.equalsIgnoreCase(p8_cardstatus) || YeePayConsts.CardPayNotifyStatus.CODE_7
                        .equalsIgnoreCase(p8_cardstatus))
                && YeePayConsts.NOTIFY_R1_CODE_FAIL.equalsIgnoreCase(params.get(YeePayConsts.R1_CODE))) {
            payOrder.setStatusCode(Consts.SC.CARD_ERROR);
            payOrder.setStatusMsg("易宝：" + statusMsg);
            LOG.error("[assemblePayOrderByNotify] get a wrong cardnum and cardpass,orderId:{}", chOrderId,
                    TraceHelper.getTrace(payOrder));
            return payOrder;
        }

        if (!params.get(YeePayConsts.R0_CMD).equalsIgnoreCase(YeePayConsts.CHARGECARDDIRECT)
                || !YeePayConsts.CardPayNotifyStatus.CODE_0.equalsIgnoreCase(p8_cardstatus)
                || !params.get(YeePayConsts.R1_CODE).equalsIgnoreCase("1")) {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg("易宝：" + statusMsg);
            LOG.error(
                    "[assemblePayOrderByNotify] unexpected channel response,orderId:{},r0_cmd:{},r1_code:{},statusmsg:{}",
                    chOrderId, params.get(YeePayConsts.R0_CMD), params.get(YeePayConsts.R1_CODE), p8_cardstatus,
                    TraceHelper.getTrace(payOrder));
            return payOrder;
        }
        if (payOrder.getAmount().compareTo(BigDecimal.valueOf(Double.valueOf(params.get(YeePayConsts.P3_AMT)))) != 0) {
            payOrder.setStatusMsg("支付平台：金额不匹配，易宝金额：" + params.get(YeePayConsts.P3_AMT) + "，平台金额："
                    + payOrder.getAmount().toPlainString());
            LOG.error("[assemblePayOrderByNotify] unmatched amount.orderId:{},statusMsg:{}", chOrderId,
                    payOrder.getStatusMsg(), TraceHelper.getTrace(payOrder));
            return payOrder;
        }
        payOrder.setStatusCode(Consts.SC.SUCCESS);
        payOrder.setStatusMsg(YeePayConsts.PAY_SUCCESS_MSG);
        payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        return payOrder;
    }

    /**
     * 根据卡密支付响应修改订单状态。
     * 
     * @param payOrder
     * @param respStr
     */
    public static void upatePayOrderByCardPayResponse(PayOrder payOrder, String respStr) {
        Map<String, String> paramsMap = rowsToMap(respStr);
        LOG.info("[upatePayOrderByCardPayResponse] paramsMap:{}", paramsMap, TraceHelper.getTrace(payOrder));
        String r1_code = paramsMap.get(YeePayConsts.R1_CODE);
        if (YeePayConsts.CardPayRespCode.CODE_1.equalsIgnoreCase(r1_code)) {
            payOrder.setStatusCode(Consts.SC.PENDING);
            payOrder.setStatusMsg("易宝：" + translateCardPayRespCode(r1_code));
            LOG.info("[upatePayOrderByCardPayResponse] channel responses success,orderid:{},r1_Code:{},statusMsg:{}",
                    OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId()), r1_code,
                    payOrder.getStatusMsg());
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg("易宝：" + translateCardPayRespCode(r1_code));
            LOG.error("[upatePayOrderByCardPayResponse] payorder failed,orderid:{},r1_Code:{},statusMsg:{}",
                    OrderIdHelper.genChOrderId(payOrder.getAppId(), payOrder.getAppOrderId()), r1_code,
                    payOrder.getStatusMsg());
        }
    }

    /**
     * 支付签名
     * 
     * @param reqParams
     * @return
     */
    public static String getPaySign(Map<String, String> reqParams, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(reqParams.get(YeePayConsts.P0_CMD));
        sb.append(reqParams.get(YeePayConsts.P1_MERID));
        sb.append(reqParams.get(YeePayConsts.P2_ORDER));
        sb.append(reqParams.get(YeePayConsts.P3_AMT));
        sb.append(reqParams.get(YeePayConsts.P4_CUR));
        sb.append(reqParams.get(YeePayConsts.P5_PID));
        sb.append(reqParams.get(YeePayConsts.P6_PCAT));
        sb.append(reqParams.get(YeePayConsts.P7_PDESC));
        sb.append(reqParams.get(YeePayConsts.P8_URL));
        sb.append(reqParams.get(YeePayConsts.P9_SAF));
        sb.append(reqParams.get(YeePayConsts.PA_MP));
        sb.append(reqParams.get(YeePayConsts.PD_FRPID));
        sb.append(reqParams.get(YeePayConsts.PR_NEEDRESPONSE));
        return DigestUtil.hmacSign(sb.toString(), key);
    }

    /**
     * 查询签名
     * 
     * @param reqParams
     * @param key
     * @return
     */
    public static String getQuerySign(Map<String, String> reqParams, String key) {
        StringBuilder encodeTarget = new StringBuilder();
        encodeTarget.append(reqParams.get(YeePayConsts.P0_CMD));
        encodeTarget.append(reqParams.get(YeePayConsts.P1_MERID));
        encodeTarget.append(reqParams.get(YeePayConsts.P2_ORDER));
        String hmac = DigestUtil.hmacSign(encodeTarget.toString(), key);
        return hmac;
    }

    /**
     * Translate a string like key_1=value_1\r\nkey_2=value_2\r\n to
     * {"key_1":"value_1","key_2":"value2"}
     * 
     * @param src
     * @return
     */
    public static Map<String, String> rowsToMap(String src) {
        String[] kvs = StringUtils.splitByWholeSeparator(src, "\n");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < kvs.length; i++) {
            String kv[] = kvs[i].split("=");
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            } else if (kv.length == 1) {
                if (StringUtils.isNotEmpty(kv[0].trim()))
                    map.put(kv[0].trim(), "");
            } else {
                ;
            }
        }
        return map;
    }

    /**
     * 通用的使用查询结果更新订单
     * 
     * @param resp
     * @param payOrder
     * @return
     */

    public static PayOrder updatePayOrderByQuery(String resp, PayOrder payOrder, boolean isCard) {
        LOG.info("[updatePayOrderByQuery] appid:{},apporderid:{}, queryResult:{}", payOrder.getAppId(),
                payOrder.getAppOrderId(), resp, TraceHelper.getTrace(payOrder));
        if (isCard) {
            return updatePayOrderByCardQuery(resp, payOrder);
        } else {
            return updatePayOrderByGateQuery(resp, payOrder);
        }

    }

    /**
     * 非银行卡类下单查询
     * 
     * @param respParams
     * @param payOrder
     * @return
     */
    private static PayOrder updatePayOrderByCardQuery(String resp, PayOrder payOrder) {
        Map<String, String> respParams = Help.getMapByRespStr(resp);
        String signInReq = respParams.remove(YeePayConsts.HMAC);
        String signExpect = getCardQueryMd5(respParams, payOrder.getAppChInfo().getChPayKeyMd5());
        if (StringUtils.isBlank(signInReq) || !signInReq.equalsIgnoreCase(signExpect)) {
            LOG.error("[updatePayOrderByQuery] not expected sign,signInReq:{},signExpect:{}", signInReq, signExpect,
                    TraceHelper.getTrace(payOrder));
            return payOrder;
        }
        String r1Code = respParams.get(YeePayConsts.R1_CODE);
        String p2Order = respParams.get(YeePayConsts.P2_ORDER);
        String p8CardStatus = respParams.get(YeePayConsts.P8_CARDSTATUS);
        if (YeePayConsts.GeneralQueryRespCode.CODE_1.equalsIgnoreCase(r1Code)) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setStatusMsg("易宝：" + YeePayConsts.PAY_SUCCESS_MSG);
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        } else if (YeePayConsts.GeneralQueryRespCode.CODE_3.equalsIgnoreCase(r1Code)
                && payOrder.getChOrderId().equalsIgnoreCase(p2Order)) {
            payOrder.setStatusCode(Consts.SC.PENDING);
            payOrder.setStatusMsg("易宝：" + YeePayConsts.GeneralQueryRespCode.MSG_3);
        } else {
            String statusMsg = translateCardPayNotifyStatus(p8CardStatus);
            payOrder.setStatusMsg("易宝：" + statusMsg);
        }

        return payOrder;
    }

    /**
     * 非银行卡查询签名
     * 
     * @param respParams
     * @param key
     * @return
     */
    private static String getCardQueryMd5(Map<String, String> respParams, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(respParams.get(YeePayConsts.R0_CMD)).append(respParams.get(YeePayConsts.R1_CODE))
                .append(respParams.get(YeePayConsts.P1_MERID)).append(respParams.get(YeePayConsts.P2_ORDER))
                .append(respParams.get(YeePayConsts.P3_AMT)).append(respParams.get(YeePayConsts.P4_FRPID))
                .append(respParams.get(YeePayConsts.P5_CARDNO)).append(respParams.get(YeePayConsts.P6_CONFIRMAMOUNT))
                .append(respParams.get(YeePayConsts.P7_REALAMOUNT)).append(respParams.get(YeePayConsts.P8_CARDSTATUS))
                .append(respParams.get(YeePayConsts.P9_MP)).append(respParams.get(YeePayConsts.PB_BALANCEAMT))
                .append(respParams.get(YeePayConsts.PC_BALANCEACT));
        LOG.info("CardQueryMd5:" + sb.toString());
        return DigestUtil.hmacSign(sb.toString(), key);
    }

    /**
     * 银行卡类下单查询
     * 
     * @param respParams
     * @param payOrder
     * @return
     */
    private static PayOrder updatePayOrderByGateQuery(String resp, PayOrder payOrder) {
        Map<String, String> respParams = rowsToMap(resp);
        String signInReq = respParams.remove(YeePayConsts.HMAC);
        String signExpect = DigestUtil.hamcSign(respParams, payOrder.getAppChInfo().getChPayKeyMd5());
        if (StringUtils.isBlank(signInReq) || !signInReq.equalsIgnoreCase(signExpect)) {
            LOG.error("[updatePayOrderByQuery] not expected sign,signInReq:{},signExpect:{}", signInReq, signExpect,
                    TraceHelper.getTrace(payOrder));
            return payOrder;
        }
        String r0_cmd = respParams.get(YeePayConsts.R0_CMD);
        String r1_code = respParams.get(YeePayConsts.R1_CODE);
        String rb_payStatus = respParams.get(YeePayConsts.RB_PAYSTATUS);
        if (YeePayConsts.QUERYORDERDETAIL.equalsIgnoreCase(r0_cmd)
                && YeePayConsts.GeneralQueryRespCode.CODE_1.equalsIgnoreCase(r1_code)
                && YeePayConsts.GeneralQueryRespStatus.CODE_SUCCESS.equalsIgnoreCase(rb_payStatus)
                && payOrder.getChOrderId().equalsIgnoreCase(respParams.get(YeePayConsts.R6_ORDER))) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setStatusMsg("易宝：" + YeePayConsts.PAY_SUCCESS_MSG);
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
            LOG.info("[updatePayOrderByQuery] query a success order,orderId:{},r1_code:{},rb_payStatus:{}",
                    payOrder.getChOrderId(), r1_code, rb_payStatus, TraceHelper.getTrace(payOrder));
        } else {
            String statusMsg = translateGeneralQueryRespCode(r1_code) + "，"
                    + translateGeneralQueryRespStatus(rb_payStatus);
            payOrder.setStatusMsg("易宝：" + statusMsg);
            LOG.info("[updatePayOrderByQuery] query a unsuccess order,orderId:{},r1_code:{},rb_payStatus:{}",
                    payOrder.getChOrderId(), r1_code, rb_payStatus, TraceHelper.getTrace(payOrder));
        }
        return payOrder;
    }

    /**
     * 退款签名
     * 
     * @param reqParams
     * @param key
     * @return
     */
    public static String getRefundSign(Map<String, String> reqParams, String key) {
        StringBuilder target = new StringBuilder();
        target.append(reqParams.get(YeePayConsts.P0_CMD));
        target.append(reqParams.get(YeePayConsts.P1_MERID));
        target.append(reqParams.get(YeePayConsts.PB_TRXID));
        target.append(reqParams.get(YeePayConsts.P3_AMT));
        target.append(reqParams.get(YeePayConsts.P4_CUR));
        target.append(reqParams.get(YeePayConsts.P5_PID));
        String hmac = DigestUtil.hmacSign(target.toString(), YeePayConsts.YEEPAY_MERCHAND_PW);
        return hmac;
    }

    /**
     * 根据退款结果更新订单
     * 
     * @param resp
     * @param payOrder
     * @return
     */
    public static PayOrder updatePayOrderByRefund(String resp, PayOrder payOrder) {
        Map<String, String> respParams = rowsToMap(resp);
        String r1_code = respParams.get(YeePayConsts.R1_CODE);
        if (!Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            throw new PayException(Consts.SC.REQ_ERROR, "订单未成功支付，不允许退款", payOrder);
        }
        if (YeePayConsts.GeneralRefundRespCode.CODE_1.equalsIgnoreCase(r1_code)) {
            payOrder.setStatusCode(Consts.SC.REFUND_SUCCESS);
        }
        String statusMsg = translateGeneralRefundRespCode(r1_code);
        payOrder.setStatusMsg("易宝：" + statusMsg);
        LOG.info("[updatePayOrderByRefund] orderId:{},statusCode:{},statusMsg:{}", payOrder.getChOrderId(),
                payOrder.getStatusCode(), payOrder.getStatusMsg());
        return payOrder;
    }

    /**
     * Format the cardTotalAmount String,set values with specified scale.
     * 
     * @param payOrder
     * @return
     */
    public static String getCardTotalAmount(PayOrder payOrder) {
        String cardTotalAmount = payOrder.getCardTotalAmount();
        if (StringUtils.isBlank(cardTotalAmount)) {
            return getFormatAmount(payOrder.getAmount());
        }
        String[] originalStringArray = cardTotalAmount.split("~");
        String[] targetStringArray = new String[originalStringArray.length];
        for (int i = 0; i < originalStringArray.length; i++) {
            targetStringArray[i] = getFormatAmount(new BigDecimal(originalStringArray[i]));
        }
        return Arrays.toString(targetStringArray).replaceAll("[\\[\\]\\s]", "");
    }

    /**
     * Reference to Yeepay document,some parameters should be in specified
     * scale.
     * 
     * @param amount
     * @return
     */
    private static String getFormatAmount(BigDecimal amount) {
        return amount.setScale(2).toPlainString();
    }

    /**
     * Translate the Yeepay response code to a description.
     * 
     * @param respCode
     * @return
     */
    private static String translateCardPayRespCode(String respCode) {
        if (YeePayConsts.CardPayRespCode.CODE_1.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_1;
        } else if (YeePayConsts.CardPayRespCode.CODE_n1.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_n1;
        } else if (YeePayConsts.CardPayRespCode.CODE_2.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_2;
        } else if (YeePayConsts.CardPayRespCode.CODE_5.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_5;
        } else if (YeePayConsts.CardPayRespCode.CODE_11.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_11;
        } else if (YeePayConsts.CardPayRespCode.CODE_21.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_21;
        } else if (YeePayConsts.CardPayRespCode.CODE_66.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_66;
        } else if (YeePayConsts.CardPayRespCode.CODE_95.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_95;
        } else if (YeePayConsts.CardPayRespCode.CODE_112.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_112;
        } else if (YeePayConsts.CardPayRespCode.CODE_8001.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_8001;
        } else if (YeePayConsts.CardPayRespCode.CODE_8002.equalsIgnoreCase(respCode)) {
            return YeePayConsts.CardPayRespCode.MSG_8002;
        } else {
            return YeePayConsts.CardPayRespCode.MSG_UNDEFINED + "，r1_code：" + respCode;
        }
    }

    /**
     * Translate general Yeepay query response code.
     * Reference the parameter named r1_Code
     * 
     * @param code
     * @return
     */

    private static String translateGeneralQueryRespCode(String code) {
        if (YeePayConsts.GeneralQueryRespCode.CODE_1.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralQueryRespCode.MSG_1;
        } else if (YeePayConsts.GeneralQueryRespCode.CODE_50.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralQueryRespCode.MSG_50;
        } else {
            return YeePayConsts.GeneralQueryRespCode.MSG_UNDEFINED + "，r1_code：" + code;
        }
    }

    /**
     * Translate general Yeepay query response status.
     * Reference the parameter named rb_PayStatus
     * 
     * @param code
     * @return
     */
    private static String translateGeneralQueryRespStatus(String code) {
        if (YeePayConsts.GeneralQueryRespStatus.CODE_INIT.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralQueryRespStatus.MSG_INIT;
        } else if (YeePayConsts.GeneralQueryRespStatus.CODE_SUCCESS.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralQueryRespStatus.MSG_SUCCESS;
        } else if (YeePayConsts.GeneralQueryRespStatus.CODE_CANCELED.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralQueryRespStatus.MSG_CANCELED;
        } else {
            return YeePayConsts.GeneralQueryRespStatus.MSG_UNDEFINED + "，rb_PayStatus：" + code;
        }
    }

    /**
     * Translate general YeePay refund response code.
     * Reference the parameter named r1_Code
     * 
     * @param code
     * @return
     */
    private static String translateGeneralRefundRespCode(String code) {
        if (YeePayConsts.GeneralRefundRespCode.CODE_1.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralRefundRespCode.MSG_1;
        } else if (YeePayConsts.GeneralRefundRespCode.CODE_2.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralRefundRespCode.MSG_2;
        } else if (YeePayConsts.GeneralRefundRespCode.CODE_7.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralRefundRespCode.MSG_7;
        } else if (YeePayConsts.GeneralRefundRespCode.CODE_10.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralRefundRespCode.MSG_10;
        } else if (YeePayConsts.GeneralRefundRespCode.CODE_18.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralRefundRespCode.MSG_18;
        } else if (YeePayConsts.GeneralRefundRespCode.CODE_50.equalsIgnoreCase(code)) {
            return YeePayConsts.GeneralRefundRespCode.MSG_50;
        } else {
            return YeePayConsts.GeneralRefundRespCode.MSG_UNDEFINED + "，r1_code：" + code;
        }
    }

    /**
     * Translate general YeePay card pay notify status.
     * Reference the parameter named p8_cardStatus
     * 
     * @param code
     * @return
     */
    private static String translateCardPayNotifyStatus(String code) {
        if (YeePayConsts.CardPayNotifyStatus.CODE_0.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_0;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_1.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_1;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_7.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_7;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_1002.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_1002;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_1003.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_1003;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_1004.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_1004;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_1006.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_1006;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_1007.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_1007;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_1008.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_1008;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_1010.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_1010;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_10000.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_10000;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_2005.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_2005;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_2006.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_2006;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_2007.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_2007;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_2008.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_2008;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_2009.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_2009;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_2010.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_2010;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_2011.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_2011;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_2012.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_2012;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_2014.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_2014;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_3001.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_3001;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_3002.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_3002;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_3003.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_3003;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_3004.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_3004;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_3005.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_3005;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_3006.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_3006;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_3007.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_3007;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_3101.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_3101;
        } else if (YeePayConsts.CardPayNotifyStatus.CODE_3102.equalsIgnoreCase(code)) {
            return YeePayConsts.CardPayNotifyStatus.MSG_3102;
        } else {
            return YeePayConsts.CardPayNotifyStatus.MSG_UNDEFINED + "，p8_cardStatus：" + code;
        }
    }
}
