package com.blemobi.payment.service;

import java.io.IOException;
import java.util.List;

import com.blemobi.payment.model.RedSend;
import com.blemobi.sep.probuf.DataPublishingProtos.PFansFilterParam;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 发红包接口类
 * 
 * @author zhaoyong
 *
 */
public interface SendService {

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
	public PMessage sendOrdinary(String send_uuid, int money, String content, String rece_uuid);

	/**
	 * 群红包
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param number
	 *            数量
	 * @param money
	 *            金额（分）
	 * @param isRandom
	 *            是否随机红包
	 * @param content
	 *            描述
	 * @param tick_uuid
	 *            勾选的用户
	 * @param fansFilterParam
	 *            粉丝筛选条件
	 * @return
	 */
	public PMessage sendGroup(String send_uuid, int number, int money, boolean isRandom, String content,
			String tick_uuid, PFansFilterParam fansFilterParam) throws IOException;

	/**
	 * 查询红包发送记录
	 * 
	 * @param send_uuid
	 *            发送者uuid
	 * @param idx
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 * @throws IOException
	 */
	public PMessage list(String send_uuid, int idx, int count) throws IOException;
	
	public List<RedSend> selectByOver();
	
	public void updateRef(RedSend rs);
}
