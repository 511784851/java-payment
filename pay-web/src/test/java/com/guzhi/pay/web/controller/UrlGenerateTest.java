/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.SecureHelper;
import com.guzhi.pay.helper.StringHelper;

/**
 * 模拟业务线生成支付请求URL。
 * 
 * @author 
 * 
 */
public class UrlGenerateTest {

    private static boolean DEBUG = true;
    private static String APPID = "101";
    private static String KEY = "";
    private static String TPAYURL = "";
    private static String GATEURL = "";
    private static String PWDKEY = "";
    private static String DEBUG_TPAYURL = "https://payplf-tpay-test.gb.com";
    private static String PROD_TPAYURL = "https://payplf-tpay.gb.com";
    private static String DEBUG_GATEURL = "https://payplf-gate-test.gb.com";
    private static String PROD_GATEURL = "https://payplf-gate.gb.com";
    private static String DEBUG_PWDKEY = "test";
    private static String PROD_PWDKEY = "";

    private static Map<String, String> DEBUG_KEYMAP = new HashMap<String, String>();
    private static Map<String, String> PROD_KEYMAP = new HashMap<String, String>();

    static {
        DEBUG_KEYMAP.put("101", "1a2b3c4d5e6f7g8h9i0j");
        DEBUG_KEYMAP.put("104", "ad12334f@!#zfva");
        DEBUG_KEYMAP.put("106", "93F0A76053EF9EB4");
        DEBUG_KEYMAP.put("112", "8H9wcJpgSu5maQKp");
        PROD_KEYMAP.put("101", "");
        PROD_KEYMAP.put("104", "");
        PROD_KEYMAP.put("106", "");
    }

    static {
        if (DEBUG) {
            GATEURL = DEBUG_GATEURL;
            TPAYURL = DEBUG_TPAYURL;
            KEY = DEBUG_KEYMAP.get(APPID);
            PWDKEY = DEBUG_PWDKEY;
        } else {
            GATEURL = PROD_GATEURL;
            TPAYURL = PROD_TPAYURL;
            KEY = PROD_KEYMAP.get(APPID);
            PWDKEY = PROD_PWDKEY;
        }
    }

    private static String PAYFORMART = TPAYURL + "/pay.do?appId=%s&data=%s&sign=%s";
    private static String QUERYFORMAT = TPAYURL + "/query.do?appId=%s&data=%s&sign=%s";

