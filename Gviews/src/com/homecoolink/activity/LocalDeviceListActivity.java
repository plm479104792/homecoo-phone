package com.homecoolink.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.homecoolink.R;
import com.homecoolink.adapter.LocalDeviceListAdapter;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.lib.pullToRefresh.PullToRefreshBase;
import com.lib.pullToRefresh.PullToRefreshListViewK14;


public class LocalDeviceListActivity extends BaseActivity implements OnClickListener{
	private ImageView mBack;
	PullToRefreshListViewK14 mpull_refresh_list;
	ListView mlistview;
	private LocalDeviceListAdapter mAdapter;
	private Context mContext;
	boolean isRegFilter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_local_device_list);
		initCompent();
		regFilter();
	}
	
	
	public void initCompent() {
		mBack=(ImageView)findViewById(R.id.back_btn);
		mpull_refresh_list = (PullToRefreshListViewK14) findViewById(R.id.pull_refresh_list);
		mpull_refresh_list.setShowIndicator(false);
		
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
				FList.getInstance().searchLocalDevice();
			}
		});
		mlistview = mpull_refresh_list.getRefreshableView();
		
		mAdapter = new LocalDeviceListAdapter(this);
		mlistview.setAdapter(mAdapter);
		
		mBack.setOnClickListener(this);
	}
	
	public void regFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.ADD_CONTACT_SUCCESS);
		filter.addAction(Constants.Action.LOCAL_DEVICE_SEARCH_END);
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getAction().equals(Constants.Action.ADD_CONTACT_SUCCESS)){
				mpull_refresh_list.onRefreshComplete();
				mAdapter.updateData();
				finish();
			}else if(intent.getAction().equals(Constants.Action.LOCAL_DEVICE_SEARCH_END)){
				mpull_refresh_list.onRefreshComplete();
				
				mAdapter.updateData();
			}
		}
	};
	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.back_btn){
			this.finish();
		}
		
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.back_btn:
//			this.finish();
//			break;
//		default:
//			break;
//		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(isRegFilter){
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}
	
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_LOCAL_DEVICE_LIST;
	}

	

}
