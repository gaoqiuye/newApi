package com.lemon.java.maven.day6.testNGdemo;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Tester {
	/*
	 * 测试用例
	 * //@Test 相当于一个标签
	 */
	
	@Test  
	public void tester() {
		Calculator calculator=new Calculator();
		int actual=calculator.add(3, 4);
		int expected=7;
		//如果两个值一样，那么就会断言通过
		Assert.assertEquals(actual, expected);
	}
	

}
