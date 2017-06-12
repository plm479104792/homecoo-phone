package com.homecoolink.fragment;

import java.util.ArrayList;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.utils.PwdErrorUtil;
import com.homecoolink.R;
import com.homecoolink.SettingListener;
import com.homecoolink.activity.AddContactActivity;
import com.homecoolink.activity.LocalDeviceListActivity;
import com.homecoolink.activity.MainActivity;
import com.homecoolink.activity.MainControlActivity;
import com.homecoolink.activity.RadarAddFirstActivity;
import com.homecoolink.adapter.MainAdapter;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.entity.LocalDevice;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.thread.MainThread;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;
import com.lib.pullToRefresh.PullToRefreshBase;
import com.lib.pullToRefresh.PullToRefreshBase.OnRefreshListener;
import com.lib.pullToRefresh.PullToRefreshListViewK14;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;


public class ContactFrag extends BaseFragment implements OnClickListener {

	public static final int CHANGE_REFRESHING_LABLE = 0x12;
	private Context mContext;
	private boolean isRegFilter = false;

	private ListView mListView;
	private ImageView mAddUser,mBackbtn;
	private MainAdapter mAdapter;
	private PullToRefreshListViewK14 mPullRefreshListView;
	boolean refreshEnd = false;
	boolean isFirstRefresh = true;
	boolean isActive;
	boolean isCancelLoading;
	
	private LinearLayout net_work_status_bar;
	private RelativeLayout local_device_bar_top;
	private TextView text_local_device_count;
	NormalDialog dialog;
	Handler handler;
	private Contact next_contact;
	boolean isOpenThread = false;
	private LayoutInflater inf = null;
	public String[] last_bind_data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		inf = inflater;
		
		View view = inflater.inflate(R.layout.fragment_contact, container,
				false);
		mContext = MainActivity.mContext;
//		mContext=MyApp.app;

		
		Log.e("my", "createContactFrag");
		initComponent(view);
		regFilter();

