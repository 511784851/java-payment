package com.blemobi.payment.service;

import org.apache.ibatis.session.SqlSession;

import com.blemobi.payment.mapper.RedMapper;
import com.blemobi.payment.model.Red;
import com.blemobi.payment.test.DBTools;
import com.blemobi.payment.util.OrdernoUtil;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 发送红包
 * 
 * @author zhaoyong
 *
 */
public class SendRedService {
	/**
	 * 红包最大领取时间
	 */
	private static final long maxInvalidTime = 24 * 60 * 60 * 1000;
	private String sendUUID;
	private String receiveUUID;
	private int amount;

	public SendRedService(String sendUUID, String receiveUUID, int amount) {
		this.sendUUID = sendUUID;
		this.receiveUUID = receiveUUID;
		this.amount = amount;
	}

	public PMessage send() {
		SqlSession session = DBTools.getSession();
		RedMapper mapper = session.getMapper(RedMapper.class);
		try {
			Red red = initRed();
			mapper.insert(red);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
		} finally {
			session.close();
		}

		return null;
	}

	private Red initRed() {
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
