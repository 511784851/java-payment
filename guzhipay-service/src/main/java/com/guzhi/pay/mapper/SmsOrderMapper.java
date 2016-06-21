/*
 * Copyright (c) 2012 guzhi.com. 
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
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.guzhi.pay.domain.SmsOrder;

/**
 * 用于Mybatis映射的类
 * 
 * @author administrator
 */
@Repository("smsOrderMapper")
public interface SmsOrderMapper {

    @Select("SELECT * FROM sms_order WHERE `phone`=#{phone} and `validCode`=#{validCode} and `statusCode`=#{statusCode}")
    List<SmsOrder> getSmsOrderByPhoneAndCode(@Param("phone") String phone, @Param("validCode") String validCode,
            @Param("statusCode") String statusCode);

    @Select("SELECT * FROM sms_order WHERE `chOrderId`=#{chOrderId} ")
    List<SmsOrder> getSmsOrderByChOrderId(@Param("chOrderId") String chOrderId);

    /**
     * 创建支付订单
     * 
     * @param smsOrder
     * @return 1：创建成功 ；0： 创建失败
     */
    @Insert({
            "insert into sms_order(`phone`,`validCode`,`chOrderId`,`creatTime`,`lastUpdateTime`,`statusCode`,`statusMsg`) values(",
            "#{phone},#{validCode},#{chOrderId},#{creatTime},#{lastUpdateTime},#{statusCode},#{statusMsg})" })
    int createSmsOrder(SmsOrder smsOrder);

    /**
     * 更新订单的支付结果
     * 
     * @param payOrder
     * @return
     */
    @Update({
            "update sms_order set statusCode=#{statusCode}, statusMsg=#{statusMsg}, lastUpdateTime=#{lastUpdateTime}",
            "where `chOrderId`=#{chOrderId} and `lastUpdateTime`<=#{lastUpdateTime}" })
    int updateSmsOrder(SmsOrder smsOrder);

}
