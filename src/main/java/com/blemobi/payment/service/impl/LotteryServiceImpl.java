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
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.grpc.DataPublishGrpcClient;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.JedisDao;
import com.blemobi.payment.dao.LotteryDao;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.service.LotteryService;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.service.order.IdWorker;
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.payment.util.DateTimeUtils;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryConfirm;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryDetail;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryList;
import com.blemobi.sep.probuf.PaymentProtos.PLotterySingle;
import com.blemobi.sep.probuf.PaymentProtos.POrderPay;
import com.blemobi.sep.probuf.PaymentProtos.PShuffle;
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
    @Autowired
    private JedisDao jedisDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PMessage createLottery(String uuid, PLotteryConfirm lottery) {
        List<PUserBaseEx> userExList = lottery.getUserListList();
        if (userExList == null || userExList.isEmpty()) {
            throw new BizException(2015008, "没有产生中奖者，抽奖异常");
        }
        // 验证中奖者是否在参与者列表
        DataPublishGrpcClient client = new DataPublishGrpcClient();
        List<String> uuidList = client.getFansByFilters(lottery.getGender(), lottery.getRegionList(), uuid);
        if (uuidList == null || uuidList.isEmpty()) {
            throw new BizException(2015012, "抽奖异常");
        }
        for (int idx = 0; idx < lottery.getUserListCount(); idx++) {
            if (!uuidList.contains(lottery.getUserList(idx).getInfo().getUUID())) {
                throw new BizException(2015013, "中奖者名单被篡改");
            }
        }
        int amt = jedisDao.findDailySendMoney(uuid);
        if ((amt + lottery.getTotAmt()) > Constants.max_daily_money) {// 支出超出上限
            throw new BizException(2015005, "单日支出超出上限");
        }
        long currTm = System.currentTimeMillis();
        IdWorker idWorder = IdWorker.getInstance();
        String orderno = idWorder.nextId(OrderEnum.LUCK_DRAW.getValue());
        Object[] params = new Object[] {orderno, lottery.getTitle(), lottery.getGender(), lottery.getWinners(),
                lottery.getTotAmt(), lottery.getTotAmt(), lottery.getWinners(), 1, uuid, currTm, currTm, ' ',
                lottery.getRemark() };
        int ret = lotteryDao.createLottery(params);
        if (ret != 1) {
            throw new BizException(2015006, "创建抽奖失败，请重试");
        }
        List<String> regions = lottery.getRegionList();
        if (regions != null && regions.size() > 0) {
            List<Object[]> param = new ArrayList<Object[]>();
            for (String loc : regions) {
                Object[] arr = new Object[3];
                arr[0] = orderno;
                arr[1] = loc;
                arr[2] = ' ';
                arr[3] = 0;
                param.add(arr);
            }
            ret = lotteryDao.createLotteryLoc(param);
            if (ret != regions.size()) {
                throw new BizException(2015007, "添加抽奖位置失败，请重试");
            }
        }

        List<Object[]> param = new ArrayList<>();

        for (PUserBaseEx user : userExList) {
            // uuid, lottery_id, nick_nm, sex, bonus, status, crt_tm, accept_tm
            PUserBase info = user.getInfo();
            Object[] arr = new Object[] {info.getUUID(), orderno, info.getNickname(), user.getGender(), user.getAmt(),
                    0, currTm, currTm };
            param.add(arr);
        }

        ret = lotteryDao.createWinners(param);
        if (ret != userExList.size()) {
            throw new BizException(2015009, "中奖者数量不正确，抽奖异常");
        }
        // TODO 通知GO 存储参与抽奖者信息
        SignHelper signHelper = new SignHelper(uuid, lottery.getTotAmt(), orderno, "抽奖");
        POrderPay orderPay = signHelper.getOrderPay();
        return ReslutUtil.createReslutMessage(orderPay);
    }

    @Override
    public PMessage lotteryList(String uuid, String keywords, int startIdx, int size) {
        List<Map<String, Object>> lotteriesList = lotteryDao.lotteryList(uuid, keywords, startIdx, size);
        PLotteryList.Builder builder = PLotteryList.newBuilder();
        if (lotteriesList != null) {
            for (Map<String, Object> entity : lotteriesList) {
                PLotterySingle.Builder sBuilder = PLotterySingle.newBuilder();
                List<String> uuids = lotteryDao.top5UUID(entity.get("id").toString());
                sBuilder.setCrtTm(entity.get("crt_tm").toString());
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
                        throw new BizException(2015100, "用户没有找到");
                    }

                }
                sBuilder.addAllUserList(uList);
                builder.addLotteries(sBuilder.build());
            }
        }
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    public PMessage lotteryDetail(String lotteryId, String keywords, int type) {
        PLotteryDetail.Builder builder = PLotteryDetail.newBuilder();
        Map<String, Object> detail = lotteryDao.lotteryDetail(lotteryId);
        if (detail != null && !detail.isEmpty()) {
            builder.setCrtTm(detail.get("crt_tm").toString());
            builder.setLotteryId(lotteryId);
            builder.setTitle(detail.get("title").toString());
            builder.setTotAmt(Integer.parseInt(detail.get("tot_amt").toString()));
            builder.setType(Integer.parseInt(detail.get("typ").toString()));
            builder.setWinners(Integer.parseInt(detail.get("winners").toString()));
            builder.setRemark(detail.get("remark").toString());
        }
        List<Map<String, Object>> locations = lotteryDao.lotteryLocations(lotteryId);
        List<String> regions = new ArrayList<>();
        if (locations != null && !locations.isEmpty()) {
            for (Map<String, Object> loc : locations) {
                regions.add(loc.get("loc_cd").toString());
            }
            builder.addAllRegion(regions);
        }
        List<Map<String, Object>> users = lotteryDao.lotteryUsers(lotteryId, keywords, type);
        if (users != null && !users.isEmpty()) {
            List<PUserBaseEx> userList = new ArrayList<PUserBaseEx>();
            for (Map<String, Object> usr : users) {
                PUserBaseEx.Builder uBuilder = PUserBaseEx.newBuilder();
                uBuilder.setAmt(Integer.parseInt(usr.get("bonus").toString()));
                String uuid = usr.get("uuid").toString();
                PUserBase.Builder infBuilder = PUserBase.newBuilder();
                infBuilder.setUUID(uuid);
                try {
                    PUserBase userBase = UserBaseCache.get(uuid);
                    uBuilder.setInfo(userBase);
                } catch (IOException e) {
                    log.error("uuid:[" + uuid + "]在缓存中没有找到");
                    throw new BizException(2015100, "用户没有找到");
                }

                uBuilder.setGender(Integer.parseInt(usr.get("sex").toString()));
                userList.add(uBuilder.build());
            }
            builder.addAllUserList(userList);
        }
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PMessage acceptPrize(String uuid, String lotteryId) {
        Map<String, Object> inf = lotteryDao.queryLotteryInf(lotteryId);
        Integer remainCnt = Integer.parseInt(inf.get("remain_cnt").toString());
        Integer remainAmt = Integer.parseInt(inf.get("remain_amt").toString());
        long updTm = DateTimeUtils.currTime();
        if (inf == null || inf.isEmpty()) {
            throw new BizException(2015000, "没找到对应的抽奖包");
        }
        if (!DateTimeUtils.in24Hours(Long.parseLong(inf.get("crt_tm").toString()))) {
            throw new BizException(2015001, "抽奖包已过期");
        }
        Integer lotterySts = Integer.parseInt(inf.get("status").toString());
        if (lotterySts.intValue() == 1) {
            throw new BizException(2015002, "抽奖包未支付");
        } else if (lotterySts.intValue() == 3 || remainCnt.intValue() < 1 || remainAmt.intValue() < 1) {
            throw new BizException(2015003, "抽奖包已完成领奖");
        } else if (lotterySts.intValue() == 4) {
            throw new BizException(2015001, "抽奖包已过期");
        } else {

        }

        Map<String, Object> winnerInf = lotteryDao.getPrizeInf(lotteryId, uuid);
        Integer sts = Integer.parseInt(winnerInf.get("status").toString());
        Integer bonus = Integer.parseInt(winnerInf.get("bonus").toString());
        if (sts.intValue() == 1) {
            throw new BizException(2015003, "抽奖包已完成领奖");
        } else if (sts.intValue() == 2) {
            throw new BizException(2015001, "抽奖包已过期");
        } else {

        }
        if ((remainAmt - bonus) < 0) {
            throw new BizException(2015004, "领奖金额异常");
        }
        int status = 3;
        if (remainCnt > 1 && ((remainAmt - bonus) > 0)) {
            status = 2;
        }
        remainCnt--;
        remainAmt -= bonus;
        lotteryDao.acceptPrize(lotteryId, uuid);
        lotteryDao.updateLottery(lotteryId, remainCnt, remainAmt, updTm, status);
        // TODO 转账
        // B2CResp req = new B2CResp();
        // RongYunWallet.b2cTransfer(req);
        return ReslutUtil.createSucceedMessage();
    }

    @Override
    public PMessage delPrize(String uuid, List<String> lotteryId) {
        int ret = lotteryDao.delPrize(lotteryId, uuid);
        if (ret != lotteryId.size()) {
            throw new BizException(2015009, "删除失败");
        }
        return ReslutUtil.createSucceedMessage();
    }

    @Override
    public PMessage shuffleLottery(String uuid, PShuffle shuffle) {
        PLotteryDetail.Builder builder = PLotteryDetail.newBuilder();
        int amt = jedisDao.findDailySendMoney(uuid);
        if ((amt + shuffle.getTotAmt()) > Constants.max_daily_money) {// 支出超出上限
            throw new BizException(2015005, "单日支出超出上限");
        }
        Integer times = jedisDao.getUserLotteryRefreshTimes(uuid);
        if (times > 1) {
            throw new BizException(2015010, "5分钟内仅能重抽2次，请稍后再试");
        }
        DataPublishGrpcClient client = new DataPublishGrpcClient();
        
        List<String> uuidList = client.getFansByFilters(shuffle.getGender(), shuffle.getRegionList(), uuid);
        if (uuidList == null || uuidList.size() < shuffle.getWinners()) {
            throw new BizException(2015011, "粉丝数量不够");
        }
        List<PUserBaseEx> winnerList = new ArrayList<PUserBaseEx>();
        for (int idx = 0; idx < shuffle.getWinners(); idx++) {
            Random r = new Random();
            int win = r.nextInt(uuidList.size());
            String uid = uuidList.get(win);
            PUserBase userBase = null;
            try {
                userBase = UserBaseCache.get(uid);
            } catch (IOException e) {
                log.error("uuid:[" + uuid + "]在缓存中没有找到");
                throw new BizException(2015100, "用户没有找到");
            }
            uuidList.remove(win);
            PUserBaseEx w = PUserBaseEx.newBuilder().setAmt(shuffle.getBonus()).setGender(userBase.getGender())
                    .setInfo(userBase).build();
            winnerList.add(w);
        }
        builder.setCrtTm(System.currentTimeMillis() + "").addAllUserList(winnerList).setLotteryId("")
                .addAllRegion(shuffle.getRegionList()).setRemark(shuffle.getRemark()).setTitle(shuffle.getTitle())
                .setTotAmt(shuffle.getTotAmt()).setType(shuffle.getGender()).setWinners(shuffle.getWinners());
        jedisDao.setUserLotteryRefreshTimes(uuid);
        return ReslutUtil.createReslutMessage(builder.build());
    }
}
