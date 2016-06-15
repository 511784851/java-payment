/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.vpay;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 盈华讯方工具类。
 * 
 * @author 
 * 
 */
public class VpayHelper {
    private static final Logger LOG = LoggerFactory.getLogger(VpayHelper.class);

    private static final int TWO = 2;

    /**
     * 获取整数格式的金额。
     * 
     * @param amount
     * @return
     */
    public static String generateIntAmountString(BigDecimal amount) {
        String plainAmount = amount.toPlainString();
        Float floatCentAmount = Float.valueOf(plainAmount);
        return String.valueOf(floatCentAmount.intValue());
    }

    /**
     * 生成短信支付请求地址
     * 
     * @param requestMap
     * @return
     */
    public static String generateSmsPayRequestUrl(Map<String, String> requestMap, PayOrder payOrder) {
        StringBuffer sb = new StringBuffer();
        sb.append(requestMap.get(VpayConsts.KEY_SP)).append(requestMap.get(VpayConsts.KEY_OD))
                .append(payOrder.getAppChInfo().getChPayKeyMd5()).append(requestMap.get(VpayConsts.KEY_MZ))
                .append(requestMap.get(VpayConsts.KEY_SPREQ)).append(requestMap.get(VpayConsts.KEY_SPSUC))
                .append(requestMap.get(VpayConsts.KEY_MOB));
        String md5 = MD5Utils.getMD5(sb.toString()).toUpperCase();
        requestMap.put(VpayConsts.KEY_MD5, md5);
        String str = StringHelper.assembleResqStr(requestMap);
        String payUrl = UrlHelper.addQuestionMark(VpayConsts.GATE_URL) + str;
        return payUrl;
    }

    /**
     * 生成PC端短信支付请求地址
     */
    public static String generatePcSmsPayUrl(Map<String, String> requestMap, PayOrder payOrder) {
        StringBuffer sb = new StringBuffer();
        sb.append(requestMap.get(VpayConsts.KEY_SP)).append(requestMap.get(VpayConsts.KEY_OD))
                .append(payOrder.getAppChInfo().getChPayKeyMd5()).append(requestMap.get(VpayConsts.KEY_MZ))
                .append(requestMap.get(VpayConsts.KEY_SPREQ)).append(requestMap.get(VpayConsts.KEY_SPSUC));
        String md5 = MD5Utils.getMD5(sb.toString()).toUpperCase();
        requestMap.put(VpayConsts.KEY_MD5, md5);
        String str = StringHelper.assembleResqStr(requestMap);
        String payUrl = UrlHelper.addQuestionMark(VpayConsts.PC_GATE_URL) + str;
        return payUrl;
    }

    /**
     * 短信支付，根据支付响应修改订单状态
     * 
     * @param response
     * @param payOrder
     */
    public static void updateSmsPayOrderWithPayResponse(String response, PayOrder payOrder) {
        String[] strArray = response.split("\\|");
        if (VpayConsts.PAY_SUCCESS_MSG.equalsIgnoreCase(strArray[0])) {
            String mobile = strArray[3];
            String code = strArray[1];
            String center = strArray[2];
            // String msg = String.format(MSG_FORMAT, mobile, code, center);
            payOrder.setPayUrl(getMsg(mobile, code, center));
            payOrder.setStatusCode(Consts.SC.PENDING);
            payOrder.setStatusMsg("短信内容已生成，等待用户支付或运营商通知");
        } else if (VpayConsts.PAY_FAIL_MSG.equalsIgnoreCase(strArray[0])) {
            payOrder.setStatusCode(Consts.SC.FAIL);
            String statusMsg = "支付失败，未知原因";
            if (strArray.length >= TWO && StringUtils.isNotBlank(strArray[1])) {
                statusMsg = "支付失败，" + strArray[1];
            }
            payOrder.setStatusMsg("盈华讯方：" + statusMsg);
        } else {
            throw new PayException(Consts.SC.UNKNOWN, "支付平台：盈华讯方返回参数出错， response:" + response);
        }
    }

    /**
     * 把结果转换成json 对象
     * 
     * @param mobile
     * @param code
     * @param center
     * @return
     */
    private static String getMsg(String mobile, String code, String center) {
        Map<String, String> result = new HashMap<String, String>();
        result.put(VpayConsts.TEL, mobile);
        result.put(VpayConsts.CONTENT, code);
        result.put(VpayConsts.DES, center);
        return JsonHelper.toJson(result);
    }

    /**
     * 根据短信支付的通知修改订单状态。
     * 
     * @param requestMap
     * @param resource
     * @return
     */

