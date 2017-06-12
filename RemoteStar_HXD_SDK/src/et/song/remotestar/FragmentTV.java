package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDeviceTV;
import et.song.etclass.ETGroup;
import et.song.etclass.ETKey;
import et.song.etclass.ETKeyEx;
import et.song.etclass.ETPage;
import et.song.etclass.ETSave;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remote.face.IRKeyValue;
import et.song.remotestar.hxd.sdk.R;
import et.song.remote.face.IRKeyExValue;
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
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentTV extends SherlockFragment implements OnClickListener,
		OnLongClickListener, IBack, OnTouchListener {
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
	
	private int mGroupIndex;
	private int mDeviceIndex;
	private ETGroup mGroup = null;
	private ETDeviceTV mDevice = null;
	private RecvReceiver mReceiver;
	private boolean mIsModity = false;
	private int mLongKey = 0;
	private String operateType;
	private String operateName;  //操作名称

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
		ETSave.getInstance(getActivity()).put("DeviceType", String.valueOf(DeviceType.DEVICE_REMOTE_TV));
		mGroup = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = (ETDeviceTV) mGroup.GetItem(mDeviceIndex);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_tv, container, false);

		TextView textViewPower = (TextView) view
				.findViewById(R.id.text_tv_power);
		textViewPower.setOnClickListener(this);
		textViewPower.setOnLongClickListener(this);
		textViewPower.setBackgroundResource(R.drawable.ic_power_selector);
		textViewPower.getLayoutParams().width = (ETGlobal.W - 80) / 5;
		textViewPower.getLayoutParams().height = (ETGlobal.W - 80) / 5;
		
		TextView textViewHome = (TextView) view
				.findViewById(R.id.text_tv_home);
		textViewHome.setOnClickListener(this);
		textViewHome.setOnLongClickListener(this);
		textViewHome.setBackgroundResource(R.drawable.ic_home_selector);
		textViewHome.getLayoutParams().width = (ETGlobal.W - 80) / 5;
		textViewHome.getLayoutParams().height = (ETGlobal.W - 80) / 5;

		TextView textViewMute = (TextView) view.findViewById(R.id.text_tv_mute);
		textViewMute.setOnClickListener(this);
		textViewMute.setOnLongClickListener(this);
		textViewMute.setBackgroundResource(R.drawable.btn_style_white);
		textViewMute.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewMute.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);

		TextView textViewMenu = (TextView) view.findViewById(R.id.text_tv_menu);
		textViewMenu.setOnClickListener(this);
		textViewMenu.setOnLongClickListener(this);
		textViewMenu.setBackgroundResource(R.drawable.btn_style_white);
		textViewMenu.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewMenu.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);

		TextView textView123 = (TextView) view.findViewById(R.id.text_tv_123);
		textView123.setOnClickListener(this);
		textView123.setBackgroundResource(R.drawable.btn_style_white);
		textView123.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textView123.getLayoutParams().height = (ETGlobal.W - 160) * 3 / (4 * 4);

		TextView textViewBack = (TextView) view.findViewById(R.id.text_tv_back);
		textViewBack.setOnClickListener(this);
		textViewBack.setOnLongClickListener(this);
		textViewBack.setBackgroundResource(R.drawable.btn_style_white);
		textViewBack.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewBack.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);

		TextView textViewOk = (TextView) view.findViewById(R.id.text_tv_ok);
		textViewOk.setOnClickListener(this);
		textViewOk.setOnLongClickListener(this);
		textViewOk.setBackgroundResource(R.drawable.ic_button_ok_selector);


		TextView textViewVolAdd = (TextView) view
				.findViewById(R.id.text_tv_vol_add);
		textViewVolAdd.setOnClickListener(this);
		textViewVolAdd.setOnTouchListener(this);
		textViewVolAdd.setOnLongClickListener(this);
		textViewVolAdd
				.setBackgroundResource(R.drawable.ic_button_round_selector);
		// viewParams =
		// (LinearLayout.LayoutParams)textViewVolAdd.getLayoutParams();
		// viewParams.width = (ETGlobal.W - 80) / 5;
		// viewParams.height = viewParams.width;

		TextView textViewChAdd = (TextView) view
				.findViewById(R.id.text_tv_ch_add);
		textViewChAdd.setOnClickListener(this);
		textViewChAdd.setOnTouchListener(this);
		textViewChAdd.setOnLongClickListener(this);
		textViewChAdd
				.setBackgroundResource(R.drawable.ic_button_round_selector);
		// viewParams =
		// (LinearLayout.LayoutParams)textViewChAdd.getLayoutParams();
		// viewParams.width = (ETGlobal.W - 80) / 5;
		// viewParams.height = viewParams.width;

		TextView textViewVolSub = (TextView) view
				.findViewById(R.id.text_tv_vol_sub);
		textViewVolSub.setOnClickListener(this);
		textViewVolSub.setOnTouchListener(this);
		textViewVolSub.setOnLongClickListener(this);
		textViewVolSub
				.setBackgroundResource(R.drawable.ic_button_round_selector);
		// viewParams =
		// (LinearLayout.LayoutParams)textViewVolSub.getLayoutParams();
		// viewParams.width = (ETGlobal.W - 80) / 5;
		// viewParams.height = viewParams.width;

		TextView textViewChSub = (TextView) view
				.findViewById(R.id.text_tv_ch_sub);
		textViewChSub.setOnClickListener(this);
		textViewChSub.setOnTouchListener(this);
		textViewChSub.setOnLongClickListener(this);
		textViewChSub
				.setBackgroundResource(R.drawable.ic_button_round_selector);
		// viewParams =
		// (LinearLayout.LayoutParams)textViewChSub.getLayoutParams();
		// viewParams.width = (ETGlobal.W - 80) / 5;
		// viewParams.height = viewParams.width;

		TextView textViewUp = (TextView) view.findViewById(R.id.text_tv_up);
		textViewUp.setOnClickListener(this);
		textViewUp.setOnLongClickListener(this);
		textViewUp.setBackgroundResource(R.drawable.ic_button_up_selector);

		TextView textViewDown = (TextView) view.findViewById(R.id.text_tv_down);
		textViewDown.setOnClickListener(this);
		textViewDown.setOnLongClickListener(this);
		textViewDown.setBackgroundResource(R.drawable.ic_button_down_selector);

		TextView textViewLeft = (TextView) view.findViewById(R.id.text_tv_left);
		textViewLeft.setOnClickListener(this);
		textViewLeft.setOnLongClickListener(this);
		textViewLeft.setBackgroundResource(R.drawable.ic_button_left_selector);

		TextView textViewRight = (TextView) view
				.findViewById(R.id.text_tv_right);
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
		filter.addAction(ETGlobal.BROADCAST_KEYCODE_VOLUME_DOWN);
		filter.addAction(ETGlobal.BROADCAST_KEYCODE_VOLUME_UP);
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
		for (int i = 0; i < menu.size(); i++) {
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
//			if (item.isChecked()) {
//				item.setChecked(false);
//				mIsModity = false;
//			} else {
//				item.setChecked(true);
//				mIsModity = true;
//				// ETTool.MessageBox(getActivity(), 0.5f,
//				// getString(R.string.str_study_start_info_2), true);
//			}
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("InflateParams")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		byte[] keyValue = null;
		int key = 0;
		mLongKey = 0;
		
		int id=v.getId();
		if(id==R.id.text_tv_0){
			key = IRKeyValue.KEY_TV_KEY0;
			operateName="频道0";
		}else if(id==R.id.text_tv_1){
			key = IRKeyValue.KEY_TV_KEY1;
			operateName="频道1";
		}else if(id==R.id.text_tv_2){
			key = IRKeyValue.KEY_TV_KEY2;
			operateName="频道2";
		}else if(id==R.id.text_tv_3){
			key = IRKeyValue.KEY_TV_KEY3;
			operateName="频道3";
		}else if(id==R.id.text_tv_4){
			key = IRKeyValue.KEY_TV_KEY4;
			operateName="频道4";
		}else if(id==R.id.text_tv_5){
			key = IRKeyValue.KEY_TV_KEY5;
			operateName="频道5";
		}else if(id==R.id.text_tv_6){
			key = IRKeyValue.KEY_TV_KEY6;
			operateName="频道6";
		}else if(id==R.id.text_tv_7){
			key = IRKeyValue.KEY_TV_KEY7;
			operateName="频道7";
		}else if(id==R.id.text_tv_8){
			key = IRKeyValue.KEY_TV_KEY8;
			operateName="频道8";
		}else if(id==R.id.text_tv_9){
			key = IRKeyValue.KEY_TV_KEY9;
			operateName="频道9";
		}else if(id==R.id.text_tv_av){
			key = IRKeyValue.KEY_TV_AV_TV;
			operateName="AVTV";
		}else if(id==R.id.text_tv_select){
			key = IRKeyValue.KEY_TV_SELECT;
			operateName="TV";
		}else if(id==R.id.text_tv_up){
			key = IRKeyValue.KEY_TV_UP;
			operateName="频道+";
		}else if(id==R.id.text_tv_down){
			key = IRKeyValue.KEY_TV_DOWN;
			operateName="频道-";
		}else if(id==R.id.text_tv_left){
			key = IRKeyValue.KEY_TV_LEFT;
			operateName="音量-";
		}else if(id==R.id.text_tv_right){
			key = IRKeyValue.KEY_TV_RIGHT;
			operateName="音量+";
		}else if(id==R.id.text_tv_ch_add){
			key = IRKeyValue.KEY_TV_CHANNEL_IN;
			operateName="频道+";
		}else if(id==R.id.text_tv_ch_sub){
			key = IRKeyValue.KEY_TV_CHANNEL_OUT;
			operateName="频道-";
		}else if(id==R.id.text_tv_vol_add){
			key = IRKeyValue.KEY_TV_VOLUME_OUT;
			operateName="音量+";
		}else if(id==R.id.text_tv_vol_sub){
			key = IRKeyValue.KEY_TV_VOLUME_IN;	
			operateName="音量-";
		}else if(id==R.id.text_tv_menu){
			key = IRKeyValue.KEY_TV_MENU;
			operateName="菜单";
		}else if(id==R.id.text_tv_mute){
			key = IRKeyValue.KEY_TV_MUTE;
			operateName="静音";
		}else if(id==R.id.text_tv_ok){
			key = IRKeyValue.KEY_TV_OK;
			operateName="OK";
		}else if(id==R.id.text_tv_back){
			key = IRKeyValue.KEY_TV_BACK;
			operateName="返回";
		}else if(id==R.id.text_tv_power){
			key = IRKeyValue.KEY_TV_POWER;
			operateName="电源";
		}else if(id==R.id.text_tv_home){
			key = IRKeyExValue.KEYEX_TV_HOME;
			operateName="频道0";
		}else if(id==R.id.text_tv_123){
				

			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View view123 = mInflater.inflate(R.layout.fragment_tv_123, null);

			TextView textView1 = (TextView) view123
					.findViewById(R.id.text_tv_1);
			textView1.setOnClickListener(this);
			textView1.setOnLongClickListener(this);
			textView1.setBackgroundResource(R.drawable.btn_style_white);
			// LinearLayout.LayoutParams viewParams =
			// (LinearLayout.LayoutParams)textView1.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;

			TextView textView2 = (TextView) view123
					.findViewById(R.id.text_tv_2);
			textView2.setOnClickListener(this);
			textView2.setOnLongClickListener(this);
			textView2.setBackgroundResource(R.drawable.btn_style_white);
			// viewParams =
			// (LinearLayout.LayoutParams)textView2.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;
			//
			TextView textView3 = (TextView) view123
					.findViewById(R.id.text_tv_3);
			textView3.setOnClickListener(this);
			textView3.setOnLongClickListener(this);
			textView3.setBackgroundResource(R.drawable.btn_style_white);
			// viewParams =
			// (LinearLayout.LayoutParams)textView3.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;
			TextView textView4 = (TextView) view123
					.findViewById(R.id.text_tv_4);
			textView4.setOnClickListener(this);
			textView4.setOnLongClickListener(this);
			textView4.setBackgroundResource(R.drawable.btn_style_white);
			// viewParams =
			// (LinearLayout.LayoutParams)textView4.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;
			TextView textView5 = (TextView) view123
					.findViewById(R.id.text_tv_5);
			textView5.setOnClickListener(this);
			textView5.setBackgroundResource(R.drawable.btn_style_white);
			textView5.setOnLongClickListener(this);
			// viewParams =
			// (LinearLayout.LayoutParams)textView5.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;
			//
			TextView textView6 = (TextView) view123
					.findViewById(R.id.text_tv_6);
			textView6.setOnClickListener(this);
			textView6.setBackgroundResource(R.drawable.btn_style_white);
			textView6.setOnLongClickListener(this);
			// viewParams =
			// (LinearLayout.LayoutParams)textView6.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;

			TextView textView7 = (TextView) view123
					.findViewById(R.id.text_tv_7);
			textView7.setOnClickListener(this);
			textView7.setOnLongClickListener(this);
			textView7.setBackgroundResource(R.drawable.btn_style_white);
			// viewParams =
			// (LinearLayout.LayoutParams)textView7.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;

			TextView textView8 = (TextView) view123
					.findViewById(R.id.text_tv_8);
			textView8.setOnClickListener(this);
			textView8.setOnLongClickListener(this);
			textView8.setBackgroundResource(R.drawable.btn_style_white);
			// viewParams =
			// (LinearLayout.LayoutParams)textView8.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;
			TextView textView9 = (TextView) view123
					.findViewById(R.id.text_tv_9);
			textView9.setOnClickListener(this);
			textView9.setOnLongClickListener(this);
			textView9.setBackgroundResource(R.drawable.btn_style_white);
			// viewParams =
			// (LinearLayout.LayoutParams)textView9.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;
			TextView textViewSelect = (TextView) view123
					.findViewById(R.id.text_tv_select);
			textViewSelect.setOnClickListener(this);
			textViewSelect.setOnLongClickListener(this);
			textViewSelect.setBackgroundResource(R.drawable.btn_style_white);
			// viewParams =
			// (LinearLayout.LayoutParams)textViewSelect.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;
			TextView textView0 = (TextView) view123
					.findViewById(R.id.text_tv_0);
			textView0.setOnClickListener(this);
			textView0.setOnLongClickListener(this);
			textView0.setBackgroundResource(R.drawable.btn_style_white);
			// viewParams =
			// (LinearLayout.LayoutParams)textView0.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;
			TextView textViewAV = (TextView) view123
					.findViewById(R.id.text_tv_av);
			textViewAV.setOnClickListener(this);
			textViewAV.setOnLongClickListener(this);
			textViewAV.setBackgroundResource(R.drawable.btn_style_white);
			// viewParams =
			// (LinearLayout.LayoutParams)textViewAV.getLayoutParams();
			// viewParams.width = (ETGlobal.W - 80) / 4;
			// viewParams.height = viewParams.width * 2 / 3;
			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
					.setView(view123).create();
			dialog.show();
		}
		
		
		boolean isSend = true;
		try {
			if (key == 0)
				return;
			keyValue = mDevice.GetKeyValueEx(key);
			if (keyValue == null)
			{
				keyValue = mDevice.GetKeyValue(key);
			}
			if (keyValue == null){
				Toast.makeText(getActivity(), R.string.str_error_no_key, Toast.LENGTH_SHORT).show();
				return;
			}

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

			System.out.println("===当前的设备是==="+name+deviceType+operateName);
		
			
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
		
		//0221屏蔽广告
	    isSend=true;
		
//		if (!isSend) {
//			Dialog alertDialog = new AlertDialog.Builder(getActivity())
//					.setMessage(R.string.str_study_start_info_6)
//					.setIcon(R.drawable.ic_launcher)
//					.setNegativeButton(R.string.str_buy_no,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									Intent intent = new Intent(
//											ETGlobal.BROADCAST_APP_BUY_NO);
//									getActivity().sendBroadcast(intent);
//								}
//							})
//					.setPositiveButton(R.string.str_buy_yes,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									Intent intent = new Intent(
//											ETGlobal.BROADCAST_APP_BUY_YES);
//									getActivity().sendBroadcast(intent);
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
//		case R.id.text_tv_0:
//			key = IRKeyValue.KEY_TV_KEY0;
//			break;
//		case R.id.text_tv_1:
//			key = IRKeyValue.KEY_TV_KEY1;
//			break;
//		case R.id.text_tv_2:
//			key = IRKeyValue.KEY_TV_KEY2;
//			break;
//		case R.id.text_tv_3:
//			key = IRKeyValue.KEY_TV_KEY3;
//			break;
//		case R.id.text_tv_4:
//			key = IRKeyValue.KEY_TV_KEY4;
//			break;
//		case R.id.text_tv_5:
//			key = IRKeyValue.KEY_TV_KEY5;
//			break;
//		case R.id.text_tv_6:
//			key = IRKeyValue.KEY_TV_KEY6;
//			break;
//		case R.id.text_tv_7:
//			key = IRKeyValue.KEY_TV_KEY7;
//			break;
//		case R.id.text_tv_8:
//			key = IRKeyValue.KEY_TV_KEY8;
//			break;
//		case R.id.text_tv_9:
//			key = IRKeyValue.KEY_TV_KEY9;
//			break;
//		case R.id.text_tv_av:
//			key = IRKeyValue.KEY_TV_AV_TV;
//			break;
//		case R.id.text_tv_select:
//			key = IRKeyValue.KEY_TV_SELECT;
//			break;
//		case R.id.text_tv_up:
//			key = IRKeyValue.KEY_TV_UP;
//			break;
//		case R.id.text_tv_down:
//			key = IRKeyValue.KEY_TV_DOWN;
//			break;
//		case R.id.text_tv_left:
//			key = IRKeyValue.KEY_TV_LEFT;
//			break;
//		case R.id.text_tv_right:
//			key = IRKeyValue.KEY_TV_RIGHT;
//			break;
//		case R.id.text_tv_ch_add:
//			key = IRKeyValue.KEY_TV_CHANNEL_IN;
//			break;
//		case R.id.text_tv_ch_sub:
//			key = IRKeyValue.KEY_TV_CHANNEL_OUT;
//			break;
//		case R.id.text_tv_vol_add:
//			key = IRKeyValue.KEY_TV_VOLUME_IN;
//			break;
//		case R.id.text_tv_vol_sub:
//			key = IRKeyValue.KEY_TV_VOLUME_OUT;
//			break;
//		case R.id.text_tv_menu:
//			key = IRKeyValue.KEY_TV_MENU;
//			break;
//		case R.id.text_tv_mute:
//			key = IRKeyValue.KEY_TV_MUTE;
//			break;
//		case R.id.text_tv_ok:
//			key = IRKeyValue.KEY_TV_OK;
//			break;
//		case R.id.text_tv_back:
//			key = IRKeyValue.KEY_TV_BACK;
//			break;
//		case R.id.text_tv_power:
//			key = IRKeyValue.KEY_TV_POWER;
//			break;
//		case R.id.text_tv_home:
//			key = IRKeyExValue.KEYEX_TV_HOME;
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
			if (key == IRKeyValue.KEY_TV_CHANNEL_IN) {
				mLongKey = key;
				handler.post(runnable);
			} else if (key == IRKeyValue.KEY_TV_CHANNEL_OUT) {
				mLongKey = key;
				handler.post(runnable);
			} else if (key == IRKeyValue.KEY_TV_VOLUME_IN) {
				mLongKey = key;
				handler.post(runnable);
			} else if (key == IRKeyValue.KEY_TV_VOLUME_OUT) {
				mLongKey = key;
				handler.post(runnable);
			}
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
						else{
							ETKeyEx keyEx =  mDevice.GetKeyByValueEx(key);
							keyEx.SetValue(ETTool.HexStringToBytes(msg));
							keyEx.Update(ETDB.getInstance(getActivity()));
						}
					} else if (select.equals("1")) {

					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (action.equals(ETGlobal.BROADCAST_KEYCODE_VOLUME_DOWN)) {
				try {
					byte[] keyValue = mDevice
							.GetKeyValue(IRKeyValue.KEY_TV_VOLUME_OUT);
					if (keyValue == null)
						return;
					ETGlobal.mTg.write(keyValue, keyValue.length);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ETGlobal.BROADCAST_KEYCODE_VOLUME_UP)) {
				try {

					byte[] keyValue = mDevice
							.GetKeyValue(IRKeyValue.KEY_TV_VOLUME_IN);
					if (keyValue == null)
						return;
					ETGlobal.mTg.write(keyValue, keyValue.length);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
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
		// transaction
		// .setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out,
		// R.anim.push_left_in, R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragmentDevice);
		// transactionBt.addToBackStack(null);
		// transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	}

	private Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			int key = mLongKey;
			try {
				if (key == 0)
					return;
				byte[] keyValue = mDevice.GetKeyValue(key);
				if (keyValue == null)
					return;
				ETGlobal.mTg.write(keyValue, keyValue.length);
			} catch (Exception e) {

			}
			handler.postDelayed(this, 300);
		}
	};
	private View view;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.text_tv_ch_add:
//			if (event.getAction() == MotionEvent.ACTION_UP) {
//				mLongKey = 0;
//				handler.removeCallbacks(runnable);
//			}
//			break;
//		case R.id.text_tv_ch_sub:
//			if (event.getAction() == MotionEvent.ACTION_UP) {
//				mLongKey = 0;
//				handler.removeCallbacks(runnable);
//			}
//			break;
//		case R.id.text_tv_vol_add:
//			if (event.getAction() == MotionEvent.ACTION_UP) {
//				mLongKey = 0;
//				handler.removeCallbacks(runnable);
//			}
//			break;
//		case R.id.text_tv_vol_sub:
//			if (event.getAction() == MotionEvent.ACTION_UP) {
//				mLongKey = 0;
//				handler.removeCallbacks(runnable);
//			}
//			break;
//		}
		return false;
	}

}
