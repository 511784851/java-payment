/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.sms;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.SmsOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.UrlHelper;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author administrator
 * 
 */
public class SmsHelper {
    private static final Logger LOG = LoggerFactory.getLogger(SmsHelper.class);
    private static final String X_VERSION = "X.509";
    private static final String SHA_RSA = "SHA1withRSA";
    private static final String VERSION = "3.0";
    private static final String RSA = "RSA";
    private static final String REP_START = "<META NAME=\"MobilePayPlatform\" CONTENT =\"";
    private static final String REP_END = "\">";
    private static final String SMS_START = "您正在使用移动手机短信充值Y币,请使用您的手机发送短信";
    private static final String SMS_MIDDLE = "到";
    private static final String SMS_END = "完成充值。如果不是您本人操作请勿回复";
    private static final String SEP = "\\|";
    private static final String MARKET = "|";
    private static final String CONTENT = "CONTENT";
    private static final String SIGN_ERROR = "签名验证失败";

    private static Map<String, String> SEND_CODE_RESULT_MAP = new HashMap<String, String>();
    private static Map<String, String> CODE_RESULT_MAP = new HashMap<String, String>();
    static {
        // 发送短信的code与reson
        SEND_CODE_RESULT_MAP.put(SmsConsts.SEND_SMS_SUCCESS, SmsConsts.SEND_SMS_SUCCESS_MSG);
        SEND_CODE_RESULT_MAP.put(SmsConsts.SEND_SMS_FAIL_SIGN, SmsConsts.SEND_SMS_FAIL_SIGN_MSG);
        SEND_CODE_RESULT_MAP.put(SmsConsts.SEND_SMS_FAIL_PARAM, SmsConsts.SEND_SMS_FAIL_PARAM_MSG);
        SEND_CODE_RESULT_MAP.put(SmsConsts.SEND_SMS_FAIL_DB, SmsConsts.SEND_SMS_FAIL_DB_MSG);
        SEND_CODE_RESULT_MAP.put(SmsConsts.SEND_SMS_FAIL_INTERFACE_MSG, SmsConsts.SEND_SMS_FAIL_INTERFACE_MSG);
        SEND_CODE_RESULT_MAP.put(SmsConsts.SEND_SMS_FAIL_PROJECT, SmsConsts.SEND_SMS_FAIL_PROJECT_MSG);
        SEND_CODE_RESULT_MAP.put(SmsConsts.SEND_SMS_FAIL_PHONE, SmsConsts.SEND_SMS_FAIL_PHONE_MSG);
    }
    static {
        CODE_RESULT_MAP.put(SmsConsts.SMS_YD_SUCCESS, SmsConsts.SMS_YD_SUCCESS_DES);
        CODE_RESULT_MAP.put(SmsConsts.SMS_YD_FAIL, SmsConsts.SMS_YD_FAIL_DES);
    }
    // 证书缓存
    private static Map<String, Key> keyStoreMap = new HashMap<String, Key>();

