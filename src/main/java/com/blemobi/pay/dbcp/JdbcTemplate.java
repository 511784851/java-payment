package com.blemobi.pay.dbcp;

import java.sql.Connection;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/*
 * mysql数据操作类
 */
public class JdbcTemplate {
	// 执行DDL语句（多条sql执行带事物）
	public static boolean execute(String... sql) {
		Connection conn = null;
		Statement stmt = null;
		boolean rs = false;
		try {
			// 2.获取数据库连接
			conn = DbcpConnect.getConnect();

			// 3.创建数据库操作对象
			stmt = conn.createStatement();

			// 4.操作数据库获取结果集
			if (sql.length == 1) {
				rs = stmt.execute(sql[0]);
			} else {
				// 开启事物
				conn.setAutoCommit(false);
				try {
					for (String s : sql) {
						rs = stmt.execute(s);
					}
					conn.commit();
				} catch (SQLException e) {
					conn.rollback();
				}
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭数据库操作对象
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
			// 关闭数据库连接
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return rs;
	}

	// 执行查询语句
	public static List query(String sql) {

		return null;
	}
}
