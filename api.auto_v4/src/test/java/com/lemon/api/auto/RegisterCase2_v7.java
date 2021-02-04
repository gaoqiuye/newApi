package com.lemon.api.auto;

//case_v3
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
public class RegisterCase2_v7 {
	
	@Test(dataProvider="datas")
	public void test1(String apiIdFromCase,String parameter) {
		String excelPath="src/test/resources/case_v3.xls";
		int []rows= {2,3,4,5,6,7,8,9,10,11,12,13,14}; // 取出“接口信息”表格中的第2-14行
		int[]cells= {1,3,4};  // 取出“接口信息”表格中的第1行apiId,第3列url和第4行apiId,总共取出2列
		String sheetName="接口信息";
		//接口地址
		Object[][] datas=ExcelUtil_v5.datas(excelPath, sheetName,rows, cells);
		
		String url="";
		String type ="";
		for(Object[] objects:datas) {//objects总共就两列apiId，apiId
			String apiIdFromRest=objects[0].toString();  //获取第一列的apiId
			if(apiIdFromCase.equals(apiIdFromCase)) {
				type=objects[1].toString();
				url=objects[2].toString();
				break;
				
			}
			
		}
		//需要参数
 
		Map<String,String> params=(Map<String, String>) JSONObject.parse(parameter);
//		if("post".equalsIgnoreCase(type)) {
//			HttpUtil.doPost(url, params);
//		}else if("get".equalsIgnoreCase(type)) {
//			HttpUtil.doGet(url, params);
//			
//		}
//		
		String result = HttpUtil.doService(url, type, params);
		System.out.println(result);
	
	}
	//数据存放在表格中POI
	//数据提供者
	@DataProvider
	public Object[][] datas(){
		int[]rows= {2,3,4,5,6,7};
		int[]cells= {3,4};
		Object[][]datas= ExcelUtil_v5.datas("src/test/resources/case_v3.xls","用例",rows,cells);//行号，列号范围
		return datas;
	}
	
	//main方法验证
//	public static void main(String[] args) {
//		String parameters="{\"mobilephone\":\"19000000000,\"pword\":\"\"}";
//		RegisterParam registerParam=JSONObject.parseObject(parameters, RegisterParam.class);
//		System.out.println(registerParam);
//		}
	}














