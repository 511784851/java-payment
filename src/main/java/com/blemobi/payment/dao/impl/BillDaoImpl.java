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
	public int insert(Object... args) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into t_bill (");
		sql.append("uuid, ord_no, money, time, type");
		sql.append(") values (?, ?, ?, ?, ?)");
		return jdbcTemplate.update(sql.toString(), args);
	}

	@Override
	public List<Bill> selectIncomeByPage(String uuid, int idx, int count) {
		return selectByPage(uuid, idx, count, BillTypeEnum.INCOME);
	}

	@Override
	public List<Bill> selectExpendByPage(String uuid, int idx, int count) {
		return selectByPage(uuid, idx, count, BillTypeEnum.EXPEND);
	}

	@Override
	public Map<String, Object> selectTotalIncome(String uuid) {
		return selectTotalMoney(uuid, BillTypeEnum.INCOME);
	}

	@Override
	public Map<String, Object> selectTotalExpend(String uuid) {
		return selectTotalMoney(uuid, BillTypeEnum.EXPEND);
	}

	/**
	 * 查询账单
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param idx
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @param billTypeEnum
	 *            账单类型
	 * @return
	 */
	public List<Bill> selectByPage(String uuid, int idx, int count, BillTypeEnum billTypeEnum) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("id, uuid, ord_no, money, time, type ");
		sql.append("from t_bill ");
		sql.append("where uuid=? and id<? ");
		if (billTypeEnum == BillTypeEnum.INCOME)
			sql.append("and money>0 ");
		else if (billTypeEnum == BillTypeEnum.EXPEND)
			sql.append("and money<0 ");
		sql.append("order by id desc limit ?");

		RowMapper<Bill> rowMapper = new BeanPropertyRowMapper<Bill>(Bill.class);
		return jdbcTemplate.query(sql.toString(), rowMapper, uuid, idx, count);
	}

	/**
	 * 查询总额
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param billTypeEnum
	 *            账单类型
	 * @return
	 */
	public Map<String, Object> selectTotalMoney(String uuid, BillTypeEnum billTypeEnum) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("ifnull(sum(money),0) total, count(money) count ");
		sql.append("from t_bill ");
		sql.append("where uuid=? ");
		if (billTypeEnum == BillTypeEnum.INCOME)
			sql.append("and money>0");
		else if (billTypeEnum == BillTypeEnum.EXPEND)
			sql.append("and money<0");
		return jdbcTemplate.queryForMap(sql.toString(), uuid);
	}
}

/**
 * 账单类型
 * 
 * @author zhaoyong
 *
 */
enum BillTypeEnum {
	/** 收入 */
	INCOME,
	/** 支出 */
	EXPEND
}