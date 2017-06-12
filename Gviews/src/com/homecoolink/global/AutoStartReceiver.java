package com.homecoolink.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.homecoolink.data.SharedPreferencesManager;
import com.homecoolink.entity.Account;

public class AutoStartReceiver extends BroadcastReceiver{
	private static final String action_boot="android.intent.action.BOOT_COMPLETED";
	Context mContext;
	public static boolean isMainActivityStart;
	@Override
    public void onReceive(Context context, Intent intent) {
		Log.e("my","************************************************");
		mContext = context;
		
        if (intent.getAction().equals(action_boot)){
        	Account activeUser = AccountPersist.getInstance().getActiveAccountInfo(context);
        	
        	if(null!=activeUser&&null!=activeUser.three_number&&!"".equals(activeUser.three_number)){
        		 if(SharedPreferencesManager.getInstance().getIsAutoStart(context, activeUser.three_number)){
        			 Log.e("my","execute");
        			 isMainActivityStart = false;
        			 NpcCommon.mThreeNum = activeUser.three_number;
        			 
            		 Intent service = new Intent(MyApp.MAIN_SERVICE_START);
                     context.startService(service);
                     MyApp.app.showNotification();
        		 }
        		 
        	}
        	
        	
        	
        	
           
        }
        Log.e("my","************************************************");
    }
}
