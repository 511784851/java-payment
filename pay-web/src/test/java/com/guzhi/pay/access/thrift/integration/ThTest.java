/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.access.thrift.integration;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.SecureHelper;

/**
 * 测试ThriftServer的实际调用
 * 
 * @author administrator
 */
public class ThTest {

    // generate a url for paying a two-cent order for WAP Alipay
    @SuppressWarnings("unchecked")
    private static String genTwoCentPayUrlForWapAli(String appOrderId) throws Exception {
        String appId = "101";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String chId = "Th";
        String payMethod = "Ykt";
        String amount = "1.00";
        String carNumber1 = "CS6666666764";
        String carNumber2 = "CS6666666771";
        String cardPass = DESEncrypt.encryptByAES("test", "529998792435834~839177101086836");
        String prodName = "gbtest";
        // TODO magbe not necessary
        String notifyUrl = "http://www.baidu.com";
        // String notifyUrl = "http://222.73.61.40/zfb";
        String autoRedirect = "true";

        @SuppressWarnings("rawtypes")
        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("cardNum", carNumber1 + "~" + carNumber2);
        data.put("cardPass", cardPass);
        data.put("prodName", prodName);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", notifyUrl);
        data.put("userContact", "{\"tel\":\"13450464026\"}");
        data.put("userAddiInfo", "{\"pass\":\"bbb\"}");
        // data.put("gbOper", "a");
        data.put("gbAmount", "100");
        data.put("userIp", "183.60.177.227");
        // data.put("autoRedirect", autoRedirect);
        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\"").append("gbuid").append("\":\"13778000\"").append("}");
        data.put("userId", sb.toString());
        String dataString = new ObjectMapper().writeValueAsString(data);
        System.out.println("【dataString】:" + dataString);
        String sign = SecureHelper.genMd5Sign("1a2b3c4d5e6f7g8h9i0j", dataString);
        data.put("cardPass", URLEncoder.encode((String) data.get("cardPass"), "utf-8"));
        dataString = new ObjectMapper().writeValueAsString(data);
        String url = "http://payplf-tpay-test.gb.com/pay.do?appId=101&data=" + dataString + "&sign=" + sign;
        return url;
    }

    // generate a url for querying order for wap Alipay.
    private static String genQueryUrlForWapAli(String appOrderId) throws Exception {
        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign("1a2b3c4d5e6f7g8h9i0j", dataString);
        String url = "http://payplf-tpay-test.gb.com/query.do?appId=101&data=" + dataString + "&sign=" + sign;
        return url;
    }

    public static void main(String[] args) throws Exception {
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String payUrlForWapAli = genTwoCentPayUrlForWapAli(appOrderId);
        System.out.println("[genTwoCentPayUrlForWapAli]");
        System.out.println(payUrlForWapAli);

        String queryUrlForWapAli = genQueryUrlForWapAli(appOrderId);
        System.out.println("[genQueryUrlForWapAli]");
        System.out.println(queryUrlForWapAli);
        // HttpClientHelper.sendRequest(payUrlForWapAli);
    }
}