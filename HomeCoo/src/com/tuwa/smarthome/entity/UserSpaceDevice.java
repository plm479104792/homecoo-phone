package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * @类名    UserSpaceDevice
 * @创建者   ppa
 * @创建时间 2016-4-19
 * @描述   TODO
 */
@DatabaseTable(tableName = "user_device_space") 
public class UserSpaceDevice {
	 @DatabaseField(columnName = "PHONENUM") 
	private String phonenum;
	 @DatabaseField(columnName = "DEVICE_NO") 
	private String deviceNo;
	 @DatabaseField(columnName = "DEVICE_NAME") 
	private String deviceName;
	 @DatabaseField(columnName = "SPACE_NO") 
	private String spaceNo;
	 @DatabaseField(columnName = "SPACE_TYPE") 
	private Integer spaceType;
	
	public String getPhonenum() {
		return phonenum;
	}
	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}
	public String getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getSpaceNo() {
		return spaceNo;
	}
	public void setSpaceNo(String spaceNo) {
		this.spaceNo = spaceNo;
	}
	
	
	public Integer getSpaceType() {
		return spaceType;
	}
	public void setSpaceType(Integer spaceType) {
		this.spaceType = spaceType;
	}
	public UserSpaceDevice() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public UserSpaceDevice(String phonenum, String deviceNo, String deviceName,
			String spaceNo, Integer spaceType) {
		super();
		this.phonenum = phonenum;
		this.deviceNo = deviceNo;
		this.deviceName = deviceName;
		this.spaceNo = spaceNo;
		this.spaceType = spaceType;
	}
	@Override
	public String toString() {
		return "UserSpaceDevice [phonenum=" + phonenum + ", deviceNo="
				+ deviceNo + ", deviceName=" + deviceName + ", spaceNo="
				+ spaceNo + ", spaceType=" + spaceType + "]";
	}
    
}
