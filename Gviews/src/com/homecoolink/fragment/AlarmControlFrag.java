package com.homecoolink.fragment;

import java.util.List;

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
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.activity.AlarmPushAccountActivity;
import com.homecoolink.activity.MainControlActivity;
import com.homecoolink.activity.ModifyBoundEmailActivity;
import com.homecoolink.data.AlarmMask;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.global.Constants;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.T;
import com.homecoolink.widget.NormalDialog;
import com.lib.addBar.AddBar;
import com.p2p.core.P2PHandler;

public class AlarmControlFrag extends BaseFragment implements OnClickListener {
	private Context mContext;
	private Contact contact;

	RelativeLayout change_buzzer, change_motion, change_email, add_alarm_item,
			change_pir,alarm_input_switch,alarm_out_switch,shield_alarm;
	LinearLayout buzzer_time;
	RadioButton radio_one, radio_two, radio_three;
	ProgressBar progressBar, progressBar_motion, progressBar_email,
			progressBar_alarmId, progressBar_pir,progressBar_alarm_input,progressBar_alarm_out,progressBar_receive_alarm,progressBar_shield;		
	ImageView buzzer_img, motion_img, icon_add_alarm_id, pir_img,img_alarm_input,
	        img_alarm_out,img_receive_alarm,shield_img;
	AddBar addBar;
	int buzzer_switch;
	int motion_switch;
	private boolean isRegFilter = false;
    
	TextView email_text, alarmId_text;
	NormalDialog dialog_loading;
	String[] last_bind_data;
	int cur_modify_buzzer_state;
	int cur_modify_motion_state;
	int infrared_switch;
	int modify_infrared_state;
    boolean current_infrared_state;
    boolean isOpenWriedAlarmInput;
    boolean isOpenWriedAlarmOut;
    boolean isReceiveAlarm=true;
    RelativeLayout layout_alarm_switch;
    String[] new_data;
    int max_alarm_count;
    boolean OpenShieldDevice=false;
    
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
		View view = inflater.inflate(R.layout.fragment_alarm_control,
				container, false);
		initComponent(view);
		regFilter();

		showProgress();
		showProgress_motion();
		showProgress_email();
//		showProgress_alarmId();
		P2PHandler.getInstance().getNpcSettings(contact.contactId,
				contact.contactPassword);
		P2PHandler.getInstance().getBindAlarmId(contact.contactId,
				contact.contactPassword);
		
