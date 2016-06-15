/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.guzhi.pay.domain.UserTransInfo;

/**
 * @author Administrator
 * 
 */
@Repository("userTransInfoMapper")
public interface UserTransInfoMapper {
    @Select("select sum(amount) from user_trans_info where gbuid=#{gbuid} and chId=#{chId} and status='success'")
    String getHisTotalAmount(@Param("gbuid") String gbuid, @Param("chId") String chId);

    @Select("select sum(amount) from user_trans_info where gbuid=#{gbuid} and chId=#{chId} and status='success' and paytime > #{startTime} and paytime < #{endTime}")
    String getHisTotalAmountByTime(@Param("gbuid") String gbuid, @Param("chId") String chId,
            @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("select count(*) from user_trans_info where account=#{account} and status='success' and paytime > #{startTime} and paytime < #{endTime}")
    int getAccountNumberByTime(@Param("account") String account, @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    @Select("select sum(amount) from user_trans_info where account=#{account} and status='success' and paytime > #{startTime} and paytime < #{endTime}")
    String getTotalAmountByTime(@Param("account") String account, @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    @Select("SELECT COUNT(*) FROM (SELECT DISTINCT(account) FROM user_trans_info WHERE gbuid=#{gbuid}  and account!=#{exAccount} and chId=#{chId} and status='success' and paytime > #{startTime} and paytime < #{endTime}) t")
    int getgbCrospPaypal(@Param("gbuid") String gbuid, @Param("chId") String chId,
            @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("exAccount") String exAccount);

    @Select("SELECT COUNT(*) FROM (SELECT DISTINCT(account) FROM user_trans_info WHERE ip=#{ip} and account!=#{exAccount} and chId=#{chId} and status='success' and paytime > #{startTime} and paytime < #{endTime}) t")
    int getIpCrospPaypal(@Param("ip") String ip, @Param("chId") String chId, @Param("startTime") String startTime,
            @Param("endTime") String endTime, @Param("exAccount") String exAccount);

    @Select("SELECT COUNT(*) FROM (SELECT DISTINCT(ip) FROM user_trans_info WHERE ip!=#{ip} and account=#{exAccount} and chId=#{chId}  and paytime > #{startTime} and paytime < #{endTime}) t")
    int getPaypalCrospIp(@Param("ip") String ip, @Param("chId") String chId, @Param("startTime") String startTime,
            @Param("endTime") String endTime, @Param("exAccount") String exAccount);

    @Select("select count(*) from user_trans_info where gbuid=#{gbuid} and chId=#{chId} and status='success' and  paytime < #{endTime}")
    int getPayTimesByEndTime(@Param("gbuid") String gbuid, @Param("chId") String chId, @Param("endTime") String endTime);

    @Insert({
            "insert into user_trans_info(`appId`,`appOrderId`,`gbuid`,`account`,`paytime`,`ip`,`address`,",
            "`ext`,`amount`,`chId`,`status`,`statusMsg`,`lastUpdateTime`) values(",
            "#{appId},#{appOrderId},#{gbuid},#{account},#{payTime},#{ip},#{address},#{ext},#{amount},#{chId},#{status},#{statusMsg},#{lastUpdateTime})" })
    int createUserTransInfo(UserTransInfo userTransInfo);

    @Update({ "update user_trans_info set status=#{status}, statusMsg=#{statusMsg},lastUpdateTime=#{lastUpdateTime} where appId=#{appId} and appOrderId=#{appOrderId} and lastUpdateTime<=#{lastUpdateTime}" })
    int updateUserTransInfo(UserTransInfo userTransInfo);
}
