package com.blemobi.payment.service;

/**
 * 转账
 * 
 * @author zhaoyong
 *
 */
public class TransferUtil {

	private String uuid;
	private int amount;

	/**
	 * 构造方法
	 * 
	 * @param uuid
	 * @param amount
	 */
	public TransferUtil(String uuid, int amount) {
		this.uuid = uuid;
		this.amount = amount;
	}

	/**
	 * B2C转账
	 * 
	 * @return
	 */
	public boolean transfer() {

		return true;
	}
}