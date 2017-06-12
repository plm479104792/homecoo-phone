package com.homecoolink.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.homecoolink.R;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.T;
import com.homecoolink.utils.UDPHelper;
import com.mediatek.elian.ElianNative;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AddWaitActicity extends BaseActivity implements OnClickListener {
	private ImageView ivBacke;
	private Context mContext;
	public Handler myhandler = new Handler();
	boolean isReceive = false;
	String ssid, pwd;

	Thread mThread = null;
	boolean mDone = true;
	public UDPHelper mHelper;
	byte type;
	int mLocalIp;
	ElianNative elain;
	private boolean isSendWifiStop = true;
	private boolean isTimerCancel = true;
	private boolean isNeedSendWifi = true;
	private long TimeOut;
	WifiManager.MulticastLock lock;
	static {

	
		System.loadLibrary("elianjni");
		

	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.activity_add_waite);

		mContext = this;
		try {
			WifiManager manager = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			lock = manager.createMulticastLock("localWifi");
			ssid = getIntent().getStringExtra("ssidname");
			pwd = getIntent().getStringExtra("wifiPwd");
			type = getIntent().getByteExtra("type", (byte) -1);
			mLocalIp = getIntent().getIntExtra("LocalIp", -1);
			isNeedSendWifi = getIntent()
					.getBooleanExtra("isNeedSendWifi", true);
			initUI();
			if (isNeedSendWifi) {
				TimeOut = 110 * 1000;
				excuteTimer();
			} else {
				TimeOut = 60 * 1000;
			}

			lock.acquire();

			mHelper = new UDPHelper(9988);
			listen();
			myhandler.postDelayed(mrunnable, TimeOut);
			mHelper.StartListen();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("343", Log.getStackTraceString(e));
		}

	}

	private Timer mTimer;
	private int time;

	private void excuteTimer() {
		mTimer = new Timer();
		TimerTask mTask = new TimerTask() {
			@Override
			public void run() {
				if (time < 3) {
					sendWifiHandler.sendEmptyMessage(0);
				} else {
					sendWifiHandler.sendEmptyMessage(1);
				}
			}
		};
		mTimer.schedule(mTask, 500, 30 * 1000);
		isTimerCancel = false;
	}

	private void cancleTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			isTimerCancel = true;
		}

	}

	private Handler sendWifiHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message arg0) {
			switch (arg0.what) {
			case 0:
				time++;
				sendWifi();
				Log.i("dxsnewTimer", "第" + time + "次发包时间:" + getTime());
				break;
			case 1:
				cancleTimer();
				Log.i("dxsnewTimer", "第" + time + "次停止计时器时间:" + getTime());
				break;
			case 2:
				stopSendWifi();
				Log.i("dxsnewTimer", "第" + time + "次停止发包时间:" + getTime());
				break;

			default:
				break;
			}
			return false;
		}
	});

	/**
	 * 发包 20秒后停止
	 */
	private void sendWifi() {
		if (elain == null) {
			elain = new ElianNative();
		}
		if (null != ssid && !"".equals(ssid)) {
			elain.InitSmartConnection(null, 1, 1);
			elain.StartSmartConnection(ssid, pwd, "", type);
			Log.e("wifi_mesg", "ssidname=" + ssid + "--" + "wifipwd=" + pwd
					+ "--" + "type=" + type);
			isSendWifiStop = false;
		}
		sendWifiHandler.postDelayed(stopRunnable, 20 * 1000);
	}

	public Runnable stopRunnable = new Runnable() {
		@Override
		public void run() {
			sendWifiHandler.sendEmptyMessage(2);
		}
	};

	/**
	 * 停止发包
	 */
	private void stopSendWifi() {
		if (elain != null) {
			elain.StopSmartConnection();
			isSendWifiStop = true;
		}

	}

	private void initUI() {
		ivBacke = (ImageView) findViewById(R.id.img_back);
		ivBacke.setOnClickListener(this);
	}

	void listen() {
		mHelper.setCallBack(new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case UDPHelper.HANDLER_MESSAGE_BIND_ERROR:
					Log.e("my", "HANDLER_MESSAGE_BIND_ERROR");
					T.showShort(mContext, R.string.port_is_occupied);
					break;
				case UDPHelper.HANDLER_MESSAGE_RECEIVE_MSG:					
					
					isReceive = true;
					Log.e("my", "HANDLER_MESSAGE_RECEIVE_MSG");
					// NormalDialog successdialog=new NormalDialog(mContext);
					// successdialog.successDialog();
					T.showShort(mContext, R.string.set_wifi_success);
					mHelper.StopListen();
					Bundle bundle = msg.getData();
					// Contact saveContact=new Contact();
					// saveContact=(Contact)
					// bundle.getSerializable("saveContact");
					// Intent add_device=new
					// Intent(mContext,AddContactNextActivity.class);
					// add_device.putExtra("saveContact", saveContact);
					// add_device.putExtra("isCreatePassword", true);
					// startActivity(add_device);

					Intent it = new Intent();
					it.setAction(Constants.Action.RADAR_SET_WIFI_SUCCESS);
					sendBroadcast(it);
					FList flist = FList.getInstance();
					flist.updateOnlineState();
					flist.searchLocalDevice();

					String contactId = bundle.getString("contactId");
					String frag = bundle.getString("frag");
					String ipFlag = bundle.getString("ipFlag");
					Contact saveContact = new Contact();
					Log.e("343", "contactId="+contactId+";frag="+frag+";ipFlag="+ipFlag);
					saveContact.contactId = contactId;
					saveContact.activeUser = NpcCommon.mThreeNum;
					Intent add_device = new Intent(mContext,
							AddContactNextActivity.class);
					add_device.putExtra("contact", saveContact);
					try {
					
						if (Integer.parseInt(frag) == Constants.DeviceFlag.UNSET_PASSWORD) {
							add_device.putExtra("isCreatePassword", true);
						} else {
							add_device.putExtra("isCreatePassword", false);
						}
						
					} catch (Exception e) {
						
						Log.e("343", Log.getStackTraceString(e));
					}
					add_device.putExtra("isfactory", true);
					add_device.putExtra("ipFlag", ipFlag);
					startActivity(add_device);
					// Intent modify = new Intent();
					// modify.setClass(mContext, LocalDeviceListActivity.class);
					// mContext.startActivity(modify);
					finish();
					break;
				}
				cancleTimer();
			}

		});
	}

	public Runnable mrunnable = new Runnable() {

		@Override
		public void run() {
			if (!isReceive) {
				if (isNeedSendWifi) {
					T.showShort(mContext, R.string.set_wifi_failed);
					Intent it = new Intent();
					it.setAction(Constants.Action.RADAR_SET_WIFI_FAILED);
					sendBroadcast(it);
					
					finish();
				} else {
					T.showShort(mContext, R.string.set_wifi_failed);
					finish();
				}

			}
		}
	};

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.img_back){
			finish();
		}
		
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.img_back:
//			finish();
//			break;
//		default:
//			break;
//		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		myhandler.removeCallbacks(mrunnable);
		sendWifiHandler.removeCallbacks(stopRunnable);
		mHelper.StopListen();
		if (!isSendWifiStop) {
			stopSendWifi();
		}
		if (!isTimerCancel) {
			cancleTimer();
		}
		lock.release();
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_ADDWAITACTIVITY;
	}

	private String getTime() {
		String time = new SimpleDateFormat("HH-mm-ss").format(new Date());
		return time;
	}

}
