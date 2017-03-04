package com.blemobi.payment.dao;

import java.util.List;

import com.blemobi.payment.model.RedReceive;

/**
 * 领红包数据库操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface RedReceiveDao {

	/**
	 * 保存领红包数据
	 */
	public int insert(Object... args);

	/**
	 * 批量查询领红包数据
	 */
	public List<RedReceive> selectByKey(String ord_no, int last_id, int count);

	/**
	 * 查询领红包数据（根据红包ID和领取人）
	 */
	public RedReceive selectByKey(String ord_no, String rece_uuid);

	/**
	 * 查询已领取用户谁手气最佳，并列时取先领取的用户
	 */
	public String selectMaxMoney(String ord_no);
}