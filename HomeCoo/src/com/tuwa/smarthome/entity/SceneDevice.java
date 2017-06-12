package com.tuwa.smarthome.entity;

public class SceneDevice {
	
	private String userId;
	private int id;
	private int sceneId;
	private String devState;
	  
	  
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSceneId() {
		return sceneId;
	}
	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
	}
	public String getDevState() {
		return devState;
	}
	public void setDevState(String devState) {
		this.devState = devState;
	}
	public SceneDevice() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SceneDevice(String userId, int id, int sceneId, String devState) {
		super();
		this.userId = userId;
		this.id = id;
		this.sceneId = sceneId;
		this.devState = devState;
	}

	
	

	  
	  
	  
	  
}
