package com.blemobi.payment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.RedJedisDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.service.RedSendService;
import com.blemobi.payment.service.helper.RandomRedHelper;
import com.blemobi.payment.service.order.IdWorker;
import com.blemobi.payment.service.order.OrderEnum;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRed;
import com.blemobi.sep.probuf.PaymentProtos.POrdinaryRed;
import com.blemobi.sep.probuf.PaymentProtos.PRedPay;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.google.common.base.Strings;
import com.google.protobuf.ProtocolStringList;

/**
 * 发红包业务实现类
 * 
 * @author zhaoyong
 *
 */
@Service("redSendService")
public class RedSendServiceImpl implements RedSendService {

	@Autowired
	private RedSendDao redSendDao;

	@Autowired
	private RedJedisDao redJedisDao;

	/** 单个红包最小金额（单位：分） */
	private final int min_each_money = 1;

	/** 单个红包最大金额（单位：分） */
	private final int max_each_money = 20000;

	/** 红包最大总金额（单位：分） */
	private final int max_tota_money = 1000000;

	/** 红包最大领取时间（单位：毫秒） */
	private static final long maxOverTime = 24 * 60 * 60 * 1000;

	private PMessage message;

	/**
	 * 发普通红包
	 */
	@Transactional
	public PMessage sendOrdinary(POrdinaryRed ordinaryRed, long send_uuid) {
		final int type = OrderEnum.RED_ORDINARY.getValue();// 红包类型
		final int tota_number = 1;// 红包数量

		String content = ordinaryRed.getContent();
		int tota_money = ordinaryRed.getMoney();
		int each_money = ordinaryRed.getMoney();

		String rece_uuid = ordinaryRed.getReceUuid();
		String ord_no = initOrderInfo(tota_money, each_money, tota_number, type, send_uuid, content, rece_uuid);
		if (Strings.isNullOrEmpty(ord_no))
			return message;

		PRedPay redPay = PRedPay.newBuilder().setOrderNum(ord_no).setFenMoney(tota_money).build();
		return ReslutUtil.createReslutMessage(redPay);
	}

	/**
	 * 发群红包
	 */
	@Transactional
	public PMessage sendGroup(PGroupRed groupRed, long send_uuid) {
		final int type = getGroupRedType(groupRed);// 红包类型
		final int tota_number = groupRed.getCount();// 红包数量

		String content = groupRed.getContent();
		int tota_money = 0;
		int each_money = 0;
		if (groupRed.getIsRandom()) {
			tota_money = groupRed.getMoney();
			each_money = tota_money / tota_number;
		} else {
			each_money = groupRed.getMoney();
			tota_money = each_money * tota_number;
		}

		ProtocolStringList rece_uuid = groupRed.getReceUuidList();
		String ord_no = initOrderInfo(tota_money, each_money, tota_number, type, send_uuid, content,
				rece_uuid.toArray());
		if (Strings.isNullOrEmpty(ord_no))
			return message;

		/** 如果是随机群红包，分配随机金额 */
		if (groupRed.getIsRandom()) {
			RandomRedHelper rdrHelper = new RandomRedHelper(tota_money, tota_number);
			int[] random_money = rdrHelper.distribution();
			redJedisDao.putRedRandDomMoney(ord_no, random_money);
		}

		PRedPay redPay = PRedPay.newBuilder().setOrderNum(ord_no).setFenMoney(tota_money).build();
		return ReslutUtil.createReslutMessage(redPay);
	}

	/**
	 * 初始化订单信息
	 * 
	 * @param tota_money
	 * @param each_money
	 * @param tota_number
	 * @param type
	 * @param send_uuid
	 * @param content
	 * @param rece_uuid
	 * @return
	 */
	private String initOrderInfo(int tota_money, int each_money, int tota_number, int type, long send_uuid,
			String content, Object... rece_uuid) {
		boolean bool = checkMoney(tota_money, each_money, tota_number);
		if (!bool)
			return "";

		long send_tm = System.currentTimeMillis();
		long over_tm = send_tm + maxOverTime;

		String ord_no = createOrdNo(type);
		int rs = redSendDao.insert(ord_no, send_uuid, type, tota_money, each_money, tota_number, content, send_tm,
				over_tm);
		if (rs != 1)
			throw new RuntimeException("添加发送红包数据异常");

		redJedisDao.putReceiveUsers(ord_no, rece_uuid);
		return ord_no;
	}

	/**
	 * 验证红包金额是否符合规则
	 * 
	 * @param tota_money
	 * @param each_money
	 * @param tota_number
	 * @return
	 */
	private boolean checkMoney(int tota_money, int each_money, int tota_number) {
		if (each_money < min_each_money) {
			message = ReslutUtil.createErrorMessage(2101001, "单个红包金额不能少于0.01元");
			return false;
		}
		if (each_money > max_each_money) {
			message = ReslutUtil.createErrorMessage(2101002, "单个红包金额不能超过200元");
			return false;
		}
		if (tota_money > max_tota_money) {
			message = ReslutUtil.createErrorMessage(2101003, "单次支付总额不可超过10000元");
			return false;
		}
		return true;
	}

	/**
	 * 获得群红包类型
	 * 
	 * @param groupRed
	 * @return
	 */
	private int getGroupRedType(PGroupRed groupRed) {
		return groupRed.getIsRandom() ? OrderEnum.RED_GROUP_RANDOM.getValue() : OrderEnum.RED_GROUP_EQUAL.getValue();
	}

	/**
	 * 生成订单号
	 * 
	 * @param type
	 * @return
	 */
	private String createOrdNo(final int type) {
		IdWorker idWorder = IdWorker.getInstance();
		return idWorder.nextId(type);
	}
}
