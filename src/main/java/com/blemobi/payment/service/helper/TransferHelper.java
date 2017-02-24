package com.blemobi.payment.service.helper;

/**
 * 转账
 * 
 * @author zhaoyong
 *
 */
public class TransferHelper {

	/** 领取人 */
	private long rece_uuid;

	/** 领取金额（单位：分） */
	private int money;

	/**
	 * 构造方法
	 * 
	 * @param uuid
	 * @param amount
	 */
	public TransferHelper(long rece_uuid, int money) {
		this.rece_uuid = rece_uuid;
		this.money = money;
	}

	/**
	 * B2C转账
	 * 
	 * @return
	 */
	public boolean execute() {

		return true;
	}
}