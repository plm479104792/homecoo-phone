package com.tuwa.smarthome.util;

import java.io.IOException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
/**
 * 联网工具类
 * */
public class NetWorkUtil {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity == null) {
			Log.i("Network", "无法获得ConnectivityManager");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].isAvailable()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean checkNetState(Context context) {
		boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
	}

	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			// Log.w(LOG_TAG, "couldn't get connectivity manager");
			Log.i("WIFI", "无法获得ConnectivityManager");
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null
					&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				if (tm != null && tm.isNetworkRoaming()) {
					return true;
				} else {
				}
			} else {
			}
		}
		return false;
	}

	/**
	 * 判断GPRS是否能用       这个并不能判断是否能上网
	 * */
	public static boolean isMobileDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isMobileDataEnable = false;

		isMobileDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

		return isMobileDataEnable;
	}

	/**
	 * 判断WIFI是否连上     这个并不能判断WIFI是否能上网
	 * */
	public static boolean isWifiDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isWifiDataEnable = false;
		isWifiDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		return isWifiDataEnable;
	}

	/**
	 * ping 是否连上internet 120.26.220.55 
	 * */ 
	public static Boolean pingHost(String str) {
		boolean b= false;
		try {
			//-c ping一次     -w 执行时间s 超过时间则失败
			Process p = Runtime.getRuntime().exec("ping -c 1 -w 2 "+str);
			int status = p.waitFor();
			if (status == 0) {
				b = true;
			} else {
				b = false;
			}
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}

		return b;
	}

}
