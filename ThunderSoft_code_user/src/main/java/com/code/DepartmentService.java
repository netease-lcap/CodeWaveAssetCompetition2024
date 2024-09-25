package com.code;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSONObject;
import com.code.entity.CompanyEntity;
import com.code.entity.DepartmentEntity;
import com.code.utils.HttpUtils;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: cfn
 * @date: 2024/3/14 11:44
 * @description: 部门相关
 */
public class DepartmentService {

	private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

	/**
	 * 根据单位id查询部门集合
	 *
	 * @param type      单位类型
	 * @param companyId 单位id
	 * @return
	 */
	@NaslLogic
	public static List<DepartmentEntity> getCompanyDepartments(String type, String companyId) {
		logger.info("开始查询所有单位");
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getCompanyDepartments soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <type xsi:type=\"xsd:int\">" + type + "</type>\n" +
					"         <companyID xsi:type=\"xsd:int\">" + companyId + "</companyID>\n" +
					"      </ws:getCompanyDepartments>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			String replaceNil = replace.replace("xsi:nil=\"true\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replaceNil);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			if(ObjectUtil.isNull(multiRef)){
				return null;
			}

			List<DepartmentEntity> departmentEntities = JSONObject.parseArray(JSONObject.toJSONString(multiRef), DepartmentEntity.class);
			departmentEntities.forEach(departmentEntity -> {
				String id = departmentEntity.getId();
				List<String> strings = JSONObject.parseArray(id, String.class);
				departmentEntity.setId(strings.get(1));
			});
			List<DepartmentEntity> list = new ArrayList<>(departmentEntities);
			return list;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据部门id查询部门
	 *
	 * @param departId 部门ID
	 * @return 部门详情
	 */
	@NaslLogic
	public static DepartmentEntity getDepartmentById(String departId) {
		logger.info("开始根据id:{}查询部门", departId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getDepartment soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <id xsi:type=\"xsd:int\">" + departId + "</id>\n" +
					"      </ws:getDepartment>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replace);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			DepartmentEntity departmentEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef),
					DepartmentEntity.class);
			if (ObjectUtil.isEmpty(departmentEntity)) {
				return null;
			}
			//处理id
			String departmentId = departmentEntity.getId();
			List<String> strings = JSONObject.parseArray(departmentId, String.class);
			departmentEntity.setId(strings.get(1));
			return departmentEntity;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据部门id查询部门所属单位
	 * @param departId 部门id
	 * @return 单位详情
	 */
	@NaslLogic
	public static CompanyEntity getDepartmentOwnerCompany(String departId){
		logger.info("开始根据部门id:{}查询单位详情", departId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getDepartmentOwnerCompany soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <departmentID xsi:type=\"xsd:int\">"+departId+"</departmentID>\n" +
					"      </ws:getDepartmentOwnerCompany>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replace);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
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
}
