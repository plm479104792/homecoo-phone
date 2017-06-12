package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "gateway") 
public class Gateway {
	@DatabaseField(columnName = "GATEWAY_NO")    //网关编号
    private String gatewayNo;
	@DatabaseField(columnName = "GATEWAY_IP") 
    private String gatewayIp;
	@DatabaseField(columnName = "GATEWAY_PWD") 
    private String gatewayPwd;
	
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public String getGatewayIp() {
		return gatewayIp;
	}
	public void setGatewayIp(String gatewayIp) {
		this.gatewayIp = gatewayIp;
	}
	public String getGatewayPwd() {
		return gatewayPwd;
	}
	public void setGatewayPwd(String gatewayPwd) {
		this.gatewayPwd = gatewayPwd;
	}
	public Gateway() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Gateway(String gatewayNo, String gatewayIp, String gatewayPwd) {
		super();
		this.gatewayNo = gatewayNo;
		this.gatewayIp = gatewayIp;
		this.gatewayPwd = gatewayPwd;
	}
	
}
