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
	public static String savePayInfo(String uuid, String bank_type, String name, String order_no, String amount, String app_ip,
			String fee_type) {

		UUID id = UUID.randomUUID();
		String pay_statu = "0";// 支付状态（0-支付中，1-支付成功，2-支付失败）

		StringBuffer sql = new StringBuffer("INSERT INTO");
		sql.append(" pay_order(id,uuid,bank_type,name,order_no,amount,app_ip,fee_type,pay_statu)");
		sql.append("VALUE('");
		sql.append(id.toString());
		sql.append("','");
		sql.append(uuid);
		sql.append("','");
		sql.append(bank_type);
		sql.append("','");
		sql.append(name);
		sql.append("','");
		sql.append(order_no);
		sql.append("','");
		sql.append(amount);
		sql.append("','");
		sql.append(app_ip);
		sql.append("','");
		sql.append(fee_type);
		sql.append("','");
		sql.append(pay_statu);
		sql.append("'");

		sql.append(")");

		log.info(sql.toString());
		
		boolean rs = JdbcTemplate.execute(sql.toString());

		if (rs) {
			return id.toString();
		} else {
			return "";
		}
	}
}
