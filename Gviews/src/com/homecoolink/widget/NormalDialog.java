package com.homecoolink.widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.activity.AlarmActivity;
import com.homecoolink.activity.QRcodeActivity;
import com.homecoolink.adapter.SelectorDialogAdapter;
import com.homecoolink.data.AlarmRecord;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.data.DefenceAreaName;
import com.homecoolink.entity.DefenceObject;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.p2p.core.P2PValue;

public class NormalDialog {
	Context context;
	String[] list_data = new String[] {};
	String title_str, content_str, btn1_str, btn2_str, itemname_str;
	AlertDialog dialog;
	public View rootView;
	private OnButtonOkListener onButtonOkListener;
	private OnButtonCancelListener onButtonCancelListener;
	private OnCancelListener onCancelListener;
	private OnItemClickListener onItemClickListener;
	private int style = 999;

	public static final int DIALOG_STYLE_NORMAL = 1;
	public static final int DIALOG_STYLE_LOADING = 2;
	public static final int DIALOG_STYLE_UPDATE = 3;
	public static final int DIALOG_STYLE_DOWNLOAD = 4;
	public static final int DIALOG_STYLE_PROMPT = 5;

	public NormalDialog(Context context, String title, String content,
			String btn1, String btn2) {
		this.context = context;
		this.title_str = title;
		this.content_str = content;
		this.btn1_str = btn1;
		this.btn2_str = btn2;
	}

	public NormalDialog(Context context) {
		this.context = context;
		this.title_str = "";
		this.content_str = "";
		this.btn1_str = "";
		this.btn2_str = "";
	}

	public NormalDialog(Context context, String itemname, String content,
			String title, String btn1, String btn2) {
		this.context = context;
		this.title_str = title;
		this.itemname_str = itemname;
		this.content_str = "";
		this.btn1_str = btn1;
		this.btn2_str = btn2;
	}

	public void showDialog() {
		switch (style) {
		case DIALOG_STYLE_NORMAL:

			showNormalDialog();
			break;
		case DIALOG_STYLE_PROMPT:

			showPromptDialog();
			break;
		case DIALOG_STYLE_LOADING:

			showLoadingDialog();
			break;
		default:

			showNormalDialog();
			break;
		}
	}


