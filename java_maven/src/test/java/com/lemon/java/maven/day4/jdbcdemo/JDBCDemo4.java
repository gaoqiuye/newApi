package com.lemon.java.maven.day4.jdbcdemo;

public class JDBCDemo4 {
	public static void main(String[] args) {
		//String sql="Select f_name,f_sex from t_member where f_full_name=? and f_status=?";
		String sql="Select f_full_name,f_name,f_sex from t_member where f_full_name=? and f_status=?";
		Object [] values= {"用户0006650","2"};
		JDBCUtil4.query(sql,values);
		JDBCUtil4.query(sql,values);
	}

}
