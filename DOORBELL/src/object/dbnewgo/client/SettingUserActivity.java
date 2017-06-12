package object.dbnewgo.client;

import object.dbnewgo.client.BridgeService.DoorBellUserInterface;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("NewApi")
public class SettingUserActivity extends BaseActivity implements
		OnClickListener, DoorBellUserInterface {

	private String user1;
	private String pwd1;
	private String user2;
	private String pwd2;
	private String user3;
	private String pwd3;
	private String user4;

	private String pwd4;
	private String user5;
	private String pwd5;
	private String user6;
	private String pwd6;

	private String user7;
	private String pwd7;
	private String user8;
	private String pwd8;
	private String alias;

	private int admin;
	private int s1;
	private int s2;
	private int s3;
	private int s4;
	private int s5;
	private int s6;
	private int s7;
	private int s8;

	private LinearLayout liner_user1, liner_user2, liner_user3, liner_user4,
			liner_user5, liner_user6, liner_user7, liner_user8;

	private TextView tv_user_show1, tv_user_show2, tv_user_show3,
			tv_user_show4, tv_user_show5, tv_user_show6, tv_user_show7,
			tv_user_show8;

	private TextView tv_user1, tv_user2, tv_user3, tv_user4, tv_user5,
			tv_user6, tv_user7, tv_user8;

	private TextView tv_statu1, tv_statu2, tv_statu3, tv_statu4, tv_statu5,
			tv_statu6, tv_statu7, tv_statu8;

	private Button btn_del1, btn_del2, btn_del3, btn_del4, btn_del5, btn_del6,
			btn_del7, btn_del8;

	private Button btn_edit1, btn_edit2, btn_edit3, btn_edit4, btn_edit5,
			btn_edit6, btn_edit7, btn_edit8;

	private Button button_cancel, button_ok;

	private String adminIsWho = "";
	private final int PARMS = 110;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PARMS:
				if (user1 != null && user1.length() > 3) {
					liner_user1.setVisibility(View.VISIBLE);
					tv_user_show1.setText(getResources().getString(
							R.string.doorbell_setting_user)
							+ "1");
					tv_user1.setText(user1);
					if (s1 == 0) {
						tv_statu1
								.setText(R.string.doorbell_setting_user_statu0);
					} else if (s1 == 1) {
						tv_statu1
								.setText(R.string.doorbell_setting_user_statu1);
					} else if (s1 == 2) {
						tv_statu1
								.setText(R.string.doorbell_setting_user_statu2);
					} else {
						tv_statu1
								.setText(R.string.doorbell_setting_user_statu3);
					}
					if (user1.equals(SystemValue.doorBellAdmin)) {
						btn_edit1.setVisibility(View.VISIBLE);
					} else {
						btn_edit1.setVisibility(View.GONE);
					}
				} else {
					liner_user1.setVisibility(View.GONE);
				}

				if (user2 != null && user2.length() > 3) {
					liner_user2.setVisibility(View.VISIBLE);
					tv_user_show2.setText(getResources().getString(
							R.string.doorbell_setting_user)
							+ "2");
					tv_user2.setText(user2);
					if (s2 == 0) {
						tv_statu2
								.setText(R.string.doorbell_setting_user_statu0);
					} else if (s2 == 1) {
						tv_statu2
								.setText(R.string.doorbell_setting_user_statu1);
					} else if (s2 == 2) {
						tv_statu2
								.setText(R.string.doorbell_setting_user_statu2);
					} else {
						tv_statu2
								.setText(R.string.doorbell_setting_user_statu3);
					}

					if (user2.equals(SystemValue.doorBellAdmin)) {
						btn_edit2.setVisibility(View.VISIBLE);
					} else {
						btn_edit2.setVisibility(View.GONE);
					}
				} else {
					liner_user2.setVisibility(View.GONE);
				}

				if (user3 != null && user3.length() > 3) {
					liner_user3.setVisibility(View.VISIBLE);
					tv_user_show3.setText(getResources().getString(
							R.string.doorbell_setting_user)
							+ "3");
					tv_user3.setText(user3);
					if (s3 == 0) {
						tv_statu3
								.setText(R.string.doorbell_setting_user_statu0);
					} else if (s3 == 1) {
						tv_statu3
								.setText(R.string.doorbell_setting_user_statu1);
					} else if (s3 == 2) {
						tv_statu3
								.setText(R.string.doorbell_setting_user_statu2);
					} else {
						tv_statu3
								.setText(R.string.doorbell_setting_user_statu3);
					}

					if (user3.equals(SystemValue.doorBellAdmin)) {
						btn_edit3.setVisibility(View.VISIBLE);
					} else {
						btn_edit3.setVisibility(View.GONE);
					}
				} else {
					liner_user3.setVisibility(View.GONE);
				}

				if (user4 != null && user4.length() > 3) {
					liner_user4.setVisibility(View.VISIBLE);
					tv_user_show4.setText(getResources().getString(
							R.string.doorbell_setting_user)
							+ "4");
					tv_user4.setText(user4);
					if (s4 == 0) {
						tv_statu4
								.setText(R.string.doorbell_setting_user_statu0);
					} else if (s4 == 1) {
						tv_statu4
								.setText(R.string.doorbell_setting_user_statu1);
					} else if (s4 == 2) {
						tv_statu4
								.setText(R.string.doorbell_setting_user_statu2);
					} else {
						tv_statu4
								.setText(R.string.doorbell_setting_user_statu3);
					}

					if (user4.equals(SystemValue.doorBellAdmin)) {
						btn_edit4.setVisibility(View.VISIBLE);
					} else {
						btn_edit4.setVisibility(View.GONE);
					}
				} else {
					liner_user4.setVisibility(View.GONE);
				}

				if (user5 != null && user5.length() > 3) {
					liner_user5.setVisibility(View.VISIBLE);
					tv_user_show5.setText(getResources().getString(
							R.string.doorbell_setting_user)
							+ "5");
					tv_user5.setText(user5);
					if (s5 == 0) {
						tv_statu5
								.setText(R.string.doorbell_setting_user_statu0);
					} else if (s5 == 1) {
						tv_statu5
								.setText(R.string.doorbell_setting_user_statu1);
					} else if (s5 == 2) {
						tv_statu5
								.setText(R.string.doorbell_setting_user_statu2);
					} else {
						tv_statu5
								.setText(R.string.doorbell_setting_user_statu3);
					}

					if (user5.equals(SystemValue.doorBellAdmin)) {
						btn_edit5.setVisibility(View.VISIBLE);
					} else {
						btn_edit5.setVisibility(View.GONE);
					}
				} else {
					liner_user5.setVisibility(View.GONE);
				}

				if (user6 != null && user6.length() > 3) {
					liner_user6.setVisibility(View.VISIBLE);
					tv_user_show6.setText(getResources().getString(
							R.string.doorbell_setting_user)
							+ "6");
					tv_user6.setText(user6);
					if (s6 == 0) {
						tv_statu6
								.setText(R.string.doorbell_setting_user_statu0);
					} else if (s6 == 1) {
						tv_statu6
								.setText(R.string.doorbell_setting_user_statu1);
					} else if (s6 == 2) {
						tv_statu6
								.setText(R.string.doorbell_setting_user_statu2);
					} else {
						tv_statu6
								.setText(R.string.doorbell_setting_user_statu3);
					}

					if (user6.equals(SystemValue.doorBellAdmin)) {
						btn_edit6.setVisibility(View.VISIBLE);
					} else {
						btn_edit6.setVisibility(View.GONE);
					}
				} else {
					liner_user6.setVisibility(View.GONE);
				}

				if (user7 != null && user7.length() > 3) {
					liner_user7.setVisibility(View.VISIBLE);
					tv_user_show7.setText(getResources().getString(
							R.string.doorbell_setting_user)
							+ "7");
					tv_user7.setText(user7);
					if (s7 == 0) {
						tv_statu7
								.setText(R.string.doorbell_setting_user_statu0);
					} else if (s7 == 1) {
						tv_statu7
								.setText(R.string.doorbell_setting_user_statu1);
					} else if (s7 == 2) {
						tv_statu7
								.setText(R.string.doorbell_setting_user_statu2);
					} else {
						tv_statu7
								.setText(R.string.doorbell_setting_user_statu3);
					}

					if (user7.equals(SystemValue.doorBellAdmin)) {
						btn_edit7.setVisibility(View.VISIBLE);
					} else {
						btn_edit7.setVisibility(View.GONE);
					}
				} else {
					liner_user7.setVisibility(View.GONE);
				}

				if (user8 != null && user8.length() > 3) {
					liner_user8.setVisibility(View.VISIBLE);
					tv_user_show8.setText(getResources().getString(
							R.string.doorbell_setting_user)
							+ "8");
					tv_user8.setText(user8);
					if (s8 == 0) {
						tv_statu8
								.setText(R.string.doorbell_setting_user_statu0);
					} else if (s8 == 1) {
						tv_statu8
								.setText(R.string.doorbell_setting_user_statu1);
					} else if (s8 == 2) {
						tv_statu8
								.setText(R.string.doorbell_setting_user_statu2);
					} else {
						tv_statu8
								.setText(R.string.doorbell_setting_user_statu3);
					}

					if (user8.equals(SystemValue.doorBellAdmin)) {
						btn_edit8.setVisibility(View.VISIBLE);
					} else {
						btn_edit8.setVisibility(View.GONE);
					}
				} else {
					liner_user8.setVisibility(View.GONE);
				}

				if (admin == 1) {
					tv_user_show1.setText(R.string.doorbell_setting_admin);
					tv_user_show1.setTextColor(0xfffb3902);
					btn_del1.setVisibility(View.GONE);
					if (SystemValue.doorBellAdmin.equals(user1)) {
						btn_edit1.setVisibility(View.VISIBLE);
						btn_del2.setVisibility(View.VISIBLE);
						btn_del3.setVisibility(View.VISIBLE);
						btn_del4.setVisibility(View.VISIBLE);
						btn_del5.setVisibility(View.VISIBLE);
						btn_del6.setVisibility(View.VISIBLE);
						btn_del7.setVisibility(View.VISIBLE);
						btn_del8.setVisibility(View.VISIBLE);
					} else {
						button_ok.setVisibility(View.GONE);
					}
				} else if (admin == 2) {
					tv_user_show2.setText(R.string.doorbell_setting_admin);
					tv_user_show2.setTextColor(0xfffb3902);
					btn_del2.setVisibility(View.GONE);
					if (SystemValue.doorBellAdmin.equals(user2)) {
						btn_edit2.setVisibility(View.VISIBLE);
						btn_del1.setVisibility(View.VISIBLE);
						btn_del3.setVisibility(View.VISIBLE);
						btn_del4.setVisibility(View.VISIBLE);
						btn_del5.setVisibility(View.VISIBLE);
						btn_del6.setVisibility(View.VISIBLE);
						btn_del7.setVisibility(View.VISIBLE);
						btn_del8.setVisibility(View.VISIBLE);
					} else {
						button_ok.setVisibility(View.GONE);
					}
				} else if (admin == 3) {
					tv_user_show3.setText(R.string.doorbell_setting_admin);
					tv_user_show3.setTextColor(0xfffb3902);
					btn_del3.setVisibility(View.GONE);
					if (SystemValue.doorBellAdmin.equals(user3)) {
						btn_edit3.setVisibility(View.VISIBLE);
						btn_del2.setVisibility(View.VISIBLE);
						btn_del1.setVisibility(View.VISIBLE);
						btn_del4.setVisibility(View.VISIBLE);
						btn_del5.setVisibility(View.VISIBLE);
						btn_del6.setVisibility(View.VISIBLE);
						btn_del7.setVisibility(View.VISIBLE);
						btn_del8.setVisibility(View.VISIBLE);
					} else {
						button_ok.setVisibility(View.GONE);
					}
				} else if (admin == 4) {
					tv_user_show4.setText(R.string.doorbell_setting_admin);
					tv_user_show4.setTextColor(0xfffb3902);
					btn_del4.setVisibility(View.GONE);
					if (SystemValue.doorBellAdmin.equals(user4)) {
						btn_edit4.setVisibility(View.VISIBLE);
						btn_del2.setVisibility(View.VISIBLE);
						btn_del3.setVisibility(View.VISIBLE);
						btn_del1.setVisibility(View.VISIBLE);
						btn_del5.setVisibility(View.VISIBLE);
						btn_del6.setVisibility(View.VISIBLE);
						btn_del7.setVisibility(View.VISIBLE);
						btn_del8.setVisibility(View.VISIBLE);
					} else {
						button_ok.setVisibility(View.GONE);
					}
				} else if (admin == 5) {
					tv_user_show5.setText(R.string.doorbell_setting_admin);
					tv_user_show5.setTextColor(0xfffb3902);
					btn_del5.setVisibility(View.GONE);
					if (SystemValue.doorBellAdmin.equals(user5)) {
						btn_edit5.setVisibility(View.VISIBLE);
						btn_del2.setVisibility(View.VISIBLE);
						btn_del3.setVisibility(View.VISIBLE);
						btn_del4.setVisibility(View.VISIBLE);
						btn_del1.setVisibility(View.VISIBLE);
						btn_del6.setVisibility(View.VISIBLE);
						btn_del7.setVisibility(View.VISIBLE);
						btn_del8.setVisibility(View.VISIBLE);
					} else {
						button_ok.setVisibility(View.GONE);
					}
				} else if (admin == 6) {
					tv_user_show6.setText(R.string.doorbell_setting_admin);
					tv_user_show6.setTextColor(0xfffb3902);
					btn_del6.setVisibility(View.GONE);
					if (SystemValue.doorBellAdmin.equals(user6)) {
						btn_edit6.setVisibility(View.VISIBLE);
						btn_del2.setVisibility(View.VISIBLE);
						btn_del3.setVisibility(View.VISIBLE);
						btn_del4.setVisibility(View.VISIBLE);
						btn_del5.setVisibility(View.VISIBLE);
						btn_del1.setVisibility(View.VISIBLE);
						btn_del7.setVisibility(View.VISIBLE);
						btn_del8.setVisibility(View.VISIBLE);
					} else {
						button_ok.setVisibility(View.GONE);
					}
				} else if (admin == 7) {
					tv_user_show7.setText(R.string.doorbell_setting_admin);
					tv_user_show7.setTextColor(0xfffb3902);
					btn_del7.setVisibility(View.GONE);
					if (SystemValue.doorBellAdmin.equals(user7)) {
						btn_edit7.setVisibility(View.VISIBLE);
						btn_del2.setVisibility(View.VISIBLE);
						btn_del3.setVisibility(View.VISIBLE);
						btn_del4.setVisibility(View.VISIBLE);
						btn_del5.setVisibility(View.VISIBLE);
						btn_del6.setVisibility(View.VISIBLE);
						btn_del1.setVisibility(View.VISIBLE);
						btn_del8.setVisibility(View.VISIBLE);
					} else {
						button_ok.setVisibility(View.GONE);
					}
				} else if (admin == 8) {
					tv_user_show8.setText(R.string.doorbell_setting_admin);
					tv_user_show8.setTextColor(0xfffb3902);
					btn_del8.setVisibility(View.GONE);
					if (SystemValue.doorBellAdmin.equals(user8)) {
						btn_edit8.setVisibility(View.VISIBLE);
						btn_del2.setVisibility(View.VISIBLE);
						btn_del3.setVisibility(View.VISIBLE);
						btn_del4.setVisibility(View.VISIBLE);
						btn_del5.setVisibility(View.VISIBLE);
						btn_del6.setVisibility(View.VISIBLE);
						btn_del7.setVisibility(View.VISIBLE);
						btn_del1.setVisibility(View.VISIBLE);
					} else {
						button_ok.setVisibility(View.GONE);
					}
				}

				if (tag == 1) {
					if (!setPwd.equals(pwd1)) {
						showToast(R.string.biz_add_edit_show_sucess);
					}
				} else if (tag == 2) {
					if (!setPwd.equals(pwd2)) {
						showToast(R.string.biz_add_edit_show_sucess);
					}
				} else if (tag == 3) {
					if (!setPwd.equals(pwd3)) {
						showToast(R.string.biz_add_edit_show_sucess);
					}
				} else if (tag == 4) {
					if (!setPwd.equals(pwd4)) {
						showToast(R.string.biz_add_edit_show_sucess);
					}
				} else if (tag == 5) {
					if (!setPwd.equals(pwd5)) {
						showToast(R.string.biz_add_edit_show_sucess);
					}
				} else if (tag == 6) {
					if (!setPwd.equals(pwd6)) {
						showToast(R.string.biz_add_edit_show_sucess);
					}
				} else if (tag == 7) {
					if (!setPwd.equals(pwd7)) {
						showToast(R.string.biz_add_edit_show_sucess);
					}
				} else if (tag == 8) {
					if (!setPwd.equals(pwd8)) {
						showToast(R.string.biz_add_edit_show_sucess);
					}
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDataFromOther();
		setContentView(R.layout.settinguser0111);
		findView();
		BridgeService.setDoorBellUserInterface(this);
		String get_alarm_config = "get_user_config.cgi?&" + "loginuse="
				+ SystemValue.doorBellAdmin + "&loginpas="
				+ SystemValue.doorBellPass + "&user="
				+ SystemValue.doorBellAdmin + "&pwd="
				+ SystemValue.doorBellPass;
		NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
		initExitPopupWindow_Group();
		tag = 0;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		BridgeService.setDoorBellUserInterface(null);
		super.onDestroy();
	}

	private void findView() {
		// TODO Auto-generated method stub
		button_cancel = (Button) findViewById(R.id.user_cancel);
		button_cancel.setOnClickListener(this);
		button_ok = (Button) findViewById(R.id.user_ok);
		button_ok.setOnClickListener(this);

		liner_user1 = (LinearLayout) findViewById(R.id.liner_user1);
		tv_user_show1 = (TextView) findViewById(R.id.tv_user_show1);
		tv_user1 = (TextView) findViewById(R.id.tv_user1);
		tv_statu1 = (TextView) findViewById(R.id.tv_statu1);
		btn_del1 = (Button) findViewById(R.id.btn_del1);
		btn_edit1 = (Button) findViewById(R.id.btn_edit1);
		btn_del1.setOnClickListener(this);
		btn_edit1.setOnClickListener(this);

		liner_user2 = (LinearLayout) findViewById(R.id.liner_user2);
		tv_user_show2 = (TextView) findViewById(R.id.tv_user_show2);
		tv_user2 = (TextView) findViewById(R.id.tv_user2);
		tv_statu2 = (TextView) findViewById(R.id.tv_statu2);
		btn_del2 = (Button) findViewById(R.id.btn_del2);
		btn_edit2 = (Button) findViewById(R.id.btn_edit2);
		btn_del2.setOnClickListener(this);
		btn_edit2.setOnClickListener(this);

		liner_user3 = (LinearLayout) findViewById(R.id.liner_user3);
		tv_user_show3 = (TextView) findViewById(R.id.tv_user_show3);
		tv_user3 = (TextView) findViewById(R.id.tv_user3);
		tv_statu3 = (TextView) findViewById(R.id.tv_statu3);
		btn_del3 = (Button) findViewById(R.id.btn_del3);
		btn_edit3 = (Button) findViewById(R.id.btn_edit3);
		btn_del3.setOnClickListener(this);
		btn_edit3.setOnClickListener(this);

		liner_user4 = (LinearLayout) findViewById(R.id.liner_user4);
		tv_user_show4 = (TextView) findViewById(R.id.tv_user_show4);
		tv_user4 = (TextView) findViewById(R.id.tv_user4);
		tv_statu4 = (TextView) findViewById(R.id.tv_statu4);
		btn_del4 = (Button) findViewById(R.id.btn_del4);
		btn_edit4 = (Button) findViewById(R.id.btn_edit4);
		btn_del4.setOnClickListener(this);
		btn_edit4.setOnClickListener(this);

		liner_user5 = (LinearLayout) findViewById(R.id.liner_user5);
		tv_user_show5 = (TextView) findViewById(R.id.tv_user_show5);
		tv_user5 = (TextView) findViewById(R.id.tv_user5);
		tv_statu5 = (TextView) findViewById(R.id.tv_statu5);
		btn_del5 = (Button) findViewById(R.id.btn_del5);
		btn_edit5 = (Button) findViewById(R.id.btn_edit5);
		btn_del5.setOnClickListener(this);
		btn_edit5.setOnClickListener(this);

		liner_user6 = (LinearLayout) findViewById(R.id.liner_user6);
		tv_user_show6 = (TextView) findViewById(R.id.tv_user_show6);
		tv_user6 = (TextView) findViewById(R.id.tv_user6);
		tv_statu6 = (TextView) findViewById(R.id.tv_statu6);
		btn_del6 = (Button) findViewById(R.id.btn_del6);
		btn_edit6 = (Button) findViewById(R.id.btn_edit6);
		btn_del6.setOnClickListener(this);
		btn_edit6.setOnClickListener(this);

		liner_user7 = (LinearLayout) findViewById(R.id.liner_user7);
		tv_user_show7 = (TextView) findViewById(R.id.tv_user_show7);
		tv_user7 = (TextView) findViewById(R.id.tv_user7);
		tv_statu7 = (TextView) findViewById(R.id.tv_statu7);
		btn_del7 = (Button) findViewById(R.id.btn_del7);
		btn_edit7 = (Button) findViewById(R.id.btn_edit7);
		btn_del7.setOnClickListener(this);
		btn_edit7.setOnClickListener(this);

		liner_user8 = (LinearLayout) findViewById(R.id.liner_user8);
		tv_user_show8 = (TextView) findViewById(R.id.tv_user_show8);
		tv_user8 = (TextView) findViewById(R.id.tv_user8);
		tv_statu8 = (TextView) findViewById(R.id.tv_statu8);
		btn_del8 = (Button) findViewById(R.id.btn_del8);
		btn_edit8 = (Button) findViewById(R.id.btn_edit8);
		btn_del8.setOnClickListener(this);
		btn_edit8.setOnClickListener(this);
	}

	private String cameraName = null;
	private String strDID = null;

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}

	@Override
	public void onClick(View arg0) {
		int id=arg0.getId();
		if (id==R.id.user_cancel) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		}else if (id==R.id.user_ok) {
			tag = 0;
			editText_GroupName.setVisibility(View.VISIBLE);
			title.setText(R.string.biz_add_poup_show);
			title_show.setText(R.string.biz_add_edit_show);
			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
					0);
		}else if (id==R.id.btn_del1) {
			String get_alarm_config1 = "set_user_config.cgi?&mod=1&newuser="
					+ user1 + "&newpwd=" + pwd1 + "&loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, get_alarm_config1, 1);
		}else if (id==R.id.btn_edit1) {
			tag = 1;
			editText_GroupName.setVisibility(View.GONE);
			title.setText(R.string.biz_add_poup_show2);
			title_show.setText(R.string.biz_add_edit_show2);
			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
					0);
		}else if (id==R.id.btn_del2) {
			String get_alarm_config2 = "set_user_config.cgi?&mod=1&newuser="
					+ user2 + "&newpwd=" + pwd2 + "&loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, get_alarm_config2, 1);
		}else if (id==R.id.btn_edit2) {
			tag = 2;
			editText_GroupName.setVisibility(View.GONE);
			title.setText(R.string.biz_add_poup_show2);
			title_show.setText(R.string.biz_add_edit_show2);
			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
					0);
		}else if (id==R.id.btn_del3) {
			String get_alarm_config3 = "set_user_config.cgi?&mod=1&newuser="
					+ user3 + "&newpwd=" + pwd3 + "&loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, get_alarm_config3, 1);
		}else if (id==R.id.btn_edit3) {
			tag = 3;
			editText_GroupName.setVisibility(View.GONE);
			title.setText(R.string.biz_add_poup_show2);
			title_show.setText(R.string.biz_add_edit_show2);
			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
					0);
		}else if (id==R.id.btn_del4) {
			String get_alarm_config4 = "set_user_config.cgi?&mod=1&newuser="
					+ user4 + "&newpwd=" + pwd4 + "&loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, get_alarm_config4, 1);
		}else if (id==R.id.btn_edit4) {
			tag = 4;
			editText_GroupName.setVisibility(View.GONE);
			title.setText(R.string.biz_add_poup_show2);
			title_show.setText(R.string.biz_add_edit_show2);
			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
					0);
		}else if (id==R.id.btn_del5) {
			String get_alarm_config5 = "set_user_config.cgi?&mod=1&newuser="
					+ user5 + "&newpwd=" + pwd5 + "&loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, get_alarm_config5, 1);
		}else if (id==R.id.btn_edit5) {
			tag = 5;
			editText_GroupName.setVisibility(View.GONE);
			title.setText(R.string.biz_add_poup_show2);
			title_show.setText(R.string.biz_add_edit_show2);
			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
					0);
		}else if (id==R.id.btn_del6) {
			String get_alarm_config6 = "set_user_config.cgi?&mod=1&newuser="
					+ user6 + "&newpwd=" + pwd6 + "&loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, get_alarm_config6, 1);
		}else if (id==R.id.btn_edit6) {
			tag = 6;
			editText_GroupName.setVisibility(View.GONE);
			title.setText(R.string.biz_add_poup_show2);
			title_show.setText(R.string.biz_add_edit_show2);
			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
					0);
		}else if (id==R.id.btn_del7) {
			String get_alarm_config7 = "set_user_config.cgi?&mod=1&newuser="
					+ user7 + "&newpwd=" + pwd7 + "&loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, get_alarm_config7, 1);
		}else if (id==R.id.btn_edit7) {
			tag = 7;
			editText_GroupName.setVisibility(View.GONE);
			title.setText(R.string.biz_add_poup_show2);
			title_show.setText(R.string.biz_add_edit_show2);
			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
					0);
		}else if (id== R.id.btn_del8) {
			String get_alarm_config8 = "set_user_config.cgi?&mod=1&newuser="
					+ user8 + "&newpwd=" + pwd8 + "&loginuse="
					+ SystemValue.doorBellAdmin + "&loginpas="
					+ SystemValue.doorBellPass + "&user="
					+ SystemValue.doorBellAdmin + "&pwd="
					+ SystemValue.doorBellPass;
			NativeCaller.TransferMessage(strDID, get_alarm_config8, 1);
		}else if (id==R.id.btn_edit8) {
			tag = 8;
			editText_GroupName.setVisibility(View.GONE);
			title.setText(R.string.biz_add_poup_show2);
			title_show.setText(R.string.biz_add_edit_show2);
			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
					0);
		}else if (id==R.id.popup_create_group_create) {
			String name = editText_GroupName.getText().toString().trim();
			String pwd = editText_GroupName_pass.getText().toString().trim();
			if (tag == 0) {
				if (name == null || name.length() < 5) {
					showToast(R.string.biz_add_show_user);
					return;
				}

				if (name.equalsIgnoreCase(user1)
						|| name.equalsIgnoreCase(user2)
						|| name.equalsIgnoreCase(user3)
						|| name.equalsIgnoreCase(user4)
						|| name.equalsIgnoreCase(user5)
						|| name.equalsIgnoreCase(user6)
						|| name.equalsIgnoreCase(user7)
						|| name.equalsIgnoreCase(user8)) {
					showToast(R.string.door_white_list_check_show);
					return;
				}

				if (pwd == null || pwd.length() < 6) {
					showToast(R.string.biz_add_show_pwd);
					return;
				}

				String get_alarm_config = "set_user_config.cgi?&mod=0&newuser="
						+ name + "&newpwd=" + pwd + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;
				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);

			} else if (tag == 1) {
				setPwd = pwd1;
				if (pwd == null || pwd.length() < 6) {
					showToast(R.string.biz_add_show_pwd);
					return;
				}
				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
						+ user1 + "&newpwd=" + pwd + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;
				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
				NativeCaller.PPPPGetSystemParams(strDID,
						ContentCommon.MSG_GET_RESET_USER);
			} else if (tag == 2) {
				setPwd = pwd2;
				if (pwd == null || pwd.length() < 6) {
					showToast(R.string.biz_add_show_pwd);
					return;
				}
				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
						+ user2 + "&newpwd=" + pwd + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;
				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
				NativeCaller.PPPPGetSystemParams(strDID,
						ContentCommon.MSG_GET_RESET_USER);
			} else if (tag == 3) {

				setPwd = pwd3;
				if (pwd == null || pwd.length() < 6) {
					showToast(R.string.biz_add_show_pwd);
					return;
				}
				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
						+ user3 + "&newpwd=" + pwd + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;
				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
				NativeCaller.PPPPGetSystemParams(strDID,
						ContentCommon.MSG_GET_RESET_USER);
			} else if (tag == 4) {

				setPwd = pwd4;
				if (pwd == null || pwd.length() < 6) {
					showToast(R.string.biz_add_show_pwd);
					return;
				}
				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
						+ user4 + "&newpwd=" + pwd + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;
				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
				NativeCaller.PPPPGetSystemParams(strDID,
						ContentCommon.MSG_GET_RESET_USER);
			} else if (tag == 5) {
				setPwd = pwd5;
				if (pwd == null || pwd.length() < 6) {
					showToast(R.string.biz_add_show_pwd);
					return;
				}
				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
						+ user5 + "&newpwd=" + pwd + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;
				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
				NativeCaller.PPPPGetSystemParams(strDID,
						ContentCommon.MSG_GET_RESET_USER);
			} else if (tag == 6) {

				setPwd = pwd6;
				if (pwd == null || pwd.length() < 6) {
					showToast(R.string.biz_add_show_pwd);
					return;
				}
				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
						+ user6 + "&newpwd=" + pwd + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;
				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
				NativeCaller.PPPPGetSystemParams(strDID,
						ContentCommon.MSG_GET_RESET_USER);
			} else if (tag == 7) {
				setPwd = pwd7;
				if (pwd == null || pwd.length() < 6) {
					showToast(R.string.biz_add_show_pwd);
					return;
				}
				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
						+ user7 + "&newpwd=" + pwd + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;
				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
				NativeCaller.PPPPGetSystemParams(strDID,
						ContentCommon.MSG_GET_RESET_USER);
			} else if (tag == 8) {
				setPwd = pwd8;
				if (pwd == null || pwd.length() < 6) {
					showToast(R.string.biz_add_show_pwd);
					return;
				}
				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
						+ user8 + "&newpwd=" + pwd + "&loginuse="
						+ SystemValue.doorBellAdmin + "&loginpas="
						+ SystemValue.doorBellPass + "&user="
						+ SystemValue.doorBellAdmin + "&pwd="
						+ SystemValue.doorBellPass;
				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
				NativeCaller.PPPPGetSystemParams(strDID,
						ContentCommon.MSG_GET_RESET_USER);
			}
			editText_GroupName.setText("");
			editText_GroupName_pass.setText("");
			popupWindow_group.dismiss();
		}else if (id==R.id.popup_create_group_cancel) {
			tag = 0;
			editText_GroupName.setText("");
			editText_GroupName_pass.setText("");
			popupWindow_group.dismiss();
		}
		
		
