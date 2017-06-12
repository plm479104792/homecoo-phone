package com.homecoolink.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.utils.T;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;


public class DeviceUpdateActivity extends BaseActivity{
	Context mContext;
	TextView content_text,button1_text,button2_text;
	RelativeLayout layout_button1,layout_button2,yao;
	LinearLayout layout_main;
	Contact mContact;
	ProgressBar content_progress;
	boolean isRegFilter;
	boolean isDownloading = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_update);
		mContext = this;
		mContact = (Contact) this.getIntent().getSerializableExtra("contact");
		initCompoment();
		regFilter();
		P2PHandler.getInstance().checkDeviceUpdate(mContact.contactId, mContact.contactPassword);
	}
	
	public void initCompoment(){
		yao = (RelativeLayout) findViewById(R.id.yao);		
		layout_main = (LinearLayout) findViewById(R.id.layout_main);
		layout_button1 = (RelativeLayout) findViewById(R.id.layout_button1);
		layout_button2 = (RelativeLayout) findViewById(R.id.layout_button2);
		content_text = (TextView) findViewById(R.id.content_text);
		button1_text = (TextView) findViewById(R.id.button1_text);
		button2_text = (TextView) findViewById(R.id.button2_text);
		content_progress = (ProgressBar) findViewById(R.id.content_progress);
		content_progress.setVisibility(View.VISIBLE);
		layout_button1.setVisibility(View.VISIBLE);
		layout_button2.setVisibility(View.GONE);
		
		button1_text.setText(R.string.cancel);
		button1_text.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FrameLayout fl = ((FrameLayout)(v.getParent().getParent().getParent().getParent()));
				RelativeLayout rl = (RelativeLayout) fl.findViewById(R.id.yao);
				rl.setVisibility(View.GONE);
				finish();
			}
		});
		
		Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.scale_in);
		layout_main.startAnimation(anim);
	}
	
	public void regFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_CHECK_DEVICE_UPDATE);
		filter.addAction(Constants.P2P.RET_CHECK_DEVICE_UPDATE);
		
		filter.addAction(Constants.P2P.ACK_RET_DO_DEVICE_UPDATE);
		filter.addAction(Constants.P2P.RET_DO_DEVICE_UPDATE);
		
		filter.addAction(Constants.P2P.ACK_RET_CANCEL_DEVICE_UPDATE);
		filter.addAction(Constants.P2P.RET_CANCEL_DEVICE_UPDATE);
		
		filter.addAction(Constants.P2P.RET_DEVICE_NOT_SUPPORT);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getAction().equals(Constants.P2P.ACK_RET_CHECK_DEVICE_UPDATE)){
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					T.showShort(mContext, R.string.password_error);
					yao.setVisibility(View.GONE);
					finish();
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					Log.e("my","net error resend:check device update");
					P2PHandler.getInstance().checkDeviceUpdate(mContact.contactId, mContact.contactPassword);
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_CHECK_DEVICE_UPDATE)){
				int result = intent.getIntExtra("result", -1);
				String cur_version = intent.getStringExtra("cur_version");
				String upg_version = intent.getStringExtra("upg_version");
				content_progress.setVisibility(View.GONE);
				content_text.setVisibility(View.VISIBLE);
				if(result==Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_VERSION){
					//content_text.setText(mContext.getResources().getString(R.string.update_device_confirm)+" "+version);
					content_text.setText(
							mContext.getResources().getString(R.string.current_version_is)
									+cur_version+","
									+mContext.getResources().getString(R.string.can_update_to)+upg_version);
					layout_button1.setVisibility(View.VISIBLE);
					layout_button2.setVisibility(View.VISIBLE);
					button1_text.setText(R.string.update_now);
					button2_text.setText(R.string.next_time);
					button2_text.setOnClickListener(new OnClickListener(){
						

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							FrameLayout fl = ((FrameLayout)(arg0.getParent().getParent().getParent().getParent()));
							RelativeLayout rl = (RelativeLayout) fl.findViewById(R.id.yao);
							rl.setVisibility(View.GONE);
							finish();
						}
					});
					
					button1_text.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							layout_button1.setVisibility(View.VISIBLE);
							layout_button2.setVisibility(View.GONE);
							content_progress.setVisibility(View.VISIBLE);
							content_text.setVisibility(View.GONE);
							button1_text.setText(R.string.cancel);
							content_text.setText(mContext.getResources().getString(R.string.down_device_update)+"0%");
							button1_text.setOnClickListener(new OnClickListener(){

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									FrameLayout fl = ((FrameLayout)(v.getParent().getParent().getParent().getParent()));
									RelativeLayout rl = (RelativeLayout) fl.findViewById(R.id.yao);
									rl.setVisibility(View.GONE);
									finish();
								}
								
								
							});
							P2PHandler.getInstance().doDeviceUpdate(mContact.contactId, mContact.contactPassword);
						}
						
						
					});
					
				}else if(result==Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_IN_SD){
					content_progress.setVisibility(View.GONE);
					content_text.setText(
							mContext.getResources().getString(R.string.current_version_is)
									+cur_version+","
									+mContext.getResources().getString(R.string.can_update_in_sd));
					
					layout_button1.setVisibility(View.VISIBLE);
					layout_button2.setVisibility(View.VISIBLE);
					button1_text.setText(R.string.update_now);
					button2_text.setText(R.string.next_time);
					button2_text.setOnClickListener(new OnClickListener(){
						

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							FrameLayout fl = ((FrameLayout)(arg0.getParent().getParent().getParent().getParent()));
							RelativeLayout rl = (RelativeLayout) fl.findViewById(R.id.yao);
							rl.setVisibility(View.GONE);
							finish();
						}
					});
					
					button1_text.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							layout_button1.setVisibility(View.VISIBLE);
							layout_button2.setVisibility(View.GONE);
							content_progress.setVisibility(View.VISIBLE);
							content_text.setVisibility(View.GONE);
							button1_text.setText(R.string.cancel);
							button1_text.setOnClickListener(new OnClickListener(){
							
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									FrameLayout fl = ((FrameLayout)(v.getParent().getParent().getParent().getParent()));
									RelativeLayout rl = (RelativeLayout) fl.findViewById(R.id.yao);
									rl.setVisibility(View.GONE);
									finish();
								}
								
								
							});
							P2PHandler.getInstance().doDeviceUpdate(mContact.contactId, mContact.contactPassword);
						}
						
						
					});
				}else if(result==Constants.P2P_SET.DEVICE_UPDATE.IS_LATEST_VERSION){
					T.showShort(mContext, mContext.getResources().getString(R.string.device_is_latest_version)+":"+cur_version);
					yao.setVisibility(View.GONE);
					finish();
				}else if(result==Constants.P2P_SET.DEVICE_UPDATE.OTHER_WAS_CHECKING){
					T.showShort(mContext, R.string.other_was_checking);
					yao.setVisibility(View.GONE);
					finish();
				}else{
					T.showShort(mContext, R.string.operator_error);
					yao.setVisibility(View.GONE);
					finish();
					
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_DO_DEVICE_UPDATE)){
				int result = intent.getIntExtra("result", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					T.showShort(mContext, R.string.password_error);
					yao.setVisibility(View.GONE);
					finish();
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					Log.e("my","net error resend:do device update");
					P2PHandler.getInstance().doDeviceUpdate(mContact.contactId, mContact.contactPassword);
				}
			}else if(intent.getAction().equals(Constants.P2P.RET_DO_DEVICE_UPDATE)){
				int result = intent.getIntExtra("result", -1);
				int value = intent.getIntExtra("value", -1);
				Log.e("my",result+":"+value);
				if(result==P2PValue.UpdateState.STATE_UPDATING){
					content_progress.setVisibility(View.GONE);
					content_text.setVisibility(View.VISIBLE);
					content_text.setText(mContext.getResources().getString(R.string.down_device_update)+value+"%");
				}else if(result==P2PValue.UpdateState.STATE_UPDATE_SUCCESS){
					T.showShort(mContext, R.string.start_update);
					yao.setVisibility(View.GONE);
					finish();
				}else{
					T.showShort(mContext, R.string.update_failed);
					yao.setVisibility(View.GONE);
					finish();
				}
			}
		}
	};
	
	@Override
	public void finish(){
		Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.scale_out);
		anim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				DeviceUpdateActivity.super.finish();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		layout_main.startAnimation(anim);
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(isRegFilter){
			isRegFilter = false;
			this.unregisterReceiver(mReceiver);
		}
		P2PHandler.getInstance().cancelDeviceUpdate(mContact.contactId, mContact.contactPassword);
	}
	
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_DEVICE_UPDATE;
	}

}
