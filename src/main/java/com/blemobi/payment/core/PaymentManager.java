package com.blemobi.payment.core;

import java.util.List;

import com.blemobi.library.cache.CacheInvalid;
import com.blemobi.library.cache.LiveThread;
import com.blemobi.library.consul.BaseService;
import com.blemobi.library.consul.ConsulManager;
import com.blemobi.library.health.HealthManager;
import com.blemobi.library.jetty.JettyServer;
import com.blemobi.library.jetty.ServerFilter;
import com.blemobi.library.log.LoggerManager;

import lombok.extern.log4j.Log4j;

/**
 * 服务启动管理类
 * 
 * @author zhaoyong
 *
 */
@Log4j
public class PaymentManager {
	/**
	 * 服务名称
	 */
	private static final String selfName = "payment";
	/**
	 * 要发布rest服务的
	 */
	private static final String packages = "com.blemobi." + selfName + ".rest";
	/**
	 * 连接Consul服务器的间隔时间
	 */
	private static long consulIntervalTime = 1000 * 30;
	/**
	 * 用户缓存失效时间（单位：毫秒）
	 */
	private static final long live_time = 1 * 24 * 60 * 60 * 1000;

	public static void main(String[] args) throws Exception {
		// 初始化Consul
		ConsulManager.startService(selfName, args, consulIntervalTime); // 启动连接Consul服务
		// 启动Jetty HTTP服务器
		startJetty();
		// 发布Consul的健康发现
		startHealth();
		// 启动线程管理用户缓存
		LiveThread LiveThread = new LiveThread(live_time, new CacheInvalid());
		LiveThread.start();
		log.info("Start Payment Server Finish!");
		// 初始化Consul日志管理
		LoggerManager.startService();
	}

	/**
	 * 发布Consul的健康发现
	 */
	private static void startHealth() {
		String health_check_port = BaseService.getProperty("health_check_port");
		int check_port = Integer.parseInt(health_check_port);
		HealthManager.startService(check_port, selfName);
	}

	/**
	 * 启动Jetty HTTP服务器
	 * 
	 * @throws Exception
	 */
	private static void startJetty() throws Exception {
		// 服务启动端口
		String jetty_port = BaseService.getProperty("jetty_port");
		int port = Integer.parseInt(jetty_port);
		// 过滤器配置
		FilterProperty filterProperty = new FilterProperty();
		List<ServerFilter> serverFilterList = filterProperty.getFilterList();
		// 启动Jetty服务
		JettyServer jettyServer = new JettyServer(selfName, packages, port, serverFilterList);
		jettyServer.start();
	}
}