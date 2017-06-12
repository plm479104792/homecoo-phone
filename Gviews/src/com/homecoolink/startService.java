package com.homecoolink;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class startService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	@Deprecated
	public void onStart(Intent i, int startId) {
		// TODO Auto-generated method stub
		super.onStart(i, startId);
		
		System.out.println("发送异常捕获广播");
		
		Intent intent = new Intent();  //Itent就是我们要发送的内容
        intent.setAction("restart");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
        sendBroadcast(intent);   
		
	}

}
