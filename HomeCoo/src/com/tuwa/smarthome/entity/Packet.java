package com.tuwa.smarthome.entity;

import java.util.Date;

public class Packet {
    private Integer packetId;

    private String gatewayNo;

    private String packet;

    private String themeNo;
    
    private Date createTime;

    private Integer createBy;
    
    private Date updateTime;

    private Integer updateBy;
    

    public Integer getPacketId() {
        return packetId;
    }

    public void setPacketId(Integer packetId) {
        this.packetId = packetId;
    }

    public String getGatewayNo() {
        return gatewayNo;
    }

    public void setGatewayNo(String gatewayNo) {
        this.gatewayNo = gatewayNo;
    }

    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getThemeNo() {
		return themeNo;
	}

	public void setThemeNo(String themeNo) {
		this.themeNo = themeNo;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Integer updateBy) {
		this.updateBy = updateBy;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	@Override
	public String toString() {
		return "Packet [packetId=" + packetId + ", gatewayNo=" + gatewayNo
				+ ", packet=" + packet + ", themeNo=" + themeNo
				+ ", createTime=" + createTime + ", createBy=" + createBy
				+ ", updateTime=" + updateTime + ", updateBy=" + updateBy + "]";
	}

	

}