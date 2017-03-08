package com.blemobi.payment.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.payment.core.PaymentManager;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryConfirm;
import com.blemobi.sep.probuf.PaymentProtos.PShuffle;

import lombok.extern.log4j.Log4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Log4j
public class LotteryProcessTest {

    private static final String BASE_URL = "http://192.168.7.245";
    // private static final String BASE_URL = "http://127.0.0.1";
    private static int port = 9014;
    //private static Map<String, String> cookies = new HashMap<String, String>();
    private static List<Cookie> cookies = new ArrayList<>();

    @Before
    public void setup() {
        Cookie cookie0 = new Cookie("uuid", "1470563129739262662");
        Cookie cookie1 = new Cookie("token", "98e7eee14df39598c458fbbfa04843cb");
        cookies.add(cookie0);
        cookies.add(cookie1);
        String[] arg = new String[] {"-env", "local" };
        try {
            PaymentManager.main(arg);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getPath(String url) {
        return BASE_URL + ":" + port + url;
    }

    @Test
    public void test()throws Exception {
        // 筛选粉丝
        /*       
        String uri = "/v1/payment/lottery/shuffle";
        PShuffle.Builder builder = PShuffle.newBuilder();
        List<String> regions = new ArrayList<String>();
        regions.add("CN;0001");
        regions.add("CN;0002");
        builder.setBonus(50).setGender(-1).setRemark("测试").setTitle("三月红包").setTotAmt(500).setWinners(10);
        PShuffle shuffle = builder.build();
        byte[] body = shuffle.toByteArray();
        log.info(getPath(uri));
        try {
            HttpUtils.getInstance().post(getPath(uri), HttpUtils.PROTO_BUF, body, cookies);
        } catch (IOException e) {
            e.printStackTrace();
        }
 */
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("title", "测试").add("winners", "2")
                .add("region", "CN;0001,CN;0002").add("remark", "1234").add("gender", "-1").add("bonus", "1")
                .add("totAmt", "2").build();
        Request request = new Request.Builder().url("http://127.0.0.1:9014/v1/payment/lottery/shuffle").post(formBody).header("Cookie", cookieHeader(cookies))
                .build();
        Response resp = client.newCall(request).execute();
        if (resp.isSuccessful()) {
            System.out.println(resp.body().string());
        } else {
            System.out.println("failed");
        }
        // 确认抽奖
        /*
        PLotteryConfirm.Builder builder = PLotteryConfirm.newBuilder();
        builder.setBonus(10);
        builder.setGender(-1);
        builder.setRemark("测试");
        builder.setTitle("标题");
        builder.setTotAmt(20);
        builder.setWinners(2);
        List<PaymentProtos.PUserBaseEx> usrList = new ArrayList<PaymentProtos.PUserBaseEx>();
        PaymentProtos.PUserBaseEx.Builder ub = PaymentProtos.PUserBaseEx.newBuilder();
        ub.setAmt(10);
        ub.setGender(1);
        ub.setRegion("CN;0001");
        PUserBase.Builder u = PUserBase.newBuilder();
        u.setUUID("11111111");
        ub.setInfo(u.build());
        builder.addAllUserList(usrList); */
        // PShuffle.
        // List<PLocation> list = new ArrayList<PLocation>();
        // PLocation p0 = PLocation.newBuilder().setLocCd("CN:0001").setLocNm("深圳").build();
        // PLocation p1 = PLocation.newBuilder().setLocCd("CN:0002").setLocNm("广州").build();
        // list.add(p0);
        // list.add(p1);
        // PLottery lottery =
        // PLottery.newBuilder().setBonus(20).setTitle("好运气").setTotAmt(100).setType(0).setWinners(5).addAllUuidList(winners).addAllLocs(list).build();
        // byte[] body = lottery.toByteArray();
        // BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, path, null, body,
        // "application/x-protobuf");
        // PMessage message = httpClient.postBodyMethod();
        // assertEquals("POrderPay", message.getType());

        /*
         * StringBuffer path = new StringBuffer("/v1/payment/lottery/create"); List<String> winners = new
         * ArrayList<String>(); winners.add("1468419313301436961"); winners.add("1468419313301436962");
         * winners.add("1468419313301436963"); winners.add("1468419313301436964"); winners.add("1468419313301436965");
         * List<PLocation> list = new ArrayList<PLocation>(); PLocation p0 =
         * PLocation.newBuilder().setLocCd("CN:0001").setLocNm("深圳").build(); PLocation p1 =
         * PLocation.newBuilder().setLocCd("CN:0002").setLocNm("广州").build(); list.add(p0); list.add(p1); PLottery
         * lottery =
         * PLottery.newBuilder().setBonus(20).setTitle("好运气").setTotAmt(100).setType(0).setWinners(5).addAllUuidList(
         * winners).addAllLocs(list).build(); byte[] body = lottery.toByteArray(); BaseHttpClient httpClient = new
         * LocalHttpClient("127.0.0.1", 9014, path, null, body, "application/x-protobuf"); PMessage message =
         * httpClient.postBodyMethod(); assertEquals("POrderPay", message.getType());
         * /**http://127.0.0.1:8081/customers/1 StringBuffer path = new
         * StringBuffer("/v1/payment/lottery/list?startIndex=0&size=5&keywords=好"); BaseHttpClient httpClient = new
         * LocalHttpClient("127.0.0.1", 9014, path, null, null, "application/x-protobuf"); PMessage message =
         * httpClient.getMethod(); assertEquals("PLotteryListRet", message.getType());
         */
        /**
         * StringBuffer path = new
         * StringBuffer("/v1/payment/lottery/detail?lotteryId=520170228286148971922067456&type=1&keywords=ff");
         * BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, path, null, null,
         * "application/x-protobuf"); PMessage message = httpClient.getMethod(); PLotteryDetailRet ret =
         * PLotteryDetailRet.parseFrom(message.getData()); System.out.println(ret.getLocs(0).getLocNm()+
         * "------------"); assertEquals("PLotteryDetailRet", message.getType());
         */

        /**
         * StringBuffer path = new StringBuffer("/v1/payment/lottery/accept"); PAcceptPrize prize =
         * PAcceptPrize.newBuilder().setLotteryId("520170228286148971922067456").build(); byte[] body =
         * prize.toByteArray(); BaseHttpClient httpClient = new LocalHttpClient("192.168.7.245", 9014, path, null, body,
         * "application/x-protobuf"); PMessage message = httpClient.postBodyMethod(); assertEquals("PResult",
         * message.getType());
         */
        /**
         * http://127.0.0.1:8081/customers/1 StringBuffer path = new
         * StringBuffer("/v1/payment/lottery/list?startIndex=0&size=5&keywords=好"); BaseHttpClient httpClient = new
         * LocalHttpClient("127.0.0.1", 9014, path, null, null, "application/x-protobuf"); PMessage message =
         * httpClient.getMethod(); assertEquals("PLotteryListRet", message.getType());
         */

    }
    
    private static String cookieHeader(List<Cookie> cookies) {
        StringBuilder cookieHeader = new StringBuilder();
        for (int i = 0, size = cookies.size(); i < size; i++) {
            if (i > 0) {
                cookieHeader.append("; ");
            }
            Cookie cookie = cookies.get(i);
            cookieHeader.append(cookie.getName()).append('=').append(cookie.getValue());
        }
        return cookieHeader.toString();
    }

}
