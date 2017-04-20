/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.service.impl
 *
 *    Filename:    GiftLotteryServiceImpl.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月22日 下午4:06:25
 *
 *    Revision:
 *
 *    2017年3月22日 下午4:06:25
 *
 *****************************************************************/
package com.blemobi.payment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.grpc.DataPublishGrpcClient;
import com.blemobi.library.grpc.NotifyGrpcClient;
import com.blemobi.library.grpc.RobotGrpcClient;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.GiftLotteryDao;
import com.blemobi.payment.dao.JedisDao;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.service.GiftLotteryService;
import com.blemobi.payment.service.helper.PushMsgHelper;
import com.blemobi.payment.service.helper.ShuffleUtils;
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.payment.util.DateTimeUtils;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.NotificationApiProtos.PNotifyInternalMessage;
import com.blemobi.sep.probuf.NotificationApiProtos.PNotifyInternalMessageList;
import com.blemobi.sep.probuf.NotificationProtos.ENotifyType;
import com.blemobi.sep.probuf.NotificationProtos.PNotifyMessage;
import com.blemobi.sep.probuf.NotificationProtos.PNotifyRawMessage;
import com.blemobi.sep.probuf.NotificationProtos.PNotifySimple;
import com.blemobi.sep.probuf.PaymentProtos.PGiftInfo;
import com.blemobi.sep.probuf.PaymentProtos.PGiftLotteryDetail;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryList;
import com.blemobi.sep.probuf.PaymentProtos.PLotterySingle;
import com.blemobi.sep.probuf.PaymentProtos.PUserBaseGiftEx;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.RobotApiProtos.PBLotteryNotifyMsg;
import com.blemobi.sep.probuf.RobotApiProtos.PPayOrderParma;
import com.blemobi.sep.probuf.RobotApiProtos.PRobotNotifyMsg;
import com.blemobi.sep.probuf.RobotApiProtos.PRobotNotifyMsgList;
import com.blemobi.sep.probuf.RobotApiProtos.PRobotRawNotifyMsg;
import com.blemobi.sep.probuf.RobotProtos.ERobotPushType;

import lombok.extern.log4j.Log4j;

/**
 * @ClassName GiftLotteryServiceImpl
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月22日 下午4:06:25
 * @version 1.0.0
 */
@Log4j
@Service("giftLotteryService")
public class GiftLotteryServiceImpl implements GiftLotteryService {

    @Autowired
    private GiftLotteryDao giftLotteryDao;
    @Autowired
    private JedisDao jedisDao;
    private static final Integer LIMIT_SIZE = 10000;

    private List<PGiftInfo> builderGiftList(List<String> idList, List<String> nmList, List<Integer> cntList) {
        List<PGiftInfo> list = new ArrayList<PGiftInfo>();
        int idx = 0;
        for (String nm : nmList) {
            PGiftInfo.Builder b = PGiftInfo.newBuilder();
            if (idList != null) {
                b.setGiftId(idList.get(idx));
            }
            b.setGiftNm(nm);
            b.setGiftCnt(cntList.get(idx));
            idx++;
            list.add(b.build());
        }
        return list;
    }

