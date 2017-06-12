package com.tuwa.smarthome.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Music;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.MusicSocket;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.ThemeData;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SceneModelActivity extends BaseActivity {
	//SharedPreferences共享数据
      SharedPreferences preferences;
      SharedPreferences.Editor editor;
      
	//activity绑定service
	private  SocketService devService ;
	
	@Bind(R.id.tv_head_submit)  TextView tvExit;
	@Bind(R.id.tv_head_back) TextView tvBack;
	@Bind(R.id.tv_head_title) TextView tvTitle;
	@Bind(R.id.gv_scenelist) GridView gvScene;
	@Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
	
	private List<Theme>  themeList=new ArrayList<Theme>();
	private List<ThemeDevice>  themeDevicelist=new ArrayList<ThemeDevice>();
	private int j=0;     //正在执行的情景关联设备指针
	private SceneAdapter sceneAdpter=null;
	private int vPsotion;   //当前点击后情景全局指针
	boolean initSceneFlag=false ;   //初始化四个默认的情景
	/**
	 * socket私有属性
	 * */
	private boolean socketStatus=false;
	private Socket socket=null;
	private OutputStream outputStream=null;
	private InputStream inputStream=null;
	StringBuffer stringBuffer=new StringBuffer();
	private String Incodemusiclist="";
	private String data;
	private static List<Music> mArrayList=new ArrayList<Music>();
	public static List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
	
	  /*辅线程动态刷新页面*/   
    Handler handler=new Handler(){
	   	 @Override
	   	 public void handleMessage(Message msg){
	   		 switch(msg.what){
	   		 case 0x129:
	   			sceneAdpter=new SceneAdapter(); 
	   			gvScene.setAdapter(sceneAdpter);
	   			break;
	   		 case 0x121:
	   			String themename=(String) msg.obj;
	   			showLoadingDialog(themename+"情景联动正在执行...");
	   		
		   		break;
	   		case 1:
	   			 //得到七寸屏发送过来的  未解码的音乐列表字符串
	   			Incodemusiclist=msg.obj.toString();
	   			stringBuffer.setLength(0);
	   			 break;
	   		 }
	   	 }
	   };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scene_model);
		ButterKnife.bind(SceneModelActivity.this);//注解工具声明
		
		// 获取只能被本应用程序读、写的SharedPreferences对象
		 preferences = getSharedPreferences("tuwa",Context.MODE_PRIVATE);
		 editor = preferences.edit();
		
		// Activity和service绑定2
 		Intent service = new Intent(SceneModelActivity.this,SocketService.class);
 		bindService(service, devconn, Context.BIND_AUTO_CREATE);
 		
		tvExit.setText("退出");
		tvTitle.setText("情景模式");
		
		initViews();
		initDatas();
		connectSocket();
	}
	
	//获取SocketService实例对象
	  ServiceConnection devconn = new ServiceConnection() {  
	        @Override  
	        public void onServiceDisconnected(ComponentName name) {  
	              
	        }  
	          
	        @Override  
	        public void onServiceConnected(ComponentName name, IBinder service) {  
	            //返回一个SocketService对象  
	        	devService = ((SocketService.SocketBinder)service).getService();  
	            
	        	devService.callSocket(new SocketCallBack() {
	        		@Override 
	    			public void callBack(TranObject tranObject) {
	    				
	    				switch (tranObject.getTranType()) {
	    				case NETMSG:   
	    					int netstatue = (Integer) tranObject.getObject();
							if (NetValue.NONET == netstatue) { // 本地连接失败
								if(!NetValue.autoFlag){
									ToastUtils.showToast(SceneModelActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
								}
								
								NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("远程");
							}else if(NetValue.INTRANET == netstatue){
								tvbttomNetwork.setText("本地");
							}
							break;	
	    				case DEVMSG:
	    				default:
	    					break;
	    				}
	    			}
				});      	
	        }  
	    };
	    
		@Override
		protected void initViews() {
			if (NetValue.netFlag==NetValue.INTRANET) {
				tvbttomNetwork.setText("本地");   //任务栏显示网络状态
			}else if (NetValue.netFlag==NetValue.OUTERNET) {
				tvbttomNetwork.setText("远程");    //任务栏显示网络状态
			} 
			
		}
		
		@Override
		protected void initDatas() {
			//根据网关号从数据库加载相应设备
			  List<Theme> allThemeList=new ArrayList<Theme>();
			  allThemeList=new ThemeDao(SceneModelActivity.this)
                             .themeListByGatewayNo(SystemValue.gatewayid);
			  themeList=WebPacketUtil.findCustomThemeFromThemesAll(allThemeList);
			  
			  if(themeList.size()>0){
				   //  异步进程更新界面
		 		    Message msg=new Message();
		            msg.what=0x129;
		            handler.sendMessage(msg);
			  }else{
				  ToastUtils.showToast(SceneModelActivity.this,"请在设置页面中添加情景！",1000);
			  }
			  

		}
	
	   //情景列表适配器
		public class SceneAdapter  extends BaseAdapter {
			@Override
			public int getCount() {
				return themeList.size();
			}
			@Override
			public Object getItem(int position) {
				return themeList.get(position);
			}
			@Override
			public long getItemId(int position) {
				return position;
			}
			@Override
			public View getView( final int position, View view, ViewGroup parent) {
				ViewHolder holder;
				if (view != null) {
				      holder = (ViewHolder) view.getTag();
				    } else {
				      view = View.inflate(SceneModelActivity.this,R.layout.item_scene,null);
				      holder = new ViewHolder(view);
				      view.setTag(holder);
				    }
				
				Theme theme = themeList.get(position);
				int themeType=theme.getThemeType();
				String deviceNo=theme.getDeviceNo();
				if(themeType==SystemValue.SCENE_HARD){  //硬件情景显示硬件情景开关的位置
					UserSpaceDevice userSpace = new UserSpaceDevDao(SceneModelActivity.this)
					    .findDeviceSpace(SystemValue.phonenum, deviceNo);
					if (userSpace != null) {
						String spacename = WebPacketUtil.getSpaceName(userSpace
								.getSpaceNo()); // 根据phonespaceid获取spacename
						holder.tvScenename.setText(spacename+"/"+theme.getThemeName());
					} else {
						Device devdto=new DevdtoDao(SceneModelActivity.this).findDevByDeviceNoAndGatewayNo(deviceNo, SystemValue.gatewayid);
						String spacename="";
						if(devdto!=null){
							 spacename = WebPacketUtil.getSpaceName(devdto.getSpaceNo()); // 根据phonespaceid获取spacename	
					    }
						holder.tvScenename.setText(spacename+"/"+theme.getThemeName());
					   
					}
				}else{
					holder.tvScenename.setText(theme.getThemeName());
				}
				
				holder.imSetting.setVisibility(View.INVISIBLE);
				holder.tgSceneSwitch.setVisibility(View.VISIBLE);
				
				vPsotion = preferences.getInt("scenepoint", -1);       //获取已经启动的scenepoint
				
				//刷新情景当前点亮图标
				if (vPsotion==position) {
					holder.tgSceneSwitch.setChecked(true);
				}else {
					holder.tgSceneSwitch.setChecked(false);
				}
				
				sceneViewOnClick(holder,position); //列表中开关按键点击事件监听
				
				return view;
			}
   
			class ViewHolder {  
				@Bind(R.id.tv_list_scenename)  TextView tvScenename;
				@Bind(R.id.im_setting)  ImageView imSetting;
				@Bind(R.id.tg_scene_switch)  ToggleButton tgSceneSwitch;
				
			    public ViewHolder(View view) {
			    	ButterKnife.bind(this,view);
			    }
			  }
			
			 /**
			  * 情景启动按钮点击事件
			  * @param holder
			  * @param position
			  */
			 private void sceneViewOnClick(ViewHolder holder, final int position) {
				   holder.tgSceneSwitch.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						 Theme theme=themeList.get(position);
						 
							//填充当前点击位置填充全局变量
						 vPsotion=position;  
						 editor.putInt("scenepoint", position);
				         editor.commit(); 
				         
				         sceneAdpter.notifyDataSetChanged();
				         
				 		//方案一：发送情景控制报文到手机
				   			ThemeData themeData=new ThemeData();
				    		themeData.setThemeNo(theme.getThemeNo());
				    		themeData.setThemeState(theme.getThemeState());
				    		
				    		SocketPacket socketPacket=WebPacketUtil.sceneControl2Packet(themeData);
				    		sentCmdByServerOrGateway(socketPacket);  //判断并通过内网或外网发送
				    		
				    		
				    		if (NetValue.netFlag==NetValue.INTRANET) {
//				    			List<APPThemeMusic> list=new APPThemeMusicDao(SceneModelActivity.this).GetAppThemeMusicListByThemeNo(theme.getThemeNo());
				    			MusicOrder musicOrder=new MusicOrder();
				    			musicOrder.setBz(theme.getThemeNo());
				    			musicOrder.setOrder("11");			//11播放情景音乐
//				    			try {
//				    				if (list.get(0).getStyle().equals("1")) {
//				    					musicOrder.setOrder("1");		//1暂停情景音乐
//				    				}
//								} catch (Exception e) {
//									System.err.println(e);
//								}
				    			musicOrder.setSongName("");
				    			musicOrder.setStyle("");
				    			musicOrder.setWgid(SystemValue.gatewayid);
				    			Log.i("insidemusic","该情景的音乐："+musicOrder.toString());
				    			sendSocket(musicOrder);
							}
				  	     
						}
				});
				 
				}
		}
		
		
		/**
		 * 根据网络类型从内网或者外网发送
		 * @param socketPacket
		 */
		private void sentCmdByServerOrGateway(SocketPacket socketPacket) {
			switch (NetValue.netFlag) {
			case NetValue.OUTERNET: // 外网
				//将命令封装为字符串发送到服务器
				byte[] sentBytes=WebPacketUtil.packetToByteStream(socketPacket);
				sendCmdToServer(sentBytes,0); // 发送到服务器的命令串

				break;
			case NetValue.INTRANET: // 内网
			
				devService.sentPacket(socketPacket); // 发送请求认证报文到网关
				break;
			}
	  
		}
		

			
