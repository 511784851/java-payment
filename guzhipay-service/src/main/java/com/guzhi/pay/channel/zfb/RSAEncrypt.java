/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.zfb;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.FileUtil;

/**
 * 非对称加密工具类
 * 
 * @author administrator
 * 
 */
public class RSAEncrypt {
    private static final Logger LOG = LoggerFactory.getLogger(RSAEncrypt.class);
    private static final String SHA1_WITH_RSA = "SHA1withRSA";
    private static final String RSA = "RSA";
    private static Map<String, Key> keyStoreMap = new HashMap<String, Key>();

    /**
     * 生成私钥签名
     * 
     * @param signMsg
     * @return
     */
    public static String sign(String signMsg, String privateKeyFilePath, String password) {
        String base64 = "";
        try {
            PrivateKey privateKey = null;
            if (keyStoreMap.containsKey(privateKeyFilePath)) {
                privateKey = (PrivateKey) keyStoreMap.get(privateKeyFilePath);
            } else {
                byte[] keyBytes = Base64.decode(FileUtil.readFile(privateKeyFilePath));
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance(RSA);
                privateKey = keyFactory.generatePrivate(keySpec);
                keyStoreMap.put(privateKeyFilePath, privateKey);
                LOG.info("[sign] generate private key:{}", privateKeyFilePath);
            }
            Signature signature = Signature.getInstance(SHA1_WITH_RSA);
            signature.initSign(privateKey);
            signature.update(signMsg.getBytes(Consts.CHARSET_UTF8));
            base64 = Base64.encode(signature.sign());
        } catch (Exception e) {
            LOG.error("[sign] zfb sign error," + e.getMessage(), e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        return base64;
    }

    /**
     * 校验非对称签名
     * 使用支付宝公钥校验
     * 
     * @param data
     * @param sign
     * @return
     */
    public static boolean checkSign(String data, String sign, String publicKeyPath) {
        boolean flag = false;
        try {
            PublicKey publicKey = null;
            if (keyStoreMap.containsKey(publicKeyPath)) {
                publicKey = (PublicKey) keyStoreMap.get(publicKeyPath);
            } else {
                String fileContent = FileUtil.readFile(publicKeyPath);
                LOG.info("[checkSign] get public key file,filename:{},fileContent:{}", publicKeyPath, fileContent);
                if (StringUtils.isBlank(fileContent)) {
                    throw new PayException(Consts.SC.INTERNAL_ERROR, "get an null file,filePath: " + publicKeyPath);
                }
                byte[] keyBytes = Base64.decode(fileContent);
                KeyFactory keyFactory = KeyFactory.getInstance(RSA);
                publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
                keyStoreMap.put(publicKeyPath, publicKey);
                LOG.info("[checkSign] generate filepath:{},publickey:{}", publicKeyPath, publicKey.toString());
            }
            Signature signature = Signature.getInstance(SHA1_WITH_RSA);
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            flag = signature.verify(Base64.decode(sign));
        } catch (Exception e) {
            LOG.warn("[checkSign] get keyfile {} fail.", publicKeyPath, e.getCause());
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        return flag;
    }
}
