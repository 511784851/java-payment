/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.dao
 *
 *    Filename:    LotteryDao.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月18日 下午12:09:11
 *
 *    Revision:
 *
 *    2017年2月18日 下午12:09:11
 *
 *****************************************************************/
package com.blemobi.payment.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName LotteryDao
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:09:11
 * @version 1.0.0 
 */
public interface LotteryDao {
    public int createLottery(Object[] param);
    public int createLotteryLoc(List<Object[]> param);
    public int createWinners(List<Object[]> param);
    public List<Map<String, Object>> lotteryList(String uuid, String keywords, int startIdx, int size);
    public List<String> top5UUID(String lotteryId);
    public Map<String, Object> lotteryDetail(String lotteryId);
    public List<Map<String, Object>> lotteryLocations(String lotteryId);
    public List<Map<String, Object>> lotteryUsers(String lotteryId, String keywords, int type);
    public int paySucc(String ordNo, int amt);
    public Map<String, Object> queryLotteryInf(String lotteryId);
    public Map<String, Object> getPrizeInf(String lotteryId, String uuid);
    public int acceptPrize(String lotteryId, String uuid);
    public int updateLottery(String lotteryId, int remainCnt, int remainAmt, long updTm, int status);
    public int delPrize(String lotteryId, String uuid);
}
