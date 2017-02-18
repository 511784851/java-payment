package com.blemobi.payment.dao;

import com.blemobi.payment.model.Red;

public interface RedDao {
	public int insert(Object... args);

	public Red selectByKey(String custorderno);

	public int updateByKey(Red record);
}