package com.blemobi.payment.service;

import com.blemobi.payment.model.Transaction;

/**
 * 钱包支付回调接口类
 * 
 * @author zhaoyong
 *
 */
public interface NotifyService {
	/**
	 * 回调处理
	 * 
	 * @return
	 */
	public String callback(Transaction transaction, String sign);
}
