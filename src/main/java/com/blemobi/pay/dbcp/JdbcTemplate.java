package com.blemobi.pay.dbcp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * mysql数据操作类
 */
public class JdbcTemplate {

	// 执行DDL语句（多条sql执行带事物）
	public static boolean executeUpdate(String sql, Object... params) {
		boolean flag = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			// 2.获取数据库连接
			conn = DbcpConnect.getConnect();
			ps = conn.prepareStatement(sql);

			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}

			int result = ps.executeUpdate();
			flag = result > 0 ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(ps, conn);
		}
		return flag;
	}

	// 执行查询语句
	public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 2.获取数据库连接
			conn = DbcpConnect.getConnect();
			ps = conn.prepareStatement(sql);

			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}

			rs = ps.executeQuery();

			ResultSetMetaData metaData = rs.getMetaData();
			int col_len = metaData.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < col_len; i++) {
					String cols_name = metaData.getColumnName(i + 1);
					Object cols_value = rs.getObject(cols_name);
					if (cols_value == null) {
						cols_value = "";
					}
					map.put(cols_name, cols_value);
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(rs, ps, conn);
		}
		return list;
	}

	// 关闭数据库连接
	private static void closeAll(ResultSet rs, PreparedStatement ps, Connection conn) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	private static void closeAll(PreparedStatement ps, Connection conn) {
		closeAll(null, ps, conn);
	}
}
