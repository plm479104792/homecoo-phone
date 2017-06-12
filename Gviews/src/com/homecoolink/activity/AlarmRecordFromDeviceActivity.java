package com.homecoolink.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.adapter.AlarmRecordAdapter3;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;
import com.lib.pullToRefresh.PullToRefreshBase;
import com.lib.pullToRefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.lib.pullToRefresh.PullToRefreshListViewK14;
import com.p2p.core.global.AlarmRecord;


public class AlarmRecordFromDeviceActivity extends BaseActivity implements
		OnClickListener {
	Contact mcontact;
	boolean isregFiter = false;
	private Context mContext;
	ImageView back, setread_btn, setreaddisable;//
	PullToRefreshListViewK14 mpull_refresh_list;
	ListView mlistview;
	AlarmRecordAdapter3 adapter;
	ArrayList<com.homecoolink.entity.AlarmRecord> list;
	ArrayList<com.homecoolink.entity.AlarmRecord> showlist = new ArrayList<com.homecoolink.entity.AlarmRecord>();
	public static final int CHANGE_REFRESHING_LABLE = 0x12;
	Boolean isemptyviewseted = false;
	TextView titleTextView;
	ArrayList<AlarmRecord> tempremotelist = new ArrayList<AlarmRecord>();
	Boolean isingeting = false;
	int page = 1;
	int pagesize = 20;
	NormalDialog dig;
	public static final int HANDLER_ID_HIDE_LAYOUT_LOADING = 0x01;
	RelativeLayout layout_loading;
	private boolean isRegFilter = false;
	String[] last_bind_data;
	public void CheckNewMess() {

		ArrayList<com.homecoolink.data.AlarmRecord> list = (ArrayList<com.homecoolink.data.AlarmRecord>) DataManager
				.findUnReadAlarmRecordByDeviceID(mContext, NpcCommon.mThreeNum,
						mcontact.contactId);
		if (list.size() > 0) {
			setread_btn.setVisibility(View.VISIBLE);
			setreaddisable.setVisibility(View.GONE);
		} else {
			setread_btn.setVisibility(View.GONE);
			setreaddisable.setVisibility(View.VISIBLE);
		}
	}

	private void showloading() {
		if (layout_loading == null) {
			layout_loading = (RelativeLayout) findViewById(R.id.layout_loading);
		}
		layout_loading.setVisibility(View.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(
				AlarmRecordFromDeviceActivity.this, android.R.anim.fade_in);
		anim.setDuration(500);
		layout_loading.startAnimation(anim);
	}

	private void stoploading() {
		layout_loading.setVisibility(View.GONE);
	}

	private void GetNext() {
		if (list != null) {
			int start = (page - 1) * pagesize;
			int end = page * pagesize;
			if (end > list.size()) {
				end = list.size();
			}
			if (start < list.size()) {
				page++;
				for (int i = start; i < end; i++) {
					showlist.add(list.get(i));
				}
			} else {

				T.showShort(
						mContext,
						getResources().getString(
								R.string.loaded_successfully_tip));
			}
		}

	}

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.activity_alarm_record1);		
		mContext = this;
		mcontact = (Contact) getIntent().getSerializableExtra("contact");
		
//		P2PHandler.getInstance().getBindAlarmId(mcontact.contactId,
//				mcontact.contactPassword);
//		regFilter();
		
		initComponent();
		
	}
	
