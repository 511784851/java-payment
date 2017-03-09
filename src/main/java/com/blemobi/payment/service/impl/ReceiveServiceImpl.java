package com.blemobi.payment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.redis.LockManager;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.BillDao;
import com.blemobi.payment.dao.RandomDao;
import com.blemobi.payment.dao.RedReceiveDao;
import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.dao.TableStoreDao;
import com.blemobi.payment.excepiton.BizException;
import com.blemobi.payment.model.RedReceive;
import com.blemobi.payment.model.RedSend;
import com.blemobi.payment.service.ReceiveService;
import com.blemobi.payment.service.helper.TransferHelper;
import com.blemobi.payment.util.Constants;
import com.blemobi.payment.util.Constants.OrderEnum;
import com.blemobi.payment.util.Constants.TABLE_NAMES;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveInfo;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveRece;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveReceList;
import com.blemobi.sep.probuf.PaymentProtos.PRedEnveStatus;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

import lombok.extern.log4j.Log4j;

/**
 * 领红包实现类
 * 
 * @author zhaoyong
 *
 */
@Log4j
@Service("receiveService")
public class ReceiveServiceImpl implements ReceiveService {

	/** 领红包安全锁的KEY */
	private static final String LOCK_KEY = "payment:lock:receive:";

	@Autowired
	private RedSendDao redSendDao;

	@Autowired
	private RedReceiveDao redReceiveDao;

	@Autowired
	private RandomDao randomDao;

	@Autowired
	private BillDao billDao;

	@Autowired
	private TableStoreDao tableStoreDao;

	@Override
	public PMessage checkStatus(String ord_no, String rece_uuid) {
		PRedEnveStatus redEnveStatus = check(ord_no, rece_uuid);
		return ReslutUtil.createReslutMessage(redEnveStatus);
	}

	/**
	 * 验证红包针对用户状态
	 * 
	 * @param redSend
	 * @param ord_no
	 * @param rece_uuid
	 * @return
	 */
	private PRedEnveStatus check(String ord_no, String rece_uuid) {
		RedSend redSend = redSendDao.selectByKey(ord_no);
		PRedEnveStatus redEnveStatus = checkStatus(redSend, ord_no, rece_uuid);
		if (redEnveStatus.getStatus() == -1) {
			int status = checkStatus(redSend);
			redEnveStatus = redEnveStatus.toBuilder().setStatus(status).build();
		}
		return redEnveStatus;
	}

	/**
	 * 验证用户是否已领红包以及有权限领红包
	 * 
	 * @param redSend
	 * @param ord_no
	 * @param rece_uuid
	 * @return
	 */
	private PRedEnveStatus checkStatus(RedSend redSend, String ord_no, String rece_uuid) {
		if (redSend == null)
			throw new RuntimeException("没有找到有效的红包信息:" + ord_no);
		if (redSend.getSend_uuid().equals(rece_uuid))
			throw new RuntimeException("发送者自己无法查看红包状态");

		int status = -1; // 0-可领取，1-已领取，2-已过期，3-已领完
		int rece_money = 0; // 已领取金额
		RedReceive receive = redReceiveDao.selectByKey(ord_no, rece_uuid);
		if (receive != null) {
			status = 1;// 已领取
			rece_money = receive.getMoney();
		} else {
			if (redSend.getType() == OrderEnum.RED_ORDINARY.getValue()) {
				if (!rece_uuid.equals(redSend.getRece_uuid5()))
					throw new BizException(2102000, "没有权限");
			} else {
				boolean bool = tableStoreDao.existsByKey(TABLE_NAMES.RED_PKG_TB.getValue(), ord_no, rece_uuid);
				if (!bool)
					throw new BizException(2102000, "没有权限");
			}
		}
		return PRedEnveStatus.newBuilder().setStatus(status).setReceMoney(rece_money).setContent(redSend.getContent())
				.build();
	}

	/**
	 * 验证红包是否已领完或已过期
	 * 
	 * @param redSend
	 * @return
	 */
	private int checkStatus(RedSend redSend) {
		int status = -1; // 0-可领取，1-已领取，2-已过期，3-已领完
		if (redSend.getRece_number() >= redSend.getTota_number() || redSend.getRece_money() >= redSend.getTota_money())
			status = 3;// 已领完
		else if (redSend.getOver_tm() < System.currentTimeMillis())
			status = 2;// 已过期
		else
			status = 0;// 可领取
		return status;
	}

	@Override
	@Transactional
	public PMessage receive(String ord_no, String rece_uuid) {
		RedSend redSend = redSendDao.selectByKey(ord_no);
		PRedEnveStatus redEnveStatus = checkStatus(redSend, ord_no, rece_uuid);
		int status = redEnveStatus.getStatus();
		if (status == -1) {
			String lock = LOCK_KEY + ord_no;
			boolean bool = LockManager.getLock(lock, 120);
			if (!bool)
				throw new RuntimeException("领红包时获得同步锁出错");
			try {
				status = checkStatus(redSend);
				if (status == 0) {// 可领取
					long now_tm = System.currentTimeMillis();
					int rece_money = 0; // 已领取金额
					int type = redSend.getType();
					if (type == OrderEnum.RED_ORDINARY.getValue()) {// 普通红包
						rece_money = redSend.getTota_money();
					} else if (type == OrderEnum.RED_GROUP_EQUAL.getValue()) {// 等额群红包
						rece_money = redSend.getEach_money();
					} else if (type == OrderEnum.RED_GROUP_RANDOM.getValue()) {// 随机群红包
						rece_money = randomDao.selectByKey(ord_no, redSend.getRece_number());
					}
					if (rece_money < Constants.min_each_money)
						throw new RuntimeException("领取红包时获取金额出错");
					receiveing(ord_no, rece_uuid, rece_money, now_tm, type);

					redEnveStatus = redEnveStatus.toBuilder().setStatus(1).setReceMoney(rece_money).build();
				}
			} finally {
				LockManager.releaseLock(lock);
			}
		}
		return ReslutUtil.createReslutMessage(redEnveStatus);
	}

