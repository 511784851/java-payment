package com.blemobi.payment.service.helper;

/**
 * 转账
 * 
 * @author zhaoyong
 *
 */
public class TransferHelper {

	private String uuid;
	private int amount;

	/**
	 * 构造方法
	 * 
	 * @param uuid
	 * @param amount
	 */
	public TransferHelper(String uuid, int amount) {
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