		if (isFirstRefresh) {
			isFirstRefresh = !isFirstRefresh;
			FList flist = FList.getInstance();
			flist.updateOnlineState();
			flist.searchLocalDevice();
		}
		return view;
	}

	public void CheckNewMess(View v, String dvid) {
		ImageView icon_new_img = (ImageView) v.findViewById(R.id.mess_new);
		ArrayList<com.homecoolink.data.AlarmRecord> list = (ArrayList<com.homecoolink.data.AlarmRecord>) DataManager
				.findUnReadAlarmRecordByDeviceID(mContext, NpcCommon.mThreeNum,
						dvid);
		if (list.size() > 0) {
			icon_new_img.setVisibility(View.VISIBLE);

		} else {
			icon_new_img.setVisibility(View.GONE);
		}
	}

	public Boolean CheckMessExitst(String dvid) {

		ArrayList<com.homecoolink.data.AlarmRecord> list = (ArrayList<com.homecoolink.data.AlarmRecord>) DataManager
				.findAlarmRecordByDeviceID(mContext, NpcCommon.mThreeNum, dvid);
		return list.size() > 0;
	}

	public void initComponent(View view) {
		mAddUser = (ImageView) view.findViewById(R.id.button_add);
		mBackbtn = (ImageView) view.findViewById(R.id.contack_back_btn);
		net_work_status_bar = (LinearLayout) view
				.findViewById(R.id.net_status_bar_top);
		local_device_bar_top = (RelativeLayout) view
				.findViewById(R.id.local_device_bar_top);
		text_local_device_count = (TextView) view
				.findViewById(R.id.text_local_device_count);
		mPullRefreshListView = (PullToRefreshListViewK14) view
				.findViewById(R.id.pull_refresh_list_K14);

		local_device_bar_top.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(mContext, LocalDeviceListActivity.class);
				mContext.startActivity(i);
			}

		});
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(mContext,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						new GetDataTask().execute();
					}
				});
		mPullRefreshListView.setShowIndicator(false);

		mListView = mPullRefreshListView.getRefreshableView();

		mListView.setEmptyView(inf.inflate(R.layout.list_contact_nodata, null));

		mAdapter = new MainAdapter(mContext, this);
		mListView.setAdapter(mAdapter);
		mAddUser.setOnClickListener(this);
		mBackbtn.setOnClickListener(this);

		List<LocalDevice> localDevices = FList.getInstance()
				.getSetPasswordLocalDevices();
		localDevices.addAll(FList.getInstance().getUnsetPasswordLocalDevices());
		if (localDevices.size() > 0) {
			local_device_bar_top.setVisibility(View.VISIBLE);
			text_local_device_count.setText("" + localDevices.size());
		} else {
			local_device_bar_top.setVisibility(View.GONE);
		}
	}


	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.REFRESH_CONTANTS);
		filter.addAction(Constants.Action.GET_FRIENDS_STATE);
		filter.addAction(Constants.Action.LOCAL_DEVICE_SEARCH_END);
		filter.addAction(Constants.Action.ACTION_NETWORK_CHANGE);
		filter.addAction(Constants.P2P.ACK_RET_CHECK_PASSWORD);
		filter.addAction(Constants.P2P.RET_GET_REMOTE_DEFENCE);
		filter.addAction(Constants.Action.SETTING_WIFI_SUCCESS);

		// by salad for new alarmmessage
		filter.addAction(Constants.Action.REFRESH_ALARM_RECORD);
		filter.addAction(Constants.P2P.ACK_RET_GET_ALAEM_RECORD);
		filter.addAction(Constants.P2P.RET_GET_ALARM_RECORD);
		filter.addAction("com.homecoolink.AlarmRecord_Read");

		filter.addAction(Constants.P2P.RET_GET_BIND_ALARM_ID);

		mContext.registerReceiver(mReceiver, filter);

		isRegFilter = true;
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case CHANGE_REFRESHING_LABLE:
				// String lable = (String) msg.obj;
				// mPullRefreshListView.setHeadLable(lable);
				break;
			case 101:
				mAdapter.notifyDataSetChanged();
				break;
			}
			return false;
		}
	});
	
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (intent.getAction().equals(
						Constants.Action.REFRESH_ALARM_RECORD)
						|| intent.getAction().equals(
								Constants.P2P.ACK_RET_GET_ALAEM_RECORD)
						|| intent.getAction().equals(
								Constants.P2P.RET_GET_ALARM_RECORD)
						|| intent.getAction().equals(
								"com.homecoolink.AlarmRecord_Read")) {

					if (mAdapter != null) {
						new Thread(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								super.run();
								
									try {
										
										sleep(200);
										mHandler.sendEmptyMessage(101);
																				
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									


							}
							
						}.start();
						
					}
				} else if (intent.getAction().equals(
						Constants.Action.REFRESH_CONTANTS)) {

					mAdapter.notifyDataSetChanged();
					List<LocalDevice> localDevices = FList.getInstance()
							.getSetPasswordLocalDevices();
					localDevices.addAll(FList.getInstance()
							.getUnsetPasswordLocalDevices());
					if (localDevices.size() > 0) {
						local_device_bar_top
								.setVisibility(View.VISIBLE);
						text_local_device_count.setText(""
								+ localDevices.size());
					} else {
						local_device_bar_top.setVisibility(View.GONE);
					}

				} else if (intent.getAction().equals(
						Constants.Action.GET_FRIENDS_STATE)) {
					mAdapter.notifyDataSetChanged();
					refreshEnd = true;
				} else if (intent.getAction().equals(
						Constants.Action.LOCAL_DEVICE_SEARCH_END)) {
					List<LocalDevice> localDevices = FList.getInstance()
							.getSetPasswordLocalDevices();
					localDevices.addAll(FList.getInstance()
							.getUnsetPasswordLocalDevices());
					if (localDevices.size() > 0) {
						local_device_bar_top
								.setVisibility(View.VISIBLE);
						text_local_device_count.setText(""
								+ localDevices.size());
					} else {
						local_device_bar_top.setVisibility(View.GONE);
					}
					Log.e("my", "" + localDevices.size());
				} else if (intent.getAction().equals(
						Constants.Action.ACTION_NETWORK_CHANGE)) {
					
					ConnectivityManager connectivityManager = (ConnectivityManager) mContext
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo activeNetInfo = connectivityManager
							.getActiveNetworkInfo();
					if (activeNetInfo != null) {
						if (!activeNetInfo.isConnected()) {
							T.showShort(mContext,
									getString(R.string.network_error) + " "
											+ activeNetInfo.getTypeName());
							net_work_status_bar
									.setVisibility(View.VISIBLE);
						} else {
							net_work_status_bar
									.setVisibility(View.GONE);
						}
					} else {
						T.showShort(mContext, R.string.network_error);
						net_work_status_bar
								.setVisibility(View.VISIBLE);
					}

				} else if (intent.getAction().equals(
						Constants.P2P.ACK_RET_CHECK_PASSWORD)) {
					
					if (!isActive) {
						return;
					}
					int result = intent.getIntExtra("result", -1);
					if (!isCancelLoading) {
						if (result == Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
							if (null != dialog && dialog.isShowing()) {
								dialog.dismiss();
								dialog = null;
							}
							Intent control = new Intent();
							control.setClass(mContext,
									MainControlActivity.class);
							control.putExtra("contact", next_contact);
							control.putExtra("type", P2PValue.DeviceType.NPC);
							mContext.startActivity(control);
						} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {

							if (null != dialog && dialog.isShowing()) {
								dialog.dismiss();
								dialog = null;
							}
							new PwdErrorUtil(getActivity())
									.PwdError(next_contact.contactId);
						} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
							P2PHandler.getInstance().checkPassword(
									next_contact.contactId,
									next_contact.contactPassword);
						} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
							if (null != dialog && dialog.isShowing()) {
								dialog.dismiss();
								dialog = null;
							}
							T.showShort(mContext,
									R.string.insufficient_permissions);
						}
					}
				} else if (intent.getAction().equals(
						Constants.P2P.RET_GET_REMOTE_DEFENCE)) {
					int state = intent.getIntExtra("state", -1);
					String contactId = intent.getStringExtra("contactId");
					Contact contact = FList.getInstance().isContact(contactId);

					if (state == Constants.DefenceState.DEFENCE_STATE_WARNING_NET) {
						if (null != contact && contact.isClickGetDefenceState) {
							T.showShort(mContext, R.string.net_error);
						}
					} else if (state == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
						if (null != contact && contact.isClickGetDefenceState) {
							new PwdErrorUtil(getActivity())
									.PwdError(contact.contactId);
						}
					}
					if (null != contact && contact.isClickGetDefenceState) {
						FList.getInstance().setIsClickGetDefenceState(
								contactId, false);
					}

					mAdapter.notifyDataSetChanged();
				} else if (intent.getAction().equals(
						Constants.Action.SETTING_WIFI_SUCCESS)) {
					FList flist = FList.getInstance();
					flist.updateOnlineState();
					flist.searchLocalDevice();
				} else if (intent.getAction().equals(
						Constants.P2P.RET_GET_BIND_ALARM_ID)) {
					
					String[] data = intent.getStringArrayExtra("data");
					// last_bind_data=data;

					String str1 = intent.getStringExtra("user");
					String str2 = intent.getStringExtra("pwd");
					if (str1 == null) {

					} else {
						
						try {

							// if (last_bind_data==null) {
							
							// }

							for (int i = 0; i < data.length; i++) {
								
								if (data[i].equals(NpcCommon.mThreeNum)) {
									
									String[] data1 = Utils
											.getDeleteAlarmIdArray(data, i);
									// last_bind_data = data;
									// NpcCommon.mThreeNum
									P2PHandler.getInstance().setBindAlarmId(
											str1, str2, data1.length, data1);
								}
							}
							//
							SettingListener.currentUser = null;
							SettingListener.currentPwd = null;
						} catch (Exception e) {
							Log.e("343", Log.getStackTraceString(e));
						}
					}
				}

			} catch (Exception se) {
				Log.e("343", Log.getStackTraceString(se));
			}
		}
	};

	

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			Log.e("my", "doInBackground");
			FList flist = FList.getInstance();
			flist.searchLocalDevice();

			if (flist.size() == 0) {
				return null;
			}
			refreshEnd = false;
			// if(isFirstRefresh){
			// Log.e("my","doInBackground2");
			// Utils.sleepThread(2000);
			// Log.e("my","doInBackground3");
			// flist.updateOnlineState();
			// isFirstRefresh = false;
			// }else{
			// flist.updateOnlineState();
			// }
			flist.updateOnlineState();
			while (!refreshEnd) {
				Utils.sleepThread(1000);
			}

			Message msg = new Message();
			msg.what = CHANGE_REFRESHING_LABLE;
			msg.obj = mContext.getResources().getString(
					R.string.pull_to_refresh_refreshing_success_label);
			mHandler.sendMessage(msg);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// mListItems.addFirst("Added after refresh...");
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	private PopupWindow popMenu;

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.button_add){
			View view = inf.inflate(R.layout.contact_menu, null, false);

			TextView btnAutoAdd = (TextView) view.findViewById(R.id.btnAutoAdd);
			TextView btnAddByHand = (TextView) view
					.findViewById(R.id.btnAddByHand);
			TextView btnAddByScan = (TextView) view
					.findViewById(R.id.btnAddByScan);
			btnAutoAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent radar_add = new Intent(mContext,
							RadarAddFirstActivity.class);
					mContext.startActivity(radar_add);

				}
			});
			btnAddByHand.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent add_contact = new Intent(mContext,
							AddContactActivity.class);
					mContext.startActivity(add_contact);
				}
			});
			btnAddByScan.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent vlan_add = new Intent(mContext,
							LocalDeviceListActivity.class);
					mContext.startActivity(vlan_add);
				}
			});
			if (view.findViewById(R.id.IAMENGLISH) != null) {
				popMenu = new PopupWindow(view, (int) getResources()
						.getDimension(R.dimen.Contact_Menu_Width_En),
						(int) getResources().getDimension(
								R.dimen.Contact_Menu_Height));
			} else {
				popMenu = new PopupWindow(view, (int) getResources()
						.getDimension(R.dimen.Contact_Menu_Width),
						(int) getResources().getDimension(
								R.dimen.Contact_Menu_Height));
			}

			// popMenu = new
			// PopupWindow(view,LayoutParams.WRAP_CONTENT,Utils.dip2px(mContext,
			// (int)getResources().getDimension(R.dimen.Contact_Menu_Height) ));
			popMenu.setFocusable(true);
			popMenu.setOutsideTouchable(true);
			popMenu.setBackgroundDrawable(new BitmapDrawable());
			popMenu.showAsDropDown(v);

		}else if(id==R.id.contack_back_btn){  //后退到homecoo系统
			Intent canel = new Intent();
			canel.setAction(Constants.Action.ACTION_SWITCH_USER);
			mContext.sendBroadcast(canel);
		}
		
