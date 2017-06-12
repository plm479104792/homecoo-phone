package com.cj.Receiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.homecoolink.data.DataManager;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.p2p.core.P2PValue;
import com.p2p.core.global.AlarmRecord;

public class AlarmRecordReceiver extends BroadcastReceiver {

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Context arg0, Intent intent) {
		if (intent.getAction().equals(Constants.P2P.ACK_RET_GET_ALAEM_RECORD)) {
			int result = intent.getIntExtra("state", -1);
			if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
				MyApp.ReGetRemote();
			} else {
				Thread ac = new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(4000);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						if (!MyApp.GACID.equals("")) {
							MyApp.GACID = "";
							MyApp.GACPWD = "";
						}
					}

				};
				ac.start();
			}
		} else if (intent.getAction()
				.equals(Constants.P2P.RET_GET_ALARM_RECORD)) {
			if (!MyApp.GACID.equals("")) {
				MyApp.tempremotelist = (ArrayList<AlarmRecord>) intent
						.getSerializableExtra("list");
				new GetRemoteDataTask().execute();
			}
		} else if (intent.getAction().equals(
				Constants.P2P.RET_DEVICE_NOT_SUPPORT)) {
			if (!MyApp.GACID.equals("")) {
				MyApp.GACID = "";
				MyApp.GACPWD = "";
			}
		}
	}

	private java.util.Date Str2Date(String datestr, String format) {
		if (format == null || format.equals("")) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		java.util.Date date;
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

		if (datestr.indexOf(":") < 0) {
			datestr += " 00:00";
		}
		try {
			date = sdf.parse(datestr);
		} catch (ParseException e) {

			date = new java.util.Date();
		}

		return date;
	}

	private com.homecoolink.data.AlarmRecord insertAlarmRecord(String alarm_id,
			int alarm_type, int group, int item, String alarmtime) {
		String atime = String.valueOf(Str2Date(alarmtime, "").getTime());
		if ((alarm_type == P2PValue.AlarmType.EXTERNAL_ALARM || alarm_type == P2PValue.AlarmType.LOW_VOL_ALARM)) {
			group = -1;
			item = -1;
		}
		if (!DataManager.Exists(MyApp.app, NpcCommon.mThreeNum, alarm_id,
				alarm_type, alarmtime, group, item)) {
			com.homecoolink.data.AlarmRecord alarmRecord = new com.homecoolink.data.AlarmRecord();
			alarmRecord.alarmTime = atime;
			alarmRecord.deviceId = String.valueOf(alarm_id);
			alarmRecord.alarmType = alarm_type;
			alarmRecord.activeUser = NpcCommon.mThreeNum;
			alarmRecord.group = group;
			alarmRecord.item = item;
			alarmRecord.alarmStatus = 0;
			return alarmRecord;
		}
		return null;

	}

	private void doRemoteList() {
		try {
			if (MyApp.tempremotelist != null && MyApp.tempremotelist.size() > 0
					&& MyApp.tempremotelist.get(0) != null) {
				ArrayList<com.homecoolink.data.AlarmRecord> listme = new ArrayList<com.homecoolink.data.AlarmRecord>();

				for (AlarmRecord alarmRecord : MyApp.tempremotelist) {
					String area_channel = alarmRecord.alarmChannel;
					int channel = Integer.parseInt(
							area_channel.substring(0, 4), 2);
					int area = Integer
							.parseInt(area_channel.substring(4, 8), 2);

					com.homecoolink.data.AlarmRecord me = insertAlarmRecord(
							MyApp.GACID, alarmRecord.alarmType, area, channel,
							alarmRecord.alarmTime);
					if (me != null) {
						Boolean isexist = false;
						for (com.homecoolink.data.AlarmRecord alarmRecord2 : listme) {

							if (alarmRecord2.alarmType == me.alarmType
									&& alarmRecord2.group == me.group
									&& alarmRecord2.item == me.item
									&& alarmRecord2.deviceId == me.deviceId
									&& (Long.parseLong(alarmRecord2.alarmTime)
											- Long.parseLong(me.alarmTime) > -10000 && Long
											.parseLong(alarmRecord2.alarmTime)
											- Long.parseLong(me.alarmTime) < 10000)) {
								isexist = true;
								break;

							}
						}
						if (!isexist) {
							listme.add(me);
						}
					}
				}
				DataManager.insertAlarmRecords(MyApp.app, listme);
			}
		} catch (Exception e) {

		}

	}

	public class GetRemoteDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {
			doRemoteList();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if (!MyApp.GACID.equals("")) {
				MyApp.GACID = "";
				MyApp.GACPWD = "";
			}
		}
	}

}
