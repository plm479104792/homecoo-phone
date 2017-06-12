package com.homecoolink.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.homecoolink.R;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.utils.T;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.P2PHandler;


public class ModifyBoundEmailActivity extends BaseActivity implements OnClickListener{
	Context mContext;
	Contact mContact;
	ImageView mBack;
	Button mSave;
	EditText mEmail;
	NormalDialog dialog;
	private boolean isRegFilter = false;
	String email;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_npc_bound_email);
		
		mContact = (Contact)getIntent().getSerializableExtra("contact");
		mContext=this;
		initCompent();
		regFilter();
	}

	public void initCompent() {
		mBack=(ImageView)findViewById(R.id.back_btn);
		mSave=(Button)findViewById(R.id.save);
		mEmail = (EditText) findViewById(R.id.email);
		
		mBack.setOnClickListener(this);
		mSave.setOnClickListener(this);
	}
	
	public void regFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.ACK_RET_SET_ALARM_EMAIL);
		filter.addAction(Constants.P2P.RET_SET_ALARM_EMAIL);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getAction().equals(Constants.P2P.RET_SET_ALARM_EMAIL)){
				int result = intent.getIntExtra("result", -1);
				if(null!=dialog&&dialog.isShowing()){
					dialog.dismiss();
					dialog = null;
				}
				if(result==Constants.P2P_SET.ALARM_EMAIL_SET.SETTING_SUCCESS){
					T.showShort(mContext, R.string.modify_success);
					finish();
				}else if(result==15){
					T.showShort(mContext, R.string.email_format_error);
				}else if(result==-1){
					T.showShort(mContext, R.string.operator_error);
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_ALARM_EMAIL)){
				int result = intent.getIntExtra("result", -1);
				if(null!=dialog&&dialog.isShowing()){
					dialog.dismiss();
					dialog = null;
				}
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					finish();
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					T.showShort(mContext, R.string.net_error_operator_fault);
				}
			}
		}
	};
	@Override
	public void onClick(View view) {
		int id=view.getId();
		if(id==R.id.back_btn){
			finish();
		}else if(id==R.id.save){
			email = mEmail.getText().toString();
			if("".equals(email.trim())){
//				T.showShort(mContext, R.string.input_email);
				P2PHandler.getInstance().setAlarmEmail(mContact.contactId, mContact.contactPassword, "0");
				return;
			}
			
			if(email.length()>31||email.length()<5){
				T.showShort(this, R.string.email_too_long);
				return; 
			}
			
			if(null==dialog){
				dialog = new NormalDialog(this,
						this.getResources().getString(R.string.verification),
						"","","");
				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
			}
			dialog.showDialog();
			
			P2PHandler.getInstance().setAlarmEmail(mContact.contactId, mContact.contactPassword, email);
		}
		
		// TODO Auto-generated method stub
//		switch(view.getId()){
//		case R.id.back_btn:
//			finish();
//			break;
//		case R.id.save:
//			email = mEmail.getText().toString();
//			if("".equals(email.trim())){
////				T.showShort(mContext, R.string.input_email);
//				P2PHandler.getInstance().setAlarmEmail(mContact.contactId, mContact.contactPassword, "0");
//				return;
//			}
//			
//			if(email.length()>31||email.length()<5){
//				T.showShort(this, R.string.email_too_long);
//				return; 
//			}
//			
//			if(null==dialog){
//				dialog = new NormalDialog(this,
//						this.getResources().getString(R.string.verification),
//						"","","");
//				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
//			}
//			dialog.showDialog();
//			
//			P2PHandler.getInstance().setAlarmEmail(mContact.contactId, mContact.contactPassword, email);
//			break;
//		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(isRegFilter){
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_MODIFYBOUNDEMAILACTIVITY;
	}
	
	
}
