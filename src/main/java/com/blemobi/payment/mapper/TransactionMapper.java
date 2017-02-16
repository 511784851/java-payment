package com.blemobi.payment.mapper;

import com.blemobi.payment.model.Transaction;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface TransactionMapper {
    @Delete({
        "delete from transaction",
        "where orderNo = #{orderno,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String orderno);

    @Insert({
        "insert into transaction (orderNo, orderAmount, ",
        "orderStatus, orderTime, ",
        "custOrderNo, receiveUid)",
        "values (#{orderno,jdbcType=VARCHAR}, #{orderamount,jdbcType=INTEGER}, ",
        "#{orderstatus,jdbcType=INTEGER}, #{ordertime,jdbcType=TIMESTAMP}, ",
        "#{custorderno,jdbcType=VARCHAR}, #{receiveuid,jdbcType=VARCHAR})"
    })
    int insert(Transaction record);

    @InsertProvider(type=TransactionSqlProvider.class, method="insertSelective")
    int insertSelective(Transaction record);

    @Select({
        "select",
        "orderNo, orderAmount, orderStatus, orderTime, custOrderNo, receiveUid",
        "from transaction",
        "where orderNo = #{orderno,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="orderNo", property="orderno", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="orderAmount", property="orderamount", jdbcType=JdbcType.INTEGER),
        @Result(column="orderStatus", property="orderstatus", jdbcType=JdbcType.INTEGER),
        @Result(column="orderTime", property="ordertime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="custOrderNo", property="custorderno", jdbcType=JdbcType.VARCHAR),
        @Result(column="receiveUid", property="receiveuid", jdbcType=JdbcType.VARCHAR)
    })
    Transaction selectByPrimaryKey(String orderno);

    @UpdateProvider(type=TransactionSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Transaction record);

    @Update({
        "update transaction",
        "set orderAmount = #{orderamount,jdbcType=INTEGER},",
          "orderStatus = #{orderstatus,jdbcType=INTEGER},",
          "orderTime = #{ordertime,jdbcType=TIMESTAMP},",
          "custOrderNo = #{custorderno,jdbcType=VARCHAR},",
          "receiveUid = #{receiveuid,jdbcType=VARCHAR}",
        "where orderNo = #{orderno,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(Transaction record);
}