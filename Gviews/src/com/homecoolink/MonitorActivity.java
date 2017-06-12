package com.homecoolink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.activity.MainActivity;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.MyApp;
import com.homecoolink.utils.PhoneWatcher;
import com.homecoolink.utils.T;
import com.p2p.core.BaseMonitorActivity;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.core.P2PView;


public class MonitorActivity extends BaseMonitorActivity implements
		OnClickListener {
	Context mContext;
	boolean isRegFilter = false;
	boolean mIsCloseVoice = false;
	int type;
	ImageView screenshot, hungup, close_voice, send_voice, img_reverse,
			definition_btn;
	AudioManager mAudioManager = null;
	int mCurrentVolume, mMaxVolume;
	PhoneWatcher mPhoneWatcher;
	RelativeLayout control_bottom;
	LinearLayout layout_voice_state;
	// LinearLayout control_top;
	ImageView voice_state;
	TextView video_mode_hd, video_mode_sd, video_mode_ld;
	TextView text_number;
	boolean isControlShow = true;
	boolean isReject = false;
	boolean isHD = false;
	int current_video_mode;
	String callId;
	String password;
	private PopupWindow popMenu;
	private Handler mhandler = new Handler();
	private LayoutInflater lif;
	RelativeLayout btndefense = null;
	ImageView imgdefense = null;
	ProgressBar progress_defence = null;
	Contact contact;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		 
		
			P2PConnect.setPlaying(true);
			Window win = getWindow();
			win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

			setContentView(R.layout.p2p_monitor);
			type = this.getIntent().getIntExtra("type", -1);
			mContext = this;
			contact = FList.getInstance().isContact(
					P2PConnect.getCurrent_call_id());
			initComponent();
			regFilter();
			startWatcher();
			if (mAudioManager == null) {
				mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
			}
			mCurrentVolume = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			mMaxVolume = mAudioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			lif = LayoutInflater.from(mContext);

	}

	@Override
	public void onHomePressed() {
		// TODO Auto-generated method stub
		super.onHomePressed();
		reject();
	}

	private void startWatcher() {
		mPhoneWatcher = new PhoneWatcher(mContext);
		mPhoneWatcher
				.setOnCommingCallListener(new PhoneWatcher.OnCommingCallListener() {

					@Override
					public void onCommingCall() {
						// TODO Auto-generated method stub
						reject();
					}

				});
		mPhoneWatcher.startWatcher();
	}

	public void initComponent() {

		definition_btn = (ImageView) findViewById(R.id.definition);

		pView = (P2PView) findViewById(R.id.pView);
		this.initP2PView(P2PConnect.getCurrentDeviceType());
		setMute(true);

		screenshot = (ImageView) findViewById(R.id.screenshot);
		hungup = (ImageView) findViewById(R.id.hungup);
		close_voice = (ImageView) findViewById(R.id.close_voice);
		control_bottom = (RelativeLayout) findViewById(R.id.control_bottom);
		// control_top = (LinearLayout) findViewById(R.id.control_top);
		layout_voice_state = (LinearLayout) findViewById(R.id.layout_voice_state);
		send_voice = (ImageView) findViewById(R.id.send_voice);
		voice_state = (ImageView) findViewById(R.id.voice_state);

		text_number = (TextView) findViewById(R.id.text_number);
		text_number.setText(this.getResources().getString(
				R.string.monitor_number)
				+ P2PConnect.getNumber());
		btndefense = (RelativeLayout) findViewById(R.id.defense);
		imgdefense = (ImageView) findViewById(R.id.image_defence_state);
		progress_defence = (ProgressBar) findViewById(R.id.progress_defence);
		getdefensestate();

		final AnimationDrawable anim = (AnimationDrawable) voice_state
				.getDrawable();
		OnPreDrawListener opdl = new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				anim.start();
				return true;
			}

		};
		voice_state.getViewTreeObserver().addOnPreDrawListener(opdl);
		send_voice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.e("bug", "" + event.getAction());
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					layout_voice_state.setVisibility(View.VISIBLE);
					setMute(false);
					return true;
				case MotionEvent.ACTION_UP:
					// layout_voice_state.setVisibility(RelativeLayout.GONE);
					// setMute(true);
					mhandler.postDelayed(mrunnable, 1000);
					return true;
				}
				return false;
			}

		});

		screenshot.setOnClickListener(this);
		hungup.setOnClickListener(this);
		close_voice.setOnClickListener(this);

		definition_btn.setOnClickListener(this);
		btndefense.setOnClickListener(this);
	}

	private void getdefensestate() {
		if (imgdefense != null && progress_defence != null) {
			if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_LOADING) {
				progress_defence.setVisibility(View.VISIBLE);
				imgdefense.setVisibility(View.GONE);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
				progress_defence.setVisibility(View.GONE);
				imgdefense.setVisibility(View.VISIBLE);
				imgdefense.setImageResource(R.drawable.contact_list_lock);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
				progress_defence.setVisibility(View.GONE);
				imgdefense.setVisibility(View.VISIBLE);
				imgdefense.setImageResource(R.drawable.contact_list_unlock);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET) {
				progress_defence.setVisibility(View.GONE);
				imgdefense.setVisibility(View.VISIBLE);
				imgdefense.setImageResource(R.drawable.ic_defence_warning);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
				progress_defence.setVisibility(View.GONE);
				imgdefense.setVisibility(View.VISIBLE);
				imgdefense.setImageResource(R.drawable.ic_defence_warning);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_NO_PERMISSION) {
				progress_defence.setVisibility(View.GONE);
				imgdefense.setVisibility(View.VISIBLE);
				imgdefense.setImageResource(R.drawable.limit);
			}
		}
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.P2P_REJECT);
		filter.addAction(Constants.P2P.P2P_MONITOR_NUMBER_CHANGE);
		filter.addAction(Constants.P2P.P2P_RESOLUTION_CHANGE);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	Runnable mrunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			layout_voice_state.setVisibility(View.GONE);
			setMute(true);
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.e("my", "onConfigurationChanged:" + newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

		}
		mHandler.sendEmptyMessageDelayed(0, 500);
	}

	public Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub.
			pView.updateScreenOrientation();
			return false;
		}
	});

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			
				
			
			if (intent.getAction().equals(Constants.P2P.P2P_REJECT)) {
				reject();
				System.out.println("摄像机挂断+P2P_REJECT");
			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				reject();
				System.out.println("摄像机挂断+ACTION_SCREEN_OFF");
			} else if (intent.getAction().equals(
					Constants.P2P.P2P_MONITOR_NUMBER_CHANGE)) {
				int number = intent.getIntExtra("number", -1);
				if (number != -1) {
					text_number.setText(mContext.getResources().getString(
							R.string.monitor_number)
							+ P2PConnect.getNumber());
				}
			} else if (intent.getAction().equals(
					Constants.P2P.P2P_RESOLUTION_CHANGE)) {
				int mode = intent.getIntExtra("mode", -1);
				if (mode != -1) {
					current_video_mode = mode;
					updateVideoModeText(current_video_mode);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_REMOTE_DEFENCE)) {
				int state = intent.getIntExtra("state", -1);
				String contactId = intent.getStringExtra("contactId");
				if (contactId.equals(P2PConnect.getCurrent_call_id())) {
					contact = FList.getInstance().isContact(contactId);
					if (state == Constants.DefenceState.DEFENCE_STATE_WARNING_NET) {
						if (null != contact && contact.isClickGetDefenceState) {
							T.showShort(mContext, R.string.net_error);
						}
					} else if (state == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
						if (null != contact && contact.isClickGetDefenceState) {
							T.showShort(mContext, R.string.password_error);
						}
					}

					getdefensestate();
				}

			}
			
		}
		
	};

	public void changeControl() {
		if (isControlShow) {
			isControlShow = false;
			// Animation anim2 = AnimationUtils.loadAnimation(this,
			// R.anim.slide_out_top);
			Animation anim2 = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out);
			anim2.setDuration(200);
			control_bottom.startAnimation(anim2);
			control_bottom.setVisibility(View.GONE);

			// control_top.startAnimation(anim2);
			// control_top.setVisibility(RelativeLayout.GONE);
		} else {
			isControlShow = true;
			control_bottom.setVisibility(View.VISIBLE);
			// control_top.setVisibility(RelativeLayout.VISIBLE);
			// Animation anim2 = AnimationUtils.loadAnimation(this,
			// R.anim.slide_in_bottom);
			Animation anim2 = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in);
			anim2.setDuration(200);
			control_bottom.startAnimation(anim2);
			// control_top.startAnimation(anim2);
		}
	}

	public void updateVideoModeText(int mode) {
		if (video_mode_hd != null) {
			if (mode == P2PValue.VideoMode.VIDEO_MODE_HD) {
				video_mode_hd.setTextColor(mContext.getResources().getColor(
						R.color.text_color_blue));
				video_mode_sd.setTextColor(mContext.getResources().getColor(
						R.color.text_color_white));
				video_mode_ld.setTextColor(mContext.getResources().getColor(
						R.color.text_color_white));
			} else if (mode == P2PValue.VideoMode.VIDEO_MODE_SD) {
				video_mode_hd.setTextColor(mContext.getResources().getColor(
						R.color.text_color_white));
				video_mode_sd.setTextColor(mContext.getResources().getColor(
						R.color.text_color_blue));
				video_mode_ld.setTextColor(mContext.getResources().getColor(
						R.color.text_color_white));
			} else if (mode == P2PValue.VideoMode.VIDEO_MODE_LD) {
				video_mode_hd.setTextColor(mContext.getResources().getColor(
						R.color.text_color_white));
				video_mode_sd.setTextColor(mContext.getResources().getColor(
						R.color.text_color_white));
				video_mode_ld.setTextColor(mContext.getResources().getColor(
						R.color.text_color_blue));
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.video_mode_hd){
			if (current_video_mode != P2PValue.VideoMode.VIDEO_MODE_HD) {
				current_video_mode = P2PValue.VideoMode.VIDEO_MODE_HD;
				P2PHandler.getInstance().setVideoMode(
						P2PValue.VideoMode.VIDEO_MODE_HD);
				updateVideoModeText(current_video_mode);
			}
		}else if(id==R.id.video_mode_sd){
			if (current_video_mode != P2PValue.VideoMode.VIDEO_MODE_SD) {
				current_video_mode = P2PValue.VideoMode.VIDEO_MODE_SD;
				P2PHandler.getInstance().setVideoMode(
						P2PValue.VideoMode.VIDEO_MODE_SD);
				updateVideoModeText(current_video_mode);
			}
		}else if(id==R.id.video_mode_ld){
			if (current_video_mode != P2PValue.VideoMode.VIDEO_MODE_LD) {
				current_video_mode = P2PValue.VideoMode.VIDEO_MODE_LD;
				P2PHandler.getInstance().setVideoMode(
						P2PValue.VideoMode.VIDEO_MODE_LD);
				updateVideoModeText(current_video_mode);
			}
		}else if(id==R.id.screenshot){
			this.captureScreen();

		}else if(id==R.id.hungup){
			reject();
		}else if(id==R.id.close_voice){
			if (mIsCloseVoice) {
				mIsCloseVoice = false;
				close_voice.setImageResource(R.drawable.volume_no);
				if (mCurrentVolume == 0) {
					mCurrentVolume = 1;
				}
				if (mAudioManager != null) {
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							mCurrentVolume, 0);
				}
			} else {
				mIsCloseVoice = true;
				close_voice.setImageResource(R.drawable.volume);
				if (mAudioManager != null) {
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
							0);
				}
			}
		}else if(id==R.id.definition){
			try {
				int w = View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED);
				int h = View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED);
				v.measure(w, h);
				int height = v.getMeasuredHeight();
				int width = v.getMeasuredWidth();
				View view = getLayoutInflater().inflate(
						R.layout.definition_layout, null, false);
				video_mode_hd = (TextView) view
						.findViewById(R.id.video_mode_hd);
				video_mode_sd = (TextView) view
						.findViewById(R.id.video_mode_sd);
				video_mode_ld = (TextView) view
						.findViewById(R.id.video_mode_ld);
				current_video_mode = P2PConnect.getMode();
				updateVideoModeText(current_video_mode);
				if (P2PConnect.getCurrentDeviceType() == P2PValue.DeviceType.IPC) {
					video_mode_hd.setVisibility(View.VISIBLE);
				} else {
					video_mode_hd.setVisibility(View.GONE);
				}
				video_mode_hd.setOnClickListener((MonitorActivity) mContext);
				video_mode_sd.setOnClickListener((MonitorActivity) mContext);
				video_mode_ld.setOnClickListener((MonitorActivity) mContext);
				popMenu = new PopupWindow(view, (int) getResources()
						.getDimension(R.dimen.p2p_control_top_item_width),
						(int) getResources().getDimension(
								R.dimen.p2p_control_top_item_height) * 3);

				popMenu.setFocusable(true);
				popMenu.setOutsideTouchable(true);
				popMenu.setBackgroundDrawable(new BitmapDrawable());
				popMenu.showAsDropDown(
						v,
						-((int) getResources().getDimension(
								R.dimen.p2p_control_top_item_width) - width) / 2,
						0);
			} catch (Exception e) {

				
				Log.e("343", Log.getStackTraceString(e));
			}

		}else if(id==R.id.defense){
			if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET
					|| contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
				progress_defence.setVisibility(View.VISIBLE);
				imgdefense.setVisibility(View.GONE);
				P2PHandler.getInstance().getDefenceStates(contact.contactId,
						contact.contactPassword);
				FList.getInstance().setIsClickGetDefenceState(
						contact.contactId, true);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
				progress_defence.setVisibility(View.VISIBLE);
				imgdefense.setVisibility(View.GONE);
				P2PHandler.getInstance().setRemoteDefence(contact.contactId,
						contact.contactPassword,
						Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
				FList.getInstance().setIsClickGetDefenceState(
						contact.contactId, true);
			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
				progress_defence.setVisibility(View.VISIBLE);
				imgdefense.setVisibility(View.GONE);
				P2PHandler.getInstance().setRemoteDefence(contact.contactId,
						contact.contactPassword,
						Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
				FList.getInstance().setIsClickGetDefenceState(
						contact.contactId, true);
			}else if (contact.defenceState == Constants.DefenceState.DEFENCE_NO_PERMISSION) {
				 T.showShort(MonitorActivity.this, R.string.insufficient_permissions);	
			}
		}
		
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		// case R.id.change_video_mode:
//		// if(isHD){
//		// isHD = false;
//		// int result =
//		// P2PHandler.getInstance().setVideoMode(P2PValue.VideoMode.VIDEO_MODE_LD);
//		// change_video_mode.setImageResource(R.drawable.ic_video_sd);
//		// Log.e("my","setVideoMode:"+result);
//		// }else{
//		// isHD = true;
//		// int result =
//		// P2PHandler.getInstance().setVideoMode(P2PValue.VideoMode.VIDEO_MODE_HD);
//		// change_video_mode.setImageResource(R.drawable.ic_video_hd);
//		// Log.e("my","setVideoMode:"+result);
//		// }
//		// break;
//		case R.id.video_mode_hd:
//			if (current_video_mode != P2PValue.VideoMode.VIDEO_MODE_HD) {
//				current_video_mode = P2PValue.VideoMode.VIDEO_MODE_HD;
//				P2PHandler.getInstance().setVideoMode(
//						P2PValue.VideoMode.VIDEO_MODE_HD);
//				updateVideoModeText(current_video_mode);
//			}
//			break;
//		case R.id.video_mode_sd:
//			if (current_video_mode != P2PValue.VideoMode.VIDEO_MODE_SD) {
//				current_video_mode = P2PValue.VideoMode.VIDEO_MODE_SD;
//				P2PHandler.getInstance().setVideoMode(
//						P2PValue.VideoMode.VIDEO_MODE_SD);
//				updateVideoModeText(current_video_mode);
//			}
//			break;
//		case R.id.video_mode_ld:
//			if (current_video_mode != P2PValue.VideoMode.VIDEO_MODE_LD) {
//				current_video_mode = P2PValue.VideoMode.VIDEO_MODE_LD;
//				P2PHandler.getInstance().setVideoMode(
//						P2PValue.VideoMode.VIDEO_MODE_LD);
//				updateVideoModeText(current_video_mode);
//			}
//			break;
//		case R.id.screenshot:
//			this.captureScreen();
//
//			break;
//		case R.id.hungup:
//			reject();
//			break;
//		case R.id.close_voice:
//			if (mIsCloseVoice) {
//				mIsCloseVoice = false;
//				close_voice.setImageResource(R.drawable.volume_no);
//				if (mCurrentVolume == 0) {
//					mCurrentVolume = 1;
//				}
//				if (mAudioManager != null) {
//					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
//							mCurrentVolume, 0);
//				}
//			} else {
//				mIsCloseVoice = true;
//				close_voice.setImageResource(R.drawable.volume);
//				if (mAudioManager != null) {
//					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
//							0);
//				}
//			}
//			break;
//		case R.id.definition:
//			try {
//				int w = View.MeasureSpec.makeMeasureSpec(0,
//						View.MeasureSpec.UNSPECIFIED);
//				int h = View.MeasureSpec.makeMeasureSpec(0,
//						View.MeasureSpec.UNSPECIFIED);
//				v.measure(w, h);
//				int height = v.getMeasuredHeight();
//				int width = v.getMeasuredWidth();
//				View view = getLayoutInflater().inflate(
//						R.layout.definition_layout, null, false);
//				video_mode_hd = (TextView) view
//						.findViewById(R.id.video_mode_hd);
//				video_mode_sd = (TextView) view
//						.findViewById(R.id.video_mode_sd);
//				video_mode_ld = (TextView) view
//						.findViewById(R.id.video_mode_ld);
//				current_video_mode = P2PConnect.getMode();
//				updateVideoModeText(current_video_mode);
//				if (P2PConnect.getCurrentDeviceType() == P2PValue.DeviceType.IPC) {
//					video_mode_hd.setVisibility(View.VISIBLE);
//				} else {
//					video_mode_hd.setVisibility(View.GONE);
//				}
//				video_mode_hd.setOnClickListener((MonitorActivity) mContext);
//				video_mode_sd.setOnClickListener((MonitorActivity) mContext);
//				video_mode_ld.setOnClickListener((MonitorActivity) mContext);
//				popMenu = new PopupWindow(view, (int) getResources()
//						.getDimension(R.dimen.p2p_control_top_item_width),
//						(int) getResources().getDimension(
//								R.dimen.p2p_control_top_item_height) * 3);
//
//				popMenu.setFocusable(true);
//				popMenu.setOutsideTouchable(true);
//				popMenu.setBackgroundDrawable(new BitmapDrawable());
//				popMenu.showAsDropDown(
//						v,
//						-((int) getResources().getDimension(
//								R.dimen.p2p_control_top_item_width) - width) / 2,
//						0);
//			} catch (Exception e) {
//
//				
//				Log.e("343", Log.getStackTraceString(e));
//			}
//
//			// popMenu.showAtLocation(definition_btn, Gravity.TOP, 100, 100);
//			break;
//		case R.id.defense:
//
//			if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET
//					|| contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
//				progress_defence.setVisibility(View.VISIBLE);
//				imgdefense.setVisibility(View.GONE);
//				P2PHandler.getInstance().getDefenceStates(contact.contactId,
//						contact.contactPassword);
//				FList.getInstance().setIsClickGetDefenceState(
//						contact.contactId, true);
//			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
//				progress_defence.setVisibility(View.VISIBLE);
//				imgdefense.setVisibility(View.GONE);
//				P2PHandler.getInstance().setRemoteDefence(contact.contactId,
//						contact.contactPassword,
//						Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
//				FList.getInstance().setIsClickGetDefenceState(
//						contact.contactId, true);
//			} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
//				progress_defence.setVisibility(View.VISIBLE);
//				imgdefense.setVisibility(View.GONE);
//				P2PHandler.getInstance().setRemoteDefence(contact.contactId,
//						contact.contactPassword,
//						Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
//				FList.getInstance().setIsClickGetDefenceState(
//						contact.contactId, true);
//			}else if (contact.defenceState == Constants.DefenceState.DEFENCE_NO_PERMISSION) {
//				 T.showShort(MonitorActivity.this, R.string.insufficient_permissions);	
//			}
//			break;
//		}
	}

	@Override
	public void onBackPressed() {
		reject();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			mCurrentVolume++;
			if (mCurrentVolume > mMaxVolume) {
				mCurrentVolume = mMaxVolume;
			}

			if (mCurrentVolume != 0) {
				mIsCloseVoice = false;
				close_voice.setImageResource(R.drawable.volume_no);
			}
			return false;
		} else if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			mCurrentVolume--;
			if (mCurrentVolume < 0) {
				mCurrentVolume = 0;
			}

			if (mCurrentVolume == 0) {
				mIsCloseVoice = true;
				close_voice.setImageResource(R.drawable.volume);
			}

			return false;
		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mAudioManager != null) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					mCurrentVolume, 0);
		}
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}

		if (null != mPhoneWatcher) {
			mPhoneWatcher.stopWatcher();
		}
		P2PConnect.setPlaying(false);

		if (!activity_stack
				.containsKey(Constants.ActivityInfo.ACTIVITY_MAINACTIVITY)) {
			Intent i = new Intent(this, MainActivity.class);
			this.startActivity(i);
		}

		Intent refreshContans = new Intent();
		refreshContans.setAction(Constants.Action.REFRESH_CONTANTS);
		mContext.sendBroadcast(refreshContans);
		mhandler.removeCallbacks(mrunnable);
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_MONITORACTIVITY;
	}

	@Override
	protected void onP2PViewSingleTap() {
		// TODO Auto-generated method stub
		changeControl();
	}

	@Override
	protected void onGoBack() {
		// TODO Auto-generated method stub
		MyApp.app.showNotification();
	}

	@Override
	protected void onGoFront() {
		// TODO Auto-generated method stub
		MyApp.app.hideNotification();
	}

	@Override
	protected void onExit() {
		// TODO Auto-generated method stub
		MyApp.app.hideNotification();
	}


	@Override
	protected void onCaptureScreenResult(boolean isSuccess) {
		// TODO Auto-generated method stub
		if (isSuccess) {
			// Capture success
			T.showShort(mContext, R.string.capture_success);
		} else {
			T.showShort(mContext, R.string.capture_failed);
		}
	}

	public void reject() {
		
			if (!isReject) {
				isReject = true;
				P2PHandler.getInstance().reject();
				finish();
			}
		

	}

	
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				T.showShort(mContext, R.string.press_again_monitor);
				exitTime = System.currentTimeMillis();
			} else {
				reject();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
