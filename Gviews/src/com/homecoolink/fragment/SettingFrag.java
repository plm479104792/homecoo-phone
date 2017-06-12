package com.homecoolink.fragment;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.utils.AlwaysMarqueeTextView;
import com.homecoolink.R;
import com.homecoolink.activity.AccountInfoActivity;
import com.homecoolink.activity.MainActivity;
import com.homecoolink.activity.ModifyLoginPasswordActivity;
import com.homecoolink.activity.SettingSystemActivity;
import com.homecoolink.data.DataManager;
import com.homecoolink.data.SysMessage;
import com.homecoolink.entity.Account;
import com.homecoolink.global.AccountPersist;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.thread.UpdateCheckVersionThread;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;


public class SettingFrag extends BaseFragment implements OnClickListener {

	private Context mContext;
	private RelativeLayout mCheckUpdateTextView = null;
	private RelativeLayout mLogOut, mExit, center_about, sys_set, sys_msg,
			modify_pwd;
	private TextView mName, mPassport;
	private AlwaysMarqueeTextView mPassPortStr;
	private ImageView account_set;
	boolean isRegFilter = false;
	// update add
	private Handler handler;
	private boolean isCancelCheck = false;
	private NormalDialog dialog;
	ImageView sysMsg_notify_img;// ,network_type

	// end

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_setting, container,
				false);
		Log.e("my", "createSettingFrag");
		mContext = MainActivity.mContext;
//		mContext=MyApp.app;

		initComponent(view);
		regFilter();
		updateSysMsg();
		return view;
	}

	public void initComponent(View view) {
		mCheckUpdateTextView = (RelativeLayout) view
				.findViewById(R.id.center_t);
		mName = (TextView) view.findViewById(R.id.mailAdr);
		mName.setText(String.valueOf(NpcCommon.mThreeNum));
		mPassport = (TextView) view.findViewById(R.id.PassPort);
		mPassPortStr = (AlwaysMarqueeTextView) view.findViewById(R.id.PassPortStr);
		mLogOut = (RelativeLayout) view.findViewById(R.id.logout_layout);
		account_set = (ImageView) view.findViewById(R.id.EditMe);
		sys_set = (RelativeLayout) view.findViewById(R.id.system_set);
		mExit = (RelativeLayout) view.findViewById(R.id.exit_layout);
		center_about = (RelativeLayout) view.findViewById(R.id.center_about);
		sys_msg = (RelativeLayout) view.findViewById(R.id.system_message);

		sysMsg_notify_img = (ImageView) view
				.findViewById(R.id.sysMsg_notify_img);

		// network_type = (ImageView) view.findViewById(R.id.network_type);

		// if(NpcCommon.mNetWorkType == NpcCommon.NETWORK_TYPE.NETWORK_WIFI){
		// network_type.setImageResource(R.drawable.wifi);
		// }else{
		// network_type.setImageResource(R.drawable.net_3g);
		// }
		modify_pwd = (RelativeLayout) view.findViewById(R.id.modify_login_pwd);

		modify_pwd.setOnClickListener(this);
		mLogOut.setOnClickListener(this);
		account_set.setOnClickListener(this);
		sys_msg.setOnClickListener(this);
		mExit.setOnClickListener(this);
		sys_set.setOnClickListener(this);
		center_about.setOnClickListener(this);
		mCheckUpdateTextView.setOnClickListener(this);
		handler = new MyHandler();
		loadAccountInfo();
	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.RECEIVE_SYS_MSG);
		filter.addAction(Constants.Action.NET_WORK_TYPE_CHANGE);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Action.RECEIVE_SYS_MSG)) {
				updateSysMsg();
			} else if (intent.getAction().equals(
					Constants.Action.NET_WORK_TYPE_CHANGE)) {
				// if(NpcCommon.mNetWorkType ==
				// NpcCommon.NETWORK_TYPE.NETWORK_WIFI){
				// network_type.setImageResource(R.drawable.wifi);
				// }else{
				// network_type.setImageResource(R.drawable.net_3g);
				// }
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	void loadAccountInfo() {
		Account account = AccountPersist.getInstance().getActiveAccountInfo(
				mContext);
		String email = "";
		String phone = "";
		String countryCode = "86";
		if (null != account) {
			email = account.email;
			phone = account.phone;
			countryCode = account.countryCode;
		}

		if (!(phone.equals("0") || phone.equals(""))) {
			mPassport.setText(Utils.getResString(mContext,
					R.string.account_number));
			mPassPortStr.setText(phone);
		} else {
			mPassport.setText(Utils.getResString(mContext,
					R.string.account_number));
			mPassPortStr.setText(email);
		}

	}

	
	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.logout_layout){
			Intent canel = new Intent();
			canel.setAction(Constants.Action.ACTION_SWITCH_USER);
			mContext.sendBroadcast(canel);

		}else if(id==R.id.exit_layout){
			Intent exit = new Intent();
			exit.setAction(Constants.Action.ACTION_EXIT);
			mContext.sendBroadcast(exit);
		}else if(id==R.id.center_t){
			if (null != dialog && dialog.isShowing()) {
				Log.e("my", "isShowing");
				return;
			}
			dialog = new NormalDialog(mContext);
			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {

					isCancelCheck = true;
				}

			});
			dialog.setTitle(mContext.getResources().getString(
					R.string.check_update));
			dialog.showLoadingDialog();
			dialog.setCanceledOnTouchOutside(false);
			isCancelCheck = false;
			new UpdateCheckVersionThread(handler).start();
		}else if(id==R.id.center_about){
			NormalDialog about = new NormalDialog(mContext);
			about.showAboutDialog();
		}else if(id==R.id.EditMe){
			Intent goAccount_set = new Intent();
			goAccount_set.setClass(mContext, AccountInfoActivity.class);
			startActivity(goAccount_set);
		}else if(id==R.id.system_set){
			Intent goSys_set = new Intent();
			goSys_set.setClass(mContext, SettingSystemActivity.class);
			startActivity(goSys_set);
		}else if(id==R.id.modify_login_pwd){
			Intent modify_password = new Intent(mContext,
					ModifyLoginPasswordActivity.class);
			startActivity(modify_password);
		}
		
