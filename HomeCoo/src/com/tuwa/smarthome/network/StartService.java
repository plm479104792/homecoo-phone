package com.tuwa.smarthome.network;

import cn.jpush.android.api.JPushInterface;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class StartService extends Service {  
	private JpushReceive mMessageReceiver;//接收服务器推送的极光广播
	public static final String MESSAGE_RECEIVED_ACTION = "com.tuwa.smarthome.permission.JPUSH_MESSAGE";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}   

  
    @Override  
    public void onCreate() {  
        // TODO Auto-generated method stub  
        super.onCreate();  
        JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志  
        JPushInterface.init(this); // 初始化 JPush  
        JPushInterface.setLatestNotificationNumber(getApplicationContext(),3);// 保留多少条通知数  
   
        registerMessageReceiver();//注册极光推送接收端
    
    }
    
	//注册极光推送接收端
	public void registerMessageReceiver() {
		mMessageReceiver = new JpushReceive();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
		System.out.println("===极光接收端已经启动===");
	}
	
	@Override
	public void onDestroy() {
		  unregisterReceiver(mMessageReceiver);  //取消接收服务器极光广播
		super.onDestroy();
	}

  
} 
