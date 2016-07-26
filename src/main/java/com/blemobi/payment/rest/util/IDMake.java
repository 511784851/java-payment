package com.blemobi.payment.rest.util;

import java.math.BigInteger;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.blemobi.payment.util.Base64;

public class IDMake {
	private static String key = "hIrH/Ry3r4JMQHRQMl74BzMsvH+sUzkKuSFEUcClErdu2vvoPOf5kgQBa3YHzgXMboVV6seC5aFdr7SRGzJFzQ==";
	private final static String hexDigits = "0123456789ABCDEF";  
	private final static String KEY_MAC = "HmacMD5";  
	
	private final static long UnSignedIntMax = (1L << 32) -1;
	  
	public static String build(String uuid,long signTime,long amount) {
		int subUuidLen = 8;//取8个字符
		int subUuidStartPoint = 12;
		if(uuid==null) throw new RuntimeException("UUID  Format Exception! ");
		uuid = uuid.replaceAll("-", "");
		if(uuid.length() < (subUuidStartPoint+subUuidLen)) throw new RuntimeException("UUID  Format Exception! ");
		long time = signTime/1000;
		String strTime = converUnSignedIntToHex(time);
		String strAmount  = converUnSignedIntToHex(amount);
		String subUUID = uuid.substring(subUuidStartPoint, subUuidStartPoint+subUuidLen);
		
		String data = strTime+strAmount+subUUID;
		
		byte[] encryptSource = converHexTobyte(data);
		
		byte[] encryptData = encryptHMAC(encryptSource,key);
		
		byte[] last8Byte = getLastEightByte(encryptData);
		
		String strUnSignedLong = converUnSignedLong(last8Byte);

		return strUnSignedLong;
	}
	
	private static byte[] converHexTobyte(String data) {
		if(data==null) throw new RuntimeException("Hex Source is null");
		int len = data.length();
		if((len %2) != 0) throw new RuntimeException("Hex Source len must be Multiple of 2");
		int byteLen = len/2;
		byte[] rtn = new byte[byteLen];
		for(int i=0; i<rtn.length; i++){
			rtn[i] = (byte) (hexDigits.indexOf(data.charAt(i*2)) <<4 | hexDigits.indexOf(data.charAt(i*2+1)));
		}
		return rtn;
	}

	private static String converUnSignedLong(byte[] last8Byte) {
		BigInteger bi = new BigInteger(1,last8Byte);
		String rtn = bi.toString();
		System.out.println(bi.bitLength());
		return rtn;
	}

	private static byte[] getLastEightByte(byte[] encryptData) {
		if(encryptData==null) throw new RuntimeException("LastEightByte Source Data Format Exception! ");
		byte[] rtn = new byte[8];
		Arrays.fill(rtn, 0, rtn.length, (byte)0);
		int sourceLen = encryptData.length;
		if(sourceLen>=rtn.length){
			System.arraycopy(encryptData, sourceLen-8, rtn, 0, rtn.length);
		}else{
			System.arraycopy(encryptData, 0, rtn, rtn.length-sourceLen, sourceLen);
		}
		return rtn;
	}

	public static byte[] encryptHMAC(byte[] data, String key) {  
        SecretKey secretKey;  
        byte[] bytes = null;  
        try {  
            secretKey = new SecretKeySpec(Base64.decode(key), KEY_MAC);  
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());  
            mac.init(secretKey);  
            bytes = mac.doFinal(data);
            System.out.println(KEY_MAC+" encrypt last len = "+bytes.length);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return bytes;  
    } 

	private static String converUnSignedIntToHex(long time) {
		if(time<0 || time>UnSignedIntMax) throw new NumberFormatException();
		String rtn = "";
		for(int i=0;i<8;i++){
			rtn = hexDigits.charAt((int) (time & 0x0f))+rtn;
			time >>= 4;
		}
		return rtn;
	}
}
