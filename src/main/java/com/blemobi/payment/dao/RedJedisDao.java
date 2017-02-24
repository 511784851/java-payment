package com.blemobi.payment.dao;

import java.util.Set;

/**
 * Redis操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface RedJedisDao {
	/**
	 * 存储有权限领红包的用户
	 */
	public int putReceiveUsers(String ord_no, Object... uuids);

	/**
	 * 查询有权限领红包的用户（根据订单号）
	 */
	public Set<String> findUsersByOrdNo(String ord_no);

	/**
	 * 存储随机红包金额
	 */
	public int putRedRandDomMoney(String ord_no, int... moneys);

	/**
	 * 查询随机红包金额（根据订单号和随机金额索引值）
	 */
	public String findRandomMoneyByOrdNoAndIdx(String ord_no, long idx);
}