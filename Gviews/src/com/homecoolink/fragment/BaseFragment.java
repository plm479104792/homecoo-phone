package com.homecoolink.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

public class BaseFragment extends Fragment{
	private boolean isRun = false;
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isRun = false;
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		try {
			super.onResume();
			isRun = true;
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("343", Log.getStackTraceString(e));
		}
	}
	
	
	public boolean getIsRun(){
		return isRun;
	}
	
}
