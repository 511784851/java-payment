/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.util.rongyun
 *
 *    Filename:    B2CReq.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月1日 下午8:44:44
 *
 *    Revision:
 *
 *    2017年3月1日 下午8:44:44
 *
 *****************************************************************/
package com.blemobi.payment.util.rongyun;

import java.math.BigDecimal;

/**
 * @ClassName B2CReq
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月1日 下午8:44:44
 * @version 1.0.0
 */
public final class B2CReq extends RongYunReq {
    private String custUid;
    private BigDecimal transferAmount;
    private String custOrderno;
    //以下字段非必填
    private String transferDesc;
    private String custMobile;
    private String custNickname;
    private String custImg;
    
    /**
     * @return the custUid
     */
    public String getCustUid() {
        return custUid;
    }
    
    /**
     * @param custUid the custUid to set
     */
    public void setCustUid(String custUid) {
        this.custUid = custUid;
    }
    
    /**
     * @return the transferAmount
     */
    public BigDecimal getTransferAmount() {
        return transferAmount;
    }
    
    /**
     * @param transferAmount the transferAmount to set
     */
    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }
    
    /**
     * @return the custOrderno
     */
    public String getCustOrderno() {
        return custOrderno;
    }
    
    /**
     * @param custOrderno the custOrderno to set
     */
    public void setCustOrderno(String custOrderno) {
        this.custOrderno = custOrderno;
    }
    
    /**
     * @return the transferDesc
     */
    public String getTransferDesc() {
        return transferDesc;
    }
    
    /**
     * @param transferDesc the transferDesc to set
     */
    public void setTransferDesc(String transferDesc) {
        this.transferDesc = transferDesc;
    }
    
    /**
     * @return the custMobile
     */
    public String getCustMobile() {
        return custMobile;
    }
    
    /**
     * @param custMobile the custMobile to set
     */
    public void setCustMobile(String custMobile) {
        this.custMobile = custMobile;
    }
    
    /**
     * @return the custNickname
     */
    public String getCustNickname() {
        return custNickname;
    }
    
    /**
     * @param custNickname the custNickname to set
     */
    public void setCustNickname(String custNickname) {
        this.custNickname = custNickname;
    }
    
    /**
     * @return the custImg
     */
    public String getCustImg() {
        return custImg;
    }
    
    /**
     * @param custImg the custImg to set
     */
    public void setCustImg(String custImg) {
        this.custImg = custImg;
    }
}
