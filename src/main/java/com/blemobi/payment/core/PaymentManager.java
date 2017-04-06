package com.blemobi.payment.core;

import java.util.List;

import com.blemobi.library.cache.CacheInvalid;
import com.blemobi.library.cache.LiveThread;
import com.blemobi.library.consul_v1.Constants.CONFIG_KV_KEY;
import com.blemobi.library.consul_v1.ConsulClientMgr;
import com.blemobi.library.consul_v1.ConsulServiceMgr;
import com.blemobi.library.consul_v1.PropsUtils;
import com.blemobi.library.jetty.JettyServer;
import com.blemobi.library.jetty.ServerFilter;
import com.blemobi.payment.bat.RefundThread;

import lombok.extern.log4j.Log4j;

/**
 * 服务启动管理类
 * 
 * @author zhaoyong
 */
@Log4j
public class PaymentManager {
	// private static String ADDRESS = "192.168.7.245";//本地环境
	private static String ADDRESS = "127.0.0.1";// 测试、生产环境
	/**
	 * 服务名称
	 */
	private static final String selfName = "payment";
	/**
	 * 要发布rest服务的
	 */
	private static final String packages = "com.blemobi." + selfName + ".rest";
	/**
	 * 用户缓存失效时间（单位：毫秒）
	 */
	private static final long live_time = 1 * 24 * 60 * 60 * 1000;

	public static void main(String[] args) throws Exception {
		ConsulClientMgr.initial(args, selfName, ADDRESS);
		log.info("consul client initialed");
		Integer jettyPort = PropsUtils.getInteger(CONFIG_KV_KEY.JETTY_PORT);
		log.info("jetty port:" + jettyPort);
		if (!ConsulClientMgr.getENV_TYPE().equalsIgnoreCase("local")) {
			Integer healthPort = PropsUtils.getInteger(CONFIG_KV_KEY.HEALTH_CHECK_PORT);
			log.info("health check port:" + healthPort);
			ConsulServiceMgr.registerServiceWithHealthChk(jettyPort, selfName, healthPort, selfName, null);
		}
		startJetty(jettyPort);
		log.info("Start Payment Server Finish!");

		// 启动线程管理用户缓存
		LiveThread LiveThread = new LiveThread(live_time, new CacheInvalid());
		LiveThread.start();
		// 退款扫描
		RefundThread rt = new RefundThread();
		(new Thread(rt)).start();
	}

	/**
	 * 启动Jetty HTTP服务器
	 * 
	 * @throws Exception
	 */
	private static void startJetty(Integer port) throws Exception {
		// 过滤器配置
		FilterProperty filterProperty = new FilterProperty();
		List<ServerFilter> serverFilterList = filterProperty.getFilterList();
		// 启动Jetty服务
		JettyServer jettyServer = new JettyServer(selfName, packages, port, serverFilterList);
		jettyServer.start();
	}
}