package com.homecoolink.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.homecoolink.R;
import com.homecoolink.activity.MainActivity;
import com.homecoolink.adapter.AlarmRecordAdapter3;
import com.homecoolink.data.AlarmRecord;
import com.homecoolink.data.DataManager;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.widget.NormalDialog;
import com.lib.pullToRefresh.PullToRefreshBase;
import com.lib.pullToRefresh.PullToRefreshListViewK14;
import com.lib.pullToRefresh.PullToRefreshBase.OnRefreshListener;

public class AlarmRecordFrag extends BaseFragment implements OnClickListener {
	// private boolean isRegFilter = false;
	private Context mContext;
	private ListView mListView;
	private AlarmRecordAdapter3 mAdapter;
	private ImageView setread_btn,setreaddisable;//
	private ArrayList<com.homecoolink.entity.AlarmRecord> al;
	private PullToRefreshListViewK14 mPullRefreshListView;
	NormalDialog dialog;
	public static final int CHANGE_REFRESHING_LABLE = 0x12;
	boolean refreshEnd = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_alarm_record, container,
				false);
		mContext = MainActivity.mContext;
//		mContext=MyApp.app;

		initComponent(view);
		// regFilter();
		return view;
	}

	public void NotifyChanged() {
		mAdapter.ChangeData(getData());
		CheckNewMess() ;
	}
	public void CheckNewMess() {
		
		ArrayList<com.homecoolink.data.AlarmRecord> list = (ArrayList<com.homecoolink.data.AlarmRecord>) DataManager
				.findUnReadAlarmRecordByActiveUser(mContext,
						NpcCommon.mThreeNum);
		if (list.size() > 0) {
			setread_btn.setVisibility(View.VISIBLE);
			setreaddisable.setVisibility(View.GONE);
		} else {
			setread_btn.setVisibility(View.GONE );
			setreaddisable.setVisibility(View.VISIBLE);
		}
	}
	public void initComponent(View view) {
		setread_btn = (ImageView) view.findViewById(R.id.setread_btn);
		setreaddisable= (ImageView) view.findViewById(R.id.setreaddisable);
		setread_btn.setOnClickListener(this);
		mPullRefreshListView = (PullToRefreshListViewK14) view
				.findViewById(R.id.pull_refresh_list);

		mListView = mPullRefreshListView.getRefreshableView();

		mListView.setEmptyView(((Activity) mListView.getContext())
				.getLayoutInflater().inflate(R.layout.list_alarm_record_nodata,
						null));
		try {

			al = getData();
			mAdapter = new AlarmRecordAdapter3(mContext, al, this);
			mListView.setAdapter(mAdapter);
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
			CheckNewMess() ;
		} catch (Exception e) {
			// handle exception
			
			Log.e("343", Log.getStackTraceString(e));
		}
	}

	// public void regFilter(){
	// IntentFilter filter = new IntentFilter();
	// filter.addAction(Constants.Action.ACTION_REFRESH_NEARLY_TELL);
	// mContext.registerReceiver(mReceiver, filter);
	// isRegFilter = true;
	// }

	// BroadcastReceiver mReceiver = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// if
	// (intent.getAction().equals(Constants.Action.ACTION_REFRESH_NEARLY_TELL))
	// {
	// getData();
	// updateListView();
	// }
	// }
	// };

	private ArrayList<com.homecoolink.entity.AlarmRecord> getData() {

		ArrayList<com.homecoolink.entity.AlarmRecord> lastal = new ArrayList<com.homecoolink.entity.AlarmRecord>();
		List<AlarmRecord> firstal = DataManager.findAlarmRecordByGroup(
				mContext, NpcCommon.mThreeNum, null);
		for (int i = 0; i < firstal.size(); i++) {
			AlarmRecord ar = firstal.get(i);
			if ("".equals(ar.deviceId)) {

				lastal.add(new com.homecoolink.entity.AlarmRecord(ar.id,
						ar.deviceId, ar.alarmType, ar.alarmTime, ar.activeUser,
						ar.alarmStatus, 0,ar.group,ar.item));
			} else {
				lastal.add(new com.homecoolink.entity.AlarmRecord(ar.id,
						ar.deviceId, ar.alarmType, ar.alarmTime, ar.activeUser,
						ar.alarmStatus, 1,ar.group,ar.item));
			}

		}
		return lastal;
	}

	// private void updateListView(){
	// if (mAdapter != null) {
	// mAdapter.updateData(nearlyTellList);
	// mAdapter.notifyDataSetChanged();
	// }else{
	// mAdapter = new NearlyTellAdapter(mContext, nearlyTellList);
	// mListView.setAdapter(mAdapter);
	// }

	// }

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.setread_btn){
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
							null);
					al = getData();
					NotifyChanged();
				}
			});
			dialog.showDialog();
		}
		
//		switch (v.getId()) {
//		// case R.id.clear:
//		// //createDeleteDialog(mContext, -1,"0");
//		// if(null!=dialog&&dialog.isShowing()){
//		// Log.e("my","isShowing");
//		// return;
//		// }
//		// dialog = new NormalDialog(
//		// mContext,
//		// mContext.getResources().getString(R.string.delete_call_records),
//		// mContext.getResources().getString(R.string.clear_confirm),
//		// mContext.getResources().getString(R.string.clear),
//		// mContext.getResources().getString(R.string.cancel)
//		// );
//		// dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
//		//
//		// @Override
//		// public void onClick() {
//		// // Auto-generated method stub
//		// DataManager.clearNearlyTell(mContext, NpcCommon.mThreeNum);
//		// Intent refreshContans=new Intent();
//		// refreshContans.setAction(Constants.Action.ACTION_REFRESH_NEARLY_TELL);
//		// mContext.sendBroadcast(refreshContans);
//		// }
//		// });
//		// dialog.showDialog();
//		// break;
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
//							null);
//					al = getData();
//					NotifyChanged();
//				}
//			});
//			dialog.showDialog();
//			break;
//		default:
//			break;
//		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// if(isRegFilter){
		// isRegFilter = false;
		// mContext.unregisterReceiver(mReceiver);
		// }
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.

			al = getData();
			if (al.size() == 0) {
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

			// while (!refreshEnd) {
			// Utils.sleepThread(1000);
			// }

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
			mAdapter.ChangeData(al);
			CheckNewMess() ;
			super.onPostExecute(result);
		}
	}
}
