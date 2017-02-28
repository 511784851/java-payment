package com.blemobi.payment.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.BillDao;
import com.blemobi.payment.model.Bill;

/**
 * 打赏数据库操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("billDao")
public class BillDaoImpl implements BillDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 添加打赏数据
	 */
	public int insert(Object... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_bill (");
		sql.append("uuid, ord_no, money, time, type ");
		sql.append(") values (?, ?, ?, ?, ?)");
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 查询账单欣喜
	 */
	public List<Bill> selectByPage(String uuid, int id, int size) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, uuid, ord_no, money, time, type ");
		sql.append("from t_bill ");
		sql.append("where id<? and uuid=? order by id desc limit ?");
		RowMapper<Bill> rowMapper = new BeanPropertyRowMapper<Bill>(Bill.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, id, uuid, size);
	}

}