package com.api.utils;

import java.io.File;
import org.apache.log4j.Logger;

public class FileUtil {
	private static Logger log = Logger.getLogger(FileUtil.class);
	public static String workPath = System.getProperty("user.dir").replace("\\", "/");

	public static void checkParentFile(String filePath) {
		File file = new File(filePath);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
	}

//	获取上传文件路径
	public static File getUploadFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile()) {
			return file;
		} else {
			filePath = workPath + "/uploadFiles/" + filePath;
			File newFile = new File(filePath);
			if (newFile.exists()) {
				return newFile;
			} else {
				log.error("请检查文件路【" + filePath + "】径是否正确！");
				return null;
			}
		}
	}

	public static Long getFileSize(String filePath) {
		File file = getUploadFile(filePath);
		long fileSize = file.length();
		return fileSize;
	}
	
	public static void main(String[] args) {
		File file =getUploadFile("0022-品茶礼仪.mp4");
		System.out.println(file.getAbsolutePath());
	}
}
