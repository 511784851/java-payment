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
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PLocation;
import com.blemobi.sep.probuf.PaymentProtos.PLottery;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryDetailRet;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryListRet;
import com.blemobi.sep.probuf.PaymentProtos.PLotterySingleRet;
import com.blemobi.sep.probuf.PaymentProtos.PRedPay;
import com.blemobi.sep.probuf.PaymentProtos.PUserBaseEx;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * @ClassName LotteryServiceImpl
 * @Description 抽奖业务类
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:07:19
 * @version 1.0.0
 */
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
        long currTm = System.currentTimeMillis();
        // 生成订单号
        // String orderno = OrdernoUtil.build(uuid, currTm, amount);
        String orderno = (currTm + "").substring(6);
        uuid = "123";
        Object[] params = new Object[] {orderno, lottery.getTitle(), lottery.getType(), lottery.getWinners(), lottery.getTotAmt(),
                lottery.getTotAmt(), 1, uuid, currTm, currTm };
        int ret = lotteryDao.createLottery(params);
        if (ret != 1) {
            throw new RuntimeException("创建抽奖失败，请重试。");
        }
        if(lottery.getLocsCount() > 0){
            List<Object[]> param = new ArrayList<Object[]>();
            ret = lotteryDao.createLotteryLoc(param);
            for(PLocation loc : lottery.getLocsList()){
                Object[] arr = new Object[3];
                arr[0] = orderno;
                arr[1] = loc.getLocCd();
                arr[2] = loc.getLocNm();
                param.add(arr);
            }
            ret = lotteryDao.createLotteryLoc(param);
            if(ret != lottery.getLocsCount()){
                throw new RuntimeException("添加抽奖位置失败，请重试。");
            }
        }
        if(lottery.getUuidListCount() > 0){
            List<Object[]> param = new ArrayList<Object[]>();
            //uuid, lottery_id, nick_nm, sex, bonus, status, crt_tm, accept_tm
            for(String uid : lottery.getUuidListList().toArray(new String[]{})){
                Object[] arr = new Object[8];
                arr[0] = uid;
                arr[1] = orderno;
                //TODO 从缓存中获取用户基本信息
                arr[2] = "11";
                arr[3] = 1;
                arr[4] = lottery.getBonus();
                arr[5] = 0;
                arr[6] = currTm;
                arr[7] = currTm;
                param.add(arr);
            }
            ret = lotteryDao.createWinners(param);
            if(ret != lottery.getUuidListCount()){
                throw new RuntimeException("添加中奖者失败，请重试。");
            }
        }
        PRedPay redPay = PRedPay.newBuilder().setOrderNum(orderno).setFenMoney(lottery.getTotAmt()).build();
        return ReslutUtil.createReslutMessage(redPay);
    }

    @Override
    public PMessage lotteryList(String keywords, int startIdx, int size) {
        List<Map<String, Object>> lotteriesList = lotteryDao.lotteryList(keywords, startIdx, size);
        PLotteryListRet.Builder builder = PLotteryListRet.newBuilder();
        if (lotteriesList != null) {
            for (Map<String, Object> entity : lotteriesList) {
                PLotterySingleRet.Builder sBuilder = PLotterySingleRet.newBuilder();
                List<String> uuids = lotteryDao.top5UUID(entity.get("id").toString());
                sBuilder.setCrtTm(entity.get("crt_tm").toString());
                sBuilder.setLotteryId(Integer.parseInt(entity.get("id").toString()));
                sBuilder.setTitle(entity.get("title").toString());
                sBuilder.setWinners(Integer.parseInt(entity.get("winners").toString()));
                // TODO 缓存中获取用户头像
                //sBuilder.addAllIcons();
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
            builder.setLotteryId(Integer.parseInt(lotteryId));
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
    
    public static void main(String[] args) {
        String ct = System.currentTimeMillis() + "";
        System.out.println(ct.substring(6));
    }
}
