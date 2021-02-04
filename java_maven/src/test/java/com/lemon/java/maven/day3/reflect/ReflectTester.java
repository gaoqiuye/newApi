package com.lemon.java.maven.day3.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectTester {
	public static void main(String[] args) throws  Exception {
		//获取一个类的字节码 
		//1-Test.class;
		Class<Student> clazz=Student.class;
		//2-class.forName("xx.Test")
		//Class<Student>  clazz1=(Class<Student>) Class.forName("com.lemon.java.maven.reflect.Student");
		//3.通过字节码调用newInstance方法创建对象，底层其实调用的是字节码对应类中默认构造函数
		Student student =clazz.newInstance();	
		//4.通过反射获取要调用的方法
		//Method[] methods=clazz.getMethods();
//		for(Method method:methods) {
//			System.out.println(method.getName());
//			//System.out.println(method.getParameterTypes());
//			System.out.println(method.getReturnType());
//		}
		Method setNmaeMethod=clazz.getMethod("setName", String.class);
		//5.通过反射完成方法调用
		setNmaeMethod.invoke(student, "张三111");
		//6.通过getName取出来数据
		Method getNameMethod=clazz.getMethod("getName");
		Object value=getNameMethod.invoke(student);
		System.out.println("name="+value.toString());
	}
	
}


