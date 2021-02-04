package com.lemon.java.maven.day6.testNGdemo;

import org.testng.annotations.Test;
//忽略测试
public class IgnoreDemo {
	@Test(enabled = false)
	public void test1() {
		System.out.println("IgnoreDemo.test1()");
	}

	@Test
	public void test2() {
		System.out.println("IgnoreDemo.test2()");
	}
}