		P2PHandler.getInstance().getAlarmEmail(contact.contactId,
				contact.contactPassword);
		return view;
	}

	public void initComponent(View view) {
		seek_alarmtime = (SeekBar) view.findViewById(R.id.seek_alarmtime);
		change_buzzer = (RelativeLayout) view.findViewById(R.id.change_buzzer);
		buzzer_img = (ImageView) view.findViewById(R.id.buzzer_img);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		buzzer_time = (LinearLayout) view.findViewById(R.id.buzzer_time);

		change_motion = (RelativeLayout) view.findViewById(R.id.change_motion);
		motion_img = (ImageView) view.findViewById(R.id.motion_img);
		progressBar_motion = (ProgressBar) view
				.findViewById(R.id.progressBar_motion);

		radio_one = (RadioButton) view.findViewById(R.id.radio_one);
		radio_two = (RadioButton) view.findViewById(R.id.radio_two);
		radio_three = (RadioButton) view.findViewById(R.id.radio_three);

		change_email = (RelativeLayout) view.findViewById(R.id.change_email);
		email_text = (TextView) view.findViewById(R.id.email_text);
		progressBar_email = (ProgressBar) view
				.findViewById(R.id.progressBar_email);

		add_alarm_item = (RelativeLayout) view
				.findViewById(R.id.add_alarm_item);
//		progressBar_alarmId = (ProgressBar) view
//				.findViewById(R.id.progressBar_alarmId);
//		icon_add_alarm_id = (ImageView) view
//				.findViewById(R.id.icon_add_alarm_id);

		change_pir = (RelativeLayout) view.findViewById(R.id.change_pir);
		pir_img = (ImageView) view.findViewById(R.id.pir_img);
		progressBar_pir = (ProgressBar) view.findViewById(R.id.progressBar_pir);
		
		alarm_input_switch=(RelativeLayout)view.findViewById(R.id.alarm_input_switch);
		img_alarm_input=(ImageView)view.findViewById(R.id.img_alarm_input);
		progressBar_alarm_input=(ProgressBar)view.findViewById(R.id.progressBar_alarm_input);
		
		alarm_out_switch=(RelativeLayout)view.findViewById(R.id.alarm_out_switch);
		img_alarm_out=(ImageView)view.findViewById(R.id.img_alarm_out);
		progressBar_alarm_out=(ProgressBar)view.findViewById(R.id.progressBar_alarm_out);
		img_receive_alarm=(ImageView)view.findViewById(R.id.img_receive_alarm);
		layout_alarm_switch=(RelativeLayout)view.findViewById(R.id.layout_alarm_switch);
		progressBar_receive_alarm=(ProgressBar)view.findViewById(R.id.progressBar_receive_alarm);
		shield_img=(ImageView)view.findViewById(R.id.shield_img);
		progressBar_shield=(ProgressBar)view.findViewById(R.id.progressBar_shield);
		shield_alarm=(RelativeLayout)view.findViewById(R.id.shield_alarm);
//		addBar = (AddBar) view.findViewById(R.id.add_bar);
//		addBar.setOnItemChangeListener(new OnItemChangeListener() {
//
//			@Override
//			public void onChange(int item) {
//				// TODO Auto-generated method stub
//				if (item > 0) {
//					add_alarm_item.setBackgroundResource(R.drawable.tiao_bg_up);
//				} else {
//					add_alarm_item
//							.setBackgroundResource(R.drawable.tiao_bg_single);
//				}
//			}
//
//		});

//		addBar.setOnLeftIconClickListener(new OnLeftIconClickListener() {
//
//			@Override
//			public void onClick(View icon, final int position) {
//				// TODO Auto-generated method stub
//				NormalDialog dialog = new NormalDialog(mContext, mContext
//						.getResources().getString(R.string.delete_alarm_id),
//						mContext.getResources().getString(
//								R.string.ensure_delete)
//								+ " 0" + last_bind_data[position] + "?",
//						mContext.getResources().getString(R.string.ensure),
//						mContext.getResources().getString(R.string.cancel));
//				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
//
//					@Override
//					public void onClick() {
//						// TODO Auto-generated method stub
//
//						if (null == dialog_loading) {
//							dialog_loading = new NormalDialog(mContext,
//									mContext.getResources().getString(
//											R.string.verification), "", "", "");
//							dialog_loading
//									.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
//						}
//						dialog_loading.showDialog();
//
//						String[] data = Utils.getDeleteAlarmIdArray(
//								last_bind_data, position);
//						last_bind_data = data;
//						P2PHandler.getInstance().setBindAlarmId(
//								contact.contactId, contact.contactPassword,
//								data.length, data);
//					}
//				});
//				dialog.showDialog();
//			}
//		});

		add_alarm_item.setOnClickListener(this);
		change_email.setOnClickListener(this);
		change_motion.setOnClickListener(this);
		change_buzzer.setOnClickListener(this);
		radio_one.setOnClickListener(this);
		radio_two.setOnClickListener(this);
		radio_three.setOnClickListener(this);
		change_pir.setOnClickListener(this);
		alarm_input_switch.setOnClickListener(this);
		alarm_out_switch.setOnClickListener(this);
//		img_receive_alarm.setOnClickListener(this);
		layout_alarm_switch.setOnClickListener(this);
		shield_alarm.setOnClickListener(this);
		layout_alarm_switch.setClickable(false);
		add_alarm_item.setClickable(false);
		if(NpcCommon.mThreeNum.equals("517400")){
			layout_alarm_switch.setVisibility(View.GONE);	
		}
		List<AlarmMask> list = DataManager.findAlarmMaskByActiveUser(mContext, NpcCommon.mThreeNum);
		progressBar_shield.setVisibility(View.GONE);
		if(list.size()<=0){
		    shield_img.setVisibility(View.VISIBLE);
		    shield_img.setBackgroundResource(R.drawable.setting_status_off);
		    OpenShieldDevice=false;

		}
		for(AlarmMask alarmMask : list){
			if(contact.contactId.equals(alarmMask.deviceId)){
				OpenShieldDevice=true;
				break;
			}
		}
		if(OpenShieldDevice)
		{
			shield_img.setVisibility(View.VISIBLE);
			shield_img.setBackgroundResource(R.drawable.setting_status_on);
		}
		else
		{
			shield_img.setVisibility(View.VISIBLE);
			shield_img.setBackgroundResource(R.drawable.setting_status_off);
		}
		seek_alarmtime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				Log.e("343", "progress=="+progress+fromUser);
				
				if (fromUser) {
					if (progress==0) {
						showProgress();
						cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE;
						P2PHandler.getInstance().setBuzzer(contact.contactId,
								contact.contactPassword, cur_modify_buzzer_state);
					}else if(progress==1){
						showProgress();
						cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_TWO_MINUTE;
						P2PHandler.getInstance().setBuzzer(contact.contactId,
								contact.contactPassword, cur_modify_buzzer_state);
					}else if (progress==2) {
						showProgress();
						cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_THREE_MINUTE;
						P2PHandler.getInstance().setBuzzer(contact.contactId,
								contact.contactPassword, cur_modify_buzzer_state);
					}	
				}
				
				
			}
		});
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_GET_NPC_SETTINGS);

		filter.addAction(Constants.P2P.ACK_RET_SET_BIND_ALARM_ID);
		filter.addAction(Constants.P2P.ACK_RET_GET_BIND_ALARM_ID);
		filter.addAction(Constants.P2P.RET_SET_BIND_ALARM_ID);
		filter.addAction(Constants.P2P.RET_GET_BIND_ALARM_ID);

		// filter.addAction(Constants.P2P.ACK_RET_SET_ALARM_EMAIL);
		filter.addAction(Constants.P2P.ACK_RET_GET_ALARM_EMAIL);
		filter.addAction(Constants.P2P.RET_SET_ALARM_EMAIL);
		filter.addAction(Constants.P2P.RET_GET_ALARM_EMAIL);

		filter.addAction(Constants.P2P.ACK_RET_SET_MOTION);
		filter.addAction(Constants.P2P.RET_SET_MOTION);
		filter.addAction(Constants.P2P.RET_GET_MOTION);

		filter.addAction(Constants.P2P.ACK_RET_SET_BUZZER);
		filter.addAction(Constants.P2P.RET_SET_BUZZER);
		filter.addAction(Constants.P2P.RET_GET_BUZZER);
		filter.addAction(Constants.P2P.RET_GET_INFRARED_SWITCH);
		filter.addAction(Constants.P2P.ACK_RET_SET_INFRARED_SWITCH);
        filter.addAction(Constants.P2P.RET_GET_WIRED_ALARM_INPUT);
        filter.addAction(Constants.P2P.RET_GET_WIRED_ALARM_OUT);
        filter.addAction(Constants.P2P.ACK_RET_SET_WIRED_ALARM_INPUT);
        filter.addAction(Constants.P2P.ACK_RET_SET_WIRED_ALARM_OUT);
        
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.P2P.RET_GET_BIND_ALARM_ID)) {
				
				showImg_receive_alarm();
				String[] data = intent.getStringArrayExtra("data");
				int max_count = intent.getIntExtra("max_count", 0);
				last_bind_data = data;
//				addBar.removeAll();
//				addBar.setMax_count(max_count);
//				for (int i = 0; i < data.length; i++) {
//					addBar.addItem(data[i]);
//				}
				max_alarm_count=max_count;
				showAlarmIdState();
				layout_alarm_switch.setClickable(true);
				add_alarm_item.setClickable(true);
				int count=0;
				for(int i=0;i<data.length;i++){
					if(data[i].equals(NpcCommon.mThreeNum)){
						img_receive_alarm.setBackgroundResource(R.drawable.setting_status_on);
						isReceiveAlarm=false;
						count=count+1;
						return;
					}
				}
				if(count==0){
					img_receive_alarm.setBackgroundResource(R.drawable.setting_status_off);
					isReceiveAlarm=true;
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_BIND_ALARM_ID)) {
				int result = intent.getIntExtra("result", -1);
				if (null != dialog_loading && dialog_loading.isShowing()) {
					dialog_loading.dismiss();
					dialog_loading = null;
				}
				if (result == Constants.P2P_SET.BIND_ALARM_ID_SET.SETTING_SUCCESS) {
//					addBar.removeAll();
					P2PHandler.getInstance().getBindAlarmId(contact.contactId,
							contact.contactPassword);
					T.showShort(mContext, R.string.modify_success);
//					if(isReceiveAlarm==true){
//						img_receive_alarm.setBackgroundResource(R.drawable.check_on);
//						isReceiveAlarm=false;
//					}else{
//						img_receive_alarm.setBackgroundResource(R.drawable.check_off);
//						isReceiveAlarm=true;
//					}
				} else {
					if (getIsRun()) {
						T.showShort(mContext, R.string.operator_error);
					}
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_ALARM_EMAIL)) {
				String email = intent.getStringExtra("email");
				if (email.equals("")||email.equals("0")) {
					email_text.setText(R.string.unbound);
				} else {
					email_text.setText(email);
				}
				showEmailState();
			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_ALARM_EMAIL)) {

				P2PHandler.getInstance().getAlarmEmail(contact.contactId,
						contact.contactPassword);

			} else if (intent.getAction().equals(Constants.P2P.RET_GET_MOTION)) {
				int state = intent.getIntExtra("motionState", -1);
				if (state == Constants.P2P_SET.MOTION_SET.MOTION_DECT_ON) {
					motion_switch = Constants.P2P_SET.MOTION_SET.MOTION_DECT_ON;
					motion_img.setBackgroundResource(R.drawable.setting_status_on);
				} else {
					motion_switch = Constants.P2P_SET.MOTION_SET.MOTION_DECT_OFF;
					motion_img
							.setBackgroundResource(R.drawable.setting_status_off);
				}
				showMotionState();
			} else if (intent.getAction().equals(Constants.P2P.RET_SET_MOTION)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.MOTION_SET.SETTING_SUCCESS) {
					if (cur_modify_motion_state == Constants.P2P_SET.MOTION_SET.MOTION_DECT_ON) {
						motion_switch = Constants.P2P_SET.MOTION_SET.MOTION_DECT_ON;
						motion_img
								.setBackgroundResource(R.drawable.setting_status_on);
					} else {
						motion_switch = Constants.P2P_SET.MOTION_SET.MOTION_DECT_OFF;
						motion_img
								.setBackgroundResource(R.drawable.setting_status_off);
					}
					showMotionState();
					T.showShort(mContext, R.string.modify_success);
				} else {
					showMotionState();
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(Constants.P2P.RET_GET_BUZZER)) {
				int state = intent.getIntExtra("buzzerState", -1);
				updateBuzzer(state);
				showBuzzerTime();
			} else if (intent.getAction().equals(Constants.P2P.RET_SET_BUZZER)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.BUZZER_SET.SETTING_SUCCESS) {
					updateBuzzer(cur_modify_buzzer_state);
					showBuzzerTime();
					T.showShort(mContext, R.string.modify_success);
				} else {
					showBuzzerTime();
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(
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
					Constants.P2P.ACK_RET_SET_BIND_ALARM_ID)) {
				int result = intent.getIntExtra("result", -1);

				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					if (null != dialog_loading && dialog_loading.isShowing()) {
						dialog_loading.dismiss();
						dialog_loading = null;
					}
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:set alarm bind id");
					P2PHandler.getInstance().setBindAlarmId(contact.contactId,
							contact.contactPassword, new_data.length,
							new_data);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_GET_BIND_ALARM_ID)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:get alarm bind id");
					P2PHandler.getInstance().getBindAlarmId(contact.contactId,
							contact.contactPassword);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_GET_ALARM_EMAIL)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:get alarm email");
					P2PHandler.getInstance().getAlarmEmail(contact.contactId,
							contact.contactPassword);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_MOTION)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:set npc settings motion");
					P2PHandler.getInstance().setMotion(contact.contactId,
							contact.contactPassword, cur_modify_motion_state);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_BUZZER)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:set npc settings buzzer");
					P2PHandler.getInstance().setBuzzer(contact.contactId,
							contact.contactPassword, cur_modify_buzzer_state);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_GET_INFRARED_SWITCH)) {
				int state = intent.getIntExtra("state", -1);
				if (state == Constants.P2P_SET.INFRARED_SWITCH.INFRARED_SWITCH_ON) {
					change_pir.setVisibility(View.VISIBLE);
					current_infrared_state=false;
					pir_img.setBackgroundResource(R.drawable.setting_status_on);
				} else if (state == Constants.P2P_SET.INFRARED_SWITCH.INFRARED_SWITCH_OFF) {
					change_pir.setVisibility(View.VISIBLE);
					current_infrared_state=true;
					pir_img.setBackgroundResource(R.drawable.setting_status_off);
				}
				showImg_infrared_switch();
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_INFRARED_SWITCH)) {
				int result=intent.getIntExtra("result",-1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					if(current_infrared_state==true){
						P2PHandler.getInstance().setInfraredSwitch(contact.contactId, contact.contactPassword,1);
					}else{
						P2PHandler.getInstance().setInfraredSwitch(contact.contactId, contact.contactPassword,0);
					}
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS){
				    if(current_infrared_state==true){
				       current_infrared_state=false;
				       pir_img.setBackgroundResource(R.drawable.setting_status_on);
				    }else{
				    	current_infrared_state=true;
				    	pir_img.setBackgroundResource(R.drawable.setting_status_off);
				    }
				    showImg_infrared_switch();
				}

			}else if(intent.getAction().equals(Constants.P2P.RET_GET_WIRED_ALARM_INPUT)){
				int state=intent.getIntExtra("state", -1);
				if(state==Constants.P2P_SET.WIRED_ALARM_INPUT.ALARM_INPUT_ON){
					alarm_input_switch.setVisibility(View.VISIBLE);
					isOpenWriedAlarmInput=false;
					img_alarm_input.setBackgroundResource(R.drawable.setting_status_on);
				}else if(state==Constants.P2P_SET.WIRED_ALARM_INPUT.ALARM_INPUT_OFF){
					alarm_input_switch.setVisibility(View.VISIBLE);
					isOpenWriedAlarmInput=true;
					img_alarm_input.setBackgroundResource(R.drawable.setting_status_off);
				}
				showImg_wired_alarm_input();
			}else if(intent.getAction().equals(Constants.P2P.RET_GET_WIRED_ALARM_OUT)){
				int state=intent.getIntExtra("state", -1);
				if(state==Constants.P2P_SET.WIRED_ALARM_OUT.ALARM_OUT_ON){
					alarm_out_switch.setVisibility(View.VISIBLE);
					isOpenWriedAlarmOut=false;
					img_alarm_out.setBackgroundResource(R.drawable.setting_status_on);
				}else if(state==Constants.P2P_SET.WIRED_ALARM_OUT.ALARM_OUT_OFF){
					alarm_out_switch.setVisibility(View.VISIBLE);
					isOpenWriedAlarmOut=true;
					img_alarm_out.setBackgroundResource(R.drawable.setting_status_off);
				}
				showImg_wired_alarm_out();
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_WIRED_ALARM_INPUT)){
				int result=intent.getIntExtra("state", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					if(isOpenWriedAlarmInput==true){
						P2PHandler.getInstance().setWiredAlarmInput(contact.contactId,contact.contactPassword, 1);
					}else{
						P2PHandler.getInstance().setWiredAlarmInput(contact.contactId,contact.contactPassword, 0);
					}
					
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS){
					if(isOpenWriedAlarmInput==true){
						isOpenWriedAlarmInput=false;
						img_alarm_input.setBackgroundResource(R.drawable.setting_status_on);
					}else{
						isOpenWriedAlarmInput=true;
						img_alarm_input.setBackgroundResource(R.drawable.setting_status_off);
					}
					showImg_wired_alarm_input();
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_WIRED_ALARM_OUT)){
				int result=intent.getIntExtra("state", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					if(isOpenWriedAlarmOut==true){
						P2PHandler.getInstance().setWiredAlarmOut(contact.contactId,contact.contactPassword, 1);
					}else{
						P2PHandler.getInstance().setWiredAlarmOut(contact.contactId,contact.contactPassword, 0);
					}
					
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS){
					if(isOpenWriedAlarmOut==true){
						isOpenWriedAlarmOut=false;
						img_alarm_out.setBackgroundResource(R.drawable.setting_status_on);
					}else{
						isOpenWriedAlarmOut=true;
						img_alarm_out.setBackgroundResource(R.drawable.setting_status_off);
					}
					showImg_wired_alarm_out();
				}
			}

		}
	};

	@Override
	public void onClick(View view) {
		int id=view.getId();
		if(id==R.id.change_email){
			Intent modify_email = new Intent(mContext,
					ModifyBoundEmailActivity.class);
			modify_email.putExtra("contact", contact);
			modify_email.putExtra("email",email_text.getText().toString());
			mContext.startActivity(modify_email);
		}else if(id==R.id.add_alarm_item){
			if(last_bind_data.length<=0||(last_bind_data[0].equals("0")&&last_bind_data.length==1)){
				T.showShort(mContext,R.string.no_alarm_account);
			}else{
				Intent it=new Intent(mContext,AlarmPushAccountActivity.class);
				it.putExtra("contactId",contact.contactId);
				it.putExtra("contactPassword", contact.contactPassword);
				startActivity(it);
			}
		}else if(id==R.id.change_buzzer){
			showProgress();
			if (buzzer_switch != Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_OFF) {
				cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_OFF;
			} else {
				cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE;
			}
			P2PHandler.getInstance().setBuzzer(contact.contactId,
					contact.contactPassword, cur_modify_buzzer_state);
        }else if(id==R.id.change_motion){
        	showProgress_motion();
			if (motion_switch != Constants.P2P_SET.MOTION_SET.MOTION_DECT_OFF) {
				cur_modify_motion_state = Constants.P2P_SET.MOTION_SET.MOTION_DECT_OFF;
				P2PHandler.getInstance().setMotion(contact.contactId,
						contact.contactPassword, cur_modify_motion_state);
			} else {
				cur_modify_motion_state = Constants.P2P_SET.MOTION_SET.MOTION_DECT_ON;
				P2PHandler.getInstance().setMotion(contact.contactId,
						contact.contactPassword, cur_modify_motion_state);
			}
        }else if(id==R.id.radio_one){
        	showProgress();
			cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE;
			P2PHandler.getInstance().setBuzzer(contact.contactId,
					contact.contactPassword, cur_modify_buzzer_state);
			seek_alarmtime.setProgress(0);
        
        }else if(id==R.id.radio_two){
        	showProgress();
			cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_TWO_MINUTE;
			P2PHandler.getInstance().setBuzzer(contact.contactId,
					contact.contactPassword, cur_modify_buzzer_state);
			seek_alarmtime.setProgress(1);
        }else if(id==R.id.radio_three){
        	showProgress();
			cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_THREE_MINUTE;
			P2PHandler.getInstance().setBuzzer(contact.contactId,
					contact.contactPassword, cur_modify_buzzer_state);
			seek_alarmtime.setProgress(2);
        }else if(id==R.id.change_pir){
        	showProgress_infrares_switch();
			if (current_infrared_state==true) {
				modify_infrared_state = Constants.P2P_SET.INFRARED_SWITCH.INFRARED_SWITCH_ON;
				P2PHandler.getInstance().setInfraredSwitch(contact.contactId,
						contact.contactPassword, modify_infrared_state);
			} else {
				modify_infrared_state = Constants.P2P_SET.INFRARED_SWITCH.INFRARED_SWITCH_OFF;
				P2PHandler.getInstance().setInfraredSwitch(contact.contactId,
						contact.contactPassword, modify_infrared_state);
			}
        }else if(id==R.id.alarm_input_switch){
        	showProgress_wired_alarm_input();
    		if(isOpenWriedAlarmInput==true){
    			P2PHandler.getInstance().setWiredAlarmInput(contact.contactId,contact.contactPassword,1);
    		}else{
    			P2PHandler.getInstance().setWiredAlarmInput(contact.contactId,contact.contactPassword,0);
    		}
        }else if(id==R.id.alarm_out_switch){
        	showProgress_wired_alarm_out();
    		if(isOpenWriedAlarmOut==true){
    			P2PHandler.getInstance().setWiredAlarmOut(contact.contactId,contact.contactPassword,1);
    		}else{
    			P2PHandler.getInstance().setWiredAlarmOut(contact.contactId,contact.contactPassword,0);
    		}
        }else if(id==R.id.layout_alarm_switch){
        	showProgress_receive_alarm();
    		if(isReceiveAlarm==true){
    			if(last_bind_data.length>=max_alarm_count){
    				T.showShort(mContext, R.string.alarm_push_limit);
    				showImg_receive_alarm();
    				return;
    			}
    			new_data = new String[last_bind_data.length+1];
    			for(int i=0;i<last_bind_data.length;i++){
    				new_data[i] = last_bind_data[i];
    			}
    			new_data[new_data.length-1] = NpcCommon.mThreeNum;
//    			last_bind_data=new_data;
    			
    			P2PHandler.getInstance().setBindAlarmId(contact.contactId,contact.contactPassword, new_data.length, new_data);	
    		}else{
    			new_data = new String[last_bind_data.length-1];
    			int count=0;
    			for(int i=0;i<last_bind_data.length;i++){
    				if(!last_bind_data[i].equals(NpcCommon.mThreeNum)){
    					new_data[count]=last_bind_data[i];
    					count++;
    				}
    			}
    			if(new_data.length==0){
    				new_data=new String[]{"0"};
    			}
//    			last_bind_data=new_data;
    			P2PHandler.getInstance().setBindAlarmId(contact.contactId, contact.contactPassword,new_data.length,new_data);
    		}
        	
        	
        }else if(id==R.id.shield_alarm){
        	if(OpenShieldDevice==true){
    			List<AlarmMask> list = DataManager.findAlarmMaskByActiveUser(mContext, NpcCommon.mThreeNum);
    			Boolean isexist = false;
    			for(AlarmMask alarmMask : list){
    				if(contact.contactId.equals(alarmMask.deviceId)){
    					isexist = true;
    					break;
    				}
    			}
    			if(!isexist)
    			{
    				return;
    			}
    			DataManager.deleteAlarmMask(mContext, NpcCommon.mThreeNum, contact.contactId);
    			shield_img.setBackgroundResource(R.drawable.setting_status_off);
    			OpenShieldDevice=false;
    		}else{
    			List<AlarmMask> list = DataManager.findAlarmMaskByActiveUser(mContext, NpcCommon.mThreeNum);
    			Boolean isexist = false;
    			for(AlarmMask alarmMask : list){
    				if(contact.contactId.equals(alarmMask.deviceId)){
    					isexist = true;
    					break;
    				}
    			}
    			if(isexist)
    			{
    				return;
    			}
    			
    			AlarmMask alarmMask = new AlarmMask();
    			alarmMask.deviceId  = contact.contactId;
    			alarmMask.activeUser = NpcCommon.mThreeNum;
    			DataManager.insertAlarmMask(mContext, alarmMask);
    			shield_img.setBackgroundResource(R.drawable.setting_status_on);
    			OpenShieldDevice=true;
    		}
        }
      
		
		
	
		
		
		
		// TODO Auto-generated method stub
//		switch (view.getId()) {
//		case R.id.change_email:
//			Intent modify_email = new Intent(mContext,
//					ModifyBoundEmailActivity.class);
//			modify_email.putExtra("contact", contact);
//			modify_email.putExtra("email",email_text.getText().toString());
//			mContext.startActivity(modify_email);
//			break;
////		case R.id.add_alarm_item:
////			if (addBar.getItemCount() >= addBar.getMax_count()) {
////				T.showShort(
////						mContext,
////						mContext.getResources().getString(
////								R.string.add_item_warm1)
////								+ " "
////								+ addBar.getMax_count()
////								+ " "
////								+ mContext.getResources().getString(
////										R.string.add_item_warm2));
////			} else {
////				Intent modify_alarmId = new Intent(mContext,
////						ModifyAlarmIdActivity.class);
////				modify_alarmId.putExtra("contact", contact);
////				modify_alarmId.putExtra("data", last_bind_data);
////				mContext.startActivity(modify_alarmId);
////			}
////
////			break;
//		case R.id.add_alarm_item:
//			if(last_bind_data.length<=0||(last_bind_data[0].equals("0")&&last_bind_data.length==1)){
//				T.showShort(mContext,R.string.no_alarm_account);
//			}else{
//				Intent it=new Intent(mContext,AlarmPushAccountActivity.class);
//				it.putExtra("contactId",contact.contactId);
//				it.putExtra("contactPassword", contact.contactPassword);
//				startActivity(it);
//			}
//			break;
//		case R.id.change_buzzer:
//			showProgress();
//			if (buzzer_switch != Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_OFF) {
//				cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_OFF;
//			} else {
//				cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE;
//			}
//			P2PHandler.getInstance().setBuzzer(contact.contactId,
//					contact.contactPassword, cur_modify_buzzer_state);
//			break;
//		case R.id.change_motion:
//			showProgress_motion();
//			if (motion_switch != Constants.P2P_SET.MOTION_SET.MOTION_DECT_OFF) {
//				cur_modify_motion_state = Constants.P2P_SET.MOTION_SET.MOTION_DECT_OFF;
//				P2PHandler.getInstance().setMotion(contact.contactId,
//						contact.contactPassword, cur_modify_motion_state);
//			} else {
//				cur_modify_motion_state = Constants.P2P_SET.MOTION_SET.MOTION_DECT_ON;
//				P2PHandler.getInstance().setMotion(contact.contactId,
//						contact.contactPassword, cur_modify_motion_state);
//			}
//			break;
//		case R.id.radio_one:
//			showProgress();
//			cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE;
//			P2PHandler.getInstance().setBuzzer(contact.contactId,
//					contact.contactPassword, cur_modify_buzzer_state);
//			seek_alarmtime.setProgress(0);
//			break;
//		case R.id.radio_two:
//			showProgress();
//			cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_TWO_MINUTE;
//			P2PHandler.getInstance().setBuzzer(contact.contactId,
//					contact.contactPassword, cur_modify_buzzer_state);
//			seek_alarmtime.setProgress(1);
//			break;
//		case R.id.radio_three:
//			showProgress();
//			cur_modify_buzzer_state = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_THREE_MINUTE;
//			P2PHandler.getInstance().setBuzzer(contact.contactId,
//					contact.contactPassword, cur_modify_buzzer_state);
//			seek_alarmtime.setProgress(2);
//			break;
//		case R.id.change_pir:
//			showProgress_infrares_switch();
//			if (current_infrared_state==true) {
//				modify_infrared_state = Constants.P2P_SET.INFRARED_SWITCH.INFRARED_SWITCH_ON;
//				P2PHandler.getInstance().setInfraredSwitch(contact.contactId,
//						contact.contactPassword, modify_infrared_state);
//			} else {
//				modify_infrared_state = Constants.P2P_SET.INFRARED_SWITCH.INFRARED_SWITCH_OFF;
//				P2PHandler.getInstance().setInfraredSwitch(contact.contactId,
//						contact.contactPassword, modify_infrared_state);
//			}
//			break;
//		case R.id.alarm_input_switch:
//			showProgress_wired_alarm_input();
//			if(isOpenWriedAlarmInput==true){
//				P2PHandler.getInstance().setWiredAlarmInput(contact.contactId,contact.contactPassword,1);
//			}else{
//				P2PHandler.getInstance().setWiredAlarmInput(contact.contactId,contact.contactPassword,0);
//			}
//			break;
//		case R.id.alarm_out_switch:
//			showProgress_wired_alarm_out();
//			if(isOpenWriedAlarmOut==true){
//				P2PHandler.getInstance().setWiredAlarmOut(contact.contactId,contact.contactPassword,1);
//			}else{
//				P2PHandler.getInstance().setWiredAlarmOut(contact.contactId,contact.contactPassword,0);
//			}
//			break;
////		case R.id.img_receive_alarm:
////			if(isReceiveAlarm==true){
////				String[] new_data = new String[last_bind_data.length+1];
////				for(int i=0;i<last_bind_data.length;i++){
////					new_data[i] = last_bind_data[i];
////				}
////				new_data[new_data.length-1] = NpcCommon.mThreeNum;
//////				last_bind_data=new_data;
////				P2PHandler.getInstance().setBindAlarmId(contact.contactId,contact.contactPassword, new_data.length, new_data);	
////			}else{
////				int count=0;
////				String[] new_data = new String[last_bind_data.length-1];
////				for(int i=0;i<last_bind_data.length;i++){
////					if(!last_bind_data[i].equals(NpcCommon.mThreeNum)){
////						new_data[count]=last_bind_data[i];
////						count++;
////					}
////				}
////				if(new_data.length==0){
////					new_data=new String[]{"0"};
////				}
//////				last_bind_data=new_data;
////				P2PHandler.getInstance().setBindAlarmId(contact.contactId, contact.contactPassword,new_data.length,new_data);
////			}
////			Log.e("mThreeNum", NpcCommon.mThreeNum);
////			break;
//		case R.id.layout_alarm_switch:
//			showProgress_receive_alarm();
//			if(isReceiveAlarm==true){
//				if(last_bind_data.length>=max_alarm_count){
//					T.showShort(mContext, R.string.alarm_push_limit);
//					showImg_receive_alarm();
//					return;
//				}
//				new_data = new String[last_bind_data.length+1];
//				for(int i=0;i<last_bind_data.length;i++){
//					new_data[i] = last_bind_data[i];
//				}
//				new_data[new_data.length-1] = NpcCommon.mThreeNum;
////				last_bind_data=new_data;
//				
//				P2PHandler.getInstance().setBindAlarmId(contact.contactId,contact.contactPassword, new_data.length, new_data);	
//			}else{
//				new_data = new String[last_bind_data.length-1];
//				int count=0;
//				for(int i=0;i<last_bind_data.length;i++){
//					if(!last_bind_data[i].equals(NpcCommon.mThreeNum)){
//						new_data[count]=last_bind_data[i];
//						count++;
//					}
//				}
//				if(new_data.length==0){
//					new_data=new String[]{"0"};
//				}
////				last_bind_data=new_data;
//				P2PHandler.getInstance().setBindAlarmId(contact.contactId, contact.contactPassword,new_data.length,new_data);
//			}
//			break;
//		case R.id.shield_alarm:
//			if(OpenShieldDevice==true){
//				List<AlarmMask> list = DataManager.findAlarmMaskByActiveUser(mContext, NpcCommon.mThreeNum);
//				Boolean isexist = false;
//				for(AlarmMask alarmMask : list){
//					if(contact.contactId.equals(alarmMask.deviceId)){
//						isexist = true;
//						break;
//					}
//				}
//				if(!isexist)
//				{
//					return;
//				}
//				DataManager.deleteAlarmMask(mContext, NpcCommon.mThreeNum, contact.contactId);
//				shield_img.setBackgroundResource(R.drawable.setting_status_off);
//				OpenShieldDevice=false;
//			}else{
//				List<AlarmMask> list = DataManager.findAlarmMaskByActiveUser(mContext, NpcCommon.mThreeNum);
//				Boolean isexist = false;
//				for(AlarmMask alarmMask : list){
//					if(contact.contactId.equals(alarmMask.deviceId)){
//						isexist = true;
//						break;
//					}
//				}
//				if(isexist)
//				{
//					return;
//				}
//				
//				AlarmMask alarmMask = new AlarmMask();
//				alarmMask.deviceId  = contact.contactId;
//				alarmMask.activeUser = NpcCommon.mThreeNum;
//				DataManager.insertAlarmMask(mContext, alarmMask);
//				shield_img.setBackgroundResource(R.drawable.setting_status_on);
//				OpenShieldDevice=true;
//			}
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

	public void updateBuzzer(int state) {
		if (state == Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE) {
		
			buzzer_switch = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_ONE_MINUTE;
			buzzer_img.setBackgroundResource(R.drawable.setting_status_on);			
//			change_buzzer.setBackgroundResource(R.drawable.tiao_bg_up);
			buzzer_time.setVisibility(View.VISIBLE);
			radio_one.setChecked(true);
			seek_alarmtime.setProgress(0);
		} else if (state == Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_TWO_MINUTE) {
			
			buzzer_switch = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_TWO_MINUTE;
			buzzer_img.setBackgroundResource(R.drawable.setting_status_on);
//			change_buzzer.setBackgroundResource(R.drawable.tiao_bg_up);
			buzzer_time.setVisibility(View.VISIBLE);
			radio_two.setChecked(true);
			seek_alarmtime.setProgress(1);
		} else if (state == Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_THREE_MINUTE) {
		
			buzzer_switch = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_ON_THREE_MINUTE;
			buzzer_img.setBackgroundResource(R.drawable.setting_status_on);
//			change_buzzer.setBackgroundResource(R.drawable.tiao_bg_up);
			buzzer_time.setVisibility(View.VISIBLE);
			radio_three.setChecked(true);
			seek_alarmtime.setProgress(2);
		} else {
		
			buzzer_switch = Constants.P2P_SET.BUZZER_SET.BUZZER_SWITCH_OFF;
			buzzer_img.setBackgroundResource(R.drawable.setting_status_off);
//			change_buzzer.setBackgroundResource(R.drawable.tiao_bg_single);
			buzzer_time.setVisibility(View.GONE);
		}
	}

	public void showProgress() {
		progressBar.setVisibility(View.VISIBLE);
		buzzer_img.setVisibility(View.GONE);
		change_buzzer.setEnabled(false);
		radio_one.setEnabled(false);
		radio_two.setEnabled(false);
		radio_three.setEnabled(false);
	}

	public void showBuzzerTime() {
		progressBar.setVisibility(View.GONE);
		buzzer_img.setVisibility(View.VISIBLE);
		change_buzzer.setEnabled(true);
		radio_one.setEnabled(true);
		radio_two.setEnabled(true);
		radio_three.setEnabled(true);
	}

	public void showMotionState() {
		progressBar_motion.setVisibility(View.GONE);
		motion_img.setVisibility(View.VISIBLE);
		change_motion.setEnabled(true);
	}

	public void showProgress_motion() {
		progressBar_motion.setVisibility(View.VISIBLE);
		motion_img.setVisibility(View.GONE);
		change_motion.setEnabled(false);
	}

	public void showEmailState() {
		progressBar_email.setVisibility(View.GONE);
		email_text.setVisibility(View.VISIBLE);
		change_email.setEnabled(true);
	}

	public void showProgress_email() {
		progressBar_email.setVisibility(View.VISIBLE);
		email_text.setVisibility(View.GONE);
		change_email.setEnabled(false);
	}

	public void showAlarmIdState() {
//		progressBar_alarmId.setVisibility(RelativeLayout.GONE);
//		icon_add_alarm_id.setVisibility(RelativeLayout.VISIBLE);
//		add_alarm_item.setEnabled(true);
	}

	public void showProgress_alarmId() {
//		progressBar_alarmId.setVisibility(RelativeLayout.VISIBLE);
//		icon_add_alarm_id.setVisibility(RelativeLayout.GONE);
//		add_alarm_item.setEnabled(false);
	}

	public void showProgress_infrares_switch() {
		progressBar_pir.setVisibility(View.VISIBLE);
		pir_img.setVisibility(View.GONE);
	}

	public void showImg_infrared_switch() {
		progressBar_pir.setVisibility(View.GONE);
		pir_img.setVisibility(View.VISIBLE);
	}
	public void showProgress_wired_alarm_input(){
		progressBar_alarm_input.setVisibility(View.VISIBLE);
		img_alarm_input.setVisibility(View.GONE);
	}
	public void showImg_wired_alarm_input(){
		progressBar_alarm_input.setVisibility(View.GONE);
		img_alarm_input.setVisibility(View.VISIBLE);
	}
	public void showProgress_wired_alarm_out(){
		progressBar_alarm_out.setVisibility(View.VISIBLE);
		img_alarm_out.setVisibility(View.GONE);
	}
	public void showImg_wired_alarm_out(){
		progressBar_alarm_out.setVisibility(View.GONE);
		img_alarm_out.setVisibility(View.VISIBLE);
	}
	public void showProgress_receive_alarm(){
		progressBar_receive_alarm.setVisibility(View.VISIBLE);
		img_receive_alarm.setVisibility(View.GONE);
	}
	public void showImg_receive_alarm(){
		progressBar_receive_alarm.setVisibility(View.GONE);
		img_receive_alarm.setVisibility(View.VISIBLE);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent it=new Intent();
		it.setAction(Constants.Action.CONTROL_BACK);
		mContext.sendBroadcast(it);
	}
}
