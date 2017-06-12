package com.tuwa.smarthome.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.homecoolink.global.MyApp;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.dao.GateWayDao;
import com.tuwa.smarthome.dao.UserDao;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.LoginUser;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.User;
import com.tuwa.smarthome.entity.UserServer;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.DatagramSocketPhoneServer;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.LocationService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.MD5Security16;
import com.tuwa.smarthome.util.PreferencesUtils;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.Utils;
import com.tuwa.smarthome.util.VerifyUtils;
import com.umeng.update.UmengUpdateAgent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;


public class LoginActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener , OnDismissListener {            
	// activity绑定service
	private SocketService socketService;
	private String isMemory = "";// isMemory变量用来判断SharedPreferences有没有数据，包括上面的YES和NO
	static String YES = "yes";
	static String NO = "no";
	static String strname, strpwd;
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;

	public int loginResult; // 用户登录结果
	public int netFlag = 0; // 0为内网，1是外网
	public int userid; // 用户id

	@Bind(R.id.bt_userRegister)
	Button mBtnRegister;
	@Bind(R.id.tv_login_exit)
	TextView tvLoginExit;
	@Bind(R.id.cb_remenmer_pwd)
	CheckBox cbRemenberPwd;
	private TextView btgetbackPwd;
	private User mpUser;

	private Button mBtnLogin;
	private EditText mEtUserName, mEtUserPwd;
	private String username, password, pwdMd5;
	private LocationService locationService;
	private String strRePasswd;
	private TextView tvWifiSet;
	
	//记住用户登录列表
	private ArrayList<LoginUser> mUsers; // 记住用户列表
	private ListView mUserIdListView; // 下拉弹出窗显示的ListView对象
	private MyAapter mAdapter; // ListView的监听器
	private PopupWindow mPop; // 下拉弹出窗
	private RelativeLayout mUserIdLinearLayout; // 将下拉弹出窗口在此容器下方显示
	private ImageView mMoreUser; // 下拉图标
	private ImageView mLoginMoreUserView; // 弹出下拉弹出窗的按钮
	

