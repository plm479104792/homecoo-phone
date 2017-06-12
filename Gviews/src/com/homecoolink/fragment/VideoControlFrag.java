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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.homecoolink.R;
import com.homecoolink.activity.MainControlActivity;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.thread.DelayThread;
import com.homecoolink.utils.T;
import com.p2p.core.P2PHandler;

public class VideoControlFrag extends BaseFragment implements OnClickListener{
	private Context mContext;
	private Contact contact;
	private boolean isRegFilter = false;
	RelativeLayout change_video_format,change_volume,video_voleme_seek,layout_reverse;
	LinearLayout video_format_radio;
	ProgressBar progressBar_video_format,progressBar_volume;
	RadioButton radio_one,radio_two;
	SeekBar seek_volume;
	int cur_modify_video_format;
	int cur_modify_video_volume;
	RelativeLayout change_image_reverse;
	ImageView img_image_reverse;
	ProgressBar progressbar_image_reverse;
	boolean isOpenReverse=true;
	boolean isOpenModify;
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
		View view = inflater.inflate(R.layout.fragment_video_control, container, false);
		initComponent(view);
		regFilter();
		showProgress_video_format();
		showProgress_volume();
		showProgress_image_reverse();
		P2PHandler.getInstance().getNpcSettings(contact.contactId, contact.contactPassword);
		return view;
	}
	
	
	public void initComponent(View view){
		change_video_format = (RelativeLayout) view.findViewById(R.id.change_video_format);
		video_format_radio = (LinearLayout) view.findViewById(R.id.video_format_radio);
		progressBar_video_format = (ProgressBar) view.findViewById(R.id.progressBar_video_format);
		
		change_volume = (RelativeLayout) view.findViewById(R.id.change_volume);
		video_voleme_seek = (RelativeLayout) view.findViewById(R.id.video_voleme_seek);
		progressBar_volume = (ProgressBar) view.findViewById(R.id.progressBar_volume);
		
		seek_volume = (SeekBar) view.findViewById(R.id.seek_volume);
		radio_one = (RadioButton)view.findViewById(R.id.radio_one);
		radio_two = (RadioButton)view.findViewById(R.id.radio_two);
		
		change_image_reverse=(RelativeLayout)view.findViewById(R.id.change_image_reverse);
		img_image_reverse=(ImageView)view.findViewById(R.id.image_reverse_img);
		progressbar_image_reverse=(ProgressBar)view.findViewById(R.id.progressBar_image_reverse);
		layout_reverse=(RelativeLayout)view.findViewById(R.id.change_image_reverse);
		radio_one.setOnClickListener(this);
		radio_two.setOnClickListener(this);
		change_image_reverse.setOnClickListener(this);
		seek_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0,int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				final int value = arg0.getProgress();
				progressBar_volume.setVisibility(View.VISIBLE);
				seek_volume.setEnabled(false);
				
				cur_modify_video_volume = value;
				switchVideoVolume(value);
			}
			
		});
	}
	
	public void regFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_GET_NPC_SETTINGS);
		
		filter.addAction(Constants.P2P.ACK_RET_SET_VIDEO_FORMAT);
		filter.addAction(Constants.P2P.RET_SET_VIDEO_FORMAT);
		filter.addAction(Constants.P2P.RET_GET_VIDEO_FORMAT);
		
		filter.addAction(Constants.P2P.ACK_RET_SET_VIDEO_VOLUME);
		filter.addAction(Constants.P2P.RET_SET_VIDEO_VOLUME);
		filter.addAction(Constants.P2P.RET_GET_VIDEO_VOLUME);
		filter.addAction(Constants.P2P.RET_GET_IMAGE_REVERSE);
		filter.addAction(Constants.P2P.ACK_VRET_SET_IMAGEREVERSE);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getAction().equals(Constants.P2P.RET_GET_VIDEO_FORMAT)){
				int type = intent.getIntExtra("type", -1);
				if(type==Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL){
					radio_one.setChecked(true);
					RadioGroup rg = (RadioGroup)radio_one.getParent();
					rg.setBackgroundResource(R.drawable.pal_open);
					
				}else if(type==Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC){
					radio_two.setChecked(true);
					RadioGroup rg = (RadioGroup)radio_two.getParent();
					rg.setBackgroundResource(R.drawable.ntsc_open);
				}
				showVideoFormat();
				radio_one.setEnabled(true);
				radio_two.setEnabled(true);
			}else if(intent.getAction().equals(Constants.P2P.RET_SET_VIDEO_FORMAT)){
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.VIDEO_FORMAT_SET.SETTING_SUCCESS){
					if(cur_modify_video_format==Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL){
						radio_one.setChecked(true);
						RadioGroup rg = (RadioGroup)radio_one.getParent();
						rg.setBackgroundResource(R.drawable.pal_open);
					}else if(cur_modify_video_format==Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC){
						radio_two.setChecked(true);
						RadioGroup rg = (RadioGroup)radio_two.getParent();
						rg.setBackgroundResource(R.drawable.ntsc_open);
					}
					showVideoFormat();
					radio_one.setEnabled(true);
					radio_two.setEnabled(true);
					T.showShort(mContext, R.string.modify_success);
				}else{
					showVideoFormat();
					radio_one.setEnabled(true);
					radio_two.setEnabled(true);
					T.showShort(mContext, R.string.operator_error);
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_GET_VIDEO_VOLUME)){
				int value = intent.getIntExtra("value", 0);
				seek_volume.setProgress(value);
				seek_volume.setEnabled(true);
				showVideoVolume();
			}else if(intent.getAction().equals(Constants.P2P.RET_SET_VIDEO_VOLUME)){
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.VIDEO_VOLUME_SET.SETTING_SUCCESS){
					seek_volume.setProgress(cur_modify_video_volume);
					seek_volume.setEnabled(true);
					showVideoVolume();
					T.showShort(mContext, R.string.modify_success);
				}else{
					seek_volume.setEnabled(true);
					showVideoVolume();
					T.showShort(mContext, R.string.operator_error);
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_GET_NPC_SETTINGS)){
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					Log.e("my","net error resend:get npc settings");
					P2PHandler.getInstance().getNpcSettings(contact.contactId, contact.contactPassword);
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_VIDEO_FORMAT)){
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					Log.e("my","net error resend:set npc settings video format");
					switchVideoFormat(cur_modify_video_format);
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_VIDEO_VOLUME)){
				
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					Log.e("my","net error resend:set npc settings video volume");
					switchVideoVolume(cur_modify_video_volume);
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_GET_IMAGE_REVERSE)){
				 int type=intent.getIntExtra("type",-1);
				 if(type==0){
					 layout_reverse.setVisibility(View.VISIBLE);
					 showImageview_image_reverse();
					 img_image_reverse.setBackgroundResource(R.drawable.setting_status_off);
					 isOpenReverse=true;
				 }else if(type==1){
					 layout_reverse.setVisibility(View.VISIBLE);
					 showImageview_image_reverse();
					 img_image_reverse.setBackgroundResource(R.drawable.setting_status_on);
					 isOpenReverse=false;
				 }
			}else if(intent.getAction().equals(Constants.P2P.ACK_VRET_SET_IMAGEREVERSE)){
				int result=intent.getIntExtra("result",-1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					if(isOpenReverse=true){
						P2PHandler.getInstance().setImageReverse(contact.contactId, contact.contactPassword, 1);
					}else{
						P2PHandler.getInstance().setImageReverse(contact.contactId, contact.contactPassword, 0);
					}
					
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS){
					if(isOpenReverse==true){
						showImageview_image_reverse();
						img_image_reverse.setBackgroundResource(R.drawable.setting_status_on);
						isOpenReverse=false;
					}else{
						showImageview_image_reverse();
						img_image_reverse.setBackgroundResource(R.drawable.setting_status_off);
						isOpenReverse=true;
					}
				}
			}
		}
	};
	
	
	@Override
	public void onClick(View view) {
		int id=view.getId();
		if(id==R.id.radio_one){
			progressBar_video_format.setVisibility(View.VISIBLE);
			radio_one.setEnabled(false);
			radio_two.setEnabled(false);
			RadioGroup rg1 = (RadioGroup)radio_one.getParent();
			rg1.setBackgroundResource(R.drawable.pal_open);
			cur_modify_video_format = Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL;
			switchVideoFormat(Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL);
		}else if(id== R.id.radio_two){
			progressBar_video_format.setVisibility(View.VISIBLE);
			radio_one.setEnabled(false);
			radio_two.setEnabled(false);
			RadioGroup rg2 = (RadioGroup)radio_two.getParent();
			rg2.setBackgroundResource(R.drawable.ntsc_open);
			cur_modify_video_format = Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC;
			switchVideoFormat(Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC);
		}else if(id==R.id.change_image_reverse){
			showProgress_image_reverse();
			if(isOpenReverse==true){
				P2PHandler.getInstance().setImageReverse(contact.contactId, contact.contactPassword, 1);
			}else{
				P2PHandler.getInstance().setImageReverse(contact.contactId, contact.contactPassword, 0);
			}
		}
		
		
		// TODO Auto-generated method stub
//		switch(view.getId()){
//		case R.id.radio_one:
//			progressBar_video_format.setVisibility(View.VISIBLE);
//			radio_one.setEnabled(false);
//			radio_two.setEnabled(false);
//			RadioGroup rg1 = (RadioGroup)radio_one.getParent();
//			rg1.setBackgroundResource(R.drawable.pal_open);
//			cur_modify_video_format = Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL;
//			switchVideoFormat(Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_PAL);
//			
//			break;
//		case R.id.radio_two:
//			progressBar_video_format.setVisibility(View.VISIBLE);
//			radio_one.setEnabled(false);
//			radio_two.setEnabled(false);
//			RadioGroup rg2 = (RadioGroup)radio_two.getParent();
//			rg2.setBackgroundResource(R.drawable.ntsc_open);
//			cur_modify_video_format = Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC;
//			switchVideoFormat(Constants.P2P_SET.VIDEO_FORMAT_SET.VIDEO_FORMAT_NTSC);
//			break;
//		case R.id.change_image_reverse:
//			showProgress_image_reverse();
//			if(isOpenReverse==true){
//				P2PHandler.getInstance().setImageReverse(contact.contactId, contact.contactPassword, 1);
//			}else{
//				P2PHandler.getInstance().setImageReverse(contact.contactId, contact.contactPassword, 0);
//			}
//			break;
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
	
	
	public void switchVideoVolume(final int toggle){
		
		new DelayThread(Constants.SettingConfig.SETTING_CLICK_TIME_DELAY,new DelayThread.OnRunListener() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				P2PHandler.getInstance().setVideoVolume(contact.contactId, contact.contactPassword, toggle);
				
			}
		}).start();
	}
	
	public void switchVideoFormat(final int toggle){
		new DelayThread(Constants.SettingConfig.SETTING_CLICK_TIME_DELAY,new DelayThread.OnRunListener() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				P2PHandler.getInstance().setVideoFormat(contact.contactId, contact.contactPassword, toggle);
			}
		}).start();
	}
	public void showProgress_image_reverse(){
		progressbar_image_reverse.setVisibility(View.VISIBLE);
		img_image_reverse.setVisibility(View.GONE);
	}
	public void showImageview_image_reverse(){
		progressbar_image_reverse.setVisibility(View.GONE);
		img_image_reverse.setVisibility(View.VISIBLE);
	}
	public void showVideoFormat(){
//		change_video_format.setBackgroundResource(R.drawable.tiao_bg_up);
		progressBar_video_format.setVisibility(View.GONE);
		video_format_radio.setVisibility(View.VISIBLE);
	}
	
	public void showProgress_video_format(){
//		change_video_format.setBackgroundResource(R.drawable.tiao_bg_single);
		progressBar_video_format.setVisibility(View.VISIBLE);
		video_format_radio.setVisibility(View.GONE);
	}
	
	public void showVideoVolume(){
//		change_volume.setBackgroundResource(R.drawable.tiao_bg_up);
		video_voleme_seek.setVisibility(View.VISIBLE);
		progressBar_volume.setVisibility(View.GONE);
		seek_volume.setEnabled(true);
	}
	
	public void showProgress_volume(){
		progressBar_volume.setVisibility(View.VISIBLE);
		seek_volume.setEnabled(false);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent it=new Intent();
		it.setAction(Constants.Action.CONTROL_BACK);
		mContext.sendBroadcast(it);
	}
}
