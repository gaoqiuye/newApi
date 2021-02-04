package com.lemon.java.maven.day2.xmls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesDemo {
	public static void main(String[] args) throws IOException {
		String filepath ="src/test/resources/log4j.properties";
		loadProperties(filepath);
	}

	private static void loadProperties(String filepath) throws IOException {
		Properties properties =new Properties();
		InputStream iStream=new FileInputStream(new File(filepath));
		properties.load(iStream);
		properties.getProperty("log4j.appender.file");
		
	}

}
