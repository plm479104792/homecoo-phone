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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FragmentWizardsOne2Two extends SherlockFragment implements OnClickListener,
		IBack {
	private RecvReceiver mReceiver;
	private int mGroupIndex;
	private int mType;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mGroupIndex = this.getArguments().getInt("group");
		mType = this.getArguments().getInt("type");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wizards_one2two, container, false);

		Button buttonBrand = (Button) view.findViewById(R.id.button_brand);
		buttonBrand.setOnClickListener(this);

		Button buttonStudy = (Button) view.findViewById(R.id.button_study);
		buttonStudy.setOnClickListener(this);
		Button buttonHand	 = (Button) view.findViewById(R.id.button_hand);
		buttonHand.setOnClickListener(this);
		Button buttonFast = (Button) view.findViewById(R.id.button_fast);
		buttonFast.setOnClickListener(this);
		Button buttonDIY = (Button) view.findViewById(R.id.button_diy);
		buttonDIY.setOnClickListener(this);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		getActivity().registerReceiver(mReceiver, filter);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
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
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
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
		}else if (id==R.id.button_hand) {
			args.putInt("type", mType);
			args.putInt("group", mGroupIndex);
			FragmentWizardsTwo fragmentWizardsTwo = new FragmentWizardsTwo();
			fragmentWizardsTwo.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentWizardsTwo);
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
		}else if (id==R.id.button_diy) {
			args.putInt("group", mGroupIndex);
			FragmentWizardsSeven fragmentWizardsSeven = new FragmentWizardsSeven();
			fragmentWizardsSeven.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentWizardsSeven);
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
//		case R.id.button_hand:
//			args.putInt("type", mType);
//			args.putInt("group", mGroupIndex);
//			FragmentWizardsTwo fragmentWizardsTwo = new FragmentWizardsTwo();
//			fragmentWizardsTwo.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragmentWizardsTwo);
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
//		case R.id.button_diy:
//			args.putInt("group", mGroupIndex);
//			FragmentWizardsSeven fragmentWizardsSeven = new FragmentWizardsSeven();
//			fragmentWizardsSeven.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragmentWizardsSeven);
//			transaction.addToBackStack(null);
//			transaction.commit();
//			break;
//		}
	}

	public class RecvReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ETGlobal.BROADCAST_APP_BACK)){
				Back();
			}
		}
	}

	@Override
	public void Back() {
		Bundle args = new Bundle();
		args.putInt("group", mGroupIndex);
		FragmentWizardsOne fragmentWizardsOne = new FragmentWizardsOne();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		fragmentWizardsOne.setArguments(args);
		transaction.replace(R.id.fragment_container, fragmentWizardsOne);
		transaction.addToBackStack(null);
		transaction.commit();
	};
}
