package com.api.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.common.HeadersManager;
import com.api.common.HttpUtil;
import com.api.data.TestDataProviders;
import com.api.utils.AES256EncryptionUtil;
import com.api.utils.AssertsUtil;
import com.api.utils.HandleUrlBodyUtil;
import com.api.utils.JsonUtil;
import com.api.utils.PropertiesUtil;

@Listeners({ com.api.utils.ExtentTestNGIReporterListener.class })
public class ApiTest {
	private static Logger log = Logger.getLogger(ApiTest.class);
	private Map<String, Object> saveDataMap = new HashMap<>();
	private Boolean resultFlag;
	private static Map<String, String> testPropertiesMap = PropertiesUtil.getAll("test.properties");
	private static Map<String, String> imagePropertiesMap = PropertiesUtil.getAll("image.properties");
	public static String workPath = System.getProperty("user.dir").replace("\\", "/");

	@BeforeSuite
	private void start() {
		PropertyConfigurator.configure(workPath + "/properties/log4j.properties");
		saveDataMap.putAll(testPropertiesMap);
		saveDataMap.putAll(imagePropertiesMap);
		saveDataMap.put("uid", UUID.randomUUID().toString().toLowerCase().replaceAll("-", ""));
		String loginUrl = testPropertiesMap.get("baseUrl") + "/oauth/api/v1/auth";
		String loginBody = "key={{loginKey}}\n" + "organization_id={{organization_id}}\n"
				+ "login_method=JZNm+1f9txtGtiE0oRMJ1g==\n" + "username={{userName}}\n" + "pword={{passWord}}\n"
				+ "captcha";
		apiTestMethod("空", "登陆接口", loginUrl, loginBody, "空", "POST", "access_token",
				"Authorization:Bearer__{{access_token}}", "Y", "access_token!=null", "空", "空", "空");
	}

