package com.homecoolink.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UDPHelper {
	public Boolean IsThreadDisable = false;
	public int port;
	InetAddress mInetAddress;
	public Handler mHandler;
	DatagramSocket datagramSocket = null;
	public static final int HANDLER_MESSAGE_BIND_ERROR = 0x01;
	public static final int HANDLER_MESSAGE_RECEIVE_MSG = 0x02;

	public UDPHelper(int port) {
		this.port = port;
	}

	public void setCallBack(Handler handler) {
		this.mHandler = handler;
	}

	public void StartListen() {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] message = new byte[1024];

				try {
					
					try {
						datagramSocket = new DatagramSocket(port);
						Log.e("port", "port=" + port);
					} catch (Exception e) {
						port = 57521;
						datagramSocket = new DatagramSocket(port);
						Log.e("port", "port=" + port);
					}

					datagramSocket.setBroadcast(true);
					DatagramPacket datagramPacket = new DatagramPacket(message,
							message.length);
					while (!IsThreadDisable) {
						
						datagramSocket.receive(datagramPacket);
						mInetAddress = datagramPacket.getAddress();
						Log.e("ip_address", "mInetAddress=" + mInetAddress);
						byte[] data = datagramPacket.getData();
						Log.e("setwifi", Arrays.toString(data));
						int contactId = bytesToInt(data, 16);
						int frag = bytesToInt(data, 24);
						if (data[0] == 1) {
							// Contact saveContact = new Contact();
							// saveContact.contactId=String.valueOf(contactId);
							// Intent it=new Intent();
							// it.setAction(Constants.Action.RADAR_SET_WIFI_SUCCESS);
							// it.putExtra("isCreatePassword", true);
							// it.putExtra("contact", saveContact);
							// MyApp.app.sendBroadcast(it);
							Log.e("setwifi", "contactId=" + contactId + "--"
									+ "frag=" + frag);
							if (null != mHandler) {
								Message msg = new Message();
								msg.what = HANDLER_MESSAGE_RECEIVE_MSG;
								Bundle bundler = new Bundle();
								bundler.putString("contactId",
										String.valueOf(contactId));
								bundler.putString("frag", String.valueOf(frag));
								String ip_address = String
										.valueOf(mInetAddress);
								bundler.putString(
										"ipFlag",
										ip_address.substring(
												ip_address.lastIndexOf(".") + 1,
												ip_address.length()));
								msg.setData(bundler);
								mHandler.sendMessage(msg);
								break;
							}
						}

					}
				} catch (SocketException e) {

				} catch (Exception e) {
					Log.e("343", Log.getStackTraceString(e));
					e.printStackTrace();
					IsThreadDisable = true;
					if (null != mHandler) {
						mHandler.sendEmptyMessage(HANDLER_MESSAGE_BIND_ERROR);
					}
				} finally {
					if (null != datagramSocket) {
						datagramSocket.close();
						datagramSocket = null;
					}
				}
			}

		}).start();

	}

	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
				| ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24);
		return value;
	}

	public void StopListen() {
		this.IsThreadDisable = true;
		if (null != datagramSocket) {
			datagramSocket.close();
			datagramSocket = null;
		}
	}

}
