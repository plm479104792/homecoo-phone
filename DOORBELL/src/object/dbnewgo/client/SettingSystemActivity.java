package object.dbnewgo.client;

import object.dbnewgo.client.BridgeService.DoorBellInterfaceParmInterface;
import object.dbnewgo.client.BridgeService.DoorBellSystemParmInterface;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * ��������
 * */
public class SettingSystemActivity extends BaseActivity implements
		OnClickListener, DoorBellSystemParmInterface {
	int bell_on = 0;
	int bell_audio = 0;
	int bell_mode = 0;
	int max_watch = 0;
	int max_talk = 0;
	int max_wait = 0;
	private String strDID = null;
	private Button ok = null;
	private Button cancel = null;
	private final int TIMEOUT = 3000;
	private final int PARAMS = 3;
	private RadioGroup group1;
	private RadioButton rb1, rb2;

	private RadioGroup group2;
	private RadioButton rb1_2, rb2_2;

	private RadioGroup group3;
	private RadioButton rb1_3, rb2_3;

	private EditText edit_time1, edit_time2, edit_time3;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case PARAMS:// get user params
				edit_time1.setText(max_watch + "");
				edit_time2.setText(max_talk + "");
				edit_time3.setText(max_wait + "");
				if (bell_on == 1) {
					rb1.setChecked(true);
					rb2.setChecked(false);
				} else {
					rb2.setChecked(true);
					rb1.setChecked(false);
				}

				if (bell_audio == 1) {
					rb1_2.setChecked(true);
					rb2_2.setChecked(false);
				} else {
					rb2_2.setChecked(true);
					rb1_2.setChecked(false);
				}

				if (bell_mode == 1) {
					rb1_3.setChecked(true);
					rb2_3.setChecked(false);
				} else {
					rb2_3.setChecked(true);
					rb1_3.setChecked(false);
				}
				break;

			default:
				break;
			}
		}
	};
	private Button setting_reboot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDataFromOther();
		setContentView(R.layout.settingsystem);
		BridgeService.setDoorBellSystemParmInterface(this);
		String get_alarm_config = "get_bell_config.cgi?&" + "loginuse="
				+ SystemValue.doorBellAdmin + "&loginpas="
				+ SystemValue.doorBellPass + "&user="
				+ SystemValue.doorBellAdmin + "&pwd="
				+ SystemValue.doorBellPass;
		NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
		edit_time1 = (EditText) findViewById(R.id.edit_time1);
		edit_time2 = (EditText) findViewById(R.id.edit_time2);
		edit_time3 = (EditText) findViewById(R.id.edit_time3);
		ok = (Button) findViewById(R.id.alerm_ok);
		cancel = (Button) findViewById(R.id.alerm_cancel);
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);

		group1 = (RadioGroup) findViewById(R.id.group1);
		rb1 = (RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rb1.getId()) {
					bell_on = 1;
				} else if (checkedId == rb2.getId()) {
					bell_on = 0;
				}
			}
		});

		group2 = (RadioGroup) findViewById(R.id.group2);
		rb1_2 = (RadioButton) findViewById(R.id.rb1_2);
		rb2_2 = (RadioButton) findViewById(R.id.rb2_2);
		setting_reboot = (Button) findViewById(R.id.setting_reboot);
		setting_reboot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showSureReboot();
			}
		});
		group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rb1_2.getId()) {
					bell_audio = 1;
				} else if (checkedId == rb2_2.getId()) {
					bell_audio = 0;
				}
			}
		});

		group3 = (RadioGroup) findViewById(R.id.group3);
		rb1_3 = (RadioButton) findViewById(R.id.rb1_3);
		rb2_3 = (RadioButton) findViewById(R.id.rb2_3);
		group3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rb1_3.getId()) {
					bell_mode = 1;
				} else if (checkedId == rb2_3.getId()) {
					bell_mode = 0;
				}
			}
		});
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
						Intent intent2 = new Intent("myback");
						sendBroadcast(intent2);
						finish();
					}
				});
		builder.setNegativeButton(R.string.str_cancel, null);
		builder.show();
	}

	private String cameraName = null;

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		BridgeService.setDoorBellSystemParmInterface(null);
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		int id=arg0.getId();
		if (id==R.id.alerm_ok) {
			if (edit_time1.getText().toString().trim().length() == 0) {
				showToast("������ʱ��");
				return;
			}
			max_watch = Integer
					.parseInt(edit_time1.getText().toString().trim());
			if (max_watch < 10 || max_watch > 300) {
				showToast("10-300");
				return;
			}
			if (edit_time2.getText().toString().trim().length() == 0) {
				showToast("������ʱ��");
				return;
			}
			max_talk = Integer.parseInt(edit_time2.getText().toString().trim());
			if (max_talk < 10 || max_talk > 300) {
				showToast("10-300");
				return;
			}
			if (edit_time3.getText().toString().trim().length() == 0) {
				showToast("������ʱ��");
				return;
			}
			max_wait = Integer.parseInt(edit_time3.getText().toString().trim());
			if (max_wait < 10 || max_wait > 60) {
				showToast("10-60");
				return;
			}
			String set_bell_config = "set_bell_config.cgi?&bell_on=" + bell_on
					+ "&bell_audio=" + bell_audio + "&bell_mode=" + bell_mode
					+ "&max_watch=" + max_watch + "&max_talk=" + max_talk
					+ "&max_wait=" + max_wait + "&" + "loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, set_bell_config, 1);
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
		}else if (id==R.id.alerm_cancel) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
		}
		
		
