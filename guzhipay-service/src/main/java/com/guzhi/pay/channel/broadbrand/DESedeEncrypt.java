/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.broadbrand;

import java.nio.charset.Charset;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HexUtil;

/**
 * 3DES 加密.
 * 新泛联天下通专用.
 * 
 * @author administrator
 * 
 */
public class DESedeEncrypt {
    private static final String DES_EDE_CFB = "DESede/CFB8/NoPadding";
    private static final String DESEDE = "DESede";
    private static SecureRandom sr = new SecureRandom();

    /**
     * 使用3DES加密.<br>
     * 具体模式为DESede/CFB8/NoPadding
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] EncryptBy3DESCFB(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance(DES_EDE_CFB);
            SecretKey secureKey = new SecretKeySpec(key.getBytes(Charset.forName(Consts.CHARSET_UTF8)), DESEDE);
            // 创建iv(初始化向量)
            byte[] ivByte = new byte[8];
            sr.nextBytes(ivByte);
            IvParameterSpec iv = new IvParameterSpec(ivByte);
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, iv);
            byte[] encodeData = cipher.doFinal(data.getBytes(Charset.forName(Consts.CHARSET_UTF8)));
            return mergeBytes(ivByte, encodeData);
        } catch (Exception e) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, "broadband txtong encrypt error.");
        }
    }

    /**
     * 将两个字节数组合并到一起.
     * 
     * @param byte_1
     * @param byte_2
     * @return
     */
    private static byte[] mergeBytes(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static void main(String[] args) throws Exception {
        String data = "37125788";
        System.out.println("原文: byte[]:" + data);
        byte[] encryptData = EncryptBy3DESCFB(data, "mrj2nzn3j4jc2mv4e68ymkrr");
        System.out.println("加密后数据: hexStr:" + HexUtil.toHexString(encryptData));
        System.out.println();
    }
}
