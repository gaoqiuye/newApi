package com.lemon.java.maven.day4.jdbcdemo;

public class JDBCDemo2 {
	public static void main(String[] args) {
		String sql="Select f_name,f_sex from t_member where f_full_name=? and f_status=? ";
		JDBCUtil.query(sql);
	}

}
