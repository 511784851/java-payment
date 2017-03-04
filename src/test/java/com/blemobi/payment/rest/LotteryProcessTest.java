package com.blemobi.payment.rest;

import static org.junit.Assert.*;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
import com.blemobi.payment.core.PaymentManager;
import com.blemobi.sep.probuf.PaymentProtos.PAcceptPrize;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public class LotteryProcessTest {
	private Cookie[] cookies;

	@Before
	public void setup() {
		cookies = new Cookie[2];
		cookies[0] = new Cookie("uuid", "1468419313301436967");
		cookies[1] = new Cookie("token", "98e7eee14df39598c458fbbfa04843cb");

		String[] arg = new String[] { "-env", "local" };
		try {
		    PaymentManager.main(arg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test() throws Exception {
	    /*StringBuffer path = new StringBuffer("/v1/payment/lottery/create");
	    List<String> winners = new ArrayList<String>();
	    winners.add("1468419313301436961");
	    winners.add("1468419313301436962");
	    winners.add("1468419313301436963");
	    winners.add("1468419313301436964");
	    winners.add("1468419313301436965");
	    List<PLocation> list = new ArrayList<PLocation>();
	    PLocation p0 = PLocation.newBuilder().setLocCd("CN:0001").setLocNm("深圳").build();
	    PLocation p1 = PLocation.newBuilder().setLocCd("CN:0002").setLocNm("广州").build();
	    list.add(p0);
	    list.add(p1);
	    PLottery lottery = PLottery.newBuilder().setBonus(20).setTitle("好运气").setTotAmt(100).setType(0).setWinners(5).addAllUuidList(winners).addAllLocs(list).build();
	    byte[] body = lottery.toByteArray();
	    BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, path, null, body, "application/x-protobuf");
	    PMessage message = httpClient.postBodyMethod();
	    assertEquals("POrderPay", message.getType());
	    /**http://127.0.0.1:8081/customers/1
	    StringBuffer path = new StringBuffer("/v1/payment/lottery/list?startIndex=0&size=5&keywords=好");
        BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, path, null, null, "application/x-protobuf");
        PMessage message = httpClient.getMethod();
        assertEquals("PLotteryListRet", message.getType());*/
	    /***/
	    StringBuffer path = new StringBuffer("/customers/2");
        BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 8081, path, null, null, "application/x-protobuf");
        PMessage message = httpClient.getMethod();
        assertEquals("Josh", message.getType());
        
	    /** 
	    StringBuffer path = new StringBuffer("/v1/payment/lottery/detail?lotteryId=520170228286148971922067456&type=1&keywords=ff");
	    BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, path, null, null, "application/x-protobuf");
        PMessage message = httpClient.getMethod();
        
        PLotteryDetailRet ret = PLotteryDetailRet.parseFrom(message.getData());
        System.out.println(ret.getLocs(0).getLocNm()+ "------------");
        assertEquals("PLotteryDetailRet", message.getType());*/
	    /**
	    StringBuffer path = new StringBuffer("/v1/payment/lottery/accept");
	    PAcceptPrize prize = PAcceptPrize.newBuilder().setLotteryId("520170228286148971922067456").build();
        byte[] body = prize.toByteArray();
        BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, path, null, body, "application/x-protobuf");
        PMessage message = httpClient.postBodyMethod();
        assertEquals("PResult", message.getType()); */
        /**http://127.0.0.1:8081/customers/1
        StringBuffer path = new StringBuffer("/v1/payment/lottery/list?startIndex=0&size=5&keywords=好");
        BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, path, null, null, "application/x-protobuf");
        PMessage message = httpClient.getMethod();
        assertEquals("PLotteryListRet", message.getType());*/
        
	}

}
