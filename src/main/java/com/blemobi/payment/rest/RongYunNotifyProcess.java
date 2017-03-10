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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSONObject;
import com.blemobi.payment.service.CallbackService;
import com.blemobi.payment.service.helper.SignHelper;
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaTypeExt.MULTIPART_FORM_DATA)
    public String callback(String jsonString) {
        log.debug("json str:" + jsonString);
        JSONObject json = JSONObject.parseObject(jsonString);

        String sign = json.getString("sign");
        String custOrderNo = json.getString("custOrderNo");
        String orderNo = json.getString("orderNo");
        String orderAmount = json.getString("orderAmount");
        String orderTime = json.getString("orderTime");
        String orderStatus = json.getString("orderStatus");
        String returnType = json.getString("returnType");
        String custUid = json.getString("custUid");
        String receiveUid = json.getString("receiveUid");

        try {
            Map<String, String> param = new HashMap<String, String>();
            param.put("custOrderNo", custOrderNo);
            param.put("orderNo", orderNo);
            param.put("orderAmount", orderAmount);
            param.put("orderTime", orderTime);
            param.put("orderStatus", orderStatus);
            param.put("returnType", returnType);
            param.put("custUid", custUid);
            param.put("receiveUid", receiveUid);
            param.put("seckey", SignHelper.seckey);

            String signLocal = SignUtil.sign(param);
            log.info(signLocal);
            // 融云支付失败，不予处理
//            if (!Constants.RONGYUN_ORD_STS.SUCCESS.getValue().equals(orderStatus)) {
//                return Constants.HTMLSTS.SUCCESS.getValue();
//            }
//            // 验签失败
//            if (signLocal == null || !signLocal.equals(sign)) {
//                log.warn("signLocal：" + signLocal + ",sign:" + sign);
//                return Constants.HTMLSTS.FAILED.getValue();
//            }
            callbackService.paySucc(orderAmount, DateTimeUtils.currTime(), custOrderNo, receiveUid, orderNo,
                    orderStatus, " ");
        } catch (Exception ex) {
            log.error("payment callback failed", ex);
            return Constants.HTMLSTS.FAILED.getValue();
        }
        return Constants.HTMLSTS.SUCCESS.getValue();
    }
}
