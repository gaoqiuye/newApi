package com.lemon.java.maven.day6.testNGdemo;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestngParameterDemo {
/*
 * 此方法能够根据浏览器类型来
 */
	@Test
	@Parameters(value = {"browserType","driverPath"})
	public void test(String browserType,String driverPath) {
		System.out.println("browserType="+browserType+",driverPath="+driverPath);
	}
}