	/* 辅线程动态刷新页面 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x121:
				dismissLoadingDialog();
				String contentStr = (String) msg.obj;
				showLoadingDialog(contentStr);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homecoo_login);
		ButterKnife.bind(LoginActivity.this);

		// 友盟更新检测
		UmengUpdateAgent.update(this);

		// Activity和service绑定
		Intent service = new Intent(LoginActivity.this, SocketService.class);
		bindService(service, conn, Context.BIND_AUTO_CREATE);
//		Intent Datagramservice = new Intent(LoginActivity.this, DatagramSocketService.class);
//		this.startService(Datagramservice);
//		Intent datagramservice = new Intent(LoginActivity.this, DatagramSocketPhoneServer.class);
//		this.startService(datagramservice);
		
		initViews();
		initDatas();
	    getLatelyUserList();   //初始化最近登录的用户列表
	    restoreNameAndPwd();   //初始化记住用户名和密码
		initLocationService();  //初始化并启动位置服务

		// 极光初始化
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		JPushInterface.setLatestNotificationNumber(getApplicationContext(), 3);// 保留多少条通知数
		
	}

    /**
     * 加载最近登录的用户列表
     */
	private void getLatelyUserList() {
		 /* 获取已经保存好的用户列表*/
		mUsers = Utils.getUserList(LoginActivity.this);
		System.out.println("mUsers列表大小"+mUsers.size());
		if (mUsers.size() > 0) {
			/* 将列表中的第一个user显示在编辑框 */
			mEtUserName.setText(mUsers.get(0).getId());
//			mEtUserPwd.setText(mUsers.get(0).getPwd());
		}
		LinearLayout parent = (LinearLayout) getLayoutInflater().inflate(
				R.layout.userifo_listview, null);
		mUserIdListView = (ListView) parent.findViewById(android.R.id.list);
		parent.removeView(mUserIdListView); // 必须脱离父子关系,不然会报错
		mUserIdListView.setOnItemClickListener(this); // 设置点击事
		mAdapter = new MyAapter(mUsers);
		mUserIdListView.setAdapter(mAdapter);
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	// 获取SocketService实例对象
	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 返回一个SocketService对象
			socketService = ((SocketService.SocketBinder) service).getService();
		}
	};



	@Override  
	protected void initViews() {
		mEtUserName = (EditText) findViewById(R.id.et_username);
		mEtUserPwd = (EditText) findViewById(R.id.et_userpwd);
		mBtnLogin = (Button) findViewById(R.id.bt_userLogin1);
		btgetbackPwd = (TextView) findViewById(R.id.bt_getback_pwd);
		
		tvWifiSet=(TextView) findViewById(R.id.tv_wifi_set);
		tvWifiSet.setOnClickListener(this);

		mBtnLogin.setOnClickListener(loginOnClickListener);
		btgetbackPwd.setOnClickListener(getbackPwdOnClickListener);

		mLoginMoreUserView = (ImageView) findViewById(R.id.login_more_user);
		mMoreUser = (ImageView) findViewById(R.id.login_more_user);
	
		mUserIdLinearLayout = (RelativeLayout) findViewById(R.id.userId_LinearLayout);
	
	}

	@Override
	protected void initDatas() {
		mLoginMoreUserView.setOnClickListener(this);
		//保存最近登录的用户名和密码
		mEtUserName.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				username = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		mEtUserPwd.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				password = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});

	}

	// 用户注册
	@OnClick(R.id.bt_userRegister)
	public void userRegister() {
		Intent intent = new Intent(LoginActivity.this, RigsterOneActivity.class);
		startActivity(intent);
		finish();
	}

	// 用户登录
	private OnClickListener loginOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			login();  //登录验证方法
		}
	};
	
	/**
	 * 用户登录验证
	 */
	public void login(){
		username = mEtUserName.getText().toString().trim();
		password = mEtUserPwd.getText().toString().trim();
		SystemValue.userName="";
		SystemValue.passWord="";
		if (VerifyUtils.isEmpty(username)) {
			ToastUtils.showToast(LoginActivity.this,"请填写账号", 1000);
			mEtUserName.requestFocus();
		} else if (VerifyUtils.isEmpty(password)) {
			ToastUtils.showToast(LoginActivity.this,"请填写密码", 1000);
		} else if (!VerifyUtils.isMobileNO(username)) { // 校验作为手机号的用户名
			ToastUtils.showToast(LoginActivity.this,"账号格式错误", 1000);
			mEtUserName.requestFocus();
		} else if (mEtUserPwd.length() < 5) {
			ToastUtils.showToast(LoginActivity.this,"密码格式错误", 1000);
		} else {
		
			saveLatelyUserList();  //保存格式合法的用户列表
			
			// 对密码进行加密
			try {
				SystemValue.gatewayid = "";
				NetValue.LOCAL_IP = "";
				SystemValue.user = null;
				pwdMd5 = MD5Security16.md5_16(password);
			} catch (Exception e) {
				e.printStackTrace();
			}

			remenberNameAndPassword(); // 记住用户名和密码
			
			SystemValue.cameraPhone=username;  //传递给摄像机的用户名和密码
			SystemValue.cameraPwd=password;

			switchNetworkToLogin(username, pwdMd5);
			NetValue.callbackflag = true;
		}
	}


	/**
	 * 内外网登录切换
	 * 
	 * @param username
	 * @param password
	 */
	private void switchNetworkToLogin(String username, String password) {

		mpUser = new UserDao(LoginActivity.this).getUser(username, password);
		if (mpUser == null) { // 本地没有注册账号的信息,用户在其它的手机端注册的账号
			outernetUserLogin(username, pwdMd5); // 此为新手机没有原来的账号信息，外网登录验证
		} else {
			String phonenum = mpUser.getPhonenum();
			SystemValue.phonenum = phonenum;
			SystemValue.user = mpUser; // 将user对象保存为全局变量

			String gatewayNo = mpUser.getGatewayNo();

			if (!VerifyUtils.isEmpty(gatewayNo)) {
				SystemValue.gatewayid = gatewayNo; // 用户已经绑定网关，赋值为全局变量
				
				PreferencesUtils.putString(LoginActivity.this, "wgid",SystemValue.gatewayid);  //保存网关号为共享变量
				
				Gateway mgateWay = new GateWayDao(LoginActivity.this)
						.getGatewayByGatewayNo(gatewayNo); // 获取该手机号下对应的第一个网关
				if (mgateWay == null) { // 没有绑定的网关网关信息
					outernetUserLogin(username, pwdMd5); // 此为新手机没有原来的账号信息，外网登录验证
				} else {
					intranetUserLogin(mgateWay); // 获取到用户名对应的网关id,内网登录验证
				}
			} else {
				outernetUserLogin(username, pwdMd5); // 此为新手机没有原来的账号信息，外网登录验证
			}
		}
	}
	

	/**
	 * 内网用户连接登录
	 * 
	 * @param gatewayId
	 *            网关id
	 * @param mpassword
	 *            网关密码
	 */
	private void intranetUserLogin(Gateway mgateWay) {
		NetValue.LOCAL_IP = mgateWay.getGatewayIp();
		NetValue.authdata = mgateWay.getGatewayPwd();

		if (!NetValue.socketauthen) {
			// 网关id和ip存在，建立socket连接,发送新认证
			socketService.socketConnect(NetValue.LOCAL_IP);
		}

		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x121;
		msg.obj = "正在使用本地登录,请稍后...";
		handler.sendMessage(msg);

		socketService.callSocket(new SocketCallBack() {
			@Override
			public void callBack(TranObject tranObject) {
				mBtnLogin.setClickable(true); // 恢复按钮点击

				switch (tranObject.getTranType()) {
				case NETMSG:

					int netstatue = (Integer) tranObject.getObject();
					if ((NetValue.NONET == netstatue)) { // 本地连接失败
						loginResult = NetValue.IN_FAILURE; // 保存登录方式为内网失败
						dismissLoadingDialog();

						outernetUserLogin(username, pwdMd5); // 内网登录失败后切换到外网登录
					}
					break;
				case DEVMSG:
					intranetLoginVerify(tranObject); // 内网用户登录校验
				default:
					break;
				}
			}

		});

	}

	/**
	 * 内网用户登录校验
	 * 
	 * @param tranObject
	 */
	private void intranetLoginVerify(TranObject tranObject) {
		SocketPacket socketPacket = (SocketPacket) tranObject.getObject();
		if (socketPacket.getDataType() == NetValue.DATA_ACK_AUTH) {
			if (socketPacket.getData().equals("0")) { // 验证成功返回0
				dismissLoadingDialog();
				NetValue.netFlag = NetValue.INTRANET; // 保存当前的网络为内网

				Intent intent = new Intent(LoginActivity.this,
						HomeActivity.class);
				startActivity(intent);
				finish();
			} else {
				dismissLoadingDialog();
				ToastUtils.showToast(LoginActivity.this, "本地验证失败，请检查用户名和密码！",
						1000);
			}
		}
	}

	/**
	 * 用户外网连接登录
	 * 
	 * @param name
	 *            用户名
	 * @param pwd
	 *            密码
	 */
	private void outernetUserLogin(final String phonenum, String pwd) {

		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x121;
		msg.obj = "正在切换远程登录,请稍后...";
		handler.sendMessage(msg);

		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phonenum);
		params.addBodyParameter("password", pwd);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.LOGIN_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						mBtnLogin.setClickable(true);

						loginResult = NetValue.OUT_FAILURE; // 保存登录方式为内网失败
						dismissLoadingDialog();
						ToastUtils.showToast(LoginActivity.this,"远程登录失败，请检查网络！", 1000);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// 根据返回值校验登录是否成功
						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,
								ResultMessage.class);

						if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
							try {
								UserServer userServer = JSONObject.parseObject(
										(String) message.getObject(),UserServer.class);//备注：服务器的User带有时间戳
								User muser = new User();
								muser.setGatewayNo(userServer.getGatewayNo());
								muser.setPhonenum(userServer.getPhonenum());
								muser.setPassword(userServer.getPassword());

								new UserDao(LoginActivity.this).insertOrUpdateUser(muser); // 添加新用户

								NetValue.netFlag = NetValue.OUTERNET;
								SystemValue.phonenum = phonenum; // 用户验证通过，手机号赋为全局变量

								// 从服务器同步的网关号
								if (!VerifyUtils.isEmpty(userServer.getGatewayNo())) {
									SystemValue.gatewayid = userServer.getGatewayNo();
									PreferencesUtils.putString(LoginActivity.this, "wgid",SystemValue.gatewayid);  //保存网关号为共享变量
								}

								if (mpUser != null) {// 本地数据库存在用户信息
									String gatewayid = mpUser.getGatewayNo(); // 网关是在本地添加的，所以本地有网关的信息
									if (!VerifyUtils.isEmpty(gatewayid)) {
										SystemValue.gatewayid = gatewayid;
										PreferencesUtils.putString(LoginActivity.this, "wgid",SystemValue.gatewayid);  //保存网关号为共享变量
										// 请求所有设备的最新状态
										if (SystemValue.deviceSysnFlag == false) {
											// 手机端从网关认证通过，向网关请求所有设备最新状态
											sysnDeviceFromServer(socketService);
										}
									} else {
									}
								} else {
									// String gatewayNo=muser.getGatewayNo();
									// SystemValue.gatewayid=gatewayNo;
									// System.out.println("===服务器返回的网关号"+SystemValue.gatewayid);

								}

								dismissLoadingDialog();// finish,LoginActivity前一定要关闭Dilog
								Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
								startActivity(intent);
								finish();
							} catch (JsonSyntaxException e) {
								System.out.println("json解析错误！");
								e.printStackTrace();
							}

						} else {
							loginResult = NetValue.OUT_FAILURE; // 外网登录失败
							dismissLoadingDialog();
							ToastUtils.showToast(LoginActivity.this,message.getMessageInfo(), 1000);
						}
					}
				});
	}

	// 获取短信验证对话框
	public void getMsgFromServerDialog(String phone) {

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_dialog_identify,
				(ViewGroup) findViewById(R.id.dialog));
		final EditText etPhone = (EditText) layout
				.findViewById(R.id.et_phoneno);
		final TextView tvGetMsg = (TextView) layout
				.findViewById(R.id.tv_getMsg);
		final EditText etIdentifyCode = (EditText) layout
				.findViewById(R.id.et_identify_code);
		layout
				.findViewById(R.id.tr_identify_code);
		etPhone.setText(phone);
		tvGetMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phoneno = etPhone.getText().toString().trim();

				if (phoneno.equals("")) {
					etPhone.requestFocus();
					ToastUtils.showToast(LoginActivity.this,"手机号码不能为空", 1000);
				} else if (!VerifyUtils.isMobileNO(phoneno)) {
					ToastUtils.showToast(LoginActivity.this,"手机号码格式错误", 1000);
					etPhone.requestFocus();
				} else { // 空间列表和用户关联表中都没有此设备

					// 填写手机号发送到服务器
					getRePasswordCodeFromServer(phoneno); // 获取验证码
				}

			}
		});

		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("短信验证").setView(layout);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 点击确定对话框不消失
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String phoneno = etPhone.getText().toString().trim();
				String identifycode = etIdentifyCode.getText().toString()
						.trim();
				// 发送验证码到服务器
				getVertifyResultFromServer(phoneno, identifycode);

				try { // 点击确定对话框消失
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).show();
	}

	/**
	 * 请求服务器发送重置密码的验证码到对应的手机
	 * 
	 * @param phonenum
	 */
	public void getRePasswordCodeFromServer(String phonenum) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phonenum);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.GET_REPWDCODE_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 访问网络失败
						ToastUtils.showToast(LoginActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Gson gson = new Gson();
						try {
							ResultMessage message = gson.fromJson(arg0.result,
									ResultMessage.class);
							if (message != null) {
								if (message.getResult().equals(
										NetValue.SUCCESS_MESSAGE)) {
									ToastUtils.showToast(LoginActivity.this,
											message.getMessageInfo(), 1000);
								} else {
									ToastUtils.showToast(LoginActivity.this,
											message.getMessageInfo(), 1000);
								}
							}
						} catch (Exception e) {
							System.out.println("json解析异常");
						}
					}
				});
	}

	/**
	 * 从服务器获取校验验证码结果
	 * 
	 * @param phone
	 */
	public void getVertifyResultFromServer(final String phonenum,
			String verifycode) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phonenum);
		params.addBodyParameter("identifyCode", verifycode);
		params.addBodyParameter("smsCodeType", "2"); // 重置验证码的类型为2

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.VERIFY_CODE_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						dismissLoadingDialog();
						ToastUtils.showToast(LoginActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						dismissLoadingDialog();
						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,
								ResultMessage.class);
						if (message != null) {
							if (message.getResult().equals(
									NetValue.SUCCESS_MESSAGE)) {
								ToastUtils.showToast(LoginActivity.this,
										message.getMessageInfo(), 1000);
								// 重置密码对话框
								resetPasswordToServerDialog(phonenum);
							} else {
								ToastUtils.showToast(LoginActivity.this,
										message.getMessageInfo(), 1000); // 本对话框一直显示
								getMsgFromServerDialog(phonenum);

							}
						}
					}
				});
	}

	// 重置密码对话框
	public void resetPasswordToServerDialog(final String phone) {

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_dialog_resetpasswd,
				(ViewGroup) findViewById(R.id.dialog));
		final EditText etLoginCode = (EditText) layout
				.findViewById(R.id.et_logincode);
		final EditText etPassword = (EditText) layout
				.findViewById(R.id.et_passwd);
		final EditText etRePassword = (EditText) layout
				.findViewById(R.id.et_repasswd);
		etLoginCode.setText(phone);
		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("密码重置").setView(layout);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 点击确定对话框不消失
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String phonenum = etLoginCode.getText().toString().trim();
				String password = etPassword.getText().toString().trim();
				String comfirmpwd = etRePassword.getText().toString().trim();

				if (!VerifyUtils.isMobileNO(phonenum)) {
					ToastUtils.showToast(LoginActivity.this,"账号格式错误", 1000);
					etLoginCode.requestFocus();
				} else if (password.length() < 5) {
					ToastUtils.showToast(LoginActivity.this,"密码格式错误", 1000);
					etPassword.requestFocus();
				} else if (!password.equals(comfirmpwd)) {
					etRePassword.setText(null);
					etRePassword.requestFocus();
					ToastUtils.showToast(LoginActivity.this,"两次输入的密码不一致", 1000);
				} else {
					try {// 点击确定对话框消失
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 重置密码到服务器
					resetPasswordToServer(phonenum, password, dialog);
				}
			}

		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).show();
	}

	// 重置用户名和密码到服务器
	private void resetPasswordToServer(final String phonenum, String passwd,
			final DialogInterface dialog) {

		// 对密码进行加密

		try {
			strRePasswd = MD5Security16.md5_16(passwd);
		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phonenum);
		params.addBodyParameter("newPassword", strRePasswd);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);

		utils.send(HttpMethod.POST, NetValue.UPDATE_REPWDCODE_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 访问网络失败
						ToastUtils.showToast(LoginActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,
								ResultMessage.class);
						if (message != null) {
							if (message.getResult().equals(
									NetValue.SUCCESS_MESSAGE)) {
								ToastUtils.showToast(LoginActivity.this,
										message.getMessageInfo(), 1000);
								// 重置密码成功后，修改本地密码
								User muser = new User();
								muser.setPhonenum(phonenum);
								muser.setPassword(strRePasswd);
								new UserDao(LoginActivity.this)
										.updateUserByPhonenum(muser);

								try {// 验证码正确，点击确定对话框消失
									Field field = dialog.getClass()
											.getSuperclass()
											.getDeclaredField("mShowing");
									field.setAccessible(true);
									field.set(dialog, true);
								} catch (Exception e) {
									e.printStackTrace();
								}

							} else {
								ToastUtils.showToast(LoginActivity.this,
										message.getMessageInfo(), 1000); // 本对话框一直显示
							}
						}

					}
				});

	}

	// remenber方法用于判断是否记住密码，checkBox1选中时，提取出EditText里面的内容，放到SharedPreferences里面的name和password中
	public void remenberNameAndPassword() {
		if (cbRemenberPwd.isChecked()) {
			if (preferences == null) {
				preferences = getSharedPreferences("tuwa", MODE_PRIVATE);
			}
			Editor edit = preferences.edit();
			edit.putString("name", mEtUserName.getText().toString());
			edit.putString("password", mEtUserPwd.getText().toString());
			edit.putString("isMemory", YES);
			edit.commit();
		} else if (!cbRemenberPwd.isChecked()) {
			if (preferences == null) {
				preferences = getSharedPreferences("tuwa", MODE_PRIVATE);
			}
			Editor edit = preferences.edit();
			edit.putString("isMemory", NO);
			edit.commit();
		}
	}

	//保存格式合法的登录用户列表
	protected void saveLatelyUserList() {
	
		boolean mIsSave = true;
		try {
			for (LoginUser user : mUsers) { // 判断本地文档是否有此ID用户
				if (user.getId().equals(username)) {
					mIsSave = false;
					break;
				}
			}
			if (mIsSave) { // 将新用户加入users
				LoginUser user = new LoginUser(username, password);
				mUsers.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		

	// 退出点击事件
	@OnClick(R.id.tv_login_exit)
	public void exit() {
		initExitDialog();
	}

	@Override
	protected void onDestroy() {
		unbindService(conn);
		super.onDestroy(); // 注意先后
	}


	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	
	private OnClickListener getbackPwdOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			getMsgFromServerDialog("");
		}
	};
	
	
	private void initLocationService() {
		
//		locationService = ((LocweatherApplication) getApplication()).locationService;  
		locationService=new LocationService(LoginActivity.this);
		
		// 获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		// 注册监听
		int type = getIntent().getIntExtra("from", 0);
		if (type == 0) {
			locationService.setLocationOption(locationService
					.getDefaultLocationClientOption());
		} else if (type == 1) {
			locationService.setLocationOption(locationService.getOption());
		}
		locationService.start();
	}
	
	
	/**
	 * 记住用户名和密码
	 */
	private void restoreNameAndPwd() {
		// 进入界面时，这个if用来判断SharedPreferences里面name和password有没有数据，有的话则直接打在EditText上面
		isMemory=PreferencesUtils.getString(LoginActivity.this,"isMemory",NO);
		if (isMemory.equals(YES)) {
			strname=PreferencesUtils.getString(LoginActivity.this,"name","");
			strpwd=PreferencesUtils.getString(LoginActivity.this,"password","");
			
			mEtUserName.setText(strname);
			mEtUserPwd.setText(strpwd);
			cbRemenberPwd.setChecked(true);
		}
		
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_wifi_set:
			Intent intent = new Intent(LoginActivity.this, WifiSetActivity.class);
			startActivity(intent);
		
			break;
		case R.id.login_more_user: // 当点击下拉栏
			if (mPop == null) {
				initPop();
			}
			if (!mPop.isShowing() && mUsers.size() > 0) {
				// Log.i(TAG, "切换为角向上图标");
				mMoreUser.setImageResource(R.drawable.login_more_down); // 切换图标
				mPop.showAsDropDown(mUserIdLinearLayout, 2, 1); // 显示弹出窗口
			}
			break;
		}
	}
	
	/*最近用户登录列表 ListView的适配器 */
	class MyAapter extends ArrayAdapter<LoginUser> {

		public MyAapter(ArrayList<LoginUser> users) {
			super(LoginActivity.this, 0, users);
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.listview_item, null);
			}

			TextView userIdText = (TextView) convertView
					.findViewById(R.id.listview_userid);
			userIdText.setText(getItem(position).getId());

			ImageView deleteUser = (ImageView) convertView
					.findViewById(R.id.login_delete_user);
			deleteUser.setOnClickListener(new OnClickListener() {
				// 点击删除deleteUser时,在mUsers中删除选中的元素
				@Override
				public void onClick(View v) {

					if (getItem(position).getId().equals(username)) {
						// 如果要删除的用户Id和Id编辑框当前值相等，则清空
						username = "";
						password = "";
						mEtUserName.setText(username);
						mEtUserPwd.setText(password);
					}
					mUsers.remove(getItem(position));
					mAdapter.notifyDataSetChanged(); // 更新ListView
				}
			});
			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mEtUserName.setText(mUsers.get(position).getId());
