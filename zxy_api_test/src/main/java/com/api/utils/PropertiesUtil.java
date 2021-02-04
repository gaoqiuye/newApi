package com.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public class PropertiesUtil {
	private static Logger log = Logger.getLogger(PropertiesUtil.class);
	private static FileInputStream fis;
	public static String workPath = System.getProperty("user.dir").replace("\\", "/");
	public static final String PROPERTIES_DICT = "/properties/";

	// 获取properties文件中全部的数据
	public static Map<String, String> getAll(String propertiesName) {
		Properties prop = new Properties();
		Map<String, String> hm = new HashMap<>();
		try {
			File file = new File(workPath + PROPERTIES_DICT + propertiesName);
			if (file.exists()) {
				fis = new FileInputStream(file);
				prop.load(fis);
				Iterator<String> it = prop.stringPropertyNames().iterator();
				while (it.hasNext()) {
					String key = it.next();
					hm.put(key, prop.getProperty(key));
				}
			} else {
				log.error("请检查" + file.getAbsolutePath() + "路径是否正确！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		return hm;
	}

	// 从properties文件中获取指定某些数据
	public static Map<String, String> get(String propertiesName, String... propertyKeys) {
		Properties prop = new Properties();
		Map<String, String> hm = new HashMap<>();
		try {
			File file = new File(workPath + PROPERTIES_DICT + propertiesName);
			if (file.exists()) {
				fis = new FileInputStream(file);
				prop.load(fis);
				for (String key : propertyKeys) {
					hm.put(key, prop.getProperty(key));
				}
			} else {
				log.error("请检查" + propertiesName + "文件是否存在及存放位置是否在properties目录下！");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		return hm;
	}

	// 新增或者修改properties文件的某一条数据
	public static void update(String propertiesName, String key, String value) {
		FileOutputStream fos = null;
		try {
			File file = new File(workPath + PROPERTIES_DICT + propertiesName);
			if (file.exists()) {
				Map<String, String> oldHeadsMap = getAll(propertiesName);
				Properties prop = new Properties();
				for (String mapKey : oldHeadsMap.keySet()) {
					prop.setProperty(mapKey, oldHeadsMap.get(mapKey));
				}
				prop.setProperty(key, value);
				fos = new FileOutputStream(file);
				// 将Properties中的属性列表（键和元素对）写入输出流
				prop.store(fos, "");
			} else {
				log.error("请检查test.properties文件是否存在及存放位置是否在properties目录下！");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	// 移除properties文件中的某一个数据
	public static void remove(String propertiesName, String key) {
		Properties prop = new Properties();
		prop.remove(key);
		FileOutputStream fos = null;
		try {
			File file = new File(workPath + PROPERTIES_DICT + propertiesName);
			if (file.exists()) {
				fos = new FileOutputStream(file);
				// 将Properties中的属性列表（键和元素对）写入输出流
				prop.store(fos, "");
			} else {
				log.error("请检查test.properties文件是否存在及存放位置是否在properties目录下！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
