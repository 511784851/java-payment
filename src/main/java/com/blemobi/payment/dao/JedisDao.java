package com.blemobi.payment.dao;

/**
 * Redis操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface JedisDao {

	/**
	 * 存放随机红包金额
	 * 
	 * @param ord_no
	 *            订单号
	 * @param moneys
	 *            随机金额数组
	 * @return
	 */
	public int putRedRandDomMoney(String ord_no, int... moneys);

	/**
	 * 获取随机金额
	 * 
	 * @param ord_no
	 *            订单号
	 * @param idx
	 *            索引
	 * @return
	 */
	public int findRandomMoneyByOrdNoAndIdx(String ord_no, long idx);

	public void setUserLotteryRefreshTimes(String uuid);

	/**
	 * @Description 获取用户5分钟内刷新次数
	 * @author HUNTER.POON
	 * @param uuid
	 * @return
	 */
	public Integer getUserLotteryRefreshTimes(String uuid);

	/**
	 * 累计用户单日发送的金额
	 * 
	 * @param send_uuid
	 *            发送用户uuid
	 * @param money
	 *            发送金额（单位：分）
	 * @return
	 */
	public long incrByDailySendMoney(String send_uuid, int money);

	/**
	 * 查询用户单日发送的金额
	 * 
	 * @param send_uuid
	 *            发送用户uuid
	 * @return 单日已发送总金额（单位：分）
	 */
	public int findDailySendMoney(String send_uuid);

	public void cleanLotteryCD(String uuid);

}