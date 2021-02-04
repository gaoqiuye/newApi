package com.lemon.java.maven.day6.testNGdemo;
//依赖测试

import org.testng.annotations.Test;

public class DependencyDemo {
	@Test(dependsOnMethods = {"b"})
	public void a() {
		System.out.println("DependencyDemo.a()");
	}
	@Test
	public void b() {
		System.out.println("DependencyDemo.b()");
	}
	@Test
	public void c() {
		System.out.println("DependencyDemo.c()");
	}
	
}


//@BeforeMethod   也能实现先执行，但是他会在每个方法运行前都会运行一次
//dependsOnMethods 只会在指定前的方法前运行