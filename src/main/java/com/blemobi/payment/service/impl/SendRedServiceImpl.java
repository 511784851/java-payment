package com.blemobi.payment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.RedDao;
import com.blemobi.payment.service.SendRedService;
import com.blemobi.payment.util.IdWorker;
import com.blemobi.payment.util.OrderEnum;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRed;
import com.blemobi.sep.probuf.PaymentProtos.POneRed;
import com.blemobi.sep.probuf.PaymentProtos.PRedPay;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.google.protobuf.ProtocolStringList;

@Service("sendRedService")
public class SendRedServiceImpl implements SendRedService {

	@Autowired
	private RedDao redDao;

	/**
	 * 红包最大领取时间
	 */
	private static final long maxInvalidTime = 24 * 60 * 60 * 1000;

	@Transactional
	public PMessage sendOrdinary(POneRed oneRed, long send_uuid) {
		final int red_type = 1;// 红包类型（1-普通红包，2-等额群红包，3-随机群红包）
		final int num = 1;// 红包数量

		long send_tm = System.currentTimeMillis();
		long invalid_tm = send_tm + maxInvalidTime;

		String content = oneRed.getContent();
		int tot_amount = oneRed.getAmount();
		int any_amount = oneRed.getAmount();

		// 生成订单号
		IdWorker idWorder = new IdWorker(OrderEnum.RED_ORDINARY);
		String ord_no = idWorder.nextId();
		int rs = redDao.insert(ord_no, send_uuid, red_type, tot_amount, any_amount, num, content, send_tm, invalid_tm);
		if (rs != 1)
			throw new RuntimeException("添加发送红包数据异常");

		String rec_uuid = oneRed.getRecuuid();
		redDao.saveRecUUIDS(ord_no, rec_uuid);

		PRedPay redPay = PRedPay.newBuilder().setOrderNum(ord_no).setFenMoney(tot_amount).build();
		return ReslutUtil.createReslutMessage(redPay);
	}

	@Transactional
	public PMessage sendGroup(PGroupRed groupRed, long send_uuid) {
		final int red_type = groupRed.getIsRanNDOM() ? 3 : 2;// 红包类型（1-普通红包，2-等额群红包，3-随机群红包）
		int num = groupRed.getCount();// 红包数量

		long send_tm = System.currentTimeMillis();
		long invalid_tm = send_tm + maxInvalidTime;

		String content = groupRed.getContent();
		int tot_amount = 0;
		int any_amount = 0;
		if (red_type == 3) {
			tot_amount = groupRed.getAmount();
		} else if (red_type == 2) {
			any_amount = groupRed.getAmount();
			tot_amount = any_amount * num;
		}

		// 生成订单号
		IdWorker idWorder = new IdWorker(OrderEnum.RED_ORDINARY);
		String ord_no = idWorder.nextId();
		int rs = redDao.insert(ord_no, send_uuid, red_type, tot_amount, any_amount, num, content, send_tm, invalid_tm);
		if (rs != 1)
			throw new RuntimeException("添加发送红包数据异常");

		ProtocolStringList rec_uuid = groupRed.getRecuuidList();
		redDao.saveRecUUIDS(ord_no, rec_uuid.toArray().toString());

		PRedPay redPay = PRedPay.newBuilder().setOrderNum(ord_no).setFenMoney(tot_amount).build();
		return ReslutUtil.createReslutMessage(redPay);
	}
}
