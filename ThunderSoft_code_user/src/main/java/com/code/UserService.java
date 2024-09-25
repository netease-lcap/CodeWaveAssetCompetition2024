package com.code;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSONObject;
import com.code.entity.CompanyEntity;
import com.code.entity.DepartmentEntity;
import com.code.entity.PositionEntity;
import com.code.entity.UserEntity;
import com.code.utils.HttpUtils;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: cfn
 * @date: 2024/3/14 11:42
 * @description: 用户相关接口类
 */
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(PositionService.class);


	/**
	 * 根据岗位id查询用户列表
	 *
	 * @param positionId 岗位id
	 * @return 用户列表
	 */
	@NaslLogic
	public static List<UserEntity> getUserListByPositionId(String positionId) {
		logger.info("开始根据岗位id查询用户");
		List<UserEntity> list = new ArrayList<>();
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getPositionUsers soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <positionID xsi:type=\"xsd:int\">" + positionId + "</positionID>\n" +
					"      </ws:getPositionUsers>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			String replaceNil = replace.replace("xsi:nil=\"true\"", "");
			String replaceBoolean = replaceNil.replace("xsi:type=\"xsd:boolean\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replaceBoolean);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			if (ObjectUtil.isEmpty(multiRef)) {
				return null;
			}
			if (multiRef instanceof JSONArray) {
				List<UserEntity> userEntities = JSONObject.parseArray(JSONObject.toJSONString(multiRef), UserEntity.class);
				userEntities.forEach(userEntity -> {
					String id = userEntity.getId();
					List<String> strings = JSONObject.parseArray(id, String.class);
					userEntity.setId(strings.get(1));
				});
				list.addAll(userEntities);
			} else {
				UserEntity userEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef), UserEntity.class);
				String id = userEntity.getId();
				List<String> strings = JSONObject.parseArray(id, String.class);
				userEntity.setId(strings.get(1));
				list.add(userEntity);
			}
			return list;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据用户id查询用户
	 *
	 * @param userId 用户id
	 * @return 用户详情
	 */
	@NaslLogic
	public static UserEntity getUserById(String userId) {
		logger.info("开始根据用户id:{}查询用户详情", userId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getUser soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <userID xsi:type=\"xsd:int\">" + userId + "</userID>\n" +
					"      </ws:getUser>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			// 去掉不需要的标签
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			String replaceNil = replace.replace("xsi:nil=\"true\"", "");
			String replaceBoolean = replaceNil.replace("xsi:type=\"xsd:boolean\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replaceBoolean);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			UserEntity userEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef),
					UserEntity.class);
			if (ObjectUtil.isEmpty(userEntity)) {
				return null;
			}
			//处理id
			String userEntityId = userEntity.getId();
			List<String> strings = JSONObject.parseArray(userEntityId, String.class);
			userEntity.setId(strings.get(1));
			return userEntity;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据用户id查询用户所属公司
	 *
	 * @param userId
	 * @return
	 */
	@NaslLogic
	public static CompanyEntity getUserOwnerCompany(String userId) {
		logger.info("开始根据用户id:{}查询用户所属公司", userId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getUserOwnerCompany soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <userID xsi:type=\"xsd:int\">"+userId+"</userID>\n" +
					"      </ws:getUserOwnerCompany>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			// 去掉不需要的标签
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			String replaceNil = replace.replace("xsi:nil=\"true\"", "");
			String replaceBoolean = replaceNil.replace("xsi:type=\"xsd:boolean\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replaceBoolean);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			CompanyEntity companyEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef),
					CompanyEntity.class);
			if (ObjectUtil.isEmpty(companyEntity)) {
				return null;
			}
			//处理id
			String companyEntityId = companyEntity.getId();
			List<String> strings = JSONObject.parseArray(companyEntityId, String.class);
			companyEntity.setId(strings.get(1));
			return companyEntity;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据用户id查询用户所属部门
	 *
	 * @param userId 用户id
	 * @return
	 */
	@NaslLogic
	public static DepartmentEntity getUserOwnerDepartment(String userId) {
		logger.info("开始根据用户id:{}查询用户所属部门", userId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getUserOwnerDepartment soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <userID xsi:type=\"xsd:int\">" + userId + "</userID>\n" +
					"      </ws:getUserOwnerDepartment>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			// 去掉不需要的标签
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			String replaceNil = replace.replace("xsi:nil=\"true\"", "");
			String replaceBoolean = replaceNil.replace("xsi:type=\"xsd:boolean\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replaceBoolean);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			DepartmentEntity departmentEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef),
					DepartmentEntity.class);
			if (ObjectUtil.isEmpty(departmentEntity)) {
				return null;
			}
			//处理id
			String departmentEntityId = departmentEntity.getId();
			List<String> strings = JSONObject.parseArray(departmentEntityId, String.class);
			departmentEntity.setId(strings.get(1));
			return departmentEntity;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据用户id查询用户主岗位
	 *
	 * @param userId
	 * @return
	 */
	@NaslLogic
	public static PositionEntity getUserMainPosition(String userId) {
		logger.info("开始根据用户id:{}查询用户所属部门", userId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getUserMainPosition soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <userID xsi:type=\"xsd:int\">" + userId + "</userID>\n" +
					"      </ws:getUserMainPosition>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			// 去掉不需要的标签
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			String replaceNil = replace.replace("xsi:nil=\"true\"", "");
			String replaceBoolean = replaceNil.replace("xsi:type=\"xsd:boolean\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replaceBoolean);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			//json转换
			PositionEntity positionEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef),
					PositionEntity.class);
			if (ObjectUtil.isEmpty(positionEntity)) {
				return null;
			}
			//处理id
			String positionEntityId = positionEntity.getId();
			List<String> strings = JSONObject.parseArray(positionEntityId, String.class);
			positionEntity.setId(strings.get(1));
			return positionEntity;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据用户id查询用户岗位列表
	 *
	 * @param userId 用户id
	 * @return 岗位列表
	 */
	@NaslLogic
	public static List<PositionEntity> getUserPositions(String userId) {
		logger.info("开始查询所有岗位");
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getUserPositions soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <userID xsi:type=\"xsd:int\">" + userId + "</userID>\n" +
					"      </ws:getUserPositions>\n" +
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
			List<PositionEntity> departmentEntities = JSONObject.parseArray(JSONObject.toJSONString(multiRef), PositionEntity.class);
			departmentEntities.forEach(departmentEntity -> {
				String id = departmentEntity.getId();
				List<String> strings = JSONObject.parseArray(id, String.class);
				departmentEntity.setId(strings.get(1));
			});
			return new ArrayList<>(departmentEntities);
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

}

