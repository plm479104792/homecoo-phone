package com.homecoolink.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.adapter.BellChoiceAdapter;
import com.homecoolink.data.SharedPreferencesManager;
import com.homecoolink.data.SystemDataManager;
import com.homecoolink.global.Constants;
import com.homecoolink.utils.T;


public class SettingBellRingActivity extends BaseActivity implements
		OnClickListener {
	Button save_btn;
	ImageView back_btn;
	ListView list_sys_bell;
	Vibrator vibrator;
	MediaPlayer player;
	RelativeLayout set_bellRing_btn, set_sd_bell_btn;
	Context context;
	MyReceiver receiver;
	TextView selectBell;
	boolean myreceiverIsReg = false;
	BellChoiceAdapter adapter;
	int checkedId;
	int selectPos;
	int vibrateState;
	int bellType;

	int settingType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_bell_ring);
		context = this;
		settingType = getIntent().getIntExtra("type", 0);

		initCompent();
		registerMonitor();
		initSelectMusicName();
	}

	public void initCompent() {
		player = new MediaPlayer();
		back_btn = (ImageView) findViewById(R.id.back_btn);
		save_btn = (Button) findViewById(R.id.save);
		set_sd_bell_btn = (RelativeLayout) findViewById(R.id.set_sd_bell_btn);
		selectBell = (TextView) findViewById(R.id.selectBell);
		list_sys_bell = (ListView) findViewById(R.id.list_sys_bell);
		initSelectState();
		ArrayList<HashMap<String, String>> bells = SystemDataManager
				.getInstance().getSysBells(this);
		adapter = new BellChoiceAdapter(this, bells);
		adapter.setCheckedId(checkedId);
		list_sys_bell.setAdapter(adapter);
		list_sys_bell.setSelection(selectPos);
		list_sys_bell.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> data = (HashMap<String, String>) adapter
						.getItem(arg2);
				Log.e("343", "9999==" + data.get("bellId"));
				// if (Integer.parseInt(data.get("bellId"))==R.raw.alarm_music)
				// {
				// int id = Integer.parseInt(data.get("bellId"));
				//
				// checkedId = id;
				// selectPos = arg2;
				// adapter.setCheckedId(id);
				// adapter.notifyDataSetChanged();
				//
				// player=MediaPlayer.create(MainActivity.mContext,R.raw.alarm_music);
				// player.start();
				//
				// }else {
				int id = Integer.parseInt(data.get("bellId"));
				checkedId = id;
				selectPos = arg2;
				adapter.setCheckedId(id);
				adapter.notifyDataSetChanged();
				// if (player.isPlaying()) {
				// player.stop();
				// }
				playMusic(checkedId);
				// }

			}

		});

		set_sd_bell_btn.setOnClickListener(this);
		save_btn.setOnClickListener(this);
		back_btn.setOnClickListener(this);
	}

	public void initSelectState() {
		if (settingType == SettingSystemActivity.SET_TYPE_COMMING_RING) {
			selectPos = SharedPreferencesManager.getInstance()
					.getCBellSelectPos(this);
			bellType = SharedPreferencesManager.getInstance()
					.getCBellType(this);

			if (bellType == SharedPreferencesManager.TYPE_BELL_SYS) {
				checkedId = SharedPreferencesManager.getInstance()
						.getCSystemBellId(this);
				selectBell.setText("");
			} else {
				checkedId = SharedPreferencesManager.getInstance()
						.getCSdBellId(this);
				HashMap<String, String> data = SystemDataManager.getInstance()
						.findSdBellById(context, checkedId);
				if (null != data) {
					selectBell.setText(data.get("bellName"));
				}
				checkedId = -1;
				selectPos = 1;
			}
		} else if (settingType == SettingSystemActivity.SET_TYPE_ALLARM_RING) {
			selectPos = SharedPreferencesManager.getInstance()
					.getABellSelectPos(this);
			bellType = SharedPreferencesManager.getInstance()
					.getABellType(this);

			if (bellType == SharedPreferencesManager.TYPE_BELL_SYS) {
				checkedId = SharedPreferencesManager.getInstance()
						.getASystemBellId(this);
				selectBell.setText("");
			} else {
				checkedId = SharedPreferencesManager.getInstance()
						.getASdBellId(this);
				HashMap<String, String> data = SystemDataManager.getInstance()
						.findSdBellById(context, checkedId);
				if (null != data) {
					selectBell.setText(data.get("bellName"));
				}
				checkedId = -1;
				selectPos = 1;
			}
		}

	}

	public void registerMonitor() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SettingSystemActivity.ACTION_CHANGEBELL);
		receiver = new MyReceiver();
		this.registerReceiver(receiver, filter);
		myreceiverIsReg = true;
	}

	@Override
	public void onClick(View view) {
		int id=view.getId();
		if(id==R.id.back_btn){
			this.finish();
		}else if(id==R.id.save){
			if (checkedId == -1) {
				T.showShort(this, R.string.savebell_error);
			} else {
				if (settingType == SettingSystemActivity.SET_TYPE_COMMING_RING) {
					SharedPreferencesManager.getInstance().putCSystemBellId(
							checkedId, this);
					SharedPreferencesManager.getInstance().putCBellSelectPos(
							selectPos, this);
					SharedPreferencesManager.getInstance().putCBellType(
							SharedPreferencesManager.TYPE_BELL_SYS, this);
					Intent i = new Intent();
					i.setAction(SettingSystemActivity.ACTION_CHANGEBELL);
					sendBroadcast(i);
				} else if (settingType == SettingSystemActivity.SET_TYPE_ALLARM_RING) {
					SharedPreferencesManager.getInstance().putASystemBellId(
							checkedId, this);
					SharedPreferencesManager.getInstance().putABellSelectPos(
							selectPos, this);
					SharedPreferencesManager.getInstance().putABellType(
							SharedPreferencesManager.TYPE_BELL_SYS, this);
					Intent i = new Intent();
					i.setAction(SettingSystemActivity.ACTION_CHANGEBELL);
					sendBroadcast(i);
				}
				this.finish();
			}
		}else if(id==R.id.set_sd_bell_btn){
			Intent go_set_sd_bell = new Intent(this,
					SettingSdBellActivity.class);
			go_set_sd_bell.putExtra("type", settingType);
			startActivity(go_set_sd_bell);
		}
		
		
		// TODO Auto-generated method stub
//		switch (view.getId()) {
//		case R.id.back_btn:
//			this.finish();
//			break;
//		case R.id.save:
//			if (checkedId == -1) {
//				T.showShort(this, R.string.savebell_error);
//			} else {
//				if (settingType == SettingSystemActivity.SET_TYPE_COMMING_RING) {
//					SharedPreferencesManager.getInstance().putCSystemBellId(
//							checkedId, this);
//					SharedPreferencesManager.getInstance().putCBellSelectPos(
//							selectPos, this);
//					SharedPreferencesManager.getInstance().putCBellType(
//							SharedPreferencesManager.TYPE_BELL_SYS, this);
//					Intent i = new Intent();
//					i.setAction(SettingSystemActivity.ACTION_CHANGEBELL);
//					sendBroadcast(i);
//				} else if (settingType == SettingSystemActivity.SET_TYPE_ALLARM_RING) {
//					SharedPreferencesManager.getInstance().putASystemBellId(
//							checkedId, this);
//					SharedPreferencesManager.getInstance().putABellSelectPos(
//							selectPos, this);
//					SharedPreferencesManager.getInstance().putABellType(
//							SharedPreferencesManager.TYPE_BELL_SYS, this);
//					Intent i = new Intent();
//					i.setAction(SettingSystemActivity.ACTION_CHANGEBELL);
//					sendBroadcast(i);
//				}
//				this.finish();
//			}
//			break;
//		case R.id.set_sd_bell_btn:
//			Intent go_set_sd_bell = new Intent(this,
//					SettingSdBellActivity.class);
//			go_set_sd_bell.putExtra("type", settingType);
//			startActivity(go_set_sd_bell);
//			break;
//		}
	}

	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(
					SettingSystemActivity.ACTION_CHANGEBELL)) {
				initSelectMusicName();
				initSelectState();
				list_sys_bell.setSelection(selectPos);
				adapter.setCheckedId(checkedId);
				adapter.notifyDataSetChanged();
			}

		}

	}


	public void initSelectMusicName() {
		if (settingType == SettingSystemActivity.SET_TYPE_COMMING_RING) {
			int cbellType = SharedPreferencesManager.getInstance()
					.getCBellType(this);
			if (cbellType == SharedPreferencesManager.TYPE_BELL_SYS) {
				int bellId = SharedPreferencesManager.getInstance()
						.getCSystemBellId(this);
				HashMap<String, String> data = SystemDataManager.getInstance()
						.findSystemBellById(this, bellId);
				if (null != data) {
					selectBell.setText("");
				}
			} else {
				int bellId = SharedPreferencesManager.getInstance()
						.getCSdBellId(this);
				HashMap<String, String> data = SystemDataManager.getInstance()
						.findSdBellById(this, bellId);
				if (null != data) {
					selectBell.setText(data.get("bellName"));
				}
			}
		} else if (settingType == SettingSystemActivity.SET_TYPE_ALLARM_RING) {
			int abellType = SharedPreferencesManager.getInstance()
					.getABellType(this);
			if (abellType == SharedPreferencesManager.TYPE_BELL_SYS) {
				int bellId = SharedPreferencesManager.getInstance()
						.getASystemBellId(this);
				HashMap<String, String> data = SystemDataManager.getInstance()
						.findSystemBellById(this, bellId);
				if (null != data) {
					selectBell.setText("");
				}
			} else {
				int bellId = SharedPreferencesManager.getInstance()
						.getASdBellId(this);
				HashMap<String, String> data = SystemDataManager.getInstance()
						.findSdBellById(this, bellId);
				if (null != data) {
					selectBell.setText(data.get("bellName"));
				}
			}
		}
	}

	@SuppressWarnings("static-access")
	public void playMusic(int bellId) {

		try {
			player.reset();

			if (bellId == R.raw.alarm_music) {
				Log.e("343", bellId + "==3333333333==" + R.raw.alarm_music);
				// Uri
				// uri=Uri.parse("android:resource://com.homecoolink/raw/alarm_music.mp3");

				// Log.e("343", uri.toString());

				// getResources().openRawResource(R.raw.alarm_music);
				// player = new MediaPlayer();
				// player.create(context,R.raw.alarm_music);
				AssetFileDescriptor fileDescriptor = getAssets().openFd(
						"alarm_music.mp3");
				player.setDataSource(fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				player.prepare();
				player.start();
			} else {
				HashMap<String, String> data;
				data = SystemDataManager.getInstance().findSystemBellById(
						context, bellId);
				String path = data.get("path");
				if (null == path || "".equals(path)) {

				} else {
					player.setDataSource(path);
					player.prepare();
					player.start();
				}
			}

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		player.stop();
		Log.e("343", "onStop()");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (myreceiverIsReg) {
			this.unregisterReceiver(receiver);
		}
		player.stop();
		player.release();
	}
//	private int playPosition;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		if (playPosition > 0 && filePath != null) {
//			playMusic();
//			player.seekTo(playPosition);
//			playPosition = 0;
//		}
//		player.start();
		Log.e("343", "onResume()");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
//		if (player.isPlaying()) {
//			playPosition = player.getCurrentPosition();// 获得当前播放位置
//			player.stop();
//		}
		
		super.onPause();
		player.stop();	
		
		Log.e("343", "onPause()");
	}
	
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_SETTINGBELLRINGACTIVITY;
	}
	
}
