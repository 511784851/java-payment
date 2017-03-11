package com.blemobi.payment.dao;

import java.io.IOException;

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
	 *            表名称
	 * @param key
	 *            存储的KEY
	 * @param member
	 *            成员uuid
	 * @return
	 */
	public boolean existsByKey(String tn, String key, String uuid) throws IOException;

	/**
	 * 查找行
	 * 
	 * @param key
	 *            表名称
	 * @param key
	 *            存储的KEY
	 * @return
	 */
	public String[] selectByKey(String tn, String key) throws IOException;
}