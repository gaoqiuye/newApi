package com.lemon.java.maven.day4.jdbcdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class JDBCUtil {
	public static void query(String sql) {
		//”？“:代表占位符
		//String sql="Select f_name,f_sex from t_member where f_full_name=? and f_status=? ";
		
		
		try {
			//1.连接数据库
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
			preparedStatement.setObject(1, "用户0006650");//设置sql中f_full_name对应的位置和值
			preparedStatement.setObject(2, "2");//设置sql中f_status对应的位置和值
			//4.调用查询方法，执行查询，返回ResultSet结果集
			ResultSet resultSet=preparedStatement.executeQuery();
			//5.从结果集取查询数据
			while(resultSet.next()){
				String f_nameValueString=resultSet.getObject("f_name").toString();
				String f_sexValueString=resultSet.getObject("f_sex").toString();
				System.out.println(f_nameValueString);
				System.out.println(f_sexValueString);
				
			}
		} catch (Exception e) {
			 
			e.printStackTrace();
		}
	}
	
}
