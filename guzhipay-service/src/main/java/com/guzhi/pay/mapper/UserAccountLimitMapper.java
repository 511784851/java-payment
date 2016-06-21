/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.guzhi.pay.domain.UserAccountLimit;

/**
 * @author Administrator
 * 
 */
public interface UserAccountLimitMapper {
    // TODO 该表没有主键,没有索引
    @Select("select * from user_account_limit where account=#{account} and chId=#{chId} and type=#{type} and lastUpdateTime > #{startTime} and lastUpdateTime < #{endTime}")
    List<UserAccountLimit> getUserAccount(@Param("account") String account, @Param("chId") String chId,
            @Param("type") String type, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Insert({ "insert into user_account_limit(`account`,`chId`,`type`,`lastUpdateTime`,`status`,`cause`) values(",
            "#{account},#{chId},#{type},#{lastUpdateTime},#{status},#{cause})" })
    int createUserAccountLimit(UserAccountLimit userAccountLimit);

    @Select("select count(*) from user_account_limit where account=#{account} and chId=#{chId} and type=#{type}")
    int getBlackAccountNumber(@Param("account") String account, @Param("chId") String chId, @Param("type") String type);
}
