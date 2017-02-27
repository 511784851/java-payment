package com.blemobi.payment.dao;

import com.blemobi.payment.model.Reward;

/**
 * 打赏数据库操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface RewardDao {

	/**
	 * 添加打赏数据
	 */
	public int insert(Object... args);

	/**
	 * 根据订单号查询详情
	 */
	public Reward selectByKey(String ord_no);
	
	/**
     * @Description 红包支付成功 
     * @author HUNTER.POON
     * @param ordNo 订单号
     * @param amt
     * @return
     */
    public int paySucc(String ordNo, int amt);
}