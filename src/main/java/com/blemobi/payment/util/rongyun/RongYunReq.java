/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.util.rongyun
 *
 *    Filename:    RongYunReq.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月1日 下午8:41:34
 *
 *    Revision:
 *
 *    2017年3月1日 下午8:41:34
 *
 *****************************************************************/
package com.blemobi.payment.util.rongyun;

import com.alibaba.fastjson.JSON;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.util.DateTimeUtils;

/**
 * @ClassName RongYunReq
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月1日 下午8:41:34
 * @version 1.0.0
 */
public abstract class RongYunReq {
    protected String partnerId = SignHelper.partnerId;
    protected String timeStamp = DateTimeUtils.getDateTime14();
    protected final String seckey = SignHelper.seckey;
    
    /**
     * @return the seckey
     */
    public String getSeckey() {
        return seckey;
    }

    protected String sign;
    

    /**
     * @return the partnerId
     */
    public String getPartnerId() {
        return partnerId;
    }

    
    /**
     * @param partnerId the partnerId to set
     */
    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
    
    /**
     * @return the timeStamp
     */
    public String getTimeStamp() {
        return timeStamp;
    }
    
    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    /**
     * @return the sign
     */
    public String getSign() {
        return sign;
    }
    
    /**
     * @param sign the sign to set
     */
    public void setSign(String sign) {
        this.sign = sign;
    }
    
    public String toString(){
        return JSON.toJSONString(this);
    }
}
