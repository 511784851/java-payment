package com.blemobi.payment.dao;

import com.blemobi.payment.model.Transaction;

public interface TransactionDao {
	public int insert(Object... args);

	public Transaction selectByPrimaryKey(String custorderno);

	public int updateByPrimaryKey(Transaction transaction);
}