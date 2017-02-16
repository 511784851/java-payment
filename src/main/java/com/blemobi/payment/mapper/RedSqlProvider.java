package com.blemobi.payment.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO;
import static org.apache.ibatis.jdbc.SqlBuilder.SET;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.UPDATE;
import static org.apache.ibatis.jdbc.SqlBuilder.VALUES;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import com.blemobi.payment.model.Red;

public class RedSqlProvider {

    public String insertSelective(Red record) {
        BEGIN();
        INSERT_INTO("red");
        
        if (record.getCustorderno() != null) {
            VALUES("custorderno", "#{custorderno,jdbcType=VARCHAR}");
        }
        
        if (record.getSenduuid() != null) {
            VALUES("sendUUID", "#{senduuid,jdbcType=VARCHAR}");
        }
        
        if (record.getReceiveuuid() != null) {
            VALUES("receiveUUID", "#{receiveuuid,jdbcType=VARCHAR}");
        }
        
        if (record.getAmount() != null) {
            VALUES("amount", "#{amount,jdbcType=INTEGER}");
        }
        
        if (record.getTitle() != null) {
            VALUES("title", "#{title,jdbcType=VARCHAR}");
        }
        
        if (record.getSendtime() != null) {
            VALUES("sendTime", "#{sendtime,jdbcType=BIGINT}");
        }
        
        if (record.getReceivetime() != null) {
            VALUES("receiveTime", "#{receivetime,jdbcType=BIGINT}");
        }
        
        if (record.getInvalidtime() != null) {
            VALUES("invalidTime", "#{invalidtime,jdbcType=BIGINT}");
        }
        
        if (record.getStatus() != null) {
            VALUES("status", "#{status,jdbcType=INTEGER}");
        }
        
        return SQL();
    }

    public String updateByPrimaryKeySelective(Red record) {
        BEGIN();
        UPDATE("red");
        
        if (record.getSenduuid() != null) {
            SET("sendUUID = #{senduuid,jdbcType=VARCHAR}");
        }
        
        if (record.getReceiveuuid() != null) {
            SET("receiveUUID = #{receiveuuid,jdbcType=VARCHAR}");
        }
        
        if (record.getAmount() != null) {
            SET("amount = #{amount,jdbcType=INTEGER}");
        }
        
        if (record.getTitle() != null) {
            SET("title = #{title,jdbcType=VARCHAR}");
        }
        
        if (record.getSendtime() != null) {
            SET("sendTime = #{sendtime,jdbcType=BIGINT}");
        }
        
        if (record.getReceivetime() != null) {
            SET("receiveTime = #{receivetime,jdbcType=BIGINT}");
        }
        
        if (record.getInvalidtime() != null) {
            SET("invalidTime = #{invalidtime,jdbcType=BIGINT}");
        }
        
        if (record.getStatus() != null) {
            SET("status = #{status,jdbcType=INTEGER}");
        }
        
        WHERE("custorderno = #{custorderno,jdbcType=VARCHAR}");
        
        return SQL();
    }
}