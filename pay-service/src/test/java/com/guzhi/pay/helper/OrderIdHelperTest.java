package com.guzhi.pay.helper;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.guzhi.pay.helper.OrderIdHelper;

public class OrderIdHelperTest {

    String appId = "101";
    String appOrderId = "abfaejewoifwefh";

    @Test
    public void genChOrderId() {
        String chOrderId = OrderIdHelper.genChOrderId(appId, appOrderId);
        Assert.assertEquals(chOrderId, appId + appOrderId);
    }
    
    @Test
    public void getAppId(){
        String chOrderId = OrderIdHelper.genChOrderId(appId, appOrderId);
        Assert.assertEquals(OrderIdHelper.getAppId(chOrderId), appId);
    }
    
    @Test
    public void getAppOrderId(){
        String chOrderId = OrderIdHelper.genChOrderId(appId, appOrderId);
        Assert.assertEquals(OrderIdHelper.getAppOrderId(chOrderId), appOrderId);
    }
    
    @Test
    public void getIdError(){
        Assert.assertEquals(OrderIdHelper.getAppId(""), "");
        Assert.assertEquals(OrderIdHelper.getAppId(null), "");
        Assert.assertEquals(OrderIdHelper.getAppId("123"), "");
        
        Assert.assertEquals(OrderIdHelper.getAppOrderId(""), "");
        Assert.assertEquals(OrderIdHelper.getAppOrderId(null), "");
        Assert.assertEquals(OrderIdHelper.getAppOrderId("123"), "");
    }
}
