package object.dbnewgo.client;

import object.dbnewgo.client.BridgeService.DoorBellAlarmParmInterface;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * ��������
 * */
public class SettingAlarmActivity extends BaseActivity implements
		OnClickListener, DoorBellAlarmParmInterface {
	private int alarm_on, alarm_type, alarm_level, alarm_delay,
			alarm_start_hour, alarm_start_minute, alarm_stop_hour,
			alarm_stop_minute;

	private String strDID = null;
	private Button ok = null;
	private Button cancel = null;
	private final int TIMEOUT = 3000;
	private final int PARAMS = 3;
	private RadioGroup group1;
	private RadioButton rb1, rb2;

	private EditText edit_alarm_delay_time;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case PARAMS:// get user params
				if (alarm_start_hour >= 0 && alarm_start_hour <= 23) {
					timePicker.setCurrentHour(alarm_start_hour);
				}
				if (alarm_start_minute >= 0 && alarm_start_minute <= 59) {
					timePicker.setCurrentMinute(alarm_start_minute);

				}

				if (alarm_stop_hour >= 0 && alarm_stop_hour <= 23) {
					timePicker2.setCurrentHour(alarm_stop_hour);
				}
				if (alarm_stop_minute >= 0 && alarm_stop_minute <= 59) {
					timePicker2.setCurrentMinute(alarm_stop_minute);
				}
				edit_alarm_delay_time.setText(alarm_delay + "");
				tv_alarm_leve.setText(alarm_level + "");
				if (alarm_type == 0) {
					tv_alarm_type
							.setText(R.string.doorbell_setting_alarm_type_0);
				} else if (alarm_type == 1) {
					tv_alarm_type
							.setText(R.string.doorbell_setting_alarm_type_1);
				} else {
					tv_alarm_type
							.setText(R.string.doorbell_setting_alarm_type_2);
				}
				if (alarm_on == 1) {
					rb1.setChecked(true);
					rb2.setChecked(false);
					layout_alarm_view_all.setVisibility(View.VISIBLE);
				} else {
					rb2.setChecked(true);
					rb1.setChecked(false);
					layout_alarm_view_all.setVisibility(View.GONE);
				}
				isGet = true;
				isGet1 = true;
				break;

			default:
				break;
			}
		}
	};

	private LinearLayout layout_alarm_view_all;
	private LinearLayout layout_alarm_delay;
	private RelativeLayout layoutr_alerm_select, layoutr_alerm_leve;

	private ImageButton img_alarm_type_dr, image_alarm_leve_dr;

	private TextView tv_alarm_type, tv_alarm_leve;
	private Button setting_reset;

	private TimePicker timePicker, timePicker2;
	private boolean isGet = false;
	private boolean isGet1 = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDataFromOther();
		setContentView(R.layout.settingalarm0111);
		BridgeService.setDoorBellAlarmParmInterface(this);
		String get_alarm_config = "get_alarm_config.cgi?&" + "loginuse="
				+ SystemValue.doorBellAdmin + "&loginpas="
				+ SystemValue.doorBellPass + "&user="
				+ SystemValue.doorBellAdmin + "&pwd="
				+ SystemValue.doorBellPass;
		NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
		edit_alarm_delay_time = (EditText) findViewById(R.id.edit_alarm_delay_time);
		tv_alarm_type = (TextView) findViewById(R.id.tv_alarm_type);
		tv_alarm_leve = (TextView) findViewById(R.id.tv_alarm_leve);
		layout_alarm_view_all = (LinearLayout) findViewById(R.id.layout_alarm_view_all);
		layout_alarm_delay = (LinearLayout) findViewById(R.id.layout_alarm_delay);
		layoutr_alerm_select = (RelativeLayout) findViewById(R.id.layoutr_alerm_select);
		layoutr_alerm_leve = (RelativeLayout) findViewById(R.id.layoutr_alerm_leve);
		img_alarm_type_dr = (ImageButton) findViewById(R.id.img_alarm_type_dr);
		image_alarm_leve_dr = (ImageButton) findViewById(R.id.image_alarm_leve_dr);
		setting_reset = (Button) findViewById(R.id.setting_reset);
		image_alarm_leve_dr.setOnClickListener(this);
		img_alarm_type_dr.setOnClickListener(this);
		ok = (Button) findViewById(R.id.alerm_ok);
		cancel = (Button) findViewById(R.id.alerm_cancel);
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
		setting_reset.setOnClickListener(this);
		group1 = (RadioGroup) findViewById(R.id.group1);
		rb1 = (RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		isGet = false;
		isGet1 = false;
		group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rb1.getId()) {
					alarm_on = 1;
					layout_alarm_view_all.setVisibility(View.VISIBLE);
				} else if (checkedId == rb2.getId()) {
					alarm_on = 0;
					layout_alarm_view_all.setVisibility(View.GONE);
				}
			}
		});

		initExitPopupWindow_type();
		initExitPopupWindow_leve();
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		timePicker.setIs24HourView(true);

		timePicker
				.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
					@Override
					public void onTimeChanged(TimePicker view, int hourOfDay,
							int minute) {
						if (isGet) {
							alarm_start_hour = hourOfDay;
							alarm_start_minute = minute;
						} else {
							isGet = true;
						}

					}
				});

		timePicker2 = (TimePicker) findViewById(R.id.timePicker2);
		timePicker2.setIs24HourView(true);

		timePicker2
				.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
					@Override
					public void onTimeChanged(TimePicker view, int hourOfDay,
							int minute) {
						if (isGet1) {
							alarm_stop_hour = hourOfDay;
							alarm_stop_minute = minute;
						} else {
							isGet1 = true;
						}
					}
				});

	}

	private String retrunTime(int starttime) {
		String hour1;
		if (starttime < 10) {
			hour1 = "0" + (int) starttime;
		} else {
			hour1 = "" + (int) starttime;
		}
		return hour1;
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
		isGet = false;
		isGet1 = false;
		BridgeService.setDoorBellAlarmParmInterface(null);
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		int id=arg0.getId();
	    if (id==R.id.setting_reset) {
	    	showSureReset();
		}else if (id==R.id.img_alarm_type_dr) {
			popupWindow_alarm_type.showAsDropDown(img_alarm_type_dr);
		}else if (id==R.id.image_alarm_leve_dr) {
			popupWindow_alarm_leve.showAsDropDown(image_alarm_leve_dr);
		}else if (id==R.id.alerm_ok) {
			if (edit_alarm_delay_time.getText().toString().trim().length() == 0) {
				showToast(R.string.door_setting_alarm_show1);
				return;
			}
			if (alarm_start_hour > alarm_stop_hour) {
				showToast(R.string.door_setting_alarm_show2);
				return;
			}
			if (alarm_start_hour == alarm_stop_hour) {
				if (alarm_start_minute > alarm_stop_minute) {
					showToast(R.string.door_setting_alarm_show2);
					return;
				}
			}
			alarm_delay = Integer.parseInt(edit_alarm_delay_time.getText()
					.toString().trim());
			if (alarm_delay < 5 || alarm_delay > 30) {
				showToast("5-30");
				return;
			}
			String set_alarm_config = "set_alarm_config.cgi?&alarm_on="
					+ alarm_on + "&alarm_type=" + alarm_type + "&alarm_level="
					+ alarm_level + "&alarm_delay=" + alarm_delay
					+ "&alarm_start_hour=" + alarm_start_hour
					+ "&alarm_start_minute=" + alarm_start_minute
					+ "&alarm_stop_hour=" + alarm_stop_hour
					+ "&alarm_stop_minute=" + alarm_stop_minute + "&loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;

			NativeCaller.TransferMessage(strDID, set_alarm_config, 1);
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
		}else if (id==R.id.alerm_cancel) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
		}else if (id==R.id.button_type_0) {
			alarm_type = 0;
			tv_alarm_type.setText(R.string.doorbell_setting_alarm_type_0);
			popupWindow_alarm_type.dismiss();
		}else if (id==R.id.button_type_1) {
			alarm_type = 1;
			tv_alarm_type.setText(R.string.doorbell_setting_alarm_type_1);
			popupWindow_alarm_type.dismiss();
		}else if (id==R.id.button_type_2) {
			alarm_type = 2;
			tv_alarm_type.setText(R.string.doorbell_setting_alarm_type_2);
			popupWindow_alarm_type.dismiss();
		}else if (id==R.id.button_leve_1) {
			alarm_level = 1;
			tv_alarm_leve.setText("1");
			popupWindow_alarm_leve.dismiss();
		}else if (id==R.id.button_leve_2) {
			alarm_level = 2;
			tv_alarm_leve.setText("2");
			popupWindow_alarm_leve.dismiss();
		}else if (id==R.id.button_leve_3) {
			alarm_level = 3;
			tv_alarm_leve.setText("3");
			popupWindow_alarm_leve.dismiss();
		}else if (id==R.id.button_leve_4) {
			alarm_level = 4;
			tv_alarm_leve.setText("4");
			popupWindow_alarm_leve.dismiss();
		}else if (id==R.id.button_leve_5) {
			alarm_level = 5;
			tv_alarm_leve.setText("5");
			popupWindow_alarm_leve.dismiss();
		}
		// TODO Auto-generated method stub
