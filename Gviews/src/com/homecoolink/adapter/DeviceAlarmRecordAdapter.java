package com.homecoolink.adapter;

import java.util.ArrayList;
import java.util.Collections;

import com.homecoolink.R;
import com.homecoolink.data.DataManager;
import com.homecoolink.data.DeviceAlarmRecord;
import com.homecoolink.utils.Utils;
import com.p2p.core.P2PValue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DeviceAlarmRecordAdapter extends BaseAdapter {
	Context context;
	ArrayList<DeviceAlarmRecord> list;
    
    public DeviceAlarmRecordAdapter(Context contex,ArrayList<DeviceAlarmRecord> list) {
		// TODO Auto-generated constructor stub
		this.context=contex;
		this.list=list;
		Collections.reverse(this.list);
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
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
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
			view = LayoutInflater.from(context).inflate(R.layout.list_alarm_record_item, null);
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
		DeviceAlarmRecord alarmRecord=list.get(arg0);
		holder.getAlarmNumber().setText(String.valueOf(arg0));
		String [] time=alarmRecord.alarmTime.split(" ");
		holder.getAllarmTime().setText(time[0]);
		holder.getAlarmtime2().setText(time[1]);
		switch(alarmRecord.alarmType){
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
		if(alarmRecord.alarmType==P2PValue.AlarmType.EXTERNAL_ALARM){
			holder.getText_group().setText(Utils.getDefenceAreaByGroup(context,alarmRecord.group));
			holder.getText_item().setText(String.valueOf(alarmRecord.item+1));
		}else{
			holder.getText_group().setText("");
			holder.getText_item().setText("");
		}
		return view;
	}
    public void updateData(Context mcontext,String deviceId){
    	list=DataManager.findDeviceAlarmRecordByDeviceId(mcontext, deviceId);
    	Collections.reverse(list);
    	this.notifyDataSetChanged();
    }
}
