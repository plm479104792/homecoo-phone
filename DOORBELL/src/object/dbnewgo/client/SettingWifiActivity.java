package object.dbnewgo.client;

import java.util.Timer;
import java.util.TimerTask;

import object.dbnewgo.client.BridgeService.WifiInterface;
import object.p2pipcam.adapter.WifiScanListAdapter;
import object.p2pipcam.bean.WifiBean;
import object.p2pipcam.bean.WifiScanBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Wifi锟斤拷锟斤拷
 * */
@SuppressLint("NewApi")
public class SettingWifiActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener, OnItemClickListener,
		WifiInterface {
	private String LOG_TAG = "SettingWifiActivity";
	private String strDID;
	private String cameraName;
	private boolean changeWifiFlag = false;// 锟叫讹拷锟角凤拷锟斤拷wifi
	private boolean successFlag = false;// 锟斤拷取锟斤拷锟斤拷锟矫的斤拷锟?
	private static final int INITTIMEOUT = 10000;
	private final int END = 1;// wifi scan end flag
	private int result;
	private final int WIFIPARAMS = 1;// getwifi params
	private final int SCANPARAMS = 2;// scan wifi params
	private final int OVER = 3;// set wifi over
	private final int TIMEOUT = 4;
	private final int ADDBEAN = 10;// wifi scan end flag
	private int CAMERAPARAM = 0xffffffff;// 锟斤拷锟斤拷状�?
	// security
	private final int NO = 0;
	private final int WEP = 1;
	private final int WPA_PSK_AES = 2;
	private final int WPA_PSK_TKIP = 3;
	private final int WPA2_PSK_AES = 4;
	private final int WPA2_PSK_TKIP = 5;
	private Timer mTimerTimeOut;
	private boolean isTimerOver = false;
	private ImageView imgDropDown;
	private Button btnOk;
	private Button btnCancel;
	private CheckBox cbxShowPwd;
	private ListView mListView;
	private PopupWindow popupWindow;
	private TextView tvName;
	private TextView tvPrompt;
	private TextView tvSafe;
	private TextView tvSigal;
	private EditText editPwd;
	private Button btnManager;
	private WifiBean wifiBean;
	private WifiScanListAdapter mAdapter;
	private View pwdView;

	private ProgressDialog scanDialog;
	private View signalView;
	private RadioGroup group1, group2;
	private RadioButton rb1, rb2, rb3, rb4;
	private LinearLayout layoutwep_show1, layoutwep_show2;

	/**
	 * wifi getParams and Scaned
	 * 
	 * **/
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ADDBEAN:
				mListView.setVisibility(View.VISIBLE);
				WifiScanBean bean = (WifiScanBean) msg.getData()
						.getSerializable("bean");
				Log.d("test", "test"+bean.getSsid());
				mAdapter.addWifiScan(bean);
				mAdapter.notifyDataSetChanged();
				break;
			case WIFIPARAMS:
				successFlag = true;
				if (progressDialog.isShowing()) {
					progressDialog.cancel();
				}
				if (wifiBean.getEnable() == 1) {
					if (!TextUtils.isEmpty(wifiBean.getSsid())) {
						tvName.setText(wifiBean.getSsid());
						tvPrompt.setText(getResources().getString(
								R.string.connected));
					} else {
						tvName.setText(getResources().getString(
								R.string.wifi_no_safe));
						tvPrompt.setText(getResources().getString(
								R.string.wifi_not_connected));
					}

					switch (wifiBean.getAuthtype()) {
					case NO:
						tvSafe.setText(getResources().getString(
								R.string.wifi_no_safe));
						break;
					case WEP:
						tvSafe.setText("WEP");
						break;
					case WPA_PSK_AES:
						tvSafe.setText("WPA_PSK(AES)");
						break;
					case WPA_PSK_TKIP:
						tvSafe.setText("WPA_PSK(TKIP)");
						break;
					case WPA2_PSK_AES:
						tvSafe.setText("WPA2_PSK(AES)");
						break;
					case WPA2_PSK_TKIP:
						tvSafe.setText("WPA2_PSK(TKIP)");
						break;
					default:
						break;
					}

				}
				break;
			case SCANPARAMS:// wifi scan
				if (scanDialog != null && scanDialog.isShowing()) {
					scanDialog.cancel();
					if (!isTimerOver) {
						mTimerTimeOut.cancel();
					}
					Log.d(LOG_TAG, "handler  scan wifi  2");
					try {
						mAdapter.wifiSort();
					} catch (Exception e) {
						// TODO: handle exception
					}
					setListViewHeight();
					mAdapter.notifyDataSetChanged();
					Log.d(LOG_TAG, "handler  scan wifi  3");
				}
				break;
			case OVER:// set over
				successFlag = true;
				if (result == 1) {
					Log.d("tag", "over");
					NativeCaller.PPPPRebootDevice(strDID);
					Toast.makeText(
							SettingWifiActivity.this,
							getResources().getString(R.string.wifi_set_success),
							Toast.LENGTH_LONG).show();
					// Intent intent2 = new Intent(SettingWifiActivity.this,
					// IpcamClientActivity.class);
					// startActivity(intent2);
					Intent intent2 = new Intent("myback");
					sendBroadcast(intent2);
					finish();
				} else if (result == 0) {
					showToast(R.string.wifi_set_failed);
				}
				break;
			case TIMEOUT:
				if (scanDialog.isShowing()) {
					scanDialog.cancel();
				}
				// showToast(R.string.wifi_scan_failed);
				break;

			default:
				break;
			}
		}
	};

	/**
	 * Listitem click
	 * **/
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tvSigal.setText(wifiBean.getDbm0() + "%");
			tvName.setText(wifiBean.getSsid());
			signalView.setVisibility(View.VISIBLE);
			tvPrompt.setText(getResources().getString(
					R.string.wifi_not_connected));
			switch (wifiBean.getAuthtype()) {
			case NO:
				pwdView.setVisibility(View.GONE);
				tvSafe.setText(getResources().getString(R.string.wifi_no_safe));
				layoutwep_show1.setVisibility(View.GONE);
				layoutwep_show2.setVisibility(View.GONE);
				break;
			case WEP:
				pwdView.setVisibility(View.VISIBLE);
				layoutwep_show1.setVisibility(View.VISIBLE);
				layoutwep_show2.setVisibility(View.VISIBLE);
				tvSafe.setText("WEP");
				break;
			case WPA_PSK_AES:
				pwdView.setVisibility(View.VISIBLE);
				tvSafe.setText("WPA_PSK(AES)");
				layoutwep_show1.setVisibility(View.GONE);
				layoutwep_show2.setVisibility(View.GONE);
				break;
			case WPA_PSK_TKIP:
				pwdView.setVisibility(View.VISIBLE);
				tvSafe.setText("WPA_PSK(TKIP)");
				layoutwep_show1.setVisibility(View.GONE);
				layoutwep_show2.setVisibility(View.GONE);
				break;
			case WPA2_PSK_AES:
				pwdView.setVisibility(View.VISIBLE);
				tvSafe.setText("WPA2_PSK(AES)");
				layoutwep_show1.setVisibility(View.GONE);
				layoutwep_show2.setVisibility(View.GONE);
				break;
			case WPA2_PSK_TKIP:
				pwdView.setVisibility(View.VISIBLE);
				tvSafe.setText("WPA2_PSK(TKIP)");
				layoutwep_show1.setVisibility(View.GONE);
				layoutwep_show2.setVisibility(View.GONE);
				break;
			default:
				break;
			}

		}
	};
	private ProgressDialog progressDialog;

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				progressDialog.dismiss();
				// showToast(R.string.wifi_getparams_failed);
			}
		}
	};

	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// 锟剿筹拷锟斤拷锟斤拷
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settingwifi0111);
		// setEdgeFromLeft();
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.wifi_getparams));
		progressDialog.show();
		mHandler.postDelayed(runnable, INITTIMEOUT);
		wifiBean = new WifiBean();
		findView();
		setListener();
		mAdapter = new WifiScanListAdapter(this);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mAdapter);
		BridgeService.setWifiInterface(this);
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_PARAMS);
		// editText_wifi_chanal.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// // TODO Auto-generated method stub
		// changeWifiFlag = true;
		// }
		// });
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}

	private void setListener() {
		imgDropDown.setOnClickListener(this);
		btnManager.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		cbxShowPwd.setOnCheckedChangeListener(this);
		// progressDialog.setOnKeyListener(new OnKeyListener(){
		//
		// @Override
		// public boolean onKey(DialogInterface dialog, int keyCode,
		// KeyEvent event) {
		//
		// if(keyCode == KeyEvent.KEYCODE_BACK){
		// return true;
		// }
		// return false;
		// }
		//
		// });
	}

	private void findView() {
		imgDropDown = (ImageView) findViewById(R.id.wifi_img_drop);
		layoutwep_show1 = (LinearLayout) findViewById(R.id.wep_show1);
		layoutwep_show2 = (LinearLayout) findViewById(R.id.wep_show2);
		btnOk = (Button) findViewById(R.id.wifi_ok);
		btnCancel = (Button) findViewById(R.id.wifi_cancel);
		cbxShowPwd = (CheckBox) findViewById(R.id.wifi_cbox_show_pwd);
		mListView = (ListView) findViewById(R.id.wifi_listview);
		tvName = (TextView) findViewById(R.id.wifi_tv_name);
		tvPrompt = (TextView) findViewById(R.id.wifi_tv_prompt);
		tvSafe = (TextView) findViewById(R.id.wifi_tv_safe);
		tvSigal = (TextView) findViewById(R.id.wifi_tv_sigal);
		editPwd = (EditText) findViewById(R.id.wifi_edit_pwd);
		btnManager = (Button) findViewById(R.id.wifi_btn_manger);
		pwdView = findViewById(R.id.wifi_pwd_view);
		signalView = findViewById(R.id.wifi_sigalview);
		tvCameraName = (TextView) findViewById(R.id.tv_camera_setting);
		group1 = (RadioGroup) findViewById(R.id.group1);
		group2 = (RadioGroup) findViewById(R.id.group2);
		rb1 = (RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		rb3 = (RadioButton) findViewById(R.id.rb3);
		rb4 = (RadioButton) findViewById(R.id.rb4);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.top);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.top_bg);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		// layout.setBackgroundDrawable(drawable);
		group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rb1.getId()) {
					wifiBean.setEncryp(0);// 0
					Log.d("tag", "wifiBean.setEncryp(0)");
				} else if (checkedId == rb2.getId()) {
					wifiBean.setEncryp(1);// 0
					Log.d("tag", "wifiBean.setEncryp(1)");
				}
			}
		});
		group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rb3.getId()) {
					wifiBean.setKeyformat(1);// 0
					Log.d("tag", "wifiBean.setKeyformat(1)");
				} else if (checkedId == rb4.getId()) {
					wifiBean.setKeyformat(0);// 0
					Log.d("tag", "wifiBean.setKeyformat(0)");
				}
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}

		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if (id==R.id.wifi_btn_manger) {
			mAdapter.clearWifi();
			mAdapter.notifyDataSetChanged();
			mListView.setVisibility(View.GONE);
			NativeCaller.PPPPGetSystemParams(strDID,
					ContentCommon.MSG_TYPE_WIFI_SCAN);
			scanDialog = new ProgressDialog(this);
			scanDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			scanDialog.setMessage(getResources().getString(
					R.string.wifi_scanning));
			scanDialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {

					if (keyCode == KeyEvent.KEYCODE_BACK) {
						return true;
					}
					return false;
				}
			});
			scanDialog.show();
			setTimeOut();
		}else if (id==R.id.wifi_ok) {
			if (!changeWifiFlag) {
				showToast(R.string.wifi_setting_no_change);
				return;
			}
			setWifi();
		}else if (id==R.id.wifi_cancel) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		}
