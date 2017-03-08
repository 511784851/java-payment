package com.blemobi.payment.service;

import java.io.IOException;

import com.blemobi.sep.probuf.DataPublishingProtos.PFansFilterParam;
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
	 * 
	 * @param ordinRedEnve
	 * @param send_uuid
	 * @return
	 */
	public PMessage sendOrdinary(String send_uuid, int money, String content, String rece_uuid);

	/**
	 * 发群红包
	 * 
	 * @param groupRedEnve
	 * @param send_uuid
	 * @return
	 */
	public PMessage sendGroup(String send_uuid, int number, int money, boolean isRandom, String content,
			String tick_uuid, PFansFilterParam fansFilterParam);

	/**
	 * 查询红包发送数据
	 * 
	 * @param uuid
	 * @param idx
	 * @param count
	 * @return
	 */
	public PMessage list(String uuid, int idx, int count) throws IOException;
}
