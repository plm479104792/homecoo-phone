package object.dbnewgo.client;


import object.dbnewgo.client.BridgeService.AddCameraInterface;
import object.dbnewgo.client.BridgeService.DoorBellOneKey;
import object.dbnewgo.client.BridgeService.WifiInterface;
import object.p2pipcam.bean.CameraParamsBean;
import object.p2pipcam.bean.WifiScanBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.DataBaseHelper;
import object.p2pipcam.utils.VibratorUtil;
import object.p2pipcam.utils.WifiSetting;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("NewApi")
public class OneSetActivity extends BaseActivity implements OnClickListener,
		AddCameraInterface, DoorBellOneKey, WifiInterface {
	private Button btn_one_set, btn_reset;
	private WifiSetting wifiSetting;
	private String wifiSSID;
	private EditText edit_wifi, edit_pwd;
	private LinearLayout l_wifi;
	private ImageView image;
	private TextView tv_show;
	private ConnectivityManager cm;
	private Handler handler = new Handler();
	private String editSSID, editPWD;
	private DataBaseHelper baseHelper;
	private String osDid = "";
	private SharedPreferences preWifi;
	private WifiScanBean wifiBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_onc_set);
		preWifi = getSharedPreferences("shix_zhao_wifi", Context.MODE_PRIVATE);
		wifiSetting = new WifiSetting(getApplicationContext());
		wifiSSID = wifiSetting.getNowWifiSSID();
		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		l_wifi = (LinearLayout) findViewById(R.id.l_wifi);
		tv_show = (TextView) findViewById(R.id.tv_show);
		image = (ImageView) findViewById(R.id.imageView1);
		btn_one_set = (Button) findViewById(R.id.btn_one_set);
		btn_one_set.setOnClickListener(this);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		btn_reset.setOnClickListener(this);
		edit_wifi = (EditText) findViewById(R.id.edit_wifi);
		edit_pwd = (EditText) findViewById(R.id.edit_pwd);
		if (wifiSSID != null) {
			tv_show.setVisibility(View.GONE);
			l_wifi.setVisibility(View.VISIBLE);
			image.setVisibility(View.VISIBLE);
			btn_one_set.setVisibility(View.VISIBLE);
			edit_wifi.setText(wifiSSID);
			edit_pwd.setText(preWifi.getString(wifiSSID, ""));
		} else {
			tv_show.setVisibility(View.VISIBLE);
			l_wifi.setVisibility(View.GONE);
			image.setVisibility(View.GONE);
			btn_one_set.setVisibility(View.GONE);
		}
		initExitPopupWindow2();
		BridgeService.setAddCameraInterface(this);
		BridgeService.setDoorBellOneKey(this);
		BridgeService.setWifiInterface(this);
		baseHelper = DataBaseHelper.getInstance(this);
		wifiBean = new WifiScanBean();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isOK = false;
		BridgeService.setAddCameraInterface(null);
		BridgeService.setDoorBellOneKey(null);
		BridgeService.setWifiInterface(null);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.btn_one_set:
