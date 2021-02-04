package com.lemon.api.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/*
 * 接口调用工具类
 */
public class HttpUtil {
	/*
	 * 以post方式处理接口调用
	 */
	public static  String doPost(String url,Map<String, String> params) {
		HttpPost post= new HttpPost(url);
		//基本键值对
		List<BasicNameValuePair> parameters=new ArrayList<BasicNameValuePair>();
		//取出map中所有的参数名
		Set<String> keys=params.keySet();
		//通过循环将参数保存在list集合
		for(String name:keys) {
			String value=params.get(name);
			parameters.add(new BasicNameValuePair(name,value));
		}
		String result="";
		//setEntity 设置消息
		try {
			post.setEntity(new UrlEncodedFormEntity(parameters, "utf-8"));
			//准备请求头数据（如果有需要，比如cookie，content-type）
			//发起请求，获取接口响应信息（状态码，响应报文或某些特殊的响应头数据）
			HttpClient client=HttpClients.createDefault();
			HttpResponse httpResponse=client.execute(post);
			//状态码
			int code =httpResponse.getStatusLine().getStatusCode();
			System.out.println(code);
			//从响应对象中获取返回数据
			httpResponse.getEntity();// json格式
			//System.out.println(httpResponse.getEntity());
			result=EntityUtils.toString(httpResponse.getEntity());//json格式转换成字符串格式
		
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	 * 以get方式处理接口调用
	 */
	
	public static String doGet(String url,Map<String, String> params) {
		Set<String> keys=params.keySet();
		//定义一个标志位
		int mark=1;
		for (String name:keys) {
			if(mark==1) {
				url+=("?"+name+"="+params.get(name));
			}else {
				url+=("&"+name+"="+params.get(name));
			}
			mark++;
			//url+=("?mobilephone=1111&pword=123456");
		}
		System.out.println("url="+url);

		//指定接口提交的方式
		HttpGet get=new HttpGet(url);
		//发送请求，拿到响应数据
		HttpClient httpClient=HttpClients.createDefault();
		HttpResponse httpResponse;
		
		String result="";
		try {
			httpResponse = httpClient.execute(get);
			int code=httpResponse.getStatusLine().getStatusCode();
			result=EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		return result;
		
	}
	
	public static String doService(String url,String type,Map<String, String> params) {
		String result="";
		if("post".equalsIgnoreCase(type)) {
			result=HttpUtil.doPost(url, params);
		}else if("get".equalsIgnoreCase(type)) {
			result=HttpUtil.doGet(url, params);
		}
		return result;

	}
}
