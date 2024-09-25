package com.code.entity;

import com.netease.lowcode.core.annotation.NaslStructure;

/**
 * @author: cfn
 * @date: 2024/3/18 19:43
 * @description: 岗位组实体
 */
@NaslStructure
public class PositionGroupEntity {

	/**
	 * 岗位组ID
	 */
	public String id;

	/**
	 * 名称
	 */
	public String name;

	/**
	 * 编码,排序用
	 */
	public String sortNumber;

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

	public String getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(String sortNumber) {
		this.sortNumber = sortNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
