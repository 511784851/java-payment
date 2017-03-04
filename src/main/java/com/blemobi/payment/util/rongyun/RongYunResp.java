/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.util.rongyun
 *
 *    Filename:    RongYunResp.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月1日 下午8:43:13
 *
 *    Revision:
 *
 *    2017年3月1日 下午8:43:13
 *
 *****************************************************************/
package com.blemobi.payment.util.rongyun;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName RongYunResp
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月1日 下午8:43:13
 * @version 1.0.0
 */
public abstract class RongYunResp {
    protected String respstat;
    protected String respmsg;
    
    /**
     * @return the respstat
     */
    public String getRespstat() {
        return respstat;
    }
    
    /**
     * @param respstat the respstat to set
     */
    public void setRespstat(String respstat) {
        this.respstat = respstat;
    }
    
    /**
     * @return the respmsg
     */
    public String getRespmsg() {
        return respmsg;
    }
    
    /**
     * @param respmsg the respmsg to set
     */
    public void setRespmsg(String respmsg) {
        this.respmsg = respmsg;
    }
    
    public String toString(){
        return JSON.toJSONString(this);
    }
}
