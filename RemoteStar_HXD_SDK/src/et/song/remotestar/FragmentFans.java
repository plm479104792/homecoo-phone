package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDeviceFANS;
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

public class FragmentFans extends SherlockFragment implements OnClickListener, OnLongClickListener, IBack{
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
	private String operateType;
	private String operateName;  //操作名称
	
	private int mGroupIndex;
	private int mDeviceIndex;
	private ETGroup mGroup = null;
	private ETDeviceFANS mDevice = null;
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
		ETSave.getInstance(getActivity()).put("DeviceType", String.valueOf(DeviceType.DEVICE_REMOTE_FANS));
		mGroup = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = (ETDeviceFANS) mGroup.GetItem(mDeviceIndex);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_fans, container, false);

		TextView textViewPower = (TextView) view
				.findViewById(R.id.text_fans_power);
		textViewPower.setOnClickListener(this);
		textViewPower.setOnLongClickListener(this);
		textViewPower.setBackgroundResource(R.drawable.ic_power_selector);
		textViewPower.getLayoutParams().width = (ETGlobal.W - 80) / 5;
		textViewPower.getLayoutParams().height = (ETGlobal.W - 80) / 5;
		
		TextView textViewSharkHead = (TextView) view
				.findViewById(R.id.text_fans_shake_head);
		textViewSharkHead.setOnClickListener(this);
		textViewSharkHead.setOnLongClickListener(this);
		textViewSharkHead.setBackgroundResource(R.drawable.ic_button_round_selector);

		
		
		TextView textViewMode = (TextView) view
				.findViewById(R.id.text_fans_mode);
		textViewMode.setOnClickListener(this);
		textViewMode.setOnLongClickListener(this);
		textViewMode.setBackgroundResource(R.drawable.ic_button_round_selector);
		textViewMode.getLayoutParams().width = (ETGlobal.W - 80) / 3;
		textViewMode.getLayoutParams().height = (ETGlobal.W - 80) / 3;
		
		TextView textViewCool = (TextView) view
				.findViewById(R.id.text_fans_cool);
		textViewCool.setOnClickListener(this);
		textViewCool.setOnLongClickListener(this);
		textViewCool.setBackgroundResource(R.drawable.ic_button_round_selector);

		TextView textViewLight = (TextView) view
				.findViewById(R.id.text_fans_light);
		textViewLight.setOnClickListener(this);
		textViewLight.setOnLongClickListener(this);
		textViewLight.setBackgroundResource(R.drawable.btn_style_white);
		textViewLight.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewLight.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewSleep = (TextView) view
				.findViewById(R.id.text_fans_sleep);
		textViewSleep.setOnClickListener(this);
		textViewSleep.setOnLongClickListener(this);
		textViewSleep.setBackgroundResource(R.drawable.ic_button_round_selector);

		
		TextView textViewLi = (TextView) view
				.findViewById(R.id.text_fans_li);
		textViewLi.setOnClickListener(this);
		textViewLi.setOnLongClickListener(this);
		textViewLi.setBackgroundResource(R.drawable.ic_button_round_selector);

		TextView textViewTimer = (TextView) view
				.findViewById(R.id.text_fans_timer);
		textViewTimer.setOnClickListener(this);
		textViewTimer.setOnLongClickListener(this);
		textViewTimer.setBackgroundResource(R.drawable.btn_style_white);
		textViewTimer.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewTimer.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewWindSpeed = (TextView) view
				.findViewById(R.id.text_fans_wind_speed);
		textViewWindSpeed.setOnClickListener(this);
		textViewWindSpeed.setOnLongClickListener(this);
		textViewWindSpeed.setBackgroundResource(R.drawable.btn_style_white);
		textViewWindSpeed.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewWindSpeed.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewWindRate = (TextView) view
				.findViewById(R.id.text_fans_wind_rate);
		textViewWindRate.setOnClickListener(this);
		textViewWindRate.setOnLongClickListener(this);
		textViewWindRate.setBackgroundResource(R.drawable.btn_style_white);
		textViewWindRate.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewWindRate.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewWindRateLow = (TextView) view
				.findViewById(R.id.text_fans_wind_rate_low);
		textViewWindRateLow.setOnClickListener(this);
		textViewWindRateLow.setOnLongClickListener(this);
		textViewWindRateLow.setBackgroundResource(R.drawable.btn_style_white);
		textViewWindRateLow.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewWindRateLow.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewWindRateMid = (TextView) view
				.findViewById(R.id.text_fans_wind_rate_mid);
		textViewWindRateMid.setOnClickListener(this);
		textViewWindRateMid.setOnLongClickListener(this);
		textViewWindRateMid.setBackgroundResource(R.drawable.btn_style_white);
		textViewWindRateMid.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewWindRateMid.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewWindRateHigh = (TextView) view
				.findViewById(R.id.text_fans_wind_rate_high);
		textViewWindRateHigh.setOnClickListener(this);
		textViewWindRateHigh.setOnLongClickListener(this);
		textViewWindRateHigh.setBackgroundResource(R.drawable.btn_style_white);
		textViewWindRateHigh.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewWindRateHigh.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		
	
		TextView textView123 = (TextView) view.findViewById(R.id.text_fans_123);
		textView123.setOnClickListener(this);
		textView123.setBackgroundResource(R.drawable.btn_style_white);
		textView123.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textView123.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);

	
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
		if(id==android.R.id.home){
			Back();
		}else if(id==R.id.menu_edit){
			if (item.isChecked()){
				item.setChecked(false);
				mIsModity = false;
			}
			else
			{
				item.setChecked(true);
				mIsModity = true;
				//ETTool.MessageBox(getActivity(), 0.5f, getString(R.string.str_study_start_info_2), true);
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


	@SuppressLint("InflateParams")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		byte[] keyValue = null;
		int key = 0;
		
		int id=v.getId();
		if(id==R.id.text_fans_power){
			key = IRKeyValue.KEY_FANS_POWER;
			operateName="电源";
		}else if(id==R.id.text_fans_shake_head){
			key = IRKeyValue.KEY_FANS_SHAKE_HEAD;
			operateName="摇头";
		}else if(id==R.id.text_fans_1){
			key = IRKeyValue.KEY_FANS_KEY1;
			operateName="1";
		}else if(id==R.id.text_fans_2){
			key = IRKeyValue.KEY_FANS_KEY2;
			operateName="2";
		}else if(id==R.id.text_fans_3){
			key = IRKeyValue.KEY_FANS_KEY3;
			operateName="3";
		}else if(id==R.id.text_fans_4){
			key = IRKeyValue.KEY_FANS_KEY4;
			operateName="4";
		}else if(id==R.id.text_fans_5){
			key = IRKeyValue.KEY_FANS_KEY5;
			operateName="5";
		}else if(id==R.id.text_fans_6){
			key = IRKeyValue.KEY_FANS_KEY6;
			operateName="6";
		}else if(id==R.id.text_fans_7){
			key = IRKeyValue.KEY_FANS_KEY7;
			operateName="7";
		}else if(id==R.id.text_fans_8){
			key = IRKeyValue.KEY_FANS_KEY8;
			operateName="8";
		}else if(id==R.id.text_fans_9){
			key = IRKeyValue.KEY_FANS_KEY9;
			operateName="9";
		}else if(id==R.id.text_fans_light){
			key = IRKeyValue.KEY_FANS_LIGHT;
			operateName="灯光";
		}else if(id==R.id.text_fans_sleep){
			key = IRKeyValue.KEY_FANS_SLEEP;
			operateName="睡眠";
		}else if(id==R.id.text_fans_li){
			key = IRKeyValue.KEY_FANS_ANION;
			operateName="风量";
		}else if(id==R.id.text_fans_timer){
			key = IRKeyValue.KEY_FANS_TIMER;
			operateName="定时";
		}else if(id==R.id.text_fans_wind_speed){
			key = IRKeyValue.KEY_FANS_WIND_SPEED;
			operateName="睡眠";
		}else if(id==R.id.text_fans_wind_rate){
			key = IRKeyValue.KEY_FANS_AIR_RATE;
			operateName="风速";
		}else if(id==R.id.text_fans_wind_rate_low){
			key = IRKeyValue.KEY_FANS_AIR_RATE_LOW;
			operateName="低";
		}else if(id==R.id.text_fans_wind_rate_mid){
			key = IRKeyValue.KEY_FANS_AIR_RATE_MIDDLE;
			operateName="中";
		}else if(id==R.id.text_fans_wind_rate_high){
			key = IRKeyValue.KEY_FANS_AIR_RATE_HIGH;
			operateName="高";
		}else if(id==R.id.text_fans_123){
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
		View view123 = mInflater.inflate(R.layout.fragment_fans_123, null);

		TextView textView1 = (TextView) view123
				.findViewById(R.id.text_fans_1);
		textView1.setOnClickListener(this);
		textView1.setOnLongClickListener(this);
		textView1.setBackgroundResource(R.drawable.btn_style_white);
		TextView textView2 = (TextView) view123
				.findViewById(R.id.text_fans_2);
		textView2.setOnClickListener(this);
		textView2.setOnLongClickListener(this);
		textView2.setBackgroundResource(R.drawable.btn_style_white);
		TextView textView3 = (TextView) view123
				.findViewById(R.id.text_fans_3);
		textView3.setOnClickListener(this);
		textView3.setOnLongClickListener(this);
		textView3.setBackgroundResource(R.drawable.btn_style_white);
		TextView textView4 = (TextView) view123
				.findViewById(R.id.text_fans_4);
		textView4.setOnClickListener(this);
		textView4.setOnLongClickListener(this);
		textView4.setBackgroundResource(R.drawable.btn_style_white);
		TextView textView5 = (TextView) view123
				.findViewById(R.id.text_fans_5);
		textView5.setOnClickListener(this);
		textView5.setOnLongClickListener(this);
		textView5.setBackgroundResource(R.drawable.btn_style_white);
		TextView textView6 = (TextView) view123
				.findViewById(R.id.text_fans_6);
		textView6.setOnClickListener(this);
		textView6.setOnLongClickListener(this);
		textView6.setBackgroundResource(R.drawable.btn_style_white);
		TextView textView7 = (TextView) view123
				.findViewById(R.id.text_fans_7);
		textView7.setOnClickListener(this);
		textView7.setOnLongClickListener(this);
		textView7.setBackgroundResource(R.drawable.btn_style_white);
		TextView textView8 = (TextView) view123
				.findViewById(R.id.text_fans_8);
		textView8.setOnClickListener(this);
		textView8.setOnLongClickListener(this);
		textView8.setBackgroundResource(R.drawable.btn_style_white);
		TextView textView9 = (TextView) view123
				.findViewById(R.id.text_fans_9);
		textView9.setOnClickListener(this);
		textView9.setOnLongClickListener(this);
		textView9.setBackgroundResource(R.drawable.btn_style_white);



		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
				.setView(view123).create();
		dialog.show();
		}
//		switch (v.getId()) {
//		case R.id.text_fans_power:
//			key = IRKeyValue.KEY_FANS_POWER;
//			
//			break;
//		case R.id.text_fans_shake_head:
//			key = IRKeyValue.KEY_FANS_SHAKE_HEAD;
//			
//			break;
//		case R.id.text_fans_1:
//			key = IRKeyValue.KEY_FANS_KEY1;
//			
//			break;
//		case R.id.text_fans_2:
//			key = IRKeyValue.KEY_FANS_KEY2;
//			
//			break;
//		case R.id.text_fans_3:
//			key = IRKeyValue.KEY_FANS_KEY3;
//			
//			break;
//		case R.id.text_fans_mode:
//			key = IRKeyValue.KEY_FANS_MODE;
//			
//			break;
//		case R.id.text_fans_4:
//			key = IRKeyValue.KEY_FANS_KEY4;
//			
//			break;
//		case R.id.text_fans_5:
//			key = IRKeyValue.KEY_FANS_KEY5;
//			
//			break;
//		case R.id.text_fans_6:
//			key = IRKeyValue.KEY_FANS_KEY6;
//			
//			break;
//		case R.id.text_fans_cool:
//			key = IRKeyValue.KEY_FANS_COOL;
//			
//			break;
//		case R.id.text_fans_7:
//			key = IRKeyValue.KEY_FANS_KEY7;
//			
//			break;
//		case R.id.text_fans_8:
//			key = IRKeyValue.KEY_FANS_KEY8;
//			
//			break;
//		case R.id.text_fans_9:
//			key = IRKeyValue.KEY_FANS_KEY9;
//			
//			break;
//		case R.id.text_fans_light:
//			key = IRKeyValue.KEY_FANS_LIGHT;
//			
//			break;
//		case R.id.text_fans_sleep:
//			key = IRKeyValue.KEY_FANS_SLEEP;
//			
//			break;
//		case R.id.text_fans_li:
//			key = IRKeyValue.KEY_FANS_ANION;
//			
//			break;
//		case R.id.text_fans_timer:
//			key = IRKeyValue.KEY_FANS_TIMER;
//			
//			break;
//		case R.id.text_fans_wind_speed:
//			key = IRKeyValue.KEY_FANS_WIND_SPEED;
//			
//			break;
//		case R.id.text_fans_wind_rate:
//			key = IRKeyValue.KEY_FANS_AIR_RATE;
//			
//			break;
//		case R.id.text_fans_wind_rate_low:
//			key = IRKeyValue.KEY_FANS_AIR_RATE_LOW;
//			
//			break;
//		case R.id.text_fans_wind_rate_mid:
//			key = IRKeyValue.KEY_FANS_AIR_RATE_MIDDLE;
//			
//			break;
//		case R.id.text_fans_wind_rate_high:
//			key = IRKeyValue.KEY_FANS_AIR_RATE_HIGH;
//			
//			break;
//		
//		case R.id.text_fans_123:
//			LayoutInflater mInflater = LayoutInflater.from(getActivity());
//			View view123 = mInflater.inflate(R.layout.fragment_fans_123, null);
//
//			TextView textView1 = (TextView) view123
//					.findViewById(R.id.text_fans_1);
//			textView1.setOnClickListener(this);
//			textView1.setOnLongClickListener(this);
//			textView1.setBackgroundResource(R.drawable.btn_style_white);
//			TextView textView2 = (TextView) view123
//					.findViewById(R.id.text_fans_2);
//			textView2.setOnClickListener(this);
//			textView2.setOnLongClickListener(this);
//			textView2.setBackgroundResource(R.drawable.btn_style_white);
//			TextView textView3 = (TextView) view123
//					.findViewById(R.id.text_fans_3);
//			textView3.setOnClickListener(this);
//			textView3.setOnLongClickListener(this);
//			textView3.setBackgroundResource(R.drawable.btn_style_white);
//			TextView textView4 = (TextView) view123
//					.findViewById(R.id.text_fans_4);
//			textView4.setOnClickListener(this);
//			textView4.setOnLongClickListener(this);
//			textView4.setBackgroundResource(R.drawable.btn_style_white);
//			TextView textView5 = (TextView) view123
//					.findViewById(R.id.text_fans_5);
//			textView5.setOnClickListener(this);
//			textView5.setOnLongClickListener(this);
//			textView5.setBackgroundResource(R.drawable.btn_style_white);
//			TextView textView6 = (TextView) view123
//					.findViewById(R.id.text_fans_6);
//			textView6.setOnClickListener(this);
//			textView6.setOnLongClickListener(this);
//			textView6.setBackgroundResource(R.drawable.btn_style_white);
//			TextView textView7 = (TextView) view123
//					.findViewById(R.id.text_fans_7);
//			textView7.setOnClickListener(this);
//			textView7.setOnLongClickListener(this);
//			textView7.setBackgroundResource(R.drawable.btn_style_white);
//			TextView textView8 = (TextView) view123
//					.findViewById(R.id.text_fans_8);
//			textView8.setOnClickListener(this);
//			textView8.setOnLongClickListener(this);
//			textView8.setBackgroundResource(R.drawable.btn_style_white);
//			TextView textView9 = (TextView) view123
//					.findViewById(R.id.text_fans_9);
//			textView9.setOnClickListener(this);
//			textView9.setOnLongClickListener(this);
//			textView9.setBackgroundResource(R.drawable.btn_style_white);
//
//
//
//			AlertDialog dialog = new AlertDialog.Builder(getActivity())
//					.setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
//					.setView(view123).create();
//			dialog.show();
//			break;
//		}
		
		
		
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
//		case R.id.text_fans_power:
//			key = IRKeyValue.KEY_FANS_POWER;
//			
//			break;
//		case R.id.text_fans_shake_head:
//			key = IRKeyValue.KEY_FANS_SHAKE_HEAD;
//			
//			break;
//		case R.id.text_fans_1:
//			key = IRKeyValue.KEY_FANS_KEY1;
//			
//			break;
//		case R.id.text_fans_2:
//			key = IRKeyValue.KEY_FANS_KEY2;
//			
//			break;
//		case R.id.text_fans_3:
//			key = IRKeyValue.KEY_FANS_KEY3;
//			
//			break;
//		case R.id.text_fans_mode:
//			key = IRKeyValue.KEY_FANS_MODE;
//			
//			break;
//		case R.id.text_fans_4:
//			key = IRKeyValue.KEY_FANS_KEY4;
//			
//			break;
//		case R.id.text_fans_5:
//			key = IRKeyValue.KEY_FANS_KEY5;
//			
//			break;
//		case R.id.text_fans_6:
//			key = IRKeyValue.KEY_FANS_KEY6;
//			
//			break;
//		case R.id.text_fans_cool:
//			key = IRKeyValue.KEY_FANS_COOL;
//			
//			break;
//		case R.id.text_fans_7:
//			key = IRKeyValue.KEY_FANS_KEY7;
//			
//			break;
//		case R.id.text_fans_8:
//			key = IRKeyValue.KEY_FANS_KEY8;
//			
//			break;
//		case R.id.text_fans_9:
//			key = IRKeyValue.KEY_FANS_KEY9;
//			
//			break;
//		case R.id.text_fans_light:
//			key = IRKeyValue.KEY_FANS_LIGHT;
//			
//			break;
//		case R.id.text_fans_sleep:
//			key = IRKeyValue.KEY_FANS_SLEEP;
//			
//			break;
//		case R.id.text_fans_li:
//			key = IRKeyValue.KEY_FANS_ANION;
//			
//			break;
//		case R.id.text_fans_timer:
//			key = IRKeyValue.KEY_FANS_TIMER;
//			
//			break;
//		case R.id.text_fans_wind_speed:
//			key = IRKeyValue.KEY_FANS_WIND_SPEED;
//			
//			break;
//		case R.id.text_fans_wind_rate:
//			key = IRKeyValue.KEY_FANS_AIR_RATE;
//			
//			break;
//		case R.id.text_fans_wind_rate_low:
//			key = IRKeyValue.KEY_FANS_AIR_RATE_LOW;
//			
//			break;
//		case R.id.text_fans_wind_rate_mid:
//			key = IRKeyValue.KEY_FANS_AIR_RATE_MIDDLE;
//			
//			break;
//		case R.id.text_fans_wind_rate_high:
//			key = IRKeyValue.KEY_FANS_AIR_RATE_HIGH;
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
