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
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.P2PHandler;

public class ModifyNpcVisitorPasswordActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private Contact mContact;
	EditText et_pwd;
	Button msave;
	ImageView back_bt;
	boolean isRegFilter=false;
	NormalDialog dialog;
    @Override
    protected void onCreate(Bundle arg0) {
    	super.onCreate(arg0);
        setContentView(R.layout.modify_npc_visitor_pwd);
		mContext=this;
		mContact = (Contact)getIntent().getSerializableExtra("contact");
		initComponent();
		regFilter();
    }
    public void regFilter(){
          IntentFilter filter=new IntentFilter();
          filter.addAction(Constants.P2P.RET_SET_VISITOR_DEVICE_PASSWORD);
          filter.addAction(Constants.P2P.ACK_RET_SET_VISITOR_DEVICE_PASSWORD);
          mContext.registerReceiver(br, filter);
          isRegFilter=true;
    }
    BroadcastReceiver br=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getAction().equals(Constants.P2P.RET_SET_VISITOR_DEVICE_PASSWORD)){
				int result = intent.getIntExtra("result", -1);
				if(dialog!=null){
					dialog.dismiss();
					dialog = null;
				}
				if(result==Constants.P2P_SET.DEVICE_VISITOR_PASSWORD_SET.SETTING_SUCCESS){
					T.showShort(mContext, R.string.modify_success);
					finish();
				}else{
					T.showShort(mContext, R.string.operator_error);
				}
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_VISITOR_DEVICE_PASSWORD)){
				int result = intent.getIntExtra("state", -1);
				if(result==Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR){
					if(dialog!=null){
						dialog.dismiss();
						dialog = null;
					}
					T.showShort(mContext, R.string.old_pwd_error);
				}else if(result==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					T.showShort(mContext, R.string.net_error_operator_fault);
				}
			}
			
		}
	};
    public void initComponent(){
    	et_pwd=(EditText)findViewById(R.id.et_pwd);
    	msave=(Button)findViewById(R.id.save);
    	back_bt=(ImageView)findViewById(R.id.back_btn);
    	msave.setOnClickListener(this);
    	back_bt.setOnClickListener(this);
    }
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_MODIFY_NPC_VISITOR_PASSWORD_ACTIVITY;
	}
	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.back_btn){
			  finish();
		}else if(id== R.id.save){
			String visitor_password=et_pwd.getText().toString();
			if("".equals(visitor_password.trim())){
				T.showShort(mContext, R.string.input_visitor_pwd);
				return;
			}
			if(visitor_password.length()>9){
				T.showShort(mContext, R.string.visitor_pwd_to_long);				
				return;
			}
			if(!Utils.isNumeric(visitor_password)||visitor_password.charAt(0)=='0'){
				T.showShort(mContext,  R.string.visitor_pwd_must_digit);
				return;
			}
			if(visitor_password.equals(mContact.contactPassword)){
				
				T.showShort(mContext,  R.string.visitor_pwdequalspwd_error);
				return;
			}
			if(null==dialog){
				dialog = new NormalDialog(this,
						this.getResources().getString(R.string.verification),
						"","","");
				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
			}
			dialog.showDialog();
			P2PHandler.getInstance().setDeviceVisitorPassword(mContact.contactId,mContact.contactPassword, visitor_password);
		}
		
//		switch (v.getId()) {
//		case R.id.back_btn:
//		    finish();
//			break;
//		case R.id.save:
//			String visitor_password=et_pwd.getText().toString();
//			if("".equals(visitor_password.trim())){
//				T.showShort(mContext, R.string.input_visitor_pwd);
//				return;
//			}
//			if(visitor_password.length()>9){
//				T.showShort(mContext, R.string.visitor_pwd_to_long);				
//				return;
//			}
//			if(!Utils.isNumeric(visitor_password)||visitor_password.charAt(0)=='0'){
//				T.showShort(mContext,  R.string.visitor_pwd_must_digit);
//				return;
//			}
//			if(visitor_password.equals(mContact.contactPassword)){
//				
//				T.showShort(mContext,  R.string.visitor_pwdequalspwd_error);
//				return;
//			}
//			if(null==dialog){
//				dialog = new NormalDialog(this,
//						this.getResources().getString(R.string.verification),
//						"","","");
//				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
//			}
//			dialog.showDialog();
//			P2PHandler.getInstance().setDeviceVisitorPassword(mContact.contactId,mContact.contactPassword, visitor_password);
//			
//			break;
//		default:
//			break;
//		}
		
	}
  @Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	if(isRegFilter){
		mContext.unregisterReceiver(br);
		isRegFilter = false;
	}
}
}
