package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "alarmMessage") 
public class AlarmMessage {
	@DatabaseField(columnName = "ID",generatedId = true,allowGeneratedIdInsert=true)
    private int id;
	@DatabaseField(columnName = "DEVICE_NO")
	private String deviceNo;
	@DatabaseField(columnName = "GATEWAY_NO")
	private String gatewayNo;
	@DatabaseField(columnName = "TIME")
	private long time;
	@DatabaseField(columnName = "NOTE")
	private String note;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public AlarmMessage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AlarmMessage(int id, String deviceNo, String gatewayNo, long time,
			String note) {
		super();
		this.id = id;
		this.deviceNo = deviceNo;
		this.gatewayNo = gatewayNo;
		this.time = time;
		this.note = note;
	}
	
}
