package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.DeviceWeiKongActivity.SocksAdapter.ViewHolder;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DeviceWeiKongActivity extends BaseActivity {

	 //activity绑定service1
	  private  SocketService devService ;
		
	  @Bind(R.id.tv_head_submit)  TextView tvExit;
	  @Bind(R.id.tv_head_back) TextView tvBack;
	  @Bind(R.id.tv_head_title) TextView tvTitle;
	  @Bind(R.id.gv_sock_list) GridView gvDevices;
	  @Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
	  
	  private List<Device>  devlist=new ArrayList<Device>();
	  private SocksAdapter deviceAdpter=null;
	  
	  final char ON=1+'0';  //字符开
	  final char OFF=0+'0';  //字符关
	  private static char [] strStaArr=new char[4];   //字符数组代表多路开关状态
	  
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
	  
	   			break;
			 case 0x001: // 内网局部刷新
				int index = msg.arg1;
				Device devdto = (Device) msg.obj;
				int firstVisible = gvDevices.getFirstVisiblePosition();
				int lastVisible = gvDevices.getLastVisiblePosition();
				if (index >= firstVisible && index <= lastVisible) {
					// 获取到index对应的holder
					ViewHolder holder = (ViewHolder) (gvDevices.getChildAt(index
							- firstVisible).getTag());
					int devtypeId=devdto.getDeviceTypeId();
					String devState=devdto.getDeviceStateCmd();
					showSockViewByDevtype(holder,devtypeId,devState);  //根据设备类型显示状态
				}
				break;
	   		 }
	   	 }
	   };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_sock);
		ButterKnife.bind(DeviceWeiKongActivity.this);//注解工具声明
	    // Activity和service绑定2
	    Intent service = new Intent(DeviceWeiKongActivity.this,SocketService.class);
	    bindService(service, devconn, Context.BIND_AUTO_CREATE);
	    
	    Intent intent=getIntent();
	    operatorType = intent.getStringExtra("operator_type");  //通知栏启动的返回直接结束
		
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
									ToastUtils.showToast(DeviceWeiKongActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
								}
								
								NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("远程");
							}else if(NetValue.INTRANET == netstatue){
								tvbttomNetwork.setText("本地");
							}
							break;	    
	    				case DEVMSG:
