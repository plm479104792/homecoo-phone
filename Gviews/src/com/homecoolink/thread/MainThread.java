package com.homecoolink.thread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.homecoolink.data.SharedPreferencesManager;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.MyApp;
import com.homecoolink.utils.Utils;
import com.p2p.core.update.UpdateManager;

public class MainThread {
	static MainThread manager;
	boolean isRun;
	private String version;
	private int serVersion;
	private static final long SYSTEM_MSG_INTERVAL = 60 * 60 * 1000;
	long lastSysmsgTime;
	private Main main;
	Context context;
	private static boolean isOpenThread;

	public MainThread(Context context) {
		manager = this;
		this.context = context;
	}

	public static MainThread getInstance() {
		return manager;

	}

	class Main extends Thread {
		@Override
		public void run() {
			isRun = true;
			Utils.sleepThread(3000);
			while (isRun) {
				if (isOpenThread == true) {
					checkUpdate();
					Log.e("my", "updateOnlineState");
					try {
						FList.getInstance().updateOnlineState();
						FList.getInstance().searchLocalDevice();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    Log.e("update", "update++");
					Utils.sleepThread(60 * 1000);
				}else{
					Utils.sleepThread(10 * 1000);
				}
				
			}
		}
	};



	public void go() {

		if (null == main || !main.isAlive()) {
			main = new Main();
			main.start();
		}
	}

	public void kill() {

		isRun = false;
		main = null;
	}

	
	public static void setOpenThread(boolean isOpenThread) {
		MainThread.isOpenThread = isOpenThread;
	}

	public void checkUpdate() {
		try {
			long last_check_update_time = SharedPreferencesManager
					.getInstance().getLastAutoCheckUpdateTime(MyApp.app);
			long now_time = System.currentTimeMillis();

			if ((now_time - last_check_update_time) > 1000 * 60 * 60 * 12) {
				SharedPreferencesManager.getInstance()
						.putLastAutoCheckUpdateTime(now_time, MyApp.app);
				Log.e("my", "后台检查更新");
				if (UpdateManager.getInstance().checkUpdate()) {
					String data = "";
					if (Utils.isZh(MyApp.app)) {
						data = UpdateManager.getInstance()
								.getUpdateDescription();
					} else {
						data = UpdateManager.getInstance()
								.getUpdateDescription_en();
					}
					Intent i = new Intent(Constants.Action.ACTION_UPDATE);
					i.putExtra("updateDescription", data);
					MyApp.app.sendBroadcast(i);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("my", "后台检查更新失败");
		}
	}

}
