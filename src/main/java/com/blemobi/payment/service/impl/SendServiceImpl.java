package com.blemobi.payment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.grpc.DataPublishGrpcClient;
import com.blemobi.library.grpc.RobotGrpcClient;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.JedisDao;
import com.blemobi.payment.dao.RandomDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.dao.TableStoreDao;
import com.blemobi.payment.dao.TransactionDao;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.model.RedSend;
import com.blemobi.payment.service.SendService;
import com.blemobi.payment.service.helper.RandomRedHelper;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.payment.util.Constants.TABLE_NAMES;
import com.blemobi.payment.util.DateTimeUtils;
import com.blemobi.payment.util.RongYunWallet;
import com.blemobi.payment.util.rongyun.B2CReq;
import com.blemobi.payment.util.rongyun.B2CResp;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.DataPublishingProtos.PFansFilterParam;
import com.blemobi.sep.probuf.DatapublishingApiProtos.PFansSaveParam;
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
 */
@Log4j
@Service("sendService")
public class SendServiceImpl implements SendService {

	@Autowired
	private RedSendDao redSendDao;
	@Autowired
	private TransactionDao transactionDao;
	@Autowired
	private RandomDao randomDao;

	@Autowired
	private JedisDao jedisDao;

	@Autowired
	private TableStoreDao tableStoreDao;

	@Override
	@Transactional
	public PMessage sendOrdinary(String send_uuid, int money, String content, String rece_uuid) {
		int type = OrderEnum.RED_ORDINARY.getValue();// 红包类型为普通红包
		int number = 1;// 红包数量固定为1
		int each_money = 0;// 单个红包金额固定为0
		int rece_tota_num = 1;// 参与人数固定为1
		if (Strings.isNullOrEmpty(content))
			content = "";
		// 验证红包发送是否符合规则
		PMessage message = verification(send_uuid, money, content, rece_uuid);
		if (message != null)
			return message;

		String ord_no = createOrdNo(type, money);// 订单号
		return savaOrder(ord_no, send_uuid, type, money, each_money, number, content, rece_tota_num, rece_uuid);
	}

	@Override
	@Transactional
	public PMessage sendGroup(String send_uuid, int number, int money, boolean isRandom, String content,
			String tick_uuid, PFansFilterParam fansFilterParam) throws IOException {
		int type = 0;// 红包类型
		int tota_money = 0;// 红包总金额
		int each_money = 0;// 单个红包金额
		if (isRandom) { // 红包类型为随机群红包
			type = OrderEnum.RED_GROUP_RANDOM.getValue();
			each_money = 0;
			tota_money = money;
		} else { // 红包类型为等额群红包
			type = OrderEnum.RED_GROUP_EQUAL.getValue();
			each_money = money;
			tota_money = each_money * number;
		}
		if (Strings.isNullOrEmpty(content))
			content = "";
		// 验证红包发送是否符合规则
		PMessage message = verification(send_uuid, tota_money, number, content);
		if (message != null)
			return message;
		// 生成订单号
		String ord_no = createOrdNo(type, tota_money);
		if (Strings.isNullOrEmpty(ord_no))
			throw new RuntimeException("发红包时，生成订单号出错");
		// 保存参与者
		saveFans(ord_no, tick_uuid, fansFilterParam);
		// 获得参与者概要
		String[] arr = tableStoreDao.selectByKey(TABLE_NAMES.RED_PKG_TB.getValue(), ord_no);
		if (arr == null)
			return ReslutUtil.createErrorMessage(2101001, "红包没有领取用户");
		// 参与人数
		int rece_tota_num = Integer.parseInt(arr[0]);
		// 前五个参与者
		String rece_uuid = arr[1];
		// 如果是随机红包，计算随机金额并保存
		if (type == OrderEnum.RED_GROUP_RANDOM.getValue()) {
			int[] moneyArray = randomMoney(number, tota_money, ord_no);
			randomDao.insert(ord_no, moneyArray);
		}
		return savaOrder(ord_no, send_uuid, type, tota_money, each_money, number, content, rece_tota_num, rece_uuid);
	}

