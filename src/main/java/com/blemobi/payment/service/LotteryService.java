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

import javax.ws.rs.CookieParam;

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
     * @Description B端删除发奖记录 
     * @author HUNTER.POON
     * @param uuid
     * @param lotteryId
     * @return
     */
    public PMessage delPrize(String uuid, String lotteryId);
    
    /**
     * @Description 抽奖包历史列表 
     * @author HUNTER.POON
     * @param uuid 红包所属者
     * @param keywords 抽奖标题关键字
     * @param startIdx 开始下标
     * @param size 结果集大小
     * @return
     */
    public PMessage lotteryList(String uuid, String keywords, int startIdx, int size);
    
    /**
     * @Description 抽奖包详情 
     * @author HUNTER.POON
     * @param lotteryId 抽奖包ID
     * @param keywords 中奖者名称关键字
     * @param type 类型(0:全部，1:男，2:女)
     * @return
     */
    public PMessage lotteryDetail(String lotteryId, String keywords, int type);
    
}
