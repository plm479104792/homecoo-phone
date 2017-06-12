package com.homecoolink.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.homecoolink.R;
import com.homecoolink.activity.MainControlActivity;
import com.homecoolink.adapter.WifiAdapter;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.thread.DelayThread;
import com.homecoolink.utils.T;
import com.homecoolink.widget.MyInputDialog;
import com.homecoolink.widget.MyListView;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;


public class NetControlFrag extends BaseFragment implements OnClickListener {
	private Context mContext;
	private Contact contact;
	private boolean isRegFilter = false;
	RelativeLayout dialog_input_mask;
	RelativeLayout net_type_bar, list_wifi_bar;
	LinearLayout net_type_radio, list_wifi_content;
	ProgressBar progressBar_net_type, progressBar_list_wifi;
	RadioButton radio_one, radio_two;

	WifiAdapter mAdapter;
	MyListView list;
	int last_net_type;
	int last_modify_net_type;
	int last_modify_wifi_type;
	String last_modify_wifi_name;
	String last_modify_wifi_password;
	NormalDialog dialog_loading;
	MyInputDialog dialog_input;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = MainControlActivity.mContext;
		contact = (Contact) getArguments().getSerializable("contact");

		View view = inflater.inflate(R.layout.fragment_net_control, container,
				false);
		initComponent(view);
		regFilter();

		showProgress_net_type();

