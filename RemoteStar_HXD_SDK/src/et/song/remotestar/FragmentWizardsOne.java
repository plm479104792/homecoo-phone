package et.song.remotestar;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevice;
import et.song.etclass.ETDevicePower;
import et.song.etclass.ETGroup;
import et.song.etclass.ETPage;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remotestar.hxd.sdk.R;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FragmentWizardsOne extends SherlockFragment implements IBack {
	private GridView mGridView;
	private List<ETDevice> mDeviceList = new ArrayList<ETDevice>();
	private int mGroupIndex;
	private RecvReceiver mReceiver;
//	private ImageView mImageWizardsInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		String[] devices = getResources().getStringArray(R.array.strs_device);
		for (int i = 0; i < ETGlobal.mDeviceTypes.length - 1; i++) {
//			if (ETGlobal.mDeviceTypes[i] ==  DeviceType.DEVICE_REMOTE_CUSTOM){
//				continue;
//			}
			ETDevice device = new ETDevice();
			device.SetName(devices[i]);
			device.SetType(ETGlobal.mDeviceTypes[i]);
			device.SetRes(i);
			mDeviceList.add(device);
		}

		mGroupIndex = this.getArguments().getInt("group");
		Log.i("GroupIndex", String.valueOf(mGroupIndex));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_wizards_one, container,
				false);
//		mImageWizardsInfo = (ImageView)view.findViewById(R.id.imageWizardsInfo); 
		mGridView = (GridView) view.findViewById(R.id.grid);
		// mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// mGridView.setBackgroundColor(Color.TRANSPARENT);
		mGridView.setAdapter(new GridAdapter(getActivity()));
		mGridView.setOnItemClickListener(new ItemClickListener());
		mGridView.setOnItemLongClickListener(new ItemLongClickListener());
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

		inflater.inflate(R.menu.menu_diy, menu);
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("Home", "Home");
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		
		int id=item.getItemId();
		if (id==android.R.id.home) {
			Back();
			return true;
		}else if (id==R.id.menu_diy) {
			args.putInt("group", mGroupIndex);
			FragmentWizardsSeven fragmentWizardsSeven = new FragmentWizardsSeven();
			fragmentWizardsSeven.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentWizardsSeven);
			transaction.addToBackStack(null);
			transaction.commit();
		}
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			Back();
//			return true;
//		case R.id.menu_diy:
//			args.putInt("group", mGroupIndex);
//			FragmentWizardsSeven fragmentWizardsSeven = new FragmentWizardsSeven();
//			fragmentWizardsSeven.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragmentWizardsSeven);
//			transaction.addToBackStack(null);
//			transaction.commit();
//			break;
//		}
		return super.onOptionsItemSelected(item);
	}

	private class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			FragmentWizardsTwo fragmentWizardsTwo = new FragmentWizardsTwo();
			FragmentTransaction transaction = getActivity()
					.getSupportFragmentManager().beginTransaction();
			Bundle args = new Bundle();
			ETDevice device = (ETDevice) arg0.getItemAtPosition(pos);
			switch (device.GetType()) {
			case DeviceType.DEVICE_REMOTE_TV:
				args.putInt("type", DeviceType.DEVICE_REMOTE_TV);
				break;
			case DeviceType.DEVICE_REMOTE_IPTV:
				args.putInt("type", DeviceType.DEVICE_REMOTE_IPTV);
				break;
			case DeviceType.DEVICE_REMOTE_STB:
				args.putInt("type", DeviceType.DEVICE_REMOTE_STB);
				break;
			case DeviceType.DEVICE_REMOTE_DVD:
				args.putInt("type", DeviceType.DEVICE_REMOTE_DVD);
				break;
			case DeviceType.DEVICE_REMOTE_FANS:
				args.putInt("type", DeviceType.DEVICE_REMOTE_FANS);
				break;
			case DeviceType.DEVICE_REMOTE_PJT:
				args.putInt("type", DeviceType.DEVICE_REMOTE_PJT);
				break;
			case DeviceType.DEVICE_REMOTE_LIGHT:
				args.putInt("type", DeviceType.DEVICE_REMOTE_LIGHT);
				break;
			case DeviceType.DEVICE_REMOTE_DC:
				args.putInt("type", DeviceType.DEVICE_REMOTE_DC);
				break;
			case DeviceType.DEVICE_REMOTE_AIR:
				args.putInt("type", DeviceType.DEVICE_REMOTE_AIR);
				break;
			case DeviceType.DEVICE_REMOTE_CUSTOM:
				args.putInt("group", mGroupIndex);
				FragmentWizardsSeven fragmentWizardsSeven = new FragmentWizardsSeven();
				fragmentWizardsSeven.setArguments(args);
				transaction.replace(R.id.fragment_container, fragmentWizardsSeven);
				transaction.addToBackStack(null);
				transaction.commit();
				return;
			case DeviceType.DEVICE_REMOTE_POWER:
				args.putInt("group", mGroupIndex);
				String[] devices = getResources().getStringArray(R.array.strs_device);
				ETGroup group = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
						mGroupIndex);
				ETDevice mDevice = new ETDevicePower(0);
				mDevice.SetGID(group.GetID());
				mDevice.SetType(DeviceType.DEVICE_REMOTE_POWER);
				mDevice.SetRes(10);
				mDevice.SetName(devices[10]);
				FragmentDevice fragmentDevice = new FragmentDevice();
				mDevice.Inster(ETDB.getInstance(getActivity()));
				args.putInt("group", mGroupIndex);
				fragmentDevice.setArguments(args);
				transaction.replace(R.id.fragment_container, fragmentDevice);
				transaction.addToBackStack(null);
				transaction.commit();
				return;
			}
			args.putInt("group", mGroupIndex);
			fragmentWizardsTwo.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentWizardsTwo);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			//mImageWizardsInfo.setVisibility(View.GONE);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			//mImageWizardsInfo.setVisibility(View.VISIBLE);
		}
	}

	private class ItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {

			return true;
		}

	}

	private class GridAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public GridAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mDeviceList.size();
		}

		@Override
		public Object getItem(int position) {

			return mDeviceList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup par) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.fragment_grid_item,
						null);
				holder = new ViewHolder();
				holder.image_grid_item_res = ((ImageView) convertView
						.findViewById(R.id.image_grid_item_res));
				holder.text_grid_item_name = ((TextView) convertView
						.findViewById(R.id.text_grid_item_name));
				holder.text_grid_item_context = ((TextView) convertView
						.findViewById(R.id.text_grid_item_context));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ETDevice device = mDeviceList.get(position);
			holder.image_grid_item_res
					.setImageResource(ETGlobal.mDeviceImages[device.GetRes()]);
			holder.text_grid_item_name.setText(device.GetName());
			holder.text_grid_item_context.setText("");
			return convertView;
		}

		private class ViewHolder {
			ImageView image_grid_item_res;
			TextView text_grid_item_name;
			TextView text_grid_item_context;
		}
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();

		FragmentDevice fragmentDevice = new FragmentDevice();
		args.putInt("group", mGroupIndex);
		fragmentDevice.setArguments(args);
		// transaction
		// .setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out,
		// R.anim.push_left_in, R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragmentDevice);
		// transactionBt.addToBackStack(null);
		// transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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
