package com.homecoolink;


import org.json.JSONObject;

import com.homecoolink.activity.MainActivity;
import com.homecoolink.data.DataManager;
import com.homecoolink.data.SharedPreferencesManager;
import com.homecoolink.entity.Account;
import com.homecoolink.fragment.ContactFrag;
import com.homecoolink.global.AccountPersist;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.CrashHandler;
import com.homecoolink.utils.Utils;
import com.p2p.core.P2PHandler;
import com.p2p.core.network.GetAccountInfoResult;
import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;
import com.p2p.core.network.RegisterResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
/**
 * 接收主工程的广播
 * @author WSN-520
 *
 */
public class RigsterBroadcast extends BroadcastReceiver {
	private Context mcontext;
	private ContactFrag contactFrag;

	@Override
	public void onReceive(Context context, Intent arg1) {
		mcontext=context;
		String email_str=arg1.getStringExtra("username")+"@homecoo.com.cn";
		String pwd_str=arg1.getStringExtra("passwd");
		
		String action=arg1.getAction();
		if(action.equals("com.homecoolink.rigsterFlag")){
			System.out.println("收到了注册广播！"+email_str+pwd_str);
			new RegisterTask("1", email_str, "", "", pwd_str, pwd_str,
					"", "1").execute();
		}else if(action.equals("com.homecoolink.loginFlag")){
			Toast.makeText(mcontext, "正在加载摄像机画面，请稍后...", 1000).show();
//			email_str="18679451786@homecoo.com.cn";
//			pwd_str="123456";
			new LoginTask(email_str, pwd_str).execute();
			
		}else if(action.equals("com.homecoolink.serverStart")){

			System.out.println("摄像机启动服务！");
//			email_str="18679451786@homecoo.com.cn";
//			pwd_str="123456";
			new ServerStartTask(email_str, pwd_str).execute();
			
			try {
				CrashHandler ch = CrashHandler.getInstance();
				ch.init(mcontext);

			} catch (Exception e) {
				// TODO: handle exception
				Log.e("343", Log.getStackTraceString(e));
			}
			
			DataManager.findAlarmMaskByActiveUser(mcontext, "");
			NpcCommon.verifyNetwork(mcontext);
			
			Account activeUser = AccountPersist.getInstance().getActiveAccountInfo(
					mcontext);

			if (activeUser != null) {
				NpcCommon.mThreeNum = activeUser.three_number;
			}
//			
			new FList();
			
			P2PHandler.getInstance().p2pInit(mcontext, new P2PListener(),
					new SettingListener());
			
			Intent service = new Intent(MyApp.MAIN_SERVICE_START);
			service.setPackage(mcontext.getPackageName());
			mcontext.startService(service);
			  
		
			
			if (null == contactFrag) {
				contactFrag = new ContactFrag();
			}
			
			new GetAccountInfoTask().execute();
			
		}
	
	}
	
	class GetAccountInfoTask extends AsyncTask {

