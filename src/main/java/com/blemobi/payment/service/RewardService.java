package com.blemobi.payment.service;

import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
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
	public PMessage reward(POrdinRedEnve ordinRedEnve, String send_uuid);
}
