package com.blemobi.payment.service;

import com.blemobi.sep.probuf.PaymentProtos.PGroupRed;
import com.blemobi.sep.probuf.PaymentProtos.POrdinaryRed;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 发红包接口类
 * 
 * @author zhaoyong
 *
 */
public interface RedSendService {

	/**
	 * 发普通红包
	 */
	public PMessage sendOrdinary(POrdinaryRed ordinaryRed, long send_uuid);

	/**
	 * 发群红包
	 */
	public PMessage sendGroup(PGroupRed groupRed, long send_uuid);
}
