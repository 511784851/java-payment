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

    /*
     * (非 Javadoc) Description:
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (true) {
            log.debug("查询过期红包、抽奖包记录");
            try {
                doExpLotteries();
                doExpRedbag();
                log.debug("处理完成");
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                log.error("thread sleep failed", e);
            }
            
        }
    }

    private void doExpRedbag() {
        try {
            List<RedSend> list = sendService.selectByOver();
            for (RedSend rs : list) {
                try {
                    sendService.updateRef(rs);
                } catch (Exception ex) {
                    log.error("红包退款异常", ex);
                }
            }
        } catch (Exception ex) {
            log.error("红包退款异常", ex);
        }
    }

    private void doExpLotteries() {
        try {
            List<Map<String, Object>> expList = lotteryService.getExpireLottery();
            if (expList != null && expList.size() > 0) {
                for (Map<String, Object> map : expList) {
                    try {
                        lotteryService.doRefund(map);
                    } catch (Exception e) {
                        log.error("抽奖退款异常", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("抽奖退款异常", e);
        }
    }
}
