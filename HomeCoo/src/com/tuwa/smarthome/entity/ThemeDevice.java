package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName = "themedevice") 
public class ThemeDevice {
	@DatabaseField(columnName = "THEME_NO")       
    private String themeNo;
	@DatabaseField(columnName = "THEME_DEVICE_NO")      //实体情景设备识别码
	private String themeDeviceNo;
	@DatabaseField(columnName = "THEME_TYPE") 
    private Integer themeType;
	@DatabaseField(columnName = "THEME_STATE")       
    private String themeState;
	@DatabaseField(columnName = "DEVICE_NO") 
    private String deviceNo;
	@DatabaseField(columnName = "GATEWAY_NO") 
    private String gatewayNo;
	@DatabaseField(columnName = "DEVICE_STATE_CMD") 
    private String deviceStateCmd;
	@DatabaseField(columnName = "INFRA_TYPE_ID")     //红外控制的类型,电视,遥控
	private Integer  infraTypeId; 
	
	public String getThemeNo() {
		return themeNo;
	}
	public void setThemeNo(String themeNo) {
		this.themeNo = themeNo;
	}
	public String getThemeDeviceNo() {
		return themeDeviceNo;
	}
	public void setThemeDeviceNo(String themeDeviceNo) {
		this.themeDeviceNo = themeDeviceNo;
	}
	public Integer getThemeType() {
		return themeType;
	}
	public void setThemeType(Integer themeType) {
		this.themeType = themeType;
	}
	public String getThemeState() {
		return themeState;
	}
	public void setThemeState(String themeState) {
		this.themeState = themeState;
	}
	public String getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public String getDeviceStateCmd() {
		return deviceStateCmd;
	}
	public void setDeviceStateCmd(String deviceStateCmd) {
		this.deviceStateCmd = deviceStateCmd;
	}
	public Integer getInfraTypeId() {
		return infraTypeId;
	}
	public void setInfraTypeId(Integer infraTypeId) {
		this.infraTypeId = infraTypeId;
	}
	public ThemeDevice() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ThemeDevice(String themeNo, String themeDeviceNo, Integer themeType,
			String themeState, String deviceNo, String gatewayNo,
			String deviceStateCmd, Integer infraTypeId) {
		super();
		this.themeNo = themeNo;
		this.themeDeviceNo = themeDeviceNo;
		this.themeType = themeType;
		this.themeState = themeState;
		this.deviceNo = deviceNo;
		this.gatewayNo = gatewayNo;
		this.deviceStateCmd = deviceStateCmd;
		this.infraTypeId = infraTypeId;
	}
	@Override
	public String toString() {
		return "ThemeDevice [themeNo=" + themeNo + ", themeDeviceNo="
				+ themeDeviceNo + ", themeType=" + themeType + ", themeState="
				+ themeState + ", deviceNo=" + deviceNo + ", gatewayNo="
				+ gatewayNo + ", deviceStateCmd=" + deviceStateCmd + "]";
	}
	
	
	

}
