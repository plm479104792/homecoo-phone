package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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

public class FragmentWizards extends SherlockFragment implements
		OnClickListener, IBack{
	private RecvReceiver mReceiver;
	private int mGroupIndex;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mGroupIndex = this.getArguments().getInt("group");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getActivity().setTitle(R.string.str_wizards);
		View view = inflater.inflate(R.layout.fragment_wizards, container,
				false);
		Button buttonWizardsOne = (Button) view
				.findViewById(R.id.buttonWizards);
		buttonWizardsOne.setOnClickListener(this);

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
		if (id==R.id.buttonWizards) {
			
//		}
//		switch (v.getId()) {
//		case R.id.buttonWizards:
			
//			FragmentTV fragmentTV = new FragmentTV();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentTV);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
//			FragmentSTB fragmentSTB = new FragmentSTB();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentSTB);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
//			FragmentDVD fragmentDVD = new FragmentDVD();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentDVD);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
//			FragmentFans fragmentFans = new FragmentFans();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentFans);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
//			FragmentPJT fragmentPJT = new FragmentPJT();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentPJT);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
//			FragmentIPTV fragmentIPTV = new FragmentIPTV();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentIPTV);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
//			FragmentLight fragmentLight = new FragmentLight();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentLight);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
//			FragmentAIR fragmentAIR = new FragmentAIR();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentAIR);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
//			Bundle args = new Bundle();
//			args.putInt("type", ETGlobal.ETDEVICE_TYPE_TV);
//			FragmentBrand fragmentBrand = new FragmentBrand();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			fragmentBrand.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragmentBrand);
//			transaction.addToBackStack(null);
//			transaction.commit();
//			
			
//			FragmentCapture fragmentCapture = new FragmentCapture();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentCapture);
//			transaction.addToBackStack(null);
//			transaction.commit();
			
			Bundle args = new Bundle();
			args.putInt("group", mGroupIndex);
			FragmentWizardsOne fragmentWizardsOne = new FragmentWizardsOne();
			FragmentTransaction transaction = getActivity()
					.getSupportFragmentManager().beginTransaction();
			fragmentWizardsOne.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentWizardsOne);
			transaction.addToBackStack(null);
			transaction.commit();
			
//			FragmentWizardsTwo fragmentWizardsTwo = new FragmentWizardsTwo();
//			FragmentTransaction transaction = getActivity()
//					.getSupportFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, fragmentWizardsTwo);
//			transaction.addToBackStack(null);
//			transaction.commit();
//			break;
		}
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentDevice fragmentDevice = new FragmentDevice();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("group", mGroupIndex);
		fragmentDevice.setArguments(args);
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);
		transaction.replace(R.id.fragment_container,
				fragmentDevice);
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
