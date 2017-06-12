package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevice;
import et.song.etclass.ETDeviceAIR;
import et.song.etclass.ETDeviceDC;
import et.song.etclass.ETDeviceDVD;
import et.song.etclass.ETDeviceFANS;
import et.song.etclass.ETDeviceIPTV;
import et.song.etclass.ETDeviceLIGHT;
import et.song.etclass.ETDevicePJT;
import et.song.etclass.ETDeviceSTB;
import et.song.etclass.ETDeviceTV;
import et.song.etclass.ETGroup;
import et.song.etclass.ETPage;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.jni.ir.ETIR;
import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.AIR;
import et.song.remotestar.hxd.sdk.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentWizardsFour extends SherlockFragment implements
		OnClickListener, IBack {
	private AlertDialog mDialog = null;
	private TextView mTextName = null;
	private TextView mTextCount = null;
	private int mType;
	private int mGroupIndex;
	private int mRow;
	private String mName;
	private int mKey = 0;
	private int mTotal = 0;
	private int mCount = 1;
	private int mIndex = 0;
	private IR mIR = null;

	private TextView mTextViewTemp;
	private TextView mTextViewModeAuto;
	private TextView mTextViewModeCool;
	private TextView mTextViewModeDrying;
	private TextView mTextViewModeWind;
	private TextView mTextViewModeWarm;
	private TextView mTextViewRate;
	private TextView mTextViewDir;
	private RecvReceiver mReceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mType = this.getArguments().getInt("type");
		mGroupIndex = getArguments().getInt("group");
		mRow = getArguments().getInt("index");
		mName = getArguments().getString("name");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_wizards_four, container,
				false);

		mTextName = (TextView) view.findViewById(R.id.text_name);
		mTextName.setText(mName);
		mTextCount = (TextView) view.findViewById(R.id.text_count);
