package com.tuwa.smarthome.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.google.gson.Gson;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.AlarmMessageDao;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.entity.AlarmMessage;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.WebService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.network.WebService.WebServiceCallBack;
import com.tuwa.smarthome.util.DataConvertUtil;
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

public class MessageActivity extends BaseActivity {
	  //activity绑定service1
	  private  SocketService devService ;
		
	  @Bind(R.id.tv_head_submit)  TextView tvExit;
	  @Bind(R.id.tv_head_back) TextView tvBack;
	  @Bind(R.id.tv_head_title) TextView tvTitle;
	  @Bind(R.id.gv_sock_list) GridView gvDevices;
	  @Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
	  
	  @Bind(R.id.rg_tab_navi) RadioGroup rg_navi_tab;
	  
	  private List<AlarmMessage>  alarmMsglist=new ArrayList<AlarmMessage>();
	  private AlarmMsgAdapter alarmMsgAdpter=null;
	  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
	  
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
	   			alarmMsgAdpter=new AlarmMsgAdapter();
	   			gvDevices.setAdapter(alarmMsgAdpter);
	   		 }
	   	 }
	   };
	   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_sock);
		ButterKnife.bind(MessageActivity.this);//注解工具声明
	    // Activity和service绑定2
	    Intent service = new Intent(MessageActivity.this,SocketService.class);
	    bindService(service, devconn, Context.BIND_AUTO_CREATE);
		
	    gvDevices.setNumColumns(1);
	    
	    tvExit.setText("退出");
		tvTitle.setText("消息");	
		

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
									ToastUtils.showToast(MessageActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
								}
								
								NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("远程");
							}else if(NetValue.INTRANET == netstatue){
								tvbttomNetwork.setText("本地");
							}
							break;	    
	    				case DEVMSG:
	    					
    					//根据网关号从数据库加载相应设备
	    				  alarmMsglist=new AlarmMessageDao(MessageActivity.this)
		                          .findAlarmMsgByGatewayid(SystemValue.gatewayid);
	    					
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
	  public  class AlarmMsgAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(alarmMsglist.size()>30){
				return 30;
			}else{
				return alarmMsglist.size();
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return alarmMsglist.get(position);
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
			      view = View.inflate(MessageActivity.this,R.layout.item_dev_sock,null);
			      holder = new ViewHolder(view);
			      view.setTag(holder);
			    }
			AlarmMessage alarmMsg=alarmMsglist.get(position);
			long alarmTime=alarmMsg.getTime();
		    Date date = new Date(alarmTime);
			String time=formatter.format(date);

            String deviceNo=alarmMsg.getDeviceNo();
            UserSpaceDevice userSpace=new UserSpaceDevDao(MessageActivity.this)
                                      .findDeviceSpace(SystemValue.phonenum, deviceNo);
            if(userSpace!=null){
            	String spacename = WebPacketUtil.getSpaceName(userSpace.getSpaceNo());   //根据phonespaceid获取spacename
    			holder.tvDevSite.setText(spacename);
    			holder.tvDevName.setText("/"+userSpace.getDeviceName()+time+"发生了报警！");
            }else{
            	Device device=new DevdtoDao(MessageActivity.this).findDevByDeviceNoAndGatewayNo(deviceNo, SystemValue.gatewayid);
            	holder.tvDevName.setText("/"+device.getDeviceName()+time+"发生了报警！");
            	
//            	String spacename = WebPacketUtil.getSpaceName(device.getSpaceNo());
//            	holder.tvDevSite.setText(spacename);
            	
            	String spaceno=device.getSpaceNo();
				if(spaceno==null){
					device.setSpaceNo("0");
				}
				String spacename = WebPacketUtil.getSpaceName(device.getSpaceNo()); // 根据phonespaceid获取spacename
				holder.tvDevSite.setText(spacename+"/" );
            }
			
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

		
	  
		/***退出系统***/
	    @OnClick(R.id.tv_head_submit)
	    public void systemExit(){
	    	initExitDialog();
	    }
	    /***返回***/
	    @OnClick(R.id.tv_head_back)
	    public void back(){
	    	Intent intent=new Intent(MessageActivity.this,HomeActivity.class);
			startActivity(intent);		
			finish();
	    }
	    
	    /***空间***/
	    @OnCheckedChanged(R.id.rb_navi_space)
	    public void  spaceDeviceShow(){
	    	Intent intent=new Intent(MessageActivity.this,SpaceDevicesActivity.class);
			startActivity(intent);		
			finish();
	    }
	    /***情景模式***/
	    @OnCheckedChanged(R.id.rb_navi_scene)
		public void sceneMode(){
			Intent sceneIntent=new Intent(MessageActivity.this,SceneModelActivity.class);
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
			Intent sceneIntent=new Intent(MessageActivity.this,DefenceAreaActivity.class);
			startActivity(sceneIntent);		
			finish();
		}
		
	    /***系统设置***/
		@OnCheckedChanged(R.id.rb_navi_set)
	    public void  systemSet(){
	    	Intent intent=new Intent(MessageActivity.this,SetActivity.class);
			startActivity(intent);		
			finish();
	    }
	    
 
	@Override
	protected void initViews() {
		if (NetValue.netFlag == NetValue.INTRANET) {
			tvbttomNetwork.setText("本地"); // 任务栏显示网络状态
		
		} else if (NetValue.netFlag == NetValue.OUTERNET) {
			tvbttomNetwork.setText("远程"); // 任务栏显示网络状态
	
		}
	}

	@Override
	protected void initDatas() {
		//根据网关号从数据库加载相应设备
		alarmMsglist=new AlarmMessageDao(MessageActivity.this)
		                       .findAlarmMsgByGatewayid(SystemValue.gatewayid);
		//  异步进程更新界面
		Message msg=new Message();
        msg.what=0x129;
        handler.sendMessage(msg);
	}
	  @Override  
	    protected void onDestroy() {  
			unbindService(devconn);
	        super.onDestroy(); //注意先后  
	    }  
}
