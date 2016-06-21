/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.lkl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;

/**
 * @author administrator
 * 
 */
public class LklHelper {
    private static final Logger LOG = LoggerFactory.getLogger(LklHelper.class);
    private static final int RESULT_LENGTH = 7;
    private static final String MACTYPE = "2";
    private static final String ZERO = "0";
    private static final String SEP = "\\|";

    /**
     * 获取支付请求的签名
     * VER|MERID|MAC_KEY|ORDERID|AMOUNT|RANDNUM|PAYURL|MACTYPE|MINCODE|
     * 
     * @param request
     * @param payOrder
     * @return
     */
    public static String getPaySign(Map<String, String> request, PayOrder payOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.get(LklConsts.VER)).append(LklConsts.SEPARATOR).append(request.get(LklConsts.MERID))
                .append(LklConsts.SEPARATOR).append(payOrder.getAppChInfo().getChPayKeyMd5())
                .append(LklConsts.SEPARATOR).append(request.get(LklConsts.ORDERID)).append(LklConsts.SEPARATOR)
                .append(request.get(LklConsts.AMOUNT)).append(LklConsts.SEPARATOR)
                .append(request.get(LklConsts.RANDNUM)).append(LklConsts.SEPARATOR)
                .append(request.get(LklConsts.PAYURL)).append(LklConsts.SEPARATOR)
                .append(request.get(LklConsts.MACTYPE)).append(LklConsts.SEPARATOR)
                .append(request.get(LklConsts.MINCODE)).append(LklConsts.SEPARATOR);
        return DESEncrypt.getMD5(sb.toString()).toLowerCase();
    }

    /**
     * 获取查询请求的签名
     * 
     * @param request
     * @param payOrder
     * @return
     */
    public static String getQuerySign(Map<String, String> request, PayOrder payOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(LklConsts.VER_ID).append(Consts.EQ).append(request.get(LklConsts.VER_ID)).append(Consts.AMP)
                .append(LklConsts.MER_ID).append(Consts.EQ).append(request.get(LklConsts.MER_ID)).append(Consts.AMP)
                .append(LklConsts.ORDER_DATE).append(Consts.EQ).append(request.get(LklConsts.ORDER_DATE))
                .append(Consts.AMP).append(LklConsts.ORDER_ID).append(Consts.EQ)
                .append(request.get(LklConsts.ORDER_ID)).append(Consts.AMP).append(LklConsts.MAC_TYPE)
                .append(Consts.EQ).append(request.get(LklConsts.MAC_TYPE)).append(Consts.AMP).append(LklConsts.MER_KEY)
                .append(Consts.EQ).append(payOrder.getAppChInfo().getChPayKeyMd5());
        return DESEncrypt.getMD5(sb.toString()).toLowerCase();
    }

    /**
     * 根据查询结果更新payorder
     * 
     * @param queryRespStr
     * @param payOrder
     */
    public static void updateByQueryResult(String queryRespStr, PayOrder payOrder) {
        String[] results = queryRespStr.split(SEP);
        if (results.length < RESULT_LENGTH) {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg(Consts.FAIL_DES);
            return;
        }
        validateQueryResultSign(results, payOrder);
        if (LklConsts.SUCCESS.equalsIgnoreCase(results[7])) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setStatusMsg(Consts.SUCCDESS_DES);
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg(Consts.FAIL_DES);
        }
    }

    /**
     * 对查询结果进行签名验证
     * 查询结果account_date|amount|pay_method|mer_id|order_date|order_id|pay_seq|
     * result|ver_id|verify_string
     * 签名验证ver_id=%s&mer_id=%s&order_date=%s&order_id=%s&amount=%s&result=%s&
     * mer_key=%s
     * 
     * @param results
     * @param payOrder
     */
    private static void validateQueryResultSign(String[] results, PayOrder payOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(LklConsts.VER_ID).append(Consts.EQ).append(results[8]).append(Consts.AMP).append(LklConsts.MER_ID)
                .append(Consts.EQ).append(results[3]).append(Consts.AMP).append(LklConsts.ORDER_DATE).append(Consts.EQ)
                .append(results[4]).append(Consts.AMP).append(LklConsts.ORDER_ID).append(Consts.EQ).append(results[5])
                .append(Consts.AMP).append(LklConsts.QUERY_AMOUNT).append(Consts.EQ).append(results[1])
                .append(Consts.AMP).append(LklConsts.RESULT).append(Consts.EQ).append(results[7]).append(Consts.AMP)
                .append(LklConsts.MER_KEY).append(Consts.EQ).append(payOrder.getAppChInfo().getChPayKeyMd5());
        String expectSign = DESEncrypt.getMD5(sb.toString()).toLowerCase();
        String sourceSign = results[9];
        if (!expectSign.equals(sourceSign)) {
            String msg = "sourceSign:" + sourceSign + "   expectSign:" + expectSign;
            LOG.error("validateQueryResultSign error " + msg, TraceHelper.getTrace(payOrder));
            throw new PayException(Consts.SC.SECURE_ERROR, msg);
        }
    }

    /**
     * 根据通知修改pay状态
     * 
     * @param requestMap
     * @param resource
     */
    public static PayOrder updatePayOrderWithNotify(Map<String, String> requestMap, DomainResource resource) {
        String chOrderId = requestMap.get(LklConsts.ORDERID);
        PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
        LOG.error("lklBalanceWithNotify] get notify request parameters:{}", requestMap, TraceHelper.getTrace(payOrder));
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return payOrder;
        }
        validateNotifySign(requestMap, payOrder);
        payOrder.setStatusCode(Consts.SC.SUCCESS);
        payOrder.setBankDealId(requestMap.get(LklConsts.PAYMENTSERI));
        payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        payOrder.setChDealId(requestMap.get(LklConsts.PLATSEQ));
        return payOrder;
    }

    /**
     * 对通知回来的结果进行签名校验
     * 
     * @param requestMap
     * @param payOrder
     */
    private static void validateNotifySign(Map<String, String> requestMap, PayOrder payOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(requestMap.get(LklConsts.VER)).append(requestMap.get(LklConsts.MERID))
                .append(payOrder.getAppChInfo().getChPayKeyMd5()).append(requestMap.get(LklConsts.ORDERID))
                .append(requestMap.get(LklConsts.AMOUNT)).append(requestMap.get(LklConsts.RANDNUM))
                .append(requestMap.get(LklConsts.ACCOUNTDATE)).append(requestMap.get(LklConsts.PLATSEQ))
                .append(MACTYPE).append(requestMap.get(LklConsts.PAYMENTSERI))
                .append(requestMap.get(LklConsts.TERMINALNO)).append(requestMap.get(LklConsts.ACCOUNTNAME));
        String expectSign = DESEncrypt.getMD5(sb.toString()).toLowerCase();
        String sourceSign = requestMap.get(LklConsts.MAC);
        if (!expectSign.equals(sourceSign)) {
            String msg = "sourceSign:" + sourceSign + "   expectSign:" + expectSign;
            LOG.error("validateNotifySign error " + msg, TraceHelper.getTrace(payOrder));
            throw new PayException(Consts.SC.SECURE_ERROR, msg);
        }

    }

    /**
     * 生成六位随机数
     * 
     * @return
     */
    public static String getRandom() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += random.nextInt(10);
        }
        return result;
    }

    /**
     * 返回通知结果
     * 
     * @return
     */
    public static String returnNotify() {
        Document document = DocumentFactory.getInstance().createDocument();
        Element root = document.addElement(LklConsts.MERPAY);
        ArrayList<Element> al = new ArrayList<Element>();
        Element elmt = root.addElement(LklConsts.RETCODE);
        elmt.setText(ZERO);
        al.add(elmt);
        elmt = root.addElement(LklConsts.MERURL);
        // 经拉卡拉技术支持确认，这个url 无作用可以设置为空
        elmt.setText("");
        al.add(elmt);
        root.setContent(al);
        StringWriter stringWriter = new StringWriter();
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding(Consts.CHARSET_GBK);
        XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
        try {
            xmlWriter.write(document);
        } catch (IOException e) {
        }
        String result = stringWriter.toString();
        LOG.info("returnNotify:{}", result);
        return result;
    }
}
