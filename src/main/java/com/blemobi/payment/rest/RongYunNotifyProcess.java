/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.rest
 *
 *    Filename:    RongYunNotifyProcess.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月27日 下午3:58:33
 *
 *    Revision:
 *
 *    2017年2月27日 下午3:58:33
 *
 *****************************************************************/
package com.blemobi.payment.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.blemobi.library.grpc.SaveFansGRPCClient;
import com.blemobi.payment.service.CallbackService;
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.DateTimeUtils;
import com.blemobi.payment.util.InstanceFactory;
import com.blemobi.payment.util.SignUtil;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

import lombok.extern.log4j.Log4j;

/**
 * @ClassName RongYunNotifyProcess
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年2月27日 下午3:58:33
 * @version 1.0.0
 */
@Log4j
@Path("v1/payment/callback")
public class RongYunNotifyProcess {
    private static final CallbackService callbackService = InstanceFactory.getInstance(CallbackService.class);
    /**
     * @Description 支付成功通知
     * @author HUNTER.POON
     * @param respstat
     *            响应码
     * @param respmsg
     *            响应消息
     * @param orderAmount
     *            订单金额
     * @param orderNo
     *            订单号
     * @param orderStatus
     *            订单状态
     * @param orderTime
     *            订单时间
     * @param custOrderNo
     *            渠道订单号
     * @param receiveUid
     *            接收人唯一标示
     * @param sign
     *            摘要
     * @return
     */
    @POST
    @Path("notify")
    @Produces(MediaTypeExt.MULTIPART_FORM_DATA)
    public String callback(@FormParam("respstat") String respstat, @FormParam("respmsg") String respmsg,
            @FormParam("orderAmount") String orderAmount, @FormParam("orderNo") String orderNo,
            @FormParam("orderStatus") String orderStatus, @FormParam("orderTime") String orderTime,
            @FormParam("custOrderNo") String custOrderNo, @FormParam("receiveUid") String receiveUid,
            @FormParam("sign") String sign) {
        try {
            Map<String, String> param = new HashMap<String, String>();
            param.put("respstat", respstat);
            param.put("respmsg", respmsg);
            param.put("orderAmount", orderAmount);
            param.put("orderNo", orderNo);
            param.put("orderStatus", orderStatus);
            param.put("orderTime", orderTime);
            param.put("custOrderNo", custOrderNo);
            param.put("receiveUid", receiveUid);
            String signLocal = SignUtil.sign(param);
            log.info(signLocal);
            //融云支付失败，不予处理
            if(!Constants.RESPSTS.SUCCESS.getValue().equals(respstat) || !Constants.RONGYUN_ORD_STS.SUCCESS.getValue().equals(orderStatus)){
                return Constants.HTMLSTS.SUCCESS.getValue();
            }
            // 验签失败
            if (signLocal == null || !signLocal.equals(sign)) {
                return Constants.HTMLSTS.FAILED.getValue();
            }
            callbackService.paySucc(orderAmount, DateTimeUtils.currTime(), custOrderNo, receiveUid, orderNo, orderStatus, respmsg);
        } catch (Exception ex) {
            log.error("payment callback failed", ex);
            return Constants.HTMLSTS.FAILED.getValue();
        }
        return Constants.HTMLSTS.SUCCESS.getValue();
    }
    
    @GET
    @Path("test")
    @Produces(MediaTypeExt.MULTIPART_FORM_DATA)
    public String callback(@QueryParam("uuid") String uuid) {
        try {
            log.info("grpc test......." + uuid);
            SaveFansGRPCClient client = new SaveFansGRPCClient();
            log.info("1");
            List<String> list = new ArrayList<String>();
            log.info("2");
            list.add("aa");
            log.info("3");
            list.add("bb");
            client.doExec(new Object[]{"hello world", 1, list, uuid});
            log.info("4");
        } catch (Exception ex) {
            log.error("payment callback failed", ex);
            return Constants.HTMLSTS.FAILED.getValue();
        } catch (Throwable e){
            log.error("payment callback failed.....", e);
            return Constants.HTMLSTS.FAILED.getValue();
        }
        log.info("6");
        return Constants.HTMLSTS.SUCCESS.getValue();
    }
}
