package com.homecoolink.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.homecoolink.R;
import com.homecoolink.activity.MainControlActivity;
import com.homecoolink.adapter.DateNumericAdapter;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.wheel.widget.WheelView;
import com.p2p.core.P2PHandler;


public class RecordControlFrag extends BaseFragment implements OnClickListener {
	private Context mContext;
	private Contact contact;
	private boolean isRegFilter = false;

	RelativeLayout change_record_type, change_record_time, change_plan_time,
			change_plan_time_title;
	LinearLayout record_type_radio, record_time_radio, time_picker;
	ProgressBar progressBar_record_type, progressBar_record_time,
			progressBar_plan_time;
	RadioButton radio_one, radio_two, radio_three;
	RadioButton radio_one_time, radio_two_time, radio_three_time;
	TextView time_text;
	WheelView hour_from, minute_from, hour_to, minute_to;
	String cur_modify_plan_time;
	int cur_modify_record_type;
	int cur_modify_record_time;

	RelativeLayout change_record;
	ProgressBar progressBar_record;
	ImageView record_img;
	TextView record_text;
	int recordState;
	int last_record;
	int last_modify_record;

	SeekBar seek_alarmtime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = MainControlActivity.mContext;
		contact = (Contact) getArguments().getSerializable("contact");
		View view = inflater.inflate(R.layout.fragment_record_control,
				container, false);
		initComponent(view);
	
