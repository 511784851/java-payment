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
	 *            SQL参数（注意status：1-收入，0-支出）
	 * @return
	 */
	public int insert(Object... args);

	/**
	 * 查询账单
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param status
	 *            1-收入，0-支出
	 * @param idx
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 */
	public List<Bill> selectByPage(String uuid, int status, int idx, int count);

	/**
	 * 查询总账单
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param status
	 *            1-收入，0-支出
	 * @return
	 */
	public Map<String, Object> selectTotalMoney(String uuid, int status);
}