package com.blemobi.pay.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;

/*
 * jetty服务管理类
 */
public class JettyServer {

	private Server server;

	private int port;
	private String packages;

	// 构造函数
	public JettyServer(int port, String packages) {
		this.port = port;
		this.packages = packages;
	}

	// 启动jetty服务
	public void startServer() throws Exception {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/pay");

		this.server = new Server(port);
		server.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);
		jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES, packages);

		server.start();
	}
}
