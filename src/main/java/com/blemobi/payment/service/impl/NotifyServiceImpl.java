package com.blemobi.payment.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.blemobi.payment.dao.RedDao;
import com.blemobi.payment.dao.impl.TransactionDaoImpl;
import com.blemobi.payment.model.Transaction;
import com.blemobi.payment.service.NotifyService;
import com.blemobi.payment.util.SignUtil;

/**
 * 钱包支付回调
 * 
 * @author zhaoyong
 *
 */
public class NotifyServiceImpl implements NotifyService {
	/**
	 * 签名密钥
	 */
	private static final String seckey = "3EDA7B432E238BAD1208BADC65E39B01";

	@Autowired
	private RedDao redDao;

	@Autowired
	private TransactionDaoImpl transactionDao;

	/**
	 * 回调处理
	 * 
	 * @return
	 */
	public String callback(Transaction transaction, String sign) {
		if (!checkSign(transaction, sign))
			return "sign error";
//
//		Red red = redDao.selectByKey(transaction.getCustorderno());
//		if (red != null && red.getStatus() == -2) {
//			red.setAmount(transaction.getOrderamount());
//			red.setStatus(0);// 已支付可以领取了
//			redDao.updateByKey(red);
//			transactionDao.insert(transaction);
//			return "success";
//		}

		return "fail";
	}

	/**
	 * 验证签名
	 * 
	 * @return
	 */
	private boolean checkSign(Transaction transaction, String sign) {
		Map<String, String> params = signParams(transaction);
		String mySign = SignUtil.sign(params);
		return sign.equals(mySign);
	}

	/**
	 * 得到参与签名的参数
	 * 
	 * @return
	 */
	private Map<String, String> signParams(Transaction transaction) {
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