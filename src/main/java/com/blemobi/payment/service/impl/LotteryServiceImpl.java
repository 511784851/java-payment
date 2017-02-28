/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.service.impl
 *
 *    Filename:    LotteryServiceImpl.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月18日 下午12:07:19
 *
 *    Revision:
 *
 *    2017年2月18日 下午12:07:19
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

import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.LotteryDao;
import com.blemobi.payment.service.LotteryService;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.service.order.IdWorker;
import com.blemobi.payment.service.order.OrderEnum;
import com.blemobi.payment.util.DateTimeUtils;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PLocation;
import com.blemobi.sep.probuf.PaymentProtos.PLottery;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryDetailRet;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryListRet;
import com.blemobi.sep.probuf.PaymentProtos.PLotterySingleRet;
import com.blemobi.sep.probuf.PaymentProtos.POrderPay;
import com.blemobi.sep.probuf.PaymentProtos.PUserBaseEx;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

import lombok.extern.log4j.Log4j;

/**
 * @ClassName LotteryServiceImpl
 * @Description 抽奖业务类
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:07:19
 * @version 1.0.0
 */
@Log4j
@Service("lotteryService")
public class LotteryServiceImpl implements LotteryService {

    @Autowired
    private LotteryDao lotteryDao;

    /**
     * 领奖有效时限
     */
    private static final long maxInvalidTime = 24 * 60 * 60 * 1000;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PMessage createLottery(String uuid, PLottery lottery) {
        // TODO PRD TO BE REMOVED
        uuid = "123";
        long currTm = System.currentTimeMillis();
        IdWorker idWorder = IdWorker.getInstance();
        String orderno = idWorder.nextId(OrderEnum.LUCK_DRAW.getValue());
        Object[] params = new Object[] {orderno, lottery.getTitle(), lottery.getType(), lottery.getWinners(),
                lottery.getWinners(), lottery.getTotAmt(), lottery.getTotAmt(), 1, uuid, currTm, currTm };
        int ret = lotteryDao.createLottery(params);
        if (ret != 1) {
            throw new RuntimeException("创建抽奖失败，请重试。");
        }
        if (lottery.getLocsCount() > 0) {
            List<Object[]> param = new ArrayList<Object[]>();
            ret = lotteryDao.createLotteryLoc(param);
            for (PLocation loc : lottery.getLocsList()) {
                Object[] arr = new Object[3];
                arr[0] = orderno;
                arr[1] = loc.getLocCd();
                arr[2] = loc.getLocNm();
                param.add(arr);
            }
            ret = lotteryDao.createLotteryLoc(param);
            if (ret != lottery.getLocsCount()) {
                throw new RuntimeException("添加抽奖位置失败，请重试。");
            }
        }
        if (lottery.getUuidListCount() > 0) {
            List<Object[]> param = new ArrayList<Object[]>();
            // uuid, lottery_id, nick_nm, sex, bonus, status, crt_tm, accept_tm
            for (String uid : lottery.getUuidListList().toArray(new String[] {})) {
                Object[] arr = new Object[8];
                arr[0] = uid;
                arr[1] = orderno;
                // TODO 从缓存中获取用户基本信息
                arr[2] = "11";
                arr[3] = 1;
                arr[4] = lottery.getBonus();
                arr[5] = 0;
                arr[6] = currTm;
                arr[7] = currTm;
                param.add(arr);
            }
            ret = lotteryDao.createWinners(param);
            if (ret != lottery.getUuidListCount()) {
                throw new RuntimeException("添加中奖者失败，请重试。");
            }
        }
        SignHelper signHelper = new SignHelper(uuid, lottery.getTotAmt(), orderno, "抽奖");
		POrderPay orderPay = signHelper.getOrderPay();
        return ReslutUtil.createReslutMessage(orderPay);
    }