    public static PayOrder assembleSmsPayOrderByNotify(Map<String, String> requestMap, DomainResource resource) {
        String spid = requestMap.get(VpayConsts.KEY_SPID);
        String md5 = requestMap.get(VpayConsts.KEY_MD5);
        String oid = requestMap.get(VpayConsts.KEY_OID);
        String sporder = requestMap.get(VpayConsts.KEY_SPORDER);
        String mz = requestMap.get(VpayConsts.KEY_MZ);
        PayOrder payOrder = Help.getPayOrderByNotify(resource, sporder);
        String sppwd = payOrder.getAppChInfo().getChPayKeyMd5();
        StringBuffer sb = new StringBuffer();
        sb.append(oid).append(sporder).append(spid).append(mz).append(sppwd);
        String expectMD5 = MD5Utils.getMD5(sb.toString());
        if (!expectMD5.equalsIgnoreCase(md5)) {
            LOG.error("[assembleSmsPayOrderByNotify] verify md5 value failed,requestMap:{}", requestMap,
                    TraceHelper.getTrace(payOrder));
            throw new PayException(Consts.SC.SECURE_ERROR, "[updateSmsPayOrderWithNotify] verify md5 value failed.");
        }
        String expectAmount = generateIntAmountString(payOrder.getAmount());
        if (!expectAmount.equalsIgnoreCase(mz)) {
            LOG.error("[assembleSmsPayOrderByNotify] verify money value failed,expect:{},actual:{}", expectAmount, mz,
                    TraceHelper.getTrace(payOrder));
            throw new PayException(Consts.SC.DATA_ERROR, "[updateSmsPayOrderWithNotify] verify money value failed.");
        }
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return payOrder;
        }
        LOG.info("[assembleSmsPayOrderByNotify] success, change status code in order.payOrder:{}", payOrder,
                TraceHelper.getTrace(payOrder));
        payOrder.setStatusCode(Consts.SC.SUCCESS);
        payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        payOrder.setStatusMsg("盈华讯方：支付成功");
        return payOrder;
    }

    /**
     * 电话支付支付请求签名
     * 需要添加签名格式
     * 
     * @param request
     * @param signKey
     * @return
     */
    public static String genSignForTel(Map<String, String> request, String signKey) {
        StringBuilder toBeMd5 = new StringBuilder();
        toBeMd5.append(StringHelper.getNotNullString(request.get(VpayConsts.KEY_SP_ORDERID)))
                .append(StringHelper.getNotNullString(request.get(VpayConsts.KEY_SP_REQ)))
                .append(StringHelper.getNotNullString(request.get(VpayConsts.KEY_NOTIFY_URL)))
                .append(StringHelper.getNotNullString(request.get(VpayConsts.KEY_SP_ID))).append(signKey)
                .append(StringHelper.getNotNullString(request.get(VpayConsts.KEY_SP_VERSION)))
                .append(StringHelper.getNotNullString(request.get(VpayConsts.KEY_MONEY)));
        LOG.debug("[VpayTelHelper.genSign] toBeMd5 is :{}", toBeMd5.toString());
        byte[] bytes = toBeMd5.toString().getBytes();
        return DigestUtils.md5DigestAsHex(bytes).trim().toUpperCase();
    }

    /**
     * 电话支付，根据通知更新订单状态。
     * 
     * @param params
     * @param resource
     * @return
     */
    public static PayOrder assembleTelPayOrderByNotify(Map<String, String> params, DomainResource resource) {
        LOG.info("[assembleTelPayOrderByNotify] params : {}", params);
        String rtcoid = params.get(VpayConsts.V7); // 商户产的唯一订单
        String rtmz = params.get(VpayConsts.V4); // 面值 2-999 整数面值
        String rtoid = params.get(VpayConsts.V6); // 盈华讯方服务器端订单 10位
        PayOrder payOrder = Help.getPayOrderByNotify(resource, rtcoid);
        checkTelNotifyAndReturnSign(params, payOrder);
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return payOrder;
        }
        if (null == rtmz || !rtmz.equals(String.valueOf(payOrder.getAmount().intValue()))) {
            LOG.error(
                    "[assembleTelPayOrderByNotify] amount error,expectAmount:{},responseAmount:{},orderid:{},userid:{}",
                    payOrder.getAmount(), rtmz, rtcoid, payOrder.getUserId(), TraceHelper.getTrace(payOrder));
            throw new PayException(Consts.SC.CHANNEL_ERROR, "支付平台：收到盈华讯方通知，金额不匹配");
        }
        payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        payOrder.setLastUpdateTime(TimeHelper.get(8, new Date()));
        payOrder.setChDealId(rtoid);
        payOrder.setStatusCode(Consts.SC.SUCCESS);
        payOrder.setStatusMsg("盈华讯方：支付成功");
        return payOrder;
    }

    /**
     * 校验盈华讯方电话支付通知和前端返回的签名。
     * 
     * @param params
     * @param payOrder
     * @return
     */
    public static boolean checkTelNotifyAndReturnSign(Map<String, String> params, PayOrder payOrder) {
        String md5InReq = params.get(VpayConsts.V1);
        String trka = params.get(VpayConsts.V2); // V币号码15位
        String rtmi = params.get(VpayConsts.V3); // V币密码6位
        String rtmz = params.get(VpayConsts.V4); // 面值 2-999 整数面值
        String rtoid = params.get(VpayConsts.V6); // 盈华讯方服务器端订单 10位
        String rtcoid = params.get(VpayConsts.V7); // 商户产的唯一订单
        String rtflag = params.get(VpayConsts.V10); // 返回状态. 1为正常发送回来
                                                    // 2为补单发送回来
        // validate sign
        String spId = payOrder.getAppChInfo().getChAccountId();
        String sppwd = payOrder.getAppChInfo().getChPayKeyMd5();
        StringBuilder toBeMd5 = new StringBuilder();
        toBeMd5.append(StringHelper.getNotNullString(trka)).append(rtmi).append(rtoid).append(spId).append(sppwd)
                .append(rtcoid).append(rtflag).append(rtmz);
        String signExpect = DigestUtils
                .md5DigestAsHex(toBeMd5.toString().getBytes(Charset.forName(Consts.CHARSET_UTF8))).trim().toUpperCase();
        // 若签名失败，观察商户号V8参数是否为空。
        if (!signExpect.equals(md5InReq)) {
            LOG.error("[assembleTelPayOrderByNotify] sign error,expectedSign:{},responseSign:{},orderid:{},userid:{}",
                    signExpect, md5InReq, rtcoid, payOrder.getUserId(), TraceHelper.getTrace(payOrder));
            throw new PayException(Consts.SC.CHANNEL_ERROR, "支付平台：收到盈华讯方通知，签名错误");
        }
        return true;
    }
}