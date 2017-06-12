package com.tuwa.smarthome.fragment;

import com.tuwa.smarthome.R;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


/**
 * @类名    HomeFragment
 * @创建者   ppa
 * @创建时间 2016-3-29
 * @描述   TODO
 */
public class SystemUpdateFragment extends Fragment implements OnClickListener {
	public Activity mActivity;
	private TextView tvVersion;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mActivity =  getActivity();
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(mActivity, R.layout.fragment_set_content, null);
		tvVersion = (TextView) view.findViewById(R.id.tv_version);
		
		Button btnUpdate = (Button) view.findViewById(R.id.btn_update);
		btnUpdate.setOnClickListener(this);
		
		initData();
		return view;
	}
	
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    	initData();
    }
  



	private void initData() {
		String version=getVersion();
		tvVersion.setText("当前版本: "+version);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_update:
			System.out.println("从服务器获取更新！");
			UmengUpdateAgent.forceUpdate(mActivity);
			break;
		
		default:
			break;
		}
		
	}
	
	
	  /**
	   * 获取版本号
	   * @return 当前应用的版本号
	   */
	  public String getVersion() {
	      try {
	         PackageManager manager = mActivity.getPackageManager();
	         PackageInfo info = manager.getPackageInfo(mActivity.getPackageName(), 0);
	          String version = info.versionName;
            return  version;
	     } catch (Exception e) {
	         e.printStackTrace();
	         return null;
	     }
	 }
	
    
}
