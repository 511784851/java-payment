package com.blemobi.payment.sql;

import java.util.UUID;

import com.blemobi.payment.dbcp.JdbcTemplate;

import lombok.extern.log4j.Log4j;

/*
 * 常用支付数据存储类
 */
@Log4j
public class SqlHelper {

	/**
	 * 保存预支付订单信息
	 * 
	 * @param uuid
	 *            用户UUID
	 * @param pay_type
	 *            支付渠道（WX-微信,ZFB-支付宝）
	 * @param orderSubject
	 *            商品名称
	 * @param orderBody
	 *            商品描述
	 * @param order_no
	 *            订单号
	 * @param amount
	 *            支付金额（单位：分）
	 * @param spbill_create_ip
	 *            用户ip
	 * @param fee_type
	 *            币种（1-人民币）
	 * @return String 返回数据ID
	 */
	public static String savePayInfo(String uuid, String pay_type, String orderSubject, String orderBody, String order_no, int amount,
			String spbill_create_ip, String fee_type) {

		UUID id = UUID.randomUUID();
		String pay_statu = "0";// 支付状态（0-支付中，1-支付成功，2-支付失败）
		long pay_time = System.currentTimeMillis();
		
		StringBuffer sql = new StringBuffer("INSERT INTO");
		sql.append(" pay_order(uuid,pay_type,orderSubject,orderBody,order_no,amount,spbill_create_ip,fee_type,pay_statu,pay_time)");
		sql.append(" VALUE(?,?,?,?,?,?,?,?,?,?)");

		log.info(sql.toString());

		boolean rs = JdbcTemplate.executeUpdate(sql.toString(), uuid, pay_type, orderSubject, orderBody, order_no, amount,
				spbill_create_ip, fee_type, pay_statu, pay_time);

		if (rs) {
			return id.toString();
		} else {
			return "";
		}
	}

	/**
	 * 保存预通知支付订单信息
	 * @param pay_statu
	 *            支付结果  1-支付成功，2-支付失败
	 * @param openid
	 *            微信支付用户的openid
	 * @param bank_type
	 *            付款银行
	 * @param total_fee
	 *            支付金额（单位：分）
	 * @param transaction_id
	 *            第三方交易流水号
	 * @param order_no
	 *            订单号
	 * @param err_code
	 *            支付失败代码
	 * @param err_code_des
	 *            支付失败原因描述
	 * @param time_end
	 *            支付完成时间
	 * @return String 返回数据ID
	 */
	public static String savePayResultInfo(int pay_statu, String openid, String bank_type, String total_fee,
			String transaction_id, String order_no, String err_code,
			String err_code_des, String time_end) {

		UUID id = UUID.randomUUID();

		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE pay_order SET pay_statu=?,openid=?,bank_type=?,total_fee=?,transaction_id=?,err_code=?,err_code_des=?,end_time=? WHERE order_no=?");
		
		log.info(sql.toString());

		boolean rs = JdbcTemplate.executeUpdate(sql.toString(), pay_statu, openid, bank_type, total_fee,
				transaction_id, order_no, pay_statu, err_code, err_code_des, time_end);

		if (rs) {
			return id.toString();
		} else {
			return "";
		}
	}
}
