/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.dao
 *
 *    Filename:    GiftLotteryDao.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月22日 下午4:47:50
 *
 *    Revision:
 *
 *    2017年3月22日 下午4:47:50
 *
 *****************************************************************/
package com.blemobi.payment.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName GiftLotteryDao
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月22日 下午4:47:50
 * @version 1.0.0
 */
public interface GiftLotteryDao {

    public int saveLottery(Object[] param);

    public int saveLocations(List<Object[]> param);

    public int saveWinners(List<Object[]> param);

    public int saveGifts(List<Object[]> param);

    public int updateLottery(Object[] param);

    public int updateGift(Object[] param);

    public Map<String, Object> queryLottery(String param);

    public Map<String, Object> queryGift(Object[] param);

    public Map<String, Object> queryWinner(Object[] param);

    public List<Map<String, Object>> historyLotteries(String uuid, String keywords, Integer startIdx);

    public List<String> lotteryLocList(String lotteryId);

    public List<Map<String, Object>> lotteryWinnerList(String lotteryId);

    public List<String> lotteryTop5WinnerList(String lotteryId);

    public List<Map<String, Object>> lotteryGiftList(String lotteryId);

    public List<Map<String, Object>> queryOverdueLotteries();

    public int updateOverdueLottery(Object[] param);

    public int delete(String uuid, List<String> lotteryId);

    public int updateLoc(String rcvNm, String rcvAddr, String rcvPhone, String rcvEmail, String rcvRemark,
            Boolean isSelf, Integer editCnt, Integer status, String lotteryId, String uuid);
}
