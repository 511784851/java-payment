package com.blemobi.payment.service.helper;

import org.apache.ibatis.session.SqlSession;

import com.blemobi.library.util.ReslutUtil;

import com.blemobi.payment.model.Red;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 领取红包
 * 
 * @author zhaoyong
 *
 */
public class ReceiveRedService {
	private String uuid;
	private String custorderno;

	private PMessage message;

	public ReceiveRedService(String uuid, String custorderno) {
		this.uuid = uuid;
		this.custorderno = custorderno;
	}

	/**
	 * 领取一对一红包
	 * 
	 * @return
	 */
	public PMessage receive() {
//		SqlSession session = DBTools.getSession();
//		RedMapper mapper = session.getMapper(RedMapper.class);
//		try {
//			Red red = mapper.selectByPrimaryKey(custorderno);
//
//			if (!check(red))
//				relsut();
//
//			red.setStatus(1);// 红包已领取
//			red.setReceivetime(System.currentTimeMillis());
//
//			// 转账给uuid
//			transfer(red.getAmount());
//
//			session.commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//			session.rollback();
//		} finally {
//			session.close();
//		}

		return null;
	}

	/**
	 * 验证是否符合领取条件
	 * 
	 * @param red
	 * @return
	 */
	private boolean check(Red red) {
		if (red == null) {
			message = ReslutUtil.createErrorMessage(2401016, "红包不存在");
			return false;
		}
		if (!uuid.equals(red.getReceiveuuid())) {
			message = ReslutUtil.createErrorMessage(2401016, "没有权限领取该红包");
			return false;
		}
		if (red.getStatus() != 0) {
			message = ReslutUtil.createErrorMessage(2401016, "红包不可领取");
			return false;
		}
		return true;
	}

	/**
	 * 转账
	 * 
	 * @param red
	 * @return
	 */
	private boolean transfer(int amount) {
		TransferHelper transferHelper = new TransferHelper(uuid, amount);
		return transferHelper.transfer();
	}

	/**
	 * 返回PMessage
	 * 
	 * @return
	 */
	private PMessage relsut() {
		return message;
	}
}
