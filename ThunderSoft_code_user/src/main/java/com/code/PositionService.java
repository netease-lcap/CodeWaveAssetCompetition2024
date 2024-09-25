package com.code;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSONObject;
import com.code.entity.DepartmentEntity;
import com.code.entity.PositionEntity;
import com.code.utils.HttpUtils;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: cfn
 * @date: 2024/3/14 11:45
 * @description: 岗位相关
 */
public class PositionService {

	private static final Logger logger = LoggerFactory.getLogger(PositionService.class);

	/**
	 * 根据部门id查询岗位列表
	 *
	 * @param departmentId
	 * @return
	 */
	@NaslLogic
	public static List<PositionEntity> getPositionByDepartmentId(String departmentId) {
		logger.info("开始查询所有岗位");
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getDepartmentPositions soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <departmentID xsi:type=\"xsd:int\">" + departmentId + "</departmentID>\n" +
					"      </ws:getDepartmentPositions>\n" +
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

	/**
	 * 根据岗位id查询岗位信息
	 *
	 * @param positionId
	 * @return
	 */
	@NaslLogic
	public static PositionEntity getPositionById(String positionId) {
		logger.info("开始根据岗位id:{}查询岗位详情", positionId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getPosition soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <positionID xsi:type=\"xsd:int\">" + positionId + "</positionID>\n" +
					"      </ws:getPosition>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replace);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			PositionEntity positionEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef),
					PositionEntity.class);
			if (ObjectUtil.isEmpty(positionEntity)) {
				return null;
			}
			//处理id
			String departmentId = positionEntity.getId();
			List<String> strings = JSONObject.parseArray(departmentId, String.class);
			positionEntity.setId(strings.get(1));
			return positionEntity;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}


	/**
	 * 根据岗位id查询当前岗位的部门信息
	 *
	 * @param positionId 岗位id
	 * @return 部门信息
	 */
	@NaslLogic
	public static DepartmentEntity getPositionOwnerDepartment(String positionId) {
		logger.info("开始根据岗位id:{}查询岗位详情", positionId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getPositionOwnerDepartment soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <positionID xsi:type=\"xsd:int\">" + positionId + "</positionID>\n" +
					"      </ws:getPositionOwnerDepartment>\n" +
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
}
