package com.homecoolink.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.homecoolink.P2PConnect;
import com.homecoolink.PlayBackActivity;
import com.homecoolink.R;
import com.homecoolink.activity.MainActivity;
import com.homecoolink.adapter.RecordAdapter;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.utils.Utils;
import com.p2p.core.MediaPlayer;
import com.p2p.core.P2PHandler;


public class RecordListFragment extends Fragment {
	Context mContext;
	GridView list_record;
	Contact contact;
	String[] names;
	AlertDialog load_record;
	View load_view;
	LayoutInflater inflater;
	boolean isDialogShowing = false;
	RecordAdapter adapter;
	List<String> list;
	boolean receiverIsReg = false;
	public View thisView;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.inflater = inflater;
		mContext = MainActivity.mContext;
//		mContext=MyApp.app;

		View view = thisView = inflater.inflate(R.layout.fragment_record,
				container, false);

		initComponent(view);
		View emptyView = inflater.inflate(R.layout.gridview_my_videos_nodata,
				null);
		emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		emptyView.setVisibility(View.GONE);
		((ViewGroup) list_record.getParent()).addView(emptyView);
		list_record.setEmptyView(emptyView);
		return view;
	}

	public void initComponent(View view) {
		list_record = (GridView) view.findViewById(R.id.video_grid);
		adapter = new RecordAdapter(mContext, list);
		adapter.contact = contact;
		list_record.setAdapter(adapter);
		list_record.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				String filename = adapter.getList().get(arg2);
				load_view = inflater.inflate(R.layout.dialog_load_record, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());

				load_record = builder.create();
				// load_record.setCancelable(false);
				load_record.show();
				isDialogShowing = true;
				load_record.setContentView(load_view);
				load_record.setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface arg0, int arg1,
							KeyEvent event) {

						if (event.getAction() == KeyEvent.ACTION_DOWN
								&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
							// createExitDialog(this);
							if (isDialogShowing) {

								load_record.cancel();
								isDialogShowing = false;
								P2PHandler.getInstance().reject();
							}
							return true;
						}
						return false;
					}

				});

				FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(
						Utils.dip2px(getActivity(), 222), Utils.dip2px(
								getActivity(), 130));
				load_view.setLayoutParams(layout);
				final AnimationDrawable anim;
				ImageView img = (ImageView) load_view
						.findViewById(R.id.load_record_img);

				anim = (AnimationDrawable) img.getDrawable();
				OnPreDrawListener opdl = new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						anim.start();
						return true;
					}

				};
				img.getViewTreeObserver().addOnPreDrawListener(opdl);

				P2PConnect.setCurrent_state(P2PConnect.P2P_STATE_CALLING);
				P2PConnect.setCurrent_call_id(contact.contactId);
				P2PHandler.getInstance().playbackConnect(contact.contactId,
						contact.contactPassword, filename, arg2);
			}

		});

		regFilter();

	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P.P2P_ACCEPT);
		filter.addAction(Constants.P2P.P2P_READY);
		filter.addAction(Constants.P2P.P2P_REJECT);
		getActivity().registerReceiver(mReceiver, filter);
		receiverIsReg = true;
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (intent.getAction().equals(Constants.P2P.P2P_ACCEPT)) {
					P2PHandler.getInstance().openAudioAndStartPlaying();
				} else if (intent.getAction().equals(Constants.P2P.P2P_READY)) {
					Intent intentCall = new Intent();

					intentCall.setClass(getActivity(), PlayBackActivity.class);

					intentCall.putExtra("type",
							Constants.P2P_TYPE.P2P_TYPE_PLAYBACK);
					intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intentCall);
					closeDialog();
				} else if (intent.getAction().equals(Constants.P2P.P2P_REJECT)) {
					closeDialog();
					P2PHandler.getInstance().reject();
				}
			} catch (Exception e) {
				// TODO: handle exception

			}

		}
	};

	@Override
	public void onDestroy() {
	
		super.onDestroy();
		if (receiverIsReg) {
			receiverIsReg = false;
			getActivity().unregisterReceiver(mReceiver);
		}

	}

	public void cancelDialog() {
		load_record.cancel();
		isDialogShowing = false;
		MediaPlayer.getInstance().native_p2p_hungup();
		Log.e("my", "hungup");
	}

	@Override
	public void onResume() {

		super.onResume();
		Log.e("my", "onResume");
	}

	@Override
	public void onStop() {
	
		super.onStop();
		if (receiverIsReg) {
			receiverIsReg = false;
			getActivity().unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!receiverIsReg) {
			regFilter();
		}
	}

	public void setList(List<String> list) {
		this.list = list;
		if (adapter != null) {

			adapter.list = list;
			adapter.notifyDataSetChanged();
		}
	}

	public List<String> getList() {
		return list;
	}

	public void setUser(Contact contact) {
		this.contact = contact;

	}

	public void closeDialog() {
		if (null != load_record) {
			load_record.cancel();
			isDialogShowing = false;
		}
	}

	public void scrollOn() {
		list_record.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return false;
			}

		});
	}

	public void scrollOff() {
		list_record.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return true;
			}

		});
	}

}
