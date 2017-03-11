package com.blemobi.payment.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.RedSendDao;
import com.blemobi.payment.model.RedSend;

/**
 * 发红包数据库操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("redSendDao")
public class RedSendDaoImpl implements RedSendDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int insert(Object... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_red_send (");
		sql.append(
				"ord_no, send_uuid, type, tota_money, each_money, tota_number, rece_money, rece_number, content, send_tm, over_tm, rece_tota_num, rece_uuid5, pay_status, ref_status");
		sql.append(") values (?, ?, ?, ?, ?, ?, 0, 0, ?, ?, ?, ?, ?,0, 0)");
		return jdbcTemplate.update(sql.toString(), args);
	}

	@Override
	public RedSend selectByKey(String ord_no, int pay_status) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append(
				"ord_no, send_uuid, type, tota_money, each_money, tota_number, rece_money, rece_number, content, send_tm, over_tm, rece_uuid5, pay_status, ref_status ");
		sql.append("from t_red_send ");
		sql.append("where pay_status=? and ord_no=?");
		RowMapper<RedSend> rowMapper = new BeanPropertyRowMapper<RedSend>(RedSend.class);
		return jdbcTemplate.queryForObject(sql.toString(), rowMapper, pay_status, ord_no);
	}

	@Override
	public int update(String ord_no, int rece_money) {
		StringBuffer sql = new StringBuffer();
		sql.append("update t_red_send set ");
		sql.append("rece_money=rece_money+?, rece_number=rece_number+1 ");
		sql.append("where pay_status=1 and ord_no=? and rece_money+?<=tota_money and rece_number+1<=tota_number");
		return jdbcTemplate.update(sql.toString(), rece_money, ord_no, rece_money);
	}

	@Override
	public List<RedSend> selectByPage(String uuid, int idx, int count) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, ord_no, type, content, send_tm, rece_tota_num, rece_uuid5 ");
		sql.append("from t_red_send ");
		sql.append("where pay_status=1 and id<? and send_uuid=? order by id desc limit ?");
		RowMapper<RedSend> rowMapper = new BeanPropertyRowMapper<RedSend>(RedSend.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, idx, uuid, count);
	}

	public List<RedSend> selectByOver() {
		long time = System.currentTimeMillis();
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, ord_no, type, rece_money, tota_money, send_uuid ");
		sql.append("from t_red_send ");
		sql.append("WHERE pay_status=1 AND ref_status=0 AND over_tm<? AND rece_money<tota_money");
		RowMapper<RedSend> rowMapper = new BeanPropertyRowMapper<RedSend>(RedSend.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, time);
	}

	public int updateRef(String ord_no) {
		StringBuffer sql = new StringBuffer();
		sql.append("update t_red_send set ");
		sql.append("ref_status=1 ");
		sql.append("where ord_no=? and pay_status=1 and ref_status=0 AND over_tm<? AND rece_money<tota_money");
		return jdbcTemplate.update(sql.toString(), ord_no);
	}

	@Override
	public int paySucc(String ordNo) {
		String sql = "UPDATE t_red_send SET pay_status = 1 WHERE ord_no = ? AND pay_status = 0";
		return jdbcTemplate.update(sql, ordNo);
	}

}