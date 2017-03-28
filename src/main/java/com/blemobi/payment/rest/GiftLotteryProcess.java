/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.rest
 *
 *    Filename:    GiftLotteryProcess.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月22日 下午2:11:10
 *
 *    Revision:
 *
 *    2017年3月22日 下午2:11:10
 *
 *****************************************************************/
package com.blemobi.payment.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;

import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.service.GiftLotteryService;
import com.blemobi.payment.util.DateTimeUtils;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

import lombok.extern.log4j.Log4j;

/**
 * @ClassName GiftLotteryProcess
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月22日 下午2:11:10
 * @version 1.0.0
 */
@Log4j
@Path("v1/payment/giftlottery")
public class GiftLotteryProcess {

    private GiftLotteryService lotteryService = InstanceFactory.getInstance(GiftLotteryService.class);

    private static void validation(String title, String remark, Long overdue) {
        if (StringUtils.isEmpty(title) || title.length() > 20) {
            log.debug("请输入标题，1-20个字符");
            throw new BizException(215006, "请输入标题，1-20个字符");
        }
        if (!StringUtils.isEmpty(remark) && remark.length() > 50) {
            log.debug("留言最多支持50个字符");
            throw new BizException(215011, "留言最多支持50个字符");
        }
        if (overdue == null) {
            log.debug("请输入截止日期");
            throw new BizException(215012, "请输入截止日期。");
        }
        long theLast = DateTimeUtils.calcTime(TimeUnit.DAYS, 30);
        if (overdue.longValue() < System.currentTimeMillis()
                || DateTimeUtils.compare(overdue.longValue(), theLast) == 1) {
            log.debug("截止日期最多可设置一个月以内");
            throw new BizException(215014, "截止日期最多可设置一个月以内");
        }
    }

    @POST
    @Path("shuffle")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage shuffleLottery(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("title") String title, @FormParam("overdueTm") Long overdueTm,
            @FormParam("winners") Integer winners, @FormParam("locCnt") Integer locCnt,
            @FormParam("regions") String regions, @FormParam("remark") String remark,
            @FormParam("gender") Integer gender, @FormParam("giftNm") String giftNm,
            @FormParam("giftCnt") String giftCnt) {
        log.debug("uuid:["+ uuid + "]shuffle");
        validation(title, remark, overdueTm);
        List<String> regionList = new ArrayList<String>();
        if (!StringUtils.isEmpty(regions)) {
            regionList = Arrays.asList(regions.split(","));
        }
        if (StringUtils.isEmpty(giftNm) || StringUtils.isEmpty(giftCnt)) {
            log.debug("uuid:["+ uuid + "]请至少填写一个奖品信息！");
            throw new BizException(215008, "请至少填写一个奖品信息！");
        }
        List<String> nmList = Arrays.asList(giftNm.split(","));
        if (nmList.size() > 10) {
            log.debug("uuid:["+ uuid + "]最多可设置10条奖项");
            throw new BizException(215013, "最多可设置10条奖项");
        }
        for (String str : nmList) {
            if (str.length() > 15 || str.length() < 2) {
                log.debug("uuid:["+ uuid + "]奖品名称仅支持2-15个字符");
                throw new BizException(215009, "奖品名称仅支持2-15个字符");
            }
        }
        String[] arr = giftCnt.split(",");
        List<Integer> cntList = new ArrayList<Integer>();
        for (String cnt : arr) {
            int c = Integer.parseInt(cnt);
            if (c < 1 || c > 9999) {
                log.debug("uuid:["+ uuid + "]正整数格式，范围1-9999");
                throw new BizException(215010, "正整数格式，范围1-9999");
            }
            cntList.add(c);
        }
        return lotteryService.shuffle(uuid, title, overdueTm, winners, locCnt, regionList, remark, gender, nmList,
                cntList);
    }

