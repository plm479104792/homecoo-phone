package com.tuwa.smarthome.activity;

import java.util.ArrayList;
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
import com.tuwa.smarthome.activity.DeviceWindowActivity.WindowsAdapter.ViewHolder;
//import com.tuwa.smarthome.activity.DeviceWindowActivity.WindowsAdapter.ViewHolder;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.SocketPacket;
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
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceWindowActivity extends BaseActivity {
	//activity绑定service1
	private  SocketService devService ;
	
	  @Bind(R.id.tv_head_submit)  TextView tvExit;
	  @Bind(R.id.tv_head_back) TextView tvBack;
	  @Bind(R.id.tv_head_title) TextView tvTitle;
	  @Bind(R.id.gv_window_list) GridView gvDevices;
	  @Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
	  
	  private List<Device>  devlist=new ArrayList<Device>();
	  private WindowsAdapter deviceAdpter=null;
	
	  final char WinON=5+'0';  //窗帘暂停
	  final char WinPK=6+'0';  //窗帘暂停
      final char WinOFF=7+'0';  //窗帘暂停 
	  
      /*辅线程动态刷新页面*/
      Handler handler=new Handler(){
	   	 @Override
	   	 public void handleMessage(Message msg){
	   		 switch(msg.what){
	   		 case 0x129:
	   			if(devlist!=null){
	   				deviceAdpter=new WindowsAdapter();
		   			gvDevices.setAdapter(deviceAdpter);
	   			}
	   		 }
	   	 }
	   };

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_window);
	    ButterKnife.bind(DeviceWindowActivity.this);//注解工具声明
		// Activity和service绑定2
		 Intent service = new Intent(DeviceWindowActivity.this,SocketService.class);
		 bindService(service, devconn, Context.BIND_AUTO_CREATE);
		
		tvExit.setText("退出");
		tvTitle.setText("门窗");
	
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
									ToastUtils.showToast(DeviceWindowActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
								}
								
								NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("远程");
							}else if(NetValue.INTRANET == netstatue){
								tvbttomNetwork.setText("本地");
							}
							break;	    
	    				case DEVMSG:
	    				
    					//根据网关号从数据库加载相应设备
    					devlist=new DevdtoDao(DeviceWindowActivity.this)
    					                       .switchListBygwId(SystemValue.gatewayid,SystemValue.WINDOW);
	    					
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
		  public  class WindowsAdapter extends BaseAdapter{
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
				      view = View.inflate(DeviceWindowActivity.this,R.layout.item_dev_window,null);
				      holder = new ViewHolder(view);
				      view.setTag(holder);
				    }
				Device devdto=devlist.get(position);
				
				initDeviceNameAndSite(holder.tvDevSite,holder.tvDevName,devdto);//初始化房间名称和位置
				
				//设备状态串转换为状态字符数组
				String mdevState=devdto.getDeviceStateCmd();
				
				showViewByDevState(holder,mdevState);  //根据设备类型显示状态
				
				windowViewOnClick(holder,position); //列表中开关按键点击事件监听
				
				return view;
			}


			class ViewHolder {
				@Bind(R.id.tv_window_devSite)  TextView tvDevSite;
				@Bind(R.id.tv_window_devtypeName)  TextView tvDevName;
				@Bind(R.id.im_btnOn) ImageView imBtnOn;
				@Bind(R.id.im_btnPause) ImageView imBtnPause;
				@Bind(R.id.im_btnOff) ImageView imBtnOff;
				
			    public ViewHolder(View view) {
			    	ButterKnife.bind(this,view);
			    }
			  }

		   }
	/**
	 * 根据state显示设备状态	    
	 * @param holder
	 * @param mdevState
	 */
	private void showViewByDevState(ViewHolder holder, String devState) {
		if (devState.equals("10")) {
			holder.imBtnOn.setBackgroundResource(R.drawable.on1);
			holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
			holder.imBtnOff.setBackgroundResource(R.drawable.off0);
		}else if (devState.equals("01")) {
			holder.imBtnOn.setBackgroundResource(R.drawable.on0);
			holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
			holder.imBtnOff.setBackgroundResource(R.drawable.off1);
		}else {
			holder.imBtnOn.setBackgroundResource(R.drawable.on0);
			holder.imBtnPause.setBackgroundResource(R.drawable.pause1);
			holder.imBtnOff.setBackgroundResource(R.drawable.off0);
		}
	}   
	    
	 /**
     * 列表中开关按键点击事件监听
     * @param holder
     * @param position   按键对应的开关在devlist中位置
     */
	private void windowViewOnClick(final ViewHolder holder,final int position) {
		holder.imBtnOn.setOnClickListener(new ClickEvent() {    //窗帘on
			
			@Override
			public void singleClick(View v) {
				cmdControl(position,6,WinON);
				holder.imBtnOn.setBackgroundResource(R.drawable.on1);
				holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
				holder.imBtnOff.setBackgroundResource(R.drawable.off0);
				
			}
		});
		holder.imBtnPause.setOnClickListener(new ClickEvent() {  //窗帘pk
					
					@Override
					public void singleClick(View v) {
						cmdControl(position,6,WinPK);
						holder.imBtnOn.setBackgroundResource(R.drawable.on0);
						holder.imBtnPause.setBackgroundResource(R.drawable.pause1);
						holder.imBtnOff.setBackgroundResource(R.drawable.off0);
						
					}
				});
		holder.imBtnOff.setOnClickListener(new ClickEvent() {    //窗帘off
			
			@Override
			public void singleClick(View v) {
				cmdControl(position,6,WinOFF);
				holder.imBtnOn.setBackgroundResource(R.drawable.on0);
				holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
				holder.imBtnOff.setBackgroundResource(R.drawable.off1);
				
			}
		});

	}   
	 /**
     * 发送命令函数
     * @param position   当前点击的开关在devlist中位置
     * @param switchid   多路开关中当前点击的位置
     * @param ch         相应的开关命令
     */
	public void cmdControl(int position,int switchid,char ch){
		Device device=devlist.get(position);
	
		String strCmd=null;  //待发送的命令
		
		if (ch==WinON) {
			strCmd="10";
		}else if (ch==WinPK) {
			strCmd="00";
		}else if (ch==WinOFF) {
			strCmd="01";
		}
	   
	   device.setDeviceStateCmd(strCmd);     //注意更改后的设备状态先加载到本地

	   //更新device最新状态到本地设备数据库(控制）
	   new DevdtoDao(DeviceWindowActivity.this).updateDevStateByDeviceNo(device);
	   
	  // 将命令转换为报文
	  SocketPacket devPacket = WebPacketUtil.devConvertToPacket(device);
		
	   switch (NetValue.netFlag) {
		case NetValue.OUTERNET:   //外网
			// 将命令封装为字符串发送到服务器
			byte[] sentBytes = WebPacketUtil.packetToByteStream(devPacket);
			sendCmdToServer(sentBytes,0); // 发送到服务器的命令串
			
			break;
        case NetValue.INTRANET:   //内网
        	devService.sentPacket(devPacket);   //发送请求认证报文到网关
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
    	Intent intent=new Intent(DeviceWindowActivity.this,HomeActivity.class);
		startActivity(intent);		
		finish();
    }
    
    /***空间***/
    @OnCheckedChanged(R.id.rb_navi_space)
    public void  spaceDeviceShow(){
    	Intent intent=new Intent(DeviceWindowActivity.this,SpaceDevicesActivity.class);
		startActivity(intent);		
		finish();
    }
    /***情景模式***/
    @OnCheckedChanged(R.id.rb_navi_scene)
	public void sceneMode(){
		Intent sceneIntent=new Intent(DeviceWindowActivity.this,SceneModelActivity.class);
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
		Intent sceneIntent=new Intent(DeviceWindowActivity.this,DefenceAreaActivity.class);
		startActivity(sceneIntent);		
		finish();
	}
	
    /***系统设置***/
	 @OnCheckedChanged(R.id.rb_navi_set)
    public void  systemSet(){
    	Intent intent=new Intent(DeviceWindowActivity.this,SetActivity.class);
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
		devlist=new DevdtoDao(DeviceWindowActivity.this)
                .switchListBygwId(SystemValue.gatewayid,SystemValue.WINDOW);

		//  异步进程更新界面
		Message msg=new Message();
		msg.what=0x129;
		handler.sendMessage(msg);
		
	}
	@Override  
    protected void onDestroy() {
//		timerhandler.removeCallbacks(timerrunnable);  //停止定时器线程
		unbindService(devconn);
        super.onDestroy(); //注意先后  
    }  
	
	
}
