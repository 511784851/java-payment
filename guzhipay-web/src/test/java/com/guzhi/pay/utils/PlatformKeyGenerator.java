/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.MD5Utils;

/**
 * 平台需配置的相关密钥生成器.
 * <ol>
 * <li>平台新业务key的分配</li>
 * <li>平台的dbpwd的加解密</li>
 * </ol>
 * 
 * @author administrator
 * 
 */
public class PlatformKeyGenerator {
    private final static int KEY_COUNT = 16;

    public String generatePlatformKey() {
        return RandomStringUtils.randomAlphanumeric(KEY_COUNT);
    }

    /**
     * 用于为新业务分配APPKEY，APPPWKEY.
     */
    @Test
    public void generatePlatformKeys() {
        System.out.println("key:" + generatePlatformKey());
        System.out.println("passwdkey:" + generatePlatformKey());
    }

    /**
     * 加密数据库密码，配置在配置文件或潜龙中.
     * 
     * @param dbUserName
     * @param decryptDbPwd
     */
    @Test
    public void encryptDbPwd() {
        String dbUserName = "";
        String decryptDbPwd = "";
        System.out.println("db username:" + dbUserName);
        System.out.println("decrypt db pwd:" + decryptDbPwd);
        System.out.println("encrypt db pwd:"
                + DESEncrypt.encryptByAES(MD5Utils.getMD5(dbUserName + dbUserName), decryptDbPwd));
    }

    /**
     * 解密数据库密码，配置在配置文件或潜龙中.
     * 
     * @param dbUserName
     * @param encryptDbPwd
     */
    @Test
    public void decryptDbPwd() {
        String dbUserName = "";
        String encryptDbPwd = "";
        System.out.println("db username:" + dbUserName);
        System.out.println("encrypt db pwd:" + encryptDbPwd);
        System.out.println("decrypt db pwd:"
                + DESEncrypt.decryptByAES(MD5Utils.getMD5(dbUserName + dbUserName), encryptDbPwd));
    }
}