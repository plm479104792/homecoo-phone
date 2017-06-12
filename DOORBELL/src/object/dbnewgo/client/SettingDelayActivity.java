package object.dbnewgo.client;

import object.dbnewgo.client.BridgeService.DoorBellLockParmInterface;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.VibratorUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingDelayActivity extends BaseActivity implements
		DoorBellLockParmInterface, OnClickListener {
	private Button back, done;
	private EditText edit_time;
	private String strDID;
	private int lock_type1 = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				edit_time.setText(msg.arg2 + "");
				lock_type1 = msg.arg1;
				if (msg.arg1 == 0) {
					rb1.setChecked(true);
					rb2.setChecked(false);
					Log.e("test", "1msg.arg1:" + msg.arg1);
				} else {
					rb2.setChecked(true);
					rb1.setChecked(false);
					Log.e("test", "2msg.arg1:" + msg.arg1);
				}
				break;
			}

		}
	};

	@Override
	protected void onPause() {
		super.onPause();
	}

	private RadioGroup group1;
	private RadioButton rb1, rb2;

	// 08-07 12:16:48.757: E/AndroidRuntime(19409):
	// android.content.ActivityNotFoundException: Unable to find explicit
	// activity class
	// {object.doorbellnew2.client/object.doorbellnew1.client.other.AllVideoCheckActivity};
	// have you declared this activity in your AndroidManifest.xml?
	private Button setting_pwd_set;
	private SharedPreferences preuser;
	private String lockPwd = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settingdelay);

		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		back = (Button) findViewById(R.id.back);
		edit_time = (EditText) findViewById(R.id.edit_time);
		preuser = getSharedPreferences("shix_zhao_user", Context.MODE_PRIVATE);
		lockPwd = preuser.getString(strDID + "lockpwd", "123456");
		String cgi1 = "get_lock_config.cgi?&" + "loginuse="
				+ SystemValue.doorBellAdmin + "&loginpas="
				+ SystemValue.doorBellPass + "&user="
				+ SystemValue.doorBellAdmin + "&pwd="
				+ SystemValue.doorBellPass;
		Log.d("test", "shix-cgi1:" + cgi1);
		setting_pwd_set = (Button) findViewById(R.id.setting_pwd_set);
		setting_pwd_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				VibratorUtil.Vibrate(SettingDelayActivity.this, 20);
				if (popupWindow_group != null) {
					stringBuffer.replace(0, stringBuffer.length(), "");
					editText_GroupName_pass.setText("");
					popupWindow_group
							.showAtLocation(back, Gravity.CENTER, 0, 0);
				}
			}
		});
		group1 = (RadioGroup) findViewById(R.id.group1);
		rb1 = (RadioButton) findViewById(R.id.rb1);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rb1.getId()) {
					lock_type1 = 0;
				} else if (checkedId == rb2.getId()) {
					lock_type1 = 1;
				}
			}
		});
		NativeCaller.TransferMessage(strDID, cgi1, 1);
		BridgeService.setDoorBellLockParmInterface(this);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);// �˳�����
			}
		});
		done = (Button) findViewById(R.id.ok);
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edit_time.getText().toString().trim().length() == 0) {
					showToast("������ʱ��");
					return;
				}
				int reInt = Integer.parseInt(edit_time.getText().toString()
						.trim());
				if (reInt < 1 || reInt > 30) {
					showToast("1-30");
					return;
				}

				String set_lock_config = "set_lock_config.cgi?&lock_type="
						+ lock_type1 + "&lock_delay=" + reInt + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;

				NativeCaller.TransferMessage(strDID, set_lock_config, 1);
				finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);// �˳�����
			}
		});
		initExitPopupWindow_Group();
	}

	private View popv_group;
	private PopupWindow popupWindow_group;
	private Button btn_create, btn_cancel;
	private EditText editText_GroupName_pass, editText_GroupName_pass2,
			editText_GroupName_pass3;
	private Button button_1, button_2, button_3, button_4, button_5, button_6,
			button_7, button_8, button_9, button_0, button_reset, button_delet;

	public void initExitPopupWindow_Group() {
		stringBuffer = new StringBuffer();
		stringBuffer2 = new StringBuffer();
		stringBuffer3 = new StringBuffer();
		LayoutInflater li = LayoutInflater.from(this);
		popv_group = li.inflate(R.layout.popup_edit_pwd, null);
		btn_create = (Button) popv_group
				.findViewById(R.id.popup_create_group_create);
		btn_cancel = (Button) popv_group
				.findViewById(R.id.popup_create_group_cancel);
		editText_GroupName_pass = (EditText) popv_group
				.findViewById(R.id.popup_create_group_edittext_pass);
		editText_GroupName_pass2 = (EditText) popv_group
				.findViewById(R.id.popup_create_group_edittext_pass2);
		editText_GroupName_pass3 = (EditText) popv_group
				.findViewById(R.id.popup_create_group_edittext_pass3);
		editText_GroupName_pass.setOnClickListener(this);
		editText_GroupName_pass2.setOnClickListener(this);
		editText_GroupName_pass3.setOnClickListener(this);

		button_1 = (Button) popv_group.findViewById(R.id.button_1);
		button_1.setOnClickListener(this);

		button_2 = (Button) popv_group.findViewById(R.id.button_2);
		button_2.setOnClickListener(this);

		button_3 = (Button) popv_group.findViewById(R.id.button_3);
		button_3.setOnClickListener(this);

		button_4 = (Button) popv_group.findViewById(R.id.button_4);
		button_4.setOnClickListener(this);

		button_5 = (Button) popv_group.findViewById(R.id.button_5);
		button_5.setOnClickListener(this);

		button_6 = (Button) popv_group.findViewById(R.id.button_6);
		button_6.setOnClickListener(this);

		button_7 = (Button) popv_group.findViewById(R.id.button_7);
		button_7.setOnClickListener(this);

		button_8 = (Button) popv_group.findViewById(R.id.button_8);
		button_8.setOnClickListener(this);

		button_9 = (Button) popv_group.findViewById(R.id.button_9);
		button_9.setOnClickListener(this);

		button_0 = (Button) popv_group.findViewById(R.id.button_0);
		button_0.setOnClickListener(this);

		button_reset = (Button) popv_group.findViewById(R.id.button_reset);
		button_reset.setOnClickListener(this);

		button_delet = (Button) popv_group.findViewById(R.id.button_delet);
		button_delet.setOnClickListener(this);

		btn_create.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		popupWindow_group = new PopupWindow(popv_group,
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		// popupWindow_group.setAnimationStyle(R.style.AnimationPreview);
		popupWindow_group.setFocusable(true);
		popupWindow_group.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		popupWindow_group
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow_group.setOutsideTouchable(true);
		popupWindow_group.setBackgroundDrawable(new ColorDrawable(0));
		// popupWindow.update();
		popupWindow_group
				.setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						popupWindow_group.dismiss();
					}
				});
		popupWindow_group.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow_group.dismiss();
				}
				return false;
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		BridgeService.setDoorBellLockParmInterface(null);
		super.onDestroy();
	}

	@Override
	public void callBackDoorBellLockParm(String did, int lock_type,
			int lock_delay) {
		// TODO Auto-generated method stub
		Log.e("door_CallBack_GetLockParm", "did:" + did + "  lock_type:"
				+ lock_type + "  lock_delay:" + lock_delay);
		Message message = new Message();
		message.what = 0;
		message.arg1 = lock_type;
		message.arg2 = lock_delay;
		handler.sendMessage(message);
	}

	private StringBuffer stringBuffer, stringBuffer2, stringBuffer3;
	private int tag = 1;

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		VibratorUtil.Vibrate(SettingDelayActivity.this, 20);
//		switch (arg0.getId()) {
//		// button_1, button_2, button_3, button_4, button_5, button_6,
//		// button_7, button_8, button_9, button_0, button_reset, button_delet;
//		case R.id.popup_create_group_edittext_pass:
//			editText_GroupName_pass
//					.setBackgroundResource(R.drawable.biz_edittext_border_press);
//			editText_GroupName_pass2
//					.setBackgroundResource(R.drawable.biz_edittext_border);
//			editText_GroupName_pass3
//					.setBackgroundResource(R.drawable.biz_edittext_border);
//			tag = 1;
//			break;
//		case R.id.popup_create_group_edittext_pass2:
//			editText_GroupName_pass
//					.setBackgroundResource(R.drawable.biz_edittext_border);
//			editText_GroupName_pass2
//					.setBackgroundResource(R.drawable.biz_edittext_border_press);
//			editText_GroupName_pass3
//					.setBackgroundResource(R.drawable.biz_edittext_border);
//			tag = 2;
//			break;
//		case R.id.popup_create_group_edittext_pass3:
//			editText_GroupName_pass
//					.setBackgroundResource(R.drawable.biz_edittext_border);
//			editText_GroupName_pass2
//					.setBackgroundResource(R.drawable.biz_edittext_border);
//			editText_GroupName_pass3
//					.setBackgroundResource(R.drawable.biz_edittext_border_press);
//			tag = 3;
//			break;
//		case R.id.button_reset:
//			if (tag == 1) {
//				stringBuffer.replace(0, stringBuffer.length(), "");
//				editText_GroupName_pass.setText(stringBuffer.toString().trim());
//			} else if (tag == 2) {
//				stringBuffer2.replace(0, stringBuffer2.length(), "");
//				editText_GroupName_pass2.setText(stringBuffer2.toString()
//						.trim());
//			} else {
//				stringBuffer3.replace(0, stringBuffer3.length(), "");
//				editText_GroupName_pass3.setText(stringBuffer3.toString()
//						.trim());
//			}
//
//			break;
//		case R.id.button_delet:
//
//			if (tag == 1) {
//				if (stringBuffer.length() - 1 >= 0) {
//					stringBuffer.delete(stringBuffer.length() - 1,
//							stringBuffer.length());
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() - 1 >= 0) {
//					stringBuffer2.delete(stringBuffer2.length() - 1,
//							stringBuffer2.length());
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() - 1 >= 0) {
//					stringBuffer3.delete(stringBuffer3.length() - 1,
//							stringBuffer3.length());
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//
//			break;
//		case R.id.button_1:
//
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("1");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("1");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("1");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//			break;
//
//		case R.id.button_2:
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("2");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("2");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("2");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//			break;
//
//		case R.id.button_3:
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("3");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("3");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("3");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//
//			break;
//
//		case R.id.button_4:
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("4");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("4");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("4");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//
//			break;
//
//		case R.id.button_5:
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("5");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("5");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("5");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//			break;
//
//		case R.id.button_6:
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("6");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("6");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("6");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//			break;
//
//		case R.id.button_7:
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("7");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("7");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("7");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//
//			break;
//
//		case R.id.button_8:
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("8");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("8");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("8");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//			break;
//
//		case R.id.button_9:
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("9");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("9");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("9");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//			break;
//
//		case R.id.button_0:
//			if (tag == 1) {
//				if (stringBuffer.length() < 6) {
//					stringBuffer.append("0");
//					editText_GroupName_pass.setText(stringBuffer.toString()
//							.trim());
//				}
//			} else if (tag == 2) {
//				if (stringBuffer2.length() < 6) {
//					stringBuffer2.append("0");
//					editText_GroupName_pass2.setText(stringBuffer2.toString()
//							.trim());
//				}
//			} else {
//				if (stringBuffer3.length() < 6) {
//					stringBuffer3.append("0");
//					editText_GroupName_pass3.setText(stringBuffer3.toString()
//							.trim());
//				}
//			}
//			break;
//
//		case R.id.popup_create_group_create:
//			String pwd0 = editText_GroupName_pass.getText().toString().trim();
//			String pwd1 = editText_GroupName_pass2.getText().toString().trim();
//			String pwd2 = editText_GroupName_pass3.getText().toString().trim();
//			if (pwd0.length() < 6 || pwd1.length() < 6 || pwd2.length() < 6) {
//				showToast(R.string.lock_pwd_setting_show4);
//				return;
//			}
//			if (!pwd0.equals(lockPwd)) {
//				showToast(R.string.lock_pwd_setting_show5);
//				return;
//			}
//			if (!pwd1.equals(pwd2)) {
//				showToast(R.string.lock_pwd_setting_show6);
//				return;
//			}
//			SharedPreferences.Editor editor = preuser.edit();
//			editor.putString(strDID + "lockpwd", pwd1);
//			editor.commit();
//			popupWindow_group.dismiss();
//			break;
//		case R.id.popup_create_group_cancel:
//			popupWindow_group.dismiss();
//			break;
//		default:
//			break;
//		}
	}

	// @Override
	// public void CallBackTimeDelay(int time) {
	// // TODO Auto-generated method stub
	// Message message = new Message();
	// message.what = 0;
	// message.arg1 = time;
	// handler.sendMessage(message);
	// }

}
