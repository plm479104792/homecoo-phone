package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "appmusic")
public class APPMusic {
	
	@DatabaseField(columnName = "SONGNAME")
	private String songName;
	@DatabaseField(columnName = "GATEWAY_NO")
	private String gatewayNo;
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
	@Override
	public String toString() {
		return "APPMusic [songName=" + songName + ", gatewayNo=" + gatewayNo
				+ "]";
	}
}
