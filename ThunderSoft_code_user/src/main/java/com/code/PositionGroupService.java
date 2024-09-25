package com.code;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSONObject;
import com.code.entity.PositionGroupEntity;
import com.code.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: cfn
 * @date: 2024/3/19 11:41
 * @description: 岗位组接口
 */
public class PositionGroupService {

	private static final Logger logger = LoggerFactory.getLogger(PositionGroupService.class);

	/**
	 * 查询所有岗位组
	 *
	 * @return 岗位组列表
	 */
	public static List<PositionGroupEntity> getAllPositionGroups() {
		logger.info("开始查询所有岗位组");
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getAllPositionGroups soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"/>\n" +
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
			if (ObjectUtil.isNull(multiRef)) {
				return null;
			}

			List<PositionGroupEntity> departmentEntities = JSONObject.parseArray(JSONObject.toJSONString(multiRef), PositionGroupEntity.class);
			departmentEntities.forEach(positionGroupEntity -> {
				String id = positionGroupEntity.getId();
				List<String> strings = JSONObject.parseArray(id, String.class);
				positionGroupEntity.setId(strings.get(1));
			});
			List<PositionGroupEntity> list = new ArrayList<>(departmentEntities);
			return list;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}


	/**
	 * 根据id查询岗位组
	 *
	 * @param positionGroupId 岗位组id
	 * @return
	 */
	public static PositionGroupEntity getPositionGroup(Integer positionGroupId) {
		logger.info("开始根据id:{}查询岗位组", positionGroupId);
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getPositionGroup soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <id xsi:type=\"xsd:int\">" + positionGroupId + "</id>\n" +
					"      </ws:getPositionGroup>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replace);
			Object multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.get("multiRef");
			PositionGroupEntity positionGroupEntity = JSONObject.parseObject(JSONObject.toJSONString(multiRef),
					PositionGroupEntity.class);
			if (ObjectUtil.isEmpty(positionGroupEntity)) {
				return null;
			}
			//处理id
			String positionGroupEntityId = positionGroupEntity.getId();
			List<String> strings = JSONObject.parseArray(positionGroupEntityId, String.class);
			positionGroupEntity.setId(strings.get(1));
			return positionGroupEntity;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据岗位组id查询岗位组成员
	 *
	 * @param positionGroupId 岗位组id
	 * @return 岗位id集合
	 */
	public static List<Integer> getPositionGroupMembers(Integer positionGroupId) {
		logger.info("开始查询所有岗位组");
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getPositionGroupMembers soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <positionGroupID xsi:type=\"xsd:int\">" + positionGroupId + "</positionGroupID>\n" +
					"      </ws:getPositionGroupMembers>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			String replaceNil = replace.replace("xsi:nil=\"true\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replaceNil);
			JSONArray multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns1:getPositionGroupMembersResponse")
					.getJSONObject("getPositionGroupMembersReturn")
					.getJSONArray("getPositionGroupMembersReturn");
			if (ObjectUtil.isNull(multiRef)) {
				return null;
			}

			List<Integer> positionGroupIds = JSONObject.parseArray(JSONObject.toJSONString(multiRef), Integer.class);
			return positionGroupIds;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

	/**
	 * 根据用户id查询岗位组id列表
	 *
	 * @param userId 用户id
	 * @return 岗位组id列表
	 */
	public static List<Integer> getUserPositionGroups(Integer userId) {
		logger.info("开始查询所有岗位组");
		try {
			//请求报文
			String content = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sso.ztjs.cn\">\n" +
					"   <soapenv:Header/>\n" +
					"   <soapenv:Body>\n" +
					"      <ws:getUserPositionGroups soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
					"         <id xsi:type=\"xsd:int\">" + userId + "</id>\n" +
					"      </ws:getUserPositionGroups>\n" +
					"   </soapenv:Body>\n" +
					"</soapenv:Envelope>";
			String result = HttpUtils.httpClientPost(Constants.user_url, content);
			String replaceInt = result.replace("xsi:type=\"xsd:int\"", "");
			String replace = replaceInt.replace("xsi:type=\"xsd:string\"", "");
			String replaceNil = replace.replace("xsi:nil=\"true\"", "");
			cn.hutool.json.JSONObject json = XML.toJSONObject(replaceNil);
			JSONArray multiRef = json.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns1:getUserPositionGroupsResponse")
					.getJSONObject("getUserPositionGroupsReturn")
					.getJSONArray("getUserPositionGroupsReturn");
			if (ObjectUtil.isNull(multiRef)) {
				return null;
			}

			List<Integer> positionIds = JSONObject.parseArray(JSONObject.toJSONString(multiRef), Integer.class);
			return positionIds;
		} catch (IOException e) {
			throw new RuntimeException("请求失败");
		}
	}

}
