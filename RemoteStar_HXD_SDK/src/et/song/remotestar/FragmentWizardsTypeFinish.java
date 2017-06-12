package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevice;
import et.song.etclass.ETDeviceAIR;
import et.song.etclass.ETDeviceDC;
import et.song.etclass.ETDeviceDVD;
import et.song.etclass.ETDeviceFANS;
import et.song.etclass.ETDeviceIPTV;
import et.song.etclass.ETDeviceLIGHT;
import et.song.etclass.ETDevicePJT;
import et.song.etclass.ETDeviceSTB;
import et.song.etclass.ETDeviceTV;
import et.song.etclass.ETGroup;
import et.song.etclass.ETPage;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remotestar.hxd.sdk.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class FragmentWizardsTypeFinish extends SherlockFragment implements
		OnClickListener, IBack {
	private int mType;
	private int mIndex;
	private int mGroupIndex;
	private EditText mEditName;
	private RecvReceiver mReceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mIndex = this.getArguments().getInt("index");
		mType = this.getArguments().getInt("type");
		mGroupIndex = this.getArguments().getInt("group");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_wizards_type_finish,
				container, false);
		Button button_save = (Button) view.findViewById(R.id.button_save);
		button_save.setOnClickListener(this);

		mEditName = (EditText) view.findViewById(R.id.edit_name);
		String name = "";
		switch (mType) {
		case DeviceType.DEVICE_REMOTE_TV:
			name = getResources().getStringArray(R.array.strs_tv_type)[mIndex];

			break;
		case DeviceType.DEVICE_REMOTE_STB:

			name = getResources().getStringArray(R.array.strs_stb_type)[mIndex];

			break;
		case DeviceType.DEVICE_REMOTE_DVD:

			name = getResources().getStringArray(R.array.strs_dvd_type)[mIndex];

			break;
		case DeviceType.DEVICE_REMOTE_PJT:

			name = getResources().getStringArray(R.array.strs_pjt_type)[mIndex];

			break;
		case DeviceType.DEVICE_REMOTE_FANS:

			name = getResources().getStringArray(R.array.strs_fans_type)[mIndex];

			break;
		case DeviceType.DEVICE_REMOTE_IPTV:

			name = getResources().getStringArray(R.array.strs_iptv_type)[mIndex];

			break;
		case DeviceType.DEVICE_REMOTE_AIR:

			name = getResources().getStringArray(R.array.strs_air_type)[mIndex];

			break;
		case DeviceType.DEVICE_REMOTE_LIGHT:

			name = getResources().getStringArray(R.array.strs_light_type)[mIndex];

			break;
		case DeviceType.DEVICE_REMOTE_DC:

			name = getResources().getStringArray(R.array.strs_dc_type)[mIndex];

			break;
		}
//		String str = name.substring(0, name.indexOf("("))
//				+ name.substring(name.indexOf("-") + 1, name.length());
//		int len = str.length() > 10 ? 10 : str.length();
		mEditName.setText(name.substring(0, 10));
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		getActivity().registerReceiver(mReceiver, filter);
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

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("Home", "Home");
		switch (item.getItemId()) {
		case android.R.id.home:
			Back();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id=v.getId();
		if (id==R.id.button_save) {
			
//		}
//		switch (v.getId()) {
//		case R.id.button_save:
			ETGroup group = (ETGroup) ETPage.getInstance(getActivity())
					.GetItem(mGroupIndex);
			ETDevice device = null;
			switch (mType) {
			case DeviceType.DEVICE_REMOTE_TV:
				device = new ETDeviceTV(mIndex);
				device.SetName(mEditName.getText().toString());
				device.SetType(DeviceType.DEVICE_REMOTE_TV);
				device.SetRes(0);
				device.SetGID(group.GetID());
				device.Inster(ETDB.getInstance(this.getActivity()));

				break;
			case DeviceType.DEVICE_REMOTE_STB:
				device = new ETDeviceSTB(mIndex);
				device.SetName(mEditName.getText().toString());
				device.SetType(DeviceType.DEVICE_REMOTE_STB);
				device.SetRes(2);
				device.SetGID(group.GetID());
				device.Inster(ETDB.getInstance(this.getActivity()));
				break;
			case DeviceType.DEVICE_REMOTE_DVD:
				device = new ETDeviceDVD(mIndex);
				device.SetName(mEditName.getText().toString());
				device.SetType(DeviceType.DEVICE_REMOTE_DVD);
				device.SetRes(3);
				device.SetGID(group.GetID());
				device.Inster(ETDB.getInstance(this.getActivity()));
				break;
			case DeviceType.DEVICE_REMOTE_PJT:
				device = new ETDevicePJT(mIndex);
				device.SetName(mEditName.getText().toString());
				device.SetType(DeviceType.DEVICE_REMOTE_PJT);
				device.SetRes(5);
				device.SetGID(group.GetID());
				device.Inster(ETDB.getInstance(this.getActivity()));
				break;
			case DeviceType.DEVICE_REMOTE_FANS:
				device = new ETDeviceFANS(mIndex);
				device.SetName(mEditName.getText().toString());
				device.SetType(DeviceType.DEVICE_REMOTE_FANS);
				device.SetRes(4);
				device.SetGID(group.GetID());
				device.Inster(ETDB.getInstance(this.getActivity()));
				break;
			case DeviceType.DEVICE_REMOTE_IPTV:
				device = new ETDeviceIPTV(mIndex);
				device.SetName(mEditName.getText().toString());
				device.SetType(DeviceType.DEVICE_REMOTE_IPTV);
				device.SetRes(1);
				device.SetGID(group.GetID());
				device.Inster(ETDB.getInstance(this.getActivity()));
				break;
			case DeviceType.DEVICE_REMOTE_AIR:
				device = new ETDeviceAIR(mIndex);
				device.SetName(mEditName.getText().toString());
				device.SetType(DeviceType.DEVICE_REMOTE_AIR);
				device.SetRes(7);
				device.SetGID(group.GetID());
				device.Inster(ETDB.getInstance(this.getActivity()));
				break;
			case DeviceType.DEVICE_REMOTE_LIGHT:
				device = new ETDeviceLIGHT();
				device.SetName(mEditName.getText().toString());
				device.SetType(DeviceType.DEVICE_REMOTE_LIGHT);
				device.SetRes(6);
				device.SetGID(group.GetID());
				device.Inster(ETDB.getInstance(this.getActivity()));
				break;
			case DeviceType.DEVICE_REMOTE_DC:
				device = new ETDeviceDC();
				device.SetName(mEditName.getText().toString());
				device.SetType(DeviceType.DEVICE_REMOTE_DC);
				device.SetRes(8);
				device.SetGID(group.GetID());
				device.Inster(ETDB.getInstance(this.getActivity()));
				break;
			}
			FragmentDevice fragmentDevice = new FragmentDevice();
			FragmentTransaction transaction = getActivity()
					.getSupportFragmentManager().beginTransaction();
			Bundle args = new Bundle();
			args.putInt("group", mGroupIndex);
			fragmentDevice.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentDevice);
			transaction.addToBackStack(null);
			transaction.commit();
//			break;
		}
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentWizardsTwo fragmentThree = new FragmentWizardsTwo();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("group", mGroupIndex);
		args.putInt("type", mType);
		fragmentThree.setArguments(args);
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragmentThree);
		// transactionBt.addToBackStack(null);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	}
	
	public class RecvReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
				Back();
			}
		}
	}

}
