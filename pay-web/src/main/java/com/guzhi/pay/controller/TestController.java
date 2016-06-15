/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guzhi.pay.mapper.TaskMapper;

/**
 * XXX This class should be removed on production env.
 * @author administrator
 * 
 */
@Controller
public class TestController {
    @Autowired
    private TaskMapper mapper;

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        System.out.println(mapper.toString());
        System.out.println(mapper.getClass());
        System.out.println(mapper.hashCode());
        return "success";
    }

}
