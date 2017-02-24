package com.blemobi.payment.service;

import com.blemobi.sep.probuf.PaymentProtos.POrdinaryRed;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * *打赏业务接口类
 * 
 * @author zhaoyong
 *
 */
public interface RewardService {
	/**
	 * 打赏
	 */
	public PMessage reward(POrdinaryRed ordinaryRed, long send_uuid);
}
