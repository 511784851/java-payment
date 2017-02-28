package com.blemobi.payment.service;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 发红包接口类
 * 
 * @author zhaoyong
 *
 */
public interface RedFindService {

	public PMessage history(String uuid, int id, int size);

}
