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

import com.guzhi.pay.domain.ChBank;
import com.guzhi.pay.mapper.ChBankMapper;

/**
 * @author Administrator
 *
 */
@ContextConfiguration(locations = "classpath*:dao-context.xml")
public class ChBankMapperTest extends AbstractTestNGSpringContextTests{
    @Autowired
    private ChBankMapper mapper;
    @Test
    public void testGet(){
        ChBank chBank = mapper.get("KQ", "ABC");
        System.out.println(chBank.getCode());
    }
    

}
