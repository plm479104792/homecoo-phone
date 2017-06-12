package object.dbnewgo.client;

import java.io.File;
import java.util.Date;

import object.dbnewgo.client.BridgeService.SnapShotInterface;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.VibratorUtil;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActivityGroup;
import android.app.KeyguardManager;
import android.app.LocalActivityManager;
import android.app.NotificationManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActivityGroup implements OnTouchListener,
		OnClickListener {
	private String strRingtoneFolder = "/sdcard/music/ringtone";
	private String strAlarmFolder = "/sdcard/music/alarms";
	private SharedPreferences preAudio;
	private LinearLayout layoutVidicon;
	private LinearLayout layoutAlarm;
	private LinearLayout layoutPic;
	private LinearLayout layoutVid;

	private TextView main_tv_vidicon, main_tv_alarm, main_tv_picture,
			main_tv_vid;
	private TextView textView_title;
	private Button btnback;   
	private int tabPosition = 1;
	private LinearLayout container;
	private MyBroadCast receiver;
	private FrameLayout img;
	Handler handler = new Handler();
	public static KeyguardLock keyguardLock = null;
	private Button btn_edit, btn_menu;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// getWindow().clearFlags(
				// WindowManager.LayoutParams.FLAG_FULLSCREEN);
				ShowScreen(IpcamClientActivity.class);
				img.setVisibility(View.GONE);
				break;
			default:
				break;
			}

		}
	};
