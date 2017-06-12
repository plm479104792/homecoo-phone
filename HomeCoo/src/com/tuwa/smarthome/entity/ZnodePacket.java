package com.tuwa.smarthome.entity;

/**
 * @类名 ZnodePacket
 * @创建者 ppa
 * @创建时间 2016-4-15
 * @描述 设备安装报文
 */
public class ZnodePacket {
	private String deviceNo;
	private Integer deviceTypeId;
	private String deviceName;
	private String spaceName;
	private Integer spaceTypeId;

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public Integer getDeviceTypeId() {
		return deviceTypeId;
	}

	public void setDeviceTypeId(Integer deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}

	public Integer getSpaceTypeId() {
		return spaceTypeId;
	}

	public void setSpaceTypeId(Integer spaceTypeId) {
		this.spaceTypeId = spaceTypeId;
	}

	public ZnodePacket(String deviceNo, Integer deviceTypeId,
			String deviceName, String spaceName, Integer spaceTypeId) {
		super();
		this.deviceNo = deviceNo;
		this.deviceTypeId = deviceTypeId;
		this.deviceName = deviceName;
		this.spaceName = spaceName;
		this.spaceTypeId = spaceTypeId;
	}

	public ZnodePacket() {
		super();
		// TODO Auto-generated constructor stub
	}

}
