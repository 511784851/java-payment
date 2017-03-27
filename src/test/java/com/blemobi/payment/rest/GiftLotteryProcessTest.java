package com.blemobi.payment.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.payment.util.DateTimeUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class GiftLotteryProcessTest {

    private static final String BASE_URL = "http://192.168.7.245";
    //private static final String BASE_URL = "http://127.0.0.1";
    private static int port = 9014;
    private static final String URI = "/v1/payment/giftlottery/";
    private static List<Cookie> cookies = new ArrayList<>();
    @Before
    public void setup() {
        Cookie cookie0 = new Cookie("uuid", "1470563129739262662");
        Cookie cookie1 = new Cookie("token",
                "GOmF/8UFINqy7NmU7cWhaSoBbTIgMTYyM2M0MTJkNzMyNzM0YmU0YTI3YWM4ZmI2NTBiYmQ=");
        cookies.add(cookie0);
        cookies.add(cookie1);
        String[] arg = new String[] {"-env", "test" };
        try {
            //PaymentManager.main(arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPath(String url) {
        return BASE_URL + ":" + port + URI + url;
    }

    @Test
    public void test() throws Exception {
        try{
        String action = "shuffle";
        Map<String, String> param = new HashMap<String, String>();
        param.put("title", "测试");
        param.put("overdueTm", DateTimeUtils.calcTime(TimeUnit.DAYS, 1) + "");
        param.put("winners", "4");
        param.put("locCnt", "2");
        param.put("regions", "CN;3701,CN;6540");
        param.put("remark", "1234");
        param.put("gender", "-1");
        param.put("giftNm", "IPHONE7,U盘");
        param.put("giftCnt", "1,3");
        //HttpUtils.getInstance().post(getPath(action), param, cookies);

        // 确认抽奖
        action = "confirm";
        param.clear();
        param.put("title", "测试1");
        param.put("overdueTm", DateTimeUtils.calcTime(TimeUnit.HOURS, 12) + "");
        param.put("winners", "4");
        param.put("locCnt", "2");
        param.put("regions", "CN;3701,CN;6540");
        param.put("remark", "1234");
        param.put("gender", "-1");
        param.put("giftNm", "xx,33盘");
        param.put("giftCnt", "1,3");
        param.put("uuidList", "1481558064279125258,1481558242396559459,1470753921857608048,1481558594837018113");
        param.put("regionList", "CN;6540,CN;3701,CN;3701,CN;6540");
        param.put("genderList", "0,0,1,0");
        HttpUtils.getInstance().post(getPath(action), param, cookies);

        // 查看抽奖
        action = "view";
        param.clear();
        String url = getPath(action);
        url += "?lotteryId=5201703241491483880945963013";
        //HttpUtils.getInstance().get(url, cookies);
        
        //accept
        action = "accept";
        param.clear();
        param.put("lotteryId", "5201703241491483880945963013");
        //HttpUtils.getInstance().post(getPath(action), param, cookies);
        
        // 列表
        action = "list";
        url = getPath(action);
        url += "?startIndex=0&keywords=";
        //HttpUtils.getInstance().get(url, cookies);
        // 详情
        action = "detail";
        url = getPath(action);
        url += "?lotteryId=5201703242002794308696100870";
        //HttpUtils.getInstance().get(url, cookies);
        
        // 删除历史
        action = "delete";
        param.clear();
        param.put("lotteryId", "5201703242002794308696100870");
        //HttpUtils.getInstance().post(getPath(action), param, cookies);
        //编辑资料
        action = "edit";
        param.clear();
        param.put("lotteryId", "5201703241491483880945963013");
        param.put("uuid", "1481558064279125258");
        //param.put("rcvNm", "张三2x");
        param.put("rcvAddr", "深圳");
        param.put("rcvPhone", "188xxxxxxxx");
        //param.put("rcvEmail", "x");
        param.put("rcvRemark", "ddddsfsfsdfs");
        //HttpUtils.getInstance().post(getPath(action), param, cookies);
        
        
        //一键提醒
        action = "remind";
        param.clear();
        param.put("lotteryId", "5201703241491483880945963013");
        param.put("uuidList", "1481558242396559459,1481558064279125258");
        //HttpUtils.getInstance().post(getPath(action), param, cookies);
        
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
