package com.tuwa.smarthome.activity;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.BaseDialog;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.SimpleListDialog;
import com.tuwa.smarthome.R.anim;
import com.tuwa.smarthome.R.id;
import com.tuwa.smarthome.R.layout;
import com.tuwa.smarthome.R.string;
import com.tuwa.smarthome.SimpleListDialog.onSimpleListItemClickListener;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.TextUtils;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.view.HandyTextView;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RigsterTwoActivity extends BaseActivity implements OnClickListener,
TextWatcher, onSimpleListItemClickListener {
	private TextView tvTitle,tvBack,tvSubmit;
	
	private HandyTextView mHtvPhoneNumber;
	private EditText mEtVerifyCode;
	private Button mBtnResend;
	private HandyTextView mHtvNoCode;
	private BaseDialog mBaseDialog;

	private static final String PROMPT = "验证码已经发送到* ";
	private static final String DEFAULT_VALIDATE_CODE = "123123";

	private boolean mIsChange = true;
	private String mVerifyCode;

	private int mReSendTime = 60;

	private boolean verifyFlag;   //服务器返回的验证结果

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mReSendTime > 1) {
				mReSendTime--;
				mBtnResend.setEnabled(false);
				mBtnResend.setText("重发(" + mReSendTime + ")");
				handler.sendEmptyMessageDelayed(0, 1000);
			} else {
				mReSendTime = 60;
				mBtnResend.setEnabled(true);
				mBtnResend.setText("重    发");
				
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_verify);
		
		initView();
		initData();
	}


	private void initView() {

		handler.sendEmptyMessage(0);

		tvTitle = (TextView)findViewById(R.id.tv_head_title);
		tvBack = (TextView) findViewById(R.id.tv_head_back);
		tvSubmit = (TextView)findViewById(R.id.tv_head_submit);
		
		tvTitle.setText("注册账号");
		tvBack.setText("返回");
		tvSubmit.setText("下一步");
		
		mHtvPhoneNumber = (HandyTextView)findViewById(R.id.reg_verify_htv_phonenumber);
		mHtvPhoneNumber.setText(PROMPT + SystemValue.phonenum);
		mEtVerifyCode = (EditText)findViewById(R.id.reg_verify_et_verifycode);
		mBtnResend = (Button)findViewById(R.id.reg_verify_btn_resend);
		mBtnResend.setEnabled(false);
		mBtnResend.setText("重发(60)");
		mHtvNoCode = (HandyTextView)findViewById(R.id.reg_verify_htv_nocode);
		TextUtils.addUnderlineText(this, mHtvNoCode, 0, mHtvNoCode
				.getText().toString().length());
	}
	
	private void initData() {
		tvBack.setOnClickListener(this);
		tvSubmit.setOnClickListener(this);
		
		mBtnResend.setOnClickListener(this);
		mHtvNoCode.setOnClickListener(this);
		mEtVerifyCode.addTextChangedListener(this);
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_head_back:
			
			Intent regOneIntent = new Intent(RigsterTwoActivity.this,RigsterOneActivity.class);
			startActivity(regOneIntent);
			finish();
			//设置切换动画，从右边进入，左边退出
	        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
			break;
			
		case R.id.tv_head_submit:
			if (validate()) {
				verifyTask = new VerifyCodeTask();
				verifyTask.execute(mVerifyCode);
			}
			
			break;
			
		case R.id.reg_verify_btn_resend:
			handler.sendEmptyMessage(0);
			getVertifyCodeFromServer(SystemValue.phonenum);
			System.out.println("点击了重新发送验证码到手机。。。");
			break;
		}
		
	}
	
	public boolean validate() {
		if (mEtVerifyCode.equals(null)) {
			ToastUtils.showToast(RigsterTwoActivity.this,"请输入验证码", 1000);
			mEtVerifyCode.requestFocus();
			return false;
		}
		mVerifyCode = mEtVerifyCode.getText().toString().trim();
		return true;
	}
	
	class VerifyCodeTask extends AsyncTask<String, Void, Boolean>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingDialog("正在验证,请稍后...");
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
				
			getVertifyResultFromServer(params[0]);
			
			return true;
		}
		
//		@Override
//		protected void onPostExecute(Boolean result) {
//			super.onPostExecute(result);
////			dismissLoadingDialog();
////			if (result) {
////				mIsChange = false;
////				UserRegisterActivity activity = (UserRegisterActivity) mActivity;
////				activity.showRegisterInfoFragment();
////			} else {
////				mBaseDialog = BaseDialog.getDialog(mActivity, "提示", "验证码错误",
////						"确认", new DialogInterface.OnClickListener() {
////
////							@Override
////							public void onClick(DialogInterface dialog,
////									int which) {
////								mEtVerifyCode.requestFocus();
////								dialog.dismiss();
////							}
////
////						});
////				mBaseDialog.show();
////			}
//		}
		
	}
	
	/**
	 * 从服务器获取校验验证码结果
	 * @param phone
	 */
	public void getVertifyResultFromServer(String verifycode) {
		verifyFlag = false;
		
		RequestParams params=new RequestParams("utf-8");
		params.addBodyParameter("phonenum",SystemValue.phonenum);
		params.addBodyParameter("identifyCode",verifycode);
		params.addBodyParameter("smsCodeType","1");

		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.POST, NetValue.VERIFY_CODE_URL,params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissLoadingDialog();
				System.out.println("===访问服务器失败返回的值为==="+arg1);
				ToastUtils.showToast(RigsterTwoActivity.this,"请检查网络连接！", 1000);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				dismissLoadingDialog();
				Gson gson=new Gson();
				ResultMessage message=gson.fromJson(arg0.result, ResultMessage.class);
			   if (message!=null) {
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
						mIsChange = false;
						
						Intent regOneIntent = new Intent(RigsterTwoActivity.this,RigsterThreeActivity.class);
						startActivity(regOneIntent);
						finish();
						//设置切换动画，从右边进入，左边退出
//				        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
						
					}else {

//						mBaseDialog = BaseDialog.getDialog(mActivity, "提示", "验证码错误",
//						mBaseDialog = BaseDialog.getDialog(getActivity(), "提示", "验证码错误",
//								"确认", new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										mEtVerifyCode.requestFocus();
//										dialog.dismiss();
//									}
//
//								});
//						mBaseDialog.show();
						ToastUtils.showToast(RigsterTwoActivity.this, "验证码错误!", 2000);
						mEtVerifyCode.requestFocus();
					}
			   }
			}
		});
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mIsChange = true;
	}
	


	private VerifyCodeTask verifyTask;


	@Override
	public void onItemClick(int position) {
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
	
	/**
	 * 发送手机号给服务器发送验证码到对应的手机号
	 * 
	 * @param phone
	 */
	public void getVertifyCodeFromServer(String phoneno) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phoneno);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.GETCODE_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						dismissLoadingDialog();
						ToastUtils.showToast(RigsterTwoActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						dismissLoadingDialog();
						System.out.println("服务器返回" + arg0.result);

						Gson gson = new Gson();
						try {
							ResultMessage message = gson.fromJson(arg0.result,
									ResultMessage.class);
							if (message != null) {
								if (message.getResult().equals(
										NetValue.SUCCESS_MESSAGE)) {
								    ToastUtils.showToast(RigsterTwoActivity.this,"已发送验证码到对应手机！", 1000);
								} else {
									ToastUtils.showToast(RigsterTwoActivity.this,message.getMessageInfo(), 1000);
								}
							}
						} catch (Exception e) {
							System.out.println("json解析异常");
						}
					}
				});
	}

}
