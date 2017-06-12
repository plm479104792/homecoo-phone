package com.tuwa.smarthome.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;

public class NetTool {
	  private int SERVERPORT = 9091;
	  private String locAddress;//存储本机ip，例：本地ip ：192.168.1.
	  private Runtime run = Runtime.getRuntime();//获取当前运行环境，来执行ping，相当于windows的cmd
	  private Process proc = null;
	  private String ping = "ping -c 1 -w 0.5 " ;//其中 -c 1为发送的次数，-w 表示发送后等待响应的时间
	  private int j;//存放ip最后一位地址 0-255
	  
	  int h=0;
	  int k=0;
	  
	  private Context ctx;//上下文
	  public NetTool(Context ctx){
	    this.ctx = ctx;
	  }
	 
	  private Handler handler = new Handler(){
	    public void dispatchMessage(Message msg) {
	      switch (msg.what) {
	      case 222:// 服务器消息
	        break;
	         
	      case 333:// 扫描完毕消息
	        Toast.makeText(ctx, "扫描到主机："+((String)msg.obj).substring(6), Toast.LENGTH_LONG).show();
	         
	        break;
	      case 444://扫描失败
	        Toast.makeText(ctx, (String)msg.obj, Toast.LENGTH_LONG).show();
	        break;
	      }
	    }
	 
	  };
	   
	 
	 
	  //向serversocket发送消息
	  public String sendMsg(String ip,String msg) {
	    String res = null;
	    Socket socket = null;
	       
	    try {
	      socket = new Socket(ip, SERVERPORT);
	      System.out.println("===AAA===验证网关ip成功===BBB==="+ip);
	      NetValue.LOCAL_IP=ip;
	      NetValue.IP_CONNECT_FLAG=true;

		  	if (ipCallBack!=null) {
		  		ipCallBack.callBack(ip); 
			}
	      
	 
	    } catch (Exception unknownHost) {
	      System.out.println("You are trying to connect to an unknown host!");
	    } finally {
	      // 4: Closing connection
	    k++;
	    System.out.println("已经适配完的ip数量："+k);
	      try {
	        if (socket != null) {
	          socket.close();
	        }
	      } catch (IOException ioException) {
	        ioException.printStackTrace();
	      }
	    }
	    return res;
	  }
	   
	 
	 
	  /**
	   * 扫描局域网内ip，找到对应服务器
	   */
	  public  void scan(){
	     System.out.println("开始扫描网络地址...");
	    locAddress = getLocAddrIndex();//获取本地ip前缀
	     
	    if(locAddress.equals("")){
	      return ;
	    }
	     
	    for ( int i = 0; i < 256; i++) {//创建256个线程分别去ping
	      j = i ;
	       
	      new Thread(new Runnable() {
	         
	        public void run() {
	          String p = NetTool.this.ping + locAddress + NetTool.this.j ;
	          String current_ip = locAddress+ NetTool.this.j;
	           
	          try {
	            proc = run.exec(p);
	             
	            int result = proc.waitFor();
	            if (result == 0) {
	              System.out.println("连接ping通" + current_ip);
	              
	              h++;
	              System.out.println("当前ping通的数量为："+h);
	              
	              sendMsg(current_ip,"hello");
	              
//	              //保存ping通的ip地址
//	              SystemValue.setIps(current_ip);
//	              // 向服务器发送验证信息
	            }
	          } catch (IOException e1) {
	            e1.printStackTrace();
	          } catch (InterruptedException e2) {
	            e2.printStackTrace();
	          } finally {
	            proc.destroy();
	          }
	        }
	      }).start();
	       
	    }
	     
	  }
	 
	   
	  
	  //获取本地ip地址
	  public String getLocAddress(){
	    String ipaddress = " ";
	    try {
	      Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
	      // 遍历所用的网络接口
	      while (en.hasMoreElements()) {
	        NetworkInterface networks = en.nextElement();
	        // 得到每一个网络接口绑定的所有ip
	        Enumeration<InetAddress> address = networks.getInetAddresses();
	        // 遍历每一个接口绑定的所有ip
	        while (address.hasMoreElements()) {
	          InetAddress ip = address.nextElement();
	          if (!ip.isLoopbackAddress()
	              && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
	            ipaddress = ip.getHostAddress();
	          }
	        }
	      }
	    } catch (SocketException e) {
	      Log.e("", "获取本地ip地址失败");
	      e.printStackTrace();
	    }
	    System.out.println("本地手机IP:" + ipaddress);
	     
	    return ipaddress;
	  }
	  

	  private String intToIp(int i) {       
	     return (i & 0xFF ) + "." +       
		  ((i >> 8 ) & 0xFF) + "." +       
		  ((i >> 16 ) & 0xFF) + "."
		  +  ( i >> 24 & 0xFF) ;
	  //    +(0x01) ;
	  } 
	  
	  //获取IP前缀
	  public String getLocAddrIndex(){
	     
		String str = getLocAddress();
	     
	    if(!str.equals("")){
	      return str.substring(0,str.lastIndexOf(".")+1);
	    }
	     
	    return null;
	  }
	   
	  //获取本机设备名称
	  public String getLocDeviceName() {
	     
	    return android.os.Build.MODEL;
	     
	  }
	  
	  /**
       * 回调函数
       * @param callBack
       */
	    public IPCallBack ipCallBack;
	  
		public void callSocket( IPCallBack callBack) {  
	    	   this.ipCallBack=callBack;    //注意全局变量的使用    
	    } 

	    /** 
	     * socket接收数据回调接口
	     */  
	    public interface IPCallBack {  
	        public void callBack(String string);  
	    }   
	  
	  
	   
}


	 
