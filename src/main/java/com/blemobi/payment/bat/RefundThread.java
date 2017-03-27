/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.bat
 *
 *    Filename:    RefundThread.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月11日 下午1:43:56
 *
 *    Revision:
 *
 *    2017年3月11日 下午1:43:56
 *
 *****************************************************************/
package com.blemobi.payment.bat;

import java.util.List;
import java.util.Map;

import com.blemobi.payment.model.RedSend;
import com.blemobi.payment.service.GiftLotteryService;
import com.blemobi.payment.service.LotteryService;
import com.blemobi.payment.service.SendService;
import com.blemobi.payment.util.InstanceFactory;

import lombok.extern.log4j.Log4j;

/**
 * @ClassName RefundThread
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月11日 下午1:43:56
 * @version 1.0.0
 */
@Log4j
public class RefundThread implements Runnable {

    private static final long SLEEP_TIME = 5 * 60 * 1000;
    private static final LotteryService lotteryService = InstanceFactory.getInstance(LotteryService.class);
    private static final SendService sendService = InstanceFactory.getInstance(SendService.class);
    private static final GiftLotteryService giftLotteryService = InstanceFactory.getInstance(GiftLotteryService.class);
    /*
     * (非 Javadoc) Description:
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (true) {
            try {
                doExpLotteries();
                doExpRedbag();
                doExpGiftLottery();
                notifyIn24Hours();
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                log.error("thread sleep failed", e);
            }
            
        }
    }

    private void doExpGiftLottery(){
        try{
            log.debug("doExpGiftLottery start");
            List<Map<String, Object>> expList = giftLotteryService.queryForExpLotteries();
            for(Map<String, Object> expMap : expList){
                log.debug("处理过期：" + expMap);
                String lotteryId = expMap.get("id").toString();
                Integer status = Integer.parseInt(expMap.get("status").toString());
                if(status.intValue() != 0){
                    status = 4;
                }
                giftLotteryService.updExp(lotteryId, status);
            }
            log.debug("doExpGiftLottery end");
        }catch(Exception ex){
            log.error("处理过期实物抽奖出现异常", ex);
        }
    }
    
    private void notifyIn24Hours(){
        try{
            log.debug("notifyIn24Hours start");
            List<Map<String, Object>> expList = giftLotteryService.queryForIn24HoursLotteries();
            for(Map<String, Object> expMap : expList){
                log.debug("通知网红发货、中奖者领奖：" + expMap);
                String lotteryId = expMap.get("id").toString();
                String uuid = expMap.get("uuid").toString();
                String title = expMap.get("title").toString();
                giftLotteryService.notfiyUser(uuid, lotteryId, title);
            }
            log.debug("notifyIn24Hours end");
        }catch(Exception ex){
            log.error("处理通知实物抽奖出现异常", ex);
        }
    }
    
    private void doExpRedbag() {
        try {
            log.debug("doExpRedbag start.");
            List<RedSend> list = sendService.selectByOver();
            for (RedSend rs : list) {
                try {
                    log.debug("发起红包过期退款操作，" + rs.toString());
                    sendService.updateRef(rs);
                    log.debug("完成红包：" + rs.toString() + "过期退款");
                } catch (Exception ex) {
                    log.error("红包退款异常", ex);
                }
            }
            log.debug("doExpRedbag end.");
        } catch (Exception ex) {
            log.error("红包退款异常", ex);
        }
    }

    private void doExpLotteries() {
        try {
            log.debug("doExpLotteries start.");
            List<Map<String, Object>> expList = lotteryService.getExpireLottery();
            if (expList != null && expList.size() > 0) {
                for (Map<String, Object> map : expList) {
                    try {
                        log.debug("发起抽奖过期退款操作，" + map);
                        lotteryService.doRefund(map);
                    } catch (Exception e) {
                        log.error("抽奖退款异常", e);
                    }
                }
            }
            log.debug("doExpLotteries start.");
        } catch (Exception e) {
            log.error("抽奖退款异常", e);
        }
    }
}
