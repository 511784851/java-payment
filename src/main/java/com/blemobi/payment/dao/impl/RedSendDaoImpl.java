package com.blemobi.payment.dao.impl;

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

	/**
	 * 初始化发红包数据
	 */
	public int insert(Object... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_red_send (");
		sql.append(
				"ord_no, send_uuid, type, tota_money, each_money, tota_number, rece_money, rece_number, content, send_tm, over_tm, pay_status, ref_status");
		sql.append(") values (?, ?, ?, ?, ?, ?, 0, 0, ?, ?, ?, 0, 0)");
		return jdbcTemplate.update(sql.toString(), args);
	}

	/**
	 * 根据红包订单号查询红包详情
	 */
	public RedSend selectByKey(String ord_no) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append(
				"ord_no, send_uuid, type, tota_money, each_money, tota_number, rece_money, rece_number, content, send_tm, over_tm, pay_status, ref_status ");
		sql.append("from t_red_send ");
		sql.append("where ord_no=?");
		RowMapper<RedSend> rowMapper = new BeanPropertyRowMapper<RedSend>(RedSend.class);
		return jdbcTemplate.queryForObject(sql.toString(), rowMapper, ord_no);
	}

	/**
	 * 领红包时更新数据
	 */
	public int update(String ord_no, int rece_money) {
		StringBuffer sql = new StringBuffer();
		sql.append("update t_red_send set ");
		sql.append("rece_money=rece_money+?, rece_number=rece_number+1 ");
		sql.append("where ord_no=? and rece_money+?<=tota_money and rece_number+1<=tota_number");
		return jdbcTemplate.update(sql.toString(), rece_money, ord_no, rece_money);
	}

}