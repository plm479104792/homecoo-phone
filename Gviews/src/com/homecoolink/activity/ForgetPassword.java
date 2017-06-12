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
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class ForgetPassword extends Activity implements OnClickListener {
	public ImageView back, towskin, oneskin, msgRb, emRb;
	public RelativeLayout clear1, clear2, clear3;// , clear4
	public LinearLayout msgll, emll;
	public EditText phone_et1, phoneverific_et2, email_et3;// , emailverific_et4
	protected Button phoneverific_btn, okbtn;
	boolean isDialogCanel = false;
	private Context context;
	NormalDialog dialog;
	private String vkey = "", ID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		initComponent();

		// setContentView(R.layout.forgetpassword);
		// wv=(WebView)findViewById(R.id.webView1);
		// wv.getSettings().setJavaScriptEnabled(true);
		// wv.loadUrl(Constants.FORGET_PASSWORD_URL);
		// wv.setWebViewClient(new WebViewClient(){
		// @Override
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// view.loadUrl(url);
		// return true;
		// }
		// });
	}

	private void initComponent() {
		setContentView(R.layout.activity_forgetpassword);
		back = (ImageView) findViewById(R.id.back_btn);
		oneskin = (ImageView) findViewById(R.id.sharecfskinone);
		towskin = (ImageView) findViewById(R.id.sharecfskintwo);
		msgll = (LinearLayout) findViewById(R.id.layout_msgradio);
		emll = (LinearLayout) findViewById(R.id.layout_emailradio);
		msgRb = (ImageView) msgll.getChildAt(0);
		emRb = (ImageView) emll.getChildAt(0);
		clear1 = (RelativeLayout) findViewById(R.id.phone_clear);
		clear2 = (RelativeLayout) findViewById(R.id.phoneverific_clear);
		clear3 = (RelativeLayout) findViewById(R.id.email_clear);
		// clear4 = (RelativeLayout) findViewById(R.id.emailverific_clear);
		phone_et1 = (EditText) findViewById(R.id.phone_et);
		phoneverific_et2 = (EditText) findViewById(R.id.phoneverific_et);
		email_et3 = (EditText) findViewById(R.id.email_et);
		// emailverific_et4 = (EditText) findViewById(R.id.emailverific_et);
		back.setOnClickListener(this);
		oneskin.setOnClickListener(this);
		towskin.setOnClickListener(this);
		msgll.setOnClickListener(this);
		emll.setOnClickListener(this);
		clear1.setOnClickListener(this);
		clear2.setOnClickListener(this);
		clear3.setOnClickListener(this);
		// clear4.setOnClickListener(this);
		msgRb.setImageResource(R.drawable.sharecf_checktrue);
		oneskin.setVisibility(View.GONE);
		towskin.setVisibility(View.VISIBLE);

		(phoneverific_btn = (Button) findViewById(R.id.phoneverific_btn))
				.setOnClickListener(this);
		(okbtn = (Button) findViewById(R.id.register)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.back_btn){
			finish();
		}else if(id==R.id.layout_msgradio){
			msgRb.setImageResource(R.drawable.sharecf_checktrue);
			emRb.setImageResource(R.drawable.sharecf_checkflase);
			oneskin.setVisibility(View.GONE);
			towskin.setVisibility(View.VISIBLE);
		}else if(id==R.id.layout_emailradio){
			emRb.setImageResource(R.drawable.sharecf_checktrue);
			msgRb.setImageResource(R.drawable.sharecf_checkflase);
			oneskin.setVisibility(View.VISIBLE);
			towskin.setVisibility(View.GONE);
		}else if(id==R.id.phone_clear){
			phone_et1.setText("");
		}else if(id==R.id.phoneverific_clear){
			phoneverific_et2.setText("");
		}else if(id==R.id.email_clear){
			email_et3.setText("");
		}else if(id==R.id.phoneverific_btn){
			if (!phone_et1.getText().toString().trim().equals("")) {
				dialog = new NormalDialog(context, context.getResources()
						.getString(R.string.waiting_verify_code), "", "", "");
				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						isDialogCanel = true;
					}

				});
				isDialogCanel = false;
				dialog.showDialog();
				new GetPhoneVrifyCodeTask(phone_et1.getText().toString())
						.execute();
			} else {

				T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip2));
			}
		}else if(id==R.id.register){
			if (oneskin.getVisibility() != View.VISIBLE&&msgRb.getVisibility() == View.VISIBLE) {
				if (vkey.equals("")) {
					T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip3));
					return;
				}
				if (phone_et1.getText().toString().trim().equals("")) {
					T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip2));
					return;
				}
				if (phoneverific_et2.getText().toString().trim().equals("")) {
					T.showShort(context, Utils.getResString(context, R.string.inputverfiycode));
					return;
				}
				dialog = new NormalDialog(context, Utils.getResString(context, R.string.forgetpassword_tip1), "", "", "");
				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						isDialogCanel = true;
					}

				});
				isDialogCanel = false;
				dialog.showDialog();
				new CheckPhoneVrifyCodeTask(phone_et1.getText().toString(),
						phoneverific_et2.getText().toString()).execute();
			} else if (towskin.getVisibility() != View.VISIBLE||emRb.getVisibility() == View.GONE){
				if (!email_et3.getText().toString().trim().equals("")) {
					dialog = new NormalDialog(context, context.getResources()
							.getString(R.string.waiting_verify_code), "", "",
							"");
					dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
					dialog.setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							isDialogCanel = true;
						}

					});
					isDialogCanel = false;
					dialog.showDialog();
					new CheckEmailVrifyCodeTask(email_et3.getText().toString())
							.execute();
				} else {

					T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip4));
				}
			}
		}
		
