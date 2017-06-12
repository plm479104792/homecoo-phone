package com.homecoolink.entity;

public class AlarmRecord {
	// id
	public int id;
	// 报警设备ID
	public String deviceId;
	// 报警类型
	public int alarmType;
	// 报警时间
	public String alarmTime;
	// 当前登录用户
	public String activeUser;
	//防区
	public int group;
	//通道
	public int item;
	
	//已读未读
	public int alarmStatus;
	
	//布局的风格
	public int layoutType;
	
//	public boolean isSupport;

	public AlarmRecord(int id, String deviceId, int alarmType,
			String alarmTime, String activeUser, int alarmStatus, int layoutType,int group,int item) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.alarmType = alarmType;
		this.alarmTime = alarmTime;
		this.activeUser = activeUser;
		this.alarmStatus = alarmStatus;
		this.layoutType = layoutType;
		this.group = group;
		this.item = item;
//		this.isSupport = isSupport;
	}
	
	
	
	//分组的标题
//	public String groupTitle;
}
