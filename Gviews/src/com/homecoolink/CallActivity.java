package com.homecoolink;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.utils.PwdErrorUtil;
import com.homecoolink.R;
import com.homecoolink.activity.MainActivity;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.data.NearlyTell;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.PhoneWatcher;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.HeaderView;
import com.p2p.core.BaseCallActivity;
import com.p2p.core.P2PHandler;


public class CallActivity extends BaseCallActivity implements OnClickListener {
	// test svn
	Context mContext;
	PhoneWatcher mPhoneWatcher;
	TextView top_text, reject_text, title_text;
	RelativeLayout accept, reject, layout_accept;
	boolean isOutCall;
	ImageView call_anim;
	HeaderView header_img;
	String callId;
	String contactName;
	String ipFlag;
	int type;
	String password;
	boolean isRegFilter = false;

	boolean isAccept = false;
	boolean isReject = false;

	// ImageView iv;
	TextView ror_tv;
	Timer timer;

	// GifView gifView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try {
			
		
		super.onCreate(savedInstanceState);
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		mContext = this;
		setContentView(R.layout.activity_call);

		isOutCall = this.getIntent().getBooleanExtra("isOutCall", false);
		callId = this.getIntent().getStringExtra("callId");
		contactName = this.getIntent().getStringExtra("contactName");
		ipFlag = this.getIntent().getStringExtra("ipFlag");
		type = this.getIntent().getIntExtra("type", -1);
		password = this.getIntent().getStringExtra("password");

		if (!Utils.hasDigit(callId)) {
			if (type == Constants.P2P_TYPE.P2P_TYPE_MONITOR) {
				T.showShort(mContext, R.string.monitor_id_must_include_digit);
			} else {
				T.showShort(mContext, R.string.call_id_must_include_digit);
			}

			finish();
		} else {
			P2PConnect.setCurrent_state(P2PConnect.P2P_STATE_CALLING);
			P2PConnect.setCurrent_call_id(callId);
			initComponent();
			regFilter();
			startWatcher();
			String push_mesg = NpcCommon.mThreeNum
					+ ":"
					+ mContext.getResources().getString(
							R.string.p2p_call_push_mesg);

			// if (!P2PHandler.getInstance().call(NpcCommon.mThreeNum, password,
			// isOutCall, type, callId, ipFlag, push_mesg)) {
			// finish();
			// }

			P2PHandler.getInstance().call(NpcCommon.mThreeNum, password,
					isOutCall, type, callId, ipFlag, push_mesg);
		}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("343", Log.getStackTraceString(e));
		}
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
		ror_tv = (TextView) findViewById(R.id.rate_of_reach_tv);
		top_text = (TextView) findViewById(R.id.top_text);
