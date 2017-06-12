package object.dbnewgo.client;

import object.dbnewgo.client.BridgeService.FtpInterface;
import object.p2pipcam.bean.FtpBean;
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
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * FTP����
 * */
public class SettingFtpActivity extends BaseActivity implements
		OnClickListener, FtpInterface {
	private String TAG = "SettingFtpActivity";
	private String strDID;
	private final int FAIL = 0;
	private final int SUCCESS = 1;
	private final int PARAMS = 3;
	private final int TIMEOUT = 3000;
	private final int UPLOADTIMETOOLONG = 4;
	private boolean successFlag;
	private String cameraName = null;
	private ProgressDialog progressDialog = null;
	private BridgeService mBridgeService = null;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FAIL:
				showToast(R.string.ftp_setting_failed);
				break;
			case SUCCESS:
				showToast(R.string.ftp_setting_success);
				finish();
				break;
			case PARAMS:
				successFlag = true;
				if (progressDialog.isShowing()) {
					progressDialog.cancel();
					editFtpServer.setText(ftpBean.getSvr_ftp());
					editFtpPort.setText(String.valueOf(ftpBean.getPort()));
					editFtpUser.setText(ftpBean.getUser());
					editFtpPwd.setText(ftpBean.getPwd());
					editFtpUploadInterval.setText(String.valueOf(ftpBean
							.getUpload_interval()));
				}
				break;
			case UPLOADTIMETOOLONG:
				editFtpUploadInterval.setText("");
				break;
			default:
				break;
			}
		}
	};

	private FtpBean ftpBean;
	private Button ftpOk;
	private Button ftpCancel;
	private EditText editFtpServer;
	private EditText editFtpPort;
	private EditText editFtpUser;
	private EditText editFtpPwd;
	private EditText editFtpUploadInterval;

	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getDataFromOther();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settingftp);
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.ftp_getparams));
		progressDialog.show();
		mHandler.postDelayed(runnable, TIMEOUT);
		ftpBean = new FtpBean();
		findView();
		setListener();

		BridgeService.setFtpInterface(this);
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_PARAMS);

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
	private TextView tvCameraName;

	private void setListener() {
		ftpOk.setOnClickListener(this);
		ftpCancel.setOnClickListener(this);
		MyTextWitch serverTW = new MyTextWitch(R.id.ftp_edit_server);
		editFtpServer.addTextChangedListener(serverTW);
		MyTextWitch portTW = new MyTextWitch(R.id.ftp_edit_port);
		editFtpPort.addTextChangedListener(portTW);
		MyTextWitch userTW = new MyTextWitch(R.id.ftp_edit_user);
		editFtpUser.addTextChangedListener(userTW);
		MyTextWitch pwdTW = new MyTextWitch(R.id.ftp_edit_pwd);
		editFtpPwd.addTextChangedListener(pwdTW);
		MyTextWitch uploadTW = new MyTextWitch(
				R.id.ftp_edit_upload_interval_time);
		editFtpUploadInterval.addTextChangedListener(uploadTW);

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

	private void findView() {
		ftpOk = (Button) findViewById(R.id.ftp_ok);
		ftpCancel = (Button) findViewById(R.id.ftp_cancel);

		editFtpServer = (EditText) findViewById(R.id.ftp_edit_server);
		editFtpPort = (EditText) findViewById(R.id.ftp_edit_port);
		editFtpUser = (EditText) findViewById(R.id.ftp_edit_user);
		editFtpPwd = (EditText) findViewById(R.id.ftp_edit_pwd);
		editFtpUploadInterval = (EditText) findViewById(R.id.ftp_edit_upload_interval_time);

		tvCameraName = (TextView) findViewById(R.id.tv_camera_setting);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.top);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.top_bg);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		// layout.setBackgroundDrawable(drawable);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.ftp_ok:
//			setFtp();
//			break;
//		case R.id.ftp_cancel:
//			finish();
//			break;
//
//		default:
//			break;
//		}
	}

	private void setFtp() {
		Log.d("tag", "bean:" + ftpBean.toString());
		NativeCaller.PPPPFtpSetting(strDID, ftpBean.getSvr_ftp(),
				ftpBean.getUser(), ftpBean.getPwd(), ftpBean.getDir(),
				ftpBean.getPort(), ftpBean.getMode(),
				ftpBean.getUpload_interval());
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
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
//			case R.id.ftp_edit_server:
//				ftpBean.setSvr_ftp(result);
//				break;
//			case R.id.ftp_edit_port:
//				if (!TextUtils.isEmpty(result)) {
//					ftpBean.setPort(Integer.parseInt(result));
//				}
//				break;
//			case R.id.ftp_edit_user:
//				ftpBean.setUser(result);
//				break;
//			case R.id.ftp_edit_pwd:
//				ftpBean.setPwd(result);
//				break;
//			case R.id.ftp_edit_upload_interval_time:
//				String uploadInterval = s.toString();
//				if (!TextUtils.isEmpty(uploadInterval)) {
//					int resul1t = Integer.parseInt(uploadInterval);
//					if (resul1t <= 3600) {
//						ftpBean.setUpload_interval(resul1t);
//					} else {
//						mHandler.sendEmptyMessage(UPLOADTIMETOOLONG);
//						showToast(R.string.alerm_uploadinterval_toolong1);
//					}
//				} else {
//					ftpBean.setUpload_interval(0);
//				}
//				break;
//
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
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void callBackFtpParams(String did, String svr_ftp, String user,
			String pwd, String dir, int port, int mode, int upload_interval) {
		ftpBean.setSvr_ftp(svr_ftp);
		ftpBean.setUpload_interval(upload_interval);
		ftpBean.setUser(user);
		ftpBean.setPwd(pwd);
		ftpBean.setPort(port);
		ftpBean.setDir(dir);
		ftpBean.setMode(mode);
		mHandler.sendEmptyMessage(PARAMS);
	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
			int result) {
		Log.d(TAG, "result:" + result + " paramType:" + paramType);
		mHandler.sendEmptyMessage(result);
	}

}
