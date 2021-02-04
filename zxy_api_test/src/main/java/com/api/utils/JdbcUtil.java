package com.api.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class JdbcUtil {
	private static Logger log = Logger.getLogger(JdbcUtil.class);
	private static String dbClassDriver;
	private static String dbUrl;
	private static String dbUser;
	private static String dbPassword;
	private static Connection con ;
	private static PreparedStatement psmt;
	private static ResultSet rs; 
	static {
		Map<String, String> map = PropertiesUtil.get("test.properties", "dbClassDriver", "dbUrl", "dbUser",
				"dbPassword");
		dbClassDriver = map.get("dbClassDriver");
		dbUrl = map.get("dbUrl");
		dbUser = map.get("dbUser");
		dbPassword = map.get("dbPassword");
	}

	private static void getConnection() {
		try {
			Class.forName(dbClassDriver);
//			if (dbUrl.equalsIgnoreCase("oracle")) {
//				Class.forName("oracle.jdbc.driver.OracleDriver");
//			} else if (dbUrl.equalsIgnoreCase("mysql")) {
//				Class.forName("com.mysql.jdbc.Driver");
//			} else if (dbUrl.equalsIgnoreCase("sqlserver")) {
//				Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
//			} else {
//				logger.error("JdbcUrl错误：" + dbUrl + "!");
//			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("数据库连接异常：" + e + "!");
		}
	}

	// 通过输入数据库地址、用户名、密码、sql语句，如果是select会返回一个list集合查询结果，如果是delete、insert等返回200
	public static List<String[]> query(String sql) {
		getConnection();
		List<String[]> list = new ArrayList<String[]>();
		try {
			PreparedStatement psmt = con.prepareStatement(sql);
			ResultSet rs = psmt.executeQuery();
			if (!sql.contains("from")) {
				String[] arr = { "200" };
				list.add(arr);
				return list;
			} else {
				String selectStr = sql.substring(0, sql.indexOf("from")).replace("select ", "").trim();
				while (rs.next()) {
					String[] arr = selectStr.split(",");
					for (int i = 0; i < arr.length; i++) {
						arr[i] = rs.getString(arr[i]);
					}
					list.add(arr);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			close(con, psmt, rs);
		}
		return list;
	}

	private static void close(Connection con, PreparedStatement psmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rs = null; // 赶紧垃圾回收
		}
		if (psmt != null) {
			try {
				psmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			psmt = null;
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			con = null;
		}
	}
}
