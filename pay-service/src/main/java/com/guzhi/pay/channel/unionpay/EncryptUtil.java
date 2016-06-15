/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.unionpay;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;

/**
 * 银联使用的加密工具类。
 * 
 * @author 
 * 
 */
public class EncryptUtil {
    private static final String CER_INST_KEY = "X.509";
    /**
     * Public keys are stored here, the keys were CER file paths.<br>
     * For example,"/data/personal/gb.com/unionpay.cer"
     */
    private static Map<String, PublicKey> publicKeyMap = new HashMap<String, PublicKey>();

    /**
     * Private keys are stored here, the keys were PFX file paths append
     * keys.<br>
     * For example, "/data/personal/gb.com/unionpay.pfxpassword"
     */
    private static Map<String, PrivateKey> privateKeyMap = new HashMap<String, PrivateKey>();

    public static byte[] encryptBy3DES(byte[] encryptKey, byte[] data) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(encryptKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "encryptBy3DES failed,because of " + e.getMessage());
        }
    }

    public static byte[] decryptBy3DES(byte[] decryptKey, byte[] data) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(decryptKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, securekey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "decryptBy3DES failed,because of " + e.getMessage());
        }
    }

    public static byte[] encryptByRSA(byte[] data, String filePath, String password) {
        byte[] encryptKey = getPrivateKey(filePath, password).getEncoded();
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(encryptKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "encryptByRSA failed,because of " + e.getMessage());
        }
    }

    /**
     * @param filePath
     * @param key
     * @return
     */
    private static PrivateKey getPrivateKey(String filePath, String password) {
        String key = filePath + password;
        if (null != privateKeyMap && privateKeyMap.containsKey(key) && null != privateKeyMap.get(key)) {
            return privateKeyMap.get(key);
        }
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream is = new FileInputStream(filePath);
            ks.load(is, password.toCharArray());
            is.close();
            Enumeration<String> enumeration = ks.aliases();
            String keyAlias = null;
            if (enumeration.hasMoreElements()) {
                keyAlias = (String) enumeration.nextElement();
            }
            PrivateKey privateKey = (PrivateKey) ks.getKey(keyAlias, password.toCharArray());
            privateKeyMap.put(key, privateKey);
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "getPrivateKey failed,because of " + e.getMessage());
        }
        return privateKeyMap.get(key);
    }

    public static PublicKey getPublicKey(String filePath) {
        if (null != publicKeyMap && publicKeyMap.containsKey(filePath) && null != publicKeyMap.get(filePath)) {
            return publicKeyMap.get(filePath);
        }
        try {
            InputStream is = new FileInputStream(new File(filePath));
            CertificateFactory cf = CertificateFactory.getInstance(CER_INST_KEY);
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
            publicKeyMap.put(filePath, cert.getPublicKey());
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "get public key of " + filePath + " failed,because of "
                    + e.getMessage());
        }
        return publicKeyMap.get(filePath);
    }

    /**
     * 
     * @param data
     */
    public static byte[] decryptByRSA(byte[] data, String filePath) {
        PublicKey pk = getPublicKey(filePath);
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pk);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "decryptByRSA failed,because of " + e.getMessage());
        }
    }
}