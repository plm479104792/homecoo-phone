package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDeviceDVD;
import et.song.etclass.ETGroup;
import et.song.etclass.ETKey;
import et.song.etclass.ETPage;
import et.song.etclass.ETSave;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remote.face.IRKeyValue;
import et.song.remotestar.hxd.sdk.R;
import et.song.tool.ETTool;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public class FragmentDVD extends SherlockFragment implements OnClickListener, OnLongClickListener, IBack {
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
	private String operateType;
	private String operateName;  //操作名称
	
	private int mGroupIndex;
	private int mDeviceIndex;
	private ETGroup mGroup = null;
	private ETDeviceDVD mDevice = null;
	private RecvReceiver mReceiver;
	private boolean mIsModity = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		// 获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getActivity().getSharedPreferences("tuwa", Context.MODE_PRIVATE);
		operateType = preferences.getString("OPERATION_TYPE","");
		
		mGroupIndex = this.getArguments().getInt("group");
		mDeviceIndex = this.getArguments().getInt("device");
		ETSave.getInstance(getActivity()).put("GroupIndex", String.valueOf(mGroupIndex));
		ETSave.getInstance(getActivity()).put("DeviceIndex", String.valueOf(mDeviceIndex));
		ETSave.getInstance(getActivity()).put("DeviceType", String.valueOf(DeviceType.DEVICE_REMOTE_DVD));
		mGroup = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = (ETDeviceDVD) mGroup.GetItem(mDeviceIndex);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_dvd, container, false);

		TextView textViewPower = (TextView) view
				.findViewById(R.id.text_dvd_power);
		textViewPower.setOnClickListener(this);
		textViewPower.setOnLongClickListener(this);
		textViewPower.setBackgroundResource(R.drawable.ic_power_selector);
		textViewPower.getLayoutParams().width = (ETGlobal.W - 80) / 5;
		textViewPower.getLayoutParams().height = (ETGlobal.W - 80) / 5;
		TextView textViewOC = (TextView) view.findViewById(R.id.text_dvd_oc);
		textViewOC.setOnClickListener(this);
		textViewOC.setOnLongClickListener(this);
		textViewOC.setBackgroundResource(R.drawable.ic_button_round_selector);
		textViewOC.getLayoutParams().width = (ETGlobal.W - 80) / 5;
		textViewOC.getLayoutParams().height = (ETGlobal.W - 80) / 5;
		
		TextView textViewPause = (TextView) view
				.findViewById(R.id.text_dvd_pause);
		textViewPause.setOnClickListener(this);
		textViewPause.setOnLongClickListener(this);
		textViewPause.setBackgroundResource(R.drawable.btn_style_white);
		textViewPause.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewPause.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewPlay = (TextView) view
				.findViewById(R.id.text_dvd_play);
		textViewPlay.setOnClickListener(this);
		textViewPlay.setOnLongClickListener(this);
		textViewPlay.setBackgroundResource(R.drawable.btn_style_white);
		textViewPlay.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewPlay.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewStop = (TextView) view
				.findViewById(R.id.text_dvd_stop);
		textViewStop.setOnClickListener(this);
		textViewStop.setOnLongClickListener(this);
		textViewStop.setBackgroundResource(R.drawable.btn_style_white);
		textViewStop.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewStop.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewMute = (TextView) view
				.findViewById(R.id.text_dvd_mute);
		textViewMute.setOnClickListener(this);
		textViewMute.setOnLongClickListener(this);
		textViewMute.setBackgroundResource(R.drawable.btn_style_white);
		textViewMute.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewMute.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewUpSong = (TextView) view
				.findViewById(R.id.text_dvd_up_song);
		textViewUpSong.setOnClickListener(this);
		textViewUpSong.setOnLongClickListener(this);
		textViewUpSong.setBackgroundResource(R.drawable.btn_style_white);
		textViewUpSong.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewUpSong.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewDownSong = (TextView) view
				.findViewById(R.id.text_dvd_down_song);
		textViewDownSong.setOnClickListener(this);
		textViewDownSong.setOnLongClickListener(this);
		textViewDownSong.setBackgroundResource(R.drawable.btn_style_white);
		textViewDownSong.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewDownSong.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewFastForward = (TextView) view
				.findViewById(R.id.text_dvd_fast_forward);
		textViewFastForward.setOnClickListener(this);
		textViewFastForward.setOnLongClickListener(this);
		textViewFastForward.setBackgroundResource(R.drawable.btn_style_white);
		textViewFastForward.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewFastForward.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewFastBack = (TextView) view
				.findViewById(R.id.text_dvd_fast_back);
		textViewFastBack.setOnClickListener(this);
		textViewFastBack.setOnLongClickListener(this);
		textViewFastBack.setBackgroundResource(R.drawable.btn_style_white);
		textViewFastBack.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewFastBack.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewTitle = (TextView) view
				.findViewById(R.id.text_dvd_title);
		textViewTitle.setOnClickListener(this);
		textViewTitle.setOnLongClickListener(this);
		textViewTitle.setBackgroundResource(R.drawable.btn_style_white);
		textViewTitle.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewTitle.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewStandard = (TextView) view
				.findViewById(R.id.text_dvd_standard);
		textViewStandard.setOnClickListener(this);
		textViewStandard.setOnLongClickListener(this);
		textViewStandard.setBackgroundResource(R.drawable.btn_style_white);
		textViewStandard.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewStandard.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewMenu = (TextView) view
				.findViewById(R.id.text_dvd_menu);
		textViewMenu.setOnClickListener(this);
		textViewMenu.setOnLongClickListener(this);
		textViewMenu.setBackgroundResource(R.drawable.btn_style_white);
		textViewMenu.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewMenu.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewBack = (TextView) view
				.findViewById(R.id.text_dvd_back);
		textViewBack.setOnClickListener(this);
		textViewBack.setOnLongClickListener(this);
		textViewBack.setBackgroundResource(R.drawable.btn_style_white);
		textViewBack.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewBack.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewOk = (TextView) view.findViewById(R.id.text_dvd_ok);
		textViewOk.setOnClickListener(this);
		textViewOk.setOnLongClickListener(this);
		textViewOk.setBackgroundResource(R.drawable.ic_button_ok_selector);

		TextView textViewUp = (TextView) view.findViewById(R.id.text_dvd_up);
		textViewUp.setOnClickListener(this);
		textViewUp.setOnLongClickListener(this);
		textViewUp.setBackgroundResource(R.drawable.ic_button_up_selector);

		TextView textViewDown = (TextView) view
				.findViewById(R.id.text_dvd_down);
		textViewDown.setOnClickListener(this);
		textViewDown.setOnLongClickListener(this);
		textViewDown.setBackgroundResource(R.drawable.ic_button_down_selector);
		TextView textViewLeft = (TextView) view
				.findViewById(R.id.text_dvd_left);
		textViewLeft.setOnClickListener(this);
		textViewLeft.setOnLongClickListener(this);
		textViewLeft.setBackgroundResource(R.drawable.ic_button_left_selector);
		TextView textViewRight = (TextView) view
				.findViewById(R.id.text_dvd_right);
		textViewRight.setOnClickListener(this);
		textViewRight.setOnLongClickListener(this);
		textViewRight
				.setBackgroundResource(R.drawable.ic_button_right_selector);

		textViewOk.getLayoutParams().width = (ETGlobal.W - 80) / 5;
		textViewOk.getLayoutParams().height = (ETGlobal.W - 80) / 5;
		textViewUp.getLayoutParams().width = (ETGlobal.W - 80) / 3;
		textViewUp.getLayoutParams().height = (int) (textViewUp
				.getLayoutParams().width * (5 / 12.0));
		textViewDown.getLayoutParams().width = (ETGlobal.W - 80) / 3;
		textViewDown.getLayoutParams().height = (int) (textViewDown
				.getLayoutParams().width * (5 / 12.0));
		textViewLeft.getLayoutParams().width = textViewDown.getLayoutParams().height;
		textViewLeft.getLayoutParams().height = textViewDown.getLayoutParams().width;
		textViewRight.getLayoutParams().width = textViewDown.getLayoutParams().height;
		textViewRight.getLayoutParams().height = textViewDown.getLayoutParams().width;
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
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Nothing to see here.
		menu.clear();
		inflater.inflate(R.menu.menu_edit, menu);
		for (int i = 0; i < menu.size(); i++){
			MenuItem item = menu.getItem(i);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("Home", "Home");
		int id=item.getItemId();
		if (id==android.R.id.home) {
			Back();
			return true;
		}else if (id==R.id.menu_look) {
		
		}else if (id==R.id.menu_edit) {
			if (item.isChecked()) {
				item.setChecked(false);
				mIsModity = false;
			} else {
				item.setChecked(true);
				mIsModity = true;
				// ETTool.MessageBox(getActivity(), 0.5f,
				// getString(R.string.str_study_start_info_2), true);
			}
			return true;
		}
		
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			Back();
//			return true;
//		case R.id.menu_look:
//			break;
//		case R.id.menu_edit:
//			if (item.isChecked()){
//				item.setChecked(false);
//				mIsModity = false;
//			}
//			else
//			{
//				item.setChecked(true);
//				mIsModity = true;
//				//ETTool.MessageBox(getActivity(), 0.5f, getString(R.string.str_study_start_info_2), true);
//			}
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		byte[] keyValue = null;
		int key = 0;
		
		int id=v.getId();
		if(id==R.id.text_dvd_power){
			key = IRKeyValue.KEY_DVD_POWER;
			operateName="电源开";
		}else if(id==R.id.text_dvd_oc){
			key = IRKeyValue.KEY_DVD_OC;
			operateName="开关仓";
		}else if(id==R.id.text_dvd_pause){
			key = IRKeyValue.KEY_DVD_PAUSE;
			operateName="暂停";
		}else if(id==R.id.text_dvd_play){
			key = IRKeyValue.KEY_DVD_PLAY;
			operateName="播放";
		}else if(id==R.id.text_dvd_stop){
			key = IRKeyValue.KEY_DVD_STOP;
			operateName="停止";
		}else if(id==R.id.text_dvd_mute){
			key = IRKeyValue.KEY_DVD_MUTE;
			operateName="静音";
		}else if(id==R.id.text_dvd_up_song){
			key = IRKeyValue.KEY_DVD_UP_SONG;
			operateName="上一曲";
		}else if(id==R.id.text_dvd_down_song){
			key = IRKeyValue.KEY_DVD_NEXT_SONG;
			operateName="下一曲";
		}else if(id==R.id.text_dvd_menu){
			key = IRKeyValue.KEY_DVD_MENU;
			operateName="菜单";
		}else if(id==R.id.text_dvd_fast_forward){
			key = IRKeyValue.KEY_DVD_FAST_FORWARD;
			operateName="快进";
		}else if(id==R.id.text_dvd_fast_back){
			key = IRKeyValue.KEY_DVD_FAST_BACK;
			operateName="快退";
		}else if(id==R.id.text_dvd_title){
			key = IRKeyValue.KEY_DVD_TITLE;
			operateName="标题";
		}else if(id==R.id.text_dvd_ok){
			key = IRKeyValue.KEY_DVD_OK;
			operateName="OK";
		}else if(id==R.id.text_dvd_standard){
			key = IRKeyValue.KEY_DVD_STANDARD;
			operateName="制式";
		}else if(id==R.id.text_dvd_back){
			key = IRKeyValue.KEY_DVD_BACK;
			operateName="返回";
		}else if(id==R.id.text_dvd_up){
			key = IRKeyValue.KEY_DVD_UP;
			operateName="向上";
		}else if(id==R.id.text_dvd_down){
			key = IRKeyValue.KEY_DVD_DOWN;
			operateName="向下";
		}else if(id==R.id.text_dvd_left){
			key = IRKeyValue.KEY_DVD_LEFT;
			operateName="向左";
		}else if(id==R.id.text_dvd_right){		
			key = IRKeyValue.KEY_DVD_RIGHT;
			operateName="向右";
		}


		boolean isSend = true;
		try {
			if (key == 0)
				return;
			keyValue = mDevice.GetKeyValue(key);
			if (keyValue == null)
				return;
			if (ETGlobal.mTg == null) {
				isSend = false;
			}
//			int n = ETGlobal.mTg.write(keyValue, keyValue.length);
//			if (n < 0) {
//				isSend = false;
//			}
			
			//0221码值输出打印
			ETGlobal.rprintHexString(keyValue);
			
//			//发送广播消息到SocketService中接收
			String name=mDevice.GetName();

//			String modelName=getModelNameByModel(model);
			int deviceType=mDevice.GetType();
			System.out.println("===当前的设备是==="+name+deviceType);
		
			
			Intent intent = new Intent();  //Itent就是我们要发送的内容
            intent.putExtra("infrared",keyValue);
            if(operateType.equals("SCENE_INFRA_SET")){
            	intent.setAction("ACTION_SCENE_INFRA_SET");
            	intent.putExtra("INFRA_TYPE",deviceType);
            	
            	intent.putExtra("INFRA_MODEL",name+" "+operateName);
   
            	System.out.println("===发送红外设置广播消息==");
            	  
            }else{
            	intent.setAction("send");  
            	System.out.println("===发送红外控制广播消息==");
            }
            getActivity().sendBroadcast(intent);   //发送广播
			
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (!isSend) {
//			Dialog alertDialog = new AlertDialog.Builder(getActivity())
//					.setMessage(R.string.str_study_start_info_6)
//					.setIcon(R.drawable.ic_launcher)
//					.setNegativeButton(R.string.str_buy_no,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO Auto-generated
//									Intent intent = new Intent(
//											ETGlobal.BROADCAST_APP_BUY_NO);
//									getActivity().sendBroadcast(
//											intent);
//								}
//							})
//					.setPositiveButton(R.string.str_buy_yes,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									Intent intent = new Intent(
//											ETGlobal.BROADCAST_APP_BUY_YES);
//									getActivity().sendBroadcast(
//											intent);
//								}
//							}).create();
//			alertDialog.show();
//		}
	}
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		int key = 0;
//		switch (v.getId()) {
//
//		case R.id.text_dvd_power:
//			key = IRKeyValue.KEY_DVD_POWER;
//
//			break;
//		case R.id.text_dvd_oc:
//			key = IRKeyValue.KEY_DVD_OC;
//
//			break;
//		case R.id.text_dvd_pause:
//			key = IRKeyValue.KEY_DVD_PAUSE;
//
//			break;
//		case R.id.text_dvd_play:
//			key = IRKeyValue.KEY_DVD_PLAY;
//
//			break;
//		case R.id.text_dvd_stop:
//			key = IRKeyValue.KEY_DVD_STOP;
//
//			break;
//		case R.id.text_dvd_mute:
//			key = IRKeyValue.KEY_DVD_MUTE;
//
//			break;
//		case R.id.text_dvd_up_song:
//			key = IRKeyValue.KEY_DVD_UP_SONG;
//
//			break;
//		case R.id.text_dvd_down_song:
//			key = IRKeyValue.KEY_DVD_NEXT_SONG;
//
//			break;
//		case R.id.text_dvd_menu:
//			key = IRKeyValue.KEY_DVD_MENU;
//
//			break;
//		case R.id.text_dvd_fast_forward:
//			key = IRKeyValue.KEY_DVD_FAST_FORWARD;
//
//			break;
//		case R.id.text_dvd_fast_back:
//			key = IRKeyValue.KEY_DVD_FAST_BACK;
//
//			break;
//		case R.id.text_dvd_title:
//			key = IRKeyValue.KEY_DVD_TITLE;
//
//			break;
//		case R.id.text_dvd_ok:
//			key = IRKeyValue.KEY_DVD_OK;
//
//			break;
//		case R.id.text_dvd_standard:
//			key = IRKeyValue.KEY_DVD_STANDARD;
//
//			break;
//		case R.id.text_dvd_back:
//			key = IRKeyValue.KEY_DVD_BACK;
//
//			break;
//		case R.id.text_dvd_up:
//			key = IRKeyValue.KEY_DVD_UP;
//
//			break;
//		case R.id.text_dvd_down:
//			key = IRKeyValue.KEY_DVD_DOWN;
//
//			break;
//		case R.id.text_dvd_left:
//			key = IRKeyValue.KEY_DVD_LEFT;
//
//			break;
//		case R.id.text_dvd_right:
//			key = IRKeyValue.KEY_DVD_RIGHT;
//
//			break;
//		}
		if (mIsModity) {
			final int k = key;
			Dialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setMessage(R.string.str_study_info_1)
					.setPositiveButton(R.string.str_other_yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Intent intentStartLearn = new Intent(
											ETGlobal.BROADCAST_START_LEARN);
									intentStartLearn.putExtra("select", "0");
									intentStartLearn.putExtra("key", k);
									getActivity().sendBroadcast(
											intentStartLearn);
								}
							})
					.setNegativeButton(R.string.str_other_no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();

			dialog.setTitle(R.string.str_dialog_set_study);
			dialog.show();
		} else {

		}
		return false;
	}

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
					} else if (select.equals("1")) {

					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else if (action.equals(ETGlobal.BROADCAST_APP_BACK)){
				Back();
			}
			
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
		transaction.replace(R.id.fragment_container, fragmentDevice);
		// transactionBt.addToBackStack(null);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	};
}
