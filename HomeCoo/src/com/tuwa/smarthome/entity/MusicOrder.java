package com.tuwa.smarthome.entity;

/*	
 * @author xiaobai
 * @Description:音乐控制类
 * */
public class MusicOrder {

	private String songName;	//歌曲名称
	private String wgid;		//网关id
	private String order;			//1:暂停     2:播放    3:上一首     4:下一首    5:单曲循环  6:列表循环   7:随机循环
	private String style;			//备用
	private String bz;			//备用
	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
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
		return "MusicOrder [songName=" + songName + ", wgid=" + wgid
				+ ", order=" + order + ", style=" + style + ", bz=" + bz + "]";
	}
	
}
