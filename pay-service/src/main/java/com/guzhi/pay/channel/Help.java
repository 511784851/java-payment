/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.OrderIdHelper;

/**
 * 所有渠道help 类的通用类
 * 
 * @author administrator
 * 
 */
public class Help {

    private static final Logger LOG = LoggerFactory.getLogger(Help.class);

    /**
     * 根据chOrderId 获取payorder
     * 
     * @param resource
     * @param chOrderId
     * @return
     */
    public static PayOrder getPayOrderByNotify(DomainResource resource, String chOrderId) {
        if (StringUtils.isBlank(chOrderId)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "chOrderId is empty");
        }
        String appId = OrderIdHelper.getAppId(chOrderId);
        String appOrderId = OrderIdHelper.getAppOrderId(chOrderId);
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appOrderId)) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "appId or appOrderId empty! chOrderId=" + chOrderId);
        }
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, payOrder.getChId(), payOrder.getPayMethod());
        if (payOrder == null || CollectionUtils.isEmpty(appChInfos)) {
            String msg = "payOrder/appChInfo not found!";
            throw new PayException(Consts.SC.CHANNEL_ERROR, msg + " chOrderId=" + chOrderId + ", payOrder=" + payOrder);
        }
        AppInfo appInfo = resource.getAppInfo(appId);
        payOrder.setAppInfo(appInfo);
        payOrder.setAppChInfo(appChInfos.get(0));
        return payOrder;
    }

    /**
     * 把返回字符串转成map
     * 字符串格式ret_code=0&ret_msg=成功&agent_id=1503152&bill_id=20120702192022
     * 
     * @param respStr
     * @return
     */
    public static Map<String, String> getMapByRespStr(String respStr) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isBlank(respStr)) {
            return map;
        }
        String data[] = respStr.trim().split(Consts.AMP);
        for (String one : data) {
            String keyValue[] = one.trim().split(Consts.EQ);
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 3) {
                map.put(keyValue[0], keyValue[1] + "=" + keyValue[2]);
            } else {
                map.put(keyValue[0], "");
            }
        }
        return map;
    }

    /**
     * 根据map 返回字符串
     * 
     * @param map 建议是linkhashmap
     * @param flag true 代表map 中的key value 可以为空，false 代表key 和value 都不能为空
     * @return 格式ret_code=0&ret_msg=xx&agent_id=1503152
     */
    public static String getStrByMap(Map<String, String> map, boolean flag) {
        String result = "";
        for (Iterator<Map.Entry<String, String>> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (flag) {
                if (StringUtils.isNotBlank(result)) {
                    result = result + Consts.AMP + key + Consts.EQ + value;
                } else {
                    result = key + Consts.EQ + value;
                }
            } else {
                if (StringUtils.isNotBlank(result)) {
                    if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                        result = result + Consts.AMP + key + Consts.EQ + value;
                    }
                } else {
                    if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                        result = key + Consts.EQ + value;
                    }
                }
            }
        }
        LOG.debug("[Help.getStrByMap] map:{},result:{}", map, result);
        return result;
    }
}
