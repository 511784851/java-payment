package com.blemobi.payment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.grpc.RobotGrpcClient;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.JedisDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.model.RedSend;
import com.blemobi.payment.service.RedSendService;
import com.blemobi.payment.service.helper.RandomRedHelper;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.DataPublishingProtos.PFansFilterParam;
import com.blemobi.sep.probuf.PaymentProtos.POrderPay;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveBaseInfo;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveList;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PStringSingle;
import com.blemobi.sep.probuf.RobotApiProtos.PPayOrderParma;
import com.google.common.base.Strings;

import lombok.extern.log4j.Log4j;

/**
 * 发红包业务实现类
 * 
 * @author zhaoyong
 *
 */
@Log4j
@Service("redSendService")
public class RedSendServiceImpl implements RedSendService {

	@Autowired
	private RedSendDao redSendDao;

	@Autowired
	private JedisDao jedisDao;

	/**
	 * 发普通红包
	 */
	@Transactional
	public PMessage sendOrdinary(String send_uuid, int money, String content, String rece_uuid) {
		int type = OrderEnum.RED_ORDINARY.getValue();// 红包类型为普通红包
		int tota_number = 1;// 红包数量固定为1
		int each_money = 0;// 单个红包金额固定为0
		return initOrderInfo(money, each_money, tota_number, type, send_uuid, content, rece_uuid);
	}

	/**
	 * 发群红包
	 */
	@Transactional
	public PMessage sendGroup(String send_uuid, int number, int money, boolean isRandom, String content,
			String tick_uuid, PFansFilterParam fansFilterParam) {
		int tota_number = number;// 红包数量
		int type = 0;// 红包类型
		int tota_money = 0;// 红包总金额
		int each_money = 0;// 单个红包金额
		if (isRandom) { // 红包类型为随机群红包
			type = OrderEnum.RED_GROUP_RANDOM.getValue();
			tota_money = money;
		} else { // 红包类型为等额群红包
			type = OrderEnum.RED_GROUP_EQUAL.getValue();
			each_money = money;
			tota_money = each_money * tota_number;
		}

		String[] rece_uuid = getReceUUIDS(fansFilterParam);

		return initOrderInfo(tota_money, each_money, tota_number, type, send_uuid, content, rece_uuid);
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
			String content, String... rece_uuid) {
		// 验证红包发送是否符合规则
		PMessage message = verification(send_uuid, tota_money, tota_number, content, rece_uuid);
		if (message != null)
			return message;

		int rece_tota_num = rece_uuid.length;// 参与人数
		String rece_uuid5 = getReceUser5(rece_uuid);

		long send_tm = System.currentTimeMillis();// 发送时间
		long over_tm = send_tm + Constants.max_interval_Time;// 失效时间
		String ord_no = createOrdNo(type, tota_money);// 订单号

		// 保存订单数据
		int rs = redSendDao.insert(ord_no, send_uuid, type, tota_money, each_money, tota_number, content, send_tm,
				over_tm, rece_tota_num, rece_uuid5);
		if (rs != 1)
			throw new RuntimeException("保存发送红包数据失败");

		// 如果是随机群红包，计算随机金额
		if (type == OrderEnum.RED_GROUP_RANDOM.getValue())
			randomRed(tota_number, tota_money, ord_no);

		// 保存群红包可领取人信息
		if (type != OrderEnum.RED_ORDINARY.getValue()) {

		}

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
		jedisDao.putRedRandDomMoney(ord_no, random_money);
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
	private PMessage verification(String send_uuid, int tota_money, int tota_number, String content,
			String... rece_uuid) {
		if (rece_uuid == null || rece_uuid.length == 0)
			return ReslutUtil.createErrorMessage(2101001, "红包没有课领取人");
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
		int has_send_money = jedisDao.findDailySendMoney(send_uuid);
		if (has_send_money + tota_money > Constants.max_daily_money)
			return ReslutUtil.createErrorMessage(2101007, "每天发送总金额（红包、抽奖、打赏）不能超过30000元 ");
		return null;
	}

	/**
	 * 获得参与者uuid
	 * 
	 * @param filter
	 *            筛选条件
	 * @return
	 */
	private String[] getReceUUIDS(PFansFilterParam filter) {
		String[] array = new String[] { "1471175703665920836", "1471175703665920837", "1471175703665920838" };
		return array;
	}

	@Override
	public PMessage list(String uuid, int idx, int count) throws IOException {

		log.debug("uuid:" + uuid);

		idx = checkIdx(idx);
		List<RedSend> list = redSendDao.selectByPage(uuid, idx, count);
		List<PRedEnveBaseInfo> redList = new ArrayList<PRedEnveBaseInfo>();
		for (RedSend redSend : list) {
			PRedEnveBaseInfo redInfo = buildRedEnveBaseInfo(redSend);
			redList.add(redInfo);
		}
		log.debug("list.size()" + list.size());
		PRedEnveList redEnveList = PRedEnveList.newBuilder().addAllRedEnveBaseInfo(redList).build();
		return ReslutUtil.createReslutMessage(redEnveList);
	}

	/**
	 * 构建PRedEnveBaseInfo对象
	 * 
	 * @param redSend
	 *            红包信息
	 * @return
	 * @throws IOException
	 */
	private PRedEnveBaseInfo buildRedEnveBaseInfo(RedSend redSend) throws IOException {
		List<PUserBase> userList5 = getReceUser5(redSend.getRece_uuid5());
		return PRedEnveBaseInfo.newBuilder().setId(redSend.getId()).setOrdNo(redSend.getOrd_no())
				.setType(redSend.getType()).setContent(redSend.getContent()).setSendTm(redSend.getSend_tm())
				.setNumber(redSend.getRece_tota_num()).addAllUserBase(userList5).build();
	}

	/**
	 * 获得红包前五个可领取用户信息
	 * 
	 * @param redSend
	 *            红包信息
	 * @return
	 * @throws IOException
	 */
	private List<PUserBase> getReceUser5(String uuid5) throws IOException {
		List<PUserBase> userList5 = new ArrayList<PUserBase>();
		for (String rece_uuid : uuid5.split(",")) {
			PUserBase userBase = UserBaseCache.get(rece_uuid);
			userList5.add(userBase);
		}
		return userList5;
	}

	/**
	 * 处理分页起始值
	 * 
	 * @param idx
	 * @return
	 */
	private int checkIdx(int idx) {
		return idx > 0 ? idx : Integer.MAX_VALUE;
	}

	/**
	 * 生成订单号
	 * 
	 * @param type
	 *            订单类型
	 * @param money
	 *            订单金额
	 * @return
	 */
	private String createOrdNo(int type, int money) {
		PPayOrderParma payOrderParma = PPayOrderParma.newBuilder().setAmount(money).setServiceNo(type)
				.build();

		RobotGrpcClient client = new RobotGrpcClient();
		PStringSingle ordNoString = client.generateOrder(payOrderParma);
		String ord_no = ordNoString != null ? ordNoString.getVal() : "";
		if (Strings.isNullOrEmpty(ord_no))
			throw new RuntimeException("生成订单号出错");

		return ord_no;
	}

	/**
	 * 获得领红包前5个用户
	 * 
	 * @param rece_uuid
	 * @return
	 */
	private String getReceUser5(String... rece_uuid) {
		StringBuffer uuid5 = new StringBuffer();
		int len = rece_uuid.length;
		len = len > 5 ? 5 : len;
		for (String uuid : rece_uuid) {
			if (uuid5.length() > 0)
				uuid5.append(",");
			uuid5.append(uuid);
		}
		return uuid5.toString();
	}

}
