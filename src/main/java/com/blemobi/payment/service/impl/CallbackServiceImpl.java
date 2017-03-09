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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.blemobi.payment.service.helper.PushMsgHelper;
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
        int bizType = Integer.parseInt(ordNo.substring(0, 1));
        String uuid = "";
        int ret = 0;
        log.info("bizType:" + bizType);
        if(bizType == OrderEnum.RED_ORDINARY.getValue() || bizType == OrderEnum.RED_GROUP_EQUAL.getValue() || bizType == OrderEnum.RED_GROUP_EQUAL.getValue()){
            //红包
            log.info("red");
            uuid = redSendDao.selectByKey(ordNo).getSend_uuid();
            ret = redSendDao.paySucc(ordNo, Integer.parseInt(amount));
        }else if(bizType == OrderEnum.LUCK_DRAW.getValue()){//抽奖
            log.info("lottery");
            uuid = lotteryDao.lotteryDetail(ordNo).get("uuid").toString();
            ret = lotteryDao.paySucc(ordNo, Integer.parseInt(amount));
        }else if(bizType == OrderEnum.REWARD.getValue()){//打赏
            log.info("reward");
            uuid = rewardDao.selectByKey(ordNo).getSend_uuid();
            ret = rewardDao.paySucc(ordNo, Integer.parseInt(amount));
        }
        if(ret != 1){
            throw new RuntimeException("update red package or lottery record failed");
        }
        jedisDao.incrByDailySendMoney(uuid, Integer.parseInt(amount));//累计日支出
        log.info("累计用户支出完成");
        //uuid,ord_no,money,time,type
        billDao.insert(new Object[]{uuid, ordNo, -Long.parseLong(amount), bizType});
        log.info("完成账单插入");
        //uuid, biz_ord_no, biz_typ, amt, ptf_sts, ptf_msg, trans_desc, corg_ord_no, corg_sts, corg_msg, crt_tm, upd_tm
        long currTm = DateTimeUtils.currTime();
        ret = transactionDao.insert(new Object[]{recUid, ordNo, bizType+"", Integer.parseInt(amount), 1, " ", " ", corgOrdId, corgSts, corgMsg, currTm, currTm});
        log.info("完成交易流水插入");
        if(ret != 1){
            throw new RuntimeException("insert into table failed");
        }
        if(bizType == OrderEnum.RED_ORDINARY.getValue() || bizType == OrderEnum.RED_GROUP_EQUAL.getValue() || bizType == OrderEnum.RED_GROUP_EQUAL.getValue()){
            //红包
            //推送消息
            PushMsgHelper pushMgr = new PushMsgHelper(uuid, ordNo);
            pushMgr.redPacketMsg();
        }else if(bizType == OrderEnum.LUCK_DRAW.getValue()){//抽奖
            log.info("lottery");
            //推送消息
            List<String> toList = new ArrayList<String>();
            List<Map<String, Object>> list = lotteryDao.lotteryUsers(ordNo);
            for(Map<String, Object> info : list){
                toList.add(info.get("uuid").toString());
            }
            PushMsgHelper pushMgr = new PushMsgHelper(uuid, ordNo, toList);
            pushMgr.lotteryMsg();
        }else if(bizType == OrderEnum.REWARD.getValue()){//打赏
            log.info("reward");
        }
        
        return true;
    }

}
