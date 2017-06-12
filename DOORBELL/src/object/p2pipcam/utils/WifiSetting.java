/**
 * 本地wifi设置绑定调用
 */
package object.p2pipcam.utils;

/**
 * @author zhaogenghuai
 * @creation 2012-12-20下午2:08:31
 */
import java.util.ArrayList;
import java.util.List;

import object.p2pipcam.bean.WifiScanBean;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiSetting {
	private final static String TAG = "WifiSetting";
	private StringBuffer mStringBuffer = new StringBuffer();
	private List<ScanResult> listResult;
	private ScanResult mScanResult;
	private List<WifiScanBean> listAllSSID = new ArrayList<WifiScanBean>();
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;
	private WifiScanBean wifiScanBeanAoni;
	private Context context1;
	/**
	 * 构造方法
	 */
	public WifiSetting(Context context) {
		this.context1 = context;
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	public WifiManager getWifiManager() {
		if (mWifiManager == null) {
			return (WifiManager) context1.getSystemService(Context.WIFI_SERVICE);
		}else{
			return mWifiManager;
		}
	}

	// 判断wifi是否可用
	public static boolean isWiFiActive(Context inContext) {
		Context context = inContext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI")
							&& info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 打开Wifi网卡
	 */
	public void openNetCard() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	/**
	 * 关闭Wifi网卡
	 */
	public void closeNetCard() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 检查当前Wifi网卡状态
	 */
	public void checkNetCardState() {
		if (mWifiManager.getWifiState() == 0) {
			Log.i(TAG, "网卡正在关闭");
		} else if (mWifiManager.getWifiState() == 1) {
			Log.i(TAG, "网卡已经关闭");
		} else if (mWifiManager.getWifiState() == 2) {
			Log.i(TAG, "网卡正在打开");
		} else if (mWifiManager.getWifiState() == 3) {
			Log.i(TAG, "网卡已经打开");
		} else {
			Log.i(TAG, "---_---晕......没有获取到状态---_---");
		}
	}

	/**
	 * 扫描周边网络
	 */
	public void scan() {
		mWifiManager.startScan();
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");
		} else {
			Log.i(TAG, "当前区域没有无线网络");
		}
	}

	/**
	 * 得到扫描结果
	 */
	public String getScanResult() {
		// 每次点击扫描之前清空上一次的扫描结果
		if (mStringBuffer != null) {
			mStringBuffer = new StringBuffer();
		}
		// 开始扫描网络
		scan();
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			for (int i = 0; i < listResult.size(); i++) {
				mScanResult = listResult.get(i);
				mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
						.append(" :").append(mScanResult.SSID).append("->")
						.append(mScanResult.BSSID).append("->")
						.append(mScanResult.capabilities).append("->")
						.append(mScanResult.frequency).append("->")
						.append(mScanResult.level).append("->")
						.append(mScanResult.describeContents()).append("\n\n");
			}
		}
		Log.i(TAG, mStringBuffer.toString());
		return mStringBuffer.toString();
	}

	/**
	 * 得到扫描后wifi的SSID
	 */
	public List<WifiScanBean> getScanSSIDResult() {
		// 每次点击扫描之前清空上一次的扫描结果
//
//		if (listAllSSID != null) {
//			listAllSSID = new ArrayList<WifiScanBean>();
//		}
//		// 开始扫描网络
//		scan();
//		listResult = mWifiManager.getScanResults();
//		if (listResult != null) {
//			for (int i = 0; i < listResult.size(); i++) {
//				mScanResult = listResult.get(i);
//				// if (mScanResult.SSID.toString().endsWith("WifiCam")) {
//				wifiScanBeanAoni = new WifiScanBean();
//				wifiScanBeanAoni.setSSID(mScanResult.SSID);
//				wifiScanBeanAoni.setBSSID(mScanResult.BSSID);
//				listAllSSID.add(wifiScanBeanAoni);
//				// }
//			}
//		}
		return listAllSSID;
	}

	/**
	 * 连接指定网络
	 */
	public void connect() {
		mWifiInfo = mWifiManager.getConnectionInfo();

	}

	/**
	 * 断开当前连接的网络
	 */
	public void disconnectWifi() {
		int netId = getNetworkId();
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}

	/**
	 * 检查当前网络状态
	 * 
	 * @return String
	 */
	public void checkNetWorkState() {
		if (mWifiInfo != null) {
			Log.i(TAG, "网络正常工作");
		} else {
			Log.i(TAG, "网络已断开");
		}
	}

	/**
	 * 得到连接的ID
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * 得到连接的ID
	 */
	public String getNowWifiSSID() {
		if (mWifiInfo == null) {
			return null;
		} else {
			return mWifiInfo.getSSID();
		}

	}

	/**
	 * 得到IP地址
	 */
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index >= mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
	public int addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
		mWifiManager.enableNetwork(wcgID, true);
		return wcgID;
	}
}
