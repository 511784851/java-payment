package com.guzhi.pay.helper;

import org.apache.commons.lang3.StringUtils;

/**
 * 内部订单号约定生成规则。
 * 
 * @author administrator
 */
public class OrderIdHelper {
    public static final int APP_ID_LEN = 3;

    /**
     * 根据appId，appOrderId简单的拼装出chOrderId。
     */
    public static String genChOrderId(String appId, String appOrderId) {
        return appId + appOrderId;
    }

    public static String getAppId(String chOrderId) {
        return isInvalidChOrderId(chOrderId) ? "" : chOrderId.substring(0, APP_ID_LEN);
    }

    public static String getAppOrderId(String chOrderId) {
    	return isInvalidChOrderId(chOrderId) ? "" :  chOrderId.substring(APP_ID_LEN);
    }
    
    private static boolean isInvalidChOrderId(String chOrderId){
    	return StringUtils.isBlank(chOrderId) || chOrderId.length() <= APP_ID_LEN;
    }
}
