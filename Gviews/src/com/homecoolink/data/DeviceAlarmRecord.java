package com.homecoolink.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DeviceAlarmRecord implements Serializable,Comparable{
		public String deviceId;
	
		public int alarmType;
		
		public String alarmTime;
	
		public int group;
	
		public int item;
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			DeviceAlarmRecord o=(DeviceAlarmRecord) arg0;
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
			long millionSeconds1 = 0;
			long millionSeconds2 = 0;
			try {
				millionSeconds1 = sdf.parse(this.alarmTime).getTime();
			    millionSeconds2 =sdf.parse(o.alarmTime).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(millionSeconds1>millionSeconds2){
				return -1;
			}else if(millionSeconds1<millionSeconds2){
				return 1;
			}else{
				return 0;
			}
		}
}
