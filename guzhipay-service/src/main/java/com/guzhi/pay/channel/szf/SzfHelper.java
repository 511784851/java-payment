/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.szf;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.channel.szf.util.DES;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;

/**
 * 神州付 帮助类
 * 
 * @author Administrator
 * 
 */
public class SzfHelper {
    private static final DecimalFormat decimalFormat = new DecimalFormat("0");
    private static final Logger LOG = LoggerFactory.getLogger(SzfHelper.class);

    /**
     * 组装充值卡加密信息
     * DES 加密并做 BASE64 编码后的数据
     * DES 加密数据格式：充值卡面额[单位:元]@充值卡序列号 @充值卡密码
     * 
     * @param payOrder
     * @return
     */
    public static String assembleCardInfo(PayOrder payOrder) {
        String desString = decimalFormat.format(Double.parseDouble(payOrder.getCardTotalAmount())) + SzfConsts.CONNECT
                + payOrder.getCardNum() + SzfConsts.CONNECT
                + DESEncrypt.decryptByAES(payOrder.getAppInfo().getPasswdKey(), payOrder.getCardPass());
        try {
            return DES.encode(desString,
                    JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), SzfConsts.DES_KEY));
        } catch (Exception e) {
            throw new PayException(Consts.SC.DATA_ERROR, e.getMessage() + "  " + payOrder.getCardTotalAmount()
                    + SzfConsts.CONNECT + payOrder.getCardNum());
        }
    }

    /**
     * 支付签名
     * version,merId,
     * payMoney,orderId,returnUrl,cardInfo,privateField,verifyType,privateKey
     * 拼成一个无间隔的字符串进行MD5加密
     * 
     * @param request
     * @param signKey
     * @return
     */
    public static String genPaySign(Map<String, String> request, String signKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.get(SzfConsts.KEY_VERSION)).append(request.get(SzfConsts.KEY_MERID))
                .append(request.get(SzfConsts.KEY_PAYMONEY)).append(request.get(SzfConsts.KEY_ORDERID))
                .append(request.get(SzfConsts.KEY_RETURNURL)).append(request.get(SzfConsts.KEY_CARDINFO))
                .append(request.get(SzfConsts.KEY_PRIVATEFIELD)).append(request.get(SzfConsts.KEY_VERIFYTYPE))
                .append(signKey);
        return DESEncrypt.getMD5(sb.toString()).toLowerCase();
    }

    /**
     * 查询签名
     * Md5(version+merId+orderIds+queryBegin+queryEnd+privateKey)，
     * 以上各参数中如果为空则以空字符串代替
     * 
     * @param request
     * @param signKey
     * @return
     */
    public static String genQuerySign(Map<String, String> request, String signKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.get(SzfConsts.KEY_VERSION)).append(request.get(SzfConsts.KEY_MERID))
                .append(request.get(SzfConsts.KEY_ORDERID)).append(signKey);
        return DESEncrypt.getMD5(sb.toString()).toLowerCase();
    }

    /**
     * 根据支付请求返回结果组装payorder
     * 
     * @param payOrder
     * @param respStr
     */
    public static void updatePayOrderByPay(PayOrder payOrder, String respStr) {
        LOG.info("[szx_in] pay results, respStr={}, payOrder={}", respStr, payOrder, TraceHelper.getTrace(payOrder));
        if (SzfConsts.PAY_SUCCESS.equals(respStr) || SzfConsts.PAY_PASWHANDLE.equals(respStr)) {
            payOrder.setStatusCode(Consts.SC.PENDING);
        } else if (SzfConsts.PAY_CARDERROR.equalsIgnoreCase(respStr)) {
            // 当返回值为104时，认为是卡密错误，具体的错误信息是“序列号，密码简单验证失败或之前曾提交过的卡密已验证失败”
            LOG.info("[updatePayOrderByPay] szf get a wrong cardnum and cardpass,orderid:{}", payOrder.getAppOrderId());
            payOrder.setStatusCode(Consts.SC.CARD_ERROR);
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        transStatusMsgByPay(payOrder, respStr);
    }

    /**
     * 解析神州付返回的状态码[pay]
     * 
     * @param payOrder
     * @param respStr
     */
    private static void transStatusMsgByPay(PayOrder payOrder, String respStr) {
        if (SzfConsts.PAY_SUCCESS.equals(respStr)) { // 请求成功，神州付收单（非订单状态为成功）
            payOrder.setStatusMsg(SzfConsts.PAY_SUCCESS_DES);
        } else if (SzfConsts.PAY_PASWHANDLE.equals(respStr)) { // 密码正在处理中
            payOrder.setStatusMsg(SzfConsts.PAY_PASWHANDLE_DES);
        } else if (SzfConsts.PAY_CARDERROR.equals(respStr)) { // 序列号，密码简单验证失败或之前曾提交过的卡密已验证失败
            payOrder.setStatusMsg(SzfConsts.PAY_CARDERROR_DES);
        } else if (SzfConsts.PAY_MD5FAIL.equals(respStr)) { // md5 验证失败
            payOrder.setStatusMsg(SzfConsts.PAY_MD5FAIL_DES);
        } else if (SzfConsts.PAY_REPEATORDER.equals(respStr)) { // 订单号重复
            payOrder.setStatusMsg(SzfConsts.PAY_REPEATORDER_DES);
        } else if (SzfConsts.PAY_BADUSER.equals(respStr)) { // 恶意用户
            payOrder.setStatusMsg(SzfConsts.PAY_BADUSER_DES);
        } else if (SzfConsts.PAY_SYSBUSY.equals(respStr)) { // 系统繁忙，暂停提交
            payOrder.setStatusMsg(SzfConsts.PAY_SYSBUSY_DES);
        } else if (SzfConsts.PAY_NSF.equals(respStr)) { // 多次充值时卡内余额不足
            payOrder.setStatusMsg(SzfConsts.PAY_NSF_DES);
        } else if (SzfConsts.PAY_DESERROR.equals(respStr)) { // des 解密失败
            payOrder.setStatusMsg(SzfConsts.PAY_DESERROR_DES);
        } else if (SzfConsts.PAY_AUTHFAIL.equals(respStr)) { // 证书验证失败
            payOrder.setStatusMsg(SzfConsts.PAY_AUTHFAIL_DES);
        } else if (SzfConsts.PAY_INSERTDBFAIL.equals(respStr)) { // 插入数据库失败
            payOrder.setStatusMsg(SzfConsts.PAY_INSERTDEFAIL_DES);
        } else if (SzfConsts.PAY_INSERTDBFAIL2.equals(respStr)) { // 插入数据库失败
            payOrder.setStatusMsg(SzfConsts.PAY_INSERTDBFAIL2_DES);
        } else if (SzfConsts.PAY_ARGSERROR.equals(respStr)) { // 商户参数不全
            payOrder.setStatusMsg(SzfConsts.PAY_ARGSERROR_DES);
        } else if (SzfConsts.PAY_MERNOTEXIST.equals(respStr)) { // 商户 ID 不存在
            payOrder.setStatusMsg(SzfConsts.PAY_MERNOTEXIST_DES);
        } else if (SzfConsts.PAY_MERNOTACTIVATE.equals(respStr)) { // 商户没有激活
            payOrder.setStatusMsg(SzfConsts.PAY_MERNOTACTIVATE_DES);
        } else if (SzfConsts.PAY_MERNOTAUTH.equals(respStr)) { // 商户没有使用该接口的权限
            payOrder.setStatusMsg(SzfConsts.PAY_MERNOTAUTH_DES);
        } else if (SzfConsts.PAY_MERNOTKEY.equals(respStr)) { // 商户没有设置
                                                              // 密钥（privateKey）
            payOrder.setStatusMsg(SzfConsts.PAY_MERNOTKEY_DES);
        } else if (SzfConsts.PAY_MERNOTDESKEY.equals(respStr)) { // 商户没有设置 DES
                                                                 // 密钥
            payOrder.setStatusMsg(SzfConsts.PAY_MERNOTDESKEY_DES);
        } else if (SzfConsts.PAY_ORDERDONE.equals(respStr)) { // 该笔订单已经处理完成（订单状态已经为确定的状态：成功
                                                              // 或者 失败）
            payOrder.setStatusMsg(SzfConsts.PAY_ORDERDONE_DES);
        } else if (SzfConsts.PAY_URLNOTQUAL.equals(respStr)) { // 服务器返回地址，不符合规范
            payOrder.setStatusMsg(SzfConsts.PAY_URLNOTQUAL_DES);
        } else if (SzfConsts.PAY_ORDERIDNOTQUAL.equals(respStr)) { // 订单号，不符合规范
            payOrder.setStatusMsg(SzfConsts.PAY_ORDERIDNOTQUAL_DES);
        } else if (SzfConsts.PAY_ILLEGALORDER.equals(respStr)) { // 非法订单
            payOrder.setStatusMsg(SzfConsts.PAY_ILLEGALORDER_DES);
        } else if (SzfConsts.PAY_CARDNOTSUPPORT.equals(respStr)) { // 该地方卡暂时不支持
            payOrder.setStatusMsg(SzfConsts.PAY_CARDNOTSUPPORT_DES);
        } else if (SzfConsts.PAY_AMOUNTERROR.equals(respStr)) { // 金额非法
            payOrder.setStatusMsg(SzfConsts.PAY_AMOUNTERROR_DES);
        } else if (SzfConsts.PAY_TOTALAMOUNTERROR.equals(respStr)) { // 卡面额非法
            payOrder.setStatusMsg(SzfConsts.PAY_TOTALAMOUNTERROR_DES);
        } else if (SzfConsts.PAY_MERNOTSUPPORTCARD.equals(respStr)) { // 商户不支持该充值卡
            payOrder.setStatusMsg(SzfConsts.PAY_MERNOTSUPPORTCART_DES);
        } else if (SzfConsts.PAY_ARGSNOTFORMAL.equals(respStr)) { // 参数格式不正确
            payOrder.setStatusMsg(SzfConsts.PAY_ARGSNOTFORMAL_DES);
        } else if (SzfConsts.PAY_NETWORKERROR.equals(respStr)) { // 网络连接失败
            payOrder.setStatusMsg(SzfConsts.PAY_NETWORKERROR_DES);
        } else {
            payOrder.setStatusMsg(respStr); // 未知状态码
        }
    }

    /**
     * 根据查询结果更新payorder
     * 
     * @param payOrder
     * @param respStr
     */
    public static void updateQueryOrderByPay(PayOrder payOrder, String respStr) {
        LOG.info("[szx_in] query results, respStr={}, payOrder={}", respStr, payOrder, TraceHelper.getTrace(payOrder));
        Map<String, String> paramsMap = getMapFromXml(respStr);
        String signInReq = paramsMap.get(SzfConsts.KEY_MD5STRING);
        // 在订单不存在的情况下，md5String为空，校验肯定不能通过
        if (StringUtils.isBlank(signInReq)) {
            LOG.info("[szx_in] sign is empty, return with out any changing,orderid:{}", payOrder.getAppOrderId());
            return;
        }
        String signExpect = getQueryResultSign(paramsMap, payOrder.getAppChInfo().getChPayKeyMd5());
        if (!signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s", signInReq,
                    signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        payOrder.setStatusCode(getPayStatus(paramsMap));
        payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        transStatusMsgByQuery(payOrder, paramsMap.get(SzfConsts.KEY_QUERYRESULT));
    }

    /**
     * 解析神州付返回的状态码[query]
     * 
     * @param payOrder
     * @param queryResult
     */
    private static void transStatusMsgByQuery(PayOrder payOrder, String queryResult) {
        if (SzfConsts.QUERY_SUCCESS.equals(queryResult)) { // 成功
            payOrder.setStatusMsg("神州付查询：" + SzfConsts.QUERY_SUCCESS_DES);
        } else if (SzfConsts.QUERY_ARGSNOTENOUGH.equals(queryResult)) { // 参数不全
            payOrder.setStatusMsg("神州付查询：" + SzfConsts.QUERY_ARGSNOTENOUGH_DES);
        } else if (SzfConsts.QUERY_MERIDNOTEXIST.equals(queryResult)) { // 商户ID不存在
            payOrder.setStatusMsg("神州付查询：" + SzfConsts.QUERY_MERIDNOTEXIST_DES);
        } else if (SzfConsts.QUERY_ORDERIDNOTEXIST.equals(queryResult)) { // 订单号不存在
            payOrder.setStatusMsg("神州付查询：" + SzfConsts.QUERY_ORDERINDOTEXIST_DES);
        } else if (SzfConsts.QUERY_MD5FAIL.equals(queryResult)) { // MD5校验失败
            payOrder.setStatusMsg("神州付查询：" + SzfConsts.QUERY_MD5FAIL_DES);
        } else {
            payOrder.setStatusMsg("神州付查询：" + queryResult); // 未知状态码
        }
    }

    /**
     * 验证查询结果签名
     * 
     * @param paramsMap
     * @param signKey
     * @return
     */
    private static String getQueryResultSign(Map<String, String> paramsMap, String signKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(paramsMap.get(SzfConsts.KEY_QUERYRESULT)).append(paramsMap.get(SzfConsts.KEY_VERSION))
                .append(paramsMap.get(SzfConsts.KEY_MERID)).append(paramsMap.get(SzfConsts.KEY_ORDERID))
                .append(paramsMap.get(SzfConsts.KEY_PAYRESULT)).append(paramsMap.get(SzfConsts.KEY_PAYMONEY))
                .append(signKey);
        return DESEncrypt.getMD5(sb.toString()).toLowerCase();
    }

    /**
     * 状态转换
     * 
     * @param paramsMap
     * @return
     */
    private static String getPayStatus(Map<String, String> paramsMap) {
        int status = Integer.parseInt(paramsMap.get(SzfConsts.KEY_PAYRESULT));
        String message = "";
        switch (status) {
        case 1:
            message = Consts.SC.SUCCESS;
            break;
        case 0:
            message = Consts.SC.FAIL;
            break;
        case 2:
            message = Consts.SC.PENDING;
            break;
        }
        return message;
    }

    /**
     * 根据返回xml 转换成map
     * 
     * @param respStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getMapFromXml(String respStr) {
        Document document = null;
        Map<String, String> paramsMap = new HashMap<String, String>();
        SAXReader READER = new SAXReader(false);
        try {
            document = READER.read(new ByteArrayInputStream(respStr.getBytes()));
            Element root = document.getRootElement();
            for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
                Element element = (Element) i.next();
                paramsMap.put(element.getName(), element.getText());
            }
        } catch (DocumentException e) {
            LOG.error("[szf_in]parse response with exception!", e);
        } finally {
            if (document != null) {
                document.clearContent();
            }
        }
        return paramsMap;
    }

    /**
     * 根据同步（ReturnUrl）或异步（NotifyUrl）中的信息，组装PayOrder
     */
    public static PayOrder assemblePayOrder(DomainResource resource, Map<String, String> params) {
        LOG.info("[szx_in] assemblePayOrder results, params={}, payOrder={}", params, "ds:trace:0");
        String chOrderId = params.get(SzfConsts.KEY_ORDERID);
        PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
        // 如果订单状态已经成功直接返回
        if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode())) {
            return payOrder;
        }
        String signInReq = params.get(SzfConsts.KEY_MD5STRING);
        String signExpect = getAsyNotifySign(params, payOrder.getAppChInfo().getChPayKeyMd5());
        if (!signExpect.equals(signInReq)) {
            String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s", signInReq,
                    signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
        if (SzfConsts.KEY_PAYSUCCESS.equals(params.get(SzfConsts.KEY_PAYRESULT))) {
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
            payOrder.setStatusCode(Consts.SC.SUCCESS);
        } else if (SzfConsts.KEY_PAYFAIL.equals(params.get(SzfConsts.KEY_PAYRESULT))) {
            // 如果不是CARD_ERROR或者FAIL,那么就更新状态，否则不更新。
            if (!Consts.SC.CARD_ERROR.equalsIgnoreCase(payOrder.getStatusCode())
                    || !Consts.SC.FAIL.equalsIgnoreCase(payOrder.getStatusCode())) {
                // 当返回的payResult为0，而且errcode为201时，认为卡密错误，具体的提示信息是“您输入的充值卡密码错误或充值卡余额不足”
                // errcode为203时，认为卡密错误，具体的提示信息是“您输入的充值卡密码非法”
                // 其他可能另作考虑的状态包括“202:您输入的充值卡已被使用 221:本卡之前被处理完毕，本次订单失败，不再继续处理”
                if (SzfConsts.NOTIFY_PWDERRAMOUNTERR.equalsIgnoreCase(params.get(SzfConsts.KEY_ERRORCODE))
                        || SzfConsts.NOTIFY_PWDILLEGAL.equalsIgnoreCase(params.get(SzfConsts.KEY_ERRORCODE))) {
                    LOG.info("[assemblePayOrder] szf get a wrong cardnum and cardpass,orderid:{}",
                            payOrder.getAppOrderId());
                    payOrder.setStatusCode(Consts.SC.CARD_ERROR);
                } else {
                    payOrder.setStatusCode(Consts.SC.FAIL);
                }
            }
        }
        transStatusMsgByNotify(payOrder, params.get(SzfConsts.KEY_ERRORCODE));
        LOG.info("[szx_in] assemblePayOrder results, params={}, payOrder={}", params, payOrder,
                TraceHelper.getTrace(payOrder));
        return payOrder;
    }

    /**
     * 解析神州付返回的状态码[notify]
     * 
     * @param payOrder
     * @param errorCode
     */
    private static void transStatusMsgByNotify(PayOrder payOrder, String errcode) {
        if (SzfConsts.NOTIFY_SUCCESS.equals(errcode)) { // 充值卡验证成功
            payOrder.setStatusMsg(SzfConsts.NOTIFY_SUCCESS_DES);
        } else if (SzfConsts.NOTIFY_PWDERRAMOUNTERR.equals(errcode)) { // 您输入的充值卡密码错误或充值卡余额不足
            payOrder.setStatusMsg(SzfConsts.NOTIFY_PWDERRAMOUNTERR_DES);
        } else if (SzfConsts.NOTIFY_CARDUSED.equals(errcode)) { // 您输入的充值卡已被使用
            payOrder.setStatusMsg(SzfConsts.NOTIFY_CARDUSED_DES);
        } else if (SzfConsts.NOTIFY_PWDILLEGAL.equals(errcode)) { // 您输入的充值卡密码非法
            payOrder.setStatusMsg(SzfConsts.NOTIFY_PWDILLEGAL_DES);
        } else if (SzfConsts.NOTIFY_FAILTOOMUCH.equals(errcode)) { // 您输入的卡号或密码错误次数过多
            payOrder.setStatusMsg(SzfConsts.NOTIFY_FAILTOOMUCH_DES);
        } else if (SzfConsts.NOTIFY_CARDNOTFORMAL.equals(errcode)) { // 卡号密码正则不匹配或者被禁止
            payOrder.setStatusMsg(SzfConsts.NOTIFY_CARDNOTFORMAL_DES);
        } else if (SzfConsts.NOTIFY_REPEATSUBMIT.equals(errcode)) { // 本卡之前被提交过，本次订单失败，不再继续处理
            payOrder.setStatusMsg(SzfConsts.NOTIFY_REPEATSUBMIT_DES);
        } else if (SzfConsts.NOTIFY_CARDNOTSUPPORT.equals(errcode)) { // 暂不支持该充值卡
            payOrder.setStatusMsg(SzfConsts.NOTIFY_CARDNOTSUPPORT_DES);
        } else if (SzfConsts.NOTIFY_CARDNUMERROR.equals(errcode)) { // 您输入的充值卡卡号错误
            payOrder.setStatusMsg(SzfConsts.NOTIFY_CARDNUMERROR_DES);
        } else if (SzfConsts.NOTIFY_CARDNOTACTIVATE.equals(errcode)) { // 您输入的充值卡未激活（生成卡）
            payOrder.setStatusMsg(SzfConsts.NOTIFY_CARDNOTACTIVATE_DES);
        } else if (SzfConsts.NOTIFY_CARDINVALID.equals(errcode)) { // 您输入的充值卡已经作废（能查到有该卡，但是没卡的信息）
            payOrder.setStatusMsg(SzfConsts.NOTIFY_CARDINVALID_DES);
        } else if (SzfConsts.NOTIFY_CARDEXPIRED.equals(errcode)) { // 您输入的充值卡已过期
            payOrder.setStatusMsg(SzfConsts.NOTIFY_CARDEXPIRED_DES);
        } else if (SzfConsts.NOTIFY_TOTALAMOUNTERROR.equals(errcode)) { // 您选择的卡面额不正确
            payOrder.setStatusMsg(SzfConsts.NOTIFY_TOTALAMOUNTERROR_DES);
        } else if (SzfConsts.NOTIFY_SPECIALCARD.equals(errcode)) { // 该卡为特殊本地业务卡，系统不支持
            payOrder.setStatusMsg(SzfConsts.NOTIFY_SPECIALCARD_DES);
        } else if (SzfConsts.NOTIFY_RISECARD.equals(errcode)) { // 该卡为增值业务卡，系统不支持
            payOrder.setStatusMsg(SzfConsts.NOTIFY_RISECARD_DES);
        } else if (SzfConsts.NOTIFY_NEWCARD.equals(errcode)) { // 新生卡
            payOrder.setStatusMsg(SzfConsts.NOTIFY_NEWCARD_DES);
        } else if (SzfConsts.NOTIFY_SYSMAIN.equals(errcode)) { // 系统维护
            payOrder.setStatusMsg(SzfConsts.NOTIFY_SYSMAIN_DES);
        } else if (SzfConsts.NOTIFY_INTERFACEMAIN.equals(errcode)) { // 接口维护
            payOrder.setStatusMsg(SzfConsts.NOTIFY_INTERFACEMAIN_DES);
        } else if (SzfConsts.NOTIFY_OSYSMAIN.equals(errcode)) { // 运营商系统维护
            payOrder.setStatusMsg(SzfConsts.NOTIFY_OSYSMAIN_DES);
        } else if (SzfConsts.NOTIFY_SYSBUSY.equals(errcode)) { // 系统忙，请稍后再试
            payOrder.setStatusMsg(SzfConsts.NOTIFY_SYSBUSY_DES);
        } else if (SzfConsts.NOTIFY_ERRORUNKNOWN.equals(errcode)) { // 未知错误
            payOrder.setStatusMsg(SzfConsts.NOTIFY_ERRORUNKNOWN_DES);
        } else if (SzfConsts.NOTIFY_CARDDONE.equals(errcode)) { // 本卡之前被处理完毕，本次订单失败，不再继续处理
            payOrder.setStatusMsg(SzfConsts.NOTIFY_CARDDONE_DES);
        } else {
            payOrder.setStatusMsg(errcode); // 未知状态码
        }
    }

    /**
     * 验证异步通知结果签名
     * 
     * @param paramsMap
     * @param signKey
     * @return
     */
    private static String getAsyNotifySign(Map<String, String> paramsMap, String signKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(paramsMap.get(SzfConsts.KEY_VERSION)).append(paramsMap.get(SzfConsts.KEY_MERID))
                .append(paramsMap.get(SzfConsts.KEY_PAYMONEY)).append(paramsMap.get(SzfConsts.KEY_ORDERID))
                .append(paramsMap.get(SzfConsts.KEY_PAYRESULT)).append(paramsMap.get(SzfConsts.KEY_PRIVATEFIELD))
                .append(paramsMap.get(SzfConsts.KEY_PAYDETAILS)).append(signKey);
        return DESEncrypt.getMD5(sb.toString()).toLowerCase();
    }
}