//		mEtUserPwd.setText(mUsers.get(position).getPwd());
		mEtUserPwd.setText("");  //切换账号后，密码置空
		mPop.dismiss();
	}

	/* PopupWindow对象dismiss时的事件 */
	@Override
	public void onDismiss() {
		// Log.i(TAG, "切换为角向下图标");
		mMoreUser.setImageResource(R.drawable.login_more_up);
	}

	/* 退出此Activity时保存users */
	@Override
	public void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
		try {
			Utils.saveUserList(LoginActivity.this, mUsers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initPop() {
		int width = mUserIdLinearLayout.getWidth() - 4;
		int height = LayoutParams.WRAP_CONTENT;
		mPop = new PopupWindow(mUserIdListView, width, height, true);
		mPop.setOnDismissListener(this);// 设置弹出窗口消失时监听器

		// 注意要加这句代码，点击弹出窗口其它区域才会让窗口消失
		mPop.setBackgroundDrawable(new ColorDrawable(0xffffffff));

	}

	
	/*****
	 * @see copy funtion to you project
	 *      定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 * 
	 */
	private BDLocationListener mListener = new BDLocationListener() {

		public void onReceiveLocation(BDLocation location) {
			// System.out.println("mListener>>>>>>>======");
			// TODO Auto-generated method stub
			if (null != location
					&& location.getLocType() != BDLocation.TypeServerError) {
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				/**
				 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
				 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
				 */
				sb.append(location.getTime()); // 当前时间年月日 时分秒
				sb.append("\nerror code : ");
				sb.append(location.getLocType());
				sb.append("\nlatitude : ");
				sb.append(location.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(location.getLongitude());
				sb.append("\nradius : ");
				sb.append(location.getRadius());
				sb.append("\nCountryCode : ");
				sb.append(location.getCountryCode());
				sb.append("\nCountry : ");
				sb.append(location.getCountry());
				sb.append("\ncitycode : ");
				sb.append(location.getCityCode());
				sb.append("\ncity : ");
				sb.append(location.getCity()); // 当前所在城市
				sb.append("\nDistrict : ");
				sb.append(location.getDistrict());
				sb.append("\nStreet : ");
				sb.append(location.getStreet());
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\nDescribe: ");
				sb.append(location.getLocationDescribe());
				sb.append("\nDirection(not all devices have value): ");
				sb.append(location.getDirection());
				sb.append("\nPoi: ");
				if (location.getPoiList() != null
						&& !location.getPoiList().isEmpty()) {
					for (int i = 0; i < location.getPoiList().size(); i++) {
						Poi poi = (Poi) location.getPoiList().get(i);
						sb.append(poi.getName() + ";");
					}
				}
				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());// 单位：km/h
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());
					sb.append("\nheight : ");
					sb.append(location.getAltitude());// 单位：米
					sb.append("\ndescribe : ");
					sb.append("gps定位成功");
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
					// 运营商信息
					sb.append("\noperationers : ");
					sb.append(location.getOperators());
					sb.append("\ndescribe : ");
					sb.append("网络定位成功");
				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
					sb.append("\ndescribe : ");
					sb.append("离线定位成功，离线定位结果也是有效的");
				} else if (location.getLocType() == BDLocation.TypeServerError) {
					sb.append("\ndescribe : ");
					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
					sb.append("\ndescribe : ");
					sb.append("网络不同导致定位失败，请检查网络是否通畅");
				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
					sb.append("\ndescribe : ");
					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
				}
				// logMsg(sb.toString());
				locationService.stop();

				if (location.getCity() != null) {
					SystemValue.city = location.getCity().substring(0,
							location.getCity().length() - 1);
					
					Log.i("343", "定位的城市为:"+SystemValue.city);
				}
			}
		}

	};
}
