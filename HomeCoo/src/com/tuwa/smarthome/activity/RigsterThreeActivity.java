package com.tuwa.smarthome.activity;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.SimpleListDialog.onSimpleListItemClickListener;
import com.tuwa.smarthome.dao.UserDao;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.User;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.MD5Security16;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.VerifyUtils;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class RigsterThreeActivity extends BaseActivity implements OnClickListener,
TextWatcher, onSimpleListItemClickListener {
	private String username, password, comfirmpwd, phoneno, email, realname,
	      address;
    private EditText etUserName, etPassWord, etConfirmPwd, etEmail, etAddress,
	      etRealName;
    private TextView tvSubmit, tvBack, tvTitle;
    private User mUser;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_register);
		
		initView();
		initData();
	}


	private void initView() {

		tvBack = (TextView)findViewById(R.id.tv_head_back);
		tvSubmit = (TextView)findViewById(R.id.tv_head_submit);

		etUserName = (EditText)findViewById(R.id.et_username);
		etPassWord = (EditText)findViewById(R.id.et_password);
		etConfirmPwd = (EditText)findViewById(R.id.et_confirmpwd);
	
		etEmail = (EditText)findViewById(R.id.et_email);
		etRealName = (EditText)findViewById(R.id.et_realname);
		etAddress = (EditText)findViewById(R.id.et_address);

		tvTitle = (TextView)findViewById(R.id.tv_head_title);
		tvTitle.setText("注册信息");
	}
	
	private void initData() {
		etUserName.setText(SystemValue.phonenum); // 用户名默认填充手机号

		tvBack.setOnClickListener(this);
		tvSubmit.setOnClickListener(this); // 提交

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_head_back:
			Intent regOneIntent = new Intent(RigsterThreeActivity.this,RigsterTwoActivity.class);
			startActivity(regOneIntent);
			finish();
			//设置切换动画，从右边进入，左边退出
	        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
			break;

		case R.id.tv_head_submit:
			submitInfoToServer();
			break;
		default:
			break;
		}

	}

	/**
	 * 校验并提交注册信息到服务器
	 */
	public void submitInfoToServer() {
		username = etUserName.getText().toString().trim();
		password = etPassWord.getText().toString().trim();
		comfirmpwd = etConfirmPwd.getText().toString().trim();
	
		email = etEmail.getText().toString().trim();
		realname = etRealName.getText().toString().trim();
		address = etAddress.getText().toString().trim();
		if (username.length() < 6 || username.length() > 18) {
			ToastUtils.showToast(RigsterThreeActivity.this,"账号应为6至18位数字或字母", 1000);
			etUserName.requestFocus();
		} else if (!VerifyUtils.matchAccount(username)) {
			ToastUtils.showToast(RigsterThreeActivity.this,"账号格式错误", 1000);
			etUserName.requestFocus();
		} else if (password.equals("")) {
			ToastUtils.showToast(RigsterThreeActivity.this,"请填写密码", 1000);
		} else if (password.length() < 6 || password.length() > 18) {
			ToastUtils.showToast(RigsterThreeActivity.this,"密码格式错误", 1000);
		} else if (comfirmpwd.equals("")) {
			ToastUtils.showToast(RigsterThreeActivity.this,"请再次输入密码", 1000);
			etConfirmPwd.requestFocus();

		} else if (!password.equals(comfirmpwd)) {
			etConfirmPwd.setText(null);
			etConfirmPwd.requestFocus();
			ToastUtils.showToast(RigsterThreeActivity.this,"两次输入的密码不一致", 1000);
		} else if (!email.equals("")) {
//			showCustomToast("请输入邮箱地址");
//			etEmail.requestFocus();
//		} else if (!VerifyUtils.matchEmail(email)) {
			if(!VerifyUtils.matchEmail(email)){
				ToastUtils.showToast(RigsterThreeActivity.this,"邮箱地址格式错误", 1000);
				etEmail.requestFocus();
			}
			
		} else {
			//手机号格式化为邮箱，注册摄像机用户
			sendRegisterCameraBroadcase(username,password);
			
			String pwdMd5 ="";   //先加密后再传到服务器
			try {
				pwdMd5=MD5Security16.md5_16(password);
				System.out.println("====md5===="+pwdMd5);
			} catch (Exception e) {
				e.printStackTrace();
			}

			User user = new User();
			user.setPhonenum(SystemValue.phonenum);
			user.setPassword(pwdMd5);
			user.setGatewayNo("");
			user.setRealname(realname);
			user.setEmail(email);
			user.setAddress(address);

			mUser = user;
			registerUserToServer(user);

		}

	}

    /**
     * 发送广播给homecoolink注册摄像机账号
     * @param username
     * @param password
     */
	private void sendRegisterCameraBroadcase(String username, String password) {
		// TODO Auto-generated method stub
		    Intent intent = new Intent();  //Itent就是我们要发送的内容
		    intent.putExtra("username", username);  
		    intent.putExtra("passwd", password);
		    intent.setAction("com.homecoolink.rigsterFlag");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
		    sendBroadcast(intent);   //发送广播
		    System.out.println("发送注册摄像机广播...");
	}


	/**
	 * 发送用户信息到服务器
	 * 
	 * @param user
	 */
	private void registerUserToServer(User user) {
		Gson gson = new Gson();
		String juser = gson.toJson(user);

		RequestParams params = new RequestParams();
		params.addBodyParameter("userJson", juser);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.USERREGISTER_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ToastUtils.showToast(RigsterThreeActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
						// 备注：result success/fail ;展示 message

						if (message != null) {
							if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
								
								ToastUtils.showToast(RigsterThreeActivity.this,"恭喜你注册成功！", 1000);
								new UserDao(RigsterThreeActivity.this).insertOrUpdateUser(mUser);
								
								Intent regOneIntent = new Intent(RigsterThreeActivity.this,LoginActivity.class);
								startActivity(regOneIntent);
								finish();
								overridePendingTransition(R.anim.out_to_right,
									  	 R.anim.in_from_left);
							} else {
								ToastUtils.showToast(RigsterThreeActivity.this,message.getMessageInfo(), 1000);
							}
						}

						// if (result.equals("true")) {
						// String message=myjObject.getString("message");
						// int userid=myjObject.getInt("userid");
						// User muser=new
						// UserDao(UserRegisterActivity.this).getUser(username,
						// password);
						// if (muser==null) { //服务器端注册，同步用户信息到本地数据库
						// user.setUserid(userid);
						// new UserDao(this).add(user);
						// }
						// showCustomToast(message);
						// finish();
						// overridePendingTransition(R.anim.out_to_right,
						// R.anim.in_from_left);
						//
						// }else {
						// String errors=myjObject.getString("errors");
						// showCustomToast(errors);
						// }
					}
				});
	}


	@Override
	public void onItemClick(int position) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void initDatas() {
		// TODO Auto-generated method stub
		
	}

}
