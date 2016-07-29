package com.blemobi.payment.sql;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.blemobi.payment.dbcp.JdbcTemplate;

import lombok.extern.log4j.Log4j;

/**
 * @author andy.zhao@blemobi.com 支付数据处理类
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
	public static String savePayInfo(String uuid, String pay_type, String orderSubject, String orderBody,
			String order_no, long amount, String spbill_create_ip, String fee_type) {

		UUID id = UUID.randomUUID();
		String pay_statu = "0";// 支付状态（0-支付中，1-支付成功，2-支付失败）
		long pay_time = System.currentTimeMillis();

		StringBuffer sql = new StringBuffer("INSERT INTO");
		sql.append(
				" pay_order(uuid,pay_type,orderSubject,orderBody,order_no,amount,spbill_create_ip,fee_type,pay_statu,pay_time)");
		sql.append(" VALUE(?,?,?,?,?,?,?,?,?,?)");

		log.info(sql.toString());

		boolean rs = JdbcTemplate.executeUpdate(sql.toString(), uuid, pay_type, orderSubject, orderBody, order_no,
				amount, spbill_create_ip, fee_type, pay_statu, pay_time);

		if (rs) {
			return id.toString();
		} else {
			return "";
		}
	}

	/**
	 * 保存通知支付订单信息
	 * 
	 * @param pay_statu
	 *            支付结果 1-支付成功，2-支付失败
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
	public static String savePayResultInfo(int pay_statu, String openid, String bank_type, long total_fee,
			String transaction_id, String order_no, String err_code, String err_code_des, long time_end) {

		UUID id = UUID.randomUUID();

		StringBuffer sql = new StringBuffer();
		sql.append(
				"UPDATE pay_order SET pay_statu=?,openid=?,bank_type=?,total_fee=?,transaction_id=?,err_code=?,err_code_des=?,end_time=? WHERE order_no=?");

		log.info(sql.toString());

		boolean rs = JdbcTemplate.executeUpdate(sql.toString(), pay_statu, openid, bank_type, total_fee, transaction_id,
				err_code, err_code_des, time_end, order_no);

		if (rs) {
			return id.toString();
		} else {
			return "";
		}
	}

	/**
	 * 根据订单编号查询订单信息
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param orderNo
	 *            要查询的订单号
	 * @return Map 返回订单信息
	 */
	public static Map<String, Object> query(String uuid, String order_no) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM pay_order t WHERE t.uuid=? AND t.order_no=?");

		log.info(sql.toString());

		List<Map<String, Object>> list = JdbcTemplate.executeQuery(sql.toString(), uuid, order_no);

		if (list != null && list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 根据订单编号查询订单信息
	 * 
	 * @param orderNo
	 *            要查询的订单号
	 * @return Map 返回订单信息
	 */
	public static Map<String, Object> query(String order_no) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM pay_order t WHERE t.order_no=?");

		log.info(sql.toString());

		List<Map<String, Object>> list = JdbcTemplate.executeQuery(sql.toString(), order_no);

		if (list != null && list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 批量查询用户支付订单信息
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param offset
	 *            批量查询起始值
	 * @param count
	 *            批量查询的数量
	 * @return PMessage 返回订单信息列表
	 */
	public static List<Map<String, Object>> queryList(String uuid, int offset, int count) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM pay_order t WHERE t.uuid=? ORDER BY t.pay_time DESC LIMIT ?,? ");

		log.info(sql.toString());

		List<Map<String, Object>> list = JdbcTemplate.executeQuery(sql.toString(), uuid, offset, count);

		return list;
	}
}
