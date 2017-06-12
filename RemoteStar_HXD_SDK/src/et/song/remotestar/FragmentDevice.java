package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevice;
import et.song.etclass.ETGroup;
import et.song.etclass.ETPage;
import et.song.etclass.ETSave;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.jni.ir.ETIR;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentDevice extends SherlockFragment implements IBack{
	private GridView mGridView;
	private int mGroupIndex;
	private RecvReceiver mReceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		((ActivityMain)getActivity()).ShowBottom();
		ETPage.getInstance(this.getActivity()).Load(ETDB.getInstance(this.getActivity()));
		mGroupIndex = getArguments().getInt("group");
		String[] devices = getResources().getStringArray(R.array.strs_device);
		ETGroup group = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		ETDevice device = (ETDevice) group.GetItem(group.GetCount() - 1);
		device.SetName(devices[devices.length - 1]);
		int count = ETIR.SearchAirCode(0, 0, 0);
		if (count >= 100) {
			ETTool.MessageBox(getActivity(), 1.0f, getString(R.string.str_caoma), true);
		}
//		if (ETGlobal.mTg == null) {
//			Dialog alertDialog = new AlertDialog.Builder(getActivity())
//					.setMessage(R.string.str_study_start_info_6)
//					.setIcon(R.drawable.ic_launcher)
//					.setNegativeButton(R.string.str_buy_no,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									Intent intent = new Intent(
//											ETGlobal.BROADCAST_APP_BUY_NO);
//									getActivity().sendBroadcast(intent);
//								}
//							})
//					.setPositiveButton(R.string.str_buy_yes,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									Intent intent = new Intent(
//											ETGlobal.BROADCAST_APP_BUY_YES);
//									getActivity().sendBroadcast(intent);
//								}
//							}).create();
//			alertDialog.show();
//		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getActivity().setTitle(R.string.str_device);
		View view = inflater
				.inflate(R.layout.fragment_device, container, false);
		mGridView = (GridView) view.findViewById(R.id.grid);
		mGridView.setBackgroundColor(Color.TRANSPARENT);
		mGridView.setAdapter(new GridAdapter(getActivity()));
		mGridView.setOnItemClickListener(new ItemClickListener());
		//mGridView.setOnItemLongClickListener(new ItemLongClickListener());
		registerForContextMenu(mGridView);
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
	private void F5()
	{
		FragmentDevice fragmentDevice = new FragmentDevice();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("group", mGroupIndex);
		fragmentDevice.setArguments(args);
		transaction.replace(R.id.fragment_container, fragmentDevice);
		//transaction.addToBackStack(null);
		transaction.commit();
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		getActivity().getMenuInflater().inflate(R.menu.menu_device_longclick,
				menu);

	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		ETGroup group = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		final ETDevice device = (ETDevice)group.GetItem(menuInfo.position);
		if (device.GetType() == DeviceType.DEVICE_ADD){
			return true;
		}
		
		int id=item.getItemId();
		if (id==R.id.menu_device_del) {
			device.Delete(ETDB.getInstance(getActivity()));
			F5();
			ETSave.getInstance(getActivity()).put("DeviceType", "");
		}else if (id==R.id.menu_device_rename) {
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View addView = mInflater.inflate(R.layout.dialog_set_name, null);
			final EditText name = (EditText) addView
					.findViewById(R.id.edit_name);
			name.setText(device.GetName());
			AlertDialog DialogSetName = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setView(addView)
					.setPositiveButton(R.string.str_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									device.SetName(name.getText().toString());
									device.Update(ETDB.getInstance(getActivity()));
									F5();
								}
							}).create();

			DialogSetName.setTitle(R.string.str_dialog_set_name_title);
			DialogSetName.show();
			return true;
		}
		
		
