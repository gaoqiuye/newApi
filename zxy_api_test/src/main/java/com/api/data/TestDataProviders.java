package com.api.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.DataProvider;
import com.api.utils.ExcleUtil;
import com.api.utils.PropertiesUtil;

public class TestDataProviders {
//	 是否去掉不执行的测试用例开关，true排除，false不排除
	private static final boolean FALG = true;
	private final static String BASE_URL = PropertiesUtil.get("test.properties", "baseUrl").get("baseUrl");

//	 获取excle表格中的数据，返回的是Object一个二维数组
	@DataProvider(name = "apiTestDataProvider")
	public static Object[][] apiTestDataProvider() {
		String totalExcleName = PropertiesUtil.get("test.properties", "totalExcleName").get("totalExcleName");
		String totalSheetName = PropertiesUtil.get("test.properties", "totalSheetName").get("totalSheetName");
		Object[][] ob = ExcleUtil.getExclesData(totalExcleName, totalSheetName);
		if (FALG) {
			ob = deleteNoTestCase(ob);
		}
		return ob;
	}

	private static Object[][] deleteNoTestCase(Object[][] obj) {
		List<Object[]> list = new ArrayList<>();
		for (int i = 0; i < obj.length; i++) {
			for (int j = 0; j < obj[i].length; j++) {
				if (obj[i][j].toString().equalsIgnoreCase("Y")) {
					String newApiUrl = obj[i][2].toString();
					// 统一处理URL域名
					if (newApiUrl.startsWith("https")) {
						if (newApiUrl.contains("zhixueyun.com")) {
							newApiUrl = BASE_URL + newApiUrl.substring(newApiUrl.indexOf(".com") + 4);
						}
					} else if (!newApiUrl.startsWith("{{")) {
						if (newApiUrl.startsWith("/")) {
							newApiUrl = BASE_URL + newApiUrl;
						} else {
							newApiUrl = BASE_URL + "/" + newApiUrl;
						}
					}
					obj[i][2] = newApiUrl;
					list.add(obj[i]);
				}
			}
		}
		Object[][] newObject = new Object[list.size()][];
		for (int i = 0; i < newObject.length; i++) {
			newObject[i] = list.get(i);
		}
		return newObject;
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString(apiTestDataProvider()[0]));
	}
}
