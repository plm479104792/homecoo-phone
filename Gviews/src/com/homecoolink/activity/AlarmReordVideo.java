package com.homecoolink.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.adapter.VideoContactAdapter;
import com.homecoolink.adapter.VideoDateAdapter;
import com.homecoolink.data.Contact;
import com.homecoolink.fragment.FaultFragment;
import com.homecoolink.fragment.LoadingFragment;
import com.homecoolink.fragment.RecordListFragment;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.utils.T;
import com.p2p.core.P2PHandler;


public class AlarmReordVideo extends BaseActivity implements OnClickListener {

	TextView dateSpinner;
	RecordListFragment rlFrag;
	LoadingFragment loadFrag;
	FaultFragment faultFrag;
	Contact contact;
	boolean receiverIsReg = false;

	protected String daten;
	protected String dvid;
	private LayoutInflater inf = null;
	PopupWindow devicepopMenu, datepopMenu;
	VideoContactAdapter va;
	VideoDateAdapter da;
	ImageView back;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		inf = getLayoutInflater();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_alarm_video);
		daten = getIntent().getStringExtra("date").split(" ")[0];
		dvid = getIntent().getStringExtra("dvid");
		contact = FList.getInstance().isContact(dvid);
		initComponent();
		
	}

	private void disableDate() {
		ColorStateList csl = getResources().getColorStateList(
				R.color.gray);
		dateSpinner.setTextColor(csl);
		dateSpinner.setText(getResources().getString(R.string.no_vedio_tip));

	}

	@SuppressWarnings("deprecation")
	private void bindDate() {
		disableDate();
		Date date = null;
		
		
				
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = format.parse(getIntent().getStringExtra("date"));
			Log.e("343", "date=="+date.toString());
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Date dt = new Date();
		dt.setYear(date.getYear());
		dt.setMonth(date.getMonth());
		dt.setDate(date.getDate());
		dt.setHours(date.getHours()+1);
		dt.setMinutes(date.getMinutes());
		dt.setSeconds(date.getSeconds());
		
		Date dtStart = new Date();
		dtStart.setYear(date.getYear());
		dtStart.setMonth(date.getMonth());
		dtStart.setDate(date.getDate());
		dtStart.setHours(date.getHours()-1);
		dtStart.setMinutes(date.getMinutes());
		dtStart.setSeconds(date.getSeconds());
		
		Log.e("343", "dt="+dt.toString());
		Log.e("343", "dtStart="+dtStart.toString());
		P2PHandler.getInstance().getRecordFiles(contact.contactId,
				contact.contactPassword, dtStart, dt);
		loadFrag = new LoadingFragment();
		this.replaceFrag(loadFrag, "loadingFrag");
	}

	private String[] vfiles = null;

	private void bindDateVal() {

		if (null == rlFrag) {
			rlFrag = new RecordListFragment();

		}
		if (vfiles != null && vfiles.length > 0) {
			String[] datesStrings = GetDateFromRecords(vfiles);
			ColorStateList csl = getResources()
					.getColorStateList(R.color.black);
			dateSpinner.setTextColor(csl);
			if(Arrays.asList(datesStrings).indexOf(daten) >=0)
			{
				dateSpinner.setText(daten);
				
				rlFrag.setList(GetRecordsByDate(daten));
			}
			else {
				dateSpinner.setText(datesStrings[0]);
				
				rlFrag.setList(GetRecordsByDate(datesStrings[0]));
			}
			
			rlFrag.setUser(contact);
			replaceFrag(rlFrag, "recordFrag");
		} else {
			disableDate();

			rlFrag.setList(GetRecordsByDate("不可用"));
			rlFrag.setUser(contact);
			replaceFrag(rlFrag, "recordFrag");
		}
	}

	public void SetSelectedDate(String date) {

		datepopMenu.dismiss();
		ColorStateList csl = getResources().getColorStateList(
				R.color.black);
		dateSpinner.setTextColor(csl);
		dateSpinner.setText(date);
		if (null == rlFrag) {
			rlFrag = new RecordListFragment();
			rlFrag.setUser(contact);
		}
		rlFrag.setList(GetRecordsByDate(date));
		rlFrag.setUser(contact);
		replaceFrag(rlFrag, "recordFrag");

	}

	private String[] GetDateFromRecords(String[] records) {

		ArrayList<String> list = new ArrayList<String>();
		for (String rString : records) {
			String date = rString.substring(6, rString.length());
			date = date.split("_")[0];
			if (!list.contains(date)) {
				list.add(date);
			}
		}
		String[] res = new String[list.size()];
		list.toArray(res);
		return res;

	}

	public void initComponent() {
		back = (ImageView) findViewById(R.id.back_btn);

		back.setOnClickListener(this);
		String strName = contact.contactName;
		if (strName.length() > 10) {
			((TextView) findViewById(R.id.title)).setText(getResources()
					.getString(R.string.alarm_video_title)
					+ "-"
					+ strName.substring(0, 10) + "...");
		} else {
			((TextView) findViewById(R.id.title)).setText(getResources()
					.getString(R.string.alarm_video_title) + "-" + strName);
		}
		dateSpinner = (TextView) findViewById(R.id.spinner2);
		dateSpinner.setOnClickListener(this);
		regFilter();
		bindDate();
	}

	private List<String> GetRecordsByDate(String dat) {

		ArrayList<String> list = new ArrayList<String>();
		if (vfiles != null) {
			for (String rString : vfiles) {
				String date = rString.substring(6, rString.length());
				date = date.split("_")[0];
				if (date.equals(dat)) {
					list.add(rString);
				}
			}
		}
		return list;

	}

	private void showPopMenu(PopupWindow p, View v) {
		if (p == null) {
			View view = inf.inflate(R.layout.item_video_spinnercontainer, null,
					false);
			View vp = (View) v.getParent();
			vp.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			p = new PopupWindow(view, vp.getWidth(), LayoutParams.WRAP_CONTENT);

			p.setFocusable(true);
			p.setOutsideTouchable(true);
			p.setBackgroundDrawable(new BitmapDrawable());

			p.showAsDropDown(vp);

			ViewGroup.LayoutParams paras = view.getLayoutParams();
			paras.width = vp.getWidth();
			if (view.getMeasuredHeight() > 300) {
				paras.height = 300;
			}
			view.setLayoutParams(paras);
			ListView lvListView = (ListView) p.getContentView().findViewById(
					R.id.listView1);

			String[] datesStrings = new String[] {};
			if (vfiles != null && vfiles.length > 0) {
				datesStrings = GetDateFromRecords(vfiles);
			}
			da = new VideoDateAdapter(AlarmReordVideo.this, datesStrings,
					AlarmReordVideo.this);
			lvListView.setAdapter(da);
			datepopMenu = p;

			return;
		}
		p.showAsDropDown((View) v.getParent());

		String[] datesStrings = new String[] {};
		if (vfiles != null && vfiles.length > 0) {
			datesStrings = GetDateFromRecords(vfiles);
		}
		da.ChangeData(datesStrings);

	}

	public void regFilter() {
		if (!receiverIsReg) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.P2P.ACK_RET_GET_PLAYBACK_FILES);
			filter.addAction(Constants.P2P.RET_GET_PLAYBACK_FILES);
			registerReceiver(mReceiver, filter);
			receiverIsReg = true;
		}

	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			 if (intent.getAction().equals(
					Constants.P2P.RET_GET_PLAYBACK_FILES)) {
				if (null == rlFrag) {
					rlFrag = new RecordListFragment();
					rlFrag.setUser(contact);
				}
				
				
				vfiles = (String[]) intent
						.getCharSequenceArrayExtra("recordList");
				
				
				bindDateVal();
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_GET_PLAYBACK_FILES)) {

				int result = intent.getIntExtra("result", -1);
				if (result != Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
					if (null == faultFrag) {
						faultFrag = new FaultFragment();
					}
					if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
						T.showShort(AlarmReordVideo.this, R.string.password_error);
					} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
						Log.e("my", "net error resend:set npc time");
						faultFrag.setErrorText(getResources().getString(
								R.string.net_error));

					} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
						faultFrag.setErrorText(getResources().getString(
								R.string.insufficient_permissions));
