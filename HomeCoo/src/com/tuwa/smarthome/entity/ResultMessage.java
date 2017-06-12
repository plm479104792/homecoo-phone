package com.tuwa.smarthome.entity;

import java.io.Serializable;

/**
 * @Project: Peace
 * @Title: Message
 * @Description: 消息对象类
 * @author: zw
 * @date :2016年3月26日 晚上22:26:49
 * @Copyright: Copyright (c) 2016
 * @version V1.0
 */
public class ResultMessage implements Serializable {

	private static final long serialVersionUID = 2991603577177353117L;
	private String phoneNum;
	private int generatedId;
	private String messageInfo;
	private String result = "success";
	private Object object;
	private String description;
	private String note;   //备注
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public int getGeneratedId() {
		return generatedId;
	}
	public void setGeneratedId(int generatedId) {
		this.generatedId = generatedId;
	}
	public String getMessageInfo() {
		return messageInfo;
	}
	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public ResultMessage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ResultMessage(String phoneNum, int generatedId, String messageInfo,
			String result, Object object, String description, String note) {
		super();
		this.phoneNum = phoneNum;
		this.generatedId = generatedId;
		this.messageInfo = messageInfo;
		this.result = result;
		this.object = object;
		this.description = description;
		this.note = note;
	}

}
