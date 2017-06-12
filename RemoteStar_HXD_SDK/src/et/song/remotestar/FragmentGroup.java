package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.etclass.ETGroup;
import et.song.etclass.ETPage;
import et.song.etclass.ETSave;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remotestar.hxd.sdk.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.Toast;

//import android.widget.AdapterView.OnItemLongClickListener;

public class FragmentGroup extends SherlockFragment implements IBack {
	private GridView mGridView;
	private RecvReceiver mReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);

		((ActivityMain) getActivity()).ShowBottom();
		ETPage.getInstance(this.getActivity()).Load(
				ETDB.getInstance(this.getActivity()));
		String[] groups = getResources().getStringArray(R.array.strs_group);
		int leng=ETPage.getInstance(getActivity()).GetCount();
		System.out.println("红外转发器的数量为：..."+leng);
		if(leng>0){
			ETGroup group = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
					ETPage.getInstance(getActivity()).GetCount() - 1);
//			group.SetName(groups[groups.length - 2]);
			group.SetName(group.GetName());
		}else{
			Toast.makeText(getActivity(), "请在设备管理界面设置红外转发器的空间！", 2000).show();
		}

		// if (ETPage.getInstance(getActivity()).GetCount() == 0){

		// for (int i = 0; i < ETGlobal.mGroupTypes.length; i++) {
		// ETGroup group = new ETGroup();
		// group.SetID(i);
		// group.SetName(groups[i]);
		// group.SetType(ETGlobal.mGroupTypes[i]);
		// group.SetRes(i);
		// group.Inster(ETDB.getInstance(getActivity()));
		// ETPage.getInstance(getActivity()).Load(ETDB.getInstance(getActivity()));
		// }
		// }

		// FragmentDevice fragmentDevice = new FragmentDevice();
		// FragmentTransaction transaction =
		// this.getActivity().getSupportFragmentManager().beginTransaction();
		// Bundle args = new Bundle();
		// args.putInt("group", 0);
		// fragmentDevice.setArguments(args);
		// transaction.replace(R.id.fragment_container, fragmentDevice);
		// transaction.commit();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getActivity().setTitle(R.string.str_group);
		View view = inflater.inflate(R.layout.fragment_group, container, false);
		mGridView = (GridView) view.findViewById(R.id.grid);
		mGridView.setBackgroundColor(Color.TRANSPARENT);
		mGridView.setAdapter(new GridAdapter(getActivity()));
		mGridView.setOnItemClickListener(new ItemClickListener());
		// mGridView.setOnItemLongClickListener(new ItemLongClickListener());
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

	private void F5() {
		FragmentGroup fragmentGroup = new FragmentGroup();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragmentGroup);
		// transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		getActivity().getMenuInflater().inflate(R.menu.menu_group_longclick,
				menu);

	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final ETGroup group = (ETGroup) ETPage.getInstance(getActivity())
				.GetItem(menuInfo.position);
		if (group.GetType() == ETGlobal.ETGROUP_TYPE_ADD) {
			return true;
		}
		
		int id=item.getItemId();
		if (id==R.id.menu_group_del) {
			ETSave.getInstance(getActivity()).put("DeviceType", "");
			group.Delete(ETDB.getInstance(getActivity()));
			F5();
			return true;
		}else if (id==R.id.menu_group_rename) {
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View addView = mInflater.inflate(R.layout.dialog_set_name, null);
			final EditText name = (EditText) addView
					.findViewById(R.id.edit_name);
			name.setText(group.GetName());
			AlertDialog DialogSetName = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setView(addView)
					.setPositiveButton(R.string.str_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									group.SetName(name.getText().toString());
									group.Update(ETDB
											.getInstance(getActivity()));
									F5();
								}
							}).create();

			DialogSetName.setTitle(R.string.str_dialog_set_name_title);
			DialogSetName.show();
			return true;
		}
		
		
