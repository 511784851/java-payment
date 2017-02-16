package com.blemobi.payment.util;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 加密
 * 
 * @author zhaoyong
 *
 */
public final class DesUtil {
	public static final String DEFAULT_PASSWORD_CRYPT_KEY = "test";

	private static final String DES = "DES";
	private static Cipher cipher = null;

	static {
		try {
			cipher = Cipher.getInstance(DES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DesUtil() {

	}

	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {

		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	public static String decrypt(String data, String key) throws UnsupportedEncodingException, Exception {
		return new String(decrypt(hex2byte(data.getBytes("UTF-8")), key.getBytes("UTF-8")));
	}

	public static String encrypt(String data, String key) throws UnsupportedEncodingException, Exception {
		return byte2hex(encrypt(data.getBytes("UTF-8"), key.getBytes("UTF-8")));
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";

		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}

		return hs.toUpperCase();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0) {
			throw new IllegalArgumentException("长度不是偶数");
		}

		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}

		return b2;
	}

	public static void main(String[] args) throws UnsupportedEncodingException, Exception {

		String url = "123123";
		String key = "037aef20f9b1c947b57efc3877a5bfac";
		url = DesUtil.encrypt(url, key);
		System.err.println(url);
		url = DesUtil.decrypt(url, key);
		System.out.println(url);
	}
}