	public void showConnectFail() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_connect_failed, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// if (null!=dialog) {
				// dialog.dismiss();
				// }
			}

		});
		Button try_again = (Button) view.findViewById(R.id.try_again);
		Button use_qrecode = (Button) view.findViewById(R.id.try_qrecode);
		try_again.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}
		});
		use_qrecode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if (null != dialog) {
					dialog.dismiss();
				}
				context.startActivity(new Intent(context, QRcodeActivity.class));
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_remind_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_reming_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);

	}
	
	public static final String CHANNEL_PWD_RE = "[0-9a-zA-Z\u4e00-\u9fa5_]+";

	public void showReNameDialog(View Itemtv) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_renamenormal, null);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		final EditText rename = (EditText) view.findViewById(R.id.rename_et);
		TextView button1 = (TextView) view.findViewById(R.id.button1_text);
		TextView button2 = (TextView) view.findViewById(R.id.button2_text);
		title.setText(title_str);

		if (itemname_str != null && itemname_str != "") {			
			rename.setText(itemname_str);
		}
		rename.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {					
					dialog.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

				} else {
					
					((InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(rename.getWindowToken(), 0);
				}
			}
		});
		button1.setText(btn1_str);
		button2.setText(btn2_str);
		button1.setTag(Itemtv.getTag());
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String newName = rename.getText().toString();
					if ("".equals(newName) || null == newName) {
						T.showShort(context, Utils.getResString(context,
								R.string.defencearea_et_hint_tip));
						return;
					}
					if (newName.trim().length() > 9) {
						T.showShort(context, Utils.getResString(context,
								R.string.defencearea_toomanywords_hint_tip));
						return;
					}
					DefenceObject defenceObject = (DefenceObject) v.getTag();
					TextView itemtv = defenceObject.tv;

					String renamestr =rename.getText().toString(); 					
					if (renamestr.matches(CHANNEL_PWD_RE)) {						
						itemtv.setText(renamestr);
						String[] strs = defenceObject.strings;
						DefenceAreaName dan = new DefenceAreaName();
						dan.groupI = strs[0];
						dan.groupJ = strs[1];
						dan.groupName = rename.getText().toString();
						dan.groupIJ = "0";
						// String isResut =
						// DataManager.checkDefenceAreaName(context, dan.groupIJ);
						// if ("0".equals(isResut)) {
						// DataManager.insertDefenceAreaName(context, dan);
						// }else if ("1".equals(isResut)) {					
						DataManager.upDefenceAreaName(context, dan);						
						// }
						
						// List<DefenceAreaName> list =
						// DataManager.findDefenceAreaNameAll(context);
						//
					
						rename.clearFocus();
						if (null != dialog) {
							dialog.dismiss();
						}
					}else {
						T.showLong(context, context.getResources().getString(R.string.regex_tip1));
					}

					

				} catch (Exception e) {
					// TODO: handle exception
					Log.e("343", Log.getStackTraceString(e));
				}
			}

			// onButtonOkListener.onClick();

		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rename.clearFocus();
				if (null == onButtonCancelListener) {
					if (null != dialog) {
						dialog.cancel();
					}
				} else {
					onButtonCancelListener.onClick();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showLoadingDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_loading, null);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		title.setText(title_str);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.Loading_dialog_width);
		view.setLayoutParams(layout);
		dialog.setOnCancelListener(onCancelListener);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showAboutDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_about,
				null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.about_dialog_width);
		view.setLayoutParams(layout);
		dialog.setOnCancelListener(onCancelListener);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showDeviceInfoDialog(String curversion, String uBootVersion,
			String kernelVersion, String rootfsVersion) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_device_info, null);
		TextView text_curversion = (TextView) view
				.findViewById(R.id.text_curversion);
		TextView text_uBootVersion = (TextView) view
				.findViewById(R.id.text_uBootVersion);
		TextView text_kernelVersion = (TextView) view
				.findViewById(R.id.text_kernelVersion);
		TextView text_rootfsVersion = (TextView) view
				.findViewById(R.id.text_rootfsVersion);
		text_curversion.setText(curversion);
		text_uBootVersion.setText(uBootVersion);
		text_kernelVersion.setText(kernelVersion);
		text_rootfsVersion.setText(rootfsVersion);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.device_info_dialog_width);
		view.setLayoutParams(layout);
		dialog.setOnCancelListener(onCancelListener);
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showLoadingDialog2() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_loading2, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.Loading_dialog2_width);
		view.setLayoutParams(layout);
		dialog.setOnCancelListener(onCancelListener);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showNormalDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_normal, null);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		TextView content = (TextView) view.findViewById(R.id.content_text);
		TextView button1 = (TextView) view.findViewById(R.id.button1_text);
		TextView button2 = (TextView) view.findViewById(R.id.button2_text);
		title.setText(title_str);
		content.setText(content_str);
		button1.setText(btn1_str);
		button2.setText(btn2_str);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != dialog) {
					dialog.dismiss();
				}

				onButtonOkListener.onClick();
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != dialog) {
					dialog.cancel();
				}
				if (null == onButtonCancelListener) {
					
				} else {
					onButtonCancelListener.onClick();
				}
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		WindowManager manager = dialog.getWindow().getWindowManager();
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		params.gravity = Gravity.CENTER;
		params.x = 0;
		dialog.getWindow().setAttributes(params);
		dialog.show();

		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);

	}

	public void showSelectorDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_selector, null);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		title.setText(title_str);

		ListView content = (ListView) view.findViewById(R.id.content_text);

		SelectorDialogAdapter adapter = new SelectorDialogAdapter(context,
				list_data);
		content.setAdapter(adapter);
		content.setOnItemClickListener(onItemClickListener);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);

		int itemHeight = (int) context.getResources().getDimension(
				R.dimen.selector_dialog_item_height);
		int margin = (int) context.getResources().getDimension(
				R.dimen.selector_dialog_margin);
		int separatorHeight = (int) context.getResources().getDimension(
				R.dimen.selector_dialog_separator_height);

		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.selector_dialog_width);
		layout.height = itemHeight * list_data.length + margin * 2
				+ (list_data.length - 1) * separatorHeight;
		view.setLayoutParams(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showPromptDialog() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_prompt, null);
		TextView content = (TextView) view.findViewById(R.id.content_text);
		TextView title = (TextView) view.findViewById(R.id.title_text);
		TextView button2 = (TextView) view.findViewById(R.id.button2_text);
		content.setText(content_str);
		title.setText(title_str);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == onButtonCancelListener) {
					if (null != dialog) {
						dialog.dismiss();
					}
				} else {
					onButtonCancelListener.onClick();
				}
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		params.gravity = Gravity.CENTER;
		params.x = 0;
		dialog.getWindow().setAttributes(params);
		dialog.show();

		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.normal_dialog_width);
		view.setLayoutParams(layout);
	}

	public void showRemindDiaglog(boolean isShow) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_insert_infrared, null);
		// view.setOnClickListener(new OnClickListener(){
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// if (null!=dialog) {
		// dialog.dismiss();
		// }
		// }
		//
		// });
		Button bt1 = (Button) view.findViewById(R.id.bt_back);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent();
				it.setAction(Constants.Action.INSERT_INFRARED_BACK);
				MyApp.app.sendBroadcast(it);

			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		if (isShow == true) {
			dialog.show();
		}
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_remind_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_reming_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showPromoptDiaglog123() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_promopt_box2, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		Button bt1 = (Button) view.findViewById(R.id.bt_determine);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != dialog) {
					dialog.dismiss();
				}
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_promopt_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_promopt_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showListenDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_listen, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		Button bt1 = (Button) view.findViewById(R.id.bt_hear);
		Button bt2 = (Button) view.findViewById(R.id.bt_no_hear);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent it = new Intent();
				it.setAction(Constants.Action.HEARED);
				context.sendBroadcast(it);
			}
		});
		bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		ImageView anim_load = (ImageView) view.findViewById(R.id.anim_load);
		AnimationDrawable animationdrawable = (AnimationDrawable) anim_load
				.getDrawable();
		animationdrawable.start();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_remind_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_reming_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showWaitConnectionDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_wait_connection, null);
		// view.setOnClickListener(new OnClickListener(){
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// if (null!=dialog) {
		// dialog.dismiss();
		// }
		// }
		//
		// });
		ImageView anim_load = (ImageView) view.findViewById(R.id.anim_wait);
		AnimationDrawable animationdrawable = (AnimationDrawable) anim_load
				.getDrawable();
		animationdrawable.start();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_remind_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_reming_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void showQRcodehelp() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_help,
				null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_remind_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_reming_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void successDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_success, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_success_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_success_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}

	public void faildDialog() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_prompt_box1, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != dialog) {
					dialog.dismiss();
				}
			}

		});
		Button bt1 = (Button) view.findViewById(R.id.bt_determine);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (dialog != null) {
					dialog.dismiss();
				}

			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		FrameLayout.LayoutParams layout = (LayoutParams) view.getLayoutParams();
		layout.width = (int) context.getResources().getDimension(
				R.dimen.dialog_promopt_width);
		layout.height = (int) context.getResources().getDimension(
				R.dimen.dialog_promopt_height);
		view.setLayoutParams(layout);
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.dialog_normal);
	}
	
	public void showAlarmTwoType(AlarmActivity aa) {
		View view = LayoutInflater.from(context).inflate(R.layout.testlayout,
				null);
		aa.loadAlarmBellMusicAndVibrate();
		AlarmRecord ar = aa.insertAlarmRecord();
		ImageView btn_close = (ImageView) view
				.findViewById(R.id.alarm_close_btn);
		TextView alarm_time = (TextView) view.findViewById(R.id.alarm_time);
		TextView alarm_type_text = (TextView) view
				.findViewById(R.id.alarm_type_text);
		TextView alarm_id_text = (TextView) view
				.findViewById(R.id.alarm_id_text);
		btn_close.setTag(aa);
		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlarmActivity aa = (AlarmActivity) v.getTag();
				if (aa == null) {
					Log.e("343", "aa是空的");
				}
				try {
					aa.exit();
				} catch (Exception e) {
					// TODO: handle exception
					Log.e("343", Log.getStackTraceString(e));
				}
			}
		});
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(Long.valueOf(ar.alarmTime));
		String time = sdf.format(date);
		alarm_time.setText(time);
		switch (ar.alarmType) {
		case P2PValue.AlarmType.DEFENCE:
			alarm_type_text.setText(R.string.defence2);
			break;
		case P2PValue.AlarmType.NO_DEFENCE:
			alarm_type_text.setText(R.string.no_defence);
			break;
		}
		Contact ct = DataManager.findContactByActiveUserAndContactId(context,
				ar.activeUser, ar.deviceId);
		alarm_id_text.setText(ct.contactName + "(" + ar.deviceId + ")");
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = builder.create();
		WindowManager manager = dialog.getWindow().getWindowManager();
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = dm.widthPixels - 20;
		params.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(params);
		dialog.show();
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);

	}

	public void setTitle(String title) {
		this.title_str = title;
	}

	public void setTitle(int id) {
		this.title_str = context.getResources().getString(id);
	}

	public void setListData(String[] data) {
		this.list_data = data;
	}

	public void setCanceledOnTouchOutside(boolean bool) {
		dialog.setCanceledOnTouchOutside(bool);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setCancelable(boolean bool) {
		dialog.setCancelable(bool);
	}

	public void cancel() {
		dialog.cancel();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public boolean isShowing() {
		return dialog.isShowing();
	}

	public void setBtnListener(TextView btn1, TextView btn2) {

	}

	public void setStyle(int style) {
		this.style = style;
	}

	public interface OnButtonOkListener {
		public void onClick();
	}

	public interface OnButtonCancelListener {
		public void onClick();
	}

	public void setOnButtonOkListener(OnButtonOkListener onButtonOkListener) {
		this.onButtonOkListener = onButtonOkListener;
	}

	public void setOnButtonCancelListener(
			OnButtonCancelListener onButtonCancelListener) {
		this.onButtonCancelListener = onButtonCancelListener;
	}

	public void setOnCancelListener(OnCancelListener onCancelListener) {
		this.onCancelListener = onCancelListener;
	}
}