//		AssetManager mgr = getActivity().getAssets();
//		Typeface tf = Typeface.createFromAsset(mgr, "fonts/Clockopia.ttf");
//		mTextCount.setTypeface(tf);

		TextView textTest = (TextView) view.findViewById(R.id.text_test);
		textTest.setOnClickListener(this);
		mIR = ETIR.Builder(mType);
		switch (mType) {
		case DeviceType.DEVICE_REMOTE_TV:
			mKey = IRKeyValue.KEY_TV_POWER;
			textTest.setText(R.string.str_other_power);
			break;
		case DeviceType.DEVICE_REMOTE_IPTV:
			mKey = IRKeyValue.KEY_IPTV_POWER;
			textTest.setText(R.string.str_other_power);
			break;
		case DeviceType.DEVICE_REMOTE_STB:
			mKey = IRKeyValue.KEY_STB_AWAIT;
			textTest.setText(R.string.str_other_await);
			break;
		case DeviceType.DEVICE_REMOTE_DVD:
			mKey = IRKeyValue.KEY_DVD_MUTE;
			textTest.setText(R.string.str_other_mute);
			break;
		case DeviceType.DEVICE_REMOTE_PJT:
			mKey = IRKeyValue.KEY_PJT_POWER_ON;
			textTest.setText(R.string.str_other_power_on);
			break;
		case DeviceType.DEVICE_REMOTE_LIGHT:
			mKey = IRKeyValue.KEY_LIGHT_POWERON;
			textTest.setText(R.string.str_other_power_on);
			break;
		case DeviceType.DEVICE_REMOTE_FANS:
			mKey = IRKeyValue.KEY_FANS_SHAKE_HEAD;
			textTest.setText(R.string.str_other_shake_head);
			break;
		case DeviceType.DEVICE_REMOTE_AIR:
			mKey = IRKeyValue.KEY_AIR_POWER;
			textTest.setText(R.string.str_other_power);
			break;
		case DeviceType.DEVICE_REMOTE_DC:
			mKey = IRKeyValue.KEY_DC_PHOTO;
			textTest.setText(R.string.str_other_photo);
			break;
		}
		if (mIR != null) {
			try {
				mTotal = mIR.GetBrandCount(mRow);
				mTextCount.setText("(" + mCount + "/" + mTotal + ")");
				getActivity().setTitle(mName + "(" + mCount + "/" + mTotal + ")");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			byte[] keyValue = null;
			switch (msg.what) {
			case 0:
				try {
					if (mCount != mTotal) {
						mCount++;
						mIndex++;
					}
					mTextCount.setText("(" + mCount + "/" + mTotal + ")");
					getActivity().setTitle(mName + "(" + mCount + "/" + mTotal + ")");
					mHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 1:
				try {
					if (mKey == 0)
						return;
					keyValue = mIR.Search(mRow, mIndex, mKey);
					if (mKey == IRKeyValue.KEY_AIR_POWER){
						AIR air = (AIR)mIR;
						if (air.GetPower() == 0x00){
							Log.i("Air Power", "Close");
							keyValue = mIR.Search(mRow, mIndex, mKey);
						}
					}
					if (keyValue == null)
						return;
					//0221码值输出打印
					ETGlobal.rprintHexString(keyValue);
					
					Intent intent = new Intent();  //Itent就是我们要发送的内容
		            intent.putExtra("infrared",keyValue);  
		            intent.setAction("send");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
		            getActivity().sendBroadcast(intent);   //发送广播
					System.out.println("===发送广播消息==");
					
					
//					ETGlobal.mTg.write(keyValue, keyValue.length);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					if (mCount != 1) {
						mCount--;
						mIndex--;
					}
					mTextCount.setText("(" + mCount + "/" + mTotal + ")");
					getActivity().setTitle(mName + "(" + mCount + "/" + mTotal + ")");
					mHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}

		}
	};

	@SuppressLint("InflateParams")
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int key = 0;
		byte[] keyValue = null;
		LayoutInflater mInflater = LayoutInflater.from(getActivity());
		View view = null;
		
		int id=arg0.getId();
		if (id==R.id.text_up) {
			mHandler.sendEmptyMessage(2);
		}else if (id==R.id.text_down) {
			mHandler.sendEmptyMessage(0);
		}else if (id==R.id.text_again) {
			mHandler.sendEmptyMessage(1);
		}else if (id==R.id.text_four_yes) {
			
//		}
		
//		switch (arg0.getId()) {
//		case R.id.text_up:
//			mHandler.sendEmptyMessage(2);
//			break;
//		case R.id.text_down:
//			mHandler.sendEmptyMessage(0);
//			break;
//		case R.id.text_again:
//			mHandler.sendEmptyMessage(1);
//			break;
//
//		case R.id.text_four_yes:
			view = mInflater.inflate(R.layout.dialog_set_name, null);
			final EditText name = (EditText) view.findViewById(R.id.edit_name);
			int len = mName.length() > 10? 10: mName.length();
			name.setText(mName.substring(0, len));
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.cancel();
			}
			mDialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setView(view)
					.setPositiveButton(R.string.str_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									ETGroup group = (ETGroup) ETPage
											.getInstance(getActivity())
											.GetItem(mGroupIndex);
									ETDevice device = null;
									switch (mType) {
									case DeviceType.DEVICE_REMOTE_TV:
										device = new ETDeviceTV(mRow, mIndex);
										device.SetName(name.getText()
												.toString());
										device.SetType(DeviceType.DEVICE_REMOTE_TV);
										device.SetRes(0);
										device.SetGID(group.GetID());
										device.Inster(ETDB
												.getInstance(getActivity()));

										break;
									case DeviceType.DEVICE_REMOTE_STB:
										device = new ETDeviceSTB(mRow, mIndex);
										device.SetName(name.getText()
												.toString());
										device.SetType(DeviceType.DEVICE_REMOTE_STB);
										device.SetRes(2);
										device.SetGID(group.GetID());
										device.Inster(ETDB
												.getInstance(getActivity()));
										break;
									case DeviceType.DEVICE_REMOTE_DVD:
										device = new ETDeviceDVD(mRow, mIndex);
										device.SetName(name.getText()
												.toString());
										device.SetType(DeviceType.DEVICE_REMOTE_DVD);
										device.SetRes(3);
										device.SetGID(group.GetID());
										device.Inster(ETDB
												.getInstance(getActivity()));
										break;
									case DeviceType.DEVICE_REMOTE_PJT:
										device = new ETDevicePJT(mRow, mIndex);
										device.SetName(name.getText()
												.toString());
										device.SetType(DeviceType.DEVICE_REMOTE_PJT);
										device.SetRes(5);
										device.SetGID(group.GetID());
										device.Inster(ETDB
												.getInstance(getActivity()));
										break;
									case DeviceType.DEVICE_REMOTE_FANS:
										device = new ETDeviceFANS(mRow, mIndex);
										device.SetName(name.getText()
												.toString());
										device.SetType(DeviceType.DEVICE_REMOTE_FANS);
										device.SetRes(4);
										device.SetGID(group.GetID());
										device.Inster(ETDB
												.getInstance(getActivity()));
										break;
									case DeviceType.DEVICE_REMOTE_IPTV:
										device = new ETDeviceIPTV(mRow, mIndex);
										device.SetName(name.getText()
												.toString());
										device.SetType(DeviceType.DEVICE_REMOTE_IPTV);
										device.SetRes(1);
										device.SetGID(group.GetID());
										device.Inster(ETDB
												.getInstance(getActivity()));
										break;
									case DeviceType.DEVICE_REMOTE_AIR:
										device = new ETDeviceAIR(mRow, mIndex);
										device.SetName(name.getText()
												.toString());
										device.SetType(DeviceType.DEVICE_REMOTE_AIR);
										device.SetRes(7);
										device.SetGID(group.GetID());
										((ETDeviceAIR)device).SetTemp(((AIR)mIR).GetTemp());
										((ETDeviceAIR)device).SetMode(((AIR)mIR).GetMode());
										((ETDeviceAIR)device).SetPower(((AIR)mIR).GetPower());
										((ETDeviceAIR)device).SetWindRate(((AIR)mIR).GetWindRate());
										((ETDeviceAIR)device).SetWindDir(((AIR)mIR).GetWindDir());
										((ETDeviceAIR)device).SetAutoWindDir(((AIR)mIR).GetAutoWindDir());
										device.Inster(ETDB
												.getInstance(getActivity()));
										break;
									case DeviceType.DEVICE_REMOTE_LIGHT:
										device = new ETDeviceLIGHT();
										device.SetName(name.getText()
												.toString());
										device.SetType(DeviceType.DEVICE_REMOTE_LIGHT);
										device.SetRes(6);
										device.SetGID(group.GetID());
										device.Inster(ETDB
												.getInstance(getActivity()));
										break;
									case DeviceType.DEVICE_REMOTE_DC:
										device = new ETDeviceDC();
										device.SetName(name.getText()
												.toString());
										device.SetType(DeviceType.DEVICE_REMOTE_DC);
										device.SetRes(8);
										device.SetGID(group.GetID());
										device.Inster(ETDB
												.getInstance(getActivity()));
										break;
									}
									FragmentDevice fragmentDevice = new FragmentDevice();
									FragmentTransaction transaction = getActivity()
											.getSupportFragmentManager()
											.beginTransaction();
									Bundle args = new Bundle();
									args.putInt("group", mGroupIndex);
									fragmentDevice.setArguments(args);
									transaction.replace(
											R.id.fragment_container,
											fragmentDevice);
									transaction.addToBackStack(null);
									transaction.commit();

								}
							}).create();

			mDialog.setTitle(R.string.str_dialog_set_name_title);
			mDialog.show();

