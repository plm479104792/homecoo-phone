package et.song.remotestar;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import et.song.etclass.ETSave;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.jni.ble.ETBleClient;
import et.song.remotestar.hxd.sdk.R;
import et.song.tg.face.IFinish;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class FragmentBle extends SherlockFragment implements OnClickListener,
		IBack {
	private ListView mBtList;
	private ProgressDialog mProgressDialog = null;
	private ProgressBar mProgressLoading = null;

	private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;

//	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 10000;
	private String BLE_NAME = "BLE LBIR";

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_bt, container, false);

		Button button_search = (Button) view
				.findViewById(R.id.button_bt_search);
		button_search.setOnClickListener(this);

		mProgressLoading = (ProgressBar) view
				.findViewById(R.id.progress_loading);
		mProgressLoading.setVisibility(View.GONE);
		mBtList = (ListView) view.findViewById(R.id.list_bt);
		mBtList.setOnItemClickListener(mDeviceClickListener);
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage(getResources().getString(
				R.string.str_bt_loading));
		mProgressDialog.setCanceledOnTouchOutside(true);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		getActivity().registerReceiver(mReceiver, filter);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		// if (!mBluetoothAdapter.isEnabled()) {
		// Intent enableBtIntent = new Intent(
		// BluetoothAdapter.ACTION_REQUEST_ENABLE);
		// startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		// }

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableIntent);
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		mBtList.setAdapter(mLeDeviceListAdapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Nothing to see here.
		menu.clear();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Back();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(
				false);
	}

	@Override
	public void onPause() {
		super.onPause();
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if (id==R.id.button_bt_search) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.cancel();
			}
			mProgressDialog.show();
			mLeDeviceListAdapter.clear();
			scanLeDevice(true);
		}
		
//		switch (v.getId()) {
//		case R.id.button_bt_search:
//			if (mProgressDialog.isShowing()) {
//				mProgressDialog.cancel();
//			}
//			mProgressDialog.show();
//			mLeDeviceListAdapter.clear();
//			scanLeDevice(true);
//			break;
//		}
	}

	public void bleOpen(String btAddress) {
		ETGlobal.mTg = new ETBleClient(getActivity(), btAddress);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ETGlobal.mTg.open(new IFinish() {
						@Override
						public void OpenCallbk(int arg0) {
							// TODO Auto-generated method stub
							mHandler.sendEmptyMessage(0);
							Intent intentMsgin = new Intent(
									ETGlobal.BROADCAST_OPEN_FINISH);
							if (arg0 < 0) {
								intentMsgin.putExtra("state", "faile");
								getActivity().sendBroadcast(intentMsgin);
								ETGlobal.mTg = null;
							} else {
								intentMsgin.putExtra("state", "success");
								getActivity().sendBroadcast(intentMsgin);
								FragmentGroup fragmentGroup = new FragmentGroup();
								FragmentTransaction transaction = getActivity()
										.getSupportFragmentManager()
										.beginTransaction();
								transaction.replace(R.id.fragment_container,
										fragmentGroup);
								transaction.addToBackStack(null);
								transaction.commit();
							}
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		@SuppressWarnings("deprecation")
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			final BluetoothDevice device = mLeDeviceListAdapter.getDevice(arg2);
			if (device == null)
				return;
			if (mScanning) {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				mScanning = false;
			}
			try {
				if (ETGlobal.mTg != null) {
					ETGlobal.mTg.close();
					ETGlobal.mTg = null;
				}
				// mProgressLoading.setVisibility(View.VISIBLE);
				getSherlockActivity().setSupportProgress(Window.PROGRESS_END);
				getSherlockActivity()
						.setSupportProgressBarIndeterminateVisibility(true);
				bleOpen(device.getAddress());
				ETSave.getInstance(getActivity()).put("comType", "ble");
				ETSave.getInstance(getActivity()).put("ble_address",
						device.getAddress());
			} catch (Exception e) {
				// mProgressLoading.setVisibility(View.GONE);
				getSherlockActivity()
						.setSupportProgressBarIndeterminateVisibility(false);
			}
		}
	};

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// mProgressLoading.setVisibility(View.GONE);
				getSherlockActivity()
						.setSupportProgressBarIndeterminateVisibility(false);
				break;
			case 1:
				if (mProgressDialog.isShowing()) {
					mProgressDialog.cancel();
				}
				break;
			}

		}
	};

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
				Back();
			}
		}
	};

	@SuppressWarnings("deprecation")
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mHandler.sendEmptyMessage(1);
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mHandler.sendEmptyMessage(1);

		}
	}

	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = getActivity().getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0
					&& deviceName.trim().equals(BLE_NAME))
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());

			return view;
		}

	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();
				}
			});
		}

	};

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentCom fragmentCom = new FragmentCom();
		FragmentTransaction transactionCom = getActivity()
				.getSupportFragmentManager().beginTransaction();
		transactionCom
				.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out,
						R.anim.push_left_in, R.anim.push_left_out);

		transactionCom.replace(R.id.fragment_container, fragmentCom);
		// transactionBt.addToBackStack(null);
		transactionCom.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transactionCom.commit();

	}

}
