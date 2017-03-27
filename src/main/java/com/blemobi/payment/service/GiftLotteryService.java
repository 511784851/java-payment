/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.service
 *
 *    Filename:    GiftLotteryService.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月22日 下午3:48:04
 *
 *    Revision:
 *
 *    2017年3月22日 下午3:48:04
 *
 *****************************************************************/
package com.blemobi.payment.service;

import java.util.List;
import java.util.Map;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * @ClassName GiftLotteryService
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月22日 下午3:48:04
 * @version 1.0.0
 */
public interface GiftLotteryService {

    public PMessage shuffle(String uuid, String title, Long overdueTm, Integer winners, Integer locCnt,
            List<String> regions, String remark, Integer gender, List<String> giftNm, List<Integer> giftCnt);

    public PMessage confirm(String uuid, String title, Long overdueTm, Integer winners, Integer locCnt,
            List<String> regions, String remark, Integer gender, List<String> giftNm, List<Integer> giftCnt,
            List<String> uuidList, List<Integer> genderList, List<String> regionList);

    public PMessage accept(String uuid, String lotteryId);

    public PMessage list(String uuid, String keywords, Integer start);

    public PMessage detail(String lotteryId);

    public PMessage view(String lotteryId);

    public PMessage delete(String uuid, List<String> lotteryId);

    public PMessage remind(String uuid, String lotteryId, List<String> uuidList);

    public PMessage edit(String uuid, String lotteryId, String uuid1, String rcvNm, String rcvAddr, String rcvPhone,
            String rcvEmail, String rcvRemark);
    
    public List<Map<String, Object>> queryForIn24HoursLotteries();
    public void notfiyUser(String uuid, String lotteryId, String title);
    public List<Map<String, Object>> queryForExpLotteries();
    public void updExp(String lotteryId, Integer status);
}