//		switch (item.getItemId()) {
//		case R.id.menu_device_del:
//			device.Delete(ETDB.getInstance(getActivity()));
//			F5();
//			ETSave.getInstance(getActivity()).put("DeviceType", "");
//			return true; /* true means: "we handled the event". */
//		case R.id.menu_device_rename:
//			LayoutInflater mInflater = LayoutInflater.from(getActivity());
//			View addView = mInflater.inflate(R.layout.dialog_set_name, null);
//			final EditText name = (EditText) addView
//					.findViewById(R.id.edit_name);
//			name.setText(device.GetName());
//			AlertDialog DialogSetName = new AlertDialog.Builder(getActivity())
//					.setIcon(R.drawable.ic_launcher)
//					.setView(addView)
//					.setPositiveButton(R.string.str_ok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int whichButton) {
//									device.SetName(name.getText().toString());
//									device.Update(ETDB.getInstance(getActivity()));
//									F5();
//								}
//							}).create();
//
//			DialogSetName.setTitle(R.string.str_dialog_set_name_title);
//			DialogSetName.show();
//			return true;
//		}
		return super.onContextItemSelected(item);
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

	private class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			((ActivityMain)getActivity()).HideBottom();
			ETDevice device = (ETDevice) arg0.getItemAtPosition(pos);
			FragmentTransaction transaction = getActivity()
					.getSupportFragmentManager().beginTransaction();
			if (device.GetType() == DeviceType.DEVICE_ADD) {			
//				Bundle args = new Bundle();
//				args.putInt("group", mGroupIndex);
//				FragmentWizards fragmentWizards = new FragmentWizards();
//				fragmentWizards.setArguments(args);
//				transaction.setCustomAnimations(R.anim.push_left_in,
//						R.anim.push_left_out, R.anim.push_left_in,
//						R.anim.push_left_out);
//
//				transaction.replace(R.id.fragment_container, fragmentWizards);
//				// transactionBt.addToBackStack(null);
//				transaction
//						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//				transaction.commit();
				
				Bundle args = new Bundle();
				args.putInt("group", mGroupIndex);
				FragmentWizardsOne fragmentWizardsOne = new FragmentWizardsOne();
				fragmentWizardsOne.setArguments(args);
				transaction.replace(R.id.fragment_container, fragmentWizardsOne);
				//transaction.addToBackStack(null);
				transaction.commit();
			} else {
				Fragment fragment = null;
				Bundle args = new Bundle();
				args.putInt("group", mGroupIndex);
				args.putInt("device", pos);
				switch (device.GetType()) {
				case DeviceType.DEVICE_REMOTE_TV:
					fragment = new FragmentTV();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_IPTV:
					fragment = new FragmentIPTV();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_STB:
					fragment = new FragmentSTB();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_DVD:
					fragment = new FragmentDVD();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_FANS:
					fragment = new FragmentFans();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_PJT:
					fragment = new FragmentPJT();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_LIGHT:
					fragment = new FragmentLight();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_AIR:
					fragment = new FragmentAIR();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_DC:
					fragment = new FragmentDC();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_POWER:
					fragment = new FragmentPOWER();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				case DeviceType.DEVICE_REMOTE_CUSTOM:
					fragment = new FragmentCustom();
					fragment.setArguments(args);
					transaction.replace(R.id.fragment_container, fragment);
					break;
				}
//				transaction.setCustomAnimations(R.anim.push_left_in,
//						R.anim.push_left_out, R.anim.push_left_in,
//						R.anim.push_left_out);

				transaction.replace(R.id.fragment_container, fragment);
				// transactionBt.addToBackStack(null);
//				transaction
//						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.commit();

			}
		}
	}

//	private class ItemLongClickListener implements OnItemLongClickListener {
//
//		@Override
//		public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos,
//				long arg3) {
//
//			return true;
//		}
//
//	}

	private class GridAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public GridAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			ETGroup group = (ETGroup) ETPage.getInstance(getActivity())
					.GetItem(mGroupIndex);
			return group.GetCount();
		}

		@Override
		public Object getItem(int position) {
			ETGroup group = (ETGroup) ETPage.getInstance(getActivity())
					.GetItem(mGroupIndex);
			return group.GetItem(position);
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
			ETGroup group = (ETGroup) ETPage.getInstance(getActivity())
					.GetItem(mGroupIndex);
			ETDevice device = (ETDevice) group.GetItem(position);
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
//		// TODO Auto-generated method stub
		FragmentGroup fragmentGroup = new FragmentGroup();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);

		transaction.replace(R.id.fragment_container, fragmentGroup);
		// transactionBt.addToBackStack(null);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
		//((ActivityMain)getActivity()).exit();
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
