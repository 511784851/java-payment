package com.blemobi.pay.core;

import com.blemobi.pay.dbcp.DbcpConnect;
import com.blemobi.pay.jetty.JettyServer;

import lombok.extern.log4j.Log4j;

@Log4j
public class PayManager {
	public static void main(String[] args) throws Exception {
		final String dpcpConfigUrl = "dbcp.properties";
		final String packages = "com.blemobi.pay.rest";
		final int jetty_port = 8000;

		log.info("mysql Pool Loading...");
		DbcpConnect dbcp = new DbcpConnect(dpcpConfigUrl);
		dbcp.loadPool();
		log.info("mysql Pool Load Finish");

		log.info("Payment Server Running Port:" + jetty_port);
		JettyServer jettyServer = new JettyServer(jetty_port, packages);
		jettyServer.startServer();
		log.info("Start Payment Server Finish!");
	}
}