//			break;
		}else if (id== R.id.text_tv_volsub) {
			key = IRKeyValue.KEY_TV_VOLUME_OUT;
		}else if (id==R.id.text_tv_voladd) {
			key = IRKeyValue.KEY_TV_VOLUME_IN;
		}else if (id==R.id.text_tv_mute) {
			key = IRKeyValue.KEY_TV_MUTE;
		}else if (id==R.id.text_iptv_volsub) {
			key = IRKeyValue.KEY_IPTV_VOLUME_OUT;
		}else if (id==R.id.text_iptv_voladd) {
			key = IRKeyValue.KEY_IPTV_VOLUME_IN;
		}else if (id==R.id.text_iptv_mute) {
			key = IRKeyValue.KEY_IPTV_MUTE;
		}else if (id==R.id.text_stb_volsub) {
			key = IRKeyValue.KEY_STB_VOLUME_OUT;
		}else if (id==R.id.text_stb_voladd) {
			key = IRKeyValue.KEY_STB_VOLUME_IN;
		}else if (id==R.id.text_stb_guide) {
			key = IRKeyValue.KEY_STB_GUIDE;
		}else if (id==R.id.text_dvd_menu) {
			key = IRKeyValue.KEY_DVD_MENU;
		}
		
		
//		case R.id.text_tv_volsub:
//			key = IRKeyValue.KEY_TV_VOLUME_OUT;
//
//			break;
//		case R.id.text_tv_voladd:
//			key = IRKeyValue.KEY_TV_VOLUME_IN;
//			break;
//		case R.id.text_tv_mute:
//			key = IRKeyValue.KEY_TV_MUTE;
//			break;
//		case R.id.text_iptv_volsub:
//			key = IRKeyValue.KEY_IPTV_VOLUME_OUT;
//			break;
//		case R.id.text_iptv_voladd:
//			key = IRKeyValue.KEY_IPTV_VOLUME_IN;
//			break;
//		case R.id.text_iptv_mute:
//			key = IRKeyValue.KEY_IPTV_MUTE;
//			break;
//		case R.id.text_stb_volsub:
//			key = IRKeyValue.KEY_STB_VOLUME_OUT;
//			break;
//		case R.id.text_stb_voladd:
//			key = IRKeyValue.KEY_STB_VOLUME_IN;
//			break;
//		case R.id.text_stb_guide:
//			key = IRKeyValue.KEY_STB_GUIDE;
//			break;
//		case R.id.text_dvd_menu:
//			key = IRKeyValue.KEY_DVD_MENU;
//			break;
//		case R.id.text_dvd_mute:
//			key = IRKeyValue.KEY_DVD_MUTE;
//			break;
//		case R.id.text_dvd_play:
//			key = IRKeyValue.KEY_DVD_PLAY;
//			break;
//		case R.id.text_pjt_voladd:
//			key = IRKeyValue.KEY_PJT_VOLUME_IN;
//			break;
//		case R.id.text_pjt_volsub:
//			key = IRKeyValue.KEY_PJT_VOLUME_OUT;
//			break;
//		case R.id.text_pjt_computer:
//			key = IRKeyValue.KEY_PJT_COMPUTER;
//			break;
//		case R.id.text_fans_wind_rate:
//			key = IRKeyValue.KEY_FANS_AIR_RATE;
//			break;
//		case R.id.text_fans_wind_speed:
//			key = IRKeyValue.KEY_FANS_WIND_SPEED;
//			break;
//		case R.id.text_fans_timer:
//			key = IRKeyValue.KEY_FANS_TIMER;
//			break;
//		case R.id.text_light_5:
//			key = IRKeyValue.KEY_LIGHT_KEY5;
//			break;
//		case R.id.text_light_brightness_add:
//			key = IRKeyValue.KEY_LIGHT_LIGHT;
//			break;
//		case R.id.text_light_brightness_sub:
//			key = IRKeyValue.KEY_LIGHT_DARK;
//			break;
		
		else if (id==R.id.text_air_power) {
			key = IRKeyValue.KEY_AIR_POWER;
		}else if (id==R.id.text_air_tempsub) {
			key = IRKeyValue.KEY_AIR_TEMPERATURE_OUT;
		}else if (id==R.id.text_air_tempadd) {
			key = IRKeyValue.KEY_AIR_TEMPERATURE_IN;
		}else if (id==R.id.text_air_mode) {
			key = IRKeyValue.KEY_AIR_MODE;
		}
		
