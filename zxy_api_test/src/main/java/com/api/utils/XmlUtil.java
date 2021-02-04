package com.api.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XmlUtil {
	static Map<String, Object> map = new HashMap<>();

	public static void main(String[] args) throws DocumentException {
		String soap = "<?xml version=\"1.0\"?>\r\n" + "<apps>\r\n" + "  <app>\r\n" + "    <id>1</id>\r\n"
				+ "    <name>Google Maps</name>\r\n" + "    <version>1.0</version>\r\n" + "  </app>\r\n" + "  <app>\r\n"
				+ "    <id>2</id>\r\n" + "    <name>Chrome</name>\r\n" + "    <version>2.1</version>\r\n"
				+ "  </app>\r\n" + "  <app>\r\n" + "    <id>3</id>\r\n" + "    <name>Google Play</name>\r\n"
				+ "    <version>2.3</version>\r\n" + "  </app>\r\n" + "</apps>";
		System.out.println(soap);
		soap = soap.replaceAll("\r\n", "");
		Document doc = DocumentHelper.parseText(soap);
		Element root = doc.getRootElement();// 获取根元素，准备递归解析这个XML树
		getCode(root);
		System.out.println(map);
	}

	public static void getCode(Element root) {
		if (root.elements() != null) {
			List<Element> list = root.elements();// 如果当前跟节点有子节点，找到子节点
			for (Element e : list) {// 遍历每个节点
				if (e.elements().size() > 0) {
					getCode(e);// 当前节点不为空的话，递归遍历子节点；
				}
				if (e.elements().size() == 0) {
					map.put(e.getName(), e.getTextTrim());
				} // 如果为叶子节点，那么直接把名字和值放入map
			}
		}
	}
}
