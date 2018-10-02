package com.zc.model;

import java.io.Serializable;

/**
 * 用户实体类
 * @author zc
 * 2018年10月2日16:01:25
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id = 0;
	private String name = null;
	private String sex = null;
	private String address = null;
	private String phone = null;

	public User() {

	}

	public User(int id, String name, String sex, String address, String phone) {
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.address = address;
		this.phone = phone;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
