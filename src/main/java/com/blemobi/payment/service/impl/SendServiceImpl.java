package com.blemobi.payment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.Row;
import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.grpc.DataPublishGrpcClient;
import com.blemobi.library.grpc.RobotGrpcClient;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.JedisDao;
import com.blemobi.payment.dao.RandomDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.dao.TableStoreDao;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.model.RedSend;
import com.blemobi.payment.service.SendService;
import com.blemobi.payment.service.helper.RandomRedHelper;
import com.blemobi.payment.service.helper.SignHelper;
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.payment.util.Constants.TABLE_NAMES;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.DataPublishingApiProtos.PFansSaveParam;
import com.blemobi.sep.probuf.DataPublishingProtos.PFansFilterParam;
import com.blemobi.sep.probuf.PaymentProtos.POrderPay;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveBaseInfo;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveList;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PStringSingle;
import com.blemobi.sep.probuf.RobotApiProtos.PPayOrderParma;
import com.google.common.base.Strings;

/**
 * 发红包业务实现类
 * 
 * @author zhaoyong
 *
 */
@Service("sendService")
public class SendServiceImpl implements SendService {

	@Autowired
	private RedSendDao redSendDao;

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
			String tick_uuid, PFansFilterParam fansFilterParam) {
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
		// 验证红包发送是否符合规则
		PMessage message = verification(send_uuid, tota_money, number, content);
		if (message != null)
			return message;
		// 生成订单号
		String ord_no = createOrdNo(type, tota_money);
		// 保存参与者
		boolean bool = saveFans(ord_no, tick_uuid, fansFilterParam);
		if (!bool)
			throw new RuntimeException("发群红包时，保存参与者失败");
		// 获得参与者概要
		//String[] arr = getReceUserData(ord_no);
		// 参与人数
		int rece_tota_num = 1;//Integer.parseInt(arr[0]);
		// 前五个参与者
		String rece_uuid = "";//arr[1];
		// 如果是随机群红包，计算随机金额并保存
		if (type == OrderEnum.RED_GROUP_RANDOM.getValue()) {
			int[] moneyArray = randomMoney(number, tota_money, ord_no);
			randomDao.insert(ord_no, moneyArray);
		}
		return savaOrder(ord_no, send_uuid, type, money, each_money, number, content, rece_tota_num, rece_uuid);
	}

	/**
	 * 保存订单
	 * 
	 * @param ord_no
	 * @param send_uuid
	 * @param type
	 * @param money
	 * @param each_money
	 * @param number
	 * @param content
	 * @param rece_tota_num
	 * @param rece_uuid
	 * @return
	 */
	private PMessage savaOrder(String ord_no, String send_uuid, int type, int money, int each_money, int number,
			String content, int rece_tota_num, String rece_uuid) {
		long send_tm = System.currentTimeMillis();// 发送时间
		long over_tm = send_tm + Constants.max_interval_Time;// 失效时间
		// 保存订单数据
		int rs = redSendDao.insert(ord_no, send_uuid, type, money, each_money, number, content, send_tm, over_tm,
				rece_tota_num, rece_uuid);
		if (rs != 1)
			throw new RuntimeException("发红包时，保存数据失败");

		// 生成支付信息给APP端
		SignHelper signHelper = new SignHelper(send_uuid, money, ord_no, "红包");
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
			List<String> list = new ArrayList<String>();
			for (String uuid : tick_uuid.split(","))
				list.add(uuid);
			fansSaveParamBuilder.addAllUuid(list);
		}

		DataPublishGrpcClient client = new DataPublishGrpcClient();
		return client.saveFans(fansSaveParamBuilder.build());
	}

	/**
	 * 获得群红包总参与人数以及前五个参与者uuid
	 * 
	 * @param key
	 * @return
	 */
	private String[] getReceUserData(String key) {
		StringBuffer sb = new StringBuffer();
		Row row = tableStoreDao.selectByKey(TABLE_NAMES.RED_PKG_TB.getValue(), key);
		if (row == null || row.getColumns().length == 0)
			throw new BizException(2101010, "没有参与用户");

		String[] arr = new String[2];
		Column[] columns = row.getColumns();
		int len = columns.length;
		arr[0] = len + "";
		if (len > 5)
			len = 5;
		for (int i = 0; i < len; i++) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(columns[i].getName());
		}
		arr[1] = sb.toString();
		return arr;
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
		String ord_no = ordNoString != null ? ordNoString.getVal() : "";
		if (Strings.isNullOrEmpty(ord_no))
			throw new RuntimeException("发红包时，生成订单号出错");

		return ord_no;
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
			return ReslutUtil.createErrorMessage(2101001, "红包没有领取人");
		if (Strings.isNullOrEmpty(content))
			return ReslutUtil.createErrorMessage(2101002, "红包描述不能为空");
		if (content.length() > 100)
			return ReslutUtil.createErrorMessage(2101003, "红包描述不能超过100个字符");
		if (money < Constants.min_each_money)
			return ReslutUtil.createErrorMessage(2101004, "单个红包金额不能少于0.01元");
		if (money > Constants.max_each_money)
			return ReslutUtil.createErrorMessage(2101005, "单个红包金额不能超过200元");
		int has_send_money = jedisDao.findDailySendMoney(send_uuid);
		if (has_send_money + money > Constants.max_daily_money)
			return ReslutUtil.createErrorMessage(2101007, "每天发送总金额（红包、抽奖、打赏）不能超过30000元 ");
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
		//if (tota_number < 1)
			//return ReslutUtil.createErrorMessage(2101001, "红包个数不能少于1！");
		if (Strings.isNullOrEmpty(content))
			return ReslutUtil.createErrorMessage(2101002, "红包描述不能为空");
		if (content.length() > 100)
			return ReslutUtil.createErrorMessage(2101003, "红包描述不能超过100个字符");
		if (number < 1)
			return ReslutUtil.createErrorMessage(2101004, "红包数量不能小于1");
		if (tota_money < number * Constants.min_each_money)
			return ReslutUtil.createErrorMessage(2101004, "单个红包金额不能少于0.01元");
		if (tota_money > number * Constants.max_each_money)
			return ReslutUtil.createErrorMessage(2101005, "单个红包金额不能超过200元");
		if (tota_money > Constants.max_tota_money)
			return ReslutUtil.createErrorMessage(2101006, "单次支付总额不可超过10000元");
		int has_send_money = jedisDao.findDailySendMoney(send_uuid);
		if (has_send_money + tota_money > Constants.max_daily_money)
			return ReslutUtil.createErrorMessage(2101007, "每天发送总金额（红包、抽奖、打赏）不能超过30000元 ");
		return null;
	}
}
