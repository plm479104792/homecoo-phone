/**
 * ����wifi���ð󶨵���
 */
package object.p2pipcam.utils;

/**
 * @author zhaogenghuai
 * @creation 2012-12-20����2:08:31
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
	// ����WifiManager����
	private WifiManager mWifiManager;
	// ����WifiInfo����
	private WifiInfo mWifiInfo;
	// ���������б�
	private List<WifiConfiguration> mWifiConfiguration;
	// ����һ��WifiLock
	WifiLock mWifiLock;
	private WifiScanBean wifiScanBeanAoni;
	private Context context1;
	/**
	 * ���췽��
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

	// �ж�wifi�Ƿ����
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
	 * ��Wifi����
	 */
	public void openNetCard() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	/**
	 * �ر�Wifi����
	 */
	public void closeNetCard() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * ��鵱ǰWifi����״̬
	 */
	public void checkNetCardState() {
		if (mWifiManager.getWifiState() == 0) {
			Log.i(TAG, "�������ڹر�");
		} else if (mWifiManager.getWifiState() == 1) {
			Log.i(TAG, "�����Ѿ��ر�");
		} else if (mWifiManager.getWifiState() == 2) {
			Log.i(TAG, "�������ڴ�");
		} else if (mWifiManager.getWifiState() == 3) {
			Log.i(TAG, "�����Ѿ���");
		} else {
			Log.i(TAG, "---_---��......û�л�ȡ��״̬---_---");
		}
	}

	/**
	 * ɨ���ܱ�����
	 */
	public void scan() {
		mWifiManager.startScan();
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			Log.i(TAG, "��ǰ��������������磬��鿴ɨ����");
		} else {
			Log.i(TAG, "��ǰ����û����������");
		}
	}

	/**
	 * �õ�ɨ����
	 */
	public String getScanResult() {
		// ÿ�ε��ɨ��֮ǰ�����һ�ε�ɨ����
		if (mStringBuffer != null) {
			mStringBuffer = new StringBuffer();
		}
		// ��ʼɨ������
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
	 * �õ�ɨ���wifi��SSID
	 */
	public List<WifiScanBean> getScanSSIDResult() {
		// ÿ�ε��ɨ��֮ǰ�����һ�ε�ɨ����
//
//		if (listAllSSID != null) {
//			listAllSSID = new ArrayList<WifiScanBean>();
//		}
//		// ��ʼɨ������
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
	 * ����ָ������
	 */
	public void connect() {
		mWifiInfo = mWifiManager.getConnectionInfo();

	}

	/**
	 * �Ͽ���ǰ���ӵ�����
	 */
	public void disconnectWifi() {
		int netId = getNetworkId();
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}

	/**
	 * ��鵱ǰ����״̬
	 * 
	 * @return String
	 */
	public void checkNetWorkState() {
		if (mWifiInfo != null) {
			Log.i(TAG, "������������");
		} else {
			Log.i(TAG, "�����ѶϿ�");
		}
	}

	/**
	 * �õ����ӵ�ID
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * �õ����ӵ�ID
	 */
	public String getNowWifiSSID() {
		if (mWifiInfo == null) {
			return null;
		} else {
			return mWifiInfo.getSSID();
		}

	}

	/**
	 * �õ�IP��ַ
	 */
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// ����WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// ����WifiLock
	public void releaseWifiLock() {
		// �ж�ʱ������
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// ����һ��WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// �õ����úõ�����
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// ָ�����úõ������������
	public void connectConfiguration(int index) {
		// �����������úõ�������������
		if (index >= mWifiConfiguration.size()) {
			return;
		}
		// �������úõ�ָ��ID������
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	// �õ�MAC��ַ
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// �õ�������BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// �õ�WifiInfo��������Ϣ��
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// ���һ�����粢����
	public int addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
		mWifiManager.enableNetwork(wcgID, true);
		return wcgID;
	}
}
