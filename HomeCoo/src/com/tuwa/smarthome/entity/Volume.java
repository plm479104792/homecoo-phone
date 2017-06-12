package com.tuwa.smarthome.entity;

public class Volume {
	private Integer id;
	private String gatewayNo;
	private String volume;
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
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	@Override
	public String toString() {
		return "Volume [id=" + id + ", gatewayNo=" + gatewayNo + ", volume="
				+ volume + "]";
	}
	
}
