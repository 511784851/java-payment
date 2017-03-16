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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.grpc.DataPublishGrpcClient;
import com.blemobi.library.grpc.RobotGrpcClient;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.BillDao;
import com.blemobi.payment.dao.JedisDao;
import com.blemobi.payment.dao.LotteryDao;
import com.blemobi.payment.dao.TransactionDao;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.service.LotteryService;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.payment.util.DateTimeUtils;
import com.blemobi.payment.util.RongYunWallet;
import com.blemobi.payment.util.rongyun.B2CReq;
import com.blemobi.payment.util.rongyun.B2CResp;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryConfirm;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryDetail;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryList;
import com.blemobi.sep.probuf.PaymentProtos.PLotterySingle;
import com.blemobi.sep.probuf.PaymentProtos.POrderPay;
import com.blemobi.sep.probuf.PaymentProtos.PShuffle;
import com.blemobi.sep.probuf.PaymentProtos.PUserBaseEx;
import com.blemobi.sep.probuf.PaymentProtos.PWinLottery;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.RobotApiProtos.PPayOrderParma;

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
    @Autowired
    private JedisDao jedisDao;
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private BillDao billDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PMessage createLottery(String uuid, PLotteryConfirm lottery) {
        List<PUserBaseEx> userExList = lottery.getUserListList();
        if (userExList == null || userExList.isEmpty()) {
            throw new RuntimeException("没有产生中奖者，抽奖异常");
        }
        DataPublishGrpcClient client = new DataPublishGrpcClient();
        List<String> uuidList = client.getFansByFilters(lottery.getGender(), lottery.getRegionList(), uuid);
        if (uuidList == null || uuidList.isEmpty()) {
            throw new RuntimeException("抽奖异常");
        }

        for (int idx = 0; idx < lottery.getUserListCount(); idx++) {
            String ual = lottery.getUserList(idx).getInfo().getUUID() + "_" + lottery.getUserList(idx).getRegion();
            String u = lottery.getUserList(idx).getInfo().getUUID() + "_";
            if (!uuidList.contains(ual) && !uuidList.contains(u)) {
                throw new RuntimeException("中奖者名单被篡改");
            }
        }
        int amt = jedisDao.findDailySendMoney(uuid);
        if ((amt + lottery.getTotAmt()) > Constants.max_daily_money) {// 支出超出上限
            throw new BizException(2101006, "单日支出超出上限");
        }
        long currTm = System.currentTimeMillis();
        RobotGrpcClient robotClient = new RobotGrpcClient();
        PPayOrderParma oparam = PPayOrderParma.newBuilder().setAmount(lottery.getBonus())
                .setServiceNo(OrderEnum.LUCK_DRAW.getValue()).build();
        String orderno = robotClient.generateOrder(oparam).getVal();
        Object[] params = new Object[] {orderno, lottery.getTitle(), lottery.getGender(), lottery.getWinners(),
                lottery.getTotAmt(), lottery.getTotAmt(), lottery.getWinners(), 1, uuid, currTm, currTm,
                lottery.getRemark(), -1};
        int ret = lotteryDao.createLottery(params);
        if (ret != 1) {
            throw new RuntimeException("创建抽奖失败，请重试");
        }
        List<String> regions = lottery.getRegionList();
        if (regions != null && regions.size() > 0) {
            List<Object[]> param = new ArrayList<Object[]>();
            for (String loc : regions) {
                Object[] arr = new Object[4];
                arr[0] = orderno;
                arr[1] = loc;
                arr[2] = " ";
                arr[3] = 0;
                param.add(arr);
            }
            log.info(param);
            ret = lotteryDao.createLotteryLoc(param);
            log.info(ret);
            if (ret != regions.size()) {
                throw new RuntimeException("添加抽奖位置失败，请重试");
            }
        }

        List<Object[]> param = new ArrayList<>();

        for (PUserBaseEx user : userExList) {
            // uuid, lottery_id, nick_nm, sex, bonus, status, crt_tm, accept_tm
            PUserBase info = user.getInfo();
            Object[] arr = new Object[] {info.getUUID(), orderno, info.getNickname(), user.getGender(), user.getAmt(),
                    0, currTm, currTm, user.getRegion() };
            param.add(arr);
        }

        ret = lotteryDao.createWinners(param);
        if (ret != userExList.size()) {
            throw new RuntimeException("中奖者数量不正确，抽奖异常");
        }
        client = new DataPublishGrpcClient();
        client.saveFans(orderno, lottery.getGender(), lottery.getRegionList(), uuid,
                Constants.TABLE_NAMES.LOTTERY_TB.getValue()); // 通知GO 存储抽奖参与者
        SignHelper signHelper = new SignHelper(uuid, lottery.getTotAmt(), orderno, "抽奖");
        POrderPay orderPay = signHelper.getOrderPay();
        jedisDao.cleanLotteryCD(uuid);
        return ReslutUtil.createReslutMessage(orderPay);
    }

    @Override
    public PMessage lotteryList(String uuid, int startIdx, String keywords) {
        List<Map<String, Object>> lotteriesList = lotteryDao.lotteryList(uuid, startIdx, keywords);
        PLotteryList.Builder builder = PLotteryList.newBuilder();
        if (lotteriesList != null) {
            for (Map<String, Object> entity : lotteriesList) {
                PLotterySingle.Builder sBuilder = PLotterySingle.newBuilder();
                List<String> uuids = lotteryDao.top5UUID(entity.get("id").toString());
                sBuilder.setCrtTm(Long.parseLong(entity.get("crt_tm").toString()));
                sBuilder.setLotteryId(entity.get("id").toString());
                sBuilder.setTitle(entity.get("title").toString());
                sBuilder.setWinners(Integer.parseInt(entity.get("winners").toString()));
                List<PUserBase> uList = new ArrayList<PUserBase>();
                for (String u : uuids) {
                    try {
                        PUserBase userBase = UserBaseCache.get(u);
                        uList.add(userBase);
                    } catch (IOException e) {
                        log.error("uuid:[" + u + "]在缓存中没有找到");
                        throw new RuntimeException("用户没有找到");
                    }

                }
                sBuilder.addAllUserList(uList);
                builder.addLotteries(sBuilder.build());
            }
        }
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    public PMessage lotteryDetail(String lotteryId) {
        PLotteryDetail.Builder builder = PLotteryDetail.newBuilder();
        Map<String, Object> detail = lotteryDao.lotteryDetail(lotteryId);
        if (detail != null && !detail.isEmpty()) {
            builder.setCrtTm(Long.parseLong(detail.get("crt_tm").toString()));
            builder.setLotteryId(lotteryId);
            builder.setTitle(detail.get("title").toString());
            builder.setTotAmt(Integer.parseInt(detail.get("tot_amt").toString()));
            builder.setType(Integer.parseInt(detail.get("typ").toString()));
            builder.setWinners(Integer.parseInt(detail.get("winners").toString()));
            builder.setRemark(detail.get("remark").toString());
        }
        Map<String, String> locs = new HashMap<String, String>();
        List<Map<String, Object>> users = lotteryDao.lotteryUsers(lotteryId);
        if (users != null && !users.isEmpty()) {
            List<PUserBaseEx> userList = new ArrayList<PUserBaseEx>();
            for (Map<String, Object> usr : users) {
                String uuid = usr.get("uuid").toString();
                log.debug("中奖用户UUID：" + uuid);
                PUserBaseEx.Builder uBuilder = PUserBaseEx.newBuilder();
                uBuilder.setAmt(Integer.parseInt(usr.get("bonus").toString()));
                uBuilder.setGender(Integer.parseInt(usr.get("sex").toString()));  
                try {
                    PUserBase userBase = UserBaseCache.get(uuid);
                    uBuilder.setInfo(userBase);
                } catch (IOException e) {
                    log.error("uuid:[" + uuid + "]在缓存中没有找到");
                    throw new RuntimeException("用户没有找到");
                }
                String loc = usr.get("loc_cd").toString();
                uBuilder.setRegion(loc);
                locs.put(loc, loc);
                userList.add(uBuilder.build());
            }
            builder.addAllUserList(userList);
        }
        
        
        List<Map<String, Object>> locations = lotteryDao.lotteryLocations(lotteryId);
        if (locations != null && !locations.isEmpty()) {
            List<String> regions = new ArrayList<>();
            for (Map<String, Object> loc : locations) {
                String location = loc.get("loc_cd").toString();
                if(locs.containsKey(location)){
                    regions.add(location);
                }
            }
            builder.addAllRegion(regions);
        }
        
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PMessage acceptPrize(String uuid, String lotteryId) {
        Map<String, Object> inf = lotteryDao.queryLotteryInf(lotteryId);
        if (inf == null || inf.isEmpty()) {
            throw new RuntimeException("没找到对应的抽奖包");
        }
        if (!DateTimeUtils.in24Hours(Long.parseLong(inf.get("crt_tm").toString()))) {
            throw new RuntimeException("抽奖包已过期");
        }
        Integer lotterySts = Integer.parseInt(inf.get("status").toString());
        Integer remainCnt = Integer.parseInt(inf.get("remain_cnt").toString());
        Integer remainAmt = Integer.parseInt(inf.get("remain_amt").toString());
        if (lotterySts.intValue() == 1) {
            throw new RuntimeException("抽奖包未支付");
        } else if (lotterySts.intValue() == 3 || remainCnt.intValue() < 1 || remainAmt.intValue() < 1) {
            throw new RuntimeException("抽奖包已完成领奖");
        } else if (lotterySts.intValue() == 4) {
            throw new RuntimeException("抽奖包已过期");
        } else {

        }

        Map<String, Object> winnerInf = lotteryDao.getPrizeInf(lotteryId, uuid);
        Integer sts = Integer.parseInt(winnerInf.get("status").toString());
        Integer bonus = Integer.parseInt(winnerInf.get("bonus").toString());
        if (sts.intValue() == 1) {
            throw new RuntimeException("抽奖包已完成领奖");
        } else if (sts.intValue() == 2) {
            throw new RuntimeException("抽奖包已过期");
        } else {

        }
        if ((remainAmt - bonus) < 0) {
            throw new RuntimeException("领奖金额异常");
        }
        int status = 3;
        if (remainCnt > 1 && ((remainAmt - bonus) > 0)) {
            status = 2;
        }
        remainCnt--;
        remainAmt -= bonus;
        int ret = lotteryDao.acceptPrize(lotteryId, uuid);
        if(ret != 1){
            throw new RuntimeException("更新领奖失败");
        }
        lotteryDao.updateLottery(lotteryId, remainCnt, remainAmt, DateTimeUtils.currTime(), status);
        RobotGrpcClient robotClient = new RobotGrpcClient();
        PPayOrderParma oparam = PPayOrderParma.newBuilder().setAmount(bonus)
                .setServiceNo(0).build();
        String orderno = robotClient.generateOrder(oparam).getVal();
        B2CReq req = new B2CReq();
        req.setCustOrderno(orderno);
        req.setFenAmt(bonus);
        req.setCustUid(uuid);
        req.setTransferDesc("领奖");
        B2CResp resp = RongYunWallet.b2cTransfer(req);
        if (!Constants.RESPSTS.SUCCESS.getValue().equals(resp.getRespstat())) {
            log.error(resp.toString());
            throw new RuntimeException("转账失败");
        } else {
            log.info(resp.toString());
            long currTm = DateTimeUtils.currTime();
            ret = billDao.insert(uuid, lotteryId, bonus, DateTimeUtils.currTime(), Constants.OrderEnum.LUCK_DRAW.getValue(), 1, inf.get("uuid").toString());
            if(ret != 1){
                throw new RuntimeException("更新账单失败");
            }
            ret = transactionDao.insert(new Object[] {uuid, lotteryId, Constants.OrderEnum.LUCK_DRAW.getValue() + "", bonus,
                    1, " ", " ", resp.getJrmfOrderno(), resp.getRespstat(), resp.getRespmsg(), currTm, currTm, orderno });
            log.info("完成交易流水插入");
            if(ret != 1){
                throw new RuntimeException("更新流水失败");
            }
            

        }
        return viewPrize(uuid, lotteryId);
    }

    @Override
    public PMessage delPrize(String uuid, List<String> lotteryId) {
        int ret = lotteryDao.delPrize(lotteryId, uuid);
        if (ret != lotteryId.size()) {
            throw new RuntimeException("删除失败");
        }
        return ReslutUtil.createSucceedMessage();
    }

    @Override
    public PMessage shuffleLottery(String uuid, PShuffle shuffle) {
        PLotteryDetail.Builder builder = PLotteryDetail.newBuilder();
        log.info("jedisDao:" + jedisDao);
        int amt = jedisDao.findDailySendMoney(uuid);
        if ((amt + shuffle.getTotAmt()) > Constants.max_daily_money) {// 支出超出上限
            throw new BizException(2101006, "单日支出超出上限");
        }
        Integer times = jedisDao.getUserLotteryRefreshTimes(uuid);
        if (times > 0) {
            throw new BizException(2105005, "操作过于频繁，稍后再试");
        }
        DataPublishGrpcClient client = new DataPublishGrpcClient();

        List<String> uuidList = client.getFansByFilters(shuffle.getGender(), shuffle.getRegionList(), uuid);
        if (uuidList == null || uuidList.size() < shuffle.getWinners()) {
            throw new BizException(2105003, "粉丝数量不够");
        }
        List<PUserBaseEx> winnerList = new ArrayList<PUserBaseEx>();
        for (int idx = 0; idx < shuffle.getWinners(); idx++) {
            Random r = new Random();
            int win = r.nextInt(uuidList.size());
            String uidAndLoc = uuidList.get(win);
            log.info("uidAndLoc:" + uidAndLoc);
            String[] ulArr = uidAndLoc.split("_");
            String uid = ulArr[0];
            String locCd = "";
            if (ulArr.length < 2) {
                locCd = "na;";
            } else {
                locCd = ulArr[1];
            }
            PUserBase userBase = null;
            try {
                userBase = UserBaseCache.get(uid);
            } catch (IOException e) {
                log.error("uuid:[" + uuid + "]在缓存中没有找到");
                throw new RuntimeException("用户没有找到");
            }
            uuidList.remove(win);
            PUserBaseEx w = PUserBaseEx.newBuilder().setAmt(shuffle.getBonus()).setGender(userBase.getGender())
                    .setRegion(locCd).setInfo(userBase).build();
            winnerList.add(w);
        }
        builder.setCrtTm(System.currentTimeMillis()).addAllUserList(winnerList).setLotteryId("")
                .addAllRegion(shuffle.getRegionList()).setRemark(shuffle.getRemark()).setTitle(shuffle.getTitle())
                .setTotAmt(shuffle.getTotAmt()).setType(shuffle.getGender()).setWinners(shuffle.getWinners());
        jedisDao.setUserLotteryRefreshTimes(uuid);
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    public PMessage viewPrize(String uuid, String lotteryId) {
        PWinLottery.Builder builder = PWinLottery.newBuilder();
        Map<String, Object> info = lotteryDao.viewLottery(lotteryId, uuid);
        builder.setAccTm(Long.parseLong(info.get("accept_tm").toString()));
        builder.setBonus(Integer.parseInt(info.get("bonus").toString()));
        builder.setCrtTm(Long.parseLong(info.get("crt_tm").toString()));
        builder.setLotteryId(lotteryId);
        builder.setRemark(info.get("remark").toString());
        String suuid = info.get("suuid").toString();
        String nkNm = "";
        try {
            PUserBase userBase = UserBaseCache.get(suuid);
            nkNm = userBase.getNickname();
        } catch (IOException e) {
            log.warn("uuid:[" + uuid + "]在缓存中没有找到");
        }
        builder.setSendNickNm(nkNm);
        builder.setSendUuid(suuid);
        builder.setStatus(Integer.parseInt(info.get("status").toString()));
        builder.setTitle(info.get("title").toString());
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    public List<Map<String, Object>> getExpireLottery() {
        long expTm = DateTimeUtils.calcTime(TimeUnit.DAYS, -2);
        
        List<Map<String, Object>> retList = lotteryDao.getExpireLottery(expTm);
        if(retList == null || retList.isEmpty()){
            return null;
        }
        List<Map<String, Object>> expList = new ArrayList<Map<String, Object>>();
        for(Map<String, Object> map : retList){
            //id, tot_amt, remain_amt, status, remain_cnt, winners, uuid
            String lotteryId = map.get("id").toString();
            String status = map.get("status").toString();
            long crt = Long.parseLong(map.get("crt_tm").toString()) + (5 * 60 * 1000);
            if(DateTimeUtils.in24Hours(crt)){
               continue; 
            }
            Integer remainAmt = Integer.parseInt(map.get("remain_amt").toString());
            Integer remainCnt = Integer.parseInt(map.get("remain_cnt").toString());
            if(remainAmt == null || remainAmt.intValue() <= 0 || remainCnt == null || remainCnt.intValue() <= 0){
                continue;
            }
            String uuid = map.get("uuid").toString();
            //COUNT(1) as cnt, SUM(bonus) amt
            Map<String, Object> win = lotteryDao.getUnacceptAmt(lotteryId);
            Integer cnt = Integer.parseInt(win.get("cnt").toString());
            Integer amt = Integer.parseInt(win.get("amt").toString());
            if(cnt == null || cnt.intValue() <= 0 || amt == null || amt.intValue() <= 0 ){
                continue;
            }
            if(cnt.intValue() != remainCnt.intValue() || amt.intValue() != remainAmt.intValue()){
                log.warn("退款金额与未领取的金额不一致或未领取数量与剩余数量不一致,未领取数量:" + cnt + ",金额:" + amt + ",需要退款的数量:" + remainCnt + "金额:" + remainAmt);
                continue;
            }
            Map<String, Object> refundMap = new HashMap<String, Object>();
            refundMap.put("lotteryId", lotteryId);
            refundMap.put("uuid", uuid);
            refundMap.put("remainAmt", remainAmt);
            refundMap.put("status", status);
            refundMap.put("cnt", cnt);
            expList.add(refundMap);
        }
        return expList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void doRefund(Map<String, Object> map) {
        long updTm = DateTimeUtils.currTime();
        int updStatus = 0;
        String lotteryId = map.get("lotteryId").toString();
        String uuid = map.get("uuid").toString();
        Integer status = Integer.parseInt(map.get("status").toString());
        Integer remainAmt = Integer.parseInt(map.get("remainAmt").toString());
        Integer cnt = Integer.parseInt(map.get("cnt").toString());
        if(status.intValue() == 2){
            updStatus = 4;
        }
        int ret = lotteryDao.updateExpireLottery(lotteryId, updTm, updStatus, status);
        if(ret != 1){
            throw new RuntimeException("操作数据库异常");
        }
        ret = lotteryDao.updateExpireWinners(lotteryId);
        if(ret != cnt.intValue()){
            throw new RuntimeException("操作数据库异常");
        }
        //B2C
        String desc = "领奖退款";
        B2CReq req = new B2CReq();
        String ordNo = "T" + lotteryId;
        req.setCustOrderno(ordNo);
        req.setFenAmt(remainAmt);
        req.setCustUid(uuid);
        req.setTransferDesc(desc);
        B2CResp resp = RongYunWallet.b2cTransfer(req);
        if (!Constants.RESPSTS.SUCCESS.getValue().equals(resp.getRespstat())) {
            log.error(resp.toString());
            throw new RuntimeException("转账失败");
        } else {
            log.info(resp.toString());
            long currTm = DateTimeUtils.currTime();
            ret = transactionDao.insert(new Object[] {uuid, lotteryId, "0", remainAmt,
                    1, " ", desc, resp.getJrmfOrderno(), resp.getRespstat(), resp.getRespmsg(), currTm, currTm, ordNo });
            if(ret != 1){
                throw new RuntimeException("插入流水失败");
            }
            log.info("完成交易流水插入");

        }
        
    }
}