//		switch (v.getId()) {
//		case R.id.wifi_btn_manger:
//			mAdapter.clearWifi();
//			mAdapter.notifyDataSetChanged();
//			mListView.setVisibility(View.GONE);
//			NativeCaller.PPPPGetSystemParams(strDID,
//					ContentCommon.MSG_TYPE_WIFI_SCAN);
//			scanDialog = new ProgressDialog(this);
//			scanDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			scanDialog.setMessage(getResources().getString(
//					R.string.wifi_scanning));
//			scanDialog.setOnKeyListener(new OnKeyListener() {
//
//				@Override
//				public boolean onKey(DialogInterface dialog, int keyCode,
//						KeyEvent event) {
//
//					if (keyCode == KeyEvent.KEYCODE_BACK) {
//						return true;
//					}
//					return false;
//				}
//
//			});
//			scanDialog.show();
//			setTimeOut();
//			break;
//		case R.id.wifi_ok:
//			if (!changeWifiFlag) {
//				showToast(R.string.wifi_setting_no_change);
//				return;
//			}
//			setWifi();
//			break;
//		case R.id.wifi_cancel:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// 锟剿筹拷锟斤拷锟斤拷
//		default:
//			break;
//		}
	}

	private void setTimeOut() {

		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Log.d(LOG_TAG, "isTimeOver");
				isTimerOver = true;
				mHandler.sendEmptyMessage(TIMEOUT);
			}
		};
		mTimerTimeOut = new Timer();
		mTimerTimeOut.schedule(task, INITTIMEOUT);
	}

	private void setWifi() {

		if (changeWifiFlag) {
			String pwd = editPwd.getText().toString();
			if (wifiBean.getAuthtype() == NO) {
				wifiBean.setWpa_psk("");
				wifiBean.setKey1("");
			} else {
				if (!TextUtils.isEmpty(pwd)) {
					if (wifiBean.getAuthtype() == WEP) {
						wifiBean.setKey1(pwd);
					} else {
						wifiBean.setWpa_psk(pwd);
					}
				} else {
					showToast(R.string.pwd_no_empty);
					return;
				}
			}
			Log.d("tag", "wifiBean.getEncryp():" + wifiBean.getEncryp()
					+ "wifiBean.getKeyformat():" + wifiBean.getKeyformat());

			try {
				NativeCaller.PPPPWifiSetting(wifiBean.getDid(),
						wifiBean.getEnable(), wifiBean.getSsid(),
						wifiBean.getChannel(), wifiBean.getMode(),
						wifiBean.getAuthtype(), wifiBean.getEncryp(),
						wifiBean.getKeyformat(), wifiBean.getDefkey(),
						wifiBean.getKey1(), wifiBean.getKey2(),
						wifiBean.getKey3(), wifiBean.getKey4(),
						wifiBean.getKey1_bits(), wifiBean.getKey2_bits(),
						wifiBean.getKey3_bits(), wifiBean.getKey4_bits(),
						wifiBean.getWpa_psk());

			} catch (Exception e) {
				showToast(R.string.wifi_scan_failed);
				e.printStackTrace();
			}
		}
		// Intent intent3 = new Intent(SettingWifiActivity.this,
		// MainActivity.class);
		// intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent3);
	}

	private void setttingTimeOut() {
		successFlag = false;
		mHandler.postAtTime(settingRunnable, INITTIMEOUT);
	}

	private Runnable settingRunnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				showToast(R.string.wifi_set_failed);
			}
		}
	};
	private TextView tvCameraName;

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			editPwd.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
		} else {
			editPwd.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		changeWifiFlag = true;
		WifiScanBean wifiScan = mAdapter.getWifiScan(position);
		wifiBean.setSsid(wifiScan.getSsid());
		wifiBean.setAuthtype(wifiScan.getSecurity());
		wifiBean.setChannel(wifiScan.getChannel());
		wifiBean.setDbm0(wifiScan.getDbm0());
		handler.sendEmptyMessage(1);

	}

	public void setListViewHeight() {
		ListAdapter adapter = mListView.getAdapter();
		if (adapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) { // listAdapter.getCount()锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷�?
			View listItem = adapter.getView(i, null, mListView);
			listItem.measure(0, 0); // 锟斤拷锟斤拷锟斤拷锟斤拷View 锟侥匡拷�?
			totalHeight += listItem.getMeasuredHeight(); // 统锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷芨叨锟?
		}
		ViewGroup.LayoutParams params = mListView.getLayoutParams();
		params.height = totalHeight
				+ (mListView.getDividerHeight() * (adapter.getCount() - 1));
		mListView.setLayoutParams(params);
	}

	/**
	 * BridgeService callback
	 * */
	@Override
	public void callBackWifiParams(String did, int enable, String ssid,
			int channel, int mode, int authtype, int encryp, int keyformat,
			int defkey, String key1, String key2, String key3, String key4,
			int key1_bits, int key2_bits, int key3_bits, int key4_bits,
			String wpa_psk) {
		Log.d("shix", "did:" + did + " enable:" + enable + " ssid:" + ssid
				+ " channel:" + channel + " authtype:" + authtype + " encryp:"
				+ encryp + " wpa_psk:" + wpa_psk);
		wifiBean.setDid(did);
		wifiBean.setEnable(1);// enable锟斤拷锟斤拷锟斤拷时�?��斤拷锟斤�?
		wifiBean.setSsid(ssid);
		wifiBean.setChannel(channel);
		wifiBean.setMode(0);// 0
		wifiBean.setAuthtype(authtype);// security 锟斤�?-锟斤拷锟斤拷锟斤拷锟斤拷
		wifiBean.setEncryp(0);// 0
		wifiBean.setKeyformat(0);// 0
		wifiBean.setDefkey(0);// 0
		wifiBean.setKey1(key1);// ""wep
		wifiBean.setKey2("");// ""
		wifiBean.setKey3("");// ""
		wifiBean.setKey4("");// ""
		wifiBean.setKey1_bits(0);// 0
		wifiBean.setKey2_bits(0);// 0
		wifiBean.setKey3_bits(0);// 0
		wifiBean.setKey4_bits(0);// 0
		wifiBean.setWpa_psk(wpa_psk);// 锟斤拷锟斤拷
		Log.d(LOG_TAG, wifiBean.toString());
		mHandler.sendEmptyMessage(WIFIPARAMS);
	}

	/**
	 * BridgeService callback
	 * */
	@Override
	public void callBackWifiScanResult(String did, String ssid, String mac,
			int security, int dbm0, int dbm1, int mode, int channel, int bEnd) {
		Log.d(LOG_TAG, "bEnd=" + bEnd);

		WifiScanBean bean = new WifiScanBean();
		bean.setDid(did);
		bean.setSsid(ssid);
		bean.setChannel(channel);
		bean.setSecurity(security);
		bean.setDbm0(dbm0);
		bean.setMac(mac);
		bean.setMode(mode);
		bean.setDbm1(dbm1);
		Message message = mHandler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putSerializable("bean", bean);
		message.what = ADDBEAN;
		message.setData(bundle);
		mHandler.sendMessage(message);
		if (bEnd == END) {
			Log.d(LOG_TAG, "缁撴�?bEnd=" + bEnd);
			mHandler.sendEmptyMessage(SCANPARAMS);
		}
	}


	/**
	 * BridgeService callback
	 * */
	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
			int result) {
		Log.d("tag", "result:" + result);
		this.result = result;
		mHandler.sendEmptyMessage(OVER);
	}

	/**
	 * BridgeService callback
	 * */
	@Override
	public void callBackPPPPMsgNotifyData(String did, int type, int param) {
		if (strDID.equals(did)) {
			if (ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS == type) {
				CAMERAPARAM = param;
			}
		}
	}
}
