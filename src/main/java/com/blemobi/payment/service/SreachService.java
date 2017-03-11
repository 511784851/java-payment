package com.blemobi.payment.service;

import java.io.IOException;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 发红包接口类
 * 
 * @author zhaoyong
 *
 */
public interface SreachService {

	/**
	 * 发普通红包
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param money
	 *            金额（分）
	 * @param content
	 *            描述
	 * @param rece_uuid
	 *            接受者uuid
	 * @return
	 */
	public PMessage list(String uuid, String keyword) throws IOException;

}
