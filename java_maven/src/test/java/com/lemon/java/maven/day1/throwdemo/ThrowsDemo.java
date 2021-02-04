package com.lemon.java.maven.day1.throwdemo;
/*异常处理机制：抛出异常
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;
/*
 * 抛出的异常传递给了上一级调用者
 * 人为的抛出一个异常，用throw
 */

public class ThrowsDemo {
/*
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String filepath ="src/test/resources/log4j.properties";
		//准备文件对象
				File file=new File(filepath);
				//将文件里面的内容读取到输入流对象中
				InputStream inStream=new FileInputStream(file);
				Properties properties=new Properties();
				properties.load(inStream);
				System.out.println(properties.getProperty("log4j.rootLogger"));
	//选中24-29行，然后右键 refactor-->extract method,封装成一个loadProperties方法
*/	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String filepath ="src/test/resources/log4j.properties";
		loadProperties(filepath);
				
	}

	private static void loadProperties(String filepath) throws FileNotFoundException, IOException, ClassNotFoundException {
		//准备文件对象
		File file=new File(filepath);
		//将文件里面的内容读取到输入流对象中
		InputStream inStream=new FileInputStream(file);
		Properties properties=new Properties();
		properties.load(inStream);
		System.out.println(properties.getProperty("log4j.rootLogger"));
		//人为的抛出一个异常，用throw
		//throw new ClassNotFoundException();
	}
}