//		case R.id.text_air_power:
//			key = IRKeyValue.KEY_AIR_POWER;
//			break;
//		case R.id.text_air_tempsub:
//			key = IRKeyValue.KEY_AIR_TEMPERATURE_OUT;
//			break;
//		case R.id.text_air_tempadd:
//			key = IRKeyValue.KEY_AIR_TEMPERATURE_IN;
//			break;
//		case R.id.text_air_mode:
//			key = IRKeyValue.KEY_AIR_MODE;
//			break;
		
		else if (id==R.id.text_yes) {
			
//		}
//		case R.id.text_yes:
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.cancel();
			}
			switch (mType) {
			case DeviceType.DEVICE_REMOTE_TV:
				view = mInflater.inflate(R.layout.fragment_wizards_four_tv,
						null);
				TextView textViewTVMute = (TextView) view
						.findViewById(R.id.text_tv_mute);
				textViewTVMute.setOnClickListener(this);
				textViewTVMute
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewTVVolAdd = (TextView) view
						.findViewById(R.id.text_tv_voladd);
				textViewTVVolAdd.setOnClickListener(this);
				textViewTVVolAdd
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewTVVolSub = (TextView) view
						.findViewById(R.id.text_tv_volsub);
				textViewTVVolSub.setOnClickListener(this);
				textViewTVVolSub
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				break;
			case DeviceType.DEVICE_REMOTE_IPTV:
				view = mInflater.inflate(R.layout.fragment_wizards_four_iptv,
						null);
				TextView textViewIPTVVolAdd = (TextView) view
						.findViewById(R.id.text_iptv_voladd);
				textViewIPTVVolAdd.setOnClickListener(this);
				textViewIPTVVolAdd
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewIPTVMute = (TextView) view
						.findViewById(R.id.text_iptv_mute);
				textViewIPTVMute.setOnClickListener(this);
				textViewIPTVMute
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewIPTVVolSub = (TextView) view
						.findViewById(R.id.text_iptv_volsub);
				textViewIPTVVolSub.setOnClickListener(this);
				textViewIPTVVolSub
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				break;
			case DeviceType.DEVICE_REMOTE_STB:
				view = mInflater.inflate(R.layout.fragment_wizards_four_stb,
						null);
				TextView textViewSTBGuide = (TextView) view
						.findViewById(R.id.text_stb_guide);
				textViewSTBGuide.setOnClickListener(this);
				textViewSTBGuide
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewSTBVolAdd = (TextView) view
						.findViewById(R.id.text_stb_voladd);
				textViewSTBVolAdd.setOnClickListener(this);
				textViewSTBVolAdd
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewSTBVolSub = (TextView) view
						.findViewById(R.id.text_stb_volsub);
				textViewSTBVolSub.setOnClickListener(this);
				textViewSTBVolSub
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				break;
			case DeviceType.DEVICE_REMOTE_DVD:
				view = mInflater.inflate(R.layout.fragment_wizards_four_dvd,
						null);
				TextView textViewMenu = (TextView) view
						.findViewById(R.id.text_dvd_menu);
				textViewMenu.setOnClickListener(this);
				textViewMenu.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewMute = (TextView) view
						.findViewById(R.id.text_dvd_mute);
				textViewMute.setOnClickListener(this);
				textViewMute.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewPlay = (TextView) view
						.findViewById(R.id.text_dvd_play);
				textViewPlay.setOnClickListener(this);
				textViewPlay.setBackgroundResource(R.drawable.ic_button_cast_selector);
				break;
			case DeviceType.DEVICE_REMOTE_PJT:
				view = mInflater.inflate(R.layout.fragment_wizards_four_pjt,
						null);
				TextView textViewPJTVolAdd = (TextView) view
						.findViewById(R.id.text_pjt_voladd);
				textViewPJTVolAdd.setOnClickListener(this);
				textViewPJTVolAdd
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewPJTComputer = (TextView) view
						.findViewById(R.id.text_pjt_computer);
				textViewPJTComputer.setOnClickListener(this);
				textViewPJTComputer
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				
				TextView textViewVolSub = (TextView) view
						.findViewById(R.id.text_pjt_volsub);
				textViewVolSub.setOnClickListener(this);
				textViewVolSub
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				break;
			case DeviceType.DEVICE_REMOTE_LIGHT:
				view = mInflater.inflate(R.layout.fragment_wizards_four_light,
						null);
				TextView textViewLightLight = (TextView) view
						.findViewById(R.id.text_light_brightness_add);
				textViewLightLight.setOnClickListener(this);
				textViewLightLight
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewLightDark = (TextView) view
						.findViewById(R.id.text_light_brightness_sub);
				textViewLightDark.setOnClickListener(this);
				textViewLightDark
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				TextView textViewLight5 = (TextView) view
						.findViewById(R.id.text_light_5);
				textViewLight5.setOnClickListener(this);
				textViewLight5
						.setBackgroundResource(R.drawable.ic_button_cast_selector);
				break;
			case DeviceType.DEVICE_REMOTE_FANS:
				view = mInflater.inflate(R.layout.fragment_wizards_four_fans,
						null);
				TextView textViewSharkHead = (TextView) view
						.findViewById(R.id.text_fans_wind_rate);
				textViewSharkHead.setOnClickListener(this);
				textViewSharkHead.setBackgroundResource(R.drawable.ic_button_cast_selector);
				
				TextView textViewLi = (TextView) view
						.findViewById(R.id.text_fans_wind_speed);
				textViewLi.setOnClickListener(this);
				textViewLi.setBackgroundResource(R.drawable.ic_button_cast_selector);
				
				TextView textViewTimer = (TextView) view
						.findViewById(R.id.text_fans_timer);
				textViewTimer.setOnClickListener(this);
				textViewTimer.setBackgroundResource(R.drawable.ic_button_cast_selector);
				break;
			case DeviceType.DEVICE_REMOTE_AIR:
				view = mInflater.inflate(R.layout.fragment_wizards_four_air,
						null);
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
//				AssetManager mgr = getActivity().getAssets();
//				Typeface tf = Typeface.createFromAsset(mgr, "fonts/Clockopia.ttf");
//				mTextViewTemp.setTypeface(tf);
				mTextViewTemp.setText("");
				
				TextView textViewPower = (TextView) view
						.findViewById(R.id.text_air_power);
				textViewPower.setOnClickListener(this);
				textViewPower.setBackgroundResource(R.drawable.ic_power_selector);
				
				TextView textViewTempAdd = (TextView) view
						.findViewById(R.id.text_air_tempadd);
				textViewTempAdd.setOnClickListener(this);
				textViewTempAdd.setBackgroundResource(R.drawable.ic_button_cast_selector);
				
				TextView textViewTempSub = (TextView) view
						.findViewById(R.id.text_air_tempsub);
				textViewTempSub.setOnClickListener(this);
				textViewTempSub.setBackgroundResource(R.drawable.ic_button_cast_selector);
				
				TextView textViewMode = (TextView) view
						.findViewById(R.id.text_air_mode);
				textViewMode.setOnClickListener(this);
				textViewMode.setBackgroundResource(R.drawable.ic_button_cast_selector);
				break;
			case DeviceType.DEVICE_REMOTE_DC:
				view = mInflater.inflate(R.layout.fragment_wizards_four_dc,
						null);

				break;
			}
			TextView textViewFourNo = (TextView) view
					.findViewById(R.id.text_four_no);
			textViewFourNo.setOnClickListener(this);
			textViewFourNo
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewFourYes = (TextView) view
					.findViewById(R.id.text_four_yes);
			textViewFourYes.setOnClickListener(this);
			textViewFourYes
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			mDialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.str_other_set)
					.setMessage(R.string.str_wizards_info_4_2).setView(view)
					.create();
			mDialog.show();
