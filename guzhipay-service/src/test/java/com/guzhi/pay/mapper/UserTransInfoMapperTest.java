/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.guzhi.pay.mapper.UserTransInfoMapper;

/**
 * @author Administrator
 * 
 */
@ContextConfiguration(locations = "classpath*:dao-context.xml")
public class UserTransInfoMapperTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private UserTransInfoMapper userTransInfoMapper;

    @Test
    public void testGetHisTotalAmount() {
        String yyuid = "1234";
        String chId = "Paypal";
        String temp = userTransInfoMapper.getHisTotalAmount(yyuid, chId);
        System.out.println("result:" + temp);
    }

    @Test
    public void testGetHisTotalAmountByTime() {
        String yyuid = "1234";
        String chId = "Paypal";
        String startTime = "2013-05-13 09:00:00";
        String endTime = "2013-05-13 11:00:00";
        String temp = userTransInfoMapper.getHisTotalAmountByTime(yyuid, chId, startTime, endTime);
        System.out.println("testGetHisTotalAmountByTime:" + temp);
    }

    @Test
    public void testGetTotalAccountByTime() {
        String account = "ceshi";
        String startTime = "20130528112440";
        String endTime = "20130529112342";
        int temp = userTransInfoMapper.getAccountNumberByTime(account, startTime, endTime);
        System.out.println("testGetHisTotalAmountByTime:" + temp);
    }

    @Test
    public void testGetYyCrospPaypal() {
        String account = "ceshi";
        String yyuid = "1234";
        String startTime = "2013-05-13 09:00:00";
        String endTime = "2013-05-13 11:00:00";
        String chId = "Paypal";
        int temp = userTransInfoMapper.getYyCrospPaypal(yyuid, chId, startTime, endTime, account);
        System.out.println("testGetYyCrospPaypal:" + temp);
    }

    @Test
    public void testGetIpCrospPaypal() {
        String ip = "127.0.0.1";
        String startTime = "2013-05-13 09:00:00";
        String endTime = "2013-06-13 11:00:00";
        String chId = "Paypal";
        int temp = userTransInfoMapper.getIpCrospPaypal(ip, chId, startTime, endTime, "");
        System.out.println("testGetIpCrospPaypal:" + temp);
    }

}
