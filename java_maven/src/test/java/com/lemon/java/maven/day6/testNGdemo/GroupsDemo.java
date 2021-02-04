package com.lemon.java.maven.day6.testNGdemo;

import org.testng.annotations.Test;

//分组测试
public class GroupsDemo {
	@Test(groups = {"g1"},timeOut =1)
	public void test1() {
		for(int i=1;i<=10000;i++)
			System.out.println(i);
	}
	@Test(groups = {"g1"})
	public void test2() {
		System.out.println("GroupsDemo.test2()");
	}
	@Test(groups = {"g1"})
	public void test3() {
		System.out.println("GroupsDemo.test3()");
	}
	@Test
	public void test4() {
		System.out.println("GroupsDemo.test4()");
	}
}
