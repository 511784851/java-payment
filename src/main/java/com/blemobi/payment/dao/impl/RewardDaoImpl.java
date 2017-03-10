package com.blemobi.payment.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.RewardDao;
import com.blemobi.payment.model.Reward;
import com.google.common.base.Strings;

/**
 * 打赏数据库操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("rewardDao")
public class RewardDaoImpl implements RewardDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int insert(Object... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_reward (");
		sql.append("ord_no, send_uuid, rece_uuid, money, content, send_tm, pay_status ");
		sql.append(") values (?, ?, ?, ?, ?, ?, 0)");
		return jdbcTemplate.update(sql.toString(), args);
	}

	@Override
	public Reward selectByKey(String ord_no, int pay_status) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, ord_no, send_uuid, rece_uuid, money, content, send_tm ");
		sql.append("from t_reward ");
		sql.append("where pay_status=? and ord_no=?");
		RowMapper<Reward> rowMapper = new BeanPropertyRowMapper<Reward>(Reward.class);
		return jdbcTemplate.queryForObject(sql.toString(), rowMapper, pay_status, ord_no);
	}

	@Override
	public int selectrTotalMoony(String send_uuid, String rece_uuid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("ifnull(sum(money),0) total ");
		sql.append("from t_reward ");
		sql.append("where pay_status=1 and send_uuid=? and rece_uuid=?");
		return jdbcTemplate.queryForObject(sql.toString(), Integer.class, send_uuid, rece_uuid);
	}

	@Override
	public List<Reward> selectByPage(String send_uuid, String rece_uuid, int idx, int count) {
		StringBuffer sql = selectSQL();
		sql.append("where pay_status=1 and send_uuid=? and rece_uuid=? and id<? order by id desc limit ?");
		RowMapper<Reward> rowMapper = new BeanPropertyRowMapper<Reward>(Reward.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, send_uuid, rece_uuid, idx, count);
	}

	@Override
	public List<Reward> selectReceByPage(String rece_uuid, String other_uuid, int idx, int count) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, ord_no, send_uuid uuid, money, send_tm, content ");
		sql.append("from t_reward ");
		sql.append("where pay_status=1 and rece_uuid=? ");
		if (!Strings.isNullOrEmpty(other_uuid))
			sql.append("and send_uuid='" + other_uuid + "' ");
		sql.append("and id<? order by id desc limit ?");
		RowMapper<Reward> rowMapper = new BeanPropertyRowMapper<Reward>(Reward.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, rece_uuid, idx, count);
	}

	@Override
	public List<Reward> selectSendByPage(String send_uuid, String other_uuid, int idx, int count) {
		StringBuffer sql = selectSQL();
		sql.append("where pay_status=1 and send_uuid=? ");
		if (!Strings.isNullOrEmpty(other_uuid))
			sql.append("and rece_uuid='" + other_uuid + "' ");
		sql.append("and id<? order by id desc limit ?");
		RowMapper<Reward> rowMapper = new BeanPropertyRowMapper<Reward>(Reward.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, send_uuid, idx, count);
	}

	@Override
	public int paySucc(String ordNo) {
		String sql = "UPDATE t_reward SET pay_status = 1 WHERE ord_no = ? AND pay_status = 0";
		Object[] param = new Object[] { ordNo };
		return jdbcTemplate.update(sql, param);
	}

	/**
	 * 查询列表SQL
	 * 
	 * @return
	 */
	private StringBuffer selectSQL() {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, ord_no, rece_uuid uuid, money, send_tm, content ");
		sql.append("from t_reward ");
		return sql;
	}
}