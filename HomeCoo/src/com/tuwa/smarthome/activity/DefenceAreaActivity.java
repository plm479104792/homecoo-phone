package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.WebService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.network.WebService.WebServiceCallBack;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class DefenceAreaActivity extends BaseActivity {
	   //activity绑定service
		private  SocketService devService ;
		
		@Bind(R.id.tv_head_submit)  TextView tvExit;
		@Bind(R.id.tv_head_back) TextView tvBack;
		@Bind(R.id.tv_head_title) TextView tvTitle;
		@Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
		@Bind(R.id.tv_indoor_bufang) ImageView tvIndoorBufang;
		@Bind(R.id.tv_indoor_chefang) ImageButton tvIndoorChefang;
		@Bind(R.id.tv_outdoor_bufang) ImageButton tvOutdoorBufang;
		@Bind(R.id.tv_outdoor_chefang) ImageButton tvOutdoorChefang;
		
		 @Bind(R.id.rg_tab_navi) RadioGroup rg_navi_tab;
		
		private Handler sentHandler;
		private List<Theme>  themelist=new ArrayList<Theme>();
		private List<Device> devicelist=new ArrayList<Device>();   //安防类设备列表
		private int j=1;     //正在执行的情景关联设备指针
		private int userid;
		private String alertdata;
		
		   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defence_area);
		ButterKnife.bind(DefenceAreaActivity.this);//注解工具声明
		
		// Activity和service绑定2
 		Intent service = new Intent(DefenceAreaActivity.this,SocketService.class);
 		bindService(service, devconn, Context.BIND_AUTO_CREATE);
		
 		userid=SystemValue.userid;
 		
		tvExit.setText("退出");
		tvTitle.setText("防区管理");
		
		 //循环发送布防报文
		  HandlerThread sentThread = new HandlerThread("SentHandlerThread");
	      sentThread.start();//创建一个HandlerThread并启动它
	      sentHandler=new Handler(sentThread.getLooper());  //发送数据线程
		
		//网络切换的标志
		if (NetValue.netFlag==NetValue.INTRANET) {
			tvbttomNetwork.setText("本地");   //任务栏显示网络状态
		}else if (NetValue.netFlag==NetValue.OUTERNET) {
			tvbttomNetwork.setText("远程");    //任务栏显示网络状态
		} 
		
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
	    					int netstatue=(Integer) tranObject.getObject();
	    					if ((NetValue.NONET==netstatue)) {  //本地连接失败
	    						if(!NetValue.autoFlag){
	    						 ToastUtils.showToast(DefenceAreaActivity.this,"本地连接失败！", 1000);
	    						 NetValue.netFlag = NetValue.OUTERNET;
	    						}
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
	
	    
	    @OnClick(R.id.tv_indoor_bufang)
	    public void indoorBufangOnClick(){
	    
   			tvIndoorBufang.setImageDrawable(getResources().getDrawable(R.drawable.area_inbufang_over));
	    	tvIndoorChefang.setImageDrawable(getResources().getDrawable(R.drawable.area_indoor_chefang));
            
	    	//初始化显示网络状态
			showLoadingDialog("室内布防"+"正在执行...");//内网情况下提示用户
			alertdata="11";
			executeAlertThemeByAlertType(1);  

	    }
	    
	    @OnClick(R.id.tv_indoor_chefang)
	    public void indoorChefangOnClick(){
	    	
   			tvIndoorBufang.setImageDrawable(getResources().getDrawable(R.drawable.area_indoor_bufang));
	    	tvIndoorChefang.setImageDrawable(getResources().getDrawable(R.drawable.area_inchefang_over));
	    	
	    	//初始化显示网络状态
			showLoadingDialog("室内撤防"+"正在执行...");//内网情况下提示用户
			alertdata="10";
			executeAlertThemeByAlertType(1);

	    }
	    //室外布防
	    @OnClick(R.id.tv_outdoor_bufang)
	    public void outdoorBufangOnClick(){
	    	
	    	tvOutdoorBufang.setImageDrawable(getResources().getDrawable(R.drawable.area_outbuffang_over));
	    	tvOutdoorChefang.setImageDrawable(getResources().getDrawable(R.drawable.area_outdoor_chefang));
	    	
	    	//初始化显示网络状态

			showLoadingDialog("室外布防"+"正在执行...");
			alertdata="11";
			executeAlertThemeByAlertType(2);

	    }
	    //室外撤防
	    @OnClick(R.id.tv_outdoor_chefang)
	    public void outdoorChefangOnClick(){
	    	
	    	tvOutdoorBufang.setImageDrawable(getResources().getDrawable(R.drawable.area_outdoor_bufang));
	    	tvOutdoorChefang.setImageDrawable(getResources().getDrawable(R.drawable.area_outchefang_over));
	    	//初始化显示网络状态

			showLoadingDialog("室外撤防"+"正在执行..."); //内网情况下提示用户
			alertdata="10";
			executeAlertThemeByAlertType(2);

	    }
	    
	    /**
		 * 通过内网执行情景
		 * @param theme
		 */
		private void executeAlertThemeByAlertType(int alertypeId) {
						
			//根据网关编号查找传感器类设备
			devicelist=new DevdtoDao(DefenceAreaActivity.this).findSensorDevicesByAlertypeId(alertypeId);
			
            j=0;  //每当新情景，指针重置为0(list从0开始)
            sentHandler.post(sentPacketRunnable);//将线程post到Handler中
						
		}
		
		  Runnable sentPacketRunnable = new Runnable() {
			
			@Override
			public void run() {
				themeExecuteLoop(alertdata);
		    	try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		/******循环发送情景表中的关联设备状态*******/
		private void themeExecuteLoop(String devstate) {
			
	        if(j<devicelist.size()){

	        	String deviceNo=devicelist.get(j).getDeviceNo();
	            Device devdto=new DevdtoDao(DefenceAreaActivity.this).findDevByDeviceNoAndGatewayNo(deviceNo,SystemValue.gatewayid);
	            if (devdto!=null) {
	            	devdto.setDeviceStateCmd(devstate);
	  	            SocketPacket devPacket=WebPacketUtil.devConvertToPacket(devdto);
	  	            
	  	            sentCmdByServerOrGateway(devPacket);  //内网或外网发送
	  	            
	  	          //更新本地数据库的设备状态，不等底层返回
		  	        new DevdtoDao(null).updateDevStateByDeviceNo(devdto);
				}
	          
			    j++;
			    sentHandler.post(sentPacketRunnable);//将线程post到Handler中
			   
		   }else {
			   sentHandler.removeCallbacks(sentPacketRunnable);
				dismissLoadingDialog();
		   }
		}
		
		
		private void sentCmdByServerOrGateway(SocketPacket socketPacket) {
			switch (NetValue.netFlag) {
			case NetValue.OUTERNET: // 外网
				//将命令封装为字符串发送到服务器
				byte[] sentBytes=WebPacketUtil.packetToByteStream(socketPacket);
				sendCmdToServer(sentBytes,0);   //发送到服务器的命令串
				break;
			case NetValue.INTRANET: // 内网
			    devService.sentPacket(socketPacket);   //发送请求认证报文到网关
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
	    	Intent intent=new Intent(DefenceAreaActivity.this,HomeActivity.class);
			startActivity(intent);		
			finish();
	    }
	    
	    /***空间***/
	    @OnCheckedChanged(R.id.rb_navi_space)
	    public void  spaceDeviceShow(){
	    	Intent intent=new Intent(DefenceAreaActivity.this,SpaceDevicesActivity.class);
			startActivity(intent);		
			finish();
	    }
	    /***情景模式***/
	    @OnCheckedChanged(R.id.rb_navi_scene)
		public void sceneMode(){
			Intent sceneIntent=new Intent(DefenceAreaActivity.this,SceneModelActivity.class);
			startActivity(sceneIntent);		
			finish();
		}
	    
	    /***网络切换***/
		@OnClick(R.id.tv_bottom_network)
		public void networkSwitchClick(){
			
			netWorkSwitch(devService,tvbttomNetwork);  //切换网络状态
		}
	    /***系统设置***/
		  @OnCheckedChanged(R.id.rb_navi_set)
	    public void  systemSet(){
	    	Intent intent=new Intent(DefenceAreaActivity.this,SetActivity.class);
			startActivity(intent);		
			finish();
	    }
		    

		@Override
		protected void initViews() {
			// TODO Auto-generated method stub
			
		}
		@Override
		protected void initDatas() {
			// TODO Auto-generated method stub
			
		}
		@Override  
	    protected void onDestroy() {  
			unbindService(devconn);
	        super.onDestroy(); //注意先后  
	    }  
}
