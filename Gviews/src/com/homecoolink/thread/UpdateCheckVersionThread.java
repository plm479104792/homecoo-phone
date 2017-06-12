package com.homecoolink.thread;

import android.os.Handler;
import android.os.Message;

import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.utils.Utils;
import com.p2p.core.update.UpdateManager;

public class UpdateCheckVersionThread extends Thread{
	boolean isNeedUpdate = false;
	Handler handler;
	public UpdateCheckVersionThread(Handler handler){
		this.handler = handler;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			isNeedUpdate = UpdateManager.getInstance().checkUpdate();
			if(isNeedUpdate){ 
				Message msg = new Message();
				msg.what = Constants.Update.CHECK_UPDATE_HANDLE_TRUE;
				String data = "";
				if(Utils.isZh(MyApp.app)){
					data = UpdateManager.getInstance().getUpdateDescription();
				}else{
					data = UpdateManager.getInstance().getUpdateDescription_en();
				}
				msg.obj = data;
				handler.sendMessage(msg);
			}else{
				Message msg = new Message();
				msg.what = Constants.Update.CHECK_UPDATE_HANDLE_FALSE;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
