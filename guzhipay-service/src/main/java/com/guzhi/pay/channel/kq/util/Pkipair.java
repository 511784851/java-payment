package com.guzhi.pay.channel.kq.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.szf.util.Base64;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;

/***
 * 快钱非对称加密算法工具类
 * 
 * @author zhaoming@chinaduo.com
 *         2012-4-20 下午04:21:11
 */
public class Pkipair {
    private static final String CER_INST_KEY = "X.509";
    private static final String SHA1_WITH_RSA = "SHA1withRSA";
    private static final String PKCS12 = "PKCS12";

    private static final Logger LOG = LoggerFactory.getLogger(Pkipair.class);
    private static Map<String, Key> keyStoreMap = new HashMap<String, Key>();

    public static void main(String[] args) throws KeyStoreException {
        // String testInfo = "AAAAA";
        // String signMsg = signMsg(testInfo,
        // "/data/var/payplf-tpay.yy.com/certifications/1001213884201.pfx",
        // "123456");
        // System.out.println(signMsg);
        // System.out.println(enCodeByCer(testInfo, signMsg,
        // "/data/var/payplf-tpay.yy.com/certifications/1001213884201.cer"));

        KeyStore ks = KeyStore.getInstance(PKCS12);
        Enumeration<String> enumeration = ks.aliases();
        String keyAlias = null;
        if (enumeration.hasMoreElements()) {
            keyAlias = (String) enumeration.nextElement();
        }
        System.out.println(keyAlias);
    }

    /**
     * 需添加注释
     * 
     * @param signMsg
     * @return
     */
    public static String signMsg(String signMsg, String privateKeyFilePath, String password) {
        String base64 = "";
        try {
            PrivateKey privateKey = null;
            if (keyStoreMap.containsKey(privateKeyFilePath)) {
                privateKey = (PrivateKey) keyStoreMap.get(privateKeyFilePath);
            } else {
                KeyStore ks = KeyStore.getInstance(PKCS12);
                FileInputStream is = new FileInputStream(privateKeyFilePath);
                BufferedInputStream ksbufin = new BufferedInputStream(is);
                char[] keyPwd = password.toCharArray();
                ks.load(ksbufin, keyPwd);
                Enumeration<String> enumeration = ks.aliases();
                String keyAlias = null;
                if (enumeration.hasMoreElements()) {
                    keyAlias = (String) enumeration.nextElement();
                }
                LOG.info("[signMsg] keyAlias:{}", keyAlias);
                privateKey = (PrivateKey) ks.getKey(keyAlias, keyPwd);
                keyStoreMap.put(privateKeyFilePath, privateKey);
                LOG.info("[signMsg] generate private key:{}", privateKeyFilePath);
            }
            Signature signature = Signature.getInstance(SHA1_WITH_RSA);
            signature.initSign(privateKey);
            signature.update(signMsg.getBytes(Consts.CHARSET_UTF8));
            base64 = new String(Base64.encode(signature.sign()), Consts.CHARSET_UTF8);
        } catch (FileNotFoundException e) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        } catch (Exception e) {
            LOG.error("kq signMsg " + e.getMessage(), e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        return base64;
    }

    /**
     * 需添加注释
     * 
     * @param val
     * @param msg
     * @return
     */
    public static boolean enCodeByCer(String val, String msg, String publicKeyFilePath) {
        boolean flag = false;
        try {
            PublicKey publicKey = null;
            if (keyStoreMap.containsKey(publicKeyFilePath)) {
                publicKey = (PublicKey) keyStoreMap.get(publicKeyFilePath);
            } else {
                FileInputStream inStream = new FileInputStream(publicKeyFilePath);
                CertificateFactory cf = CertificateFactory.getInstance(CER_INST_KEY);
                X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
                publicKey = cert.getPublicKey();
                keyStoreMap.put(publicKeyFilePath, publicKey);
                LOG.info("[enCodeByCer] generate public key:{}", publicKeyFilePath);
            }
            Signature signature = Signature.getInstance(SHA1_WITH_RSA);
            signature.initVerify(publicKey);
            signature.update(val.getBytes());
            flag = signature.verify(Base64.decode(msg.getBytes(Consts.CHARSET_UTF8)));
        } catch (Exception e) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        return flag;
    }

}
