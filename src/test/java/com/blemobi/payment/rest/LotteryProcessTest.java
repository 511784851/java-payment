package com.blemobi.payment.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.payment.core.PaymentManager;

import lombok.extern.log4j.Log4j;

@Log4j
public class LotteryProcessTest {

   private static final String BASE_URL = "http://192.168.7.245";
   //private static final String BASE_URL = "http://127.0.0.1";
    private static int port = 9014;
    private static final String URI = "/v1/payment/lottery/";
    //private static Map<String, String> cookies = new HashMap<String, String>();
    private static List<Cookie> cookies = new ArrayList<>();

    @Before
    public void setup() {
        //        Cookie cookie0 = new Cookie("uuid", "1470823631370937498");
Cookie cookie0 = new Cookie("uuid", "1470564370290423368");
        Cookie cookie1 = new Cookie("token", "GOmF/8UFINqy7NmU7cWhaSoBbTIgMTYyM2M0MTJkNzMyNzM0YmU0YTI3YWM4ZmI2NTBiYmQ=");
        cookies.add(cookie0);
        cookies.add(cookie1);
        String[] arg = new String[] {"-env", "local" };
        try {
         //  PaymentManager.main(arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPath(String url) {
        return BASE_URL + ":" + port + URI + url;
    }

    @Test
    public void test()throws Exception {
        String action = "shuffle";
        Map<String, String> param = new HashMap<String, String>();
        
        /*
        param.put("title", "测试");
        param.put("winners", "2");
        //param.put("region", "CN;0001,CN;0002");
        param.put("remark", "1234");
        param.put("gender", "-1");
        param.put("bonus", "1");
        param.put("totAmt", "2");
        HttpUtils.getInstance().post(getPath(action), param, cookies);
        */
        // 确认抽奖
        action = "confirm";
        param.clear();
        //1470745499613875660_CN;6101, 1470663570819724205_CN;3506, 1470584248405601560_
        /**/
        param.put("title", "三月");
        param.put("winners", "3");
        param.put("regions", "CN;6101,CN;3506");
        param.put("remark", "1234");
        param.put("gender", "-1");
        param.put("bonus", "1");
        param.put("totAmt", "3");
        param.put("uuid", "1470745499613875660,1470663570819724205");
        param.put("genders", "0,0");
        HttpUtils.getInstance().post(getPath(action), param, cookies);
        
        //领奖
        action = "accept";
        param.clear();
        //param.put("lotteryId", "1");
        //HttpUtils.getInstance().post(getPath(action), param, cookies);
        
        //删除历史
        action = "delete";
        param.clear();
        param.put("lotteryId", "520170311839966685359460353,520170311799642233849729033");
        //HttpUtils.getInstance().post(getPath(action), param, cookies);
        
        //列表
//        action = "list";
//        String url = getPath(action);
//        url += "?startIndex=0&keywords=";
//        HttpUtils.getInstance().get(url, cookies);
        //详情
//         action = "detail";
//         String url = getPath(action);
//         url += "?lotteryId=520170228286148971922067456";
//        HttpUtils.getInstance().get(url, cookies);
        
        /*action = "view";
      String url = getPath(action);
      url += "?lotteryId=5201703083259046610411409409";
      System.out.println(url);
      HttpUtils.getInstance().get(url, cookies);
        */
        /*action = "accept";
        param.clear();
        param.put("lotteryId", "52017030832590426610411409409");
        HttpUtils.getInstance().post(getPath(action), param, cookies);*/
    }
}
