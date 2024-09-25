package com.code.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author: cfn
 * @date: 2024/2/18 16:43
 * @description:
 */
public class HttpUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	/**
	 * 使用apache的HttpClient发送http请求
	 *
	 * @param url     请求URL
	 * @param content 请求参数
	 * @return XML String
	 * @Author: WgRui
	 * @Date: 2020/12/16
	 */
	public static String httpClientPost(String url, String content) throws IOException {
		// 获得Http客户端
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		// 创建Post请求
		HttpPost httpPost = new HttpPost(url);
		// 将数据放入entity中
		StringEntity entity = new StringEntity(content, "UTF-8");
		httpPost.setEntity(entity);
		// 响应模型
		String result = null;
		CloseableHttpResponse response = null;
		try {
			//设置请求头
			//特别说明一下，此处为SOAP1.1协议
			//如果用的是SOAP1.2协议，改为："application/soap+xml;charset=UTF-8"
			httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
			//命名空间+方法名
			//如为SOAP1.2协议不需要此项
			httpPost.setHeader("SOAPAction", "\"http://tempuri.org/Request\"");
			// 由客户端执行(发送)Post请求
			response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			logger.info("响应ContentType为:" + responseEntity.getContentType());
			logger.info("响应状态为:" + response.getStatusLine());
			result = EntityUtils.toString(responseEntity);
			logger.info("响应内容为:" + result);
		} catch (IOException e) {
			throw new RuntimeException("接口调用出现异常");
		} finally {
			// 释放资源
			if (httpClient != null) {
				httpClient.close();
			}
			if (response != null) {
				response.close();
			}
		}
		return result;
	}

}