	@Test(dataProvider = "apiTestDataProvider", dataProviderClass = TestDataProviders.class)
	public void testCaseRequest(String sequenceNum, String functionDescribe, String apiUrl, String body,
			String filePath, String requestMethod, String needSaveParameters, String useHeaders, String booleanExecute,
			String assertStr, String debug, String tryNum, String delayTime, String remarks, String excleName,
			String sheetName) {
		resultFlag = false;
		int tNum = 0;
		int dTime = 0;
		if (!tryNum.equals("空") && !delayTime.equals("空")) {
			try {
				tNum = Integer.parseInt(tryNum);
				String deTime = delayTime.replace("ms", "").replace("s", "");
				dTime = Integer.parseInt(deTime);
			} catch (Exception e) {
				log.error("请检查" + excleName + ">>>" + sheetName + ">>> 第" + sequenceNum + "条用例的【" + tryNum + "】和【"
						+ delayTime + "】格式是否正确！");
				resultFlag = true;
			}
		}
		if (resultFlag == false) {
			for (int i = 0; i < tNum + 1; i++) {
				if (resultFlag == true) {
					break;
				}
				if (i != 0) {
					if (delayTime.endsWith("ms")) {
						log.info(excleName + " >>> " + sheetName + " >>> 第" + sequenceNum + "条用例，" + dTime
								+ "毫秒后【Restart】第" + i + "次请求...");
					} else {
						log.info(excleName + " >>> " + sheetName + " >>> 第" + sequenceNum + "条用例，" + dTime
								+ "秒后【Restart】第" + i + "次请求...");
					}
					log.info(functionDescribe);
					try {
						if (delayTime.endsWith("ms")) {
							Thread.sleep(dTime);
						} else {
							Thread.sleep(dTime * 1000);
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				} else {
					log.info(excleName + " >>> " + sheetName + " >>> 第" + sequenceNum + "条用例" + " >>> Start...");
					log.info(functionDescribe);
				}
				apiTestMethod(sequenceNum, functionDescribe, apiUrl, body, filePath, requestMethod, needSaveParameters,
						useHeaders, booleanExecute, assertStr, debug, excleName, sheetName);
				log.info(excleName + " >>> " + sheetName + " >>> 第" + sequenceNum + "条用例" + " >>> End... \r\n\r\n\r\n");
			}
		} else {
			log.error(excleName + " >>> " + sheetName + " >>> 第" + sequenceNum + "条用例，跳过不执行！ \r\n\r\n\r\n");
		}
	}

	public void apiTestMethod(String sequenceNum, String functionDescribe, String apiUrl, String body, String filePath,
			String requestMethod, String needSaveParameters, String useHeaders, String booleanExecute, String assertStr,
			String debug, String excleName, String sheetName) {
		HeadersManager.saveHeadersToFile(useHeaders);
		HandleUrlBodyUtil hubu = new HandleUrlBodyUtil();
		List<String> list = hubu.getNewUrlAndBody(saveDataMap, apiUrl, body);
		HttpUtil http = new HttpUtil();
		Map<String, String> responseMap = http.request(list.get(0), list.get(1), filePath, requestMethod, useHeaders);
		saveDataMap.put("response", responseMap.get("response"));
		saveDataMap.put("responseCode", responseMap.get("responseCode"));
		Map<String, Object> tempSaveDataMap = null;
		if (needSaveParameters != "空") {
			JsonUtil jsonUtil = new JsonUtil();
			tempSaveDataMap = jsonUtil.saveJsonDatas(saveDataMap, responseMap.get("response"), needSaveParameters);
			for (String key : tempSaveDataMap.keySet()) {
				if (tempSaveDataMap.get(key).equals("88888888")) {
					saveDataMap.put(key, "");
				} else {
					if (key.startsWith("!$")) {
						try {
							log.info("待解密数据：" + key + "=" + tempSaveDataMap.get(key));
							String decryptStr = AES256EncryptionUtil.aesDecrypt(tempSaveDataMap.get(key).toString());
							saveDataMap.put(key, decryptStr);
						} catch (Exception e) {
							log.error("保存参数时，解密异常！");
						}
					} else {
						saveDataMap.put(key, tempSaveDataMap.get(key));
//						if (key.equals("access_token")) {
//							PropertiesUtil.update("cookies.properties", "Cookie",
//									"Authorization=Bearer__" + tempSaveDataMap.get(key).toString());
//						}
					}
				}
			}
		}

		AssertsUtil au = new AssertsUtil();
		if (assertStr.equals("空")) {
			resultFlag = au.compareResult(list.get(0), list.get(1), saveDataMap, "responseCode==200");
		} else {
			resultFlag = au.compareResult(list.get(0), list.get(1), saveDataMap, assertStr);
		}
		if (resultFlag == true && !sequenceNum.equals("空")) {
			log.info(" 执行结果 :【 Pass 】");
		} else if (resultFlag == false && !sequenceNum.equals("空")) {
			log.error("执行结果 :【 Fail 】");
			// 请求失败，打印请求url、body、响应详细信息
			log.info("请求URL为：" + "\n" + list.get(0));
			if (!list.get(1).equals("空")) {
				if (list.get(1).startsWith("{") && list.get(1).endsWith("}")) {
					log.info("请求Body为：" + "\n" + JsonUtil.formatResponseStr(list.get(1)));
				} else {
					log.info("请求Body为：" + "\n" + list.get(1));
				}
			}
			try {
				log.info("Response消息为：" + "\n" + JsonUtil.formatResponseStr(responseMap.get("response")));
			} catch (Exception e) {
				log.info("Response消息体过长无法显示！");
				saveDataMap.remove("response");
			}
		}
		HeadersManager.saveHeadersToFile(useHeaders, saveDataMap);
//		 为空时默认打印已保存的参数，不为空时打印需要打印的参数
		if (debug.equals("空") && !needSaveParameters.equals("空") && !sequenceNum.equals("空")) {
			for (String tempSaveKey : tempSaveDataMap.keySet()) {
				log.info("- - " + tempSaveKey + " = " + saveDataMap.get(tempSaveKey));
			}
		} else if (!debug.equals("空")) {
			String[] keyArr = debug.replace(";", "").split("\n");
			for (String key : keyArr) {
				if (key.equals("response")) {
					log.info(key + " = " + "\n" + JsonUtil.formatResponseStr(responseMap.get("response")));
				} else {
					log.info("- - " + key + " = " + saveDataMap.get(key));
				}
			}
		}
		// 将上传文件获得的fileKey存储至headers.properties配置文件，并删除集合map中的fileKey
		if (saveDataMap.containsKey("fileKey")) {
			PropertiesUtil.update("headers.properties", "fileKey", saveDataMap.get("fileKey").toString());
			saveDataMap.remove("fileKey");
		}
	}

	@AfterSuite
	private void end() {
//		log.info();
	}
}