//	    					SocketPacket socketPacket=(SocketPacket) tranObject.getObject();
//	    					Short devtype=socketPacket.getDevType();
//	    				    if (devtype==NetValue.DEV_LOCAL_PHONE) {     //手机认证类型反馈 
//	    				    	if (socketPacket.getData().equals("0")) {    //验证成功返回0
//	    							dismissLoadingDialog();
//	    					    	//通过验证，跳转到主界面
//	    				            
//	    						    NetValue.netFlag=NetValue.INTRANET;  //保存当前的网络为内网
//
//	    						 }else {
//	    							 dismissLoadingDialog();
//	    							 showCustomToast("本地验证失败，请检查网关连接！");   //用户名对应的网关id错误
//	    						}
//	    					}else {
//	    						  //根据网关号从数据库加载相应设备
//	     					     devlist=new DevdtoDao(DeviceWeiKongActivity.this)
//	     					                       .switchListBygwId(SystemValue.gatewayid,SystemValue.weikong2);
//	   	    					
//	   	   					  //  异步进程更新界面
//	   	   		   		      Message msg=new Message();
//	   	   		              msg.what=0x129;
//	   	   		              handler.sendMessage(msg);
//							}
	    				    
	    					SocketPacket socketPacket = (SocketPacket) tranObject.getObject();
							String devid = socketPacket.getDevId();
							String devstate =(String)socketPacket.getData();
							
							Device devDTO = null;
							int vposition = -1;
							// 进行数据对比获取对应数据在list中的位置
							for (int j = 0; j < devlist.size(); j++) {
								String strdevid = devlist.get(j).getDeviceNo();
								if (devid.equalsIgnoreCase(strdevid)) {
									devDTO = devlist.get(j);
									devDTO.setDeviceStateCmd(devstate);
									vposition = j;
									// 异步进程更新界面
									Message msg = new Message();
									msg.what = 0x001;
									msg.arg1 = vposition;
									msg.obj = devDTO;
									handler.sendMessage(msg);
									break;
								}
							}
							break;
  					  
	    				default:
	    					break;
	    				}
	    			}
	        		
				});      	
	        }  
	    };

	private String operatorType;
	    
	  
	
	  //list数据适配器
	  public  class SocksAdapter extends BaseAdapter{
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
			ViewHolder holder;
			if (view != null) {
			      holder = (ViewHolder) view.getTag();
			    } else {
			      view = View.inflate(DeviceWeiKongActivity.this,R.layout.item_dev_sock,null);
			      holder = new ViewHolder(view);
			      view.setTag(holder);
			    }
			Device devdto=devlist.get(position);
			int devtype=devdto.getDeviceTypeId();
			
			initDeviceNameAndSite(holder.tvDevSite,holder.tvDevName,devdto);//初始化房间名称和位置
			
			//设备状态串转换为状态字符数组
			String mdevState=devdto.getDeviceStateCmd();
			strStaArr=mdevState.toCharArray();
			
			showSockViewByDevtype(holder,devtype,mdevState);  //根据设备类型显示状态
			
			switchViewOnClick(holder,position); //列表中开关按键点击事件监听
			
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
	  private void showSockViewByDevtype(ViewHolder holder,int devtype,String devstate) {
		  holder.tgSock.setVisibility(View.VISIBLE);
		  switch (devtype) {
		  case 110:     //门磁报警器
				if (devstate.equals("11000000")||devstate.equals("11")
						||devstate.equals("01000000")) {  //入网，布防，报警
					 holder.tgSock.setChecked(true);
				}else {                       //显示撤防状态为11
					 holder.tgSock.setChecked(false);
				}
				break;
		 case 113:     //红外报警器
				if (devstate.equals("11000000")||devstate.equals("11")
						||devstate.equals("01000000")) {  //zigbee入网上报11代表布防，手动布防11
				 holder.tgSock.setChecked(true);
//			}else {                       //显示布防状态为11
//				 holder.tgSock.setChecked(false);
//			}
			}else if(devstate.equals("10000000")||devstate.equals("10")) {                       //显示布防状态为11
				 holder.tgSock.setChecked(false);
			}
			break;
		 case 115:     //燃气报警器
				if (devstate.equals("11000000")||devstate.equals("11")
						||devstate.equals("01000000")) {  //zigbee入网上报11代表布防，手动布防11
				 holder.tgSock.setChecked(true);
//			}else {                       //显示布防状态为11
//				 holder.tgSock.setChecked(false);
//			}
			}else if(devstate.equals("10000000")||devstate.equals("10")) {                       //显示布防状态为11
				 holder.tgSock.setChecked(false);
			}
			break;
		 case 118:     //烟感报警器
				if (devstate.equals("11000000")||devstate.equals("11")
						||devstate.equals("01000000")) {  //zigbee入网上报11代表布防，手动布防11
				 holder.tgSock.setChecked(true);
			}else {                       //显示布防状态为11
				 holder.tgSock.setChecked(false);
			}
			break;

		 case 51:    //风扇  51

			if(devstate.equals("1")){   
		  		  holder.tgSock.setChecked(true);
		    }else if(devstate.equals("0")){
		    	 holder.tgSock.setChecked(false);
		    }
			break;
		}
			
			
		}

		  /**
		 * 列表中开关按键点击事件监听
		 * @param holder
		 * @param position   按键对应的开关在devlist中位置
		 */
		private void switchViewOnClick(final ViewHolder holder,final int position) {
			
			holder.tgSock.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (holder.tgSock.isChecked()) {
						cmdControl(position,1,ON);
//						NetValue.isAcceptAlert=true;
//						Log.i("343", "手动布防+NetValue.isAcceptAlert"+NetValue.isAcceptAlert);
					}else {
						cmdControl(position,1,OFF);
//						NetValue.isAcceptAlert=false;   //不执行安防联动
//						Log.i("343", "手动撤防+NetValue.isAcceptAlert"+NetValue.isAcceptAlert);
					}
				}
			});
		}  
		
		/**
		 * 发送命令函数
		 * 
		 * @param position
		 *            当前点击的开关在devlist中位置
		 * @param switchid
		 *            多路开关中当前点击的位置
		 * @param ch
		 *            相应的开关命令
		 */
		public void cmdControl(int position, int switchid, char ch) {
			Device device = devlist.get(position);
			String sqlCmd = WebPacketUtil.convertCmdToSql(device, switchid, ch); // 控制的devstate转码到本地

			device.setDeviceStateCmd(sqlCmd); // 注意更改后的设备状态先加载到本地
			// //更新device最新状态到本地设备数据库（3）
			new DevdtoDao(DeviceWeiKongActivity.this).updateDevStateByDeviceNo(device);
			//将命令转换为报文
			SocketPacket devPacket = WebPacketUtil.devConvertToPacket(device);
			
			switch (NetValue.netFlag) {
			case NetValue.OUTERNET: // 外网
				//将命令封装为字符串发送到服务器
				byte[] sentBytes=WebPacketUtil.packetToByteStream(devPacket);
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
	    	if(operatorType.equals("notification")){
	    		finish();
	    	}else{
	    		Intent intent=new Intent(DeviceWeiKongActivity.this,HomeActivity.class);
				startActivity(intent);		
				finish();
	    	}
	    }
	    
	    /***空间***/
	    @OnCheckedChanged(R.id.rb_navi_space)
	    public void  spaceDeviceShow(){
	    	Intent intent=new Intent(DeviceWeiKongActivity.this,SpaceDevicesActivity.class);
			startActivity(intent);		
			finish();
	    }
	    /***情景模式***/
	    @OnCheckedChanged(R.id.rb_navi_scene)
		public void sceneMode(){
			Intent sceneIntent=new Intent(DeviceWeiKongActivity.this,SceneModelActivity.class);
			startActivity(sceneIntent);		
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
			Intent sceneIntent=new Intent(DeviceWeiKongActivity.this,DefenceAreaActivity.class);
			startActivity(sceneIntent);		
			finish();
		}
		
	    /***系统设置***/
		 @OnCheckedChanged(R.id.rb_navi_set)
	    public void  systemSet(){
	    	Intent intent=new Intent(DeviceWeiKongActivity.this,SetActivity.class);
			startActivity(intent);		
			finish();
	    }
	    
	    
	@Override
	protected void initViews() {
	    tvExit.setText("退出");
		tvTitle.setText("微控");	
		
		if (NetValue.netFlag==NetValue.INTRANET) {   //通过本地socket验证
			tvbttomNetwork.setText("本地");   //任务栏显示网络状态
		}else{
			tvbttomNetwork.setText("远程");   //任务栏显示网络状态
		}
		
	}


	  @Override  
	    protected void onDestroy() {  
			unbindService(devconn);
	        super.onDestroy(); //注意先后  
	    }

	@Override
	protected void initDatas() {
		//根据网关号从数据库加载相应设备
		List<Device> allDevList=new ArrayList<Device>();
		allDevList=new DevdtoDao(DeviceWeiKongActivity.this)
              .switchListBygwId(SystemValue.gatewayid,SystemValue.SENSOR);
		
		if(allDevList!=null){
			 devlist=WebPacketUtil.DeleteSensorFromDevicesAll(allDevList);
				
			    //  异步进程更新界面
			  Message msg=new Message();
		      msg.what=0x129;
		      handler.sendMessage(msg);
		}
	         
		
	}  
}

	

