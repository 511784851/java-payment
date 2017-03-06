package com.blemobi.payment.service;

import java.io.IOException;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 领红包接口类
 * 
 * @author zhaoyong
 *
 */
public interface RedReceiveService {
	/**
	 * 查询红包针对用户状态
	 * 
	 * @param ord_no
	 * @param rece_uuid
	 * @return
	 */
	public PMessage findRedEnveStatus(String ord_no, String rece_uuid);

	/**
	 * 领红包
	 * 
	 * @param ord_no
	 * @param rece_uuid
	 * @return
	 */
	public PMessage receive(String ord_no, String rece_uuid);

	/**
	 * 查看红包详情
	 * 
	 * @param ord_no
	 * @param rece_uuid
	 * @return
	 */
	public PMessage findRedEnveInfo(String ord_no, String rece_uuid) throws IOException;

	/**
	 * 加载更多领红包用户
	 * 
	 * @param ord_no
	 * @param rece_uuid
	 * @param last_id
	 * @param count
	 * @return
	 */
	public PMessage find(String ord_no, String rece_uuid, int last_id, int count) throws IOException;
}
