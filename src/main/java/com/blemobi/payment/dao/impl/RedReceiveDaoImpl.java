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
	 * 批量查询领红包数据
	 */
	public List<RedReceive> selectByKey(String ord_no, int last_id, int count) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, ord_no, rece_uuid, money, rece_tm ");
		sql.append("from t_red_receive ");
		sql.append("where ord_no=? and id>? order by id asc limit ?");

		RowMapper<RedReceive> rowMapper = new BeanPropertyRowMapper<RedReceive>(RedReceive.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, ord_no, last_id, count);
	}

	/**
	 * 查询领红包数据
	 */
	public RedReceive selectByKey(String ord_no, String rece_uuid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, ord_no, rece_uuid, money, rece_tm ");
		sql.append("from t_red_receive ");
		sql.append("where ord_no=? and rece_uuid=?");

		RowMapper<RedReceive> rowMapper = new BeanPropertyRowMapper<RedReceive>(RedReceive.class);
		List<RedReceive> list = jdbcTemplate.query(sql.toString(), rowMapper, ord_no, rece_uuid);
		if (list == null || list.size() == 0)
			return null;
		else if (list.size() == 1)
			return list.get(0);
		else
			throw new RuntimeException("根据订单号和领取人查询领取信息超出一条数据行");
	}

	/**
	 * 查询已领取用户谁手气最佳，并列时取先领取的用户
	 */
	public String selectMaxMoney(String ord_no) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("rece_uuid ");
		sql.append("from t_red_receive ");
		sql.append("where ord_no=? order by money desc,id asc limit 1");
		return jdbcTemplate.queryForObject(sql.toString(), String.class, ord_no);
	}
}