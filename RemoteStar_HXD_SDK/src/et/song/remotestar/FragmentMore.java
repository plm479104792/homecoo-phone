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
public class FragmentMore extends SherlockFragment implements IBack {
	private RecvReceiver mReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		((ActivityMain) getActivity()).HideBottom();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle(R.string.str_more);
		View view = inflater.inflate(R.layout.fragment_more, container, false);

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
	public void Back() {
		// TODO Auto-generated method stub
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
