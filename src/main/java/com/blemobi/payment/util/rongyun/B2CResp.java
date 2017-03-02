/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.util.rongyun
 *
 *    Filename:    B2CResp.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月1日 下午8:48:39
 *
 *    Revision:
 *
 *    2017年3月1日 下午8:48:39
 *
 *****************************************************************/
package com.blemobi.payment.util.rongyun;


/**
 * @ClassName B2CResp
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月1日 下午8:48:39
 * @version 1.0.0
 */
public final class B2CResp extends RongYunResp {
    private String jrmfOrderno;

    
    /**
     * @return the jrmfOrderno
     */
    public String getJrmfOrderno() {
        return jrmfOrderno;
    }

    
    /**
     * @param jrmfOrderno the jrmfOrderno to set
     */
    public void setJrmfOrderno(String jrmfOrderno) {
        this.jrmfOrderno = jrmfOrderno;
    }
}