		P2PHandler.getInstance().getNpcSettings(contact.contactId,
				contact.contactPassword);
		return view;

	}

	public void initComponent(View view) {
		dialog_input_mask = (RelativeLayout) view
				.findViewById(R.id.dialog_input_mask);

		net_type_bar = (RelativeLayout) view.findViewById(R.id.net_type_bar);
		list_wifi_bar = (RelativeLayout) view.findViewById(R.id.list_wifi_bar);

		net_type_radio = (LinearLayout) view.findViewById(R.id.net_type_radio);
		list_wifi_content = (LinearLayout) view
				.findViewById(R.id.list_wifi_content);

		progressBar_net_type = (ProgressBar) view
				.findViewById(R.id.progressBar_net_type);
		progressBar_list_wifi = (ProgressBar) view
				.findViewById(R.id.progressBar_list_wifi);

		list = (MyListView) view.findViewById(R.id.list_wifi);
		mAdapter = new WifiAdapter(mContext, this);
		list.setAdapter(mAdapter);
		radio_one = (RadioButton) view.findViewById(R.id.radio_one);
		radio_two = (RadioButton) view.findViewById(R.id.radio_two);
		radio_one.setOnClickListener(this);
		radio_two.setOnClickListener(this);
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();

		filter.addAction(Constants.Action.CLOSE_INPUT_DIALOG);

		filter.addAction(Constants.P2P.ACK_RET_GET_NPC_SETTINGS);

		filter.addAction(Constants.P2P.ACK_RET_SET_NET_TYPE);
		filter.addAction(Constants.P2P.RET_SET_NET_TYPE);
		filter.addAction(Constants.P2P.RET_GET_NET_TYPE);

		filter.addAction(Constants.P2P.ACK_RET_SET_WIFI);
		filter.addAction(Constants.P2P.ACK_RET_GET_WIFI);
		filter.addAction(Constants.P2P.RET_SET_WIFI);
		filter.addAction(Constants.P2P.RET_GET_WIFI);

		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.Action.CLOSE_INPUT_DIALOG)) {
				if (null != dialog_input) {
					dialog_input.hide(dialog_input_mask);
				}
			} else if (intent.getAction()
					.equals(Constants.P2P.RET_GET_NET_TYPE)) {
				int type = intent.getIntExtra("type", -1);
				if (type == Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIRED) {
					last_net_type = Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIRED;
					radio_one.setChecked(true);
					RadioGroup rg = (RadioGroup) radio_one.getParent();
					rg.setBackgroundResource(R.drawable.wired_open);

					if (contact.contactType == P2PValue.DeviceType.DOORBELL
							|| contact.contactType == P2PValue.DeviceType.IPC
							|| contact.contactType == P2PValue.DeviceType.NPC) {
						showProgressWiFiList();
						P2PHandler.getInstance().getWifiList(contact.contactId,
								contact.contactPassword);
					} else {
						hideWiFiList();
					}

				} else if (type == Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIFI) {
					last_net_type = Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIFI;
					radio_two.setChecked(true);
					RadioGroup rg = (RadioGroup) radio_two.getParent();
					rg.setBackgroundResource(R.drawable.wifi_open);
					showProgressWiFiList();
					P2PHandler.getInstance().getWifiList(contact.contactId,
							contact.contactPassword);
				}
				showNetType();
				setRadioEnable(true);
			} else if (intent.getAction()
					.equals(Constants.P2P.RET_SET_NET_TYPE)) {
				// int result = intent.getIntExtra("result", -1);
				// if(result==Constants.P2P_SET.NET_TYPE_SET.SETTING_SUCCESS){
				// last_net_type = last_modify_net_type;
				// if(last_modify_net_type==Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIFI){
				// showProgressWiFiList();
				// P2PHandler.getInstance().getWifiList(contact.contactId,
				// contact.contactPassword);
				// radio_two.setChecked(true);
				// }else{
				// hideWiFiList();
				// radio_one.setChecked(true);
				// }
				// T.showShort(mContext, R.string.modify_success);
				// }else{
				// if(last_net_type==Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIFI){
				// showProgressWiFiList();
				// radio_two.setChecked(true);
				// }else{
				// hideWiFiList();
				// radio_one.setChecked(true);
				// }
				// T.showShort(mContext, R.string.operator_error);
				// }
				// showNetType();
				// setRadioEnable(true);
			} else if (intent.getAction().equals(Constants.P2P.RET_GET_WIFI)) {

				int iCurrentId = intent.getIntExtra("iCurrentId", 0);
				int iCount = intent.getIntExtra("iCount", 0);
				if (iCurrentId <= iCount - 1) {
					if (radio_two.isChecked()==true) {
						int[] iType = intent.getIntArrayExtra("iType");
						int[] iStrength = intent.getIntArrayExtra("iStrength");
						String[] names = intent.getStringArrayExtra("names");

						String[] new_names = new String[iCount];
						int[] new_iType = new int[iCount];
						int[] new_iStrength = new int[iCount];
						int new_iCurrentId = 0;

						Log.e("343", "iCurrentId==" + iCurrentId);
						Log.e("343", "iCount==" + iCount);
						Log.e("343", "names==" + names[iCurrentId]);

						for (int i = 0; i < names.length; i++) {
							
							if (i == 0) {
								new_names[i] = names[iCurrentId];
							} else if (iCurrentId == i) {
								new_names[i] = names[0];
							} else {
								new_names[i] = names[i];
							}
						}

						for (int i = 0; i < iStrength.length; i++) {
							
							if (i == 0) {
								new_iStrength[i] = iStrength[iCurrentId];
							} else if (iCurrentId == i) {
								new_iStrength[i] = iStrength[0];
							} else {
								new_iStrength[i] = iStrength[i];

							}
						}

						for (int i = 0; i < iType.length; i++) {
							
							if (i == 0) {
								new_iType[i] = iType[iCurrentId];
							} else if (iCurrentId == i) {
								new_iType[i] = iType[0];
							} else {
								new_iType[i] = iType[i];
							}
						}
					
						for (int j = 0; j < new_names.length; j++) {
							Log.i("343", "new_names原来第" + j + "个：" + new_names[j]);
						}

						for (int j = 0; j < new_iStrength.length; j++) {
							Log.i("343", "new_iStrength原来第" + j + "个："
									+ new_iStrength[j]);
						}
						for (int j = 0; j < new_iType.length; j++) {
							Log.i("343", "new_iType原来第" + j + "个：" + new_iType[j]);
						}

						mAdapter.updateData(new_iCurrentId, iCount, new_iType,
								new_iStrength, new_names);

						showWiFiList();

						list.setSelection(iCurrentId);
					}else {
						list_wifi_bar.setVisibility(View.GONE);
					}
					

				} else {
					

					int[] iType = intent.getIntArrayExtra("iType");
					int[] iStrength = intent.getIntArrayExtra("iStrength");
					String[] names = intent.getStringArrayExtra("names");
					mAdapter.updateData(iCurrentId, iCount, iType,
							iStrength, names);
					showWiFiList();

				}
			} else if (intent.getAction().equals(Constants.P2P.RET_SET_WIFI)) {
				int result = intent.getIntExtra("result", -1);
				// if(result==Constants.P2P_SET.WIFI_SET.SETTING_SUCCESS){
				// hideWiFiList();
				// showProgress_net_type();
				// P2PHandler.getInstance().getWifiList(contact.contactId,
				// contact.contactPassword);
				// T.showShort(mContext, R.string.modify_success);
				// }else if(result==20){
				// T.showShort(mContext, R.string.wifi_pwd_format_error);
				// }else{
				// hideWiFiList();
				// showProgress_net_type();
				// P2PHandler.getInstance().getNpcSettings(contact.contactId,
				// contact.contactPassword);
				// T.showShort(mContext, R.string.operator_error);
				// }
				if (result == Constants.P2P_SET.WIFI_SET.SETTING_SUCCESS) {

				} else if (result == 20) {
					T.showShort(mContext, R.string.wifi_pwd_format_error);
				} else {
					T.showShort(mContext, R.string.operator_error);
				}

			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_GET_NPC_SETTINGS)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:get npc settings");
					P2PHandler.getInstance().getNpcSettings(contact.contactId,
							contact.contactPassword);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_NET_TYPE)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:set npc settings net type");
					if (null != dialog_loading && dialog_loading.isShowing()) {
						P2PHandler.getInstance().setNetType(contact.contactId,
								contact.contactPassword, last_modify_net_type);
					}
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
					if (null != dialog_loading) {
						dialog_loading.dismiss();
					}
					hideWiFiList();
					showProgress_net_type();
					P2PHandler.getInstance().getNpcSettings(contact.contactId,
							contact.contactPassword);
					setRadioEnable(true);
				}
			} else if (intent.getAction()
					.equals(Constants.P2P.ACK_RET_GET_WIFI)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:get wifi list");
					P2PHandler.getInstance().getWifiList(contact.contactId,
							contact.contactPassword);
				}
			} else if (intent.getAction()
					.equals(Constants.P2P.ACK_RET_SET_WIFI)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:set wifi");
					if (null != dialog_loading && dialog_loading.isShowing()) {
						P2PHandler.getInstance().setWifi(contact.contactId,
								contact.contactPassword, last_modify_wifi_type,
								last_modify_wifi_name,
								last_modify_wifi_password);
					}

				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
					if (null != dialog_loading) {
						dialog_loading.dismiss();
					}
					hideWiFiList();
					showProgress_net_type();
					P2PHandler.getInstance().getNpcSettings(contact.contactId,
							contact.contactPassword);
				}
			}

		}
	};

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.radio_one){
			changeNetType(Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIRED);
		}else if(id==R.id.radio_two){
			changeNetType(Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIFI);
		}
		
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.radio_one:
//			changeNetType(Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIRED);
//			
//			break;
//		case R.id.radio_two:
//			changeNetType(Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIFI);
//			
//			break;
//		}
	}

	public void changeNetType(final int type) {
		final NormalDialog dialog = new NormalDialog(mContext, mContext
				.getResources().getString(R.string.warning), mContext
				.getResources().getString(R.string.modify_net_warning),
				mContext.getResources().getString(R.string.change), mContext
						.getResources().getString(R.string.cancel));
		switch (last_net_type) {
		case Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIFI:
			dialog.setOnButtonCancelListener(new NormalDialog.OnButtonCancelListener() {

				@Override
				public void onClick() {
					// TODO Auto-generated method stub
					radio_two.setChecked(true);
					RadioGroup rg2 = (RadioGroup) radio_two.getParent();
					rg2.setBackgroundResource(R.drawable.wifi_open);
					dialog.dismiss();
				}
			});
			break;
		case Constants.P2P_SET.NET_TYPE_SET.NET_TYPE_WIRED:
			dialog.setOnButtonCancelListener(new NormalDialog.OnButtonCancelListener() {

				@Override
				public void onClick() {
					// TODO Auto-generated method stub
					radio_one.setChecked(true);
					RadioGroup rg1 = (RadioGroup) radio_one.getParent();
					rg1.setBackgroundResource(R.drawable.wired_open);
					dialog.dismiss();
				}
			});
			break;
		}
		dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				// progressBar_net_type.setVisibility(RelativeLayout.VISIBLE);
				if (null == dialog_loading) {
					dialog_loading = new NormalDialog(mContext, mContext
							.getResources().getString(R.string.verification),
							"", "", "");
					dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				}
				dialog_loading.showDialog();
				new DelayThread(
						Constants.SettingConfig.SETTING_CLICK_TIME_DELAY,
						new DelayThread.OnRunListener() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								last_modify_net_type = type;
								P2PHandler.getInstance().setNetType(
										contact.contactId,
										contact.contactPassword, type);
							}
						}).start();
				setRadioEnable(false);

			}
		});

		dialog.showNormalDialog();
		dialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}

	public void setRadioEnable(boolean bool) {
		if (bool) {
			radio_one.setEnabled(true);
			radio_two.setEnabled(true);
		} else {
			radio_one.setEnabled(false);
			radio_two.setEnabled(false);
		}
	}

	public void showProgress_net_type() {
		// net_type_bar.setBackgroundResource(R.drawable.tiao_bg_single);
		progressBar_net_type.setVisibility(View.VISIBLE);
		net_type_radio.setVisibility(View.GONE);
	}

	public void showNetType() {
		// net_type_bar.setBackgroundResource(R.drawable.tiao_bg_up);
		progressBar_net_type.setVisibility(View.GONE);
		net_type_radio.setVisibility(View.VISIBLE);
	}

	public void hideWiFiList() {
		list_wifi_bar.setVisibility(View.GONE);
		list_wifi_content.setVisibility(View.GONE);
	}

	public void showProgressWiFiList() {
		// list_wifi_bar.setBackgroundResource(R.drawable.tiao_bg_single);
		list_wifi_bar.setVisibility(View.VISIBLE);
		progressBar_list_wifi.setVisibility(View.VISIBLE);
		list_wifi_content.setVisibility(View.GONE);
	}

	public void showWiFiList() {
		// list_wifi_bar.setBackgroundResource(R.drawable.tiao_bg_up);
		list_wifi_bar.setVisibility(View.VISIBLE);
		progressBar_list_wifi.setVisibility(View.GONE);
		list_wifi_content.setVisibility(View.VISIBLE);
	}

	public void showModfyWifi(final int type, final String name) {
		dialog_input = new MyInputDialog(mContext);
		dialog_input.setTitle(mContext.getResources().getString(
				R.string.change_wifi)
				+ "(" + name + ")");
		dialog_input.setBtn1_str(mContext.getResources().getString(
				R.string.ensure));
		dialog_input.setBtn2_str(mContext.getResources().getString(
				R.string.cancel));
		dialog_input
				.setOnButtonOkListener(new MyInputDialog.OnButtonOkListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						String password = dialog_input.getInput1Text();
						if ("".equals(password.trim())) {
							T.showShort(mContext, R.string.input_wifi_pwd);
							return;
						}
						dialog_input.hide(dialog_input_mask);
						if (null == dialog_loading) {
							dialog_loading = new NormalDialog(mContext,
									mContext.getResources().getString(
											R.string.verification), "", "", "");
							dialog_loading
									.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
						}
						dialog_loading.showDialog();
						last_modify_wifi_type = type;
						last_modify_wifi_name = name;
						last_modify_wifi_password = password;
						P2PHandler.getInstance().setWifi(contact.contactId,
								contact.contactPassword, type, name, password);
					}
				});
		dialog_input.show(dialog_input_mask);
		dialog_input.setInput1HintText(R.string.input_wifi_pwd);
	}

	public boolean IsInputDialogShowing() {
		if (null != dialog_input) {
			return dialog_input.isShowing();
		} else {
			return false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent it = new Intent();
		it.setAction(Constants.Action.CONTROL_BACK);
		mContext.sendBroadcast(it);
	}
}
