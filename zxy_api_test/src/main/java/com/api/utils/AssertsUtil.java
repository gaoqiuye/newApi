package com.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.testng.Assert;

public class AssertsUtil {
	private Logger log = Logger.getLogger(AssertsUtil.class);
	public static boolean flag;
	public static List<Error> errors = new ArrayList<Error>();

	public boolean compareResult(String url, String body, Map<String, Object> saveResponseParametersMap,
			String assertStr) {
		String message = "";
		if (body == "空") {
			message = "\n" + "请求Url为: " + url + "\n" + "Response消息为: " + "\n"
					+ JsonUtil.formatResponseStr(saveResponseParametersMap.get("response").toString()) + "\n";
		} else {
			message = "\n" + "请求Url为: " + url + "\n" + "请求Body为: " + body + "\n" + "Response消息为: " + "\n"
					+ JsonUtil.formatResponseStr(saveResponseParametersMap.get("response").toString()) + "\n";
		}
		boolean bool = false;
		String[] arrStr = assertStr.replace(";", "").split("\n");
		for (int i = 0; i < arrStr.length; i++) {
			if (arrStr[i].contains("==")) {
				String act = arrStr[i].substring(0, arrStr[i].indexOf("=="));
				String expect = arrStr[i].substring(arrStr[i].indexOf("==") + 2);
				if (saveResponseParametersMap.containsKey(act)) {
					if (expect.startsWith("{{")) {
						String expectKey = expect.replace("{{", "").replace("}}", "");
						if (saveResponseParametersMap.containsKey(expectKey)) {
							expect = saveResponseParametersMap.get(expectKey).toString();
						} else {
							log.error("请检查之前请求中是否保存【" + expect + "】!");
							expect = "";
						}
					}
//					bool = verifyEquals(saveResponseParametersMap.get(act), expect);
					bool = verifyEquals(saveResponseParametersMap.get(act), expect, message);
					if (bool) {
//						log.debug("【" + act + "==" + expect + "】判断结果正确！");
					} else {
						log.error("【" + act + "==" + expect + "】判断结果不正确！");
						break;
					}
				} else {
					log.error("之前的请求中没有保存【" + act + "】参数，无法进行断言!");
					bool = false;
					verifyEquals(bool, true);
					break;
				}
			} else if (arrStr[i].contains("!=")) {
				String act = arrStr[i].substring(0, arrStr[i].indexOf("!="));
				String expect = arrStr[i].substring(arrStr[i].indexOf("!=") + 2);
				if (saveResponseParametersMap.containsKey(act)) {
					if (expect.startsWith("{{")) {
						String expectKey = expect.replace("{{", "").replace("}}", "");
						if (saveResponseParametersMap.containsKey(expectKey)) {
							expect = saveResponseParametersMap.get(expectKey).toString();
						} else {
							log.error("请检查之前请求中是否保存【" + expect + "】!");
							expect = "";
						}
					}
//					bool = verifyNotEquals(saveResponseParametersMap.get(act), expect);
					bool = verifyNotEquals(saveResponseParametersMap.get(act), expect, message);
					if (bool) {
						log.debug("【" + act + "!=" + expect + "】判断结果正确！");
					} else {
						log.error("【" + act + "!=" + expect + "】判断结果不正确！");
						break;
					}
				} else {
					log.error("之前的请求中没有保存【" + act + "】参数及其对应的值，无法进行断言!");
					bool = false;
					verifyEquals(bool, true);
					break;
				}
			} else if (arrStr[i].toLowerCase().contains(".contains.")) {
				String act = arrStr[i].substring(0, arrStr[i].indexOf(".contains."));
				String expect = arrStr[i].substring(arrStr[i].indexOf(".contains.") + 10);
				if (saveResponseParametersMap.containsKey(act)) {
					if (expect.startsWith("{{")) {
						String expectKey = expect.replace("{{", "").replace("}}", "");
						if (saveResponseParametersMap.containsKey(expectKey)) {
							expect = saveResponseParametersMap.get(expectKey).toString();
						} else {
							log.error("请检查之前请求中是否保存【" + expect + "】!");
							expect = "";
						}
					}
					bool = verifyContains(saveResponseParametersMap.get(act), expect);
					if (bool) {
						log.debug("【" + act + ".contains." + expect + "】判断结果正确！");
					} else {
						log.error("【" + act + ".contains." + expect + "】判断结果不正确！");
						break;
					}
				} else {
					log.error("之前的请求中没有保存【" + act + "】参数及其对应的值，无法进行断言!");
					bool = false;
					verifyEquals(bool, true);
					break;
				}
			} else {
				bool = saveResponseParametersMap.get("notJsonResponse").toString().contains(arrStr[i]);
				verifyEquals(bool, true);
				if (bool) {
					log.debug("接口返回的数据与期望的【" + arrStr[i] + "】匹配!");
				} else {
					log.error("接口返回的数据与期望的【" + arrStr[i] + "】不匹配!");
					break;
				}
			}
		}
		return bool;
	}

	private static Boolean verifyEquals(Object actual, Object expected) {
		try {
			Assert.assertEquals(actual, expected);
			flag = true;
		} catch (Error e) {
			errors.add(e);
			flag = false;
		}
		return flag;
	}

	private static Boolean verifyEquals(Object actual, Object expected, String message) {
		try {
			Assert.assertEquals(actual, expected, message);
			flag = true;
		} catch (Error e) {
			errors.add(e);
			flag = false;
		}
		return flag;
	}

	private static Boolean verifyNotEquals(Object actual, Object expected) {
		try {
			Assert.assertNotEquals(actual, expected);
			flag = true;
		} catch (Error e) {
			errors.add(e);
			flag = false;
		}
		return flag;
	}

	private static Boolean verifyNotEquals(Object actual, Object expected, String message) {
		try {
			Assert.assertNotEquals(actual, expected, message);
			flag = true;
		} catch (Error e) {
			errors.add(e);
			flag = false;
		}
		return flag;
	}

	private static Boolean verifyContains(Object actual, Object expected) {
		try {
			Assert.assertTrue(actual.toString().contains(expected.toString()));
			flag = true;
		} catch (Error e) {
			errors.add(e);
			flag = false;
		}
		return flag;
	}
}
