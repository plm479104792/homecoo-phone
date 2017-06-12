package com.tuwa.smarthome.entity;

import com.tuwa.smarthome.global.TranObjectType;


public class TranObject {
	private Object object;
	private TranObjectType tranType;
	
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public TranObjectType getTranType() {
		return tranType;
	}
	public void setTranType(TranObjectType tranType) {
		this.tranType = tranType;
	}
	public TranObject() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TranObject(Object object, TranObjectType tranType) {
		super();
		this.object = object;
		this.tranType = tranType;
	}
	
	
	
	
	
}
