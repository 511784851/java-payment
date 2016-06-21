/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.utils;

import com.guzhi.pay.helper.MD5Utils;

/**
 * @author administrator
 * 
 */
public class MD5Test {

    public static void main(String[] args) {
        System.out.println("src:1234567890123456\nMD5:" + MD5Utils.getMD5("1234567890123456"));
    }
}
