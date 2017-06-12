package com.homecoolink.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.utils.Utils;
import com.p2p.core.P2PValue;
import com.p2p.core.network.AlarmRecordResult.SAlarmRecord;


public class AlarmRecordAdapter extends BaseAdapter{
	Context mContext;
	List<SAlarmRecord> list;
    int showCount=20;
	
	public AlarmRecordAdapter(Context context){
		this.mContext = context;
		list=new ArrayList<SAlarmRecord>();
	}
	
	
	class ViewHolder{
		private TextView robotId;
		private TextView allarmType;
		private TextView allarmTime;
		private LinearLayout layout_extern;
		private TextView text_group;
		private TextView text_item;
		private TextView alarmtime2;
		private TextView alarmNumber;
		public TextView getAlarmNumber() {
			return alarmNumber;
		}
		public void setAlarmNumber(TextView alarmNumber) {
			this.alarmNumber = alarmNumber;
		}
		public TextView getAlarmtime2() {
			return alarmtime2;
		}
		public void setAlarmtime2(TextView alarmtime2) {
			this.alarmtime2 = alarmtime2;
		}
		public LinearLayout getLayout_extern() {
			return layout_extern;
		}
		public void setLayout_extern(LinearLayout layout_extern) {
			this.layout_extern = layout_extern;
		}
		public TextView getText_group() {
			return text_group;
		}
		public void setText_group(TextView text_group) {
			this.text_group = text_group;
		}
		public TextView getText_item() {
			return text_item;
		}
		public void setText_item(TextView text_item) {
			this.text_item = text_item;
		}
		public TextView getRobotId() {
			return robotId;
		}
		public void setRobotId(TextView robotId) {
			this.robotId = robotId;
		}
		public TextView getAllarmType() {
			return allarmType;
		}
		public void setAllarmType(TextView allarmType) {
			this.allarmType = allarmType;
		}
		public TextView getAllarmTime() {
			return allarmTime;
		}
		public void setAllarmTime(TextView allarmTime) {
			this.allarmTime = allarmTime;
		}
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(list.size()<showCount){
			   return list.size();	
			}
			return showCount;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = arg1;
		ViewHolder holder;
		if(null==view){
//			view = LayoutInflater.from(mContext).inflate(R.layout.list_alarm_record_item, null);
//			holder = new ViewHolder();
//			holder.setRobotId((TextView) view.findViewById(R.id.robot_id));
//			holder.setAllarmType((TextView) view.findViewById(R.id.allarm_type));
//			holder.setAllarmTime((TextView) view.findViewById(R.id.allarm_time));
//			holder.setLayout_extern((LinearLayout)view.findViewById(R.id.layout_extern));
//			holder.setText_group((TextView) view.findViewById(R.id.text_group));
//			holder.setText_item((TextView) view.findViewById(R.id.text_item));
//			view.setTag(holder);
			view = LayoutInflater.from(mContext).inflate(R.layout.list_alarm_record_item, null);
			holder = new ViewHolder();
			holder.setAlarmNumber((TextView)view.findViewById(R.id.tv_number));
			holder.setAllarmTime((TextView) view.findViewById(R.id.tv_time1));
			holder.setAlarmtime2((TextView)view.findViewById(R.id.tv_time2));
			holder.setAllarmType((TextView) view.findViewById(R.id.tv_type));
			holder.setText_group((TextView) view.findViewById(R.id.tv_defence_area));
			holder.setText_item((TextView) view.findViewById(R.id.tv_channel));
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		final SAlarmRecord ar = list.get(arg0);
		holder.getAlarmNumber().setText(String.valueOf(arg0));
		String alarmtime=Utils.ConvertTimeByLong(ar.alarmTime);
		String [] time=alarmtime.split(" ");
		Log.e("time", alarmtime);
		holder.getAllarmTime().setText(time[0]);
		holder.getAlarmtime2().setText(time[1]);
		switch(ar.alarmType){
		case P2PValue.AlarmType.EXTERNAL_ALARM:
			holder.getAllarmType().setText(R.string.allarm_type1);
			break;
		case P2PValue.AlarmType.MOTION_DECT_ALARM:
			holder.getAllarmType().setText(R.string.allarm_type2);
			break;
		case P2PValue.AlarmType.EMERGENCY_ALARM:
			holder.getAllarmType().setText(R.string.allarm_type3);
			break;

		case P2PValue.AlarmType.LOW_VOL_ALARM:
			holder.getAllarmType().setText(R.string.low_voltage_alarm);
			break;
		case P2PValue.AlarmType.PIR_ALARM:
			holder.getAllarmType().setText(R.string.allarm_type4);
			break;
		case P2PValue.AlarmType.EXT_LINE_ALARM:
			holder.getAllarmType().setText(R.string.allarm_type5);
			break;
		case P2PValue.AlarmType.DEFENCE:
			holder.getAllarmType().setText(R.string.defence2);
			break;
		case P2PValue.AlarmType.NO_DEFENCE:
			holder.getAllarmType().setText(R.string.no_defence);
			break;
		}
		if(ar.defenceArea==-1){
			holder.getText_group().setText(" ");
			holder.getText_item().setText(" ");
		}else{
			holder.getText_group().setText(Utils.getDefenceAreaByGroup(mContext, ar.defenceArea));
			holder.getText_item().setText(String.valueOf(ar.channel+1));
		}
		
//		holder.getAllarmTime().setText(Utils.getFormatTellDate(mContext, ar.alarmTime));
		
//		holder.getLayout_extern().setVisibility(RelativeLayout.GONE);
//		switch(ar.alarmType){
//		case 1:
//			holder.getAllarmType().setText(R.string.allarm_type1);
//			if(ar.group>=0&&ar.item>=0){
//				holder.getLayout_extern().setVisibility(RelativeLayout.VISIBLE);
//				holder.getText_group().setText(
//						mContext.getResources().getString(R.string.area)+
//						":"+Utils.getDefenceAreaByGroup(mContext, ar.group));
//				holder.getText_item().setText(
//						mContext.getResources().getString(R.string.channel)+
//						":"+(ar.item+1));
//			}
//			break;
//		case 2:
//			holder.getAllarmType().setText(R.string.allarm_type2);
//			break;
//		case 3:
//			holder.getAllarmType().setText(R.string.allarm_type3);
//			break;
//		case 4:
//			holder.getAllarmType().setText(R.string.allarm_type4);
//			break;
//		case 5:
//			holder.getAllarmType().setText(R.string.allarm_type5);
//			break;
//		case 6:
//			holder.getAllarmType().setText(R.string.allarm_type6);
//			if(ar.group>=0&&ar.item>=0){
//				holder.getLayout_extern().setVisibility(RelativeLayout.VISIBLE);
//				holder.getText_group().setText(
//						mContext.getResources().getString(R.string.area)+
//						":"+Utils.getDefenceAreaByGroup(mContext, ar.group));
//				holder.getText_item().setText(
//						mContext.getResources().getString(R.string.channel)+
//						":"+(ar.item+1));
//			}
//			break;
//		}
		
//		view.setOnLongClickListener(new OnLongClickListener(){
//
//			@Override
//			public boolean onLongClick(View arg0) {
//				// TODO Auto-generated method stub
//				NormalDialog dialog = new NormalDialog(
//						mContext,
//						mContext.getResources().getString(R.string.delete_alarm_records),
//						mContext.getResources().getString(R.string.are_you_sure_delete)+" "+ar.deviceId+"?",
//						mContext.getResources().getString(R.string.delete),
//						mContext.getResources().getString(R.string.cancel)
//						);
//				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
//					
//					@Override
//					public void onClick() {
//						// TODO Auto-generated method stub
//						
////						DataManager.deleteAlarmRecordById(mContext, ar.id);
////						Intent refreshAlarm=new Intent();
////						refreshAlarm.setAction(Constants.Action.REFRESH_ALARM_RECORD);
////						mContext.sendBroadcast(refreshAlarm);
//					}
//				});
//				dialog.showDialog();
//				return true;
//			}
//			
//		});
		return view;
	} 
	public void updateNewDate(List<SAlarmRecord> datas) {
		if (datas.size() <= 0) {
			return;
		}
		Collections.sort(datas);
		if (!this.list.contains(datas.get(datas.size() - 1))) {
			this.list.clear();
		}

		for (SAlarmRecord gxar : datas) {
			if (!this.list.contains(gxar)) {
				this.list.add(gxar);
				// showCount++;
				Log.e("alarm","messgeIds="+gxar.messgeId+"sourceId="+gxar.sourceId+"pictureUrl="+gxar.pictureUrl+"alarmTime="+gxar.alarmTime
						+"alarmType="+gxar.alarmType+"defenceArea="+gxar.defenceArea+"channel="+gxar.channel+"serverReceiveTime="
						+gxar.serverReceiveTime);
			}
		}
		Collections.sort(this.list);
		Log.e("my", "AlarmRecordCount:" + this.list.size());
		this.notifyDataSetChanged();
	}
	public void updateHistoryData(List<SAlarmRecord> data) {
		if (data.size() <= 0) {
			return;
		}

		int count = 0;
		for (SAlarmRecord gxar : data) {
			if (!this.list.contains(gxar)) {
				this.list.add(gxar);
				count++;
			}
		}
		Collections.sort(this.list);
		showCount=list.size();
		Log.e("my", "AlarmRecordCount:" + this.list.size() + "->showCount:"
				+ showCount);
		this.notifyDataSetChanged();
	}
	public void updateNewRecord(List<SAlarmRecord> datas) {
		if (datas.size() <= 0) {
			return;
		}
		Collections.sort(datas);
		for (SAlarmRecord gxar : datas) {
			if (!this.list.contains(gxar)) {
				this.list.add(gxar);
				// showCount++;
				Log.e("alarm","messgeIds="+gxar.messgeId+"sourceId="+gxar.sourceId+"pictureUrl="+gxar.pictureUrl+"alarmTime="+gxar.alarmTime
						+"alarmType="+gxar.alarmType+"defenceArea="+gxar.defenceArea+"channel="+gxar.channel+"serverReceiveTime="
						+gxar.serverReceiveTime);
			}
		}
		Collections.sort(this.list);
		Log.e("my", "AlarmRecordCount:" + this.list.size());
		this.notifyDataSetChanged();
	}
	public String getLastIndex() {
//		if (this.list.size() <= showCount && this.list.size() > 0) {
//			return this.list.get(this.list.size() - 1).messgeId;
//		} else {
//			return "";
//		}
		if(this.list.size()>0){
			return this.list.get(this.list.size()-1).messgeId;
		}else{
			return "";
		}

	}
	public String getFirstIndex() {
//		if (this.list.size() <= showCount && this.list.size() > 0) {
//			return this.list.get(this.list.size() - 1).messgeId;
//		} else {
//			return "";
//		}
		if(this.list.size()>0){
			return this.list.get(0).messgeId;
		}else{
			return "";
		}

	}
}
