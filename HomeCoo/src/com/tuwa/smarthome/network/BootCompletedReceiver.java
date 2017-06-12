package com.tuwa.smarthome.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {  

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {  
            System.out.println("检测到开机启动，去启动服务");
            Intent newIntent = new Intent(context, StartService.class);  
            context.startService(newIntent);  
        }  
		
	}  
}  