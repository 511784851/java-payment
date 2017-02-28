package com.blemobi.payment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.RedJedisDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.service.RedSendService;
import com.blemobi.payment.service.helper.RandomRedHelper;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.service.order.IdWorker;
import com.blemobi.payment.service.order.OrderEnum;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRedEnve;
import com.blemobi.sep.probuf.PaymentProtos.POrderPay;
import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
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
	public PMessage sendOrdinary(POrdinRedEnve ordinRedEnve, String send_uuid) {
		final int type = OrderEnum.RED_ORDINARY.getValue();// 红包类型
		final int tota_number = 1;// 红包数量
		final String goodsName = "红包";

		String content = ordinRedEnve.getContent();
		int tota_money = ordinRedEnve.getMoney();
		int each_money = ordinRedEnve.getMoney();

		String rece_uuid = ordinRedEnve.getReceUuid();
		String ord_no = initOrderInfo(tota_money, each_money, tota_number, type, send_uuid, content, rece_uuid);
		if (Strings.isNullOrEmpty(ord_no))
			return message;

		SignHelper signHelper = new SignHelper(send_uuid, tota_money, ord_no, goodsName);
		POrderPay orderPay = signHelper.getOrderPay();
		return ReslutUtil.createReslutMessage(orderPay);
	}

	/**
	 * 发群红包
	 */
	@Transactional
	public PMessage sendGroup(PGroupRedEnve groupRedEnve, String send_uuid) {
		final int type = getGroupRedType(groupRedEnve);// 红包类型
		final int tota_number = groupRedEnve.getNumber();// 红包数量

		String content = groupRedEnve.getContent();
		int tota_money = 0;
		int each_money = 0;
		if (groupRedEnve.getIsRandom()) {
			tota_money = groupRedEnve.getMoney();
			each_money = tota_money / tota_number;
		} else {
			each_money = groupRedEnve.getMoney();
			tota_money = each_money * tota_number;
		}

		ProtocolStringList rece_uuid = groupRedEnve.getReceUuidList();
		String ord_no = initOrderInfo(tota_money, each_money, tota_number, type, send_uuid, content,
				rece_uuid.toArray());
		if (Strings.isNullOrEmpty(ord_no))
			return message;

		/** 如果是随机群红包，分配随机金额 */
		if (groupRedEnve.getIsRandom()) {
			RandomRedHelper rdrHelper = new RandomRedHelper(tota_money, tota_number);
			int[] random_money = rdrHelper.distribution();
			redJedisDao.putRedRandDomMoney(ord_no, random_money);
		}

		POrderPay redPay = POrderPay.newBuilder().setOrderNum(ord_no).setFenMoney(tota_money).build();
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
	private String initOrderInfo(int tota_money, int each_money, int tota_number, int type, String send_uuid,
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
	private int getGroupRedType(PGroupRedEnve groupRedEnve) {
		return groupRedEnve.getIsRandom() ? OrderEnum.RED_GROUP_RANDOM.getValue()
				: OrderEnum.RED_GROUP_EQUAL.getValue();
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
