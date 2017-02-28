package com.blemobi.payment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.RewardDao;
import com.blemobi.payment.service.RewardService;
import com.blemobi.payment.service.order.IdWorker;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.sep.probuf.PaymentProtos.POrderPay;
import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.google.common.base.Strings;

/**
 * 打赏业务实现类
 * 
 * @author zhaoyong
 *
 */
@Service("rewardService")
public class RewardServiceImpl implements RewardService {

	@Autowired
	private RewardDao rewardDao;

	/** 打赏最小金额（单位：分） */
	private final int min_money = 1;

	/** 打赏最大金额（单位：分） */
	private final int max_money = 1000000;

	private PMessage message;

	/**
	 * 打赏
	 */
	@Transactional
	public PMessage reward(POrdinRedEnve ordinRedEnve, String send_uuid) {
		String content = ordinRedEnve.getContent();
		int money = ordinRedEnve.getMoney();
		String rece_uuid = ordinRedEnve.getReceUuid();

		String ord_no = initRewardInfo(money, send_uuid, content, rece_uuid);
		if (Strings.isNullOrEmpty(ord_no))
			return message;

		POrderPay redPay = POrderPay.newBuilder().setOrderNum(ord_no).setFenMoney(money).build();
		return ReslutUtil.createReslutMessage(redPay);
	}

	/**
	 * 初始化订单信息
	 * 
	 * @param money
	 * @param send_uuid
	 * @param content
	 * @param rece_uuid
	 * @return
	 */
	private String initRewardInfo(int money, String send_uuid, String content, String rece_uuid) {
		boolean bool = checkMoney(money);
		if (!bool)
			return "";

		long send_tm = System.currentTimeMillis();

		String ord_no = createOrdNo(OrderEnum.REWARD.getValue());
		int rs = rewardDao.insert(ord_no, send_uuid, rece_uuid, money, content, send_tm);
		if (rs != 1)
			throw new RuntimeException("添加发送红包数据异常");

		return ord_no;
	}

	/**
	 * 验证金额是否符合规则
	 * 
	 * @param tota_money
	 * @param each_money
	 * @param tota_number
	 * @return
	 */
	private boolean checkMoney(int money) {
		if (money < min_money) {
			message = ReslutUtil.createErrorMessage(2101001, "单个红包金额不能少于0.01元");
			return false;
		}
		if (money > max_money) {
			message = ReslutUtil.createErrorMessage(2101003, "单次支付总额不可超过10000元");
			return false;
		}
		return true;
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