package et.song.remotestar;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;

import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevice;
import et.song.etclass.ETGroup;
import et.song.etclass.ETPage;
import et.song.etclass.ETSave;
import et.song.global.ETGlobal;
import et.song.jni.ble.ETBleClient;
import et.song.jni.bt.ETBtBroadcast;
import et.song.jni.bt.ETBtClient;
import et.song.jni.io.ETIO;
import et.song.jni.ir.ETIR;
import et.song.jni.usb.ETUSB;
import et.song.jni.wifi.ETWifiClient;
import et.song.network.ETNetClientAdapter;
import et.song.network.HXDP2PClient;
import et.song.network.HXDTCPClient;
import et.song.network.face.INetClient;
import et.song.remote.face.IRKeyValue;
import et.song.remotestar.hxd.sdk.R;

import et.song.tg.face.IFinish;
import et.song.tool.ETEnv;
import et.song.tool.ETTool;
import et.song.tool.ETWindow;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMain extends SherlockFragmentActivity implements
		OnClickListener {
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;

	private static final int MENU_TV = 0x00020000;
	private static final int MENU_USER = 0x00010000;
	private static final int MENU_USER_LOGIN = MENU_USER | 0x00000001;
	private static final int MENU_USER_IMPORT = MENU_USER | 0x00000002;
	private static final int MENU_USER_EXPORT = MENU_USER | 0x00000003;
	private static final int MENU_CONTROL = 0x00030000;
	private static final int MENU_CONTROL_IO = MENU_CONTROL | 0x00000002;
	private static final int MENU_CONTROL_DIR = MENU_CONTROL | 0x00000003;
	private static final int MENU_CONTROL_EXIT = MENU_CONTROL | 0x00000006;
	private static final int MENU_CONTROL_UPDATE = MENU_CONTROL | 0x00000007;
	private static final int MENU_CONTROL_ABOUT = MENU_CONTROL | 0x00000005;

	// private final static String UUID_SEND =
	// "0000dc86-0000-1000-8000-00805f9b34fb";
	// private final String UUID_SERVICE_SEND =
	// "00001802-0000-1000-8000-00805f9b34fb";
	// private final static String UUID_SEND =
	// "00002a06-0000-1000-8000-00805f9b34fb";
	// private final String UUID_SERVICE_RECV =
	// "0000ffa0-0000-1000-8000-00805f9b34fb";
	// private final static String UUID_RECV =
	// "0000ffa3-0000-1000-8000-00805f9b34fb";
	// private final static String UUID_ENABLE =
	// "0000ffa1-0000-1000-8000-00805f9b34fb";
	private final String UUID_SERVICE = "0000ffa0-0000-1000-8000-00805f9b34fb";
	private final static String UUID_SEND = "0000dc86-0000-1000-8000-00805f9b34fb";
	private final static String UUID_RECV = "0000ffa3-0000-1000-8000-00805f9b34fb";
	private final static String UUID_ENABLE = "0000ffa1-0000-1000-8000-00805f9b34fb";

	private boolean mIsExit = false;
	private StudyTask mStudyTask = null;
	private RecvReceiver mReceiver;
	private AlertDialog mAlertDialog;
	private Handler mHandlerTimer = null;
	private SlidingMenu mSlidingRight;
	private ListView mListViewRight = null;
	private SlidingTaskRight mSlidingTaskRight = null;
	private SlidingTaskLeft mSlidingTaskLeft = null;
	private SlidingMenu mSlidingLeft;
	private ListView mListViewLeft = null;
	// private PullToRefreshListView mPullRefreshListView;
	private Activity mActivity = null;
	private boolean mIsTimeOut = false;
	private View mBottom = null;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothAdapter.LeScanCallback mLeScanCallback;

	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		
		// 获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getSharedPreferences("tuwa", Context.MODE_PRIVATE);
		editor = preferences.edit();
		
		String operation = preferences.getString("OPERATION_TYPE", "");
		System.out.println("=======红外转发器操作类型======="+operation);
		
		setContentView(R.layout.activity_main2);
		setProgressBarIndeterminateVisibility(false);
		ETWindow.ScreenON(this);
		mActivity = this;
//		mBottom = (View) findViewById(R.id.fragment_bottom);
		LinearLayout channel1 = (LinearLayout) findViewById(R.id.channel1);
		channel1.setOnClickListener(this);
		LinearLayout channel2 = (LinearLayout) findViewById(R.id.channel2);
		channel2.setOnClickListener(this);
		LinearLayout channel3 = (LinearLayout) findViewById(R.id.channel3);
		channel3.setOnClickListener(this);

		if (getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
			mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

				@Override
				public void onLeScan(final BluetoothDevice device, int rssi,
						byte[] scanRecord) {
					String addr = ETSave.getInstance(mActivity).get(
							"ble_address");
					if (device.getAddress().equals(addr)) {
						scanLeDevice(false);
						bleOpen(addr);
					}
				}
			};
		}
		
        //遥控器按键学习
		LayoutInflater mInflater = LayoutInflater.from(this);
		View studyView = mInflater.inflate(R.layout.fragment_about_study, null);
		mAlertDialog = new AlertDialog.Builder(this).setView(studyView)
				.create();
		Window window = mAlertDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.7f;
		window.setAttributes(lp);
		mAlertDialog.setCancelable(false);

		// SlidingMenu
		mSlidingLeft = new SlidingMenu(this);
		mSlidingLeft.setMode(SlidingMenu.LEFT);
		mSlidingLeft.setShadowDrawable(R.drawable.drawer_shadow);
		mSlidingLeft.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// mSlidingLeft.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidingLeft.setBehindWidth(getResources().getDimensionPixelSize(
				R.dimen.menu_width));
		mSlidingLeft.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		mSlidingLeft.setMenu(R.layout.sliding_menu_left);
		mSlidingLeft.setOnOpenedListener(new OnOpenedListener() {
			@Override
			public void onOpened() {
				FLeft();
			}
		});
		mSlidingLeft.setOnClosedListener(new OnClosedListener() {
			@Override
			public void onClosed() {
				// mSlidingLeft.removeAllViews();
			}
		});
		mListViewLeft = (ListView) mSlidingLeft.findViewById(R.id.page_list);
		mListViewLeft.setOnItemClickListener(ItemClickLeft);

		// SlidingMenu Right
		mSlidingRight = new SlidingMenu(this);
		mSlidingRight.setMode(SlidingMenu.RIGHT);
		mSlidingRight.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// mSlidingRight.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingRight.setBehindWidth(getResources().getDimensionPixelSize(
				R.dimen.menu_width));
		mSlidingRight.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mSlidingRight.setMenu(R.layout.sliding_menu_right);
		mSlidingRight.setOnOpenedListener(new OnOpenedListener() {
			@Override
			public void onOpened() {
				FRight();
			}
		});
		mSlidingRight.setOnClosedListener(new OnClosedListener() {
			@Override
			public void onClosed() {
			}
		});

		mListViewRight = (ListView) mSlidingRight.findViewById(R.id.page_list);
		mListViewRight.setOnItemClickListener(ItemClickRight);
		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
			// FragmentDevice fragmentDevice = new FragmentDevice();
			// Bundle args = new Bundle();
			// args.putInt("group", 0);
			// fragmentDevice.setArguments(args);
			// getSupportFragmentManager().beginTransaction()
			// .add(R.id.fragment_container, fragmentDevice).commit();
			Fragment fragment = new FragmentGroup();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, fragment).commit();

			// Bundle args = new Bundle();
			// args.putInt("group", 0);
			// FragmentWizards fragmentWizards = new FragmentWizards();
			// fragmentWizards.setArguments(args);
			// getSupportFragmentManager().beginTransaction()
			// .add(R.id.fragment_container, fragmentWizards).commit();
		}

		mHandler.sendEmptyMessage(6);
		if (ETSave.getInstance(this).get("isSlidingMenu").equals("1")) {
			return;
		}
