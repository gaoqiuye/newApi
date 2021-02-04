package com.lemon.java.maven.day4.jdbcdemo;

import java.util.Map;
import java.util.Set;

public class JDBCDemo7 {
	public static void main(String[] args) {
		String sql1="Select f_full_name,f_name,f_sex from t_member where f_full_name=? and f_status=?";
		String sql2="Select f_full_name,f_name,f_sex from t_member where f_full_name=? and f_status=?";
		Object [] values1= {"用户0006650","2"};
		Object [] values2= {"用户001","2"};
		Map<String,Object> columnLabelAndValues1=JDBCUtil7.query(sql1, values1);
		Map<String,Object> columnLabelAndValues2=JDBCUtil7.query(sql2, values2);
		
		System.out.println("开始遍历map1");
		Set<String> columnLabels=columnLabelAndValues1.keySet();
		for(String columnLabel:columnLabels) {
			System.out.println("columnLabel:"+columnLabel+"; columnvalue:"+columnLabelAndValues1.get(columnLabel));
		}
		
		System.out.println("开始遍历map2");
		Set<String> columnLabels2=columnLabelAndValues1.keySet();
		for(String columnLabel:columnLabels2) {
			System.out.println("columnLabel:"+columnLabel+"; columnvalue:"+columnLabelAndValues2.get(columnLabel));
		}
		
	}

}
