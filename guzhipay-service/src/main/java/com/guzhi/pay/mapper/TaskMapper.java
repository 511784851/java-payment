/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.guzhi.pay.domain.Task;

/**
 * 任务表的Mapper
 * 
 * @author administrator
 */
@Repository("taskMapper")
public interface TaskMapper {

    /**
     * 创建任务
     * 
     * @param payResult
     * @return
     */
    @Insert({ "insert into task(`appId`,`appOrderId`,`type`,`flag`,`nextTime`,`payMethod`,`chId`) values(#{appId},#{appOrderId},#{type},#{flag},#{nextTime},#{payMethod},#{chId})" })
    int createTask(Task task);

    /**
     * 通过appId+appOrderId+type查询任务
     * 
     * @param appId
     * @param appOrderId
     * @param type
     * @return
     */
    @Select("select * from task where appId=#{appId} and appOrderId=#{appOrderId} and `type` = #{type} ")
    Task get(@Param("appId") String appId, @Param("appOrderId") String appOrderId, @Param("type") String type);

    // /**
    // * 查出一条idle的查询任务
    // *
    // * @return
    // */
    // @Select("select * from task where `type` = 'q' and flag = 'i' and nextTime <= current_timestamp order by nextTime limit 1")
    // Task getOneExecutableQueryTask();
    //
    // /**
    // * 查出一条idle的通知任务
    // *
    // * @return
    // */
    // @Select("select * from task where `type` = 'n' and flag = 'i' and nextTime <= current_timestamp order by nextTime limit 1")
    // Task getOneExecutableNotifyTask();
    //
    // /**
    // * @param appId
    // * @param appOrderId
    // * @param type
    // * @return
    // */
    // @Select("select count(1) from task where appId=#{appId} and appOrderId=#{appOrderId} and `type` = #{type}")
    // int hasTask(@Param("appId") String appId, @Param("appOrderId") String
    // appOrderId, @Param("type") String type);

    /**
     * 返回一条可执行的task
     * 
     * @return
     */
    @Select("select * from task where flag = 'i' and nextTime <= current_timestamp and retryTimes <= #{maxRetryTimes} order by nextTime limit 1")
    Task getOneExecutableTask(@Param("maxRetryTimes") int maxRetryTimes);

    /**
     * 返回一条可执行的task
     * 
     * @return
     */
    @Select("select * from task where type=#{type} and flag = 'i' and nextTime <= current_timestamp and retryTimes <= #{maxRetryTimes}  limit 1")
    Task getOneExecutableTaskByType(@Param("type") String type, @Param("maxRetryTimes") int maxRetryTimes);

    /**
     * 返回size条可执行的task
     * 
     * @return
     */
    @Select("select * from task where type = #{type} and flag = 'i' and nextTime <= current_timestamp and retryTimes <= #{maxRetryTimes}  limit #{size}")
    List<Task> getExecutableTaskByType(@Param("type") String type, @Param("maxRetryTimes") int maxRetryTimes,
            @Param("size") int size);

    /**
     * 返回一条可执行的task
     * 
     * @return
     */
    @Select("select * from task where type!=#{type} and flag = 'i' and nextTime <= current_timestamp and retryTimes <= #{maxRetryTimes}  limit 1")
    Task getOneExecutableTaskByUnType(@Param("type") String type, @Param("maxRetryTimes") int maxRetryTimes);

    /**
     * 返回size条可执行的task
     * 
     * @return
     */
    @Select("select * from task where type!=#{type} and flag = 'i' and nextTime <= current_timestamp and retryTimes <= #{maxRetryTimes} limit #{size}")
    List<Task> getExecutableTaskByUnType(@Param("type") String type, @Param("maxRetryTimes") int maxRetryTimes,
            @Param("size") int size);

    /**
     * 更新任务为占用状态
     * 
     * @param appId
     * @param appOrderId
     * @param type
     * @return 占坑成功为1，失败为0
     */
    @Update("update task set flag='o' where appId=#{appId} and appOrderId=#{appOrderId} and `type` = #{type} and retryTimes= #{retryTimes} and flag='i'")
    int updateTaskToOccupied(@Param("appId") String appId, @Param("appOrderId") String appOrderId,
            @Param("type") String type, @Param("retryTimes") int retryTimes);

    /**
     * 更新任务下次某个时间执行(场景举例：任务执行失败，便于控制下次的执行时间)
     * 注: 必须是occupied任务才能更新成功
     * 
     * @param appId
     * @param appOrderId
     * @param nextTime
     * @return 更新成功为1,失败为0
     */
    @Update("update task set nextTime = #{nextTime}, flag='i' , retryTimes = retryTimes+1 where appId=#{appId} and appOrderId=#{appOrderId} and `type`= #{type} and flag='o'")
    int updateTaskDelay(@Param("appId") String appId, @Param("appOrderId") String appOrderId,
            @Param("type") String type, @Param("nextTime") Date nextTime);

    /**
     * 更新任务下次某个时间执行（场景举例：补通知）
     * 
     * @param task
     * @return 更新成功为1,失败为0
     */
    @Update("update task set nextTime = #{nextTime}, flag='i' , retryTimes = #{retryTimes} where appId=#{appId} and appOrderId=#{appOrderId} and `type`= #{type}")
    int updateTaskForReplacement(Task task);

    /**
     * 删除一条task任务
     * 
     * @param appId
     * @param appOrderId
     * @param type
     * @return
     */
    @Delete("delete from task where appId=#{appId} and appOrderId=#{appOrderId} and `type` = #{type}")
    int deleteTask(@Param("appId") String appId, @Param("appOrderId") String appOrderId, @Param("type") String type);
}
