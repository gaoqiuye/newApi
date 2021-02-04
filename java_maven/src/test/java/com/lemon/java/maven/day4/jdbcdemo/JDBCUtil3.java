package com.lemon.java.maven.day4.jdbcdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;


public class JDBCUtil3 {
	public static void query(String sql,Object...values) {
		//”？“:代表占位符
		//String sql="Select f_name,f_sex from t_member where f_full_name=? and f_status=? ";
		//1.连接数据库
		
		try {
			Properties properties=new Properties();
			InputStream iStream=new FileInputStream(new File("src/test/resources/jdbc.properties"));
			properties.load(iStream);
			String url=properties.getProperty("jdbc.url");
			String user=properties.getProperty("jdbc.username");
			String password=properties.getProperty("jdbc.password");
			
			Connection connection= DriverManager.getConnection(url, user, password);
			//2.获取PreparedStatement对象（此类型的对象有提供数据库的操作方法）
			PreparedStatement preparedStatement=connection.prepareStatement(sql);
			//3.设置条件字段的值
//			preparedStatement.setObject(1, "用户0006650");//设置sql中f_full_name对应的位置和值
//			preparedStatement.setObject(2, "2");//设置sql中f_status对应的位置和值
			for(int i=0;i<values.length;i++) {
				preparedStatement.setObject(i+1, values[i]); //setObject 从where f_full_name=? and f_status=?第一个开始取值，所以用i+1。
			}
			//4.调用查询方法，执行查询，返回ResultSet结果集
			ResultSet resultSet=preparedStatement.executeQuery();//循环完后执行sql
			//5.从结果集取查询数据
			while(resultSet.next()){
				String f_nameValueString=resultSet.getObject("f_name").toString();
				String f_sexValueString=resultSet.getObject("f_sex").toString();  //这里取得字段跟SQL里需要查询的字段一致
				System.out.println(f_nameValueString);
				System.out.println(f_sexValueString);
				
			}
		} catch (Exception e) {
			 
			e.printStackTrace();
		}
	}
	
}
