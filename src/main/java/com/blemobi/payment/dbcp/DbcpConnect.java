package com.blemobi.payment.dbcp;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

import com.blemobi.payment.global.Constant;

/*
 * mysql连接池管理类
 */
public class DbcpConnect {
	private static BasicDataSource dataSourec = null;

	public DbcpConnect() {
		dataSourec = new BasicDataSource();
	}

	// 初始化连接池
	public void loadPool() {
		dataSourec.setDriverClassName(Constant.getDbDriverClassName());
		dataSourec.setUrl(Constant.getDbUrl());
		dataSourec.setUsername(Constant.getDbUsername());
		dataSourec.setPassword(Constant.getDbPassword());
		dataSourec.setInitialSize(Integer.parseInt(Constant.getDbInitialSize()));
		dataSourec.setMaxActive(Integer.parseInt(Constant.getDbMaxActive()));
		dataSourec.setMaxIdle(Integer.parseInt(Constant.getDbMaxIdle()));
		dataSourec.setMinIdle(Integer.parseInt(Constant.getDbMinIdle()));
		dataSourec.setMaxWait(Integer.parseInt(Constant.getDbMaxWait()));
	}

	// 获得连接
	public static Connection getConnect() throws SQLException {
		return dataSourec.getConnection();
	}
}
