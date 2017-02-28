package com.blemobi.payment.dao;

import java.util.List;

import com.blemobi.payment.model.RedSend;

/**
 * 发红包数据库操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface RedSendDao {
	/**
	 * 初始化发红包数据
	 */
	public int insert(Object... args);

	/**
	 * 根据红包订单号查询红包详情
	 */
	public RedSend selectByKey(String ord_no);

	/**
	 * 领红包时更新数据
	 */
	public int update(String ord_no, int rece_money);

	/**
	 * 批量查询红包发送记录
	 */
	public List<RedSend> selectByPage(String uuid, int id, int size);
	
	/**
	 * @Description 红包支付成功 
	 * @author HUNTER.POON
	 * @param ordNo 订单号
	 * @param amt
	 * @return
	 */
	public int paySucc(String ordNo, int amt);
}