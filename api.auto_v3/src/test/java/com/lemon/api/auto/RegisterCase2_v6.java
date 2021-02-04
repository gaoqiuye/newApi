package com.lemon.api.auto;

//case_v2

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
public class RegisterCase2_v6 {
 

	@Test(dataProvider="datas")
	public void test1(String parameter) {
		String url="https://test9.zhixueyun.com/oauth/api/v1/auth";
//		Map<String,String> params=(Map<String, String>) JSONObject.parse(parameter);
//		HttpUtil.doPost(url, params);
		
	}
	//数据存放在表格中POI
	//数据提供者
	@DataProvider
	public Object[][] datas(){
		int[]rows= {2,3,4,5,6,7};
		int[]cells= {6};
		Object[][]datas= ExcelUtil_v5.datas("src/test/resources/case_v2.xls","用例",rows,cells);//行号，列号范围
		return datas;
	}
	public static void main(String[] args) {
		String parameters="{\"mobilephone\":\"19000000000,\"pword\":\"\"}";
		RegisterParam registerParam=JSONObject.parseObject(parameters, RegisterParam.class);
		System.out.println(registerParam);
		}
	}














