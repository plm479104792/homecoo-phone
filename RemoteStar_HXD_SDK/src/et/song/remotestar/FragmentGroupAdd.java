package et.song.remotestar;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.etclass.ETGroup;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FragmentGroupAdd extends SherlockFragment implements IBack {
	private GridView mGridView;
	private List<ETGroup> mGroupList = new ArrayList<ETGroup>();
	private RecvReceiver mReceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		String[] groups = getResources().getStringArray(R.array.strs_group);
		for (int i = 0; i < ETGlobal.mGroupTypes.length - 1; i++) {
			ETGroup group = new ETGroup();
			group.SetName(groups[i]);
			group.SetType(ETGlobal.mGroupTypes[i]);
			group.SetRes(i);
			mGroupList.add(group);
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getActivity().setTitle(R.string.str_group_add);
		View view = inflater.inflate(R.layout.fragment_group_add, container,
				false);
		mGridView = (GridView) view.findViewById(R.id.grid);
//		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridView.setBackgroundColor(Color.TRANSPARENT);
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

	@SuppressLint("InflateParams")
	private void Group(final ETGroup group) throws Exception {
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
								group.Inster(ETDB.getInstance(getActivity()));
								FragmentGroup fragmentGroup = new FragmentGroup();
								FragmentTransaction transactionGroup = getActivity()
										.getSupportFragmentManager()
										.beginTransaction();
								transactionGroup.replace(
										R.id.fragment_container, fragmentGroup);
								transactionGroup.addToBackStack(null);
								transactionGroup.commit();
							}
						})
				.setNegativeButton(R.string.str_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();

		DialogSetName.setTitle(R.string.str_dialog_set_name_title);
		DialogSetName.show();
	}
	private class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int pos,
				long arg3) {
			ETGroup group = (ETGroup) arg0.getItemAtPosition(pos);
			try {
				Group(group);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			return mGroupList.size();
		}

		@Override
		public Object getItem(int position) {

			return mGroupList.get(position);
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

			ETGroup group = mGroupList.get(position);
			holder.image_grid_item_res
					.setImageResource(ETGlobal.mGroupImages[group.GetRes()]);
			holder.text_grid_item_name.setText(group.GetName());
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
		FragmentGroup fragmentGroup = new FragmentGroup();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);

		transaction.replace(R.id.fragment_container,
				fragmentGroup);
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
