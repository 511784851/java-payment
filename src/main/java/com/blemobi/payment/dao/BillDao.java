package com.blemobi.payment.dao;

import java.util.List;
import java.util.Map;

import com.blemobi.payment.model.Bill;

/**
 * 账单数据库操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface BillDao {

	/**
	 * 添加账单信息
	 * 
	 * @param args
	 *            SQL参数
	 * @return
	 */
	public int insert(Object... args);

	/**
	 * 查询收入账单
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param idx
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 */
	public List<Bill> selectIncomeByPage(String uuid, int idx, int count);

	/**
	 * 查询支出账单
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param idx
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 */
	public List<Bill> selectExpendByPage(String uuid, int idx, int count);

	/**
	 * 查询总收入
	 * 
	 * @param uuid
	 *            用户uuid
	 * @return
	 */
	public Map<String, Object> selectTotalIncome(String uuid);

	/**
	 * 查询总支出
	 * 
	 * @param uuid
	 *            用户uuid
	 * @return
	 */
	public Map<String, Object> selectTotalExpend(String uuid);
}