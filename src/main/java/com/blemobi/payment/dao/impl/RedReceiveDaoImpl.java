package com.blemobi.payment.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.RedReceiveDao;
import com.blemobi.payment.model.RedReceive;

/**
 * 领红包数据库操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("redReceiveDao")
public class RedReceiveDaoImpl implements RedReceiveDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 保存领红包数据
	 */
	public int insert(Object... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_red_receive (");
		sql.append("ord_no, rece_uuid, money, rece_tm");
		sql.append(") values (?, ?, ?, ?)");
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 批量查询领红包数据（根据红包ID）
	 */
	public List<RedReceive> selectByKey(String ord_no) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, ord_no, rece_uuid, money, rece_tm ");
		sql.append("from t_red_receive ");
		sql.append("where ord_no=?");

		RowMapper<RedReceive> rowMapper = new BeanPropertyRowMapper<RedReceive>(RedReceive.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, ord_no);
	}

	/**
	 * 查询领红包数据（根据红包ID和领取人）
	 */
	public RedReceive selectByKey(String ord_no, long rece_uuid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, ord_no, rece_uuid, money, rece_tm ");
		sql.append("from t_red_receive ");
		sql.append("where ord_no=? and rece_uuid=?");

		RowMapper<RedReceive> rowMapper = new BeanPropertyRowMapper<RedReceive>(RedReceive.class);
		List<RedReceive> list = jdbcTemplate.query(sql.toString(), rowMapper, ord_no, rece_uuid);
		return (list == null || list.size() == 0) ? null : list.get(0);
	}
}