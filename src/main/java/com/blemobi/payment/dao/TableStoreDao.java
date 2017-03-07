package com.blemobi.payment.dao;

/**
 * 阿里云表格存储操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface TableStoreDao {

	/**
	 * 查找成员是否存在
	 * 
	 * @param key
	 *            存储的KEY
	 * @param member
	 *            成员uuid
	 * @return
	 */
	public boolean existsByKey(String key, String member);
}