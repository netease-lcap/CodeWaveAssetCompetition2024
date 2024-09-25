package com.code;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSONObject;
import com.code.entity.CompanyEntity;
import com.code.entity.CompanyTreeEntity;
import com.code.utils.HttpUtils;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: cfn
 * @date: 2024/3/14 11:44
 * @description: 单位相关接口
 */
public class CompanyService {

	private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);


	/**
	 * 查询单位类型
	 *
	 * @return 单位类型
	 */
	public static List<String> getCompanyTypes() {
		//请求报文
		String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
				"   <soapenv:Header/>\n" +
				"   <soapenv:Body>\n" +
				"      <ws:getCompanyTypes soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"/>\n" +
				"   </soapenv:Body>\n" +
				"</soapenv:Envelope>";
		try {
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replace = result.replace("xsi:type=\"xsd:int\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replace);
			JSONArray array = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns1:getCompanyTypesResponse")
					.getJSONObject("getCompanyTypesReturn")
					.getJSONArray("getCompanyTypesReturn");
			List<String> strings = JSONObject.parseArray(JSONObject.toJSONString(array), String.class);
			return strings;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 查询所有单位
	 *
	 * @return 单位列表
	 */
	@NaslLogic
	public static List<CompanyEntity> getAllCompany() {
		logger.info("开始查询所有单位");
		TimeInterval timer = DateUtil.timer();
		try {
			List<String> companyTypes = getCompanyTypes();
			List<CompanyEntity> list = new ArrayList<>();
			// 异步处理
			List<CompletableFuture<List<CompanyEntity>>> futures = new LinkedList<>();
			companyTypes.forEach(type -> {
				CompletableFuture<List<CompanyEntity>> future = CompletableFuture.supplyAsync(() -> {
					List<CompanyEntity> lists = new ArrayList<>();
					//请求报文
					String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
							"   <soapenv:Header/>\n" +
							"   <soapenv:Body>\n" +
							"      <ws:getAllCompanys soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
							"         <type xsi:type=\"xsd:int\">" + type + "</type>\n" +
							"      </ws:getAllCompanys>\n" +
							"   </soapenv:Body>\n" +
							"</soapenv:Envelope>";
					try {
						String result = HttpUtils.httpClientPost(Constants.user_url, content);
						String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
						String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
						cn.hutool.json.JSONObject json = XML.toJSONObject(replace);
						Object multiRef = json.getJSONObject("soapenv:Envelope")
								.getJSONObject("soapenv:Body")
								.get("multiRef");
						if (multiRef instanceof JSONArray) {
							List<CompanyEntity> companyEntities = JSONObject.parseArray(JSONObject.toJSONString(multiRef), CompanyEntity.class);
							companyEntities.forEach(companyEntity -> {
								String id = companyEntity.getId();
								List<String> strings = JSONObject.parseArray(id, String.class);
								companyEntity.setId(strings.get(1));
							});
							lists.addAll(companyEntities);
						} else {
							CompanyEntity companyEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef), CompanyEntity.class);
							String id = companyEntity.getId();
							List<String> strings = JSONObject.parseArray(id, String.class);
							companyEntity.setId(strings.get(1));
							lists.add(companyEntity);
						}
					} catch (IOException e) {
						throw new RuntimeException("请求失败");
					}
					return lists;
				});
				futures.add(future);
			});
			//合并计算
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
			for (CompletableFuture<List<CompanyEntity>> future : futures) {
				try {
					// 获取每个异步操作的结果
					list.addAll(future.get());
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException("请求失败");
				}
			}
			logger.info("消耗时间{}", timer.interval());
			return list;
		} catch (RuntimeException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据单位id和单位类型查询下级单位列表
	 *
	 * @param type      单位类型
	 * @param companyId 单位id
	 * @return 下级单位列表
	 */
	@NaslLogic
	public static List<CompanyEntity> getChildCompanies(String type, String companyId) {
		logger.info("开始查询子级单位");
		try {
			List<CompanyEntity> lists = new ArrayList<>();
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getChildCompanies soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <type xsi:type=\"xsd:int\">" + type + "</type>\n" +
					"         <id xsi:type=\"xsd:int\">" + companyId + "</id>\n" +
					"      </ws:getChildCompanies>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			try {
				String result = HttpUtils.httpClientPost(Constants.user_url_two, content);
				String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
				String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
				cn.hutool.json.JSONObject json = XML.toJSONObject(replace);
				Object multiRef = json.getJSONObject("soapenv:Envelope")
						.getJSONObject("soapenv:Body")
						.get("multiRef");
				if(ObjectUtil.isEmpty(multiRef)){
					return null;
				}
				if (multiRef instanceof JSONArray) {
					List<CompanyEntity> companyEntities = JSONObject.parseArray(JSONObject.toJSONString(multiRef), CompanyEntity.class);
					companyEntities.forEach(companyEntity -> {
						String id = companyEntity.getId();
						List<String> strings = JSONObject.parseArray(id, String.class);
						companyEntity.setId(strings.get(1));
					});
					lists.addAll(companyEntities);
				} else {
					CompanyEntity companyEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef), CompanyEntity.class);
					String id = companyEntity.getId();
					List<String> strings = JSONObject.parseArray(id, String.class);
					companyEntity.setId(strings.get(1));
					lists.add(companyEntity);
				}
				return lists;
			} catch (IOException e) {
				throw new RuntimeException("请求失败");
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("请求失败");
		}

	}

	/**
	 * 根据单位id查询单位
	 *
	 * @param type      单位类型
	 * @param companyId 单位id
	 * @return 单位详情
	 */
	@NaslLogic
	public static CompanyEntity getCompanyEntityById(String type, String companyId) {
		logger.info("开始根据id:{}查询单位", companyId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getCompany soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <type xsi:type=\"xsd:int\">" + type + "</type>\n" +
					"         <id xsi:type=\"xsd:int\">" + companyId + "</id>\n" +
					"      </ws:getCompany>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replace);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			// json转换
			CompanyEntity companyEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef),
					CompanyEntity.class);
			if (ObjectUtil.isEmpty(companyEntity)) {
				return null;
			}
			//处理id
			String departmentId = companyEntity.getId();
			List<String> strings = JSONObject.parseArray(departmentId, String.class);
			companyEntity.setId(strings.get(1));
			return companyEntity;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据单位id查询父级单位
	 *
	 * @param type
	 * @param companyId
	 * @return
	 */
	@NaslLogic
	public static CompanyEntity getParentCompany(String type, String companyId) {
		logger.info("开始根据id:{}查询父级单位", companyId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getParentCompany soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <type xsi:type=\"xsd:int\">" + type + "</type>\n" +
					"         <id xsi:type=\"xsd:int\">" + companyId + "</id>\n" +
					"      </ws:getParentCompany>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url_two, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replace);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			// json转换
			CompanyEntity companyEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef),
					CompanyEntity.class);
			if (ObjectUtil.isEmpty(companyEntity)) {
				return null;
			}
			//处理id
			String departmentId = companyEntity.getId();
			List<String> strings = JSONObject.parseArray(departmentId, String.class);
			companyEntity.setId(strings.get(1));
			return companyEntity;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 递归查询下所有的单位
	 *
	 * @param type
	 * @param companyId
	 * @param list
	 * @return
	 */
	@NaslLogic
	public static List<CompanyTreeEntity> buildCompanyTree(String type, String companyId, List<CompanyTreeEntity> list) {
		List<CompanyEntity> companyEntityList = getChildCompanies(type, companyId);
		if (CollUtil.isNotEmpty(companyEntityList)) {
			companyEntityList.forEach(companyEntity -> {
				CompanyTreeEntity companyTreeEntity = BeanUtil.copyProperties(companyEntity, CompanyTreeEntity.class);
				companyTreeEntity.setParentId(companyId);
				String id = companyEntity.getId();
				String type1 = companyEntity.getType();
				list.add(companyTreeEntity);
				buildCompanyTree(type1, id, list);
			});
			return list;
		} else {
			return list;
		}
	}

}
