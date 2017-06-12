package com.tuwa.smarthome.entity;

public class Music {
	private int id;
	private String uuid;
	private String wgid;
	private String familyName;
	private String songName;
	private String space;			//空间名称(多个七寸屏)   备用
	private String bz;
	public Music() {
		super();
	}
	public Music(int id, String uuid, String wgid, String familyName,
			String songName, String space, String bz) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.wgid = wgid;
		this.familyName = familyName;
		this.songName = songName;
		this.space = space;
		this.bz = bz;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getWgid() {
		return wgid;
	}
	public void setWgid(String wgid) {
		this.wgid = wgid;
	}
	
	public String getSpace() {
		return space;
	}
	public void setSpace(String space) {
		this.space = space;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	@Override
	public String toString() {
		return "Music [id=" + id + ", uuid=" + uuid + ", wgid=" + wgid
				+ ", familyName=" + familyName + ", songName=" + songName
				+ ", space=" + space + ", bz=" + bz + "]";
	}
	
}
