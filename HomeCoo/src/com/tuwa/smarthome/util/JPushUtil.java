package com.tuwa.smarthome.util;

import java.sql.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.tuwa.smarthome.R;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.android.data.JPushLocalNotification;

public class JPushUtil {

	/**
	 * 同时设置别名与标签
	 * 
	 * @param context
	 * @param alias
	 *            为null时不设置此值,"" （空字符串）表示取消之前的设置，字母（区分大小写）、数字、下划线、汉字，长度限制为 40
	 *            字节。（判断长度需采用UTF-8编码）
	 * @param tags
	 *            为null时不设置此值,空数组或列表表示取消之前的设置，字母（区分大小写）、数字、下划线、汉字，长度限制为 40
	 *            字节，最多支持设置 100 个 tag，但总长度不得超过1K字节。（判断长度需采用UTF-8编码）
	 * @param callback
	 *            在 TagAliasCallback 的 gotResult 方法，返回对应的参数 alias,
	 *            tags。并返回对应的状态码：0为成功
	 * 
	 * @描述 每次调用设置有效的别名，覆盖之前的设置。
	 */
	public static void setAliasAndTags(Context context, String alias,
			Set<String> tags, TagAliasCallback callback) {
		JPushInterface.setAliasAndTags(context, alias, tags, callback);
	}
	
	/**
	 * 设置别名
	 * 
	 * @param context
	 * @param alias
	 *            "" （空字符串）表示取消之前的设置,字母（区分大小写）、数字、下划线、汉字
	 * @param callback
	 *            在TagAliasCallback 的 gotResult 方法，返回对应的参数 alias,
	 *            tags。并返回对应的状态码：0为成功
	 * @描述：每次调用设置有效的别名，覆盖之前的设置。alias 命名长度限制为 40 字节。（判断长度需采用UTF-8编码）
	 * 
	 */
	public static void setAlias(Context context, String alias,
			TagAliasCallback callback) {
		JPushInterface.setAlias(context, alias, callback);
	}
	

	/**
	 * 设置标签
	 * 
	 * @param context
	 * @param tags
	 *            空数组或列表表示取消之前的设置,字母（区分大小写）、数字、下划线、汉字
	 * @param callback
	 *            在 TagAliasCallback 的 gotResult 方法，返回对应的参数 alias,
	 *            tags。并返回对应的状态码：0为成功
	 * @描述: 每次调用至少设置一个 tag，覆盖之前的设置，不是新增.每个 tag 命名长度限制为 40 字节，最多支持设置 100 个
	 *      tag，但总长度不得超过1K字节。（判断长度需采用UTF-8编码）.单个设备最多支持设置 100 个 tag。App 全局 tag
	 *      数量无限制。
	 */
	public static void setTags(Context context, Set<String> tags,
			TagAliasCallback callback) {
		JPushInterface.setTags(context, tags, callback);
	}
	
	/**
	 * 滤掉无效的 tags
	 * @param tags
	 * @return 有效的 tag 集合。
	 */
	public static Set<String> filterValidTags(Set<String> tags){
		return JPushInterface.filterValidTags(tags);
	}
	
	/**
	 * 设置推送时间
	 * 
	 * @param context
	 *            ApplicationContext
	 * @param startHour
	 *            int startHour 允许推送的开始时间 （24小时制：startHour的范围为0到23）
	 * @param endHour
	 *            int endHour 允许推送的结束时间 （24小时制：endHour的范围为0到23）
	 * @param integers
	 *            0表示星期天，1表示星期一，以此类推。
	 *            （7天制，Set集合里面的int范围为0到6）,set的值为null,则任何时间都可以收到消息和通知
	 *            ，set的size为0，则表示任何时间都收不到消息和通知.
	 */
	public static void setPushTime(Context context, int startHour, int endHour,
			Integer... integers) {
		// 设置推送时间
		Set<Integer> daysIntegers = new HashSet<Integer>();
		if (integers == null) {
			daysIntegers = null;
		} else {
			for (int i = 0; i < integers.length; i++) {

				daysIntegers.add(integers[i]);
			}
		}
		JPushInterface.setPushTime(context, daysIntegers, startHour, endHour);
	}
	