//		Dialog alertDialog = new AlertDialog.Builder(this)
//				.setMessage(R.string.str_study_start_info_4)
//				.setIcon(R.drawable.ic_launcher)
//				.setNegativeButton(R.string.str_info_no,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated
//								// method stub
//							}
//						})
//				.setPositiveButton(R.string.str_info_yes,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								ETSave.getInstance(mActivity).put(
//										"isSlidingMenu", "1");
//							}
//						}).create();
//		alertDialog.show();
	}

	@Override
	public void onStart() {
		super.onStart();

		ETGlobal.W = ETWindow.GetWindowWidth(this);
		ETGlobal.H = ETWindow.GetWindowHeight(this);
//		if (ETGlobal.W  > ETGlobal.H){
//			ETGlobal.H = ETWindow.GetWindowWidth(this);
//			ETGlobal.W = ETWindow.GetWindowHeight(this);			
//		}
		if (ETGlobal.W  < 320){
			ETGlobal.W = 320;
					
		}
		if (ETGlobal.H  < 240){
			ETGlobal.H = 240;
					
		}
		Log.i("W", String.valueOf(ETGlobal.W));
		Log.i("H", String.valueOf(ETGlobal.H));
		
		mHandlerTimer = new Handler();
		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETBtBroadcast.BROADCAST_BT_PAIR);
		filter.addAction(ETGlobal.BROADCAST_APP_UPDATE_START);
		filter.addAction(ETGlobal.BROADCAST_APP_UPDATE_LOADING);
		filter.addAction(ETGlobal.BROADCAST_APP_BUY_YES);
		filter.addAction(ETGlobal.BROADCAST_APP_BUY_NO);
		filter.addAction(ETGlobal.BROADCAST_OPEN_FINISH);
		filter.addAction(ETGlobal.BROADCAST_START_LEARN);
		filter.addAction(ETGlobal.BROADCAST_REPEAT_LEARN);
		filter.addAction(ETGlobal.BROADCAST_END_LEARN);
		filter.addAction(ETGlobal.BROADCAST_FOUND_COL);
		filter.addAction(ETBleClient.ACTION_GATT_SERVICES_DISCOVERED);
		filter.addAction(ETUSB.BROADCAST_USB_PERMISSION);
		registerReceiver(mReceiver, filter);
		try {
			if (ETGlobal.mTg != null) {
				ETGlobal.mTg.close();
				ETGlobal.mTg = null;
			}
			if (ETSave.getInstance(getApplication()).get("comType") == null) {
				ETSave.getInstance(getApplication()).put("comType", "");
			}
			if (ETSave.getInstance(this).get("comType").equals("bt")) {
				btOpen(ETSave.getInstance(this).get("bt_address"));
			} else if (ETSave.getInstance(this).get("comType").equals("ble")) {
				scanLeDevice(true);
			} else if (ETSave.getInstance(this).get("comType").equals("wifi")) {
				INetClient client = new HXDP2PClient(ETSave.getInstance(this)
						.get("wifi_uid"));
				ETNetClientAdapter mNetClientAdapter = new ETNetClientAdapter(
						client);
				ETGlobal.mTg = new ETWifiClient(mNetClientAdapter);
				Open();
			} else if (ETSave.getInstance(this).get("comType")
					.equals("wifilan")) {
				INetClient client = new HXDTCPClient(ETSave.getInstance(this)
						.get("wifilan_ip"), Integer.valueOf(ETSave.getInstance(
						this).get("wifilan_port")));
				ETNetClientAdapter mNetClientAdapter = new ETNetClientAdapter(
						client);
				ETGlobal.mTg = new ETWifiClient(mNetClientAdapter);
				Open();
			} else if (ETSave.getInstance(this).get("comType").equals("io")) {
				ETGlobal.mTg = new ETIO();
				Open();
			} else if (ETSave.getInstance(this).get("comType").equals("usb")) {

				if (ETUSB.hasPermission(getApplication(),
						ETUSB.getDevice(getApplication()))) {
					ETUSB.Init(getApplication());
					ETGlobal.mTg = new ETUSB(getApplication());
					Open();
				} else {
					ETUSB.Init(getApplication());
					ETGlobal.mTg = new ETUSB(getApplication());
				}

			} else if (ETSave.getInstance(this).get("comType").equals("sound")) {
				ETGlobal.mTg = new ThreeManRemote(getApplication());
				Open();
			} else {
				ETGlobal.mTg = new ETIO();
				Open();
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		jumpFragment();  //判断设备类型
	}

	@Override
	public void onStop() {
		super.onStop();
		mHandlerTimer.removeCallbacks(runnable);
		this.unregisterReceiver(mReceiver);
		if (ETGlobal.mTg != null) {
			try {
				ETGlobal.mTg.close();
				ETGlobal.mTg = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mSlidingTaskLeft != null && !mSlidingTaskLeft.isCancelled()) {
			mSlidingTaskLeft.cancel(true);
		}
		if (mSlidingTaskRight != null && !mSlidingTaskRight.isCancelled()) {
			mSlidingTaskRight.cancel(true);
		}
		

	}

	public void jumpFragment() {
		if (ETGlobal.mIsWifiWan) {
			return;
		}
		String strType = ETSave.getInstance(this).get("DeviceType");
		if (strType != null && !strType.equals("")) {
			Fragment fragment = null;
			int type = Integer.valueOf(strType);
			int deviceIndex = Integer.valueOf(ETSave.getInstance(this).get(
					"DeviceIndex"));
			int groupIndex = Integer.valueOf(ETSave.getInstance(this).get(
					"GroupIndex"));
			Bundle args = new Bundle();
			args.putInt("group", groupIndex);
			args.putInt("device", deviceIndex);
			switch (type) {
			case DeviceType.DEVICE_REMOTE_TV:
				fragment = new FragmentTV();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_IPTV:
				fragment = new FragmentIPTV();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_STB:
				fragment = new FragmentSTB();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_DVD:
				fragment = new FragmentDVD();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_FANS:
				fragment = new FragmentFans();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_PJT:
				fragment = new FragmentPJT();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_LIGHT:
				fragment = new FragmentLight();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_AIR:
				fragment = new FragmentAIR();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_DC:
				fragment = new FragmentDC();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_POWER:
				fragment = new FragmentPOWER();
				fragment.setArguments(args);
				break;
			case DeviceType.DEVICE_REMOTE_CUSTOM:
				fragment = new FragmentCustom();
				fragment.setArguments(args);
				break;
			}
			//2016.03.01
//			FragmentTransaction transaction = getSupportFragmentManager()
//					.beginTransaction();
//			fragment.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragment);
//			transaction.commit();
//			HideBottom();
		}

	}

	public void HideBottom() {
//		mBottom.setVisibility(View.GONE);
	}

	public void ShowBottom() {
//		mBottom.setVisibility(View.VISIBLE);
	}



	private void FLeft() {
		if (mSlidingTaskLeft == null
				|| mSlidingTaskLeft.getStatus().equals(
						AsyncTask.Status.FINISHED)) {
			mSlidingTaskLeft = new SlidingTaskLeft();
			mSlidingTaskLeft.execute();
		}
	}

	private void FRight() {
		if (mSlidingTaskRight == null
				|| mSlidingTaskRight.getStatus().equals(
						AsyncTask.Status.FINISHED)) {
			mSlidingTaskRight = new SlidingTaskRight();
			mSlidingTaskRight.execute();
		}
	}

	public void exit() {
//		if (!mIsExit) {
//			mIsExit = true;
//			Toast.makeText(getApplicationContext(),
//					getString(R.string.str_exit_info), Toast.LENGTH_SHORT)
//					.show();
//			mHandler.sendEmptyMessageDelayed(0, 2000);
//		} else {
			ETGlobal.mIsWifiWan = false;
			//使整个系统暂时隐藏
//			Intent i = new Intent(Intent.ACTION_MAIN);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			i.addCategory(Intent.CATEGORY_HOME);
//			startActivity(i);
			
			this.finish();
//		}
	
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if (ETGlobal.mIsTop) {
			Intent intent = new Intent(ETGlobal.BROADCAST_APP_BACK);
			sendBroadcast(intent);
			// exit();
			return true;
			// } else {
			// return super.onKeyDown(keyCode, event);
			// }
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			Log.i("Action", "ETGlobal.BROADCAST_KEYCODE_VOLUME_UP");
			Intent intentPass = new Intent(ETGlobal.BROADCAST_KEYCODE_VOLUME_UP);
			sendBroadcast(intentPass);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			Log.i("Action", "ETGlobal.BROADCAST_KEYCODE_VOLUME_DOWN");
			Intent intentPass = new Intent(
					ETGlobal.BROADCAST_KEYCODE_VOLUME_DOWN);
			sendBroadcast(intentPass);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (ETGlobal.mTg != null) {
				boolean isException = false;
				try {
					int len = ETGlobal.mTg.write("LINK".getBytes(),
							"LINK".length());
					if (len < 0) {
						isException = true;
					}
				} catch (Exception e) {
					isException = true;
				}
				if (isException) {
					try {
						ETGlobal.mTg.close();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ETGlobal.mTg = null;
				}
			}
			mHandlerTimer.postDelayed(this, 20000);
		}
	};

	Runnable runnableTimeOut = new Runnable() {
		@Override
		public void run() {
			mIsTimeOut = true;
		}
	};

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@SuppressLint("InflateParams")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mIsExit = false;
				break;
			case 1:
				if (!mAlertDialog.isShowing()) {
					mAlertDialog.show();
				}
				break;
			case 2:
				if (mAlertDialog.isShowing()) {
					mAlertDialog.dismiss();
				}
				break;
			case 3:
				// Bundle args = msg.getData();
				// Intent intent = new Intent(ETGlobal.BROADCAST_PASS_LEARN);
				// intent.putExtra("select", args.getString("select"));
				// intent.putExtra("msg", args.getString("msg"));
				// sendBroadcast(intent);

				break;
			case 4:
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(3000);
						} catch (Exception ex) {

						}
						mHandlerTimer.postDelayed(runnable, 20000);//
					}

				}).start();
				break;
			case 5:
				final Bundle bundle = msg.getData();
				final byte[] buffer = bundle.getByteArray("buffer");
				final String select = bundle.getString("select");
				final int k = bundle.getInt("key", 0);

				LayoutInflater mInflater = LayoutInflater.from(mActivity);
				View view = mInflater.inflate(R.layout.fragment_study_finish,
						null);
				TextView textViewTest = (TextView) view
						.findViewById(R.id.text_study_test);
				textViewTest.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						byte[] keyBuf = new byte[buffer.length + 2];
						for (int i = 0; i < buffer.length; i++) {
							keyBuf[i] = buffer[i];
						}
						ETIR.StudyCode(keyBuf, keyBuf.length);
						try {
							ETGlobal.mTg.write(keyBuf, keyBuf.length);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				AlertDialog dialog = new AlertDialog.Builder(mActivity)
						.setIcon(R.drawable.ic_launcher)
						.setTitle(R.string.str_study_finish)
						.setView(view)
						.setPositiveButton(R.string.str_study_save,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										try {
											String msg;
											msg = ETTool
													.BytesToHexString(buffer);
											Intent intent = new Intent(
													ETGlobal.BROADCAST_END_LEARN);
											intent.putExtra("select", select);
											intent.putExtra("msg", msg);
											intent.putExtra("key", k);
											sendBroadcast(intent);
										} catch (Exception e) {
											// TODO Auto-generated catch
											// block
											e.printStackTrace();
										}

									}
								})
						.setNegativeButton(R.string.str_study_repeat,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												ETGlobal.BROADCAST_START_LEARN);
										intent.putExtra("select", select);
										intent.putExtra("key", k);

										sendBroadcast(intent);

									}
								}).create();
				dialog.show();
				break;
			case 6:
				break;
			case 7:
				Dialog alertDialog = new AlertDialog.Builder(mActivity)
						.setMessage(R.string.str_update_5)
						.setIcon(R.drawable.ic_launcher)
						.setPositiveButton(R.string.str_ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated
										// method stub
									}
								}).create();
				alertDialog.show();
				break;
			case 8:

				LayoutInflater mInflaterAbout = LayoutInflater.from(mActivity);
				View aboutView = mInflaterAbout.inflate(
						R.layout.fragment_about, null);
				TextView text_about = (TextView) aboutView
						.findViewById(R.id.text_about);
				String strAbout = String.format(
						getResources().getString(R.string.str_about),
						String.valueOf(ETEnv.getVerName(getApplication(),
								ETEnv.getPackageName(getApplication()))));
				text_about.setText(strAbout);
				int count = ETIR.SearchAirCode(0, 0, 0);
				Log.i("Count", String.valueOf(count));
				if (count >= 100) {
					TextView text_copyright = (TextView) aboutView
							.findViewById(R.id.text_copyright);
					text_copyright.setVisibility(View.VISIBLE);
				}
				AlertDialog aboutDialog = new AlertDialog.Builder(mActivity)
						.setIcon(R.drawable.ic_launcher)
						.setTitle(R.string.about)
						.setView(aboutView)
						.setPositiveButton(R.string.str_other_ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								}).create();
				aboutDialog.show();
				break;
			case 9:
				FragmentWatchTV fragmentWatchTV = new FragmentWatchTV();
				FragmentTransaction transactionWatchTV = getSupportFragmentManager()
						.beginTransaction();
				transactionWatchTV.setCustomAnimations(R.anim.push_left_in,
						R.anim.push_left_out, R.anim.push_left_in,
						R.anim.push_left_out);
				transactionWatchTV.replace(R.id.fragment_container,
						fragmentWatchTV);
				transactionWatchTV.commit();
				break;
			case 10:
				Fragment fragmentCom = new FragmentCom();
				FragmentTransaction transactionCom = getSupportFragmentManager()
						.beginTransaction();
				transactionCom.setCustomAnimations(R.anim.push_left_in,
						R.anim.push_left_out, R.anim.push_left_in,
						R.anim.push_left_out);
				transactionCom.replace(R.id.fragment_container, fragmentCom);
				// transactionCom.addToBackStack(null);
				transactionCom.commit();
				break;
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		// getSupportMenuInflater().inflate(R.menu.menu_group, menu);
		// SubMenu subMenuUser = menu.addSubMenu(MENU_USER, MENU_USER, 0,
		// getString(R.string.str_menu_user));
		// subMenuUser.add(MENU_USER, MENU_USER_LOGIN, MENU_USER,
		// R.string.str_menu_user_login);
		// subMenuUser.add(MENU_USER, MENU_USER_IMPORT, MENU_USER,
		// R.string.str_menu_user_import);
		// subMenuUser.add(MENU_USER, MENU_USER_EXPORT, MENU_USER,
		// R.string.str_menu_user_export);
		// MenuItem menuItemUser = subMenuUser.getItem();
		// menuItemUser.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
		// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		//
		// menu.add(MENU_TV, MENU_TV, 1, getString(R.string.str_menu_tv))
		// .setShowAsAction(
		// MenuItem.SHOW_AS_ACTION_ALWAYS
		// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		// SubMenu subMenuControl = menu.addSubMenu(MENU_CONTROL, MENU_CONTROL,
		// 2,
		// getString(R.string.str_menu_more));
		// subMenuControl.add(MENU_CONTROL, MENU_CONTROL_IO, MENU_CONTROL,
		// R.string.str_menu_control_io);
		// subMenuControl.add(MENU_CONTROL, MENU_CONTROL_DIR, MENU_CONTROL,
		// R.string.str_menu_control_dir);
		// subMenuControl.add(MENU_CONTROL, MENU_CONTROL_UPDATE, MENU_CONTROL,
		// R.string.str_menu_control_update);
		// subMenuControl.add(MENU_CONTROL, MENU_CONTROL_EXIT, MENU_CONTROL,
		// R.string.str_menu_control_exit);
		// subMenuControl.add(MENU_CONTROL, MENU_CONTROL_ABOUT, MENU_CONTROL,
		// R.string.str_menu_control_about);
		// MenuItem menuItemControl = subMenuControl.getItem();
		// menuItemControl.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
		// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_TV:
			mHandler.sendEmptyMessage(9);
			break;
		case MENU_USER_LOGIN:
			break;
		case MENU_USER_IMPORT:
			break;
		case MENU_USER_EXPORT:
			break;
		case MENU_CONTROL_IO:
			mHandler.sendEmptyMessage(10);
			break;
		case MENU_CONTROL_DIR:

			break;
		case MENU_CONTROL_UPDATE:
			break;
		case MENU_CONTROL_EXIT:
			finish();
			break;
		case MENU_CONTROL_ABOUT:
			mHandler.sendEmptyMessage(8);
			// FragmentAbout fragmentAbout = new FragmentAbout();
			// getSupportFragmentManager().beginTransaction()
			// .add(R.id.fragment_container, fragmentAbout).commit();
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		
		int id=v.getId();
		if (id==R.id.channel1) {
			FragmentGroup fragmentGroup = new FragmentGroup();
			transaction.replace(R.id.fragment_container, fragmentGroup);
			transaction.commit();
		}else if (id==R.id.channel2) {
			mHandler.sendEmptyMessage(9);
		}else if (id==R.id.channel3) {
			FragmentMore fragmenMore = new FragmentMore();
			transaction.replace(R.id.fragment_container, fragmenMore);
			transaction.commit();
		}
		
		
