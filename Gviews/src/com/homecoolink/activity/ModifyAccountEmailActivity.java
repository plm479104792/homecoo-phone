package com.homecoolink.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.homecoolink.R;
import com.homecoolink.entity.Account;
import com.homecoolink.global.AccountPersist;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.T;
import com.homecoolink.widget.MyInputDialog;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.network.NetManager;



public class ModifyAccountEmailActivity extends BaseActivity implements OnClickListener{
	Context mContext;
	ImageView mBack;
	EditText mEmail;
	Button mNext;
	NormalDialog dialog;
	RelativeLayout dialog_input_mask;
	MyInputDialog dialog_input;
	boolean isRegFilter = false;
	String old_email;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_account_email);
		
		mContext=this;
		initCompent();
		regFilter();
	}

	public void initCompent() {
		mBack=(ImageView)findViewById(R.id.back_btn);
		mNext=(Button)findViewById(R.id.next);
		mEmail = (EditText) findViewById(R.id.email);
		mNext.setOnClickListener(this);
		Account account = AccountPersist.getInstance().getActiveAccountInfo(mContext);
		old_email = account.email;
//		mEmail.addTextChangedListener(new TextWatcher(){
//
//			@Override
//			public void afterTextChanged(Editable arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1,
//					int arg2, int arg3) {
//				// TODO Auto-generated method stub
//			}
//
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
//					int arg3) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
		mEmail.setText(account.email);
		dialog_input_mask = (RelativeLayout) findViewById(R.id.dialog_input_mask);
		mBack.setOnClickListener(this);
	}
	
	
	public void regFilter(){
		IntentFilter filter = new IntentFilter();
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
		}
	};
	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.back_btn){
			finish();
		}else if(id==R.id.next){
	        String email = mEmail.getText().toString();
			
			Log.e("343", "email="+email+";old_email="+old_email);
			if("".equals(email.trim())&&!old_email.equals("")){
				showInputPwd(email);				
				return;
			}
			if("".equals(email.trim())&&old_email.equals("")) {
				T.showShort(mContext,mContext.getResources().getString(R.string.input_email));
				return;
			}
			
			
			
			if(email.length()>31||email.length()<5){
				T.showShort(this, R.string.email_too_long);
				return;
			}
			
			if(email.equals(old_email)){
//				mNext.setTextColor(R.color.gray);
				//mEmail.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
				
				T.showShort(ModifyAccountEmailActivity.this, R.string.modify_tip);
				return;
			}
//			else{
////				mNext.setTextColor(R.drawable.back);
//				//mEmail.setTextColor(mContext.getResources().getColor(R.color.text_color_white));
//				mNext.setOnClickListener(ModifyAccountEmailActivity.this);
//			}
			
			showInputPwd(email);
		}
		
		
		// TODO Auto-generated method stub
//		switch(v.getId()){
//		case R.id.back_btn:
//			finish();
//			break;
//		case R.id.next:
//			
//			String email = mEmail.getText().toString();
//			
//			Log.e("343", "email="+email+";old_email="+old_email);
//			if("".equals(email.trim())&&!old_email.equals("")){
//				showInputPwd(email);				
//				return;
//			}
//			if("".equals(email.trim())&&old_email.equals("")) {
//				T.showShort(mContext,mContext.getResources().getString(R.string.input_email));
//				return;
//			}
//			
//			
//			
//			if(email.length()>31||email.length()<5){
//				T.showShort(this, R.string.email_too_long);
//				return;
//			}
//			
//			if(email.equals(old_email)){
////				mNext.setTextColor(R.color.gray);
//				//mEmail.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
//				
//				T.showShort(ModifyAccountEmailActivity.this, R.string.modify_tip);
//				return;
//			}
////			else{
//////				mNext.setTextColor(R.drawable.back);
////				//mEmail.setTextColor(mContext.getResources().getColor(R.color.text_color_white));
////				mNext.setOnClickListener(ModifyAccountEmailActivity.this);
////			}
//			
//			showInputPwd(email);
//			break;
//			
//		}
	}
	
	public void showInputPwd(final String email){
		dialog_input = new MyInputDialog(mContext);
		dialog_input.setTitle(mContext.getResources().getString(R.string.change_email));
		dialog_input.setBtn1_str(mContext.getResources().getString(R.string.ensure));
		dialog_input.setBtn2_str(mContext.getResources().getString(R.string.cancel));
		dialog_input.setOnButtonOkListener(new MyInputDialog.OnButtonOkListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				String password = dialog_input.getInput1Text();
				if("".equals(password.trim())){
					T.showShort(mContext, R.string.input_login_pwd);
					return;
				}
				dialog_input.hide(dialog_input_mask);
				if(null==dialog){
					dialog = new NormalDialog(mContext,
							mContext.getResources().getString(R.string.verification),
							"","","");
					dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				}
				dialog.showDialog();
				new SetAccountInfoTask(password,email).execute();
			}
		});
		dialog_input.show(dialog_input_mask);
		dialog_input.setInput1HintText(R.string.input_login_pwd);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(null!=dialog_input&&dialog_input.isShowing()){
			dialog_input.hide(dialog_input_mask);
		}else{
			finish();
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(isRegFilter){
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}
	
	class SetAccountInfoTask extends AsyncTask{
		private String password;
		private String email;
		public SetAccountInfoTask(String password,String email){
			this.password = password;
			this.email = email;
		}
		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Account account = AccountPersist.getInstance().getActiveAccountInfo(mContext);
			return NetManager.getInstance(mContext).setAccountInfo(
					NpcCommon.mThreeNum,
					account.phone,
					email,
					account.countryCode,
					account.sessionId,
					password,
					"2",
					""
					);
		}
		
		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			
			int result = (Integer) object;
			switch(result){
			case NetManager.SESSION_ID_ERROR:
				Intent i = new Intent();
      		    i.setAction(Constants.Action.SESSION_ID_ERROR);
      		    MyApp.app.sendBroadcast(i);
				break;
			case NetManager.CONNECT_CHANGE:
				new SetAccountInfoTask(password,email).execute();
				return;
			case NetManager.SET_ACCOUNT_SUCCESS:
				if(null!=dialog&&dialog.isShowing()){
					dialog.dismiss();
					dialog = null;
				}
				
				Account account = AccountPersist.getInstance().getActiveAccountInfo(mContext);
				account.email = email;
				AccountPersist.getInstance().setActiveAccount(mContext, account);
				
				T.showShort(mContext, R.string.modify_success);
				finish();
				break;
			case NetManager.SET_ACCOUNT_PWD_ERROR:
				if(null!=dialog&&dialog.isShowing()){
					dialog.dismiss();
					dialog = null;
				}
				
				T.showShort(mContext, R.string.password_error);
				break;
			case NetManager.SET_ACCOUNT_EMAIL_USED:
				if(null!=dialog&&dialog.isShowing()){
					dialog.dismiss();
					dialog = null;
				}
				
				T.showShort(mContext, R.string.email_used);
				break;
			case NetManager.SET_ACCOUNT_EMAIL_FORMAT_ERROR:
				if(null!=dialog&&dialog.isShowing()){
					dialog.dismiss();
					dialog = null;
				}
				
				T.showShort(mContext, R.string.email_format_error);
				break;
			default:
				if(null!=dialog&&dialog.isShowing()){
					dialog.dismiss();
					dialog = null;
				}
				T.showShort(mContext, R.string.operator_error);
				break;
			}
		}
		
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_MODIFYACCOUNTEMAILACTIVITY;
	}
}
