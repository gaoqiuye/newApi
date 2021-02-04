package com.lemon.java.maven.day6.testNGdemo;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DataProviderDemo {
	//@Test(dataProvider="dataProvider2")
	@Test(dataProvider="aa")
	public void test(String name,int age,String gender) {
		System.out.println("name="+name+", age=" +age+",gender="+gender);
	}
	/*
	 * 同时提供多组测试数据
	 */
	@DataProvider(name="aa")
	public Object[][] dataProvider1(){
		Object[][] datas= {
				{"zhangsan",28,"nan"},
				{"hanmeimei",25,"nv"}
				};
		return datas;
	}
	@DataProvider
	public Object[][] dataProvider2(){
		Object[][] datas= {
				{"zhang",28,"nan"},
				{"han",25,"nv"}
				};
		return datas;
	}
	
}
