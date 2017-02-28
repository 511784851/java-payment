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

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.payment.service.LotteryService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.PaymentProtos.PAcceptPrize;
import com.blemobi.sep.probuf.PaymentProtos.PLottery;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * @ClassName LotteryProcess
 * @Description 抽奖接口
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:05:45
 * @version 1.0.0
 */
@Path("v1/payment/lottery")
public class LotteryProcess {

    //@Autowired
    private LotteryService lotteryService = InstanceFactory.getInstance(LotteryService.class);

    /**
     * @Description 创建新抽奖 
     * @author HUNTER.POON
     * @param uuid 用户uuid
     * @param token 令牌
     * @param lottery 抽奖包
     * @return
     */
    @POST
    @Path("create")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage createLottery(@CookieParam("uuid") String uuid, @CookieParam("token") String token, PLottery lottery) {
        //TODO 数据校验
        PMessage ret = lotteryService.createLottery(uuid, lottery);
        return ret;
    }
    
    
    /**
     * @Description 中奖者领奖 
     * @author HUNTER.POON
     * @param uuid 用户uuid
     * @param token 令牌
     * @param lottery 抽奖包
     * @return
     */
    @POST
    @Path("accept")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF) 
    public PMessage acceptPrize(@CookieParam("uuid") String uuid, @CookieParam("token") String token, PAcceptPrize prize) {
        //TODO prd to be removed
        uuid = "1468419313301436965";
        PMessage ret = lotteryService.acceptPrize(uuid, prize.getLotteryId());
        return ret;
    }
    
    /**
     * @Description 抽奖包列表查询（最近一个月）
     * @author HUNTER.POON
     * @param uuid 当前用户UUID
     * @param token 令牌
     * @param startIndex 开始下标
     * @param size 结果集大小
     * @param keywords 关键字
     * @return
     */
    @GET
    @Path("list")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage lotteryList(@CookieParam("uuid") String uuid, @CookieParam("token") String token, @QueryParam("startIndex") int startIndex, @QueryParam("size") int size, @QueryParam("keywords") String keywords) {
        //TODO PRD TO BE REMOVED
        uuid = "123";
        PMessage ret = lotteryService.lotteryList(uuid, keywords, startIndex, size);
        return ret;
    }
    
    /**
     * @Description 抽奖包详情
     * @author HUNTER.POON
     * @param uuid 用户UUID
     * @param token  令牌
     * @param lotteryId 抽奖包ID
     * @param keywords 中奖者人名关键字
     * @param type 中奖者类型（0:全部，1:男，2:女）
     * @return
     */
    @GET
    @Path("detail")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage lotteryDetail(@CookieParam("uuid") String uuid, @CookieParam("token") String token, @QueryParam("lotteryId") String lotteryId, @QueryParam("keywords") String keywords, @QueryParam("type") int type) {
        PMessage ret = lotteryService.lotteryDetail(lotteryId, keywords, type);
        return ret;
    }
    
}
