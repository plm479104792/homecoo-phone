package com.homecoolink.activity;

import java.io.File;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.homecoolink.CallActivity;
import com.homecoolink.R;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.global.Constants.AlarmType_Pxy;
import com.homecoolink.utils.ImageUtils;
import com.homecoolink.utils.Utils;
import com.p2p.core.P2PValue;


public class AlarmReordDetailActivity extends Activity implements
		OnClickListener {
	Contact contact;
	Context mContext;
	Button monitor_btn, nowvideo_btn;
	TextView alarm_sj_text, alarm_sb_text, alarm_sbid_text, alarm_content_text,
			alarmTime_text;// ,alarm_id_text
	ImageView alarm_img;
	int alarm_type, group, item;
	String alarm_sbid, alarm_time;
	String alarm_sb, alarm_content;
	ImageView back;
	TextView alarmrecorddetail_sbdefencename;
	LinearLayout isSupport;
	private boolean isRegFilter = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;

//		List<DefenceAreaName> list = DataManager.findDefenceAreaNameAll(this);
//		Log.e("343", group + "==" + item);
//		Log.e("343", "看看数据库里面的大小=" + list.size());
//		for (int i = 0; i < list.size(); i++) {
////			Log.e("343", i + "=groupIJ=="
////					+ list.get(i).groupIJ);
//			list.get(i).groupIJ;
//		}
		
		alarm_type = getIntent().getIntExtra("alarm_type", 0);
		group = getIntent().getIntExtra("group", 0);
		item = getIntent().getIntExtra("item", 0);
		
		alarm_sbid = getIntent().getStringExtra("alarm_sbid");
		alarm_time = getIntent().getStringExtra("alarm_time");
		setContentView(R.layout.activity_alarm_recorddetail);		
		if (group==0) {
			initComponent(false);
		}else {
			initComponent(true);
		}
		

	}	

	public void updateImage(String dvid, ImageView imgv) {
		Bitmap tempBitmap;
		try {

			tempBitmap = ImageUtils.getBitmap(new File(
					"/sdcard/screenshot/tempHead/" + NpcCommon.mThreeNum + "/"
							+ dvid + ".jpg"), 200, 200);

			// tempBitmap = ImageUtils.roundCorners(tempBitmap,
			// ImageUtils.getScaleRounded(tempBitmap.getWidth()));
			imgv.setImageBitmap(tempBitmap);
		} catch (Exception e) {

			tempBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.contact_list_defaultpic);
			// tempBitmap = ImageUtils.roundCorners(tempBitmap,
			// ImageUtils.getScaleRounded(tempBitmap.getWidth()));
			imgv.setImageBitmap(tempBitmap);

		}
	}

	public void initComponent(boolean bl) {
		back = (ImageView) findViewById(R.id.back_btn);

		back.setOnClickListener(this);
		monitor_btn = (Button) findViewById(R.id.alarmrecorddetail_btnvideo);
		nowvideo_btn = (Button) findViewById(R.id.btn_nowvideo);
		alarm_sj_text = (TextView) findViewById(R.id.alarmrecorddetail_sj);

		alarmTime_text = (TextView) findViewById(R.id.alarmrecorddetail_time);
		alarm_sb_text = (TextView) findViewById(R.id.alarmrecorddetail_sb);
		alarm_sbid_text = (TextView) findViewById(R.id.alarmrecorddetail_sbid);
		alarm_content_text = (TextView) findViewById(R.id.alarmrecorddetail_content);

		alarm_img = (ImageView) findViewById(R.id.alarmrecorddetail_defaultpic);
		alarmrecorddetail_sbdefencename = (TextView) findViewById(R.id.alarmrecorddetail_sbdefencename);
		isSupport = (LinearLayout) findViewById(R.id.isSupportDefence);
		updateImage(alarm_sbid, alarm_img);
		alarmTime_text.setText(alarm_time);
		alarm_sbid_text.setText(alarm_sbid);
		
		if (bl) {
			isSupport.setVisibility(View.VISIBLE);			
			alarmrecorddetail_sbdefencename.setText(Utils.getDefenceAreaByGroup(mContext, group));			
		}else {
			isSupport.setVisibility(View.GONE);
		}
		
		String strName = DataManager.findContactByActiveUserAndContactId(this,
				NpcCommon.mThreeNum, alarm_sbid).contactName;
		if (strName.length() > 10) {
			alarm_sb_text.setText(strName.subSequence(0, 10) + "...");
		} else {
			alarm_sb_text.setText(strName);
		}

		switch (alarm_type) {
		case P2PValue.AlarmType.EXTERNAL_ALARM:
			alarm_sj_text.setText(R.string.allarm_type1);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.GONE);
			monitor_btn.setVisibility(View.VISIBLE);
			monitor_btn.setOnClickListener(this);
			break;
		case P2PValue.AlarmType.MOTION_DECT_ALARM:
			alarm_sj_text.setText(R.string.allarm_type2);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.GONE);
			monitor_btn.setVisibility(View.VISIBLE);
			monitor_btn.setOnClickListener(this);
			break;
		case P2PValue.AlarmType.EMERGENCY_ALARM:
			alarm_sj_text.setText(R.string.allarm_type3);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.GONE);
			monitor_btn.setVisibility(View.VISIBLE);
			monitor_btn.setOnClickListener(this);
			break;

		case P2PValue.AlarmType.LOW_VOL_ALARM:
			alarm_sj_text.setText(R.string.low_voltage_alarm);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.GONE);
			monitor_btn.setVisibility(View.VISIBLE);
			monitor_btn.setOnClickListener(this);
			break;
		case P2PValue.AlarmType.PIR_ALARM:
			alarm_sj_text.setText(R.string.allarm_type7);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.GONE);
			monitor_btn.setVisibility(View.VISIBLE);
			monitor_btn.setOnClickListener(this);
			break;

		case P2PValue.AlarmType.EXT_LINE_ALARM:
			alarm_sj_text.setText(R.string.allarm_type5);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.GONE);
			monitor_btn.setVisibility(View.VISIBLE);
			monitor_btn.setOnClickListener(this);
			break;
		case P2PValue.AlarmType.DEFENCE:
			alarm_sj_text.setText(R.string.defence2);
			alarm_content_text.setText(R.string.sharecf_alarm_tip1);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case P2PValue.AlarmType.NO_DEFENCE:
			alarm_sj_text.setText(R.string.no_defence);
			alarm_content_text.setText(R.string.sharecf_alarm_tip2);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		
		case P2PValue.AlarmType.BATTERY_LOW_ALARM:
			alarm_sj_text.setText(R.string.allarm_type10);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH:
			alarm_sj_text.setText(R.string.allarm_type13);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case P2PValue.AlarmType.RECORD_FAILED_ALARM:
			alarm_sj_text.setText(R.string.allarm_type15);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		
		case AlarmType_Pxy.DEBUG_ALARM:
			alarm_sj_text.setText(R.string.allarm_type4);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.PARAM_ID_UPDATE_TO_SER:
			alarm_sj_text.setText(R.string.allarm_type11);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.TH_ALARM:
			alarm_sj_text.setText(R.string.allarm_type12);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.FORCE_FROM_KEYPRESS_ALARM:
			alarm_sj_text.setText(R.string.allarm_type14);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.EMAIL_TOO_OFTEN_ALARM:
			alarm_sj_text.setText(R.string.allarm_type16);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.UART_INPUT_ALARM:
			alarm_sj_text.setText(R.string.allarm_type17);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.FIRE_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type18);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.GAS_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type19);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.STEAL_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type20);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.GONE);
			monitor_btn.setVisibility(View.VISIBLE);
			monitor_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.AROUND_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type21);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.FORCE_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type22);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.I20_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type23);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);			
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.PREVENTDISCONNECT_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type24);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.GONE);
			monitor_btn.setVisibility(View.VISIBLE);
			monitor_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.COMMUNICATION_TIMING_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type25);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.LOW_POWER_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type26);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.LOW_POWER_RECOVERY_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type27);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.POWERONPROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type28);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.POWEROFF_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type29);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.DEF_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type30);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.DEFDIS_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type31);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;
		case AlarmType_Pxy.EXT_PROBER_ALARM:
			alarm_sj_text.setText(R.string.allarm_type32);
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.GONE);
			monitor_btn.setVisibility(View.VISIBLE);
			monitor_btn.setOnClickListener(this);
			break;
		default:
			alarm_sj_text.setText(mContext.getResources()
					.getString(R.string.allarm_nofound).replace(":", ""));
			alarm_content_text.setText(R.string.alarmrecord_default_content);
			nowvideo_btn.setVisibility(View.VISIBLE);
			monitor_btn.setVisibility(View.GONE);
			nowvideo_btn.setOnClickListener(this);
			break;

		}
	}

	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			// finish();
			
			// String[] data = (String[]) msg.obj;
			contact = FList.getInstance().isContact("" + msg.obj);
			if (contact == null) {
				return false;
			} else {
				Intent monitor = new Intent();
				monitor.setClass(mContext, CallActivity.class);
				monitor.putExtra("callId", contact.contactId);
				monitor.putExtra("password", contact.contactPassword);
				monitor.putExtra("isOutCall", true);
				monitor.putExtra("type", Constants.P2P_TYPE.P2P_TYPE_MONITOR);
				startActivity(monitor);
				return false;
			}
		}
	});

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.back_btn){
			finish();
		}else if(id==R.id.alarmrecorddetail_btnvideo){
			Intent video = new Intent(this, AlarmReordVideo.class);
			video.putExtra("date", alarm_time);
			video.putExtra("dvid", alarm_sbid);
			startActivity(video);
		}else if(id==R.id.btn_nowvideo){
			Message msg = handler.obtainMessage(1, alarm_sbid);
			msg.sendToTarget();
		}
		

//		switch (v.getId()) {
//		case R.id.back_btn:
//			finish();
//			break;
//		case R.id.alarmrecorddetail_btnvideo:
//			Intent video = new Intent(this, AlarmReordVideo.class);
//			video.putExtra("date", alarm_time);
//			video.putExtra("dvid", alarm_sbid);
//			startActivity(video);
//			break;
//		case R.id.btn_nowvideo:
//			Message msg = handler.obtainMessage(1, alarm_sbid);
//			msg.sendToTarget();
//			break;
//		}
	}

	@Override
	protected void onStop() {

		super.onStop();

	}

}
