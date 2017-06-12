package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "appthememusic")
public class APPThemeMusic {

	@DatabaseField(columnName="SONGNAME")
	private String songName;
	@DatabaseField(columnName="GATEWAYNO")
	private String gatewayNo;
	@DatabaseField(columnName="THEMENO")
	private String themeNo;
	@DatabaseField(columnName="DEVICENO")
	private String deviceNo;		//硬件情景面板
	@DatabaseField(columnName="DEVICESTATE")
	private String deviceState;     //情景面板上的设置状态
	@DatabaseField(columnName="THEMENAME")
	private String themeName;
	@DatabaseField(columnName="STYLE")
	private String style;
	@DatabaseField(columnName="SPACE")
	private String space;
	@DatabaseField(columnName="BZ")
	private String bz;
	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
	}
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
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
	public String getDeviceState() {
		return deviceState;
	}
	public void setDeviceState(String deviceState) {
		this.deviceState = deviceState;
	}
	public String getThemeName() {
		return themeName;
	}
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getSpace() {
		return space;
	}
	public void setSpace(String space) {
		this.space = space;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	@Override
	public String toString() {
		return "APPThemeMusic [songName=" + songName + ", gatewayNo="
				+ gatewayNo + ", themeNo=" + themeNo + ", deviceNo=" + deviceNo
				+ ", deviceState=" + deviceState + ", themeName=" + themeName
				+ ", style=" + style + ", space=" + space + ", bz=" + bz + "]";
	}
	
}
