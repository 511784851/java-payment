package com.blemobi.payment.dao;

import java.util.Set;

/**
 * Redis操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface RedJedisDao {

    public void setUserLotteryRefreshTimes(String uuid);
    /**
     * @Description 获取用户5分钟内刷新次数 
     * @author HUNTER.POON
     * @param uuid
     * @return
     */
    public Integer getUserLotteryRefreshTimes(String uuid);
    
	/**
	 * 存储有权限领红包的用户
	 * 
	 * @param ord_no
	 *            订单号
	 * @param uuids
	 *            用户uuid
	 */
	public int putReceiveUsers(String ord_no, Object... uuids);

	/**
	 * 查询有权限领红包的用户（根据订单号）
	 * 
	 * @param ord_no
	 *            订单号
	 */
	public Set<String> findUsersByOrdNo(String ord_no);

	/**
	 * 存储随机红包金额
	 * 
	 * @param ord_no
	 *            订单号
	 * @param moneys
	 *            随机金额
	 */
	public int putRedRandDomMoney(String ord_no, int... moneys);

	/**
	 * 查询随机红包金额（根据订单号和随机金额索引值）
	 * 
	 * @param ord_no
	 *            订单号
	 * @param idx
	 *            随机红包索引值
	 */
	public String findRandomMoneyByOrdNoAndIdx(String ord_no, long idx);

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