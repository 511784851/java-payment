/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.util
 *
 *    Filename:    RongYunWallet.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月1日 下午8:36:31
 *
 *    Revision:
 *
 *    2017年3月1日 下午8:36:31
 *
 *****************************************************************/
package com.blemobi.payment.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.blemobi.library.consul.BaseService;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.util.rongyun.B2CReq;
import com.blemobi.payment.util.rongyun.B2CResp;

import lombok.extern.log4j.Log4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @ClassName RongYunWallet
 * @Description 融云钱包
 * @author HUNTER.POON
 * @Date 2017年3月1日 下午8:36:31
 * @version 1.0.0
 */
@Log4j
public final class RongYunWallet {

    /**
     * @Description 渠道转账至个人用户
     * @author HUNTER.POON
     * @param req 请求对象
     * @return
     */
    public static final B2CResp b2cTransfer(B2CReq req) {
        BigDecimal amt = new BigDecimal(req.getFenAmt());
        amt.setScale(2);
        req.setTransferAmount(amt.divide(new BigDecimal(100)));;
        Map<String, String> param = BeanMapUtils.bean2Map(req);
        param.remove("fenAmt");
        String sign = SignUtil.sign(param);
        param.put("sign", sign);
        String reqUri = BaseService.getProperty("ry.transferURI");
        return reqRongYun(reqUri, param, B2CResp.class);
    }

    /**
     * @Description 对融云交互接口 
     * @author HUNTER.POON
     * @param url 请求地址
     * @param param 参数
     * @param clazz 返回对象
     * @return
     */
    private static <T> T reqRongYun(final String url, final Map<String, String> param, Class<T> clazz) {
        log.info("URL:" + url + ",PARAM:" + param);
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        if (param != null && !param.isEmpty()) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response resp = client.newCall(request).execute();
            if (resp.isSuccessful()) {
                String json = resp.body().string();
                log.info("rong yun response:" + json);
                return JSON.parseObject(json, clazz);
            }else {
                throw new RuntimeException("请求融云钱包出现异常");
            }
        } catch (IOException e) {
            log.error("request rong yun wallet failed", e);
            throw new RuntimeException("请求融云钱包出现异常");
        }
    }
    
    public static void main(String[] args) {
        B2CReq req = new B2CReq();
        req.setCustImg("ddd");
        req.setCustMobile("18890376529");
        req.setCustNickname("nickname");
        req.setCustOrderno("1234555d112x");
        req.setFenAmt(1);
        req.setCustUid("1470823631370937498");
        req.setTransferDesc("领奖");
        System.out.println(b2cTransfer(req));
    }
}
