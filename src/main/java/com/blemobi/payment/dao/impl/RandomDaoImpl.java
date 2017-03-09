package com.blemobi.payment.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.RandomDao;

/**
 * 随机红包金额数据库操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("randomDao")
public class RandomDaoImpl implements RandomDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int[] insert(String ord_no, int[] moneyArray) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_red_random (");
		sql.append("ord_no, money, sort");
		sql.append(") values (?, ?, ?)");

		List<Object[]> batchArgs = new ArrayList<Object[]>();
		int i = 0;
		for (int money : moneyArray)
			batchArgs.add(new Object[] { ord_no, money, i++ });

		return jdbcTemplate.batchUpdate(sql.toString(), batchArgs);
	}

	@Override
	public int selectByKey(String ord_no, int sort) {
		StringBuffer sql = new StringBuffer();
		sql.append("select money from t_red_random ");
		sql.append("where ord_no=? and sort=?");
		return jdbcTemplate.queryForObject(sql.toString(), Integer.class, ord_no, sort);
	}
}