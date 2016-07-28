package com.blemobi.payment.consul;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import lombok.extern.log4j.Log4j;
@Log4j
public class LocalProp {
	private static final String[] Propkey = new String[]{
			"health_check_port",
			"redis_user_addr",
			"redis_user_auth",
			"jetty_port",
			"dbDriverClassName ",
			"dbUrl",
			"dbUsername",
			"dbPassword",
			"dbInitialSize",
			"dbMaxActive",
			"dbMaxIdle",
			"dbMinIdle",
			"dbMaxWait"
	};
	private static String[] account	= null;
	private static String[] oss 	= null;
	private static String[] login 	= null;
	private static String[] wallet 	= null;
	
	private static HashMap<String,String> propInfo = new HashMap<String,String>();
	
	public static void invokeEnv(ConsulChangeListener adapter) {
		
		adapter.onServiceChange("account", new String[][]{account});
		adapter.onServiceChange("oss", new String[][]{oss});
		adapter.onServiceChange("login", new String[][]{login});
		adapter.onServiceChange("wallet", new String[][]{wallet});
		
		adapter.onEnvChange(propInfo);
	}

	public static void setLocalEnv(String filePath) throws IOException {

		String path = System.getProperty("user.dir")+File.separator+filePath;
		InputStream in =new FileInputStream(path);

		Properties fileProp = new Properties();
		fileProp.load(in);
		
		log.info("--- Start listing properties ---");
		for(String key:Propkey){
			propInfo.put(key, ""+fileProp.getProperty(key));
			log.info(key+" = ["+fileProp.getProperty(key)+"]");
		}
		
		account = new String[]{(String) fileProp.get("account_addr"),(String) fileProp.get("account_port")};
		oss = new String[]{(String) fileProp.get("oss_addr"),(String) fileProp.get("oss_port")};
		login = new String[]{(String) fileProp.get("login_addr"),(String) fileProp.get("login_port")};
		wallet = new String[]{(String) fileProp.get("wallet_addr"),(String) fileProp.get("wallet_port")};
	
	}

}
