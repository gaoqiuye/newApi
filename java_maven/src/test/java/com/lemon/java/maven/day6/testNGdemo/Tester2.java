package com.lemon.java.maven.day6.testNGdemo;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Tester2 {
	@BeforeSuite
	public void beforeSuite() {
		System.out.println("Tester.beforeSuite");
	}
	
	@BeforeTest
	public void beforeTest() {
		System.out.println("Tester.beforeTest");
	}
	
	@BeforeClass
	public void beforeClass() {
		System.out.println("Tester.beforeClass");
	}
	
	@BeforeMethod
	public void beforeMethod() {
		System.out.println("Tester.beforeMethod");
	}
	
	@Test  
	public void tester() {
		System.out.println("Tester.tester()");
		}
	
	@AfterMethod
	public void afterMethod() {
			System.out.println("Tester.afterMethod()");
		}
	
	@AfterClass
	public void afterClass() {
		System.out.println("Tester.afterClass");
	}

	@AfterTest
	public void AfterTest() {
		System.out.println("Tester.afterTest");
	}
	@AfterSuite
	public void afterSuite() {
		System.out.println("Tester.afterSuite");
	}

}