//		switch (v.getId()) {
//		case R.id.button_add:
//			// Intent add_contact = new
//			// Intent(mContext,ThreeAddContactActivity.class);
//			// mContext.startActivity(add_contact);
//			View view = inf.inflate(R.layout.contact_menu, null, false);
//
//			TextView btnAutoAdd = (TextView) view.findViewById(R.id.btnAutoAdd);
//			TextView btnAddByHand = (TextView) view
//					.findViewById(R.id.btnAddByHand);
//			TextView btnAddByScan = (TextView) view
//					.findViewById(R.id.btnAddByScan);
//			btnAutoAdd.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//
//					Intent radar_add = new Intent(mContext,
//							RadarAddFirstActivity.class);
//					mContext.startActivity(radar_add);
//
//				}
//			});
//			btnAddByHand.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//
//					Intent add_contact = new Intent(mContext,
//							AddContactActivity.class);
//					mContext.startActivity(add_contact);
//				}
//			});
//			btnAddByScan.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//
//					Intent vlan_add = new Intent(mContext,
//							LocalDeviceListActivity.class);
//					mContext.startActivity(vlan_add);
//				}
//			});
//			if (view.findViewById(R.id.IAMENGLISH) != null) {
//				popMenu = new PopupWindow(view, (int) getResources()
//						.getDimension(R.dimen.Contact_Menu_Width_En),
//						(int) getResources().getDimension(
//								R.dimen.Contact_Menu_Height));
//			} else {
//				popMenu = new PopupWindow(view, (int) getResources()
//						.getDimension(R.dimen.Contact_Menu_Width),
//						(int) getResources().getDimension(
//								R.dimen.Contact_Menu_Height));
//			}
//
//			// popMenu = new
//			// PopupWindow(view,LayoutParams.WRAP_CONTENT,Utils.dip2px(mContext,
//			// (int)getResources().getDimension(R.dimen.Contact_Menu_Height) ));
//			popMenu.setFocusable(true);
//			popMenu.setOutsideTouchable(true);
//			popMenu.setBackgroundDrawable(new BitmapDrawable());
//			popMenu.showAsDropDown(v);
//
//			break;
//		default:
//			break;
//		}
	}

	

	

	

	public void quickSetting(final Contact contact) {
		if (contact.contactId == null || contact.contactId.equals("")) {
			T.showShort(mContext, R.string.username_error);
			return;
		}
		if (contact.contactPassword == null
				|| contact.contactPassword.equals("")) {
			T.showShort(mContext, R.string.password_error);
			return;
		}
		next_contact = contact;
		dialog = new NormalDialog(mContext);
		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {

				
				isCancelLoading = true;
				
			}

		});
		dialog.showLoadingDialog2();

		dialog.setCanceledOnTouchOutside(false);
	
		isCancelLoading = false;
		
		P2PHandler.getInstance().checkPassword(contact.contactId,
				contact.contactPassword);

	}

	@Override
	public void onPause() {

		MainThread.setOpenThread(false);
		super.onPause();
		isActive = false;

	}

	@Override
	public void onResume() {

		super.onResume();
		MainThread.setOpenThread(true);
		isActive = true;
	}

	@Override
	public void onDestroy() {

		// MainThread.getInstance().kill();
		super.onDestroy();
		Log.e("my", "onDestroy");
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

}