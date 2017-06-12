package com.tuwa.smarthome.activity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.TimeTestActivity;
import com.tuwa.smarthome.dao.GateWayDao;
import com.tuwa.smarthome.dao.VersionDao;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.NetTool;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.VerifyUtils;
import com.tuwa.smarthome.util.WebPacketUtil;
import com.umeng.update.UmengUpdateAgent;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

public class SetActivity extends BaseActivity {     
	private  SocketService socketService ;
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
	
	private Handler serviceHandler;  
	
    @Bind(R.id.tv_head_back) TextView tvBack;
    @Bind(R.id.tv_head_title) TextView tvtitle;
    @Bind(R.id.tv_head_submit)  TextView tvExit;
    @Bind(R.id.bt_systemSet)  Button btGatewaySet;
    @Bind(R.id.bt_roomManager)  Button btSpaceManege;
    @Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		
		// Activity和service绑定
	   Intent service = new Intent(SetActivity.this,SocketService.class);
	   bindService(service, conn, Context.BIND_AUTO_CREATE);
	   
		// 获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getSharedPreferences("tuwa", Context.MODE_PRIVATE);
		editor = preferences.edit();
		
	   ButterKnife.bind(SetActivity.this);
		
		tvtitle.setText("设置");
		tvExit.setText("退出");
		
