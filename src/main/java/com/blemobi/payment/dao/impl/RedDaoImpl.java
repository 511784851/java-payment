package com.blemobi.payment.dao.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.blemobi.library.redis.RedisManager;
import com.blemobi.payment.dao.RedDao;
import com.blemobi.payment.model.RedSend;

import redis.clients.jedis.Jedis;

@Repository("redDao")
public class RedDaoImpl implements RedDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int insert(Object... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_red_send (");
		sql.append(
				"ord_no, send_uuid, red_type, tot_amount, any_amount, num, content, send_tm, invalid_tm, rec_status, pay_status");
		sql.append(") values (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0)");
		return jdbcTemplate.update(sql.toString(), args);
	}

	public RedSend selectByKey(String ord_no) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append(
				"send_uuid, red_type, tot_amount, any_amount, num, content, send_tm, invalid_tm, rec_status, pay_status ");
		sql.append("from t_red_send ");
		sql.append("where ord_no=?");
		RowMapper<RedSend> rowMapper = new BeanPropertyRowMapper<RedSend>(RedSend.class);
		return jdbcTemplate.queryForObject(sql.toString(), rowMapper, ord_no);
	}

	private final String REC_KEY = "payment:rec:";

	public int saveRecUUIDS(String ord_no, String... uuids) {
		String key = REC_KEY + ord_no;
		Jedis jedis = RedisManager.getRedis();
		for (String member : uuids)
			jedis.zadd(key, 0, member);
		RedisManager.returnResource(jedis);
		return 0;
	}

	public Set<String> findByOrdNo(String ord_no) {
		String key = REC_KEY + ord_no;
		Jedis jedis = RedisManager.getRedis();
		return jedis.zrange(key, 0, -1);
	}

}