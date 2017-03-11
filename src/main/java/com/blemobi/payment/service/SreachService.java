package com.blemobi.payment.service;

import java.io.IOException;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 搜素接口类
 * 
 * @author zhaoyong
 *
 */
public interface SreachService {

	/**
	 * 搜素发红包和领到的打赏
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param keyword
	 *            昵称关键字
	 * @return
	 * @throws IOException
	 */
	public PMessage list(String uuid, String keyword) throws IOException;

}
