package com.blemobi.payment.service;

import java.io.IOException;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 领红包接口类
 * 
 * @author zhaoyong
 *
 */
public interface ReceiveService {
	/**
	 * 查询红包针对用户状态
	 * 
	 * @param rece_uuid
	 *            用户uuid
	 * @param ord_no
	 *            订单号
	 * @return
	 */
	public PMessage checkStatus(String ord_no, String rece_uuid);

	/**
	 * 领红包
	 * 
	 * @param rece_uuid
	 *            领取用户uuid
	 * 
	 * @param ord_no
	 *            业务订单号
	 * @return
	 */
	public PMessage receive(String ord_no, String rece_uuid);

	/**
	 * 查看红包详情
	 * 
	 * @param rece_uuid
	 *            领取用户uuid
	 * 
	 * @param ord_no
	 *            业务订单号
	 * @return
	 */
	public PMessage findInfo(String ord_no, String rece_uuid) throws IOException;

	/**
	 * 查询领红包用户
	 * 
	 * @param rece_uuid
	 *            领红包用户
	 * @param ord_no
	 *            业务订单号
	 * @param last_id
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 */
	public PMessage findList(String ord_no, String rece_uuid, int last_id, int count) throws IOException;
}
