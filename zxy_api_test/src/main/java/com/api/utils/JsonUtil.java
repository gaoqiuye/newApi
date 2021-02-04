package com.api.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil {
	private Map<String, Object> defaultDateMap = new HashMap<String, Object>();
	private List<Object> defaultDataList = new ArrayList<Object>();
	private static Logger log = Logger.getLogger(JsonUtil.class);

	// 格式化response字符串输出
	public static String formatResponseStr(String jsonStr) {
		String formatJsonStr = "";
		if (jsonStr.startsWith("{")) {
			JSONObject jObj = JSONObject.parseObject(jsonStr);
			formatJsonStr = JSON.toJSONString(jObj, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteDateUseDateFormat);
		} else if (jsonStr.startsWith("[")) {
			JSONArray jArr = JSONArray.parseArray(jsonStr);
			formatJsonStr = JSON.toJSONString(jArr, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteDateUseDateFormat);
		} else {
			return jsonStr;
		}
		return formatJsonStr;
	}

	public Map<String, Object> saveJsonDatas(Map<String, Object> saveDataMap, String responseData,
			String needSaveDatas) {
		// 为了保证转换后的顺序保持不变，使用1.1.32版本fastJson
		// 先将null值替换为"88888888",防止将null属性的对象转换为json时自动屏蔽掉
		if (responseData.contains("null")) {
			responseData = responseData.replace("null", "88888888");
		}
		String[] saveDatasArr = needSaveDatas.replace(";", "").split("\n");
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < saveDatasArr.length; i++) {
			// 相对路径方法：匹配jsonpath下的值
			if (saveDatasArr[i].contains("==") && saveDatasArr[i].contains("?")) {
				String str = saveDatasArr[i].trim();
				String[] tempArr = str.split("\\?");
				String expectJsonPath = "newTemp=" + tempArr[0].substring(0, tempArr[0].indexOf("=="));
				String expectValue = tempArr[0].substring(tempArr[0].indexOf("==") + 2);
				if (expectValue.startsWith("{{")) {
					expectValue = saveDataMap.get(expectValue.replace("{{", "").replace("}}", "")).toString();
				}
				String bleanIsJsonArrayPath = expectJsonPath.substring(expectJsonPath.indexOf("{{") + 2,
						expectJsonPath.indexOf("["));
				String parameter = tempArr[1];
				String tempJsonArrayStr = "";
				if (bleanIsJsonArrayPath.equals("")) {
					tempJsonArrayStr = responseData;
				} else {
					tempJsonArrayStr = handleJsonData(responseData,
							expectJsonPath.substring(0, expectJsonPath.indexOf("[") - 1) + "}}").get("newTemp")
									.toString();
				}
				JSONArray jsonArray = JSONArray.parseArray(tempJsonArrayStr);
				for (int j = 0; j < jsonArray.size(); j++) {
					if (j == 0) {
						expectJsonPath = expectJsonPath.replace("*", j + "");
					} else {
						expectJsonPath = expectJsonPath.replace((j - 1) + "", j + "");
					}
					String realStr = handleJsonData(responseData, expectJsonPath).get("newTemp").toString();
//					log.info("realStr:::" + realStr);
					if (realStr.contains(expectValue)) {
						parameter = parameter.replace("*", j + "");
						map.putAll(handleJsonData(responseData, parameter));
						break;
					}
				}
			} else if (saveDatasArr[i].contains("[all]")) {
				// 一次性取jsonPath下所有相同id对应的value，且以，隔开
				String key = saveDatasArr[i].substring(0, saveDatasArr[i].indexOf("="));
				String tempPath = saveDatasArr[i].substring(saveDatasArr[i].indexOf("=") + 1,
						saveDatasArr[i].indexOf("[all]") - 1) + "}}";
				String tempResponseJsonStr = responseData;
				if (tempPath != "") {
					tempResponseJsonStr = handleJsonData(responseData, key + "=" + tempPath).get(key).toString();
				}
				JSONArray jr = JSONArray.parseArray(tempResponseJsonStr);
				String allSameKeyToValue = "";
				for (int j = 0; j < jr.size(); j++) {
					tempPath = saveDatasArr[i].replace("[all]", "[" + j + "]");
					allSameKeyToValue += handleJsonData(responseData, tempPath).get(key).toString() + ",";
				}
				map.put(key, allSameKeyToValue.substring(0, allSameKeyToValue.length() - 1));
			} else {
				map.putAll(handleJsonData(responseData, saveDatasArr[i]));
			}
		}
		return map;
	}

	// 将response中想要保存的参数和值，保存起来
	private Map<String, Object> handleJsonData(String responseData, String needSaveData) {
		// 处理响应数据，将空格和换行符去掉，否则在保存接口数据时，会将接口空格、换行符一起保存进来
		// ------------------------------------------------------
		responseData = responseData.trim().replace("\r\n", "");
		Map<String, Object> needSaveDataMap = new HashMap<>();
		if (responseData.startsWith("{") || responseData.startsWith("[")) {
			Map<String, Object> defaultSaveDataMap = responseDataToMap(responseData);
			String temp = responseData;
			if (needSaveData.contains("{{")) {
				String[] keyAndValueArr1 = needSaveData.split("=");
				// 拿到jsonPath，过滤掉replace
				String jsonPath = keyAndValueArr1[1].substring(keyAndValueArr1[1].indexOf("{{") + 2,
						keyAndValueArr1[1].indexOf("}}"));
				if (jsonPath.toLowerCase().equals("response")) {
					needSaveDataMap.put(keyAndValueArr1[0], responseData);
				} else {
					String[] pathArr = jsonPath.split("\\.");
					for (String jsonKey : pathArr) {
						if (jsonKey.startsWith("[")) {
							// replace("\n", "")处理部分返回数据存在换行符
							temp = handleJsonArrayStr(temp, jsonKey).toString().replace("\n", "");
						} else {
							temp = handleJsonObjectStr(temp, jsonKey).toString().replace("\n", "");
						}
					}
				}
				// 将\"xxxxx\"还原为null值
				temp = temp.replace("88888888", "null");
				// 移除已保存data中不想要的key和对应的value
				if (needSaveData.contains("remove")) {
					String[] removeKeys = needSaveData
							.substring(needSaveData.indexOf("remove(") + 7, needSaveData.indexOf(")")).split(",");
					// 筛选出需要删除的字符串，且将\"xxxxx\"还原为null
					ArrayList<String> deleteList = new ArrayList<>();
					for (Object saveListStr : defaultDataList) {
						for (String removeKey : removeKeys) {
							if (saveListStr.toString().startsWith(removeKey)) {
								deleteList.add(
										saveListStr.toString().replace("88888888", "null").replace("88888888", "null"));
							}
						}
					}
//					log.info("需要删除的集合:" + deleteList);
					for (String deleteListStr : deleteList) {
						String needDeleteFirstStr = "{" + deleteListStr.toString() + "},";
						String needDeleteLastStr = ",{" + deleteListStr.toString() + "}";
						if (temp.contains(needDeleteFirstStr)) {
							// 删除开头的数据
							temp = temp.replace(needDeleteFirstStr, "");
//							log.info("删除开头数据:" + needDeleteFirstStr);
						} else if (temp.contains(needDeleteLastStr)) {
							// 删除结尾的数据
							temp = temp.replace(needDeleteLastStr, "");
//							log.info("删除结尾数据:" + needDeleteLastStr);
						} else {
							// 删除默认的数据
							temp = temp.replace((deleteListStr + ","), "");
//							log.info("删除默认数据:" + needDeleteLastStr);
						}
					}
				}
				// 替换已保存data中需要replace的数据
				if (needSaveData.contains("replace")) {
					needSaveData = needSaveData.substring(needSaveData.indexOf("(") + 1, needSaveData.indexOf(")"));
					if (needSaveData.endsWith(">")) {
						temp = temp.replace(needSaveData.substring(0, needSaveData.indexOf(">")), "");
					} else {
						String[] strArr = needSaveData.split(">");
						temp = temp.replace(strArr[0], strArr[1]);
					}
				}
				needSaveDataMap.put(keyAndValueArr1[0], temp);
			} else if (needSaveData.contains("=") && !needSaveData.contains("{{") && !needSaveData.contains("}}")) {
				String[] keyAndValueArr2 = needSaveData.split("=");
				if (keyAndValueArr2[1].toLowerCase().startsWith("random")) {
					Random r = new Random();
					int randomInt = r
							.nextInt(Integer.parseInt(keyAndValueArr2[1].substring(keyAndValueArr2[1].indexOf("(") + 1),
									keyAndValueArr2[1].indexOf(")")));
					needSaveDataMap.put(keyAndValueArr2[0], randomInt + "");
				} else {
					needSaveDataMap.put(keyAndValueArr2[0], keyAndValueArr2[1]);
				}
			} else {
				if (defaultSaveDataMap.containsKey(needSaveData)) {
					needSaveDataMap.put(needSaveData, defaultSaveDataMap.get(needSaveData));
				} else {
					log.error("接口返回的消息不存在【" + needSaveData + "】!");
				}
			}
		} else {
			needSaveDataMap.put("notJsonResponse", responseData);
		}
		return needSaveDataMap;
	}

	private Object handleJsonObjectStr(String jsonObjectStr, String jsonKey) {
		Object ob = "";
		JSONObject obJson = null;
		try {
			obJson = JSONObject.parseObject(jsonObjectStr);
			if (obJson.containsKey(jsonKey)) {
				ob = obJson.get(jsonKey) + "";
			} else {
				log.error("接口返回的数据不存在：【" + jsonKey + "】！");
			}
		} catch (Exception e) {
			log.error("【" + jsonObjectStr + "】字符串转换jsonObject格式异常！");
		}
		obJson = JSONObject.parseObject(jsonObjectStr);
		return ob;
	}

	private Object handleJsonArrayStr(String JsonArrayStr, String jsonArrayIndex) {
		String ob = "";
		JSONArray arrJson = null;
		try {
			arrJson = JSONArray.parseArray(JsonArrayStr);
			if (jsonArrayIndex.toLowerCase().contains("last")) {
				ob = arrJson.get(arrJson.size() - 1).toString() + "";
			} else {
				int index = Integer.parseInt(jsonArrayIndex.substring(1, jsonArrayIndex.indexOf("]")));
				if (index <= (arrJson.size() - 1)) {
					ob = arrJson.get(index).toString();
				} else {
					log.error("输入的索引，超过了【" + arrJson + "】的边界值!");
				}
			}
		} catch (Exception e) {
			log.error("【" + JsonArrayStr + "】字符串转换jsonArray格式异常！");
		}
		return ob;
	}

	// 处理jsonObject\jsonArray类型字符串所有组合对象都存储到map集合
	public Map<String, Object> responseDataToMap(String responseData) {
		if (responseData.startsWith("{") && responseData.endsWith("}")) {
			try {
				JSONObject jsObject = JSONObject.parseObject(responseData);
				defaultDateMap.putAll(jsonObjectToMap(jsObject));
			} catch (Exception e) {
				log.error("以下数据转换jsonObject异常：\n" + responseData);
			}
		} else if (responseData.startsWith("[") && responseData.endsWith("]")) {
			try {
				JSONArray jsArray = JSONArray.parseArray(responseData);
				defaultDateMap.putAll(jsonArrayToMap(jsArray));
			} catch (Exception e) {
				log.error("以下数据转换jsonArray异常：\n" + responseData);
			}
		}
		return defaultDateMap;
	}

	// jsonArray转map集合
	private Map<String, Object> jsonArrayToMap(JSONArray jsonArray) {
		for (Object o : jsonArray) {
			if (o instanceof JSONObject) {
				o = jsonObjectToMap((JSONObject) o);
			}
		}
		return defaultDateMap;
	}

	// jsonObject转map集合,并将json各个元素添加到list集合中
	private Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
		for (String key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);
			defaultDateMap.put(key, value + "");
			String listMem = "\"" + key + "\"" + ":" + value;
			if (!defaultDataList.contains(listMem)) {
				defaultDataList.add(listMem);
			}
			if (value instanceof JSONObject) {
				value = jsonObjectToMap((JSONObject) value);
			} else if (value instanceof JSONArray) {
				jsonArrayToList((JSONArray) value);
			} else {
//				defaultDateMap.put(key, value + "");
			}
		}
		return defaultDateMap;
	}

	// jsonArray转list集合
	private List<Object> jsonArrayToList(JSONArray jsonArray) {
		for (Object object : jsonArray) {
			if (object instanceof JSONArray) {
				jsonArrayToList((JSONArray) object);
			} else if (object instanceof JSONObject) {
				jsonObjectToMap((JSONObject) object);
			}
//			defaultDataList.add(object);
		}
		return defaultDataList;
	}

	public static void main(String[] args) {
		String s = "{\"items\":[{\"categoryName\":\"000001\",\"goodsCount\":1,\"id\":\"83393402-83a8-407c-a665-90d336145c87\",\"sort\":1},{\"categoryName\":\"类别2\",\"goodsCount\":5,\"id\":\"7d2848e9-f32d-4aa4-8fd4-b544dd12c998\",\"sort\":2},{\"categoryName\":\"类别1111\",\"goodsCount\":26,\"id\":\"1078aef9-b619-4a32-af5b-4a61a124d5d8\",\"sort\":3},{\"categoryName\":\"笔\",\"goodsCount\":8,\"id\":\"1360f155-0690-49d5-be80-6d2014a9ffff\",\"sort\":4},{\"categoryName\":\"类别3\",\"goodsCount\":16,\"id\":\"6bc61af0-acb5-4fb5-9b4b-7f0620968bb7\",\"sort\":5},{\"categoryName\":\"哈哈哈哈哈哈\",\"goodsCount\":3,\"id\":\"e9cb8b4a-75c1-4704-94ea-1120c12bbd2c\",\"sort\":6},{\"categoryName\":\"1111\",\"goodsCount\":0,\"id\":\"067d1a91-1f8f-4607-a0be-613b1d0c20bc\",\"sort\":7},{\"categoryName\":\"132432\",\"goodsCount\":0,\"id\":\"58c7f7c2-ff12-4a5c-a652-a05a36b67116\",\"sort\":8},{\"categoryName\":\"112901\",\"goodsCount\":0,\"id\":\"4f600fd9-1682-48e1-941c-7f7ce02e9a3d\",\"sort\":9},{\"categoryName\":\"111111\",\"goodsCount\":0,\"id\":\"4a923062-3b7d-4730-adbd-d2a33d97d6b7\",\"sort\":10},{\"categoryName\":\"null 啊\",\"goodsCount\":0,\"id\":\"e59d5df1-3cd8-4148-adb7-53833bd749ac\",\"sort\":11},{\"categoryName\":\"13224\",\"goodsCount\":0,\"id\":\"87668967-ed3c-4e50-b33b-2ae685e32c1a\",\"sort\":12},{\"categoryName\":\"测试1129\",\"goodsCount\":0,\"id\":\"9d76acb7-3209-4870-8b96-b14e09c12da2\",\"sort\":13},{\"categoryName\":\"商品类别ga\",\"goodsCount\":2,\"id\":\"f4413890-5db2-4d5c-b724-34df1425d4b3\",\"sort\":14},{\"categoryName\":\"数码产品\",\"goodsCount\":0,\"id\":\"7d8d20a2-914e-47ec-9c21-b734336b12c3\",\"sort\":15}],\"recordCount\":15}";
		Map<String, Object> saveDataMap = new HashMap<>();
		JsonUtil ju = new JsonUtil();
		saveDataMap = ju.saveJsonDatas(saveDataMap, s, "goodCategoryName={{items.[0].categoryName}}");
		System.out.println(saveDataMap);

	}
}
