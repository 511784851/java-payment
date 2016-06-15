/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;

/**
 * 用于针对业务层的请求和返回，进行安全校验与签名生成。<br>
 * 注意：对于渠道层，由于差异较大，需要各渠道实现部分自行实现，不放在这里。
 * 
 * @author administrator
 */
public class SecureHelper {
    private static final Logger logger = LoggerFactory.getLogger(SecureHelper.class);

    public static void verifyMd5Sign(String key, String sign, String data) {
        emptyCheck(sign, "sign");
        String expect = genMd5Sign(key, data);
        if (!sign.equals(expect)) {
            logger.warn("sign not match!, expect={}, sign={}, key={}, data={}", expect, sign, key, data);
            throw new PayException(Consts.SC.SECURE_ERROR, "sign not match!");
        }
    }

    public static String genMd5Sign(String key, String data) {
        emptyCheck(key, "key");
        emptyCheck(data, "data");

        String strForGen = "data=" + data + "&key=" + key;
        // logger.info("strForGen:{}", strForGen);
        String sign = DigestUtils.md5DigestAsHex(strForGen.getBytes(Charset.forName("UTF-8")));
        return sign;
    }

    private static void emptyCheck(String obj, String name) {
        if (StringUtils.isBlank(obj)) {
            String msg = String.format("manditory field missed or empty, name=%s", obj);
            throw new PayException(Consts.SC.SECURE_ERROR, msg, null, null);
        }
    }
}
