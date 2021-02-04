package com.lemon.java.maven.day4.jdbcdemo;

public class JDBCDemo3 {
	public static void main(String[] args) {
		String sql="Select f_name,f_sex from t_member where f_full_name=? and f_status=?";
		Object [] values= {"用户0006650","2"};
		JDBCUtil3.query(sql,values);
	}

}
