/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.guzhi.pay.domain.PayResult;

/**
 * 支付结果保存表的Mapper
 * 
 * @author administrator
 */
@Repository("payResultMapper")
public interface PayResultMapper {

    /**
     * 保存支付结果到pay_result表
     * 
     * @param payResult
     * @return
     */
    @Insert({ "insert into pay_result values(#{appId},#{appOrderId},#{amount},#{chFee},#{bankId},",
            "#{bankDealId},#{bankDealTime},#{statusCode},#{statusMsg},current_timestamp)" })
    int createPayResult(PayResult payResult);

    /**
     * 
     * @param appId
     * @param appOrderId
     */
    @Delete("delete from pay_result where appId=#{appId} and appOrderId=#{appOrderId}")
    void deletePayResult(@Param("appId") String appId, @Param("appOrderId") String appOrderId);

}
