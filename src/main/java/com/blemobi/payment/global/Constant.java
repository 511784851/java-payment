package com.blemobi.payment.global;

/**
 * @author 李子才<davis.lee@blemobi.com>
 * 这是跟前端App相关的全局变量值的工具类，前端App需要的参数，都是重这里获取。
 * 系统启动时，从 consul服务器获取到的运行参数，统一保存在这里.
 * 系统中其他的子模块需要用参数信息，统一从这里获取.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.blemobi.payment.consul.ConsulChangeListener;
import com.google.common.base.Strings;

import lombok.extern.log4j.Log4j;

@Log4j
public class Constant  {
	
	//通用键值的key的声明
	private static final String KEY_REDIS_USER_ADDR = "redis_user_addr";
	private static final String KEY_REDIS_USER_AUTH = "redis_user_auth";
	private static final String KEY_HEALTH_CHECK_PORT = "health_check_port";
	private static final String KEY_JETTY_PORT = "jetty_port";
	
	// 我们对外服务的业务所在的包目录。目录之外，是禁止访问
	private static final String OutServicePermitPackagePath = "com.blemobi.payment.rest"; 
	// Consul定时任务间隔时间，单位：毫秒
	private static final long ConsulIntervalTime = 1000 * 30;

	
	private static String[][] accountInfo = null;
	private static List<String> errorAccount = new ArrayList();
	
	private static String[][] ossInfo = null;
	private static List<String> errorOss = new ArrayList();
	
	private static String[][] loginInfo = null;
	private static List<String> errorLogin = new ArrayList();
	
	private static String[][] walletInfo = null;//大概39行位置
	private static List<String> errorWallet = new ArrayList();
	
	// 定义我们自己的Jetty服务的端口
	private static int jettyServerPort = 9014; 

	// 定义Redis服务的IP地址
	private static String redisServerIP = null; 
	// 定义Redis服务的端口
	private static String redisServerPort = null; 
	// 定义Redis服务的认证信息
	private static String redisServerAuth = null; 
	// 聊天服务器的健康发现的端口
	private static int contactServiceHealthPort = 19014;
	
	//创建Consul服务器的适配器对象，该对象能接受从consul服务器传递过来的配置信息变更通知。
	private static ConsulChangeListener adapter = new ConsulChangeListener(){
		
		public void onEnvChange(Map<String, String> prop) {
			
			try{
				redisServerAuth = prop.get(KEY_REDIS_USER_AUTH);
				
				contactServiceHealthPort  = Integer.parseInt(prop.get(KEY_HEALTH_CHECK_PORT));
				
				jettyServerPort  = Integer.parseInt(prop.get(KEY_JETTY_PORT));
				
				String redisUserAddr = prop.get(KEY_REDIS_USER_ADDR);; // 定义Redis服务的IP地址和端口
				
				int point = redisUserAddr.indexOf(':');
				if(point < 1 || point >= (redisUserAddr.length()-1)){
					log.info("redis_user_addr format error!");
				}else{
					redisServerIP = redisUserAddr.substring(0, point);
					redisServerPort = redisUserAddr.substring(point+1);
				}
			}catch(RuntimeException e){
				log.info("Get Constant Info error! Info=(Consul Server error or Local Info not find.)");
				log.info("Please check config information.)");
				throw e;
			}
			
			
		}

		public void onServiceChange(String serviceName, String[][] serverInfo) {
			if(serviceName.equals("account")){
				accountInfo = serverInfo;
				errorAccount.clear();
			}
			
			if(serviceName.equals("oss")){
				ossInfo = serverInfo;
				errorOss.clear();
			}
			
			if(serviceName.equals("login")){
				loginInfo = serverInfo;
				errorLogin.clear();
			}
			if(serviceName.equals("wallet")){
				walletInfo = serverInfo;
				errorWallet.clear();
			}
			
		}
	};
	
	/**
	 * 获取账户服务的服务器列表。
	 * 整个过程是：先从consul中获取所有的账户服务器，然后再筛选健康的服务器，再从健康中随机取一台做为服务返回给用户。
	 * @return 返回“健康正常账户服务”的服务器信息。String[0]为IP地址，String[1]为端口。
	 */
	public static String[] getAccountServer() {
		String[][] healthAccount = getOnlineServer(accountInfo,errorAccount);
		int ramdom = (int)(Math.random() * healthAccount.length);
		return healthAccount[ramdom];
//		return new String[]{"192.168.1.241","9002"};// 账户系统IP和port信息
	}
	
	/**
	 * 获取登录服务的服务器列表。
	 * 整个过程是：先从consul中获取所有的账户服务器，然后再筛选健康的服务器，再从健康中随机取一台做为服务返回给用户。
	 * @return 返回“健康正常账户服务”的服务器信息。String[0]为IP地址，String[1]为端口。
	 */
	public static String[] getLoginServer() {
		
		String[][] healthLogin = getOnlineServer(loginInfo,errorLogin);
		int ramdom = (int)(Math.random() * healthLogin.length);
		
		return healthLogin[ramdom];
//		return new String[]{"192.168.1.241","9001"};// 账户系统IP和port信息
	}
	
	/**
	 * 获取Oss服务的服务器列表。
	 * 整个过程是：先从consul中获取所有的好友服务器，然后再筛选健康的服务器，再从健康中随机取一台做为服务返回给用户。
	 * @return 返回“健康正常Oss服务”的服务器信息。String[0]为IP地址，String[1]为端口。
	 */
	public static String[] getOssServer() {
		String[][] healthOss = getOnlineServer(ossInfo,errorOss);
		int ramdom = (int)(Math.random() * healthOss.length);
		return healthOss[ramdom];
//		return new String[]{"192.168.1.241","9008"};// 好友系统IP和port信息
	}
	
	
	public static String[] getWalletServer() { //大概120行位置
		
		String[][] healthAccount = getOnlineServer(walletInfo,errorWallet);
		int ramdom = (int)(Math.random() * healthAccount.length);
		
		return healthAccount[ramdom];
	}
	
	/**
	 * 从consul服务器获取Redis服务器的信息。
	 * @param host 是consul服务器的域名或IP地址。
	 * @return 返回Redis服务器的信息。String[0]为IP地址，String[1]为端口。
	 */
	public static String[] getRedisServer() {
		return new String[]{redisServerIP,redisServerPort};
	}
	
	/**
	 * 从consul服务器获取Redis服务器的信息。
	 * @return 返回Redis服务器的的Token认证信息。
	 */
	public static String getRedisUserAuth() {
		return redisServerAuth;// Redis服务器的认证
	}
	
	/**
	 * 报告有异常的账户服务器，该服务器将记录在异常服务器列表中，当用户获取账户服务器时，则过滤掉该服务器。
	 * 记录服务器的信息为(ip+port)的方式来代表一台服务器。
	 * @param addr 有异常的账户服务器的IP地址。
	 * @param port 有异常的账户服务器的Port端口。
	 */
	public static void reportErrorAccountServer(String addr,int port) {
		//如果是系统不是local模式，才对异常服务器列表过滤。
		//因此本地模式，该服务器只有一台，如果提示有服务器异常，该服务器就没有服务器了！
		if(!Strings.isNullOrEmpty(System.getProperty("EnvMode", ""))){
			errorAccount.add(addr+"-"+port);
		}
	}
	
	/**
	 * 报告有异常的登录服务器，该服务器将记录在异常服务器列表中，当用户获取账户服务器时，则过滤掉该服务器。
	 * 记录服务器的信息为(ip+port)的方式来代表一台服务器。
	 * @param addr 有异常的账户服务器的IP地址。
	 * @param port 有异常的账户服务器的Port端口。
	 */
	public static void reportErrorLoginServer(String addr,int port) {
		//如果是系统不是local模式，才对异常服务器列表过滤。
		//因此本地模式，该服务器只有一台，如果提示有服务器异常，该服务器就没有服务器了！
		if(!Strings.isNullOrEmpty(System.getProperty("EnvMode", ""))){
			errorLogin.add(addr+"-"+port);
		}
	}
	
	/**
	 * 报告有异常的好友服务器，该服务器将记录在异常服务器列表中，当用户获取好友服务器时，则过滤掉该服务器。
	 * 记录服务器的信息为(ip+port)的方式来代表一台服务器。
	 * @param addr 有异常的好友服务器的IP地址。
	 * @param port 有异常的好友服务器的Port端口。
	 */
	public static void reportErrorFriendServer(String addr,int port) {
		//如果是系统不是local模式，才对异常服务器列表过滤。
		//因此本地模式，该服务器只有一台，如果提示有服务器异常，该服务器就没有服务器了！
		if(!Strings.isNullOrEmpty(System.getProperty("EnvMode", ""))){
			errorOss.add(addr+"-"+port);
		}
	}

	/**
	 * 从某个服务的consul服务器列表中，过来出来健康的服务器列表。
	 * @param all 某个服务的所有服务器，也就是从consul服务器中获取到该服务的所有服务器。
	 * @param error 某个服务的所有服务器中，有异常的服务器类别。
	 * @return 返回某个服务的健康的服务器的列表，也就是“all集合”中排除掉“error集合”后 ，剩余的结果。String[i][0]为IP地址，String[i][1]为端口。
	 */
	private static String[][] getOnlineServer(String[][] all, List<String> error) {
		int count = all.length;
		String[][] buff = new String[count][2];
		int index = 0;
		
		String[] errorList = new String[error.size()];
		error.toArray(errorList);
		
		for(int i=0;i<count;i++){
			String v = buff[i][0] + "-" + buff[i][1];
			if(!isExist(errorList,v)){
				buff[index][0] = all[i][0];
				buff[index][1] = all[i][1];
				index++;
			}
		}

		String[][] rtn = new String[index][2];
		System.arraycopy(buff, 0, rtn, 0, rtn.length);
		
		return rtn;
	}

	/**
	 * 判断集合里是否存在某个元素。
	 * @param errorList 大集合。
	 * @param v 待查找存在的字符串对象。
	 * @return 如果存在，则返回true。
	 */
	private static boolean isExist(String[] errorList, String v) {
		for(int i=0;i<errorList.length;i++){
			if(errorList[i].equals(v)){
				return true;
			}
		}
		return false;
	}


	/**
	 * 获取jesry容器中允许访问的包名目录。默认值是com.blemobi.contact.rest。
	 * @return 授权访问的包名。
	 */
	public static String getOutservicepermitpackagepath() {
		return OutServicePermitPackagePath;
	}



	/**
	 * 获取访问聊天系统的服务端口，默认值是9006。
	 * @return 服务端口。
	 */
	public static int getJettyServerPort() {
		return jettyServerPort;
	}

	/**
	 * 获取访问consul服务器的间隔时间。
	 * @return 间隔时间，单位是毫秒的值。
	 */
	public static long getConsulIntervaltime() {
		return ConsulIntervalTime;
	}


	/**
	 * 获取注册的适配器，当适配器注册登记到ConsulManager管理类后，则可以收到Consul的配置信息变更的通知。
	 * @return 适配器对象。
	 */
	public static ConsulChangeListener getAdapter() {
		return adapter;
	}
	
	/**
	 * 获取聊天服务器的健康发现的端口。
	 * @return 网络Socket端口值。
	 */
	public static int getChatServiceHealthPort() {
		return contactServiceHealthPort ;
	}
}
