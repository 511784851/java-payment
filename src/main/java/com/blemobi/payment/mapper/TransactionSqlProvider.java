package com.blemobi.payment.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO;
import static org.apache.ibatis.jdbc.SqlBuilder.SET;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.UPDATE;
import static org.apache.ibatis.jdbc.SqlBuilder.VALUES;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import com.blemobi.payment.model.Transaction;

public class TransactionSqlProvider {

    public String insertSelective(Transaction record) {
        BEGIN();
        INSERT_INTO("transaction");
        
        if (record.getOrderno() != null) {
            VALUES("orderNo", "#{orderno,jdbcType=VARCHAR}");
        }
        
        if (record.getOrderamount() != null) {
            VALUES("orderAmount", "#{orderamount,jdbcType=INTEGER}");
        }
        
        if (record.getOrderstatus() != null) {
            VALUES("orderStatus", "#{orderstatus,jdbcType=INTEGER}");
        }
        
        if (record.getOrdertime() != null) {
            VALUES("orderTime", "#{ordertime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getCustorderno() != null) {
            VALUES("custOrderNo", "#{custorderno,jdbcType=VARCHAR}");
        }
        
        if (record.getReceiveuid() != null) {
            VALUES("receiveUid", "#{receiveuid,jdbcType=VARCHAR}");
        }
        
        return SQL();
    }

    public String updateByPrimaryKeySelective(Transaction record) {
        BEGIN();
        UPDATE("transaction");
        
        if (record.getOrderamount() != null) {
            SET("orderAmount = #{orderamount,jdbcType=INTEGER}");
        }
        
        if (record.getOrderstatus() != null) {
            SET("orderStatus = #{orderstatus,jdbcType=INTEGER}");
        }
        
        if (record.getOrdertime() != null) {
            SET("orderTime = #{ordertime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getCustorderno() != null) {
            SET("custOrderNo = #{custorderno,jdbcType=VARCHAR}");
        }
        
        if (record.getReceiveuid() != null) {
            SET("receiveUid = #{receiveuid,jdbcType=VARCHAR}");
        }
        
        WHERE("orderNo = #{orderno,jdbcType=VARCHAR}");
        
        return SQL();
    }
}