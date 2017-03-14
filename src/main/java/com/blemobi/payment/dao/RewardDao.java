package com.blemobi.payment.dao;

import java.util.List;

import com.blemobi.payment.model.Reward;

/**
 * 打赏数据库操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface RewardDao {

	/**
	 * 添加打赏数据
	 * 
	 * @param args
	 *            SQL参数
	 * @return
	 */
	public int insert(String ord_no, String send_uuid, String rece_uuid, int money, String content, long send_tm);

	/**
	 * 根据订单号查询详情
	 * 
	 * @param ord_no
	 *            订单号
	 * @param pay_status
	 *            支付状态(0-未支付，1-已支付，2-支付异常)
	 * @return
	 */
	public Reward selectByKey(String ord_no, int pay_status);

	/**
	 * 查询打赏总金额（根据发送者和接受者）
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param rece_uuid
	 *            接受者uuid
	 * @return
	 */
	public int selectrTotalMoony(String send_uuid, String rece_uuid);

	/**
	 * 查询打赏列表（根据发送者和接受者）
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param rece_uuid
	 *            接受者uuid
	 * @param idx
	 * @param count
	 * @return
	 */
	public List<Reward> selectByPage(String send_uuid, String rece_uuid, int idx, int count);

	/**
	 * 查询收到的打赏数据（根据接受者）
	 * 
	 * @param rece_uuid
	 *            接受者uuid
	 * @param idx
	 * @param count
	 * @return
	 */
	public List<Reward> selectReceByPage(String rece_uuid, String other_uuid, int idx, int count);

	/**
	 * 查询发送的打赏数据（根据发送者）
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param idx
	 * @param count
	 * @return
	 */
	public List<Reward> selectSendByPage(String send_uuid, String other_uuid, int idx, int count);

	/**
	 * @Description 红包支付成功
	 * @author HUNTER.POON
	 * @param ordNo
	 *            订单号
	 * @param amt
	 * @return
	 */
	public int paySucc(String ordNo);
}