    @POST
    @Path("confirm")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage confirmLottery(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("title") String title, @FormParam("overdueTm") Long overdueTm,
            @FormParam("winners") Integer winners, @FormParam("locCnt") Integer locCnt,
            @FormParam("regions") String regions, @FormParam("remark") String remark,
            @FormParam("gender") Integer gender, @FormParam("giftNm") String giftNm,
            @FormParam("giftCnt") String giftCnt, @FormParam("uuidList") String uuidList,
            @FormParam("genderList") String genderList, @FormParam("regionList") String regionList) {
        validation(title, remark, overdueTm);
        List<String> regionLists = new ArrayList<String>();
        if (!StringUtils.isEmpty(regions)) {
            regionLists = Arrays.asList(regions.split(","));
        }
        if (StringUtils.isEmpty(giftNm) || StringUtils.isEmpty(giftCnt)) {
            throw new BizException(215008, "请至少填写一个奖品信息！");
        }
        List<String> nmList = Arrays.asList(giftNm.split(","));
        if (nmList.size() > 10) {
            throw new BizException(215013, "最多可设置10条奖项");
        }
        for (String str : nmList) {
            if (str.length() > 15 || str.length() < 2) {
                throw new BizException(215009, "奖品名称仅支持2-15个字符");
            }
        }
        String[] arr = giftCnt.split(",");
        List<Integer> cntList = new ArrayList<Integer>();
        for (String cnt : arr) {
            int c = Integer.parseInt(cnt);
            if (c < 1 || c > 9999) {
                throw new BizException(215010, "正整数格式，范围1-9999");
            }
            cntList.add(c);
        }

        List<String> uList = Arrays.asList(uuidList.split(","));
        List<Integer> gList = new ArrayList<Integer>();
        String[] gArr = genderList.split(",");
        for (String g : gArr) {
            gList.add(Integer.parseInt(g));
        }
        List<String> rList = Arrays.asList(regionList.split(","));
        return lotteryService.confirm(uuid, title, overdueTm, winners, locCnt, regionLists, remark, gender, nmList,
                cntList, uList, gList, rList);
    }

    @POST
    @Path("accept")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage acceptPrize(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("lotteryId") String lotteryId) {
        return lotteryService.accept(uuid, lotteryId);
    }

    @GET
    @Path("list")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage lotteryList(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @QueryParam("startIndex") int startIndex, @QueryParam("keywords") String keywords) {
        return lotteryService.list(uuid, keywords, startIndex);
    }

    @GET
    @Path("detail")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage lotteryDetail(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @QueryParam("lotteryId") String lotteryId) {
        return lotteryService.detail(lotteryId);
    }

    @GET
    @Path("view")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage view(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @QueryParam("lotteryId") String lotteryId) {
        return lotteryService.view(lotteryId);
    }


    @POST
    @Path("delete")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage delete(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("lotteryId") String lotteryId) {
        if(StringUtils.isEmpty(lotteryId)){
            log.debug("没有选择要删除的抽奖包");
            throw new RuntimeException("没有选择要删除的抽奖包");
        }
        return lotteryService.delete(uuid, Arrays.asList(lotteryId.split(",")));
    }

    @POST
    @Path("edit")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage edit(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("lotteryId") String lotteryId, @FormParam("uuid") String uuid1, @FormParam("rcvNm") String rcvNm,
            @FormParam("rcvAddr") String rcvAddr, @FormParam("rcvPhone") String rcvPhone,
            @FormParam("rcvEmail") String rcvEmail, @FormParam("rcvRemark") String rcvRemark) {
        return lotteryService.edit(uuid, lotteryId, uuid1, rcvNm, rcvAddr, rcvPhone, rcvEmail, rcvRemark);
    }
    
    @POST
    @Path("remind")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage remind(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("lotteryId") String lotteryId, @FormParam("uuidList") String uuidList) {
        if(StringUtils.isEmpty(uuidList) || StringUtils.isEmpty(lotteryId)){
            log.debug("没有通知的目标用户、或者抽奖包ID为空");
            throw new RuntimeException("没有通知的目标用户、或者抽奖包ID为空");
        }
        return lotteryService.remind(uuid, lotteryId, Arrays.asList(uuidList.split(",")));
    }
}
