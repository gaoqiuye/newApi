package com.lemon.java.maven.day3.reflect;

public class Student {
	private String name;
	private String age;
	private String gender;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	 public Student() {
		 System.out.println("默认的构造方法");
	}
	 public Student(String name,String age,String gender) {
		 this.name=name;
		 this.age=age;
		 this.gender=gender;
		 
	}
		

	@Override
	public String toString() {
		 
		return "name="+this.name+",age="+this.age+",gender="+this.gender;
	}
	
	
	
}
