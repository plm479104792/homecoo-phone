package com.p2p.core.global;

import java.io.Serializable;

public class AlarmRecord implements Serializable{
	public String alarmTime;
	public int alarmType;
	public String alarmChannel;
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmChannel() {
		return alarmChannel;
	}
	public void setAlarmChannel(String alarmChannel) {
		this.alarmChannel = alarmChannel;
	}
	

}