//			VibratorUtil.Vibrate(OneSetActivity.this, 80);
//			editSSID = edit_wifi.getText().toString().trim();
//			editPWD = edit_pwd.getText().toString().trim();
//			if (editPWD == null || editPWD.length() < 2) {
//				showToast(R.string.doorbell_wifi_pwd_errer);
//				return;
//			}
//			SharedPreferences.Editor editor = preWifi.edit();
//			editor.putString(editSSID, editPWD);
//			editor.commit();
//			connectToHotpot();
//			pp_progressBar.setProgress(1);
//			showWindow.showAtLocation(btn_reset, Gravity.BOTTOM, 0, 0);
//			handler.post(runnable);
//			break;
//		case R.id.btn_reset:
//			wifiSetting = null;
//			wifiSSID = null;
//			wifiSetting = new WifiSetting(getApplicationContext());
//			wifiSSID = wifiSetting.getNowWifiSSID();
//			Log.d("test", "zhao-wifi:" + wifiSSID);
//			if (wifiSSID != null) {
//				tv_show.setVisibility(View.GONE);
//				l_wifi.setVisibility(View.VISIBLE);
//				image.setVisibility(View.VISIBLE);
//				btn_one_set.setVisibility(View.VISIBLE);
//				edit_wifi.setText(wifiSSID);
//				edit_pwd.setText(preWifi.getString(wifiSSID, ""));
//			} else {
//				tv_show.setVisibility(View.VISIBLE);
//				l_wifi.setVisibility(View.GONE);
//				image.setVisibility(View.GONE);
//				btn_one_set.setVisibility(View.GONE);
//			}
//			break;
//		default:
//			break;
//		}
	}

	private boolean isWifiConnected() {
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		Log.d("wifi", "ni.getState()==" + ni.getState()
				+ " ----ni.getDetailedState()=" + ni.getDetailedState());
		if (ni.getState() == State.CONNECTED) {
			return true;
		} else {
			return false;
		}
	}

	private boolean wifiConFlag = false;
	private boolean noStartSerch = false;

	public void connectToHotpot() {
		wifiConFlagOld = false;
		wifiConFlag = false;
		noStartSerch = false;
		WifiConfiguration wifiConfig = this.setWifiParams("NEOBELL-0001",
				"123456789");

		int wcgID = wifiSetting.getWifiManager().addNetwork(wifiConfig);

		wifiConFlag = wifiSetting.getWifiManager().enableNetwork(wcgID, true);

	}

	private boolean wifiConFlagOld = false;

	public void connectToHotpotOld(String wifi, String pwd) {
		wifiConFlagOld = false;
		WifiConfiguration wifiConfig = this.setWifiParams(wifi, pwd);

		int wcgID = wifiSetting.getWifiManager().addNetwork(wifiConfig);

		wifiConFlagOld = wifiSetting.getWifiManager()
				.enableNetwork(wcgID, true);
		// countI = 0;
	}

	public WifiConfiguration setWifiParams(String ssid, String Password) {
		Log.d("test", "zhao-ssid:" + ssid + "  Password:" + Password);
		WifiConfiguration apConfig = new WifiConfiguration();

		apConfig.SSID = "\"" + ssid + "\"";

		apConfig.preSharedKey = "\"" + Password + "\"";
		// apConfig.BSSID = "\"" + bssid + "\"";
		apConfig.hiddenSSID = true;

		apConfig.status = WifiConfiguration.Status.ENABLED;

		apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

		apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

		apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

		apConfig.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.TKIP);

		apConfig.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.CCMP);

		apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

		return apConfig;

	}

	private View popv;
	private PopupWindow showWindow;
	private ProgressBar pp_progressBar;

	public void initExitPopupWindow2() {
		LayoutInflater li = LayoutInflater.from(this);
		popv = li.inflate(R.layout.popup_show1, null);
		pp_progressBar = (ProgressBar) popv.findViewById(R.id.pp_progressBar);
		showWindow = new PopupWindow(popv,
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		showWindow.setAnimationStyle(R.style.AnimationPreview);
		showWindow.setFocusable(true);
	}

	int pro = 0;
	private boolean isClose = false;
	private boolean isFinish = false;
	// private int countI = 0;
	Runnable runnable = new Runnable() {
		public void run() {
			if (!noStartSerch) {
				if (wifiConFlag) {
					if (isWifiConnected()) {
						noStartSerch = true;
						isClose = false;
						isFinish = false;
						// countI = 0;
						isOK = false;
						NativeCaller.StartSearch();
					}
				}
			}
			if (osDid != null && osDid.length() > 1 && !isClose && isFinish) {
				connectToHotpotOld(editSSID, editPWD);
				Log.d("test", "zhao1_editSSID:" + editSSID);
				isClose = true;
			}
			if (wifiConFlagOld) {
				// countI++;
				if (isWifiConnected() && isFinish) {
					if (showWindow != null && showWindow.isShowing()) {
						showWindow.dismiss();
					}
					handler.removeCallbacks(runnable);
					pp_progressBar.setProgress(0);
					OneSetActivity.this.finish();
					overridePendingTransition(R.anim.out_to_right,
							R.anim.in_from_left);
				}
			}
			pro = pp_progressBar.getProgress() + 1;
			pp_progressBar.setProgress(pro);
			if (pro < pp_progressBar.getMax()) {
				pp_progressBar.postDelayed(runnable, 500);
			} else {
				if (showWindow != null && showWindow.isShowing()) {
					showWindow.dismiss();
				}
				handler.removeCallbacks(runnable);
				pp_progressBar.setProgress(0);
				OneSetActivity.this.finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);
			}
		}
	};
	private boolean isOK = false;
	private String didzhao = "";

	// 03-05 17:26:55.386: D/tes(26103): zhao-did: param:0

	@Override
	public void callBackSearchResultData(int cameraType, String strMac,
			String strName, String strDeviceID, String strIpAddr, int port) {
		// TODO Auto-generated method stub

		if (!isOK) {
			didzhao = strDeviceID.replace("-", "");
			isOK = true;
			Log.d("test", "zhao-strDeviceID:" + strDeviceID);

			new Thread() {
				public void run() {
					AddCamera("DoorBell", didzhao, "admin", "");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					NativeCaller.StopSearch();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					osDid = didzhao;
					 StartPPPP(osDid, "admin", "");
					Log.d("test", "NativeCaller.StartPPPP:" + osDid);
				};
			}.start();
		}
	}

	// 03-05 20:07:16.668: D/tes(8973): zhao-did:NIP096258AAABD param:2

	private boolean CheckCameraInfo(String did) {
		synchronized (this) {
			int count = SystemValue.arrayList.size();
			for (int i = 0; i < count; i++) {
				String strDid = SystemValue.arrayList.get(i).getDid();
				if (strDid.equals(did)) {
					return false;
				}
			}
			return true;
		}
	}

	public boolean AddCamera(String name, String did, String user, String pwd) {
		if (!CheckCameraInfo(did) || did == null || did.length() < 3) {
			return false;
		}
		baseHelper.createCamera("DoorBell", did, "admin", "");
		CameraParamsBean bean = new CameraParamsBean();
		bean.setAuthority(false);
		bean.setName(name);
		bean.setDid(did);
		bean.setUser(user);
		bean.setPwd(pwd);
		bean.setStatus(ContentCommon.PPPP_STATUS_UNKNOWN);
		bean.setMode(ContentCommon.PPPP_MODE_UNKNOWN);
		synchronized (this) {
			SystemValue.arrayList.add(bean);
		}
		return true;
	}

	private Handler PPPPMsgHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 2) {
				// isFinish = true;
				NativeCaller.PPPPGetSystemParams(didzhao,
						ContentCommon.MSG_TYPE_WIFI_SCAN);
				Log.d("tes", "zhao-did:" + osDid + "getWifi" + "  didzhao:"
						+ didzhao);
			} else if (msg.what == 111) {
				NativeCaller.PPPPWifiSetting(wifiBean.getDid(), 1,
						wifiBean.getSsid(), wifiBean.getChannel(),
						wifiBean.getMode(), wifiBean.getSecurity(), 0, 0, 0,
						"", "", "", "", 0, 0, 0, 0, editPWD);
			} else if (msg.what == 112) {
				NativeCaller.PPPPRebootDevice(osDid);
				isFinish = true;
				showToast(R.string.doorbell_wifi_set_sucess);
			} else if (msg.what == 113) {
				isFinish = true;
				showToast(R.string.doorbell_wifi_set_faile);
				connectToHotpotOld(editSSID, editPWD);
			}
		}
	};

	@Override
	public void BSMsgNotifyData(String did, int type, int param) {
		// TODO Auto-generated method stub
		Log.d("tes", "zhao-did:" + did + "  param:" + param);
		if (param == 2) {
			Bundle bd = new Bundle();
			Message msg = PPPPMsgHandler.obtainMessage();
			msg.what = 2;
			PPPPMsgHandler.sendMessage(msg);
		}
	}

	@Override
	public void callBackWifiParams(String did, int enable, String ssid,
			int channel, int mode, int authtype, int encryp, int keyformat,
			int defkey, String key1, String key2, String key3, String key4,
			int key1_bits, int key2_bits, int key3_bits, int key4_bits,
			String wpa_psk) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callBackWifiScanResult(String did, String ssid, String mac,
			int security, int dbm0, int dbm1, int mode, int channel, int bEnd) {
		// TODO Auto-generated method stub
		Log.d("shix", "zhao1_ssid:" + ssid + " mac:" + mac + " security:"
				+ security + " dbm0��" + dbm0 + " dbm1:" + dbm1 + " mode:"
				+ mode + " channel:" + channel + " bEnd:" + bEnd);
		Log.d("test", "zhao-editSSID:" + editSSID);
		if (editSSID.equals(ssid) || editSSID.contains(ssid)) {
			Log.d("tet", "zhao-ok");
			wifiBean.setDid(didzhao);
			wifiBean.setSsid(ssid);
			wifiBean.setChannel(channel);
			wifiBean.setSecurity(security);
			wifiBean.setDbm0(dbm0);
			wifiBean.setMac(mac);
			wifiBean.setMode(mode);
			wifiBean.setDbm1(dbm1);
			PPPPMsgHandler.sendEmptyMessage(111);
		}

	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
			int result) {
		// TODO Auto-generated method stub
		if (result == 1) {
			PPPPMsgHandler.sendEmptyMessage(112);
		} else if (result == 0) {
			PPPPMsgHandler.sendEmptyMessage(113);
		}
	}

	@Override
	public void callBackPPPPMsgNotifyData(String did, int type, int param) {
		// TODO Auto-generated method stub

	}
}
