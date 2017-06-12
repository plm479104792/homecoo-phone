package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDeviceAIR;
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
import android.content.res.AssetManager;
import android.graphics.Typeface;
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

public class FragmentAIR extends SherlockFragment implements OnClickListener,
		OnLongClickListener, IBack, OnTouchListener {
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
	
	private int mGroupIndex;
	private int mDeviceIndex;
	private ETGroup mGroup = null;
	private ETDeviceAIR mDevice = null;
	private TextView mTextViewTemp;
	private TextView mTextViewModeAuto;
	private TextView mTextViewModeCool;
	private TextView mTextViewModeDrying;
	private TextView mTextViewModeWind;
	private TextView mTextViewModeWarm;
	private TextView mTextViewRate;
	private TextView mTextViewDir;
	private RecvReceiver mReceiver;
	private boolean mIsModity = false;
	private int mLongKey = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getActivity().getSharedPreferences("tuwa", Context.MODE_PRIVATE);
		operateType = preferences.getString("OPERATION_TYPE","");
		
		
		setHasOptionsMenu(true);
		mGroupIndex = this.getArguments().getInt("group");
		mDeviceIndex = this.getArguments().getInt("device");
		ETSave.getInstance(getActivity()).put("GroupIndex", String.valueOf(mGroupIndex));
		ETSave.getInstance(getActivity()).put("DeviceIndex", String.valueOf(mDeviceIndex));
		ETSave.getInstance(getActivity()).put("DeviceType", String.valueOf(DeviceType.DEVICE_REMOTE_AIR));
		mGroup = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = (ETDeviceAIR) mGroup.GetItem(mDeviceIndex);
		if (mDevice!=null) {
			mDevice.Load(ETDB.getInstance(getActivity()));
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_air, container, false);

		TextView textViewPower = (TextView) view
				.findViewById(R.id.text_air_power);
		textViewPower.setOnClickListener(this);
		textViewPower.setOnLongClickListener(this);
		textViewPower.setBackgroundResource(R.drawable.ic_power_selector);

		mTextViewModeAuto = (TextView) view
				.findViewById(R.id.text_air_mode_auto);
		mTextViewModeCool = (TextView) view
				.findViewById(R.id.text_air_mode_cool);
		mTextViewModeDrying = (TextView) view
				.findViewById(R.id.text_air_mode_drying);
		mTextViewModeWind = (TextView) view
				.findViewById(R.id.text_air_mode_wind);
		mTextViewModeWarm = (TextView) view
				.findViewById(R.id.text_air_mode_warm);

		mTextViewRate = (TextView) view.findViewById(R.id.text_air_rate);

		mTextViewDir = (TextView) view.findViewById(R.id.text_air_dir);

		mTextViewTemp = (TextView) view.findViewById(R.id.text_air_temp);
//		AssetManager mgr = getActivity().getAssets();
//		Typeface tf = Typeface.createFromAsset(mgr, "fonts/Clockopia.ttf");
//		mTextViewTemp.setTypeface(tf);
		mTextViewTemp.setText("");

		TextView textViewMode = (TextView) view
				.findViewById(R.id.text_air_mode);
		textViewMode.setOnClickListener(this);
		textViewMode.setOnLongClickListener(this);
		textViewMode.setBackgroundResource(R.drawable.ic_button_up_bg_selector);

		TextView textViewSpeed = (TextView) view
				.findViewById(R.id.text_air_speed);
		textViewSpeed.setOnClickListener(this);
		textViewSpeed.setOnLongClickListener(this);
		textViewSpeed
				.setBackgroundResource(R.drawable.ic_button_down_bg_selector);

		TextView textViewWindHand = (TextView) view
				.findViewById(R.id.text_air_hand);
		textViewWindHand.setOnClickListener(this);
		textViewWindHand.setOnLongClickListener(this);
		textViewWindHand
				.setBackgroundResource(R.drawable.ic_button_round_selector);

		TextView textViewWindAuto = (TextView) view
				.findViewById(R.id.text_air_auto);
		textViewWindAuto.setOnClickListener(this);
		textViewWindAuto.setOnLongClickListener(this);
		textViewWindAuto
				.setBackgroundResource(R.drawable.ic_button_round_selector);

		TextView textViewTempAdd = (TextView) view
				.findViewById(R.id.text_air_tempadd);
		textViewTempAdd.setOnClickListener(this);
		textViewTempAdd.setOnLongClickListener(this);
		textViewTempAdd
				.setBackgroundResource(R.drawable.ic_button_round_selector);

		TextView textViewTempSub = (TextView) view
				.findViewById(R.id.text_air_tempsub);
		textViewTempSub.setOnClickListener(this);
		textViewTempSub.setOnLongClickListener(this);
		textViewTempSub
				.setBackgroundResource(R.drawable.ic_button_round_selector);

		textViewPower.getLayoutParams().width = (ETGlobal.W - 80) / 4;
		textViewPower.getLayoutParams().height = (ETGlobal.W - 80) / 4;
		textViewMode.getLayoutParams().width = (ETGlobal.W - 80) / 2;
		textViewMode.getLayoutParams().height = (int) (textViewMode
				.getLayoutParams().width * (5 / 12.0));
		textViewSpeed.getLayoutParams().width = (ETGlobal.W - 80) / 2;
		textViewSpeed.getLayoutParams().height = (int) (textViewSpeed
				.getLayoutParams().width * (5 / 12.0));
		
		if (mDevice!=null) {
			F5();
		}
		

		return view;
	}

	public void F5() {
		if (mDevice.GetPower() == 0x01) {
			switch (mDevice.GetMode()) {
			case 1:
				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_2);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_1);
				break;
			case 2:

				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_2);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_1);

				break;
			case 3:

				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_2);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_1);
				break;
			case 4:

				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_2);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_1);
				break;
			case 5:

				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_2);
				break;
			}

			switch (mDevice.GetWindRate()) {
			case 1:
				mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_1);

				break;
			case 2:
				mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_2);

				break;
			case 3:
				mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_3);

				break;
			case 4:
				mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_4);

				break;
			}

			if (mDevice.GetAutoWindDir() == 0x01) {
				mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_1);
			} else {
				switch (mDevice.GetWindDir()) {
				case 1:
					mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_2);
					break;
				case 2:
					mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_3);
					break;
				case 3:
					mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_4);
					break;
				}
			}
			if (mDevice.GetMode() == 2 || mDevice.GetMode() == 5) {
				mTextViewTemp.setText(Byte.valueOf(mDevice.GetTemp())
						.toString());
			} else {
				mTextViewTemp.setText("");
			}
		} else {

			mTextViewModeAuto
					.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
			mTextViewModeCool
					.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
			mTextViewModeDrying
					.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
			mTextViewModeWind
					.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
			mTextViewModeWarm
					.setBackgroundResource(R.drawable.ic_air_mode_warm_1);
			mTextViewRate.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
			mTextViewDir.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
			mTextViewTemp.setText("");
		}
		mDevice.Update(ETDB.getInstance(getActivity()));
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
		
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		byte[] keyValue = null;
		int key = 0;
		
		int id=v.getId();
		if (id==R.id.text_air_power) {
			key = IRKeyValue.KEY_AIR_POWER;
		}else if (id==R.id.text_air_mode) {
			key = IRKeyValue.KEY_AIR_MODE;
		}else if (id==R.id.text_air_hand) {
			key = IRKeyValue.KEY_AIR_WIND_DIRECTION;
		}else if (id==R.id.text_air_auto) {
			key = IRKeyValue.KEY_AIR_AUTOMATIC_WIND_DIRECTION;
		}else if (id==R.id.text_air_speed) {
			key = IRKeyValue.KEY_AIR_WIND_RATE;
		}else if (id==R.id.text_air_tempadd) {
			key = IRKeyValue.KEY_AIR_TEMPERATURE_IN;
		}else if (id==R.id.text_air_tempsub) {
			key = IRKeyValue.KEY_AIR_TEMPERATURE_OUT;
		}
		
		
		ETKey k = mDevice.GetKeyByValue(key);
		if (k.GetState() != ETKey.ETKEY_STATE_STUDY
				&& k.GetState() != ETKey.ETKEY_STATE_DIY) {
			if (key != IRKeyValue.KEY_AIR_POWER && mDevice.GetPower() != 0x01) {
				return;
			}
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
			
			//0221码值输出打印
			ETGlobal.rprintHexString(keyValue);
			
			//发送广播消息到SocketService中接收
			String temp=Byte.valueOf(mDevice.GetTemp()).toString();
			int model=mDevice.GetMode();
			String modelName=getModelNameByModel(model);
			int deviceType=mDevice.GetType();
			System.out.println("===当前的空调温度是==="+Byte.valueOf(mDevice.GetTemp()).toString());
		
			
			Intent intent = new Intent();  //Itent就是我们要发送的内容
            intent.putExtra("infrared",keyValue);
            if(operateType.equals("SCENE_INFRA_SET")){
            	intent.setAction("ACTION_SCENE_INFRA_SET");
            	intent.putExtra("INFRA_TYPE",deviceType);
            	if(mDevice.GetPower() == 0x01){  //电源键打开
            		intent.putExtra("INFRA_MODEL",modelName+" "+temp);
            	}else{
            		intent.putExtra("INFRA_MODEL","关");
            	}
            	
            	System.out.println("===发送红外设置广播消息==");
            	  
            }else{
            	intent.setAction("send");  
            	System.out.println("===发送红外控制广播消息==");
            }
            getActivity().sendBroadcast(intent);   //发送广播
		
			
//			int n = ETGlobal.mTg.write(keyValue, keyValue.length);
			
//			if (n < 0) {
//				isSend = false;
//			}
			F5();
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
//									// TODO Auto-generated
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

	/**
	 * 获取对应的空调模式名称
	 * @param model
	 * @return
	 */
	private String getModelNameByModel(int model) {
		String modelName="";
		switch (model) {
		case 1:
			return modelName="自动";
		case 2:
			return modelName="制冷";
		case 3:
			return modelName="干燥";
		case 4:
			return modelName="送风";
		case 5:
			return modelName="制热";
		default:
			return modelName;
		}
		
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		int key = 0;
		
		int id=v.getId();
		if (id==R.id.text_air_power) {
			key = IRKeyValue.KEY_AIR_POWER;
		}else if (id==R.id.text_air_mode) {
			key = IRKeyValue.KEY_AIR_MODE;
		}else if (id==R.id.text_air_hand) {
			key = IRKeyValue.KEY_AIR_WIND_DIRECTION;
		}else if (id==R.id.text_air_auto) {
			key = IRKeyValue.KEY_AIR_AUTOMATIC_WIND_DIRECTION;
		}else if (id==R.id.text_air_speed) {
			key = IRKeyValue.KEY_AIR_WIND_RATE;
		}else if (id==R.id.text_air_tempadd) {
			key = IRKeyValue.KEY_AIR_TEMPERATURE_IN;
		}else if (id==R.id.text_air_tempsub) {
			key = IRKeyValue.KEY_AIR_TEMPERATURE_OUT;
		}
		
//		switch (v.getId()) {
//		case R.id.text_air_power:
//			key = IRKeyValue.KEY_AIR_POWER;
//
//			break;
//		case R.id.text_air_mode:
//			key = IRKeyValue.KEY_AIR_MODE;
//
//			break;
//		case R.id.text_air_hand:
//			key = IRKeyValue.KEY_AIR_WIND_DIRECTION;
//
//			break;
//		case R.id.text_air_auto:
//			key = IRKeyValue.KEY_AIR_AUTOMATIC_WIND_DIRECTION;
//
//			break;
//		case R.id.text_air_speed:
//			key = IRKeyValue.KEY_AIR_WIND_RATE;
//
//			break;
//		case R.id.text_air_tempadd:
//			key = IRKeyValue.KEY_AIR_TEMPERATURE_IN;
//
//			break;
//		case R.id.text_air_tempsub:
//			key = IRKeyValue.KEY_AIR_TEMPERATURE_OUT;
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
			if (key == IRKeyValue.KEY_AIR_TEMPERATURE_IN) {
				mLongKey = key;
				handler.post(runnable);
			} else if (key == IRKeyValue.KEY_AIR_TEMPERATURE_OUT) {
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
					} else if (select.equals("1")) {

					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (action.equals(ETGlobal.BROADCAST_KEYCODE_VOLUME_DOWN)) {
				try {
					byte[] keyValue = mDevice
							.GetKeyValue(IRKeyValue.KEY_AIR_TEMPERATURE_OUT);
					if (keyValue == null)
						return;
					
					//0221码值输出打印
					ETGlobal.rprintHexString(keyValue);
					System.out.println("===当前的空调温度-是==="+Byte.valueOf(mDevice.GetTemp())
							.toString());
					Intent intent1 = new Intent();  //Itent就是我们要发送的内容
		            intent1.putExtra("infrared",keyValue);  
		            intent1.setAction("send");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
		            getActivity().sendBroadcast(intent1);   //发送广播
					System.out.println("===发送广播消息==");
					
//					ETGlobal.mTg.write(keyValue, keyValue.length);
					
					F5();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ETGlobal.BROADCAST_KEYCODE_VOLUME_UP)) {
				try {
					byte[] keyValue = mDevice
							.GetKeyValue(IRKeyValue.KEY_AIR_TEMPERATURE_IN);
					if (keyValue == null)
						return;
					
					//0221码值输出打印
					ETGlobal.rprintHexString(keyValue);
					System.out.println("===当前的空调温度+是==="+Byte.valueOf(mDevice.GetTemp())
							.toString());
		
					Intent intent2 = new Intent();  //Itent就是我们要发送的内容
		            intent2.putExtra("infrared",keyValue);  
		            intent2.setAction("send");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
		            getActivity().sendBroadcast(intent2);   //发送广播
					System.out.println("===发送广播消息==");
					
//					ETGlobal.mTg.write(keyValue, keyValue.length);
					F5();
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
		// transaction.setCustomAnimations(R.anim.push_left_in,
		// R.anim.push_left_out, R.anim.push_left_in,
		// R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragmentDevice);
		// transactionBt.addToBackStack(null);
		// transaction
		// .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	};

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
				
				//0221码值输出打印
				ETGlobal.rprintHexString(keyValue);
				
				Intent intent = new Intent();  //Itent就是我们要发送的内容
	            intent.putExtra("infrared",keyValue);  
	            intent.setAction("send");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
	            getActivity().sendBroadcast(intent);   //发送广播
				System.out.println("===发送广播消息==");
				
				
//				ETGlobal.mTg.write(keyValue, keyValue.length);
				F5();
			} catch (Exception e) {

			}
			handler.postDelayed(this, 300);
		}
	};
	private String operateType;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int id=v.getId();
		if (id==R.id.text_air_tempadd) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				mLongKey = 0;
				handler.removeCallbacks(runnable);
			}
		}else if (id==R.id.text_air_tempsub) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				mLongKey = 0;
				handler.removeCallbacks(runnable);
			}
		}
		
		
//		switch (v.getId()) {
//		case R.id.text_air_tempadd:
//			if (event.getAction() == MotionEvent.ACTION_UP) {
//				mLongKey = 0;
//				handler.removeCallbacks(runnable);
//			}
//			break;
//		case R.id.text_air_tempsub:
//			if (event.getAction() == MotionEvent.ACTION_UP) {
//				mLongKey = 0;
//				handler.removeCallbacks(runnable);
//			}
//			break;
//		}
		return false;
	}
}
