package com.tuwa.smarthome.entity;

import java.util.Date;

public class Schedule {
    private Integer scheduleId;	//主键 (你新加的设置为 null 之前设置了点 传的时候传这个   删除定时任务 传这个scheduleID  或者 整个对象都行)
    private String xingqi;		//礼拜
    private String riqi;			//日期(年月日 时分秒)
    private String shij;			//时分秒   
    private String state;		//状态  0 代表重复执行的不变 1 代表一次执行的 2 代表一次执行完后的状态 3表示音乐  4表示红外定时
    private String strategy;	//策略  1一次性的  2 周期性
    private String phoneNum;
    private String deviceNo;
    private String themeNo;
    private String gatewayNo;
    private String deviceState;        //定时的设备状态
    private String scheduleName;      //定时任务名称
    private String packetData;		   //报文
    private Date createTime;
    private String createBy;
    
    
	public Integer getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getXingqi() {
		return xingqi;
	}
	public void setXingqi(String xingqi) {
		this.xingqi = xingqi;
	}
	public String getRiqi() {
		return riqi;
	}
	public void setRiqi(String riqi) {
		this.riqi = riqi;
	}
	public String getShij() {
		return shij;
	}
	public void setShij(String shij) {
		this.shij = shij;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStrategy() {
		return strategy;
	}
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	public String getThemeNo() {
		return themeNo;
	}
	public void setThemeNo(String themeNo) {
		this.themeNo = themeNo;
	}
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public String getDeviceState() {
		return deviceState;
	}
	public void setDeviceState(String deviceState) {
		this.deviceState = deviceState;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String timerTaskName) {
		this.scheduleName = timerTaskName;
	}
	public String getPacketData() {
		return packetData;
	}
	public void setPacketData(String packetData) {
		this.packetData = packetData;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	
	public Schedule() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Schedule(Integer scheduleId, String xingqi, String riqi, String shij,
			String state, String strategy, String phoneNum, String deviceNo,
			String themeNo, String gatewayNo, String deviceState,
			String timerTaskName, String packetData, Date createTime,
			String createBy) {
		super();
		this.scheduleId = scheduleId;
		this.xingqi = xingqi;
		this.riqi = riqi;
		this.shij = shij;
		this.state = state;
		this.strategy = strategy;
		this.phoneNum = phoneNum;
		this.deviceNo = deviceNo;
		this.themeNo = themeNo;
		this.gatewayNo = gatewayNo;
		this.deviceState = deviceState;
		this.scheduleName = timerTaskName;
		this.packetData = packetData;
		this.createTime = createTime;
		this.createBy = createBy;
	}
    
    
}