	/**
	 * 设置通知静默时间段
	 * 
	 * @param context
	 *            ApplicationContext
	 * @param startHour
	 *            int startHour 静音时段的开始时间 - 小时 （24小时制，范围：0~23 ）
	 * @param startMinute
	 *            int startMinute 静音时段的开始时间 - 分钟（范围：0~59 ）
	 * @param endHour
	 *            int endHour 静音时段的结束时间 - 小时 （24小时制，范围：0~23 ）
	 * @param endMinute
	 *            int endMinute 静音时段的结束时间 - 分钟（范围：0~59 ）
	 */
	public static void setSilenceTime(Context context, int startHour,
			int startMinute, int endHour, int endMinute) {
		JPushInterface.setSilenceTime(context, startHour, startMinute, endHour,
				endMinute);
	}
	
	public static class CustomNotificationBuilder {
		private Context context;
		private int number;
		private int layoutId;
		private int iconTipId = -1;
		private int iconShowId = -1;
		private int notificationFlags = -1;
		private int notificationDefaults = 0;

	
		/**
		 * 
		 * @param context
		 *            Context
		 */
		public CustomNotificationBuilder(Context context) {
			super();
			this.context = context;
		}
	
	
		
		/**
		 * 设置编号
		 * 
		 * @param number
		 *            编号
		 * @return
		 */
		public CustomNotificationBuilder setNumber(int number) {
			this.number = number;
			return this;
		}
		
		/**
		 * 设置通知栏显示布局
		 * 
		 * @param layoutId
		 *            布局id
		 * @return
		 */
		public CustomNotificationBuilder setLayout(int layoutId) {
			this.layoutId = layoutId;
			return this;
		}


		/**
		 * 设置最顶层状态栏小图标
		 * 
		 * @param iconTipId
		 *            图片id
		 * @return
		 */
		public CustomNotificationBuilder setIconTip(int iconTipId) {
			this.iconTipId = iconTipId;
			return this;
		}
		
		/**
		 * 设置下拉状态栏时显示的通知图标
		 * 
		 * @param iconShowId
		 *            图片id
		 * @return
		 */
		public CustomNotificationBuilder setIconShow(int iconShowId) {
			this.iconShowId = iconShowId;
			return this;
		}
		
		/**
		 * 设置行为
		 * 
		 * @param flags
		 *            例如 Notification.FLAG_AUTO_CANCEL; 自动消失
		 * @return
		 */
		public CustomNotificationBuilder setFlags(int flags) {
			this.notificationFlags = flags;
			return this;
		}

