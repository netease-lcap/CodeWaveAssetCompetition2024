package com.code.entity;

import com.netease.lowcode.core.annotation.NaslStructure;

/**
 * @author: cfn
 * @date: 2024/3/8 16:58
 * @description: 部门
 */
@NaslStructure
public class DepartmentEntity {

	/**
	 *id
	 */
	public String id;

	/**
	 *部门名称
	 */
	public String name;

	/**
	 * 排序
	 */
	public String sortNumber;

	/**
	 *电话
	 */
	public String telephone;

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

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
