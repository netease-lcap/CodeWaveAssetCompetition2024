package com.code.entity;

import com.netease.lowcode.core.annotation.NaslStructure;

/**
 * @author: cfn
 * @date: 2024/3/19 11:37
 * @description: 子系统实体
 */
@NaslStructure
public class SubSystemEntity {

	/**
	 * ID
	 */
	public String id;
	/**
	 * 名称
	 */
	public String name;
	/**
	 * 描述
	 */
	public String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