		/**
		 * 设置铃声，震动，提示灯
		 * 
		 * @param defaults
		 *            铃声 Notification.DEFAULT_SOUND; 震动
		 *            Notification.DEFAULT_VIBRATE ; 提示灯
		 *            Notification.DEFAULT_LIGHTS
		 * @return
		 */
		public CustomNotificationBuilder setDefaults(int... defaults) {
			for (int i = 0; i < defaults.length; i++) {
				notificationDefaults |= defaults[i];
			}
			return this;
		}
		public void init() {
			// 指定定制的 Notification Layout
			CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(context, iconShowId, iconShowId, iconShowId, iconShowId);
			if (iconTipId != -1) {
				// 指定最顶层状态栏小图标
				builder.statusBarDrawable = iconTipId;
			}
			if (iconShowId != -1) {
				// 指定下拉状态栏时显示的通知图标
				builder.layoutIconDrawable = iconShowId;
			}
			// 设置行为
			if (notificationFlags != -1) {
				builder.notificationFlags = notificationFlags;
			}
			// 设置铃声，震动，提示灯。
			if (notificationDefaults != 0) {
				builder.notificationDefaults = notificationDefaults;
			}

			JPushInterface.setPushNotificationBuilder(number, builder);
		}

	}
	/**
	 * 自定义通知栏
	 * 
	 * @param context
	 * @param number
	 *            编号
	 * @param layoutId
	 *            布局Id
	 * @param iconTipId
	 *            指定最顶层状态栏小图标
	 * @param iconShowId
	 *            指定下拉状态栏时显示的通知图标
	 * @param flags
	 *            设置行为
	 * @param defaults
	 *            设置铃声，震动，提示灯
	 * 
	 * @params flags 例如 Notification.FLAG_AUTO_CANCEL; 自动消失
	 * @params defaults 铃声 Notification.DEFAULT_SOUND; 震动
	 *         Notification.DEFAULT_VIBRATE ; 提示灯 Notification.DEFAULT_LIGHTS
	 */
	public static void initCustomPushNotificationBuilder(Context context,
			int number, int layoutId, int iconTipId, int iconShowId, int flags,
			int... defaults) {
		CustomNotificationBuilder builder = new CustomNotificationBuilder(
				context);
		builder.setLayout(layoutId);
		builder.setIconTip(iconTipId);
		builder.setIconShow(iconShowId);
		builder.setNumber(number);
		builder.setFlags(flags);
		builder.setDefaults(defaults);
		builder.init();
	}
	
	/**
	 * 自定义通知栏
	 * 
	 * @param context
	 * @param number
	 *            编号
	 * @param layoutId
	 *            布局Id
	 * @param iconTipId
	 *            指定最顶层状态栏小图标
	 * @param iconShowId
	 *            指定下拉状态栏时显示的通知图标
	 */
	public static void customPushNotification(Context context, int number,
			int layoutId, int iconTipId, int iconShowId) {
		// 指定定制的 Notification Layout
		CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(context, iconShowId, iconShowId, iconShowId, iconShowId);

		// 指定最顶层状态栏小图标
		builder.statusBarDrawable = iconTipId;

		// 指定下拉状态栏时显示的通知图标
		builder.layoutIconDrawable = iconShowId;

		JPushInterface.setPushNotificationBuilder(number, builder);
	}
	
	/**
	 * 自定义通知栏
	 * 
	 * @param context
	 * @param number
	 *            编号
	 * @param layoutId
	 *            布局Id
	 */
	public static void customPushNotification(Context context, int number,
			int layoutId) {
		// 指定定制的 Notification Layout
		CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(context, layoutId, layoutId, layoutId, layoutId);

		// 指定最顶层状态栏小图标
		builder.statusBarDrawable = R.drawable.ic_launcher;

		// 指定下拉状态栏时显示的通知图标
		builder.layoutIconDrawable = R.drawable.ic_launcher;

		JPushInterface.setPushNotificationBuilder(number, builder);
	}
	
	/**
	 * 设置保留最近通知条数
	 * 
	 * @param context
	 * @param maxNum
	 *            最多显示的条数
	 */
	public static void setLatestNotificationNumber(Context context, int maxNum) {
		JPushInterface.setLatestNotificationNumber(context, maxNum);
	}
	
	/**
	 * 本地通知工具帮助类
	 * 
	 * @author yangshuai
	 * 
	 */
	public static class localNotificationBuilder {
		private Context context;
		private JPushLocalNotification ln = new JPushLocalNotification();

		/**
		 * getApplicationContext
		 * 
		 * @param context
		 */
		public localNotificationBuilder(Context context) {
			this.context = context;
		}

		/**
		 * 设置编号
		 * 
		 * @param number
		 * @return
		 */
		public localNotificationBuilder setBuilderId(long number) {
			ln.setBuilderId(number);
			return this;
		}

		/**
		 * 设置标题
		 * 
		 * @param title
		 * @return
		 */
		public localNotificationBuilder setTitle(String title) {
			ln.setTitle(title);
			return this;
		}

		/**
		 * 设置内容
		 * 
		 * @param content
		 * @return
		 */
		public localNotificationBuilder setContent(String content) {
			ln.setContent(content);
			return this;
		}

