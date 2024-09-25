package com.code.entity;

import com.netease.lowcode.core.annotation.NaslStructure;

/**
 * @author: cfn
 * @date: 2024/5/23 14:19
 * @description: openId的用户实体
 */
@NaslStructure
public class OPenIdUserEntity {

	public String email;

	public String id;

	public String fullname;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
}
