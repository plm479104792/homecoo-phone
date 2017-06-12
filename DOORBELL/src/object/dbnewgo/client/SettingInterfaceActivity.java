package object.dbnewgo.client;

import object.dbnewgo.client.BridgeService.DoorBellInterfaceParmInterface;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * ��������
 * */
public class SettingInterfaceActivity extends BaseActivity implements
		OnClickListener, DoorBellInterfaceParmInterface {
	int pin = 0;
	int pin_bind = 0;
	int pout = 0;
	int pout_bind = 0;
	private String strDID = null;
	private Button ok = null;
	private Button cancel = null;
	private final int TIMEOUT = 3000;
	private final int PARAMS = 3;
	private RadioGroup group1;
	private RadioButton rb1, rb2;
	private RadioGroup group1_out;
	private RadioButton rb1_out, rb2_out;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case PARAMS:// get user params
				if (pin_bind == 0) {
					tv_alarm_type
							.setText(R.string.doorbell_setting_interface_in_rele_dooralarm);
				}
				if (pout_bind == 0) {
					tv_alarm_leve
							.setText(R.string.doorbell_setting_interface_out_rele_talk);
				} else if (pout_bind == 1) {
					tv_alarm_leve
							.setText(R.string.doorbell_setting_interface_out_rele_alrm);
				}
				if (pin == 0) {
					rb1.setChecked(true);
					rb2.setChecked(false);
				} else {
					rb2.setChecked(true);
					rb1.setChecked(false);
				}
				if (pout == 0) {
					rb1_out.setChecked(true);
					rb2_out.setChecked(false);
				} else {
					rb2_out.setChecked(true);
					rb1_out.setChecked(false);
				}
				break;

			default:
				break;
			}
		}
	};

	private ImageButton img_alarm_type_dr, image_alarm_leve_dr;

	private TextView tv_alarm_type, tv_alarm_leve;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDataFromOther();
		setContentView(R.layout.settinginterface);
		BridgeService.setDoorBellInterfaceParmInterface(this);
		String get_alarm_config = "get_pin_config.cgi?&" + "loginuse="
				+ SystemValue.doorBellAdmin + "&loginpas="
				+ SystemValue.doorBellPass + "&user="
				+ SystemValue.doorBellAdmin + "&pwd="
				+ SystemValue.doorBellPass;
		NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
		tv_alarm_type = (TextView) findViewById(R.id.tv_alarm_type);
		tv_alarm_leve = (TextView) findViewById(R.id.tv_alarm_leve);
		img_alarm_type_dr = (ImageButton) findViewById(R.id.img_alarm_type_dr);
		image_alarm_leve_dr = (ImageButton) findViewById(R.id.image_alarm_leve_dr);
		image_alarm_leve_dr.setOnClickListener(this);
		img_alarm_type_dr.setOnClickListener(this);
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
					pin = 0;
				} else if (checkedId == rb2.getId()) {
					pin = 1;
				}
			}
		});

		group1_out = (RadioGroup) findViewById(R.id.group1_out);
		rb1_out = (RadioButton) findViewById(R.id.rb1_out);
		rb2_out = (RadioButton) findViewById(R.id.rb2_out);
		group1_out
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == rb1_out.getId()) {
							pout = 0;
						} else if (checkedId == rb2_out.getId()) {
							pout = 1;
						}
					}
				});
		initExitPopupWindow_type();
		initExitPopupWindow_leve();
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
		BridgeService.setDoorBellInterfaceParmInterface(null);
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		int id=arg0.getId();
		if (id==R.id.img_alarm_type_dr) {
			popupWindow_alarm_type.showAsDropDown(img_alarm_type_dr);
		}else if (id==R.id.image_alarm_leve_dr) {
			popupWindow_alarm_leve.showAsDropDown(image_alarm_leve_dr);
		}else if (id==R.id.alerm_ok) {
			String set_pin_config = "set_pin_config.cgi?&pin=" + pin
					+ "&pin_bind=" + pin_bind + "&pout=" + pout + "&pout_bind="
					+ pout_bind + "&" + "loginuse=" + SystemValue.doorBellAdmin
					+ "&loginpas=" + SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, set_pin_config, 1);
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		}else if (id==R.id.alerm_cancel) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		}else if (id==R.id.button_type_0) {
			pin_bind = 0;
			tv_alarm_type
					.setText(R.string.doorbell_setting_interface_in_rele_dooralarm);
			popupWindow_alarm_type.dismiss();
		}else if (id==R.id.button_leve_1) {
			pout_bind = 0;
			tv_alarm_leve
					.setText(R.string.doorbell_setting_interface_out_rele_talk);
			popupWindow_alarm_leve.dismiss();
		}else if (id==R.id.button_leve_2) {
			pout_bind = 1;
			tv_alarm_leve
					.setText(R.string.doorbell_setting_interface_out_rele_alrm);
			popupWindow_alarm_leve.dismiss();
		}
		
		
		// TODO Auto-generated method stub
