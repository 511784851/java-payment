/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.service.impl
 *
 *    Filename:    CallbackServiceImpl.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月27日 下午6:02:21
 *
 *    Revision:
 *
 *    2017年2月27日 下午6:02:21
 *
 *****************************************************************/
package com.blemobi.payment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.payment.dao.LotteryDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.dao.RewardDao;
import com.blemobi.payment.dao.TransactionDao;
import com.blemobi.payment.service.CallbackService;
import com.blemobi.payment.service.order.OrderEnum;


/**
 * @ClassName CallbackServiceImpl
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年2月27日 下午6:02:21
 * @version 1.0.0
 */
@Service("callbackService")
public class CallbackServiceImpl implements CallbackService {
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private LotteryDao lotteryDao;
    @Autowired
    private RedSendDao redSendDao;
    @Autowired
    private RewardDao rewardDao;
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean paySucc(String amount, long time, String ordNo, String recUid, String corgOrdId,
            String corgSts, String corgMsg) {
        int ret = transactionDao.insert(new Object[]{Integer.parseInt(amount), 1, time, ordNo, recUid, corgOrdId, corgSts, corgMsg});
        if(ret != 1){
            throw new RuntimeException("insert into table failed");
        }
        //TODO 判断订单来源
        int type = 1;
        if(type == OrderEnum.RED_ORDINARY.getValue() || type == OrderEnum.RED_GROUP_EQUAL.getValue() || type == OrderEnum.RED_GROUP_EQUAL.getValue()){//红包
            ret = redSendDao.paySucc(ordNo, Integer.parseInt(amount));
        }else if(type == OrderEnum.LUCK_DRAW.getValue()){//抽奖
            ret = lotteryDao.paySucc(ordNo, Integer.parseInt(amount));
        }else if(type == OrderEnum.REWARD.getValue()){//打赏
            ret = rewardDao.paySucc(ordNo, Integer.parseInt(amount));
            if(ret != 1){
                throw new RuntimeException("update reward record failed");
            }
            //TODO 转账给网红
        }
        if(ret != 1){
            throw new RuntimeException("update red package or lottery record failed");
        }
        return true;
    }

}
