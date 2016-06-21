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

import com.guzhi.pay.domain.AppInfo;

/**
 * @author administrator
 * 
 */
@Repository("appInfoMapper")
public interface AppInfoMapper {
    @Select("select * from app_info where appId = #{value}")
    AppInfo getAppInfo(String appId);

    @Update("update app_info set status=#{status} where appId = #{appId}")
    int updateStatus(@Param("appId") String appId, @Param("status") String status);

    /**
     * @author xiaoweiteng
     * @param appInfo
     * @return
     */
    @Insert("insert into app_info values(#{appId},#{appName},#{status},#{ipWhitelist},#{key},#{passwdKey},#{type})")
    int createAppInfo(AppInfo appInfo);

    /**
     * @author xiaoweiteng
     * @param appId
     * @param appName
     * @param status
     * @param ipWhitelist
     * @param key
     * @param passwdKey
     * @return
     */
    @Update("update app_info set appName=#{appName}, status=#{status}, ipWhitelist=#{ipWhitelist}, `key`=#{key}, passwdKey=#{passwdKey} where appId=#{appId}")
    int updateAppInfo(AppInfo appInfo);

    /**
     * @author xiaoweiteng
     * @param appId
     * @return
     */
    @Delete("delete from app_info where appId=#{appId}")
    int deleteAppInfo(@Param("appId") String appId);

    /**
     * @author xiaoweiteng
     * @return
     */
    @Select("select * from app_info")
    List<AppInfo> getAppInfos();

}
