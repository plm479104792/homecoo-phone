package com.tuwa.smarthome.entity;

import java.util.Arrays;

/**
 * @Description:socket传输，只是传输byte[] 所以用这个类接收  用于内网音乐
 * */
public class MusicSocket {

	
	private Integer id;
	private String style;		//1:表示 音乐列表    2:表示控制命令
	private byte[] songName;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public byte[] getSongName() {
		return songName;
	}
	public void setSongName(byte[] songName) {
		this.songName = songName;
	}
	@Override
	public String toString() {
		return "MusicSocket [id=" + id + ", style=" + style + ", songName="
				+ Arrays.toString(songName) + "]";
	}
	
}
