/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.service
 *
 *    Filename:    LotteryService.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月18日 下午12:06:55
 *
 *    Revision:
 *
 *    2017年2月18日 下午12:06:55
 *
 *****************************************************************/
package com.blemobi.payment.service;

import java.util.List;
import java.util.Map;

import com.blemobi.sep.probuf.PaymentProtos.PLotteryConfirm;
import com.blemobi.sep.probuf.PaymentProtos.PShuffle;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * @ClassName LotteryService
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:06:55
 * @version 1.0.0
 */
public interface LotteryService {
    
    /**
     * @Description 摇奖 
     * @author HUNTER.POON
     * @param uuid
     * @param shuffle
     * @return
     */
    public PMessage shuffleLottery(String uuid, PShuffle shuffle);
    
    /**
     * @Description 确认抽奖 
     * @author HUNTER.POON
     * @param uuid 创建人
     * @param lottery 抽奖包对象
     * @return
     */
    public PMessage createLottery(String uuid, PLotteryConfirm lottery);
    
    /**
     * @Description 中奖者领奖
     * @author HUNTER.POON
     * @param uuid 中奖者uuid
     * @param lotteryId 抽奖包ID
     * @return 
     */
    public PMessage acceptPrize(String uuid, String lotteryId);
    /**
     * @Description 中奖者查看领奖
     * @author HUNTER.POON
     * @param uuid 中奖者uuid
     * @param lotteryId 抽奖包ID
     * @return 
     */
    public PMessage viewPrize(String uuid, String lotteryId);
    
    /**
     * @Description B端删除发奖记录 
     * @author HUNTER.POON
     * @param uuid
     * @param lotteryId
     * @return
     */
    public PMessage delPrize(String uuid, List<String> lotteryId);
    
    /**
     * @Description 抽奖包历史列表 
     * @author HUNTER.POON
     * @param uuid 红包所属者
     * @param startIdx 开始下标
     * @return
     */
    public PMessage lotteryList(String uuid, int startIdx, String keywords);
    
    /**
     * @Description 抽奖包详情 
     * @author HUNTER.POON
     * @param lotteryId 抽奖包ID
     * @return
     */
    public PMessage lotteryDetail(String lotteryId);
    
    /**
     * @Description 查询过期的订单
     * @author HUNTER.POON
     * @return
     */
    public List<Map<String, Object>> getExpireLottery();
    
    /**
     * @Description 将订单改为过期，退款 
     * @author HUNTER.POON
     */
    public void doRefund(Map<String, Object> map);
    
}
