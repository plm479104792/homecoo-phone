package com.tuwa.smarthome.entity;

public class DevWidetype {
	
	private int imageId;
    private String title;
    private int widetypeId;
    
    public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getWidetypeId() {
		return widetypeId;
	}
	public void setWidetypeId(int widetypeId) {
		this.widetypeId = widetypeId;
	}
	public DevWidetype() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DevWidetype(int imageId, String title, int widetypeId) {
		super();
		this.imageId = imageId;
		this.title = title;
		this.widetypeId = widetypeId;
	}

      
    
	
}
