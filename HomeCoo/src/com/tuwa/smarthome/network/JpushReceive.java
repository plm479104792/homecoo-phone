package com.tuwa.smarthome.network;

import com.alibaba.fastjson.JSONObject;
import com.tuwa.smarthome.activity.DeviceWeiKongActivity;
import com.tuwa.smarthome.activity.LoginActivity;
import com.tuwa.smarthome.entity.Jpush;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;


public class JpushReceive extends BroadcastReceiver {

	@SuppressWarnings("unused")
	@Override
	public void onReceive(Context context, Intent intent) {
//		System.out.println("通过广播启动service模式");

		Bundle bundle=intent.getExtras();
		
		// 消息标题，对应 API 消息内容的 title 字段，Portal 推送消息界上不作展示
		if (bundle.containsKey(JPushInterface.EXTRA_TITLE)) {
			String titleString = bundle.getString(JPushInterface.EXTRA_TITLE);
		}
		
		// 附加字段,是个 JSON 字符串,对应 API 消息内容的 extras 字段.
		// 对应 Portal推送消息界面上的“可选设置”里的附加字段
		if (bundle.containsKey(JPushInterface.EXTRA_EXTRA)) {
			String extrasString = bundle.getString(JPushInterface.EXTRA_EXTRA);
		}
		
		// 内容类型，对应 API 消息内容的 content_type 字段
		if (bundle.containsKey(JPushInterface.EXTRA_CONTENT_TYPE)) {
			String typeString = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
		}
		
		// 唯一标识消息的 ID, 可用于上报统计等。
		if (bundle.containsKey(JPushInterface.EXTRA_MSG_ID)) {
			String msgIdString = bundle.getString(JPushInterface.EXTRA_MSG_ID);
		}
		
		/**
		 * 对action行为的判断
		 */
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			// SDK 向 JPush Server 注册所得到的注册 全局唯一的 ID ，可以通过此 ID 向对应的客户端发送消息和通知。
			String registrationIdString = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			
			String jpushMessage=bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			System.out.println("收到了自定义消息。消息内容是："
//					+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			
			if(NetValue.netFlag==NetValue.OUTERNET){
				//发送广播，在socketService中接收
				 Intent jpushIntent = new Intent();  //Itent就是我们要发送的内容
				 jpushIntent.putExtra("jpushMessage",jpushMessage );  
				 jpushIntent.setAction("ACTION_JPUSH_MESSAGE");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
				 Jpush jpush=JSONObject.parseObject(jpushMessage,Jpush.class); 
				 if ((jpush.getMesssageType()!=SystemValue.MUSIC_JPUSH) && (jpush.getMesssageType()!=SystemValue.THEME_MUSIC_JPUSH)) {
					 context.sendBroadcast(jpushIntent);   //发送广播
				}
			}
		
			
			
			// 消息内容,对应 API 消息内容的 message 字段,对应 Portal 推送消息界面上的"自定义消息内容”字段
			if (bundle.containsKey(JPushInterface.EXTRA_MESSAGE)) {
				String messageString = bundle
						.getString(JPushInterface.EXTRA_MESSAGE);
			}
//			processCustomMessage(context, bundle);//传送内容到主界面上
		} 
		else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
		//	System.out.println("收到了通知");
			// 在这里可以做些统计，或者做些其他工作

			// 通知的标题,对应 API 通知内容的 n_title 字段,对应 Portal 推送通知界面上的“通知标题”字段
			String notificationTileString = bundle
					.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			// 通知内容,对应 API 通知内容的 n_content 字段,对应 Portal 推送通知界面上的“通知内容”字段
			String alertString = bundle.getString(JPushInterface.EXTRA_ALERT);
			// 通知栏的Notification ID，可以用于清除Notification
			
			int notificationId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			System.out.println(notificationId+"接收到极光推送通知：标题："+notificationTileString+"内容"+alertString);
		} 
		else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			// 在这里可以自己写代码去定义用户点击后的行为
			JPushInterface.reportNotificationOpened(context,
					bundle.getString(JPushInterface.EXTRA_MSG_ID));// 用于上报用户的通知栏被打开
			
