package object.dbnewgo.client;

import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * ��ʾĳ̨���������������б�
 * */
@SuppressLint("NewApi")
public class SettingActivity extends BaseActivity implements OnClickListener {
	// private static final String LOG_TAG = "SettingActivity";
	private String strDID;
	private String cameraName;
	private TextView tvCameraName;
	private Button btnBack;
	private MyBackBrod myBackBrod = null;
	private Button buttonWifi, buttonAp, buttonUser, buttonTime, buttonAler,
			buttonLock, buttonFtp, buttonSd, setting_reboot, setting_reset,
			setting_interface, setting_system, setting_mail;

	class MyBackBrod extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String action = arg1.getAction();
			if ("myback".equals(action)) {
				finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);// �˳�����
			}

		}

	}

	public void showSureReboot() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app);
		builder.setTitle(getResources().getString(
				R.string.doorbell_setting_reboot));
		builder.setMessage(R.string.doorbell_setting_reboot_show);
		builder.setPositiveButton(R.string.str_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Process.killProcess(Process.myPid());
						NativeCaller.PPPPRebootDevice(strDID);
						finish();
					}
				});
		builder.setNegativeButton(R.string.str_cancel, null);
		builder.show();
	}

	public void showSureReset() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app);
		builder.setTitle(getResources().getString(
				R.string.doorbell_setting_reset));
		builder.setMessage(R.string.doorbell_setting_reset_show);
		builder.setPositiveButton(R.string.str_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Process.killProcess(Process.myPid());
						String reset_alarm_config = "reset_alarm_config.cgi?&"
								+ "loginuse=" + SystemValue.doorBellAdmin
								+ "&loginpas=" + SystemValue.doorBellPass
								+ "&user=" + SystemValue.doorBellAdmin
								+ "&pwd=" + SystemValue.doorBellPass;
						NativeCaller.TransferMessage(strDID,
								reset_alarm_config, 1);
						finish();
					}
				});
		builder.setNegativeButton(R.string.str_cancel, null);
		builder.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting0111);
		// setEdgeFromLeft();
		findView();

		tvCameraName.setText(cameraName + "  "
				+ getResources().getString(R.string.setting));
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (myBackBrod == null) {
			myBackBrod = new MyBackBrod();
			IntentFilter filter = new IntentFilter();
			filter.addAction("myback");
			registerReceiver(myBackBrod, filter);
		}
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}

	private void findView() {
		tvCameraName = (TextView) findViewById(R.id.tv_camera_setting);
		btnBack = (Button) findViewById(R.id.back);
		buttonWifi = (Button) findViewById(R.id.setting_wifi);
		buttonAp = (Button) findViewById(R.id.setting_ap);
		buttonAler = (Button) findViewById(R.id.setting_alerm);
		buttonUser = (Button) findViewById(R.id.setting_use);
		buttonLock = (Button) findViewById(R.id.setting_lock);
		buttonFtp = (Button) findViewById(R.id.setting_ftp);
		buttonSd = (Button) findViewById(R.id.setting_sdcard);
		buttonTime = (Button) findViewById(R.id.setting_time);
		setting_reboot = (Button) findViewById(R.id.setting_reboot);
		setting_reset = (Button) findViewById(R.id.setting_reset);
		setting_interface = (Button) findViewById(R.id.setting_interface);
		setting_system = (Button) findViewById(R.id.setting_system);
		setting_mail = (Button) findViewById(R.id.setting_mail);
		setting_mail.setOnClickListener(this);
		setting_system.setOnClickListener(this);
		setting_interface.setOnClickListener(this);
		setting_reset.setOnClickListener(this);
		setting_reboot.setOnClickListener(this);
		buttonWifi.setOnClickListener(this);
		buttonAp.setOnClickListener(this);
		buttonUser.setOnClickListener(this);
		buttonTime.setOnClickListener(this);
		buttonAler.setOnClickListener(this);
		buttonLock.setOnClickListener(this);
		buttonFtp.setOnClickListener(this);
		buttonSd.setOnClickListener(this);
		btnBack.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		int id=arg0.getId();
		if (id==R.id.setting_mail) {
			Intent intent111 = new Intent(this, SettingMailActivity.class);
			intent111.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent111.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent111);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_system) {
			Intent intent10 = new Intent(this, SettingSystemActivity.class);
			intent10.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent10.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent10);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_interface) {
			Intent intent0 = new Intent(this, SettingInterfaceActivity.class);
			intent0.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent0.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent0);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_reboot) {
			showSureReboot();
		}else if (id==R.id.setting_reset) {
			showSureReset();
		}else if (id==R.id.setting_wifi) {
			Intent intent = new Intent(this, SettingWifiActivity.class);
			intent.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_use) {
			Intent intent1 = new Intent(this, SettingUserActivity.class);
			intent1.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent1.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent1);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_alerm) {
			Intent intent2 = new Intent(this, SettingAlarmActivity.class);
			intent2.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent2.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent2);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_time) {
			Intent intent3 = new Intent(this, SettingDateActivity.class);
			intent3.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent3.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent3);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_lock) {
			Intent intent4 = new Intent(this, SettingDelayActivity.class);
			intent4.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent4.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent4);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_ftp) {
			Intent intent5 = new Intent(this, SettingFtpActivity.class);
			intent5.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent5.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent5);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_sdcard) {
			Intent intent6 = new Intent(this, SettingSDCardActivity.class);
			intent6.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent6.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent6);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.setting_ap) {
			Intent intent7 = new Intent(this, SettingApActivity.class);
			intent7.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
			intent7.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
			startActivity(intent7);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}else if (id==R.id.back) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
		}
		
//		switch (arg0.getId()) {
//		case R.id.setting_mail:
//			Intent intent111 = new Intent(this, SettingMailActivity.class);
//			intent111.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent111.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent111);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.setting_system:
//			Intent intent10 = new Intent(this, SettingSystemActivity.class);
//			intent10.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent10.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent10);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.setting_interface:
//			Intent intent0 = new Intent(this, SettingInterfaceActivity.class);
//			intent0.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent0.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent0);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.setting_reboot:
//			showSureReboot();
//			break;
//		case R.id.setting_reset:
//			showSureReset();
//			break;
//		case R.id.setting_wifi:
//			Intent intent = new Intent(this, SettingWifiActivity.class);
//			intent.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.setting_use:
//			Intent intent1 = new Intent(this, SettingUserActivity.class);
//			intent1.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent1.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent1);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;

//		case R.id.setting_alerm:
//			Intent intent2 = new Intent(this, SettingAlarmActivity.class);
//			intent2.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent2.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent2);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.setting_time:
//			Intent intent3 = new Intent(this, SettingDateActivity.class);
//			intent3.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent3.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent3);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.setting_lock:
//			// ��ʱ����
//			Intent intent4 = new Intent(this, SettingDelayActivity.class);
//			intent4.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent4.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent4);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.setting_ftp:
//			Intent intent5 = new Intent(this, SettingFtpActivity.class);
//			intent5.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent5.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent5);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.setting_sdcard:
//			Intent intent6 = new Intent(this, SettingSDCardActivity.class);
//			intent6.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent6.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent6);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.setting_ap:
//			Intent intent7 = new Intent(this, SettingApActivity.class);
//			intent7.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//			intent7.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
//			startActivity(intent7);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			break;
//		case R.id.back:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myBackBrod != null) {
			unregisterReceiver(myBackBrod);
		}

	}
}