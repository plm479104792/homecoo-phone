package com.homecoolink.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.homecoolink.R;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class ForgetPasswordEdit extends Activity implements OnClickListener {
	public ImageView back;
	public RelativeLayout clear1, clear2;

	public EditText password, cpassword;
	protected Button okbtn;
	boolean isDialogCanel = false;
	private Context context;
	NormalDialog dialog;
	private String vkey="",ID="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		vkey = getIntent().getStringExtra("VKEY");
		ID= getIntent().getStringExtra("ID");
		initComponent();
	}

	private void initComponent() {
		setContentView(R.layout.activity_forgetpasswordedit);
		back = (ImageView) findViewById(R.id.back_btn);
		clear1 = (RelativeLayout) findViewById(R.id.password_clear);
		clear2 = (RelativeLayout) findViewById(R.id.cpassword_clear);
		password = (EditText)findViewById(R.id.password);
		cpassword = (EditText)findViewById(R.id.cpassword);
		back.setOnClickListener(this);
		clear1.setOnClickListener(this);
		clear2.setOnClickListener(this);
		(okbtn =  (Button) findViewById(R.id.okbtn)).setOnClickListener(this);;
	}


	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.back_btn){
			finish();
		}else if(id==R.id.password_clear){
			password.setText("");
		}else if(id==R.id.cpassword_clear){
			cpassword.setText("");
		}else if(id==R.id.okbtn){
			if(!password.getText().toString().trim().equals("") && !cpassword.getText().toString().trim().equals(""))
			{
				if(!password.getText().toString().trim().equals(cpassword.getText().toString().trim()))
				{
					T.showLong(context, Utils.getResString(context, R.string.forgetpassword_tip6));
					return;
				}
				dialog = new NormalDialog(context, context.getResources().getString(
						R.string.waiting_verify_code), "", "", "");
				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
					
						isDialogCanel = true;
					}

				});
				isDialogCanel = false;
				dialog.showDialog();
				new UpdatePwd(password.getText().toString()).execute();
			}
			else {
				
				T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip2));
			}
		}
		
		
//		switch (v.getId()) {
//		case R.id.back_btn:
//			finish();
//			break;
//		case R.id.password_clear:
//			password.setText("");
//			break;
//		case R.id.cpassword_clear:
//			cpassword.setText("");
//			break;
//		
//		case R.id.okbtn:
//			if(!password.getText().toString().trim().equals("") && !cpassword.getText().toString().trim().equals(""))
//			{
//				if(!password.getText().toString().trim().equals(cpassword.getText().toString().trim()))
//				{
//					T.showLong(context, Utils.getResString(context, R.string.forgetpassword_tip6));
//					return;
//				}
//				dialog = new NormalDialog(context, context.getResources().getString(
//						R.string.waiting_verify_code), "", "", "");
//				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
//				dialog.setOnCancelListener(new OnCancelListener() {
//
//					@Override
//					public void onCancel(DialogInterface dialog) {
//					
//						isDialogCanel = true;
//					}
//
//				});
//				isDialogCanel = false;
//				dialog.showDialog();
//				new UpdatePwd(password.getText().toString()).execute();
//			}
//			else {
//				
//				T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip2));
//			}
//			break;
//		default:
//			break;
//		}

	}
	
	
	

	
	
	
	class UpdatePwd extends AsyncTask<Object, Object, Object> {
		String pwd;

		public UpdatePwd(String p) {
			this.pwd = p;

		}

		@Override
		protected Object doInBackground(Object... params) {
			
			return new com.cj.utils.NetManager(ForgetPasswordEdit.this)
					.UpdatePassWord(pwd, ID,vkey);
		}

		
		
		
		@Override
		protected void onPostExecute(Object object) {

			JSONObject j = (JSONObject) object;
			 try {
				int errcode = 	j.getInt("error_code");
				
				switch (errcode) {
				case 0:
					vkey="";
					ID="";
					if (isDialogCanel) {
						return;
					}
					if (null != dialog) {
						dialog.dismiss();
						dialog = null;
					}
					if (!isDialogCanel) {
						T.showShort(context,Utils.getResString(context, R.string.forgetpassword_tip7));
						finish();
					}
					
					break;
				default:
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					if (!isDialogCanel) {
						//T.showShort(context,"出错了,err_code为:" + errcode);
						Utils.showPromptDialog(context, R.string.prompt,
								R.string.findpassword_edit_fail);
					}
					break;
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}

	}

}