//			break;
		}else if (id==R.id.text_test) {
			mHandler.sendEmptyMessage(1);
			
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.cancel();
			}
			view = mInflater.inflate(R.layout.fragment_wizards_four_1, null);
			TextView textFourUp = (TextView) view.findViewById(R.id.text_up);
			textFourUp.setOnClickListener(this);
			textFourUp
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textFourDown = (TextView) view
					.findViewById(R.id.text_down);
			textFourDown.setOnClickListener(this);
			textFourDown
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textFourAgain = (TextView) view
					.findViewById(R.id.text_again);
			textFourAgain.setOnClickListener(this);
			textFourAgain
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textFourYes = (TextView) view.findViewById(R.id.text_yes);
			textFourYes.setOnClickListener(this);
			textFourYes
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			mDialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.str_other_set)
					.setMessage(R.string.str_wizards_info_4_1).setView(view)
					.create();
			mDialog.show();
		}else if (id==R.id.text_four_no) {
//			
//		}
//		
//		
////		case R.id.text_test:
////			mHandler.sendEmptyMessage(1);
//		case R.id.text_four_no:
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.cancel();
			}
			view = mInflater.inflate(R.layout.fragment_wizards_four_1, null);
			TextView textFourUp = (TextView) view.findViewById(R.id.text_up);
			textFourUp.setOnClickListener(this);
			textFourUp
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textFourDown = (TextView) view
					.findViewById(R.id.text_down);
			textFourDown.setOnClickListener(this);
			textFourDown
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textFourAgain = (TextView) view
					.findViewById(R.id.text_again);
			textFourAgain.setOnClickListener(this);
			textFourAgain
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textFourYes = (TextView) view.findViewById(R.id.text_yes);
			textFourYes.setOnClickListener(this);
			textFourYes
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			mDialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.str_other_set)
					.setMessage(R.string.str_wizards_info_4_1).setView(view)
					.create();
			mDialog.show();
