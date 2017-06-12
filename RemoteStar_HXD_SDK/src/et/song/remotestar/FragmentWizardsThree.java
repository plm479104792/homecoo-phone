package et.song.remotestar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.device.DeviceType;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remotestar.hxd.sdk.R;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentWizardsThree extends SherlockListFragment implements
		 IBack{

	private SideBar mIndexBar;
	private ListView mListView = null;
	private BrandTask mBrandTask = null;
	private int mType;
	private int mGroupIndex;
	private ProgressDialog mProgressDialog = null;
	private AdapterBrandList mAdapter = null;
	private RecvReceiver mReceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mType = this.getArguments().getInt("type");
		mGroupIndex = this.getArguments().getInt("group");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_wizards_three, container,
				false);

		mListView = (ListView) view.findViewById(android.R.id.list);
		mIndexBar = (SideBar) view.findViewById(R.id.sideBar);
		mIndexBar.setListView(mListView);
		TextView dialogText = (TextView) view.findViewById(R.id.text_az);
		mIndexBar.setTextView(dialogText);
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage(getResources().getString(
				R.string.str_loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		if (mBrandTask == null
				|| mBrandTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
			mBrandTask = new BrandTask();
			mBrandTask.execute();
		}


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
		if (!mBrandTask.isCancelled()) {
			mBrandTask.cancel(true);
		}
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
	public void onListItemClick(ListView parent, View v, int position, long id) {
		AdapterPYinItem item = (AdapterPYinItem) parent
				.getItemAtPosition(position);
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("index", item.getPos());
		args.putInt("type", mType);
		args.putInt("group", mGroupIndex);
		FragmentWizardsTypeFinish fragmentFinish = new FragmentWizardsTypeFinish();
		fragmentFinish.setArguments(args);
		transaction.replace(R.id.fragment_container, fragmentFinish);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	class BrandTask extends AsyncTask<String, Void, Void> {
		ArrayList<AdapterPYinItem> items = new ArrayList<AdapterPYinItem>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			switch (mType) {
			case DeviceType.DEVICE_REMOTE_TV:
				String tvs[] = getResources().getStringArray(
						R.array.strs_tv_type);
				int iTv = 0;
				for (String name : tvs) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).contains(
								"zhanghong")) {
							pyin = "changhong";
						}
						Log.i("ETPYin", pyin);
						items.add(new AdapterPYinItem(name, pyin, iTv));
						iTv++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_STB:
			
				String stbs[] = getResources().getStringArray(
						R.array.strs_stb_type);
				int iStb = 0;
				for (String name:stbs) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).contains(
								"zhanghong")) {
							pyin = "changhong";
						}
						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, iStb));
						iStb++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_DVD:
				
				String dvds[] = getResources().getStringArray(
						R.array.strs_dvd_type);
				int iDVD = 0;
				for (String name:dvds) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).contains(
								"zhanghong")) {
							pyin = "changhong";
						}
						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, iDVD));
						iDVD++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_PJT:
			
				String pjts[] = getResources().getStringArray(
						R.array.strs_pjt_type);
				int iPJT = 0;
				for (String name:pjts) {

					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);

						if (pyin.toLowerCase(Locale.getDefault()).contains(
								"zhanghong")) {
							pyin = "changhong";
						}
						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, iPJT));
						iPJT++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_FANS:
			
				String fans[] = getResources().getStringArray(
						R.array.strs_fans_type);
				int iFAN = 0;
				for (String name:fans) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).contains(
								"zhanghong")) {
							pyin = "changhong";
						}
						Log.i("ETPYin", pyin);
						items.add(new AdapterPYinItem(name, pyin, iFAN));
						iFAN++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_IPTV:
				
				String iptvs[] = getResources().getStringArray(
						R.array.strs_iptv_type);
				int iIPTV = 0;
				for (String name:iptvs) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).contains(
								"zhanghong")) {
							pyin = "changhong";
						}

						Log.i("ETPYin", pyin);
						items.add(new AdapterPYinItem(name, pyin, iIPTV));
						iIPTV++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case DeviceType.DEVICE_REMOTE_AIR:
				
				String airs[] = getResources().getStringArray(
						R.array.strs_air_type);
				int iAIR = 0;
				for (String name:airs) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).contains(
								"zhanghong")) {
							pyin = "changhong";
						}
						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, iAIR));
						iAIR++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_LIGHT:
			
				String lights[] = getResources().getStringArray(
						R.array.strs_light_type);
				int iLIGHT = 0;
				for (String name:lights) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						items.add(new AdapterPYinItem(name, pyin, iLIGHT));
						iLIGHT++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_DC:
				
				String dcs[] = getResources().getStringArray(
						R.array.strs_dc_type);
				int iDC = 0;
				for (String name:dcs) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						items.add(new AdapterPYinItem(name, pyin, iDC));
						iDC++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgressDialog.cancel();
			if (!items.isEmpty()) {
				Collections.sort(items, new PinyinComparator());
				mAdapter = new AdapterBrandList(getActivity(), items);
				mListView.setAdapter(mAdapter);
			}
		}
	}



	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentWizardsTwo fragment = new FragmentWizardsTwo();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("type", mType);
		args.putInt("group", mGroupIndex);
		fragment.setArguments(args);
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragment);
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