//		switch (arg0.getId()) {
//		case R.id.user_cancel:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
//			break;
//
//		case R.id.user_ok:
//			tag = 0;
//			editText_GroupName.setVisibility(View.VISIBLE);
//			title.setText(R.string.biz_add_poup_show);
//			title_show.setText(R.string.biz_add_edit_show);
//			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
//					0);
//			break;
//
//		case R.id.btn_del1:
//			String get_alarm_config1 = "set_user_config.cgi?&mod=1&newuser="
//					+ user1 + "&newpwd=" + pwd1 + "&loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, get_alarm_config1, 1);
//			break;
//		case R.id.btn_edit1:
//			tag = 1;
//			editText_GroupName.setVisibility(View.GONE);
//			title.setText(R.string.biz_add_poup_show2);
//			title_show.setText(R.string.biz_add_edit_show2);
//			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
//					0);
//
//			break;

//		case R.id.btn_del2:
//			String get_alarm_config2 = "set_user_config.cgi?&mod=1&newuser="
//					+ user2 + "&newpwd=" + pwd2 + "&loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, get_alarm_config2, 1);
//			break;
//		case R.id.btn_edit2:
//			tag = 2;
//			editText_GroupName.setVisibility(View.GONE);
//			title.setText(R.string.biz_add_poup_show2);
//			title_show.setText(R.string.biz_add_edit_show2);
//			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
//					0);
//			break;