//		switch (v.getId()) {
//
//		case R.id.logout_layout:
//			Intent canel = new Intent();
//			canel.setAction(Constants.Action.ACTION_SWITCH_USER);
//			mContext.sendBroadcast(canel);
//
//			break;
//	
//		case R.id.exit_layout:
//			Intent exit = new Intent();
//			exit.setAction(Constants.Action.ACTION_EXIT);
//			mContext.sendBroadcast(exit);
//			break;
//
//		case R.id.center_t:
//			if (null != dialog && dialog.isShowing()) {
//				Log.e("my", "isShowing");
//				return;
//			}
//			dialog = new NormalDialog(mContext);
//			dialog.setOnCancelListener(new OnCancelListener() {
//
//				@Override
//				public void onCancel(DialogInterface arg0) {
//
//					isCancelCheck = true;
//				}
//
//			});
//			dialog.setTitle(mContext.getResources().getString(
//					R.string.check_update));
//			dialog.showLoadingDialog();
//			dialog.setCanceledOnTouchOutside(false);
//			isCancelCheck = false;
//			new UpdateCheckVersionThread(handler).start();
//			break;
//
//		case R.id.center_about:
//			// Intent i = new Intent(mContext,AboutActivity.class);
//			// mContext.startActivity(i);
//
//			NormalDialog about = new NormalDialog(mContext);
//			about.showAboutDialog();
//			break;
//	
//		case R.id.EditMe:
//			Intent goAccount_set = new Intent();
//			goAccount_set.setClass(mContext, AccountInfoActivity.class);
//			startActivity(goAccount_set);
//			break;
//		
//		case R.id.system_set:
//			Intent goSys_set = new Intent();
//			goSys_set.setClass(mContext, SettingSystemActivity.class);
//			startActivity(goSys_set);
//			break;
//		
//		case R.id.system_message:
////			Intent goSysMsg = new Intent(mContext, SysMsgActivity.class);
////			startActivity(goSysMsg);
//			break;
//		
//		case R.id.modify_login_pwd:
//			Intent modify_password = new Intent(mContext,
//					ModifyLoginPasswordActivity.class);
//			startActivity(modify_password);
//			break;
//		default:
//			break;
//
//		}
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case Constants.Update.CHECK_UPDATE_HANDLE_FALSE:
				Log.e("my", "diss");
				if (null != dialog) {
					Log.e("my", "diss ok");
					dialog.dismiss();
					dialog = null;
				}
				if (isCancelCheck) {
					return;
				}
				dialog = new NormalDialog(mContext, mContext.getResources()
						.getString(R.string.update_prompt_title), mContext
						.getResources().getString(R.string.update_check_false),
						"", "");
				dialog.setStyle(NormalDialog.DIALOG_STYLE_PROMPT);
				dialog.showDialog();
				break;
			case Constants.Update.CHECK_UPDATE_HANDLE_TRUE:
				if (null != dialog) {
					dialog.dismiss();
					dialog = null;
				}
				if (isCancelCheck) {
					return;
				}
				Intent i = new Intent(Constants.Action.ACTION_UPDATE);
				i.putExtra("updateDescription", (String) msg.obj);
				mContext.sendBroadcast(i);
				break;
			}
		}
	}

	public void updateSysMsg() {
		List<SysMessage> sysmessages = DataManager.findSysMessageByActiveUser(
				mContext, NpcCommon.mThreeNum);
		boolean isNewSysMsg = false;
		for (SysMessage msg : sysmessages) {
			if (msg.msgState == SysMessage.MESSAGE_STATE_NO_READ) {
				isNewSysMsg = true;
			}
		}

		if (isNewSysMsg) {
			sysMsg_notify_img.setVisibility(View.VISIBLE);
		} else {
			sysMsg_notify_img.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isRegFilter) {
			isRegFilter = false;
			mContext.unregisterReceiver(mReceiver);
		}
	}
}
