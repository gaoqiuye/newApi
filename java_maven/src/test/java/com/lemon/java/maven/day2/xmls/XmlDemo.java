package com.lemon.java.maven.day2.xmls;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/*
 * html  展示数据
 * xml   保存数据  extensible markup language 可扩展性标记语言
 */
public class XmlDemo {
	public static void main(String[] args) throws DocumentException {
		String path ="src/test/resources/student.xml";
		parseXml(path);
	}

	private static void parseXml(String path) throws DocumentException {
		// 创建解析器saxReader对象
		SAXReader reader=new SAXReader();
		//获取document对象
		Document document=reader.read(new File(path));
		//获取根元素
		Element root=document.getRootElement();
		//获取根元素下的子元素
		List<Element> studentElements=root.elements("student");
		int i=0;
		//通过循环处理三个student元素
		for(Element studentElement:studentElements) {
			i++;
			System.out.println("处理第【"+i+"】个student元素");
			Element nameElement=studentElement.element("name");
			Element ageElement=studentElement.element("age");
			Element genderElement=studentElement.element("gender");
			String name=nameElement.getText();
			String age=ageElement.getText();
			String gender=genderElement.getText();
			//String name=nameElement.getData().toString();
			//String name=studentElement.elementText("name");
			System.out.println("name="+name+",age="+age+",gender="+gender);
		}
	}
	

}