//		case R.id.btn_del3:
//			String get_alarm_config3 = "set_user_config.cgi?&mod=1&newuser="
//					+ user3 + "&newpwd=" + pwd3 + "&loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, get_alarm_config3, 1);
//			break;
//		case R.id.btn_edit3:
//			tag = 3;
//			editText_GroupName.setVisibility(View.GONE);
//			title.setText(R.string.biz_add_poup_show2);
//			title_show.setText(R.string.biz_add_edit_show2);
//			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
//					0);
//			break;

//		case R.id.btn_del4:
//			String get_alarm_config4 = "set_user_config.cgi?&mod=1&newuser="
//					+ user4 + "&newpwd=" + pwd4 + "&loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, get_alarm_config4, 1);
////			break;
//		case R.id.btn_edit4:
//			tag = 4;
//			editText_GroupName.setVisibility(View.GONE);
//			title.setText(R.string.biz_add_poup_show2);
//			title_show.setText(R.string.biz_add_edit_show2);
//			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
//					0);
//			break;

//		case R.id.btn_del5:
//			String get_alarm_config5 = "set_user_config.cgi?&mod=1&newuser="
//					+ user5 + "&newpwd=" + pwd5 + "&loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, get_alarm_config5, 1);
//			break;
//		case R.id.btn_edit5:
//			tag = 5;
//			editText_GroupName.setVisibility(View.GONE);
//			title.setText(R.string.biz_add_poup_show2);
//			title_show.setText(R.string.biz_add_edit_show2);
//			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
//					0);
//			break;

