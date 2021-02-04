package com.lemon.api.auto;

//case_v1

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
//区别于4：这个取的是表格中不连续的行数
/*
 * 完成注册接口的测试
 * 
 */
public class RegisterCase2_v5 {
	/*mobilephone:"19111111111",pword:""
	 * mobilephone:"",pword:"123456"
	 * mobilephone:"19111111111",pword:"123456"
	 * 
	 * 
	 */

	@Test(dataProvider="datas")
	public void test1(String mobilephone,String pword) {
		String url="https://test9.zhixueyun.com/oauth/api/v1/auth";
		Map<String, String> params=new HashMap<String, String>();
		params.put("mobilephone", mobilephone);
		params.put("pword", pword);
		HttpUtil.doPost(url, params);
	}
	
	//数据存放在表格中POI
	@DataProvider
	public Object[][] datas(){
		int[]rows= {2,3,4,5,6,7};
		int[]cells= {6,7};
		Object[][]datas= ExcelUtil_v5.datas("src/test/resources/case_v1.xls","用例",rows,cells);//行号，列号范围
		return datas;
	}
}













