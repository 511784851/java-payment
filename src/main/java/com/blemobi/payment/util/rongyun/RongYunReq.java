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

import com.blemobi.payment.util.DateTimeUtils;

/**
 * @ClassName RongYunReq
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月1日 下午8:41:34
 * @version 1.0.0
 */
public abstract class RongYunReq {
    protected String artnerId;
    protected String timeStamp = DateTimeUtils.getDateTime14();
    protected String sign;
    
    /**
     * @return the artnerId
     */
    public String getArtnerId() {
        return artnerId;
    }
    
    /**
     * @param artnerId the artnerId to set
     */
    public void setArtnerId(String artnerId) {
        this.artnerId = artnerId;
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
}
