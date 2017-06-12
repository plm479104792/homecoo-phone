package com.homecoolink.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.CallActivity;
import com.homecoolink.P2PConnect;
import com.homecoolink.R;
import com.homecoolink.data.AlarmMask;
import com.homecoolink.data.AlarmRecord;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.data.SharedPreferencesManager;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.global.Constants.AlarmType_Pxy;
import com.homecoolink.utils.ImageUtils;
import com.homecoolink.utils.MusicManger;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;
import com.homecoolink.widget.XRTextView;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;


public class AlarmActivity extends Activity implements OnClickListener {

	Context mContext;
	ImageView monitor_btn, ignore_btn, alarm_defence_img;// ,shield_btn
	TextView alarm_type_text;
	ImageView alarm_img;
	int alarm_id, alarm_type, group, item;
	RelativeLayout alarm_defence_btn = null;
	boolean isSupport;
	ProgressBar progress_defence = null;
	LinearLayout layout_area_chanel;
	TextView area_text, chanel_text;
	LinearLayout alarm_input, alarm_dialog;
	TextView alarm_go;
	EditText mPassword;
	boolean isAlarm;
	boolean hasContact = false;
	NormalDialog dialog;
	Contact contact;
	// TextView tv_alarm_defence,tv_alarm_channel;//tv_number,
	// LinearLayout L_alarm_defence,L_alarm_channel;
	TextView alarmTime;
	XRTextView alarm_id_text;
	boolean isRegFilter = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		alarm_type = getIntent().getIntExtra("alarm_type", 0);
		if (P2PConnect.isPlaying()) {
			if (alarm_type != P2PValue.AlarmType.DEFENCE
					&& alarm_type != P2PValue.AlarmType.NO_DEFENCE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
			}
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		final Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		mContext = this;

		alarm_id = getIntent().getIntExtra("alarm_id", 0);

		isSupport = getIntent().getBooleanExtra("isSupport", false);

		group = getIntent().getIntExtra("group", 0);
		item = getIntent().getIntExtra("item", 0);

	

		if (alarm_type == P2PValue.AlarmType.DEFENCE
				|| alarm_type == P2PValue.AlarmType.NO_DEFENCE
				|| alarm_type == AlarmType_Pxy.LOW_POWER_PROBER_ALARM
				|| alarm_type == AlarmType_Pxy.LOW_POWER_RECOVERY_PROBER_ALARM
				|| alarm_type == AlarmType_Pxy.POWERONPROBER_ALARM
				|| alarm_type == AlarmType_Pxy.POWEROFF_PROBER_ALARM) {
			// NormalDialog nd = new NormalDialog(mContext);
			// nd.showAlarmTwoType(AlarmActivity.this);
			AlarmRecord ar = insertAlarmRecord();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(Long.valueOf(ar.alarmTime));
			String time = sdf.format(date);
			String eve = "";
			switch (ar.alarmType) {
			case P2PValue.AlarmType.DEFENCE:
				eve = getResources().getString(R.string.defence2);
				break;
			case P2PValue.AlarmType.NO_DEFENCE:
				eve = getResources().getString(R.string.no_defence);
				break;
			case AlarmType_Pxy.LOW_POWER_PROBER_ALARM:
				eve = getResources().getString(R.string.allarm_type26);
				break;
			case AlarmType_Pxy.LOW_POWER_RECOVERY_PROBER_ALARM:
				eve = getResources().getString(R.string.allarm_type27);
				break;
			case AlarmType_Pxy.POWERONPROBER_ALARM:
				eve = getResources().getString(R.string.allarm_type28);
				break;
			case AlarmType_Pxy.POWEROFF_PROBER_ALARM:
				eve = getResources().getString(R.string.allarm_type29);
				break;
			}
			if (isSupport) {
				MyApp.app.showAlarmNotification(eve, ar.deviceId, time,
						Utils.getDefenceAreaByGroup(mContext, group));
			} else {
				MyApp.app.showAlarmNotification(eve, ar.deviceId, time, null);
			}
			exit();
		} else {

			switch (alarm_type) {
			case P2PValue.AlarmType.LOW_VOL_ALARM: 
			case P2PValue.AlarmType.EXT_LINE_ALARM:
			case P2PValue.AlarmType.BATTERY_LOW_ALARM: 
			case P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH: 
			case P2PValue.AlarmType.RECORD_FAILED_ALARM:
			case AlarmType_Pxy.PARAM_ID_UPDATE_TO_SER: 
			case AlarmType_Pxy.TH_ALARM:
			case AlarmType_Pxy.EMAIL_TOO_OFTEN_ALARM:
			case AlarmType_Pxy.COMMUNICATION_TIMING_PROBER_ALARM:
				// case AlarmType_Pxy.LOW_POWER_PROBER_ALARM:
				// case AlarmType_Pxy.LOW_POWER_RECOVERY_PROBER_ALARM:
				// case AlarmType_Pxy.POWERONPROBER_ALARM: 
				// case AlarmType_Pxy.POWEROFF_PROBER_ALARM: 
				
				exit();
				finish();
				break;
			default:
				

				setContentView(R.layout.activity_alarmcj);
				initComponent();
				loadMusicAndVibrate();
				insertAlarmRecord();
				/*if (MyApp.RecordControlType == -100) {

				} else if (MyApp.RecordControlType == Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_ALARM
						|| MyApp.RecordControlType == Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_TIMER) {

				} else if (MyApp.RecordControlType == Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_MANUAL) {
					P2PHandler
							.getInstance()
							.setRemoteRecord(
									contact.contactId,
									contact.contactPassword,
									Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_OFF);
				}*/

				regFilter();
				break;
			}

		}
	}

