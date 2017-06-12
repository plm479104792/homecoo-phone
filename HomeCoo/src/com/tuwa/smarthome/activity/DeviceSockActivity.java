package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.DeviceSockActivity.SocksAdapter.ViewHolder;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;


public class DeviceSockActivity extends BaseActivity implements AdpterOnItemClick{
	  //activity绑定service1
	  private  SocketService devService ;
		
	  @Bind(R.id.tv_head_submit)  TextView tvExit;
	  @Bind(R.id.tv_head_back) TextView tvBack;
	  @Bind(R.id.tv_head_title) TextView tvTitle;
	  @Bind(R.id.gv_sock_list) GridView gvDevices;
	  @Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
	  
	  @Bind(R.id.rg_tab_navi) RadioGroup rg_navi_tab;
	  
	  private List<Device>  devlist=new ArrayList<Device>();
	  private SocksAdapter deviceAdpter=null;
	  
	  final char ON=1+'0';  //字符开
	  final char OFF=0+'0';  //字符关
	  private static char [] strStaArr=new char[4];   //字符数组代表多路开关状态
	  private boolean tgbtn1_checked;
	   /*辅线程动态刷新页面*/
      Handler handler=new Handler(){
	   	 @Override
	   	 public void handleMessage(Message msg){
	   		 switch(msg.what){
	   		 case 0x129:
	   			if(devlist!=null){
		   			deviceAdpter=new SocksAdapter();
		   			gvDevices.setAdapter(deviceAdpter);
	   			}
	   		 }
	   	 }
	   };
	   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_sock);
		ButterKnife.bind(DeviceSockActivity.this);//注解工具声明
	    // Activity和service绑定2
	    Intent service = new Intent(DeviceSockActivity.this,SocketService.class);
	    bindService(service, devconn, Context.BIND_AUTO_CREATE);
		
	    tvExit.setText("退出");
		tvTitle.setText("插座");	
		
		initViews();
		initDatas();
		
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
									ToastUtils.showToast(DeviceSockActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
								}
								
								NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("远程");
							}else if(NetValue.INTRANET == netstatue){
								tvbttomNetwork.setText("本地");
							}
							break;	    
	    				case DEVMSG:
	    					
    					//根据网关号从数据库加载相应设备
    					devlist=new DevdtoDao(DeviceSockActivity.this)
    					                       .switchListBygwId(SystemValue.gatewayid,SystemValue.SOCK);
	    					
	   					  //  异步进程更新界面
	   		   		      Message msg=new Message();
	   		              msg.what=0x129;
	   		              handler.sendMessage(msg);
	    					
	    				default:
	    					break;
	    				}
	    			}
				});      	
	        }  
	    };
	    
	
	  //list数据适配器
	  public  class SocksAdapter extends BaseAdapter{
			private AdpterOnItemClick myAdpterOnclick;

			public void onListener(AdpterOnItemClick listener) {
				this.myAdpterOnclick = listener;
			}
			
			
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return devlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return devlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return -1;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			final ViewHolder holder;
			if (view != null) {
			      holder = (ViewHolder) view.getTag();
			    } else {
			      view = View.inflate(DeviceSockActivity.this,R.layout.item_dev_sock,null);
			      holder = new ViewHolder(view);
			      view.setTag(holder);
			    }
			Device devdto=devlist.get(position);
			int devtype=devdto.getDeviceTypeId();
			
			initDeviceNameAndSite(holder.tvDevSite,holder.tvDevName,devdto);//初始化房间名称和位置
			 
			//设备状态串转换为状态字符数组
			String mdevState=devdto.getDeviceStateCmd();
			strStaArr=mdevState.toCharArray();
			
			final int fpostion = position;
			final ViewHolder fHolder = holder;
			
			showSockViewByDevtype(holder,devtype);  //根据设备类型显示状态
			
//			switchViewOnClick(holder,position); //列表中开关按键点击事件监听
			
			
			holder.tgSock.setOnClickListener(new ClickEvent() {
				@Override
				public void singleClick(View v) {
//					if (myAdpterOnclick != null) {
//						int which = v.getId();
//						myAdpterOnclick.onApterClick(which, fpostion);
						if (!holder.tgSock.isChecked()) { // 当前状态为开，点击的时候发送off
							cmdControl(fpostion, 1, OFF);
						} else {
							cmdControl(fpostion, 1, ON);
						}
//					} else {
//						System.out.println("===myAdpterOnclick为空====");
//					}
				}
			});
			return view;
		}
   
		class ViewHolder {    
			@Bind(R.id.tv_switch_devSite)  TextView tvDevSite;
			@Bind(R.id.tv_switch_devtypeName)  TextView tvDevName;
			@Bind(R.id.tg_sock)  ToggleButton tgSock;
		
		    public ViewHolder(View view) {
		    	ButterKnife.bind(this,view);
		    }
		  }
	   }
	  
	  /**
	  * 根据设备类型显示按钮
	  * @param holder
	  * @param devtype
	  */
	  private void showSockViewByDevtype(ViewHolder holder,int devtype) {
		  holder.tgSock.setVisibility(View.VISIBLE);
		  
		  holder.tgSock.setVisibility(View.VISIBLE);
			if(strStaArr[0]==ON){
      		holder.tgSock.setChecked(true);
			}
		}

		  /**
		 * 列表中开关按键点击事件监听
		 * @param holder
		 * @param position   按键对应的开关在devlist中位置
		 */