    @Override
    public PMessage shuffle(String uuid, String title, Long overdueTm, Integer winners, Integer locCnt,
            List<String> regions, String remark, Integer gender, List<String> giftNm, List<Integer> giftCnt) {
        Integer times = jedisDao.getUserLotteryRefreshTimes(uuid);
        if (times > 0) {
            log.debug("UUID:[" + uuid + "]操作过于频繁，稍后再试.");
            throw new BizException(2105005, "操作过于频繁，稍后再试");
        }
        log.debug("获取查询粉丝的grpc接口");
        DataPublishGrpcClient client = new DataPublishGrpcClient();
        log.debug("获取查询粉丝的grpc接口：" + client);
        List<String> uuidList = client.getFansByFilters(gender, regions, uuid, LIMIT_SIZE);
        if (uuidList == null || uuidList.size() < winners) {
            log.debug("UUID:[" + uuid + "]粉丝数量不够.");
            throw new BizException(2105003, "粉丝数量不够");
        }
        PGiftLotteryDetail.Builder detailBuilder = PGiftLotteryDetail.newBuilder();
        List<PUserBaseGiftEx> winnersList = ShuffleUtils.shuffle(uuidList, winners);
        detailBuilder.setGender(gender).addAllGifts(builderGiftList(null, giftNm, giftCnt)).setOverdueTm(overdueTm)
                .addAllRegions(regions).setRemark(remark).setTitle(title).setWinners(winners)
                .addAllUserList(winnersList).setCrtTm(System.currentTimeMillis());
        jedisDao.setUserLotteryRefreshTimes(uuid);
        return ReslutUtil.createReslutMessage(detailBuilder.build());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PMessage confirm(String uuid, String title, Long overdueTm, Integer winners, Integer locCnt,
            List<String> regions, String remark, Integer gender, List<String> gift, List<Integer> giftCnt,
            List<String> uuidList, List<Integer> genderList, List<String> regionList) {
        RobotGrpcClient robotClient = new RobotGrpcClient();
        PPayOrderParma oparam = PPayOrderParma.newBuilder().setAmount(1).setServiceNo(OrderEnum.LUCK_DRAW.getValue())
                .build();
        String orderno = robotClient.generateOrder(oparam).getVal();
        Long currTm = DateTimeUtils.currTime();
        // id, title, gender, remark, area_cnt, overdue_tm, uuid, status, winners, remain_cnt, crt_tm, upd_tm
        Object[] lotteryParam = new Object[] {orderno, title, gender, remark, locCnt, overdueTm, uuid, 1, winners,
                winners, currTm, currTm };
        log.debug(
                "新增实物抽奖参数:(id, title, gender, remark, area_cnt, overdue_tm, uuid, status, winners, remain_cnt, crt_tm, upd_tm)"
                        + StringUtils.join(lotteryParam, ","));
        int ret = giftLotteryDao.saveLottery(lotteryParam);
        if (ret != 1) {
            log.error("insert into gift lottery table failed");
            throw new RuntimeException("插入抽奖表失败");
        }
        if (regions != null && !regions.isEmpty()) {
            List<Object[]> locationList = new ArrayList<Object[]>();
            for (String cd : regions) {
                Object[] locationParam = new Object[] {orderno, cd, " " };
                locationList.add(locationParam);
            }
            ret = giftLotteryDao.saveLocations(locationList);
            if (ret != regions.size()) {
                log.error("insert into gift location table failed");
                throw new RuntimeException("插入抽奖位置表失败");
            }
        }
        List<String> idList = new ArrayList<>();
        if (gift != null && giftCnt != null && !gift.isEmpty() && gift.size() == giftCnt.size()) {
            List<Object[]> giftList = new ArrayList<>();
            for (int idx = 0; idx < gift.size(); idx++) {
                // id, lottery_id, gift_nm, gift_cnt, remain_cnt, crt_tm, overdue_tm, upd_tm, sort
                String id = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                for (int j = 0; j < giftCnt.get(idx); j++) {
                    idList.add(id);
                }
                Object[] giftParam = new Object[] {id, orderno, gift.get(idx), giftCnt.get(idx), giftCnt.get(idx),
                        currTm, overdueTm, currTm, idx };
                giftList.add(giftParam);
            }
            ret = giftLotteryDao.saveGifts(giftList);
            if (ret != gift.size()) {
                log.error("insert into gift table failed");
                throw new RuntimeException("插入抽奖礼物表失败");
            }
        } else {
            throw new BizException(2105008, "请至少填写一个奖品信息！");
        }
        if (uuidList != null && genderList != null && regionList != null && !uuidList.isEmpty()
                && uuidList.size() == genderList.size() && uuidList.size() == regionList.size()
                && uuidList.size() == idList.size()) {
            // gender, loc_cd, uuid, lottery_id, gift_id,
            List<Object[]> winnerList = new ArrayList<>();
            for (int idx = 0; idx < uuidList.size(); idx++) {
                Object[] winnerParam = new Object[] {genderList.get(idx), regionList.get(idx), uuidList.get(idx),
                        orderno, idList.get(idx) };
                winnerList.add(winnerParam);
            }
            ret = giftLotteryDao.saveWinners(winnerList);
            if (ret != uuidList.size()) {
                log.error("insert into winners table failed");
                throw new RuntimeException("插入抽奖中奖者表失败");
            }
        } else {
            log.error("中奖者数量与礼物数量不一致");
            throw new RuntimeException("中奖者数量与礼物数量不一致");
        }
        DataPublishGrpcClient client = new DataPublishGrpcClient();
        client.saveFans(orderno, gender, regions, uuid, Constants.TABLE_NAMES.LOTTERY_TB.getValue()); // 通知GO 存储抽奖参与者
        // 发送通知
        remark = "恭喜！天降神秘大奖，赶紧去领吧！";
        PushMsgHelper pushMgr = new PushMsgHelper(uuid, orderno, uuidList, remark);
        pushMgr.lotteryMsg(ERobotPushType.GiftLottery);
        return ReslutUtil.createSucceedMessage();
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.service.GiftLotteryService#accept(java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PMessage accept(String uuid, String lotteryId) {
        long currTm = DateTimeUtils.currTime();
        Map<String, Object> lottery = giftLotteryDao.queryLottery(lotteryId);
        Long expTm = Long.parseLong(lottery.get("overdue_tm").toString());
        int status = Integer.parseInt(lottery.get("status").toString());
        int ret = DateTimeUtils.compare(expTm, currTm);
        if (ret < 0 || status == 4) {
            log.error("领奖包已过期");
            throw new RuntimeException("领奖包已过期");
        }
        Object[] winnerParam = new Object[] {uuid, lotteryId };
        Map<String, Object> winnerInfo = giftLotteryDao.queryWinner(winnerParam);
        Integer wStatus = Integer.parseInt(winnerInfo.get("status").toString());
        if (wStatus.intValue() != -1) {
            log.error("你已领奖，不能重复领奖");
            throw new RuntimeException("你已领奖，不能重复领奖");
        }
        int remainCnt = Integer.parseInt(lottery.get("remain_cnt").toString());
        if (remainCnt >= 1) {
            remainCnt--;
        } else {
            log.error("领奖失败，数量有误");
            throw new RuntimeException("领奖失败，数量有误");
        }
        if (remainCnt > 0) {
            status = 2;
        } else {
            status = 3;
        }
        // status = ?, remain_cnt = ?, upd_tm = ? WHERE id = ? AND uuid = ? AND overdue_tm > ?
        Object[] lotteryparam = new Object[] {status, remainCnt, currTm, lotteryId, currTm };
        ret = giftLotteryDao.updateLottery(lotteryparam);
        if (ret != 1) {
            log.error("更新实物抽奖表异常");
            throw new RuntimeException("更新实物抽奖表异常");
        }

        String giftId = winnerInfo.get("gift_id").toString();
        Object[] giftParam = new Object[] {giftId, lotteryId };
        Map<String, Object> giftInfo = giftLotteryDao.queryGift(giftParam);
        remainCnt = Integer.parseInt(giftInfo.get("remain_cnt").toString());
        if (remainCnt >= 1) {
            remainCnt--;
        } else {
            log.error("领奖失败，数量有误");
            throw new RuntimeException("领奖失败，数量有误");
        }
        Object[] giftUpdParam = new Object[] {remainCnt, currTm, giftId, lotteryId };

        ret = giftLotteryDao.updateGift(giftUpdParam);
        if (ret != 1) {
            log.error("更新gift表异常");
            throw new RuntimeException("更新gift表异常");
        }
        Integer winnerId = Integer.parseInt(winnerInfo.get("id").toString());
        ret = giftLotteryDao.updateWinner(winnerId);
        if (ret != 1) {
            log.error("更新winner表异常");
            throw new RuntimeException("更新winner表异常");
        }
        return detail(lotteryId);
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.service.GiftLotteryService#list(java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    public PMessage list(String uuid, String keywords, Integer start) {
        List<Map<String, Object>> list = giftLotteryDao.historyLotteries(uuid, keywords, start);
        PLotteryList.Builder builder = PLotteryList.newBuilder();

        if (list != null && !list.isEmpty()) {
            List<PLotterySingle> sList = new ArrayList<PLotterySingle>();
            for (Map<String, Object> map : list) {
                PLotterySingle.Builder b = PLotterySingle.newBuilder();
                b.setCrtTm(Long.parseLong(map.get("crt_tm").toString()));
                b.setTitle(map.get("title").toString());
                b.setWinners(Integer.parseInt(map.get("winners").toString()));
                String lotteryId = map.get("id").toString();
                b.setLotteryId(lotteryId);
                Long ot = Long.parseLong(map.get("overdue_tm").toString());
                Boolean in24 = DateTimeUtils.in24Hours1(ot, System.currentTimeMillis());
                log.debug("lotteryId->" + lotteryId + ", in24->" + in24);
                if (in24) {
                    b.setIn24Hours(in24);
                }
                List<String> uuidList = giftLotteryDao.lotteryTop5WinnerList(lotteryId);
                if (uuidList != null && !uuidList.isEmpty()) {
                    List<PUserBase> uList = new ArrayList<PUserBase>();
                    for (String u : uuidList) {
                        try {
                            PUserBase userBase = UserBaseCache.get(u);
                            uList.add(userBase);
                        } catch (IOException e) {
                            log.error("uuid:[" + u + "]在缓存中没有找到");
                            throw new RuntimeException("用户没有找到");
                        }

                    }
                    b.addAllUserList(uList);
                }
                sList.add(b.build());
            }
            builder.addAllLotteries(sList);
        }
        return ReslutUtil.createReslutMessage(builder.build());
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.service.GiftLotteryService#detail(java.lang.String, java.lang.String)
     */
    @Override
    public PMessage detail(String lotteryId) {
        PGiftLotteryDetail.Builder builder = PGiftLotteryDetail.newBuilder();
        Map<String, Object> lotteryInfo = giftLotteryDao.queryLottery(lotteryId);
        builder.setGender(Integer.parseInt(lotteryInfo.get("gender").toString())).setLotteryId(lotteryId)
                .setOverdueTm(Long.parseLong(lotteryInfo.get("overdue_tm").toString()))
                .setRemainCnt(Integer.parseInt(lotteryInfo.get("remain_cnt").toString()))
                .setRemark(lotteryInfo.get("remark").toString())
                .setStatus(Integer.parseInt(lotteryInfo.get("status").toString()))
                .setTitle(lotteryInfo.get("title").toString())
                .setWinners(Integer.parseInt(lotteryInfo.get("winners").toString()))
                .setRegionCnt(Integer.parseInt(lotteryInfo.get("area_cnt").toString()))
                .setCrtTm(Long.parseLong(lotteryInfo.get("crt_tm").toString()));
        List<String> locList = giftLotteryDao.lotteryLocList(lotteryId);
        if (locList != null && !locList.isEmpty()) {
            builder.addAllRegions(locList);
        }
        List<Map<String, Object>> giftList = giftLotteryDao.lotteryGiftList(lotteryId);
        Map<String, PGiftInfo> gmap = new HashMap<String, PGiftInfo>();
        if (giftList != null && !giftList.isEmpty()) {
            List<PGiftInfo> gList = new ArrayList<PGiftInfo>();
            for (Map<String, Object> gift : giftList) {
                PGiftInfo.Builder gb = PGiftInfo.newBuilder();
                gb.setGiftCnt(Integer.parseInt(gift.get("gift_cnt").toString())).setGiftId(gift.get("id").toString())
                        .setGiftNm(gift.get("gift_nm").toString());
                PGiftInfo g = gb.build();
                gmap.put(gift.get("id").toString(), g);
                gList.add(g);
            }
            builder.addAllGifts(gList);
        }
        List<Map<String, Object>> winnerList = giftLotteryDao.lotteryWinnerList(lotteryId);
        if (winnerList != null && !winnerList.isEmpty()) {
            List<PUserBaseGiftEx> wList = new ArrayList<PUserBaseGiftEx>();
            for (Map<String, Object> winner : winnerList) {
                PUserBaseGiftEx.Builder ub = PUserBaseGiftEx.newBuilder();
                String uid = winner.get("uuid").toString();
                PUserBase userBase = null;
                try {
                    userBase = UserBaseCache.get(uid);
                } catch (IOException e) {
                    log.error("uuid:[" + uid + "]在缓存中没有找到");
                    throw new RuntimeException("用户没有找到");
                }
                ub.setBrcvAddr(winner.get("b_rcv_addr").toString()).setBrcvEmail(winner.get("b_rcv_email").toString())
                        .setBrcvNm(winner.get("b_rcv_nm").toString()).setBrcvPhone(winner.get("b_rcv_phone").toString())
                        .setBrcvRemark(winner.get("b_rcv_remark").toString())
                        .setEditCnt(Integer.parseInt(winner.get("edit_cnt").toString()))
                        .setGift(gmap.get(winner.get("gift_id").toString())).setInfo(userBase)
                        .setRcvAddr(winner.get("rcv_addr").toString()).setRcvEmail(winner.get("rcv_email").toString())
                        .setRcvNm(winner.get("rcv_nm").toString()).setRcvPhone(winner.get("rcv_phone").toString())
                        .setRcvRemark(winner.get("rcv_remark").toString()).setRegion(winner.get("loc_cd").toString())
                        .setStatus(Integer.parseInt(winner.get("status").toString()))
                        .setAcceptTm(Long.parseLong(winner.get("accept_tm").toString()));
                wList.add(ub.build());
            }
            builder.addAllUserList(wList);
        }
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    public PMessage view(String lotteryId) {
        log.debug("查看抽奖包详情:" + lotteryId);
        Map<String, Object> lotteryInfo = giftLotteryDao.queryLottery(lotteryId);
        String title = lotteryInfo.get("title").toString();
        String remark = lotteryInfo.get("remark").toString();
        Long overdue = Long.parseLong(lotteryInfo.get("overdue_tm").toString());
        Integer status = Integer.parseInt(lotteryInfo.get("status").toString());
        PGiftLotteryDetail.Builder builder = PGiftLotteryDetail.newBuilder();
        builder.setTitle(title).setRemark(remark).setOverdueTm(overdue).setStatus(status)
                .setCrtTm(Long.parseLong(lotteryInfo.get("crt_tm").toString()));
        return ReslutUtil.createReslutMessage(builder.build());
    }

    @Override
    public PMessage delete(String uuid, List<String> lotteryId) {
        int ret = giftLotteryDao.delete(uuid, lotteryId);
        if (ret < lotteryId.size()) {
            throw new BizException(2105018, "礼物抽奖记录截止日期一个月之后才能被删除！删除取消。");
        }
        return ReslutUtil.createSucceedMessage();
    }

    @Override
    public PMessage remind(String uuid, String lotteryId, List<String> uuidList) {
        RobotGrpcClient client = new RobotGrpcClient();
        PRobotNotifyMsgList.Builder builder = PRobotNotifyMsgList.newBuilder();
        PRobotNotifyMsg.Builder rnmBuilder = PRobotNotifyMsg.newBuilder();
        PRobotRawNotifyMsg.Builder rrnmBuilder = PRobotRawNotifyMsg.newBuilder();
        PBLotteryNotifyMsg.Builder lnmBuilder = PBLotteryNotifyMsg.newBuilder();
        PUserBase userBase = null;
        try {
            userBase = UserBaseCache.get(uuid);
        } catch (IOException e) {
            log.error("uuid:[" + uuid + "]在缓存中没有找到");
            throw new RuntimeException("用户没有找到");
        }
        //lnmBuilder.setOrdNo(lotteryId).setText(String.format("%s给你发了一个领奖提醒", userBase.getNickname()));
        lnmBuilder.setOrdNo(lotteryId).setText("你有一个大奖未领取哦");
        rrnmBuilder.setLottery(lnmBuilder.build());
        rnmBuilder.addAllTo(uuidList).setFrom(uuid).setMsgType(ERobotPushType.LotteryRemind)
                .setContent(rrnmBuilder.build());
        builder.addList(rnmBuilder.build());
        client.push(builder.build());
        return ReslutUtil.createSucceedMessage();
    }

    @Override
    public PMessage edit(String uuid, String lotteryId, String uuid1, String rcvNm, String rcvAddr, String rcvPhone,
            String rcvEmail, String rcvRemark) {
        Boolean isSelf = uuid.equals(uuid1);
        boolean f1 = false, f2 = false, f3 = false;
        if (StringUtils.isBlank(rcvNm) || StringUtils.isBlank(rcvAddr) || StringUtils.isBlank(rcvPhone)) {
            if (StringUtils.isBlank(rcvNm)) {
                rcvNm = " ";
            }
            if (StringUtils.isBlank(rcvAddr)) {
                rcvAddr = " ";
            }
            if (StringUtils.isBlank(rcvPhone)) {
                rcvPhone = " ";
            }
            f1 = true;
        }
        if (StringUtils.isBlank(rcvEmail)) {
            if (StringUtils.isBlank(rcvEmail)) {
                rcvEmail = " ";
            }
            f2 = true;
        }
        if (StringUtils.isBlank(rcvRemark)) {
            if (StringUtils.isBlank(rcvRemark)) {
                rcvRemark = " ";
            }
            f3 = true;
        }
        if (f1 && f2 && f3) {
            throw new BizException(2105016, "请至少输入收货信息、邮箱、留言中的一种");
        }
        Map<String, Object> wInfo = giftLotteryDao.queryWinner(new Object[] {uuid1, lotteryId });
        Integer status = Integer.parseInt(wInfo.get("status").toString());
        Integer editCnt = Integer.parseInt(wInfo.get("edit_cnt").toString());
        if (isSelf) {
            if (editCnt.intValue() > 2) {
                throw new BizException(2105015, "更新次数超出限制");
            }
            editCnt++;
//            String rcv_nm = wInfo.get("rcv_nm").toString();
//            String rcv_phone = wInfo.get("rcv_phone").toString();
//            String rcv_addr = wInfo.get("rcv_addr").toString();
//            String rcv_email = wInfo.get("rcv_email").toString();
//            String rcv_remark = wInfo.get("rcv_remark").toString();
//            String b_rcv_nm = wInfo.get("b_rcv_nm").toString();
//            String b_rcv_phone = wInfo.get("b_rcv_phone").toString();
//            String b_rcv_addr = wInfo.get("b_rcv_addr").toString();
//            String b_rcv_email = wInfo.get("b_rcv_email").toString();
//            String b_rcv_remark = wInfo.get("b_rcv_remark").toString();
            String giftId = wInfo.get("gift_id").toString();
//            if (!rcv_nm.equals(b_rcv_nm) || !rcv_phone.equals(b_rcv_phone) || !rcv_addr.equals(b_rcv_addr)
//                    || !rcv_email.equals(b_rcv_email) || !rcv_remark.equals(b_rcv_remark)) {
//                status = 3;
//            } else {
//                status = 1;
//            }
            if(editCnt > 1){
            	status = 3;
            }
            Map<String, Object> lottery = giftLotteryDao.queryLottery(lotteryId);
            String uid = lottery.get("uuid").toString();
            Map<String, Object> gift = giftLotteryDao.queryGift(new Object[] {giftId, lotteryId });
            NotifyGrpcClient client = new NotifyGrpcClient();
            PNotifyInternalMessageList.Builder builder = PNotifyInternalMessageList.newBuilder();
            PNotifyInternalMessage.Builder nimBuilder = PNotifyInternalMessage.newBuilder();
            PNotifyMessage.Builder nmBuilder = PNotifyMessage.newBuilder();
            PNotifySimple.Builder nsBuilder = PNotifySimple.newBuilder();
            PNotifyRawMessage.Builder nrmBuilder = PNotifyRawMessage.newBuilder();
            String uri = String.format(
                    "payment://lottery/editrcv?uuid=%s&lottery_id=%s&gift_nm=%s&rcv_nm=%s&rcv_addr=%s&rcv_phone=%s&rcv_email=%s&rcv_remark=%s",
                    uuid, lotteryId, gift.get("gift_nm").toString(), rcvNm, rcvAddr, rcvPhone, rcvEmail, rcvRemark);
            nsBuilder.setUri(uri);
            PUserBase userBase = null;
            try {
                userBase = UserBaseCache.get(uuid);
            } catch (IOException e) {
                log.error("uuid:[" + uuid + "]在缓存中没有找到");
                throw new RuntimeException("用户没有找到");
            }
            String desc = "你的粉丝" + userBase.getNickname() + "更新了领奖收货资料，请注意查看。";
            nrmBuilder.setContent(desc).setSimple(nsBuilder.build());
            nmBuilder.setType(ENotifyType.SimpleMessage).setContent(nrmBuilder.build());
            nimBuilder.setService("payment").setStateless(true).addRecipient(uid).setMessage(nmBuilder.build());
            builder.addList(nimBuilder.build());
            client.send(builder.build());
        }
        giftLotteryDao.updateLoc(rcvNm, rcvAddr, rcvPhone, rcvEmail, rcvRemark, isSelf, editCnt, status, lotteryId,
                uuid1);
        return ReslutUtil.createSucceedMessage();
    }

    @Override
    public List<Map<String, Object>> queryForIn24HoursLotteries() {
        return giftLotteryDao.queryForIn24HoursLotteries();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void notfiyUser(String uuid, String lotteryId, String title) {
        List<String> uuidList = giftLotteryDao.queryWinners(lotteryId);
        giftLotteryDao.updNotifyCnt(lotteryId);
        // 通知网红发货提醒
        NotifyGrpcClient client = new NotifyGrpcClient();
        PNotifyInternalMessageList.Builder builder = PNotifyInternalMessageList.newBuilder();
        PNotifyInternalMessage.Builder nimBuilder = PNotifyInternalMessage.newBuilder();
        PNotifyMessage.Builder nmBuilder = PNotifyMessage.newBuilder();
        PNotifySimple.Builder nsBuilder = PNotifySimple.newBuilder();
        PNotifyRawMessage.Builder nrmBuilder = PNotifyRawMessage.newBuilder();
        String uri = String.format("payment://lottery/shipping?lottery_id=%s", lotteryId);
        nsBuilder.setUri(uri);
        String desc = "你的抽奖活动“" + title + "”24小时后到截止日期，别忘记发货哦。";
        nrmBuilder.setContent(desc).setSimple(nsBuilder.build());
        nmBuilder.setType(ENotifyType.SimpleMessage).setContent(nrmBuilder.build())
                .setTime((System.currentTimeMillis() / 1000));
        nimBuilder.setService("payment").setStateless(true).addRecipient(uuid).setMessage(nmBuilder.build());
        builder.addList(nimBuilder.build());
        client.send(builder.build());

        // 提醒还未领奖者领奖
        if (uuidList != null && !uuidList.isEmpty()) {
            //desc = "你有一个24小时内过期的抽奖活动未领取，赶紧去看看。";
        	desc = "你有一个24小时内过期的抽奖活动未领取。";
            PushMsgHelper push = new PushMsgHelper("", lotteryId, uuidList, desc);
            try {
                push.pushOver(desc);
            } catch (IOException e) {
                log.error("通知中奖者出现异常");
                throw new RuntimeException("通知中奖者出现异常");
            }
            PushMsgHelper pushMgr = new PushMsgHelper(uuid, lotteryId, uuidList, desc);
            pushMgr.lotteryMsg(ERobotPushType.LotteryExpire);
        }

    }

    @Override
    public List<Map<String, Object>> queryForExpLotteries() {
        return giftLotteryDao.queryForExpLotteries();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updExp(String lotteryId, Integer status) {
        giftLotteryDao.updExp(lotteryId, status);
    }
}
