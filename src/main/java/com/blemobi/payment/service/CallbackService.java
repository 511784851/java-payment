/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.service
 *
 *    Filename:    CallbackService.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月27日 下午5:27:03
 *
 *    Revision:
 *
 *    2017年2月27日 下午5:27:03
 *
 *****************************************************************/
package com.blemobi.payment.service;


/**
 * @ClassName CallbackService
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年2月27日 下午5:27:03
 * @version 1.0.0
 */
public interface CallbackService {
    public Boolean paySucc(String amount, long time, String ordNo, String recUid, String corgOrdId, String corgSts, String corgMsg);
}
