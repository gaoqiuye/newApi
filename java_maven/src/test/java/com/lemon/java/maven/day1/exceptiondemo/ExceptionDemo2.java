package com.lemon.java.maven.day1.exceptiondemo;

import java.util.ArrayList;
import java.util.List;

/*
 * 运行时异常
 */
public class ExceptionDemo2 {
	public static void main(String[] args) {
		//IndexOutOfBoundException==索引越界异常
		List<String> names=new ArrayList<String>();
		names.add("小红");
		names.add("小名");
		System.out.println(names.get(3));
	}

}
