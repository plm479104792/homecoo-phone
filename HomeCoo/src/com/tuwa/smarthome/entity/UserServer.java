package com.tuwa.smarthome.entity;

import java.util.Date;

public class UserServer {
    private Integer userId;

    private String phonenum;

    private String password;

    private String realname;

    private String email;

    private String gatewayNo;

    private String address;

    private Boolean isOnline;

    private Date createTime;
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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGatewayNo() {
        return gatewayNo;
    }

    public void setGatewayNo(String gatewayNo) {
        this.gatewayNo = gatewayNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", phonenum=" + phonenum
				+ ", password=" + password + ", realname=" + realname
				+ ", email=" + email + ", gatewayNo=" + gatewayNo
				+ ", address=" + address + ", isOnline=" + isOnline
				+ ", createTime=" + createTime + "]";
	}

}