	@Override
	public PMessage findInfo(String ord_no, String rece_uuid) throws IOException {
		long now_tm = System.currentTimeMillis();
		RedSend redSend = redSendDao.selectByKey(ord_no);
		boolean status = now_tm >= redSend.getOver_tm();// 红包是否已过期
		int user_rece_money = 0;// 当前用户领取的金额（单位：分），如果是0表示网红自己查看详情
		if (!redSend.getSend_uuid().equals(rece_uuid)) {
			RedReceive receive = redReceiveDao.selectByKey(ord_no, rece_uuid);
			if (receive == null)
				throw new RuntimeException("没有权限领取红包: " + ord_no);
			user_rece_money = receive.getMoney();// 已领取金额
		}

		PRedEnveInfo redInfo = buildRedInfo(redSend, ord_no, user_rece_money, status);
		return ReslutUtil.createReslutMessage(redInfo);
	}

	@Override
	public PMessage findList(String ord_no, String rece_uuid, int last_id, int count) throws IOException {
		RedSend redSend = redSendDao.selectByKey(ord_no);
		List<PRedEnveRece> list = getReceUser(redSend, ord_no, last_id, count);
		PRedEnveReceList redEnveReceList = PRedEnveReceList.newBuilder().addAllRedEnveRece(list).build();
		return ReslutUtil.createReslutMessage(redEnveReceList);
	}

	/**
	 * 构建红包详情数据
	 * 
	 * @param redSend
	 * @param type
	 * @param p_receive_list
	 * @return
	 * @throws IOException
	 */
	private PRedEnveInfo buildRedInfo(RedSend redSend, String ord_no, int user_rece_money, boolean status)
			throws IOException {
		List<PRedEnveRece> list = getReceUser(redSend, ord_no, 0, 10);
		PUserBase userBase = UserBaseCache.get(redSend.getSend_uuid());
		PRedEnveInfo redInfo = PRedEnveInfo.newBuilder().setOrdNo(redSend.getOrd_no()).setUserReceMoney(user_rece_money)
				.setType(redSend.getType()).setTotaMoney(redSend.getTota_money())
				.setTotaNumber(redSend.getTota_number()).setReceMoney(redSend.getRece_money())
				.setReceNumber(redSend.getRece_number()).setContent(redSend.getContent())
				.setSendTm(redSend.getSend_tm()).setUserBase(userBase).setStatus(status).addAllRedEnveRece(list)
				.build();
		return redInfo;
	}

	/**
	 * 批量查询红包已领取用户信息
	 * 
	 * @param ord_no
	 *            业务订单号
	 * @param last_id
	 *            上一次查询最后数据的ID
	 * @param count
	 *            查询的数据量
	 * @return
	 */
	/**
	 * @param redSend
	 * @param ord_no
	 * @param last_id
	 * @param count
	 * @return
	 * @throws IOException
	 */
	private List<PRedEnveRece> getReceUser(RedSend redSend, String ord_no, int last_id, int count) throws IOException {
		String luck_uuid = "";
		if (redSend.getType() == OrderEnum.RED_GROUP_RANDOM.getValue()
				&& (redSend.getRece_number() == redSend.getTota_money()
						|| redSend.getOver_tm() > System.currentTimeMillis())) {
			luck_uuid = redReceiveDao.selectMaxMoney(ord_no);
		}
		List<RedReceive> receiveList = redReceiveDao.selectByKey(ord_no, last_id, count);
		List<PRedEnveRece> list = new ArrayList<PRedEnveRece>();
		for (RedReceive redReceive : receiveList) {
			int luck_level = luck_uuid.equals(redReceive.getRece_uuid()) ? 1 : 0;//// 幸运级别（0-一般，1-手气最佳，2-手气最差）
			PUserBase userBase = UserBaseCache.get(redReceive.getRece_uuid());
			PRedEnveRece p_receive = PRedEnveRece.newBuilder().setId(redReceive.getId()).setMoney(redReceive.getMoney())
					.setReceTm(redReceive.getRece_tm()).setLuckLevel(luck_level).setUserBase(userBase).build();
			list.add(p_receive);
		}
		return list;
	}

	/**
	 * 领红包并转账
	 * 
	 * @param ord_no
	 *            业务订单号
	 * @param rece_uuid
	 *            领取人uuid
	 * @param each_money
	 *            领取金额（单位：分）
	 * @param rece_tm
	 *            领取时间
	 */
	private void receiveing(String ord_no, String rece_uuid, int rece_money, long rece_tm, int type) {
		// 保存领取记录
		redReceiveDao.insert(ord_no, rece_uuid, rece_money, rece_tm);
		// 更新红包已领取金额和数量
		redSendDao.update(ord_no, rece_money);
		// 保存流水
		billDao.insert(rece_uuid, ord_no, rece_money, rece_tm, type);
		// 转账给用户
		TransferHelper th = new TransferHelper(rece_uuid, rece_money);
		boolean th_bool = th.execute();
		if (!th_bool)
			throw new RuntimeException("领取红包时转账出错");
	}

}
