package com.homecoolink.activity;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.data.SharedPreferencesManager;
import com.homecoolink.entity.Account;
import com.homecoolink.global.AccountPersist;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;


public class LoginActivity extends BaseActivity implements OnClickListener {
	
	public static Context mContext;
	TextView title_text;
	private boolean isRegFilter = false;
	
	public static final int ANIMATION_END = 2;
	
	public static final int ACCOUNT_NO_EXIST = 3;
	Button mLogin;
	TextView mregister;
	TextView mRegister_phone, mRegister_email;
	private EditText mAccountPwd;
	private EditText mAccountName;
	private String mInputName, mInputPwd;

	LinearLayout remember_pass;
	RelativeLayout dialog_remember;

	ImageView remember_pwd_img, login_qq;
	private boolean isDialogCanel = false;
	NormalDialog dialog;
//	TextView dfault_name, dfault_count;

	// RelativeLayout choose_country;
	// RadioButton type_phone, type_email;
	int current_type;
	TextView forget_pwd; 

	RelativeLayout name_clearrl, pwd_clearrl;

	
	public LoginActivity() {

	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_login);
		mContext = this;
		initComponent();
		if (SharedPreferencesManager.getInstance().getRecentLoginType(mContext) == Constants.LoginType.PHONE) {
			
			current_type = Constants.LoginType.PHONE;
		} else {
		
			current_type = Constants.LoginType.EMAIL;		
		}
		regFilter();
		initRememberPass();
	}

	
	public void initComponent() {

		mLogin = (Button) findViewById(R.id.login);
		mregister = (TextView) findViewById(R.id.register);
		mregister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		name_clearrl = (RelativeLayout) findViewById(R.id.name_clear);
		pwd_clearrl = (RelativeLayout) findViewById(R.id.pwd_clear);
		mAccountName = (EditText) findViewById(R.id.phone_number);
		mAccountPwd = (EditText) findViewById(R.id.password);
		mAccountName.setTypeface(Typeface.SANS_SERIF);
		mAccountPwd.setTypeface(Typeface.SANS_SERIF);
		
		
		mAccountName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					name_clearrl.setVisibility(View.VISIBLE);
				} else {
					name_clearrl.setVisibility(View.GONE);
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
		mAccountPwd.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					pwd_clearrl.setVisibility(View.VISIBLE);
				} else {
					pwd_clearrl.setVisibility(View.GONE);
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
		
		remember_pass = (LinearLayout) findViewById(R.id.remember_pass);
		remember_pwd_img = (ImageView) findViewById(R.id.remember_pwd_img);
		login_qq = (ImageView) findViewById(R.id.login_qq);
		dialog_remember = (RelativeLayout) findViewById(R.id.dialog_remember);
		
//		dfault_name = (TextView) findViewById(R.id.name);
//		dfault_count = (TextView) findViewById(R.id.count);

		forget_pwd = (TextView) findViewById(R.id.forget_pwd);
		forget_pwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		forget_pwd.setOnClickListener(this);
		mLogin.setOnClickListener(this);
		mregister.setOnClickListener(this);
		remember_pass.setOnClickListener(this);
		name_clearrl.setOnClickListener(this);
		pwd_clearrl.setOnClickListener(this);
		login_qq.setOnClickListener(this);
	}

	
	public void initRememberPass() {
		String recentName = "";
		String recentPwd = "";
		String recentCode = "";
		if (current_type == Constants.LoginType.PHONE) {
			recentName = SharedPreferencesManager.getInstance().getData(
					mContext, SharedPreferencesManager.SP_FILE_GWELL,
					SharedPreferencesManager.KEY_RECENTNAME);
			recentPwd = SharedPreferencesManager.getInstance().getData(
					mContext, SharedPreferencesManager.SP_FILE_GWELL,
					SharedPreferencesManager.KEY_RECENTPASS);
			recentCode = SharedPreferencesManager.getInstance().getData(
					mContext, SharedPreferencesManager.SP_FILE_GWELL,
					SharedPreferencesManager.KEY_RECENTCODE);

			if (!recentName.equals("")) {
				mAccountName.setText(recentName);
			} else {
				mAccountName.setText("");
			}

			if (!recentCode.equals("")) {
//				dfault_count.setText("+" + recentCode);
				String name = SearchListActivity.getNameByCode(mContext,
						Integer.parseInt(recentCode));
//				dfault_name.setText(name);
			} else {
				if (getResources().getConfiguration().locale.getCountry()
						.equals("TW")) {
//					dfault_count.setText("+886");
					String name = SearchListActivity.getNameByCode(mContext,
							886);
//					dfault_name.setText(name);
				} else if (getResources().getConfiguration().locale
						.getCountry().equals("CN")) {
//					dfault_count.setText("+86");
					String name = SearchListActivity
							.getNameByCode(mContext, 86);
//					dfault_name.setText(name);
				} else {
//					dfault_count.setText("+1");
					String name = SearchListActivity.getNameByCode(mContext, 1);
//					dfault_name.setText(name);
				}
			}

			if (SharedPreferencesManager.getInstance().getIsRememberPass(
					mContext)) {
				remember_pwd_img.setImageResource(R.drawable.login_autocb_press);
				if (!recentPwd.equals("")) {
					mAccountPwd.setText(recentPwd);
				} else {
					mAccountPwd.setText("");
				}
			} else {
				remember_pwd_img.setImageResource(R.drawable.login_autocb_nor);
				mAccountPwd.setText("");
			}
		} else {
			recentName = SharedPreferencesManager.getInstance().getData(
					mContext, SharedPreferencesManager.SP_FILE_GWELL,
					SharedPreferencesManager.KEY_RECENTNAME_EMAIL);
			recentPwd = SharedPreferencesManager.getInstance().getData(
					mContext, SharedPreferencesManager.SP_FILE_GWELL,
					SharedPreferencesManager.KEY_RECENTPASS_EMAIL);

			if (!recentName.equals("")) {
				mAccountName.setText(recentName);
			} else {
				mAccountName.setText("");
			}

			if (SharedPreferencesManager.getInstance().getIsRememberPass_email(
					mContext)) {
				remember_pwd_img.setImageResource(R.drawable.login_autocb_press);
				if (!recentPwd.equals("")) {
					mAccountPwd.setText(recentPwd);
				} else {
					mAccountPwd.setText("");
				}
			} else {
				remember_pwd_img.setImageResource(R.drawable.login_autocb_nor);
				mAccountPwd.setText("");
			}
		}
	}

	

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.REPLACE_EMAIL_LOGIN);
		filter.addAction(Constants.Action.REPLACE_PHONE_LOGIN);
		filter.addAction(Constants.Action.ACTION_COUNTRY_CHOOSE);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.Action.REPLACE_EMAIL_LOGIN)) {
				mAccountName.setText(intent.getStringExtra("username"));
				mAccountPwd.setText(intent.getStringExtra("password"));
				current_type = Constants.LoginType.EMAIL;
				
				login();
			} else if (intent.getAction().equals(
					Constants.Action.REPLACE_PHONE_LOGIN)) {
				
				mAccountName.setText(intent.getStringExtra("username"));
				mAccountPwd.setText(intent.getStringExtra("password"));
//				dfault_count.setText("+" + intent.getStringExtra("code"));
				current_type = Constants.LoginType.PHONE;
				login();
			} else if (intent.getAction().equals(
					Constants.Action.ACTION_COUNTRY_CHOOSE)) {
				String[] info = intent.getStringArrayExtra("info");
//				dfault_name.setText(info[0]);
//				dfault_count.setText("+" + info[1]);
			}
		}
	};

	
	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.forget_pwd){
			Intent open_web = new Intent(mContext, ForgetPassword.class);
			startActivity(open_web);
		}else if(id==R.id.login_qq){
			dialog = new NormalDialog(mContext, mContext.getResources()
					.getString(R.string.login_qq_notok_title), mContext
					.getResources().getString(R.string.login_qq_notok_content),
					"", "");
			dialog.setStyle(NormalDialog.DIALOG_STYLE_PROMPT);
			dialog.showDialog();
		}else if(id==R.id.login){
			login();
		}
		
		
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.forget_pwd:
//			// Uri uri = Uri.parse(Constants.FORGET_PASSWORD_URL);
//			Intent open_web = new Intent(mContext, ForgetPassword.class);
//			startActivity(open_web);
//			break;
//		case R.id.login_qq:
//			dialog = new NormalDialog(mContext, mContext.getResources()
//					.getString(R.string.login_qq_notok_title), mContext
//					.getResources().getString(R.string.login_qq_notok_content),
//					"", "");
//			dialog.setStyle(NormalDialog.DIALOG_STYLE_PROMPT);
//			dialog.showDialog();
//			
//			
//			break;
//		// case R.id.country_layout:
//		// Intent i = new Intent(mContext, SearchListActivity.class);
//		// startActivity(i);
//		// break;
//		// case R.id.type_phone:
//		// type_phone.setChecked(true);
//		// type_email.setChecked(false);
//		// choose_country.setVisibility(RelativeLayout.VISIBLE);
//		// current_type = Constants.LoginType.PHONE;
//		// initRememberPass();
//		// break;
//		// case R.id.type_email:
//		// type_phone.setChecked(false);
//		// type_email.setChecked(true);
//		// choose_country.setVisibility(RelativeLayout.GONE);
//		// current_type = Constants.LoginType.EMAIL;
//		// initRememberPass();
//		// break;
//		case R.id.login:
//					
//				login();
//			
//			break;
//		// case R.id.register_phone:
//		// Intent register = new Intent(mContext,RegisterActivity.class);
//		// startActivity(register);
//		// break;
//		// case R.id.register_email:
//		// Intent register_email = new Intent(mContext,RegisterActivity2.class);
//		// register_email.putExtra("isEmailRegister", true);
//		// startActivity(register_email);
//		// break;
//		case R.id.register:
//			Intent register = new Intent(mContext,
//					RegisterActivity2.class);
//			startActivity(register);
//			break;
//		case R.id.remember_pass:
//			
//				boolean isChecked = false;
//				if (current_type == Constants.LoginType.PHONE) {
//					
//					isChecked = SharedPreferencesManager.getInstance()
//							.getIsRememberPass(mContext);
//				} else {
//					isChecked = SharedPreferencesManager.getInstance()
//							.getIsRememberPass_email(mContext);
//				}
//
//				if (isChecked) {
//					//isChecked true
//					TextView dialog_text = (TextView) dialog_remember
//							.findViewById(R.id.dialog_text);
//					ImageView dialog_img = (ImageView) dialog_remember
//							.findViewById(R.id.dialog_img);
//					dialog_img.setImageResource(R.drawable.ic_unremember_pwd);
//					dialog_text.setText(R.string.un_rem_pass);
//					dialog_text.setGravity(Gravity.CENTER);
//					dialog_remember.setVisibility(View.VISIBLE);
//					Animation anim = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1f,
//							Animation.RELATIVE_TO_SELF, 0.5f,
//							Animation.RELATIVE_TO_SELF, 0.5f);
//					anim.setDuration(200);
//					anim.setAnimationListener(new AnimationListener() {
//
//						@Override
//						public void onAnimationEnd(Animation arg0) {
//							// TODO Auto-generated method stub
//							Message msg = new Message();
//							msg.what = ANIMATION_END;
//							mHandler.sendMessageDelayed(msg, 500);
//						}
//
//						@Override
//						public void onAnimationRepeat(Animation arg0) {
//							// TODO Auto-generated method stub
//
//						}
//
//						@Override
//						public void onAnimationStart(Animation arg0) {
//							// TODO Auto-generated method stub
//
//						}
//
//					});
//					dialog_remember.startAnimation(anim);
//
//					if (current_type == Constants.LoginType.PHONE) {
//						SharedPreferencesManager.getInstance()
//								.putIsRememberPass(mContext, false);
//					} else {
//						SharedPreferencesManager.getInstance()
//								.putIsRememberPass_email(mContext, false);
//					}
//
//					remember_pwd_img
//							.setImageResource(R.drawable.login_autocb_nor);
//				} else {
//					TextView dialog_text = (TextView) dialog_remember
//							.findViewById(R.id.dialog_text);
//					ImageView dialog_img = (ImageView) dialog_remember
//							.findViewById(R.id.dialog_img);
//					dialog_img.setImageResource(R.drawable.ic_remember_pwd);
//					dialog_text.setText(R.string.rem_pass);
//					dialog_text.setGravity(Gravity.CENTER);
//					dialog_remember.setVisibility(View.VISIBLE);
//					Animation anim = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1f,
//							Animation.RELATIVE_TO_SELF, 0.5f,
//							Animation.RELATIVE_TO_SELF, 0.5f);
//					anim.setDuration(200);
//					anim.setAnimationListener(new AnimationListener() {
//
//						@Override
//						public void onAnimationEnd(Animation arg0) {
//							// TODO Auto-generated method stub
//							Message msg = new Message();
//							msg.what = ANIMATION_END;
//							mHandler.sendMessageDelayed(msg, 500);
//						}
//
//						@Override
//						public void onAnimationRepeat(Animation arg0) {
//							// TODO Auto-generated method stub
//
//						}
//
//						@Override
//						public void onAnimationStart(Animation arg0) {
//							// TODO Auto-generated method stub
//
//						}
//
//					});
//					dialog_remember.startAnimation(anim);
//					if (current_type == Constants.LoginType.PHONE) {
//						SharedPreferencesManager.getInstance()
//								.putIsRememberPass(mContext, true);
//						
//					} else {
//						SharedPreferencesManager.getInstance()
//								.putIsRememberPass_email(mContext, true);
//					}
//					remember_pwd_img
//							.setImageResource(R.drawable.login_autocb_press);
//
//				}
//			
//			break;
//		case R.id.name_clear:		
//			mAccountName.setText("");
//			break;
//		case R.id.pwd_clear:
//			mAccountPwd.setText("");
//			break;
//		}
	}

	private Handler mHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case ANIMATION_END:
				Animation anim_on = new ScaleAnimation(1.0f, 0.1f, 1.0f, 0.1f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				anim_on.setDuration(300);
				dialog_remember.startAnimation(anim_on);
				dialog_remember.setVisibility(View.GONE);
				break;
			case ACCOUNT_NO_EXIST:
				T.showShort(mContext, R.string.account_no_exist);
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				break;
			default:
				break;
			}

			return false;
		}
	});

	
	@Override
	public void onBackPressed() {
		super.isGoExit(true);
		this.finish();
	}

	private void login() {
		
//		try {
			mInputName = mAccountName.getText().toString().trim();
			mInputPwd = mAccountPwd.getText().toString().trim();
			if ((mInputName != null && !mInputName.equals(""))
					&& (mInputPwd != null && !mInputPwd.equals(""))) {
				if (mInputName.contains("@")) {
					
					current_type = Constants.LoginType.EMAIL;
				}else {
					current_type = Constants.LoginType.PHONE;
				}
				if (null != dialog && dialog.isShowing()) {
					Log.e("my", "isShowing");
					return;
				}
				dialog = new NormalDialog(mContext);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						// TODO Auto-generated method stub
						isDialogCanel = true;
					}

				});
				dialog.setTitle(mContext.getResources().getString(
						R.string.login_ing));
				dialog.showLoadingDialog();
				dialog.setCanceledOnTouchOutside(false);
				isDialogCanel = false;

				if (current_type == Constants.LoginType.PHONE) {
					
//					String name = dfault_count.getText().toString() + "-"
//							+ mInputName;
					String name = "+86"+ "-"
							+ mInputName;
					try {
						new LoginTask(name, mInputPwd).execute();						
					} catch (Exception e) {
						// TODO: handle exception
						Log.e("343", Log.getStackTraceString(e));
					}
				} else {
					
					if (Utils.isNumeric(mInputName)) {
						if (mInputName.charAt(0) != '0') {
							mHandler.sendEmptyMessage(ACCOUNT_NO_EXIST);
							return;
						}
						new LoginTask(mInputName, mInputPwd).execute();
					} else {
						new LoginTask(mInputName, mInputPwd).execute();
					}
				}

			} else {
				if ((mInputName == null || mInputName.equals(""))
						&& (mInputPwd != null && !mInputPwd.equals(""))) {
					T.showShort(mContext, R.string.input_account);
				} else if ((mInputName != null && !mInputName.equals(""))
						&& (mInputPwd == null || mInputPwd.equals(""))) {
					T.showShort(mContext, R.string.input_password);
				} else {
					T.showShort(mContext, R.string.input_tip);
				}
			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			Log.e("343","错误："+Log.getStackTraceString(e));
//			
//		}
	
		
	}

	class LoginTask extends AsyncTask {
		String username;
		String password;

		public LoginTask(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Utils.sleepThread(1000);
			return NetManager.getInstance(mContext).login(username, password);
		}

		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			LoginResult result = NetManager
					.createLoginResult((JSONObject) object);
			switch (Integer.parseInt(result.error_code)) {
			case NetManager.SESSION_ID_ERROR:
				Intent i = new Intent();
				i.setAction(Constants.Action.SESSION_ID_ERROR);
				MyApp.app.sendBroadcast(i);
				break;
			case NetManager.CONNECT_CHANGE:
				new LoginTask(username, password).execute();
				return;
			case NetManager.LOGIN_SUCCESS:
				
				try {
					
				
				if (isDialogCanel) {
					return;
				}
				if (null != dialog) {
					dialog.dismiss();
					dialog = null;
				}

				if (current_type == Constants.LoginType.PHONE) {
					
					SharedPreferencesManager.getInstance()
							.putData(mContext,
									SharedPreferencesManager.SP_FILE_GWELL,
									SharedPreferencesManager.KEY_RECENTNAME,
									mInputName);
					SharedPreferencesManager.getInstance().putData(mContext,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTPASS, mInputPwd);
					String code = "+86";
					code = code.substring(1, code.length());
					SharedPreferencesManager.getInstance().putData(mContext,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTCODE, code);
					SharedPreferencesManager.getInstance().putRecentLoginType(
							mContext, Constants.LoginType.PHONE);
				} else {
					SharedPreferencesManager.getInstance().putData(mContext,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTNAME_EMAIL,
							mInputName);
					SharedPreferencesManager.getInstance().putData(mContext,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTPASS_EMAIL,
							mInputPwd);
					SharedPreferencesManager.getInstance().putRecentLoginType(
							mContext, Constants.LoginType.EMAIL);
				}

				String codeStr1 = String.valueOf(Long.parseLong(result.rCode1));
				String codeStr2 = String.valueOf(Long.parseLong(result.rCode2));
				Account account = AccountPersist.getInstance()
						.getActiveAccountInfo(mContext);
				if (null == account) {
					account = new Account();
				}
				account.three_number = result.contactId;
				account.phone = result.phone;
				account.email = result.email;
				account.sessionId = result.sessionId;
				account.rCode1 = codeStr1;
				account.rCode2 = codeStr2;
				account.countryCode = result.countryCode;
				AccountPersist.getInstance()
						.setActiveAccount(mContext, account);
				NpcCommon.mThreeNum = AccountPersist.getInstance()
						.getActiveAccountInfo(mContext).three_number;
				
				Intent login = new Intent(mContext, MainActivity.class);
				startActivity(login);
				
				((LoginActivity) mContext).finish();
				} catch (Exception e) {
					// TODO: handle exception
					Log.e("343", Log.getStackTraceString(e));
				}
				break;
			case NetManager.LOGIN_USER_UNEXIST:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					T.showShort(mContext, R.string.account_no_exist);
				}
				break;
			case NetManager.LOGIN_PWD_ERROR:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					T.showShort(mContext, R.string.password_error);
				}
				break;
			default:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (!isDialogCanel) {
					T.showShort(mContext, R.string.loginfail);
				}
				break;
			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (isRegFilter) {
			isRegFilter = false;
			// mContext.unregisterReceiver(mReceiver);
		}
	}

	
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_LOGINACTIVITY;
	}

}
