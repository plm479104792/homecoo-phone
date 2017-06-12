package object.dbnewgo.client;

import object.dbnewgo.client.BridgeService.MailInterface;
import object.p2pipcam.bean.MailBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * �ʼ�����
 * */
public class SettingMailActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener, OnTouchListener,
		MailInterface {
	private String TAG = "SettingMailActivity";
	private String strDID;
	private final int FAIL = 0;
	private final int SUCCESS = 1;
	private final int PARAMS = 3;
	private String cameraName = null;
	private EditText editSender = null;
	private EditText editSmtpServer = null;
	private EditText editSmtpPort = null;
	private EditText editSSL = null;
	private EditText editReceiver1 = null;
	private EditText editReceiver2 = null;
	private EditText editReceiver3 = null;
	private EditText editReceiver4 = null;
	private ImageButton imgBtnSmtpServerDown = null;
	private ImageButton imgBtnSSLDown = null;
	private CheckBox cbxCheck = null;
	private View cbxView = null;
	private MailBean mailBean = null;
	private EditText editSmtpUser = null;
	private EditText editSmtpPwd = null;
	private PopupWindow smtpPopWindow = null;
	private PopupWindow sslPopWindow = null;
	private final int TIMEOUT = 3000;
	private boolean successFlag;
	private ProgressDialog progressDialog = null;
	private TextView tvCameraName = null;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FAIL:
				showToast(R.string.mail_setting_failed);
				break;
			case SUCCESS:
				showToast(R.string.mail_setting_success);
				finish();
				break;
			case PARAMS:
				successFlag = true;
				if (progressDialog.isShowing()) {
					progressDialog.cancel();
					editSender.setText(mailBean.getSender());
					editSmtpServer.setText(mailBean.getSvr());
					editReceiver1.setText(mailBean.getReceiver1());
					editReceiver2.setText(mailBean.getReceiver2());
					editReceiver3.setText(mailBean.getReceiver3());
					editReceiver4.setText(mailBean.getReceiver4());
					editSmtpPort.setText(String.valueOf(mailBean.getPort()));
					switch (mailBean.getSsl()) {
					case 0:// ������
						editSSL.setText("NONE");
						break;
					case 1:// SSL����
						editSSL.setText("SSL");
						break;
					case 2:// TSL����
						editSSL.setText("TLS");
						break;

					default:
						break;
					}
					Log.i("mailsetting", mailBean.getUser());
					if (!TextUtils.isEmpty(mailBean.getUser())) {
						Log.i("mailsetting", "����");
						mailBean.setChecked(true);
						cbxCheck.setChecked(true);
						cbxView.setVisibility(View.VISIBLE);
						editSmtpUser.setText(mailBean.getUser());
						editSmtpPwd.setText(mailBean.getPwd());
					} else {
						cbxView.setVisibility(View.GONE);
						mailBean.setChecked(false);
					}
				}
				break;

			}
		}
	};
	private Button btnOk;
	private Button btnCancel;

	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
		super.onPause();
	}

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settingmail);
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.mail_getparams));
		progressDialog.show();
		mHandler.postDelayed(runnable, TIMEOUT);
		findView();
		setListener();
		mailBean = new MailBean();
		BridgeService.setMailInterface(this);
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_PARAMS);
		showSmtpPopWindow();
		showSslPopWindow();
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				progressDialog.dismiss();
				// showToast(R.string.wifi_getparams_failed);
			}
		}
	};

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}

	private void findView() {
		editSender = (EditText) findViewById(R.id.mail_edit_sender);
		editSmtpServer = (EditText) findViewById(R.id.mail_edit_smtp_server);
		editSmtpPort = (EditText) findViewById(R.id.mail_edit_smtp_port);
		editSmtpUser = (EditText) findViewById(R.id.mail_edit_smtp_user);
		editSmtpPwd = (EditText) findViewById(R.id.mail_edit_smtp_pwd);

		editReceiver1 = (EditText) findViewById(R.id.mail_edit_receiver1);
		editReceiver2 = (EditText) findViewById(R.id.mail_edit_receiver2);
		editReceiver3 = (EditText) findViewById(R.id.mail_edit_receiver3);
		editReceiver4 = (EditText) findViewById(R.id.mail_edit_receiver4);

		editSSL = (EditText) findViewById(R.id.mail_edit_ssl);

		imgBtnSmtpServerDown = (ImageButton) findViewById(R.id.mail_img_smtp_down);
		imgBtnSSLDown = (ImageButton) findViewById(R.id.mail_img_ssl_down);

		cbxCheck = (CheckBox) findViewById(R.id.mail_cbx_check);
		cbxView = findViewById(R.id.mail_check_view);

		btnOk = (Button) findViewById(R.id.mail_ok);
		btnCancel = (Button) findViewById(R.id.mail_cancel);

		tvCameraName = (TextView) findViewById(R.id.tv_camera_setting);

		ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView1);
		scrollView.setOnTouchListener(this);

		// RelativeLayout layout = (RelativeLayout) findViewById(R.id.top);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.top_bg);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		// layout.setBackgroundDrawable(drawable);
	}

	private void setListener() {
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		imgBtnSmtpServerDown.setOnClickListener(this);
		imgBtnSSLDown.setOnClickListener(this);
		cbxCheck.setOnCheckedChangeListener(this);
		editSSL.setOnClickListener(this);
		MyTextWitch myTextWitchReceiver1 = new MyTextWitch(
				R.id.mail_edit_receiver1);
		editReceiver1.addTextChangedListener(myTextWitchReceiver1);
		MyTextWitch myTextWitchReceiver2 = new MyTextWitch(
				R.id.mail_edit_receiver2);
		editReceiver2.addTextChangedListener(myTextWitchReceiver2);
		MyTextWitch myTextWitchReceiver3 = new MyTextWitch(
				R.id.mail_edit_receiver3);
		editReceiver3.addTextChangedListener(myTextWitchReceiver3);
		MyTextWitch myTextWitchReceiver4 = new MyTextWitch(
				R.id.mail_edit_receiver4);
		editReceiver4.addTextChangedListener(myTextWitchReceiver4);

		MyTextWitch myTextWitchSender = new MyTextWitch(R.id.mail_edit_sender);
		editSender.addTextChangedListener(myTextWitchSender);

		MyTextWitch myTextWitchPort = new MyTextWitch(R.id.mail_edit_smtp_port);
		editSmtpPort.addTextChangedListener(myTextWitchPort);

		MyTextWitch myTextWitchUser = new MyTextWitch(R.id.mail_edit_smtp_user);
		editSmtpUser.addTextChangedListener(myTextWitchUser);

		MyTextWitch myTextWitchPwd = new MyTextWitch(R.id.mail_edit_smtp_pwd);
		editSmtpPwd.addTextChangedListener(myTextWitchPwd);

		MyTextWitch myTextWitchServer = new MyTextWitch(
				R.id.mail_edit_smtp_server);
		editSmtpServer.addTextChangedListener(myTextWitchServer);

		progressDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}

		});
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.mail_cancel:
//			finish();
//			break;
//		case R.id.mail_ok:
//			setMail();
//			break;
//		case R.id.mail_img_smtp_down:
//			if (smtpPopWindow != null && smtpPopWindow.isShowing()) {
//				//smtpPopWindow.dismiss();
//			} else {
//				smtpPopWindow.showAsDropDown(imgBtnSmtpServerDown, -210, 0);
//			}
//			break;
//		case R.id.mail_edit_ssl:
//		case R.id.mail_img_ssl_down:
//			if (sslPopWindow != null && sslPopWindow.isShowing()) {
//				//smtpPopWindow.dismiss();
//			} else {
//				sslPopWindow.showAsDropDown(imgBtnSSLDown, -160, 4);
//			}
//
//			break;
//		case R.id.mail_ssl_ssl:
//			sslPopWindow.dismiss();
//			mailBean.setSsl(1);
//			editSSL.setText("SSL");
//			break;
//		case R.id.mail_ssl_none:
//			sslPopWindow.dismiss();
//			mailBean.setSsl(0);
//			editSSL.setText("NONE");
//			break;
//		case R.id.mail_ssl_tsl:
//			sslPopWindow.dismiss();
//			mailBean.setSsl(2);
//			editSSL.setText("TLS");
//			break;
//
//		// mail
//
//		case R.id.mail_163:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_163));
//			editSmtpServer.setText(R.string.mail_163);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_126:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_126));
//			editSmtpServer.setText(R.string.mail_126);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_21cn:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_21cn));
//			editSmtpServer.setText(R.string.mail_21cn);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_263:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_263));
//			editSmtpServer.setText(R.string.mail_263);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_eyou:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_eyou));
//			editSmtpServer.setText(R.string.mail_eyou);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_gmail:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_gmail));
//			editSmtpServer.setText(R.string.mail_gmail);
//			editSmtpPort.setText("465");
//			break;
//		case R.id.mail_sina:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_sina));
//			editSmtpServer.setText(R.string.mail_sina);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_qq:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_qq));
//			editSmtpServer.setText(R.string.mail_qq);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_sohu:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_sohu));
//			editSmtpServer.setText(R.string.mail_sohu);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_yahoo:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(
//					R.string.mail_yahoo_com));
//			editSmtpServer.setText(R.string.mail_yahoo_com);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_tom:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_tom));
//			editSmtpServer.setText(R.string.mail_tom);
//			editSmtpPort.setText("25");
//			break;
//		case R.id.mail_yeah:
//			smtpPopWindow.dismiss();
//			mailBean.setSvr(this.getResources().getString(R.string.mail_yeah));
//			editSmtpServer.setText(R.string.mail_yeah);
//			editSmtpPort.setText("25");
//			break;
//		default:
//			break;
//		}
	}

	private void setMail() {
		if (!mailBean.isChecked()) {
			mailBean.setUser("");
			mailBean.setPwd("");
		}
		if (editSmtpPort.getText().toString().trim().length() == 0
				|| editSmtpPort.getText().toString().trim() == null) {
			showToast(R.string.mail_set_show_port);
			return;
		}

		NativeCaller.PPPPMailSetting(strDID, mailBean.getSvr(),
				mailBean.getPort(), mailBean.getUser(), mailBean.getPwd(),
				mailBean.getSsl(), mailBean.getSender(),
				mailBean.getReceiver1(), mailBean.getReceiver2(),
				mailBean.getReceiver3(), mailBean.getReceiver4());

		Log.d("tag", mailBean.toString());
	}

	private void showSslPopWindow() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.settingmail_ssl_popwindow, null);
		TextView tvSsl = (TextView) layout.findViewById(R.id.mail_ssl_ssl);
		TextView tvNone = (TextView) layout.findViewById(R.id.mail_ssl_none);
		TextView tvTls = (TextView) layout.findViewById(R.id.mail_ssl_tsl);
		tvSsl.setOnClickListener(this);
		tvNone.setOnClickListener(this);
		tvTls.setOnClickListener(this);

		sslPopWindow = new PopupWindow(layout, 160,
				WindowManager.LayoutParams.WRAP_CONTENT);
		sslPopWindow.setFocusable(true);
		sslPopWindow.setOutsideTouchable(true);
		sslPopWindow.setBackgroundDrawable(new ColorDrawable(0));
		// popupWindow.update();
		sslPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				sslPopWindow.dismiss();
			}
		});
		sslPopWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					sslPopWindow.dismiss();
				}
				return false;
			}
		});
	}

	private void showSmtpPopWindow() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.settingmail_smtp_popwindow, null);
		TextView mail_163 = (TextView) layout.findViewById(R.id.mail_163);
		TextView mail_126 = (TextView) layout.findViewById(R.id.mail_126);
		TextView mail_21cn = (TextView) layout.findViewById(R.id.mail_21cn);
		TextView mail_263 = (TextView) layout.findViewById(R.id.mail_263);
		TextView mail_eyou = (TextView) layout.findViewById(R.id.mail_eyou);
		TextView mail_gmail = (TextView) layout.findViewById(R.id.mail_gmail);
		TextView mail_sina = (TextView) layout.findViewById(R.id.mail_sina);
		TextView mail_qq = (TextView) layout.findViewById(R.id.mail_qq);
		TextView mail_sohu = (TextView) layout.findViewById(R.id.mail_sohu);
		Button mail_yahoo = (Button) layout.findViewById(R.id.mail_yahoo);
		TextView mail_tom = (TextView) layout.findViewById(R.id.mail_tom);
		TextView mail_yeah = (TextView) layout.findViewById(R.id.mail_yeah);

		mail_163.setOnClickListener(this);
		mail_126.setOnClickListener(this);
		mail_21cn.setOnClickListener(this);
		mail_263.setOnClickListener(this);
		mail_eyou.setOnClickListener(this);
		mail_gmail.setOnClickListener(this);
		mail_sina.setOnClickListener(this);
		mail_qq.setOnClickListener(this);
		mail_sohu.setOnClickListener(this);
		mail_yahoo.setOnClickListener(this);
		mail_tom.setOnClickListener(this);
		mail_yeah.setOnClickListener(this);
		smtpPopWindow = new PopupWindow(layout, 200,
				WindowManager.LayoutParams.WRAP_CONTENT);
		smtpPopWindow.setFocusable(true);
		smtpPopWindow.setOutsideTouchable(true);
		smtpPopWindow.setBackgroundDrawable(new ColorDrawable(0));
		// popupWindow.update();
		smtpPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				smtpPopWindow.dismiss();
			}
		});
		smtpPopWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					smtpPopWindow.dismiss();
				}
				return false;
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private class MyTextWitch implements TextWatcher {
		private int id;

		public MyTextWitch(int id) {
			this.id = id;
		}

		@Override
		public void afterTextChanged(Editable s) {
			String result = s.toString();
//			switch (id) {
//			case R.id.mail_edit_sender:
//				mailBean.setSender(result);
//				break;
//			case R.id.mail_edit_smtp_port:
//				if (!TextUtils.isEmpty(result)) {
//					mailBean.setPort(Integer.valueOf(result));
//				}
//				break;
//			case R.id.mail_edit_smtp_user:
//				mailBean.setUser(result);
//				break;
//			case R.id.mail_edit_smtp_pwd:
//				mailBean.setPwd(result);
//				break;
//			case R.id.mail_edit_smtp_server:
//				mailBean.setSvr(result);
//				break;
//			case R.id.mail_edit_receiver1:
//				mailBean.setReceiver1(result);
//				break;
//			case R.id.mail_edit_receiver2:
//				mailBean.setReceiver2(result);
//				break;
//			case R.id.mail_edit_receiver3:
//				mailBean.setReceiver3(result);
//				break;
//			case R.id.mail_edit_receiver4:
//				mailBean.setReceiver4(result);
//				break;
//			default:
//				break;
//			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton cbxBtn, boolean isChecked) {
		mailBean.setChecked(isChecked);
		if (isChecked) {
			cbxView.setVisibility(View.VISIBLE);
		} else {
			mailBean.setUser("");
			mailBean.setPwd("");
			cbxView.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

		if (sslPopWindow != null && sslPopWindow.isShowing()) {
			sslPopWindow.dismiss();
			sslPopWindow = null;
		}
		if (smtpPopWindow != null && smtpPopWindow.isShowing()) {
			smtpPopWindow.dismiss();
			smtpPopWindow = null;
		}
		return false;
	}

	/***
	 * BridgeService callback
	 * */
	@Override
	public void callBackMailParams(String did, String svr, int port,
			String user, String pwd, int ssl, String sender, String receiver1,
			String receiver2, String receiver3, String receiver4) {
		mailBean.setSender(sender);
		mailBean.setSvr(svr);
		mailBean.setPort(port);
		mailBean.setUser(user);
		mailBean.setPwd(pwd);
		mailBean.setSsl(ssl);
		mailBean.setReceiver1(receiver1);
		mailBean.setReceiver2(receiver2);
		mailBean.setReceiver3(receiver3);
		mailBean.setReceiver4(receiver4);
		mHandler.sendEmptyMessage(PARAMS);
		Log.i("wifisetting", "did=" + did);
		Log.i("wifisetting", "svr=" + svr);
		Log.i("wifisetting", "user=" + user);
		Log.i("wifisetting", "pwd=" + pwd);
		Log.i("wifisetting", "ssl=" + ssl);
		Log.i("wifisetting", "sender=" + sender);
		Log.i("wifisetting", "receiver1=" + receiver1);
		Log.i("wifisetting", "receiver2=" + receiver2);
		Log.i("wifisetting", "receiver3=" + receiver3);
		Log.i("wifisetting", "receiver4=" + receiver4);

	}

	/***
	 * BridgeService callback
	 * */
	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
			int result) {
		Log.d(TAG, "result:" + result + " paramType:" + paramType);
		mHandler.sendEmptyMessage(result);
	}

}
