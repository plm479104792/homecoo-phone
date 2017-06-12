package com.tuwa.smarthome.entity;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "version")
public class Version {
	@DatabaseField(columnName = "PHONENUM")
	private String phoneNum;
	@DatabaseField(columnName = "GATEWAY_NO")
	private String gatewayNo;
	@DatabaseField(columnName = "VERSION_TYPE")
	private int versionType;
	@DatabaseField(columnName = "VERSION_CODE")
	private String versionCode;
	@DatabaseField(columnName = "VERSION_DESCRIPTION")
	private String versionDescription;
	@DatabaseField(columnName = "UPDATE_TIME")
	private long updateTime;
	public String getPhonenum() {
		return phoneNum;
	}
	public void setPhonenum(String phonenum) {
		this.phoneNum = phonenum;
	}
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public int getVersionType() {
		return versionType;
	}
	public void setVersionType(int versionType) {
		this.versionType = versionType;
	}
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionDescription() {
		return versionDescription;
	}
	public void setVersionDescription(String versionDescription) {
		this.versionDescription = versionDescription;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	public Version() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Version(String phonenum, String gatewayNo, int versionType,
			String versionCode, String versionDescription, long updateTime) {
		super();
		this.phoneNum = phonenum;
		this.gatewayNo = gatewayNo;
		this.versionType = versionType;
		this.versionCode = versionCode;
		this.versionDescription = versionDescription;
		this.updateTime = updateTime;
	}
	
}