//		private void switchViewOnClick(final ViewHolder holder,final int position) {
//			
//			 holder.tgSock.setOnCheckedChangeListener(new CheckClickEvent() {
//					@Override
//					public void singleClick(View v, boolean isChecked) {
//						
//						if (!isChecked) { //当前状态为开，点击的时候发送off
//							cmdControl(position,1,OFF);
//						}else {
//							cmdControl(position,1,ON);
//						}
//					}
//				});
//		}  
		
	
		
		  /**
	     * 发送命令函数
	     * @param position   当前点击的开关在devlist中位置
	     * @param switchid   多路开关中当前点击的位置
	     * @param ch         相应的开关命令
	     */
	public void cmdControl(int position, int switchid, char ch) {
		Device device = devlist.get(position);
		String strCmd = null; // 待发送的命令

		String sDevState = device.getDeviceStateCmd();
		strStaArr = sDevState.toCharArray();
		if (switchid == 1) {
			strStaArr[0] = ch;
		}
		strCmd = new String(strStaArr);

		device.setDeviceStateCmd(strCmd); // 注意更改后的设备状态先加载到本地
		// 更新device最新状态到本地设备数据库（3）
		new DevdtoDao(DeviceSockActivity.this).updateDevStateByDeviceNo(device);
		// 将命令转换为报文
		SocketPacket devPacket = WebPacketUtil.devConvertToPacket(device);

		switch (NetValue.netFlag) {
		case NetValue.OUTERNET: // 外网
			// 将命令封装为字符串发送到服务器
			byte[] sentBytes = WebPacketUtil.packetToByteStream(devPacket);
			sendCmdToServer(sentBytes,0); // 发送到服务器的命令串
			break;
		case NetValue.INTRANET: // 内网
			devService.sentPacket(devPacket); // 发送请求认证报文到网关
			break;
		}
	}
		
		
	
	  
		/***退出系统***/
	    @OnClick(R.id.tv_head_submit)
	    public void systemExit(){
	    	initExitDialog();
	    }
	    /***返回***/
	    @OnClick(R.id.tv_head_back)
	    public void back(){
	    	Intent intent=new Intent(DeviceSockActivity.this,HomeActivity.class);
			startActivity(intent);		
			finish();
	    }
	    
	    /***空间***/
	    @OnCheckedChanged(R.id.rb_navi_space)
	    public void  spaceDeviceShow(){
	    	Intent intent=new Intent(DeviceSockActivity.this,SpaceDevicesActivity.class);
			startActivity(intent);		
			finish();
	    }
	    /***情景模式***/
	    @OnCheckedChanged(R.id.rb_navi_scene)
		public void sceneMode(){
			Intent sceneIntent=new Intent(DeviceSockActivity.this,SceneModelActivity.class);
			startActivity(sceneIntent);		
			finish();
		}
	    
	    /***网络切换***/
		@OnClick(R.id.tv_bottom_network)
		public void networkSwitchClick(){
			
			 netWorkSwitch(devService,tvbttomNetwork);
		}
		
		/***防区管理***/
		@OnCheckedChanged(R.id.rb_navi_alert)
		public void DefenceAreaClick(){
			Intent sceneIntent=new Intent(DeviceSockActivity.this,DefenceAreaActivity.class);
			startActivity(sceneIntent);		
			finish();
		}
		
	    /***系统设置***/
		@OnCheckedChanged(R.id.rb_navi_set)
	    public void  systemSet(){
	    	Intent intent=new Intent(DeviceSockActivity.this,SetActivity.class);
			startActivity(intent);		
			finish();
	    }
	    
 
	@Override
	protected void initViews() {
		if (NetValue.netFlag == NetValue.INTRANET) {
			tvbttomNetwork.setText("本地"); // 任务栏显示网络状态
		
		} else if (NetValue.netFlag == NetValue.OUTERNET) {
			tvbttomNetwork.setText("远程"); // 任务栏显示网络状态
		    //访问服务器线程  //【定时线程步骤2】
//			timerhandler.post(timerrunnable); 
		}
	}

	@Override
	protected void initDatas() {
		//根据网关号从数据库加载相应设备
		devlist=new DevdtoDao(DeviceSockActivity.this)
		                       .switchListBygwId(SystemValue.gatewayid,SystemValue.SOCK);
		//  异步进程更新界面
		Message msg=new Message();
        msg.what=0x129;
        handler.sendMessage(msg);
	}
	  @Override  
	    protected void onDestroy() {  
//		    timerhandler.removeCallbacks(timerrunnable);  //停止定时器线程
			unbindService(devconn);
	        super.onDestroy(); //注意先后  
	    }

	@Override
	public void onApterClick(int which, int postion) {
		// TODO Auto-generated method stub
		
	}  
}
