package com.blemobi.payment.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.blemobi.payment.mapper.RedMapper;
import com.blemobi.payment.mapper.TransactionMapper;
import com.blemobi.payment.model.Red;
import com.blemobi.payment.model.Transaction;
import com.blemobi.payment.test.DBTools;
import com.blemobi.payment.util.SignUtil;

/**
 * 钱包支付回调
 * 
 * @author zhaoyong
 *
 */
public class CallbackService {
	/**
	 * 签名密钥
	 */
	private static final String seckey = "3EDA7B432E238BAD1208BADC65E39B01";

	/**
	 * 交易信息
	 */
	private Transaction transaction;
	private String sign;

	/**
	 * 构造方法
	 * 
	 * @param transaction
	 * @param sign
	 */
	public CallbackService(Transaction transaction, String sign) {
		this.transaction = transaction;
		this.sign = sign;
	}

	/**
	 * 回调处理
	 * 
	 * @return
	 */
	public String callback() {
		if (!checkSign())
			return "sign error";

		SqlSession session = DBTools.getSession();
		try {
			RedMapper redMapper = session.getMapper(RedMapper.class);
			Red red = redMapper.selectByPrimaryKey(transaction.getCustorderno());
			if (red != null && red.getStatus() == -2) {
				red.setAmount(transaction.getOrderamount());
				red.setStatus(0);// 已支付可以领取了
				redMapper.updateByPrimaryKey(red);

				TransactionMapper transactionMapper = session.getMapper(TransactionMapper.class);
				transactionMapper.insert(transaction);

				session.commit();
				return "success";
			}
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
		} finally {
			session.close();
		}

		return "fail";
	}

	/**
	 * 验证签名
	 * 
	 * @return
	 */
	private boolean checkSign() {
		Map<String, String> params = signParams();
		String mySign = SignUtil.sign(params);
		return sign.equals(mySign);
	}

	/**
	 * 得到参与签名的参数
	 * 
	 * @return
	 */
	private Map<String, String> signParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderAmount", transaction.getOrderamount() + "");
		params.put("orderNo", transaction.getOrderno());
		params.put("orderStatus", transaction.getOrderstatus() + "");
		params.put("orderTime", transaction.getOrdertime() + "");
		params.put("custOrderNo", transaction.getCustorderno());
		params.put("receiveUid", transaction.getReceiveuid());
		params.put("seckey", seckey);
		return params;
	}
}