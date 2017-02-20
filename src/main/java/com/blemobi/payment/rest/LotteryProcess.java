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

import java.math.BigDecimal;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;

import com.blemobi.payment.service.LotteryService;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

import lombok.extern.log4j.Log4j;

/**
 * @ClassName LotteryProcess
 * @Description 抽奖接口
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:05:45
 * @version 1.0.0
 */
@Log4j
@Path("payment/lottery")
public class LotteryProcess {

    @Autowired
    private LotteryService lotteryService;

    /**
     * @Description 创建新红包 
     * @author HUNTER.POON
     * @param uuid 创建人UUID
     * @param title 抽奖标题
     * @param type 参与类型（全部|男|女）
     * @param locations 位置
     * @param members 抽奖个数
     * @param amount 总金额
     * @return 
     */
    @PUT
    @Path("create")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage createLottery(@CookieParam("uuid") String uuid, @QueryParam("title") String title,
            @QueryParam("type") String type, @QueryParam("locations") String[] locations,
            @QueryParam("members") Integer members, @QueryParam("amount") BigDecimal amount) {
        log.debug("create new lottery bigin.");
        
        log.debug("create new lottery end.");
        return null;
    }
    
    /**
     * @Description 历史抽奖详情 
     * @author HUNTER.POON
     * @param uuid 当前人UUID
     * @param lotteryId 抽奖ID
     * @return
     */
    @GET
    @Path("detail")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage lotteryDetail(@CookieParam("uuid") String uuid, @QueryParam("lotteryId") String lotteryId) {
        log.debug("lottery:[" + lotteryId + "] detail begin.");
        
        log.debug("lottery:[" + lotteryId + "] detail end.");
        return null;
    }
    
}
