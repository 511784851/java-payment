package com.blemobi.payment.rest.common;

public class WalletTools {
	//这是支付充传值成功后对钱包系统的通知
	public static boolean invokeAddDiamond(String uuid,String token,int rmoney,String ordernumber) {
		boolean rtn = false;
		int max=5;
		while(max-->0){
			if(invokeAddDiamond2(uuid,token,rmoney,ordernumber)){
				rtn = true;
				break;
			}
		}
		return rtn;
		
	}
	
	private static boolean invokeAddDiamond2(String uuid,String token,int rmoney,String ordernumber) {
		//钱包系统的protocol文件为
		return false;
		
	}

}
