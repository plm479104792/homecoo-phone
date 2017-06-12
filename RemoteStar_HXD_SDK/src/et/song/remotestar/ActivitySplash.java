package et.song.remotestar;

import et.song.db.ETDB;
import et.song.etclass.ETPage;
import et.song.remotestar.hxd.sdk.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ActivitySplash extends Activity {
	private Activity mActivity = null;
	private SplashTask mTaskSplash = null;
	//private RecvReceiver mReceiver;
	private ProgressBar mProgressBarLoad = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mActivity = this;
		ETPage.getInstance(this).Load(ETDB.getInstance(this));

		mProgressBarLoad = (ProgressBar) findViewById(R.id.progress_splash_load);

	}

	@Override
	public void onStart() {
		super.onStart();
//		mReceiver = new RecvReceiver();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(ETUSB.ACTION_USB_PERMISSION);
//		registerReceiver(mReceiver, filter);
		Splash();
	}

	@Override
	public void onStop() {
		super.onStop();
		//unregisterReceiver(mReceiver);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		mTaskSplash.cancel(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private void Splash() {
		if (mTaskSplash == null
				|| mTaskSplash.getStatus().equals(AsyncTask.Status.FINISHED)) {
			mTaskSplash = new SplashTask();
			mTaskSplash.execute();
		}
	}

	class SplashTask extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {		
			
			for (int i = 0; i < 100; i++){
				mProgressBarLoad.setProgress(i + 1);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Intent mainIntent = new Intent(mActivity, ActivityMain.class);
			mActivity.startActivity(mainIntent);
			mActivity.finish();
		}
	}

	

	public class RecvReceiver extends BroadcastReceiver {
		@SuppressLint({ "InlinedApi", "NewApi" })
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i("Action", action);
		}
	}
}
