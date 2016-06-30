package com.blemobi.pay.dbcp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

/*
 * mysql连接池管理类
 */
public class DbcpConnect {
	// mysql连接池配置信息
	private String dpcpConfigUrl;
	private static DataSource ds = null;

	public DbcpConnect(String dpcpConfigUrl) {
		this.dpcpConfigUrl = dpcpConfigUrl;
	}

	// 初始化连接池
	public void loadPool() throws Exception {
		Properties prop = new Properties();
		// 通过类路径来加载属性文件
		prop.load(DbcpConnect.class.getClassLoader().getResourceAsStream(dpcpConfigUrl));
		// 获取数据源
		ds = BasicDataSourceFactory.createDataSource(prop);
	}

	// 获得连接
	public static Connection getConnect() throws SQLException {
		return ds.getConnection();
	}
}
