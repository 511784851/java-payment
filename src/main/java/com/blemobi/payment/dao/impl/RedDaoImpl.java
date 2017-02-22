package com.blemobi.payment.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.RedDao;
import com.blemobi.payment.model.Red;

@Repository("redDao")
public class RedDaoImpl implements RedDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int insert(Object... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into red_send (");
		sql.append("orderno, senduuid, type, amount, content, sendTime, invalidTime, status");
		sql.append(") values (?, ?, ?, ?, ?, ?, ?, ?)");
		return jdbcTemplate.update(sql.toString(), args);
	}

	public Red selectByKey(String custorderno) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("custorderno, sendUUID, receiveUUID, amount, title, sendTime, receiveTime, invalidTime, status ");
		sql.append("from red ");
		sql.append("where custorderno=?");
		RowMapper<Red> rowMapper = new BeanPropertyRowMapper<Red>(Red.class);
		return jdbcTemplate.queryForObject(sql.toString(), rowMapper, custorderno);
	}

	public int updateByKey(Red record) {
		// TODO Auto-generated method stub
		return 0;
	}

}