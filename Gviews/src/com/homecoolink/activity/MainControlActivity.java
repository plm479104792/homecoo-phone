package com.homecoolink.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.data.Contact;
import com.homecoolink.fragment.AlarmControlFrag;
import com.homecoolink.fragment.DefenceAreaControlFrag;
import com.homecoolink.fragment.MainControlFrag;
import com.homecoolink.fragment.NetControlFrag;
import com.homecoolink.fragment.RecordControlFrag;
import com.homecoolink.fragment.SecurityControlFrag;
import com.homecoolink.fragment.TimeControlFrag;
import com.homecoolink.fragment.VideoControlFrag;
import com.homecoolink.global.Constants;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.HeaderView;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;



public class MainControlActivity extends BaseActivity implements
		OnClickListener {

	public static final int FRAG_MAIN = 0;
	public static final int FRAG_TIME_CONTROL = 1;
	public static final int FRAG_REMOTE_CONTROL = 2;
	public static final int FRAG_ALARM_CONTROL = 5;
	public static final int FRAG_VIDEO_CONTROL = 6;
	public static final int FRAG_RECORD_CONTROL = 7;
	public static final int FRAG_SECURITY_CONTROL = 8;
	public static final int FRAG_NET_CONTROL = 9;
	public static final int FRAG_DEFENCE_AREA_CONTROL = 10;

	private ImageView back;
	private TextView contactName;
	private NormalDialog dialog;
	boolean isCancelCheck = false;
	boolean isCancelDoUpdate = false;
	public int current_frag = -1;

	private String[] fragTags = new String[] { "mainFrag", "timeFrag",
			"remoteFrag", "loadFrag", "faultFrag", "alarmFrag", "videoFrag",
			"recordFrag", "securityFrag", "netFrag", "defenceAreaFrag" };
	public static Context mContext;
	boolean isRegFilter = false;
	private Contact contact;
	private int device_type;

	MainControlFrag mainFrag; 

	TimeControlFrag timeFrag; 
	AlarmControlFrag alarmFrag;
	VideoControlFrag videoFrag;
	RecordControlFrag recordFrag; 
	SecurityControlFrag securityFrag;
	NetControlFrag netFrag;
	DefenceAreaControlFrag defenceAreaFrag;

	HeaderView header_img;
	Button viewDeviceVersionBtn;
	TextView tv_setting;
	TextView tv_divnum;
	ImageView account_img;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_control_main);
		contact = (Contact) getIntent().getSerializableExtra("contact");
		device_type = getIntent().getIntExtra("type", -1);
		mContext = this;

		initComponent();
		regFilter();

		replaceFragment(FRAG_MAIN, false, true);
	}

	public void initComponent() {
		tv_setting = (TextView) findViewById(R.id.tv_setting);
		tv_divnum = (TextView) findViewById(R.id.divnum);
		account_img = (ImageView) findViewById(R.id.editaccount);
		viewDeviceVersionBtn = (Button) findViewById(R.id.viewDeviceVersionBtn);
		contactName = (TextView) findViewById(R.id.contactName);
		header_img = (HeaderView) findViewById(R.id.header_img);
		header_img.updateImage(contact.contactId, false);
		back = (ImageView) findViewById(R.id.back_btn);
		back.setOnClickListener(this);
		viewDeviceVersionBtn.setOnClickListener(this);
		account_img.setOnClickListener(this);

		tv_divnum.setText("("
				+ Utils.getResString(mContext, R.string.alarmrecorddetail_sbid)
				+ contact.contactId + ")");

		String strName = contact.contactName;
		
//		if (strName.length() > 8) {
//			contactName.setText(contact.contactName.subSequence(0, 7) + "...");
//			// tv_setting.setText(Utils.getResString(mContext, R.string.mode)
//			// + "-" + strName.substring(0, 7) + "...");
//			//
//		} else {
			contactName.setText(strName);
//		}
		tv_setting.setText(Utils.getResString(mContext, R.string.mode) + "-"
				+ strName);

		if (contact.contactType == P2PValue.DeviceType.NPC) {
			viewDeviceVersionBtn.setVisibility(View.GONE);
		} else {
			viewDeviceVersionBtn.setVisibility(View.VISIBLE);
		}
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.REPLACE_SETTING_TIME);
		filter.addAction(Constants.Action.REPLACE_ALARM_CONTROL);
		filter.addAction(Constants.Action.REPLACE_REMOTE_CONTROL);
		filter.addAction(Constants.Action.REFRESH_CONTANTS);
		filter.addAction(Constants.Action.REPLACE_VIDEO_CONTROL);
		filter.addAction(Constants.Action.REPLACE_RECORD_CONTROL);
		filter.addAction(Constants.Action.REPLACE_SECURITY_CONTROL);
		filter.addAction(Constants.Action.REPLACE_NET_CONTROL);
		filter.addAction(Constants.Action.REPLACE_DEFENCE_AREA_CONTROL);
		filter.addAction(Constants.Action.REPLACE_MAIN_CONTROL);
		filter.addAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
		filter.addAction(Constants.P2P.ACK_RET_GET_DEVICE_INFO);
		filter.addAction(Constants.P2P.RET_GET_DEVICE_INFO);
		filter.addAction(Constants.Action.CONTROL_BACK);
		this.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			boolean isEnforce = intent.getBooleanExtra("isEnforce", false);
			if (intent.getAction().equals(
					Constants.Action.CONTROL_SETTING_PWD_ERROR)) {
				T.showShort(mContext, getString(R.string.password_error));
				finish();
			} else if (intent.getAction().equals(
					Constants.Action.REFRESH_CONTANTS)) {
				Contact c = (Contact) intent.getSerializableExtra("contact");
				if (null != c) {
					contact = c;
					String strName = c.contactName;
//					if (strName.length() > 8) {
//						contactName.setText(contact.contactName.subSequence(0,
//								7) + "...");
//						tv_setting.setText(Utils.getResString(mContext,
//								R.string.mode) + "-" + strName);
//					} else {
						contactName.setText(contact.contactName);
						tv_setting.setText(Utils.getResString(mContext,
								R.string.mode) + "-" + strName);
//					}

				}
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_MAIN_CONTROL)) {
				replaceFragment(FRAG_MAIN, true, true);
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_SETTING_TIME)) {
				tv_setting.setText(R.string.time_set);
				replaceFragment(FRAG_TIME_CONTROL, true, isEnforce);
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_DEFENCE_AREA_CONTROL)) {
				tv_setting.setText(R.string.defense_zone_set);
				replaceFragment(FRAG_DEFENCE_AREA_CONTROL, true, isEnforce);
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_NET_CONTROL)) {
				tv_setting.setText(R.string.network_set);
				replaceFragment(FRAG_NET_CONTROL, true, isEnforce);
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_ALARM_CONTROL)) {
				tv_setting.setText(R.string.alarm_set);
				replaceFragment(FRAG_ALARM_CONTROL, true, isEnforce);
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_VIDEO_CONTROL)) {
				tv_setting.setText(R.string.media_set);
				replaceFragment(FRAG_VIDEO_CONTROL, true, isEnforce);
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_RECORD_CONTROL)) {
				tv_setting.setText(R.string.video_set);
				replaceFragment(FRAG_RECORD_CONTROL, true, isEnforce);
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_SECURITY_CONTROL)) {
				tv_setting.setText(R.string.security_set);
				replaceFragment(FRAG_SECURITY_CONTROL, true, isEnforce);
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_REMOTE_CONTROL)) {
				replaceFragment(FRAG_REMOTE_CONTROL, true, isEnforce);
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_GET_DEVICE_INFO)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					if (null != dialog) {
						dialog.dismiss();
						dialog = null;
					}
					T.showShort(mContext, R.string.password_error);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:get device info");
					P2PHandler.getInstance().getDeviceVersion(
							contact.contactId, contact.contactPassword);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_DEVICE_INFO)) {
				int result = intent.getIntExtra("result", -1);
				String cur_version = intent.getStringExtra("cur_version");
				int iUbootVersion = intent.getIntExtra("iUbootVersion", 0);
				int iKernelVersion = intent.getIntExtra("iKernelVersion", 0);
				int iRootfsVersion = intent.getIntExtra("iRootfsVersion", 0);
				if (null != dialog) {
					dialog.dismiss();
					dialog = null;
				}

				if (isCancelCheck) {
					return;
				}

				NormalDialog deviceInfo = new NormalDialog(mContext);
				deviceInfo.showDeviceInfoDialog(cur_version,
						String.valueOf(iUbootVersion),
						String.valueOf(iKernelVersion),
						String.valueOf(iRootfsVersion));
			} else if (intent.getAction().equals(Constants.Action.CONTROL_BACK)) {
				tv_setting.setText(R.string.device_set);
			}

		}
	};

	@Override
	public void onClick(View view) {
		int id=view.getId();
		if(id==R.id.back_btn){
			back();
		}else if(id==R.id.viewDeviceVersionBtn){
			if (null != dialog && dialog.isShowing()) {
				Log.e("my", "isShowing");
				return;
			}
			dialog = new NormalDialog(mContext);
			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					isCancelCheck = true;
				}

			});
			dialog.setTitle(mContext.getResources().getString(
					R.string.device_info));
			dialog.showLoadingDialog();
			dialog.setCanceledOnTouchOutside(false);
			isCancelCheck = false;
			P2PHandler.getInstance().getDeviceVersion(contact.contactId,
					contact.contactPassword);
		}else if(id==R.id.editaccount){
			Intent modify = new Intent();
			modify.setClass(mContext, ModifyContactActivity.class);
			modify.putExtra("contact", contact);
			mContext.startActivity(modify);
		}
		
		
		// TODO Auto-generated method stub
