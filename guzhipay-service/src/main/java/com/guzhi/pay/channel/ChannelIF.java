/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel;

import com.guzhi.pay.domain.PayOrder;

/**
 * 渠道接口。<br>
 * 为避免文档重复，主要的说明请见接口文档（yypay/docs），这里仅作必要补充。
 * 
 * @author administrator
 */
public interface ChannelIF {
    /**
     * 检查渠道健康状态
     * 
     * @return JSON字符串，接口状况信息。
     */
    String status();

    /**
     * 支付，生成需要支付的URL
     * 
     * @param payOrder
     * @return payOrder对象
     */
    PayOrder pay(PayOrder payOrder);

    /**
     * 查询，查询支付订单结果
     * 
     * @param payOrder
     */
    PayOrder query(PayOrder payOrder);

    /**
     * 对账请求
     * 
     * @param accounts
     * @return
     */
    // PayAccounts accounts(PayAccounts accounts);
    /**
     * 支付退款处理接口
     * 
     * @param payOrder
     */
    PayOrder refund(PayOrder payOrder);
}
