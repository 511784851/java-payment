package com.blemobi.payment.dao;

/**
 * Redis操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface JedisDao {

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

}