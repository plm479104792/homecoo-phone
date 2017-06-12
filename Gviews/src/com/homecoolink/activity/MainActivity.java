package com.homecoolink.activity;

import java.io.File;
import java.util.ArrayList;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.homecoolink.P2PListener;
import com.homecoolink.R;
import com.homecoolink.SettingListener;
import com.homecoolink.data.DataManager;
import com.homecoolink.entity.Account;
import com.homecoolink.fragment.AlarmRecordFrag;
import com.homecoolink.fragment.ContactFrag;
import com.homecoolink.fragment.ImageFrag;
import com.homecoolink.fragment.SettingFrag;
import com.homecoolink.global.AccountPersist;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.global.NpcCommon.NETWORK_TYPE;
import com.homecoolink.utils.CrashHandler;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.network.GetAccountInfoResult;
import com.p2p.core.network.NetManager;
import com.p2p.core.update.UpdateManager;


public class MainActivity extends BaseActivity implements OnClickListener {
	public static Context mContext;

	private ImageView contact_img, alarmrecord_img, settings_img, discover_img,
			icon_new_img;	
	private RelativeLayout contact, alarmrecord, settings, discover;
	boolean isRegFilter = false;

	private int currFrag = 0;
	private AlertDialog dialog_update;
	private String[] fragTags = new String[] { "contactFrag",
			"AlarmRecordFrag", "imageFrag", "settingFrag" };// ,"keyboardFrag"
	private SettingFrag settingFrag;
	// private KeyboardFrag keyboardFrag;
	private AlarmRecordFrag AlarmRecordFrag;
	private ContactFrag contactFrag;
	// private UtilsFrag utilsFrag;
	private ImageFrag imageFrag;

	CrashHandler ch;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		Log.e("343", "MainActivity->>onCreate");
		super.onCreate(arg0);
		mContext = this;
		try {
			ch = CrashHandler.getInstance();
			ch.init(MainActivity.this);

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("343", Log.getStackTraceString(e));
		}
	

		DataManager.findAlarmMaskByActiveUser(mContext, "");
		NpcCommon.verifyNetwork(mContext);
		regFilter();
		
