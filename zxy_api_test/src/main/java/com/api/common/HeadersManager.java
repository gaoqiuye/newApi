package com.api.common;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.api.utils.PropertiesUtil;

public class HeadersManager {
	private static Logger log = Logger.getLogger(HeadersManager.class);
//	public static String workPath = System.getProperty("user.dir").replace("\\", "/");

//	存储自定义的headers至headers.properties配置文件
	public static void saveHeadersToFile(String useHeaders) {
		if (useHeaders != "空") {
			String[] headerStrArr = useHeaders.split("\n");
			for (String keyValueStr : headerStrArr) {
				if (keyValueStr.contains(":") && !keyValueStr.toLowerCase().contains("cookie")) {
					String[] tempArr = keyValueStr.split(":");
					if (tempArr.length >= 2 && !tempArr[1].contains("{{")) {
						if (tempArr[1].lastIndexOf(";") == (tempArr[1].length() - 1)) {
							PropertiesUtil.update("headers.properties", tempArr[0],
									keyValueStr.substring(keyValueStr.indexOf(":") + 1, keyValueStr.length() - 1));
						} else {
							PropertiesUtil.update("headers.properties", tempArr[0],
									keyValueStr.substring(keyValueStr.indexOf(":") + 1));
						}
					} else if (tempArr.length == 1) {
						PropertiesUtil.update("headers.properties", tempArr[0], "");
					}
				}
			}
		}
	}

//	根据请求response，存储headers至headers.properties配置文件，例如access_token	
	public static void saveHeadersToFile(String useHeaders, Map<String, Object> saveDataMap) {
		if (useHeaders != "空" && useHeaders.contains("{{")) {
			String[] headerStrArr = useHeaders.replace(";", "").split("\n");
			for (String keyValueStr : headerStrArr) {
				if (keyValueStr.contains("{{")) {
					String[] tempArr = keyValueStr.split(":");
					String strTemp = tempArr[1].substring((tempArr[1].indexOf("{{") + 2), tempArr[1].indexOf("}}"));
					if (saveDataMap.containsKey(strTemp)) {
						strTemp = tempArr[1].replace(("{{" + strTemp + "}}"), saveDataMap.get(strTemp).toString());
						PropertiesUtil.update("headers.properties", tempArr[0], strTemp);
					} else {
						log.error("保存【" + keyValueStr + "】header失败，请检查之前的接口是否保存了【" + strTemp + "】对应的值！");
					}
				}
			}
		}
	}

	public static void saveCookiesToFile(HttpURLConnection connection) {
		// 先获取之前保存的cookie，记录下来存到map集合
		Map<String, String> tempCookiesMap = readFileCookiesToMap();

		Map<String, List<String>> map = connection.getHeaderFields();
		if (map.containsKey("Set-Cookie")) {
			List<String> setCookieslist = map.get("Set-Cookie");
			for (String cookies : setCookieslist) {
				String[] cookieArr = cookies.split(";");
				for (int i = 0; i < cookieArr.length; i++) {
					String[] keyValueArr = cookieArr[i].trim().split("=");
					if (keyValueArr.length == 2) {
						tempCookiesMap.put(keyValueArr[0], keyValueArr[1]);
					} else {
						tempCookiesMap.put(keyValueArr[0], "");
					}
				}
			}
		}
		StringBuilder bStr = new StringBuilder();
		for (String key : tempCookiesMap.keySet()) {
			if (!tempCookiesMap.get(key).equals("")) {
				bStr.append(key + "=" + tempCookiesMap.get(key) + ";");
			} else {
				bStr.append(key + ";");
			}
		}
		PropertiesUtil.update("cookies.properties", "Cookie", bStr.toString());
	}

	public static HttpURLConnection useCookies(HttpURLConnection connection, String cookieKeys) {
		if (!cookieKeys.equals("空")) {
			Map<String, String> cookiesMap = readFileCookiesToMap();
			String[] keyArr = cookieKeys.replace(" ", "").split(";");
			for (String key : keyArr) {
				if (cookiesMap.containsKey(key)) {
					connection.addRequestProperty("Cookie", key + "=" + cookiesMap.get(key).toString());
					log.debug("正在添加cookie：" + key + "=" + cookiesMap.get(key).toString());
				} else {
					log.error("cookie没有保存" + key + "对应的的值！");
				}
			}
		}
		return connection;
	}

	public static Map<String, String> readFileCookiesToMap() {
		Map<String, String> cookiesFileMap = PropertiesUtil.get("cookies.properties", "Cookie");
		Map<String, String> cookiesMap = new HashMap<>();
		String[] cookiesArr = cookiesFileMap.get("Cookie").split(";");
		for (int i = 0; i < cookiesArr.length; i++) {
			String[] cookieKeyValueArr = cookiesArr[i].trim().split("=");
			if (cookieKeyValueArr.length <= 1) {
				cookiesMap.put(cookieKeyValueArr[0], "");
			} else {
				cookiesMap.put(cookieKeyValueArr[0], cookieKeyValueArr[1]);
			}
		}
		return cookiesMap;
	}

	public static void main(String[] args) {
		Map<String, String> mm = readFileCookiesToMap();
		for (String key : mm.keySet()) {
			System.out.println(key + "  =  " + mm.get(key));
		}
	}
}