//	   //连续执行情景线程
//		Handler timerhandler=new Handler();  
//		Runnable timerrunnable=new Runnable() {  
//		    @Override  
//		    public void run() {  
//		    	themeExecuteLoop();
//		    	try {
//					Thread.sleep(500); //每隔2秒执行一个情景状态
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//		    }  
//		};
//		/******循环发送情景表中的关联设备状态*******/
//		private void themeExecuteLoop() {
//			
//	        if(j<themeDevicelist.size()){
//	        	ThemeDevice themedevice=themeDevicelist.get(j);
//	        	String gatewayNo=SystemValue.gatewayid;
//	        	String deviceNo=themedevice.getDeviceNo();
//	            Device devdto=new DevdtoDao(SceneModelActivity.this).findDevByDeviceNoAndGatewayNo(deviceNo,gatewayNo);
//	            if (devdto!=null) {
//	            	 devdto.setDeviceStateCmd(themedevice.getDeviceStateCmd());
//	            	 SocketPacket devPacket=WebPacketUtil.devConvertToPacket(devdto);
//	  	             devService.sentPacket(devPacket);   //发送联动设备状态
//	  	             //更新本地数据库的设备状态，不等底层返回
//	  	           new DevdtoDao(null).updateDevStateByDeviceNo(devdto);
//				}
//	            timerhandler.post(timerrunnable);
//			    j++;
//			    System.out.println("====当前执行情景的第========"+j);
//		   }else {
//				dismissLoadingDialog();
//		  }
//		}
//		

		   
		   /***退出系统***/
		    @OnClick(R.id.tv_head_submit)
		    public void systemExit(){
		    	initExitDialog();
		    }
		    /***返回***/
		    @OnClick(R.id.tv_head_back)
		    public void back(){
		    	Intent intent=new Intent(SceneModelActivity.this,HomeActivity.class);
				startActivity(intent);		
				finish();
		    }
		    
		    /***空间***/
		    @OnCheckedChanged(R.id.rb_navi_space)
		    public void  spaceDeviceShow(){
		    	Intent intent=new Intent(SceneModelActivity.this,SpaceDevicesActivity.class);
				startActivity(intent);		
				finish();
		    }
		    
		    
		    /***网络切换***/
			@OnClick(R.id.tv_bottom_network)
			public void networkSwitchClick(){
				
				netWorkSwitch(devService,tvbttomNetwork);  //切换网络状态
			}
			
			/***防区管理***/
			@OnCheckedChanged(R.id.rb_navi_alert)
			public void DefenceAreaClick(){
				Intent sceneIntent=new Intent(SceneModelActivity.this,DefenceAreaActivity.class);
				startActivity(sceneIntent);		
				finish();
			}
		    /***系统设置***/
			 @OnCheckedChanged(R.id.rb_navi_set)
		    public void  systemSet(){
		    	Intent intent=new Intent(SceneModelActivity.this,SetActivity.class);
				startActivity(intent);		
				finish();
		    }
		

		@Override  
	    protected void onDestroy() {  
			unbindService(devconn);
	        super.onDestroy(); //注意先后  
	    }  

		/**
		 * @Description:建立socket
		 * @param IP
		 * */
		private void connectSocket(){
			System.out.println("MusicMainActivity        通过socket连接七寸屏!");
			if (SystemValue.MUSIC_SCREEN_IP==null || SystemValue.MUSIC_SCREEN_IP.equals("")) {
				Toast.makeText(SceneModelActivity.this, "请检查七寸屏是否已经连上WIFI!", 2000).show();
			}
			Thread thread=new Thread(){
				
				public void run(){
					super.run();
					if (!socketStatus) {
						try {
							socket=new Socket(SystemValue.MUSIC_SCREEN_IP,8000);
//							socket=new Socket("192.168.0.108",8000);
							System.out.println("已经连接上七寸屏!"+SystemValue.MUSIC_SCREEN_IP);
							if (socket!=null) {
								socketStatus=true;
							}
							outputStream=socket.getOutputStream();
							inputStream=socket.getInputStream();
							new ServerThread(socket,inputStream).start();
							MusicOrder order=MusicUtil.GetMusicList();
							sendSocket(order);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
				
			};
			thread.start();
		}

		class ServerThread extends Thread{
			private Socket socket;
			private InputStream inputStream;
			private StringBuffer stringBuffer=SceneModelActivity.this.stringBuffer;
			
			public ServerThread(Socket socket,InputStream inputStream){
				this.socket=socket;
				this.inputStream=inputStream;
			}
			
			public void run() {
				int len;
				byte[] bytes=new byte[20];
				boolean isString=false;
				
				// 在这里需要明白一下什么时候其会等于 -1，其在输入流关闭时才会等于 -1，
				// 并不是数据读完了，再去读才会等于-1，数据读完了，最结果也就是读不到数据为0而已；
				if (stringBuffer.length()>0) {
					stringBuffer.delete(0, stringBuffer.length()-1);
				}
				try {
					while ((len = inputStream.read(bytes))!=-1) {
						for (int i = 0; i < len; i++) {
							if (bytes[i] != '\0') {
								stringBuffer.append( (char) bytes[i]);
							}else{
								isString=true;
								break;
							}
						}
						if (isString) {
							String aa=stringBuffer.toString();
							List<MusicSocket> musicSockets=MusicUtil.ToMusicSocketList(aa);
							mArrayList.clear();
							mArrayList=MusicUtil.ToMusicList(musicSockets);
							listems=MusicUtil.TolistMap(mArrayList);
							if (mArrayList.size()>0) {
								//  异步进程更新界面
								Message mesg=new Message();
								mesg.what=0x129;
								handler.sendMessage(mesg);
							}
							//刷新  清空stringBuffer
							Message msg=handler.obtainMessage();
							msg.what=1;
							msg.obj=stringBuffer;
							handler.sendMessage(msg);
							isString=false;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}

		/**
		 * @Description:socket发送音乐控制  MusicOrder 到七寸屏
		 * */
		public void sendSocket(MusicOrder musicOrder){
			//在后面加'\0'是为了在服务端方便我们解析
//			data=MusicUtil.ToMusicOrderSocketJson(musicOrder)+'\0';
			data=MusicUtil.ToMusicOrderSocketJson(musicOrder);
			Log.i("insidemusic","发送到七寸屏上的情景音乐数据 "+data);
			Thread thread=new Thread(){																																						
				
				@Override
				public void run() {
					super.run();
					if (socketStatus) {
						try {
							outputStream.write(data.getBytes());
							outputStream.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					
				}
				
			};
			thread.start();
		}
}