//		switch (v.getId()) {
//		case R.id.back_btn:
//			finish();
//			break;
//		case R.id.sharecfskinone:
//
//			break;
//		case R.id.sharecfskintwo:
//
//			break;
//		case R.id.layout_msgradio:
//			msgRb.setImageResource(R.drawable.sharecf_checktrue);
//			emRb.setImageResource(R.drawable.sharecf_checkflase);
//			oneskin.setVisibility(View.GONE);
//			towskin.setVisibility(View.VISIBLE);
//			break;
//		case R.id.layout_emailradio:
//			emRb.setImageResource(R.drawable.sharecf_checktrue);
//			msgRb.setImageResource(R.drawable.sharecf_checkflase);
//			oneskin.setVisibility(View.VISIBLE);
//			towskin.setVisibility(View.GONE);
//			break;
//		case R.id.phone_clear:
//			phone_et1.setText("");
//			break;
//		case R.id.phoneverific_clear:
//			phoneverific_et2.setText("");
//			break;
//		case R.id.email_clear:
//			email_et3.setText("");
//			break;
//		// case R.id.emailverific_clear:
//		// emailverific_et4.setText("");
//		// break;
//		case R.id.phoneverific_btn:
//			if (!phone_et1.getText().toString().trim().equals("")) {
//				dialog = new NormalDialog(context, context.getResources()
//						.getString(R.string.waiting_verify_code), "", "", "");
//				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
//				dialog.setOnCancelListener(new OnCancelListener() {
//
//					@Override
//					public void onCancel(DialogInterface dialog) {
//						// TODO Auto-generated method stub
//						isDialogCanel = true;
//					}
//
//				});
//				isDialogCanel = false;
//				dialog.showDialog();
//				new GetPhoneVrifyCodeTask(phone_et1.getText().toString())
//						.execute();
//			} else {
//
//				T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip2));
//			}
//			break;
//		case R.id.register:
//			if (oneskin.getVisibility() != View.VISIBLE&&msgRb.getVisibility() == View.VISIBLE) {
//				if (vkey.equals("")) {
//					T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip3));
//					return;
//				}
//				if (phone_et1.getText().toString().trim().equals("")) {
//					T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip2));
//					return;
//				}
//				if (phoneverific_et2.getText().toString().trim().equals("")) {
//					T.showShort(context, Utils.getResString(context, R.string.inputverfiycode));
//					return;
//				}
//				dialog = new NormalDialog(context, Utils.getResString(context, R.string.forgetpassword_tip1), "", "", "");
//				dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
//				dialog.setOnCancelListener(new OnCancelListener() {
//
//					@Override
//					public void onCancel(DialogInterface dialog) {
//						// TODO Auto-generated method stub
//						isDialogCanel = true;
//					}
//
//				});
//				isDialogCanel = false;
//				dialog.showDialog();
//				new CheckPhoneVrifyCodeTask(phone_et1.getText().toString(),
//						phoneverific_et2.getText().toString()).execute();
//			} else if (towskin.getVisibility() != View.VISIBLE||emRb.getVisibility() == View.GONE){
//				if (!email_et3.getText().toString().trim().equals("")) {
//					dialog = new NormalDialog(context, context.getResources()
//							.getString(R.string.waiting_verify_code), "", "",
//							"");
//					dialog.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
//					dialog.setOnCancelListener(new OnCancelListener() {
//
//						@Override
//						public void onCancel(DialogInterface dialog) {
//							// TODO Auto-generated method stub
//							isDialogCanel = true;
//						}
//
//					});
//					isDialogCanel = false;
//					dialog.showDialog();
//					new CheckEmailVrifyCodeTask(email_et3.getText().toString())
//							.execute();
//				} else {
//
//					T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip4));
//				}
//			}
//			break;
//		default:
//			break;
//		}

	}

	private Handler mHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				int time = msg.arg1;
				phoneverific_btn.setText(String.valueOf(time));
				if (time == 0) {
					phoneverific_btn.setText(R.string.resend);
					phoneverific_btn.setClickable(true);
				}
				if (time == 180) {
					phoneverific_btn.setClickable(false);
				}
				break;
			default:
				break;
			}

			return false;
		}
	});

	public void changeButton() {
		new Thread() {
			@Override
			public void run() {
				int time = 180;
				while (time >= 0) {
					Message change = new Message();
					change.what = 1;
					change.arg1 = time;
					mHandler.sendMessage(change);
					time--;
					Utils.sleepThread(1000);
				}
			}
		}.start();
	}

	class GetPhoneVrifyCodeTask extends AsyncTask<Object, Object, Object> {
		String phone;

		public GetPhoneVrifyCodeTask(String p) {
			this.phone = p;

		}

		@Override
		protected Object doInBackground(Object... params) {

			return new com.cj.utils.NetManager(ForgetPassword.this)
					.GetCheckCodeByPhone(phone);
		}

		@Override
		protected void onPostExecute(Object object) {

			JSONObject j = (JSONObject) object;
			try {
				int errcode = j.getInt("error_code");

				switch (errcode) {
				case 0:
					vkey = j.getString("VKey");
					ID = j.getString("ID");
					if (isDialogCanel) {
						return;
					}
					if (null != dialog) {
						dialog.dismiss();
						dialog = null;
					}
					if (!isDialogCanel) {
						T.showShort(
								context,
								getResources().getString(
										R.string.phone_verify_prompt)
										+ phone);
						changeButton();
					}

					break;
				default:
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					if (!isDialogCanel) {
						// T.showShort(context, "出错了,err_code为:" + errcode);
						Utils.showPromptDialog(context, R.string.prompt,
								R.string.findpassword_phone_checkcodefail);
					}
					break;
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}

	}

	class CheckPhoneVrifyCodeTask extends AsyncTask<Object, Object, Object> {
		String phone;
		String phonecode;

		public CheckPhoneVrifyCodeTask(String p, String pcode) {
			this.phone = p;
			this.phonecode = pcode;
		}

		@Override
		protected Object doInBackground(Object... params) {

			return new com.cj.utils.NetManager(ForgetPassword.this)
					.CheckCodePhoneCode(phone, phonecode, ID, vkey);
		}

		@Override
		protected void onPostExecute(Object object) {

			JSONObject j = (JSONObject) object;
			try {
				int errcode = j.getInt("error_code");

				switch (errcode) {
				case 0:
					vkey = j.getString("VKey");
					ID = j.getString("ID");
					if (isDialogCanel) {
						return;
					}
					if (null != dialog) {
						dialog.dismiss();
						dialog = null;
					}
					if (!isDialogCanel) {
						Intent i = new Intent(context, ForgetPasswordEdit.class);
						i.putExtra("VKEY", vkey);
						i.putExtra("ID", ID);
						startActivity(i);
						finish();
					}

					break;
				default:

					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					if (!isDialogCanel) {
						// T.showShort(context, "出错了,err_code为:" + errcode);
						Utils.showPromptDialog(context, R.string.prompt,
								R.string.findpassword_phone_checkfail);
					}
					break;
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}

	}

	class CheckEmailVrifyCodeTask extends AsyncTask<Object, Object, Object> {
		String email;

		public CheckEmailVrifyCodeTask(String e) {
			this.email = e;

		}

		@Override
		protected Object doInBackground(Object... params) {

			return new com.cj.utils.NetManager(ForgetPassword.this)
					.GetCheckCodeByEmail(email);
		}

		@Override
		protected void onPostExecute(Object object) {

			JSONObject j = (JSONObject) object;
			try {
				int errcode = j.getInt("error_code");

				switch (errcode) {
				case 0:

					if (isDialogCanel) {
						return;
					}
					if (null != dialog) {
						dialog.dismiss();
						dialog = null;
					}
					if (!isDialogCanel) {
						T.showShort(context, Utils.getResString(context, R.string.forgetpassword_tip5));
						finish();
					}

					break;
				default:

					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					if (!isDialogCanel) {
						// T.showShort(context, "出错了,err_code为:" + errcode);
						Utils.showPromptDialog(context, R.string.prompt,
								R.string.findpassword_email_checkfail);
					}
					break;
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}

	}

}
