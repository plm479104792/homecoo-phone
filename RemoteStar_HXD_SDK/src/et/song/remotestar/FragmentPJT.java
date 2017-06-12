package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevicePJT;
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

public class FragmentPJT extends SherlockFragment implements OnClickListener, OnLongClickListener, IBack{
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
	private String operateType;
	private String operateName;  //操作名称
	
	
	private int mGroupIndex;
	private int mDeviceIndex;
	private ETGroup mGroup = null;
	private ETDevicePJT mDevice = null;
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
		ETSave.getInstance(getActivity()).put("DeviceType", String.valueOf(DeviceType.DEVICE_REMOTE_PJT));
		mGroup = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = (ETDevicePJT) mGroup.GetItem(mDeviceIndex);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_pjt, container, false);

		
		TextView textViewPowerON = (TextView) view
				.findViewById(R.id.text_pjt_power_on);
		textViewPowerON.setOnClickListener(this);
		textViewPowerON.setOnLongClickListener(this);
		textViewPowerON.setBackgroundResource(R.drawable.ic_button_round_selector);
		textViewPowerON.getLayoutParams().width = (ETGlobal.W - 80) / 5;
		textViewPowerON.getLayoutParams().height = (ETGlobal.W - 80) / 5;
		TextView textViewPowerOFF = (TextView) view
				.findViewById(R.id.text_pjt_power_off);
		textViewPowerOFF.setOnClickListener(this);
		textViewPowerOFF.setOnLongClickListener(this);
		textViewPowerOFF.setBackgroundResource(R.drawable.ic_button_round_selector);
		textViewPowerOFF.getLayoutParams().width = (ETGlobal.W - 80) / 5;
		textViewPowerOFF.getLayoutParams().height = (ETGlobal.W - 80) / 5;
		
		TextView textViewComputer = (TextView) view
				.findViewById(R.id.text_pjt_computer);
		textViewComputer.setOnClickListener(this);
		textViewComputer.setOnLongClickListener(this);
		textViewComputer.setBackgroundResource(R.drawable.btn_style_white);
		textViewComputer.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewComputer.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		TextView textViewVideo = (TextView) view
				.findViewById(R.id.text_pjt_video);
		textViewVideo.setOnClickListener(this);
		textViewVideo.setOnLongClickListener(this);
		textViewVideo.setBackgroundResource(R.drawable.btn_style_white);
		textViewVideo.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewVideo.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		TextView textViewSignal = (TextView) view
				.findViewById(R.id.text_pjt_signal);
		textViewSignal.setOnClickListener(this);
		textViewSignal.setOnLongClickListener(this);
		textViewSignal.setBackgroundResource(R.drawable.btn_style_white);
		textViewSignal.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewSignal.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		TextView textViewMute = (TextView) view
				.findViewById(R.id.text_pjt_mute);
		textViewMute.setOnClickListener(this);
		textViewMute.setOnLongClickListener(this);
		textViewMute.setBackgroundResource(R.drawable.btn_style_white);
		textViewMute.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewMute.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		TextView textViewZoomIn = (TextView) view
				.findViewById(R.id.text_pjt_zoom_in);
		textViewZoomIn.setOnClickListener(this);
		textViewZoomIn.setOnLongClickListener(this);
		textViewZoomIn.setBackgroundResource(R.drawable.ic_button_round_selector);

		
		TextView textViewZoomOut = (TextView) view
				.findViewById(R.id.text_pjt_zoom_out);
		textViewZoomOut.setOnClickListener(this);
		textViewZoomOut.setOnLongClickListener(this);
		textViewZoomOut.setBackgroundResource(R.drawable.ic_button_round_selector);
		
		TextView textViewAuto = (TextView) view
				.findViewById(R.id.text_pjt_auto);
		textViewAuto.setOnClickListener(this);
		textViewAuto.setOnLongClickListener(this);
		textViewAuto.setBackgroundResource(R.drawable.btn_style_white);
		textViewAuto.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewAuto.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		TextView textViewBrightness = (TextView) view
				.findViewById(R.id.text_pjt_brightness);
		textViewBrightness.setOnClickListener(this);
		textViewBrightness.setOnLongClickListener(this);
		textViewBrightness.setBackgroundResource(R.drawable.btn_style_white);
		textViewBrightness.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewBrightness.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		TextView textViewPictureIn = (TextView) view
				.findViewById(R.id.text_pjt_picture_in);		
		textViewPictureIn.setOnClickListener(this);
		textViewPictureIn.setOnLongClickListener(this);
		textViewPictureIn.setBackgroundResource(R.drawable.btn_style_white);
		textViewPictureIn.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewPictureIn.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewPictureOut = (TextView) view
				.findViewById(R.id.text_pjt_picture_out);		
		textViewPictureOut.setOnClickListener(this);
		textViewPictureOut.setOnLongClickListener(this);
		textViewPictureOut.setBackgroundResource(R.drawable.btn_style_white);
		textViewPictureOut.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewPictureOut.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		
		TextView textViewPause = (TextView) view
				.findViewById(R.id.text_pjt_pause);
		textViewPause.setOnClickListener(this);
		textViewPause.setOnLongClickListener(this);
		textViewPause.setBackgroundResource(R.drawable.btn_style_white);
		textViewPause.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewPause.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		TextView textViewExit = (TextView) view
				.findViewById(R.id.text_pjt_exit);
		textViewExit.setOnClickListener(this);
		textViewExit.setOnLongClickListener(this);
		textViewExit.setBackgroundResource(R.drawable.btn_style_white);
		textViewExit.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewExit.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		TextView textViewVolAdd = (TextView) view
				.findViewById(R.id.text_pjt_vol_add);
		textViewVolAdd.setOnClickListener(this);
		textViewVolAdd.setOnLongClickListener(this);
		textViewVolAdd.setBackgroundResource(R.drawable.ic_button_round_selector);
		
		TextView textViewVolSub = (TextView) view
				.findViewById(R.id.text_pjt_vol_sub);
		textViewVolSub.setOnClickListener(this);
		textViewVolSub.setOnLongClickListener(this);
		textViewVolSub.setBackgroundResource(R.drawable.ic_button_round_selector);
		
		TextView textViewMenu = (TextView) view
				.findViewById(R.id.text_pjt_menu);
		textViewMenu.setOnClickListener(this);
		textViewMenu.setOnLongClickListener(this);
		textViewMenu.setBackgroundResource(R.drawable.ic_button_round_selector);
		textViewMenu.getLayoutParams().width = (ETGlobal.W - 80) / 5;
		textViewMenu.getLayoutParams().height = (ETGlobal.W - 80) / 5;
		
		TextView textViewOk = (TextView) view
				.findViewById(R.id.text_pjt_ok);
		
		textViewOk.setOnClickListener(this);
		textViewOk.setOnLongClickListener(this);
		textViewOk.setBackgroundResource(R.drawable.ic_button_ok_selector);
		
		TextView textViewUp = (TextView) view
				.findViewById(R.id.text_pjt_up);
		textViewUp.setOnClickListener(this);
		textViewUp.setOnLongClickListener(this);
		textViewUp.setBackgroundResource(R.drawable.ic_button_up_selector);
		
		TextView textViewDown = (TextView) view
				.findViewById(R.id.text_pjt_down);
		textViewDown.setOnClickListener(this);
		textViewDown.setOnLongClickListener(this);
		textViewDown.setBackgroundResource(R.drawable.ic_button_down_selector);
		
		TextView textViewLeft = (TextView) view
				.findViewById(R.id.text_pjt_left);
		textViewLeft.setOnClickListener(this);
		textViewLeft.setOnLongClickListener(this);
		textViewLeft.setBackgroundResource(R.drawable.ic_button_left_selector);
		
		TextView textViewRight = (TextView) view
				.findViewById(R.id.text_pjt_right);
		textViewRight.setOnClickListener(this);
		textViewRight.setOnLongClickListener(this);
		textViewRight.setBackgroundResource(R.drawable.ic_button_right_selector);

		
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
		if(id==R.id.text_pjt_power_on){
			key = IRKeyValue.KEY_PJT_POWER_ON;
			operateName="电源开";
		}else if(id==R.id.text_pjt_power_off){
			key = IRKeyValue.KEY_PJT_POWER_OFF;
			operateName="电源关";
		}else if(id==R.id.text_pjt_computer){
			key = IRKeyValue.KEY_PJT_COMPUTER;
			operateName="电脑";
		}else if(id==R.id.text_pjt_video){
			key = IRKeyValue.KEY_PJT_VIDEO;
			operateName="视频";
		}else if(id==R.id.text_pjt_signal){
			key = IRKeyValue.KEY_PJT_SIGNAL;
			operateName="信号";
		}else if(id==R.id.text_pjt_mute){
			key = IRKeyValue.KEY_PJT_MUTE;
			operateName="静音";
		}else if(id==R.id.text_pjt_zoom_in){
			key = IRKeyValue.KEY_PJT_ZOOM_IN;
			operateName="变焦+";
		}else if(id==R.id.text_pjt_zoom_out){
			key = IRKeyValue.KEY_PJT_ZOOM_OUT;
			operateName="变焦—";
		}else if(id==R.id.text_pjt_auto){
			key = IRKeyValue.KEY_PJT_AUTOMATIC;
			operateName="自动";
		}else if(id==R.id.text_pjt_brightness){
			key = IRKeyValue.KEY_PJT_BRIGHTNESS;
			operateName="亮度";
		}else if(id==R.id.text_pjt_picture_in){
			key = IRKeyValue.KEY_PJT_PICTURE_IN;
			operateName="画面+";
		}else if(id==R.id.text_pjt_picture_out){
			key = IRKeyValue.KEY_PJT_PICTURE_OUT;
			operateName="画面—";
		}else if(id==R.id.text_pjt_pause){
			key = IRKeyValue.KEY_PJT_PAUSE;
			operateName="暂停";
		}else if(id==R.id.text_pjt_exit){
			key = IRKeyValue.KEY_PJT_EXIT;
			operateName="退出";
		}else if(id==R.id.text_pjt_vol_add){
			key = IRKeyValue.KEY_PJT_VOLUME_IN;
			operateName="音量+";
		}else if(id==R.id.text_pjt_vol_sub){
			key = IRKeyValue.KEY_PJT_VOLUME_OUT;
			operateName="音量—";
		}else if(id==R.id.text_pjt_menu){
			key = IRKeyValue.KEY_PJT_MENU;
			operateName="菜单";
		}else if(id==R.id.text_pjt_up){
			key = IRKeyValue.KEY_PJT_UP;
			operateName="向上";
		}else if(id==R.id.text_pjt_down){
			key = IRKeyValue.KEY_PJT_DOWN;	
			operateName="向下";
		}else if(id==R.id.text_pjt_left){
			key = IRKeyValue.KEY_PJT_LEFT;
			operateName="向左";
		}else if(id==R.id.text_pjt_right){
			key = IRKeyValue.KEY_PJT_RIGHT;
			operateName="向右";
		}else if(id==R.id.text_pjt_ok){
			key = IRKeyValue.KEY_PJT_OK;
			operateName="OK";
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
//		case R.id.text_pjt_power_on:
//			key = IRKeyValue.KEY_PJT_POWER_ON;
//			
//			break;
//		case R.id.text_pjt_power_off:
//			key = IRKeyValue.KEY_PJT_POWER_OFF;
//			
//			break;
//		case R.id.text_pjt_computer:
//			key = IRKeyValue.KEY_PJT_COMPUTER;
//			
//			break;
//		case R.id.text_pjt_video:
//			key = IRKeyValue.KEY_PJT_VIDEO;
//			
//			break;
//		case R.id.text_pjt_signal:
//			key = IRKeyValue.KEY_PJT_SIGNAL;
//			
//			break;
//		case R.id.text_pjt_mute:
//			key = IRKeyValue.KEY_PJT_MUTE;
//			
//			break;
//		case R.id.text_pjt_zoom_in:
//			key = IRKeyValue.KEY_PJT_ZOOM_IN;
//			
//			break;
//		case R.id.text_pjt_zoom_out:
//			key = IRKeyValue.KEY_PJT_ZOOM_OUT;
//			
//			break;
//		case R.id.text_pjt_auto:
//			key = IRKeyValue.KEY_PJT_AUTOMATIC;
//			
//			break;
//		case R.id.text_pjt_brightness:
//			key = IRKeyValue.KEY_PJT_BRIGHTNESS;
//			
//			break;
//		case R.id.text_pjt_picture_in:
//			key = IRKeyValue.KEY_PJT_PICTURE_IN;
//			
//			break;
//		case R.id.text_pjt_picture_out:
//			key = IRKeyValue.KEY_PJT_PICTURE_OUT;
//			
//			break;
//		case R.id.text_pjt_pause:
//			key = IRKeyValue.KEY_PJT_PAUSE;
//			
//			break;
//		case R.id.text_pjt_exit:
//			key = IRKeyValue.KEY_PJT_EXIT;
//			
//			break;
//		case R.id.text_pjt_vol_add:
//			key = IRKeyValue.KEY_PJT_VOLUME_IN;
//			
//			break;
//		case R.id.text_pjt_vol_sub:
//			key = IRKeyValue.KEY_PJT_VOLUME_OUT;
//			
//			break;
//		case R.id.text_pjt_menu:
//			key = IRKeyValue.KEY_PJT_MENU;
//			
//			break;
//		case R.id.text_pjt_up:
//			key = IRKeyValue.KEY_PJT_UP;
//			
//			break;
//		case R.id.text_pjt_down:
//			key = IRKeyValue.KEY_PJT_DOWN;
//			
//			break;
//		case R.id.text_pjt_left:
//			key = IRKeyValue.KEY_PJT_LEFT;
//			
//			break;
//		case R.id.text_pjt_right:
//			key = IRKeyValue.KEY_PJT_RIGHT;
//			
//			break;
//		case R.id.text_pjt_ok:
//			key = IRKeyValue.KEY_PJT_OK;
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
