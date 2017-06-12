package com.homecoolink.fragment;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

import com.homecoolink.R;
import com.homecoolink.adapter.ImageBrowserAdapter;
import com.homecoolink.adapter.VideoContactAdapter;
import com.homecoolink.adapter.VideoDateAdapter;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.utils.T;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.P2PHandler;


public class ImageFrag extends BaseFragment implements OnClickListener {
	private TextView myPhotoTab, myVideoTab;
	private LinearLayout ll_xuanxiang, layout_2, layout_1;
	File[] files;
	GridView list;
	private AlertDialog mDeleteDialog;
	ImageBrowserAdapter adapter;
	int screenWidth, screenHeight;
	Bitmap mTempBitmap;
	int length;
	int selectedItem;
	private ImageView mclear;
	TextView deviceSpinner;
	TextView dateSpinner;
	RecordListFragment rlFrag;
	LoadingFragment loadFrag;
	FaultFragment faultFrag;
	Contact contact;
	boolean receiverIsReg = false;
	private LayoutInflater inf = null;
	PopupWindow devicepopMenu, datepopMenu;
	VideoContactAdapter va;
	VideoDateAdapter da;
	NormalDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		inf = inflater;
		View view = inflater.inflate(R.layout.fragment_image, container, false);
		initComponent(view);
		if (null == files) {
			files = new File[0];
		}
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		adapter = new ImageBrowserAdapter(ImageFrag.this);
		list.setAdapter(adapter);
		View emptyView = inflater.inflate(R.layout.gridview_my_album_nodata,
				null);
		emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		emptyView.setVisibility(View.GONE);
		((ViewGroup) list.getParent()).addView(emptyView);
		list.setEmptyView(emptyView);

