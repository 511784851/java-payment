/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import com.guzhi.pay.helper.FileUtil;
import com.guzhi.pay.helper.TimeHelper;

/**
 * @author administrator
 * 
 */
public class OrderIdGenerator {
    private static final String TMP_DIR = "E:\\\\data\\\\test\\\\";

    private static int COUNT = 100000;

    public static void main(String[] args) {
        for (int j = 0; j < 10; j++) {
            String fileName = TMP_DIR + TimeHelper.get(8, new Date()) + ".csv";
            List<String> orderIds = new ArrayList<String>(COUNT);
            for (int i = 0; i < COUNT; i++) {
                orderIds.add(RandomStringUtils.randomAlphanumeric(20));
            }
            try {
                FileUtil.write(fileName, orderIds);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}