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
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.Constants.OrderEnum;
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

	/**
	 * 发普通红包
	 */
	@Transactional
	public PMessage sendOrdinary(POrdinRedEnve ordinRedEnve, String send_uuid) {
		int type = OrderEnum.RED_ORDINARY.getValue();// 红包类型为普通红包
		int tota_number = 1;// 红包数量固定为1
		int each_money = 0;// 单个红包金额固定为0
		int tota_money = ordinRedEnve.getMoney();// 红包金额
		String content = ordinRedEnve.getContent();// 描述
		String rece_uuid = ordinRedEnve.getReceUuid();// 领取用户
		return initOrderInfo(tota_money, each_money, tota_number, type, send_uuid, content, rece_uuid);
	}

	/**
	 * 发群红包
	 */
	@Transactional
	public PMessage sendGroup(PGroupRedEnve groupRedEnve, String send_uuid) {
		int tota_number = groupRedEnve.getNumber();// 红包数量
		int type = 0;// 红包类型
		int tota_money = 0;// 红包总金额
		int each_money = 0;// 单个红包金额
		if (groupRedEnve.getIsRandom()) { // 红包类型为随机群红包
			type = OrderEnum.RED_GROUP_RANDOM.getValue();
			tota_money = groupRedEnve.getMoney();
		} else { // 红包类型为等额群红包
			type = OrderEnum.RED_GROUP_EQUAL.getValue();
			each_money = groupRedEnve.getMoney();
			tota_money = each_money * tota_number;
		}
		String content = groupRedEnve.getContent();
		ProtocolStringList rece_uuid = groupRedEnve.getReceUuidList();
		return initOrderInfo(tota_money, each_money, tota_number, type, send_uuid, content, rece_uuid.toArray());
	}

	/**
	 * 初始化订单信息
	 * 
	 * @param tota_money
	 *            总金额
	 * @param each_money
	 *            单个金额
	 * @param tota_number
	 *            总数量
	 * @param type
	 *            类型
	 * @param send_uuid
	 *            发送者uuid
	 * @param content
	 *            描述
	 * @param rece_uuid
	 *            领取这uuid
	 * @return
	 */
	private PMessage initOrderInfo(int tota_money, int each_money, int tota_number, int type, String send_uuid,
			String content, Object... rece_uuid) {
		// 验证红包发送是否符合规则
		PMessage message = verification(tota_money, tota_number, content);
		if (message != null)
			return message;

		long send_tm = System.currentTimeMillis();// 发送时间
		long over_tm = send_tm + Constants.max_interval_Time;// 失效时间
		String ord_no = createOrdNo(type);// 订单号

		// 保存订单数据
		int rs = redSendDao.insert(ord_no, send_uuid, type, tota_money, each_money, tota_number, content, send_tm,
				over_tm);
		if (rs != 1)
			throw new RuntimeException("保存发送红包数据未成功");

		// 保存红包可领取人信息
		redJedisDao.putReceiveUsers(ord_no, rece_uuid);

		// 如果是随机群红包，计算随机金额
		if (type == OrderEnum.RED_GROUP_RANDOM.getValue())
			randomRed(tota_number, tota_money, ord_no);

		// 生成支付信息给APP端
		SignHelper signHelper = new SignHelper(send_uuid, tota_money, ord_no, "红包");
		POrderPay orderPay = signHelper.getOrderPay();
		return ReslutUtil.createReslutMessage(orderPay);
	}

	/**
	 * 计算随机金额
	 * 
	 * @param tota_number
	 *            总金额
	 * @param tota_money
	 *            总数量
	 * @param ord_no
	 *            订单号
	 */
	private void randomRed(int tota_number, int tota_money, String ord_no) {
		RandomRedHelper rdrHelper = new RandomRedHelper(tota_money, tota_number);
		int[] random_money = rdrHelper.distribution();
		redJedisDao.putRedRandDomMoney(ord_no, random_money);
	}

	/**
	 * 验证红包是否符合规则
	 * 
	 * @param tota_money
	 *            总金额
	 * @param tota_number
	 *            总数量
	 * @return
	 */
	private PMessage verification(int tota_money, int tota_number, String content) {
		if (tota_number < 1)
			return ReslutUtil.createErrorMessage(2101001, "红包个数不能少于1！");
		if (Strings.isNullOrEmpty(content))
			return ReslutUtil.createErrorMessage(2101002, "红包描述不能为空");
		if (content.length() > 100)
			return ReslutUtil.createErrorMessage(2101003, "红包描述不能超过100个字符");
		if (tota_money < tota_number * Constants.min_each_money)
			return ReslutUtil.createErrorMessage(2101004, "单个红包金额不能少于0.01元");
		if (tota_money > tota_number * Constants.max_each_money)
			return ReslutUtil.createErrorMessage(2101005, "单个红包金额不能超过200元");
		if (tota_money > Constants.max_tota_money)
			return ReslutUtil.createErrorMessage(2101006, "单次支付总额不可超过10000元");
		return null;
	}

	/**
	 * 生成订单号
	 * 
	 * @param type
	 *            订单类型
	 * @return
	 */
	private String createOrdNo(int type) {
		IdWorker idWorder = IdWorker.getInstance();
		return idWorder.nextId(type);
	}
}
