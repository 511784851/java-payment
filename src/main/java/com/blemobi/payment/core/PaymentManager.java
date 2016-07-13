package com.blemobi.payment.core;

import com.blemobi.payment.consul.ConsulManager;
import com.blemobi.payment.global.Constant;
import com.blemobi.payment.health.HealthManager;
import com.blemobi.payment.jetty.JettyServer;

import lombok.extern.log4j.Log4j;
@Log4j
public class PaymentManager {

	public static void main(String[] args) {
		// 初始化Consul，获取连接Consul服务器的间隔时间
		long consulIntervalTime = Constant.getConsulIntervaltime();

		// 初始化Consul, 确定连接了正确的服务器环境（本地测试，北美测试服务器，或者是生产服务器）
		ConsulManager.startService(args, consulIntervalTime); // 启动连接Consul服务

		// 注册监听器，如果Consul服务器获取有配置信息变更，则通知Constant
		ConsulManager.addConsulChangeListener(Constant.getAdapter());

		log.info("Starting Payment Server ...");

		// 启动Jetty HTTP服务器
		String packages = Constant.getOutservicepermitpackagepath();
		int port = Constant.getJettyServerPort();
		log.info("Payment Server Running Port:"+port);
		JettyServer jettyServer = new JettyServer();
		jettyServer.startServer(port, packages);
		
		//发布Consul的健康发现
		int healthPort = Constant.getChatServiceHealthPort();
		HealthManager.startService(healthPort); 
		
		/*
		 * 1）从Consul读取信息 2) 开启Jetty HTTP服务器 3) 通过protobuff连接帐户服务器 4) 连接wukong
		 */
		log.info("Start Payment Server Finish!");
		log.info("Waiting client connect...");
	}

}
