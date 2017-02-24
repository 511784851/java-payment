package com.blemobi.payment.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.RedJedisDao;
import com.blemobi.payment.dao.RedReceiveDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.model.RedReceive;
import com.blemobi.payment.model.RedSend;
import com.blemobi.payment.service.RedReceiveService;
import com.blemobi.payment.service.helper.TransferHelper;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PReceive;
import com.blemobi.sep.probuf.PaymentProtos.PRedInfo;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 领红包实现类
 * 
 * @author zhaoyong
 *
 */
@Service("redReceiveService")
public class RedReceiveServiceImpl implements RedReceiveService {

	@Autowired
	private RedSendDao redSendDao;

	@Autowired
	private RedReceiveDao redReceiveDao;

	@Autowired
	private RedJedisDao redJedisDao;

	private PMessage message;

	/**
	 * 领红包
	 */
	@Transactional
	public PMessage receive(String ord_no, long rece_uuid) {
		int status = 0;// 红包当前针对用户状态（0-无权限领取，1-可领取，2-已领取，3-已领完，4-已过期）

		RedSend redSend = redSendDao.selectByKey(ord_no);
		RedReceive receive = redReceiveDao.selectByKey(ord_no, rece_uuid);
		if (receive != null) {
			status = 2;
		} else {
			boolean bool = check(ord_no, rece_uuid, redSend);
			if (!bool)
				return message;

			int type = redSend.getType();
			int each_money = redSend.getEach_money();
			long rece_tm = System.currentTimeMillis();
			if (type == 1 || type == 2) {// 普通红包 &等额群红包
				receiveing(ord_no, rece_uuid, each_money, rece_tm);
			} else if (type == 3) {// 随机群红包
				String random_money_str = redJedisDao.findRandomMoneyByOrdNoAndIdx(ord_no, redSend.getRece_number());
				int random_money = Integer.parseInt(random_money_str);
				receiveing(ord_no, rece_uuid, random_money, rece_tm);
			}
		}

		PRedInfo redInfo = buildRedInfo(redSend, ord_no, status);

		return ReslutUtil.createReslutMessage(redInfo);
	}

	/**
	 * 构建PRedInfo数据
	 * 
	 * @param redSend
	 * @param type
	 * @param p_receive_list
	 * @return
	 */
	private PRedInfo buildRedInfo(RedSend redSend, String ord_no, int status) {
		List<RedReceive> receiveList = redReceiveDao.selectByKey(ord_no);
		List<PReceive> p_receive_list = new ArrayList<PReceive>();
		for (RedReceive redReceive : receiveList) {
			PUserBase userBase = PUserBase.newBuilder().setUUID(redReceive.getRece_uuid() + "").build();
			PReceive p_receive = PReceive.newBuilder().setId(redReceive.getId()).setMoney(redReceive.getMoney())
					.setReceTm(redReceive.getRece_tm()).setUserBase(userBase).build();
			p_receive_list.add(p_receive);
		}

		PUserBase userBase = PUserBase.newBuilder().setUUID(redSend.getSend_uuid() + "").build();
		PRedInfo redInfo = PRedInfo.newBuilder().setOrdNo(redSend.getOrd_no()).setUserBase(userBase).setStatus(status)
				.setType(redSend.getType()).setTotaMoney(redSend.getTota_money())
				.setTotaNumber(redSend.getTota_number()).setReceMoney(redSend.getRece_money())
				.setReceNumber(redSend.getRece_number()).setContent(redSend.getContent())
				.setSendTm(redSend.getSend_tm()).addAllReceive(p_receive_list).build();
		return redInfo;
	}

	/**
	 * 领红包并转账
	 * 
	 * @param ord_no
	 * @param rece_uuid
	 * @param each_money
	 * @param rece_tm
	 */
	private void receiveing(String ord_no, long rece_uuid, int each_money, long rece_tm) {
		TransferHelper th = new TransferHelper(rece_uuid, each_money);
		boolean th_bool = th.execute();
		if (th_bool) {
			redSendDao.update(ord_no, each_money);
			redReceiveDao.insert(ord_no, rece_uuid, each_money, rece_tm);
		}
	}

	/**
	 * 验证用户是否可以领取该红包
	 * 
	 * @param ord_no
	 * @param rece_uuid
	 * @param redSend
	 * @return
	 */
	private boolean check(String ord_no, long rece_uuid, RedSend redSend) {
		Set<String> set = redJedisDao.findUsersByOrdNo(ord_no);
		if (!set.contains(rece_uuid + "")) {
			message = ReslutUtil.createErrorMessage(2101001, "没有权限领取红包");
			return false;
		}

		if (redSend.getPay_status() != 1) {
			message = ReslutUtil.createErrorMessage(2101002, "红包还不可以领取");
			return false;
		}

		if (redSend.getRece_number() >= redSend.getTota_number()
				|| redSend.getRece_money() >= redSend.getTota_money()) {
			message = ReslutUtil.createErrorMessage(2101002, "红包已领完");
			return false;
		}

		long now = System.currentTimeMillis();
		if (now >= redSend.getOver_tm()) {
			message = ReslutUtil.createErrorMessage(2101003, "红包已过期");
			return false;
		}

		return true;
	}
}
