/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.access.thrift.integration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guzhi.pay.access.thrift.PayServiceHandler;
import com.guzhi.pay.access.thrift.ThriftServerControl_v05;
import com.guzhi.pay.business.PayService;
import com.guzhi.pay.helper.SecureHelper;
import com.gb.guzhiPay.access.thrift.generated.TReq;
import com.gb.guzhiPay.access.thrift.generated.TResp;
import com.gb.guzhiPay.access.thrift.generated.TguzhiPayService;
import com.gb.guzhiPay.access.thrift.generated.TguzhiPayService.Client;

/**
 * 测试ThriftServer的实际调用
 * 
 * @author administrator
 */
public class ThriftClientTest {

    private final int PORT = 8088;
    private final String LISTEN_IP = "0.0.0.0";
    // private final String CONNTECT_IP = "172.19.103.106";
    private final String CONNTECT_IP = "127.0.0.1";
    // private final String CONNTECT_IP = "222.73.61.40";
    // private final String CONNTECT_IP = "183.61.12.174";

    private PayService payService;
    private ThriftServerControl_v05 thriftServerControl;
    private TguzhiPayService.Client client;
    private TSocket transport;

    @BeforeClass
    public void setUp() throws Exception {
        // startThriftServer();
        initClient();
    }

    @AfterClass
    public void tearDown() {
        // stopThriftServer();
        closeClient();
    }

    @Test
    public void testPay() throws Exception {
        TReq treq = new TReq();
        treq.setAppId("101");
        treq.setData("{\"appOrderId\":\"201305141518300018\",\"chId\":\"zfb\",\"payMethod\":\"gate\",\"appOrderTime\":\"20130514151830\","
                + "\"amount\":\"0.01\",\"prodName\":\"测试\",\"bankId\":\"CMB\",\"notifyUrl\":\"http://www.baidu.com\"}");
        treq.setSign("13b93810740f15a5337ec9f7d9470860");
        TResp result = client.pay(treq);
        System.out.println(result);
        // TODO: more test here, e.g. MD5 verify
    }

    @Test
    public void testStatus() throws Exception {
        String status = client.status();
        Assert.assertTrue(status.contains("version="), "status IF should at least return version info");
    }

    @Test
    public void testQuery() throws Exception {
        TReq treq = new TReq();
        treq.setAppId("101");
        treq.setData("{\"appOrderId\":\"gbEDU-13042600052636\"}");
        treq.setSign("695daf3c19a5a6789b879ad8c5db2c10");
        TResp result = client.query(treq);
        System.out.println(result);
    }

    @Test
    public void testRefund() throws Exception {
        TReq treq = new TReq();
        treq.setAppId("101");
        // treq.setData("{\"appOrderId\":\"1\",\"chId\":\"\",\"amount\":\"1.00\",\"chDealId\":\"2\",\"appRefundTime\":\"201304113183437\","
        // +
        // "\"refundAmount\":\"1.00\",\"refundDesc\":\"desc\",\"orphanRefund\":\"Y\",\"myValue\":\"1.00\"}");
        treq.setData("{\"appOrderId\":\"201304241518300018\",\"chId\":\"Zfb\",\"amount\":\"0.01\",\"chDealId\":\"2013042413817274\","
                + "\"appRefundTime\":\"20130424183510\",\"refundAmount\":\"0.01\",\"refundDesc\":\"desc\",\"orphanRefund\":\"Y\"}");
        treq.setSign("1e77ae9892ce26e13205f484951600dc");
        TResp result = client.refund(treq);
        System.out.println(result);
    }

    /**
     * 测试骏网一卡通支付
     */
    @Test
    public void testJwJkPay() throws Exception {
        TReq treq = new TReq();
        treq.setAppId("101");
        treq.setData("{\"appOrderId\":\"20130506155630\",\"chId\":\"jw\",\"payMethod\":\"jk\",\"appOrderTime\":\"20130506155630\","
                + "\"amount\":\"100\",\"cardNum\":\"1109095023529127\",\"cardPass\":\"1915406663837724\"}");
        treq.setSign("c3627fdd40882b84f4316579498689c3");
        TResp result = client.pay(treq);
        System.out.println(result);
    }

