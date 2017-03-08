/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.rest
 *
 *    Filename:    LotteryProcess.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月18日 下午12:05:45
 *
 *    Revision:
 *
 *    2017年2月18日 下午12:05:45
 *
 *****************************************************************/
package com.blemobi.payment.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;

import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.service.LotteryService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryConfirm;
import com.blemobi.sep.probuf.PaymentProtos.PShuffle;
import com.blemobi.sep.probuf.PaymentProtos.PUserBaseEx;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * @ClassName LotteryProcess

import com.pakulov.jersey.protobu
 * @Description 抽奖接口
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:05:45
 * @version 1.0.0
 */
@Path("v1/payment/lottery")
public class LotteryProcess {

    // @Autowired
    private LotteryService lotteryService = InstanceFactory.getInstance(LotteryService.class);

    /**
     * @Description 创建新抽奖
     * @author HUNTER.POON
     * @param uuid
     *            用户uuid
     * @param token
     *            令牌
     * @param lottery
     *            抽奖包
     * @return
     */
    // @POST
    // @Path("shuffle")
    // @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    // public PMessage shuffleLottery(@CookieParam("uuid") String uuid, @CookieParam("token") String token, PShuffle
    // shuffle) {
    // return lotteryService.shuffleLottery(uuid, shuffle);
    // }

    @POST
    @Path("shuffle")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage shuffleLottery(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("title") String title, @FormParam("winners") Integer winners, @FormParam("region") String region,
            @FormParam("remark") String remark, @FormParam("gender") Integer gender, @FormParam("bonus") Integer bonus,
            @FormParam("totAmt") Integer totAmt) {
        PShuffle.Builder builder = PShuffle.newBuilder().setTitle(title).setWinners(winners).setRemark(remark).setGender(gender)
                .setBonus(bonus).setTotAmt(totAmt);
        if(!StringUtils.isEmpty(region)){
            builder.addAllRegion(Arrays.asList(region.split(",")));
        }
        return lotteryService.shuffleLottery(uuid, builder.build());
    }

    /**
     * @Description 确认新抽奖
     * @author HUNTER.POON
     * @param uuid
     *            用户uuid
     * @param token
     *            令牌
     * @param lottery
     *            抽奖包
     * @return
     */
    @POST
    @Path("confirm")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage confirmLottery(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("title") String title, @FormParam("winners") Integer winners, @FormParam("region") String region,
            @FormParam("remark") String remark, @FormParam("gender") Integer gender, @FormParam("bonus") Integer bonus,
            @FormParam("totAmt") Integer totAmt, @FormParam("uuid") String uuids, @FormParam("genders") String genders, @FormParam("regions") String regions) {
        PLotteryConfirm.Builder builder = PLotteryConfirm.newBuilder();
        builder.setTitle(title).setWinners(winners).setRemark(remark).setGender(gender).setBonus(bonus).setTotAmt(totAmt);
        if(!StringUtils.isEmpty(region)){
            builder.addAllRegion(Arrays.asList(region.split(",")));
        }
        if(!StringUtils.isEmpty(uuids) && !StringUtils.isEmpty(genders) && !StringUtils.isEmpty(regions)){
            String[] uArr = uuids.split(",");
            String[] gArr = genders.split(",");
            String[] lArr = regions.split(",");
            if(uArr.length != gArr.length || uArr.length != lArr.length){
                throw new BizException(2017000, "中奖者名单有误");
            }
            List<PUserBaseEx> ue = new ArrayList<PUserBaseEx>();
            int idx = 0;
            for(String uid : uArr){
                PUserBaseEx.Builder ub = PUserBaseEx.newBuilder();
                PUserBase u = PUserBase.newBuilder().setUUID(uid).build();
                ub.setGender(Integer.parseInt(gArr[idx]));
                ub.setAmt(bonus);
                ub.setInfo(u);
                ub.setRegion(lArr[idx]);
                ue.add(ub.build());
                idx++;
            }
            builder.addAllUserList(ue);
        }else {
            throw new BizException(2017000, "中奖者名单有误");
        }
        PMessage ret = lotteryService.createLottery(uuid, builder.build());
        return ret;
    }
    
//    @POST
//    @Path("confirm")
//    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
//    public PMessage confirmLottery(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
//            PLotteryConfirm lottery) {
//        PMessage ret = lotteryService.createLottery(uuid, lottery);
//        return ret;
//    }

    /**
     * @Description 中奖者领奖
     * @author HUNTER.POON
     * @param uuid
     *            用户uuid
     * @param token
     *            令牌
     * @param prize
     *            抽奖包
     * @return
     */
    @POST
    @Path("accept")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage acceptPrize(@CookieParam("uuid") String uuid, @CookieParam("token") String token, @FormParam("lotteryId") String lotteryId) {
        PMessage ret = lotteryService.acceptPrize(uuid, lotteryId);
        return ret;
    }
//    @POST
//    @Path("accept")
//    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
//    public PMessage acceptPrize(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
//            PAcceptPrize prize) {
//        PMessage ret = lotteryService.acceptPrize(uuid, prize.getLotteryId());
//        return ret;
//    }

    /**
     * @Description B端删除发奖记录
     * @author HUNTER.POON
     * @param uuid
     *            用户uuid
     * @param token
     *            令牌
     * @param lotteryDel
     *            抽奖包
     * @return
     */
    
    @POST
    @Path("delete")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage delete(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("lotteryId") String lotteryId) {
        PMessage ret = lotteryService.delPrize(uuid, Arrays.asList(lotteryId.split(",")));
        return ret;
    }
//    @POST
//    @Path("delete")
//    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
//    public PMessage delete(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
//            PLotteryDel lotteryDel) {
//        PMessage ret = lotteryService.delPrize(uuid, lotteryDel.getLotteryIdList());
//        return ret;
//    }

    /**
     * @Description 抽奖包列表查询（最近一个月）
     * @author HUNTER.POON
     * @param uuid
     *            当前用户UUID
     * @param token
     *            令牌
     * @param startIndex
     *            开始下标
     * @param size
     *            结果集大小
     * @param keywords
     *            关键字
     * @return
     */
    @GET
    @Path("list")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage lotteryList(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @QueryParam("startIndex") int startIndex, @QueryParam("keywords") String keywords) {
        PMessage ret = lotteryService.lotteryList(uuid, startIndex, keywords);
        return ret;
    }

    /**
     * @Description 抽奖包详情
     * @author HUNTER.POON
     * @param uuid
     *            用户UUID
     * @param token
     *            令牌
     * @param lotteryId
     *            抽奖包ID
     * @param keywords
     *            中奖者人名关键字
     * @param type
     *            中奖者类型（0:全部，1:男，2:女）
     * @return
     */
    @GET
    @Path("detail")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage lotteryDetail(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @QueryParam("lotteryId") String lotteryId) {
        PMessage ret = lotteryService.lotteryDetail(lotteryId);
        return ret;
    }

}
