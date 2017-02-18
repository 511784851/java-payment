package com.blemobi.payment.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.TransactionDao;
import com.blemobi.payment.model.Transaction;

@Repository("transactionDao")
public class TransactionDaoImpl implements TransactionDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int insert(Object... args) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Transaction selectByPrimaryKey(String custorderno) {
		// TODO Auto-generated method stub
		return null;
	}

	public int updateByPrimaryKey(Transaction transaction) {
		// TODO Auto-generated method stub
		return 0;
	}

}