	/**
	 * 保存订单
	 * 
	 * @param ord_no
	 *            订单号
	 * @param send_uuid
	 *            发送者uuid
	 * @param type
	 *            类型
	 * @param money
	 *            总金额
	 * @param each_money
	 *            的那个金额
	 * @param number
	 *            数量
	 * @param content
	 *            描述
	 * @param rece_tota_num
	 *            参与人数
	 * @param rece_uuid
	 *            前5个参与用户uuid
	 * @return
	 */
	private PMessage savaOrder(String ord_no, String send_uuid, int type, int tota_money, int each_money, int number,
			String content, int rece_tota_num, String rece_uuid) {
		long send_tm = System.currentTimeMillis();// 发送时间
		long over_tm = send_tm + Constants.max_interval_Time;// 失效时间
		// 保存订单数据
		int rs = redSendDao.insert(ord_no, content, send_uuid, type, tota_money, each_money, number, send_tm, over_tm,
				rece_tota_num, rece_uuid);
		if (rs != 1)
			throw new RuntimeException("发红包时，保存数据失败");

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
	private int[] randomMoney(int tota_number, int tota_money, String ord_no) {
		RandomRedHelper rdrHelper = new RandomRedHelper(tota_money, tota_number);
		return rdrHelper.distribution();
	}

	/**
	 * 保存参与粉丝
	 * 
	 * @param key
	 * @param filter
	 * @return
	 */
	private boolean saveFans(String key, String tick_uuid, PFansFilterParam filter) {
		PFansSaveParam.Builder fansSaveParamBuilder = PFansSaveParam.newBuilder()
				.setTable(TABLE_NAMES.RED_PKG_TB.getValue()).setKey(key);

		if (Strings.isNullOrEmpty(tick_uuid)) {
			fansSaveParamBuilder.setFilter(filter);
		} else {
			List<String> list = Arrays.asList(tick_uuid.split(","));
			fansSaveParamBuilder.addAllUuid(list);
		}

		DataPublishGrpcClient client = new DataPublishGrpcClient();
		return client.saveFans(fansSaveParamBuilder.build());
	}

	@Override
	public PMessage list(String uuid, int idx, int count) throws IOException {
		idx = checkIdx(idx);
		List<RedSend> list = redSendDao.selectByPage(uuid, idx, count);
		List<PRedEnveBaseInfo> redList = new ArrayList<PRedEnveBaseInfo>();
		for (RedSend redSend : list) {
			PRedEnveBaseInfo redInfo = buildRedEnveBaseInfo(redSend);
			redList.add(redInfo);
		}
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
		PPayOrderParma payOrderParma = PPayOrderParma.newBuilder().setAmount(money).setServiceNo(type).build();
		RobotGrpcClient client = new RobotGrpcClient();
		PStringSingle ordNoString = client.generateOrder(payOrderParma);
		return ordNoString.getVal();
	}

	/**
	 * 验证发普通红包是否合法
	 * 
	 * @param tota_money
	 *            总金额
	 * @param tota_number
	 *            总数量
	 * @return
	 */
	private PMessage verification(String send_uuid, int money, String content, String rece_uuid) {
		if (Strings.isNullOrEmpty(rece_uuid))
			return ReslutUtil.createErrorMessage(2101001, "红包没有领取用户");
		if (content.length() > 100)
			return ReslutUtil.createErrorMessage(2101003, "红包描述不能超过100个字符");
		if (money < Constants.min_each_money)
			return ReslutUtil.createErrorMessage(2101004, "单个红包金额不能少于0.01元");
		if (money > Constants.max_each_money)
			return ReslutUtil.createErrorMessage(2101005, "单个红包金额不能超过200元");
		int has_send_money = jedisDao.findDailySendMoney(send_uuid);
		if (has_send_money + money > Constants.max_daily_money)
			return ReslutUtil.createErrorMessage(2101006, "每天发送总金额（红包、抽奖、打赏）不能超过30000元 ");
		return null;
	}

	/**
	 * 验证发送群红包是否合法
	 * 
	 * @param send_uuid
	 * @param tota_money
	 * @param tota_number
	 * @param content
	 * @return
	 */
	private PMessage verification(String send_uuid, int tota_money, int number, String content) {
		if (number < 1)
			return ReslutUtil.createErrorMessage(2101007, "红包数量不能少于1！");
		if (content.length() > 100)
			return ReslutUtil.createErrorMessage(2101003, "红包描述不能超过100个字符");
		if (tota_money < number * Constants.min_each_money)
			return ReslutUtil.createErrorMessage(2101004, "单个红包金额不能少于0.01元");
		if (tota_money > number * Constants.max_each_money)
			return ReslutUtil.createErrorMessage(2101005, "单个红包金额不能超过200元");
		if (tota_money > Constants.max_tota_money)
			return ReslutUtil.createErrorMessage(2101008, "单次支付总额不可超过10000元");
		int has_send_money = jedisDao.findDailySendMoney(send_uuid);
		if (has_send_money + tota_money > Constants.max_daily_money)
			return ReslutUtil.createErrorMessage(2101006, "每天发送总金额（红包、抽奖、打赏）不能超过30000元 ");
		return null;
	}

	@Override
	public List<RedSend> selectByOver() {
		return redSendDao.selectByOver();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateRef(RedSend rs) {
		int ret = redSendDao.updateRef(rs.getOrd_no());
		if (ret != 1) {
			throw new RuntimeException("insert into transaction table异常");
		}
		// B2C
		String desc = "红包退款";
		B2CReq req = new B2CReq();
		String ordNo = "1" + rs.getOrd_no();
		req.setCustOrderno(ordNo);
		int refAmt = rs.getTota_money() - rs.getRece_money();
		req.setFenAmt(refAmt);
		req.setCustUid(rs.getSend_uuid());
		req.setTransferDesc(desc);
		B2CResp resp = RongYunWallet.b2cTransfer(req);
		if (!Constants.RESPSTS.SUCCESS.getValue().equals(resp.getRespstat())) {
			log.error(resp.toString());
			throw new BizException(2015020, resp.getRespmsg());
		} else {
			log.info(resp.toString());
			long currTm = DateTimeUtils.currTime();
			ret = transactionDao.insert(new Object[] { rs.getSend_uuid(), rs.getOrd_no(), "0", refAmt, 1, " ", desc,
					resp.getJrmfOrderno(), resp.getRespstat(), resp.getRespmsg(), currTm, currTm, ordNo });
			if (ret != 1) {
				throw new RuntimeException("insert into transaction table异常");
			}
			log.info("完成交易流水插入");

		}
	}
}
