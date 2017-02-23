package com.blemobi.payment.dao;

import java.util.Set;

import com.blemobi.payment.model.RedSend;

public interface RedDao {
	public int insert(Object... args);

	public RedSend selectByKey(String custorderno);

	public int saveRecUUIDS(String ord_no, String... uuids);

	public Set<String> findByOrdNo(String ord_no);
}