		/**
		 * 设置额外的数据信息extras为json字符串
		 * 
		 * @param extras
		 *            Map<String , Object>
		 * @return
		 */
		public localNotificationBuilder setExtras(Map<String, Object> extras) {
			JSONObject json = new JSONObject(extras);
			ln.setExtras(json.toString());
			return this;
		}

		/**
		 * 设置本地通知触发时间
		 * 
		 * @param broadCastTime
		 *            long
		 * @return
		 */
		public localNotificationBuilder setBroadcastTime(long broadCastTime) {
			ln.setBroadcastTime(broadCastTime);
			return this;
		}

		/**
		 * 设置本地通知触发时间
		 * 
		 * @param date
		 *            Date
		 * @return
		 */
		public localNotificationBuilder setBroadcastTime(Date date) {
			ln.setBroadcastTime(date);
			return this;
		}

		public localNotificationBuilder setBroadcastTime(int year, int month,
				int day, int hour, int minute, int second) {
			ln.setBroadcastTime(year, month, day, hour, minute, second);
			return this;
		}

		/**
		 * 设置本地通知的ID
		 * 
		 * @param notificationId
		 * @return
		 */
		public localNotificationBuilder setNotificationId(long notificationId) {
			ln.setNotificationId(notificationId);
			return this;
		}

		public void create() {

			JPushInterface.addLocalNotification(context, ln);
		}

	}
	
	/**
	 * 显示本地通知
	 * 
	 * @param context
	 * @param number
	 * @param title
	 * @param content
	 * @param notificationId
	 * @param time
	 */
	public static void showLocalNotification(Context context, long number,
			String title, String content, long notificationId, long time) {
		JPushUtil.localNotificationBuilder builder = new localNotificationBuilder(
				context);
		builder.setBuilderId(number);
		builder.setContent(content);
		builder.setTitle(title);
		builder.setNotificationId(notificationId);
		builder.setBroadcastTime(time);
		builder.create();
	}
	
	/**
	 * 清除所有的通知
	 * 
	 * @param context
	 *            ApplicationContext
	 */
	public static void clearAllNotifications(Context context) {
		JPushInterface.clearAllNotifications(context);
	}
	
	/**
	 * 清除指定Id的通知
	 * 
	 * @param context
	 *            ApplicationContext
	 * @param notificationId
	 *            通知ID
	 */
	public static void clearNotificationById(Context context, int notificationId) {
		JPushInterface.clearNotificationById(context, notificationId);
	}

	public static final String KEY_APP_KEY = "JPUSH_APPKEY";

	public static boolean isEmpty(String s) {
		if (null == s)
			return true;
		if (s.length() == 0)
			return true;
		if (s.trim().length() == 0)
			return true;
		return false;
	}
	
	// 校验Tag Alias 只能是数字,英文字母和中文
		public static boolean isValidTagAndAlias(String s) {
			Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
			Matcher m = p.matcher(s);
			return m.matches();
		}

		// 取得AppKey
		public static String getAppKey(Context context) {
			Bundle metaData = null;
			String appKey = null;
			try {
				ApplicationInfo ai = context.getPackageManager()
						.getApplicationInfo(context.getPackageName(),
								PackageManager.GET_META_DATA);
				if (null != ai)
					metaData = ai.metaData;
				if (null != metaData) {
					appKey = metaData.getString(KEY_APP_KEY);
					if ((null == appKey) || appKey.length() != 24) {
						appKey = null;
					}
				}
			} catch (NameNotFoundException e) {

			}
			return appKey;
		}
		
		// 取得版本号
		public static String GetVersion(Context context) {
			try {
				PackageInfo manager = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0);
				return manager.versionName;
			} catch (NameNotFoundException e) {
				return "Unknown";
			}
		}
		public static void showToast(final String toast, final Context context) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
					Looper.loop();
				}
			}).start();
		}
		
		public static boolean isConnected(Context context) {
			ConnectivityManager conn = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = conn.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		}
		
}




















