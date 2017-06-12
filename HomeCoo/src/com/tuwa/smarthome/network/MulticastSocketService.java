/*package com.tuwa.smarthome.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.tuwa.smarthome.global.SystemValue;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


public class MulticastSocketService extends Service implements Runnable{

	*//**
	 * @Description:IP协议为多点广播提供了这批特殊的IP地址，这些IP地址的范围是224.0.0.0至239.255.255.255 
	 *                                                                        多点广播
	 * *//*
	private String multicasthost = "224.0.0.1";
	private MulticastSocket multicastSocket;
	private InetAddress receiveAddress;
	public static boolean flag=true;
	StringBuffer stringBuffer=new StringBuffer();
	
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				SystemValue.MUSIC_SCREEN_IP = msg.obj.toString();
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
			multicastSocket = new MulticastSocket(8003);
			receiveAddress=InetAddress.getByName(multicasthost);
			multicastSocket.joinGroup(receiveAddress);
			new Thread(this).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}



	@Override
	public void run() {
		byte buf[] = new byte[1024];
//		DatagramPacket dp = new DatagramPacket(buf, 1024);
		DatagramPacket dp = new DatagramPacket(buf, buf.length, receiveAddress, 8003);
		while (flag) {
			try {
				multicastSocket.receive(dp);
				String IpAddress = new String(buf, 0, dp.getLength());
				Log.i("IP","multicasesocketservice "+IpAddress);
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