//		case R.id.btn_del6:
//			String get_alarm_config6 = "set_user_config.cgi?&mod=1&newuser="
//					+ user6 + "&newpwd=" + pwd6 + "&loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, get_alarm_config6, 1);
//			break;
//		case R.id.btn_edit6:
//			tag = 6;
//			editText_GroupName.setVisibility(View.GONE);
//			title.setText(R.string.biz_add_poup_show2);
//			title_show.setText(R.string.biz_add_edit_show2);
//			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
//					0);
//			break;

//		case R.id.btn_del7:
//			String get_alarm_config7 = "set_user_config.cgi?&mod=1&newuser="
//					+ user7 + "&newpwd=" + pwd7 + "&loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, get_alarm_config7, 1);
//			break;
//		case R.id.btn_edit7:
//			tag = 7;
//			editText_GroupName.setVisibility(View.GONE);
//			title.setText(R.string.biz_add_poup_show2);
//			title_show.setText(R.string.biz_add_edit_show2);
//			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
//					0);
//			break;

//		case R.id.btn_del8:
//
//			String get_alarm_config8 = "set_user_config.cgi?&mod=1&newuser="
//					+ user8 + "&newpwd=" + pwd8 + "&loginuse="
//					+ SystemValue.doorBellAdmin + "&loginpas="
//					+ SystemValue.doorBellPass + "&user="
//					+ SystemValue.doorBellAdmin + "&pwd="
//					+ SystemValue.doorBellPass;
//			NativeCaller.TransferMessage(strDID, get_alarm_config8, 1);
//			break;
//		case R.id.btn_edit8:
//			tag = 8;
//			editText_GroupName.setVisibility(View.GONE);
//			title.setText(R.string.biz_add_poup_show2);
//			title_show.setText(R.string.biz_add_edit_show2);
//			popupWindow_group.showAtLocation(button_cancel, Gravity.CENTER, 0,
//					0);
//			break;

