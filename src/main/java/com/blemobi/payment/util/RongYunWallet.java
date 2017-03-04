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
import java.util.Map;

import com.alibaba.fastjson.JSON;
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
        B2CResp resp = new B2CResp();
        Map<String, String> param = BeanMapUtils.bean2Map(req);
        String sign = SignUtil.sign(param);
        param.put("sign", sign);
        String reqUri = Constants.RONG_YUN_BASE_URL + Constants.B2C_TRANSFER_URI;
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
                throw new BizException(2106000, "请求融云钱包出现异常");
            }
        } catch (IOException e) {
            log.error("request rong yun wallet failed", e);
            throw new BizException(2106000, "请求融云钱包出现异常");
        }
    }
}