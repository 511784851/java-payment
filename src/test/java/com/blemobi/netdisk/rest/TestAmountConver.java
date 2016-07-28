package com.blemobi.netdisk.rest;

public class TestAmountConver {

	public static void main(String[] args) {
		long fen = 0;
		String yuan = converAmountToYuan(fen);
		System.out.println(fen+" -->"+yuan);

	}
	
	//把分转成元
		private static String converAmountToYuan(long fen) {
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


}
