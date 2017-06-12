package et.song.remotestar;


import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevice;
import et.song.etclass.ETGroup;
import et.song.etclass.ETKey;
import et.song.etclass.ETPage;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remote.face.IRKeyValue;
import et.song.remotestar.hxd.sdk.R;
import et.song.tool.ETTool;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentWizardsFive extends SherlockFragment implements
		OnClickListener, IBack {
	private View[] mViews = null;
	private int mType;
	private int mGroupIndex;
//	private StudyTask mStudyTask = null;
	private ETDevice mDevice = null;
	private RecvReceiver mReceiver;
//	private boolean mIsLock = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mType = this.getArguments().getInt("type");
		mGroupIndex = this.getArguments().getInt("group");
		ETGroup group = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = ETDevice.Builder(mType);
		mDevice.SetGID(group.GetID());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getActivity().setTitle(R.string.str_wizards);
		View view = inflater.inflate(R.layout.fragment_wizards_five, container,
				false);
		Button buttonStartStudy = (Button) view
				.findViewById(R.id.button_start_study);
		buttonStartStudy.setOnClickListener(this);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETGlobal.BROADCAST_PASS_LEARN);
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		getActivity().registerReceiver(mReceiver, filter);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
//		if (mStudyTask != null && !mStudyTask.isCancelled())
//		{
//			mStudyTask.cancel(true);
//		}
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        int id=v.getId();
        if (id==R.id.button_start_study) {
//		}
//		switch (v.getId()) {
//		case R.id.button_start_study:
			LinearLayout layout = new LinearLayout(getActivity());
			layout.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			layout.setLayoutParams(layoutParams);
			switch (mType) {
			case DeviceType.DEVICE_REMOTE_TV:
				mViews = new TextView[IRKeyValue.TV_KEY_COUNT];
				LinearLayout lt = null;
				for (int i = 0; i < mViews.length; i++) {
					if (i % 5 == 0) {
						if (lt != null) {
							layout.addView(lt);
						}
						lt = new LinearLayout(getActivity());
						lt.setGravity(Gravity.CENTER);
						lt.setOrientation(LinearLayout.HORIZONTAL);
					}
					TextView view = new TextView(getActivity());

					view.setId(IRKeyValue.TVValue | (i * 2 + 1));
					view.setTag(IRKeyValue.TVValue | (i * 2 + 1));
					view.setText(getResources().getStringArray(
							R.array.strs_tv_key)[i]);
					view.setLayoutParams(new LinearLayout.LayoutParams(
							ETGlobal.W / 6, ETGlobal.W / 7));
					view.setTextSize(12);
					view.setOnClickListener((OnClickListener) this);
					view.setGravity(Gravity.CENTER);
					view.setBackgroundResource(R.drawable.ic_button_cast_up_bg);
					lt.addView(view);
					mViews[i] = view;
				}
				layout.addView(lt);
				break;
			case DeviceType.DEVICE_REMOTE_IPTV:

				break;
			case DeviceType.DEVICE_REMOTE_STB:

				break;
			case DeviceType.DEVICE_REMOTE_DVD:

				break;
			case DeviceType.DEVICE_REMOTE_PJT:

				break;
			case DeviceType.DEVICE_REMOTE_LIGHT:

				break;
			case DeviceType.DEVICE_REMOTE_FANS:

				break;
			case DeviceType.DEVICE_REMOTE_AIR:
				// view = mInflater.inflate(R.layout.fragment_wizards_five_air,
				// null);
				// TextView textViewTempAdd = (TextView) view
				// .findViewById(R.id.text_air_tempadd);
				// textViewTempAdd.setOnClickListener(this);
				// textViewTempAdd.setBackgroundResource(R.drawable.ic_button_cast_selector);
				//
				// TextView textViewMode = (TextView) view
				// .findViewById(R.id.text_air_mode);
				// textViewMode.setOnClickListener(this);
				// textViewMode.setBackgroundResource(R.drawable.ic_button_cast_selector);
				break;
			case DeviceType.DEVICE_REMOTE_DC:
				break;
			}
			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.str_other_study)
					.setMessage(R.string.str_wizards_info_5_1).setView(layout)
					.create();
			dialog.show();
//			if (mStudyTask == null
//					|| mStudyTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
//				mStudyTask = new StudyTask();
//				mStudyTask.execute();
//			}
//			break;
		}
	}

//	class StudyTask extends AsyncTask<String, Void, Void> {
//		ArrayList<AdapterPYinItem> items = new ArrayList<AdapterPYinItem>();
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//		}
//
//		@Override
//		protected Void doInBackground(String... params) {
//
//			switch (mType) {
//			case DeviceType.DEVICE_REMOTE_TV:
//				mDevice.SetName(getResources().getStringArray(
//						R.array.strs_device)[0]);
//				mDevice.SetType(DeviceType.DEVICE_REMOTE_TV);
//				mDevice.SetRes(0);
//				for (int i = 0; i < mViews.length; i++) {
//					mViews[i].setBackgroundResource(R.drawable.ic_button_cast_down_bg);
//					Intent intentStartLearn = new Intent(
//							ETGlobal.BROADCAST_START_LEARN);
//					intentStartLearn.putExtra("select", "0");
//					intentStartLearn.putExtra("key", mViews[i].getId());
//					getActivity().sendBroadcast(intentStartLearn);
//					mIsLock = true;
//					while(mIsLock);
//				}
//				break;
//			case DeviceType.DEVICE_REMOTE_STB:
//
//				break;
//			case DeviceType.DEVICE_REMOTE_DVD:
//
//				break;
//			case DeviceType.DEVICE_REMOTE_PJT:
//
//				break;
//			case DeviceType.DEVICE_REMOTE_FANS:
//
//				break;
//			case DeviceType.DEVICE_REMOTE_IPTV:
//
//				break;
//			case DeviceType.DEVICE_REMOTE_AIR:
//
//				break;
//			case DeviceType.DEVICE_REMOTE_LIGHT:
//
//				break;
//			case DeviceType.DEVICE_REMOTE_DC:
//				break;
//			}
//			mDevice.Inster(ETDB.getInstance(getActivity()));
//			FragmentDevice fragmentDevice = new FragmentDevice();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			Bundle args = new Bundle();
//			args.putInt("group", mGroupIndex);
//			fragmentDevice.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragmentDevice);
//			transaction.addToBackStack(null);
//			transaction.commit();
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//
//		}
//	}

	public class RecvReceiver extends BroadcastReceiver {
		@SuppressLint({ "InlinedApi", "NewApi" })
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ETGlobal.BROADCAST_PASS_LEARN)) {
				try {
					Log.i("Recv", "Recv");
					String select = intent.getStringExtra("select");
					int key = intent.getIntExtra("key", 0);
					String msg = intent.getStringExtra("msg");
					Log.i("Key",
							String.valueOf(ETTool.HexStringToBytes(msg).length));
					if (select.equals("0")) {
						ETKey k = mDevice.GetKeyByValue(key);
						if (k != null) {
							k.SetState(ETKey.ETKEY_STATE_STUDY);
							k.SetValue(ETTool.HexStringToBytes(msg));
							k.Update(ETDB.getInstance(getActivity()));
						}
//						mIsLock = false;
					} else if (select.equals("1")) {

					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
				Back();
			}
		}
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentWizardsTwo fragmentTwo = new FragmentWizardsTwo();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("type", mType);
		args.putInt("group", mGroupIndex);
		fragmentTwo.setArguments(args);
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragmentTwo);
		// transactionBt.addToBackStack(null);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	};
}
