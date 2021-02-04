package com.lemon.java.maven.day2.xmls;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/*
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
public class XmlUtil {
	public static void main(String[] args) {
		String filepath="src/test/resources/UILibrary.xml";
		Page page =parse(filepath);
		System.out.println(page.getKeyword());
		List<Locator> locators=page.getLocators();
		for(Locator locator:locators) {
		System.out.println(locator);
		}
	}

	private static Page parse(String filepath) {
		//1.获取解析器
		SAXReader reader=new SAXReader();
		Page page =new Page();
		//2.获取docment对象
		try {
			Document document=reader.read(new File(filepath));
			//3.获取根节点
			Element rootElement=document.getRootElement();
			String pageKeyword=rootElement.attributeValue("keyword");//获取根属性值
			page.setKeyword(pageKeyword);
			//4.通过根节点遍历子元素，获取所有子元素<UIElement>
			List<Element> uiElements=rootElement.elements("UIElement");
			//准备集合保护所有的Locator对象
			List<Locator> locators=new ArrayList<Locator>();
			//循环处理所有的子元素<UIElement>
			for(Element uiElement:uiElements) {
				//获取<UIElement>的keyword属性值
				String uiElementKeyword=uiElement.attributeValue("keyword");
				String uiElementBy=uiElement.attributeValue("by");
				String uiElemengValue=uiElement.attributeValue("value");
				//准备Locator对象
				Locator locator=new Locator();/////////////////////////////////////
				//将keyword属性值封装进对象
				locator.setKeyword(uiElementKeyword);
				locator.setKeyword(uiElementBy);
				locator.setKeyword(uiElemengValue);
				//将封装好的Locator保存到集合
				locators.add(locator);
			}
			//设置page对象的locators属性
			page.setLocators(locators);
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return page;
		
	}
}