//		switch (arg0.getId()) {
//		case R.id.alerm_ok:
//			// String set_bell_config =
//			// "set_bell_config.cgi?&bell_on=1&bell_audio=1&bell_mode=0&max_watch=10&max_talk=11
//			// &max_wait=20&"
//			// + "loginuse="
//			// + SystemValue.doorBellAdmin
//			// + "&loginpas="
//			// + SystemValue.doorBellPass
//			// + "&user="
//			// + SystemValue.doorBellAdmin
//			// + "&pwd=" int max_watch = 0;
//			// int max_talk = 0;
//			// int max_wait = 0;
//			// + SystemValue.doorBellPass;
//			if (edit_time1.getText().toString().trim().length() == 0) {
//				showToast("������ʱ��");
//				return;
//			}
//			max_watch = Integer
//					.parseInt(edit_time1.getText().toString().trim());
//			if (max_watch < 10 || max_watch > 300) {
//				showToast("10-300");
//				return;
//			}
//			if (edit_time2.getText().toString().trim().length() == 0) {
//				showToast("������ʱ��");
//				return;
//			}
//			max_talk = Integer.parseInt(edit_time2.getText().toString().trim());
//			if (max_talk < 10 || max_talk > 300) {
//				showToast("10-300");
//				return;
//			}
//			if (edit_time3.getText().toString().trim().length() == 0) {
//				showToast("������ʱ��");
//				return;
//			}
//			max_wait = Integer.parseInt(edit_time3.getText().toString().trim());
//			if (max_wait < 10 || max_wait > 60) {
//				showToast("10-60");
//				return;
//			}
//			String set_bell_config = "set_bell_config.cgi?&bell_on=" + bell_on
//					+ "&bell_audio=" + bell_audio + "&bell_mode=" + bell_mode
//					+ "&max_watch=" + max_watch + "&max_talk=" + max_talk
//					+ "&max_wait=" + max_wait + "&" + "loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, set_bell_config, 1);
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//
//			break;
//		case R.id.alerm_cancel:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//		default:
//			break;
//		}
	}

	@Override
	public void callBackDoorBellSystemParm(String did, int bell_on,
			int bell_audio, int bell_mode, int max_watch, int max_talk,
			int max_wait) {
		// TODO Auto-generated method stub
		Log.e("test", "1did:" + did + "  bell_on:" + bell_on + "  bell_audio:"
				+ bell_audio + "  bell_mode:" + bell_mode + "  max_watch:"
				+ max_watch + "  max_talk:" + max_talk + "  max_wait:"
				+ max_wait);
		this.bell_on = bell_on;
		this.bell_audio = bell_audio;
		this.bell_mode = bell_mode;
		this.max_watch = max_watch;
		this.max_talk = max_talk;
		this.max_wait = max_wait;
		mHandler.sendEmptyMessage(PARAMS);
	}
}
