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

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.guzhi.pay.domain.AppChInfo;

/**
 * @author administrator
 */
@Repository("appChInfoMapper")
public interface AppChInfoMapper {

    @Update("update app_ch_info set status=#{status} where appId=#{appId} and payMethod=#{payMethod}")
    int updateStatus(@Param("appId") String appId, @Param("status") String status, @Param("payMethod") String payMethod);

    @Select("select * from app_ch_info where appId=#{appId} and chId=#{chId} and status='valid'")
    List<AppChInfo> getAppChInfoWithChId(@Param("appId") String appId, @Param("chId") String chId);

    @Select("select * from app_ch_info where appId=#{appId} and payMethod=#{payMethod} and status='valid'")
    List<AppChInfo> getAppChInfoWithPayMethod(@Param("appId") String appId, @Param("payMethod") String payMethod);

    @Select("select * from app_ch_info where appId=#{appId} and chId=#{chId} and payMethod=#{payMethod} and status='valid'")
    List<AppChInfo> getAppChInfoWithPayMethodChId(@Param("appId") String appId, @Param("chId") String chId,
            @Param("payMethod") String payMethod);

    /**
     * @author xiaoweiteng
     */
    @Insert({ "insert into app_ch_info values(#{appId},#{status},#{chId},#{payMethod},#{chName},",
            "#{chWeight},#{chAccountId},#{chAccountName},#{chPayKeyMd5},#{chAccountsKeyMd5},#{additionalInfo})" })
    int createAppChInfo(AppChInfo appChInfo);

    /**
     * @author xiaoweiteng
     * @param appId
     * @param status
     * @param chId
     * @param payMethod
     * @param chName
     * @param chWeight
     * @param chAccount
     * @param chAccountName
     * @param chPayKeyMd5
     * @param additionalInfo
     * @return
     */
    @Update("update app_ch_info set status=#{status}, chName=#{chName}, chWeight=#{chWeight}, chAccountId=#{chAccountId}, chAccountName=#{chAccountName}, chPayKeyMd5=#{chPayKeyMd5}, additionalInfo=#{additionalInfo} where appId=#{appId} and chId=#{chId} and payMethod=#{payMethod}")
    int updateAppChInfo(AppChInfo appChInfo);

    /**
     * @author xiaoweiteng
     * @param appId
     * @param chId
     * @param payMethod
     * @return
     */
    @Delete("delete from app_ch_info where appId=#{appId} and chId=#{chId} and payMethod=#{payMethod}")
    int deleteAppChInfo(@Param("appId") String appId, @Param("chId") String chId, @Param("payMethod") String payMethod);

    /**
     * 获取所有的渠道信息
     * 
     * @author xiaoweiteng
     * @return
     */
    @Select("select * from app_ch_info")
    List<AppChInfo> getAppChInfos();

    /**
     * @author xiaoweiteng
     * @param appId
     * @param chId
     * @param payMethod
     * @return
     */
    @Select("select * from app_ch_info where appId=#{appId} and chId=#{chId} and payMethod=#{payMethod}")
    AppChInfo getAppChInfo(@Param("appId") String appId, @Param("chId") String chId,
            @Param("payMethod") String payMethod);

}
