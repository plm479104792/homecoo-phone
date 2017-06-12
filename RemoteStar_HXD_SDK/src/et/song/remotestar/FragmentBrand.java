package et.song.remotestar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockListFragment;
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

public class FragmentBrand extends SherlockListFragment implements IBack {

	private SideBar mIndexBar;
	private ListView mListView = null;
	private BrandTask mBrandTask = null;
	private int mType;
	private int mGroup;
	private ProgressDialog mProgressDialog = null;
	private AdapterBrandList mAdapter = null;
	private RecvReceiver mReceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mType = this.getArguments().getInt("type");
		mGroup = this.getArguments().getInt("group");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_brand, container, false);

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
		getActivity().unregisterReceiver(mReceiver);
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
		// AdapterItem item = (AdapterItem) parent.getItemAtPosition(position);
		// FragmentTransaction transaction = getActivity()
		// .getSupportFragmentManager().beginTransaction();
		// Bundle args = new Bundle();
		// args.putInt("index", item.getPos());
		// args.putString("name", item.getName());
		// switch (mType) {
		// case DeviceType.DEVICE_REMOTE_TV:
		// FragmentStepKnowTV fragmentStepKnowTV = new FragmentStepKnowTV();
		// fragmentStepKnowTV.setArguments(args);
		// transaction.replace(R.id.fragment_container, fragmentStepKnowTV);
		// transaction.addToBackStack(null);
		// transaction.commit();
		// break;
		// case DeviceType.DEVICE_REMOTE_STB:
		// FragmentStepKnowSTB fragmentStepKnowSTB = new FragmentStepKnowSTB();
		// fragmentStepKnowSTB.setArguments(args);
		// transaction.replace(R.id.fragment_container, fragmentStepKnowSTB);
		// transaction.addToBackStack(null);
		// transaction.commit();
		// break;
		// case DeviceType.DEVICE_REMOTE_DVD:
		// FragmentStepKnowDVD fragmentStepKnowDVD = new FragmentStepKnowDVD();
		// fragmentStepKnowDVD.setArguments(args);
		// transaction.replace(R.id.fragment_container, fragmentStepKnowDVD);
		// transaction.addToBackStack(null);
		// transaction.commit();
		// break;
		//
		// case DeviceType.DEVICE_REMOTE_PJT:
		// FragmentStepKnowPJT fragmentStepKnowPJT = new FragmentStepKnowPJT();
		// fragmentStepKnowPJT.setArguments(args);
		// transaction.replace(R.id.fragment_container, fragmentStepKnowPJT);
		// transaction.addToBackStack(null);
		// transaction.commit();
		// break;
		// case DeviceType.DEVICE_REMOTE_FANS:
		// FragmentStepKnowFANS fragmentStepKnowFANS = new
		// FragmentStepKnowFANS();
		// fragmentStepKnowFANS.setArguments(args);
		// transaction.replace(R.id.fragment_container, fragmentStepKnowFANS);
		// transaction.addToBackStack(null);
		// transaction.commit();
		// break;
		// case DeviceType.DEVICE_REMOTE_AIR:
		// FragmentStepKnowAir fragmentStepKnowAir = new FragmentStepKnowAir();
		// fragmentStepKnowAir.setArguments(args);
		// transaction.replace(R.id.fragment_container, fragmentStepKnowAir);
		// transaction.addToBackStack(null);
		// transaction.commit();
		// break;
		// }
		// ETTool.Vibrate(getActivity(), 100);
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

			int len = 0;
			switch (mType) {
			case DeviceType.DEVICE_REMOTE_TV:
				len = getResources().getStringArray(R.array.strs_tv_brand).length;
				for (int i = 0; i < len; i++) {
					String name = getResources().getStringArray(
							R.array.strs_tv_brand)[i];

					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhangcheng(changcheng)")) {
							pyin = "changcheng";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhangfei(changfei)")) {
							pyin = "changfei";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhangfeng(changfeng)")) {
							pyin = "changfeng";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhanghai(changhai)")) {
							pyin = "changhai";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhanghong(changhong)")) {
							pyin = "changhong";
						}
						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case DeviceType.DEVICE_REMOTE_STB:
				len = getResources().getStringArray(R.array.strs_stb_brand).length;
				for (int i = 0; i < len; i++) {
					String name = getResources().getStringArray(
							R.array.strs_stb_brand)[i];

					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhanghongjidinghe(changhong stb)")) {
							pyin = "changhongjidinghe";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhongqing(chongqing)")) {
							pyin = "chongqing";
						}

						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_DVD:
				len = getResources().getStringArray(R.array.strs_dvd_brand).length;
				for (int i = 0; i < len; i++) {
					String name = getResources().getStringArray(
							R.array.strs_dvd_brand)[i];

					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhanghong(changhong)")) {
							pyin = "changhong";
						}
						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_PJT:
				len = getResources().getStringArray(R.array.strs_pjt_brand).length;
				for (int i = 0; i < len; i++) {
					String name = getResources().getStringArray(
							R.array.strs_pjt_brand)[i];

					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);

						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_FANS:
				len = getResources().getStringArray(R.array.strs_fans_brand).length;
				for (int i = 0; i < len; i++) {
					String name = getResources().getStringArray(
							R.array.strs_fans_brand)[i];
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhangcheng(changcheng)")) {
							pyin = "changcheng";
						}

						Log.i("ETPYin", pyin);
						items.add(new AdapterPYinItem(name, pyin, i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_IPTV:
				len = getResources().getStringArray(R.array.strs_iptv_brand).length;
				for (int i = 0; i < len; i++) {
					String name = getResources().getStringArray(
							R.array.strs_iptv_brand)[i];
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhangcheng(changcheng)")) {
							pyin = "changcheng";
						}

						Log.i("ETPYin", pyin);
						items.add(new AdapterPYinItem(name, pyin, i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case DeviceType.DEVICE_REMOTE_AIR:
				len = getResources().getStringArray(R.array.strs_air_brand).length;
				for (int i = 0; i < len; i++) {
					String name = getResources().getStringArray(
							R.array.strs_air_brand)[i];
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhanghong(changhong)")) {
							pyin = "changhong";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhangling(changling)")) {
							pyin = "changling";
						}
						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_LIGHT:
				len = getResources().getStringArray(R.array.strs_light_brand).length;
				for (int i = 0; i < len; i++) {
					String name = getResources().getStringArray(
							R.array.strs_light_brand)[i];
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);

						items.add(new AdapterPYinItem(name, pyin, i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_DC:
				len = getResources().getStringArray(R.array.strs_dc_brand).length;
				for (int i = 0; i < len; i++) {
					String name = getResources().getStringArray(
							R.array.strs_dc_brand)[i];
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						items.add(new AdapterPYinItem(name, pyin, i));
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
		FragmentWizardsThree fragmentThree = new FragmentWizardsThree();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("pos", mGroup);
		fragmentThree.setArguments(args);
		transaction.replace(R.id.fragment_container, fragmentThree);
		transaction.addToBackStack(null);
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
