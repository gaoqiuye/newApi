package com.lemon.java.maven.day6.testNGdemo;

import org.testng.annotations.Test;

//超时测试
public class TimeoutDemo {
	@Test(timeOut = 1)
	public void test() {
		for(int i=1;i<=10000;i++)
			System.out.println(i);
	}
}