		showProgress_record_type();
		// P2PHandler.getInstance().getNpcSettings(contact.contactId,
		// contact.contactPassword);
		return view;
	}

	public void initComponent(View view) {
		seek_alarmtime = (SeekBar) view.findViewById(R.id.seek_alarmtime);

		change_record_type = (RelativeLayout) view
				.findViewById(R.id.change_record_type);
		record_type_radio = (LinearLayout) view
				.findViewById(R.id.record_type_radio);
		progressBar_record_type = (ProgressBar) view
				.findViewById(R.id.progressBar_record_type);

		radio_one = (RadioButton) view.findViewById(R.id.radio_one);
		radio_two = (RadioButton) view.findViewById(R.id.radio_two);
		radio_three = (RadioButton) view.findViewById(R.id.radio_three);

		change_record_time = (RelativeLayout) view
				.findViewById(R.id.change_record_time);
		record_time_radio = (LinearLayout) view
				.findViewById(R.id.record_time_radio);
		progressBar_record_time = (ProgressBar) view
				.findViewById(R.id.progressBar_record_time);

		radio_one_time = (RadioButton) view.findViewById(R.id.radio_one_time);
		radio_two_time = (RadioButton) view.findViewById(R.id.radio_two_time);
		radio_three_time = (RadioButton) view
				.findViewById(R.id.radio_three_time);

		change_plan_time = (RelativeLayout) view
				.findViewById(R.id.change_plan_time);
		change_plan_time_title = (RelativeLayout) view
				.findViewById(R.id.change_plan_time_title);
		progressBar_plan_time = (ProgressBar) view
				.findViewById(R.id.progressBar_plan_time);
		time_picker = (LinearLayout) view.findViewById(R.id.time_picker);
		time_text = (TextView) view.findViewById(R.id.time_text);
		initTimePicker(view);

		change_plan_time.setOnClickListener(this);

		radio_one.setOnClickListener(this);
		radio_two.setOnClickListener(this);
		radio_three.setOnClickListener(this);

		radio_one_time.setOnClickListener(this);
		radio_two_time.setOnClickListener(this);
		radio_three_time.setOnClickListener(this);

		change_record = (RelativeLayout) view.findViewById(R.id.change_record);
		record_img = (ImageView) view.findViewById(R.id.record_img);
		record_text = (TextView) view.findViewById(R.id.record_text);

		progressBar_record = (ProgressBar) view
				.findViewById(R.id.progressBar_record);

		change_record.setOnClickListener(this);

		seek_alarmtime
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// TODO Auto-generated method stub

						Log.e("343", "progress==" + progress + fromUser);

						if (fromUser) {
							if (progress == 0) {
								progressBar_record_time
										.setVisibility(View.VISIBLE);
								radio_one_time.setEnabled(false);
								radio_two_time.setEnabled(false);
								radio_three_time.setEnabled(false);
								cur_modify_record_time = Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_ONE_MINUTE;
								P2PHandler.getInstance().setRecordTime(
										contact.contactId,
										contact.contactPassword,
										cur_modify_record_time);

							} else if (progress == 1) {
								progressBar_record_time
										.setVisibility(View.VISIBLE);
								radio_one_time.setEnabled(false);
								radio_two_time.setEnabled(false);
								radio_three_time.setEnabled(false);
								cur_modify_record_time = Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_TWO_MINUTE;
								P2PHandler.getInstance().setRecordTime(
										contact.contactId,
										contact.contactPassword,
										cur_modify_record_time);
							} else if (progress == 2) {
								progressBar_record_time
										.setVisibility(View.VISIBLE);
								radio_one_time.setEnabled(false);
								radio_two_time.setEnabled(false);
								radio_three_time.setEnabled(false);
								cur_modify_record_time = Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_THREE_MINUTE;
								P2PHandler.getInstance().setRecordTime(
										contact.contactId,
										contact.contactPassword,
										cur_modify_record_time);

							}
						}

					}
				});

	}

	public void initTimePicker(View view) {
		hour_from = (WheelView) view.findViewById(R.id.hour_from);
		hour_from.setViewAdapter(new DateNumericAdapter(mContext, 0, 23));
		hour_from.setCyclic(true);

		minute_from = (WheelView) view.findViewById(R.id.minute_from);
		minute_from.setViewAdapter(new DateNumericAdapter(mContext, 0, 59));
		minute_from.setCyclic(true);

		hour_to = (WheelView) view.findViewById(R.id.hour_to);
		hour_to.setViewAdapter(new DateNumericAdapter(mContext, 0, 23));
		hour_to.setCyclic(true);

		minute_to = (WheelView) view.findViewById(R.id.minute_to);
		minute_to.setViewAdapter(new DateNumericAdapter(mContext, 0, 59));
		minute_to.setCyclic(true);
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_GET_NPC_SETTINGS);

		filter.addAction(Constants.P2P.ACK_RET_SET_RECORD_TYPE);
		filter.addAction(Constants.P2P.RET_SET_RECORD_TYPE);
		filter.addAction(Constants.P2P.RET_GET_RECORD_TYPE);

		filter.addAction(Constants.P2P.ACK_RET_SET_RECORD_TIME);
		filter.addAction(Constants.P2P.RET_SET_RECORD_TIME);
		filter.addAction(Constants.P2P.RET_GET_RECORD_TIME);

		filter.addAction(Constants.P2P.ACK_RET_SET_RECORD_PLAN_TIME);
		filter.addAction(Constants.P2P.RET_SET_RECORD_PLAN_TIME);
		filter.addAction(Constants.P2P.RET_GET_RECORD_PLAN_TIME);

		filter.addAction(Constants.P2P.ACK_RET_SET_REMOTE_RECORD);
		filter.addAction(Constants.P2P.RET_SET_REMOTE_RECORD);
		filter.addAction(Constants.P2P.RET_GET_REMOTE_RECORD);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(
					Constants.P2P.ACK_RET_GET_NPC_SETTINGS)) {
				
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:get npc settings");
					P2PHandler.getInstance().getNpcSettings(contact.contactId,
							contact.contactPassword);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_RECORD_TYPE)) {
				
				int type = intent.getIntExtra("type", -1);
				updateRecordType(type);
				showRecordType();
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_RECORD_TYPE)) {
			
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.RECORD_TYPE_SET.SETTING_SUCCESS) {
					
					updateRecordType(cur_modify_record_type);
					MyApp.RecordControlType = cur_modify_record_type;
					showRecordType();
//					if (cur_modify_record_type == Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_ALARM
//							|| cur_modify_record_type == Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_TIMER) {
//						updateRecord(-1);
//					}
					P2PHandler.getInstance().getNpcSettings(contact.contactId,
							contact.contactPassword);
					T.showShort(mContext, R.string.modify_success);

				} else {
				
					showRecordType();
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_RECORD_TIME)) {
			
				int time = intent.getIntExtra("time", -1);
				if (time == Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_ONE_MINUTE) {
					radio_one_time.setChecked(true);
					seek_alarmtime.setProgress(0);
				} else if (time == Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_TWO_MINUTE) {
					radio_two_time.setChecked(true);
					seek_alarmtime.setProgress(1);
				} else if (time == Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_THREE_MINUTE) {
					radio_three_time.setChecked(true);
					seek_alarmtime.setProgress(2);
				}
				radio_one_time.setEnabled(true);
				radio_two_time.setEnabled(true);
				radio_three_time.setEnabled(true);
				progressBar_record_time.setVisibility(View.GONE);
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_RECORD_TIME)) {
				int result = intent.getIntExtra("result", -1);
				if (result == 0) {
					if (cur_modify_record_time == Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_ONE_MINUTE) {
						radio_one_time.setChecked(true);
						seek_alarmtime.setProgress(0);
					} else if (cur_modify_record_time == Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_TWO_MINUTE) {
						radio_two_time.setChecked(true);
						seek_alarmtime.setProgress(1);
					} else if (cur_modify_record_time == Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_THREE_MINUTE) {
						radio_three_time.setChecked(true);
						seek_alarmtime.setProgress(2);
					}
					radio_one_time.setEnabled(true);
					radio_two_time.setEnabled(true);
					radio_three_time.setEnabled(true);
					progressBar_record_time.setVisibility(View.GONE);
					T.showShort(mContext, R.string.modify_success);
				} else {
					radio_one_time.setEnabled(true);
					radio_two_time.setEnabled(true);
					radio_three_time.setEnabled(true);
					progressBar_record_time.setVisibility(View.GONE);
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_RECORD_PLAN_TIME)) {
				
				String time = intent.getStringExtra("time");

				time_text.setText(time);
				change_plan_time.setEnabled(true);
				progressBar_plan_time.setVisibility(View.GONE);
				time_text.setVisibility(View.VISIBLE);
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_RECORD_PLAN_TIME)) {
				
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.RECORD_PLAN_TIME_SET.SETTING_SUCCESS) {
					time_text.setText(cur_modify_plan_time);
					change_plan_time.setEnabled(true);
					progressBar_plan_time.setVisibility(View.GONE);
					time_text.setVisibility(View.VISIBLE);
					T.showShort(mContext, R.string.modify_success);
				} else {
					change_plan_time.setEnabled(true);
					progressBar_plan_time.setVisibility(View.GONE);
					time_text.setVisibility(View.VISIBLE);
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_RECORD_TYPE)) {
				
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					
					Log.e("my", "net error resend:set npc settings record type");
					P2PHandler.getInstance().setRecordType(contact.contactId,
							contact.contactPassword, cur_modify_record_type);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_RECORD_TIME)) {
				
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					
					Log.e("my", "net error resend:set npc settings record time");
					P2PHandler.getInstance().setRecordType(contact.contactId,
							contact.contactPassword, cur_modify_record_type);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_RECORD_PLAN_TIME)) {
				
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					
					Log.e("my",
							"net error resend:set npc settings record plan time");
					P2PHandler.getInstance().setRecordPlanTime(
							contact.contactId, contact.contactPassword,
							cur_modify_plan_time);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_REMOTE_RECORD)) {
			
				int state = intent.getIntExtra("state", -1);
				progressBar_record.setVisibility(View.GONE);
				record_img.setVisibility(View.VISIBLE);
				
				updateRecord(state);
				
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_REMOTE_RECORD)) {
			
				P2PHandler.getInstance().getNpcSettings(contact.contactId,
						contact.contactPassword);
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_REMOTE_RECORD)) {
				
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					
					Log.e("my", "net error resend:set remote record");
					P2PHandler.getInstance().setRemoteRecord(contact.contactId,
							contact.contactPassword, last_modify_record);
				}
			}
		}
	};

	public void updateRecord(int state) {
		if (state == Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_ON) {
			last_record = Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_ON;

			record_img.setBackgroundResource(R.drawable.setting_status_on);
		} else {
			last_record = Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_OFF;

			record_img.setBackgroundResource(R.drawable.setting_status_off);
		}
	}

	void updateRecordType(int type) {
		if (type == Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_MANUAL) {
			
			radio_one.setChecked(true);
			hideRecordTime();
			hidePlanTime();
			showManual();
		} else if (type == Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_ALARM) {
		
			radio_two.setChecked(true);
			hidePlanTime();
			hideManual();
			showRecordTime();

		} else if (type == Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_TIMER) {
		
			radio_three.setChecked(true);
			hideRecordTime();
			hideManual();
			showPlanTime();
		}
	}

	@Override
	public void onClick(View view) {
		int id=view.getId();
		if(id==R.id.change_record){
			progressBar_record.setVisibility(View.VISIBLE);
			record_img.setVisibility(View.GONE);
			if (last_record == Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_ON) {
				last_modify_record = Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_OFF;
				P2PHandler.getInstance().setRemoteRecord(contact.contactId,
						contact.contactPassword, last_modify_record);
			} else {
				last_modify_record = Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_ON;
				P2PHandler.getInstance().setRemoteRecord(contact.contactId,
						contact.contactPassword, last_modify_record);
			}
		}else if(id== R.id.radio_one){
			progressBar_record_type.setVisibility(View.VISIBLE);
			radio_one.setEnabled(false);
			radio_two.setEnabled(false);
			radio_three.setEnabled(false);
			cur_modify_record_type = Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_MANUAL;
			P2PHandler.getInstance().setRecordType(contact.contactId,
					contact.contactPassword, cur_modify_record_type);
		}else if(id==R.id.radio_two){
			progressBar_record_type.setVisibility(View.VISIBLE);
			radio_one.setEnabled(false);
			radio_two.setEnabled(false);
			radio_three.setEnabled(false);
			cur_modify_record_type = Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_ALARM;
			P2PHandler.getInstance().setRecordType(contact.contactId,
					contact.contactPassword, cur_modify_record_type);
		}else if(id==R.id.radio_three){
			radio_one.setEnabled(false);
			radio_two.setEnabled(false);
			radio_three.setEnabled(false);
			progressBar_record_type.setVisibility(View.VISIBLE);
			cur_modify_record_type = Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_TIMER;
			P2PHandler.getInstance().setRecordType(contact.contactId,
					contact.contactPassword, cur_modify_record_type);
		}else if(id==R.id.radio_one_time){
			progressBar_record_time.setVisibility(View.VISIBLE);
			radio_one_time.setEnabled(false);
			radio_two_time.setEnabled(false);
			radio_three_time.setEnabled(false);
			cur_modify_record_time = Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_ONE_MINUTE;
			P2PHandler.getInstance().setRecordTime(contact.contactId,
					contact.contactPassword, cur_modify_record_time);
			seek_alarmtime.setProgress(0);
		}else if(id==R.id.radio_two_time){
			progressBar_record_time.setVisibility(View.VISIBLE);
			radio_one_time.setEnabled(false);
			radio_two_time.setEnabled(false);
			radio_three_time.setEnabled(false);
			cur_modify_record_time = Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_TWO_MINUTE;
			P2PHandler.getInstance().setRecordTime(contact.contactId,
					contact.contactPassword, cur_modify_record_time);
			seek_alarmtime.setProgress(1);
		}else if(id==R.id.radio_three_time){
			progressBar_record_time.setVisibility(View.VISIBLE);
			radio_one_time.setEnabled(false);
			radio_two_time.setEnabled(false);
			radio_three_time.setEnabled(false);
			cur_modify_record_time = Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_THREE_MINUTE;
			P2PHandler.getInstance().setRecordTime(contact.contactId,
					contact.contactPassword, cur_modify_record_time);
			seek_alarmtime.setProgress(2);
		}else if(id==R.id.change_plan_time){
			showProgress_plan_time();

			cur_modify_plan_time = Utils.convertPlanTime(
					hour_from.getCurrentItem(), minute_from.getCurrentItem(),
					hour_to.getCurrentItem(), minute_to.getCurrentItem());
			P2PHandler.getInstance().setRecordPlanTime(contact.contactId,
					contact.contactPassword, cur_modify_plan_time);
		}
		
		
		// TODO Auto-generated method stub
//		switch (view.getId()) {
//		case R.id.change_record:
//			progressBar_record.setVisibility(View.VISIBLE);
//			record_img.setVisibility(View.GONE);
//			if (last_record == Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_ON) {
//				last_modify_record = Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_OFF;
//				P2PHandler.getInstance().setRemoteRecord(contact.contactId,
//						contact.contactPassword, last_modify_record);
//			} else {
//				last_modify_record = Constants.P2P_SET.REMOTE_RECORD_SET.RECORD_SWITCH_ON;
//				P2PHandler.getInstance().setRemoteRecord(contact.contactId,
//						contact.contactPassword, last_modify_record);
//			}
//			break;
//		case R.id.radio_one:
//			progressBar_record_type.setVisibility(View.VISIBLE);
//			radio_one.setEnabled(false);
//			radio_two.setEnabled(false);
//			radio_three.setEnabled(false);
//			cur_modify_record_type = Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_MANUAL;
//			P2PHandler.getInstance().setRecordType(contact.contactId,
//					contact.contactPassword, cur_modify_record_type);
//			break;
//		case R.id.radio_two:
//			progressBar_record_type.setVisibility(View.VISIBLE);
//			radio_one.setEnabled(false);
//			radio_two.setEnabled(false);
//			radio_three.setEnabled(false);
//			cur_modify_record_type = Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_ALARM;
//			P2PHandler.getInstance().setRecordType(contact.contactId,
//					contact.contactPassword, cur_modify_record_type);
//			break;
//		case R.id.radio_three:
//			radio_one.setEnabled(false);
//			radio_two.setEnabled(false);
//			radio_three.setEnabled(false);
//			progressBar_record_type.setVisibility(View.VISIBLE);
//			cur_modify_record_type = Constants.P2P_SET.RECORD_TYPE_SET.RECORD_TYPE_TIMER;
//			P2PHandler.getInstance().setRecordType(contact.contactId,
//					contact.contactPassword, cur_modify_record_type);
//			break;
//		case R.id.radio_one_time:
//			progressBar_record_time.setVisibility(View.VISIBLE);
//			radio_one_time.setEnabled(false);
//			radio_two_time.setEnabled(false);
//			radio_three_time.setEnabled(false);
//			cur_modify_record_time = Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_ONE_MINUTE;
//			P2PHandler.getInstance().setRecordTime(contact.contactId,
//					contact.contactPassword, cur_modify_record_time);
//			seek_alarmtime.setProgress(0);
//			break;
//		case R.id.radio_two_time:
//			progressBar_record_time.setVisibility(View.VISIBLE);
//			radio_one_time.setEnabled(false);
//			radio_two_time.setEnabled(false);
//			radio_three_time.setEnabled(false);
//			cur_modify_record_time = Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_TWO_MINUTE;
//			P2PHandler.getInstance().setRecordTime(contact.contactId,
//					contact.contactPassword, cur_modify_record_time);
//			seek_alarmtime.setProgress(1);
//			break;
//		case R.id.radio_three_time:
//			progressBar_record_time.setVisibility(View.VISIBLE);
//			radio_one_time.setEnabled(false);
//			radio_two_time.setEnabled(false);
//			radio_three_time.setEnabled(false);
//			cur_modify_record_time = Constants.P2P_SET.RECORD_TIME_SET.RECORD_TIME_THREE_MINUTE;
//			P2PHandler.getInstance().setRecordTime(contact.contactId,
//					contact.contactPassword, cur_modify_record_time);
//			seek_alarmtime.setProgress(2);
//			break;
//		case R.id.change_plan_time:
//			showProgress_plan_time();
//
//			cur_modify_plan_time = Utils.convertPlanTime(
//					hour_from.getCurrentItem(), minute_from.getCurrentItem(),
//					hour_to.getCurrentItem(), minute_to.getCurrentItem());
//			P2PHandler.getInstance().setRecordPlanTime(contact.contactId,
//					contact.contactPassword, cur_modify_plan_time);
//			break;
//		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}

	public void showRecordType() {
		// change_record_type.setBackgroundResource(R.drawable.tiao_bg_up);
		progressBar_record_type.setVisibility(View.GONE);
		record_type_radio.setVisibility(View.VISIBLE);
		radio_one.setEnabled(true);
		radio_two.setEnabled(true);
		radio_three.setEnabled(true);
	}

	public void showProgress_record_type() {
		// change_record_type.setBackgroundResource(R.drawable.tiao_bg_single);
		progressBar_record_type.setVisibility(View.VISIBLE);
		record_type_radio.setVisibility(View.GONE);
	}

	public void showRecordTime() {
		change_record_time.setVisibility(View.VISIBLE);
		record_time_radio.setVisibility(View.VISIBLE);
		progressBar_record_time.setVisibility(View.GONE);
	}

	public void showProgress_record_time() {
		change_record_time.setVisibility(View.VISIBLE);
		record_time_radio.setVisibility(View.VISIBLE);
		progressBar_record_time.setVisibility(View.VISIBLE);
	}

	public void showPlanTime() {
		time_picker.setVisibility(View.VISIBLE);
		change_plan_time.setVisibility(View.VISIBLE);
		change_plan_time_title.setVisibility(View.VISIBLE);
		progressBar_plan_time.setVisibility(View.GONE);
		time_text.setVisibility(View.VISIBLE);
	}

	public void showProgress_plan_time() {
		time_picker.setVisibility(View.VISIBLE);
		change_plan_time.setVisibility(View.VISIBLE);
		change_plan_time.setEnabled(false);
		progressBar_plan_time.setVisibility(View.VISIBLE);
		time_text.setVisibility(View.GONE);
	}

	public void showManual() {
		change_record.setVisibility(View.VISIBLE);
	}

	public void hideRecordTime() {
		change_record_time.setVisibility(View.GONE);
		record_time_radio.setVisibility(View.GONE);
	}

	public void hidePlanTime() {
		change_plan_time_title.setVisibility(View.GONE);
		change_plan_time.setVisibility(View.GONE);
		time_picker.setVisibility(View.GONE);
	}

	public void hideManual() {
		change_record.setVisibility(View.GONE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent it = new Intent();
		it.setAction(Constants.Action.CONTROL_BACK);
		mContext.sendBroadcast(it);
	}

	@Override
	public void onStop() {

		super.onStop();
		if (isRegFilter) {
			isRegFilter = false;
			mContext.unregisterReceiver(mReceiver);
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		regFilter();
		P2PHandler.getInstance().getNpcSettings(contact.contactId,
				contact.contactPassword);
	}
}