		//初始化显示网络状态
		if (NetValue.netFlag == NetValue.INTRANET) {
			tvbttomNetwork.setText("本地");   //任务栏显示网络状态
		}else {
			tvbttomNetwork.setText("远程");    //任务栏显示网络状态
		}
		
	}
	
	//获取SocketService实例对象
	  ServiceConnection conn = new ServiceConnection() {  
	        @Override  
	        public void onServiceDisconnected(ComponentName name) {  
	              
	        }  
	          
	        @Override  
	        public void onServiceConnected(ComponentName name, IBinder service) {  
	            //返回一个SocketService对象  
	            socketService = ((SocketService.SocketBinder)service).getService();  
	            
	        	socketService.callSocket(new SocketCallBack(){
	    			@Override 
	    			public void callBack(TranObject tranObject) {
	    				
	    				switch (tranObject.getTranType()) {
	    				case NETMSG:   
	    					int netstatue = (Integer) tranObject.getObject();
							if (NetValue.NONET == netstatue) { // 本地连接失败
								if(!NetValue.autoFlag){
									ToastUtils.showToast(SetActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
								}
								
								NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("远程");
							}else if(NetValue.INTRANET == netstatue){
								tvbttomNetwork.setText("本地");
							}
							break;	
	    				case DEVMSG:
	    					SocketPacket socketPacket=(SocketPacket) tranObject.getObject();
	    					Short devtype=socketPacket.getDevType();
	    				    if (devtype==NetValue.DEV_LOCAL_PHONE) {     //手机认证类型反馈 
	    				    	if (socketPacket.getData().equals("0")) {    //验证成功返回0
	    					    	//通过验证，跳转到主界面
	    				            
	    				            SocketPacket devAllPacket=WebPacketUtil.getDevAllStatePacket(SystemValue.gatewayid);
	    							socketService.sentPacket(devAllPacket);   //发送请求所有设备状态
	    						    NetValue.netFlag=NetValue.INTRANET;  //保存当前的网络为内网

	    						 }else {
	    							 dismissLoadingDialog();
	    							 ToastUtils.showToast(SetActivity.this,"本地验证失败，请检查网关连接！", 1000);
	    						 }
	    					}
	    				default:
	    					break;
	    				}
	    			}
	    		});
	        }  
	    }; 

	//网关管理   
	@OnClick(R.id.bt_gatewaySet)
	public void gatewaySet(){
		
//		  new VersionDao(SetActivity.this).deleteVersionByVersionType(SystemValue.VERSION_GATEWAY);	
		
		Intent intent=new Intent(SetActivity.this,GatewayManegeActivity.class);
		startActivity(intent);	
		//设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
		finish();
	}
	//空间管理
	@OnClick(R.id.bt_roomManager)
	public void spaceManege(){
		
		if(!(VerifyUtils.isEmpty(SystemValue.gatewayid))){
			Intent intent=new Intent(SetActivity.this,SpaceManegeActivity.class);
			startActivity(intent);	
			//设置切换动画，从右边进入，左边退出
	        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
		}else{
			ToastUtils.showToast(SetActivity.this, "请先绑定网关！", 2000);
		}
		
	}
	//联动设置
	@OnClick(R.id.bt_linkageSet)
	public void linkManege(){
		if(!(VerifyUtils.isEmpty(SystemValue.gatewayid))){
			Intent intent=new Intent(SetActivity.this,DeviceSensorActivity.class);
			startActivity(intent);	
			//设置切换动画，从右边进入，左边退出
	        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
		}else{
			ToastUtils.showToast(SetActivity.this, "请先绑定网关！", 2000);
		}
	}
	//情景管理
	@OnClick(R.id.bt_sceneManager)
	public void sceneManege(){
		if(!(VerifyUtils.isEmpty(SystemValue.gatewayid))){
			
			Intent sceneIntent=new Intent(SetActivity.this,SceneManegeActivity.class);
			startActivity(sceneIntent);	
			//设置切换动画，从右边进入，左边退出
	        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
	 
		}else{
			ToastUtils.showToast(SetActivity.this, "请先绑定网关！", 2000);
		}
	}

	//产品管理
	@OnClick(R.id.bt_productManager)
	public void deviceManege(){
		if(!(VerifyUtils.isEmpty(SystemValue.gatewayid))){
			//清空版本信息
//	        new VersionDao(SetActivity.this).deleteVersionByVersionType(SystemValue.VERSION_DEVICE);		
			
			Intent intent=new Intent(SetActivity.this,DeviceManegeActivity.class);
			startActivity(intent);	
			//设置切换动画，从右边进入，左边退出
	        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
	 		
		}else{
			ToastUtils.showToast(SetActivity.this, "请先绑定网关！", 2000);
		}
	}
	//定时管理
	@OnClick(R.id.bt_timerSet)
	public void timeTaskManege(){
		if(!(VerifyUtils.isEmpty(SystemValue.gatewayid))){
			Intent intent=new Intent(SetActivity.this,DeviceTimerSetActivity.class);
//			Intent intent=new Intent(SetActivity.this,TimeTestActivity.class);
			startActivity(intent);	
		}else{
			ToastUtils.showToast(SetActivity.this, "请先绑定网关！", 2000);
		}
	}
	
	@OnClick(R.id.bt_systemSet)
	public void systemSet(){
		Intent intent=new Intent(SetActivity.this,SystemSetActivity.class);
		startActivity(intent);	
		//设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
	}
	
	/***返回***/
	@OnClick(R.id.tv_head_back)
	public void back(){
		Intent intent=new Intent(SetActivity.this,HomeActivity.class);
		startActivity(intent);		
		finish();
	}
	/***退出系统***/
    @OnClick(R.id.tv_head_submit)
    public void systemExit(){
    	initExitDialog();
    }
    
    /***空间***/
    @OnCheckedChanged(R.id.rb_navi_space)
    public void  spaceDeviceShow(){
    	Intent intent=new Intent(SetActivity.this,SpaceDevicesActivity.class);
		startActivity(intent);		
		finish();
    }
    /***情景模式***/
    @OnCheckedChanged(R.id.rb_navi_scene)
	public void sceneMode(){
		
		Intent sceneIntent=new Intent(SetActivity.this,SceneModelActivity.class);
		startActivity(sceneIntent);		
		finish();
	}
    
    /***网络切换***/
	@OnClick(R.id.tv_bottom_network)
	public void networkSwitchClick(){
		
		netWorkSwitch(socketService,tvbttomNetwork);  //切换网络状态
	}
	
   /***防区管理***/
	@OnCheckedChanged(R.id.rb_navi_alert)
	public void DefenceAreaClick(){
		Intent sceneIntent=new Intent(SetActivity.this,DefenceAreaActivity.class);
		startActivity(sceneIntent);		
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
		unbindService(conn);
        super.onDestroy(); //注意先后  
    } 
	

}
