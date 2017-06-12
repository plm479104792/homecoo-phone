package et.song.remotestar;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevice;
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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentWatchTV extends SherlockFragment implements IBack {
	private GridView mGridView;
	private List<AdapterWatchTVItem> mWatchTVList = new ArrayList<AdapterWatchTVItem>();
	private RecvReceiver mReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		((ActivityMain)getActivity()).HideBottom();
		Load(ETDB.getInstance(getActivity()));
		if (ETSave.getInstance(getActivity()).get("isWatchTV").equals("1")) {
			return;
		}
		Dialog alertDialog = new AlertDialog.Builder(getActivity())
				.setMessage(R.string.str_study_start_info_3)
				.setIcon(R.drawable.ic_launcher)
				.setNegativeButton(R.string.str_info_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated
								// method stub
							}
						})
				.setPositiveButton(R.string.str_info_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ETSave.getInstance(getActivity()).put(
										"isWatchTV", "1");
							}
						}).create();
		alertDialog.show();
		// ETTool.MessageBox(getActivity(), 0.8f,
		// getString(R.string.str_study_start_info_3), true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getActivity().setTitle(R.string.str_watch_tv);
		View view = inflater.inflate(R.layout.fragment_watch_tv, container,
				false);
		mGridView = (GridView) view.findViewById(R.id.grid);
		// mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// mGridView.setBackgroundColor(Color.TRANSPARENT);
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		getActivity().getMenuInflater().inflate(R.menu.menu_watchtv_longclick,
				menu);

	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final AdapterWatchTVItem watchtv = (AdapterWatchTVItem) this.mWatchTVList
				.get(menuInfo.position);
		int itemid=item.getItemId();
		if (itemid==R.id.menu_watch_longclick_edit) {
			
//		}
//		switch (item.getItemId()) {
//		case R.id.menu_watch_longclick_edit:
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View addView = mInflater
					.inflate(R.layout.dialog_watchtv_edit, null);
			final EditText edit_value = (EditText) addView
					.findViewById(R.id.edit_watchtv_num);
			if (!watchtv.getValue().equals("")) {
				edit_value.setText(String.valueOf(watchtv.getValue()));
			}
			final CheckBox check_ok = (CheckBox) addView
					.findViewById(R.id.check_watchtv_ok);
			check_ok.setChecked(watchtv.isOK());
			final CheckBox check_select = (CheckBox) addView
					.findViewById(R.id.check_watchtv_select);
			check_select.setChecked(watchtv.isSelect());

			final Spinner spinner_did = (Spinner) addView
					.findViewById(R.id.spinner_watchtv_did);
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					getActivity(), R.layout.fragment_watchtv_list_item);
			spinner_did.setAdapter(arrayAdapter);
			int n = 0;
			final List<Integer> l = new ArrayList<Integer>();
			try {
				Cursor c = ETDB.getInstance(getActivity()).queryData2Cursor(
						"select * from " + DBProfile.DEVICE_TABLE_NAME, null);
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					int gid = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_GROUP_ID));
					Log.i("GID", String.valueOf(gid));
					int id = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_ID));
					String name = c.getString(c
							.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_NAME));
					int type = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_TYPE));
					switch (type) {
					case DeviceType.DEVICE_REMOTE_TV:
					case DeviceType.DEVICE_REMOTE_IPTV:
					case DeviceType.DEVICE_REMOTE_STB:
						arrayAdapter.add(name);
						l.add(id);
						break;
					}
					if (watchtv.getDID() == id) {
						n = arrayAdapter.getCount() - 1;
						Log.i("N", String.valueOf(n));
					}
				}
				spinner_did.setSelection(n);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AlertDialog DialogSetName = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setView(addView)
					.setNegativeButton(R.string.str_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							})
					.setPositiveButton(R.string.str_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (spinner_did.getCount() != 0) {
										watchtv.setDID(l.get(spinner_did
												.getSelectedItemPosition()));
										watchtv.setValue(edit_value.getText()
												.toString());
										watchtv.setOK(check_ok.isChecked());
										watchtv.setSelect(check_select
												.isChecked());
										Update(watchtv);
									}
								}
							}).create();

			DialogSetName.setTitle(R.string.str_menu_control_edit);
			DialogSetName.show();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void Update(AdapterWatchTVItem item) {
		try {
			ETDB db = ETDB.getInstance(getActivity());
			Cursor c = db.queryData2Cursor("select * from "
					+ DBProfile.WATCHTV_TABLE_NAME + " where "
					+ DBProfile.TABLE_WATCHTV_FIELD_ID + " = " + item.getID(),
					null);

			ContentValues value = new ContentValues();
			value.put(DBProfile.TABLE_WATCHTV_FIELD_DEVICE_ID, item.getDID());
			value.put(DBProfile.TABLE_WATCHTV_FIELD_NAME, item.getName());
			value.put(DBProfile.TABLE_WATCHTV_FIELD_RES, item.getRes());
			value.put(DBProfile.TABLE_WATCHTV_FIELD_VALUE, 0);
			if (item.isOK()) {
				value.put(DBProfile.TABLE_WATCHTV_FIELD_ISOK, 1);
			} else {
				value.put(DBProfile.TABLE_WATCHTV_FIELD_ISOK, 0);
			}
			if (item.isSelect()) {
				value.put(DBProfile.TABLE_WATCHTV_FIELD_ISSELECT, 1);
			} else {
				value.put(DBProfile.TABLE_WATCHTV_FIELD_ISSELECT, 0);
			}
			value.put(DBProfile.TABLE_WATCHTV_FIELD_VALUE_EX, item.getValue());
			value.put(DBProfile.TABLE_WATCHTV_FIELD_CONTEXT, item.getContext());

			if (c.getCount() == 0) {
				db.insertData(DBProfile.WATCHTV_TABLE_NAME, value);
			} else {
				db.updataData(DBProfile.WATCHTV_TABLE_NAME, value,
						DBProfile.TABLE_WATCHTV_FIELD_ID + " = ?",
						new String[] { String.valueOf(item.getID()) });
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Load(ETDB db) {
		// TODO Auto-generated method stub
		mWatchTVList.clear();
		try {
			Cursor c = db.queryData2Cursor("select * from "
					+ DBProfile.WATCHTV_TABLE_NAME, null);
			String[] watchtvs = getResources().getStringArray(
					R.array.strs_watch_tv);
			for (int i = 0; i < ETGlobal.mWatchTVImages.length; i++) {
				AdapterWatchTVItem item = new AdapterWatchTVItem();
				item.setContext("");
				item.setDID(-1);
				item.setName(watchtvs[i]);
				item.setValue("");
				item.setRes(i);
				mWatchTVList.add(item);
			}
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				int id = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_ID));
				int did = c
						.getInt(c
								.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_DEVICE_ID));
				String name = c.getString(c
						.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_NAME));
				String context = c.getString(c
						.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_CONTEXT));
				int res = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_RES));
				int value = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_VALUE));
				String value_ex = c
						.getString(c
								.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_VALUE_EX));
				int isOK = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_ISOK));
				int isSelect = c
						.getInt(c
								.getColumnIndex(DBProfile.TABLE_WATCHTV_FIELD_ISSELECT));
				AdapterWatchTVItem item = mWatchTVList.get(res);
				item.setID(id);
				item.setDID(did);
				item.setName(name);
				item.setContext(context);
				if (value_ex == null) {
					item.setValue(String.valueOf(value));
				} else {
					item.setValue(value_ex);
				}
				if (isOK == 0) {
					item.setOK(false);
				} else {
					item.setOK(true);
				}
				if (isSelect == 0) {
					item.setSelect(false);
				} else {
					item.setSelect(true);
				}
				item.setRes(res);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			AdapterWatchTVItem item = (AdapterWatchTVItem) arg0
					.getItemAtPosition(pos);
			try {
				Cursor c = ETDB.getInstance(getActivity()).queryData2Cursor(
						"select * from " + DBProfile.DEVICE_TABLE_NAME
								+ " where " + DBProfile.TABLE_DEVICE_FIELD_ID
								+ " = " + item.getDID(), null);
				if (c.getCount() == 0) {
					ETTool.MessageBox(getActivity(), 0.8f,
							getString(R.string.str_study_start_info_7), true);
					return;
				}
				if (item.getValue().equals("000")) {
					ETTool.MessageBox(getActivity(), 0.8f,
							getString(R.string.str_study_start_info_7), true);
					return;
				}
				c.moveToFirst();
				ETDevice device = null;
				int type = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_TYPE));
				device = ETDevice.Builder(type);
				if (device == null)
					return;
				device.SetID(item.getDID());
				device.Load(ETDB.getInstance(getActivity()));
				if (item.isSelect()) {
					int key = 0;
					if (type == DeviceType.DEVICE_REMOTE_TV) {
						key = IRKeyValue.KEY_TV_SELECT;
					} else if (type == DeviceType.DEVICE_REMOTE_STB) {

					} else if (type == DeviceType.DEVICE_REMOTE_IPTV) {

					}
					if (key == 0)
						return;
					byte[] keyValue = device.GetKeyValue(key);
					if (keyValue == null)
						return;
					ETGlobal.mTg.write(keyValue, keyValue.length);
					Thread.sleep(400);
				}
				String val = String.valueOf(item.getValue());
				for (int i = 0; i < val.length(); i++) {
					int v = Integer.valueOf(val.substring(i, i + 1));
					int key = 0;
					if (type == DeviceType.DEVICE_REMOTE_TV) {
						switch (v) {
						case 0:
							key = IRKeyValue.KEY_TV_KEY0;
							break;
						case 1:
							key = IRKeyValue.KEY_TV_KEY1;
							break;
						case 2:
							key = IRKeyValue.KEY_TV_KEY2;
							break;
						case 3:
							key = IRKeyValue.KEY_TV_KEY3;
							break;
						case 4:
							key = IRKeyValue.KEY_TV_KEY4;
							break;
						case 5:
							key = IRKeyValue.KEY_TV_KEY5;
							break;
						case 6:
							key = IRKeyValue.KEY_TV_KEY6;
							break;
						case 7:
							key = IRKeyValue.KEY_TV_KEY7;
							break;
						case 8:
							key = IRKeyValue.KEY_TV_KEY8;
							break;
						case 9:
							key = IRKeyValue.KEY_TV_KEY9;
							break;
						}
					} else if (type == DeviceType.DEVICE_REMOTE_IPTV) {
						switch (v) {
						case 0:
							key = IRKeyValue.KEY_IPTV_KEY0;
							break;
						case 1:
							key = IRKeyValue.KEY_IPTV_KEY1;
							break;
						case 2:
							key = IRKeyValue.KEY_IPTV_KEY2;
							break;
						case 3:
							key = IRKeyValue.KEY_IPTV_KEY3;
							break;
						case 4:
							key = IRKeyValue.KEY_IPTV_KEY4;
							break;
						case 5:
							key = IRKeyValue.KEY_IPTV_KEY5;
							break;
						case 6:
							key = IRKeyValue.KEY_IPTV_KEY6;
							break;
						case 7:
							key = IRKeyValue.KEY_IPTV_KEY7;
							break;
						case 8:
							key = IRKeyValue.KEY_IPTV_KEY8;
							break;
						case 9:
							key = IRKeyValue.KEY_IPTV_KEY9;
							break;
						}
					} else if (type == DeviceType.DEVICE_REMOTE_STB) {
						switch (v) {
						case 0:
							key = IRKeyValue.KEY_STB_KEY0;
							break;
						case 1:
							key = IRKeyValue.KEY_STB_KEY1;
							break;
						case 2:
							key = IRKeyValue.KEY_STB_KEY2;
							break;
						case 3:
							key = IRKeyValue.KEY_STB_KEY3;
							break;
						case 4:
							key = IRKeyValue.KEY_STB_KEY4;
							break;
						case 5:
							key = IRKeyValue.KEY_STB_KEY5;
							break;
						case 6:
							key = IRKeyValue.KEY_STB_KEY6;
							break;
						case 7:
							key = IRKeyValue.KEY_STB_KEY7;
							break;
						case 8:
							key = IRKeyValue.KEY_STB_KEY8;
							break;
						case 9:
							key = IRKeyValue.KEY_STB_KEY9;
							break;
						}
					}
					if (key == 0)
						continue;
					byte[] keyValue = device.GetKeyValue(key);
					if (keyValue == null)
						continue;
					ETGlobal.mTg.write(keyValue, keyValue.length);
					Thread.sleep(400);
				}
				if (item.isOK()) {
					int key = 0;
					if (type == DeviceType.DEVICE_REMOTE_TV) {
						key = IRKeyValue.KEY_TV_OK;
					} else if (type == DeviceType.DEVICE_REMOTE_STB) {
						key = IRKeyValue.KEY_STB_OK;
					} else if (type == DeviceType.DEVICE_REMOTE_IPTV) {
						key = IRKeyValue.KEY_IPTV_OK;
					}
					if (key == 0)
						return;
					byte[] keyValue = device.GetKeyValue(key);
					if (keyValue == null)
						return;
					ETGlobal.mTg.write(keyValue, keyValue.length);
					Thread.sleep(400);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// private class ItemLongClickListener implements OnItemLongClickListener {
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
			return mWatchTVList.size();
		}

		@Override
		public Object getItem(int position) {

			return mWatchTVList.get(position);
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
			AdapterWatchTVItem item = mWatchTVList.get(position);
			holder.image_grid_item_res
					.setImageResource(ETGlobal.mWatchTVImages[item.getRes()]);
			holder.text_grid_item_name.setText(item.getName());
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
		// FragmentDevice fragmentDevice = new FragmentDevice();
		// Bundle args = new Bundle();
		// args.putInt("group", 0);
		// fragmentDevice.setArguments(args);
		// FragmentTransaction transaction = getActivity()
		// .getSupportFragmentManager().beginTransaction();
		// transaction
		// .setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out,
		// R.anim.push_left_in, R.anim.push_left_out);
		//
		// transaction.replace(R.id.fragment_container, fragmentDevice);
		// // transactionBt.addToBackStack(null);
		// // transaction
		// // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		// transaction.commit();

		FragmentGroup fragmentGroup = new FragmentGroup();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragmentGroup);
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
