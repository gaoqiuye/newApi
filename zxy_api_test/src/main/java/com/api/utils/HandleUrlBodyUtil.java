package com.api.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class HandleUrlBodyUtil {
	static Logger log = Logger.getLogger(HandleUrlBodyUtil.class);
	private final static String BASE_URL = PropertiesUtil.get("test.properties", "baseUrl").get("baseUrl");
	// key对应的value中的 "+" 需要encode编码
	private static final String notEncodeStr = "key,organization_id,login_method,username,pword,image,loginType";

	public List<String> getNewUrlAndBody(Map<String, Object> saveOtherParametersMap, String apiUrl, String body) {
		List<String> list = new ArrayList<>();
		if (apiUrl != "空") {
			list.add(handleApiUrl(saveOtherParametersMap, apiUrl));
		} else {
			log.error("apiUrl是必填项！");
		}
		if (body != "空") {
			list.add(handleBody(saveOtherParametersMap, body));
		} else {
			list.add(handleBody(saveOtherParametersMap, "空"));
		}
		return list;
	}

	private String handleApiUrl(Map<String, Object> saveOtherParametersMap, String apiUrl) {
		// 统一处理需要修改和替换的参数
		if (apiUrl.contains("{{") && apiUrl.contains("}}")) {
			Pattern p = Pattern.compile("(?<=\\{\\{)(.+?)(?=\\}\\})");
			Matcher m = p.matcher(apiUrl);
			List<String> listKeys = new ArrayList<>();
			while (m.find()) {
				listKeys.add(m.group());
			}
			for (String key : listKeys) {
				String tempKey = key;
				String[] oldNewStrArr = null;
				if (key.contains("replace(")) {
					tempKey = key.substring(0, key.indexOf("."));
					oldNewStrArr = key.substring(key.indexOf("(") + 1, key.indexOf(")")).split(">");
				}
				if (saveOtherParametersMap.containsKey(tempKey)) {
					String tempValue = "";
					try {
						// URLEncoder必须设置为UTF-8编码格式，不然转译的汉字是乱码
						if (saveOtherParametersMap.get(tempKey).toString().startsWith("http")) {
							// 【接口url】整体为替换参数
							tempValue = saveOtherParametersMap.get(tempKey).toString();
						} else {
							if (key.contains("replace(")) {
								if (oldNewStrArr.length > 1) {
									tempValue = URLEncoder.encode(saveOtherParametersMap.get(tempKey).toString()
											.replace(oldNewStrArr[0], oldNewStrArr[1]), "UTF-8");
								} else {
									tempValue = URLEncoder.encode(
											saveOtherParametersMap.get(tempKey).toString().replace(oldNewStrArr[0], ""),
											"UTF-8");
								}
								// 如果含有/(%2F),即表示为为非参数部分，不进行编译
								if (tempValue.contains("%2F")) {
									tempValue = URLDecoder.decode(tempValue, "UTF-8");
								}
							} else {
								tempValue = URLEncoder.encode(saveOtherParametersMap.get(tempKey).toString(), "UTF-8");
							}
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (tempValue.equals("")) {
						log.error(tempKey + "保存的value是空值，请检查之前接口保存的参数是否正确！");
					}
					apiUrl = apiUrl.replace("{{" + key + "}}", tempValue);
//					log.info(newApiUrl);
				} else if (key.startsWith("time")) {
					apiUrl = apiUrl.replace("{{" + key + "}}", DateUtil.getTime(key));
				} else if (key.equals("formatCurrentTime")) {
					apiUrl = apiUrl.replace("{{" + key + "}}", DateUtil.formatCurrentTime());
				} else if (key.startsWith("formatTime")) {
					apiUrl = apiUrl.replace("{{" + key + "}}", DateUtil.formatTime(key));
				} else if (key.equals("UUID")) {
					apiUrl = apiUrl.replace("{{UUID}}",
							UUID.randomUUID().toString().toLowerCase().replaceAll("-", ""));
				} else {
					log.error("之前的请求中没有保存【" + tempKey + "】参数及其对应的值,无法重新构建请求的Url!");
				}
			}
		}
		return apiUrl.replace("\n", "");
	}

	private String handleBody(Map<String, Object> saveOtherParametersMap, String body) {
		// 统一处理需要修改和替换的参数
		String newBody = body;
		if (body.contains("{{") && body.contains("}}")) {
			Pattern p = Pattern.compile("(?<=\\{\\{)(.+?)(?=\\}\\})");
			Matcher m = p.matcher(body);
			List<String> list = new ArrayList<>();
			while (m.find()) {
				list.add(m.group());
			}
			for (int i = 0; i < list.size(); i++) {
				if (saveOtherParametersMap.containsKey(list.get(i))) {
					if (list.get(i).equals("")) {
						log.error(list.get(i) + "保存的value是空值，请检查之前接口保存的参数是否正确！");
					}
					newBody = newBody.replace("{{" + list.get(i) + "}}",
							saveOtherParametersMap.get(list.get(i)).toString());
				} else if (list.get(i).endsWith("++")) {
					String tempNum = "";
					try {
						tempNum += (Integer
								.parseInt(saveOtherParametersMap.get(list.get(i).replace("+", "")).toString()) + 1);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
					newBody = newBody.replace("{{" + list.get(i) + "}}", tempNum);
				} else if (list.get(i).startsWith("time")) {
					newBody = newBody.replace("{{" + list.get(i) + "}}", DateUtil.getTime(list.get(i)));
				} else if (list.get(i).equals("formatCurrentTime")) {
					newBody = newBody.replace("{{" + list.get(i) + "}}", DateUtil.formatCurrentTime());
				} else if (list.get(i).startsWith("formatTime")) {
					newBody = newBody.replace("{{" + list.get(i) + "}}", DateUtil.formatTime(list.get(i)));
				} else if (list.get(i).toUpperCase().equals("BASE_URL")) {
					newBody = newBody.replace("{{" + list.get(i) + "}}", BASE_URL);
				} else if (list.get(i).equals("UUID")) {
					newBody = newBody.replace("{{UUID}}", UUID.randomUUID().toString());
				} else {
					log.error("之前的请求中没有保存【" + list.get(i) + "】参数及其对应的值，无法重新构建请求的Body!");
				}
			}
		}

		StringBuffer nsb = new StringBuffer();
		if (!newBody.equals("空") && !newBody.startsWith("{") && !newBody.startsWith("[")) {
			String[] parameters = newBody.split("\n");
			for (int i = 0; i < parameters.length; i++) {
				if (parameters[i].contains("=")) {
					String key = parameters[i].substring(0, parameters[i].indexOf("="));
					String value = "";
					// 记录原始的value，判断是否包含$需要加密的东西
					String oldValue = parameters[i].substring(parameters[i].indexOf("=") + 1);
					if (parameters[i].contains(".insert(")) {
						value = parameters[i].substring(parameters[i].indexOf("=") + 1,
								parameters[i].indexOf(".insert("));
					} else {
						value = parameters[i].substring(parameters[i].indexOf("=") + 1);
						if (key.equals("username") || key.equals("pword")) {
							value = AES256EncryptionUtil.aesEncrypt(value);
						}
						if (value.startsWith("$")) {
							value = AES256EncryptionUtil.aesEncrypt(value.substring(1));
						}
					}
					// 在body中的某些value中插入某些数据
					if (parameters[i].contains(".insert(")) {
						String splitStr = parameters[i].substring(parameters[i].indexOf(".insert("));
						String[] insertArrArr = splitStr.split(".insert");
//						log.info("原标准值：：：" + insertArrArr[0]);
//						log.info("需要替换的值：：：" + insertArrArr[1]);
						for (int j = 1; j < insertArrArr.length; j++) {
							String tempValue = "";
							if (insertArrArr[j].contains(">")) {
								// 向后追加字符串
								String[] tempStrArr = insertArrArr[j].replace("(", "").replace(")", "").split(">");
								if (tempStrArr[0].equals("{") || tempStrArr[0].equals("[")) {
									tempStrArr[0] = "\\" + tempStrArr[0];
								}
								String[] valueArr = value.split(tempStrArr[0]);
								tempStrArr[0] = tempStrArr[0].replace("\\", "");
								for (int k = 0; k < valueArr.length; k++) {
									if (k < valueArr.length - 1) {
										tempValue = tempValue + valueArr[k] + tempStrArr[0] + tempStrArr[1];
									} else {
										tempValue = tempValue + valueArr[k];
									}
								}
							} else if (insertArrArr[j].contains("<")) {
								// 向前追加字符串
								String[] tempStrArr = insertArrArr[j].replace("(", "").replace(")", "").split("<");
								String[] valueArr = value.split(tempStrArr[0]);
								for (int k = 0; k < valueArr.length; k++) {
									if (k < valueArr.length - 1) {
										tempValue = tempValue + valueArr[k] + tempStrArr[1] + tempStrArr[0];
									} else {
										if (k == valueArr.length - 1 && value.endsWith(tempStrArr[0])) {
											tempValue = tempValue + valueArr[k] + tempStrArr[1] + tempStrArr[0];
										} else {
											tempValue = tempValue + valueArr[k];
										}
									}
								}
							}
							value = tempValue;
//							JsonUtil ju = new JsonUtil();
//							log.info("\n" + ju.formatResponseStr(value));
//							log.info(value);
						}
					}
					try {
						nsb.append(URLEncoder.encode(key, "UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					nsb.append("=");
					try {
						if (oldValue.startsWith("<?xml")) {
							//xml格式的数据，不需要全部编码
							nsb.append(value);
						} else if (oldValue.startsWith("$")) {
							nsb.append(URLEncoder.encode(value, "UTF-8"));
						} else {
							// 处理value中的 "+" 不需要编码
							if (!notEncodeStr.contains(key) && value.contains("+")) {
								String[] tempArr = value.split("\\+");
								for (int j = 0; j < tempArr.length; j++) {
									nsb.append(URLEncoder.encode(tempArr[j], "UTF-8"));
									if (j < tempArr.length - 1) {
										nsb.append("+");
									}
								}
							} else {
//							 用utf-8编码方式编码，不然会出现乱码
								nsb.append(URLEncoder.encode(value, "UTF-8"));
							}
						}
					} catch (UnsupportedEncodingException e) {
						log.error(e.getMessage());
					}
				} else {
					try {
						nsb.append(URLEncoder.encode(parameters[i], "UTF-8")).append("=");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (i < parameters.length - 1) {
					nsb.append("&");
				}
			}
		} else {
			return body;
		}
		return nsb.toString();
	}

	public static void main(String[] args) {
		HandleUrlBodyUtil hu = new HandleUrlBodyUtil();
		Map<String, Object> map = new HashMap<>();
		String str = "[{\"classConfigId\":\"1_2\",\"code\":\"common_project\",\"createTime\":1514516763362,\"id\":\"1_id_1\",\"isDelete\":1,\"isInit\":1,\"name\":\"日常项目\",\"organizationId\":\"1\",\"sort\":1,\"state\":1},{\"classConfigId\":\"1_2\",\"code\":\"banjileibie\",\"createTime\":1524738878761,\"id\":\"47528c09-0d95-4569-bac9-7e0968cb207b\",\"isDelete\":1,\"isInit\":0,\"name\":\"新增数据\",\"organizationId\":\"1\",\"sort\":2,\"state\":1},{\"classConfigId\":\"1_2\",\"code\":\"import_project\",\"createTime\":1514459025228,\"id\":\"1_id_2\",\"isDelete\":1,\"isInit\":1,\"name\":\"重点项目\",\"organizationId\":\"1\",\"sort\":3,\"state\":1},{\"classConfigId\":\"1_2\",\"code\":\"zhiliangzhongxin\",\"createTime\":1545047926182,\"id\":\"b9f348f9-5669-4333-ab50-6640aed1f66b\",\"isDelete\":1,\"isInit\":0,\"name\":\"质量中心\",\"organizationId\":\"1\",\"sort\":4,\"state\":0},{\"classConfigId\":\"1_2\",\"code\":\"banjileibie1\",\"createTime\":1575964410367,\"id\":\"2da36154-839b-4f10-a059-b32a3e170ac7\",\"isDelete\":1,\"isInit\":0,\"name\":\"新的类别\",\"organizationId\":\"1\",\"sort\":5,\"state\":1},{\"classConfigId\":\"1_2\",\"code\":\"AAA\",\"createTime\":1577694418985,\"id\":\"a3cf3138-f568-407c-9f3e-83bbde936e83\",\"isDelete\":1,\"isInit\":0,\"name\":\"AAA\",\"organizationId\":\"1\",\"sort\":6,\"state\":1},{\"classConfigId\":\"1_2\",\"code\":\"ceshibu\",\"createTime\":1583310038684,\"id\":\"2e43b6a2-ec20-4a16-b423-8b8005c1d729\",\"isDelete\":1,\"isInit\":0,\"name\":\"测试部\",\"organizationId\":\"1\",\"sort\":7,\"state\":1},{\"classConfigId\":\"1_2\",\"code\":\"banjileibie2\",\"createTime\":1583855527749,\"id\":\"0ac4b35d-df83-4ab5-b1d6-d22f14e7ccbc\",\"isDelete\":1,\"isInit\":0,\"name\":\"新增数据\",\"organizationId\":\"1\",\"sort\":8,\"state\":1}]";
		map.put("response", str);
		map.put("classConfigLastNum", "8");
		map.put("configClasstypeId", "1_2");
		String sss = "name=aaaaaaa\r\n" + "trainType=0\r\n" + "beginDate=1584028800000\r\n" + "endDate={{time++5d}}\r\n"
				+ "implementDept=ffa26c61-f9cf-4d1e-9522-8a9335e7dbeb\r\n"
				+ "categoryId=5709e8d1-f3bc-4007-8c39-fc1094a2b58e\r\n"
				+ "classTeachers=[{\"teacherId\":\"fc5668f8-f6c9-4965-a01b-46ae3024d4d0\",\"teacherName\":\"presh007\"}]\r\n"
				+ "planNum=20\r\n" + "money=300000\r\n" + "trainPlaceId\r\n" + "trainPlace\r\n" + "trainContent\r\n"
				+ "topicIds\r\n" + "classNotice\r\n" + "organizationId=ffa26c61-f9cf-4d1e-9522-8a9335e7dbeb\r\n"
				+ "publisherId=0006133f-a78d-49b5-af84-48f9d2c9ed35\r\n" + "cover\r\n" + "coverPath\r\n"
				+ "trainObject\r\n" + "trainObjectText\r\n" + "trainContentText\r\n" + "classNoticeText\r\n"
				+ "topics\r\n" + "status=1";
		hu.handleBody(map, sss);

	}
}