//01-15 18:16:31.254: A/libc(396): Fatal signal 11 (SIGSEGV), code 1, fault addr 0xc in tid 1843 (Thread-1791)

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(ContentCommon.MAIN_KEY_MENU);
			sendBroadcast(intent);
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent intent = new Intent(ContentCommon.MAIN_KEY_BACK);
//			sendBroadcast(intent);
			finish(); 
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mymain);
		preAudio = getSharedPreferences("shix_zhao_audio", Context.MODE_PRIVATE);
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, BridgeService.class);
		startService(intent);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.d("test", "------------------1---------------");
					NativeCaller.PPPPInitial(SystemValue.SystemSerVer);
					Log.d("test", "------------------2---------------");
					long lStartTime = new Date().getTime();
					int nRes = NativeCaller.PPPPNetworkDetect();
					Log.d("test", "------------------3---------------");
					long lEndTime = new Date().getTime();
					Message msg1 = new Message();
					msg1.what = 0;
					mHandler.sendMessage(msg1);
					if (lEndTime - lStartTime <= 1000) {
//						Thread.sleep(3000);
						Thread.sleep(1000);
					}
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				} catch (Exception e) {

				}
			}
		}).start();
		findView();
		setListener();
		receiver = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ContentCommon.MAIN_KEY_MENU);
		filter.addAction(ContentCommon.MAIN_KEY_BACK);
		registerReceiver(receiver, filter);
		initExitPopupWindow_more_funtion();
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		keyguardLock = keyguardManager.newKeyguardLock("test");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		      //notificationManager.cancel(1514);
		notificationManager.cancelAll();
		super.onStop();
	}

	private void setListener() {
		layoutVidicon.setOnTouchListener(this);
		layoutAlarm.setOnTouchListener(this);
		layoutPic.setOnTouchListener(this);
		layoutVid.setOnTouchListener(this);

	}

	private void findView() {

		// TextView main_tv_vidicon, main_tv_alarm, main_tv_picture,
		// main_tv_vid;
		btn_edit = (Button) findViewById(R.id.btn_edit);
		btn_menu = (Button) findViewById(R.id.btn_menu);
		btnback=(Button) findViewById(R.id.bt_main_back);
		btn_edit.setOnClickListener(this);
		btn_menu.setOnClickListener(this);
		btnback.setOnClickListener(this);

		main_tv_vidicon = (TextView) findViewById(R.id.main_tv_vidicon);
		main_tv_alarm = (TextView) findViewById(R.id.main_tv_alarm);
		main_tv_picture = (TextView) findViewById(R.id.main_tv_picture);
		main_tv_vid = (TextView) findViewById(R.id.main_tv_vid);
		textView_title = (TextView) findViewById(R.id.textView_title);
		layoutVidicon = (LinearLayout) findViewById(R.id.main_layout_vidicon);
		layoutAlarm = (LinearLayout) findViewById(R.id.main_layout_alarm);
		layoutPic = (LinearLayout) findViewById(R.id.main_layout_pic);
		layoutVid = (LinearLayout) findViewById(R.id.main_layout_vid);
		container = (LinearLayout) findViewById(R.id.container);
		img = (FrameLayout) findViewById(R.id.img);
		
	}

	private int colorPress = 0xffffffff;
	private int colorNomal = 0xff000000;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			VibratorUtil.Vibrate(MainActivity.this, 20);
		
			int id=v.getId();
			if (id==R.id.main_layout_vidicon) {
				textView_title.setText(R.string.app_name);
				main_tv_vidicon.setTextColor(colorPress);
				btn_edit.setVisibility(View.VISIBLE);
				switch (tabPosition) {
				case 2:
					main_tv_alarm.setTextColor(colorNomal);
					break;
				case 3:
					main_tv_picture.setTextColor(colorNomal);
					break;
				case 4:
					main_tv_vid.setTextColor(colorNomal);
					break;
				default:
					break;
				}
				tabPosition = 1;
				ShowScreen(IpcamClientActivity.class);
			}else if (id==R.id.main_layout_alarm) {
				textView_title.setText(R.string.main_alarm);
				main_tv_alarm.setTextColor(colorPress);
				btn_edit.setVisibility(View.GONE);
				switch (tabPosition) {
				case 1:
					main_tv_vidicon.setTextColor(colorNomal);
					break;
				case 3:
					main_tv_picture.setTextColor(colorNomal);
					break;
				case 4:
					main_tv_vid.setTextColor(colorNomal);
					break;
				default:
					break;
				}
				tabPosition = 2;
				ShowScreen(AlarmActivity.class);
			}else if (id==R.id.main_layout_pic) {
				textView_title.setText(R.string.main_pic);
				main_tv_picture.setTextColor(colorPress);
				btn_edit.setVisibility(View.GONE);
				switch (tabPosition) {
				case 1:
					main_tv_vidicon.setTextColor(colorNomal);
					break;
				case 2:
					main_tv_alarm.setTextColor(colorNomal);
					break;
				case 4:
					main_tv_vid.setTextColor(colorNomal);
					break;
				default:
					break;
				}
				tabPosition = 3;
				ShowScreen(PictureActivity.class);
			}else if (id==R.id.main_layout_vid) {
				textView_title.setText(R.string.main_vid);
				main_tv_vid.setTextColor(colorPress);
				btn_edit.setVisibility(View.GONE);
				switch (tabPosition) {
				case 1:
					main_tv_vidicon.setTextColor(colorNomal);
					break;
				case 2:
					main_tv_alarm.setTextColor(colorNomal);
					break;
				case 3:
					main_tv_picture.setTextColor(colorNomal);
					break;
				default:
					break;
				}
				tabPosition = 4;
				ShowScreen(VideoActivity.class);
			}
			
		}
		return false;
	}

	/**
	 * 
	 * ����Activity��ӵ�ActivityGroup����
	 * **/
	private void ShowScreen(Class clzz) {
		LocalActivityManager localActivityManager = getLocalActivityManager();
		String id = clzz.getSimpleName();
		Intent intent = new Intent(this, clzz);
		Window window = localActivityManager.startActivity(id, intent);
		View view = window.getDecorView();
		container.removeAllViews();
		container.setVisibility(View.VISIBLE);
		container.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (arg1.getAction().equals(ContentCommon.MAIN_KEY_MENU)) {
				Log.e("test", "ContentCommon.MAIN_KEY_MENU");
				SystemValue.isStartRun = false;
				if (popupWindow_more_funtion != null
						&& popupWindow_more_funtion.isShowing()) {
					popupWindow_more_funtion.dismiss();
					tap = 0;
				}
				if (popupWindow_more_funtion != null) {
					popupWindow_more_funtion.showAtLocation(layoutVidicon,
							Gravity.BOTTOM, 0, 0);
				}

			} else if (arg1.getAction().equals(ContentCommon.MAIN_KEY_BACK)) {
				SystemValue.isStartRun = false;
				moveTaskToBack(true);
			}
		}

	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (keyguardLock != null) {
			keyguardLock.reenableKeyguard();
		}
		unregisterReceiver(receiver);
		//停止接收广播消息