//		case R.id.popup_create_group_create:
//			String name = editText_GroupName.getText().toString().trim();
//			String pwd = editText_GroupName_pass.getText().toString().trim();
//			if (tag == 0) {
//				if (name == null || name.length() < 5) {
//					showToast(R.string.biz_add_show_user);
//					return;
//				}
//
//				if (name.equalsIgnoreCase(user1)
//						|| name.equalsIgnoreCase(user2)
//						|| name.equalsIgnoreCase(user3)
//						|| name.equalsIgnoreCase(user4)
//						|| name.equalsIgnoreCase(user5)
//						|| name.equalsIgnoreCase(user6)
//						|| name.equalsIgnoreCase(user7)
//						|| name.equalsIgnoreCase(user8)) {
//					showToast(R.string.door_white_list_check_show);
//					return;
//				}
//
//				if (pwd == null || pwd.length() < 6) {
//					showToast(R.string.biz_add_show_pwd);
//					return;
//				}
//
//				String get_alarm_config = "set_user_config.cgi?&mod=0&newuser="
//						+ name + "&newpwd=" + pwd + "&loginuse="
//						+ SystemValue.doorBellAdmin + "&loginpas="
//						+ SystemValue.doorBellPass + "&user="
//						+ SystemValue.doorBellAdmin + "&pwd="
//						+ SystemValue.doorBellPass;
//				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
//
//			} else if (tag == 1) {
//				setPwd = pwd1;
//				if (pwd == null || pwd.length() < 6) {
//					showToast(R.string.biz_add_show_pwd);
//					return;
//				}
//				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
//						+ user1 + "&newpwd=" + pwd + "&loginuse="
//						+ SystemValue.doorBellAdmin + "&loginpas="
//						+ SystemValue.doorBellPass + "&user="
//						+ SystemValue.doorBellAdmin + "&pwd="
//						+ SystemValue.doorBellPass;
//				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
//				NativeCaller.PPPPGetSystemParams(strDID,
//						ContentCommon.MSG_GET_RESET_USER);
//			} else if (tag == 2) {
//				setPwd = pwd2;
//				if (pwd == null || pwd.length() < 6) {
//					showToast(R.string.biz_add_show_pwd);
//					return;
//				}
//				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
//						+ user2 + "&newpwd=" + pwd + "&loginuse="
//						+ SystemValue.doorBellAdmin + "&loginpas="
//						+ SystemValue.doorBellPass + "&user="
//						+ SystemValue.doorBellAdmin + "&pwd="
//						+ SystemValue.doorBellPass;
//				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
//				NativeCaller.PPPPGetSystemParams(strDID,
//						ContentCommon.MSG_GET_RESET_USER);
//			} else if (tag == 3) {
//
//				setPwd = pwd3;
//				if (pwd == null || pwd.length() < 6) {
//					showToast(R.string.biz_add_show_pwd);
//					return;
//				}
//				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
//						+ user3 + "&newpwd=" + pwd + "&loginuse="
//						+ SystemValue.doorBellAdmin + "&loginpas="
//						+ SystemValue.doorBellPass + "&user="
//						+ SystemValue.doorBellAdmin + "&pwd="
//						+ SystemValue.doorBellPass;
//				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
//				NativeCaller.PPPPGetSystemParams(strDID,
//						ContentCommon.MSG_GET_RESET_USER);
//			} else if (tag == 4) {
//
//				setPwd = pwd4;
//				if (pwd == null || pwd.length() < 6) {
//					showToast(R.string.biz_add_show_pwd);
//					return;
//				}
//				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
//						+ user4 + "&newpwd=" + pwd + "&loginuse="
//						+ SystemValue.doorBellAdmin + "&loginpas="
//						+ SystemValue.doorBellPass + "&user="
//						+ SystemValue.doorBellAdmin + "&pwd="
//						+ SystemValue.doorBellPass;
//				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
//				NativeCaller.PPPPGetSystemParams(strDID,
//						ContentCommon.MSG_GET_RESET_USER);
//			} else if (tag == 5) {
//				setPwd = pwd5;
//				if (pwd == null || pwd.length() < 6) {
//					showToast(R.string.biz_add_show_pwd);
//					return;
//				}
//				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
//						+ user5 + "&newpwd=" + pwd + "&loginuse="
//						+ SystemValue.doorBellAdmin + "&loginpas="
//						+ SystemValue.doorBellPass + "&user="
//						+ SystemValue.doorBellAdmin + "&pwd="
//						+ SystemValue.doorBellPass;
//				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
//				NativeCaller.PPPPGetSystemParams(strDID,
//						ContentCommon.MSG_GET_RESET_USER);
//			} else if (tag == 6) {
//
//				setPwd = pwd6;
//				if (pwd == null || pwd.length() < 6) {
//					showToast(R.string.biz_add_show_pwd);
//					return;
//				}
//				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
//						+ user6 + "&newpwd=" + pwd + "&loginuse="
//						+ SystemValue.doorBellAdmin + "&loginpas="
//						+ SystemValue.doorBellPass + "&user="
//						+ SystemValue.doorBellAdmin + "&pwd="
//						+ SystemValue.doorBellPass;
//				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
//				NativeCaller.PPPPGetSystemParams(strDID,
//						ContentCommon.MSG_GET_RESET_USER);
//			} else if (tag == 7) {
//				setPwd = pwd7;
//				if (pwd == null || pwd.length() < 6) {
//					showToast(R.string.biz_add_show_pwd);
//					return;
//				}
//				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
//						+ user7 + "&newpwd=" + pwd + "&loginuse="
//						+ SystemValue.doorBellAdmin + "&loginpas="
//						+ SystemValue.doorBellPass + "&user="
//						+ SystemValue.doorBellAdmin + "&pwd="
//						+ SystemValue.doorBellPass;
//				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
//				NativeCaller.PPPPGetSystemParams(strDID,
//						ContentCommon.MSG_GET_RESET_USER);
//			} else if (tag == 8) {
//				setPwd = pwd8;
//				if (pwd == null || pwd.length() < 6) {
//					showToast(R.string.biz_add_show_pwd);
//					return;
//				}
//				String get_alarm_config = "set_user_config.cgi?&mod=4&newuser="
//						+ user8 + "&newpwd=" + pwd + "&loginuse="
//						+ SystemValue.doorBellAdmin + "&loginpas="
//						+ SystemValue.doorBellPass + "&user="
//						+ SystemValue.doorBellAdmin + "&pwd="
//						+ SystemValue.doorBellPass;
//				NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
//				NativeCaller.PPPPGetSystemParams(strDID,
//						ContentCommon.MSG_GET_RESET_USER);
//			}
//			editText_GroupName.setText("");
//			editText_GroupName_pass.setText("");
//			popupWindow_group.dismiss();
//			break;

