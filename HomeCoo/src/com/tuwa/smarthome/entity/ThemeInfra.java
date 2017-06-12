package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "themeInfra") 
public class ThemeInfra {
	@DatabaseField(columnName = "THEME_NO")       
    private String themeNo;
	@DatabaseField(columnName = "DEVICE_NO") 
    private String deviceNo;
	@DatabaseField(columnName = "INFRA_TYPE_ID")     //红外控制的类型
	private Integer  infraTypeId; 
	@DatabaseField(columnName = "INFRA_CRL_NAME")   //红外操作名称
    private String infraControlName;
	@DatabaseField(columnName = "DEVICE_STATE_CMD") 
	private String  deviceStateCmd;
	@DatabaseField(columnName = "GATEWAY_NO") 
	private String  gatewayNo;
	public String getThemeNo() {
		return themeNo;
	}
	public void setThemeNo(String themeNo) {
		this.themeNo = themeNo;
	}
	public String getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	public Integer getInfraTypeId() {
		return infraTypeId;
	}
	public void setInfraTypeId(Integer infraTypeId) {
		this.infraTypeId = infraTypeId;
	}
	public String getInfraControlName() {
		return infraControlName;
	}
	public void setInfraControlName(String infraControlName) {
		this.infraControlName = infraControlName;
	}
	public String getDeviceStateCmd() {
		return deviceStateCmd;
	}
	public void setDeviceStateCmd(String deviceStateCmd) {
		this.deviceStateCmd = deviceStateCmd;
	}
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public ThemeInfra(String themeNo, String deviceNo, Integer infraTypeId,
			String infraControlName, String deviceStateCmd, String gatewayNo) {
		super();
		this.themeNo = themeNo;
		this.deviceNo = deviceNo;
		this.infraTypeId = infraTypeId;
		this.infraControlName = infraControlName;
		this.deviceStateCmd = deviceStateCmd;
		this.gatewayNo = gatewayNo;
	}
	public ThemeInfra() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
