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

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.guzhi.pay.domain.ChBank;

/**
 * @author Administrator
 *         银行渠道mapper
 */
@Repository("chBankMapper")
public interface ChBankMapper {
    /**
     * 通过渠道 和银行ID 查询对应渠道的银行ID
     * 
     * @param appId
     * @param appOrderId
     * @param type
     * @return
     */
    @Select("select * from ch_bank where chId=#{chId} and bankId=#{bankId} and `status` = '1' ")
    ChBank get(@Param("chId") String chId, @Param("bankId") String bankId);

    /**
     * @param chIds
     * @param bankId
     * @return
     */
    @Select("select lower(chId) from ch_bank where chId in(${chIds}) and bankId = #{bankId}")
    List<String> getChIds(@Param("chIds") String chIds, @Param("bankId") String bankId);

}
