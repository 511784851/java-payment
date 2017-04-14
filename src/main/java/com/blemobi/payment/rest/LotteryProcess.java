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

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.blemobi.payment.dto.TrashDto;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.excepiton.RestException;
import com.blemobi.payment.service.LotteryService;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PLotteryConfirm;
import com.blemobi.sep.probuf.PaymentProtos.PShuffle;
import com.blemobi.sep.probuf.PaymentProtos.PUserBaseEx;
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
@Path("v1/payment/lottery")
public class LotteryProcess {

    // @Autowired
    private LotteryService lotteryService = InstanceFactory.getInstance(LotteryService.class);

    private void validation(Integer bonus, Integer cnt, String title, String desc) {
        if (bonus == null) {
            throw new BizException(2105001, "请输入单个金额，0.01-200.00元");
        }
        if (bonus.intValue() < 1 || bonus.intValue() > 20000) {
            throw new BizException(2105000, "单个中奖金额为0.01-200.00元");
        }

        if (cnt == null || cnt.intValue() < 1 || cnt.intValue() > 50) {
            throw new BizException(2105002, "请设置1-50个中奖人数");
        }
        if (StringUtils.isEmpty(title) || title.length() > 20) {
            throw new BizException(2105006, "请输入标题，1-20个字符");
        }
        if (!StringUtils.isEmpty(desc) && desc.length() > 50) {
            throw new BizException(2105004, "最多仅支持50个字符");
        }
    }

    @POST
    @Path("resotre")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String resotre(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("lotteryId[]") List<String> lotteryId, @FormParam("type[]") List<Integer> type) {
        log.debug(StringUtils.join(lotteryId, ","));
        log.debug(StringUtils.join(type, ","));
        try {
            if (lotteryId == null || lotteryId.isEmpty() || type == null || type.isEmpty()
                    || lotteryId.size() != type.size()) {
                throw new Exception("输入参数有误");
            }
            lotteryService.restoreLottery(lotteryId, type, uuid);
        } catch (Exception ex) {
            throw new RestException(1001012, "系统繁忙");
        }
        JSONObject json = new JSONObject();
        json.put("ID", 0);
        json.put("Str", "操作成功");
        return json.toJSONString();
    }

    @POST
    @Path("foreverdel")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String foreverdel(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("lotteryId[]") List<String> lotteryId, @FormParam("type[]") List<Integer> type) {
        try {
            if (lotteryId == null || lotteryId.isEmpty() || type == null || type.isEmpty()
                    || lotteryId.size() != type.size()) {
                throw new Exception("输入参数有误");
            }
            lotteryService.deleteforeverLottery(lotteryId, type, uuid);
        } catch (Exception ex) {
            throw new RestException(1001012, "系统繁忙");
        }
        JSONObject json = new JSONObject();
        json.put("ID", 0);
        json.put("Str", "操作成功");
        return json.toJSONString();
    }

    @GET
    @Path("trashList")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String trashList(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @QueryParam("startIdx") Integer startIdx) {
        try {
            if (startIdx == null || startIdx.intValue() < 0) {
                startIdx = 0;
            }
            List<TrashDto> list = lotteryService.trashList(uuid, startIdx);
            String json = JSONObject.toJSONString(list);
            log.debug(json);
            return json;
        } catch (Exception ex) {
            throw new RestException(1001012, "系统繁忙");
        }
    }

    @POST
    @Path("shuffle")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage shuffleLottery(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("title") String title, @FormParam("winners") Integer winners, @FormParam("region") String region,
            @FormParam("remark") String remark, @FormParam("gender") Integer gender, @FormParam("bonus") Integer bonus,
            @FormParam("totAmt") Integer totAmt) {
        validation(bonus, winners, title, remark);
        PShuffle.Builder builder = PShuffle.newBuilder().setTitle(title).setWinners(winners).setRemark(remark)
                .setGender(gender).setBonus(bonus).setTotAmt(totAmt);
        if (!StringUtils.isEmpty(region)) {
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
            @FormParam("totAmt") Integer totAmt, @FormParam("uuid") String uuids, @FormParam("genders") String genders,
            @FormParam("regions") String regions) {
        validation(bonus, winners, title, remark);
        PLotteryConfirm.Builder builder = PLotteryConfirm.newBuilder();
        builder.setTitle(title).setWinners(winners).setRemark(remark).setGender(gender).setBonus(bonus)
                .setTotAmt(totAmt);
        if (!StringUtils.isEmpty(region)) {
            builder.addAllRegion(Arrays.asList(region.split(",")));
        }
        if (!StringUtils.isEmpty(uuids) && !StringUtils.isEmpty(genders) && !StringUtils.isEmpty(regions)) {
            String[] uArr = uuids.split(",");
            String[] gArr = genders.split(",");
            String[] lArr = regions.split(",");
            if (uArr.length != gArr.length || uArr.length != lArr.length) {
                throw new RuntimeException("中奖者名单有误");
            }
            List<PUserBaseEx> ue = new ArrayList<PUserBaseEx>();
            int idx = 0;
            for (String uid : uArr) {
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
        } else {
            throw new RuntimeException("中奖者名单有误");
        }
        PMessage ret = lotteryService.createLottery(uuid, builder.build());
        return ret;
    }

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
    public PMessage acceptPrize(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @FormParam("lotteryId") String lotteryId) {
        PMessage ret = lotteryService.acceptPrize(uuid, lotteryId);
        return ret;
    }

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

    @GET
    @Path("view")
    @Produces(MediaTypeExt.APPLICATION_PROTOBUF)
    public PMessage viewPrize(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
            @QueryParam("lotteryId") String lotteryId) {
        PMessage ret = lotteryService.viewPrize(uuid, lotteryId);
        return ret;
    }
}
