/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.mapper;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.guzhi.pay.domain.UserAccountLimit;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.mapper.UserAccountLimitMapper;
import com.guzhi.pay.traderule.TradeRuleConsts;

/**
 * @author Administrator
 * 
 */
@ContextConfiguration(locations = "classpath*:dao-context.xml")
public class UserAccountLimitMapperTerst extends AbstractTestNGSpringContextTests {
    @Autowired
    UserAccountLimitMapper userAccountLimitMapper;

    @Test
    public void testGetUserAccount() {
        String account = "1234";
        String chId = "Paypal";
        String startTime = "2013-05-13 09:00:00";
        String endTime = "2013-05-13 11:00:00";
        List<UserAccountLimit> test = userAccountLimitMapper.getUserAccount(account, chId, UserAccountLimit.TYPE_BLACK,
                startTime, endTime);
        System.out.println(test.size());
    }

    @Test
    public void testCreateUserAccountLimit() {
        UserAccountLimit userAccountLimit = new UserAccountLimit();
        userAccountLimit.setAccount("aaa");
        userAccountLimit.setChId("bb");
        userAccountLimit.setLastUpdateTime(TimeHelper.get(8, new Date()));
        userAccountLimit.setStatus(TradeRuleConsts.STATUS_VAILID);
        userAccountLimit.setType(TradeRuleConsts.BLACK);
        userAccountLimitMapper.createUserAccountLimit(userAccountLimit);
    }
}
