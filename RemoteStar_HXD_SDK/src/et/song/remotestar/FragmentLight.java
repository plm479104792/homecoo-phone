package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDeviceLIGHT;
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

public class FragmentLight extends SherlockFragment implements OnClickListener, OnLongClickListener, IBack{
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
	private String operateType;
	private String operateName;  //操作名称
	
	private int mGroupIndex;
	private int mDeviceIndex;
	private ETGroup mGroup = null;
	private ETDeviceLIGHT mDevice = null;
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
		ETSave.getInstance(getActivity()).put("DeviceType", String.valueOf(DeviceType.DEVICE_REMOTE_LIGHT));
		mGroup = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = (ETDeviceLIGHT) mGroup.GetItem(mDeviceIndex);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_light, container, false);

		TextView textViewPowerAllOn = (TextView) view
				.findViewById(R.id.text_light_power_all_on);

		textViewPowerAllOn.setOnClickListener(this);
		textViewPowerAllOn.setOnLongClickListener(this);
		textViewPowerAllOn.setBackgroundResource(R.drawable.ic_button_round_selector);

		TextView textViewPowerAllOff = (TextView) view
				.findViewById(R.id.text_light_power_all_off);

		textViewPowerAllOff.setOnClickListener(this);
		textViewPowerAllOff.setOnLongClickListener(this);
		textViewPowerAllOff.setBackgroundResource(R.drawable.ic_button_round_selector);

		TextView textViewPowerON = (TextView) view
				.findViewById(R.id.text_light_power_on);
		textViewPowerON.setOnClickListener(this);
		textViewPowerON.setOnLongClickListener(this);
		textViewPowerON.setBackgroundResource(R.drawable.ic_button_round_selector);

		TextView textViewPowerOFF = (TextView) view
				.findViewById(R.id.text_light_power_off);

		textViewPowerOFF.setOnClickListener(this);
		textViewPowerOFF.setOnLongClickListener(this);
		textViewPowerOFF.setBackgroundResource(R.drawable.ic_button_round_selector);

		TextView textViewBrightnessAdd = (TextView) view
				.findViewById(R.id.text_light_brightness_add);

		textViewBrightnessAdd.setOnClickListener(this);
		textViewBrightnessAdd.setOnLongClickListener(this);
		textViewBrightnessAdd.setBackgroundResource(R.drawable.btn_style_white);
		textViewBrightnessAdd.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewBrightnessAdd.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewBrightnessSub = (TextView) view
				.findViewById(R.id.text_light_brightness_sub);

		textViewBrightnessSub.setOnClickListener(this);
		textViewBrightnessSub.setOnLongClickListener(this);
		textViewBrightnessSub.setBackgroundResource(R.drawable.btn_style_white);
		textViewBrightnessSub.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewBrightnessSub.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewStudy = (TextView) view
				.findViewById(R.id.text_light_study);

		textViewStudy.setOnClickListener(this);
		textViewStudy.setOnLongClickListener(this);
		textViewStudy.setBackgroundResource(R.drawable.btn_style_white);
		textViewStudy.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewStudy.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewLost = (TextView) view
				.findViewById(R.id.text_light_lost);

		textViewLost.setOnClickListener(this);
		textViewLost.setOnLongClickListener(this);
		textViewLost.setBackgroundResource(R.drawable.btn_style_white);
		textViewLost.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewLost.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewA = (TextView) view
				.findViewById(R.id.text_light_letter_a);

		textViewA.setOnClickListener(this);
		textViewA.setOnLongClickListener(this);
		textViewA.setBackgroundResource(R.drawable.btn_style_white);
		textViewA.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewA.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewB = (TextView) view
				.findViewById(R.id.text_light_letter_b);

		textViewB.setOnClickListener(this);
		textViewB.setOnLongClickListener(this);
		textViewB.setBackgroundResource(R.drawable.btn_style_white);
		textViewB.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewB.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewC = (TextView) view
				.findViewById(R.id.text_light_letter_c);

		textViewC.setOnClickListener(this);
		textViewC.setOnLongClickListener(this);
		textViewC.setBackgroundResource(R.drawable.btn_style_white);
		textViewC.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewC.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewD = (TextView) view
				.findViewById(R.id.text_light_letter_d);

		textViewD.setOnClickListener(this);
		textViewD.setOnLongClickListener(this);
		textViewD.setBackgroundResource(R.drawable.btn_style_white);
		textViewD.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewD.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewTimer1 = (TextView) view
				.findViewById(R.id.text_light_timer1);

		textViewTimer1.setOnClickListener(this);
		textViewTimer1.setOnLongClickListener(this);
		textViewTimer1.setBackgroundResource(R.drawable.btn_style_white);
		textViewTimer1.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewTimer1.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewTimer2 = (TextView) view
				.findViewById(R.id.text_light_timer2);

		textViewTimer2.setOnClickListener(this);
		textViewTimer2.setOnLongClickListener(this);
		textViewTimer2.setBackgroundResource(R.drawable.btn_style_white);
		textViewTimer2.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewTimer2.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewTimer3 = (TextView) view
				.findViewById(R.id.text_light_timer3);

		textViewTimer3.setOnClickListener(this);
		textViewTimer3.setOnLongClickListener(this);
		textViewTimer3.setBackgroundResource(R.drawable.btn_style_white);
		textViewTimer3.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewTimer3.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		
		TextView textViewTimer4 = (TextView) view
				.findViewById(R.id.text_light_timer4);

		textViewTimer4.setOnClickListener(this);
		textViewTimer4.setOnLongClickListener(this);
		textViewTimer4.setBackgroundResource(R.drawable.btn_style_white);
		textViewTimer4.getLayoutParams().width = (ETGlobal.W - 160) / 4;
		textViewTimer4.getLayoutParams().height = (ETGlobal.W - 160) * 3
				/ (4 * 4);
		TextView textViewSet = (TextView) view
				.findViewById(R.id.text_light_set);

		textViewSet.setOnClickListener(this);
		textViewSet.setOnLongClickListener(this);
		textViewSet.setBackgroundResource(R.drawable.ic_button_round_selector);
		textViewSet.getLayoutParams().width = (ETGlobal.W - 80) / 3;
		textViewSet.getLayoutParams().height = (ETGlobal.W - 80) / 3;

		TextView textView123 = (TextView) view
				.findViewById(R.id.text_light_123);
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
		if(id==R.id.text_light_1){
			key = IRKeyValue.KEY_LIGHT_KEY1;
			operateName="电源开";
		}else if(id==R.id.text_light_2){
			key = IRKeyValue.KEY_LIGHT_KEY2;
			operateName="电源开";
		}else if(id==R.id.text_light_3){
			key = IRKeyValue.KEY_LIGHT_KEY3;
			operateName="电源开";
		}else if(id==R.id.text_light_4){
			key = IRKeyValue.KEY_LIGHT_KEY4;
			operateName="电源开";
		}else if(id==R.id.text_light_5){
			key = IRKeyValue.KEY_LIGHT_KEY5;
			operateName="电源开";
		}else if(id==R.id.text_light_6){
			key = IRKeyValue.KEY_LIGHT_KEY6;
			operateName="电源开";
		}else if(id==R.id.text_light_power_all_on){
			key = IRKeyValue.KEY_LIGHT_POWERALLON;
			operateName="电源开";
		}else if(id==R.id.text_light_power_all_off){
			key = IRKeyValue.KEY_LIGHT_POWERALLOFF;
			operateName="电源开";
		}else if(id==R.id.text_light_power_on){
			key = IRKeyValue.KEY_LIGHT_POWERON;
			operateName="电源开";
		}else if(id==R.id.text_light_power_off){
			key = IRKeyValue.KEY_LIGHT_POWEROFF;
			operateName="电源开";
		}else if(id==R.id.text_light_brightness_add){
			key = IRKeyValue.KEY_LIGHT_LIGHT;
			operateName="电源开";
		}else if(id==R.id.text_light_brightness_sub){
			key = IRKeyValue.KEY_LIGHT_DARK;
			operateName="电源开";
		}else if(id==R.id.text_light_study){
			key = IRKeyValue.KEY_LIGHT_STUDY;
			operateName="电源开";
		}else if(id==R.id.text_light_lost){
			key = IRKeyValue.KEY_LIGHT_LOST;
			operateName="电源开";
		}else if(id==R.id.text_light_letter_a){
			key = IRKeyValue.KEY_LIGHT_KEYA;
			operateName="电源开";
		}else if(id==R.id.text_light_letter_b){
			key = IRKeyValue.KEY_LIGHT_KEYB;
			operateName="电源开";
		}else if(id==R.id.text_light_letter_c){
			key = IRKeyValue.KEY_LIGHT_KEYC;
			operateName="电源开";
		}else if(id==R.id.text_light_letter_d){
			key = IRKeyValue.KEY_LIGHT_KEYD;
			operateName="电源开";
		}else if(id==R.id.text_light_timer1){
			key = IRKeyValue.KEY_LIGHT_TIMER1;
			operateName="电源开";
		}else if(id==R.id.text_light_timer2){
			key = IRKeyValue.KEY_LIGHT_TIMER2;
			operateName="电源开";
		}else if(id==R.id.text_light_timer3){
			key = IRKeyValue.KEY_LIGHT_TIMER3;
			operateName="电源开";
		}else if(id==R.id.text_light_timer4){
			key = IRKeyValue.KEY_LIGHT_TIMER4;
			operateName="电源开";
		}else if(id==R.id.text_light_set){
			key = IRKeyValue.KEY_LIGHT_SETTING;
			operateName="电源开";
		}else if(id==R.id.text_light_123){
			
		
		
		
//		switch (v.getId()) {
//		case R.id.text_light_1:
//			key = IRKeyValue.KEY_LIGHT_KEY1;
//
//			break;
//		case R.id.text_light_2:
//			key = IRKeyValue.KEY_LIGHT_KEY2;
//
//			break;
//		case R.id.text_light_3:
//			key = IRKeyValue.KEY_LIGHT_KEY3;
//
//			break;
//		case R.id.text_light_4:
//			key = IRKeyValue.KEY_LIGHT_KEY4;
//
//			break;
//		case R.id.text_light_5:
//			key = IRKeyValue.KEY_LIGHT_KEY5;
//
//			break;
//		case R.id.text_light_6:
//			key = IRKeyValue.KEY_LIGHT_KEY6;
//
//			break;
//		case R.id.text_light_power_all_on:
//			key = IRKeyValue.KEY_LIGHT_POWERALLON;
//
//			break;
//		case R.id.text_light_power_all_off:
//			key = IRKeyValue.KEY_LIGHT_POWERALLOFF;
//
//			break;
//		case R.id.text_light_power_on:
//			key = IRKeyValue.KEY_LIGHT_POWERON;
//
//			break;
//		case R.id.text_light_power_off:
//			key = IRKeyValue.KEY_LIGHT_POWEROFF;
//
//			break;
//		case R.id.text_light_brightness_add:
//			key = IRKeyValue.KEY_LIGHT_LIGHT;
//
//			break;
//		case R.id.text_light_brightness_sub:
//			key = IRKeyValue.KEY_LIGHT_DARK;
//
//			break;
//		case R.id.text_light_study:
//			key = IRKeyValue.KEY_LIGHT_STUDY;
//
//			break;
//		case R.id.text_light_lost:
//			key = IRKeyValue.KEY_LIGHT_LOST;
//
//			break;
//		case R.id.text_light_letter_a:
//			key = IRKeyValue.KEY_LIGHT_KEYA;
//
//			break;
//		case R.id.text_light_letter_b:
//			key = IRKeyValue.KEY_LIGHT_KEYB;
//
//			break;
//		case R.id.text_light_letter_c:
//			key = IRKeyValue.KEY_LIGHT_KEYC;
//
//			break;
//		case R.id.text_light_letter_d:
//			key = IRKeyValue.KEY_LIGHT_KEYD;
//
//			break;
//		case R.id.text_light_timer1:
//			key = IRKeyValue.KEY_LIGHT_TIMER1;
//
//			break;
//		case R.id.text_light_timer2:
//			key = IRKeyValue.KEY_LIGHT_TIMER2;
//
//			break;
//		case R.id.text_light_timer3:
//			key = IRKeyValue.KEY_LIGHT_TIMER3;
//
//			break;
//		case R.id.text_light_timer4:
//			key = IRKeyValue.KEY_LIGHT_TIMER4;
//
//			break;
//		case R.id.text_light_set:
//			key = IRKeyValue.KEY_LIGHT_SETTING;
//
//			break;
//		case R.id.text_light_123:
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View view123 = mInflater.inflate(R.layout.fragment_light_123, null);

			TextView textView1 = (TextView) view123
					.findViewById(R.id.text_light_1);
			textView1.setOnClickListener(this);
			textView1.setOnLongClickListener(this);
			textView1.setBackgroundResource(R.drawable.btn_style_white);
			TextView textView2 = (TextView) view123
					.findViewById(R.id.text_light_2);
			textView2.setOnClickListener(this);
			textView2.setOnLongClickListener(this);
			textView2.setBackgroundResource(R.drawable.btn_style_white);
			TextView textView3 = (TextView) view123
					.findViewById(R.id.text_light_3);
			textView3.setOnClickListener(this);
			textView3.setOnLongClickListener(this);
			textView3.setBackgroundResource(R.drawable.btn_style_white);
			TextView textView4 = (TextView) view123
					.findViewById(R.id.text_light_4);
			textView4.setOnClickListener(this);
			textView4.setOnLongClickListener(this);
			textView4.setBackgroundResource(R.drawable.btn_style_white);
			TextView textView5 = (TextView) view123
					.findViewById(R.id.text_light_5);
			textView5.setOnClickListener(this);
			textView5.setBackgroundResource(R.drawable.btn_style_white);
			textView5.setOnLongClickListener(this);
			TextView textView6 = (TextView) view123
					.findViewById(R.id.text_light_6);
			textView6.setOnClickListener(this);
			textView6.setBackgroundResource(R.drawable.btn_style_white);
			textView6.setOnLongClickListener(this);


			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
					.setView(view123).create();
			dialog.show();
//			break;
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
//		case R.id.text_light_1:
//			key = IRKeyValue.KEY_LIGHT_KEY1;
//
//			break;
//		case R.id.text_light_2:
//			key = IRKeyValue.KEY_LIGHT_KEY2;
//
//			break;
//		case R.id.text_light_3:
//			key = IRKeyValue.KEY_LIGHT_KEY3;
//
//			break;
//		case R.id.text_light_4:
//			key = IRKeyValue.KEY_LIGHT_KEY4;
//
//			break;
//		case R.id.text_light_5:
//			key = IRKeyValue.KEY_LIGHT_KEY5;
//
//			break;
//		case R.id.text_light_6:
//			key = IRKeyValue.KEY_LIGHT_KEY6;
//
//			break;
//		case R.id.text_light_power_all_on:
//			key = IRKeyValue.KEY_LIGHT_POWERALLON;
//
//			break;
//		case R.id.text_light_power_all_off:
//			key = IRKeyValue.KEY_LIGHT_POWERALLOFF;
//
//			break;
//		case R.id.text_light_power_on:
//			key = IRKeyValue.KEY_LIGHT_POWERON;
//
//			break;
//		case R.id.text_light_power_off:
//			key = IRKeyValue.KEY_LIGHT_POWEROFF;
//
//			break;
//		case R.id.text_light_brightness_add:
//			key = IRKeyValue.KEY_LIGHT_LIGHT;
//
//			break;
//		case R.id.text_light_brightness_sub:
//			key = IRKeyValue.KEY_LIGHT_DARK;
//
//			break;
//		case R.id.text_light_study:
//			key = IRKeyValue.KEY_LIGHT_STUDY;
//
//			break;
//		case R.id.text_light_lost:
//			key = IRKeyValue.KEY_LIGHT_LOST;
//
//			break;
//		case R.id.text_light_letter_a:
//			key = IRKeyValue.KEY_LIGHT_KEYA;
//
//			break;
//		case R.id.text_light_letter_b:
//			key = IRKeyValue.KEY_LIGHT_KEYB;
//
//			break;
//		case R.id.text_light_letter_c:
//			key = IRKeyValue.KEY_LIGHT_KEYC;
//
//			break;
//		case R.id.text_light_letter_d:
//			key = IRKeyValue.KEY_LIGHT_KEYD;
//
//			break;
//		case R.id.text_light_timer1:
//			key = IRKeyValue.KEY_LIGHT_TIMER1;
//
//			break;
//		case R.id.text_light_timer2:
//			key = IRKeyValue.KEY_LIGHT_TIMER2;
//
//			break;
//		case R.id.text_light_timer3:
//			key = IRKeyValue.KEY_LIGHT_TIMER3;
//
//			break;
//		case R.id.text_light_timer4:
//			key = IRKeyValue.KEY_LIGHT_TIMER4;
//
//			break;
//		case R.id.text_light_set:
//			key = IRKeyValue.KEY_LIGHT_SETTING;
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
