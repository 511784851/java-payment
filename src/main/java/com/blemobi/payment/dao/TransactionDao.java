package com.blemobi.payment.dao;

import com.blemobi.payment.model.Transaction;

/**
 * 交易数据库操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface TransactionDao {
	public int insert(Object... args);

	public Transaction selectByPrimaryKey(String custorderno);

	public int updateByPrimaryKey(Transaction transaction);
}