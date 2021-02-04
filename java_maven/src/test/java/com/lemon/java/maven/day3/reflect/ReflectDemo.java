package com.lemon.java.maven.day3.reflect;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ReflectDemo {
	public static void main(String[] args) throws Exception {
		String path="src/test/resources/student.xml";
		List<Student> students= parseXml(path);
		for(Student student:students) {
			System.out.println(student);
		}
		//获取类的字节码
		//通过字节码去创建对象
		//反射得到要调用的方法对象method
		//通过反射调用方法
	}

	private static List<Student> parseXml(String path) throws  Exception  {
		// 创建解析器saxReader对象
		SAXReader reader=new SAXReader();
		//获取document对象
		Document document=reader.read(new File(path));
		//获取根元素
		Element root=document.getRootElement();
		//获取根元素下的子元素
		List<Element> studentElements=root.elements("student");
		List<Student> students =new ArrayList<Student>();
		//获取类的字节码
		Class<Student> clazz=Student.class;
		//通过循环处理三个student元素
		for(Element studentElement:studentElements) {
			//获取类的字节码
			//通过字节码去创建对象
			//反射得到要调用的方法对象method
			//通过反射调用方法
//			String name=studentElement.elementText("name");
//			String age=studentElement.elementText("age");
//			String gender=studentElement.elementText("gender");
			List<Element> elements=studentElement.elements();
			//反射得到对象
			Student student=clazz.newInstance();
			//完成三个子元素的解析，并将数据封装在student对象中
			for(Element element:elements) {
				//获取元素的标签名
				String elementName=element.getName(); 
				//通过拼接得到对应的set方法名
				String methonName="set"+elementName;
				//传入方法名和参数类型，反射得到方法对象
				Method method=clazz.getMethod(methonName,String.class);
				//反射调用方法（第一个参数为对象，第二个为实参-有就传，没有就不穿）
				method.invoke(student, element.getText());
			}
			
			students.add(student);
		}
		return students;
	}
	
}
