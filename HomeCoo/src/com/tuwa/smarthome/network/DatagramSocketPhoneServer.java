package com.tuwa.smarthome.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import com.alibaba.fastjson.JSONObject;
import com.tuwa.smarthome.entity.MusicSocketByte;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.MusicUtil;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 手机APP 作为UDP广播的server端 用于主动向七寸屏获取IP 2016-09-29
 * */
public class DatagramSocketPhoneServer extends Service {

	private DatagramSocket datagramSocket;
	public static DatagramSocket datagramSocketReceive=null;
	private String multicasthost = "255.255.255.255";
	private InetAddress inetAddress = null;
	
	private volatile boolean isRuning = true;
	public static boolean datasocketserverSendFlag = true;      //111
	public static boolean datasocketserverReceiveFlag = true; //444
	public static boolean datasocketslientReceive = true;   //222
	public static boolean socketStatus=false;				//333
	public static boolean SendSocketFlag=true;

	private OutputStream outputStream = null;
	private InputStream inputStream = null;
//	private StringBuffer stringBuffer = DatagramSocketPhoneServer.this.stringBuffer;
	private StringBuffer stringBuffer=new StringBuffer();
	private String data;
	
	
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				
				datasocketserverSendFlag = false;      
				datasocketserverReceiveFlag = false; 
				datasocketslientReceive = false;   
				socketStatus=true;				
				SendSocketFlag=false;
				
//				datasocketslientReceive=false;
//				datasocketserverReceiveFlag=false;
//				datasocketserverSendFlag=false;
				MusicUtil.StopDatagramSocketServer(DatagramSocketPhoneServer.this);
				
				break;
			case 2:

				break;
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
//		try {
//			datagramSocket = new DatagramSocket();
//			inetAddress = InetAddress.getByName(multicasthost);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			datagramSocketReceive = new DatagramSocket(8004);
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}
//		StartDatagramSocketServer();
//		ReceiveDatagramSocket();
//		Log.i("X-2016-Udp", "创建DatagramSocketService");
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
//		 datasocketslientReceive = true;   //222
//		socketStatus=false;				//333
//		 SendSocketFlag=true;
//		StartDatagramSocketServer();
//		ReceiveDatagramSocket();
//		Log.i("X-2016-Udp","调用onstart方法");
		try {
			datagramSocket = new DatagramSocket();
			inetAddress = InetAddress.getByName(multicasthost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (datagramSocketReceive==null) {
				datagramSocketReceive = new DatagramSocket(8004);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		StartDatagramSocketServer();
		ReceiveDatagramSocket();
		
		return super.onStartCommand(intent, flags, startId);
	}
//	
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		Log.i("X-2016-Udp","停止DatagramSocketService");
//	}
	
	

	/**
	 * 启动一个DatagramSocketserver 去广播本机的gatewayNo 用于获取七寸屏的IP
	 * */
	public void StartDatagramSocketServer() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				DatagramPacket datagramPacket = null;
				String SData = "S" + SystemValue.gatewayid;
				// byte[] data=SystemValue.gatewayid.getBytes();
				byte[] data = SData.getBytes();
				datagramPacket = new DatagramPacket(data, data.length,
						inetAddress, 8004);
				while (datasocketserverSendFlag) {
					if (isRuning) {
						try {
							datagramSocket.send(datagramPacket);
							datagramSocket.setSoTimeout(30000); // 设置发送/接收
																// 超时时间30S
//							Log.i("X-2016-Udp", "发送UDP广播 Data : " + SData);
							Thread.sleep(10000);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}

			}
		};
		thread.start();

	}

	/**
	 * 开启一个线程用于获取UDP广播 接收数据
	 * */
	public void ReceiveDatagramSocket() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				while (datasocketserverReceiveFlag) {
					try {
						datagramSocketReceive.receive(packet);
						String receiveData = new String(buf, 0,
								packet.getLength());
//						Log.i("X-2016-Udp", "DatagramSocketServer 收到data: "
//								+ receiveData);
						String RDataHead = receiveData.substring(0, 1);
						String RData = receiveData.substring(1);
						if (RDataHead.equals("C")) {
							// 收到七寸屏发送自己的IP 地址信息
							SystemValue.MUSIC_SCREEN_IP = RData;
							ConnectScreen(RData);
						}
//						Log.i("X-2016-Udp", "收到的data第一个字节：" + RDataHead
//								+ "  收到 有用的Data:" + RData);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		};
		thread.start();
	}

	/**
	 * new 一个socketclient 去连接七寸屏，如果收到返回的消息，说明该IP地址是对的
	 * */
	public void ConnectScreen(final String Ip) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				if (!socketStatus) {
				try {
					@SuppressWarnings("resource")
					Socket socket = new Socket(Ip, 8000);
					outputStream = socket.getOutputStream();
					inputStream = socket.getInputStream();
					if (socket!=null) {
						socketStatus=true;
					}
					sendSocket();
					SocketReceive(inputStream);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
				
			}
		};
		thread.start();
	}

	public void SocketReceive(final InputStream inputStream) {

		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();

				while (datasocketslientReceive) {

					int len;
					byte[] bytes = new byte[20];
					boolean isString = false;

					// 在这里需要明白一下什么时候其会等于 -1，其在输入流关闭时才会等于 -1，
					// 并不是数据读完了，再去读才会等于-1，数据读完了，最结果也就是读不到数据为0而已；
					if (stringBuffer.length() > 0) {
//						stringBuffer.delete(0, stringBuffer.length() - 1);
						stringBuffer.setLength(0);
					}
					try {
						while ((len = inputStream.read(bytes)) != -1) {
							for (int i = 0; i < len; i++) {
								if (bytes[i] != '\0') {
									stringBuffer.append((char) bytes[i]);
								} else {
									isString = true;
									break;
								}
							}
							if (isString) {
								
								String aa = stringBuffer.toString();
//								Log.i("X-2016-Udp", "=====收到七寸屏的广播 ===== " + aa);
//								System.err.println(aa);
								try {
								MusicSocketByte byte1 = JSONObject.parseObject(aa, MusicSocketByte.class);
								// 根据order 判断进行什么样的操作
								if (byte1.getOrder().equals(SystemValue.UDP_SCREEN_IP_TRUE)) {
									//正确收到七寸屏的socket回复之后     这里启动一个线程去关闭线程
									Message msg=handler.obtainMessage();
									msg.what=1;
									handler.sendMessage(msg);
									
								}
								} catch (Exception e) {
									System.err.println("本地获取广播消息JSON解析异常："+e);
								}
								isString = false;
							}
						}
					} catch (IOException e) {
//						e.printStackTrace();
						Log.i("X-2016-Udp",e.getMessage());
					}

				}

			}
		};
		thread.start();
	}

	
	/**
	 * @Description:socket发送音乐控制  MusicOrder 到七寸屏
	 * */
	public void sendSocket(){
		//在后面加'\0'是为了在服务端方便我们解析
//		data=MusicUtil.ToMusicOrderSocketJson(musicOrder);
		data=MusicUtil.SendIPFlagToScreen();
		Thread thread=new Thread(){																				
			
			@Override
			public void run() {
				super.run();
				
//				while (SendSocketFlag) {
					if (socketStatus) {
						try {
							outputStream.write(data.getBytes());
							outputStream.flush();
						} catch (IOException e) {
							
							
							e.printStackTrace();
						}
					}
				}
				
//			}
			
		};
		thread.start();
	}
	
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
