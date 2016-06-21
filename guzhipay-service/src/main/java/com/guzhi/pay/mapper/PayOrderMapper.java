/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.guzhi.pay.domain.PayOrder;

/**
 * 用于Mybatis映射的类
 * 
 * @author administrator
 */
@Repository("payOrderMapper")
public interface PayOrderMapper {

    @Select("SELECT * FROM pay_order${suffix} WHERE `appId`=#{appId} and `appOrderId`=#{appOrderId}")
    PayOrder getPayOrder(@Param("appId") String appId, @Param("appOrderId") String appOrderId,
            @Param("suffix") String suffix);

    @Select("SELECT * FROM pay_order${suffix} WHERE `chDealId`=#{chDealId} ")
    PayOrder getPayOrderByChDealId(@Param("chDealId") String chDealId, @Param("suffix") String suffix);

    /**
     * 创建支付订单
     * 
     * @param payOrder
     * @return 1：创建成功 ；0： 创建失败
     */
    @Insert({
            "insert into pay_order${suffix}(`appId`,`appOrderId`,`prodId`,`appRefundTime`,`refundAmount`,`refundDesc`,`orphanRefund`,",
            "`prodName`,`prodAddiInfo`,`prodDesc`,`payUrl`,`returnUrl`,`notifyUrl`,",
            "`chId`,`chOrderId`,`appOrderTime`,`payMethod`,`bankId`,`amount`,`statusCode`,`statusMsg`,",
            "`userId`,`userName`,`userAddiInfo`,`userContact`,`chAccountId`,`cardNum`,`chIp`,`appIp`,`userIp`,`submitTime`,`lastUpdateTime`,`chDealId`"
                    + ",`cardTotalAmount`,`autoRedirect`,`yyOper`,`yyAmount`,`unit`,`ext`,`category`) values(",
            "#{order.appId},#{order.appOrderId},#{order.prodId},#{order.appRefundTime},#{order.refundAmount},#{order.refundDesc},#{order.orphanRefund},#{order.prodName},#{order.prodAddiInfo},#{order.prodDesc},",
            "#{order.payUrl},#{order.returnUrl},#{order.notifyUrl},#{order.chId},#{order.chOrderId},#{order.appOrderTime},#{order.payMethod},",
            "#{order.bankId},#{order.amount},#{order.statusCode},#{order.statusMsg},#{order.userId},#{order.userName},#{order.userAddiInfo},",
            "#{order.userContact},#{order.chAccountId},#{order.cardNum},#{order.chIp},#{order.appIp},#{order.userIp},#{order.submitTime},#{order.lastUpdateTime},#{order.chDealId}"
                    + ",#{order.cardTotalAmount},#{order.autoRedirect},#{order.yyOper},#{order.yyAmount},#{order.unit},#{order.ext},#{order.category})" })
    int createPayOrder(@Param("order") PayOrder payOrder, @Param("suffix") String suffix);

    /**
     * 更新订单的支付结果
     * 
     * @param payOrder
     * @return
     */
    @Update({
            "update pay_order${suffix} set statusCode=#{order.statusCode}, statusMsg=#{order.statusMsg}, chDealId=#{order.chDealId},",
            " chOrderId=#{order.chOrderId}, chDealTime=#{order.chDealTime}, chIp=#{order.chIp}, chFee=#{order.chFee}, lastUpdateTime=#{order.lastUpdateTime},",
            " bankDealId=#{order.bankDealId}, bankDealTime=#{order.bankDealTime} , appRefundTime=#{order.appRefundTime}, refundAmount=#{order.refundAmount}, refundDesc=#{order.refundDesc},"
                    + " orphanRefund=#{order.orphanRefund} where appId=#{order.appId} and appOrderId=#{order.appOrderId} and lastUpdateTime<=#{order.lastUpdateTime}" })
    int updatePayOrder(@Param("order") PayOrder payOrder, @Param("suffix") String suffix);

    /**
     * 更新ext字段
     * ext字段目前主要用于保存增加Y币和增加保证金的结果
     * 
     * @param payOrder
     * @return
     */
    @Update({ "update pay_order${suffix} set ext = #{order.ext} where appId=#{order.appId} and appOrderId=#{order.appOrderId}" })
    int updateExt(@Param("order") PayOrder payOrder, @Param("suffix") String suffix);

}
