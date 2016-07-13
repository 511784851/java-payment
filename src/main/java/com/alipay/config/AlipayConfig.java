package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088421352394906";
	
	// 商户收款账号
	public static final String seller = "shenzhen-office-external@blemobi.com";
	
	// 商户的私钥
	public static String private_key = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAOBJi4gKVkq092dupsIcbj6QdTOEeCJ4xqkftFKZ9szRM/eRLUhzunFMGgOx98v+VTiucomycRyolUzx2jj18TpFiNbz72vNZzWnbkqGf+OYt1GqOP6eljle+b5b83d/3tRk8smbq77XPrvge50I/xk1TIETg/8rxG/2QY1wZwdlAgMBAAECgYEApcQVeT1Sm7J2bB6u5yZGIL15sdLFS40CHvtMtgQPET/Jbb8Bvduyv4vkaji9kSpvaA4en0CgMvMyLYVOqOy1FN6hrWfOM3k9lKwi0z9W50Y63DVtz7iIq8jxjUZ2YOqoVfjy6Tt8QwUlU4CKWrIvntItyMmaoWrn6jTVhS/g2CECQQD8gdqGEwU1nvCx759I+Zo4hNMDTg50IeA/ZSSiG33SrTdTbNnQbCGnMZsD0l0lC+YPsC7idlT+QBNip//H3wdNAkEA42PDNXboQ2oDZxOlg/x7YOTpJciwbJ+RppmgJbzlqqEPP635/ggRqppgq8t2/cGLjx8ZPyQ84G+Ji2SwxWzkeQI/fkBvApqAAE94CX/GJLaoZZoD56MflvFZLllj96nHP49cGlpSjeOC8BXdbAvChsNsRGvTBWglFvrytmFJgqYBAkBc1zCutZvjWneFMGpV11Jwn0XqHWSbFROc2AChJ90Fq7jUIS5+38CGzX1G503wEYPvrZzbQOpyIFgiR7w8/1bRAkBWjNZ/ctbMZ+R1jKTMhxPG6bHj1W00TFoa5gqV42tF1Nub9Bfo4qTpg4LxtPrc42uj/3Gqgpz1zbepklsywe8D";
	
	// 支付宝的公钥，无需修改该值
	public static String ali_public_key  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgSYuIClZKtPdnbqbCHG4+kHUzhHgieMapH7RSmfbM0TP3kS1Ic7pxTBoDsffL/lU4rnKJsnEcqJVM8do49fE6RYjW8+9rzWc1p25Khn/jmLdRqjj+npY5Xvm+W/N3f97UZPLJm6u+1z674HudCP8ZNUyBE4P/K8Rv9kGNcGcHZQIDAQAB";

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	
	

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = ".\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式 不需修改
	public static String sign_type = "RSA";

}
