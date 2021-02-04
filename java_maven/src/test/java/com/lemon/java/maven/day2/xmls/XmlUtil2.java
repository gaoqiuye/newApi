package com.lemon.java.maven.day2.xmls;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/* 采用反射的方法实现
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <Page keyword="注册页">
		<UIElement keyword="用户名" by="id" value="mobilephone"></UIElement>>
		<UIElement keyword="密码" by="id" value="password"></UIElement>>
		<UIElement keyword="重置密码" by="id" value="pwdconfirm"></UIElement>>
		<UIElement keyword="注册按钮" by="id" value="signup-button"></UIElement>>
		<UIElement keyword="错误提示" by="classname" value="tips"></UIElement>>
</Page>

 * 定义一个XMLUtil类，定义一个load方法，要求利用domj4解析技术完成数据解析封装，再返回Page对象；
 * 1.根据xml结果设计Page，此Page对象必须包括keyword属性值和所有子元素<UIElement>信息
 * 2.<UIElement>的所有属性值要求封装在Locator类中，请设计Locator类的属性
 * 
 * 
 */
public class XmlUtil2 {
	public static void main(String[] args) {
		Page page=parse("src/test/resources/UILibrary.xml");
		System.out.println(page.getKeyword());
		List<Locator> locators=page.getLocators();
		for(Locator locator:locators) {
			System.out.println(locator);
		}
	}
	private static Page parse(String filepath) {
		//1.获取解析器
		SAXReader reader=new SAXReader();
		Class<Page> pageclazz=Page.class;
		
		//2.获取docment对象
		try {
			Page page=pageclazz.newInstance();
			Document document=reader.read(new File(filepath));
			//3.获取根节点
			Element rootElement=document.getRootElement();
			//获取<page>元素的keyword属性
			Attribute pageKeywordAttribute=rootElement.attribute(1);
			//获取属性名keyword
			String pageAttributeName=pageKeywordAttribute.getName();
			//获取属性名keyword对应的属性值
			String pageAttributeValue=pageKeywordAttribute.getValue();
			//得到反射的方法--->"keyword"
			//String pageKeyword=rootElement.attributeValue("keyword");//获取根属性值
			//page.setKeyword(pageKeyword);
			//4.通过根节点遍历子元素，获取所有子元素<UIElement>
			List<Element> uiElements=rootElement.elements("UIElement");
			//准备集合保护所有的Locator对象
			List<Locator> locators=new ArrayList<Locator>();
			//循环处理所有的子元素<UIElement>
			
			Class<Locator> clazz=Locator.class;
			for(Element uiElement:uiElements) {
				//把每一个<UIElement>封装为Locator对象
				Locator locator=clazz.newInstance();
				//通过反射获取要调用的方法
			 
				List<Attribute> attributes=uiElement.attributes();
				for (Attribute attribute:attributes) {
					//取出属性的名字
					String attributeName=attribute.getName();
					//取出属性的值
					String attributeValue=attribute.getValue();
					
					//获取首字母并装换为大写
					String firstChar=attributeName.substring(0,1).toUpperCase();
					//获取首字母以外的其他字符
					String remainChars=attributeName.substring(1);
					//通过拼接 得到要反射的方法名
					String methodName="set"+firstChar+remainChars;
					//通过反射得到要调用的方法对象
					Method method=clazz.getMethod(methodName, String.class);
					//通过反射调用方法
					method.invoke(locator, attributeValue);
				
				}
				locators.add(locator);
			}
			//设置page对象的locators属性
			//告诉他locators属性的值要通过“setLocators”方法反射进去
			
			page.setLocators(locators);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
		
	}
}