//		switch (v.getId()) {
//		case R.id.channel1:
//			FragmentGroup fragmentGroup = new FragmentGroup();
//			transaction.replace(R.id.fragment_container, fragmentGroup);
//			transaction.commit();
//			break;
//		case R.id.channel2:
//			mHandler.sendEmptyMessage(9);
//			break;
//		case R.id.channel3:
//			FragmentMore fragmenMore = new FragmentMore();
//			transaction.replace(R.id.fragment_container, fragmenMore);
//			transaction.commit();
//			break;
//		}
	}




	class StudyTask extends AsyncTask<String, Integer, Void> {
		private byte[] buffer = null;
		private byte[] key = null;
		private int k = 0;
		private String select = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mIsTimeOut = false;
			mHandlerTimer.removeCallbacks(runnable);
			mHandlerTimer.postDelayed(runnableTimeOut, 45000);
		}

		@SuppressLint("InflateParams")
		@Override
		protected Void doInBackground(String... params) {
			boolean isException = false;
			Log.i("Start Learn", "Start Learn  1");
			k = Integer.valueOf(params[1]);
			if (params[0].equals("0")) {
				select = "0";
				buffer = new byte[110];
				key = new byte[] { (byte) 0x30, (byte) 0x10, (byte) 0x40 };
			} else if (params[0].equals("1")) {
				select = "1";
				buffer = new byte[230];
				key = new byte[] { (byte) 0x30, (byte) 0x20, (byte) 0x50 };
			} else if (params[0].equals("99")) {
				select = "99";
				buffer = new byte[110];
				key = new byte[] { (byte) 0x30, (byte) 0x10, (byte) 0x40 };
			} else if (params[0].equals("100")) {
				select = "100";
				buffer = new byte[230];
				key = new byte[] { (byte) 0x30, (byte) 0x20, (byte) 0x50 };
			} else {
				return null;
			}
			try {
				ETGlobal.mTg.write(key, key.length);
				Thread.sleep(200);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			mHandler.sendEmptyMessage(1);
			while (true && !mIsTimeOut) {
				Arrays.fill(buffer, (byte) 0x00);
				int bytes;
				try {
					bytes = ETGlobal.mTg.read(buffer, buffer.length);
					Log.i("Study", String.valueOf(bytes));
					if (bytes > 0) {
						// if (buffer[1] == 0x00 && buffer[2] == 0x00
						// && buffer[3] == 0x00 && buffer[4] == 0x00) {
						// break;
						// }
						if (select.equals("99") || select.equals("100")) {
							String msg;
							msg = ETTool.BytesToHexString(buffer);
							Intent intent = new Intent(
									ETGlobal.BROADCAST_END_LEARN);
							intent.putExtra("select", select);
							intent.putExtra("msg", msg);
							intent.putExtra("key", k);
							sendBroadcast(intent);
						} else {
							Bundle bundle = new Bundle();
							bundle.putByteArray("buffer", buffer);
							bundle.putString("select", select);
							bundle.putInt("key", k);
							Message msg = new Message();
							msg.what = 5;
							msg.setData(bundle);
							mHandler.sendMessage(msg);
						}
						break;
					} else if (bytes == -1001) {
						Log.i("Study", "Study  4");
						continue;
					} else if (bytes == -10007) {
						Log.i("Study", "Study  6");
						continue;
					} else {
						Log.i("Study", "Study  5");
						byte[] data = new byte[] { (byte) 0x10, (byte) 0x20,
								(byte) 0x30, (byte) 0x40 };
						int len = ETGlobal.mTg.write(data, data.length);
						if (len < 0) {
							isException = true;
							break;
						}
						Thread.sleep(100);
						ETGlobal.mTg.write(key, key.length);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					isException = true;
					break;
				}
				// publishProgress(1);
			}
			if (isException) {
				try {
					ETGlobal.mTg.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ETGlobal.mTg = null;
			}
			mHandlerTimer.removeCallbacks(runnableTimeOut);
			mHandlerTimer.postDelayed(runnable, 20000);
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mHandler.sendEmptyMessage(2);
		}
	}

	public class RecvReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i("action", intent.getAction());
			// if (intent.getAction().equals(ETBtBroadcast.BROADCAST_BT_PAIR)) {
			// BluetoothDevice btDevice = intent
			// .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// try {
			// ETBtClsUtils.setPin(btDevice.getClass(), btDevice, "1234");
			// ETBtClsUtils.createBond(btDevice.getClass(), btDevice);
			// ETBtClsUtils.cancelPairingUserInput(btDevice.getClass(),
			// btDevice);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// } else
			if (action.equals(ETGlobal.BROADCAST_OPEN_FINISH)) {
				String state = intent.getStringExtra("state");
				ETGlobal.mIsWifiWan = false;
				if (state.equals("success")) {
					Toast.makeText(context,
							getString(R.string.str_success_open_device),
							Toast.LENGTH_SHORT).show();
					mHandler.sendEmptyMessage(4);

				} else if (state.equals("faile")) {
					Toast.makeText(context,
							getString(R.string.str_error_open_device),
							Toast.LENGTH_SHORT).show();
				}
			} else if (action
					.equals(ETBleClient.ACTION_GATT_SERVICES_DISCOVERED)) {
				displayGattServices(((ETBleClient) (ETGlobal.mTg))
						.getSupportedGattServices());
			} else if (action.equals(ETGlobal.BROADCAST_FOUND_COL)) {
				String select = intent.getStringExtra("select");
				int key = intent.getIntExtra("key", 0);
				if (mStudyTask == null
						|| mStudyTask.getStatus().equals(
								AsyncTask.Status.FINISHED)) {
					mStudyTask = new StudyTask();
					mStudyTask.execute(select, String.valueOf(key));
				}
			} else if (action.equals(ETGlobal.BROADCAST_START_LEARN)) {

				String select = intent.getStringExtra("select");
				int key = intent.getIntExtra("key", 0);
				if (mStudyTask == null
						|| mStudyTask.getStatus().equals(
								AsyncTask.Status.FINISHED)) {
					mStudyTask = new StudyTask();
					mStudyTask.execute(select, String.valueOf(key));
				}
			} else if (action.equals(ETGlobal.BROADCAST_REPEAT_LEARN)) {
				String select = intent.getStringExtra("select");
				int key = intent.getIntExtra("key", 0);
				if (mStudyTask == null
						|| mStudyTask.getStatus().equals(
								AsyncTask.Status.FINISHED)) {
					mStudyTask = new StudyTask();
					mStudyTask.execute(select, String.valueOf(key));
				}
			} else if (action.equals(ETGlobal.BROADCAST_END_LEARN)) {
				String select = intent.getStringExtra("select");
				String data = intent.getStringExtra("msg");
				int key = intent.getIntExtra("key", 0);
				Intent intentPass = new Intent(ETGlobal.BROADCAST_PASS_LEARN);
				intentPass.putExtra("select", select);
				intentPass.putExtra("key", key);
				intentPass.putExtra("msg", data);
				sendBroadcast(intentPass);

				// Message msg = new Message();
				// msg.what = 3;
				// Bundle args = new Bundle();
				// args.putString("select", select);
				// args.putString("msg", data);
				// msg.setData(args);
				// mHandler.sendMessage(msg);

			} else if (action.equals(ETGlobal.BROADCAST_DATABASE_LOAD)) {

			} else if (action.equals(ETGlobal.BROADCAST_APP_BUY_YES)) {
				mHandler.sendEmptyMessage(8);

			} else if (action.equals(ETGlobal.BROADCAST_APP_BUY_NO)) {
				mHandler.sendEmptyMessage(10);
			} else if (action.equals(ETGlobal.BROADCAST_APP_UPDATE_START)) {
				mHandler.sendEmptyMessage(6);
			} else if (ETUSB.BROADCAST_USB_PERMISSION.equals(action)) {
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,
						false)) {
					Open();
				}
			} 
		}
	}

	class SlidingTaskRight extends AsyncTask<String, Void, Void> {

		ArrayList<AdapterWatchTVItem> items = new ArrayList<AdapterWatchTVItem>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				Cursor c = ETDB.getInstance(mActivity).queryData2Cursor(
						"select * from " + DBProfile.WATCHTV_TABLE_NAME, null);
				String[] watchtvs = getResources().getStringArray(
						R.array.strs_watch_tv);
				for (int i = 0; i < ETGlobal.mWatchTVImages.length; i++) {
					AdapterWatchTVItem item = new AdapterWatchTVItem();
					item.setContext("");
					item.setDID(-1);
					item.setName(watchtvs[i]);
					item.setValue("");
					item.setRes(i);
					items.add(item);
				}
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					int id = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_ID));
					int did = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_DEVICE_ID));
					String name = c
							.getString(c
									.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_NAME));
					String context = c
							.getString(c
									.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_CONTEXT));
					int res = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_RES));
					int value = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_VALUE));
					String value_ex = c
							.getString(c
									.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_VALUE_EX));
					int isOK = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_ISOK));
					AdapterWatchTVItem item = items.get(res);
					item.setID(id);
					item.setDID(did);
					item.setName(name);
					item.setContext(context);
					if (value_ex == null) {
						item.setValue(String.valueOf(value));
					} else {
						item.setValue(value_ex);
					}
					if (isOK == 0) {
						item.setOK(false);
					} else {
						item.setOK(true);
					}
					item.setRes(res);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			AdapterWatchTVList mAdapter = new AdapterWatchTVList(mActivity,
					items);
			mListViewRight.setAdapter(mAdapter);
		}

	}

	private void ShowEditInfo() {
		Dialog alertDialog = new AlertDialog.Builder(this)
				.setMessage(R.string.str_study_start_info_8)
				.setIcon(R.drawable.ic_launcher)
				.setNegativeButton(R.string.str_edit_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated
								// method stub
							}
						})
				.setPositiveButton(R.string.str_edit_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mSlidingRight.showContent();
								mHandler.sendEmptyMessage(9);
							}
						}).create();
		alertDialog.show();
	}

	OnItemClickListener ItemClickRight = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			AdapterWatchTVItem item = (AdapterWatchTVItem) arg0
					.getItemAtPosition(arg2);
			try {
				Cursor c = ETDB.getInstance(mActivity).queryData2Cursor(
						"select * from " + DBProfile.DEVICE_TABLE_NAME
								+ " where " + DBProfile.TABLE_DEVICE_FIELD_ID
								+ " = " + item.getDID(), null);
				if (c.getCount() == 0) {
					ShowEditInfo();
					return;
				}
				if (item.getValue().equals("000")) {
					ShowEditInfo();
					return;
				}

				c.moveToFirst();
				ETDevice device = null;
				int type = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_TYPE));
				device = ETDevice.Builder(type);
				if (device == null)
					return;
				device.SetID(item.getDID());
				device.Load(ETDB.getInstance(mActivity));
				if (item.isSelect()) {
					int key = 0;
					if (type == DeviceType.DEVICE_REMOTE_TV) {
						key = IRKeyValue.KEY_TV_SELECT;
					} else if (type == DeviceType.DEVICE_REMOTE_STB) {

					} else if (type == DeviceType.DEVICE_REMOTE_IPTV) {

					}
					if (key == 0)
						return;
					byte[] keyValue = device.GetKeyValue(key);
					if (keyValue == null)
						return;
					ETGlobal.mTg.write(keyValue, keyValue.length);
					Thread.sleep(400);
				}
				String val = String.valueOf(item.getValue());
				for (int i = 0; i < val.length(); i++) {
					int v = Integer.valueOf(val.substring(i, i + 1));
					int key = 0;
					if (type == DeviceType.DEVICE_REMOTE_TV) {
						switch (v) {
						case 0:
							key = IRKeyValue.KEY_TV_KEY0;
							break;
						case 1:
							key = IRKeyValue.KEY_TV_KEY1;
							break;
						case 2:
							key = IRKeyValue.KEY_TV_KEY2;
							break;
						case 3:
							key = IRKeyValue.KEY_TV_KEY3;
							break;
						case 4:
							key = IRKeyValue.KEY_TV_KEY4;
							break;
						case 5:
							key = IRKeyValue.KEY_TV_KEY5;
							break;
						case 6:
							key = IRKeyValue.KEY_TV_KEY6;
							break;
						case 7:
							key = IRKeyValue.KEY_TV_KEY7;
							break;
						case 8:
							key = IRKeyValue.KEY_TV_KEY8;
							break;
						case 9:
							key = IRKeyValue.KEY_TV_KEY9;
							break;
						}
					} else if (type == DeviceType.DEVICE_REMOTE_IPTV) {
						switch (v) {
						case 0:
							key = IRKeyValue.KEY_IPTV_KEY0;
							break;
						case 1:
							key = IRKeyValue.KEY_IPTV_KEY1;
							break;
						case 2:
							key = IRKeyValue.KEY_IPTV_KEY2;
							break;
						case 3:
							key = IRKeyValue.KEY_IPTV_KEY3;
							break;
						case 4:
							key = IRKeyValue.KEY_IPTV_KEY4;
							break;
						case 5:
							key = IRKeyValue.KEY_IPTV_KEY5;
							break;
						case 6:
							key = IRKeyValue.KEY_IPTV_KEY6;
							break;
						case 7:
							key = IRKeyValue.KEY_IPTV_KEY7;
							break;
						case 8:
							key = IRKeyValue.KEY_IPTV_KEY8;
							break;
						case 9:
							key = IRKeyValue.KEY_IPTV_KEY9;
							break;
						}
					} else if (type == DeviceType.DEVICE_REMOTE_STB) {
						switch (v) {
						case 0:
							key = IRKeyValue.KEY_STB_KEY0;
							break;
						case 1:
							key = IRKeyValue.KEY_STB_KEY1;
							break;
						case 2:
							key = IRKeyValue.KEY_STB_KEY2;
							break;
						case 3:
							key = IRKeyValue.KEY_STB_KEY3;
							break;
						case 4:
							key = IRKeyValue.KEY_STB_KEY4;
							break;
						case 5:
							key = IRKeyValue.KEY_STB_KEY5;
							break;
						case 6:
							key = IRKeyValue.KEY_STB_KEY6;
							break;
						case 7:
							key = IRKeyValue.KEY_STB_KEY7;
							break;
						case 8:
							key = IRKeyValue.KEY_STB_KEY8;
							break;
						case 9:
							key = IRKeyValue.KEY_STB_KEY9;
							break;
						}
					}
					if (key == 0)
						continue;
					byte[] keyValue = device.GetKeyValue(key);
					if (keyValue == null)
						continue;
					ETGlobal.mTg.write(keyValue, keyValue.length);
					Thread.sleep(400);
				}
				if (item.isOK()) {
					int key = 0;
					if (type == DeviceType.DEVICE_REMOTE_TV) {
						key = IRKeyValue.KEY_TV_OK;
					} else if (type == DeviceType.DEVICE_REMOTE_STB) {
						key = IRKeyValue.KEY_STB_OK;
					} else if (type == DeviceType.DEVICE_REMOTE_IPTV) {
						key = IRKeyValue.KEY_IPTV_OK;
					}
					if (key == 0)
						return;
					byte[] keyValue = device.GetKeyValue(key);
					if (keyValue == null)
						return;
					ETGlobal.mTg.write(keyValue, keyValue.length);
					Thread.sleep(400);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	};

	class SlidingTaskLeft extends AsyncTask<String, Void, Void> {
		ArrayList<AdapterSlidingItem> items = new ArrayList<AdapterSlidingItem>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			ETPage page = ETPage.getInstance(getApplication());
			// page.Load(ETDB.getInstance(getApplication()));
			for (int i = 0; i < page.GetCount(); i++) {
				ETGroup group = (ETGroup) page.GetItem(i);
				if (group.GetType() != ETGlobal.ETGROUP_TYPE_ADD) {
					items.add(new AdapterSlidingItem(group.GetName(), "("
							+ String.valueOf(group.GetCount() - 1) + ")", true,
							group.GetRes(), group.GetType(), i, 0));
					for (int j = 0; j < group.GetCount(); j++) {
						ETDevice device = (ETDevice) group.GetItem(j);
						if (device.GetType() != DeviceType.DEVICE_ADD) {
							items.add(new AdapterSlidingItem("", device
									.GetName(), false, device.GetRes(), device
									.GetType(), i, j));
						}
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			AdapterSlidingList mAdapter = new AdapterSlidingList(
					ActivityMain.this, items);
			mListViewLeft.setAdapter(mAdapter);
			// mPullRefreshListView.onRefreshComplete();
		}
	}

	OnItemClickListener ItemClickLeft = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			AdapterSlidingItem item = (AdapterSlidingItem) arg0
					.getItemAtPosition(arg2);
			if (item.isGroup()) {
				FragmentDevice fragmentDevice = new FragmentDevice();
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				Bundle args = new Bundle();
				args.putInt("pos", item.getGroupPos());
				fragmentDevice.setArguments(args);
				transaction.replace(R.id.fragment_container, fragmentDevice);
				transaction.commit();
			} else {
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				Fragment fragment = null;
				Bundle args = new Bundle();
				args.putInt("group", item.getGroupPos());
				args.putInt("device", item.getDevicePos());
				switch (item.getType()) {
				case DeviceType.DEVICE_REMOTE_TV:
					fragment = new FragmentTV();

					break;
				case DeviceType.DEVICE_REMOTE_IPTV:
					fragment = new FragmentIPTV();
					break;
				case DeviceType.DEVICE_REMOTE_STB:
					fragment = new FragmentSTB();

					break;
				case DeviceType.DEVICE_REMOTE_DVD:
					fragment = new FragmentDVD();

					break;
				case DeviceType.DEVICE_REMOTE_FANS:
					fragment = new FragmentFans();

					break;
				case DeviceType.DEVICE_REMOTE_PJT:
					fragment = new FragmentPJT();

					break;
				case DeviceType.DEVICE_REMOTE_LIGHT:
					fragment = new FragmentLight();

					break;
				case DeviceType.DEVICE_REMOTE_AIR:
					fragment = new FragmentAIR();

					break;
				case DeviceType.DEVICE_REMOTE_DC:
					fragment = new FragmentDC();

					break;
				case DeviceType.DEVICE_REMOTE_CUSTOM:
					fragment = new FragmentCustom();
					break;
				}
				fragment.setArguments(args);
				transaction.replace(R.id.fragment_container, fragment);
				transaction.commit();
				mSlidingLeft.showContent();
			}
		}

	};

	private void btOpen(String btAddress) {

		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		BluetoothDevice remoteDevice = BluetoothAdapter.getDefaultAdapter()
				.getRemoteDevice(btAddress);
		// if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
		// try {
		// ETBtClsUtils.setPin(remoteDevice.getClass(), remoteDevice,
		// "1234"); // �ֻ��������ɼ������
		// ETBtClsUtils.createBond(remoteDevice.getClass(), remoteDevice);
		// // ClsUtils.cancelPairingUserInput(device.getClass(), device);
		// } catch (Exception e) {
		// e.printStackTrace();
		// } //
		//
		// } else {
		// try {
		// ETBtClsUtils.createBond(remoteDevice.getClass(), remoteDevice);//
		// ������
		// ETBtClsUtils.setPin(remoteDevice.getClass(), remoteDevice,
		// "1234"); // �ֻ��������ɼ������
		// ETBtClsUtils.createBond(remoteDevice.getClass(), remoteDevice);
		// // ClsUtils.cancelPairingUserInput(device.getClass(), device);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		ETGlobal.mTg = new ETBtClient(remoteDevice,
				UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
		try {
			ETGlobal.mTg.open(new IFinish() {
				@Override
				public void OpenCallbk(int arg0) {
					if (arg0 < 0) {
						ETGlobal.mTg = null;
					} else {
						try {
							Intent intent = new Intent(
									ETGlobal.BROADCAST_OPEN_FINISH);
							intent.putExtra("state", "success");
							sendBroadcast(intent);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void Open() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ETGlobal.mTg.open(new IFinish() {
						@Override
						public void OpenCallbk(int arg0) {
							// TODO Auto-generated method stub
							if (arg0 < 0) {
								ETGlobal.mTg = null;
							} else {
								try {
									Intent intent = new Intent(
											ETGlobal.BROADCAST_OPEN_FINISH);
									intent.putExtra("state", "success");
									sendBroadcast(intent);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void dir(View view) {
		// if (getRequestedOrientation() ==
		// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// } else if (getRequestedOrientation() ==
		// ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// }
		int dir = getRequestedOrientation();
		if (dir == 0) {
			setRequestedOrientation(1);
		} else if (dir == 1) {
			setRequestedOrientation(8);
		} else if (dir == 8) {
			setRequestedOrientation(9);
		} else if (dir == 9) {
			setRequestedOrientation(0);
		}

	}

	public void io(View view) {
		Fragment fragmentCom = new FragmentCom();
		FragmentTransaction transactionCom = getSupportFragmentManager()
				.beginTransaction();
		transactionCom
				.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out,
						R.anim.push_left_in, R.anim.push_left_out);
		transactionCom.replace(R.id.fragment_container, fragmentCom);
		// transactionCom.addToBackStack(null);
		transactionCom.commit();
	}

	public void update(View view) {
	}

	public void exit(View view) {
		ETGlobal.mIsWifiWan = false;
		finish();
	}

	public void about(View view) {
		mHandler.sendEmptyMessage(8);
	}

	@SuppressLint("InflateParams")
	public void rq(View view) {
		LayoutInflater mInflaterRQ = LayoutInflater.from(mActivity);
		View RQView = mInflaterRQ.inflate(R.layout.fragment_rq, null);
		AlertDialog aboutDialog = new AlertDialog.Builder(mActivity)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.str_menu_info_rq)
				.setView(RQView)
				.setPositiveButton(R.string.str_other_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		aboutDialog.show();
	}

	public void bleOpen(String btAddress) {
		ETGlobal.mTg = new ETBleClient(this, btAddress);
		try {
			ETGlobal.mTg.open(new IFinish() {
				@Override
				public void OpenCallbk(int arg0) {
					// TODO Auto-generated method stub
					Intent intentMsgin = new Intent(
							ETGlobal.BROADCAST_OPEN_FINISH);
					if (arg0 < 0) {
						intentMsgin.putExtra("state", "faile");
						sendBroadcast(intentMsgin);
						ETGlobal.mTg = null;
					} else {
						intentMsgin.putExtra("state", "success");
						sendBroadcast(intentMsgin);
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Handler handler = new Handler();

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void scanLeDevice(boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, 10000);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	@SuppressLint("NewApi")
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;
		for (BluetoothGattService gattService : gattServices) {
			if (UUID_SERVICE.equals(gattService.getUuid().toString())) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					if (gattCharacteristic.getUuid().compareTo(
							UUID.fromString(UUID_SEND)) == 0) {
						((ETBleClient) (ETGlobal.mTg)).RegUUIDForSend(
								UUID_SEND, gattCharacteristic);
					} else if (gattCharacteristic.getUuid().toString()
							.equals(UUID_RECV)) {
						((ETBleClient) (ETGlobal.mTg)).RegUUIDForRecv(
								UUID_RECV, gattCharacteristic);
					} else if (gattCharacteristic.getUuid().toString()
							.equals(UUID_ENABLE)) {
						((ETBleClient) (ETGlobal.mTg)).RegUUIDForEnable(
								UUID_ENABLE, gattCharacteristic);
					}
				}
			}

			// else if (UUID_SERVICE_RECV.equals(gattService.getUuid()
			// .toString())) {
			// List<BluetoothGattCharacteristic> gattCharacteristics =
			// gattService
			// .getCharacteristics();
			// for (BluetoothGattCharacteristic gattCharacteristic :
			// gattCharacteristics) {
			// if (gattCharacteristic.getUuid().toString()
			// .equals(UUID_RECV)) {
			// ((ETBleClient) (mTg)).RegUUIDForRecv(UUID_RECV,
			// gattCharacteristic);
			// } else if (gattCharacteristic.getUuid().toString()
			// .equals(UUID_ENABLE)) {
			// ((ETBleClient) (mTg)).RegUUIDForEnable(UUID_ENABLE,
			// gattCharacteristic);
			// }
			//
			// }
			//
			// }
		}
	}
}
