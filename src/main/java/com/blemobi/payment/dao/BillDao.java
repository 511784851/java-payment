package com.blemobi.payment.dao;

import java.util.List;

import com.blemobi.payment.model.Bill;

/**
 * 打赏数据库操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface BillDao {

	/**
	 * 添加账单信息
	 */
	public int insert(Object... args);

	/**
	 * 查询账单欣喜
	 */
	public List<Bill> selectByPage(String uuid, int id, int siee);
}