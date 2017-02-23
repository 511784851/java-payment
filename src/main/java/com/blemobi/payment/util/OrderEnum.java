package com.blemobi.payment.util;

/**
 * 业务订单类型
 * 
 * @author zhaoyong
 *
 */
public enum OrderEnum {

	/**
	 * 普通红包
	 */
	RED_ORDINARY(1),

	/**
	 * 等额群红包
	 */
	RED_GROUP_AVG(2),

	/**
	 * 随机群红包
	 */
	RED_GROUP_RAND(3),

	/**
	 * 打赏
	 */
	REWARD(4),

	/**
	 * 抽奖
	 */
	LUCK_DRAW(5);

	private int value;

	private OrderEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
