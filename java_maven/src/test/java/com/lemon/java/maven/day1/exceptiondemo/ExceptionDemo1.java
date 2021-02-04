package com.lemon.java.maven.day1.exceptiondemo;
/*
 * 文件异常：
 * 编译时异常====
 * 运行时异常
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/*
 * 编译时异常
 */
public class ExceptionDemo1 {

	public static void main(String[] args) throws IOException {
		//获取properties对象
		Properties properties=new Properties();
		//根据文件路径创建文件对象--
		File file=new File("src/test/resources/log4j.properties");
		//将文件里面的内容读取到  输入流对象中--FileNotFoundException---文件找不到异常(编译时异常)
		InputStream inStream=new FileInputStream(file);
		//调用load方法从输入流中加载数据数据到对象中
		//IOException(编译时异常)
		properties.load(inStream);
		
//		try {
//			InputStream inStream=new FileInputStream(file);
//			//调用load方法从输入流中加载数据数据到对象中
//			//IOException(编译时异常)
//			properties.load(inStream);
//		} catch (Exception e) {
//			System.out.println("解析properties文件出错");
//		}
		System.out.println(properties.getProperty("log4j.rootLogger" ));
	}
}
