package com.tuwa.smarthome.entity;

import java.util.ArrayList;

/**
 * @类名 ThemeData
 * @创建者 ppa
 * @创建时间 2016-4-17
 * @描述 TODO
 * 发送给网关的情景填充到报文的data中
 */
public class ThemeData {
	String deviceNo;
	String themeNo;
	String themeState;
	Integer themeType;
	String themeName;
	Integer triggerNum;    // 触发情景的设备数量
	Integer deviceNum;     // 联动的设备数量
	public ArrayList<Item> triggerList;
	public ArrayList<Item> deviceList;

	
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

	public String getThemeState() {
		return themeState;
	}

	public void setThemeState(String themeState) {
		this.themeState = themeState;
	}

	public Integer getThemeType() {
		return themeType;
	}

	public void setThemeType(Integer themeType) {
		this.themeType = themeType;
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public Integer getTriggerNum() {
		return triggerNum;
	}

	public void setTriggerNum(Integer triggerNum) {
		this.triggerNum = triggerNum;
	}

	public Integer getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(Integer deviceNum) {
		this.deviceNum = deviceNum;
	}

	public ArrayList<Item> getTriggerList() {
		return triggerList;
	}

	public void setTriggerList(ArrayList<Item> triggerList) {
		this.triggerList = triggerList;
	}

	public ArrayList<Item> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(ArrayList<Item> sceneList) {
		this.deviceList = sceneList;
	}

	public ThemeData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ThemeData(String deviceNo, String themeId, String themeState,
			Integer themeType, String themeName, Integer triggerNum,
			Integer deviceNum, ArrayList<Item> triggerList,
			ArrayList<Item> sceneList) {
		super();
		this.deviceNo = deviceNo;
		this.themeNo = themeId;
		this.themeState = themeState;
		this.themeType = themeType;
		this.themeName = themeName;
		this.triggerNum = triggerNum;
		this.deviceNum = deviceNum;
		this.triggerList = triggerList;
		this.deviceList = sceneList;
	}

}