	public void regFilter() {
		if (!isRegFilter) {
			isRegFilter = true;
			IntentFilter filter = new IntentFilter();

			filter.addAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
			mContext.registerReceiver(mReceiver, filter);
			isRegFilter = true;
		}

	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.P2P.RET_GET_REMOTE_DEFENCE)) {
				int state = intent.getIntExtra("state", -1);
				String contactId = intent.getStringExtra("contactId");
				if (contactId.equals(contact.contactId)) {

					if (state == Constants.DefenceState.DEFENCE_STATE_WARNING_NET) {
						if (null != contact && contact.isClickGetDefenceState) {
							T.showShort(mContext, R.string.net_error);
						}
					} else if (state == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
						if (null != contact && contact.isClickGetDefenceState) {
							T.showShort(mContext, R.string.password_error);
						}
					}else if (intent.getAction().equals(  //20161013
    						Constants.P2P.RET_GET_REMOTE_DEFENCE)) {
					    contact = FList.getInstance().isContact(contactId);
				}
				Log.i("343", "AlarmActivity刷新布防状态"+state);

					getdefensestate();
				}

			}
		}
	};

	public AlarmRecord insertAlarmRecord() {
		AlarmRecord alarmRecord = new AlarmRecord();
		alarmRecord.alarmTime = String.valueOf(System.currentTimeMillis());
		alarmRecord.deviceId = String.valueOf(alarm_id);
		alarmRecord.alarmType = alarm_type;
		alarmRecord.activeUser = NpcCommon.mThreeNum;
		if ((alarm_type == P2PValue.AlarmType.EXTERNAL_ALARM || alarm_type == P2PValue.AlarmType.LOW_VOL_ALARM)
				&& isSupport) {
			alarmRecord.group = group;
			
			alarmRecord.item = item;
		} else {
			
			alarmRecord.group = -1;
			alarmRecord.item = -1;
		}
		// DataManager.insertAlarmRecord(mContext, alarmRecord);
		MyApp.GetRemote(
				alarmRecord.deviceId,
				FList.getInstance().isContact(alarmRecord.deviceId).contactPassword);
		Intent i = new Intent();
		i.setAction(Constants.Action.REFRESH_ALARM_RECORD);
		mContext.sendBroadcast(i);
		return alarmRecord;
	}

	public void loadMusicAndVibrate() {
		isAlarm = true;
		int a_muteState = SharedPreferencesManager.getInstance().getAMuteState(
				MyApp.app);
//		if (a_muteState == 1) {
			MusicManger.getInstance().playAlarmMusic();
//		}

		int a_vibrateState = SharedPreferencesManager.getInstance()
				.getAVibrateState(MyApp.app);
		if (a_vibrateState == 1) {
			new Thread() {
				@Override
				public void run() {
					while (isAlarm) {
						MusicManger.getInstance().Vibrate();
						Utils.sleepThread(100);
					}
					MusicManger.getInstance().stopVibrate();

				}
			}.start();
		}
	}

	public void loadAlarmBellMusicAndVibrate() {
		isAlarm = true;
		int a_muteState = SharedPreferencesManager.getInstance().getAMuteState(
				MyApp.app);
//		if (a_muteState == 1) {
			MusicManger.getInstance().playAlarmBellMusic();
//		}

		int a_vibrateState = SharedPreferencesManager.getInstance()
				.getAVibrateState(MyApp.app);
		if (a_vibrateState == 1) {
			new Thread() {
				@Override
				public void run() {
					while (isAlarm) {
						MusicManger.getInstance().Vibrate();
						Utils.sleepThread(100);
					}
					MusicManger.getInstance().stopVibrate();

				}
			}.start();
		}
	}

	public void updateImage(String threeNum, boolean isGray, ImageView imgv) {
		Bitmap tempBitmap;
		try {

			tempBitmap = ImageUtils.getBitmap(new File(
					"/sdcard/screenshot/tempHead/" + NpcCommon.mThreeNum + "/"
							+ threeNum + ".jpg"), 200, 200);

			// tempBitmap = ImageUtils.roundCorners(tempBitmap,
			// ImageUtils.getScaleRounded(tempBitmap.getWidth()));
			imgv.setImageBitmap(tempBitmap);
		} catch (Exception e) {

			tempBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.contact_list_defaultpic);
			// tempBitmap = ImageUtils.roundCorners(tempBitmap,
			// ImageUtils.getScaleRounded(tempBitmap.getWidth()));
			imgv.setImageBitmap(tempBitmap);

		}
	}

	public void initComponent() {
		
		monitor_btn = (ImageView) findViewById(R.id.alarm_check_btn);
		ignore_btn = (ImageView) findViewById(R.id.alarm_ignore_btn);
		alarm_defence_img = (ImageView) findViewById(R.id.alarm_defence_btn);
		alarm_defence_btn = (RelativeLayout) findViewById(R.id.defense);
		// shield_btn = (Button) findViewById(R.id.bt_block);
		alarm_id_text = (XRTextView) findViewById(R.id.alarm_device_text);
		alarm_type_text = (TextView) findViewById(R.id.alarm_type_text);
		progress_defence = (ProgressBar) findViewById(R.id.progress_defence);
		// tv_number=(TextView)findViewById(R.id.tv_number);
		// tv_alarm_defence=(TextView)findViewById(R.id.tv_alarm_defence);
		// tv_alarm_channel=(TextView)findViewById(R.id.tv_alarm_channel);
		// L_alarm_defence=(LinearLayout)findViewById(R.id.L_alarm_defence);
		// L_alarm_channel=(LinearLayout)findViewById(R.id.L_alarm_channel);
		alarmTime = (TextView) findViewById(R.id.alarm_time_text);
		// alarm_go = (TextView) findViewById(R.id.alarm_go);
		// alarm_go.setOnTouchListener(new OnTouchListener(){
		//
		// @Override
		// public boolean onTouch(View arg0, MotionEvent event) {
		//
		// switch(event.getAction()){
		// case MotionEvent.ACTION_DOWN:
		// alarm_go.setTextColor(mContext.getResources().getColor(R.color.text_color_white));
		// break;
		// case MotionEvent.ACTION_UP:
		// alarm_go.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
		// break;
		// }
		// return false;
		// }
		//
		// });
		// alarm_input = (LinearLayout) findViewById(R.id.alarm_input);
		alarm_img = (ImageView) findViewById(R.id.alarm_img);
		// mPassword = (EditText) findViewById(R.id.password);
		// mPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
		// mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
		// final AnimationDrawable anim = (AnimationDrawable)
		// alarm_img.getDrawable();
		// OnPreDrawListener opdl = new OnPreDrawListener(){
		// @Override
		// public boolean onPreDraw() {
		// anim.start();
		// return true;
		// }
		//
		// };
		// alarm_img.getViewTreeObserver().addOnPreDrawListener(opdl);
		// alarm_dialog = (LinearLayout) findViewById(R.id.alarm_dialog);
		// alarm_dialog.startAnimation(AnimationUtils.loadAnimation(mContext,
		// R.anim.slide_in_right));
		contact = FList.getInstance().isContact(String.valueOf(alarm_id));
		alarm_id_text.setText(contact.contactName + "(" + alarm_id + ")");
		updateImage(contact.contactId, false, alarm_img);
		// layout_area_chanel = (LinearLayout)
		// findViewById(R.id.layout_area_chanel);
		area_text = (TextView) findViewById(R.id.alarm_area_text);
		chanel_text = (TextView) findViewById(R.id.alarm_channel_text);
		if (isSupport) {
			chanel_text.setVisibility(View.GONE);
			// area_text.setText(
			// mContext.getResources().getString(R.string.area)+
			// ":"+Utils.getDefenceAreaByGroup(mContext, group));
			// chanel_text.setText(
			// mContext.getResources().getString(R.string.channel)+
			// ":"+(item+1));
			// L_alarm_channel.setVisibility(LinearLayout.VISIBLE);
			// L_alarm_defence.setVisibility(LinearLayout.VISIBLE);
			// tv_alarm_defence.setText(Utils.getDefenceAreaByGroup(mContext,
			// group));
			// tv_alarm_channel.setText(String.valueOf(item+1));

			area_text.setVisibility(View.VISIBLE);
			area_text.setText(Utils.getDefenceAreaByGroup(mContext, group));
			Log.e("343", Utils.getDefenceAreaByGroup(mContext, 0));
			Log.e("343", Utils.getDefenceAreaByGroup(mContext, 1));
			Log.e("343", Utils.getDefenceAreaByGroup(mContext, 2));
			Log.e("343", Utils.getDefenceAreaByGroup(mContext, 3));
			Log.e("343", Utils.getDefenceAreaByGroup(mContext, 4));
			Log.e("343", Utils.getDefenceAreaByGroup(mContext, 5));
			Log.e("343", Utils.getDefenceAreaByGroup(mContext, 6));
			Log.e("343", Utils.getDefenceAreaByGroup(mContext, 7));
			Log.e("343", Utils.getDefenceAreaByGroup(mContext, 8));

			// chanel_text.setVisibility(View.VISIBLE);
			// List<DefenceAreaName> list = DataManager
			// .findDefenceAreaNameAll(this);
			
			// // for (int i = 0; i < list.size(); i++) {
			// // Log.e("343",
			// //
			// i+"=="+list.get(i).groupName+"=="+list.get(i).groupIJ+"=="+list.get(i).groupI+"=="+list.get(i).groupJ);
			// //
			// // }
			//
			// if (list.size() > 0) {
			//
			// for (int i = 0; i < list.size(); i++) {
			// // Log.e("343", "group"+group+"=="+"item"+item);
			// DefenceAreaName dfaName = list.get(i);
			// // Log.e("343",
			// //
			// "dfaName.groupI="+dfaName.groupI+"=="+"dfaName.groupJ="+dfaName.groupJ+"=="+"groupName="+dfaName.groupName);
			// if (dfaName.groupI.equals("" + group)
			// && dfaName.groupJ.equals("" + item)) {
			//
			
			// if (dfaName.groupName.equals("")
			// || dfaName.groupName == null) {
			// chanel_text.setText(Utils
			// .getDefenceAreaByGroup(mContext, group)
			// + "(" + String.valueOf(item + 1) + ")");
			// } else {
			// chanel_text.setText(dfaName.groupName + "("
			// + String.valueOf(item + 1) + ")");
			// }
			//
			// break;
			// }
			// }
			// } else {
			// chanel_text.setText(Utils.getDefenceAreaByGroup(mContext,
			// group) + "(" + String.valueOf(item + 1) + ")");
			// }

		}

		switch (alarm_type) {
		case P2PValue.AlarmType.EXTERNAL_ALARM: 
			alarm_type_text.setText(R.string.allarm_type1);

			break;
		case P2PValue.AlarmType.MOTION_DECT_ALARM:
			alarm_type_text.setText(R.string.allarm_type2);
			// tv_number.setText("00");
			break;
		case P2PValue.AlarmType.EMERGENCY_ALARM:
			alarm_type_text.setText(R.string.allarm_type3);
			break;

		// case P2PValue.AlarmType.LOW_VOL_ALARM:
		// alarm_type_text.setText(R.string.low_voltage_alarm);
		// if (isSupport) {
		// layout_area_chanel.setVisibility(RelativeLayout.VISIBLE);
		// area_text.setText(
		// mContext.getResources().getString(R.string.area)+
		// ":"+Utils.getDefenceAreaByGroup(mContext, group));
		// chanel_text.setText(
		// mContext.getResources().getString(R.string.channel)+
		// ":"+(item+1));
		// L_alarm_channel.setVisibility(LinearLayout.VISIBLE);
		// L_alarm_defence.setVisibility(LinearLayout.VISIBLE);
		// tv_alarm_defence.setText(Utils.getDefenceAreaByGroup(mContext,
		// group));
		// tv_alarm_channel.setText(String.valueOf(item+1));
		// }
		// break;
		case P2PValue.AlarmType.PIR_ALARM:
			alarm_type_text.setText(R.string.allarm_type7);
			// tv_number.setText("01");
			break;
		// case P2PValue.AlarmType.EXT_LINE_ALARM:
		// alarm_type_text.setText(R.string.allarm_type5);
		// break;
		case P2PValue.AlarmType.DEFENCE:
			alarm_type_text.setText(R.string.defence2);
			break;
		case P2PValue.AlarmType.NO_DEFENCE:
			alarm_type_text.setText(R.string.no_defence);
			break;
		
		// case P2PValue.AlarmType.BATTERY_LOW_ALARM:
		// alarm_type_text.setText(R.string.allarm_type10);
		// break;
		// case P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH:
		// alarm_type_text.setText(R.string.allarm_type13);
		// break;
		// case P2PValue.AlarmType.RECORD_FAILED_ALARM:
		// alarm_type_text.setText(R.string.allarm_type15);
		// break;
		
		case AlarmType_Pxy.DEBUG_ALARM:
			alarm_type_text.setText(R.string.allarm_type4);
			break;
		// case AlarmType_Pxy.PARAM_ID_UPDATE_TO_SER:
		// alarm_type_text.setText(R.string.allarm_type11);
		// break;
		// case AlarmType_Pxy.TH_ALARM:
		// alarm_type_text.setText(R.string.allarm_type12);
		// break;
		case AlarmType_Pxy.FORCE_FROM_KEYPRESS_ALARM:
			alarm_type_text.setText(R.string.allarm_type14);
			break;
		// case AlarmType_Pxy.EMAIL_TOO_OFTEN_ALARM:
		// alarm_type_text.setText(R.string.allarm_type16);
		// break;
		case AlarmType_Pxy.UART_INPUT_ALARM:
			alarm_type_text.setText(R.string.allarm_type17);
			break;
		case AlarmType_Pxy.FIRE_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type18);
			break;
		case AlarmType_Pxy.GAS_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type19);
			break;
		case AlarmType_Pxy.STEAL_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type20);
			break;
		case AlarmType_Pxy.AROUND_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type21);
			break;
		case AlarmType_Pxy.FORCE_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type22);
			break;
		case AlarmType_Pxy.I20_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type23);
			break;
		case AlarmType_Pxy.PREVENTDISCONNECT_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type24);
			break;
		// case AlarmType_Pxy.COMMUNICATION_TIMING_PROBER_ALARM:
		// alarm_type_text.setText(R.string.allarm_type25);
		// break;
		// case AlarmType_Pxy.LOW_POWER_PROBER_ALARM:
		// alarm_type_text.setText(R.string.allarm_type26);
		// break;
		// case AlarmType_Pxy.LOW_POWER_RECOVERY_PROBER_ALARM:
		// alarm_type_text.setText(R.string.allarm_type27);
		// break;
		// case AlarmType_Pxy.POWERONPROBER_ALARM:
		// alarm_type_text.setText(R.string.allarm_type28);
		// break;
		// case AlarmType_Pxy.POWEROFF_PROBER_ALARM:
		// alarm_type_text.setText(R.string.allarm_type29);
		// break;
		case AlarmType_Pxy.DEF_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type30);
			break;
		case AlarmType_Pxy.DEFDIS_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type31);
			break;
		case AlarmType_Pxy.EXT_PROBER_ALARM:
			alarm_type_text.setText(R.string.allarm_type32);
			break;
		default:
			// try {
			alarm_type_text.setText(R.string.allarm_nofound + alarm_type);
			// } catch (Exception e) {
			// // TODO: handle exception
			// Log.e("343", Log.getStackTraceString(e));
			// }
			break;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String time = sdf.format(date);
		alarmTime.setText(time);
		contact.defenceState=Constants.DefenceState.DEFENCE_STATE_ON;
		getdefensestate();

		// alarm_go.setOnClickListener(this);
		monitor_btn.setOnClickListener(this);
		ignore_btn.setOnClickListener(this);
		alarm_defence_btn.setOnClickListener(this);
		// shield_btn.setOnClickListener(this);
	}

	private void getdefensestate() {
		if (alarm_defence_img != null && progress_defence != null) {
			if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_LOADING) {
				progress_defence.setVisibility(View.VISIBLE);
				alarm_defence_img.setVisibility(View.GONE);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
				progress_defence.setVisibility(View.GONE);
				alarm_defence_img.setVisibility(View.VISIBLE);
				alarm_defence_img.setImageResource(R.drawable.alarm_defence_on);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
				progress_defence.setVisibility(View.GONE);
				alarm_defence_img.setVisibility(View.VISIBLE);
				alarm_defence_img
						.setImageResource(R.drawable.alarm_defence_off);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET) {
				progress_defence.setVisibility(View.GONE);
				alarm_defence_img.setVisibility(View.VISIBLE);
				alarm_defence_img
						.setImageResource(R.drawable.ic_defence_warning);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
				progress_defence.setVisibility(View.GONE);
				alarm_defence_img.setVisibility(View.VISIBLE);
				alarm_defence_img
						.setImageResource(R.drawable.ic_defence_warning);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_NO_PERMISSION) {
				progress_defence.setVisibility(View.GONE);
				alarm_defence_img.setVisibility(View.VISIBLE);
				alarm_defence_img.setImageResource(R.drawable.limit);
			}
		}
	}

	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			finish();
			String[] data = (String[]) msg.obj;
			Intent monitor = new Intent();
			monitor.setClass(mContext, CallActivity.class);
			monitor.putExtra("callId", data[0]);
			monitor.putExtra("password", data[1]);
			monitor.putExtra("isOutCall", true);
			monitor.putExtra("type", Constants.P2P_TYPE.P2P_TYPE_MONITOR);
			startActivity(monitor);
			return false;
		}
	});

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.alarm_ignore_btn){
			ignore();
		}else if(id==R.id.alarm_check_btn){
			if (null != contact) {
				ignore();
				hasContact = true;
				P2PConnect.vReject("");
				new Thread() {
					@Override
					public void run() {
						while (true) {
							if (P2PConnect.getCurrent_state() == P2PConnect.P2P_STATE_NONE) {
								Message msg = new Message();
								String[] data = new String[] {
										contact.contactId,
										contact.contactPassword };
								msg.obj = data;
								handler.sendMessage(msg);
								break;
							}
							Utils.sleepThread(500);
						}
					}
				}.start();
			}
		}else if(id==R.id.defense){
			if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET
					|| contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
				progress_defence.setVisibility(View.VISIBLE);
				alarm_defence_img.setVisibility(View.GONE);
				P2PHandler.getInstance().getDefenceStates(contact.contactId,
						contact.contactPassword);
				FList.getInstance().setIsClickGetDefenceState(
						contact.contactId, true);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
				progress_defence.setVisibility(View.VISIBLE);
				alarm_defence_img.setVisibility(View.GONE);
				P2PHandler.getInstance().setRemoteDefence(contact.contactId,
						contact.contactPassword,
						Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
				FList.getInstance().setIsClickGetDefenceState(
						contact.contactId, true);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
				progress_defence.setVisibility(View.VISIBLE);
				alarm_defence_img.setVisibility(View.GONE);
				P2PHandler.getInstance().setRemoteDefence(contact.contactId,
						contact.contactPassword,
						Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
				FList.getInstance().setIsClickGetDefenceState(
						contact.contactId, true);
			}
		}
		

//		switch (v.getId()) {
//		case R.id.alarm_ignore_btn:
//			ignore();
//			break;
//		case R.id.alarm_check_btn:
//
//			if (null != contact) {
//				ignore();
//				hasContact = true;
//				P2PConnect.vReject("");
//				new Thread() {
//					@Override
//					public void run() {
//						while (true) {
//							if (P2PConnect.getCurrent_state() == P2PConnect.P2P_STATE_NONE) {
//								Message msg = new Message();
//								String[] data = new String[] {
//										contact.contactId,
//										contact.contactPassword };
//								msg.obj = data;
//								handler.sendMessage(msg);
//								break;
//							}
//							Utils.sleepThread(500);
//						}
//					}
//				}.start();
//			}
//
//			if (!hasContact) {
//				// if(alarm_input.getVisibility()==RelativeLayout.VISIBLE){
//				// return;
//				// }
//				//
//				// alarm_input.setVisibility(RelativeLayout.VISIBLE);
//				// alarm_input.requestFocus();
//				// Animation anim = AnimationUtils.loadAnimation(mContext,
//				// R.anim.slide_in_right);
//				// anim.setAnimationListener(new AnimationListener(){
//				//
//				// @Override
//				// public void onAnimationEnd(Animation arg0) {
//				//
//				// InputMethodManager m = (InputMethodManager)
//				// alarm_input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//				// m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//				//
//				// }
//				//
//				// @Override
//				// public void onAnimationRepeat(Animation arg0) {
//				//
//				//
//				// }
//				//
//				// @Override
//				// public void onAnimationStart(Animation arg0) {
//				//
//				//
//				// }
//				//
//				// });
//				// alarm_input.startAnimation(anim);
//			}
//			break;
//		// case R.id.alarm_go:
//		// final String password = mPassword.getText().toString();
//		// if(password.trim().equals("")){
//		// T.showShort(mContext, R.string.input_monitor_pwd);
//		// return;
//		// }
//		//
//		// if(password.length()>9){
//		// T.showShort(mContext, R.string.password_length_error);
//		// return;
//		// }
//		//
//		// P2PConnect.vReject("");
//		//
//		// new Thread(){
//		// public void run(){
//		// while(true){
//		// if(P2PConnect.getCurrent_state()==P2PConnect.P2P_STATE_NONE){
//		// Message msg = new Message();
//		// String[] data = new String[]{String.valueOf(alarm_id),password};
//		// msg.obj = data;
//		// handler.sendMessage(msg);
//		// break;
//		// }
//		// Utils.sleepThread(500);
//		// }
//		// }
//		// }.start();
//		// break;
//			//这段代码是屏蔽设备用的 可以删掉
////		case R.id.bt_block:
////			dialog = new NormalDialog(mContext, mContext.getResources()
////					.getString(R.string.shielded), mContext.getResources()
////					.getString(R.string.shielded_alarm_promp), mContext
////					.getResources().getString(R.string.ensure), mContext
////					.getResources().getString(R.string.cancel));
////			dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
////
////				@Override
////				public void onClick() {
////
////					List<AlarmMask> alarmMasks = DataManager
////							.findAlarmMaskByActiveUser(mContext,
////									NpcCommon.mThreeNum);
////
////					boolean isExist = false;
////					for (AlarmMask alarmMask : alarmMasks) {
////						if (String.valueOf(alarm_id).equals(alarmMask.deviceId)) {
////							isExist = true;
////							break;
////						}
////					}
////
////					if (!isExist) {
////						Contact saveContact = new Contact();
////						saveContact.contactId = String.valueOf(alarm_id);
////						saveContact.activeUser = NpcCommon.mThreeNum;
////
////						AlarmMask alarmMask = new AlarmMask();
////						alarmMask.deviceId = String.valueOf(alarm_id);
////						alarmMask.activeUser = NpcCommon.mThreeNum;
////						DataManager.insertAlarmMask(mContext, alarmMask);
////
////						Intent add_success = new Intent();
////						add_success
////								.setAction(Constants.Action.ADD_ALARM_MASK_ID_SUCCESS);
////						add_success.putExtra("alarmMask", alarmMask);
////						mContext.sendBroadcast(add_success);
////					}
////					finish();
////				}
////			});
////			dialog.showDialog();
////			break;
////			
//			
//		case R.id.defense:
//
//			if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET
//					|| contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
//				progress_defence.setVisibility(View.VISIBLE);
//				alarm_defence_img.setVisibility(View.GONE);
//				P2PHandler.getInstance().getDefenceStates(contact.contactId,
//						contact.contactPassword);
//				FList.getInstance().setIsClickGetDefenceState(
//						contact.contactId, true);
//			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
//				progress_defence.setVisibility(View.VISIBLE);
//				alarm_defence_img.setVisibility(View.GONE);
//				P2PHandler.getInstance().setRemoteDefence(contact.contactId,
//						contact.contactPassword,
//						Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
//				FList.getInstance().setIsClickGetDefenceState(
//						contact.contactId, true);
//			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
//				progress_defence.setVisibility(View.VISIBLE);
//				alarm_defence_img.setVisibility(View.GONE);
//				P2PHandler.getInstance().setRemoteDefence(contact.contactId,
//						contact.contactPassword,
//						Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
//				FList.getInstance().setIsClickGetDefenceState(
//						contact.contactId, true);
//			}
//			break;
//		}
	}

	public void ignore() {
		FList flist = FList.getInstance();
		flist.updateOnlineState();
		int timeInterval = SharedPreferencesManager.getInstance()
				.getAlarmTimeInterval(mContext);
		SharedPreferencesManager.getInstance().putIgnoreAlarmTime(mContext,
				System.currentTimeMillis());
		T.showShort(
				mContext,
				mContext.getResources().getString(
						R.string.ignore_alarm_prompt_start)
						+ " "
						+ timeInterval
						+ " "
						+ mContext.getResources().getString(
								R.string.ignore_alarm_prompt_end));
		finish();
	}

	public void exit() {
		isAlarm = false;
		P2PConnect.vEndAllarm();
		FList flist = FList.getInstance();
		flist.updateOnlineState();
		finish();

	}

	@Override
	protected void onStop() {

		super.onStop();
		if (isRegFilter) {
			isRegFilter = false;
			mContext.unregisterReceiver(mReceiver);
		}

		MusicManger.getInstance().stop();
		isAlarm = false;
		P2PConnect.vEndAllarm();

	}

}
