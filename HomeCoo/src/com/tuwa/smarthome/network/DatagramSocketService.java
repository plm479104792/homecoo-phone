/*package com.tuwa.smarthome.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.alibaba.fastjson.JSON;
import com.tuwa.smarthome.global.SystemValue;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

*//**
 * UDP通过DatagramSocket实现广播七寸屏的IP
 * *//*
public class DatagramSocketService extends Service implements Runnable{

	private DatagramSocket datagramSocket;
	public static boolean flag=true;
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				SystemValue.MUSIC_SCREEN_IP = msg.obj.toString();
				//TODO 2016-08-31 10:30   AB都用了HOMECOO  防止A在B家内网连上B家的七寸屏        七寸屏端需要修改发送的数据
//				try {
//					SocketConnect socketConnect=JSON.parseObject(msg.obj.toString(),SocketConnect.class);
//					if (socketConnect.getGatewayNo().equals(SystemValue.gatewayid)) {
//						SystemValue.MUSIC_SCREEN_IP=socketConnect.getIpAddress();
//					}
//				} catch (Exception e) {
//				}
				break;
			case 2:
//				bt_send.setText(msg.obj.toString());
//				stringBuffer.setLength(0);
				break;
			}
		}
	};


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			datagramSocket=new DatagramSocket(8004);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		new Thread(this).start();
		
	}
	
	@Override
	public void run() {
		byte buf[] = new byte[1024];
		DatagramPacket dp=new DatagramPacket(buf, buf.length);
		while (flag) {
			try {
				datagramSocket.receive(dp);
				String IpAddress = new String(buf, 0, dp.getLength());
				Log.i("IP","DatagramSocketsocketservice"+IpAddress);
				Message message = new Message();
				message.what = 1;
				message.obj = IpAddress;
				handler.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
*/