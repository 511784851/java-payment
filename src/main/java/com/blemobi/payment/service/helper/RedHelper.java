package com.blemobi.payment.service.helper;

import com.blemobi.payment.model.Red;
import com.blemobi.payment.util.OrdernoUtil;

/**
 * 发送红包
 * 
 * @author zhaoyong
 *
 */
public class RedHelper {
	/**
	 * 红包最大领取时间
	 */
	private static final long maxInvalidTime = 24 * 60 * 60 * 1000;
	private String sendUUID;
	private String receiveUUID;
	private long amount;

	public RedHelper(String sendUUID, String receiveUUID, long amount) {
		this.sendUUID = sendUUID;
		this.receiveUUID = receiveUUID;
		this.amount = amount;
	}

	public Red initOne() {
		long sendTime = System.currentTimeMillis();
		long invalidTime = sendTime + maxInvalidTime;
		// 生成订单号
		String custorderno = OrdernoUtil.build(sendUUID, sendTime, amount);

		Red red = new Red();
		red.setCustorderno(custorderno);
		red.setSenduuid(sendUUID);
		red.setReceiveuuid(receiveUUID);
		red.setSendtime(sendTime);
		red.setInvalidtime(invalidTime);
		red.setStatus(-2);// 等待支付
		return red;
	}
}
