package com.tuwa.smarthome;

import com.tuwa.smarthome.activity.HomeActivity;
import com.tuwa.smarthome.dao.GateWayDao;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.PreferencesUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class alarmreceiver extends BroadcastReceiver {
	
	@Override
    public void onReceive(Context context, Intent i) {
		System.out.println(">>>alarmreceiver+接收到重启广播");
		
		String strname=PreferencesUtils.getString(context,"name","");
		String strpwd=PreferencesUtils.getString(context,"password","");
		String strwgid=PreferencesUtils.getString(context,"wgid","");
		
		SystemValue.phonenum=strname;
		SystemValue.gatewayid=strwgid;
		
		Intent intent = new Intent(context,HomeActivity.class);
		intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		
		Gateway mgateWay = new GateWayDao(context)
		                .getGatewayByGatewayNo(strwgid); // 获取该手机号下对应的第一个网关
		if(mgateWay!=null){
			NetValue.LOCAL_IP=mgateWay.getGatewayIp();
		}
    }
}
