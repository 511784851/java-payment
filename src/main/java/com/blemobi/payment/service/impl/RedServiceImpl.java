package com.blemobi.payment.service.impl;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.RedDao;
import com.blemobi.payment.service.RedService;
import com.blemobi.payment.util.OrdernoUtil;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRed;
import com.blemobi.sep.probuf.PaymentProtos.POneRed;
import com.blemobi.sep.probuf.PaymentProtos.PRedPay;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

@Service("redService")
public class RedServiceImpl implements RedService {

	@Autowired
	private RedDao redDao;

	/**
	 * 红包最大领取时间
	 */
	private static final long maxInvalidTime = 24 * 60 * 60 * 1000;

	public PMessage sendOrdinary(POneRed oneRed, String senduuid) {
		final int type = 1;// 普通红包
		final int status = 0;
		int amount = oneRed.getAmount();
		long sendTime = System.currentTimeMillis();
		long invalidTime = sendTime + maxInvalidTime;
		// 生成订单号
		String orderno = OrdernoUtil.build(senduuid, sendTime, oneRed.getAmount());
		int rs = redDao.insert(orderno, senduuid, type, amount, oneRed.getContent(), sendTime, invalidTime, status);
		if (rs != 1)
			throw new RuntimeException("添加发送红包数据异常");

		PRedPay redPay = PRedPay.newBuilder().setOrderNum(orderno).setFenMoney(amount).build();
		return ReslutUtil.createReslutMessage(redPay);
	}

	public PMessage sendGroup(PGroupRed groupRed, String senduuid) {
		final int type = groupRed.getIsRanNDOM();
		final int status = 0;
		int amount = groupRed.getAmount();
		long sendTime = System.currentTimeMillis();
		long invalidTime = sendTime + maxInvalidTime;
		// 生成订单号
		String orderNo = OrdernoUtil.build(senduuid, sendTime, groupRed.getAmount());
		redDao.insert(orderNo, senduuid, amount, groupRed.getContent(), sendTime, invalidTime);

		PRedPay redPay = PRedPay.newBuilder().setOrderNum(orderNo).setFenMoney(amount).build();
		return ReslutUtil.createReslutMessage(redPay);
	}
}
