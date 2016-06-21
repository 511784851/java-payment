/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.broadbrand;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HexUtil;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TimeHelper;

/**
 * 新泛联帮助类
 * 
 * @author administrator
 * 
 */
public class BroadbandHelper {

    private static final Logger LOG = LoggerFactory.getLogger(BroadbandHelper.class);

    /**
     * 生成转16进制之后的密码.
     * 
     * @param cardPass
     * @param desKey
     * @return
     */
    public static String generateHexPassword(String cardPass, String desKey) {
        byte[] encrptCardPass = DESedeEncrypt.EncryptBy3DESCFB(cardPass, desKey);
        return HexUtil.toHexString(encrptCardPass);
    }

    /**
     * 生成预处理游戏卡供货接口签名.
     * 
     * @param chPayKeyMd5
     * @param requestMap
     * @return
     */
    public static String getSign(Map<String, String> requestMap, String chPayKeyMd5) {
        List<String> keys = new ArrayList<String>(requestMap.keySet());
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            sb.append(StringUtils.isBlank(requestMap.get(key)) ? StringUtils.EMPTY : requestMap.get(key));
        }
        sb.append(chPayKeyMd5);
        String sign = MD5Utils.getMD5(sb.toString());
        LOG.debug("[broadbandTxtong.sign] sign params:{},sign:{}", sb.toString(), sign);
        return sign;
    }

    /**
     * 按照游戏卡供货接口支付请求的响应组装订单.
     * 
     * @param payOrder
     * @param respStr
     * @return
     */
    public static void updatePayOrderByPay(PayOrder payOrder, String respStr) {
        LOG.info("[broadbandTxtong.async] update order by pay response,order:{},response:{}", payOrder, respStr);
        updatePayOrder(payOrder, respStr, true);
    }

    /**
     * 按照游戏卡供货接口通知组装订单.（同样适用查询响应）
     * 
     * @param payOrder
     * @param respStr
     * @return
     */
    public static void updatePayOrderByNotify(PayOrder payOrder, String respStr) {
        updatePayOrder(payOrder, respStr, false);
    }

    /**
     * 按照游戏卡供货接口支付请求的响应组装订单.
     * 
     * @param payOrder
     * @param respStr
     * @param isPay 是否支付同步返回
     * @return
     */
    private static void updatePayOrder(PayOrder payOrder, String respStr, boolean isPay) {
        Map<String, String> map = Help.getMapByRespStr(respStr);
        Map<String, String> signMap = new LinkedHashMap<String, String>();
        signMap.put(BroadbandConsts.KEY_CODE, map.get(BroadbandConsts.KEY_CODE));
        signMap.put(BroadbandConsts.KEY_CONSUME_PAR, map.get(BroadbandConsts.KEY_CONSUME_PAR));
        signMap.put(BroadbandConsts.KEY_MCH_ORDER_ID, map.get(BroadbandConsts.KEY_MCH_ORDER_ID));
        if (StringUtils.isNotBlank(map.get(BroadbandConsts.KEY_MESSAGE))) {
            signMap.put(BroadbandConsts.KEY_MESSAGE,
                    StringHelper.decode(map.get(BroadbandConsts.KEY_MESSAGE), Consts.CHARSET_UTF8));
        }
        signMap.put(BroadbandConsts.KEY_ORDER_ID, map.get(BroadbandConsts.KEY_ORDER_ID));
        String expectSign = getSign(signMap, payOrder.getAppChInfo().getChPayKeyMd5());
        if (!expectSign.equals(map.get(BroadbandConsts.KEY_SIGN))) {
            String errorMsg = "[broadbandTxtong.assemble] get a invalid sign,orderid:" + payOrder.getAppOrderId();
            LOG.error(errorMsg);
            throw new PayException(Consts.SC.CHANNEL_ERROR, errorMsg);
        }
        String code = map.get(BroadbandConsts.KEY_CODE);
        payOrder.setStatusCode(transStatus(code, isPay));

        // 只有在成功订单时才判断金额
        if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode()) && map.containsKey(BroadbandConsts.KEY_CONSUME_PAR)) {
            int partnerAmount = MapUtils.getInteger(map, BroadbandConsts.KEY_CONSUME_PAR);
            if (payOrder.getAmount().intValue() > partnerAmount) {
                String errorMsg = "[broadbandTxtong.assemble] get an amout mismatch,amount:" + payOrder.getAmount()
                        + ",parter amount:" + partnerAmount + ",orderid:" + map.get(BroadbandConsts.KEY_MCH_ORDER_ID);
                LOG.error(errorMsg);
                throw new PayException(Consts.SC.CHANNEL_ERROR, errorMsg);
            }
        }

        payOrder.setStatusMsg(transCode(code, isPay));
        if (map.containsKey(BroadbandConsts.KEY_ORDER_ID)) {
            payOrder.setChDealId(map.get(BroadbandConsts.KEY_ORDER_ID));
            payOrder.setChDealTime(TimeHelper.get(8, new Date()));
        }
        LOG.info("[broadbandTxtong.assemble] after assemble,respStr:{},payOrder:{}", respStr, payOrder);
    }

    /**
     * 转换游戏卡供货接口返回code为订单状态.
     * 
     * @param code
     * @param isPay 是否是支付同步返回
     */
    private static String transStatus(String code, boolean isPay) {
        if (isPay) {
            if (BroadbandConsts.RETURNCODE.CODE_101.equalsIgnoreCase(code)) {
                return Consts.SC.PENDING;
            } else if (BroadbandConsts.RETURNCODE.CODE_111.equals(code)) {
                return Consts.SC.CARD_ERROR;
            } else {
                return Consts.SC.FAIL;
            }
        } else {
            if (BroadbandConsts.NOTIFYCODE.CODE_201.equalsIgnoreCase(code)) {
                return Consts.SC.SUCCESS;
            } else if (BroadbandConsts.NOTIFYCODE.CODE_203.equals(code)) {
                return Consts.SC.CARD_ERROR;
            } else if (BroadbandConsts.NOTIFYCODE.CODE_221.equals(code)) {
                return Consts.SC.PENDING;
            } else {
                return Consts.SC.FAIL;
            }
        }
    }

    /**
     * 转换游戏卡供货接口返回code为订单状态.
     * 
     * @param code
     * @param asyn 是否是支付同步返回
     */
    private static String transCode(String code, boolean asyn) {
        if (asyn) {
            return transReturnCode(code);
        } else {
            return transNotifyCode(code);
        }
    }

    /**
     * 翻译游戏卡供货接口同步返回码.
     * 
     * @param code
     */
    private static String transReturnCode(String code) {
        if (BroadbandConsts.RETURNCODE.CODE_100.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_100;
        } else if (BroadbandConsts.RETURNCODE.CODE_101.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_101;
        } else if (BroadbandConsts.RETURNCODE.CODE_103.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_103;
        } else if (BroadbandConsts.RETURNCODE.CODE_104.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_104;
        } else if (BroadbandConsts.RETURNCODE.CODE_105.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_105;
        } else if (BroadbandConsts.RETURNCODE.CODE_106.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_106;
        } else if (BroadbandConsts.RETURNCODE.CODE_107.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_107;
        } else if (BroadbandConsts.RETURNCODE.CODE_110.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_110;
        } else if (BroadbandConsts.RETURNCODE.CODE_111.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_111;
        } else if (BroadbandConsts.RETURNCODE.CODE_112.equals(code)) {
            return BroadbandConsts.RETURNCODE.MSG_112;
        } else {
            return "新泛联：不能识别的返回码" + code;
        }
    }

    /**
     * 翻译游戏卡供货接口异步返回码.
     * 
     * @param code
     */
    private static String transNotifyCode(String code) {
        if (BroadbandConsts.NOTIFYCODE.CODE_201.equals(code)) {
            return BroadbandConsts.NOTIFYCODE.MSG_201;
        } else if (BroadbandConsts.NOTIFYCODE.CODE_200.equals(code)) {
            return BroadbandConsts.NOTIFYCODE.MSG_200;
        } else if (BroadbandConsts.NOTIFYCODE.CODE_203.equals(code)) {
            return BroadbandConsts.NOTIFYCODE.MSG_203;
        } else if (BroadbandConsts.NOTIFYCODE.CODE_207.equals(code)) {
            return BroadbandConsts.NOTIFYCODE.MSG_207;
        } else if (BroadbandConsts.NOTIFYCODE.CODE_208.equals(code)) {
            return BroadbandConsts.NOTIFYCODE.MSG_208;
        } else if (BroadbandConsts.NOTIFYCODE.CODE_221.equals(code)) {
            return BroadbandConsts.NOTIFYCODE.MSG_221;
        } else if (BroadbandConsts.NOTIFYCODE.CODE_222.equals(code)) {
            return BroadbandConsts.NOTIFYCODE.MSG_222;
        } else {
            return "新泛联：不能识别的返回码" + code;
        }
    }
}