    public static void main(String[] args) throws Exception {
        while (true) {
            Map<String, Object> values = new HashMap<String, Object>();

            values = new ObjectMapper()
                    .readValue(
                            "{\"appId\":\"101\",\"appOrderId\":\"201311010833022000\",\"chId\":\"Zfb\",\"appOrderTime\":\"20131101083302\",\"payMethod\":\"WapApp\",\"amount\":0.02,\"bankId\":\"\",\"statusCode\":\"CODE_PENDING\",\"statusMsg\":\"等待用户支付，或等待支付宝通知\",\"chOrderId\":\"101201311010833022000\",\"chAccountId\":\"2088111050324223\",\"submitTime\":\"20131101163303\",\"lastUpdateTime\":\"20131101163303\",\"payUrl\":\"service=\\\"mobile.securitypay.pay\\\"&partner=\\\"2088111050324223\\\"&_input_charset=\\\"utf-8\\\"&notify_url=\\\"https%3A%2F%2Fpayplf-tpay-test.gb.com%2Fch%2Fnotify%2Fzfbwapapp.do\\\"&out_trade_no=\\\"101201311010833022000\\\"&subject=\\\"欢聚支付平台测试\\\"&payment_type=\\\"1\\\"&seller_id=\\\"payplf@gb.com\\\"&total_fee=\\\"0.02\\\"&sign_type=\\\"RSA\\\"&sign=\\\"SOHlKmN9Dv%2Fm%2B%2BZJ2WK7kGbxebuiWvAvlW4KBD9etIPUgItzld%2BeZbVZMGQ3bWkvjmOI0yuTALpJNxhD6XE7YuRVb55bKotsIYhVIveSDj7UBwuAAySfZYNxEjygQJbivWkqQPZsBxDVtLPA7lqrIpK3PJoWfJjVdDt1z7oS5qo%3D\\\"\"}",
                            Map.class);
            for (String key : values.keySet()) {
                // System.out.println("key:" + key + ",value:" +
                // values.get(key));
            }

            System.out.println(generateAccount());

            // String payUrlFor99BillBalanceForAcd =
            // genOneYuanPayUrlFor99BillBalanceForAcd();
            // System.out.println("[genOneYuanPayUrlFor99BillBalanceForAcd]");
            // System.out.println(payUrlFor99BillBalanceForAcd);

            // String payUrlForWapApp = genTwoCentPayUrlForWapApp();
            // System.out.println("[genTwoCentPayUrlForWapApp]");
            // System.out.println(payUrlForWapApp);

            // String payUrlForZfbGate = genTwoCentPayUrlForZfbGate();
            // System.out.println("[genTwoCentPayUrlForZfbGate]");
            // System.out.println(payUrlForZfbGate);

            // String payUrlForZfbApp = genTwoCentPayUrlForWapApp();
            // System.out.println("[genTwoCentPayUrlForWapApp]");
            // System.out.println(payUrlForZfbApp);

            // String payUrlForZfbWapApp2Gate =
            // genTwoCentPayUrlForWapApp2Gate();
            // System.out.println("[genTwoCentPayUrlForWapApp2Gate]");
            // System.out.println(payUrlForZfbWapApp2Gate);

            // String payUrlForUnionpayWap = genOneCentPayUrlForUnionpayWap();
            // System.out.println("[genOneCentPayUrlForUnionpayWap]");
            // System.out.println(payUrlForUnionpayWap);
            //
            // String payUrlForYeePayGate = genOneCentPayUrlForYeePayGate();
            // System.out.println("[genOneCentPayUrlForYeePayGate]");
            // System.out.println(payUrlForYeePayGate);
            //
            // String payUrlForYeePayCard = genOneYuanPayUrlForYeePayCard();
            // System.out.println("[genOneCentPayUrlForYeePayCard]");
            // System.out.println(payUrlForYeePayCard);
            //
            // String payUrlForZfbGateAndDeposit =
            // genOneCentPayUrlForZfbGateAndDeposit();
            // System.out.println("[genOneCentPayUrlForZfbGateAndDeposit]");
            // System.out.println(payUrlForZfbGateAndDeposit);
            //
            // String payUrlFor99BillGate = genOneCentPayUrlFor99BillGate();
            // System.out.println("[genOneCentPayUrlFor99BillGate]");
            // System.out.println(payUrlFor99BillGate);

            // String payUrlFor99BillBalance =
            // genOneYuanPayUrlFor99BillBalance();
            // System.out.println("[genOneYuanPayUrlFor99BillBalance]");
            // System.out.println(payUrlFor99BillBalance);
            //
            // String payUrlFor99BillGate = genOneCentPayUrlFor99BillGate();
            // System.out.println("[payUrlFor99BillGate]");
            // System.out.println(payUrlFor99BillGate);

            // String payUrlFor99BillSd = genOneYuanPayUrlFor99BillSd();
            // System.out.println("[genOneYuanPayUrlFor99BillSd]");
            // System.out.println(payUrlFor99BillSd);

            // String payUrlFor99BillSzx = genFiftgbuanPayUrlFor99BillSzx();
            // System.out.println("[genFiftgbuanPayUrlFor99BillSzx]");
            // System.out.println(payUrlFor99BillSzx);

            // String payUrlFor99BillWy = genOneYuanPayUrlFor99BillWy();
            // System.out.println("[genOneYuanPayUrlFor99BillWy]");
            // System.out.println(payUrlFor99BillWy);

            // String payUrlForJwJk = genOneYuanPayUrlForJwJk();
            // System.out.println("[genOneYuanPayUrlForJwJk]");
            // System.out.println(payUrlForJwJk);

            // String payUrlForThYkt = genOneYuanPayUrlForTh();
            // System.out.println("[payUrlForThYkt]");
            // System.out.println(payUrlForThYkt);

            // String payUrlForVpaySms = genFiveYuanPayUrlForVpaySms();
            // System.out.println("[payUrlForVpaySms]");
            // System.out.println(payUrlForVpaySms);

            // String payUrlForVpayTel = genOneYuanPayUrlForVpayTel();
            // System.out.println("[payUrlForVpayTel]");
            // System.out.println(payUrlForVpayTel);

            // String payUrlForVpayPCSms = genFiveYuanPayUrlForVpayPCSms();
            // System.out.println("[payUrlForVpayPCSms]");
            // System.out.println(payUrlForVpayPCSms);

            // String payUrlForSzfSzx = genFiftgbuanPayUrlForSzf();
            // System.out.println("[payUrlForSzfSzx]");
            // System.out.println(payUrlForSzfSzx);

            // String payUrlForUnionpayWapApp =
            // genOneCentPayUrlForUnionpayWapApp();
            // System.out.println("[payUrlForUnionpayWapApp]");
            // System.out.println(payUrlForUnionpayWapApp);

            // String payUrlForWapali = genTwoCentPayUrlForWapAli();
            // System.out.println("[payUrlForWapali]");
            // System.out.println(payUrlForWapali);

            // String payUrlForUnionpayWapApp =
            // genOneCentPayUrlForUnionpayWapApp();
            // System.out.println("[payUrlForUnionpayWapApp]");
            // System.out.println(payUrlForUnionpayWapApp);

            // String payUrlForSmsYd = genTwoYuanPayUrlForSmsYd();
            // System.out.println("[payUrlForSmsYd]");
            // System.out.println(payUrlForSmsYd);

            // String payUrlForBroadbandTxtong =
            // genTenYuanPayUrlForBroadBandTxtong();
            // System.out.println("[payUrlForBroadbandTxtong]");
            // System.out.println(payUrlForBroadbandTxtong);

            String appOrderId = "5932T57URHNTE3DNHMRM";
            String queryUrl = genQueryUrl(appOrderId);
            System.out.println("[queryUrl]");
            System.out.println(queryUrl);

            // for (int i = 0; i < 20; i++) {
            // String payUrl = genOneYuanPayUrlForTh();
            // HttpClientHelper.sendRequest(payUrl);
            // System.out.println("[" + i + "] th test:" + payUrl);
            // Thread.sleep(1000);
            // }

            byte[] command = new byte[10];
            System.in.read(command);
            if (new String(command).equals("1")) {
                continue;
            }
        }
        // HttpClientHelper.sendRequest(payUrlForWapAli);
    }

