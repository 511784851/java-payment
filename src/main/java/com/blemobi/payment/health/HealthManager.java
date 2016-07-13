package com.blemobi.payment.health;
/**
 * @author 李子才<davis.lee@blemobi.com>
 * 这是聊天系统的健康服务发现的实现类，实现对外的ServerSocket服务，它是一个http服务器。
 */
import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

import lombok.extern.log4j.Log4j;
@Log4j
@SuppressWarnings("restriction")
public class HealthManager {
	/**
	 * 启动对外的健康服务监听。
	 * @param healthPort 健康服务的端口。
	 */
	public static void startService(int healthPort) {
		try {
        	HttpServer server = HttpServer.create(new InetSocketAddress(healthPort), 0);
            server.createContext("/", new HealthHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            log.info("Health Report Server Running Port:"+healthPort);
		} catch (Exception e) {
			log.info("Health Report Server catch an exception, Port["+healthPort+"] is used!");
        	log.info("System exit!");
        	log.info("Good bye!");
        	
			System.exit(0);
		}
	}
}
