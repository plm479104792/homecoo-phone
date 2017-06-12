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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentWizardsTwo extends SherlockListFragment implements OnClickListener,IBack {

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
		mType = this.getArguments().getInt("type");
		mGroupIndex = getArguments().getInt("group");
		Log.i("GroupIndex", String.valueOf(mGroupIndex));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_wizards_two,
				container, false);
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
		Button buttonBrand = (Button) view.findViewById(R.id.button_brand);
		buttonBrand.setOnClickListener(this);
		Button buttonStudy = (Button) view.findViewById(R.id.button_study);
		buttonStudy.setOnClickListener(this);
		Button buttonFast = (Button) view.findViewById(R.id.button_fast);
		buttonFast.setOnClickListener(this);
		
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Nothing to see here.
		menu.clear();
	}
	@Override
	public void onStop() {
		super.onStop();
		if (!mBrandTask.isCancelled())
		{
			mBrandTask.cancel(true);
		}
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
		AdapterPYinItem item = (AdapterPYinItem) parent
				.getItemAtPosition(position);
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Fragment fragment = null;
		Bundle args = new Bundle();
		args.putInt("index", item.getPos());
		args.putString("name", item.getName());
		args.putInt("type", mType);
		args.putInt("group", mGroupIndex);
		fragment = new FragmentWizardsFour();
		fragment.setArguments(args);
		transaction.replace(R.id.fragment_container, fragment);
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
			Log.i("Type", String.valueOf(mType));
			switch (mType) {
			case DeviceType.DEVICE_REMOTE_TV:
				String tvs[] = getResources().getStringArray(
						R.array.strs_tv_brand);
				int iTV = 0;
				for (String name : tvs) {

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

						items.add(new AdapterPYinItem(name, pyin, iTV));
						iTV++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_STB:
				String stbs[] = getResources().getStringArray(
						R.array.strs_stb_brand);
				int iSTB = 0;
				for (String name:stbs) {

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

						items.add(new AdapterPYinItem(name, pyin, iSTB));
						iSTB++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case DeviceType.DEVICE_REMOTE_DVD:
				String dvds[] = getResources().getStringArray(
						R.array.strs_dvd_brand);
				int iDVD = 0;
				for (String name:dvds) {

					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhanghong(changhong)")) {
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
						R.array.strs_pjt_brand);
				int iPJT = 0;
				for (String name:pjts) {

					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);

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
						R.array.strs_fans_brand);
				int iFAN = 0;
				for (String name:fans) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhangcheng(changcheng)")) {
							pyin = "changcheng";
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
						R.array.strs_iptv_brand);
				int iIPTV = 0;
				for (String name:iptvs) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhangcheng(changcheng)")) {
							pyin = "changcheng";
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
						R.array.strs_air_brand);
				int iAIR = 0;
				for (String name:airs) {
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
						R.array.strs_light_brand);
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
						R.array.strs_dc_brand);
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
	public void onClick(View v) {
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		// TODO Auto-generated method stub
		int id=v.getId();
		if (id==R.id.button_brand) {
			args.putInt("type", mType);
			args.putInt("group", mGroupIndex);
			FragmentWizardsThree fragmentWizardsThree = new FragmentWizardsThree();
			fragmentWizardsThree.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentWizardsThree);
			transaction.addToBackStack(null);
			transaction.commit();
		}else if (id==R.id.button_study) {
			args.putInt("type", mType);
			args.putInt("group", mGroupIndex);
			FragmentWizardsSix fragmentWizardsSix = new FragmentWizardsSix();
			fragmentWizardsSix.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentWizardsSix);
			transaction.addToBackStack(null);
			transaction.commit();
		}else if (id==R.id.button_fast) {
			args.putInt("type", mType);
			args.putInt("group", mGroupIndex);
			FragmentWizardsEight fragmentWizardsEight = new FragmentWizardsEight();
			fragmentWizardsEight.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentWizardsEight);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		
		
		
//		switch (v.getId()) {
//		case R.id.button_brand:
//			args.putInt("type", mType);
//			args.putInt("group", mGroupIndex);
//			FragmentWizardsThree fragmentWizardsThree = new FragmentWizardsThree();
//			fragmentWizardsThree.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragmentWizardsThree);
//			transaction.addToBackStack(null);
//			transaction.commit();
//			break;
//		case R.id.button_study:
//			args.putInt("type", mType);
//			args.putInt("group", mGroupIndex);
//			FragmentWizardsSix fragmentWizardsSix = new FragmentWizardsSix();
//			fragmentWizardsSix.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragmentWizardsSix);
//			transaction.addToBackStack(null);
//			transaction.commit();
//			break;
//		case R.id.button_fast:
//			args.putInt("type", mType);
//			args.putInt("group", mGroupIndex);
//			FragmentWizardsEight fragmentWizardsEight = new FragmentWizardsEight();
//			fragmentWizardsEight.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragmentWizardsEight);
//			transaction.addToBackStack(null);
//			transaction.commit();
//			break;
//		}
	}
	
	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentWizardsOne fragment = new FragmentWizardsOne();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
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