			if(SystemValue.loginFlag){
				Intent a=new Intent(context,DeviceWeiKongActivity.class);
				a.putExtra("operator_type", "notification");
				a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(a);
			}else{
				Intent loingIntent=new Intent(context,LoginActivity.class);
				loingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(loingIntent);
			}

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {// 网络断开，连接
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			// Toast.makeText(arg0, "网络连接" + connected,
			// Toast.LENGTH_SHORT).show();
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			// 接受富推送

			// 富文本页面 Javascript 回调API，获取参数参数 ”params“
			if (bundle.containsKey(JPushInterface.EXTRA_EXTRA)) {
				String params = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
			}

			// 富媒体通消息推送下载后的文件路径和文件名。
			if (bundle.containsKey(JPushInterface.EXTRA_RICHPUSH_FILE_PATH)) {
				String filePathString = bundle
						.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
			}

			// 富媒体通知推送下载的HTML的文件路径,用于展现WebView。
			if (bundle.containsKey(JPushInterface.EXTRA_RICHPUSH_HTML_PATH)) {
				String fileHtmlPath = bundle
						.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
			}

			// 富媒体通知推送下载的图片资源的文件名,多个文件名用 “，” 分开,路径为 fileHtmlPath
			if (bundle.containsKey(JPushInterface.EXTRA_RICHPUSH_HTML_RES)) {
				String fileImageStr = bundle
						.getString(JPushInterface.EXTRA_RICHPUSH_HTML_RES);
				String[] fileNames = fileImageStr.split(",");
			}
		} else {
			Log.d("其它的action行为", "Unhandled intent - " + intent.getAction());
		}
	
		
	}
	
	// 把消息发送给主activity
//		private void processCustomMessage(Context context, Bundle bundle) {
//			if (MusicMainActivity.isForeground) {
//				String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//				System.out.println("<<<<<<>>>>>>"+message);
//				String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//				Intent msgIntent = new Intent(MusicMainActivity.MESSAGE_RECEIVED_ACTION);
//				msgIntent.putExtra(MusicMainActivity.KEY_MESSAGE, message);
//				if (!JPushUtil.isEmpty(extras)) {
//					try {
//						JSONObject extraJson = new JSONObject(extras);
//						if (null != extraJson && extraJson.length() > 0) {
//							msgIntent.putExtra(MusicMainActivity.KEY_EXTRAS, extras);
//						}
//					} catch (JSONException e) {
//
//					}
//				}
//				context.sendBroadcast(msgIntent);
//			}
//		}
		// 打印所有的 intent extra 数据
		@SuppressWarnings("unused")
		private static String printBundle(Bundle bundle) {
			StringBuilder sb = new StringBuilder();
			for (String key : bundle.keySet()) {
				if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
					sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
				} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
					sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
				} else {
					sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
				}
			}
			return sb.toString();
		}
	
		
		
		private void showNotification() {
//	        // 创建一个NotificationManager的引用
//	        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//	        // 定义Notification的各种属性
//	        Notification notification = new Notification(R.drawable.ic_launcher, "新消息", System.currentTimeMillis());
//	        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
//	        builder.setSmallIcon(R.drawable.ic_launcher);
//	        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//	        // 设置通知的事件消息
//	        CharSequence contentTitle = "Title"; // 通知栏标题
//	        CharSequence contentText = "Text"; // 通知栏内容
//
//	        Intent clickIntent = new Intent(mContext, NotificationClickReceiver.class); //点击通知之后要发送的广播
//	        int id = (int) (System.currentTimeMillis() / 1000);
//	        PendingIntent contentIntent = PendingIntent.getBroadcast(this.getApplicationContext(), id, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//	        notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
//	        notificationManager.notify(id, notification);
	    }
		
}
	

		

