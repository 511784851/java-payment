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

import com.blemobi.payment.dao.BillDao;
import com.blemobi.payment.dao.JedisDao;
import com.blemobi.payment.dao.LotteryDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.dao.RewardDao;
import com.blemobi.payment.dao.TransactionDao;
import com.blemobi.payment.service.CallbackService;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.payment.util.DateTimeUtils;

import lombok.extern.log4j.Log4j;


/**
 * @ClassName CallbackServiceImpl
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年2月27日 下午6:02:21
 * @version 1.0.0
 */
@Log4j
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
    @Autowired
    private JedisDao jedisDao;
    @Autowired
    private BillDao billDao;
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean paySucc(String amount, long time, String ordNo, String recUid, String corgOrdId,
            String corgSts, String corgMsg) {
        int bizType = 5;
        String uuid = "";
        //TODO 判断订单来源
        int ret = 0;
        if(bizType == OrderEnum.RED_ORDINARY.getValue() || bizType == OrderEnum.RED_GROUP_EQUAL.getValue() || bizType == OrderEnum.RED_GROUP_EQUAL.getValue()){
            //红包
            uuid = redSendDao.selectByKey(ordNo).getSend_uuid();
            ret = redSendDao.paySucc(ordNo, Integer.parseInt(amount));
        }else if(bizType == OrderEnum.LUCK_DRAW.getValue()){//抽奖
            uuid = lotteryDao.lotteryDetail(ordNo).get("uuid").toString();
            ret = lotteryDao.paySucc(ordNo, Integer.parseInt(amount));
        }else if(bizType == OrderEnum.REWARD.getValue()){//打赏
            uuid = rewardDao.selectByKey(ordNo).getSend_uuid();
            ret = rewardDao.paySucc(ordNo, Integer.parseInt(amount));
        }
        if(ret != 1){
            throw new RuntimeException("update red package or lottery record failed");
        }
        jedisDao.incrByDailySendMoney(uuid, Integer.parseInt(amount));//累计日支出
        //uuid,ord_no,money,time,type
        billDao.insert(new Object[]{uuid, ordNo, -Long.parseLong(amount), bizType});
        //uuid, biz_ord_no, biz_typ, amt, ptf_sts, ptf_msg, trans_desc, corg_ord_no, corg_sts, corg_msg, crt_tm, upd_tm
        long currTm = DateTimeUtils.currTime();
        log.info(corgMsg);
        ret = transactionDao.insert(new Object[]{recUid, ordNo, bizType+"", Integer.parseInt(amount), 1, " ", " ", corgOrdId, corgSts, corgMsg, currTm, currTm});
        if(ret != 1){
            throw new RuntimeException("insert into table failed");
        }
        return true;
    }

}