//		switch (view.getId()) {
//		case R.id.back_btn:
//			back();
//			break;
//		case R.id.viewDeviceVersionBtn:
//			if (null != dialog && dialog.isShowing()) {
//				Log.e("my", "isShowing");
//				return;
//			}
//			dialog = new NormalDialog(mContext);
//			dialog.setOnCancelListener(new OnCancelListener() {
//
//				@Override
//				public void onCancel(DialogInterface arg0) {
//					// TODO Auto-generated method stub
//					isCancelCheck = true;
//				}
//
//			});
//			dialog.setTitle(mContext.getResources().getString(
//					R.string.device_info));
//			dialog.showLoadingDialog();
//			dialog.setCanceledOnTouchOutside(false);
//			isCancelCheck = false;
//			P2PHandler.getInstance().getDeviceVersion(contact.contactId,
//					contact.contactPassword);
//			break;
//		case R.id.editaccount:
//			Intent modify = new Intent();
//			modify.setClass(mContext, ModifyContactActivity.class);
//			modify.putExtra("contact", contact);
//			mContext.startActivity(modify);
//			break;
//		}

	}

	public void back() {
		if (current_frag != FRAG_MAIN) {
			replaceFragment(FRAG_MAIN, true, true);
		} else {
			finish();
		}
	}

	public boolean isReplace(int type, boolean isEnforce) {

		if (isEnforce || current_frag != FRAG_MAIN) {
			return true;
		} else {
			return false;
		}
	}

	public void replaceFragment(int type, boolean isAnim, boolean isEnforce) {
		if (type == current_frag) {

			return;
		}

		if (!isReplace(type, isEnforce)) {
			return;
		}
		Fragment fragment = newFragInstance(type);

		try {
			FragmentManager manager = getSupportFragmentManager();
			// FragmentManager manager = getChildFragmentManager();

			FragmentTransaction transaction = manager.beginTransaction();
			if (isAnim) {
				transaction.setCustomAnimations(android.R.anim.fade_in,
						android.R.anim.fade_out);
				switch (type) {
				case FRAG_REMOTE_CONTROL:
					if (current_frag == FRAG_MAIN || current_frag == -1) {
						transaction.setCustomAnimations(R.anim.slide_in_right,
								R.anim.slide_out_left);
					}
					break;
				case FRAG_MAIN:
					if (current_frag == FRAG_REMOTE_CONTROL
							|| current_frag == FRAG_TIME_CONTROL
							|| current_frag == FRAG_ALARM_CONTROL
							|| current_frag == FRAG_VIDEO_CONTROL
							|| current_frag == FRAG_RECORD_CONTROL
							|| current_frag == FRAG_SECURITY_CONTROL
							|| current_frag == FRAG_NET_CONTROL
							|| current_frag == FRAG_DEFENCE_AREA_CONTROL) {
						transaction.setCustomAnimations(R.anim.slide_in_left,
								R.anim.slide_out_right);
					}
					break;
				case FRAG_TIME_CONTROL:
					if (current_frag == FRAG_MAIN || current_frag == -1) {
						transaction.setCustomAnimations(R.anim.slide_in_right,
								R.anim.slide_out_left);
					}
					break;
				case FRAG_ALARM_CONTROL:
					if (current_frag == FRAG_MAIN || current_frag == -1) {
						transaction.setCustomAnimations(R.anim.slide_in_right,
								R.anim.slide_out_left);
					}
					break;
				case FRAG_VIDEO_CONTROL:
					if (current_frag == FRAG_MAIN || current_frag == -1) {
						transaction.setCustomAnimations(R.anim.slide_in_right,
								R.anim.slide_out_left);
					}
				case FRAG_RECORD_CONTROL:
					if (current_frag == FRAG_MAIN || current_frag == -1) {
						transaction.setCustomAnimations(R.anim.slide_in_right,
								R.anim.slide_out_left);
					}
					break;
				case FRAG_SECURITY_CONTROL:
					if (current_frag == FRAG_MAIN || current_frag == -1) {
						transaction.setCustomAnimations(R.anim.slide_in_right,
								R.anim.slide_out_left);
					}
				case FRAG_NET_CONTROL:
					if (current_frag == FRAG_MAIN || current_frag == -1) {
						transaction.setCustomAnimations(R.anim.slide_in_right,
								R.anim.slide_out_left);
					}
					break;
				case FRAG_DEFENCE_AREA_CONTROL:
					if (current_frag == FRAG_MAIN || current_frag == -1) {
						transaction.setCustomAnimations(R.anim.slide_in_right,
								R.anim.slide_out_left);
					}
					break;
				}
			}
			current_frag = type;
			transaction.replace(R.id.fragContainer, fragment,
					fragTags[current_frag]);
			transaction.commit();

			manager.executePendingTransactions();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("my", "replaceFrag error--main");
		}
	}

	public Fragment newFragInstance(int type) {
		Bundle args = new Bundle();
		args.putSerializable("contact", contact);
		args.putInt("type", device_type);

		switch (type) {
		case FRAG_MAIN:
			if (null == mainFrag) {
				mainFrag = new MainControlFrag();
				mainFrag.setArguments(args);
			}
			return mainFrag;		
		case FRAG_TIME_CONTROL:
			timeFrag = new TimeControlFrag();
			timeFrag.setArguments(args);
			return timeFrag;
		case FRAG_ALARM_CONTROL:
			alarmFrag = new AlarmControlFrag();
			alarmFrag.setArguments(args);
			return alarmFrag;
		case FRAG_VIDEO_CONTROL:
			videoFrag = new VideoControlFrag();
			videoFrag.setArguments(args);
			return videoFrag;
		case FRAG_RECORD_CONTROL:
			recordFrag = new RecordControlFrag();
			recordFrag.setArguments(args);
			return recordFrag;
		case FRAG_SECURITY_CONTROL:
			securityFrag = new SecurityControlFrag();
			securityFrag.setArguments(args);
			return securityFrag;
		case FRAG_NET_CONTROL:
			netFrag = new NetControlFrag();
			netFrag.setArguments(args);
			return netFrag;
		case FRAG_DEFENCE_AREA_CONTROL:
			defenceAreaFrag = new DefenceAreaControlFrag();
			defenceAreaFrag.setArguments(args);
			return defenceAreaFrag;
		default:
			return null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
		if (isRegFilter) {
			isRegFilter = false;
			this.unregisterReceiver(mReceiver);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (null != netFrag && current_frag == FRAG_NET_CONTROL) {
				if (netFrag.IsInputDialogShowing()) {
					Intent close_input_dialog = new Intent();
					close_input_dialog
							.setAction(Constants.Action.CLOSE_INPUT_DIALOG);
					mContext.sendBroadcast(close_input_dialog);
					return true;
				}
			}
			if (current_frag != FRAG_MAIN) {
				replaceFragment(FRAG_MAIN, true, true);
				return true;
			}

		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_MAINCONTROLACTIVITY;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		try {
			super.onResume();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("343", Log.getStackTraceString(e));
		}
	}

}
