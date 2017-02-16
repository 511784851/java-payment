package com.blemobi.payment.mapper;

import com.blemobi.payment.model.Red;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface RedMapper {
    @Delete({
        "delete from red",
        "where custorderno = #{custorderno,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String custorderno);

    @Insert({
        "insert into red (custorderno, sendUUID, ",
        "receiveUUID, amount, ",
        "title, sendTime, ",
        "receiveTime, invalidTime, ",
        "status)",
        "values (#{custorderno,jdbcType=VARCHAR}, #{senduuid,jdbcType=VARCHAR}, ",
        "#{receiveuuid,jdbcType=VARCHAR}, #{amount,jdbcType=INTEGER}, ",
        "#{title,jdbcType=VARCHAR}, #{sendtime,jdbcType=BIGINT}, ",
        "#{receivetime,jdbcType=BIGINT}, #{invalidtime,jdbcType=BIGINT}, ",
        "#{status,jdbcType=INTEGER})"
    })
    int insert(Red record);

    @InsertProvider(type=RedSqlProvider.class, method="insertSelective")
    int insertSelective(Red record);

    @Select({
        "select",
        "custorderno, sendUUID, receiveUUID, amount, title, sendTime, receiveTime, invalidTime, ",
        "status",
        "from red",
        "where custorderno = #{custorderno,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="custorderno", property="custorderno", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="sendUUID", property="senduuid", jdbcType=JdbcType.VARCHAR),
        @Result(column="receiveUUID", property="receiveuuid", jdbcType=JdbcType.VARCHAR),
        @Result(column="amount", property="amount", jdbcType=JdbcType.INTEGER),
        @Result(column="title", property="title", jdbcType=JdbcType.VARCHAR),
        @Result(column="sendTime", property="sendtime", jdbcType=JdbcType.BIGINT),
        @Result(column="receiveTime", property="receivetime", jdbcType=JdbcType.BIGINT),
        @Result(column="invalidTime", property="invalidtime", jdbcType=JdbcType.BIGINT),
        @Result(column="status", property="status", jdbcType=JdbcType.INTEGER)
    })
    Red selectByPrimaryKey(String custorderno);

    @UpdateProvider(type=RedSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Red record);

    @Update({
        "update red",
        "set sendUUID = #{senduuid,jdbcType=VARCHAR},",
          "receiveUUID = #{receiveuuid,jdbcType=VARCHAR},",
          "amount = #{amount,jdbcType=INTEGER},",
          "title = #{title,jdbcType=VARCHAR},",
          "sendTime = #{sendtime,jdbcType=BIGINT},",
          "receiveTime = #{receivetime,jdbcType=BIGINT},",
          "invalidTime = #{invalidtime,jdbcType=BIGINT},",
          "status = #{status,jdbcType=INTEGER}",
        "where custorderno = #{custorderno,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(Red record);
}