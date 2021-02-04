package com.lemon.java.maven.day1.throwdemo;
/*异常处理机制：捕获异常
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

public class ThrowsDemo2 {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String filepath ="src/test/resource/log4j.properties";
		loadProperties3(filepath);
				
	}

	private static void loadProperties1(String filepath) throws FileNotFoundException, IOException, ClassNotFoundException {
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
	private static void loadProperties2(String filepath) {
		//准备文件对象
		File file=new File(filepath); 
		//将文件里面的内容读取到输入流对象中
		InputStream inStream = null;
		try {//try代码块,里面放的是受保护的代码（可能会发生异常的代码）
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			//catch代码块，里面写的是捕捉到异常后的后续操作（输出异常错误信息，记录错误信息到日志文件）
			System.out.println("捕捉异常， 文件找不到");
		}
		Properties properties=new Properties();
		
		try {
			properties.load(inStream); 
		} catch (IOException e) {
			//e.printStackTrace();  //打印异常错误信息-打印在控制台
			//e.getMessage();获取错误信息，不会打印在控制台
			System.out.println("IO异常");
		}
		System.out.println(properties.getProperty("log4j.rootLogger"));
		//人为的抛出一个异常，用throw
		//throw new ClassNotFoundException();
	}
	
	//多重捕获
	private static void loadProperties3(String filepath) {
		//准备文件对象
		File file=new File(filepath); 
		//将文件里面的内容读取到输入流对象中
		InputStream inStream = null;
		Properties properties=new Properties();
		 
		try {//try代码块,里面放的是受保护的代码（可能会发生异常的代码）
			inStream = new FileInputStream(file);
			properties.load(inStream);
		} catch (FileNotFoundException e) {
			//catch代码块，里面写的是捕捉到异常后的后续操作（输出异常错误信息，记录错误信息到日志文件）
			//System.out.println(e.getMessage());
			e.printStackTrace();
			
		} catch (IOException e) {
			System.out.println("IO异常");
		}
		finally {
			//finally代码块一定会执行,不管是否发生异常，
			if (inStream!=null) {
				try {
					inStream.close();
				} catch (IOException e) {
					System.out.println("关闭流异常");
				}
			}else {
				System.out.println("iStream为空");
			}
			
		}
	
		System.out.println(properties.getProperty("log4j.rootLogger"));
		//人为的抛出一个异常，用throw
		//throw new ClassNotFoundException();
	}
}