//	public void regFilter() {
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(Constants.P2P.RET_GET_BIND_ALARM_ID);
//		mContext.registerReceiver(mReceiver, filter);
//		isRegFilter = true;
//	}
//	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context arg0, Intent intent) {
//			if (intent.getAction().equals(Constants.P2P.RET_GET_BIND_ALARM_ID)) {
//						
//				String[] data = intent.getStringArrayExtra("data");				
//				last_bind_data = data;
//				for (int i = 0; i < last_bind_data.length; i++) {
//					if (.equals(last_bind_data[i])) {
//						initComponent();
//					}					
//				}
//			}else {
//				
//			}
//		}
//	};

	
	

	private ArrayList<com.homecoolink.entity.AlarmRecord> getData() {
		ArrayList<com.homecoolink.entity.AlarmRecord> lastal = new ArrayList<com.homecoolink.entity.AlarmRecord>();

		try {
			List<com.homecoolink.data.AlarmRecord> firstal = DataManager
					.findAlarmRecordByGroup(mContext, NpcCommon.mThreeNum,
							mcontact.contactId);
			for (int i = 0; i < firstal.size(); i++) {
				com.homecoolink.data.AlarmRecord ar = firstal.get(i);
				if ("".equals(ar.deviceId)) {

					lastal.add(new com.homecoolink.entity.AlarmRecord(ar.id,
							ar.deviceId, ar.alarmType, ar.alarmTime,
							ar.activeUser, ar.alarmStatus, 0,ar.group,ar.item));
				} else {
					lastal.add(new com.homecoolink.entity.AlarmRecord(ar.id,
							ar.deviceId, ar.alarmType, ar.alarmTime,
							ar.activeUser, ar.alarmStatus, 1,ar.group,ar.item));
				}

			}
		} catch (Exception e) {

		}

		return lastal;
	}

	public void initComponent() {
		back = (ImageView) findViewById(R.id.back_btn);
		setread_btn = (ImageView) findViewById(R.id.setread_btn);
		setreaddisable = (ImageView) findViewById(R.id.setreaddisable);
		titleTextView = (TextView) findViewById(R.id.title);
//		if (mcontact.contactName.length() > 8) {
//			titleTextView.setText(Utils.getResString(mContext,
//					R.string.message_record)
//					+ "-"
//					+ mcontact.contactName.substring(0, 7) + "...");
//		} else {
			titleTextView.setText(Utils.getResString(mContext,
					R.string.message_record) + "-" + mcontact.contactName);
//		}

		setread_btn.setOnClickListener(this);

		back.setOnClickListener(this);

		mpull_refresh_list = (PullToRefreshListViewK14) findViewById(R.id.pull_refresh_list);
		mpull_refresh_list.setShowIndicator(false);
		// mpull_refresh_list
		// .setMode(com.lib.pullToRefresh.PullToRefreshBase.Mode.BOTH);
		// mpull_refresh_list.getLoadingLayoutProxy(false, true).setPullLabel(
		// "上拉加载更多");
		// mpull_refresh_list.getLoadingLayoutProxy(false,
		// true).setReleaseLabel(
		// "释放加载更多");
		// mpull_refresh_list.getLoadingLayoutProxy(false, true)
		// .setLastUpdatedLabel("");
		// mpull_refresh_list.getLoadingLayoutProxy(false, true)
		// .setRefreshingLabel("正在加载");
		mpull_refresh_list
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (!isingeting) {
							isingeting = true;
							showloading();
							new GetNextTask().execute();
						}
					}
				});
		mpull_refresh_list
				.setOnRefreshListener(new com.lib.pullToRefresh.PullToRefreshBase.OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(mContext,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						mpull_refresh_list.getLoadingLayoutProxy(true, false)
								.setLastUpdatedLabel(label);
						new GetRemoteDataTask().execute();
					}
				});
		mlistview = mpull_refresh_list.getRefreshableView();
		// TextView tView = new TextView(mContext);
		// tView.setText("正在加载...");
		// mlistview.setEmptyView(tView);
		

		dig = new NormalDialog(mContext);
		dig.setTitle(R.string.download);
		dig.showLoadingDialog();
		new GetRemoteDataTask().execute();
	}

	public void NotifyChanged() {
		list = getData();
		int start = 0;
		int end = page * pagesize;
		if (end > list.size()) {
			end = list.size();
		}
		showlist.clear();
		showlist = null;
		showlist = new ArrayList<com.homecoolink.entity.AlarmRecord>();
		for (int i = start; i < end; i++) {
			showlist.add(list.get(i));
		}

		INITadapter();
		CheckNewMess();
	}

	private void INITadapter() {

		if (adapter == null) {
			adapter = new AlarmRecordAdapter3(mContext, showlist, this);
			adapter.isdevice = true;
			mlistview.setAdapter(adapter);
		} else {
			adapter.ChangeData(showlist);
		}
	}

	@Override
	public int getActivityInfo() {

		return Constants.ActivityInfo.ACTIVITY_ALARMRECORDFROMDEVICEACTIVITY;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
//		if (isRegFilter) {
//			mContext.unregisterReceiver(mReceiver);
//			isRegFilter = false;
//		}
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.back_btn){
			finish();
		}else if(id==R.id.setread_btn){
			NormalDialog dialog = new NormalDialog(mContext, mContext
					.getResources().getString(R.string.setread_alarm_records),
					mContext.getResources().getString(
							R.string.confirm_setread_alarm_records), mContext
							.getResources().getString(R.string.setread),
					mContext.getResources().getString(R.string.cancel));
			dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

				@Override
				public void onClick() {
					DataManager.updateAlarmRecordRead(mContext,
							mcontact.contactId);
					list = getData();
					NotifyChanged();
				}
			});
			dialog.showDialog();
		}

//		switch (v.getId()) {
//		case R.id.back_btn:
//			finish();
//			break;
//		case R.id.setread_btn:
//			NormalDialog dialog = new NormalDialog(mContext, mContext
//					.getResources().getString(R.string.setread_alarm_records),
//					mContext.getResources().getString(
//							R.string.confirm_setread_alarm_records), mContext
//							.getResources().getString(R.string.setread),
//					mContext.getResources().getString(R.string.cancel));
//			dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
//
//				@Override
//				public void onClick() {
//					DataManager.updateAlarmRecordRead(mContext,
//							mcontact.contactId);
//					list = getData();
//					NotifyChanged();
//				}
//			});
//			dialog.showDialog();
//			break;
//		default:
//			break;
//		}
	}

	public class GetRemoteDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {
			MyApp.GetRemote(mcontact.contactId, mcontact.contactPassword);		
			while (!MyApp.GACID.equals("")) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			list = getData();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			mpull_refresh_list.onRefreshComplete();
			if (list.size() > 0) {
				page = 1;
				showlist.clear();
				showlist = null;
				showlist = new ArrayList<com.homecoolink.entity.AlarmRecord>();
				GetNext();
				INITadapter();
			} else {
				if (!isemptyviewseted) {
					isemptyviewseted = true;
					mlistview.setEmptyView(getLayoutInflater().inflate(
							R.layout.list_alarm_record_nodata, null));
				}
			}
			if (dig != null && dig.isShowing()) {
				dig.dismiss();
				dig = null;
			}
			CheckNewMess();
			super.onPostExecute(result);
		}
	}

	public class GetNextTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {

			GetNext();

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			INITadapter();
			isingeting = false;
			stoploading();
			CheckNewMess();
			super.onPostExecute(result);
		}
	}

}
