package com.homecoolink.global;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.homecoolink.R;
import com.homecoolink.activity.ForwardActivity;
import com.homecoolink.activity.ForwardDownActivity;
import com.homecoolink.data.SharedPreferencesManager;
import com.p2p.core.P2PHandler;
import com.p2p.core.global.AlarmRecord;
import com.p2p.core.update.UpdateManager;

public class MyApp extends Application {
	public static final String MAIN_SERVICE_START = Constants.PACKAGE_NAME
			+ "service.MAINSERVICE";
	public static final int NOTIFICATION_DOWN_ID = 0x53256562;
	public static MyApp app;
	public static boolean isActive;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private RemoteViews cur_down_view;
	public static String GACID = "";
	public static String GACPWD = "";
	public static String CALLID;
	public static Context context;
	public static ArrayList<AlarmRecord> tempremotelist = new ArrayList<AlarmRecord>();
	public static int RecordControlType = -100;
	
	public static String userName,passWord;
	public  ArrayList<Activity> list = new ArrayList<Activity>(); 
	
	@Override
	public void onCreate() {
		context = this.getApplicationContext();
		app = this;
		super.onCreate();
		isActive = true;

	}

	public static void GetRemote(String cid, String cpwd) {
		if (GACID.equals("")) {
			GACID = cid;
			GACPWD = cpwd;
			tempremotelist.clear();
			tempremotelist = new ArrayList<AlarmRecord>();

			try {
				P2PHandler.getInstance().getAlarmRecord(GACID, GACPWD);
			} catch (Exception se) {
			}
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					if (!GACID.equals("")) {
						GACID = "";
						GACPWD = "";
					}
				}
			}.start();
		}
	}

	public static void ReGetRemote() {
		if (!GACID.equals("")) {
			tempremotelist.clear();
			tempremotelist = new ArrayList<AlarmRecord>();
			P2PHandler.getInstance().getAlarmRecord(GACID, GACPWD);
		}
	}

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	/**
	 * 创建挂机图标
	 */
	@SuppressWarnings("deprecation")
	public void showNotification() {
		boolean isShowNotify = SharedPreferencesManager.getInstance()
				.getIsShowNotify(this);
		if (isShowNotify) {
			mNotificationManager = getNotificationManager();
			mNotification = new Notification();

			long when = System.currentTimeMillis();
			mNotification = new Notification(R.drawable.ic_launcher, this
					.getResources().getString(R.string.app_name), when);

			// 放置在"正在运行"栏目中
			mNotification.flags = Notification.FLAG_ONGOING_EVENT;

			RemoteViews contentView = new RemoteViews(getPackageName(),
					R.layout.notify_status_bar);
			contentView.setImageViewResource(R.id.icon, R.drawable.ic_launcher);
			contentView.setTextViewText(
					R.id.title,
					this.getResources().getString(R.string.app_name)
							+ " "
							+ this.getResources().getString(
									R.string.running_in_the_background));
			// contentView.setTextViewText(R.id.text, "");
			// contentView.setLong(R.id.time, "setTime", when);
			// 指定个性化视图
			mNotification.contentView = contentView;

			Intent intent = new Intent(this, ForwardActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			// 指定内容意图
			mNotification.contentIntent = contentIntent;
			mNotificationManager.notify(R.string.app_name, mNotification);
		}
	}

	public void hideNotification() {
		mNotificationManager = getNotificationManager();
		mNotificationManager.cancel(R.string.app_name);
	}

	/**
	 * 创建下载图标
	 */
	@SuppressWarnings("deprecation")
	public void showDownNotification(int state, int value) {
		boolean isShowNotify = SharedPreferencesManager.getInstance()
				.getIsShowNotify(this);
		if (isShowNotify) {
			mNotificationManager = getNotificationManager();
			mNotification = new Notification();

			long when = System.currentTimeMillis();
			mNotification = new Notification(R.drawable.ic_launcher, this
					.getResources().getString(R.string.app_name), when);

			// 放置在"正在运行"栏目中
			mNotification.flags = Notification.FLAG_ONGOING_EVENT
					| Notification.FLAG_AUTO_CANCEL;

			RemoteViews contentView = new RemoteViews(getPackageName(),
					R.layout.notify_down_bar);
			cur_down_view = contentView;
			contentView.setImageViewResource(R.id.icon, R.drawable.ic_launcher);

			Intent intent = new Intent(this, ForwardDownActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			switch (state) {
			case UpdateManager.HANDLE_MSG_DOWN_SUCCESS:
				cur_down_view
						.setTextViewText(
								R.id.down_complete_text,
								this.getResources().getString(
										R.string.down_complete_click));
				cur_down_view.setTextViewText(R.id.progress_value, "100%");
				contentView.setProgressBar(R.id.progress_bar, 100, 100, false);

				intent.putExtra("state", UpdateManager.HANDLE_MSG_DOWN_SUCCESS);

				break;
			case UpdateManager.HANDLE_MSG_DOWNING:
				cur_down_view.setTextViewText(R.id.down_complete_text, this
						.getResources().getString(R.string.down_londing_click));
				cur_down_view.setTextViewText(R.id.progress_value, value + "%");
				contentView
						.setProgressBar(R.id.progress_bar, 100, value, false);

				intent.putExtra("state", UpdateManager.HANDLE_MSG_DOWNING);
				break;
			case UpdateManager.HANDLE_MSG_DOWN_FAULT:
				cur_down_view.setTextViewText(R.id.down_complete_text, this
						.getResources().getString(R.string.down_fault_click));
				cur_down_view.setTextViewText(R.id.progress_value, value + "%");
				contentView
						.setProgressBar(R.id.progress_bar, 100, value, false);

				intent.putExtra("state", UpdateManager.HANDLE_MSG_DOWN_FAULT);
				break;
			}
			mNotification.contentView = contentView;
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mNotification.contentIntent = contentIntent;

			mNotificationManager.notify(NOTIFICATION_DOWN_ID, mNotification);
		}
	}

	/**
	 * @Title: hideDownNotification
	 * @Description: (取消消息栏中的消息)
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void hideDownNotification() {
		mNotificationManager = getNotificationManager();
		mNotificationManager.cancel(NOTIFICATION_DOWN_ID);
	}

	/**
	 * 创建挂机图标
	 */
	private int ntifindex = 9999;

	@SuppressWarnings("deprecation")
	public void showAlarmNotification(String eve, String dvid, String atime,
			String fangqu) {

		boolean isShowNotify = SharedPreferencesManager.getInstance()
				.getIsShowNotify(this);
		if (isShowNotify) {
			hideAlarmNotification();
			mNotificationManager = getNotificationManager();
			mNotification = new Notification();
			StringBuffer sb = new StringBuffer();
			sb.append(this.getResources().getString(R.string.app_name) + " ");
			sb.append(this.getResources().getString(R.string.with_blank) + " ");
			sb.append(eve + " ");
			sb.append(this.getResources().getString(
					R.string.alarmrecorddetail_sbid)
					+ " ");
			sb.append(dvid + " ");
			if (fangqu != null) {
				sb.append(this.getResources()
						.getString(R.string.alarm_defence2)
						+ " "
						+ fangqu
						+ " ");
			}
			sb.append(this.getResources().getString(
					R.string.alarmrecorddetail_time)
					+ " ");
			sb.append(atime);

			long when = System.currentTimeMillis();
			mNotification = new Notification(R.drawable.ic_launcher,
					sb.toString(), when);

			mNotification.sound = Uri.parse("android.resource://"
					+ getPackageName() + "/" + R.raw.alarmnotify);
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;

			RemoteViews contentView = new RemoteViews(getPackageName(),
					R.layout.notify_alarm_bar);
			contentView.setImageViewResource(R.id.icon, R.drawable.ic_launcher);
			contentView.setTextViewText(R.id.title, this.getResources()
					.getString(R.string.with_blank) + " " + eve);
			contentView.setTextViewText(R.id.tcontent, this.getResources()
					.getString(R.string.alarmrecorddetail_sbid) + " " + dvid);

			if (fangqu != null) {
				contentView.setViewVisibility(R.id.fangqu, View.VISIBLE);
				contentView.setTextViewText(R.id.fangqu, this.getResources()
						.getString(R.string.alarm_defence2) + " " + fangqu);
			}

			contentView.setTextViewText(R.id.timestr, this.getResources()
					.getString(R.string.alarmrecorddetail_time) + " " + atime);
			// contentView.setTextViewText(R.id.text, "");
			// contentView.setLong(R.id.time, "setTime", when);
			// 指定个性化视图
			mNotification.contentView = contentView;
			// mNotification.bigContentView = contentView;
			mNotificationManager.notify(ntifindex, mNotification);

		}
	}

	public void hideAlarmNotification() {
		mNotificationManager = getNotificationManager();
		mNotificationManager.cancel(ntifindex);
	}

	/*********************APP闪退后捕获异常重新启动*************************/
	public void init(){  
        //设置该CrashHandler为程序的默认处理器    
//        UnCeHandler catchExcep = new UnCeHandler(this);  
//        Thread.setDefaultUncaughtExceptionHandler(catchExcep);   
    }  
      
    /** 
     * Activity关闭时，删除Activity列表中的Activity对象*/  
    public void removeActivity(Activity a){  
        list.remove(a);  
    }  
      
    /** 
     * 向Activity列表中添加Activity对象*/  
    public void addActivity(Activity a){  
        list.add(a);  
    }  
      
    /** 
     * 关闭Activity列表中的所有Activity*/  
    public void finishActivity(){  
        for (Activity activity : list) {    
            if (null != activity) {    
                activity.finish();    
            }    
        }  
        //杀死该应用进程  
       android.os.Process.killProcess(android.os.Process.myPid());    
    }  
	
}
