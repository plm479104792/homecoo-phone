package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "device") 
public class Device {
	@DatabaseField(columnName = "DEVICE_ID",generatedId = true,allowGeneratedIdInsert=true)
	private Integer deviceId;    //本地数据库使用的主键 
	@DatabaseField(columnName = "DEVICE_NO")
	private String deviceNo; 
	@DatabaseField(columnName = "DEVICE_TYPE_ID")     //设备类型
	private Integer  deviceTypeId; 
	@DatabaseField(columnName = "DEVICE_TYPE_NAME") 
	private String  deviceTypeName; 
	@DatabaseField(columnName = "DEVICE_CATEGORY_ID")    //设备大类
	private Integer  deviceCategoryId; 
	@DatabaseField(columnName = "DEVICE_CATEGORY_NAME") 
	private String  deviceCategoryName; 
	@DatabaseField(columnName = "DEVICE_NAME") 
	private String  deviceName; 
	@DatabaseField(columnName = "GATEWAY_NO") 
	private String  gatewayNo; 
	@DatabaseField(columnName = "SPACE_TYPE_ID")  //房间类型 : 室内/室外
	private Integer  spaceTypeId; 
	@DatabaseField(columnName = "SPACE_NO") 
	private String  spaceNo; 
	@DatabaseField(columnName = "PHONE_NUM") 
	private String  phoneNum; 
	@DatabaseField(columnName = "DEVICE_STATE_CMD") 
	private String  deviceStateCmd; 
	
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	public Integer getDeviceTypeId() {
		return deviceTypeId;
	}
	public void setDeviceTypeId(Integer deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}
	public String getDeviceTypeName() {
		return deviceTypeName;
	}
	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}
	public Integer getDeviceCategoryId() {
		return deviceCategoryId;
	}
	public void setDeviceCategoryId(Integer deviceCategoryId) {
		this.deviceCategoryId = deviceCategoryId;
	}
	public String getDeviceCategoryName() {
		return deviceCategoryName;
	}
	public void setDeviceCategoryName(String deviceCategoryName) {
		this.deviceCategoryName = deviceCategoryName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public Integer getSpaceTypeId() {
		return spaceTypeId;
	}
	public void setSpaceTypeId(Integer spaceTypeId) {
		this.spaceTypeId = spaceTypeId;
	}
	public String getSpaceNo() {
		return spaceNo;
	}
	public void setSpaceNo(String spaceNo) {
		this.spaceNo = spaceNo;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getDeviceStateCmd() {
		return deviceStateCmd;
	}
	public void setDeviceStateCmd(String deviceStateCmd) {
		this.deviceStateCmd = deviceStateCmd;
	}
	public Device() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Device(Integer deviceId, String deviceNo, Integer deviceTypeId,
			String deviceTypeName, Integer deviceCategoryId,
			String deviceCategoryName, String deviceName, String gatewayNo,
			Integer spaceTypeId, String spaceNo, String phoneNum,
			String deviceStateCmd) {
		super();
		this.deviceId = deviceId;
		this.deviceNo = deviceNo;
		this.deviceTypeId = deviceTypeId;
		this.deviceTypeName = deviceTypeName;
		this.deviceCategoryId = deviceCategoryId;
		this.deviceCategoryName = deviceCategoryName;
		this.deviceName = deviceName;
		this.gatewayNo = gatewayNo;
		this.spaceTypeId = spaceTypeId;
		this.spaceNo = spaceNo;
		this.phoneNum = phoneNum;
		this.deviceStateCmd = deviceStateCmd;
	}

}
