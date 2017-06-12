package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.etclass.ETSave;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.jni.io.ETIO;
import et.song.jni.usb.ETUSB;
import et.song.remotestar.hxd.sdk.R;
import et.song.tg.face.IFinish;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FragmentCom extends SherlockFragment implements OnClickListener,
		IBack {
	private RecvReceiver mReceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getActivity().setTitle(R.string.str_com_select);
		View view = inflater.inflate(R.layout.fragment_com, container, false);

		Button buttonBT = (Button) view.findViewById(R.id.buttonBT);
		buttonBT.setOnClickListener(this);

		Button buttonBle = (Button) view.findViewById(R.id.buttonBle);
		buttonBle.setOnClickListener(this);

		Button buttonWifi = (Button) view.findViewById(R.id.buttonWifi);
		buttonWifi.setOnClickListener(this);

		
		Button buttonWifiDirect = (Button) view.findViewById(R.id.buttonWifiLan);
		buttonWifiDirect.setOnClickListener(this);
//		buttonWifiEx.setVisibility(View.GONE);
		Button buttonIO = (Button) view.findViewById(R.id.buttonIO);
		buttonIO.setOnClickListener(this);
		Button buttonUSB = (Button) view.findViewById(R.id.buttonUSB);
		buttonUSB.setOnClickListener(this);
		Button buttonSound = (Button) view.findViewById(R.id.buttonSound);
		buttonSound.setOnClickListener(this);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETUSB.BROADCAST_USB_PERMISSION);
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
		Log.i("Home", "Home");
		switch (item.getItemId()) {
		case android.R.id.home:
           System.out.println("点击了返回");
			Back();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
	}

	private void Open() {
		Log.i("Com", "Open");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ETGlobal.mTg.open(new IFinish() {
						@Override
						public void OpenCallbk(int arg0) {
							// TODO Auto-generated method stub
							Intent intentMsgin = new Intent(
									ETGlobal.BROADCAST_OPEN_FINISH);
							//02.21屏蔽打开串口
							arg0=1;
							
							if (arg0 < 0) {
								intentMsgin.putExtra("state", "faile");
								getActivity().sendBroadcast(intentMsgin);
								ETGlobal.mTg = null;
							} else {
								try {
									intentMsgin.putExtra("state", "success");
									getActivity().sendBroadcast(intentMsgin);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
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

	@SuppressLint("InlinedApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id=v.getId();
		if (id==R.id.buttonBT) {
			FragmentBt fragmentBt = new FragmentBt();
			FragmentTransaction transactionBt = getActivity()
					.getSupportFragmentManager().beginTransaction();
			transactionBt.setCustomAnimations(R.anim.push_left_in,
					R.anim.push_left_out, R.anim.push_left_in,
					R.anim.push_left_out);

			transactionBt.replace(R.id.fragment_container, fragmentBt);
			// transactionBt.addToBackStack(null);
			transactionBt
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transactionBt.commit();
		}else if (id==R.id.buttonBle) {
			if (!getActivity().getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_BLUETOOTH_LE)) {
				Toast.makeText(getActivity(), R.string.ble_not_supported,
						Toast.LENGTH_SHORT).show();
				return;
			}
			BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
					.getSystemService(Context.BLUETOOTH_SERVICE);
			BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
			if (bluetoothAdapter == null) {
				Toast.makeText(getActivity(),
						R.string.error_bluetooth_not_supported,
						Toast.LENGTH_SHORT).show();
				return;
			}
			FragmentBle fragmentBle = new FragmentBle();
			FragmentTransaction transactionBle = getActivity()
					.getSupportFragmentManager().beginTransaction();
			transactionBle.setCustomAnimations(R.anim.push_left_in,
					R.anim.push_left_out, R.anim.push_left_in,
					R.anim.push_left_out);

			transactionBle.replace(R.id.fragment_container, fragmentBle);
			// transactionBt.addToBackStack(null);
//			transactionBle
//					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transactionBle.commit();
		}else if (id==R.id.buttonWifi) {
			FragmentWifiWAN fragmentWifi = new FragmentWifiWAN();
			FragmentTransaction transactionWifi = getActivity()
					.getSupportFragmentManager().beginTransaction();
			transactionWifi.setCustomAnimations(R.anim.push_left_in,
					R.anim.push_left_out, R.anim.push_left_in,
					R.anim.push_left_out);
			transactionWifi.replace(R.id.fragment_container, fragmentWifi);
			// transactionWifi.addToBackStack(null);
			transactionWifi.commit();
		}else if (id==R.id.buttonWifiLan) {
			FragmentWifiLAN fragmentWifiLan = new FragmentWifiLAN();
			FragmentTransaction transactionWifiLan = getActivity()
					.getSupportFragmentManager().beginTransaction();
			transactionWifiLan.setCustomAnimations(R.anim.push_left_in,
					R.anim.push_left_out, R.anim.push_left_in,
					R.anim.push_left_out);
			transactionWifiLan.replace(R.id.fragment_container, fragmentWifiLan);
			// transactionWifi.addToBackStack(null);
			transactionWifiLan.commit();
		}else if (id==R.id.buttonIO) {
			try {
				if (ETGlobal.mTg != null) {
					ETGlobal.mTg.close();
					ETGlobal.mTg = null;
				}
				ETGlobal.mTg = new ETIO();
				Open();
				ETSave.getInstance(getActivity()).put(
						"comType", "io");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (id==R.id.buttonUSB) {
			if (!getActivity().getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_USB_HOST)) {
				Toast.makeText(getActivity(), R.string.usb_not_supported,
						Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				if (ETGlobal.mTg != null) {
					ETGlobal.mTg.close();
					ETGlobal.mTg = null;
				}
				if (ETUSB.hasPermission(getActivity(),
						ETUSB.getDevice(getActivity()))) {
					ETUSB.Init(getActivity());
					ETGlobal.mTg = new ETUSB(getActivity());
					Open();
					ETSave.getInstance(getActivity()).put(
							"comType", "usb");
				} else {
					ETUSB.Init(getActivity());
					ETGlobal.mTg = new ETUSB(getActivity());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(this.getActivity(), this.getString(R.string.str_error_open_usb), Toast.LENGTH_SHORT).show();
			}
		}else if (id==R.id.buttonSound) {
			try {
				if (ETGlobal.mTg != null) {
					ETGlobal.mTg.close();
					ETGlobal.mTg = null;
				}
				ETGlobal.mTg =  new ThreeManRemote(getActivity());
				Open();
				ETSave.getInstance(getActivity()).put(
						"comType", "sound");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
//		switch (v.getId()) {
//		case R.id.buttonBT:
//			FragmentBt fragmentBt = new FragmentBt();
//			FragmentTransaction transactionBt = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transactionBt.setCustomAnimations(R.anim.push_left_in,
//					R.anim.push_left_out, R.anim.push_left_in,
//					R.anim.push_left_out);
//
//			transactionBt.replace(R.id.fragment_container, fragmentBt);
//			// transactionBt.addToBackStack(null);
//			transactionBt
//					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			transactionBt.commit();
//			break;
//		case R.id.buttonBle:
//			if (!getActivity().getPackageManager().hasSystemFeature(
//					PackageManager.FEATURE_BLUETOOTH_LE)) {
//				Toast.makeText(getActivity(), R.string.ble_not_supported,
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//			BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
//					.getSystemService(Context.BLUETOOTH_SERVICE);
//			BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//			if (bluetoothAdapter == null) {
//				Toast.makeText(getActivity(),
//						R.string.error_bluetooth_not_supported,
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//			FragmentBle fragmentBle = new FragmentBle();
//			FragmentTransaction transactionBle = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transactionBle.setCustomAnimations(R.anim.push_left_in,
//					R.anim.push_left_out, R.anim.push_left_in,
//					R.anim.push_left_out);
//
//			transactionBle.replace(R.id.fragment_container, fragmentBle);
//			// transactionBt.addToBackStack(null);
////			transactionBle
////					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			transactionBle.commit();
//			break;
//		case R.id.buttonWifi:
//			FragmentWifiWAN fragmentWifi = new FragmentWifiWAN();
//			FragmentTransaction transactionWifi = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transactionWifi.setCustomAnimations(R.anim.push_left_in,
//					R.anim.push_left_out, R.anim.push_left_in,
//					R.anim.push_left_out);
//			transactionWifi.replace(R.id.fragment_container, fragmentWifi);
//			// transactionWifi.addToBackStack(null);
//			transactionWifi.commit();
//			break;
//		case R.id.buttonWifiLan:		
//			FragmentWifiLAN fragmentWifiLan = new FragmentWifiLAN();
//			FragmentTransaction transactionWifiLan = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transactionWifiLan.setCustomAnimations(R.anim.push_left_in,
//					R.anim.push_left_out, R.anim.push_left_in,
//					R.anim.push_left_out);
//			transactionWifiLan.replace(R.id.fragment_container, fragmentWifiLan);
//			// transactionWifi.addToBackStack(null);
//			transactionWifiLan.commit();
//			break;
////			try {
////				if (ETGlobal.mTg != null) {
////					ETGlobal.mTg.close();
////					ETGlobal.mTg = null;
////				}
////				INetClient client = new HXDTCPClient("192.168.0.1", 9090);
////				ETNetClientAdapter mNetClientAdapter = new ETNetClientAdapter(
////						client);
////				ETGlobal.mTg = new ETWifiClient(mNetClientAdapter);
////				Open();
////				ETSave.getInstance(getActivity()).put(
////						"comType", "wifidirect");
////				ETSave.getInstance(getActivity()).put(
////						"wifidirect_ip", "");
////				ETSave.getInstance(getActivity()).put(
////						"wifidirect_port", "");
////			} catch (Exception e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////			break;
//		case R.id.buttonIO:
//			try {
//				if (ETGlobal.mTg != null) {
//					ETGlobal.mTg.close();
//					ETGlobal.mTg = null;
//				}
//				ETGlobal.mTg = new ETIO();
//				Open();
//				ETSave.getInstance(getActivity()).put(
//						"comType", "io");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			break;
//		case R.id.buttonUSB:
//			if (!getActivity().getPackageManager().hasSystemFeature(
//					PackageManager.FEATURE_USB_HOST)) {
//				Toast.makeText(getActivity(), R.string.usb_not_supported,
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//			try {
//				if (ETGlobal.mTg != null) {
//					ETGlobal.mTg.close();
//					ETGlobal.mTg = null;
//				}
//				if (ETUSB.hasPermission(getActivity(),
//						ETUSB.getDevice(getActivity()))) {
//					ETUSB.Init(getActivity());
//					ETGlobal.mTg = new ETUSB(getActivity());
//					Open();
//					ETSave.getInstance(getActivity()).put(
//							"comType", "usb");
//				} else {
//					ETUSB.Init(getActivity());
//					ETGlobal.mTg = new ETUSB(getActivity());
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				Toast.makeText(this.getActivity(), this.getString(R.string.str_error_open_usb), Toast.LENGTH_SHORT).show();
//			}
//			break;
//		case R.id.buttonSound:
//			try {
//				if (ETGlobal.mTg != null) {
//					ETGlobal.mTg.close();
//					ETGlobal.mTg = null;
//				}
//				ETGlobal.mTg =  new ThreeManRemote(getActivity());
//				Open();
//				ETSave.getInstance(getActivity()).put(
//						"comType", "sound");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			break;
//		}
	}

	public class RecvReceiver extends BroadcastReceiver {
		@SuppressLint({ "InlinedApi", "NewApi" })
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ETUSB.BROADCAST_USB_PERMISSION.equals(action)) {
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,
						false)) {
					Open();
					ETSave.getInstance(getActivity()).put(
							"comType", "usb");
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
//		FragmentDevice fragmentDevice = new FragmentDevice();
//		FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
//		Bundle args = new Bundle();
//		args.putInt("group", 0);
//		fragmentDevice.setArguments(args);
//		transaction.addToBackStack(null);
//		transaction.replace(R.id.fragment_container, fragmentDevice);
//		transaction.commit();
		
		FragmentMore fragmentMore = new FragmentMore();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);

		transaction.replace(R.id.fragment_container,
				fragmentMore);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	};
}
