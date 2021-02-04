package com.lemon.java.maven.day2.files;

import java.io.File;
import java.io.IOException;

public class FileDemo {
	public static void main(String[] args) throws IOException {
		//判断目录是否存在
		String path1="/Users/qiuyegao/Ye_er/learning/柠檬班Java自动化_解压缩/2.Java基础";
		//或者String path2=”\\Users\\qiuyegao“
		File file=new File(path1);
		System.out.println(file.exists());
		
		//创建文件夹
		String path2="/Users/qiuyegao/Ye_er/aa/bb";
		File dir=new File(path2);
		dir.mkdirs();
		System.out.println("创建目录");
	 
		
		//创建文件,目录要都存在
		String path3="/Users/qiuyegao/Ye_er/aa/bb/cc.txt";
		File file3=new File(path3);
		file3.createNewFile();
		System.out.println("创建文件");
		
		//判断一个路径是否是目录,还是文件,true====目录，false====文件
		String path4="/Users/qiuyegao/Ye_er/aa/bb/cc.txt";
		File file4=new File(path4);
		System.out.println(file4.isDirectory());
		
		
		
	
		
	}
}
