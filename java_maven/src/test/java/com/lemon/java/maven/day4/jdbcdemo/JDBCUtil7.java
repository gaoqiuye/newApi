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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//区别于JDBCUtil6的是：6的query方法没有返回数据，7这里返回Map
public class JDBCUtil7 {
	public static Properties properties=new Properties();  
	static{
		System.out.println("静态代码块解析properties数据");
		InputStream iStream;
		try {
			iStream = new FileInputStream(new File("src/test/resources/jdbc.properties"));
			properties.load(iStream);
		
		} catch (IOException e) {
			System.out.println("发生了异常");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//返回Map
	public static Map <String,Object> query(String sql,Object...values) {//values是可变参数，可变参数一定要写在最后
		//”？“:代表占位符
		//String sql="Select f_name,f_sex from t_member where f_full_name=? and f_status=? ";
		//1.连接数据库
		
		Map<String, Object> columnLabelAndValues=null;
		try {
			//1.连接数据库
			Connection connection = getConnection();
			
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
			//以下是区别于JDBCUtil3的：
			
			//获取查询相关的信息---MetaData:元数据
			ResultSetMetaData metaData=resultSet.getMetaData();
			//得到查询字段的数目
			int columnCount=metaData.getColumnCount();
//			for(int i=1;i<=columnCount;i++) {
//				String columnLabel=metaData.getColumnLabel(i);
//			}    //for循环这一段放在while里
			
			//5.从结果集取查询数据
		    columnLabelAndValues=new HashMap<String, Object>();
			while(resultSet.next()){
				//循环取出每个字段的数据
				for(int i=1;i<=columnCount;i++) {
					String columnLabel=metaData.getColumnLabel(i);//取出列名，i=1时，就可以拿到第一列的列名
					String columnValue=resultSet.getObject(columnLabel).toString();
					//String f_nameValueString=resultSet.getObject("f_name").toString();
					//System.out.println(columnValue);
					columnLabelAndValues.put(columnLabel, columnValue);
					
				}
				
				
			}
		} catch (Exception e) {
			 
			e.printStackTrace();
		}
		return columnLabelAndValues;
	}
	/*
	 * 获取数据库连接
	 * 
	 */
	public static Connection getConnection() throws SQLException {
		//从properties获取url,username,password
		String url=properties.getProperty("jdbc.url");
		String user=properties.getProperty("jdbc.username");
		String password=properties.getProperty("jdbc.password");
		Connection connection= DriverManager.getConnection(url, user, password);
		return connection;
	}
	
}
