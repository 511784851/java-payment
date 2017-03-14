package com.blemobi.payment.dao;

import java.util.List;

import com.blemobi.payment.model.RedSend;

/**
 * 发红包数据库操作接口类
 * 
 * @author zhaoyong
 *
 */
public interface RedSendDao {
	/**
	 * 保存发红包数据
	 * 
	 * @param args
	 *            SQL参数
	 * @return
	 */
	public int insert(String ord_no, String send_uuid, int type, int tota_money, int each_money, int number,
			String content, long send_tm, long over_tm, int rece_tota_num, String rece_uuid);

	/**
	 * 查询红包发送详情
	 * 
	 * @param ord_no
	 *            订单号
	 * @param pay_status
	 *            支付状态(0-未支付，1-已支付，2-支付异常)
	 * @return
	 */
	public RedSend selectByKey(String ord_no, int pay_status);

	/**
	 * 领红包时更新数据
	 * 
	 * @param ord_no
	 *            订单号
	 * @param rece_money
	 *            领取金额
	 * @return
	 */
	public int update(String ord_no, int rece_money);

	/**
	 * 查询红包发送记录
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param idx
	 *            分页起始值
	 * @param count
	 *            分页大小
	 * @return
	 */
	public List<RedSend> selectByPage(String uuid, int idx, int count);

	/**
	 * @Description 红包支付成功
	 * @author HUNTER.POON
	 * @param ordNo
	 *            订单号
	 * @return
	 */
	public int paySucc(String ordNo);

	/**
	 * 查询符合退款条件的红包订单
	 * 
	 * @return
	 */
	public List<RedSend> selectByOver();

	/**
	 * 退款成功时修改状态
	 * 
	 * @param ord_no
	 * @return
	 */
	public int updateRef(String ord_no);
}