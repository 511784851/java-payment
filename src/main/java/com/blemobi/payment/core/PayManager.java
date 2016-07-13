package com.blemobi.payment.core;


import com.blemobi.payment.jetty.JettyServer;

import lombok.extern.log4j.Log4j;

@Log4j
public class PayManager {

	public static void main(String[] args) {
		// 启动Jetty HTTP服务器
		String packages = "com.blemobi.payment.rest";
		int port = 9085;
		log.info("Payment Server Running Port:"+port);
		JettyServer jettyServer = new JettyServer();
		jettyServer.startServer(port, packages);
	}

}
