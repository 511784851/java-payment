package com.blemobi.payment.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.RewardDao;
import com.blemobi.payment.model.Reward;

/**
 * 打赏数据库操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("rewarDao")
public class RewardDaoImpl implements RewardDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 添加打赏数据
	 */
	public int insert(Object... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_reward (");
		sql.append("ord_no, send_uuid, rece_uuid, money, content, send_tm, pay_status ");
		sql.append(") values (?, ?, ?, ?, ?, ?, 0)");
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 根据订单号查询详情
	 */
	public Reward selectByKey(String ord_no) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("ord_no, send_uuid, rece_uuid, money, content, send_tm, pay_status ");
		sql.append("from t_reward ");
		sql.append("where ord_no=?");
		RowMapper<Reward> rowMapper = new BeanPropertyRowMapper<Reward>(Reward.class);
		return jdbcTemplate.queryForObject(sql.toString(), rowMapper, ord_no);
	}

}