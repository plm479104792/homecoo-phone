package et.song.remotestar;



import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDeviceCustom;
import et.song.etclass.ETGroup;
import et.song.etclass.ETKey;
import et.song.etclass.ETPage;
import et.song.etclass.ETSave;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remote.face.IRKeyValue;
import et.song.remotestar.hxd.sdk.R;
import et.song.tool.ETTool;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
public class FragmentCustom extends SherlockFragment implements OnClickListener,
		OnLongClickListener, IBack {
	private int mGroupIndex;
	private int mDeviceIndex;
	private ETGroup mGroup = null;
	private ETDeviceCustom mDevice = null;
	private RecvReceiver mReceiver;
	private boolean mIsModity = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mGroupIndex = this.getArguments().getInt("group");
		mDeviceIndex = this.getArguments().getInt("device");
		ETSave.getInstance(getActivity()).put("GroupIndex", String.valueOf(mGroupIndex));
		ETSave.getInstance(getActivity()).put("DeviceIndex", String.valueOf(mDeviceIndex));
		ETSave.getInstance(getActivity()).put("DeviceType", String.valueOf(DeviceType.DEVICE_REMOTE_CUSTOM));
		mGroup = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = (ETDeviceCustom) mGroup.GetItem(mDeviceIndex);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_custom, container, false);
		RelativeLayout content = (RelativeLayout) view
				.findViewById(R.id.content);
		ETKey key = null;
		for (int i = 0; i < mDevice.GetCount(); i++){
			View button_custom = null;
			key = (ETKey)mDevice.GetItem(i);
			RelativeLayout.LayoutParams viewParams = null;
			viewParams = new RelativeLayout.LayoutParams(
					(ETGlobal.W - 80) / 5, (ETGlobal.W - 80) / 7);
			if (key.GetRes() != -1){
				button_custom = new ImageView(getActivity());
				button_custom.setOnClickListener(this);
				button_custom.setOnLongClickListener(this);
				button_custom.setBackgroundResource(R.drawable.btn_style_green);
				((ImageView)button_custom).setImageResource(ETGlobal.mAddButtonImages[key.GetRes()]);
				button_custom.setTag(key.GetKey());
//				button_custom.setText(ETHtml.GetSpanned(getActivity(),
//						"<img src='" + ETGlobal.mAddButtonImages[key.GetRes()] + "'/>"));
			}
			else {
				button_custom = new TextView(getActivity());
				button_custom.setOnClickListener(this);
				button_custom.setOnLongClickListener(this);
				button_custom.setBackgroundResource(R.drawable.btn_style_green);
				((TextView)button_custom).setTextColor(Color.WHITE);
				((TextView)button_custom).setGravity(Gravity.CENTER);
				button_custom.setTag(key.GetKey());
				((TextView)button_custom).setText(key.GetName());
			}
			viewParams.leftMargin = (int)key.GetX();
			viewParams.topMargin = (int)key.GetY();
			content.addView(button_custom, viewParams);
		}


		
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETGlobal.BROADCAST_PASS_LEARN);
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		getActivity().registerReceiver(mReceiver, filter);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Nothing to see here.
		menu.clear();
		inflater.inflate(R.menu.menu_edit, menu);
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("Home", "Home");
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			Back();
//			return true;
//		case R.id.menu_edit:
//			if (item.isChecked()) {
//				item.setChecked(false);
//				mIsModity = false;
//			} else {
//				item.setChecked(true);
//				mIsModity = true;
//				//ETTool.MessageBox(getActivity(), 0.5f, getString(R.string.str_study_start_info_2), true);
//			}
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("InflateParams")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		byte[] keyValue = null;
		int key = ((Integer)v.getTag()).intValue();
		boolean isSend = true;
		try {
			if (key == 0)
				return;
			keyValue = mDevice.GetKeyValue(key);
			if (keyValue == null)
				return;
			if (ETGlobal.mTg == null) {
				isSend = false;
			}
			int n = ETGlobal.mTg.write(keyValue, keyValue.length);
			if (n < 0) {
				isSend = false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!isSend) {
			Dialog alertDialog = new AlertDialog.Builder(getActivity())
					.setMessage(R.string.str_study_start_info_6)
					.setIcon(R.drawable.ic_launcher)
					.setNegativeButton(R.string.str_buy_no,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated
									Intent intent = new Intent(
											ETGlobal.BROADCAST_APP_BUY_NO);
									getActivity().sendBroadcast(
											intent);
								}
							})
					.setPositiveButton(R.string.str_buy_yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											ETGlobal.BROADCAST_APP_BUY_YES);
									getActivity().sendBroadcast(
											intent);
								}
							}).create();
			alertDialog.show();
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		int key = ((Integer)v.getTag()).intValue();
		if (mIsModity) {
			final int k = key;
			Dialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setMessage(R.string.str_study_info_1)
					.setPositiveButton(R.string.str_other_yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Intent intentStartLearn = new Intent(
											ETGlobal.BROADCAST_START_LEARN);
									intentStartLearn.putExtra("select", "0");
									intentStartLearn.putExtra("key", k);
									getActivity().sendBroadcast(
											intentStartLearn);
								}
							})
					.setNegativeButton(R.string.str_other_no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();

			dialog.setTitle(R.string.str_dialog_set_study);
			dialog.show();
		} else {

		}
		return false;
	}

	

	public class RecvReceiver extends BroadcastReceiver {
		@SuppressLint({ "InlinedApi", "NewApi" })
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ETGlobal.BROADCAST_PASS_LEARN)) {
				try {
					Log.i("Recv", "Recv");
					String select = intent.getStringExtra("select");
					int key = intent.getIntExtra("key", 0);
					String msg = intent.getStringExtra("msg");
					Log.i("Key",
							String.valueOf(ETTool.HexStringToBytes(msg).length));
					if (select.equals("0")) {
						ETKey k = mDevice.GetKeyByValue(key);
						if (k != null) {
							k.SetState(ETKey.ETKEY_STATE_STUDY);
							k.SetValue(ETTool.HexStringToBytes(msg));
							k.Update(ETDB.getInstance(getActivity()));
						}
					} else if (select.equals("1")) {

					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (action.equals(ETGlobal.BROADCAST_KEYCODE_VOLUME_DOWN)) {
				try {
					byte[] keyValue = mDevice
							.GetKeyValue(IRKeyValue.KEY_TV_VOLUME_OUT);
					if (keyValue == null)
						return;
					ETGlobal.mTg.write(keyValue, keyValue.length);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ETGlobal.BROADCAST_KEYCODE_VOLUME_UP)) {
				try {

					byte[] keyValue = mDevice
							.GetKeyValue(IRKeyValue.KEY_TV_VOLUME_IN);
					if (keyValue == null)
						return;
					ETGlobal.mTg.write(keyValue, keyValue.length);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (action.equals(ETGlobal.BROADCAST_APP_BACK)){
				Back();
			}
		}
	}



	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentDevice fragmentDevice = new FragmentDevice();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("group", mGroupIndex);
		fragmentDevice.setArguments(args);
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragmentDevice);
		// transactionBt.addToBackStack(null);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	}

}