//		Intent intent = new Intent();
//		intent.setClass(this, BridgeService.class);
//		stopService(intent);
		SystemValue.checkSDStatu = 0;
		// NotificationManager notificationManager = (NotificationManager) this
		// .getSystemService(NOTIFICATION_SERVICE);
		// notificationManager.cancel(1514);
	}

	private int tap = 0;
	private View popv_more_funtion;
	private PopupWindow popupWindow_more_funtion;
	private Button btn_more_about, btn_more_setting, btn_more_alarm,
			btn_zhuxiao, btn_exite;

	@SuppressLint("NewApi")
	public void initExitPopupWindow_more_funtion() {
		LayoutInflater li = LayoutInflater.from(this);
		popv_more_funtion = li.inflate(R.layout.popup_more_funtion, null);

		btn_more_about = (Button) popv_more_funtion
				.findViewById(R.id.btn_more_about);
		btn_more_about.setOnClickListener(this);

		btn_more_alarm = (Button) popv_more_funtion
				.findViewById(R.id.btn_more_alarm);
		btn_more_alarm.setOnClickListener(this);
		btn_more_setting = (Button) popv_more_funtion
				.findViewById(R.id.btn_more_setting);
		btn_more_setting.setOnClickListener(this);

		btn_zhuxiao = (Button) popv_more_funtion.findViewById(R.id.btn_zhuxiao);
		btn_zhuxiao.setOnClickListener(this);

		btn_exite = (Button) popv_more_funtion.findViewById(R.id.btn_exite);
		btn_exite.setOnClickListener(this);
		popupWindow_more_funtion = new PopupWindow(popv_more_funtion,
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		popupWindow_more_funtion
				.setAnimationStyle(R.style.MainAnimationPreview);
		popupWindow_more_funtion.setFocusable(true);

		popupWindow_more_funtion
				.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		popupWindow_more_funtion
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow_more_funtion.setOutsideTouchable(true);
		popupWindow_more_funtion.setBackgroundDrawable(new ColorDrawable(0));
		popv_more_funtion.setFocusableInTouchMode(true);
		popv_more_funtion.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				tap++;
				if (keyCode == KeyEvent.KEYCODE_MENU && tap == 2) {
					popupWindow_more_funtion.dismiss();
					tap = 0;
				}
				return false;
			}
		});
		popupWindow_more_funtion
				.setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						popupWindow_more_funtion.dismiss();
						tap = 0;
					}
				});
		popupWindow_more_funtion.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow_more_funtion.dismiss();
					tap = 0;
				}
				return false;
			}
		});
	}

	private boolean isEdited;

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		VibratorUtil.Vibrate(MainActivity.this, 20);
		int id=arg0.getId();
		if (id==R.id.bt_main_back) {
			finish();
		}
		else if (id==R.id.btn_edit) {
			if (mainEditInterface != null) {
				isEdited = mainEditInterface.EditOnclick(btn_edit, isEdited);
			}
			Log.d("mainactivity", "isEdited:" + isEdited);
		}else if (id==R.id.btn_menu) {
			SystemValue.isStartRun = false;
			if (popupWindow_more_funtion != null
					&& popupWindow_more_funtion.isShowing()) {
				popupWindow_more_funtion.dismiss();
				tap = 0;
			}
			if (popupWindow_more_funtion != null) {
				popupWindow_more_funtion.showAtLocation(layoutVidicon,
						Gravity.BOTTOM, 0, 0);
			}
		}else if (id==R.id.btn_more_about) {
			Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
			startActivity(intent);
			popupWindow_more_funtion.dismiss();
		}else if (id==R.id.btn_more_alarm) {
			if (bFolder(strAlarmFolder)) {
				Log.i("iBtnRingtone", "�����������������ť");
				// ����������
				Intent intent = new Intent(
						RingtoneManager.ACTION_RINGTONE_PICKER);
				// ����Ϊ��������
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
						RingtoneManager.TYPE_ALARM);
				// ������ʾ��title
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "���ñ�������");
				// ��������ɺ󷵻ص���ǰ��Activity
				startActivityForResult(intent, 1);
			}
			popupWindow_more_funtion.dismiss();
		}else if(id==R.id.btn_more_setting){
			if (bFolder(strRingtoneFolder)) {
				Log.i("iBtnRingtone", "�����������������ť");
				// ����������
				Intent intent = new Intent(
						RingtoneManager.ACTION_RINGTONE_PICKER);
				// ����Ϊ����Ringtone
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
						RingtoneManager.TYPE_RINGTONE);
				// ������ʾ��title
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "������������");
				// ��������ɺ󷵻ص���ǰ��Activity
				startActivityForResult(intent, 0);
			}
			popupWindow_more_funtion.dismiss();
		}else if (id==R.id.btn_zhuxiao) {
			finish();
			popupWindow_more_funtion.dismiss();
		}else if (id==R.id.btn_exite) {
			moveTaskToBack(true);
			popupWindow_more_funtion.dismiss();
		}
		
	}

	// ����������֮�� �Ļص�����
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		Log.e("test", "�����");
		switch (requestCode) {
		case 0:
			try {
				// �õ�ѡ�������
				Uri pickedUri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				// ��ѡ�����������ΪĬ��ֵ
				if (pickedUri != null) {
					// RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this,
					// RingtoneManager.TYPE_RINGTONE, pickedUri);
					String sounduri = getRealPathFromURI(pickedUri);
					Log.d("tets", "sounduri:" + sounduri);
					if (sounduri != null) {
						SharedPreferences.Editor editor = preAudio.edit();
						editor.putString("doorbell_audio", sounduri);
						editor.commit();
					}
				} else {
					SharedPreferences.Editor editor = preAudio.edit();
					editor.putString("doorbell_audio", "no");
					editor.commit();
					Toast.makeText(getApplicationContext(),
							"��ѡ���˾������������������Ϊ������", 1).show();
				}
			} catch (Exception ex) {
				SharedPreferences.Editor editor = preAudio.edit();
				editor.putString("doorbell_audio", "default");
				editor.commit();
				Toast.makeText(getApplicationContext(),
						"��ѡ����Ĭ�������������������Ϊ�����е�Ĭ������", 1).show();
			}

			break;

		case 1:
			try {
				// �õ�ѡ�������
				Uri pickedUri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				// ��ѡ�����������ΪĬ��ֵ
				if (pickedUri != null) {
					// RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this,
					// RingtoneManager.TYPE_RINGTONE, pickedUri);
					String sounduri = getRealPathFromURI(pickedUri);
					Log.d("tets", "sounduri:" + sounduri);
					if (sounduri != null) {
						SharedPreferences.Editor editor = preAudio.edit();
						editor.putString("doorbell_audio_alarm", sounduri);
						editor.commit();
					}
				} else {
					SharedPreferences.Editor editor = preAudio.edit();
					editor.putString("doorbell_audio_alarm", "no_alarm");
					editor.commit();
					Toast.makeText(getApplicationContext(), "��ѡ���˾���������������Ϊ������",
							1).show();
				}
			} catch (Exception ex) {
				SharedPreferences.Editor editor = preAudio.edit();
				editor.putString("doorbell_audio_alarm", "default_alarm");
				editor.commit();
				Toast.makeText(getApplicationContext(),
						"��ѡ����Ĭ��������������Ϊ�����е�Ĭ������", 1).show();
			}

			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean bFolder(String strFolder) {
		boolean btmp = false;
		File f = new File(strFolder);
		if (!f.exists()) {
			if (f.mkdirs()) {
				btmp = true;
			} else {
				btmp = false;
			}
		} else {
			btmp = true;
		}
		f = null;
		return btmp;
	}

	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private static MainEditInterface mainEditInterface;

	public static void setMainEditInterface(MainEditInterface snape) {
		mainEditInterface = snape;
	}

	public interface MainEditInterface {
		boolean EditOnclick(Button edit, boolean isEdited);
	}

}