//		switch (item.getItemId()) {
//		case R.id.menu_group_del:
//			ETSave.getInstance(getActivity()).put("DeviceType", "");
//			group.Delete(ETDB.getInstance(getActivity()));
//			F5();
//			return true; /* true means: "we handled the event". */
//		case R.id.menu_group_rename:
//			LayoutInflater mInflater = LayoutInflater.from(getActivity());
//			View addView = mInflater.inflate(R.layout.dialog_set_name, null);
//			final EditText name = (EditText) addView
//					.findViewById(R.id.edit_name);
//			name.setText(group.GetName());
//			AlertDialog DialogSetName = new AlertDialog.Builder(getActivity())
//					.setIcon(R.drawable.ic_launcher)
//					.setView(addView)
//					.setPositiveButton(R.string.str_ok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int whichButton) {
//									group.SetName(name.getText().toString());
//									group.Update(ETDB
//											.getInstance(getActivity()));
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

	private class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			// if (ETGlobal.mTg == null)
			// {
			// Fragment fragmentCom = new FragmentCom();
			// FragmentTransaction transactionCom =
			// getActivity().getSupportFragmentManager()
			// .beginTransaction();
			// transactionCom.setCustomAnimations(R.anim.push_left_in,
			// R.anim.push_left_out, R.anim.push_left_in,
			// R.anim.push_left_out);
			// transactionCom.replace(R.id.fragment_container, fragmentCom);
			// // transactionCom.addToBackStack(null);
			// transactionCom.commit();
			// return;
			// }
			((ActivityMain) getActivity()).HideBottom();
			ETGroup group = (ETGroup) arg0.getItemAtPosition(pos);
			if (group.GetType() == ETGlobal.ETGROUP_TYPE_ADD) {
				FragmentGroupAdd fragmentGroupAdd = new FragmentGroupAdd();
				FragmentTransaction transaction = getActivity()
						.getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, fragmentGroupAdd);
				transaction.addToBackStack(null);
				transaction.commit();
			} else {
				String devid=group.getmDevid();
				
				Intent intent = new Intent();  //Itent就是我们要发送的内容
	            intent.setAction("INFRA_DEVID");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
	            intent.putExtra("devid",devid);  
	            getActivity().sendBroadcast(intent);   //发送广播
				System.out.println("===发送广播消息=="+devid);
				
				FragmentDevice fragmentDevice = new FragmentDevice();
				FragmentTransaction transaction = getActivity()
						.getSupportFragmentManager().beginTransaction();
				Bundle args = new Bundle();
				args.putInt("group", pos);
				fragmentDevice.setArguments(args);
				transaction.replace(R.id.fragment_container, fragmentDevice);
				// transaction.addToBackStack(null);
				transaction.commit();
			}
		}
	}

	// private class ItemLongClickListener implements OnItemLongClickListener {
	//
	// @Override
	// public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos,
	// long arg3) {
	//
	// return true;
	// }
	//
	// }

	private class GridAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public GridAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return ETPage.getInstance(getActivity()).GetCount();
		}

		@Override
		public Object getItem(int position) {
			return ETPage.getInstance(getActivity()).GetItem(position);
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
					.GetItem(position);
			holder.image_grid_item_res
					.setImageResource(ETGlobal.mGroupImages[group.GetRes()]);
			holder.text_grid_item_name.setText(group.GetName());
			int count = group.GetCount() > 0 ? group.GetCount() - 1 : 0;
			holder.text_grid_item_context.setText(count
					+ getString(R.string.str_group_context));
			return convertView;
		}

		private class ViewHolder {
			ImageView image_grid_item_res;
			TextView text_grid_item_name;
			TextView text_grid_item_context;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Nothing to see here.
		menu.clear();

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("Home", "Home");
//		switch (item.getItemId()) {
//		case android.R.id.home:
           System.out.println("点击红外返回按钮====");
			Back();
			return true;
//		}
//		return super.onOptionsItemSelected(item);
	}

	@Override
	public void Back() {
		System.out.println("group返回");
		((ActivityMain) getActivity()).exit();
		
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
