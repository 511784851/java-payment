/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.unionpay;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Administrator
 * 
 */
public class DateTest {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("gbgbMMddhhmmss");

    public static void main(String[] args) {
        Date now = new Date();
        String sendTime = dateFormat.format(now);
        System.out.println("dateString:" + sendTime);
    }
}
