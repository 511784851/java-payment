/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.th;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.TimeHelper;

/**
 * 天宏帮助类
 * 
 * @author administrator
 * 
 */
public class ThYktHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ThYktHelper.class);

    /**
     * 获取支付时md5签名
     * 
     * @param request
     * @param signKey
     * @return
     */
    public static String genPaySign(Map<String, String> request, String signKey) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(ThConsts.USERNAME, request.get(ThConsts.USERNAME));
        map.put(ThConsts.PRODUCTID, request.get(ThConsts.PRODUCTID));
        map.put(ThConsts.CKNUM, request.get(ThConsts.CKNUM));
        for (int i = 0; i < Integer.valueOf(request.get(ThConsts.CKNUM)); i++) {
            map.put(ThConsts.KAOHAO + String.valueOf(i + 1), request.get(ThConsts.KAOHAO + String.valueOf(i + 1)));
            map.put(ThConsts.MIMA + String.valueOf(i + 1), request.get(ThConsts.MIMA + String.valueOf(i + 1)));
        }
        map.put(ThConsts.BUYNUM, request.get(ThConsts.BUYNUM));
        map.put(ThConsts.ORDERNUM, request.get(ThConsts.ORDERNUM));
        // map.put(ThConsts.RETURNURL, request.get(ThConsts.RETURNURL));
        return DESEncrypt.getMD5(Help.getStrByMap(map, true) + Consts.CONNECT + signKey).toLowerCase();
    }

    /**
     * 获取查询时md5签名
     * 
     * @param request
     * @param signKey
     * @return
     */
    public static String getQuerySign(Map<String, String> request, String signKey) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(ThConsts.USERNAME_Q, request.get(ThConsts.USERNAME_Q));
        map.put(ThConsts.ORDERNUM_Q, request.get(ThConsts.ORDERNUM_Q));
        return DESEncrypt.getMD5(Help.getStrByMap(map, true) + Consts.CONNECT + signKey).toLowerCase();
    }

    /**
     * 根据支付结果更新payorder
     * 
     * @param payOrder
     * @param respStr
     */
    public static void updatePayOrderByPay(PayOrder payOrder, String respStr) {
        LOG.info("[ThYkt] updatePayOrderByPay results, respStr={}, payOrder={}", respStr, payOrder);
        Document document = null;
        Map<String, String> paramsMap = new HashMap<String, String>();
        try {
            document = new SAXReader(false).read(new ByteArrayInputStream(respStr.getBytes(Charset
                    .forName(Consts.CHARSET_UTF8))));
            Element request = document.getRootElement().element(ThConsts.STATE);
            if (request != null) {
                for (@SuppressWarnings("unchecked")
                Iterator<Element> iterator = request.elementIterator(); iterator.hasNext();) {
                    Element temp = (Element) iterator.next();
                    paramsMap.put(temp.getName(), temp.getText());
                }
            }
        } catch (DocumentException e) {
            LOG.error("[ThYkt] parse xml error", e);
        }
        if (ThConsts.SUCCESS.equalsIgnoreCase(paramsMap.get(ThConsts.CODE))) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        } else if (ThConsts.CARD_ERROR.equalsIgnoreCase(paramsMap.get(ThConsts.CODE))) {
            // 当code为-6时，认为是卡密错误，错误信息可能为“1张卡的密码错误”或“第1张卡号不存在或已经使用”
            LOG.info(
                    "[ThYkt] updatePayOrderByPay set payorder to card_error, get a wrong cardNum and cardPass,chOrderid:{}",
                    payOrder.getAppOrderId());
            payOrder.setStatusCode(Consts.SC.CARD_ERROR);
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        // StringBuilder str = new StringBuilder();
        // str.append(ThConsts.CODE + paramsMap.get(ThConsts.CODE) + SEMI);
        // str.append(ThConsts.MSG + paramsMap.get(ThConsts.MSG) + SEMI);
        // str.append(ThConsts.MONEY + paramsMap.get(ThConsts.MONEY) + SEMI);

        payOrder.setStatusMsg(statusMsgConvertor(paramsMap.get(ThConsts.MSG)));
    }

    // 把天宏返回的“MD5验证错误”转为业务可识别的“卡号无效”
    private static String statusMsgConvertor(String msg) {
        return StringUtils.defaultString(msg, "").replace(ThConsts.MD5ERRORMSG, ThConsts.PAYERRORMSG);
    }

    /**
     * 根据查询结果更新payorder
     * 
     * @param payOrder
     * @param respStr
     */
    public static void updatePayOrderByQuery(PayOrder payOrder, String respStr) {
        LOG.info("[ThYkt] updatePayOrderByQuery results, respStr={}, payOrder={}", respStr, payOrder);
        Map<String, String> queryMap = getQueryMap(respStr);
        LOG.debug("[ThYkt] queryMap:{}", queryMap);
        validateResponseQuerySign(queryMap, payOrder.getAppChInfo().getChPayKeyMd5());
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return;
        }
        if (ThConsts.SUCCESS.equalsIgnoreCase(queryMap.get(ThConsts.CODE))) {
            payOrder.setStatusCode(Consts.SC.SUCCESS);
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        } else {
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        payOrder.setStatusMsg(statusMsgConvertor(queryMap.get(ThConsts.MSG)));
    }

    /**
     * 对查询结果返回签名验证
     * 
     * @param queryMap
     */
    private static void validateResponseQuerySign(Map<String, String> queryMap, String signKey) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(ThConsts.USERNAME_Q, queryMap.get(ThConsts.USERNAME_Q));
        map.put(ThConsts.BUYNUM_Q, queryMap.get(ThConsts.BUYNUM_Q));
        map.put(ThConsts.ORDERNUM_Q, queryMap.get(ThConsts.ORDERNUM_Q));
        map.put(ThConsts.PAYDATE_Q, queryMap.get(ThConsts.PAYDATE_Q));
        map.put(ThConsts.CODE, queryMap.get(ThConsts.CODE));
        String signExpect = DESEncrypt.getMD5(Help.getStrByMap(map, true) + Consts.CONNECT + signKey).toLowerCase();
        String signReq = queryMap.get(ThConsts.MD5KEY);
        if (!signExpect.equals(signReq)) {
            String msg = String.format("sign: unsupported type or unmatched!  signInReq=%s, signExpect=%s", signReq,
                    signExpect);
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg);
        }
    }

    /**
     * 对查询结果返回签名验证
     * 
     * @param queryMap
     */
    static void validateKahaoAndMima(String[] kahaos, String[] mimas) {
        if (kahaos == null || mimas == null || kahaos.length != mimas.length) {
            LOG.error("[ThYkt] validateKahaoAndMima results：false, kahaos={}, mimas={}", kahaos, mimas);
            throw new PayException(Consts.SC.DATA_ERROR, "kahaos unmatched mimas ");
        }
    }

    /**
     * 解析查询xml 成为map
     * 
     * @param respStr
     * @return
     */
    private static Map<String, String> getQueryMap(String respStr) {
        Document document = null;
        Map<String, String> queryMap = new HashMap<String, String>();
        try {
            document = new SAXReader(false).read(new ByteArrayInputStream(respStr.getBytes(Charset
                    .forName(Consts.CHARSET_UTF8))));
            Element request = document.getRootElement().element(ThConsts.STATE);
            if (request != null) {
                for (@SuppressWarnings("unchecked")
                Iterator<Element> iterator = request.elementIterator(); iterator.hasNext();) {
                    Element temp = (Element) iterator.next();
                    queryMap.put(temp.getName(), temp.getText());
                }
            }
            Element item = document.getRootElement().element(ThConsts.ITEMS).element(ThConsts.ITEM);
            if (request != null) {
                for (@SuppressWarnings("unchecked")
                Iterator<Element> iterator = item.elementIterator(); iterator.hasNext();) {
                    Element temp = (Element) iterator.next();
                    queryMap.put(temp.getName(), temp.getText());
                }
            }

            // put进剩余的,目前只有md5Key
            for (@SuppressWarnings("unchecked")
            Iterator<Element> i = document.getRootElement().elementIterator(); i.hasNext();) {
                Element element = (Element) i.next();
                if (element.getName().equals(ThConsts.STATE) || element.getName().equals(ThConsts.ITEMS)) {
                    continue;
                } else {
                    queryMap.put(element.getName(), element.getText());
                }
            }
        } catch (DocumentException e) {
            LOG.error("[ThYkt.query] parse query response xml error,response:{}", respStr, e);
        }
        return queryMap;
    }
}
