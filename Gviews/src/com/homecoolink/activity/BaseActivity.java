package com.homecoolink.activity;


import com.homecoolink.global.MyApp;
import com.p2p.core.BaseCoreActivity;


public abstract class BaseActivity extends BaseCoreActivity{
	
	@Override
	protected void onGoBack() {
		// TODO Auto-generated method stub
		MyApp.app.showNotification();
	}

	@Override
	protected void onGoFront() {
		// TODO Auto-generated method stub
		MyApp.app.hideNotification();
	}

	@Override
	protected void onExit() {
		// TODO Auto-generated method stub
		MyApp.app.hideNotification();
	}
	
	@Override
	public abstract int getActivityInfo();
	
	
}
