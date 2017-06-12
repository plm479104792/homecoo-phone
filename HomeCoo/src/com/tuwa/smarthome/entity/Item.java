package com.tuwa.smarthome.entity;


/**
 * @类名    Item
 * @创建者   ppa
 * @创建时间 2016-4-17
 * @描述   TODO
 */
public class Item {
	String deviceNo;
	String deviceStateCmd;
	int dataLen;
	int deviceType;
	
	public String getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	public String getDeviceStateCmd() {
		return deviceStateCmd;
	}
	public void setDeviceStateCmd(String deviceStateCmd) {
		this.deviceStateCmd = deviceStateCmd;
	}
	public int getDataLen() {
		return dataLen;
	}
	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public Item(String deviceNo, String deviceStateCmd, int dataLen,
			int deviceType) {
		super();
		this.deviceNo = deviceNo;
		this.deviceStateCmd = deviceStateCmd;
		this.dataLen = dataLen;
		this.deviceType = deviceType;
	}
	public Item() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
