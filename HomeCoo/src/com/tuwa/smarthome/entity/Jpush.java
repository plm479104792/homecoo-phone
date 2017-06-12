package com.tuwa.smarthome.entity;
/**
 * 极光推送消息类
 * */
public class Jpush {
	private String gatewayNo;			//JPush推送别名  JPush根据gatewayNo推送到对应的网关下的所有手机
	private Object object;
	private int messsageType;			//1:设备状态更新  2:安防报警  3：音乐
	private long time;
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public int getMesssageType() {
		return messsageType;
	}
	public void setMesssageType(int messsageType) {
		this.messsageType = messsageType;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Jpush(String gatewayNo, Object object, int messsageType, long time) {
		super();
		this.gatewayNo = gatewayNo;
		this.object = object;
		this.messsageType = messsageType;
		this.time = time;
	}
	public Jpush() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
