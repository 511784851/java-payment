package com.guzhi.pay.helper;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.guzhi.pay.channel.zfb.Base64;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;

public class DESEncrypt {

    private static final String DES = "DES";
    private static final String CHARSET = "utf-8";
    private static final String EMPTY_STRING = "";
    private static final String DESEDE = "DESede";
    private static final String MD5 = "MD5";
    private final static String AES_ALGORITHM = "AES";
    private static final int AES_KEY_SIZIE = 128;

    /**
     * AES加密方法
     * 
     * @param encryptKey 加密的Key
     * @param data 被加密数据
     * @return
     * @throws EncryptException
     */
    public static String encryptByAES(String encryptKey, String data) {
        try {
            SecretKey secretKey = getKey(encryptKey);
            byte[] encodeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(encodeFormat, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM); // 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec); // 初始化
            byte[] result = cipher.doFinal(data.getBytes());
            return new String(Base64.encode(result));
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "src = " + data + "  ," + e.getMessage());
        }
    }

    /**
     * AES解密方法
     * 
     * @param decryptKey
     * @param data
     * @return
     * @throws DecryptException
     * @throws EncryptException
     */
    public static String decryptByAES(String decryptKey, String data) {
        try {
            SecretKey secretKey = getKey(decryptKey);
            byte[] encodeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(encodeFormat, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);// 初始化
            byte[] encodeByte = Base64.decode(data);
            byte[] decryptedByte = cipher.doFinal(encodeByte);
            return new String(decryptedByte);
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "src = " + data + "  ," + e.getMessage());
        }
    }

    private static SecretKey getKey(String key) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes());
        keyGenerator.init(AES_KEY_SIZIE, secureRandom);
        return keyGenerator.generateKey();
        // KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        // keyGenerator.init(AES_KEY_SIZIE, new SecureRandom(key.getBytes()));
        // SecretKey secretKey = keyGenerator.generateKey();
        // return secretKey;
    }

    public static byte[] encoder(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    public static byte[] decoder(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    public final static String decoder(String data, String key) {
        try {
            return new String(decoder(hex2byte(data.getBytes(CHARSET)), key.getBytes(CHARSET)));
        } catch (Exception e) {
        }
        return EMPTY_STRING;
    }

    public final static String encoder(String password, String key) {
        try {
            return byte2hex(encoder(password.getBytes(CHARSET), key.getBytes(CHARSET)));
        } catch (Exception e) {
        }
        return EMPTY_STRING;
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();

    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("HAHA");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * 3DES加密
     * 
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBy3DES(String src, String key) {
        byte[] srcB = src.getBytes();
        byte[] keyB = key.getBytes();
        byte[] encodedB = encryptMode(keyB, srcB);
        return getBase64Str(encodedB);
    }

    /**
     * 3DES解密.
     * 
     * 经过验证，这里的iv值与Y币中心约定的一样，不能变更.
     * 
     * @param encryptText
     * @param secretKey
     * @return
     */
    public static String decryptBy3DES(String encryptText, String secretKey) {
        Key deskey = null;
        DESedeKeySpec spec;
        try {
            spec = new DESedeKeySpec(secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec("01234567".getBytes());
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
            byte[] decodedBytes = Base64.decode(encryptText);
            byte[] decryptData = cipher.doFinal(decodedBytes);
            return new String(decryptData, CHARSET);
        } catch (Exception e) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
    }

    /**
     * 定义 加密算法,可用 DES,DESede,Blowfish
     * keybyte为加密密钥，长度为24字节
     * src为被加密的数据缓冲区（源）
     */
    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
            SecretKey deskey = new SecretKeySpec(keybyte, DESEDE);
            Cipher c1 = Cipher.getInstance(DESEDE);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "src = " + src.toString() + "  ," + e1.getMessage());
        } catch (javax.crypto.NoSuchPaddingException e2) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "src = " + src.toString() + "  ," + e2.getMessage());
        } catch (java.lang.Exception e3) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "src = " + src.toString() + "  ," + e3.getMessage());
        }
    }

    /**
     * 返回base64 字符串
     * 
     * @param strByte
     * @return
     */
    public static String getBase64Str(byte[] strByte) {
        String s = Base64.encode(strByte);
        return (s);
    }

    /**
     * md5 加密
     */
    public static String getMD5(String str) {
        return encode(str, MD5);
    }

    /**
     * 根据type 进行加密
     * 
     * @param str
     * @param type
     * @return
     */
    private static String encode(String str, String type) {
        try {
            MessageDigest alga = java.security.MessageDigest.getInstance(type);
            alga.update(str.getBytes("UTF-8"));
            byte[] digesta = alga.digest();
            return byte2hex(digesta);
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR,
                    "str = " + str + "  ," + "type = " + type + "  ," + e.getMessage());
        }
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        String key = "UX8901Y102";
        String src = "chineese";
        String encode = encoder(src, key);
        System.out.println("encode:" + encode);
        String decode = decoder(encode, key);
        System.out.println("decode:" + decode);
    }
}
