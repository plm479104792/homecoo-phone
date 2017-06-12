package com.homecoolink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.homecoolink.R;
import com.homecoolink.global.Constants;


public class LogoActivity extends BaseActivity{
	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {			
			setContentView(R.layout.activity_logo);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("343", Log.getStackTraceString(e));
			Intent i = new Intent(LogoActivity.this,MainActivity.class);
			startActivity(i);
			finish();
		}
		
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Intent i = new Intent(LogoActivity.this,MainActivity.class);
				startActivity(i);
				finish();
			}
			
		};
		Message msg = new Message();
		msg.what = 0x11;
		handler.sendMessageDelayed(msg, 2000);
	}
	
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_LOGOACTIVITY;
	}

}
