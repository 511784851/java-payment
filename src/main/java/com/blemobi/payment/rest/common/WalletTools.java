package com.blemobi.payment.rest.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.blemobi.payment.global.PathGlobal;
import com.blemobi.payment.util.ClientUtilImpl;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PResult;

import lombok.extern.log4j.Log4j;
@Log4j
public class WalletTools {
	//这是支付充传值成功后对钱包系统的通知
	public static boolean invokeWalletDiamondAdd(String uuid,String token,int rmoney,String ordernumber){
		boolean rtn = false;
		int max=5; //通讯连接，最多重复5遍。
		while(max-->0){
			try {
				if(invokeWalletDiamondAdd2(uuid,token,rmoney,ordernumber)){
					rtn = true;
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		return rtn;
		
	}
	
	private static boolean invokeWalletDiamondAdd2(String uuid,String token,int rmoney,String ordernumber) throws Exception {
		boolean rtn = false;
		ClientUtilImpl clientUtil = new ClientUtilImpl();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uuid", uuid));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("rmoney", ""+rmoney));
		params.add(new BasicNameValuePair("ordernumber", ordernumber));
		
		
		Cookie[] cookies = new Cookie[2];
		cookies[0] = new Cookie("uuid", uuid);
		cookies[1] = new Cookie("token", token);

		String url = clientUtil.createWalletUrl(PathGlobal.GetWalletDiamondAdd);
		PMessage message = clientUtil.getMethod(url, params, cookies);

		String type = message.getType();
		log.info("type=["+type+"]");
		if ("PUser".equals("PDiamondCount")) {
			rtn = true;
		}else{
			PResult pr = PResult.parseFrom(message.getData());
			log.info("getErrorCode=["+pr.getErrorCode()+"]");
			log.info("getErrorMsg=["+pr.getErrorMsg()+"]");
			rtn = false;
		}
		return rtn;
		
	}
}