		if (!verifyLogin()) {
			Log.i("343", "跳转到了登录页面");
			Intent login = new Intent(mContext, LoginActivity.class);
			startActivity(login);
			finish();
		} else {
			Log.i("343", "没有进行登录验证");
			initComponent();
			new FList();
			P2PHandler.getInstance().p2pInit(this, new P2PListener(),
					new SettingListener());
			connect();
			if (null == contactFrag) {
				contactFrag = new ContactFrag();
			}
			// List<Contact> list =
			// DataManager.findContactByActiveUser(mContext,
			// NpcCommon.mThreeNum);
			// if(list.size()>0){
			currFrag = 0;
			if (null == contactFrag) {
				contactFrag = new ContactFrag();
			}
			replaceFragment(R.id.fragContainer, contactFrag, fragTags[0]);
			changeIconShow();
			// }else{
			// currFrag = 1;
			// if(null==AlarmRecordFrag){
			// AlarmRecordFrag = new AlarmRecordFrag();
			// }
			// replaceFragment(R.id.fragContainer,AlarmRecordFrag,fragTags[1]);
			// changeIconShow();
			// }

			changeIconShow();
			new GetAccountInfoTask().execute();

		}

	}

	private RelativeLayout getMenu(int id) {
		RelativeLayout res = (RelativeLayout) findViewById(id);
		// android.view.ViewGroup.LayoutParams paras = res.getLayoutParams();
		// paras.height=90;
		// res.setLayoutParams(paras);
		res.setOnClickListener(this);
		return res;
	}

	public void CheckNewMess() {
		icon_new_img = (ImageView) findViewById(R.id.icon_new_img);
		ArrayList<com.homecoolink.data.AlarmRecord> list = (ArrayList<com.homecoolink.data.AlarmRecord>) DataManager
				.findUnReadAlarmRecordByActiveUser(mContext,
						NpcCommon.mThreeNum);
		Log.e("343", "list.size=="+list.size());
		if (list.size() > 0) {
			icon_new_img.setVisibility(View.VISIBLE);

		} else {
			icon_new_img.setVisibility(View.GONE);
		}
	}
		

	public void initComponent() {		
		setContentView(R.layout.activity_main);
		// dials = getMenu(R.id.icon_keyboard);
		// dials_img = (ImageView) findViewById(R.id.icon_keyboard_img);
		contact = getMenu(R.id.icon_my);
		contact_img = (ImageView) findViewById(R.id.icon_my_img);
		alarmrecord = getMenu(R.id.icon_mess);
		alarmrecord_img = (ImageView) findViewById(R.id.icon_mess_img);
		settings = getMenu(R.id.icon_setting);
		settings_img = (ImageView) findViewById(R.id.icon_setting_img);
		discover = getMenu(R.id.icon_video);
		discover_img = (ImageView) findViewById(R.id.icon_video_img);
		CheckNewMess();
	}

	public void regFilter() {

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.ACTION_NETWORK_CHANGE);
		filter.addAction(Constants.Action.ACTION_SWITCH_USER);
		filter.addAction(Constants.Action.ACTION_EXIT);
		filter.addAction(Constants.Action.RECEIVE_MSG);
		filter.addAction(Constants.Action.RECEIVE_SYS_MSG);
		filter.addAction(Intent.ACTION_LOCALE_CHANGED);
		filter.addAction(Constants.Action.ACTION_UPDATE);
		filter.addAction(Constants.Action.SESSION_ID_ERROR);
		// by salad for new alarmmessage
		filter.addAction(Constants.Action.REFRESH_ALARM_RECORD);
		filter.addAction(Constants.P2P.ACK_RET_GET_ALAEM_RECORD);
		filter.addAction(Constants.P2P.RET_GET_ALARM_RECORD);
		filter.addAction("com.homecoolink.AlarmRecord_Read");
		// filter.addAction(Constants.Action.SETTING_WIFI_SUCCESS);
		this.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	Handler handler = new Handler() {
		long last_time;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int value = msg.arg1;
			switch (msg.what) {
			case UpdateManager.HANDLE_MSG_DOWNING:
				if ((System.currentTimeMillis() - last_time) > 1000) {
					MyApp.app.showDownNotification(
							UpdateManager.HANDLE_MSG_DOWNING, value);
					last_time = System.currentTimeMillis();
				}
				break;
			case UpdateManager.HANDLE_MSG_DOWN_SUCCESS:
				// MyApp.app.showDownNotification(UpdateManager.HANDLE_MSG_DOWN_SUCCESS,0);
				MyApp.app.hideDownNotification();
				// T.showShort(mContext, R.string.down_success);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				File file = new File(Environment.getExternalStorageDirectory()
						+ "/" + Constants.Update.SAVE_PATH + "/"
						+ Constants.Update.FILE_NAME);
				if (!file.exists()) {
					return;
				}
				intent.setDataAndType(Uri.fromFile(file),
						Constants.Update.INSTALL_APK);
				mContext.startActivity(intent);
				break;
			case UpdateManager.HANDLE_MSG_DOWN_FAULT:

				MyApp.app.showDownNotification(
						UpdateManager.HANDLE_MSG_DOWN_FAULT, value);
				T.showShort(mContext, R.string.down_fault);
				break;
			case 101:
				CheckNewMess();
				break;
			}
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			
			if (intent.getAction()
					.equals(Constants.Action.REFRESH_ALARM_RECORD)
					|| intent.getAction().equals(
							Constants.P2P.ACK_RET_GET_ALAEM_RECORD)
					|| intent.getAction().equals(
							Constants.P2P.RET_GET_ALARM_RECORD)
					|| intent.getAction().equals(
							"com.homecoolink.AlarmRecord_Read")) {		
				
				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						try {
							
							sleep(200);
							
//							CheckNewMess();
							handler.sendEmptyMessage(101);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}.start();
				
			} else if (intent.getAction().equals(
					Constants.Action.ACTION_NETWORK_CHANGE)) {
				boolean isNetConnect = false;
				ConnectivityManager connectivityManager = (ConnectivityManager) mContext
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				if (activeNetInfo != null) {
					if (activeNetInfo.isConnected()) {
						isNetConnect = true;
						T.showShort(mContext,
								getString(R.string.message_net_connect)
										+ activeNetInfo.getTypeName());
						Log.e("connect", "----");
					} else {
						T.showShort(mContext, getString(R.string.network_error)
								+ " " + activeNetInfo.getTypeName());
					}

					if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						NpcCommon.mNetWorkType = NETWORK_TYPE.NETWORK_WIFI;
					} else {
						NpcCommon.mNetWorkType = NETWORK_TYPE.NETWORK_2GOR3G;
					}
				} else {
					Toast.makeText(mContext, getString(R.string.network_error),
							Toast.LENGTH_SHORT).show();
				}

				NpcCommon.setNetWorkState(isNetConnect);

				Intent intentNew = new Intent();
				intentNew.setAction(Constants.Action.NET_WORK_TYPE_CHANGE);
				mContext.sendBroadcast(intentNew);
			} else if (intent.getAction().equals(
					Constants.Action.ACTION_SWITCH_USER)) {
//				Account account = AccountPersist.getInstance()
//						.getActiveAccountInfo(mContext);
//				new ExitTask(account).execute();
//				AccountPersist.getInstance().setActiveAccount(mContext,
//						new Account());
//				NpcCommon.mThreeNum = "";
//				Intent i = new Intent(MyApp.MAIN_SERVICE_START);
//				stopService(i);
//				Intent login = new Intent(mContext, LoginActivity.class);
//				startActivity(login);
				
				finish();
			} else if (intent.getAction().equals(
					Constants.Action.SESSION_ID_ERROR)) {
				Account account = AccountPersist.getInstance()
						.getActiveAccountInfo(mContext);
				new ExitTask(account).execute();
				AccountPersist.getInstance().setActiveAccount(mContext,
						new Account());
				Intent i = new Intent(MyApp.MAIN_SERVICE_START);
				stopService(i);
				Intent login = new Intent(mContext, LoginActivity.class);
				startActivity(login);
				T.showShort(mContext, R.string.session_id_error);
				finish();
			} else if (intent.getAction().equals(Constants.Action.ACTION_EXIT)) {
				NormalDialog dialog = new NormalDialog(mContext, mContext
						.getResources().getString(R.string.exit), mContext
						.getResources().getString(R.string.confirm_exit),
						mContext.getResources().getString(R.string.exit),
						mContext.getResources().getString(R.string.cancel));
				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						Intent i = new Intent(MyApp.MAIN_SERVICE_START);
						stopService(i);
						isGoExit(true);
						finish();
					}
				});
				dialog.showNormalDialog();
			} else if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {

			} else if (intent.getAction().equals(Constants.Action.RECEIVE_MSG)) {
				int result = intent.getIntExtra("result", -1);
				String msgFlag = intent.getStringExtra("msgFlag");

				if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
//					DataManager.updateMessageStateByFlag(mContext, msgFlag,
//							Constants.MessageType.SEND_SUCCESS);
//					Intent refresh = new Intent();
//					refresh.setAction(MessageActivity.REFRESH_MESSAGE);
//					sendBroadcast(refresh);
				} else {
//					DataManager.updateMessageStateByFlag(mContext, msgFlag,
//							Constants.MessageType.SEND_FAULT);
//					Intent refresh = new Intent();
//					refresh.setAction(MessageActivity.REFRESH_MESSAGE);
//					sendBroadcast(refresh);
				}

			} else if (intent.getAction().equals(
					Constants.Action.RECEIVE_SYS_MSG)) {

			} else if (intent.getAction()
					.equals(Constants.Action.ACTION_UPDATE)) {
				if (null != dialog_update && dialog_update.isShowing()) {
					Log.e("my", "isShowing");
					return;
				}

				View view = LayoutInflater.from(mContext).inflate(
						R.layout.dialog_update, null);
				TextView title = (TextView) view.findViewById(R.id.title_text);
				WebView content = (WebView) view
						.findViewById(R.id.content_text);
				TextView button1 = (TextView) view
						.findViewById(R.id.button1_text);
				TextView button2 = (TextView) view
						.findViewById(R.id.button2_text);

				title.setText(R.string.update);
				content.setBackgroundColor(0); 
				content.getBackground().setAlpha(0); 
				String data = intent.getStringExtra("updateDescription");
				content.loadDataWithBaseURL(null, data, "text/html", "utf-8",
						null);
				button1.setText(R.string.update_now);
				button2.setText(R.string.next_time);
				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != dialog_update) {
							dialog_update.dismiss();
							dialog_update = null;
						}
						if (UpdateManager.getInstance().getIsDowning()) {
							return;
						}
						MyApp.app.showDownNotification(
								UpdateManager.HANDLE_MSG_DOWNING, 0);
						T.showShort(mContext, R.string.start_down);
						new Thread() {
							@Override
							public void run() {
								UpdateManager.getInstance().downloadApk(
										handler, Constants.Update.SAVE_PATH,
										Constants.Update.FILE_NAME);
							}
						}.start();
					}
				});
				button2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != dialog_update) {
							dialog_update.cancel();
						}
					}
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				dialog_update = builder.create();
				dialog_update.show();
				dialog_update.setContentView(view);
				FrameLayout.LayoutParams layout = (LayoutParams) view
						.getLayoutParams();
				layout.width = (int) mContext.getResources().getDimension(
						R.dimen.update_dialog_width);
				view.setLayoutParams(layout);
				dialog_update.setCanceledOnTouchOutside(false);
				Window window = dialog_update.getWindow();
				window.setWindowAnimations(R.style.dialog_normal);
			} else if (intent.getAction().equals(
					Constants.Action.SETTING_WIFI_SUCCESS)) {
				currFrag = 0;
				if (null == contactFrag) {
					contactFrag = new ContactFrag();
				}
				replaceFragment(R.id.fragContainer, contactFrag, fragTags[0]);
				changeIconShow();
			}
		}

	};

	private void connect() {
		Intent service = new Intent(MyApp.MAIN_SERVICE_START);
		Log.e("wzytest", "se "+ getPackageName());
		service.setPackage(getPackageName());
		startService(service);
	}

	
	private boolean verifyLogin() {
		Account activeUser = AccountPersist.getInstance().getActiveAccountInfo(
				mContext);

		if (activeUser != null) {

			NpcCommon.mThreeNum = activeUser.three_number;

			return true;
		}

		return false;
	}

	@Override
	public void onClick(View view) {
		int id=view.getId();
		if(id==R.id.icon_my){
			currFrag = 0;
			if (null == contactFrag) {
				contactFrag = new ContactFrag();
			}
			replaceFragment(R.id.fragContainer, contactFrag, fragTags[0]);
			changeIconShow();
		}else if(id==R.id.icon_mess){
			currFrag = 1;
			if (null == AlarmRecordFrag) {
				AlarmRecordFrag = new AlarmRecordFrag();
			}
			replaceFragment(R.id.fragContainer, AlarmRecordFrag, fragTags[1]);
			changeIconShow();
		}else if(id==R.id.icon_setting){
			currFrag = 3;
			if (null == settingFrag) {
				settingFrag = new SettingFrag();
			}
			replaceFragment(R.id.fragContainer, settingFrag, fragTags[2]);
			changeIconShow();
		}else if(id==R.id.icon_video){
			currFrag = 2;
			if (null == imageFrag) {
				imageFrag = new ImageFrag();
			}
			replaceFragment(R.id.fragContainer, imageFrag, fragTags[3]);
			changeIconShow();
		}
		
		
		// TODO Auto-generated method stub
//		switch (view.getId()) {
//		case R.id.icon_my:
//			currFrag = 0;
//			if (null == contactFrag) {
//				contactFrag = new ContactFrag();
//			}
//			replaceFragment(R.id.fragContainer, contactFrag, fragTags[0]);
//			changeIconShow();
//			break;
//		// case R.id.icon_keyboard:
//		// currFrag = 1;
//		// if(null==keyboardFrag){
//		// keyboardFrag = new KeyboardFrag();
//		// }
//		// replaceFragment(R.id.fragContainer,keyboardFrag,fragTags[1]);
//		// changeIconShow();
//		// break;
//		case R.id.icon_mess:
//			currFrag = 1;
//			if (null == AlarmRecordFrag) {
//				AlarmRecordFrag = new AlarmRecordFrag();
//			}
//			replaceFragment(R.id.fragContainer, AlarmRecordFrag, fragTags[1]);
//			changeIconShow();
//			break;
//
//		case R.id.icon_setting:
//			currFrag = 3;
//			if (null == settingFrag) {
//				settingFrag = new SettingFrag();
//			}
//			replaceFragment(R.id.fragContainer, settingFrag, fragTags[2]);
//			changeIconShow();
//			break;
//		case R.id.icon_video:
//			currFrag = 2;
//			if (null == imageFrag) {
//				imageFrag = new ImageFrag();
//			}
//			replaceFragment(R.id.fragContainer, imageFrag, fragTags[3]);
//			changeIconShow();
//			break;
//		}

	}

	public void changeIconShow() {
		switch (currFrag) {
		case 0:
			contact_img.setImageResource(R.drawable.main_my_press);
			alarmrecord_img.setImageResource(R.drawable.main_mess);
			settings_img.setImageResource(R.drawable.main_setting);
			discover_img.setImageResource(R.drawable.main_video);
			contact.setSelected(true);
			alarmrecord.setSelected(false);
			settings.setSelected(false);
			discover.setSelected(false);
			break;
		case 1:
			contact_img.setImageResource(R.drawable.main_my);
			alarmrecord_img.setImageResource(R.drawable.main_mess_press);
			settings_img.setImageResource(R.drawable.main_setting);
			discover_img.setImageResource(R.drawable.main_video);
			contact.setSelected(false);
			alarmrecord.setSelected(true);
			settings.setSelected(false);
			discover.setSelected(false);
			break;
		case 2:
			contact_img.setImageResource(R.drawable.main_my);
			alarmrecord_img.setImageResource(R.drawable.main_mess);
			settings_img.setImageResource(R.drawable.main_setting);
			discover_img.setImageResource(R.drawable.main_video_press);
			contact.setSelected(false);
			alarmrecord.setSelected(false);
			settings.setSelected(false);
			discover.setSelected(true);
			break;
		case 3:
			contact_img.setImageResource(R.drawable.main_my);
			alarmrecord_img.setImageResource(R.drawable.main_mess);
			settings_img.setImageResource(R.drawable.main_setting_press);
			discover_img.setImageResource(R.drawable.main_video);
			contact.setSelected(false);
			alarmrecord.setSelected(false);
			settings.setSelected(true);
			discover.setSelected(false);
			break;

		}
	}

	public void replaceFragment(int container, Fragment fragment, String tag) {
		try {
			FragmentManager manager = getSupportFragmentManager();		
			FragmentTransaction transaction = manager.beginTransaction();
			// transaction.setCustomAnimations(android.R.anim.fade_in,
			// android.R.anim.fade_out);
			transaction.replace(R.id.fragContainer, fragment, tag);
			transaction.commit();
			manager.executePendingTransactions();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("my", "replaceFrag error--main");
		}
	}

	@Override
	public void onPause() {
		Log.e("life", "MainActivity->>onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.e("life", "MainActivity->>onResume");
		super.onResume();
		CheckNewMess();
	}

	@Override
	public void onStart() {
		Log.e("life", "MainActivity->>onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.e("life", "MainActivity->>onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.e("life", "MainActivity->>onDestroy");
		super.onDestroy();
		if (isRegFilter) {
			isRegFilter = false;
			this.unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onBackPressed() {
		Log.e("my", "onBackPressed");
		// if(null!=keyboardFrag&&currFrag==1){
		// if(keyboardFrag.IsInputDialogShowing()){
		// Intent close_input_dialog = new Intent();
		// close_input_dialog.setAction(Constants.Action.CLOSE_INPUT_DIALOG);
		// mContext.sendBroadcast(close_input_dialog);
		// return;
		// }
		// }
		Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		mContext.startActivity(mHomeIntent);
	}

	class GetAccountInfoTask extends AsyncTask {

		public GetAccountInfoTask() {

		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				Utils.sleepThread(1000);
				Account account = AccountPersist.getInstance()
						.getActiveAccountInfo(mContext);
				JSONObject jsonObject = NetManager.getInstance(mContext)
						.getAccountInfo(NpcCommon.mThreeNum, account.sessionId);
				return jsonObject;
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("343", Log.getStackTraceString(e));
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			if (object == null) {
				GetAccountInfoResult result = NetManager
						.createGetAccountInfoResult((JSONObject) object);
				switch (Integer.parseInt(result.error_code)) {
				case NetManager.SESSION_ID_ERROR:
					Intent i = new Intent();
					i.setAction(Constants.Action.SESSION_ID_ERROR);
					MyApp.app.sendBroadcast(i);
					break;
				case NetManager.CONNECT_CHANGE:
					new GetAccountInfoTask().execute();
					return;
				case NetManager.GET_ACCOUNT_SUCCESS:
					try {
						String email = result.email;
						String phone = result.phone;
						Account account = AccountPersist.getInstance()
								.getActiveAccountInfo(mContext);
						if (null == account) {
							account = new Account();
						}
						account.email = email;
						account.phone = phone;
						AccountPersist.getInstance().setActiveAccount(mContext,
								account);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}

		}

	}

	class ExitTask extends AsyncTask {
		Account account;

		public ExitTask(Account account) {
			this.account = account;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return NetManager.getInstance(mContext).exit_application(
					account.three_number, account.sessionId);
		}

		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			int result = (Integer) object;
			switch (result) {
			case NetManager.CONNECT_CHANGE:
				new ExitTask(account).execute();
				return;
			default:

				break;
			}
		}
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_MAINACTIVITY;
	}

}
