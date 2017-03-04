/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.util
 *
 *    Filename:    Constants.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月27日 下午4:52:17
 *
 *    Revision:
 *
 *    2017年2月27日 下午4:52:17
 *
 *****************************************************************/
package com.blemobi.payment.util;

/**
 * @ClassName Constants
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年2月27日 下午4:52:17
 * @version 1.0.0
 */
public final class Constants {

	/** 单个红包最小金额（单位：分） */
	public static final int min_each_money = 1;

	/** 单个红包最大金额（单位：分） */
	public static final int max_each_money = 20000;

	/** 红包最大总金额（单位：分） */
	public static final int max_tota_money = 1000000;

	/** 单日发送最大总金额（单位：分） */
	public static final int max_daily_money = 3000000;

	/** 红包最大有效领取时间（单位：毫秒） */
	public static final long max_interval_Time = 24 * 60 * 60 * 1000;
	
	public static final String RONG_YUN_BASE_URL = "http://api-test.jrmf360.com";
	public static final String B2C_TRANSFER_URI = "/api/v1/standardWallet/transferToUser.shtml";
	public enum HTMLSTS {
		SUCCESS("success"), FAILED("failed");
		private String value;

		private HTMLSTS(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum RESPSTS {
		SUCCESS("0000");
		private String value;

		private RESPSTS(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum RONGYUN_ORD_STS {
		SUCCESS("1");
		private String value;

		private RONGYUN_ORD_STS(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * 业务订单类型
	 * 
	 * @author zhaoyong
	 *
	 */
	public enum OrderEnum {
		/** 普通红包 */
		RED_ORDINARY(1),

		/** 等额群红包 */
		RED_GROUP_EQUAL(2),

		/** 随机群红包 */
		RED_GROUP_RANDOM(3),

		/** 打赏 */
		REWARD(4),

		/** 抽奖 */
		LUCK_DRAW(5);

		private int value;

		private OrderEnum(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}