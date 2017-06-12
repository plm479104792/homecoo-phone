package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user")  
public class User {
	@DatabaseField(columnName = "USER_ID",generatedId = true,allowGeneratedIdInsert=true)
    private Integer userId;
	@DatabaseField(columnName = "PHONENUM") 
    private String phonenum;
	@DatabaseField(columnName = "PASSWORD") 
    private String password;
	@DatabaseField(columnName = "GATEWAY_NO") 
    private String gatewayNo;
	@DatabaseField(columnName = "EMAIL") 
    private String email;
	@DatabaseField(columnName = "REALNAME") 
    private String realname;
	@DatabaseField(columnName = "ADDRESS") 
    private String address;
	@DatabaseField(columnName = "IS_ONLINE") 
    private String isOnline;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getPhonenum() {
		return phonenum;
	}
	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(Integer userId, String phonenum, String password,
			String gatewayNo, String email, String realname, String address,
			String isOnline) {
		super();
		this.userId = userId;
		this.phonenum = phonenum;
		this.password = password;
		this.gatewayNo = gatewayNo;
		this.email = email;
		this.realname = realname;
		this.address = address;
		this.isOnline = isOnline;
	}

	
	
	
	
}