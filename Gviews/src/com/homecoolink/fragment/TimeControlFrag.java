package com.homecoolink.fragment;

import java.util.Calendar;

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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.activity.MainControlActivity;
import com.homecoolink.adapter.DateNumericAdapter;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.thread.DelayThread;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.wheel.widget.OnWheelScrollListener;
import com.homecoolink.wheel.widget.WheelView;
import com.p2p.core.P2PHandler;


public class TimeControlFrag extends BaseFragment implements OnClickListener{
	private Context mContext;
	private Contact contact;
	private boolean isRegFilter = false;
	WheelView date_year,date_month,date_day,date_hour,date_minute,w_urban;
	
	RelativeLayout setting_time,setting_urban_title;
	TextView time_text;
	ProgressBar progressBar;
	
	String cur_modify_time;
	int current_urban;
	Button bt_set_timezone;
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
		View view = inflater.inflate(R.layout.fragment_time_control, container, false);
		initComponent(view);
		regFilter();
		
		P2PHandler.getInstance().getDeviceTime(contact.contactId, contact.contactPassword);
		P2PHandler.getInstance().getNpcSettings(contact.contactId, contact.contactPassword);
		return view;
	}
	
	public void initComponent(View view){
		Calendar calendar = Calendar.getInstance();
		setting_time = (RelativeLayout) view.findViewById(R.id.setting_time);
		time_text = (TextView) view.findViewById(R.id.time_text);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		setting_time.setEnabled(false);
		setting_time.setOnClickListener(this);
		
		// year
        int curYear = calendar.get(Calendar.YEAR);
		date_year = (WheelView) view.findViewById(R.id.date_year);
		date_year.setViewAdapter(new DateNumericAdapter(mContext, 2010, 2036));
		date_year.setCurrentItem(curYear-2010);
		date_year.addScrollingListener(scrolledListener);
		date_year.setCyclic(true);
		
		int curMonth = calendar.get(Calendar.MONTH)+1;
		date_month = (WheelView) view.findViewById(R.id.date_month);
		date_month.setViewAdapter(new DateNumericAdapter(mContext, 1, 12));
		date_month.setCurrentItem(curMonth-1);
		date_month.addScrollingListener(scrolledListener);
		date_month.setCyclic(true);
		
		int curDay = calendar.get(Calendar.DAY_OF_MONTH);
		date_day = (WheelView) view.findViewById(R.id.date_day);
		date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 31));
		date_day.setCurrentItem(curDay-1);
		date_day.addScrollingListener(scrolledListener);
		date_day.setCyclic(true);
		
		int curHour = calendar.get(Calendar.HOUR_OF_DAY);
		date_hour = (WheelView) view.findViewById(R.id.date_hour);
		date_hour.setViewAdapter(new DateNumericAdapter(mContext, 0, 23));
		date_hour.setCurrentItem(curHour);
		date_hour.setCyclic(true);
		
		int curMinute = calendar.get(Calendar.MINUTE);
		date_minute = (WheelView) view.findViewById(R.id.date_minute);
		date_minute.setViewAdapter(new DateNumericAdapter(mContext, 0, 59));
		date_minute.setCurrentItem(curMinute);
		date_minute.setCyclic(true);
		
		w_urban=(WheelView)view.findViewById(R.id.w_urban);
		w_urban.setViewAdapter(new DateNumericAdapter(mContext, -11, 12));
		w_urban.setCyclic(true);
		bt_set_timezone=(Button)view.findViewById(R.id.bt_set_timezone);
		bt_set_timezone.setOnClickListener(this);
		setting_urban_title=(RelativeLayout)view.findViewById(R.id.setting_urban_title);
		
	}
	
	
	private boolean wheelScrolled = false;
	  
	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
	      @Override
		public void onScrollingStarted(WheelView wheel) {
	          wheelScrolled = true;
	          updateStatus();
	      }
	      @Override
		public void onScrollingFinished(WheelView wheel) {
	          wheelScrolled = false;
	          updateStatus();
	      }
	  };

	
	public void updateStatus(){
		int year = date_year.getCurrentItem()+2010;
		int month = date_month.getCurrentItem()+1;
		
		if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
			date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 31));
		}else if(month==2){
			
			boolean isLeapYear = false;
			if(year%100==0){
				if(year%400==0){
					isLeapYear = true;
				}else{
					isLeapYear = false;
				}
			}else{
				if(year%4==0){
					isLeapYear = true;
				}else{
					isLeapYear = false;
				}
			}
			if(isLeapYear){
				if(date_day.getCurrentItem()>28){
					date_day.scroll(30, 2000);
	    		}
				date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 29));
			}else{
				if(date_day.getCurrentItem()>27){
					date_day.scroll(30, 2000);
	    		}
				date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 28));
			}
			
		}else{
			if(date_day.getCurrentItem()>29){
				date_day.scroll(30, 2000);
			}
			date_day.setViewAdapter(new DateNumericAdapter(mContext, 1, 30));
		}
		
	}
	
	public void regFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_SET_TIME);
		filter.addAction(Constants.P2P.ACK_RET_GET_TIME);
		filter.addAction(Constants.P2P.RET_SET_TIME);
		filter.addAction(Constants.P2P.RET_GET_TIME);
		filter.addAction(Constants.P2P.RET_GET_TIME_ZONE);
		filter.addAction(Constants.P2P.ACK_RET_SET_TIME_ZONE);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getAction().equals(Constants.P2P.RET_GET_TIME)){
				String time = intent.getStringExtra("time");
				time_text.setText(time);
				progressBar.setVisibility(View.GONE);
				time_text.setVisibility(View.VISIBLE);
				setting_time.setEnabled(true);
			}else if(intent.getAction().equals(Constants.P2P.RET_SET_TIME)){
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.DEVICE_TIME_SET.SETTING_SUCCESS){
					time_text.setText(cur_modify_time);
					progressBar.setVisibility(View.GONE);
					time_text.setVisibility(View.VISIBLE);
					setting_time.setEnabled(true);
					T.showShort(mContext, R.string.modify_success);
				}else{
					progressBar.setVisibility(View.GONE);
					time_text.setVisibility(View.VISIBLE);
					setting_time.setEnabled(true);
					T.showShort(mContext, R.string.operator_error);
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_GET_TIME)){
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
					
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					Log.e("my","net error resend:get npc time");
					P2PHandler.getInstance().getDeviceTime(contact.contactId, contact.contactPassword);
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_TIME)){
				
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					Log.e("my","net error resend:set npc time");
					P2PHandler.getInstance().setDeviceTime(contact.contactId, contact.contactPassword, cur_modify_time);
				}
				
			}else if(intent.getAction().equals(Constants.P2P.RET_GET_TIME_ZONE)){
				 int timezone=intent.getIntExtra("state", -1);
				 if(timezone!=-1){
					 setting_urban_title.setVisibility(View.VISIBLE);
				 }
				 w_urban.setCurrentItem(timezone);
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_TIME_ZONE)){
				int state=intent.getIntExtra("state",-1);
				if(state==Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS){
					T.showShort(mContext,R.string.timezone_success);
					P2PHandler.getInstance().getDeviceTime(contact.contactId, contact.contactPassword);
				}else if(state==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					P2PHandler.getInstance().setTimeZone(contact.contactId,contact.contactName,current_urban);
				}
			}
		}
	};
	
	@Override
	public void onClick(View view) {
		int id=view.getId();
		if(id==R.id.setting_time){
			progressBar.setVisibility(View.VISIBLE);
			time_text.setVisibility(View.GONE);
			setting_time.setEnabled(false);
			new DelayThread(Constants.SettingConfig.SETTING_CLICK_TIME_DELAY,new DelayThread.OnRunListener() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					cur_modify_time = Utils.convertDeviceTime(
							date_year.getCurrentItem()+10,
							date_month.getCurrentItem()+1,
							date_day.getCurrentItem()+1,
							date_hour.getCurrentItem(), 
							date_minute.getCurrentItem());
					P2PHandler.getInstance().setDeviceTime(contact.contactId, contact.contactPassword, cur_modify_time);
				}
			}).start();
		}else if(id==R.id.bt_set_timezone){
			current_urban=w_urban.getCurrentItem();
			P2PHandler.getInstance().setTimeZone(contact.contactId, contact.contactPassword, current_urban);	
		}
		
		
		// TODO Auto-generated method stub
//		switch(view.getId()){
//		case R.id.setting_time:
//			progressBar.setVisibility(View.VISIBLE);
//			time_text.setVisibility(View.GONE);
//			setting_time.setEnabled(false);
//			new DelayThread(Constants.SettingConfig.SETTING_CLICK_TIME_DELAY,new DelayThread.OnRunListener() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					cur_modify_time = Utils.convertDeviceTime(
//							date_year.getCurrentItem()+10,
//							date_month.getCurrentItem()+1,
//							date_day.getCurrentItem()+1,
//							date_hour.getCurrentItem(), 
//							date_minute.getCurrentItem());
//					P2PHandler.getInstance().setDeviceTime(contact.contactId, contact.contactPassword, cur_modify_time);
//				}
//			}).start();
//			break;
//		case R.id.bt_set_timezone:
//			current_urban=w_urban.getCurrentItem();
//			P2PHandler.getInstance().setTimeZone(contact.contactId, contact.contactPassword, current_urban);	
//		    break;
//		}
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if(isRegFilter){
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent it=new Intent();
		it.setAction(Constants.Action.CONTROL_BACK);
		mContext.sendBroadcast(it);
	}
	
}
