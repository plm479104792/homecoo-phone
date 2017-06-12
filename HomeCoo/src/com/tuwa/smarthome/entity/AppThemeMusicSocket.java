package com.tuwa.smarthome.entity;

import java.util.Arrays;


/**
 * @Description:这个实体类主要是用与放在 musicorderde bz 上面   内网添加情景音乐使用
 * */
public class AppThemeMusicSocket {
	
	private byte[] songName;
	private String gatewayNo;
	private String themeNo;
	private String deviceNo;		//硬件情景面板
	private String deviceState;     //情景面板上的设置状态
	private byte[] themeName;
	private String style;
	private String space;
	private String bz;
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
	public byte[] getSongName() {
		return songName;
	}
	public void setSongName(byte[] songName) {
		this.songName = songName;
	}
	public byte[] getThemeName() {
		return themeName;
	}
	public void setThemeName(byte[] themeName) {
		this.themeName = themeName;
	}
	@Override
	public String toString() {
		return "AppThemeMusicSocket [songName=" + Arrays.toString(songName)
				+ ", gatewayNo=" + gatewayNo + ", themeNo=" + themeNo
				+ ", deviceNo=" + deviceNo + ", deviceState=" + deviceState
				+ ", themeName=" + Arrays.toString(themeName) + ", style="
				+ style + ", space=" + space + ", bz=" + bz + "]";
	}
	
}
