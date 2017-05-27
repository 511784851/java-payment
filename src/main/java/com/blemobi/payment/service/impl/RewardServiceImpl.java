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
import com.blemobi.payment.dao.RewardDao;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.model.Reward;
import com.blemobi.payment.service.RewardService;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.POrderPay;
import com.blemobi.sep.probuf.PaymentProtos.PRewardInfo;
import com.blemobi.sep.probuf.PaymentProtos.PRewardInfoList;
import com.blemobi.sep.probuf.PaymentProtos.PRewardList;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PStringSingle;
import com.blemobi.sep.probuf.RobotApiProtos.PPayOrderParma;
import com.google.common.base.Strings;

/**
 * 红包业务实现类
 * 
 * @author zhaoyong
 *
 */
@Service("rewardService")
public class RewardServiceImpl implements RewardService {

	@Autowired
	private RewardDao rewardDao;

	@Autowired
	private JedisDao jedisDao;

	@Override
	@Transactional
	public PMessage reward(String send_uuid, int money, String content, String rece_uuid) {
		if (Strings.isNullOrEmpty(content))
			content = "";
		PMessage message = verification(send_uuid, money, content, rece_uuid);
		if (message != null)
			return message;

		long send_tm = System.currentTimeMillis();
		String ord_no = createOrdNo(OrderEnum.REWARD.getValue(), money);
		int rs = rewardDao.insert(ord_no, send_uuid, rece_uuid, money, content, send_tm);
		if (rs != 1)
			throw new RuntimeException("保存红包数据失败");

		// 生成支付信息给APP端
		SignHelper signHelper = new SignHelper(send_uuid, money, ord_no, "红包", rece_uuid);
		POrderPay orderPay = signHelper.getOrderPay();
		return ReslutUtil.createReslutMessage(orderPay);
	}

	@Override
	public PMessage list(String uuid, String other_uuid, int type, int idx, int count) throws IOException {
		idx = checkIdx(idx);

		List<Reward> list = null;
		if (type == 0)
			list = rewardDao.selectReceByPage(uuid, other_uuid, idx, count);
		else if (type == 1)
			list = rewardDao.selectSendByPage(uuid, other_uuid, idx, count);

		List<PRewardInfo> rewardInfoList = buildRewardList(list);

		PRewardList rewardList = PRewardList.newBuilder().addAllRewardInfo(rewardInfoList).build();
		return ReslutUtil.createReslutMessage(rewardList);
	}

	@Override
	public PMessage info(String ord_no, String uuid, int idx, int count) throws IOException {
		idx = checkIdx(idx);

		PUserBase userBase = null;// 对方信息
		int money = 0;// 红包总金额
		List<Reward> list = null;// 红包记录

		Reward reward = rewardDao.selectByKey(ord_no, 1);
		if (uuid.equals(reward.getSend_uuid())) {// 用户为发送者，需要处理接受者数据
			userBase = UserBaseCache.get(reward.getRece_uuid());
			money = rewardDao.selectrTotalMoony(uuid, reward.getRece_uuid());
			list = rewardDao.selectByPage(uuid, reward.getRece_uuid(), idx, count);
		} else if (uuid.equals(reward.getRece_uuid())) {// 用户为接受者，需要处理发送者数量
			userBase = UserBaseCache.get(reward.getSend_uuid());
			money = rewardDao.selectrTotalMoony(reward.getSend_uuid(), uuid);
			list = rewardDao.selectByPage(reward.getSend_uuid(), uuid, idx, count);
		} else
			throw new BizException(1901010, "没有权限");

		// 红包信息
		PRewardInfo rewardInfo = buildRawardInfo(userBase, reward);
		// 历史红包记录
		List<PRewardInfo> rewardList = buildRewardList(list);
		// 返回数据内容
		PRewardInfoList rewardInfoList = PRewardInfoList.newBuilder().setRewardInfo(rewardInfo).setMoney(money)
				.addAllRewardList(rewardList).build();

		return ReslutUtil.createReslutMessage(rewardInfoList);
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
	 * 构建 RawardInfo对象
	 * 
	 * @param userBase
	 * @param reward
	 * @return
	 */
	private PRewardInfo buildRawardInfo(PUserBase userBase, Reward reward) {
		PRewardInfo rewardInfo = PRewardInfo.newBuilder().setId(reward.getId()).setOrdNo(reward.getOrd_no())
				.setMoney(reward.getMoney()).setContent(reward.getContent()).setTime(reward.getSend_tm())
				.setUserBase(userBase).build();
		return rewardInfo;
	}

	/**
	 * 构建PRewardInfo列表对象
	 * 
	 * @param list
	 * @return
	 * @throws IOException
	 */
	private List<PRewardInfo> buildRewardList(List<Reward> list) throws IOException {
		List<PRewardInfo> rewardList = new ArrayList<PRewardInfo>();
		for (Reward reward : list) {
			PUserBase userBase = UserBaseCache.get(reward.getUuid());
			PRewardInfo rewardInfo = buildRawardInfo(userBase, reward);
			rewardList.add(rewardInfo);
		}
		return rewardList;
	}

	/**
	 * 验证是否符合红包规则
	 * 
	 * @param send_uuid
	 * @param money
	 * @param content
	 * @return
	 */
	private PMessage verification(String send_uuid, int money, String content, String rece_uuid) {
		if (Strings.isNullOrEmpty(rece_uuid))
			return ReslutUtil.createErrorMessage(2102001, "红包没有选择领赏用户");
		if (content.length() > 100)
			return ReslutUtil.createErrorMessage(2102003, "红包描述不能超过100个字符");
		if (money < Constants.min_each_money)
			return ReslutUtil.createErrorMessage(2102004, "红包金额不能少于0.01元");
		if (money > Constants.max_each_money)
			return ReslutUtil.createErrorMessage(2102005, "红包金额最大仅支持200元");

		int has_send_money = jedisDao.findDailySendMoney(send_uuid);
		if (has_send_money + money > Constants.max_daily_money)
			return ReslutUtil.createErrorMessage(2101006, "每天发送总金额（红包、抽奖、红包）不能超过30000元 ");
		return null;
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
		PPayOrderParma payOrderParma = PPayOrderParma.newBuilder().setAmount(money).setServiceNo(type).build();

		RobotGrpcClient client = new RobotGrpcClient();
		PStringSingle ordNoString = client.generateOrder(payOrderParma);
		String ord_no = ordNoString != null ? ordNoString.getVal() : "";
		if (Strings.isNullOrEmpty(ord_no))
			throw new RuntimeException("生成订单号出错");

		return ord_no;
	}

}