		public GetAccountInfoTask() {
			System.out.println("摄像机启动GetAccountInfoTask！");
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				Utils.sleepThread(1000);
				Account account = AccountPersist.getInstance()
						.getActiveAccountInfo(mcontext);
				JSONObject jsonObject = NetManager.getInstance(mcontext)
						.getAccountInfo(NpcCommon.mThreeNum, account.sessionId);
				return jsonObject;
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("343", Log.getStackTraceString(e));
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object object) {
			// TODO Auto-generated method stub
			if (object == null) {
				GetAccountInfoResult result = NetManager
						.createGetAccountInfoResult((JSONObject) object);
				switch (Integer.parseInt(result.error_code)) {
				case NetManager.SESSION_ID_ERROR:
					Intent i = new Intent();
					i.setAction(Constants.Action.SESSION_ID_ERROR);
					MyApp.app.sendBroadcast(i);
					break;
				case NetManager.CONNECT_CHANGE:
					new GetAccountInfoTask().execute();
					return;
				case NetManager.GET_ACCOUNT_SUCCESS:
					try {
						String email = result.email;
						String phone = result.phone;
						Account account = AccountPersist.getInstance()
								.getActiveAccountInfo(mcontext);
						if (null == account) {
							account = new Account();
						}
						account.email = email;
						account.phone = phone;
						AccountPersist.getInstance().setActiveAccount(mcontext,
								account);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}

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
			return NetManager.getInstance(mcontext).register(VersionFlag, Email,
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
				System.out.println("===摄像机注册成功===");
				
				break;
				
			case NetManager.REGISTER_EMAIL_USED:
				System.out.println("===邮箱已经注册===");
				break;
				
//				T.showShort(context,
//						Utils.getResString(context, R.string.registersuccess));
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					if (!"".equals(Email)) {
//						
//						Intent i = new Intent();
//						i.setAction(Constants.Action.REPLACE_EMAIL_LOGIN);
//						i.putExtra("username", Email);
//						i.putExtra("password", Pwd);
//						context.sendBroadcast(i);
//						finish();
//					} else {
//						
//						Intent i = new Intent();
//						i.setAction(Constants.Action.REPLACE_PHONE_LOGIN);
//						i.putExtra("username", PhoneNO);
//						i.putExtra("password", Pwd);
//						i.putExtra("code", CountryCode);
//						context.sendBroadcast(i);
//						finish();
//					}
//				}
//				break;
//			case NetManager.REGISTER_EMAIL_USED:
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					Utils.showPromptDialog(context, R.string.prompt,
//							R.string.email_used);
//				}
//				break;
//			case NetManager.REGISTER_EMAIL_FORMAT_ERROR:
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					Utils.showPromptDialog(context, R.string.prompt,
//							R.string.email_format_error);
//				}
//				break;
//			case NetManager.REGISTER_PASSWORD_NO_MATCH:
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//
//				break;
//
//			default:
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					T.showShort(context, R.string.operator_error);
//				}
//				break;
//			}
		}

	}

	}
	
	class LoginTask extends AsyncTask{
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
			return NetManager.getInstance(mcontext).login(username, password);
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
				System.out.println("====切换登陆...======");
				new LoginTask(username, password).execute();
				return;
			case NetManager.LOGIN_SUCCESS:
				
				System.out.println("====摄像机登录成功======");
				
				try {
//		
					SharedPreferencesManager.getInstance().putData(mcontext,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTNAME_EMAIL,
							username);
					SharedPreferencesManager.getInstance().putData(mcontext,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTPASS_EMAIL,
							password);
					SharedPreferencesManager.getInstance().putRecentLoginType(
							mcontext, Constants.LoginType.EMAIL);

				String codeStr1 = String.valueOf(Long.parseLong(result.rCode1));
				String codeStr2 = String.valueOf(Long.parseLong(result.rCode2));
				Account account = AccountPersist.getInstance()
						.getActiveAccountInfo(mcontext);
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
						.setActiveAccount(mcontext, account);
				NpcCommon.mThreeNum = AccountPersist.getInstance()
						.getActiveAccountInfo(mcontext).three_number;
				
				Intent intent = new Intent(mcontext, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mcontext.startActivity(intent);
				
				System.out.println("跳转到mainActivity");
				
//				((LoginActivity) mcontext).finish();
				
				} catch (Exception e) {
					// TODO: handle exception
					Log.e("343", Log.getStackTraceString(e));
					System.out.println("抛出异常了。。。。");
				}
				break;
			case NetManager.LOGIN_USER_UNEXIST:
				System.out.println("====LOGIN_USER_UNEXIST======");
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					T.showShort(mContext, R.string.account_no_exist);
//				}
				break;
			case NetManager.LOGIN_PWD_ERROR:
				System.out.println("====LOGIN_PWD_ERROR======");
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					T.showShort(mContext, R.string.password_error);
//				}
				break;
//			default:
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					T.showShort(mContext, R.string.loginfail);
//				}
//				break;
			}
		}

	}
	
	
	class ServerStartTask extends AsyncTask{
		String username;
		String password;

		public ServerStartTask(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Utils.sleepThread(1000);
			return NetManager.getInstance(mcontext).login(username, password);
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
				System.out.println("====切换登陆...======");
				new ServerStartTask(username, password).execute();
				return;
			case NetManager.LOGIN_SUCCESS:
				
				System.out.println("====摄像机登录成功======");
				
				try {
//		
					SharedPreferencesManager.getInstance().putData(mcontext,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTNAME_EMAIL,
							username);
					SharedPreferencesManager.getInstance().putData(mcontext,
							SharedPreferencesManager.SP_FILE_GWELL,
							SharedPreferencesManager.KEY_RECENTPASS_EMAIL,
							password);
					SharedPreferencesManager.getInstance().putRecentLoginType(
							mcontext, Constants.LoginType.EMAIL);

				String codeStr1 = String.valueOf(Long.parseLong(result.rCode1));
				String codeStr2 = String.valueOf(Long.parseLong(result.rCode2));
				Account account = AccountPersist.getInstance()
						.getActiveAccountInfo(mcontext);
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
						.setActiveAccount(mcontext, account);
				NpcCommon.mThreeNum = AccountPersist.getInstance()
						.getActiveAccountInfo(mcontext).three_number;
				
				} catch (Exception e) {
					// TODO: handle exception
					Log.e("343", Log.getStackTraceString(e));
					System.out.println("抛出异常了。。。。");
				}
				break;
			case NetManager.LOGIN_USER_UNEXIST:
				System.out.println("====LOGIN_USER_UNEXIST======");
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					T.showShort(mContext, R.string.account_no_exist);
//				}
				break;
			case NetManager.LOGIN_PWD_ERROR:
				System.out.println("====LOGIN_PWD_ERROR======");
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					T.showShort(mContext, R.string.password_error);
//				}
				break;
//			default:
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				if (!isDialogCanel) {
//					T.showShort(mContext, R.string.loginfail);
//				}
//				break;
			}
		}

	}
	

	

}
