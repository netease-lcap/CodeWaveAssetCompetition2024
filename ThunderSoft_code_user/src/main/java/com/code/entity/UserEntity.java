package com.code.entity;

import com.netease.lowcode.core.annotation.NaslStructure;

/**
 * @author: cfn
 * @date: 2024/3/14 14:45
 * @description: 用户实体
 */
@NaslStructure
public class UserEntity {

	/**
	 * id
	 */
	public String id;

	/**
	 * 姓名，唯一标识用户，作为登录用户名。
	 */
	public String name;

	/**
	 * 性别, true - 男性， false - 女性
	 */
	public Boolean gender;

	/**
	 * 年龄
	 */
	public String age;

	/**
	 * 学历
	 */
	public String educationDegree;

	/**
	 * 籍贯
	 */
	public String jiGuan;

	/**
	 * 电子邮件
	 */
	public String email;

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

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getEducationDegree() {
		return educationDegree;
	}

	public void setEducationDegree(String educationDegree) {
		this.educationDegree = educationDegree;
	}

	public String getJiGuan() {
		return jiGuan;
	}

	public void setJiGuan(String jiGuan) {
		this.jiGuan = jiGuan;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
