package com.tuwa.smarthome.entity;

import java.util.Arrays;

public class MusicSocketByte {
	
	private byte[] songName;
	private String wgid;		//网关id
	private String order;			// 0:获取歌曲列表  1:暂停     2:播放    3:上一首     4:下一首    5:单曲循环  6:列表循环   7:随机循环  8:获取音乐列表
									// 9:删除情景音乐   10:设置情景音乐    11:播放情景音乐     12:情景音乐设置暂停   15:内网下音量控制
	private String style;			//备用
	private String bz;			//备用
	public byte[] getSongName() {
		return songName;
	}
	public void setSongName(byte[] songName) {
		this.songName = songName;
	}
	public String getWgid() {
		return wgid;
	}
	public void setWgid(String wgid) {
		this.wgid = wgid;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	@Override
	public String toString() {
		return "MusicSocketByte [songName=" + Arrays.toString(songName)
				+ ", wgid=" + wgid + ", order=" + order + ", style=" + style
				+ ", bz=" + bz + "]";
	}
	
}
