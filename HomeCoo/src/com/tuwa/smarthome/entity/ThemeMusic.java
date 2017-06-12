package com.tuwa.smarthome.entity;

public class ThemeMusic {
	private Integer id;
	private String songName;
	private String gatewayNo;
	private String themeNo;
	private String deviceNo;		//硬件情景面板
	private String deviceState;     //情景面板上的设置状态
	private String themeName;
	private String style;
	private String space;
	private String bz;
	public ThemeMusic() {
		super();
	}
	
	
	
	public ThemeMusic(Integer id, String songName, String gatewayNo,
			String themeNo, String deviceNo, String deviceState,
			String themeName, String style, String space, String bz) {
		super();
		this.id = id;
		this.songName = songName;
		this.gatewayNo = gatewayNo;
		this.themeNo = themeNo;
		this.deviceNo = deviceNo;
		this.deviceState = deviceState;
		this.themeName = themeName;
		this.style = style;
		this.space = space;
		this.bz = bz;
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



	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
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
	public String getThemeName() {
		return themeName;
	}
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "ThemeMusic [id=" + id + ", songName=" + songName
				+ ", gatewayNo=" + gatewayNo + ", themeNo=" + themeNo
				+ ", deviceNo=" + deviceNo + ", deviceState=" + deviceState
				+ ", themeName=" + themeName + ", style=" + style + ", space="
				+ space + ", bz=" + bz + "]";
	}
	
}