    private static String generateAccount() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("appOrderIds", new ArrayList<String>() {
            {
                // 成功的订单
                add("06K139KZSHSGZSGUM7PK");
                // 失败的订单
                add("41PLX2CYSHDDT5NF02RC");
            }
        });
        String dataString = JsonHelper.toJson(paramMap);
        String sign = SecureHelper.genMd5Sign(KEY, dataString);
        return GATEURL + "/account.do?appId=" + APPID + "&sign=" + sign + "&data=" + dataString;
    }

    @Test
    public void genAuthUrl() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("urlKey", "e00d5bb3bc1a457bbd51cea3a25ff1b5");
        dataMap.put("urlType", "0");
        dataMap.put("confirm", "1");// 同意
        dataMap.put("deductSettings", "0");// 再提醒
        dataMap.put("appOrderId", "20140325172436FSG61S");
        String dataString = "0";
        try {
            dataString = new ObjectMapper().writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String sign = SecureHelper.genMd5Sign(KEY, dataString);
        String authUrlForgbWapBalance = TPAYURL + "/ch/util/authgb.do?appId=" + APPID + "&sign=" + sign + "&data="
                + dataString;
        System.out.println(authUrlForgbWapBalance);
    }

    @Test
    public void genOneYuanPayUrlForgbWapBalance() {
        Map<String, Object> dataMap = genRequestMap();
        dataMap.put("chId", "gb");
        dataMap.put("payMethod", "Wapbalance");
        String dataString = "1";
        try {
            dataString = new ObjectMapper().writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String sign = SecureHelper.genMd5Sign(KEY, dataString);
        String payUrlForgbWapBalance = TPAYURL + "/pay.do?appId=" + APPID + "&sign=" + sign + "&data=" + dataString;
        System.out.println(payUrlForgbWapBalance);
    }

    private static Map<String, Object> genRequestMap() {
        final String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        return new HashMap<String, Object>() {
            private static final long serialVersionUID = 236170521263219798L;
            {
                put("appOrderTime", appOrderTime);
                put("appOrderId", appOrderTime + RandomStringUtils.randomAlphanumeric(6).toUpperCase());
                put("amount", "1");
                put("prodName", "测试1元商品名");
                put("prodId", "测试1元商品ID");
                put("prodDesc", "测试1元商品描述");
                put("userIp", "183.60.177.228");
                put("userId", "{\"gbuid\":\"50012910\"}");
                put("userContact", "{\"tel\":\"15013033426\"}");
            }
        };
    }

    @Test
    public void tenpayWeixin() {
        PayOrder payOrder = new PayOrder();
        payOrder.setChId("Tenpay");
        payOrder.setPayMethod("Weixin");
        payOrder.setAmount(new BigDecimal("200.00"));
        payOrder.setgbAmount(new BigDecimal("200.00"));
        payOrder.setAutoRedirect("false");
        System.out.println(buildRequestUrl(payOrder));
    }

    @Test
    public void tenpayGate() {
        PayOrder payOrder = new PayOrder();
        payOrder.setChId("Tenpay");
        payOrder.setPayMethod("Gate");
        payOrder.setBankId("CMB");
        payOrder.setAmount(new BigDecimal("0.01"));
        payOrder.setgbAmount(new BigDecimal("0.01"));
        payOrder.setAutoRedirect("false");
        System.out.println(buildRequestUrl(payOrder));
    }

    @Test
    public void tenpagbalance() {
        PayOrder payOrder = new PayOrder();
        payOrder.setChId("Tenpay");
        payOrder.setPayMethod("Balance");
        payOrder.setAmount(new BigDecimal("0.01"));
        payOrder.setgbAmount(new BigDecimal("0.01"));
        payOrder.setAutoRedirect("false");
        System.out.println(buildRequestUrl(payOrder));
    }

    /**
     * @return
     * @throws JsonProcessingException
     */
    private static String genTenYuanPayUrlForBroadBandTxtong() throws JsonProcessingException {
        String passwdKey = "test";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Broadband";
        String payMethod = "txtong";
        String amount = "10";
        String prodName = "天下通10元";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        Map<String, String> data = new HashMap<String, String>();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodName", prodName);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", "{\"gbuid\":\"50012910\"}");
        data.put("cardNum", "312500096430800");
        data.put("cardPass", DESEncrypt.encryptByAES(passwdKey, "78583590"));
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(KEY, dataString);
        String url = String.format(PAYFORMART, APPID, StringHelper.encode(dataString, "utf8"), sign);
        return url;
    }

    /**
     * @return
     */
    private static String genFiveYuanPayUrlForVpayPCSms() throws Exception {
        // For production
        String appId = "106";
        String key = "dlsuowo209";
        String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        // String appId = "101";
        // String key = "1a2b3c4d5e6f7g8h9i0j";
        // String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";

        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Vpay";
        String payMethod = "PcSms";
        // vpayPcsmsAdapter
        String amount = "5";
        String prodName = "VPaySms测试5元";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String autoRedirect = "false";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodName", prodName);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", "{\"gbuid\":\"50012910\"}");
        data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    public static void generateManRealPayUrlForUnionpayWap(int count) throws Exception {
        String formatUrl = "http://upwap.bypay.cn/sgw/068604d88f56f7527ebab9cb578b9f68";
        String formatUrlPrefix = "http://upwap.bypay.cn/sgw/";
        while (count-- > 0) {
            String payUrl = genOneCentPayUrlForUnionpayWap();
            String response = HttpClientHelper.sendRequest(payUrl);
            int startIndex = response.indexOf(formatUrlPrefix);
            int endIndex = startIndex + formatUrl.length();
            String realPayUrl = response.substring(startIndex, endIndex);
            System.out.println(payUrl);
            System.out.println(realPayUrl.substring(0, formatUrlPrefix.length()) + " "
                    + realPayUrl.substring(formatUrlPrefix.length()));
            Thread.sleep(1000);
        }
    }

    // generate a url for paying a two-cent order for WAP Alipay
    private static String genTwoCentPayUrlForWapAli() throws Exception {
        String appId = "101";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Zfb";
        String payMethod = "WapAlipay";
        String amount = "0.02";
        String prodName = "测试";
        // TODO magbe not necessary
        String bankId = "CMB";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodName", prodName);
        data.put("bankId", bankId);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign("1a2b3c4d5e6f7g8h9i0j", dataString);
        String url = "https://payplf-tpay-test.gb.com/pay.do?appId=" + appId + "&data=" + dataString + "&sign=" + sign;
        return url;
    }

    // generate a url for querying order.
    private static String genQueryUrl(String appOrderId) throws Exception {
        String appId = APPID;
        String key = KEY;
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("appOrderId", appOrderId);
        // data.put("appId", APPID);
        // data.put("chId", "Kq");
        // data.put("payMethod", "Sd");
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = String.format(QUERYFORMAT, appId, dataString, sign);
        return url;
    }

    // generate a url for paying a two-cent order for WAP Alipay
    private static String genTwoCentPayUrlForWapApp() throws Exception {
        // For production
        String appId = "104";
        String key = "20lk3408213lj2";
        String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        // String appId = "106";
        // String key = "93F0A76053EF9EB4";
        // String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Zfb";
        String payMethod = "WapApp";
        String amount = "0.02";
        String prodName = "欢聚时代测试商品";
        // TODO magbe not necessary
        String notifyUrl = "http://www.guzhi.com";
        String autoRedirect = "true";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodName", prodName);
        data.put("notifyUrl", notifyUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", "{\"gbuid\":\"50012910\"}");
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    private static String genTwoCentPayUrlForWapApp2Gate() throws Exception {
        String appId = "104";
        String chId = "Zfb";
        String payMethod = "Wapapp";
        String amount = "0.10";
        String prodId = "0.1YCoin";
        String prodName = "欢聚时代测试商品";
        String payUnit = "RMB";

        Map data = new HashMap();
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("payUnit", payUnit);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        Map user = new HashMap();
        user.put("gbuid", "506280165");
        String userIdString = new ObjectMapper().writeValueAsString(user);
        data.put("userId", user);
        String dataString = new ObjectMapper().writeValueAsString(data);
        // String key = "1a2b3c4d5e6f7g8h9i0j";
        String key = "ad12334f@!#zfva";
        String sign = SecureHelper.genMd5Sign(key, dataString);

        // String url = "http://payplf-gate-test.gb.com/pay.do?appId=" + appId +
        // "&data="
        // + StringHelper.encodeStr(dataString) + "&sign=" + "test";

        String url = "https://payplf-gate-test.gb.com/ut/wap/recharge.do?appId=" + appId + "&sign=test" + "&data="
                + dataString;
        return url;
    }

    // generate a url for paying a two-cent order for zfb gate
    private static String genTwoCentPayUrlForZfbGate() throws Exception {
        // For production
        // String appId = "106";
        // String key = "dlsuowo209";
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        String appId = "101";
        String key = "1a2b3c4d5e6f7g8h9i0j";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";

        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + getCode();
        String chId = "Zfb";
        String payMethod = "Balance";
        String amount = "0.01";
        String prodName = "测试";
        String bankId = "CMB";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodName", prodName);
        data.put("bankId", bankId);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", "{\"gbuid\":\"50012910\"}");
        // data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    private static String genLimitPayUrlForZfbGate() throws Exception {
        String appId = "101";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "3000";
        String chId = "Zfb";
        String payMethod = "Gate";
        String amount = "2";
        String prodName = "测试";
        String bankId = "";
        String notifyUrl = "http://www.guzhi.com";
        String returnUrl = "http://www.gb.com";
        String autoRedirect = "false";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodName", prodName);
        data.put("bankId", bankId);
        data.put("notifyUrl", notifyUrl);
        data.put("userIp", "183.60.177.228");
        data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign("1a2b3c4d5e6f7g8h9i0j", dataString);
        String url = "https://payplf-tpay-test.gb.com/pay.do?appId=101&data=" + dataString + "&sign=" + sign;
        return url;
    }

    private static String genLimitPayUrlForgbBalance() throws Exception {
        String appId = "101";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "3000";
        String chId = "gb";
        String payMethod = "Balance";
        String amount = "200001";
        String prodName = "TEST";
        // TODO magbe not necessary
        String bankId = "CMB";
        String notifyUrl = "https://payplf-tpay.gb.com/ch/notify/zfb";
        String autoRedirect = "true";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodName", prodName);
        data.put("bankId", bankId);
        data.put("notifyUrl", notifyUrl);
        data.put("userIp", "183.60.177.228");
        // data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign("1a2b3c4d5e6f7g8h9i0j", dataString);
        String url = "https://payplf-tpay.gb.com/pay.do?appId=101&data=" + dataString + "&sign=" + sign;
        return url;
    }

    // generate a url for paying a two-cent order for unionpay wap
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static String genOneCentPayUrlForUnionpayWap() throws Exception {
        // For production
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        String appId = APPID;
        String key = KEY;
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + getCode();
        String chId = "Unionpay";
        String payMethod = "Wap";
        String amount = "0.01";
        String prodName = "测试";
        // TODO magbe not necessary
        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("returnUrl", "http://www.gb.com");
        data.put("notifyUrl", "http://www.guzhi.com");
        data.put("prodName", prodName);
        data.put("userIp", "183.60.177.228");
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    // generate a url for paying a one-yuan order for YingHuaXunFang vpay tel
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String genOneYuanPayUrlForVpayTel() throws Exception {

        // For production
        String appId = "106";
        String key = "93F0A76053EF9EB4";
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";

        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Vpay";
        String payMethod = "Tel";
        String amount = "1";
        String prodName = "VPayTel测试1元";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String autoRedirect = "false";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodName", prodName);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", "{\"gbuid\":\"50012910\"}");
        data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    // generate a url for paying a one-yuan order for YeePay Gate
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String genOneCentPayUrlForYeePayGate() throws Exception {
        String appId = "101";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "YeePay";
        String payMethod = "Gate";
        String amount = "0.02";
        String prodId = "多玩ID";
        String prodName = "多玩Name";
        String prodDesc = "多玩Desc";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.gb.com";
        String autoRedirect = "true";
        String gbOper = "a";
        String gbAmount = "0.01";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", "{\"gbuid\":\"50012910\"}");
        // data.put("bankId", "CMB");
        data.put("gbOper", gbOper);
        data.put("gbAmount", gbAmount);
        // data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign("1a2b3c4d5e6f7g8h9i0j", dataString);
        String url = "https://payplf-tpay-test.gb.com/pay.do?appId=" + appId + "&data=" + dataString + "&sign=" + sign;
        return url;
    }

    // generate a url for paying a one-yuan order for YeePay Card
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String genOneYuanPayUrlForYeePayCard() throws Exception {

        // For test
        String appId = "101";
        String key = "1a2b3c4d5e6f7g8h9i0j";
        String passwdKey = "test";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";

        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "YeePay";
        String payMethod = "Szx";
        String amount = "1.00";
        String prodId = "多玩G币ID";
        String prodName = "多玩G币Name";
        String prodDesc = "多玩G币Desc";
        String cardTotalAmount = "50.00";
        String notifyUrl = "http://www.gb.com";
        String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("cardTotalAmount", cardTotalAmount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        data.put("autoRedirect", autoRedirect);
        data.put("cardNum", "1111111111112111234");
        data.put("cardPass", DESEncrypt.encryptByAES(passwdKey, "111112111111111212213"));
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encode(dataString, "utf8") + "&sign=" + sign;
        return url;
    }

    private static String genOneCentPayUrlForZfbGateAndDeposit() throws Exception {
        // String appId = "106";
        // String key = "93F0A76053EF9EB4";
        String appId = "107";
        String key = "1a2b3c4d5e6f7g8h9i0j";

        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "3000";
        String chId = "Zfb";
        String payMethod = "Gate";
        String amount = "0.01";
        String prodName = "测试";
        // TODO magbe not necessary
        String bankId = "ABC";
        String notifyUrl = "http://www.gb.com";
        String autoRedirect = "true";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map<String, String> data = new HashMap<String, String>();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodName", prodName);
        data.put("bankId", bankId);
        data.put("notifyUrl", notifyUrl);
        data.put("userIp", "183.60.177.228");
        data.put("gbOper", "ad");
        data.put("gbAmount", "0.01");
        data.put("userId", userId);
        data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = "https://payplf-tpay-test.gb.com/pay.do?appId=" + appId + "&data="
                + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    private static String genOneCentPayUrlFor99BillGate() throws IOException {
        // For production
        // String appId = "106";
        // String key = "dlsuowo209";
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        String appId = "106";
        String key = "93F0A76053EF9EB4";
        // String appId = "101";
        // String key = "1a2b3c4d5e6f7g8h9i0j";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Kq";
        String payMethod = "Gate";
        String bankId = "CMB";
        String amount = "0.01";
        String prodId = "多玩测试";
        String prodName = "多玩测试";
        String prodDesc = "多玩测试";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("bankId", bankId);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    private static String genOneYuanPayUrlFor99BillBalanceForAcd() throws IOException {
        String appId = "101";
        String key = "1a2b3c4d5e6f7g8h9i0j";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Kq";
        String payMethod = "Balance";
        String bankId = "CMB";
        String amount = "0.01";
        String prodId = "多玩测试";
        String prodName = "多玩测试";
        String prodDesc = "多玩测试";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("bankId", bankId);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    private static String genOneYuanPayUrlFor99BillBalance() throws IOException {
        // String appId = "106";
        // String key = "93F0A76053EF9EB4";
        String appId = "101";
        String key = "1a2b3c4d5e6f7g8h9i0j";
        // For production
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";
        // For test
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Kq";
        String payMethod = "Balance";
        String amount = "1.00";
        String prodId = "G币ID";
        String prodName = "G币Name";
        String prodDesc = "G币Desc";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        // data.put("gbOper", "acd");
        // data.put("gbAmount", "1.00");
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    private static String genOneYuanPayUrlFor99BillSd() throws IOException {
        String appId = "106";
        String key = "93F0A76053EF9EB4";
        // For production
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        // String appId = "101";
        // String key = "1a2b3c4d5e6f7g8h9i0j";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Kq";
        String payMethod = "Sd";
        String amount = "1.00";
        String prodId = "ID";
        String prodName = "NAME";
        String prodDesc = "DESC";
        // String prodId = "DuowanID";
        // String prodName = "DuowanName";
        // String prodDesc = "DuowanDesc";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encode(dataString, "utf8") + "&sign=" + sign;
        return url;
    }

    private static String genFiftgbuanPayUrlFor99BillSzx() throws IOException {
        String appId = "106";
        String key = "93F0A76053EF9EB4";
        String passwdKey = "639AE95A7D0";
        // For production
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        // String appId = "101";
        // String key = "1a2b3c4d5e6f7g8h9i0j";
        // String passwdKey = "test";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";

        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Kq";
        String payMethod = "Szx";
        String amount = "50.00";
        String prodId = "DuowanID";
        String prodName = "DuowanName";
        String prodDesc = "DuowanDesc";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        data.put("autoRedirect", autoRedirect);
        data.put("cardNum", "12345678901234545");
        data.put("cardPass", DESEncrypt.encryptByAES(passwdKey, "031012345678912345"));
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encode(dataString, "utf8") + "&sign=" + sign;
        return url;
    }

    private static String genFiftgbuanPayUrlForSzf() throws IOException {
        // For test
        String appId = "101";
        String key = "1a2b3c4d5e6f7g8h9i0j";
        String passwdKey = "test";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";

        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Szf";
        String payMethod = "szx";
        String amount = "50.00";
        String cardTotalAmount = "50";
        String prodId = "DuowanID";
        String prodName = "DuowanName";
        String prodDesc = "DuowanDesc";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String autoRedirect = "";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        data.put("gbAmount", "24.00");
        data.put("gbOper", "a");
        // data.put("autoRedirect", autoRedirect);
        data.put("cardNum", "1212334567890231234566");
        data.put("cardPass", DESEncrypt.encryptByAES(passwdKey, "03101234567234438912345"));
        data.put("cardTotalAmount", cardTotalAmount);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encode(dataString, "utf8") + "&sign=" + sign;
        return url;
    }

    private static String genOneYuanPayUrlFor99BillWy() throws IOException {
        // For production
        // String appId = "106";
        // String key = "93F0A76053EF9EB4";
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        String appId = "101";
        String key = "1a2b3c4d5e6f7g8h9i0j";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        // String appOrderId = "10620130904152406";
        String chId = "Kq";
        String payMethod = "Wy";
        String amount = "10.00";
        String prodId = "gbID";
        String prodName = "gbName";
        String prodDesc = "gbDesc";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        // String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        // data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + dataString + "&sign=" + sign;
        return url;
    }

    private static String genOneYuanPayUrlForJwJk() throws IOException {
        String appId = "101";
        String key = "1a2b3c4d5e6f7g8h9i0j";
        String passwdKey = "test";
        // For production
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Jw";
        String payMethod = "Jk";
        String amount = "1.00";
        String prodId = "多玩ID";
        String prodName = "多玩Name";
        String prodDesc = "多玩Desc";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        data.put("autoRedirect", autoRedirect);
        data.put("cardTotalAmount", "1.00");
        data.put("cardNum", "1304280039293021");
        data.put("cardPass", DESEncrypt.encryptByAES(passwdKey, "1923598059805080"));
        data.put("gbOper", "a");
        data.put("gbAmount", "0.90");
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    private static String genOneYuanPayUrlForTh() throws IOException {
        // String appId = "106";
        // String key = "93F0A76053EF9EB4";
        // String passwdKey = "639AE95A7D0";
        // For production
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        String appId = "101";
        String key = "1a2b3c4d5e6f7g8h9i0j";
        String passwdKey = "test";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";

        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Th";
        String payMethod = "Ykt";
        String amount = "1.00";
        String prodId = "多玩G币ID";
        String prodName = "多玩G币Name";
        String prodDesc = "多玩G币Desc";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        data.put("cardTotalAmount", "10.00~10.00");
        data.put("cardNum", "FD1003602053~FD1003602079");
        data.put("cardPass", DESEncrypt.encryptByAES(passwdKey, "743789996228832~151912393968569"));
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + StringHelper.encodeStr(dataString) + "&sign=" + sign;
        return url;
    }

    private static String genFiveYuanPayUrlForVpaySms() throws JsonProcessingException {
        String appId = "101";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Vpay";
        String payMethod = "Sms";
        String amount = "5.00";
        String prodId = "多玩G币ID";
        String prodName = "多玩G币Name";
        String prodDesc = "多玩G币Desc";
        String notifyUrl = "http://www.gb.com";
        String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userContact", "{\"tel\":\"15013033426\"}");
        data.put("userId", userId);
        data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign("1a2b3c4d5e6f7g8h9i0j", dataString);
        String url = "https://payplf-tpay-test.gb.com/pay.do?appId=" + appId + "&data=" + dataString + "&sign=" + sign;
        return url;
    }

    private static String genOneCentPayUrlForUnionpayWapApp() throws IOException {
        // For production
        // String appId = "106";
        // String key = "93F0A76053EF9EB4";
        // String urlPrefix = "https://payplf-tpay.gb.com/pay.do?appId=";

        // For test
        String appId = "104";
        String key = "ad12334f@!#zfva";
        // String appId = "101";
        // String key = "1a2b3c4d5e6f7g8h9i0j";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        // String appOrderId = "201311220724052000";
        // String appOrderId = "10620130904152406";
        String chId = "Unionpay";
        String payMethod = "WapApp";
        String amount = "0.01";
        String prodId = "gbID";
        String prodName = "gbName";
        String prodDesc = "gbDesc";
        String notifyUrl = "http://www.gb.com";
        String returnUrl = "http://www.guzhi.com";
        // String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("returnUrl", returnUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userId", userId);
        // data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + dataString + "&sign=" + sign;
        return url;
    }

    private static String genTwoYuanPayUrlForSmsYd() throws JsonProcessingException {
        // For test
        String appId = "106";
        String key = "93F0A76053EF9EB4";
        String urlPrefix = "https://payplf-tpay-test.gb.com/pay.do?appId=";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "2000";
        String chId = "Sms";
        String payMethod = "Yd";
        String amount = "2.00";
        String prodId = "多玩G币ID";
        String prodName = "多玩G币Name";
        String prodDesc = "多玩G币Desc";
        String notifyUrl = "http://www.gb.com";
        String autoRedirect = "false";
        String userId = "{\"gbuid\":\"50012910\"}";

        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", chId);
        data.put("payMethod", payMethod);
        data.put("appOrderTime", appOrderTime);
        data.put("amount", amount);
        data.put("prodId", prodId);
        data.put("prodName", prodName);
        data.put("prodDesc", prodDesc);
        data.put("notifyUrl", notifyUrl);
        data.put("userIp", "183.60.177.228");
        data.put("userContact", "{\"tel\":\"15013033426\"}");
        data.put("userId", userId);
        data.put("autoRedirect", autoRedirect);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign(key, dataString);
        String url = urlPrefix + appId + "&data=" + dataString + "&sign=" + sign;
        return url;
    }

    private static String getCode() {
        return String.valueOf(RandomUtils.nextInt() % 1000);
    }

    private String buildRequestUrl(PayOrder payOrder) {
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        data.put("chId", payOrder.getChId());
        data.put("payMethod", payOrder.getPayMethod());
        data.put("appOrderTime", appOrderTime);
        data.put("amount", payOrder.getAmount() == null ? "1.00" : payOrder.getAmount().toPlainString());
        data.put("gbAmount", payOrder.getgbAmount() == null ? "1.00" : payOrder.getgbAmount().toPlainString());
        data.put("prodId", getDefaultStringIfBlank(payOrder.getProdId(), "1gb"));
        data.put("prodName", getDefaultStringIfBlank(payOrder.getProdName(), "1元G币测试商品名称"));
        data.put("prodDesc", getDefaultStringIfBlank(payOrder.getProdDesc(), "1元G币测试商品描述"));
        // data.put("prodName", getDefaultStringIfBlank(payOrder.getProdName(),
        // "1gbName"));
        // data.put("prodDesc", getDefaultStringIfBlank(payOrder.getProdDesc(),
        // "1gbDesc"));
        data.put("userIp", getDefaultStringIfBlank(payOrder.getUserId(), "183.60.177.228"));
        data.put("userContact", getDefaultStringIfBlank(payOrder.getUserId(), "{\"tel\":\"15013033426\"}"));
        data.put("userId", getDefaultStringIfBlank(payOrder.getUserId(), "{\"gbuid\":\"50012910\"}"));
        data.put("autoRedirect", getDefaultStringIfBlank(payOrder.getAutoRedirect(), "false"));
        if (StringUtils.isNotBlank(payOrder.getBankId())) {
            data.put("bankId", payOrder.getBankId());
        }
        String dataString = "";
        try {
            dataString = new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String sign = SecureHelper.genMd5Sign(KEY, dataString);
        String url = TPAYURL + "/pay.do?appId=" + APPID + "&data=" + dataString + "&sign=" + sign;
        return url;
    }

    private String getDefaultStringIfBlank(String target, String defaultString) {
        return StringUtils.isBlank(target) ? defaultString : target;
    }
}