package com.blemobi.payment.rest.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.blemobi.payment.dbcp.JdbcTemplate;
import com.blemobi.payment.rest.util.IDMake;
import com.blemobi.payment.util.ReslutUtil;
import com.blemobi.sep.probuf.PaymentProtos;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

import lombok.extern.log4j.Log4j;
@Log4j
public class AliPayUtil {
	
	private final static String notifyUrl="http://47.88.10.109:8001/payment/alipay/notify";

	public static PMessage paySign(String uuid, String token, String orderSubject, String orderBody,
			long amount) {
		//String orderNo = getOutTradeNo();
		long time = System.currentTimeMillis();//订单时间
		
		//形成订单号的规则是 uuid+时间+金额
		String orderNo = IDMake.build(uuid, time, amount);
		
		boolean saveFlag = saveOrderInfo(uuid,orderNo,orderSubject, orderBody, amount);
		
		String orderInfo = getOrderInfo(orderNo,orderSubject, orderBody, amount);
		
		/**
		 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
		 */
		String sign = sign(orderInfo);
		try {
			/**
			 * 仅需对sign 做URL编码
			 */
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
		
		
		PaymentProtos.PAlipayOrderInfo rtn = PaymentProtos.PAlipayOrderInfo.newBuilder().setOrderNo(orderNo).setPayInfo(payInfo).build();
		
		log.info("getOrderNo="+rtn.getOrderNo());
		log.info("getPayInfo="+rtn.getPayInfo());
		
		return ReslutUtil.createReslutMessage(rtn);
		
	}

	private static boolean saveOrderInfo(String uuid, String orderNo, String orderSubject, String orderBody, long amount) {
		//String pay_statu = "0";// 支付状态（0-支付中，1-支付成功，2-支付失败）

		String sql = "INSERT INTO pay_order(id,uuid,bank_type,name,order_no,amount,app_ip,fee_type,pay_statu) VALUE('%s','%s','%s','%s','%s','%s','%s','%s','%s')";

		String id = UUID.randomUUID().toString();
		sql = String.format(sql, id,uuid,"ZFB",orderSubject,orderNo,amount,"127.0.0.1","1","0");
		log.info(sql.toString());
		boolean rtn = JdbcTemplate.executeUpdate(sql);
		return rtn;
	}

	//把金额的元转换成分。
	private static long converYuanToFen(String yuan) {
		long rtn = 0L;
		if(!yuan.equals("0")){
			String fen = "";
			String keyword = ".";
			int point = yuan.indexOf(keyword);
			yuan = yuan+"00";
			if(point>0){
				fen = yuan.substring(0,point)+yuan.substring(point+1,2);
			}
			while(fen.startsWith("0")){
				fen = fen.substring(1);
			}
			rtn = Long.parseLong(fen);
		}
		return rtn;
	}

	public static String payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号	String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//支付宝交易号	String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

		//商户号
		String seller_id = new String(request.getParameter("seller_id").getBytes("ISO-8859-1"),"UTF-8");
		//金额
		String total_fee = new String(request.getParameter("total_fee").getBytes("ISO-8859-1"),"UTF-8");

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

		if(AlipayNotify.verify(params)){//验证成功
			//////////////////////////////////////////////////////////////////////////////////////////
			//请在这里加上商户的业务逻辑程序代码

			//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			
			if(trade_status.equals("TRADE_FINISHED")){
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
					
				//注意：
				//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
				//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
				if(seller_id.equals(AlipayConfig.seller)){
					//判断商户号，订单号，金额一致。
					String sql = "update pay_order set pay_statu='%s' where order_no='%s' and amount='%s'";
					long amount = converYuanToFen(total_fee);
					sql = String.format(sql, "2",trade_no,amount);
					log.info(sql.toString());
					boolean rtn = JdbcTemplate.executeUpdate(sql);
				}
				
			} else if (trade_status.equals("TRADE_SUCCESS")){
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
					
				//注意：
				//付款完成后，支付宝系统发送该交易状态通知
				//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的


				//String pay_statu = "0";// 支付状态（0-支付中，1-支付成功，2-支付失败）
				
				if(seller_id.equals(AlipayConfig.seller)){
					//判断商户号，订单号，金额一致。
					String sql = "update pay_order set pay_statu='%s' where order_no='%s' and amount='%s'";
					long amount = converYuanToFen(total_fee);
					sql = String.format(sql, "1",trade_no,amount);
					log.info(sql.toString());
					boolean rtn = JdbcTemplate.executeUpdate(sql);
				}
			}

			//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
				
			return "success";	//请不要修改或删除

			//////////////////////////////////////////////////////////////////////////////////////////
		}else{//验证失败
			return "fail";
		}
		
	}
	
	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private static String getOrderInfo(String orderNo, String subject, String body, long amount) {
		String yuanAmount = converFenToYuan(amount);

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + AlipayConfig.partner + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + AlipayConfig.seller + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderNo + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + yuanAmount + "\"";

		// 服务器异步通知页面路径
		//orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";
		orderInfo += "&notify_url=" + "\"" + notifyUrl + "\"";

		
		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	//把分转成元
	private static String converFenToYuan(long fen) {
		String rtn = "";
		if(fen==0){
			rtn = "0";
		}else if((fen%100)==0){
			rtn = ""+(fen/100);
		}else if((fen%10)==0){
			long v = fen/10;
			if(v<10){
				rtn = "0."+v;
			}else{
				rtn = ""+v;
				rtn = rtn.substring(0,rtn.length()-1)+"."+rtn.substring(rtn.length()-1);
			}
		}else{
			if(fen<10){
				rtn = "0.0"+fen;
			}else if(fen<100){
				rtn = "0."+fen;
			}else{
				rtn = ""+fen;
				rtn = rtn.substring(0,rtn.length()-2)+"."+rtn.substring(rtn.length()-2);
			}
		}
		return rtn;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		key = key+"-"+UUID.randomUUID().toString();//在原有基础上再加上UUID
		return key;
	}


	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private static String sign(String content) {
		return SignUtils.sign(content, AlipayConfig.private_key);
	}
	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private static String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