//						T.showShort(AlarmReordVideo.this,
//								R.string.insufficient_permissions);
					}
					replaceFrag(faultFrag, "faultFrag");
				}
			}
		}
	};

	public void replaceFrag(Fragment fragment, String mark) {
		try {
			FragmentManager manager = getSupportFragmentManager();

			FragmentTransaction transaction = manager.beginTransaction();
			transaction.setCustomAnimations(android.R.anim.fade_in,
					android.R.anim.fade_out);
			transaction.replace(R.id.layout_2, fragment, mark);
			transaction.commit();
			manager.executePendingTransactions();
		} catch (Exception e) {
			Log.e("my", "replaceFrag error" + e.getMessage());
		}
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.back_btn){
			finish();
		}else if(id==R.id.spinner2){
			showPopMenu(datepopMenu, v);
		}
		
//		switch (v.getId()) {
//		case R.id.back_btn:
//			finish();
//			break;
//		case R.id.spinner2:
//			showPopMenu(datepopMenu, v);
//		}
	}

	@Override
	public void onStop() {

		super.onStop();
		if (receiverIsReg) {
			receiverIsReg = false;
			unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onStart() {

		super.onStart();
		if (!receiverIsReg) {
			regFilter();
		}
	}

	@Override
	public int getActivityInfo() {

		return Constants.ActivityInfo.ACTIVITY_ALARMVIDEO;
	}

}