		return view;

	}

	public void initComponent(View view) {
		mclear = (ImageView) view.findViewById(R.id.delallimg);
		myPhotoTab = (TextView) view.findViewById(R.id.tab_myphoto);
		myVideoTab = (TextView) view.findViewById(R.id.tab_myvideo);
		ll_xuanxiang = (LinearLayout) view.findViewById(R.id.layout_xuanxiang);
		layout_1 = (LinearLayout) view.findViewById(R.id.layout_1);
		layout_2 = (LinearLayout) view.findViewById(R.id.layout_2);
		list = (GridView) view.findViewById(R.id.list_grid);
		myPhotoTab.setOnClickListener(this);
		myVideoTab.setOnClickListener(this);
		mclear.setOnClickListener(this);
		regFilter();
	}

	public void regFilter() {
		if (!receiverIsReg) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.Action.REFRESH_CONTANTS);
			filter.addAction(Constants.P2P.ACK_RET_GET_PLAYBACK_FILES);
			filter.addAction(Constants.P2P.RET_GET_PLAYBACK_FILES);

			getActivity().registerReceiver(mReceiver, filter);
			receiverIsReg = true;
		}

	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {

				if (intent.getAction()
						.equals(Constants.Action.REFRESH_CONTANTS)) {

				} else if (intent.getAction().equals(
						Constants.P2P.RET_GET_PLAYBACK_FILES)) {
					if (null != dialog && dialog.isShowing()) {
						Log.e("my", "isShowing");
						dialog.dismiss();
					}
					if (null == rlFrag) {
						rlFrag = new RecordListFragment();
						rlFrag.setUser(contact);
					}
					if (vfiles != null) {
						
						String[] vfiles2 = (String[]) intent
								.getCharSequenceArrayExtra("recordList");
						ArrayList<String> al = new ArrayList<String>();
						for (int i = 0; i < vfiles.length; i++) {
							al.add(vfiles[i]);
						}
						if (al.size() > 0 && vfiles2.length > 0) {

							for (int j = 0; j < vfiles2.length; j++) {

								boolean bl = false;
								for (int j2 = 0; j2 < al.size(); j2++) {
									if (al.get(j2).equals(vfiles2[j])) {
										bl = true;
										break;
									}
								}
								if (!bl) {
									al.add(vfiles2[j]);
								}

							}

						}
						String[] revfiles = new String[al.size()];
						al.toArray(revfiles);
						vfiles = revfiles;

					} else {
						vfiles = (String[]) intent
								.getCharSequenceArrayExtra("recordList");
					}
					
					bindDateVal();
				} else if (intent.getAction().equals(
						Constants.P2P.ACK_RET_GET_PLAYBACK_FILES)) {

					int result = intent.getIntExtra("result", -1);
					if (result != Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS) {
						if (null == faultFrag) {
							faultFrag = new FaultFragment();
						}
						if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
							T.showShort(getActivity(), R.string.password_error);
						} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
							Log.e("my", "net error resend:set npc time");
							faultFrag.setErrorText(getResources().getString(
									R.string.net_error));

							da.getFooterView().setVisibility(View.GONE);

						} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_INSUFFICIENT_PERMISSIONS) {
							// T.showShort(getActivity(),
							// R.string.insufficient_permissions);
							faultFrag.setErrorText(getResources().getString(
									R.string.insufficient_permissions));
						}

						replaceFrag(faultFrag, "faultFrag");

					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private void bindDev() {
		FList.getInstance().searchLocalDevice();
		FList.getInstance().updateOnlineState();

		GetDev();
	}

	private void disableDate() {
		ColorStateList csl = (ColorStateList) getActivity().getResources()
				.getColorStateList(R.color.gray);
		dateSpinner.setTextColor(csl);
		dateSpinner.setText(getActivity().getResources().getString(
				R.string.no_vedio_tip));

	}

	private static Date dtStart;

	@SuppressWarnings("deprecation")
	private void bindDate(Object val) {
		disableDate();
		contact = FList.getInstance().isContact(val.toString());
		Date dt = new Date();
		dtStart = new Date();
		dtStart.setYear(dt.getYear() - 1);
		P2PHandler.getInstance().getRecordFiles(contact.contactId,
				contact.contactPassword, dtStart, dt);
		
		loadFrag = new LoadingFragment();
		this.replaceFrag(loadFrag, "loadingFrag");
	}

	public String[] vfiles = null;

	private void bindDateVal() {

		if (null == rlFrag) {
			rlFrag = new RecordListFragment();

		}
		if (vfiles != null && vfiles.length > 0) {
			String[] datesStrings = GetDateFromRecords(vfiles);
			ColorStateList csl = (ColorStateList) getActivity().getResources()
					.getColorStateList(R.color.black);
			dateSpinner.setTextColor(csl);
			dateSpinner.setText(datesStrings[0]);
			if (da != null) {
				
				da.setDatesStrings(datesStrings);
				da.notifyDataSetChanged();

			}

			rlFrag.setUser(contact);
			rlFrag.setList(GetRecordsByDate(datesStrings[0]));

			rlFrag.setUser(contact);
			replaceFrag(rlFrag, "recordFrag");
		} else {
			disableDate();

			rlFrag.setList(GetRecordsByDate("不可用"));
			rlFrag.setUser(contact);
			replaceFrag(rlFrag, "recordFrag");
		}

	}

	public void replaceFrag(Fragment fragment, String mark) {
		try {
			FragmentManager manager = getActivity().getSupportFragmentManager();

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

	private void GetDev() {

		List<Contact> list = FList.getInstance().list();
		Boolean haslistonline = false;
		for (Contact c : list) {
			if (c.onLineState == Constants.DeviceState.ONLINE) {
				contact = c;
				ColorStateList csl = (ColorStateList) getActivity()
						.getResources().getColorStateList(R.color.dialog_title);
				deviceSpinner.setTextColor(csl);
				deviceSpinner.setText(contact.contactName);
				haslistonline = true;
				bindDate(contact.contactId);
				break;
			}
		}
		if (!haslistonline) {
			ColorStateList csl = (ColorStateList) getActivity().getResources()
					.getColorStateList(R.color.gray);
			deviceSpinner.setTextColor(csl);
			deviceSpinner.setText(getActivity().getResources().getString(
					R.string.sp_enable));

		}
	}

	public void SetSelectedDev(Contact c) {

		if (c.onLineState == Constants.DeviceState.ONLINE) {
			devicepopMenu.dismiss();
			contact = c;
			if (contact.onLineState == Constants.DeviceState.ONLINE) {
				ColorStateList csl = (ColorStateList) getActivity()
						.getResources().getColorStateList(R.color.dialog_title);
				deviceSpinner.setTextColor(csl);
				bindDate(contact.contactId);
			} else {

				ColorStateList csl = (ColorStateList) getActivity()
						.getResources().getColorStateList(R.color.gray);
				deviceSpinner.setTextColor(csl);
			}
			deviceSpinner.setText(contact.contactName);
		}

	}

	public void SetSelectedDate(String date) {

		datepopMenu.dismiss();
		ColorStateList csl = (ColorStateList) getActivity().getResources()
				.getColorStateList(R.color.black);
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
		// if (da != null) {
		// String[] dates = da.getDatesStrings();
		// for (int i = 0; i < dates.length; i++) {
		// list.add(dates[i]);
		// }
		// }

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

	public String getLastItem() {
		if (vfiles.length > 0) {
			String lastTime = vfiles[vfiles.length - 1].substring(6, 22);
			lastTime = lastTime.replace("_", " ");
			Log.e("343", "lastTime==" + lastTime);
			return lastTime;
		} else {
			return "";
		}
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
		
		int id=v.getId();
		if(id==R.id.spinner1){
			va = new VideoContactAdapter(getActivity(), ImageFrag.this);
			lvListView.setAdapter(va);
			devicepopMenu = p;
		}else if(id==R.id.spinner2){
			String[] datesStrings = new String[] {};
			if (vfiles != null && vfiles.length > 0) {
				datesStrings = GetDateFromRecords(vfiles);
			}
			da = new VideoDateAdapter(getActivity(), datesStrings,
					ImageFrag.this);
			for (int i = 0; i < datesStrings.length; i++) {
				Log.e("343", "看看这个日期==" + datesStrings[i]);
			}

			View fv = getActivity().getLayoutInflater().inflate(
					R.layout.video_spinner_deviceitem, null);
			fv.setVisibility(View.GONE);
			TextView tv = (TextView) fv.findViewById(R.id.time_group_title);
			tv.setText(getResources().getString(R.string.date_more));
			tv.setCompoundDrawables(null, null, null, null);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (null != dialog && dialog.isShowing()) {
						Log.e("my", "isShowing");
						return;
					}
					dialog = new NormalDialog(getActivity());

					dialog.setTitle(getActivity().getResources().getString(
							R.string.loading));
					dialog.showLoadingDialog();
					dialog.setCanceledOnTouchOutside(false);

					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");

					Date nextStartTime = dtStart;

					// String lasttime = v.getTag() + "";
					String lasttime = getLastItem();
					if (lasttime == null || nextStartTime == null) {
						return;
					}
					Date nextEndTime;
					try {
						nextEndTime = sdf.parse(lasttime);
						if (nextEndTime == null || nextEndTime.equals("")
								|| nextStartTime == null
								|| nextStartTime.equals("")) {
							return;
						}
						P2PHandler.getInstance().getRecordFiles(
								contact.contactId, contact.contactPassword,
								nextStartTime, nextEndTime);
						Log.e("343", nextStartTime.toString());
						Log.e("343", nextEndTime.toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			da.setFooterView(fv);
			lvListView.addFooterView(fv);
			lvListView.setAdapter(da);
			datepopMenu = p;
		}
		
		
//			switch (v.getId()) {
//			case R.id.spinner1:
//				va = new VideoContactAdapter(getActivity(), ImageFrag.this);
//				lvListView.setAdapter(va);
//				devicepopMenu = p;
//				break;
//			case R.id.spinner2:
//				String[] datesStrings = new String[] {};
//				if (vfiles != null && vfiles.length > 0) {
//					datesStrings = GetDateFromRecords(vfiles);
//				}
//				da = new VideoDateAdapter(getActivity(), datesStrings,
//						ImageFrag.this);
//				for (int i = 0; i < datesStrings.length; i++) {
//					Log.e("343", "看看这个日期==" + datesStrings[i]);
//				}
//
//				View fv = getActivity().getLayoutInflater().inflate(
//						R.layout.video_spinner_deviceitem, null);
//				fv.setVisibility(View.GONE);
//				TextView tv = (TextView) fv.findViewById(R.id.time_group_title);
//				tv.setText(getResources().getString(R.string.date_more));
//				tv.setCompoundDrawables(null, null, null, null);
//				tv.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						if (null != dialog && dialog.isShowing()) {
//							Log.e("my", "isShowing");
//							return;
//						}
//						dialog = new NormalDialog(getActivity());
//
//						dialog.setTitle(getActivity().getResources().getString(
//								R.string.loading));
//						dialog.showLoadingDialog();
//						dialog.setCanceledOnTouchOutside(false);
//
//						SimpleDateFormat sdf = new SimpleDateFormat(
//								"yyyy-MM-dd HH:mm");
//
//						Date nextStartTime = dtStart;
//
//						// String lasttime = v.getTag() + "";
//						String lasttime = getLastItem();
//						if (lasttime == null || nextStartTime == null) {
//							return;
//						}
//						Date nextEndTime;
//						try {
//							nextEndTime = sdf.parse(lasttime);
//							if (nextEndTime == null || nextEndTime.equals("")
//									|| nextStartTime == null
//									|| nextStartTime.equals("")) {
//								return;
//							}
//							P2PHandler.getInstance().getRecordFiles(
//									contact.contactId, contact.contactPassword,
//									nextStartTime, nextEndTime);
//							Log.e("343", nextStartTime.toString());
//							Log.e("343", nextEndTime.toString());
//						} catch (ParseException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//				});
//				da.setFooterView(fv);
//				lvListView.addFooterView(fv);
//				lvListView.setAdapter(da);
//				datepopMenu = p;
//
//				break;
//			}
//
//			return;
//		}
//		p.showAsDropDown((View) v.getParent());
//
//		switch (v.getId()) {
//		case R.id.spinner1:
//			va.ChangeData();
//			break;
//		case R.id.spinner2:
//			String[] datesStrings = new String[] {};
//			if (vfiles != null && vfiles.length > 0) {
//				datesStrings = GetDateFromRecords(vfiles);
//			}
//			da.ChangeData(datesStrings);
//			break;
		}

	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.tab_myphoto){
			myPhotoTab.setTextColor(getResources().getColor(R.color.white));
			myPhotoTab.setBackgroundResource(R.drawable.sharecf_btnbg_border);
			myVideoTab.setTextColor(getResources().getColor(R.color.black));
			myVideoTab.setBackgroundDrawable(null);
			ll_xuanxiang.setVisibility(View.GONE);
			layout_1.setVisibility(View.VISIBLE);
			layout_2.setVisibility(View.GONE);
			mclear.setVisibility(View.VISIBLE);
		}else if(id==R.id.tab_myvideo){
			vfiles = null;
			if (da != null) {
				da.notifyDataSetChanged();
			}
			myVideoTab.setTextColor(getResources().getColor(R.color.white));
			myVideoTab.setBackgroundResource(R.drawable.sharecf_btnbg_border);
			myPhotoTab.setTextColor(getResources().getColor(R.color.black));
			myPhotoTab.setBackgroundDrawable(null);
			ll_xuanxiang.setVisibility(View.VISIBLE);
			layout_1.setVisibility(View.GONE);
			layout_2.setVisibility(View.VISIBLE);
			mclear.setVisibility(View.GONE);
			deviceSpinner = (TextView) this.getActivity().findViewById(
					R.id.spinner1);
			dateSpinner = (TextView) this.getActivity().findViewById(
					R.id.spinner2);
			deviceSpinner.setOnClickListener(this);
			dateSpinner.setOnClickListener(this);
			bindDev();
		}else if(id==R.id.spinner1){
			showPopMenu(devicepopMenu, v);
		}else if(id==R.id.spinner2){
			showPopMenu(datepopMenu, v);
		}else if(id==R.id.delallimg){
			NormalDialog dialog = new NormalDialog(getActivity(), getActivity()
					.getResources().getString(R.string.delete_screenshot),
					getActivity().getResources().getString(
							R.string.confirm_clear), getActivity()
							.getResources().getString(R.string.clear),
					getActivity().getResources().getString(R.string.cancel));
			dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

				@Override
				public void onClick() {

					File[] files = adapter.getAllScreenShot();
					for (File f : files) {
						f.delete();
					}
					adapter.updateData();
					adapter.notifyDataSetChanged();
				}
			});
			dialog.showDialog();
		}
//		switch (v.getId()) {
//		case R.id.tab_myphoto:
//			myPhotoTab.setTextColor(getResources().getColor(R.color.white));
//			myPhotoTab.setBackgroundResource(R.drawable.sharecf_btnbg_border);
//			myVideoTab.setTextColor(getResources().getColor(R.color.black));
//			myVideoTab.setBackgroundDrawable(null);
//			ll_xuanxiang.setVisibility(View.GONE);
//			layout_1.setVisibility(View.VISIBLE);
//			layout_2.setVisibility(View.GONE);
//			mclear.setVisibility(View.VISIBLE);
//			break;
//		case R.id.tab_myvideo:
//			vfiles = null;
//			if (da != null) {
//				da.notifyDataSetChanged();
//			}
//			myVideoTab.setTextColor(getResources().getColor(R.color.white));
//			myVideoTab.setBackgroundResource(R.drawable.sharecf_btnbg_border);
//			myPhotoTab.setTextColor(getResources().getColor(R.color.black));
//			myPhotoTab.setBackgroundDrawable(null);
//			ll_xuanxiang.setVisibility(View.VISIBLE);
//			layout_1.setVisibility(View.GONE);
//			layout_2.setVisibility(View.VISIBLE);
//			mclear.setVisibility(View.GONE);
//			deviceSpinner = (TextView) this.getActivity().findViewById(
//					R.id.spinner1);
//			dateSpinner = (TextView) this.getActivity().findViewById(
//					R.id.spinner2);
//			deviceSpinner.setOnClickListener(this);
//			dateSpinner.setOnClickListener(this);
//			bindDev();
//
//			break;
//		case R.id.spinner1:
//			// vfiles = null;
//
//			showPopMenu(devicepopMenu, v);
//			break;
//		case R.id.spinner2:
//			showPopMenu(datepopMenu, v);
//			break;
//		case R.id.delallimg:
//			NormalDialog dialog = new NormalDialog(getActivity(), getActivity()
//					.getResources().getString(R.string.delete_screenshot),
//					getActivity().getResources().getString(
//							R.string.confirm_clear), getActivity()
//							.getResources().getString(R.string.clear),
//					getActivity().getResources().getString(R.string.cancel));
//			dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
//
//				@Override
//				public void onClick() {
//
//					File[] files = adapter.getAllScreenShot();
//					for (File f : files) {
//						f.delete();
//					}
//					adapter.updateData();
//					adapter.notifyDataSetChanged();
//				}
//			});
//			dialog.showDialog();
//			break;
//		}
	}

	
	public void createGalleryDialog(final int position) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_gallery, null);
		String screenshotPath = Environment.getExternalStorageDirectory()
				.getPath() + "/screenshot";
		File file = new File(screenshotPath);
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {

				if (pathname.getName().endsWith(".jpg")) {
					return true;
				} else {
					return false;
				}

			}
		};
		files = file.listFiles(filter);

		String path = ((File) files[position]).getPath();
		mTempBitmap = BitmapFactory.decodeFile(path);
		selectedItem = position;
		final ImageSwitcher switcher = (ImageSwitcher) view
				.findViewById(R.id.img_container);

		switcher.setFactory(new ViewFactory() {

			@Override
			public View makeView() {

				ImageView view = new ImageView(getActivity());
				view.setScaleType(ScaleType.FIT_CENTER);
				view.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				Log.e("my", Runtime.getRuntime().totalMemory() + "");
				return view;
			}

		});
		switcher.setImageDrawable(new BitmapDrawable(mTempBitmap));
		final GestureDetector gd = new GestureDetector(new OnGestureListener() {

			@Override
			public boolean onDown(MotionEvent arg0) {

				return false;
			}

			@Override
			public boolean onFling(MotionEvent arg0, MotionEvent arg1,
					float arg2, float arg3) {

				float x1 = arg0.getRawX();
				float x2 = arg1.getRawX();
				float distance = x1 - x2;
				if ((distance > 0) && (Math.abs(distance) > 30)) {
					if (++selectedItem < files.length) {
						switcher.setInAnimation(AnimationUtils.loadAnimation(
								getActivity(), R.anim.slide_in_right_100));
						switcher.setOutAnimation(AnimationUtils.loadAnimation(
								getActivity(), R.anim.slide_out_left_100));

						String path = ((File) files[selectedItem]).getPath();
						mTempBitmap = BitmapFactory.decodeFile(path);
						switcher.setImageDrawable(new BitmapDrawable(
								mTempBitmap));
					} else {
						selectedItem = files.length - 1;
					}

					Log.e("my", Runtime.getRuntime().totalMemory() + "");
				} else if ((distance < 0) && (Math.abs(distance) > 30)) {
					if (--selectedItem >= 0) {
						switcher.setInAnimation(AnimationUtils.loadAnimation(
								getActivity(), R.anim.slide_in_left_100));
						switcher.setOutAnimation(AnimationUtils.loadAnimation(
								getActivity(), R.anim.slide_out_right_100));
						String path = ((File) files[selectedItem]).getPath();
						mTempBitmap = BitmapFactory.decodeFile(path);
						switcher.setImageDrawable(new BitmapDrawable(
								mTempBitmap));
					} else {
						selectedItem = 0;
					}

					Log.e("my", Runtime.getRuntime().totalMemory() + "");
				}
				return true;
			}

			@Override
			public void onLongPress(MotionEvent arg0) {

			}

			@Override
			public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
					float arg2, float arg3) {

				return false;
			}

			@Override
			public void onShowPress(MotionEvent arg0) {

			}

			@Override
			public boolean onSingleTapUp(MotionEvent arg0) {

				return false;
			}

		});
		switcher.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				gd.onTouchEvent(arg1);
				return true;
			}

		});

		// gallery.setOnTouchListener(new OnTouchListener(){
		//
		// @Override
		// public boolean onTouch(View arg0, MotionEvent arg1) {
		//
		// switcher.setInAnimation(AnimationUtils.loadAnimation(context,
		// android.R.anim.fade_in));
		// switcher.setOutAnimation(AnimationUtils.loadAnimation(context,
		// android.R.anim.fade_out));
		// return false;
		// }
		//
		// });
		// gallery.setOnItemSelectedListener(new OnItemSelectedListener(){
		//
		// @Override
		// public void onItemSelected(AdapterView<?> arg0, View img,
		// int arg2, long arg3) {
		//
		// String path = ((File)files[arg2]).getPath();
		// if(bitmap!=null&&!bitmap.isRecycled()){
		// //bitmap.recycle();
		// bitmap = BitmapFactory.decodeFile(path);
		// Log.e("my",Runtime.getRuntime().totalMemory()+"");
		// }
		// selectedItem = arg2;
		// switcher.setImageDrawable(new BitmapDrawable(bitmap));
		// gAdapter.setSelectedItem(arg2);
		//
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {
		//
		// Log.e("my","onNothingSelected");
		// }
		//
		// });
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		mDeleteDialog = builder.create();
		mDeleteDialog.show();
		mDeleteDialog.setContentView(view);
		FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) view
				.getLayoutParams();
		params.width = screenWidth;
		params.height = screenHeight;
		view.setLayoutParams(params);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (receiverIsReg) {
			receiverIsReg = false;
			getActivity().unregisterReceiver(mReceiver);
		}

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!receiverIsReg) {
			regFilter();
		}
	}

}
