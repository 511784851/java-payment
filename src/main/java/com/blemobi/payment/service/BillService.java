package com.blemobi.payment.service;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 账单接口类
 * 
 * @author zhaoyong
 *
 */
public interface BillService {

	/**
	 * 查询账单
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param type
	 *            类型（0-收入 1-支出）
	 * @param last_id
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 */
	public PMessage list(String uuid, int type, int last_id, int count);
}
