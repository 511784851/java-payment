package com.blemobi.payment.service.helper;

import java.util.HashMap;
import java.util.Map;

import com.blemobi.payment.util.SignUtil;
import com.blemobi.sep.probuf.PaymentProtos.POrderPay;

/**
 * 融云支付授权管理类
 * 
 * @author zhaoyong
 *
 */
public class SignHelper {
	/** 私钥 */
	public static final String seckey = "test";

	/** 接受者账户类型（0-个人，1-企业） */
	private int recAccountType;
	/** 用户id */
	private String cusId;
	/** 接受者id */
	private String receivedId;
	/** 支付金额（单位：分） */
	private int fenMoney;
	/** 业务订单号 */
	private String ord_no;
	/** 商品名称 */
	private String goodsName;

	/**
	 * 构造方法
	 * 
	 * @param cusId
	 *            用户uuid
	 */
	public SignHelper(String cusId) {
		this.cusId = cusId;
	}

	/**
	 * 构造方法
	 * 
	 * @param cusId
	 *            用户uuid
	 * @param fenMoney
	 *            支付金额（单位：分）
	 * @param orderNum
	 *            业务订单号
	 * @param goodsName
	 *            商品名称
	 */
	public SignHelper(String cusId, int fenMoney, String ord_no, String goodsName) {
		this.cusId = cusId;
		this.fenMoney = fenMoney;
		this.ord_no = ord_no;
		this.goodsName = goodsName;
		this.recAccountType = 1;// 企业
		this.receivedId = "";// 企业账户

	}

	/**
	 * 构造方法
	 * 
	 * @param cusId
	 *            用户uuid
	 * @param fenMoney
	 *            支付金额（单位：分）
	 * @param orderNum
	 *            业务订单号
	 * @param goodsName
	 *            商品名称
	 * @param receivedId
	 *            个人账户
	 */
	public SignHelper(String cusId, int fenMoney, String ord_no, String goodsName, String receivedId) {
		this.cusId = cusId;
		this.fenMoney = fenMoney;
		this.ord_no = ord_no;
		this.goodsName = goodsName;
		this.recAccountType = 0;// 个人
		this.receivedId = receivedId;// 个人账户

	}

	/**
	 * 获取用户钱包凭证
	 * 
	 * @return
	 */
	public String getThirdToken() {
		String cusIdSeckey = cusId + seckey;
		return SignUtil.sign(cusIdSeckey);
	}

	/**
	 * 获取订单支付凭证
	 * 
	 * @return
	 */
	public POrderPay getOrderPay() {
		Map<String, String> params = signParams();
		String sign = SignUtil.sign(params);
		return buildPay(sign);
	}

	/**
	 * 签名参数
	 * 
	 * @return
	 */
	private Map<String, String> signParams() {
		Map<String, String> params = new HashMap<>();
		params.put("recAccountType", recAccountType + "");
		params.put("cusId", cusId);
		params.put("receivedId", receivedId);
		params.put("fenMoney", fenMoney + "");
		params.put("orderNum", ord_no);
		params.put("goodsName", goodsName);
		params.put("seckey", seckey);
		return params;
	}

	/**
	 * 构建支付数据
	 * 
	 * @param sign
	 * @return
	 */
	private POrderPay buildPay(String sign) {
		return POrderPay.newBuilder().setRecAccountType(recAccountType).setCusId(cusId).setReceivedId(receivedId)
				.setFenMoney(fenMoney).setOrdNo(ord_no).setGoodsName(goodsName).setSign(sign).build();
	}
}
