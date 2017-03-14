package com.blemobi.payment.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.BillDao;
import com.blemobi.payment.model.Bill;

/**
 * 账单数据库操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("billDao")
public class BillDaoImpl implements BillDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int insert(String uuid, String ord_no, int money, long time, int type, int status, String from_uuid) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_bill (");
		sql.append("uuid, ord_no, money, time, type, status, from_uuid");
		sql.append(") values (?, ?, ?, ?, ?, ?, ?)");
		return jdbcTemplate.update(sql.toString(), uuid, ord_no, money, time, type, status, from_uuid);
	}

	@Override
	public List<Bill> selectByPage(String uuid, int status, int idx, int count) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, uuid, ord_no, money, time, type ");
		sql.append("from t_bill ");
		sql.append("where uuid=? and status=? and id<? order by id desc limit ?");

		RowMapper<Bill> rowMapper = new BeanPropertyRowMapper<Bill>(Bill.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, uuid, status, idx, count);
	}

	@Override
	public Map<String, Object> selectTotalMoney(String uuid, int status) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("ifnull(sum(money),0) total, count(money) count ");
		sql.append("from t_bill ");
		sql.append("where uuid=? and status=?");
		return jdbcTemplate.queryForMap(sql.toString(), uuid, status);
	}
}