//			break;
		}
		

		boolean isSend = true;
		try {
			
			if (key == 0)
				return;
			keyValue = mIR.Search(mRow, mIndex, key);
			if (keyValue == null)
				return;
			if (ETGlobal.mTg == null) {
				isSend = false;
			}
			//0221码值输出打印
			ETGlobal.rprintHexString(keyValue);
			
			Intent intent = new Intent();  //Itent就是我们要发送的内容
            intent.putExtra("infrared",keyValue);  
            intent.setAction("send");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
            getActivity().sendBroadcast(intent);   //发送广播
			System.out.println("===发送广播消息==");
			
			
//			int n = ETGlobal.mTg.write(keyValue, keyValue.length);
//			if (n < 0) {
//				isSend = false;
//			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mType == DeviceType.DEVICE_REMOTE_AIR)
		{
			F5();
		}
		
		//0221屏蔽弹出广告
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
//									// method stub
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
	
	public void F5() {
		if (((AIR)mIR).GetPower() == 0x01) {
			switch (((AIR)mIR).GetMode()) {
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

			switch (((AIR)mIR).GetWindRate()) {
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

			if (((AIR)mIR).GetAutoWindDir() == 0x01) {
				mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_1);
			} else {
				switch (((AIR)mIR).GetWindDir()) {
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
			if (((AIR)mIR).GetMode() == 2 || ((AIR)mIR).GetMode() == 5) {
				mTextViewTemp.setText(Byte.valueOf(((AIR)mIR).GetTemp())
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
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		this.getActivity().setTitle(R.string.str_wizards);
		FragmentWizardsTwo fragmentThree = new FragmentWizardsTwo();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("type", mType);
		args.putInt("group", mGroupIndex);
		fragmentThree.setArguments(args);
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragmentThree);
		// transactionBt.addToBackStack(null);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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
