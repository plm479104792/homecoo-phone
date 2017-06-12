package com.homecoolink.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.homecoolink.R;
import com.homecoolink.activity.AlarmReordDetailActivity;
import com.homecoolink.data.DataManager;
import com.homecoolink.entity.AlarmRecord;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.global.Constants.AlarmType_Pxy;
import com.homecoolink.utils.Utils;
import com.p2p.core.P2PValue;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmRecordAdapter3 extends BaseAdapter {

	private Context mcontext;
	private LayoutInflater mlif;
	private ArrayList<AlarmRecord> mal;
	private static final int ITEM_TITLE = 0;
	private static final int ITEM_ALARM = 1;
	public boolean isdevice = false;
	private HashMap<String, String> contactnames = new HashMap<String, String>();
	private boolean isFromDevice = false;
	private com.homecoolink.activity.AlarmRecordFromDeviceActivity fa;
	private com.homecoolink.fragment.AlarmRecordFrag ff;

	// private ArrayList<com.homecoolink.entity.AlarmRecord> getData() {
	//
	// ArrayList<com.homecoolink.entity.AlarmRecord> lastal = new
	// ArrayList<com.homecoolink.entity.AlarmRecord>();
	// List<com.homecoolink.data.AlarmRecord> firstal = DataManager
	// .findAlarmRecordByGroup(mcontext, NpcCommon.mThreeNum, null);
	// for (int i = 0; i < firstal.size(); i++) {
	// com.homecoolink.data.AlarmRecord ar = firstal.get(i);
	// if ("".equals(ar.deviceId)) {
	//
	// lastal.add(new com.homecoolink.entity.AlarmRecord(ar.id,
	// ar.deviceId, ar.alarmType, ar.alarmTime, ar.activeUser,
	// ar.alarmStatus, 0));
	// } else {
	// lastal.add(new com.homecoolink.entity.AlarmRecord(ar.id,
	// ar.deviceId, ar.alarmType, ar.alarmTime, ar.activeUser,
	// ar.alarmStatus, 1));
	// }
	//
	// }
	// return lastal;
	// }

	private String GetDVName(String dvid) {
		String k = dvid + "k";
		if (!contactnames.containsKey(k)) {

			String dName = DataManager.findContactByActiveUserAndContactId(
					mcontext, NpcCommon.mThreeNum, dvid).contactName;
			if (dName.length() > 10) {
				contactnames.put(k, dName.substring(0, 8) + "...");
			} else {
				contactnames.put(k, dName);
			}
		}
		return contactnames.get(k).toString();
	}

	public AlarmRecordAdapter3(Context context, ArrayList<AlarmRecord> al,
			com.homecoolink.activity.AlarmRecordFromDeviceActivity fa) {
		this.mcontext = context;
		this.mlif = LayoutInflater.from(mcontext);
		this.mal = al;
		this.fa = fa;
		this.isFromDevice = true;
		context.getResources();
	}

	public AlarmRecordAdapter3(Context context, ArrayList<AlarmRecord> al,
			com.homecoolink.fragment.AlarmRecordFrag ff) {
		this.mcontext = context;
		this.mlif = LayoutInflater.from(mcontext);
		this.mal = al;
		this.ff = ff;
		this.isFromDevice = false;
		context.getResources();
	}

	public void ChangeData(ArrayList<AlarmRecord> al) {
		mal = al;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mal.size();
	}

	@Override
	public Object getItem(int position) {
		return mal.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		return mal.get(position).layoutType;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e("343", "====");
		final AlarmRecord ar = mal.get(position);
		View view = convertView;
		TitleViewHolder titleholder = null;
		AlarmViewHolder alarmholder = null;
		int type = ar.layoutType;
		if (null == view) {
			switch (type) {
			case ITEM_TITLE:
				titleholder = new TitleViewHolder();
				view = mlif.inflate(R.layout.divgrouptitle, null);
				titleholder.setTitleTv((TextView) view
						.findViewById(R.id.time_group_title));
				view.setTag(titleholder);
				break;
			case ITEM_ALARM:
				alarmholder = new AlarmViewHolder();
				view = mlif.inflate(R.layout.fragment_alarm_record_item, null);
				alarmholder.setAlarmItem_Iv1((ImageView) view
						.findViewById(R.id.alarmtpyeimg));
				alarmholder.setAlarmItem_Type((TextView) view
						.findViewById(R.id.alarmtype));
				alarmholder.setAlarmItem_Time1((TextView) view
						.findViewById(R.id.alarmtime1));
				alarmholder.setAlarmItem_Time2((TextView) view
						.findViewById(R.id.alarmtime2));
				alarmholder.setAlarmItem_Dis((TextView) view
						.findViewById(R.id.alarmdis));
				alarmholder.setAlarmItem_RedSpot((ImageView) view
						.findViewById(R.id.redspot));
				alarmholder.setAlarmItem_TypeContant((TextView) view
						.findViewById(R.id.alarmtypecontant));
				view.setTag(alarmholder);
				break;
			default:
				break;
			}
		} else {
			switch (type) {
			case ITEM_TITLE:
				titleholder = (TitleViewHolder) view.getTag();

				break;
			case ITEM_ALARM:
				alarmholder = (AlarmViewHolder) view.getTag();

				break;
			default:
				break;
			}

		}
		switch (type) {
		case ITEM_TITLE:
			if (ar.alarmTime.equals("今天")) {
				titleholder.getTitleTv().setText(
						Utils.getResString(mcontext, R.string.time_static3));
			} else if (ar.alarmTime.equals("昨天")) {
				titleholder.getTitleTv().setText(
						Utils.getResString(mcontext, R.string.time_static4));
			} else {
				titleholder.getTitleTv().setText(ar.alarmTime);
			}

			break;
		case ITEM_ALARM:
			alarmholder = (AlarmViewHolder) view.getTag();
			String dname = isdevice ? "" : GetDVName(ar.deviceId);

			String startString = isdevice ? Utils.getResString(mcontext,
					R.string.with_blank) : "("
					+ Utils.getResString(mcontext, R.string.with_blank);
			String endString = isdevice ? "" : ")";
			if (dname == null || dname.equals("")) {
				alarmholder.getAlarmItem_Type().setVisibility(View.GONE);
				alarmholder.getAlarmItem_TypeContant().setGravity(
						Gravity.BOTTOM);
			} else {
				alarmholder.getAlarmItem_Type().setVisibility(View.VISIBLE);
			}
			switch (ar.alarmType) {
			case P2PValue.AlarmType.EXTERNAL_ALARM: 
				alarmholder.getAlarmItem_Type().setText(dname);
				alarmholder.getAlarmItem_TypeContant().setText(
						startString
								+ mcontext.getResources().getString(
										R.string.allarm_type1) + endString);
				alarmholder.getAlarmItem_Iv1().setImageResource(
						R.drawable.sharecf_alarm_invade);
				alarmholder.getAlarmItem_Dis().setText(
						R.string.alarmrecord_default_content);
				break;
			case P2PValue.AlarmType.MOTION_DECT_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type2),
						endString);
				break;
			case P2PValue.AlarmType.EMERGENCY_ALARM:
				alarmholder.getAlarmItem_Type().setText(dname);
				alarmholder.getAlarmItem_TypeContant().setText(
						startString
								+ mcontext.getResources().getString(
										+R.string.allarm_type3) + endString);
				alarmholder.getAlarmItem_Iv1().setImageResource(
						R.drawable.sharecf_alarm_invade);
				alarmholder.getAlarmItem_Dis().setText(
						R.string.alarmrecord_default_content);
				break;

			case P2PValue.AlarmType.LOW_VOL_ALARM:
				alarmholder.getAlarmItem_Type()
						.setText(
								dname
										+ startString
										+ mcontext.getResources().getString(
												R.string.low_voltage_alarm)
										+ endString);
				alarmholder.getAlarmItem_Iv1().setImageResource(
						R.drawable.sharecf_alarm_invade);
				alarmholder.getAlarmItem_Dis().setText(
						R.string.alarmrecord_default_content);
				break;
			case P2PValue.AlarmType.PIR_ALARM:
				alarmholder.getAlarmItem_Type().setText(dname);
				alarmholder.getAlarmItem_TypeContant().setText(
						startString
								+ mcontext.getResources().getString(
										+R.string.allarm_type7) + endString);
				alarmholder.getAlarmItem_Iv1().setImageResource(
						R.drawable.sharecf_alarm_invade);
				alarmholder.getAlarmItem_Dis().setText(
						R.string.alarmrecord_default_content);
				break;

			case P2PValue.AlarmType.EXT_LINE_ALARM:
				alarmholder.getAlarmItem_Type().setText(dname);
				alarmholder.getAlarmItem_TypeContant().setText(
						startString
								+ mcontext.getResources().getString(
										+R.string.allarm_type5) + endString);
				alarmholder.getAlarmItem_Iv1().setImageResource(
						R.drawable.sharecf_alarm_invade);
				alarmholder.getAlarmItem_Dis().setText(
						R.string.sharecf_alarm_tip1);
				alarmholder.getAlarmItem_Dis().setText(
						R.string.alarmrecord_default_content);
				break;
			case P2PValue.AlarmType.DEFENCE:
				alarmholder.getAlarmItem_Type().setText(dname);
				alarmholder.getAlarmItem_TypeContant().setText(
						startString
								+ mcontext.getResources().getString(
										+R.string.defence2) + endString);
				alarmholder.getAlarmItem_Iv1().setImageResource(
						R.drawable.sharecf_alarmbell);
				alarmholder.getAlarmItem_Dis().setText(
						R.string.sharecf_alarm_tip1);
				break;
			case P2PValue.AlarmType.NO_DEFENCE:
				alarmholder.getAlarmItem_Type().setText(dname);
				alarmholder.getAlarmItem_TypeContant().setText(
						startString
								+ mcontext.getResources().getString(
										+R.string.no_defence) + endString);
				alarmholder.getAlarmItem_Iv1().setImageResource(
						R.drawable.sharecf_alarmbell);
				alarmholder.getAlarmItem_Dis().setText(
						R.string.sharecf_alarm_tip2);
				break;
			
			case P2PValue.AlarmType.BATTERY_LOW_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type10),
						endString);
				break;
			case P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type13),
						endString);
				break;
			case P2PValue.AlarmType.RECORD_FAILED_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type15),
						endString);
				break;
			
			case AlarmType_Pxy.DEBUG_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type4),
						endString);
				break;
			case AlarmType_Pxy.PARAM_ID_UPDATE_TO_SER:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type11),
						endString);
				break;
			case AlarmType_Pxy.TH_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type12),
						endString);
				break;
			case AlarmType_Pxy.FORCE_FROM_KEYPRESS_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type14),
						endString);
				break;
			case AlarmType_Pxy.EMAIL_TOO_OFTEN_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type16),
						endString);
				break;
			case AlarmType_Pxy.UART_INPUT_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type17),
						endString);
				break;
			case AlarmType_Pxy.FIRE_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type18),
						endString);
				break;
			case AlarmType_Pxy.GAS_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type19),
						endString);
				break;
			case AlarmType_Pxy.STEAL_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type20),
						endString);
				break;
			case AlarmType_Pxy.AROUND_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type21),
						endString);
				break;
			case AlarmType_Pxy.FORCE_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type22),
						endString);
				break;
			case AlarmType_Pxy.I20_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type23),
						endString);
				break;
			case AlarmType_Pxy.PREVENTDISCONNECT_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type24),
						endString);
				break;
			case AlarmType_Pxy.COMMUNICATION_TIMING_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type25),
						endString);
				break;
			case AlarmType_Pxy.LOW_POWER_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type26),
						endString);
				break;
			case AlarmType_Pxy.LOW_POWER_RECOVERY_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type27),
						endString);
				break;
			case AlarmType_Pxy.POWERONPROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type28),
						endString);
				break;
			case AlarmType_Pxy.POWEROFF_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type29),
						endString);
				break;
			case AlarmType_Pxy.DEF_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type30),
						endString);
				break;
			case AlarmType_Pxy.DEFDIS_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type31),
						endString);
				break;
			case AlarmType_Pxy.EXT_PROBER_ALARM:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_type32),
						endString);
				break;
			default:
				setAlarmLayout(alarmholder, dname, startString, mcontext
						.getResources().getString(R.string.allarm_nofound)
						+ type, endString);
				break;
			}

			String[] time = ar.alarmTime.split(" ");
			if (time.length > 1) {
				String[] time1 = time[1].split(":");
				alarmholder.getAlarmItem_Time1().setText(
						getTimeStatic(time1[0]));
				alarmholder.getAlarmItem_Time2().setText(
						time1[0] + ":" + time1[1] + ":" + time1[2]);
			}

			switch (ar.alarmStatus) {
			case 0: 
				alarmholder.getAlarmItem_RedSpot().setVisibility(View.VISIBLE);
				break;
			case 1: 
				alarmholder.getAlarmItem_RedSpot().setVisibility(View.GONE);
				break;
			}

			break;
		default:
			break;
		}
		// view.setOnLongClickListener(new View.OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View v) {
		// NormalDialog dialog = new NormalDialog(mcontext, Utils
		// .getResString(mcontext, R.string.delete_message),
		// mcontext.getResources().getString(
		// R.string.are_you_sure_delete)
		// + Utils.getResString(mcontext,
		// R.string.delete_content_blank),
		// mcontext.getResources().getString(R.string.delete),
		// mcontext.getResources().getString(R.string.cancel));
		// dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
		//
		// @Override
		// public void onClick() {
		//
		// DataManager.deleteAlarmRecordById(mcontext, ar.id);
		// if (isFromDevice) {
		// mal = getDataByDeviceId(ar.activeUser, ar.deviceId);
		// } else {
		// mal = getDataByDeviceId(ar.activeUser, null);
		// }
		//
		// notifyDataSetChanged();
		// }
		// });
		// dialog.showDialog();
		// return true;
		// }
		// });
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int type = ar.layoutType;
				switch (type) {
				case ITEM_TITLE:
					break;
				case ITEM_ALARM:
					DataManager.updateAlarmRecordReadById(mcontext, ar.id);

					// mal = getData();

					if (isFromDevice) {
						fa.NotifyChanged();
					} else {
						ff.NotifyChanged();
					}
					notifyDataSetChanged();

					switch (ar.alarmType) {
					case P2PValue.AlarmType.EXTERNAL_ALARM:
					case P2PValue.AlarmType.MOTION_DECT_ALARM:
					case P2PValue.AlarmType.EMERGENCY_ALARM:
					case P2PValue.AlarmType.LOW_VOL_ALARM:
					case P2PValue.AlarmType.PIR_ALARM:
					case P2PValue.AlarmType.EXT_LINE_ALARM:
						
					case P2PValue.AlarmType.BATTERY_LOW_ALARM:
					case P2PValue.AlarmType.ALARM_TYPE_DOORBELL_PUSH:
					case P2PValue.AlarmType.RECORD_FAILED_ALARM:
						
					case AlarmType_Pxy.DEBUG_ALARM:
					case AlarmType_Pxy.PARAM_ID_UPDATE_TO_SER:
					case AlarmType_Pxy.TH_ALARM:
					case AlarmType_Pxy.FORCE_FROM_KEYPRESS_ALARM:
					case AlarmType_Pxy.EMAIL_TOO_OFTEN_ALARM:
					case AlarmType_Pxy.UART_INPUT_ALARM:
					case AlarmType_Pxy.FIRE_PROBER_ALARM:
					case AlarmType_Pxy.GAS_PROBER_ALARM:
					case AlarmType_Pxy.STEAL_PROBER_ALARM:
					case AlarmType_Pxy.AROUND_PROBER_ALARM:
					case AlarmType_Pxy.FORCE_PROBER_ALARM:
					case AlarmType_Pxy.I20_PROBER_ALARM:
					case AlarmType_Pxy.PREVENTDISCONNECT_PROBER_ALARM:
					case AlarmType_Pxy.COMMUNICATION_TIMING_PROBER_ALARM:
					case AlarmType_Pxy.LOW_POWER_PROBER_ALARM:
					case AlarmType_Pxy.LOW_POWER_RECOVERY_PROBER_ALARM:
					case AlarmType_Pxy.POWERONPROBER_ALARM:
					case AlarmType_Pxy.POWEROFF_PROBER_ALARM:
					case AlarmType_Pxy.DEF_PROBER_ALARM:
					case AlarmType_Pxy.DEFDIS_PROBER_ALARM:
					case AlarmType_Pxy.EXT_PROBER_ALARM:
						Intent alarm = new Intent();
						alarm.setClass(mcontext, AlarmReordDetailActivity.class);
//						alarm.putExtra("",ar.);
						alarm.putExtra("alarm_id", ar.id);
						alarm.putExtra("alarm_type", ar.alarmType);
						alarm.putExtra("group", ar.group);
						alarm.putExtra("item", ar.item);
						alarm.putExtra("alarm_sbid", ar.deviceId);
						alarm.putExtra("alarm_time", ar.alarmTime);
						
						mcontext.startActivity(alarm);
						break;
					case P2PValue.AlarmType.DEFENCE:
						Intent alarm_defence = new Intent();
						alarm_defence.setClass(mcontext,
								AlarmReordDetailActivity.class);
						alarm_defence.putExtra("alarm_id", ar.id);
						alarm_defence.putExtra("alarm_type", ar.alarmType);
						alarm_defence.putExtra("group", ar.group);
						alarm_defence.putExtra("item", ar.item);
						alarm_defence.putExtra("alarm_sbid", ar.deviceId);
						alarm_defence.putExtra("alarm_time", ar.alarmTime);
						
						mcontext.startActivity(alarm_defence);
						break;

					case P2PValue.AlarmType.NO_DEFENCE:
						Intent alarm_nodefence = new Intent();
						alarm_nodefence.setClass(mcontext,
								AlarmReordDetailActivity.class);
						alarm_nodefence.putExtra("alarm_id", ar.id);
						alarm_nodefence.putExtra("alarm_type", ar.alarmType);
						alarm_nodefence.putExtra("group", ar.group);
						alarm_nodefence.putExtra("item", ar.item);
						alarm_nodefence.putExtra("alarm_sbid", ar.deviceId);
						alarm_nodefence.putExtra("alarm_time", ar.alarmTime);
						
						mcontext.startActivity(alarm_nodefence);
						break;
					default:
						Intent alarm_nofound = new Intent();
						alarm_nofound.setClass(mcontext, AlarmReordDetailActivity.class);
						alarm_nofound.putExtra("alarm_id", ar.id);
						alarm_nofound.putExtra("alarm_type", ar.alarmType);
						alarm_nofound.putExtra("group", ar.group);
						alarm_nofound.putExtra("item", ar.item);
						alarm_nofound.putExtra("alarm_sbid", ar.deviceId);
						alarm_nofound.putExtra("alarm_time", ar.alarmTime);
						
						mcontext.startActivity(alarm_nofound);
						break;
					}

				}
			}
		});
		return view;
	}

	private void setAlarmLayout(AlarmViewHolder alarmholder, String dname,
			String startString, String eventString, String endString) {
		alarmholder.getAlarmItem_Type().setText(dname);
		alarmholder.getAlarmItem_TypeContant().setText(
				startString + eventString + endString);
		alarmholder.getAlarmItem_Iv1().setImageResource(
				R.drawable.sharecf_alarm_invade);
		alarmholder.getAlarmItem_Dis().setText(
				R.string.alarmrecord_default_content);
	}

	class TitleViewHolder {
		private TextView titleTv;

		public TextView getTitleTv() {
			return titleTv;
		}

		public void setTitleTv(TextView titleTv) {
			this.titleTv = titleTv;
		}

	}

	class AlarmViewHolder {
		private ImageView alarmItem_Iv1;
		private ImageView alarmItem_RedSpot;
		private TextView alarmItem_Type;
		private TextView alarmItem_Dis;
		private TextView alarmItem_Time1, alarmItem_Time2;
		private TextView alarmItem_TypeContant;

		public TextView getAlarmItem_TypeContant() {
			return alarmItem_TypeContant;
		}

		public void setAlarmItem_TypeContant(TextView alarmItem_TypeContant) {
			this.alarmItem_TypeContant = alarmItem_TypeContant;
		}

		public ImageView getAlarmItem_Iv1() {
			return alarmItem_Iv1;
		}

		public void setAlarmItem_Iv1(ImageView alarmItem_Iv1) {
			this.alarmItem_Iv1 = alarmItem_Iv1;
		}

		public ImageView getAlarmItem_RedSpot() {
			return alarmItem_RedSpot;
		}

		public void setAlarmItem_RedSpot(ImageView alarmItem_RedSpot) {
			this.alarmItem_RedSpot = alarmItem_RedSpot;
		}

		public TextView getAlarmItem_Type() {
			return alarmItem_Type;
		}

		public void setAlarmItem_Type(TextView alarmItem_Type) {
			this.alarmItem_Type = alarmItem_Type;
		}

		public TextView getAlarmItem_Dis() {
			return alarmItem_Dis;
		}

		public void setAlarmItem_Dis(TextView alarmItem_Dis) {
			this.alarmItem_Dis = alarmItem_Dis;
		}

		public TextView getAlarmItem_Time1() {
			return alarmItem_Time1;
		}

		public void setAlarmItem_Time1(TextView alarmItem_Time1) {
			this.alarmItem_Time1 = alarmItem_Time1;
		}

		public TextView getAlarmItem_Time2() {
			return alarmItem_Time2;
		}

		public void setAlarmItem_Time2(TextView alarmItem_Time2) {
			this.alarmItem_Time2 = alarmItem_Time2;
		}

	}

	public String getTimeStatic(String str) {
		int currentTime = Integer.parseInt(str);
		if (currentTime >= 0 && currentTime <= 12) {
			return Utils.getResString(mcontext, R.string.time_static1);
		} else {
			return Utils.getResString(mcontext, R.string.time_static2);
		}

	}

	public ArrayList<AlarmRecord> getal() {
		return mal;
	}
}
