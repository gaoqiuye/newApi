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

//区别于JDBCUtil4的是：4里面properties文件会解析多次，占用磁盘空间，如果把这块设置成静态的，那properties这个对象就是共享的文件了
//静态代码块里的数据会最先执行 且只执行一次
public class JDBCUtil5 {
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
	public static void query(String sql,Object...values) {
		//”？“:代表占位符
		//String sql="Select f_name,f_sex from t_member where f_full_name=? and f_status=? ";
		//1.连接数据库
		
		try {
			
//			Properties properties=new Properties();
//			InputStream iStream=new FileInputStream(new File("src/test/resources/jdbc.properties"));
//			properties.load(iStream);
			//从properties获取url,username,password
			
			//46-49行选中，然后点击-Refactor-->ExtractMethod,getConnection方法,实现后查看JDBCUtil6
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
			//以下是区别于JDBCUtil3的：
			
			//获取查询相关的信息---MetaData:元数据
			ResultSetMetaData metaData=resultSet.getMetaData();
			//得到查询字段的数目
			int columnCount=metaData.getColumnCount();
//			for(int i=1;i<=columnCount;i++) {
//				String columnLabel=metaData.getColumnLabel(i);
//			}    //for循环这一段放在while里
			
			//5.从结果集取查询数据
			while(resultSet.next()){
				//循环取出每个字段的数据
				for(int i=1;i<=columnCount;i++) {
					String columnLabel=metaData.getColumnLabel(i);//取出列名，i=1时，就可以拿到第一列的列名
					String columnValue=resultSet.getObject(columnLabel).toString();
					//String f_nameValueString=resultSet.getObject("f_name").toString();
					System.out.println(columnValue);
					
				}
				
				
			}
		} catch (Exception e) {
			 
			e.printStackTrace();
		}
	}
	
}
