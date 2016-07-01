package com.blemobi.pay.sql;

import java.util.UUID;

import com.blemobi.pay.dbcp.JdbcTemplate;

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
	 * @param bank_type
	 *            支付渠道（WX-微信,ZFB-支付宝）
	 * @param name
	 *            商品名称
	 * @param order_no
	 *            订单号
	 * @param amount
	 *            支付金额（单位：分）
	 * @param app_ip
	 *            App ID
	 * @param fee_type
	 *            币种（1-人民币）
	 * @return String 返回数据ID
	 */
	public static String savePayInfo(String uuid, String bank_type, String name, String order_no, String amount,
			String app_ip, String fee_type) {

		UUID id = UUID.randomUUID();
		String pay_statu = "0";// 支付状态（0-支付中，1-支付成功，2-支付失败）

		StringBuffer sql = new StringBuffer("INSERT INTO");
		sql.append(" pay_order(id,uuid,bank_type,name,order_no,amount,app_ip,fee_type,pay_statu)");
		sql.append(" VALUE(?,?,?,?,?,?,?)");

		log.info(sql.toString());

		boolean rs = JdbcTemplate.executeUpdate(sql.toString(), id.toString(), uuid, bank_type, name, order_no, amount,
				app_ip, fee_type, pay_statu);

		if (rs) {
			return id.toString();
		} else {
			return "";
		}
	}

	/**
	 * 保存预通知支付订单信息
	 * 
	 * @param openid
	 *            用户在商户appid下的唯一标识
	 * @param trade_type
	 *            支付渠道（WX-微信,ZFB-支付宝）
	 * @param bank_type
	 *            支付金额（单位：分）
	 * @param amount
	 *            支付金额（单位：分）
	 * @param fee_type
	 *            币种（1-人民币）
	 * @param transaction_id
	 *            第三方交易流水号
	 * @param order_no
	 *            订单号
	 * @param pay_statu
	 *            支付结果（SUCCESS/FAIL）
	 * @param err_code
	 *            支付失败代码
	 * @param err_code_des
	 *            支付失败原因描述
	 * @param time_end
	 *            支付完成时间
	 * @return String 返回数据ID
	 */
	public static String savePayResultInfo(String openid, String trade_type, String bank_type, String amount,
			String fee_type, String transaction_id, String order_no, String pay_statu, String err_code,
			String err_code_des, String time_end) {

		UUID id = UUID.randomUUID();

		StringBuffer sql = new StringBuffer("INSERT INTO");
		sql.append(
				" pay_result(id,openid,trade_type,bank_type,amount,fee_type,transaction_id,order_no,pay_statu,err_code,err_code_des,time_end)");
		sql.append(" VALUE(?,?,?,?,?,?,?,?,?,?,?,?)");

		log.info(sql.toString());

		boolean rs = JdbcTemplate.executeUpdate(sql.toString(), id.toString(), openid, trade_type, bank_type, amount,
				fee_type, transaction_id, order_no, pay_statu, err_code, err_code_des, time_end);

		if (rs) {
			return id.toString();
		} else {
			return "";
		}
	}
}