//		switch (arg0.getId()) {
//		case R.id.setting_reset:
//			showSureReset();
//			break;
//		case R.id.img_alarm_type_dr:
//			popupWindow_alarm_type.showAsDropDown(img_alarm_type_dr);
//			break;
//		case R.id.image_alarm_leve_dr:
//			popupWindow_alarm_leve.showAsDropDown(image_alarm_leve_dr);
//			break;
//		case R.id.alerm_ok:
//			if (edit_alarm_delay_time.getText().toString().trim().length() == 0) {
//				showToast(R.string.door_setting_alarm_show1);
//				return;
//			}
//			if (alarm_start_hour > alarm_stop_hour) {
//				showToast(R.string.door_setting_alarm_show2);
//				return;
//			}
//			if (alarm_start_hour == alarm_stop_hour) {
//				if (alarm_start_minute > alarm_stop_minute) {
//					showToast(R.string.door_setting_alarm_show2);
//					return;
//				}
//			}
//			alarm_delay = Integer.parseInt(edit_alarm_delay_time.getText()
//					.toString().trim());
//			if (alarm_delay < 5 || alarm_delay > 30) {
//				showToast("5-30");
//				return;
//			}
//			// final String set_alarm_config =
//			// "set_alarm_config.cgi?&alarm_on=1&alarm_type=2&alarm_level=0&alarm_delay=14
//			// &alarm_start_hour=12&alarm_start_minute=24&alarm_stop_hour=16&alarm_stop_minute=30"
//			// + "loginuse="
//			// + SystemValue.doorBellAdmin
//			// + "&loginpas="
//			// + SystemValue.doorBellPass
//			// + "&user="
//			// + SystemValue.doorBellAdmin
//			// + "&pwd="
//			// + SystemValue.doorBellPass;
//			String set_alarm_config = "set_alarm_config.cgi?&alarm_on="
//					+ alarm_on + "&alarm_type=" + alarm_type + "&alarm_level="
//					+ alarm_level + "&alarm_delay=" + alarm_delay
//					+ "&alarm_start_hour=" + alarm_start_hour
//					+ "&alarm_start_minute=" + alarm_start_minute
//					+ "&alarm_stop_hour=" + alarm_stop_hour
//					+ "&alarm_stop_minute=" + alarm_stop_minute + "&loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//
//			NativeCaller.TransferMessage(strDID, set_alarm_config, 1);
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//
//			break;
//		case R.id.alerm_cancel:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//		case R.id.button_type_0:
//			// private TextView tv_alarm_type, tv_alarm_leve;
//			alarm_type = 0;
//			tv_alarm_type.setText(R.string.doorbell_setting_alarm_type_0);
//			popupWindow_alarm_type.dismiss();
//			break;
//		case R.id.button_type_1:
//			alarm_type = 1;
//		tv_alarm_type.setText(R.string.doorbell_setting_alarm_type_1);
//		popupWindow_alarm_type.dismiss();
//			break;
//		case R.id.button_type_2:
//			alarm_type = 2;
//			tv_alarm_type.setText(R.string.doorbell_setting_alarm_type_2);
//			popupWindow_alarm_type.dismiss();
//			break;
//
//		case R.id.button_leve_1:
//			alarm_level = 1;
//			tv_alarm_leve.setText("1");
//			popupWindow_alarm_leve.dismiss();
//			break;
//		case R.id.button_leve_2:
//			alarm_level = 2;
//			tv_alarm_leve.setText("2");
//			popupWindow_alarm_leve.dismiss();
//			break;
//		case R.id.button_leve_3:
//			alarm_level = 3;
//			tv_alarm_leve.setText("3");
//			popupWindow_alarm_leve.dismiss();
//			break;
//		case R.id.button_leve_4:
//			alarm_level = 4;
//			tv_alarm_leve.setText("4");
//			popupWindow_alarm_leve.dismiss();
//			break;
//		case R.id.button_leve_5:
//			alarm_level = 5;
//			tv_alarm_leve.setText("5");
//			popupWindow_alarm_leve.dismiss();
//			break;
//
//		default:
//			break;
//		}
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

	View popv_alerm_type;
	Button button_type_0, button_type_1, button_type_2;
	PopupWindow popupWindow_alarm_type;

	public void initExitPopupWindow_type() {
		LayoutInflater li = LayoutInflater.from(this);
		popv_alerm_type = li.inflate(R.layout.popup_alarm_type, null);
		button_type_0 = (Button) popv_alerm_type
				.findViewById(R.id.button_type_0);
		button_type_1 = (Button) popv_alerm_type
				.findViewById(R.id.button_type_1);
		button_type_2 = (Button) popv_alerm_type
				.findViewById(R.id.button_type_2);
		button_type_0.setOnClickListener(this);
		button_type_1.setOnClickListener(this);
		button_type_2.setOnClickListener(this);
		popupWindow_alarm_type = new PopupWindow(popv_alerm_type,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		popupWindow_alarm_type.setAnimationStyle(R.style.AnimationPreview);
		popupWindow_alarm_type.setFocusable(true);
		popupWindow_alarm_type.setOutsideTouchable(true);
		popupWindow_alarm_type.setBackgroundDrawable(new ColorDrawable(0));
		// popupWindow.update();
		popupWindow_alarm_type
				.setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						popupWindow_alarm_type.dismiss();
					}
				});
		popupWindow_alarm_type.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow_alarm_type.dismiss();
				}
				return false;
			}
		});
	}

	View popv_alerm_leve;
	Button button_leve_1, button_leve_2, button_leve_3, button_leve_4,
			button_leve_5;
	PopupWindow popupWindow_alarm_leve;

	public void initExitPopupWindow_leve() {
		LayoutInflater li = LayoutInflater.from(this);
		popv_alerm_leve = li.inflate(R.layout.popup_alarm_leve, null);
		button_leve_1 = (Button) popv_alerm_leve
				.findViewById(R.id.button_leve_1);
		button_leve_2 = (Button) popv_alerm_leve
				.findViewById(R.id.button_leve_2);
		button_leve_3 = (Button) popv_alerm_leve
				.findViewById(R.id.button_leve_3);
		button_leve_4 = (Button) popv_alerm_leve
				.findViewById(R.id.button_leve_4);
		button_leve_5 = (Button) popv_alerm_leve
				.findViewById(R.id.button_leve_5);
		button_leve_1.setOnClickListener(this);
		button_leve_2.setOnClickListener(this);
		button_leve_3.setOnClickListener(this);
		button_leve_4.setOnClickListener(this);
		button_leve_5.setOnClickListener(this);
		popupWindow_alarm_leve = new PopupWindow(popv_alerm_leve,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		popupWindow_alarm_leve.setAnimationStyle(R.style.AnimationPreview);
		popupWindow_alarm_leve.setFocusable(true);
		popupWindow_alarm_leve.setOutsideTouchable(true);
		popupWindow_alarm_leve.setBackgroundDrawable(new ColorDrawable(0));
		// popupWindow.update();
		popupWindow_alarm_leve
				.setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						popupWindow_alarm_leve.dismiss();
					}
				});
		popupWindow_alarm_leve.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow_alarm_leve.dismiss();
				}
				return false;
			}
		});
	}

	@Override
	public void callBackDoorBellAlarmParm(String did, int alarm_on,
			int alarm_type, int alarm_level, int alarm_delay,
			int alarm_start_hour, int alarm_stop_hour, int alarm_start_minute,
			int alarm_stop_minute) {
		// TODO Auto-generated method stub
		this.alarm_on = alarm_on;
		this.alarm_type = alarm_type;
		this.alarm_level = alarm_level;
		this.alarm_delay = alarm_delay;
		this.alarm_start_hour = alarm_start_hour;
		this.alarm_start_minute = alarm_start_minute;
		this.alarm_stop_hour = alarm_stop_hour;
		this.alarm_stop_minute = alarm_stop_minute;
		mHandler.sendEmptyMessage(PARAMS);
	}
}
