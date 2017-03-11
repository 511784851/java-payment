package com.blemobi.payment.service;

import java.io.IOException;

import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 打赏业务接口类
 * 
 * @author zhaoyong
 *
 */
public interface RewardService {

	/**
	 * 打赏
	 * 
	 * @param ordinRedEnve
	 *            打赏信息
	 * @param send_uuid
	 *            发送者uuid
	 * @return
	 */
	public PMessage reward(String send_uuid, int money, String content, String rece_uuid);

	/**
	 * 查询打赏列表
	 * 
	 * @param uuid
	 *            发送者或接受者uuid
	 * @param type
	 *            0-领赏 1-打赏
	 * @param idx
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 */
	public PMessage list(String uuid, String other_uuid, int type, int idx, int count) throws IOException;

	/**
	 * 查看打赏详情以及打赏记录
	 * 
	 * @param ord_no
	 *            业务订单号
	 * @param uuid
	 *            用户uuid
	 * @param idx
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 */
	public PMessage info(String ord_no, String uuid, int idx, int count) throws IOException;
}
