package com.homecoolink.activity;

import org.json.JSONObject;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.network.NetManager;
import com.p2p.core.network.RegisterResult;


public class RegisterActivity2 extends BaseActivity implements OnClickListener {
	private String count;
	private String phone;
	private String code;
	private boolean isEmailRegister;
	private EditText email, pwd, confirm_pwd, phone_et, verific_et;
	private RelativeLayout clear1, clear2, clear3, clear4;
	Button register;
	boolean isDialogCanel = false;
	private Context context;
	NormalDialog dialog;
	private ImageView back;
	private Button getCodeBtn;
	public static final int CHANGE_BUTTON_TEXT = 8000;
	public static final String PWD_RE= "[0-9a-zA-Z]+";
	public TextView tip_tv;

	private RadioGroup rg;
	private LinearLayout layout_phone, layout_email, layout_verify;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_form2);
		context = this;
		// isEmailRegister =
		// getIntent().getBooleanExtra("isEmailRegister",false);
		// if(!isEmailRegister){
		// count = getIntent().getStringExtra("count");
		// phone = getIntent().getStringExtra("phone");
		// code = getIntent().getStringExtra("code");
		// }
		initComponent();

		layout_phone = (LinearLayout) findViewById(R.id.layout_phome);
		layout_email = (LinearLayout) findViewById(R.id.layout_email);
		layout_verify = (LinearLayout) findViewById(R.id.layout_verify);

		rg = (RadioGroup) findViewById(R.id.register_two_fun);
		if (rg!=null) {
			rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub

					RadioButton rb_phone = (RadioButton) group
							.findViewById(R.id.phone_register);
					RadioButton rb_email = (RadioButton) group
							.findViewById(R.id.email_register);
					LayoutParams lp_phone = (LayoutParams) rb_phone
							.getLayoutParams();
					LayoutParams lp_email = (LayoutParams) rb_email
							.getLayoutParams();

//					switch (checkedId) {
//					case R.id.phone_register:
//						rb_phone.setTextColor(context.getResources().getColor(
//								R.color.white));
//						rb_email.setTextColor(context.getResources().getColor(
//								R.color.black));
//						lp_phone.setMargins(0, 0, 0, 0);
//						lp_email.setMargins(0, 10, 10, 10);
//						rb_phone.setLayoutParams(lp_phone);
//						rb_email.setLayoutParams(lp_email);
//						layout_email.setVisibility(View.GONE);
//						layout_phone.setVisibility(View.VISIBLE);
//						layout_verify.setVisibility(View.VISIBLE);
//						break;
//					case R.id.email_register:
//						rb_email.setTextColor(context.getResources().getColor(
//								R.color.white));
//						rb_phone.setTextColor(context.getResources().getColor(
//								R.color.black));
//						lp_phone.setMargins(10, 10, 0, 10);
//						lp_email.setMargins(0, 0, 0, 0);
//						rb_phone.setLayoutParams(lp_phone);
//						rb_email.setLayoutParams(lp_email);
//						layout_email.setVisibility(View.VISIBLE);
//						layout_phone.setVisibility(View.GONE);
//						layout_verify.setVisibility(View.GONE);
//						break;
//					default:
//						break;
//					}

				}
			});

		}
		
	}

	public void initComponent() {
		tip_tv = (TextView) findViewById(R.id.tip_tv);

		getCodeBtn = (Button) findViewById(R.id.getcodebtn);

		back = (ImageView) findViewById(R.id.back_btn);

		clear1 = (RelativeLayout) findViewById(R.id.register_phone_clear);
		clear2 = (RelativeLayout) findViewById(R.id.register_msgverific_clear);
		clear3 = (RelativeLayout) findViewById(R.id.register_email_clear);
		clear4 = (RelativeLayout) findViewById(R.id.register_pwd_clear);

		email = (EditText) findViewById(R.id.register_email);
		pwd = (EditText) findViewById(R.id.register_pwd);
		pwd.setTypeface(Typeface.SANS_SERIF);

		phone_et = (EditText) findViewById(R.id.register_phone);
		verific_et = (EditText) findViewById(R.id.register_msgverific);
		email.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					clear3.setVisibility(View.VISIBLE);
				} else {
					clear3.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		pwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					clear4.setVisibility(View.VISIBLE);
				} else {
					clear4.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		phone_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					clear1.setVisibility(View.VISIBLE);
				} else {
					clear1.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		verific_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					clear2.setVisibility(View.VISIBLE);
				} else {
					clear2.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		// confirm_pwd = (EditText) findViewById(R.id.confirm_pwd); 确定密码新页面没有

		register = (Button) findViewById(R.id.register);
		getCodeBtn.setOnClickListener(this);
		back.setOnClickListener(this);
		register.setOnClickListener(this);
		clear1.setOnClickListener(this);
		clear2.setOnClickListener(this);
		clear3.setOnClickListener(this);
		clear4.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
//		switch (view.getId()) {
//		case R.id.getcodebtn:
//			getPhoneCode();
//			break;
//		case R.id.back_btn:
//			finish();
//			break;
//		case R.id.register_phone_clear:
//			phone_et.setText("");
//			break;
//		case R.id.register_msgverific_clear:
//			verific_et.setText("");
//			break;
//		case R.id.register_email_clear:
//			email.setText("");
//			break;
//		case R.id.register_pwd_clear:
//			pwd.setText("");
//			break;
//		case R.id.register:
//			if (tip_tv != null) {
//				if (tip_tv.getVisibility() == View.VISIBLE) {
//					register();
//				} else {
//					register2();
//				}
//			} else {
//				if (layout_phone.getVisibility() == View.VISIBLE) {
//					register1();
//				} else {
//					register2();
//				}
//			}
//			break;
//		}
	}

	
	private void register2() {
		final String email_str = email.getText().toString();
		final String pwd_str = pwd.getText().toString();
		final String confirm_pwd_str = pwd_str;
		if (null == email_str || "".equals(email_str)) {
			if (phone == null || phone.equals("")) {
				T.showShort(this, R.string.input_email);
				return;
			}
		} else {
			if (email_str.length() > 31 || email_str.length() < 5) {
				T.showShort(this, R.string.email_too_long);
				return;
			}
		}

		if (null == pwd_str || "".equals(pwd_str)) {
			T.showShort(this, R.string.inputpassword);
			return;
		}

		if (pwd_str.length() > 27) {
			T.showShort(this, R.string.password_length_error);
			return;
		}

		if (null == confirm_pwd_str || "".equals(confirm_pwd_str)) {
			T.showShort(this, R.string.reinputpassword);
			return;
		}

		if (!pwd_str.equals(confirm_pwd_str)) {
			T.showShort(this, R.string.differentpassword);
			return;
		}
		
		if (!pwd_str.matches(PWD_RE)) {
			T.showLong(context, context.getResources().getString(R.string.regex_tip2));
			return;
		}
		
		
		
		dialog = new NormalDialog(context, context.getResources().getString(
				R.string.registering), "", "", "");
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
		new RegisterTask("1", email_str, "", "", pwd_str, confirm_pwd_str, "",
				"1").execute();

	}

	private void register1() {

		final String phone = phone_et.getText().toString();

		final String pwd_str = pwd.getText().toString();
		// final String confirm_pwd_str = confirm_pwd.getText().toString();
		final String confirm_pwd_str = pwd_str;

		if (phone == null || phone.equals("")) {

			T.showShort(context, R.string.input_phone);
			return;

		} else {
			if (phone.length() < 6 || phone.length() > 15) {
				T.showShort(this, R.string.phone_too_long);
				return;
			}
		}

		if (null == pwd_str || "".equals(pwd_str)) {
			T.showShort(this, R.string.inputpassword);
			return;
		}

		if (pwd_str.length() > 27) {
			T.showShort(this, R.string.password_length_error);
			return;
		}

		if (null == confirm_pwd_str || "".equals(confirm_pwd_str)) {
			T.showShort(this, R.string.reinputpassword);
			return;
		}

		if (!pwd_str.equals(confirm_pwd_str)) {
			T.showShort(this, R.string.differentpassword);
			return;
		}
		if (!pwd_str.matches(PWD_RE)) {
			T.showLong(context, context.getResources().getString(R.string.regex_tip2));
			return;
		}

		
		code = verific_et.getText().toString();
		if (code == null || code.equals("")) {
			T.showShort(context, R.string.input_vf_code);
			return;
		} else {
			checkCode(new RegisterTask("1", "", "86", phone, pwd_str,
					confirm_pwd_str, code, "1"));
		}

	}

	private void register() {

		final String email_str = email.getText().toString();
		final String phone = phone_et.getText().toString();

		final String pwd_str = pwd.getText().toString();
		// final String confirm_pwd_str = confirm_pwd.getText().toString();
		final String confirm_pwd_str = pwd_str;

		boolean phone_bl = false;
		boolean email_bl = false;
		if (phone == null || phone.equals("")) {
			if (null == email_str || "".equals(email_str)) {
				T.showShort(context, R.string.input_phone);
				return;
			}
		} else {
			if (phone.length() < 6 || phone.length() > 15) {
				T.showShort(this, R.string.phone_too_long);
				return;
			} else {
				phone_bl = true;
			}
		}

		if (null == email_str || "".equals(email_str)) {
			if (phone == null || phone.equals("")) {
				T.showShort(this, R.string.input_email);
				return;
			}
		} else {
			if (email_str.length() > 31 || email_str.length() < 5) {
				T.showShort(this, R.string.email_too_long);
				return;
			} else {
				email_bl = true;
			}
		}

		if (null == pwd_str || "".equals(pwd_str)) {
			T.showShort(this, R.string.inputpassword);
			return;
		}

		if (pwd_str.length() > 27) {
			T.showShort(this, R.string.password_length_error);
			return;
		}

		if (null == confirm_pwd_str || "".equals(confirm_pwd_str)) {
			T.showShort(this, R.string.reinputpassword);
			return;
		}

		if (!pwd_str.equals(confirm_pwd_str)) {
			T.showShort(this, R.string.differentpassword);
			return;
		}
		if (!pwd_str.matches(PWD_RE)) {
			T.showLong(context, context.getResources().getString(R.string.regex_tip2));
			return;
		}
		
		
		
		Log.e("343", "email_str=" + email_str + ";count=" + count + ";phone="
				+ phone + ";pwd_str=" + pwd_str + ";confirm_pwd_str"
				+ confirm_pwd_str + ";code=" + code);
		if (phone_bl && email_bl) {
			
			//
			code = verific_et.getText().toString();
			if (code == null || code.equals("")) {
				T.showShort(context, R.string.input_vf_code);
				return;
			} else {
				checkCode(new RegisterTask("1", email_str, "86", phone,
						pwd_str, confirm_pwd_str, code, "1"));
			}
		} else if (email_bl) {
		
			dialog = new NormalDialog(context, context.getResources()
					.getString(R.string.registering), "", "", "");
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
			new RegisterTask("1", email_str, "", "", pwd_str, confirm_pwd_str,
					"", "1").execute();

		} else if (phone_bl) {
			
			code = verific_et.getText().toString();
			if (code == null || code.equals("")) {
				if (null == email_str || "".equals(email_str)) {
					T.showShort(context, R.string.input_vf_code);
					return;
				}
			} else {
				checkCode(new RegisterTask("1", "", "86", phone, pwd_str,
						confirm_pwd_str, code, "1"));
			}

		} else {
			
		}

	}

	class RegisterTask extends AsyncTask {
		String VersionFlag;
		String Email;
		String CountryCode;
		String PhoneNO;
		String Pwd;
		String RePwd;
		String VerifyCode;
		String IgnoreSafeWarning;

		public RegisterTask(String VersionFlag, String Email,
				String CountryCode, String PhoneNO, String Pwd, String RePwd,
				String VerifyCode, String IgnoreSafeWarning) {
			this.VersionFlag = VersionFlag;
			this.Email = Email;
			this.CountryCode = CountryCode;
			this.PhoneNO = PhoneNO;
			this.Pwd = Pwd;
			this.RePwd = RePwd;
			this.VerifyCode = VerifyCode;
			this.IgnoreSafeWarning = IgnoreSafeWarning;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Utils.sleepThread(1000);
			return NetManager.getInstance(context).register(VersionFlag, Email,
					CountryCode, PhoneNO, Pwd, RePwd, VerifyCode,
					IgnoreSafeWarning);
		}

		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			RegisterResult result = NetManager
					.createRegisterResult((JSONObject) object);
			switch (Integer.parseInt(result.error_code)) {
			case NetManager.SESSION_ID_ERROR:
				Intent relogin = new Intent();
				relogin.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(relogin);
				break;
			case NetManager.CONNECT_CHANGE:
				new RegisterTask(VersionFlag, Email, CountryCode, PhoneNO, Pwd,
						RePwd, VerifyCode, IgnoreSafeWarning).execute();
				return;
			case NetManager.REGISTER_SUCCESS:
				
				T.showShort(context,
						Utils.getResString(context, R.string.registersuccess));
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					if (!"".equals(Email)) {
						
						Intent i = new Intent();
						i.setAction(Constants.Action.REPLACE_EMAIL_LOGIN);
						i.putExtra("username", Email);
						i.putExtra("password", Pwd);
						context.sendBroadcast(i);
						finish();
					} else {
						
						Intent i = new Intent();
						i.setAction(Constants.Action.REPLACE_PHONE_LOGIN);
						i.putExtra("username", PhoneNO);
						i.putExtra("password", Pwd);
						i.putExtra("code", CountryCode);
						context.sendBroadcast(i);
						finish();
					}
				}
				break;
			case NetManager.REGISTER_EMAIL_USED:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					Utils.showPromptDialog(context, R.string.prompt,
							R.string.email_used);
				}
				break;
			case NetManager.REGISTER_EMAIL_FORMAT_ERROR:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					Utils.showPromptDialog(context, R.string.prompt,
							R.string.email_format_error);
				}
				break;
			case NetManager.REGISTER_PASSWORD_NO_MATCH:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}

				break;

			default:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					T.showShort(context, R.string.operator_error);
				}
				break;
			}
		}

	}

	
	private void getPhoneCode() {
		phone = phone_et.getText().toString();
		if (phone == null || phone.equals("")) {
			T.showShort(context, R.string.input_phone);
			return;
		}

		if (phone.length() < 6 || phone.length() > 15) {
			T.showShort(this, R.string.phone_too_long);
			return;
		}

		dialog = new NormalDialog(this, this.getResources().getString(
				R.string.waiting_verify_code), "", "", "");
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

		String count = "+86";
		new GetPhoneCodeTask(count.substring(1, count.length()), phone)
				.execute();

	}

	class GetPhoneCodeTask extends AsyncTask {
		String CountryCode;
		String PhoneNO;

		public GetPhoneCodeTask(String CountryCode, String PhoneNO) {
			this.CountryCode = CountryCode;
			this.PhoneNO = PhoneNO;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Utils.sleepThread(1000);
			Log.e("343", "CountryCode=" + CountryCode + ";PhoneNo=" + PhoneNO);
			return NetManager.getInstance(context).getPhoneCode(CountryCode,
					PhoneNO);
		}

		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			int result = (Integer) object;
			Log.e("343", "result的值" + result);
			switch (result) {
			case NetManager.SESSION_ID_ERROR:
				Intent relogin = new Intent();
				relogin.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(relogin);
				break;
			case NetManager.CONNECT_CHANGE:
				new GetPhoneCodeTask(CountryCode, PhoneNO).execute();
				return;
			case NetManager.GET_PHONE_CODE_SUCCESS:
				if (isDialogCanel) {
					return;
				}
				if (null != dialog) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					if (CountryCode.equals("86")) {
						T.showShort(
								context,
								getResources().getString(
										R.string.phone_verify_prompt)
										+ PhoneNO);
						changeButton();
						// Intent i = new
						// Intent(context,VerifyPhoneActivity.class);
						// i.putExtra("phone", PhoneNO);
						// i.putExtra("count", CountryCode);
						// startActivity(i);
						// finish();
					} else {
						// Intent i = new
						// Intent(context,RegisterActivity2.class);
						// i.putExtra("phone", PhoneNO);
						// i.putExtra("count", CountryCode);
						// startActivity(i);
						// finish();
					}
				}
				break;
			case NetManager.GET_PHONE_CODE_TOO_TIMES:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					T.showShort(context, R.string.get_phone_code_too_times);
				}
				break;
			case NetManager.GET_PHONE_CODE_PHONE_FORMAT_ERROR:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					Utils.showPromptDialog(context, R.string.prompt,
							R.string.phone_format_error);
				}
				break;
			case NetManager.GET_PHONE_CODE_PHONE_USED:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					Utils.showPromptDialog(context, R.string.prompt,
							R.string.phone_number_used);
				}
				break;
			default:
				
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					Utils.showPromptDialog(context, R.string.prompt,
							R.string.registerfail);
				}
				break;
			}
		}

	}

	private Handler mHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case CHANGE_BUTTON_TEXT:
				if (layout_phone.getVisibility()==View.VISIBLE) {					
					int time = msg.arg1;
					getCodeBtn.setText(String.valueOf(time));
					if (time == 0) {
						getCodeBtn.setText(R.string.resend);
						getCodeBtn.setClickable(true);
					}
					if (time == 180) {
						getCodeBtn.setClickable(false);
					}
				}else {
					
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
					change.what = CHANGE_BUTTON_TEXT;
					change.arg1 = time;
					mHandler.sendMessage(change);
					time--;
					Utils.sleepThread(1000);
				}
			}
		}.start();
	}

	public void checkCode(RegisterTask rt) {
		final String phone = phone_et.getText().toString();
		if (phone == null || phone.equals("")) {
			T.showShort(context, R.string.input_phone);
			return;
		}

		if (phone.length() < 6 || phone.length() > 15) {
			T.showShort(this, R.string.phone_too_long);
			return;
		}

		dialog = new NormalDialog(this, this.getResources().getString(
				R.string.verifing), "", "", "");
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
		Log.e("343", "count=" + count + ";phont=" + phone + ";code=" + code);
		new VerifyCodeTask("86", phone, code, rt).execute();
	}

	class VerifyCodeTask extends AsyncTask {
		String countryCode;
		String phoneNO;
		String code;
		RegisterTask rt;

		public VerifyCodeTask(String countryCode, String phoneNO, String code,
				RegisterTask rt) {
			this.countryCode = countryCode;
			this.phoneNO = phoneNO;
			this.code = code;
			this.rt = rt;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Utils.sleepThread(1000);
			return NetManager.getInstance(context).verifyPhoneCode(countryCode,
					phoneNO, code);
		}

		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			int result = (Integer) object;
			switch (result) {
			case NetManager.SESSION_ID_ERROR:
				Intent relogin = new Intent();
				relogin.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(relogin);
				break;
			case NetManager.CONNECT_CHANGE:
				new VerifyCodeTask(countryCode, phoneNO, code, rt).execute();
				return;
			case NetManager.VERIFY_CODE_SUCCESS:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					T.showLong(context,
							Utils.getResString(context, R.string.regist_tip1));
					try {
						dialog = new NormalDialog(context,
								context.getResources().getString(
										R.string.registering), "", "", "");
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
						rt.execute();
					} catch (Exception e) {
						// TODO: handle exception
						
						Log.e("343", Log.getStackTraceString(e));
					}
					// Intent i = new Intent(context,RegisterActivity2.class);
					// i.putExtra("phone", phone);
					// i.putExtra("count", count);
					// i.putExtra("code", code);
					// startActivity(i);
					// finish();
				}
				break;
			case NetManager.VERIFY_CODE_ERROR:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					Utils.showPromptDialog(context, R.string.prompt,
							R.string.vfcode_error);
				}
				break;
			case NetManager.VERIFY_CODE_TIME_OUT:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					Utils.showPromptDialog(context, R.string.prompt,
							R.string.vfcode_timeout);
				}
				break;
			default:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					T.showShort(context, R.string.operator_error);
				}
				break;
			}
		}

	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_REGISTERACTIVITY2;
	}
}