//		case R.id.popup_create_group_cancel:
//			tag = 0;
//			editText_GroupName.setText("");
//			editText_GroupName_pass.setText("");
//			popupWindow_group.dismiss();
//			break;
//		default:
//			break;
//		}
	}

	private String setPwd = "";

	private View popv_group;
	private PopupWindow popupWindow_group;
	private Button btn_create, btn_cancel;
	private EditText editText_GroupName;
	private EditText editText_GroupName_pass;
	private int tag = 0;
	private TextView title, title_show;

	public void initExitPopupWindow_Group() {
		LayoutInflater li = LayoutInflater.from(this);
		popv_group = li.inflate(R.layout.popup_create_group, null);
		title = (TextView) popv_group.findViewById(R.id.title);
		title_show = (TextView) popv_group.findViewById(R.id.title_show);

		btn_create = (Button) popv_group
				.findViewById(R.id.popup_create_group_create);
		btn_cancel = (Button) popv_group
				.findViewById(R.id.popup_create_group_cancel);
		editText_GroupName = (EditText) popv_group
				.findViewById(R.id.popup_create_group_edittext);
		editText_GroupName_pass = (EditText) popv_group
				.findViewById(R.id.popup_create_group_edittext_pass);
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
	public void CallBackUserParms(String did, String user1, String pwd1,
			String user2, String pwd2, String user3, String pwd3, String user4,
			String pwd4, String user5, String pwd5, String user6, String pwd6,
			String user7, String pwd7, String user8, String pwd8, String alias,
			int admin, int s1, int s2, int s3, int s4, int s5, int s6, int s7,
			int s8) {
		// TODO Auto-generated method stub
		Log.d("test","pwd8:"+pwd8+"  s8:"+s8);
		this.user1 = user1;
		this.pwd1 = pwd1;
		this.user2 = user2;
		this.pwd2 = pwd2;
		this.user3 = user3;
		this.pwd3 = pwd3;
		this.user4 = user4;
		this.pwd4 = pwd4;
		this.user5 = user5;
		this.pwd5 = pwd5;
		this.user6 = user6;
		this.pwd6 = pwd6;
		this.user7 = user7;
		this.pwd7 = pwd7;
		this.user8 = user8;
		this.pwd8 = pwd8;
		this.admin = admin;
		this.s1 = s1;
		this.s2 = s2;
		this.s3 = s3;
		this.s4 = s4;
		this.s5 = s5;
		this.s6 = s6;
		this.s7 = s7;
		this.s8 = s8;
		handler.sendEmptyMessage(PARMS);
	}
}