    @Override
    public PMessage lotteryList(String uuid, String keywords, int startIdx, int size) {
        List<Map<String, Object>> lotteriesList = lotteryDao.lotteryList(uuid, keywords, startIdx, size);
        PLotteryListRet.Builder builder = PLotteryListRet.newBuilder();
        if (lotteriesList != null) {
            for (Map<String, Object> entity : lotteriesList) {
                log.debug(entity.get("id").toString() + "--------");
                PLotterySingleRet.Builder sBuilder = PLotterySingleRet.newBuilder();
                List<String> uuids = lotteryDao.top5UUID(entity.get("id").toString());
                sBuilder.setCrtTm(entity.get("crt_tm").toString());
                sBuilder.setLotteryId(entity.get("id").toString());
                sBuilder.setTitle(entity.get("title").toString());
                sBuilder.setWinners(Integer.parseInt(entity.get("winners").toString()));
                // TODO 缓存中获取用户头像
                // sBuilder.addAllIcons();
                builder.addLotteries(sBuilder.build());
            }
        }
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    public PMessage lotteryDetail(String lotteryId, String keywords, int type) {
        PLotteryDetailRet.Builder builder = PLotteryDetailRet.newBuilder();
        Map<String, Object> detail = lotteryDao.lotteryDetail(lotteryId);
        if (detail != null && !detail.isEmpty()) {
            builder.setCrtTm(detail.get("crt_tm").toString());
            builder.setLotteryId(lotteryId);
            builder.setTotAmt(Integer.parseInt(detail.get("tot_amt").toString()));
            builder.setType(Integer.parseInt(detail.get("typ").toString()));
            builder.setWinners(Integer.parseInt(detail.get("winners").toString()));
        }
        List<Map<String, Object>> locations = lotteryDao.lotteryLocations(lotteryId);
        if (locations != null && !locations.isEmpty()) {
            List<PLocation> locs = new ArrayList<PLocation>();
            for (Map<String, Object> loc : locations) {
                PLocation.Builder lBuilder = PLocation.newBuilder();
                lBuilder.setLocCd(loc.get("loc_cd").toString());
                lBuilder.setLocNm(loc.get("loc_nm").toString());
                locs.add(lBuilder.build());
            }
            builder.addAllLocs(locs);
        }
        List<Map<String, Object>> users = lotteryDao.lotteryUsers(lotteryId, keywords, type);
        if (users != null && !users.isEmpty()) {
            List<PUserBaseEx> userList = new ArrayList<PUserBaseEx>();
            for (Map<String, Object> usr : users) {
                PUserBaseEx.Builder uBuilder = PUserBaseEx.newBuilder();
                uBuilder.setAmt(Integer.parseInt(usr.get("bonus").toString()));
                String uuid = usr.get("uuid").toString();
                // TODO 缓存中获取用户信息
                PUserBase.Builder infBuilder = PUserBase.newBuilder();
                infBuilder.setUUID(uuid);
                uBuilder.setInfo(infBuilder.build());
                uBuilder.setSex(Integer.parseInt(usr.get("sex").toString()));
                userList.add(uBuilder.build());
            }
            builder.addAllUsers(userList);
        }
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    public PMessage acceptPrize(String uuid, String lotteryId) {
        Map<String, Object> inf = lotteryDao.queryLotteryInf(lotteryId);
        lotteryDao.acceptPrize(lotteryId, uuid);
        Integer remainCnt = Integer.parseInt(inf.get("remain_cnt").toString());
        Integer remainAmt = Integer.parseInt(inf.get("remain_amt").toString());
        Integer totAmt = Integer.parseInt(inf.get("tot_amt").toString());
        long updTm = DateTimeUtils.currTime();
        if(inf == null || inf.isEmpty()){
            // TODO 没找到对应的抽奖包 
            throw new RuntimeException("");
        }
        if (!DateTimeUtils.in24Hours(Long.parseLong(inf.get("crt_tm").toString()))) {
            // TODO 抽奖包已过期
            throw new RuntimeException("");
        }
        Integer lotterySts = Integer.parseInt(inf.get("status").toString());
        if(lotterySts.intValue() == 1){
            // TODO 抽奖包未支付
            throw new RuntimeException("");
        }else if(lotterySts.intValue() == 3 || remainCnt.intValue() < 1 || remainAmt.intValue() < 1){
            // TODO 抽奖包已完成领奖
            throw new RuntimeException("");
        }else if(lotterySts.intValue() == 4){
            // TODO 抽奖包已过期
            throw new RuntimeException("");
        }else{
            
        }
        
        Map<String, Object> winnerInf = lotteryDao.getPrizeInf(lotteryId, uuid);
        Integer sts = Integer.parseInt(winnerInf.get("status").toString());
        Integer bonus = Integer.parseInt(winnerInf.get("bonus").toString());
        if (sts.intValue() == 1) {
            // TODO 已领奖
            throw new RuntimeException("");
        } else if (sts.intValue() == 2) {
            // TODO 已过期
            throw new RuntimeException("");
        } else {

        }
        if((remainAmt + bonus) > totAmt){
            // TODO 领奖包金额异常
            throw new RuntimeException("");
        }
        int status = 3;
        if(remainCnt > 1 && ((remainAmt + bonus) < totAmt)){
            status = 2;
        }
        remainCnt--;
        remainAmt -= bonus;
        lotteryDao.updateLottery(lotteryId, remainCnt, remainAmt, updTm, status);
        return null;
    }
}