//		switch (arg0.getId()) {
//		case R.id.img_alarm_type_dr:
//			popupWindow_alarm_type.showAsDropDown(img_alarm_type_dr);
//			break;
//		case R.id.image_alarm_leve_dr:
//			popupWindow_alarm_leve.showAsDropDown(image_alarm_leve_dr);
//			break;
//		case R.id.alerm_ok:
			// final String set_alarm_config =
			// "set_alarm_config.cgi?&alarm_on=1&alarm_type=2&alarm_level=0&alarm_delay=14
			// &alarm_start_hour=12&alarm_start_minute=24&alarm_stop_hour=16&alarm_stop_minute=30"
			// + "loginuse="
			// + SystemValue.doorBellAdmin
			// + "&loginpas="
			// + SystemValue.doorBellPass
			// + "&user="
			// + SystemValue.doorBellAdmin
			// + "&pwd="
			// + SystemValue.doorBellPass;
//			String set_pin_config = "set_pin_config.cgi?&pin=" + pin
//					+ "&pin_bind=" + pin_bind + "&pout=" + pout + "&pout_bind="
//					+ pout_bind + "&" + "loginuse=" + SystemValue.doorBellAdmin
//					+ "&loginpas=" + SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, set_pin_config, 1);
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����

//			break;
//		case R.id.alerm_cancel:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//		case R.id.button_type_0:
//			pin_bind = 0;
//			tv_alarm_type
//					.setText(R.string.doorbell_setting_interface_in_rele_dooralarm);
//			popupWindow_alarm_type.dismiss();
//			break;
//
//		case R.id.button_leve_1:
//			pout_bind = 0;
//			tv_alarm_leve
//					.setText(R.string.doorbell_setting_interface_out_rele_talk);
//			popupWindow_alarm_leve.dismiss();
//			break;
//		case R.id.button_leve_2:
//			pout_bind = 1;
//			tv_alarm_leve
//					.setText(R.string.doorbell_setting_interface_out_rele_alrm);
//			popupWindow_alarm_leve.dismiss();
//			break;
//
//		default:
//			break;
//		}
	}

	View popv_alerm_type;
	Button button_type_0;
	PopupWindow popupWindow_alarm_type;

	public void initExitPopupWindow_type() {
		LayoutInflater li = LayoutInflater.from(this);
		popv_alerm_type = li.inflate(R.layout.popup_interface_in, null);
		button_type_0 = (Button) popv_alerm_type
				.findViewById(R.id.button_type_0);

		button_type_0.setOnClickListener(this);
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
	Button button_leve_1, button_leve_2;
	PopupWindow popupWindow_alarm_leve;

	public void initExitPopupWindow_leve() {
		LayoutInflater li = LayoutInflater.from(this);
		popv_alerm_leve = li.inflate(R.layout.popup_interface_out, null);
		button_leve_1 = (Button) popv_alerm_leve
				.findViewById(R.id.button_leve_1);
		button_leve_2 = (Button) popv_alerm_leve
				.findViewById(R.id.button_leve_2);

		button_leve_1.setOnClickListener(this);
		button_leve_2.setOnClickListener(this);

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
	public void callBackDoorBellInterfaceParm(String did, int pin,
			int pin_bind, int pout, int pout_bind) {
		// TODO Auto-generated method stub
		this.pin = pin;
		this.pin_bind = pin_bind;
		this.pout = pout;
		this.pout_bind = pout_bind;
		mHandler.sendEmptyMessage(PARAMS);
	}
}