    /**
     * 测试骏网一卡通查询
     */
    @Test
    public void testJwJkQuery() throws Exception {
        TReq treq = new TReq();
        treq.setAppId("101");
        treq.setData("{\"appOrderId\":\"20130506155630\"}");
        treq.setSign("7f9c5dab648f7062590009e775871512");
        TResp result = client.query(treq);
        System.out.println(result);
    }

    /**
     * 测试神州付神州行支付
     */
    @Test
    public void testSzfSzxPay() throws Exception {
        TReq treq = new TReq();
        treq.setAppId("101");
        treq.setData("{\"appOrderId\":\"20130508155630\",\"chId\":\"szf\",\"payMethod\":\"szx\",\"appOrderTime\":\"20130508155630\","
                + "\"amount\":\"1\",\"cardNum\":\"12535091309484432\",\"cardPass\":\"091335543036491925\",\"cardTotalAmount\":\"50\"}");
        treq.setSign("b1eae420c41e5de0ecbbaa3493df9c97");
        TResp result = client.pay(treq);
        System.out.println(result);
    }

    /**
     * 测试神州付神州行查询
     */
    @Test
    public void testSzfSzxQuery() throws Exception {
        TReq treq = new TReq();
        treq.setAppId("101");
        treq.setData("{\"appOrderId\":\"20130508155630\"}");
        treq.setSign("78ee5400ea6761ed738e7ccb212cb710");
        TResp result = client.query(treq);
        System.out.println(result);
    }

    private void stopThriftServer() {
        thriftServerControl.stop();
    }

    private void startThriftServer() throws Exception {
        payService = Mockito.mock(PayService.class);
        PayServiceHandler payServiceHandler = new PayServiceHandler();
        payServiceHandler.setPayService(payService);
        thriftServerControl = new ThriftServerControl_v05();
        thriftServerControl.setBindHost(LISTEN_IP);
        thriftServerControl.setBindPort(PORT);
        thriftServerControl.setPayServiceHandler(payServiceHandler);
        thriftServerControl.start();
        Thread.sleep(2000); // wait server fully start
    }

    private void initClient() throws Exception {
        transport = new TSocket(CONNTECT_IP, PORT);
        transport.setTimeout(500000);
        transport.open();
        // TProtocol protocol = new TBinaryProtocol(transport);
        TProtocol protocol = new TBinaryProtocol(new TFramedTransport(transport));
        client = new Client(protocol);
    }

    private void closeClient() {
        transport.close();
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
        String notifyUrl = "https://payplf-tpay.gb.com/ch/notify/zfbwap";
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

    // generate a url for querying order for wap Alipay.
    private static String genQueryUrlForWapAli(String appOrderId) throws Exception {
        Map data = new HashMap();
        data.put("appOrderId", appOrderId);
        String dataString = new ObjectMapper().writeValueAsString(data);
        String sign = SecureHelper.genMd5Sign("1a2b3c4d5e6f7g8h9i0j", dataString);
        String url = "https://payplf-tpay.gb.com/query.do?appId=101&data=" + dataString + "&sign=" + sign;
        return url;
    }

    // generate a url for paying a two-cent order for zfb gate
    private static String genTwoCentPayUrlForZfbGate() throws Exception {
        String appId = "101";
        String appOrderTime = new SimpleDateFormat("gbgbMMddHHmmss").format(new Date());
        String appOrderId = appOrderTime + "3000";
        String chId = "Zfb";
        String payMethod = "Gate";
        String amount = "0.02";
        String prodName = "测试";
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

    public static void main(String[] args) throws Exception {
        String payUrlForWapAli = genTwoCentPayUrlForWapAli();
        System.out.println("[genTwoCentPayUrlForWapAli]");
        System.out.println(payUrlForWapAli);

        String payUrlForZfbGate = genTwoCentPayUrlForZfbGate();
        System.out.println("[genTwoCentPayUrlForZfbGate]");
        System.out.println(payUrlForZfbGate);

        String appOrderId = "201306071418072000";
        String queryUrlForWapAli = genQueryUrlForWapAli(appOrderId);
        System.out.println("[genQueryUrlForWapAli]");
        System.out.println(queryUrlForWapAli);
        // HttpClientHelper.sendRequest(payUrlForWapAli);
    }
}