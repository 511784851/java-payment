/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.FileUtil;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.StringHelper;

import sun.misc.BASE64Encoder;

/**
 * 获取联动优势结算文件
 * 
 * @author 
 * 
 */
public class SmsFile {

    private final static Logger LOG = LoggerFactory.getLogger(SmsFile.class);
    private static final String SHA_RSA = "SHA1withRSA";
    private static final String RSA = "RSA";

    /**
     * 联动优势定义的常量.
     */
    private static final String BILLADDR = "http://payment.umpay.com/hfwebbusi/bill/trans.dl";
    private static final String VERSION = "3.0";

    /******************* 本地配置的信息. **********************/
    /**
     * 要对账的商户号.
     */
    private static final String MERCHANTID = "5713";
    /**
     * 对账商户号的私钥文件保存路径.
     */
    private static final String PRIVATE_FILE_PATH = "E:\\\\data\\\\var\\\\payplf-tpay.gb.com\\\\certifications\\\\5713_HuaDuoWangLuo.key.p8";
    /**
     * 下载的对账文件保存路径.
     */
    private static final String TMP_DIR = "E:\\\\data\\\\ch\\\\";

    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DATE, 1);
        for (int i = 0; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            getSmsFileByDay(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }
    }

    /**
     * 获取指定日期当天的对账文件.
     */
    private static String getSmsFileByDay(Date date) {
        try {
            String payDate = new SimpleDateFormat("gbgbMMdd").format(date);
            // NOTE 以下参数要按照顺序添加.
            Map<String, String> dataMap = new LinkedHashMap<String, String>();
            dataMap.put("merId", MERCHANTID);
            dataMap.put("payDate", payDate);
            dataMap.put("version", VERSION);
            dataMap.put("sign", generateSign(StringHelper.assembleResqStr(dataMap, "UTF-8", false, false)));
            String responseString = HttpClientHelper.sendRequest(BILLADDR + "?"
                    + StringHelper.assembleResqStr(dataMap, "UTF-8", false, false));
            FileUtil.write(TMP_DIR + payDate + ".txt", responseString);
            return responseString;
        } catch (Exception e) {
            LOG.error("get account file error.", e);
            return "";
        }
    }

    /**
     * 参考主程序，生成对账请求的签名.
     */
    private static String generateSign(String returnStr) {
        PrivateKey pk = null;
        byte[] kb = new byte[4096];
        FileInputStream fis;
        try {
            fis = new FileInputStream(ResourceUtils.getFile(PRIVATE_FILE_PATH));
            fis.read(kb);
            fis.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        PKCS8EncodedKeySpec peks = null;
        KeyFactory kf = null;
        try {
            peks = new PKCS8EncodedKeySpec(kb);
            kf = KeyFactory.getInstance(RSA);
            pk = kf.generatePrivate(peks);
            LOG.info("generate privatekey and cache it,filepath:{}", PRIVATE_FILE_PATH);
        } catch (Exception e) {
            LOG.error("invalid primary key format", e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        Signature sig = null;
        byte[] sb = (byte[]) null;
        try {
            sig = Signature.getInstance(SHA_RSA);
            sig.initSign(pk);
            sig.update(returnStr.getBytes(Consts.CHARSET_GB_2312));
            sb = sig.sign();
        } catch (Exception e) {
            LOG.error("sign procedure failed", e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        String b64Str = null;
        try {
            BASE64Encoder base64 = new BASE64Encoder();
            b64Str = base64.encode(sb);
        } catch (Exception e) {
            LOG.error("base64 generation failed", e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        try {
            BufferedReader br = new BufferedReader(new StringReader(b64Str));
            String tmpStr = "";
            String tmpStr1 = "";
            while ((tmpStr = br.readLine()) != null) {
                tmpStr1 = tmpStr1 + tmpStr;
            }
            b64Str = tmpStr1;
            return b64Str;
        } catch (Exception e) {
            LOG.error("new base64 generation failed", e);
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
    }
}