    /**
     * 根据联动优势传过来的参数查询sms 订单
     * 
     * @param params
     * @param resource
     * @return
     */
    public static String assembeSmsOrder(Map<String, String> params, DomainResource resource) {
        String goodsInf = params.get(SmsConsts.GOODS_INF);
        String mobileId = params.get(SmsConsts.MOBILE_ID);
        SmsOrder smsOrder = resource.getSmsOrder(mobileId, goodsInf, Consts.SC.PENDING);
        if (smsOrder == null) {
            LOG.info("sms order not found ,the params:{}", params);
            throw new PayException(Consts.SC.DATA_ERROR, "the sms order not found");
        }
        PayOrder payOrder = Help.getPayOrderByNotify(resource, smsOrder.getChOrderId());
        String cerfilePath = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), SmsConsts.CRT_PATH);
        // 签名验证
        validatePaySign(params, cerfilePath);
        String priKeyPath = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), SmsConsts.PRI_KEY_PATH);
        // 组装返回串
        String returnMsg = getReturnMsg(params, priKeyPath, payOrder);
        LOG.info("sms order return:{}", returnMsg);
        return returnMsg;
    }

    /**
     * 根据通知结果更新Order 以及smsorder 状态
     * 
     * @param params
     * @param resource
     * @return
     */
    public static PayOrder assembeSmsOrderByNotify(Map<String, String> params, DomainResource resource) {
        String chOrderId = params.get(SmsConsts.ORDER_ID);
        SmsOrder smsOrder = resource.getSmsOrder(chOrderId);
        if (smsOrder == null) {
            LOG.info("sms order not found ,the params:{}", params);
            throw new PayException(Consts.SC.DATA_ERROR, "the sms order not found");
        }
        PayOrder payOrder = Help.getPayOrderByNotify(resource, smsOrder.getChOrderId());
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return payOrder;
        }
        String cerfilePath = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), SmsConsts.CRT_PATH);
        // 签名验证
        validateNotifySign(params, cerfilePath);
        String retCode = params.get(SmsConsts.RET_CODE);
        if (SmsConsts.SMS_YD_SUCCESS.equalsIgnoreCase(retCode)) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            smsOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
            smsOrder.setStatusCode(Consts.SC.FAIL);
        }
        String des = CODE_RESULT_MAP.get(retCode) == null ? retCode : CODE_RESULT_MAP.get(retCode);
        String goodsId = params.get(SmsConsts.GOODS_ID);
        String merDate = params.get(SmsConsts.MER_DATE);
        String newExt = JsonHelper.putJson(payOrder.getExt(), SmsConsts.GOODS_ID, goodsId);
        newExt = JsonHelper.putJson(newExt, SmsConsts.MER_DATE, merDate);
        payOrder.setExt(newExt);
        payOrder.setStatusMsg(des);
        smsOrder.setStatusMsg(des);
        resource.updateSmsOrder(smsOrder);
        return payOrder;
    }

    /**
     * 组装通知返回结果
     * 
     * @param params
     * @param payOrder
     * @return
     */
    public static String getNotifyReturnMsg(Map<String, String> params, PayOrder payOrder) {
        String priKeyPath = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), SmsConsts.PRI_KEY_PATH);
        String merId = params.get(SmsConsts.MER_ID);
        String goodsId = params.get(SmsConsts.GOODS_ID);
        String orderId = params.get(SmsConsts.ORDER_ID);
        String merDate = params.get(SmsConsts.MER_DATE);
        String retCode = params.get(SmsConsts.RET_CODE);
        StringBuilder sb = new StringBuilder();
        sb.append(merId).append(SmsConsts.CONNECT).append(goodsId).append(SmsConsts.CONNECT).append(orderId)
                .append(SmsConsts.CONNECT).append(merDate).append(SmsConsts.CONNECT).append(retCode)
                .append(SmsConsts.CONNECT).append(merDate).append(SmsConsts.CONNECT).append(VERSION);
        String returnStr = sb.toString();
        LOG.info("sms yd return str:{},appOrderId:{}", returnStr, payOrder.getAppOrderId());
        return returnStr + SmsConsts.CONNECT + getReturnPaySign(returnStr, priKeyPath);
    }

    /**
     * 通知签名验证
     * 
     * @param params
     * @param filePath
     */
    private static void validateNotifySign(Map<String, String> params, String filePath) {
        String merId = params.get(SmsConsts.MER_ID);
        String goodsId = params.get(SmsConsts.GOODS_ID);
        String orderId = params.get(SmsConsts.ORDER_ID);
        String merDate = params.get(SmsConsts.MER_DATE);
        String payDate = params.get(SmsConsts.PAY_DATE);
        String amount = params.get(SmsConsts.AMOUNT);
        String amtType = params.get(SmsConsts.AMT_TYPE);
        String bankType = params.get(SmsConsts.BANK_TYPE);
        String mobileId = params.get(SmsConsts.MOBILE_ID);
        String transType = params.get(SmsConsts.TRANS_TYPE);
        String settleDate = params.get(SmsConsts.SETTLE_DATE);
        String merPriv = params.get(SmsConsts.MER_PRIV);
        String retCode = params.get(SmsConsts.RET_CODE);
        String version = params.get(SmsConsts.VERSION);
        String sign = params.get(SmsConsts.SIGN);
        StringBuilder sb = new StringBuilder();
        sb.append(SmsConsts.MER_ID).append(Consts.EQ).append(merId).append(Consts.AMP).append(SmsConsts.GOODS_ID)
                .append(Consts.EQ).append(goodsId).append(Consts.AMP).append(SmsConsts.ORDER_ID).append(Consts.EQ)
                .append(orderId).append(Consts.AMP).append(SmsConsts.MER_DATE).append(Consts.EQ).append(merDate)
                .append(Consts.AMP).append(SmsConsts.PAY_DATE).append(Consts.EQ).append(payDate).append(Consts.AMP)
                .append(SmsConsts.AMOUNT).append(Consts.EQ).append(amount).append(Consts.AMP)
                .append(SmsConsts.AMT_TYPE).append(Consts.EQ).append(amtType).append(Consts.AMP)
                .append(SmsConsts.BANK_TYPE).append(Consts.EQ).append(bankType).append(Consts.AMP)
                .append(SmsConsts.MOBILE_ID).append(Consts.EQ).append(mobileId).append(Consts.AMP)
                .append(SmsConsts.TRANS_TYPE).append(Consts.EQ).append(transType).append(Consts.AMP)
                .append(SmsConsts.SETTLE_DATE).append(Consts.EQ).append(settleDate).append(Consts.AMP)
                .append(SmsConsts.MER_PRIV).append(Consts.EQ).append(merPriv).append(Consts.AMP)
                .append(SmsConsts.RET_CODE).append(Consts.EQ).append(retCode).append(Consts.AMP)
                .append(SmsConsts.VERSION).append(Consts.EQ).append(version);
        String signStr = sb.toString();
        if (!getPaySign(signStr, sign, filePath)) {
            String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s", sign,
                    signStr);
            throw new PayException(Consts.SC.SECURE_ERROR, msg);
        }
    }

    /**
     * 对 联动优势下单请求进行签名
     * 
     * @param params
     */
    private static void validatePaySign(Map<String, String> params, String filePath) {
        String merId = params.get(SmsConsts.MER_ID);
        String goodsId = params.get(SmsConsts.GOODS_ID);
        String goodsInf = params.get(SmsConsts.GOODS_INF);
        String mobileId = params.get(SmsConsts.MOBILE_ID);
        String amtType = params.get(SmsConsts.AMT_TYPE);
        String bankType = params.get(SmsConsts.BANK_TYPE);
        String version = params.get(SmsConsts.VERSION);
        String signInReq = params.get(SmsConsts.SIGN);
        StringBuilder sb = new StringBuilder();
        sb.append(SmsConsts.MER_ID).append(Consts.EQ).append(merId).append(Consts.AMP).append(SmsConsts.GOODS_ID)
                .append(Consts.EQ).append(goodsId).append(Consts.AMP).append(SmsConsts.GOODS_INF).append(Consts.EQ)
                .append(goodsInf).append(Consts.AMP).append(SmsConsts.MOBILE_ID).append(Consts.EQ).append(mobileId)
                .append(Consts.AMP).append(SmsConsts.AMT_TYPE).append(Consts.EQ).append(amtType).append(Consts.AMP)
                .append(SmsConsts.BANK_TYPE).append(Consts.EQ).append(bankType).append(Consts.AMP)
                .append(SmsConsts.VERSION).append(Consts.EQ).append(version);
        String signStr = sb.toString();
        if (!getPaySign(signStr, signInReq, filePath)) {
            String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s", signInReq,
                    signStr);
            throw new PayException(Consts.SC.SECURE_ERROR, msg);
        }
    }

    /**
     * 对返回串进行签名
     * 
     * @param params
     * @param filePath
     * @param payOrder
     * @return
     */
    private static String getReturnMsg(Map<String, String> params, String filePath, PayOrder payOrder) {
        String merId = params.get(SmsConsts.MER_ID);
        String goodsId = params.get(SmsConsts.GOODS_ID);
        String merDate = TimeHelper.get(7, new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(merId).append(SmsConsts.CONNECT).append(goodsId).append(SmsConsts.CONNECT)
                .append(payOrder.getChOrderId()).append(SmsConsts.CONNECT).append(merDate).append(SmsConsts.CONNECT)
                .append(StringHelper.getAmount(payOrder.getAmount())).append(SmsConsts.CONNECT)
                .append(SmsConsts.NOTIFY_URL_ADDR).append(SmsConsts.CONNECT).append(merDate).append(SmsConsts.CONNECT)
                .append(merDate).append(SmsConsts.CONNECT).append(SmsConsts.SMS_YD_SUCCESS).append(SmsConsts.CONNECT)
                .append(SmsConsts.SMS_YD_SUCCESS).append(SmsConsts.CONNECT).append(VERSION);
        String returnStr = sb.toString();
        LOG.info("sms yd return str:{},apporderid:{}", returnStr, payOrder.getAppOrderId());
        return returnStr + SmsConsts.CONNECT + getReturnPaySign(returnStr, filePath);
    }

    /**
     * 获取证书文件字节
     * 
     * @param filePath
     * @return
     */
    private static byte[] readFile(String filePath) {
        FileInputStream fis = null;
        byte[] kb = (byte[]) null;
        try {
            File f = new File(filePath);
            kb = new byte[(int) f.length()];
            fis = new FileInputStream(f);
            fis.read(kb);
        } catch (Exception e) {
            LOG.error("file not exist ", e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception localException1) {
                } finally {
                    fis = null;
                }
            }
        }
        return kb;
    }

    /**
     * 获取证书文件
     * 
     * @param returnStr
     * @param filePath
     * @return
     */
    private static String getReturnPaySign(String returnStr, String filePath) {
        PrivateKey pk = null;
        if (keyStoreMap.containsKey(filePath)) {
            pk = (PrivateKey) keyStoreMap.get(filePath);
        } else {
            byte[] kb = readFile(filePath);
            PKCS8EncodedKeySpec peks = null;
            KeyFactory kf = null;
            try {
                peks = new PKCS8EncodedKeySpec(kb);
                kf = KeyFactory.getInstance(RSA);
                pk = kf.generatePrivate(peks);
                keyStoreMap.put(filePath, pk);
                LOG.info("generate privatekey and cache it,filepath:{}", filePath);
            } catch (Exception e) {
                LOG.error("invalid primary key format", e);
                throw new PayException(Consts.SC.CHANNEL_ERROR, SIGN_ERROR);
            }
        }
        Signature sig = null;
        byte[] sb = (byte[]) null;
        try {
            sig = Signature.getInstance(SHA_RSA);
            sig.initSign(pk);
            sig.update(returnStr.getBytes(Consts.CHARSET_GB_2312));
            sb = sig.sign();
        } catch (Exception e) {
            LOG.error("sign procedure failed", e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        String b64Str = null;
        try {
            BASE64Encoder base64 = new BASE64Encoder();
            b64Str = base64.encode(sb);
        } catch (Exception e) {
            LOG.error("base64 generation failed", e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        try {
            BufferedReader br = new BufferedReader(new StringReader(b64Str));
            String tmpStr = "";
            String tmpStr1 = "";
            while ((tmpStr = br.readLine()) != null) {
                tmpStr1 = tmpStr1 + tmpStr;
            }
            b64Str = tmpStr1;
            return b64Str;
        } catch (Exception e) {
            LOG.error("new base64 generation failed", e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
    }

    /**
     * 获取证书文件
     * 
     * @param signExpect
     * @param signInReq
     * @param filePath
     * @return
     */
    private static boolean getPaySign(String signExpect, String signInReq, String filePath) {
        PublicKey publicKey = null;
        if (keyStoreMap.containsKey(filePath)) {
            publicKey = (PublicKey) keyStoreMap.get(filePath);
        } else {
            byte[] cb = readFile(filePath);
            ByteArrayInputStream bais = new ByteArrayInputStream(cb);
            CertificateFactory cf = null;
            X509Certificate cert = null;
            try {
                cf = CertificateFactory.getInstance(X_VERSION);
                cert = (X509Certificate) cf.generateCertificate(bais);
                publicKey = cert.getPublicKey();
                keyStoreMap.put(filePath, publicKey);
                LOG.info("generate public key and cache it,filepath:{}", filePath);
            } catch (Exception e) {
                LOG.error("cer error ", e);
                throw new PayException(Consts.SC.CHANNEL_INFO_ERROR, e.getMessage());
            }
        }
        try {
            BASE64Decoder base64 = new BASE64Decoder();
            byte[] signed = base64.decodeBuffer(signInReq);
            Signature sig = Signature.getInstance(SHA_RSA);
            sig.initVerify(publicKey);
            sig.update(signExpect.getBytes(Consts.CHARSET_UTF8));
            return sig.verify(signed);
        } catch (Exception e) {

            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
    }

    /**
     * 组装返回结果
     * <META NAME="MobilePayPlatform" CONTENT =
     * "9996|100|576216|20090402|1000|http://www.notify.com|||0000|下订单成功|3.0|wlItyXiYPPm/2QcSzf8wrl/XxzkF8m9aN14qlBBvhB30pFJE7zR4cMRO2Ods"
     * >
     * 
     * @param returnValue
     * @return
     */
    public static String genResp(String returnValue) {
        String result = REP_START + returnValue + REP_END;
        return result;
    }

    /**
     * 发送短信
     * 
     * @param phone
     * @param password
     * @param subuser
     * @param smscode
     * @param key
     * @param smsReceiveAddress
     * @return
     */
    public static String send(String phone, String password, String subuser, String smscode, String key,
            String smsReceiveAddress) {
        LOG.info("sms phone:{},password:{},subuser:{},smscode:{}smsReceiveAddress:{}", phone, password, subuser,
                smscode, smsReceiveAddress);
        String time = TimeHelper.get(8, new Date());
        String mac = MD5Utils.getMD5(password + phone + subuser + time + key).toLowerCase();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(SmsConsts.PHONE, phone);
        paramMap.put(SmsConsts.USERID, password);
        paramMap.put(SmsConsts.SUBUSER, subuser);
        paramMap.put(SmsConsts.TIME, time);
        paramMap.put(SmsConsts.MAC, mac);
        paramMap.put(SmsConsts.CONTENT, SMS_START + smscode + SMS_MIDDLE + smsReceiveAddress + SMS_END);
        String url = UrlHelper.addQuestionMark(SmsConsts.SMSURL) + StringHelper.assembleResqStr(paramMap);
        String respStr = HttpClientHelper.sendRequest(url, SmsConsts.CHARSET_UTF8).trim();
        LOG.info("sms send phone:{},smscode:{},result:{}", phone, smscode, respStr);
        return respStr;
    }

    /**
     * 根据发送短信的结果组装payorder
     * 
     * @param returnMsg
     * @param order
     * @param phone
     * @param smsCode
     * @param resource
     */
    public static void assembePayOrderByPay(String returnMsg, PayOrder order, String phone, String smsCode,
            DomainResource resource) {
        LOG.info("sms assembePayOrderByPay phone:{},smsCode:{},returnMsg:{},appOrderid;{}", phone, smsCode, returnMsg,
                order.getAppOrderId());
        if (SmsConsts.SEND_SMS_SUCCESS.equals(returnMsg)) {
            order.setStatusCode(Consts.SC.PENDING);
            order.appendMsg("smsCode created successfully,[send sms:]"
                    + SEND_CODE_RESULT_MAP.get(SmsConsts.SEND_SMS_SUCCESS));
            SmsOrder smsOrder = SmsHelper.assembleSmsOrder(order, phone, smsCode);
            resource.createSmsOrder(smsOrder);
        } else {
            order.setStatusCode(Consts.SC.FAIL);
            String des = SEND_CODE_RESULT_MAP.get(returnMsg) == null ? returnMsg : SEND_CODE_RESULT_MAP.get(returnMsg);
            order.setStatusMsg(des);
            LOG.info("[SmsYdAdapter.sendsms] fail,respStr:{}", des);
        }
    }

    public static boolean validPhone(String reg, String phone) {
        if (StringUtils.isEmpty(reg) || StringUtils.isEmpty(phone)) {
            return false;
        }
        return phone.matches(reg);
    }

    public static SmsOrder assembleSmsOrder(PayOrder order, String phone, String smsCode) {
        SmsOrder smsOrder = new SmsOrder();
        smsOrder.setChOrderId(order.getChOrderId());
        smsOrder.setPhone(phone);
        smsOrder.setValidCode(smsCode);
        smsOrder.setStatusCode(Consts.SC.PENDING);
        smsOrder.setStatusMsg("[created successfully]");
        smsOrder.setChOrderId(order.getChOrderId());
        smsOrder.setChOrderId(order.getChOrderId());
        return smsOrder;
    }

    /**
     * 获取查询签名
     * merId=9996&goodsId=100&orderId=576216&merDate=20090402&mobileId=
     * 13426399070&version=3.0
     * 
     * @param order
     * @param querMap
     * @return
     */
    public static String getQuerySign(PayOrder order, Map<String, String> querMap) {
        String signStr = Help.getStrByMap(querMap, true);
        String priKeyPath = JsonHelper.fromJson(order.getAppChInfo().getAdditionalInfo(), SmsConsts.PRI_KEY_PATH);
        return getReturnPaySign(signStr, priKeyPath);
    }

    /**
     * 根据查询结果更新 payorder
     * 返回数据格式 <META NAME="MobilePayPlatform" CONTENT =
     * "9996|100|576216|20090402|20090402|1000|02|3|13426399070||0|1|20090403|1||0000|3.0|wlItyXiYPPm/2QcSzf8wrl/XxzkF8m9aN14qlBBvhB30pFJE7zR4cMRO2Ods"
     * >
     */
    public static void updateByQuerySmsYd(PayOrder order, String resp, DomainResource resource) {
        int index = resp.indexOf(CONTENT);
        String source = resp.substring(index + 9, resp.length() - 2);
        int last = source.lastIndexOf(MARKET);
        String singStr = source.substring(0, last);
        String expectSign = source.substring(last + 1, source.length());
        String cerfilePath = JsonHelper.fromJson(order.getAppChInfo().getAdditionalInfo(), SmsConsts.CRT_PATH);
        try {
            boolean singFlag = getPaySign(singStr, expectSign, cerfilePath);
            if (!singFlag) {
                String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s",
                        singStr, expectSign);
                throw new PayException(Consts.SC.SECURE_ERROR, msg);
            }
        } catch (Exception e) {
            if (e instanceof PayException) {
                PayException ee = (PayException) e;
                if (Consts.SC.CHANNEL_ERROR.equalsIgnoreCase(ee.getStatusCode())) {
                    updateFailed(order, resource, expectSign);
                    return;
                }
            }
        }
        String[] queryResult = source.split(SEP);
        if (queryResult.length != 18) {
            return;
        }
        String transState = queryResult[11];
        transStatus(order, transState);
        order.setChDealTime(TimeHelper.get(8, new Date()));
        order.setChDealId(queryResult[2]);
        // String msg = CODE_RESULT_MAP.get(queryResult[15]) == null ?
        // queryResult[15] : CODE_RESULT_MAP
        // .get(queryResult[15]);
        // order.setStatusMsg(msg);
        updateSmsOrderByQuery(order, resource);
    }

    /**
     * @param order
     * @param resource
     * @param resp
     */
    private static void updateFailed(PayOrder order, DomainResource resource, String resp) {

        order.setStatusMsg(StringUtils.left(resp, 1000));

        SmsOrder smsOrder = resource.getSmsOrder(order.getChDealId());
        if (smsOrder != null) {
            smsOrder.setStatusMsg(order.getStatusMsg());
            smsOrder.setStatusCode(order.getSmsCode());
            resource.updateSmsOrder(smsOrder);
        }
    }

    /**
     * 更新smsorder
     * 
     * @param order
     * @param resource
     */
    private static void updateSmsOrderByQuery(PayOrder order, DomainResource resource) {
        SmsOrder smsOrder = resource.getSmsOrder(order.getChOrderId());
        smsOrder.setStatusCode(order.getStatusCode());
        smsOrder.setStatusMsg(order.getStatusMsg());
        resource.updateSmsOrder(smsOrder);
    }

    /**
     * 状态转换
     * 0：初始状态
     * 1：交易成功
     * 2：交易失败
     * 
     * @param order
     * @param transState
     */
    private static void transStatus(PayOrder order, String transState) {
        if (SmsConsts.QUERY_SUCCESS.equalsIgnoreCase(transState)) {
            order.setStatusCode(Consts.SC.SUCCESS);
            order.setStatusMsg("联动：支付成功");
        } else if (SmsConsts.QUERY_FAIL.equalsIgnoreCase(transState)) {
            order.setStatusCode(Consts.SC.FAIL);
            order.setStatusMsg("联动：支付失败");
        } else {
            order.setStatusCode(Consts.SC.PENDING);
            order.setStatusMsg("等待支付");
        }
    }
}
