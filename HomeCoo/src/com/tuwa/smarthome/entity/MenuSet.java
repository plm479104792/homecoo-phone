package com.tuwa.smarthome.entity;

public class MenuSet {
	private int imageId;
    private String title;
    
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
	public MenuSet() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MenuSet(int imageId, String title) {
		super();
		this.imageId = imageId;
		this.title = title;
	}


    
   
}
