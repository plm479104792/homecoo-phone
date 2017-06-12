package et.song.remotestar;

import java.util.Locale;
import java.util.UUID;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import et.song.etclass.ETSave;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.jni.bt.ETBtClient;
import et.song.remotestar.hxd.sdk.R;
import et.song.tg.face.IFinish;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentBt extends SherlockFragment implements OnClickListener,
		IBack {
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mBtArrayAdapter;
	private ListView mBtList;
	private ProgressDialog mProgressDialog = null;
	private ProgressBar mProgressLoading = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBtAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableIntent);
		}
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
		mBtArrayAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.fragment_bt_list_item);
		mBtList.setAdapter(mBtArrayAdapter);
		mBtList.setOnItemClickListener(mDeviceClickListener);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		getActivity().registerReceiver(mReceiver, filter);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
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
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
//		int id=v.getId();
//		if (id== R.id.button_bt_search) {
//			if (mProgressDialog != null && mProgressDialog.isShowing()) {
//				mProgressDialog.cancel();
//			}
//			mProgressDialog = ProgressDialog.show(getActivity(),
//					getString(R.string.app_name),
//					getString(R.string.str_bt_loading), false);
//			doDiscovery();
//		}
		
//		switch (v.getId()) {
//		case R.id.button_bt_search:
//			if (mProgressDialog != null && mProgressDialog.isShowing()) {
//				mProgressDialog.cancel();
//			}
//			mProgressDialog = ProgressDialog.show(getActivity(),
//					getString(R.string.app_name),
//					getString(R.string.str_bt_loading), false);
//			doDiscovery();
//			break;
//		}
	}

	public void btOpen(final String btAddress) {
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}
		if (ETGlobal.mTg != null) {
			try {
				ETGlobal.mTg.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ETGlobal.mTg = null;
		}
		BluetoothDevice remoteDevice = BluetoothAdapter.getDefaultAdapter()
				.getRemoteDevice(btAddress);
		// if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
		// try {
		//
		// ETBtClsUtils.setPin(remoteDevice.getClass(), remoteDevice, "1234");
		// ETBtClsUtils.createBond(remoteDevice.getClass(), remoteDevice);
		// ETBtClsUtils.cancelPairingUserInput(remoteDevice.getClass(),
		// remoteDevice);
		// } catch (Exception e) {
		// e.printStackTrace();
		// } //
		//
		// } else {
		// try {
		//
		// ETBtClsUtils.setPin(remoteDevice.getClass(), remoteDevice, "1234");
		// ETBtClsUtils.createBond(remoteDevice.getClass(), remoteDevice);//
		// ������
		// ETBtClsUtils.cancelPairingUserInput(remoteDevice.getClass(),
		// remoteDevice);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		ETGlobal.mTg = new ETBtClient(remoteDevice,
				UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));

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
						
						ETSave.getInstance(getActivity()).put("comType",
								"bt");
						ETSave.getInstance(getActivity()).put("bt_address",
								btAddress);
						intentMsgin.putExtra("state", "success");
						getActivity().sendBroadcast(intentMsgin);
						FragmentGroup fragmentGroup = new FragmentGroup();
						FragmentTransaction transaction = getActivity()
								.getSupportFragmentManager().beginTransaction();
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

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			if (mBtAdapter.isDiscovering()) {
				mBtAdapter.cancelDiscovery();
			}
			String info = ((TextView) v).getText().toString();
			String[] strs = info.split("\n");
			if (strs[1] != null && strs[1].length() != 0) {
				try {

					// mProgressLoading.setVisibility(View.VISIBLE);
					getSherlockActivity().setSupportProgress(
							Window.PROGRESS_END);
					getSherlockActivity()
							.setSupportProgressBarIndeterminateVisibility(true);
					btOpen(strs[1]);
				} catch (Exception e) {
					// mProgressLoading.setVisibility(View.GONE);
					getSherlockActivity()
							.setSupportProgressBarIndeterminateVisibility(false);
				}

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
				mBtArrayAdapter.notifyDataSetChanged();
				break;
			}

		}
	};

	private void doDiscovery() {

		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}
		mBtArrayAdapter.clear();
		// Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		// if (pairedDevices.size() > 0) {
		// for (BluetoothDevice device : pairedDevices) {
		// mBtArrayAdapter.add(device.getName().trim() + "\n"
		// + device.getAddress().trim());
		// }
		// }
		mBtAdapter.startDiscovery();
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
				if (device.getName() != null && device.getAddress() != null) {
					if (device.getAddress().length() != 0
							&& device.getName().length() != 0) {
						String str = device.getName().trim() + "\n"
								+ device.getAddress().trim();
						if (mBtArrayAdapter.getPosition(str) < 0) {
							mBtArrayAdapter.add(str);
						}
						if (device.getName().trim()
								.toUpperCase(Locale.getDefault())
								.contains("HXD")) {
							if (mBtAdapter.isDiscovering()) {
								mBtAdapter.cancelDiscovery();
							}
							//btOpen(device.getAddress().trim());
						}
					}
				}
				// }
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				mProgressDialog.cancel();
				// getActivity().setProgressBarIndeterminateVisibility(false);
			} else if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
				Back();
			}
		}
	};

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentCom fragmentCom = new FragmentCom();
		FragmentTransaction transactionCom = getActivity()
				.getSupportFragmentManager().beginTransaction();
		// transactionCom.setCustomAnimations(R.anim.push_left_in,
		// R.anim.push_left_out, R.anim.push_left_in,
		// R.anim.push_left_out);

		transactionCom.replace(R.id.fragment_container, fragmentCom);
		// transactionBt.addToBackStack(null);
		// transactionCom
		// .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transactionCom.commit();
	}

}
