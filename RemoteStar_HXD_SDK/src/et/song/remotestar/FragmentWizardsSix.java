package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevice;
import et.song.etclass.ETGroup;
import et.song.etclass.ETKey;
import et.song.etclass.ETKeyEx;
import et.song.etclass.ETPage;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remote.face.IRKeyExValue;
import et.song.remote.face.IRKeyValue;
import et.song.remotestar.hxd.sdk.R;
import et.song.tool.ETTool;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentWizardsSix extends SherlockFragment implements
		OnClickListener, OnLongClickListener, IBack{
	private int mType;
	private int mGroupIndex;
	private ETDevice mDevice = null;
	private RecvReceiver mReceiver;
	TextView mView = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mType = this.getArguments().getInt("type");
		mGroupIndex = this.getArguments().getInt("group");
		ETGroup group = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = ETDevice.Builder(mType);
		mDevice.SetGID(group.GetID());
		ETTool.MessageBox(getActivity(), 0.8f, getString(R.string.str_study_start_info_1), true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getActivity().setTitle(R.string.str_wizards);

		View view = null;
		if (mType == DeviceType.DEVICE_REMOTE_TV) {
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
			textViewVolAdd
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewChAdd = (TextView) view
					.findViewById(R.id.text_tv_ch_add);
			textViewChAdd.setOnClickListener(this);
			textViewChAdd
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewVolSub = (TextView) view
					.findViewById(R.id.text_tv_vol_sub);
			textViewVolSub.setOnClickListener(this);
			textViewVolSub
					.setBackgroundResource(R.drawable.ic_button_round_selector);
			TextView textViewChSub = (TextView) view
					.findViewById(R.id.text_tv_ch_sub);
			textViewChSub.setOnClickListener(this);

			textViewChSub
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewUp = (TextView) view.findViewById(R.id.text_tv_up);
			textViewUp.setOnClickListener(this);

			textViewUp.setBackgroundResource(R.drawable.ic_button_up_selector);

			TextView textViewDown = (TextView) view
					.findViewById(R.id.text_tv_down);
			textViewDown.setOnClickListener(this);
			textViewDown
					.setBackgroundResource(R.drawable.ic_button_down_selector);

			TextView textViewLeft = (TextView) view
					.findViewById(R.id.text_tv_left);
			textViewLeft.setOnClickListener(this);
			textViewLeft
					.setBackgroundResource(R.drawable.ic_button_left_selector);
			TextView textViewRight = (TextView) view
					.findViewById(R.id.text_tv_right);
			textViewRight.setOnClickListener(this);
			textViewRight
					.setBackgroundResource(R.drawable.ic_button_right_selector);

			mDevice.SetName(getResources().getStringArray(R.array.strs_device)[0]);
			mDevice.SetType(DeviceType.DEVICE_REMOTE_TV);
			mDevice.SetRes(0);
		} else if (mType == DeviceType.DEVICE_REMOTE_IPTV) {
			view = inflater.inflate(R.layout.fragment_iptv, container, false);

			TextView textViewPower = (TextView) view
					.findViewById(R.id.text_iptv_power);
			textViewPower.setOnClickListener(this);
			textViewPower.setOnLongClickListener(this);
			textViewPower.setBackgroundResource(R.drawable.ic_power_selector);
			textViewPower.getLayoutParams().width = (ETGlobal.W - 80) / 5;
			textViewPower.getLayoutParams().height = (ETGlobal.W - 80) / 5;
			
			TextView textViewHome = (TextView) view
					.findViewById(R.id.text_iptv_home);
			textViewHome.setOnClickListener(this);
			textViewHome.setOnLongClickListener(this);
			textViewHome.setBackgroundResource(R.drawable.ic_home_selector);
			textViewHome.getLayoutParams().width = (ETGlobal.W - 80) / 5;
			textViewHome.getLayoutParams().height = (ETGlobal.W - 80) / 5;

			TextView textViewMute = (TextView) view.findViewById(R.id.text_iptv_mute);
			textViewMute.setOnClickListener(this);
			textViewMute.setOnLongClickListener(this);
			textViewMute.setBackgroundResource(R.drawable.btn_style_white);
			textViewMute.getLayoutParams().width = (ETGlobal.W - 160) / 4;
			textViewMute.getLayoutParams().height = (ETGlobal.W - 160) * 3
					/ (4 * 4);

			TextView textViewPlayPause = (TextView) view.findViewById(R.id.text_iptv_play_pause);
			textViewPlayPause.setOnClickListener(this);
			textViewPlayPause.setOnLongClickListener(this);
			textViewPlayPause.setBackgroundResource(R.drawable.btn_style_white);
			textViewPlayPause.getLayoutParams().width = (ETGlobal.W - 160) / 4;
			textViewPlayPause.getLayoutParams().height = (ETGlobal.W - 160) * 3
					/ (4 * 4);

			TextView textView123 = (TextView) view.findViewById(R.id.text_iptv_123);
			textView123.setOnClickListener(this);
			textView123.setBackgroundResource(R.drawable.btn_style_white);
			textView123.getLayoutParams().width = (ETGlobal.W - 160) / 4;
			textView123.getLayoutParams().height = (ETGlobal.W - 160) * 3
					/ (4 * 4);

			TextView textViewOk = (TextView) view.findViewById(R.id.text_iptv_ok);
			textViewOk.setOnClickListener(this);
			textViewOk.setOnLongClickListener(this);
			textViewOk.setBackgroundResource(R.drawable.ic_button_ok_selector);

			TextView textViewBack = (TextView) view.findViewById(R.id.text_iptv_back);
			textViewBack.setOnClickListener(this);
			textViewBack.setOnLongClickListener(this);
			textViewBack.setBackgroundResource(R.drawable.btn_style_white);
			textViewBack.getLayoutParams().width = (ETGlobal.W - 160) / 4;
			textViewBack.getLayoutParams().height = (ETGlobal.W - 160) * 3
					/ (4 * 4);

			TextView textViewVolAdd = (TextView) view
					.findViewById(R.id.text_iptv_vol_add);
			textViewVolAdd.setOnClickListener(this);
			textViewVolAdd.setOnLongClickListener(this);
			textViewVolAdd
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewChAdd = (TextView) view
					.findViewById(R.id.text_iptv_ch_add);
			textViewChAdd.setOnClickListener(this);
			textViewChAdd.setOnLongClickListener(this);
			textViewChAdd
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewVolSub = (TextView) view
					.findViewById(R.id.text_iptv_vol_sub);
			textViewVolSub.setOnClickListener(this);
			textViewVolSub.setOnLongClickListener(this);
			textViewVolSub
					.setBackgroundResource(R.drawable.ic_button_round_selector);
			TextView textViewChSub = (TextView) view
					.findViewById(R.id.text_iptv_ch_sub);
			textViewChSub.setOnClickListener(this);
			textViewChSub.setOnLongClickListener(this);
			textViewChSub
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewUp = (TextView) view
					.findViewById(R.id.text_iptv_up);
			textViewUp.setOnClickListener(this);
			textViewUp.setOnLongClickListener(this);
			textViewUp.setBackgroundResource(R.drawable.ic_button_up_selector);

			TextView textViewDown = (TextView) view
					.findViewById(R.id.text_iptv_down);
			textViewDown.setOnClickListener(this);
			textViewDown.setOnLongClickListener(this);
			textViewDown
					.setBackgroundResource(R.drawable.ic_button_down_selector);
			TextView textViewLeft = (TextView) view
					.findViewById(R.id.text_iptv_left);
			textViewLeft.setOnClickListener(this);
			textViewLeft.setOnLongClickListener(this);
			textViewLeft
					.setBackgroundResource(R.drawable.ic_button_left_selector);
			TextView textViewRight = (TextView) view
					.findViewById(R.id.text_iptv_right);
			textViewRight.setOnClickListener(this);
			textViewRight.setOnLongClickListener(this);
			textViewRight
					.setBackgroundResource(R.drawable.ic_button_right_selector);
			mDevice.SetName(getResources().getStringArray(R.array.strs_device)[1]);
			mDevice.SetType(DeviceType.DEVICE_REMOTE_IPTV);
			mDevice.SetRes(1);

		} else if (mType == DeviceType.DEVICE_REMOTE_STB) {
			view = inflater.inflate(R.layout.fragment_stb, container, false);
			TextView textViewAwait = (TextView) view
					.findViewById(R.id.text_stb_await);
			textViewAwait.setOnClickListener(this);
			textViewAwait.setOnLongClickListener(this);
			textViewAwait
					.setBackgroundResource(R.drawable.ic_button_round_selector);
			TextView textView123 = (TextView) view
					.findViewById(R.id.text_stb_123);
			textView123.setOnClickListener(this);
			textView123
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewBack = (TextView) view
					.findViewById(R.id.text_stb_back);
			textViewBack.setOnClickListener(this);
			textViewBack.setOnLongClickListener(this);
			textViewBack
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewMenu = (TextView) view
					.findViewById(R.id.text_stb_menu);
			textViewMenu.setOnClickListener(this);
			textViewMenu.setOnLongClickListener(this);
			textViewMenu
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewGuide = (TextView) view
					.findViewById(R.id.text_stb_guide);
			textViewGuide.setOnClickListener(this);
			textViewGuide.setOnLongClickListener(this);
			textViewGuide
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewOk = (TextView) view
					.findViewById(R.id.text_stb_ok);
			textViewOk.setOnClickListener(this);
			textViewOk.setOnLongClickListener(this);
			textViewOk.setBackgroundResource(R.drawable.ic_button_ok_selector);

			TextView textViewVolAdd = (TextView) view
					.findViewById(R.id.text_stb_vol_add);
			textViewVolAdd.setOnClickListener(this);
			textViewVolAdd.setOnLongClickListener(this);
			textViewVolAdd
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewChAdd = (TextView) view
					.findViewById(R.id.text_stb_ch_add);
			textViewChAdd.setOnClickListener(this);
			textViewChAdd.setOnLongClickListener(this);
			textViewChAdd
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewVolSub = (TextView) view
					.findViewById(R.id.text_stb_vol_sub);
			textViewVolSub.setOnClickListener(this);
			textViewVolSub.setOnLongClickListener(this);
			textViewVolSub
					.setBackgroundResource(R.drawable.ic_button_round_selector);
			TextView textViewChSub = (TextView) view
					.findViewById(R.id.text_stb_ch_sub);
			textViewChSub.setOnClickListener(this);
			textViewChSub.setOnLongClickListener(this);
			textViewChSub
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewUp = (TextView) view
					.findViewById(R.id.text_stb_up);
			textViewUp.setOnClickListener(this);
			textViewUp.setOnLongClickListener(this);
			textViewUp.setBackgroundResource(R.drawable.ic_button_up_selector);

			TextView textViewDown = (TextView) view
					.findViewById(R.id.text_stb_down);
			textViewDown.setOnClickListener(this);
			textViewDown.setOnLongClickListener(this);
			textViewDown
					.setBackgroundResource(R.drawable.ic_button_down_selector);
			TextView textViewLeft = (TextView) view
					.findViewById(R.id.text_stb_left);
			textViewLeft.setOnClickListener(this);
			textViewLeft.setOnLongClickListener(this);
			textViewLeft
					.setBackgroundResource(R.drawable.ic_button_left_selector);
			TextView textViewRight = (TextView) view
					.findViewById(R.id.text_stb_right);
			textViewRight.setOnClickListener(this);
			textViewRight.setOnLongClickListener(this);
			textViewRight
					.setBackgroundResource(R.drawable.ic_button_right_selector);
			mDevice.SetName(getResources().getStringArray(R.array.strs_device)[2]);
			mDevice.SetType(DeviceType.DEVICE_REMOTE_STB);
			mDevice.SetRes(2);
			for (int i = 0; i < IRKeyValue.STB_KEY_COUNT ; i++){
				ETKey key = new ETKey();
				key.SetState(ETKey.ETKEY_STATE_STUDY);
				key.SetKey(IRKeyValue.STBValue | (i * 2 + 1));
				key.SetDID(mDevice.GetID());
				key.SetBrandIndex(0);
				key.SetBrandPos(0);
				key.SetName("");
				key.SetPos(0, 0);
				key.SetRes(0);
				key.SetRow(0);
				mDevice.SetKey(key);
			}
		} else if (mType == DeviceType.DEVICE_REMOTE_DVD) {
			view = inflater.inflate(R.layout.fragment_dvd, container, false);

			TextView textViewPower = (TextView) view
					.findViewById(R.id.text_dvd_power);
			textViewPower.setOnClickListener(this);
			textViewPower.setOnLongClickListener(this);
			textViewPower.setBackgroundResource(R.drawable.ic_power_selector);

			TextView textViewOC = (TextView) view
					.findViewById(R.id.text_dvd_oc);
			textViewOC.setOnClickListener(this);
			textViewOC.setOnLongClickListener(this);
			textViewOC
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewPause = (TextView) view
					.findViewById(R.id.text_dvd_pause);
			textViewPause.setOnClickListener(this);
			textViewPause.setOnLongClickListener(this);
			textViewPause
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewPlay = (TextView) view
					.findViewById(R.id.text_dvd_play);
			textViewPlay.setOnClickListener(this);
			textViewPlay.setOnLongClickListener(this);
			textViewPlay
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewStop = (TextView) view
					.findViewById(R.id.text_dvd_stop);
			textViewStop.setOnClickListener(this);
			textViewStop.setOnLongClickListener(this);
			textViewStop
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewMute = (TextView) view
					.findViewById(R.id.text_dvd_mute);
			textViewMute.setOnClickListener(this);
			textViewMute.setOnLongClickListener(this);
			textViewMute
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewUpSong = (TextView) view
					.findViewById(R.id.text_dvd_up_song);
			textViewUpSong.setOnClickListener(this);
			textViewUpSong.setOnLongClickListener(this);
			textViewUpSong
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewDownSong = (TextView) view
					.findViewById(R.id.text_dvd_down_song);
			textViewDownSong.setOnClickListener(this);
			textViewDownSong.setOnLongClickListener(this);
			textViewDownSong
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewFastForward = (TextView) view
					.findViewById(R.id.text_dvd_fast_forward);
			textViewFastForward.setOnClickListener(this);
			textViewFastForward.setOnLongClickListener(this);
			textViewFastForward
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewFastBack = (TextView) view
					.findViewById(R.id.text_dvd_fast_back);
			textViewFastBack.setOnClickListener(this);
			textViewFastBack.setOnLongClickListener(this);
			textViewFastBack
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewTitle = (TextView) view
					.findViewById(R.id.text_dvd_title);
			textViewTitle.setOnClickListener(this);
			textViewTitle.setOnLongClickListener(this);
			textViewTitle
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewStandard = (TextView) view
					.findViewById(R.id.text_dvd_standard);
			textViewStandard.setOnClickListener(this);
			textViewStandard.setOnLongClickListener(this);
			textViewStandard
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewMenu = (TextView) view
					.findViewById(R.id.text_dvd_menu);
			textViewMenu.setOnClickListener(this);
			textViewMenu.setOnLongClickListener(this);
			textViewMenu
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewBack = (TextView) view
					.findViewById(R.id.text_dvd_back);
			textViewBack.setOnClickListener(this);
			textViewBack.setOnLongClickListener(this);
			textViewBack
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewOk = (TextView) view
					.findViewById(R.id.text_dvd_ok);
			textViewOk.setOnClickListener(this);
			textViewOk.setOnLongClickListener(this);
			textViewOk.setBackgroundResource(R.drawable.ic_button_ok_selector);

			TextView textViewUp = (TextView) view
					.findViewById(R.id.text_dvd_up);
			textViewUp.setOnClickListener(this);
			textViewUp.setOnLongClickListener(this);
			textViewUp.setBackgroundResource(R.drawable.ic_button_up_selector);

			TextView textViewDown = (TextView) view
					.findViewById(R.id.text_dvd_down);
			textViewDown.setOnClickListener(this);
			textViewDown.setOnLongClickListener(this);
			textViewDown
					.setBackgroundResource(R.drawable.ic_button_down_selector);
			TextView textViewLeft = (TextView) view
					.findViewById(R.id.text_dvd_left);
			textViewLeft.setOnClickListener(this);
			textViewLeft.setOnLongClickListener(this);
			textViewLeft
					.setBackgroundResource(R.drawable.ic_button_left_selector);
			TextView textViewRight = (TextView) view
					.findViewById(R.id.text_dvd_right);
			textViewRight.setOnClickListener(this);
			textViewRight.setOnLongClickListener(this);
			textViewRight
					.setBackgroundResource(R.drawable.ic_button_right_selector);
			mDevice.SetName(getResources().getStringArray(R.array.strs_device)[3]);
			mDevice.SetType(DeviceType.DEVICE_REMOTE_DVD);
			mDevice.SetRes(3);
			for (int i = 0; i < IRKeyValue.DVD_KEY_COUNT ; i++){
				ETKey key = new ETKey();
				key.SetState(ETKey.ETKEY_STATE_STUDY);
				key.SetKey(IRKeyValue.DVDValue | (i * 2 + 1));
				key.SetDID(mDevice.GetID());
				key.SetBrandIndex(0);
				key.SetBrandPos(0);
				key.SetName("");
				key.SetPos(0, 0);
				key.SetRes(0);
				key.SetRow(0);
				mDevice.SetKey(key);
			}
		} else if (mType == DeviceType.DEVICE_REMOTE_FANS) {
			view = inflater.inflate(R.layout.fragment_fans, container, false);

			TextView textViewPower = (TextView) view
					.findViewById(R.id.text_fans_power);
			textViewPower.setOnClickListener(this);
			textViewPower.setBackgroundResource(R.drawable.ic_power_selector);

			TextView textViewSharkHead = (TextView) view
					.findViewById(R.id.text_fans_shake_head);
			textViewSharkHead.setOnClickListener(this);
			textViewSharkHead
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewMode = (TextView) view
					.findViewById(R.id.text_fans_mode);
			textViewMode.setOnClickListener(this);
			textViewMode
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewCool = (TextView) view
					.findViewById(R.id.text_fans_cool);
			textViewCool.setOnClickListener(this);
			textViewCool
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewLight = (TextView) view
					.findViewById(R.id.text_fans_light);
			textViewLight.setOnClickListener(this);
			textViewLight
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewSleep = (TextView) view
					.findViewById(R.id.text_fans_sleep);
			textViewSleep.setOnClickListener(this);
			textViewSleep
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewLi = (TextView) view
					.findViewById(R.id.text_fans_li);
			textViewLi.setOnClickListener(this);
			textViewLi
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewTimer = (TextView) view
					.findViewById(R.id.text_fans_timer);
			textViewTimer.setOnClickListener(this);
			textViewTimer
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewWindSpeed = (TextView) view
					.findViewById(R.id.text_fans_wind_speed);
			textViewWindSpeed.setOnClickListener(this);
			textViewWindSpeed
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewWindRate = (TextView) view
					.findViewById(R.id.text_fans_wind_rate);
			textViewWindRate.setOnClickListener(this);
			textViewWindRate
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewWindRateLow = (TextView) view
					.findViewById(R.id.text_fans_wind_rate_low);
			textViewWindRateLow.setOnClickListener(this);
			textViewWindRateLow
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewWindRateMid = (TextView) view
					.findViewById(R.id.text_fans_wind_rate_mid);
			textViewWindRateMid.setOnClickListener(this);
			textViewWindRateMid
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewWindRateHigh = (TextView) view
					.findViewById(R.id.text_fans_wind_rate_high);
			textViewWindRateHigh.setOnClickListener(this);
			textViewWindRateHigh
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textView123 = (TextView) view
					.findViewById(R.id.text_fans_123);
			textView123.setOnClickListener(this);
			textView123
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			mDevice.SetName(getResources().getStringArray(R.array.strs_device)[4]);
			mDevice.SetType(DeviceType.DEVICE_REMOTE_FANS);
			mDevice.SetRes(4);
			for (int i = 0; i < IRKeyValue.FANS_KEY_COUNT ; i++){
				ETKey key = new ETKey();
				key.SetState(ETKey.ETKEY_STATE_STUDY);
				key.SetKey(IRKeyValue.FANSValue | (i * 2 + 1));
				key.SetDID(mDevice.GetID());
				key.SetBrandIndex(0);
				key.SetBrandPos(0);
				key.SetName("");
				key.SetPos(0, 0);
				key.SetRes(0);
				key.SetRow(0);
				mDevice.SetKey(key);
			}
		} else if (mType == DeviceType.DEVICE_REMOTE_PJT) {
			view = inflater.inflate(R.layout.fragment_pjt, container, false);

			TextView textViewPowerON = (TextView) view
					.findViewById(R.id.text_pjt_power_on);
			textViewPowerON.setOnClickListener(this);
			textViewPowerON.setOnLongClickListener(this);
			textViewPowerON
					.setBackgroundResource(R.drawable.ic_button_round_selector);
			TextView textViewPowerOFF = (TextView) view
					.findViewById(R.id.text_pjt_power_off);
			textViewPowerOFF.setOnClickListener(this);
			textViewPowerOFF.setOnLongClickListener(this);
			textViewPowerOFF
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewComputer = (TextView) view
					.findViewById(R.id.text_pjt_computer);
			textViewComputer.setOnClickListener(this);
			textViewComputer.setOnLongClickListener(this);
			textViewComputer
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewVideo = (TextView) view
					.findViewById(R.id.text_pjt_video);
			textViewVideo.setOnClickListener(this);
			textViewVideo.setOnLongClickListener(this);
			textViewVideo
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewSignal = (TextView) view
					.findViewById(R.id.text_pjt_signal);
			textViewSignal.setOnClickListener(this);
			textViewSignal.setOnLongClickListener(this);
			textViewSignal
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewMute = (TextView) view
					.findViewById(R.id.text_pjt_mute);
			textViewMute.setOnClickListener(this);
			textViewMute.setOnLongClickListener(this);
			textViewMute
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewZoomIn = (TextView) view
					.findViewById(R.id.text_pjt_zoom_in);
			textViewZoomIn.setOnClickListener(this);
			textViewZoomIn.setOnLongClickListener(this);
			textViewZoomIn
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewZoomOut = (TextView) view
					.findViewById(R.id.text_pjt_zoom_out);
			textViewZoomOut.setOnClickListener(this);
			textViewZoomOut.setOnLongClickListener(this);
			textViewZoomOut
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewAuto = (TextView) view
					.findViewById(R.id.text_pjt_auto);
			textViewAuto.setOnClickListener(this);
			textViewAuto.setOnLongClickListener(this);
			textViewAuto
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewBrightness = (TextView) view
					.findViewById(R.id.text_pjt_brightness);
			textViewBrightness.setOnClickListener(this);
			textViewBrightness.setOnLongClickListener(this);
			textViewBrightness
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewPictureIn = (TextView) view
					.findViewById(R.id.text_pjt_picture_in);
			textViewPictureIn.setOnClickListener(this);
			textViewPictureIn.setOnLongClickListener(this);
			textViewPictureIn
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewPictureOut = (TextView) view
					.findViewById(R.id.text_pjt_picture_out);
			textViewPictureOut.setOnClickListener(this);
			textViewPictureOut.setOnLongClickListener(this);
			textViewPictureOut
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewPause = (TextView) view
					.findViewById(R.id.text_pjt_pause);
			textViewPause.setOnClickListener(this);
			textViewPause.setOnLongClickListener(this);
			textViewPause
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewExit = (TextView) view
					.findViewById(R.id.text_pjt_exit);
			textViewExit.setOnClickListener(this);
			textViewExit.setOnLongClickListener(this);
			textViewExit
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewVolAdd = (TextView) view
					.findViewById(R.id.text_pjt_vol_add);
			textViewVolAdd.setOnClickListener(this);
			textViewVolAdd.setOnLongClickListener(this);
			textViewVolAdd
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewVolSub = (TextView) view
					.findViewById(R.id.text_pjt_vol_sub);
			textViewVolSub.setOnClickListener(this);
			textViewVolSub.setOnLongClickListener(this);
			textViewVolSub
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewMenu = (TextView) view
					.findViewById(R.id.text_pjt_menu);
			textViewMenu.setOnClickListener(this);
			textViewMenu.setOnLongClickListener(this);
			textViewMenu
					.setBackgroundResource(R.drawable.ic_button_round_selector);

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
			textViewDown
					.setBackgroundResource(R.drawable.ic_button_down_selector);

			TextView textViewLeft = (TextView) view
					.findViewById(R.id.text_pjt_left);
			textViewLeft.setOnClickListener(this);
			textViewLeft.setOnLongClickListener(this);
			textViewLeft
					.setBackgroundResource(R.drawable.ic_button_left_selector);

			TextView textViewRight = (TextView) view
					.findViewById(R.id.text_pjt_right);
			textViewRight.setOnClickListener(this);
			textViewRight.setOnLongClickListener(this);
			textViewRight
					.setBackgroundResource(R.drawable.ic_button_right_selector);
			mDevice.SetName(getResources().getStringArray(R.array.strs_device)[5]);
			mDevice.SetType(DeviceType.DEVICE_REMOTE_PJT);
			mDevice.SetRes(5);
			for (int i = 0; i < IRKeyValue.PJT_KEY_COUNT ; i++){
				ETKey key = new ETKey();
				key.SetState(ETKey.ETKEY_STATE_STUDY);
				key.SetKey(IRKeyValue.PJTValue | (i * 2 + 1));
				key.SetDID(mDevice.GetID());
				key.SetBrandIndex(0);
				key.SetBrandPos(0);
				key.SetName("");
				key.SetPos(0, 0);
				key.SetRes(0);
				key.SetRow(0);
				mDevice.SetKey(key);
			}
		} else if (mType == DeviceType.DEVICE_REMOTE_LIGHT) {
			view = inflater.inflate(R.layout.fragment_light, container, false);

			TextView textViewPowerAllOn = (TextView) view
					.findViewById(R.id.text_light_power_all_on);

			textViewPowerAllOn.setOnClickListener(this);
			textViewPowerAllOn.setOnLongClickListener(this);
			textViewPowerAllOn
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewPowerAllOff = (TextView) view
					.findViewById(R.id.text_light_power_all_off);

			textViewPowerAllOff.setOnClickListener(this);
			textViewPowerAllOff.setOnLongClickListener(this);
			textViewPowerAllOff
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewPowerON = (TextView) view
					.findViewById(R.id.text_light_power_on);
			textViewPowerON.setOnClickListener(this);
			textViewPowerON.setOnLongClickListener(this);
			textViewPowerON
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewPowerOFF = (TextView) view
					.findViewById(R.id.text_light_power_off);

			textViewPowerOFF.setOnClickListener(this);
			textViewPowerOFF.setOnLongClickListener(this);
			textViewPowerOFF
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textViewBrightnessAdd = (TextView) view
					.findViewById(R.id.text_light_brightness_add);

			textViewBrightnessAdd.setOnClickListener(this);
			textViewBrightnessAdd.setOnLongClickListener(this);
			textViewBrightnessAdd
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewBrightnessSub = (TextView) view
					.findViewById(R.id.text_light_brightness_sub);

			textViewBrightnessSub.setOnClickListener(this);
			textViewBrightnessSub.setOnLongClickListener(this);
			textViewBrightnessSub
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewStudy = (TextView) view
					.findViewById(R.id.text_light_study);

			textViewStudy.setOnClickListener(this);
			textViewStudy.setOnLongClickListener(this);
			textViewStudy
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewLost = (TextView) view
					.findViewById(R.id.text_light_lost);

			textViewLost.setOnClickListener(this);
			textViewLost.setOnLongClickListener(this);
			textViewLost
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewA = (TextView) view
					.findViewById(R.id.text_light_letter_a);

			textViewA.setOnClickListener(this);
			textViewA.setOnLongClickListener(this);
			textViewA.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewB = (TextView) view
					.findViewById(R.id.text_light_letter_b);

			textViewB.setOnClickListener(this);
			textViewB.setOnLongClickListener(this);
			textViewB.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewC = (TextView) view
					.findViewById(R.id.text_light_letter_c);

			textViewC.setOnClickListener(this);
			textViewC.setOnLongClickListener(this);
			textViewC.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewD = (TextView) view
					.findViewById(R.id.text_light_letter_d);

			textViewD.setOnClickListener(this);
			textViewD.setOnLongClickListener(this);
			textViewD.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewTimer1 = (TextView) view
					.findViewById(R.id.text_light_timer1);

			textViewTimer1.setOnClickListener(this);
			textViewTimer1.setOnLongClickListener(this);
			textViewTimer1
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textViewTimer2 = (TextView) view
					.findViewById(R.id.text_light_timer2);

			textViewTimer2.setOnClickListener(this);
			textViewTimer2.setOnLongClickListener(this);
			textViewTimer2
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewTimer3 = (TextView) view
					.findViewById(R.id.text_light_timer3);

			textViewTimer3.setOnClickListener(this);
			textViewTimer3.setOnLongClickListener(this);
			textViewTimer3
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewTimer4 = (TextView) view
					.findViewById(R.id.text_light_timer4);

			textViewTimer4.setOnClickListener(this);
			textViewTimer4.setOnLongClickListener(this);
			textViewTimer4
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewSet = (TextView) view
					.findViewById(R.id.text_light_set);

			textViewSet.setOnClickListener(this);
			textViewSet.setOnLongClickListener(this);
			textViewSet
					.setBackgroundResource(R.drawable.ic_button_round_selector);

			TextView textView123 = (TextView) view
					.findViewById(R.id.text_light_123);
			textView123.setOnClickListener(this);
			textView123
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			mDevice.SetName(getResources().getStringArray(R.array.strs_device)[6]);
			mDevice.SetType(DeviceType.DEVICE_REMOTE_LIGHT);
			mDevice.SetRes(6);
			for (int i = 0; i < IRKeyValue.LIGHT_KEY_COUNT ; i++){
				ETKey key = new ETKey();
				key.SetState(ETKey.ETKEY_STATE_STUDY);
				key.SetKey(IRKeyValue.LIGHTValue | (i * 2 + 1));
				key.SetDID(mDevice.GetID());
				key.SetBrandIndex(0);
				key.SetBrandPos(0);
				key.SetName("");
				key.SetPos(0, 0);
				key.SetRes(0);
				key.SetRow(0);
				mDevice.SetKey(key);
			}
		} else if (mType == DeviceType.DEVICE_REMOTE_AIR) {
			view = inflater.inflate(R.layout.fragment_air, container, false);

			TextView textViewPower = (TextView) view
					.findViewById(R.id.text_air_power);
			textViewPower.setOnClickListener(this);
			textViewPower.setOnLongClickListener(this);
			textViewPower.setBackgroundResource(R.drawable.ic_power_selector);

			

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
			mDevice.SetName(getResources().getStringArray(R.array.strs_device)[7]);
			mDevice.SetType(DeviceType.DEVICE_REMOTE_AIR);
			mDevice.SetRes(7);
			for (int i = 0; i < IRKeyValue.AIR_KEY_COUNT ; i++){
				ETKey key = new ETKey();
				key.SetState(ETKey.ETKEY_STATE_STUDY);
				key.SetKey(IRKeyValue.AIRValue | (i * 2 + 1));
				key.SetDID(mDevice.GetID());
				key.SetBrandIndex(0);
				key.SetBrandPos(0);
				key.SetName("");
				key.SetPos(0, 0);
				key.SetRes(0);
				key.SetRow(0);
				mDevice.SetKey(key);
			}
		} else if (mType == DeviceType.DEVICE_REMOTE_DC) {
			view = inflater.inflate(R.layout.fragment_dc, container, false);

			TextView textViewPhoto = (TextView) view
					.findViewById(R.id.text_dc_photo);
			textViewPhoto.setOnClickListener(this);
			textViewPhoto.setOnLongClickListener(this);
			textViewPhoto.setBackgroundResource(R.drawable.ic_button_round_selector);
			mDevice.SetName(getResources().getStringArray(R.array.strs_device)[8]);
			mDevice.SetType(DeviceType.DEVICE_REMOTE_DC);
			mDevice.SetRes(8);
			for (int i = 0; i < IRKeyValue.DC_KEY_COUNT ; i++){
				ETKey key = new ETKey();
				key.SetState(ETKey.ETKEY_STATE_STUDY);
				key.SetKey(IRKeyValue.DCValue | (i * 2 + 1));
				key.SetDID(mDevice.GetID());
				key.SetBrandIndex(0);
				key.SetBrandPos(0);
				key.SetName("");
				key.SetPos(0, 0);
				key.SetRes(0);
				key.SetRow(0);
				mDevice.SetKey(key);
			}
		}
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		filter.addAction(ETGlobal.BROADCAST_PASS_LEARN);
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
		inflater.inflate(R.menu.menu_study, menu);
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("Home", "Home");
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		
		int id=item.getItemId();
		if (id==android.R.id.home) {
			Back();
			return true;
		}else if (id==R.id.menu_save) {
			mDevice.Inster(ETDB.getInstance(getActivity()));
			args.putInt("group", mGroupIndex);
			FragmentDevice fragmentDevice = new FragmentDevice();
			fragmentDevice.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentDevice);
			transaction.addToBackStack(null);
			transaction.commit();
			return true;
		}

//		switch (item.getItemId()) {
//		case android.R.id.home:
//			Back();
//			return true;
//		case R.id.menu_save:
//			mDevice.Inster(ETDB.getInstance(getActivity()));
//			args.putInt("group", mGroupIndex);
//			FragmentDevice fragmentDevice = new FragmentDevice();
//			fragmentDevice.setArguments(args);
//			transaction.replace(R.id.fragment_container, fragmentDevice);
//			transaction.addToBackStack(null);
//			transaction.commit();
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("InflateParams")
	@Override
	public void onClick(View v) {
		int key = 0;
		mView = (TextView)v;
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
//		case R.id.text_iptv_0:
//			key = IRKeyValue.KEY_IPTV_KEY0;
//			break;
//		case R.id.text_iptv_1:
//			key = IRKeyValue.KEY_IPTV_KEY1;
//			break;
//		case R.id.text_iptv_2:
//			key = IRKeyValue.KEY_IPTV_KEY2;
//			break;
//		case R.id.text_iptv_3:
//			key = IRKeyValue.KEY_IPTV_KEY3;
//			break;
//		case R.id.text_iptv_4:
//			key = IRKeyValue.KEY_IPTV_KEY4;
//			break;
//		case R.id.text_iptv_5:
//			key = IRKeyValue.KEY_IPTV_KEY5;
//			break;
//		case R.id.text_iptv_6:
//			key = IRKeyValue.KEY_IPTV_KEY6;
//			break;
//		case R.id.text_iptv_7:
//			key = IRKeyValue.KEY_IPTV_KEY7;
//			break;
//		case R.id.text_iptv_8:
//			key = IRKeyValue.KEY_IPTV_KEY8;
//			break;
//		case R.id.text_iptv_9:
//			key = IRKeyValue.KEY_IPTV_KEY9;
//			break;
//		case R.id.text_iptv_up:
//			key = IRKeyValue.KEY_IPTV_UP;
//			break;
//		case R.id.text_iptv_down:
//			key = IRKeyValue.KEY_IPTV_DOWN;
//			break;
//		case R.id.text_iptv_left:
//			key = IRKeyValue.KEY_IPTV_LEFT;
//			break;
//		case R.id.text_iptv_right:
//			key = IRKeyValue.KEY_IPTV_RIGHT;
//			break;
//		case R.id.text_iptv_ch_add:
//			key = IRKeyValue.KEY_IPTV_CHANNEL_IN;
//			break;
//		case R.id.text_iptv_ch_sub:
//			key = IRKeyValue.KEY_IPTV_CHANNEL_OUT;
//			break;
//		case R.id.text_iptv_vol_add:
//			key = IRKeyValue.KEY_IPTV_VOLUME_IN;
//			break;
//		case R.id.text_iptv_vol_sub:
//			key = IRKeyValue.KEY_IPTV_VOLUME_OUT;
//			break;
//		case R.id.text_iptv_play_pause:
//			key = IRKeyValue.KEY_IPTV_PLAY_PAUSE;
//			break;
//		case R.id.text_iptv_mute:
//			key = IRKeyValue.KEY_IPTV_MUTE;
//			break;
//		case R.id.text_iptv_ok:
//			key = IRKeyValue.KEY_IPTV_OK;
//			break;
//		case R.id.text_iptv_back:
//			key = IRKeyValue.KEY_IPTV_BACK;
//			break;
//		case R.id.text_iptv_power:
//			key = IRKeyValue.KEY_IPTV_POWER;
//			break;
//		case R.id.text_iptv_home:
//			key = IRKeyExValue.KEYEX_IPTV_HOME;
//			break;
//		case R.id.text_stb_await:
//			key = IRKeyValue.KEY_STB_AWAIT;
//			break;
//		case R.id.text_stb_1:
//			key = IRKeyValue.KEY_STB_KEY1;
//			break;
//		case R.id.text_stb_2:
//			key = IRKeyValue.KEY_STB_KEY2;
//			break;
//		case R.id.text_stb_3:
//			key = IRKeyValue.KEY_STB_KEY3;
//			break;
//		case R.id.text_stb_guide:
//			key = IRKeyValue.KEY_STB_GUIDE;
//			break;
//		case R.id.text_stb_4:
//			key = IRKeyValue.KEY_STB_KEY4;
//			break;
//		case R.id.text_stb_5:
//			key = IRKeyValue.KEY_STB_KEY5;
//			break;
//		case R.id.text_stb_6:
//			key = IRKeyValue.KEY_STB_KEY6;
//			break;
//		case R.id.text_stb_menu:
//			key = IRKeyValue.KEY_STB_MENU;
//			break;
//		case R.id.text_stb_7:
//			key = IRKeyValue.KEY_STB_KEY7;
//			break;
//		case R.id.text_stb_8:
//			key = IRKeyValue.KEY_STB_KEY8;
//			break;
//		case R.id.text_stb_9:
//			key = IRKeyValue.KEY_STB_KEY9;
//			break;
//		case R.id.text_stb_ok:
//			key = IRKeyValue.KEY_STB_OK;
//			break;
//		case R.id.text_stb_0:
//			key = IRKeyValue.KEY_STB_KEY0;
//
//			break;
//		case R.id.text_stb_up:
//			key = IRKeyValue.KEY_STB_UP;
//
//			break;
//		case R.id.text_stb_down:
//			key = IRKeyValue.KEY_STB_DOWN;
//
//			break;
//		case R.id.text_stb_left:
//			key = IRKeyValue.KEY_STB_LEFT;
//
//			break;
//		case R.id.text_stb_right:
//			key = IRKeyValue.KEY_STB_RIGHT;
//
//			break;
//		case R.id.text_stb_back:
//			key = IRKeyValue.KEY_STB_BACK;
//
//			break;
//		case R.id.text_stb_vol_add:
//			key = IRKeyValue.KEY_STB_VOLUME_IN;
//
//			break;
//		case R.id.text_stb_ch_add:
//			key = IRKeyValue.KEY_STB_CHANNEL_IN;
//
//			break;
//		case R.id.text_stb_vol_sub:
//			key = IRKeyValue.KEY_STB_VOLUME_OUT;
//			break;
//		case R.id.text_stb_ch_sub:
//			key = IRKeyValue.KEY_STB_CHANNEL_OUT;
//			break;
//		case R.id.text_dvd_power:
//			key = IRKeyValue.KEY_DVD_POWER;
//			break;
//		case R.id.text_dvd_oc:
//			key = IRKeyValue.KEY_DVD_OC;
//			break;
//		case R.id.text_dvd_pause:
//			key = IRKeyValue.KEY_DVD_PAUSE;
//			break;
//		case R.id.text_dvd_play:
//			key = IRKeyValue.KEY_DVD_PLAY;
//			break;
//		case R.id.text_dvd_stop:
//			key = IRKeyValue.KEY_DVD_STOP;
//			break;
//		case R.id.text_dvd_mute:
//			key = IRKeyValue.KEY_DVD_MUTE;
//			break;
//		case R.id.text_dvd_up_song:
//			key = IRKeyValue.KEY_DVD_UP_SONG;
//			break;
//		case R.id.text_dvd_down_song:
//			key = IRKeyValue.KEY_DVD_NEXT_SONG;
//			break;
//		case R.id.text_dvd_menu:
//			key = IRKeyValue.KEY_DVD_MENU;
//			break;
//		case R.id.text_dvd_fast_forward:
//			key = IRKeyValue.KEY_DVD_FAST_FORWARD;
//			break;
//		case R.id.text_dvd_fast_back:
//			key = IRKeyValue.KEY_DVD_FAST_BACK;
//			break;
//		case R.id.text_dvd_title:
//			key = IRKeyValue.KEY_DVD_TITLE;
//			break;
//		case R.id.text_dvd_ok:
//			key = IRKeyValue.KEY_DVD_OK;
//			break;
//		case R.id.text_dvd_standard:
//			key = IRKeyValue.KEY_DVD_STANDARD;
//			break;
//		case R.id.text_dvd_back:
//			key = IRKeyValue.KEY_DVD_BACK;
//			break;
//		case R.id.text_dvd_up:
//			key = IRKeyValue.KEY_DVD_UP;
//			break;
//		case R.id.text_dvd_down:
//			key = IRKeyValue.KEY_DVD_DOWN;
//			break;
//		case R.id.text_dvd_left:
//			key = IRKeyValue.KEY_DVD_LEFT;
//			break;
//		case R.id.text_dvd_right:
//			key = IRKeyValue.KEY_DVD_RIGHT;
//			break;
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
//		case R.id.text_dc_photo:
//			key = IRKeyValue.KEY_DC_PHOTO;
//			break;
//		}
		if (v.getId() == R.id.text_tv_123) {
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View view123 = mInflater.inflate(R.layout.fragment_tv_123, null);
			TextView textView1 = (TextView) view123
					.findViewById(R.id.text_tv_1);
			textView1.setOnClickListener(this);
			textView1.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView2 = (TextView) view123
					.findViewById(R.id.text_tv_2);
			textView2.setOnClickListener(this);
			textView2.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView3 = (TextView) view123
					.findViewById(R.id.text_tv_3);
			textView3.setOnClickListener(this);
			textView3.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView4 = (TextView) view123
					.findViewById(R.id.text_tv_4);
			textView4.setOnClickListener(this);
			textView4.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView5 = (TextView) view123
					.findViewById(R.id.text_tv_5);
			textView5.setOnClickListener(this);
			textView5.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView6 = (TextView) view123
					.findViewById(R.id.text_tv_6);
			textView6.setOnClickListener(this);
			textView6.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView7 = (TextView) view123
					.findViewById(R.id.text_tv_7);
			textView7.setOnClickListener(this);
			textView7.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView8 = (TextView) view123
					.findViewById(R.id.text_tv_8);
			textView8.setOnClickListener(this);
			textView8.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView9 = (TextView) view123
					.findViewById(R.id.text_tv_9);
			textView9.setOnClickListener(this);
			textView9.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewSelect = (TextView) view123
					.findViewById(R.id.text_tv_select);
			textViewSelect.setOnClickListener(this);
			textViewSelect
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView0 = (TextView) view123
					.findViewById(R.id.text_tv_0);
			textView0.setOnClickListener(this);
			textView0.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewAV = (TextView) view123
					.findViewById(R.id.text_tv_av);
			textViewAV.setOnClickListener(this);
			textViewAV
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
					.setView(view123).create();
			dialog.show();
		} else if (v.getId() == R.id.text_iptv_123) {
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View view123 = mInflater.inflate(R.layout.fragment_iptv_123, null);
			TextView textView1 = (TextView) view123
					.findViewById(R.id.text_iptv_1);
			textView1.setOnClickListener(this);
			textView1.setOnLongClickListener(this);
			textView1.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView2 = (TextView) view123
					.findViewById(R.id.text_iptv_2);
			textView2.setOnClickListener(this);
			textView2.setOnLongClickListener(this);
			textView2.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView3 = (TextView) view123
					.findViewById(R.id.text_iptv_3);
			textView3.setOnClickListener(this);
			textView3.setOnLongClickListener(this);
			textView3.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView4 = (TextView) view123
					.findViewById(R.id.text_iptv_4);
			textView4.setOnClickListener(this);
			textView4.setOnLongClickListener(this);
			textView4.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView5 = (TextView) view123
					.findViewById(R.id.text_iptv_5);
			textView5.setOnClickListener(this);
			textView5.setBackgroundResource(R.drawable.ic_button_cast_selector);
			textView5.setOnLongClickListener(this);
			TextView textView6 = (TextView) view123
					.findViewById(R.id.text_iptv_6);
			textView6.setOnClickListener(this);
			textView6.setBackgroundResource(R.drawable.ic_button_cast_selector);
			textView6.setOnLongClickListener(this);
			TextView textView7 = (TextView) view123
					.findViewById(R.id.text_iptv_7);
			textView7.setOnClickListener(this);
			textView7.setOnLongClickListener(this);
			textView7.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView8 = (TextView) view123
					.findViewById(R.id.text_iptv_8);
			textView8.setOnClickListener(this);
			textView8.setOnLongClickListener(this);
			textView8.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView9 = (TextView) view123
					.findViewById(R.id.text_iptv_9);
			textView9.setOnClickListener(this);
			textView9.setOnLongClickListener(this);
			textView9.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textView0 = (TextView) view123
					.findViewById(R.id.text_iptv_0);
			textView0.setOnClickListener(this);
			textView0.setOnLongClickListener(this);
			textView0.setBackgroundResource(R.drawable.ic_button_cast_selector);

			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
					.setView(view123).create();
			dialog.show();
		} else if (v.getId() == R.id.text_stb_123) {
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View view123 = mInflater.inflate(R.layout.fragment_stb_123, null);
			TextView textView1 = (TextView) view123
					.findViewById(R.id.text_stb_1);
			textView1.setOnClickListener(this);
			textView1.setOnLongClickListener(this);
			textView1.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView2 = (TextView) view123
					.findViewById(R.id.text_stb_2);
			textView2.setOnClickListener(this);
			textView2.setOnLongClickListener(this);
			textView2.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView3 = (TextView) view123
					.findViewById(R.id.text_stb_3);
			textView3.setOnClickListener(this);
			textView3.setOnLongClickListener(this);
			textView3.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView4 = (TextView) view123
					.findViewById(R.id.text_stb_4);
			textView4.setOnClickListener(this);
			textView4.setOnLongClickListener(this);
			textView4.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView5 = (TextView) view123
					.findViewById(R.id.text_stb_5);
			textView5.setOnClickListener(this);
			textView5.setBackgroundResource(R.drawable.ic_button_cast_selector);
			textView5.setOnLongClickListener(this);
			TextView textView6 = (TextView) view123
					.findViewById(R.id.text_stb_6);
			textView6.setOnClickListener(this);
			textView6.setBackgroundResource(R.drawable.ic_button_cast_selector);
			textView6.setOnLongClickListener(this);
			TextView textView7 = (TextView) view123
					.findViewById(R.id.text_stb_7);
			textView7.setOnClickListener(this);
			textView7.setOnLongClickListener(this);
			textView7.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView8 = (TextView) view123
					.findViewById(R.id.text_stb_8);
			textView8.setOnClickListener(this);
			textView8.setOnLongClickListener(this);
			textView8.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView9 = (TextView) view123
					.findViewById(R.id.text_stb_9);
			textView9.setOnClickListener(this);
			textView9.setOnLongClickListener(this);
			textView9.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textView0 = (TextView) view123
					.findViewById(R.id.text_stb_0);
			textView0.setOnClickListener(this);
			textView0.setOnLongClickListener(this);
			textView0.setBackgroundResource(R.drawable.ic_button_cast_selector);

			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
					.setView(view123).create();
			dialog.show();
		} else if (v.getId() == R.id.text_fans_123) {
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View view123 = mInflater.inflate(R.layout.fragment_fans_123, null);

			TextView textView1 = (TextView) view123
					.findViewById(R.id.text_fans_1);
			textView1.setOnClickListener(this);
			textView1.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView2 = (TextView) view123
					.findViewById(R.id.text_fans_2);
			textView2.setOnClickListener(this);
			textView2.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView3 = (TextView) view123
					.findViewById(R.id.text_fans_3);
			textView3.setOnClickListener(this);
			textView3.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView4 = (TextView) view123
					.findViewById(R.id.text_fans_4);
			textView4.setOnClickListener(this);
			textView4.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView5 = (TextView) view123
					.findViewById(R.id.text_fans_5);
			textView5.setOnClickListener(this);
			textView5.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView6 = (TextView) view123
					.findViewById(R.id.text_fans_6);
			textView6.setOnClickListener(this);
			textView6.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView7 = (TextView) view123
					.findViewById(R.id.text_fans_7);
			textView7.setOnClickListener(this);
			textView7.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView8 = (TextView) view123
					.findViewById(R.id.text_fans_8);
			textView8.setOnClickListener(this);
			textView8.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView9 = (TextView) view123
					.findViewById(R.id.text_fans_9);
			textView9.setOnClickListener(this);
			textView9.setBackgroundResource(R.drawable.ic_button_cast_selector);

			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
					.setView(view123).create();
			dialog.show();
		} else if (v.getId() == R.id.text_light_123) {
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View view123 = mInflater.inflate(R.layout.fragment_light_123, null);

			TextView textView1 = (TextView) view123
					.findViewById(R.id.text_light_1);
			textView1.setOnClickListener(this);
			textView1.setOnLongClickListener(this);
			textView1.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView2 = (TextView) view123
					.findViewById(R.id.text_light_2);
			textView2.setOnClickListener(this);
			textView2.setOnLongClickListener(this);
			textView2.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView3 = (TextView) view123
					.findViewById(R.id.text_light_3);
			textView3.setOnClickListener(this);
			textView3.setOnLongClickListener(this);
			textView3.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView4 = (TextView) view123
					.findViewById(R.id.text_light_4);
			textView4.setOnClickListener(this);
			textView4.setOnLongClickListener(this);
			textView4.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textView5 = (TextView) view123
					.findViewById(R.id.text_light_5);
			textView5.setOnClickListener(this);
			textView5.setBackgroundResource(R.drawable.ic_button_cast_selector);
			textView5.setOnLongClickListener(this);
			TextView textView6 = (TextView) view123
					.findViewById(R.id.text_light_6);
			textView6.setOnClickListener(this);
			textView6.setBackgroundResource(R.drawable.ic_button_cast_selector);
			textView6.setOnLongClickListener(this);

			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
					.setView(view123).create();
			dialog.show();
		}
		if (key != 0) {
			Intent intentStartLearn = new Intent(
					ETGlobal.BROADCAST_START_LEARN);
			intentStartLearn.putExtra("select", "0");
			intentStartLearn.putExtra("key", key);
			getActivity().sendBroadcast(intentStartLearn);
		}
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
							k.SetValue(ETTool.HexStringToBytes(msg));
							mView.setTextColor(Color.BLUE);
						}
						else{
							ETKeyEx keyEx =  mDevice.GetKeyByValueEx(key);
							keyEx.SetValue(ETTool.HexStringToBytes(msg));
						}
//						ETKey k = new ETKey();
//						k.SetDID(mDevice.GetID());
//						k.SetBrandIndex(0);
//						k.SetBrandPos(0);
//						k.SetKey(key);
//						k.SetName("");
//						k.SetPos(0, 0);
//						k.SetRes(0);
//						k.SetRow(0);
//						k.SetState(ETKey.ETKEY_STATE_STUDY);
//						k.SetValue(ETTool.HexStringToBytes(msg));
//						mDevice.SetKey(k);
					} else if (select.equals("1")) {

					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
				Back();
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentWizardsTwo fragment = new FragmentWizardsTwo();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("type", mType);
		args.putInt("group", mGroupIndex);
		fragment.setArguments(args);
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragment);
		// transactionBt.addToBackStack(null);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	};
}
