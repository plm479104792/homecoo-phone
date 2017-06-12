package et.song.remotestar;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.dialog.ETLoadDialog;
import et.song.etclass.ETSave;
import et.song.etclass.ETWifiDevice;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.jni.wifi.ETWifiClient;
import et.song.jni.wifi.ETWifiMg;
import et.song.network.ETNetClientAdapter;
import et.song.network.ETTCPClient;
import et.song.network.HXDP2PClient;
import et.song.network.face.INetClient;
import et.song.remotestar.hxd.sdk.R;
import et.song.tg.face.IFinish;
import et.song.tool.ETTool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentWifiWAN extends SherlockFragment implements
		OnClickListener, IBack {
	private EditText mEditWifiPwd;
	private EditText mEditWifiId;
	private List<ScanResult> mWifiList;
	private RecvReceiver mReceiver;
	private ETWifiMg mWifiManager = null;
	private LinkTask mLinkTask = null;
	private ProgressBar mProgressLoad = null;
	private ProgressBar mProgressBar = null;
	private TextView mTextInfo = null;
	// private ArrayAdapter<String> mWifiArrayAdapter;
	private List<ETWifiDevice> mWifiDevices = new ArrayList<ETWifiDevice>();
	private GridView mGridView;
	public final static int SCANNIN_GREQUEST_CODE = 0x00000001;
	private ETWifiDevice mWifiDevice = null;
	private ETLoadDialog mLoadOpen = null;
	private boolean mIsStart = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getWifiDevices();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_wifi_wan, container,
				false);
		mGridView = (GridView) view.findViewById(R.id.grid);
		mGridView.setBackgroundColor(Color.TRANSPARENT);
		mGridView.setAdapter(new GridAdapter(getActivity()));
		mGridView.setOnItemClickListener(new ItemClickListener());
		// mGridView.setOnItemLongClickListener(new ItemLongClickListener());
		registerForContextMenu(mGridView);
		Button mButtonScan = (Button) view.findViewById(R.id.button_scan);
		mButtonScan.setOnClickListener(this);
		Button mButtonOk = (Button) view.findViewById(R.id.button_ok);
		mButtonOk.setOnClickListener(this);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
		mProgressBar.setVisibility(View.GONE);
		mProgressLoad = (ProgressBar) view.findViewById(R.id.progress_load);
		mProgressLoad.setMax(100);
		mProgressLoad.setIndeterminate(false);
		mTextInfo = (TextView) view.findViewById(R.id.text_show_info);
		mEditWifiPwd = (EditText) view.findViewById(R.id.edit_pass);
		mEditWifiId = (EditText) view.findViewById(R.id.edit_id);
		CheckBox check_show_pass = (CheckBox) view
				.findViewById(R.id.check_show_pass);
		check_show_pass
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						// TODO Auto-generated method stub
						if (arg1) {

							mEditWifiPwd
									.setTransformationMethod(HideReturnsTransformationMethod
											.getInstance());
						} else {

							mEditWifiPwd
									.setTransformationMethod(PasswordTransformationMethod
											.getInstance());
						}
					}

				});
		CheckNetwork();

		// Spinner spinner_ssid = (Spinner) view
		// .findViewById(R.id.spinner_wifi_ssid);
		// mWifiArrayAdapter = new ArrayAdapter<String>(getActivity(),
		// R.layout.fragment_wifi_list_item);
		// spinner_ssid.setAdapter(mWifiArrayAdapter);
		// spinner_ssid.setOnItemSelectedListener(this);

		// for (int i = 0; i < mWifiList.size(); i++) {
		// mWifiArrayAdapter.add(mWifiList.get(i).SSID);
		// }

		return view;
	}

	private boolean CheckNetwork() {

		boolean flag = false;

		ConnectivityManager cwjManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cwjManager.getActiveNetworkInfo() != null)

			flag = cwjManager.getActiveNetworkInfo().isAvailable();

		if (!flag) {
			Builder b = new AlertDialog.Builder(this.getActivity())

			.setMessage(getString(R.string.str_wifi_info));

			b.setPositiveButton(getString(R.string.str_ok),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

							startActivity(new Intent(
									Settings.ACTION_WIFI_SETTINGS));

						}

					})
					.setNeutralButton(getString(R.string.str_cancel),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int whichButton) {

									dialog.cancel();

								}

							}).create();

			b.show();
		}
		return flag;

	}

	@Override
	public void onStart() {
		super.onStart();
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
		if (mWifiManager == null) {
			mWifiManager = new ETWifiMg(getActivity());
		}
		mWifiManager.startScan();
		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public void onStop() {
		super.onStop();
		this.getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLinkTask != null && !mLinkTask.isCancelled()) {
			mLinkTask.cancel(true);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Nothing to see here.
		menu.clear();
		inflater.inflate(R.menu.menu_wifi, menu);
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			Back();
//			return true;
//		case R.id.menu_control_about:
//			LayoutInflater mInflater = LayoutInflater.from(getActivity());
//			View settingView = mInflater.inflate(R.layout.fragment_about, null);
//			AlertDialog aboutDialog = new AlertDialog.Builder(getActivity())
//					.setIcon(R.drawable.ic_launcher)
//					.setTitle(R.string.about)
//					.setView(settingView)
//					.setPositiveButton(R.string.str_other_ok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int whichButton) {
//								}
//							}).create();
//			aboutDialog.show();
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.button_scan:
//			ETGlobal.mIsWifiWan = true;
//			Intent intent = new Intent();
//			intent.setClass(getActivity(), ActivityCapture.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
//			break;
//		case R.id.button_ok:
//			String uid = mEditWifiId.getText().toString();
//			String pwd = mEditWifiPwd.getText().toString();
//			if (uid.length() < 20) {
//				mEditWifiId.setText("");
//				mEditWifiId.setFocusable(true);
//				return;
////			}
//			if (pwd.length() == 0) {
//				return;
//			}
//	//		 if (mWifiManager.getSSID().contains("YuanHang")) {
//	//		 return;
//	//		 }
//			mProgressBar.setVisibility(View.VISIBLE);
//			mTextInfo.setText("");
//			ETWifiDevice device = new ETWifiDevice();
//			device.SetIP("");
//			device.SetName(uid.substring(0, 8));
//			device.SetPort(0);
//			device.SetRes(0);
//			device.SetType(0);
//			device.SetWan(1);
//			device.SetUID(uid);
//			device.SetSSID(mWifiManager.getSSID().replace("\"", ""));
//			device.SetPWD(mEditWifiPwd.getText().toString());
//			mWifiDevice = device;
//			for (int i = 0; i < mWifiList.size(); i++) {
//				ScanResult result = mWifiList.get(i);
//				if (result.SSID.contains("YuanHang")) {
//					mWifiManager.disconnectWifi(mWifiManager.getNetworkId());
//					mWifiManager.addNetwork(mWifiManager.CreateWifiInfo(
//							"YuanHang", "12345678", ETWifiMg.TYPE_WPA2));
//					mIsStart = true;
//					break;
//				}
//			}
//			break;
//		}
	}

	public void Setting() {
		if (mLinkTask == null
				|| mLinkTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
			mLinkTask = new LinkTask();
			mLinkTask.execute();
		}
	}

	//
	// @Override
	// public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	// // TODO Auto-generated method stub
	// String ssid = ((TextView) arg1).getText().toString();
	// mEditWifiSSID.setText(ssid);
	// }
	//
	// @Override
	// public void onNothingSelected(AdapterView<?> arg0) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("Data", Integer.valueOf(requestCode).toString());
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				mEditWifiId.setText(bundle.getString("result"));
			}
			break;
		}
	}

	private void getWifiDevices() {
		mWifiDevices.clear();
		ETDB db = ETDB.getInstance(getActivity());
		try {
			Cursor c = db.queryData2Cursor("select * from "
					+ DBProfile.WIFIDEVICE_TABLE_NAME + " where "
					+ DBProfile.TABLE_WIFIDEVICE_FIELD_WAN + " = 1 ", null);
			Log.i("Count", Integer.valueOf(c.getCount()).toString());
			if (c.getCount() > 0) {
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					ETWifiDevice device = new ETWifiDevice();
					int id = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_ID));
					String name = c
							.getString(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_NAME));
					String uid = c
							.getString(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_UID));
					String ssid = c
							.getString(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_SSID));
					String pwd = c
							.getString(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_PWD));
					int type = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_TYPE));
					int res = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_RES));
					int iswan = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_WAN));
					int port = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_PORT));
					String ip = c
							.getString(c
									.getColumnIndex(DBProfile.TABLE_WIFIDEVICE_FIELD_IP));
					device.SetID(id);
					device.SetUID(uid);
					device.SetSSID(ssid);
					device.SetPWD(pwd);
					device.SetName(name);
					device.SetType(type);
					device.SetRes(res);
					device.SetWan(iswan);
					device.SetIP(ip);
					device.SetPort(port);
					device.Load(db);
					mWifiDevices.add(device);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			mWifiDevice = (ETWifiDevice) arg0.getItemAtPosition(pos);
			try {
				if (ETGlobal.mTg != null) {
					ETGlobal.mTg.close();
				}
				mTextInfo.setText("");
				INetClient client = new HXDP2PClient(mWifiDevice.GetUID());
				ETNetClientAdapter mNetClientAdapter = new ETNetClientAdapter(
						client);
				ETGlobal.mTg = new ETWifiClient(mNetClientAdapter);
				Open();
				ETSave.getInstance(getActivity()).put("comType", "wifi");
				ETSave.getInstance(getActivity()).put("wifi_uid",
						mWifiDevice.GetUID());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// private class ItemLongClickListener implements OnItemLongClickListener {
	//
	// @Override
	// public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos,
	// long arg3) {
	//
	// return true;
	// }
	//
	// }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		getActivity().getMenuInflater().inflate(R.menu.menu_wifi_longclick,
				menu);

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		ETWifiDevice device = mWifiDevices.get(menuInfo.position);
//		switch (item.getItemId()) {
//		case R.id.menu_wifi_longclick_del:
//			device.Delete(ETDB.getInstance(getActivity()));
//			getWifiDevices();
//			((GridAdapter) mGridView.getAdapter()).notifyDataSetChanged();
//			ETSave.getInstance(getActivity()).put("comType", "");
//			ETSave.getInstance(getActivity()).put("wifi_uid", "");
//			return true; /* true means: "we handled the event". */
//		}
		return super.onContextItemSelected(item);
	}

	private class GridAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public GridAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mWifiDevices.size();
		}

		@Override
		public Object getItem(int position) {
			return mWifiDevices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup par) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.fragment_wifidevice_item, null);
				holder = new ViewHolder();
				holder.image_grid_item_res = ((ImageView) convertView
						.findViewById(R.id.image_grid_item_res));
				holder.text_grid_item_name = ((TextView) convertView
						.findViewById(R.id.text_grid_item_name));
				holder.text_grid_item_context = ((TextView) convertView
						.findViewById(R.id.text_grid_item_context));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ETWifiDevice device = (ETWifiDevice) mWifiDevices.get(position);
			holder.image_grid_item_res
					.setImageResource(ETGlobal.mWifiDeviceImages[device
							.GetRes()]);
			holder.text_grid_item_name.setText(device.GetName());
			holder.text_grid_item_context.setText("");
			return convertView;
		}

		private class ViewHolder {
			ImageView image_grid_item_res;
			TextView text_grid_item_name;
			TextView text_grid_item_context;
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				((GridAdapter) mGridView.getAdapter()).notifyDataSetChanged();
				break;
			}
		}
	};

	private void Open() {
		mLoadOpen = new ETLoadDialog(getActivity());
		mLoadOpen.setTitle(R.string.str_setting_finish);
		mLoadOpen.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ETGlobal.mTg.open(new IFinish() {
						@Override
						public void OpenCallbk(int arg0) {
							// TODO Auto-generated method stub
							if (mLoadOpen != null && mLoadOpen.isShowing()) {
								mLoadOpen.dismiss();
							}
							Intent intentMsgin = new Intent(
									ETGlobal.BROADCAST_OPEN_FINISH);
							if (arg0 < 0) {
								ETGlobal.mTg = null;
								intentMsgin.putExtra("state", "faile");
								getActivity().sendBroadcast(intentMsgin);
							} else {
								intentMsgin.putExtra("state", "success");
								getActivity().sendBroadcast(intentMsgin);
								FragmentGroup fragmentGroup = new FragmentGroup();
								FragmentTransaction transaction = getActivity()
										.getSupportFragmentManager()
										.beginTransaction();
								transaction.replace(R.id.fragment_container,
										fragmentGroup);
								transaction.addToBackStack(null);
								transaction.commit();
							}
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	class LinkTask extends AsyncTask<String, Integer, Void> {
		private String mAuthMode = null;
		// private String mEncrypType = null;
		private String mSSID = null;
		// private String mBSSID = null;
		private String mStaAuth;
		private String mStaEncrypt;
		private int mChannel;
		private boolean mIsFound = false;

		private void Set_Auth_Encry(String mCap) {
			if (mCap == null)
				return;
			if (mCap.indexOf("WPA2-PSK-CCMP") != -1) {
				mAuthMode = "WPA2PSK";
				// mEncrypType = "AES";
				mStaEncrypt = "6";
				mStaAuth = "7";
			} else if (mCap.indexOf("WPA2-PSK-TKIP") != -1) {
				mAuthMode = "WPA2PSK";
				// mEncrypType = "TKIP";
				mStaEncrypt = "4";
				mStaAuth = "7";

			} else if (mCap.indexOf("WPA2-PSK-TKIP+CCMP") != -1) {
				mAuthMode = "WPA2PSK";
				// mEncrypType = "AES";
				mStaEncrypt = "6";
				mStaAuth = "7";
			} else if (mCap.indexOf("WPA-PSK-TKIP") != -1) {
				mAuthMode = "WPAPSK";
				// mEncrypType = "TKIP";
				mStaEncrypt = "4";
				mStaAuth = "4";
			} else if (mCap.indexOf("WPA-PSK-CCMP") != -1) {
				mAuthMode = "WPAPSK";
				// mEncrypType = "AES";
				mStaEncrypt = "6";
				mStaAuth = "4";
			} else if (mCap.indexOf("WPA-PSK-TKIP+CCMP") != -1) {
				mAuthMode = "WPAPSK";
				// mEncrypType = "AES";
				mStaEncrypt = "6";
				mStaAuth = "4";
			}

			return;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			String ssid = mWifiDevice.GetSSID();
			for (int i = 0; i < mWifiList.size(); i++) {
				ScanResult result = mWifiList.get(i);
				if (result.SSID.equals(ssid)) {
					mSSID = result.SSID;
					// mBSSID = result.BSSID;
					mAuthMode = result.capabilities;
					mChannel = mWifiManager.getChanel(result.frequency);
					Set_Auth_Encry(mAuthMode);
					mIsFound = true;
					// System.out.print(mBSSID + mEncrypType);
					break;
				}
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			if (!mIsFound) {
				return null;
			}
			if (mWifiDevice == null)
				return null;
			String packet = "AT+netmode=5&" + "AT+wifi_conf=" + mSSID + ","
					+ mStaAuth + "," + mStaEncrypt + "," + mWifiDevice.GetPWD()
					+ "&" + "AT+Channel=" + mChannel + "&" + "AT+dhcpc=dhcp&"
					+ "AT+p2p_uid=" + mWifiDevice.GetUID() + "&"
					+ "AT+net_commit=1&";
			int size = packet.length();
			if (size > 0 && size < 99) {
				packet = "ST" + "2" + String.valueOf(size) + "00" + packet
						+ "E";
			} else if (size > 100 && size < 999) {
				packet = "ST" + "3" + String.valueOf(size) + "0" + packet + "E";
			} else if (size > 1000 && size < 9999) {
				packet = "ST" + "4" + String.valueOf(size) + packet + "E";
			}
			ETTCPClient client = new ETTCPClient("192.168.0.1", 9090);
			int count = 5;
			while (count-- > 0) {
				if (client.open() < 0) {
					try {
						Thread.sleep(500);
					} catch (Exception ex) {

					}
					continue;
				} else {
					break;
				}
			}
			try {
				client.Write(packet.getBytes(), 0, packet.length());
				client.close();
			} catch (Exception e) {
				return null;
			}

			mWifiManager.disconnectWifi(mWifiManager.getNetworkId());

			mWifiManager.closeWifi();
			long s = ETTool.T();
			while (true) {
				long e = ETTool.T();
				long t = e - s;
				if (t > 80000) {
					break;
				}
				if (t % 1000 == 0) {
					publishProgress(((int) (t / 1000)) * 1);
				}
			}
			publishProgress(100);
			ETDB db = ETDB.getInstance(getActivity());
			ETWifiDevice device = new ETWifiDevice();
			device.SetIP("");
			device.SetName(mWifiDevice.GetUID().substring(0, 8));
			device.SetPort(9090);
			device.SetRes(0);
			device.SetType(0);
			device.SetWan(1);
			device.SetUID(mWifiDevice.GetUID());
			device.SetSSID(mWifiDevice.GetSSID());
			device.SetPWD(mWifiDevice.GetPWD());
			device.Inster(db);
			getWifiDevices();
			mHandler.sendEmptyMessage(1);
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			mProgressLoad.setProgress(5 + progress[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgressBar.setVisibility(View.GONE);
			if (mProgressLoad.getProgress() < 75) {
				return;
			}
			mTextInfo.setText("");
			INetClient client = new HXDP2PClient(mWifiDevice.GetUID());
			ETNetClientAdapter mNetClientAdapter = new ETNetClientAdapter(
					client);
			ETGlobal.mTg = new ETWifiClient(mNetClientAdapter);
			Open();
			ETSave.getInstance(getActivity()).put("comType", "wifi");
			ETSave.getInstance(getActivity()).put("wifi_uid",
					mWifiDevice.GetUID());
		}
	}

	class RecvReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				mWifiList = mWifiManager.getWifiList();
			} else if (intent.getAction().equals(
					WifiManager.WIFI_STATE_CHANGED_ACTION)) {

				int wifiState = intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE, 0);
				switch (wifiState) {
				case WifiManager.WIFI_STATE_DISABLED:
					mWifiManager.openWifi();
					break;
				case WifiManager.WIFI_STATE_DISABLING:

					break;
				case WifiManager.WIFI_STATE_ENABLED:
					if (mWifiDevice != null) {
						mWifiManager
								.disconnectWifi(mWifiManager.getNetworkId());
						mWifiManager.addNetwork(mWifiManager.CreateWifiInfo(
								mWifiDevice.GetSSID(), mWifiDevice.GetPWD(),
								ETWifiMg.TYPE_WPA2));
					}
					break;
				}

			} else if (intent.getAction().equals(
					WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				Parcelable parcelableExtra = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					boolean isConnected = state == State.CONNECTED;
					if (isConnected) {
						String ssid = mWifiManager.getSSID().replace("\"", "");
						if ("YuanHang".equals(ssid) && mIsStart) {
							Setting();
						}
					}
				}
			} else if (intent.getAction().equals(ETGlobal.BROADCAST_APP_BACK)) {
				Back();
			}
		}
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentCom fragmentCom = new FragmentCom();
		FragmentTransaction transactionCom = getActivity()
				.getSupportFragmentManager().beginTransaction();
		// transactionCom.setCustomAnimations(R.anim.push_left_in,
		// R.anim.push_left_out, R.anim.push_left_in,
		// R.anim.push_left_out);

		transactionCom.replace(R.id.fragment_container, fragmentCom);
		// transactionBt.addToBackStack(null);
		// transactionCom
		// .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transactionCom.commit();
	}

}
