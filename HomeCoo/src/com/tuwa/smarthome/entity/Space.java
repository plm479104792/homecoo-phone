package com.tuwa.smarthome.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "space") 
public class Space {
	@DatabaseField(columnName = "SPACE_ID",generatedId = true,allowGeneratedIdInsert=true)
	private Integer spaceId;    
    @DatabaseField(columnName = "SPACE_NO") 
    private String spaceNo; 
    @DatabaseField(columnName = "SPACE_NAME") 
    private String spaceName;
    @DatabaseField(columnName = "GATEWAY_NO") 
    private String gatewayNo;  
    @DatabaseField(columnName = "PHONE_NUM") 
    private String phoneNum;    
    
	public Integer getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(Integer spaceId) {
		this.spaceId = spaceId;
	}
	public String getSpaceNo() {
		return spaceNo;
	}
	public void setSpaceNo(String spaceNo) {
		this.spaceNo = spaceNo;
	}
	public String getSpaceName() {
		return spaceName;
	}
	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}
	public String getGatewayNo() {
		return gatewayNo;
	}
	public void setGatewayNo(String gatewayNo) {
		this.gatewayNo = gatewayNo;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public Space() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Space(Integer spaceId, String spaceNo, String spaceName,
			String gatewayNo, String phoneNum) {
		super();
		this.spaceId = spaceId;
		this.spaceNo = spaceNo;
		this.spaceName = spaceName;
		this.gatewayNo = gatewayNo;
		this.phoneNum = phoneNum;
	}

}
