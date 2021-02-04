package com.lemon.java.maven.day2.files;

/*
 * IO流
 * 读文件--输入流
 * 写文件--输出流
 * 
 * 字节流：主要掌握FileInputStream，FileOutputStream
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import javax.swing.text.DefaultEditorKit.CopyAction;

public class FileCopy {
	public static void main(String[] args) throws IOException {
		 String fromPath="/Users/qiuyegao/Ye_er/aa/bb/cc.txt";
		 String toPath="/Users/qiuyegao/Ye_er/aa/bb/ccd.txt";
		 InputStream inputStream=new FileInputStream(new File(fromPath));
		 OutputStream outputStream=new FileOutputStream(toPath);
		 int size=0;
		 while((size=inputStream.read())!=-1) {
			 outputStream.write(size);	
			}
		 if(inputStream!=null) {
			 inputStream.close();
		 }
		 if (outputStream!=null) {
			 outputStream.close();
		 }
		 System.out.println("finish");
		}
}