//		accept = (RelativeLayout) findViewById(R.id.accept);
//		layout_accept = (RelativeLayout) findViewById(R.id.layout_accept);
//		reject = (RelativeLayout) findViewById(R.id.reject);
//		reject_text = (TextView) findViewById(R.id.reject_text);
//		title_text = (TextView) findViewById(R.id.title_text);
//		call_anim = (ImageView) findViewById(R.id.call_anim);
		header_img = (HeaderView) findViewById(R.id.header_img);
		header_img.updateImage(callId, false);
		if (isOutCall) {
//			reject_text.setText(R.string.hungup);
//			layout_accept.setVisibility(View.GONE);
			if (type == Constants.P2P_TYPE.P2P_TYPE_MONITOR) {
				top_text.setText(mContext.getResources().getString(
						R.string.connecting_to)
						+ "......");
//				if (contactName != null && !contactName.equals("")) {
////					title_text.setText(contactName);
//				} else {
//					title_text.setText(callId);
//				}
//				call_anim.setImageResource(R.anim.monitor);

			} else {
//				if (contactName != null && !contactName.equals("")) {
//					title_text.setText(contactName);
//				} else {
//					title_text.setText(callId);
//				}
//				call_anim.setImageResource(R.anim.call_out);
				top_text.setText(mContext.getResources().getString(
						R.string.calling_to)
						+ "......");
			}

		} else {
//			call_anim.setImageResource(R.anim.call_in);
//			reject_text.setText(R.string.reject);
//			layout_accept.setVisibility(View.VISIBLE);
			// top_text.setText(mContext.getResources().getString(
			// R.string.calling_from)
			// + " " + callId);

			Contact contact = FList.getInstance().isContact(callId);
//			if (contact == null) {
//				title_text.setText(callId);
//			} else {
//				title_text.setText(contact.contactName);
//			}

		}		

		timer = new Timer(true);
		timer.schedule(task, 1000, 1000);
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.P2P_ACCEPT);
		filter.addAction(Constants.P2P.P2P_READY);
		filter.addAction(Constants.P2P.P2P_REJECT);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	Intent intentCall;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {

			if (intent.getAction().equals(Constants.P2P.P2P_ACCEPT)) {
				P2PHandler.getInstance().openAudioAndStartPlaying();

			} else if (intent.getAction().equals(Constants.P2P.P2P_READY)) {
				
				intentCall = new Intent();
				if (type == Constants.P2P_TYPE.P2P_TYPE_CALL) {
					Log.e("343", "P2P_TYPE_CALL");
//					intentCall.setClass(mContext, VideoActivity.class);
				} else if (type == Constants.P2P_TYPE.P2P_TYPE_MONITOR) {
					Log.e("343", "P2P_TYPE_MONITOR");
					task.cancel();
					ror_tv.setText("100");

					intentCall.setClass(mContext, MonitorActivity.class);

					// iv.clearAnimation();
					// intentCall.putExtra("type", type);
					// Log.e("343", "看看这个传的是什么东西"+type);
					// intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// handler.sendEmptyMessage(10);
					// mContext.startActivity(intentCall);
					// finish();
					// intentCall = null;
				}
				intentCall.putExtra("type", type);
				intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intentCall);
				overridePendingTransition(android.R.anim.fade_in,
						android.R.anim.fade_out);

				finish();
			} else if (intent.getAction().equals(Constants.P2P.P2P_REJECT)) {
				if (intent.getStringExtra("msg") != null
						&& intent.getStringExtra("msg").equals(
								getResources().getString(R.string.pw_incrrect))) {
					new PwdErrorUtil(CallActivity.this).PwdError(callId, new PwdErrorUtil.DialogHandler() {
						@Override
						public void onClose() {
							reject();
						}
					});

				} else {
					reject();
				}

			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				reject();
			}
		}
	};

	@Override
	public void onBackPressed() {
		reject();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.accept:
//			if (!isAccept) {
//				isAccept = true;
//				P2PHandler.getInstance().accept();
//			}
//			break;
//		case R.id.reject:
//			reject();
//			break;
//		}
	}

	public void reject() {
		if (!isReject) {
			isReject = true;
			P2PHandler.getInstance().reject();
			if (!activity_stack
					.containsKey(Constants.ActivityInfo.ACTIVITY_MAINACTIVITY)) {
				Intent i = new Intent(CallActivity.this, MainActivity.class);
				startActivity(i);
			}
			finish();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}

		if (null != mPhoneWatcher) {
			mPhoneWatcher.stopWatcher();
		}
		insertNearly();
	}

	public void insertNearly() {
		NearlyTell nearlyTell = new NearlyTell();
		nearlyTell.activeUser = NpcCommon.mThreeNum;
		nearlyTell.tellId = callId;
		nearlyTell.tellTime = String.valueOf(System.currentTimeMillis());
		nearlyTell.tellState = type;
		if (isOutCall && isReject) {
			nearlyTell.tellState = NearlyTell.TELL_STATE_CALL_OUT_REJECT;
		} else if (isOutCall && !isReject) {
			nearlyTell.tellState = NearlyTell.TELL_STATE_CALL_OUT_ACCEPT;
		} else if (!isOutCall && isReject) {
			nearlyTell.tellState = NearlyTell.TELL_STATE_CALL_IN_REJECT;
		} else {
			nearlyTell.tellState = NearlyTell.TELL_STATE_CALL_IN_ACCEPT;
		}
		DataManager.insertNearlyTell(mContext, nearlyTell);
	}

	Handler handler = new Handler() {
		//
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				ror_tv.setText("10");
				break;
			case 2:
				ror_tv.setText("20");
				break;
			case 3:
				ror_tv.setText("30");
				break;
			case 4:
				ror_tv.setText("40");
				break;
			case 5:
				ror_tv.setText("50");
				break;
			case 6:
				ror_tv.setText("60");
				break;
			case 7:
				ror_tv.setText("70");
				break;
			case 8:
				ror_tv.setText("80");
				break;
			case 9:
				ror_tv.setText("90");

				break;
			case 10:
				// mContext.startActivity(intentCall);
				// overridePendingTransition(android.R.anim.fade_in,
				// android.R.anim.fade_out);
				// intentCall = null;
				// finish();

				break;
			default:
				break;
			}
		}

	};

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_CALLACTIVITY;
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

	int i_count = 1;
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = i_count;
			if (i_count < 10) {
				handler.sendEmptyMessage(i_count);
				i_count++;
			} else {
				task.cancel();
			}
		}
	};

}
