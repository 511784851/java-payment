package com.blemobi.payment.service;

import java.io.IOException;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 用户接口类
 * 
 * @author zhaoyong
 *
 */
public interface UserService {

	/**
	 * 网红列表
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param keyword
	 *            关键字
	 * @return
	 * @throws IOException
	 */
	public PMessage celebrity(String uuid, String keyword) throws IOException;
}
