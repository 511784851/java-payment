package com.blemobi.payment.dao;

/**
 * 随机红包金额操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface RandomDao {
	/**
	 * 查询随机红包金额（根据订单号和随机金额索引值）
	 * 
	 * @param ord_no
	 *            订单号
	 * @param idx
	 *            随机红包索引值
	 */
	public int selectByKey(String ord_no, int sort);

}