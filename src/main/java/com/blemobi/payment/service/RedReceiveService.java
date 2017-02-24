package com.blemobi.payment.service;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 领红包接口类
 * 
 * @author zhaoyong
 *
 */
public interface RedReceiveService {
	/**
	 * 领红包
	 */
	public PMessage receive(String ord_no, long rece_uuid);
}
