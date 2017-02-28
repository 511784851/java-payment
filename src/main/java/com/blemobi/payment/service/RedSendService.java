package com.blemobi.payment.service;

import com.blemobi.sep.probuf.PaymentProtos.PGroupRedEnve;
import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
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
	public PMessage sendOrdinary(POrdinRedEnve ordinRedEnve, String send_uuid);

	/**
	 * 发群红包
	 */
	public PMessage sendGroup(PGroupRedEnve groupRedEnve, String send_uuid);
}
