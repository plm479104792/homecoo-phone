package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.SpaceDevicesActivity.DevicesAllAdapter.Holder;
import com.tuwa.smarthome.adapter.SpaceDeviceViewAdapter;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.UserSpaceDevice;
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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class SpaceDevicesActivity extends BaseActivity implements AdpterOnItemClick,OnCheckedChangeListener{
	//activity绑定service1
	private  SocketService devService ;
	  
	 private ViewPager viewPager;  
	 private ArrayList<View> pageViews;  
	 private List<Space> spacelist = new ArrayList<Space>();
	 private List<Device>  gvlistAll=new ArrayList<Device>();  //在列表中显示的list
	 private List<Device>  devlist=new ArrayList<Device>();
	 private ImageView imageView;  
	 private ImageView[] imageViews; 
	 private TextView tvBack,tvTitle,tvExit, tvbttomNetwork,
	            tvSpace,tvScene,tvNetwork,tvAlert,tvSet;
	 private RadioGroup rg_tab_navi;
	 private GridView[] mgvDevices;
	 private LayoutInflater inflater;
	 // 包裹滑动页面LinearLayout
	 private ViewGroup main;
	 // 包裹小圆点的LinearLayout
	 private ViewGroup group;
	 
	  //滑动缓存加载Listview
	  private final int LOAD_STATE_IDLE=0;       //没有在加载，并且list中还有数据没加载
	  private final int LOAD_STATE_LOADING=1;    //正在加载
	  private final int LOAD_STATE_FINISH=2;     //list中数据全部加载完毕
	  private int loadState=LOAD_STATE_IDLE;     //记录加载状态
	  private int LIST_COUNT;         //list中的总数目
	  private static int EACH_COUNT=10;         //每页加载的数目
	 
	 
	 private View[] spaceViews; //动态生成的view数组
	 private int viewsNum;
	 private int selectIndex;
	 private String vSpaceid;   //当前的spaceid用于从服务器定时刷新
	 
	 private DevicesAllAdapter deviceAdpter=new DevicesAllAdapter();
	  final char ON=1+'0';  //字符开
	  final char OFF=0+'0';  //字符关
	  final char WinON=5+'0';  //窗帘暂停
	  final char WinPK=6+'0';  //窗帘暂停
      final char WinOFF=7+'0';  //窗帘暂停 
	  public String sLightVal;   //可调关的亮度
	 private static char [] strStaArr=new char[4];   //字符数组代表多路开关状态
     private String pSpaceid=""; //全局的空间id
	
	 
	  /*辅线程动态刷新页面*/
     Handler handler=new Handler(){
	   	 @Override
	   	 public void handleMessage(Message msg){
	   		 switch(msg.what){
	   		 case 0x129:
	   			 
	   			 LIST_COUNT=gvlistAll.size();   //实例化所有设备的总数
	   			 loadState=LOAD_STATE_IDLE;     //记录加载状态
	   		     deviceAdpter.onListener(SpaceDevicesActivity.this);
	   	     	 mgvDevices[selectIndex].setAdapter( deviceAdpter);
	   	          break;
	   		  case 0x008:
	   			 @SuppressWarnings("unchecked")
				 ArrayList<Device> result=((ArrayList<Device>) msg.obj);
		   		 
	   			 devlist.addAll(result);
		   		 deviceAdpter.notifyDataSetChanged();
	   			  break;
	   		  case 0x009:
	   			 int index = msg.arg1;  
		            Device devdto = (Device) msg.obj; 
		            int firstVisible =  mgvDevices[selectIndex].getFirstVisiblePosition();  
		            int lastVisible =  mgvDevices[selectIndex].getLastVisiblePosition();  
		            if (index >= firstVisible && index <= lastVisible) {
		            	//获取到index对应的holder
		            	Holder holder = (Holder) ( mgvDevices[selectIndex] 
		                        .getChildAt(index - firstVisible).getTag());  
		            	  showViewByDevtype(holder,devdto);
		            }  
	   			  break;
	   			  
	   		 }
	   	 }
	   };
	   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	     // Activity和service绑定2
 		Intent service = new Intent(SpaceDevicesActivity.this,SocketService.class);
 		bindService(service, devconn, Context.BIND_AUTO_CREATE);
	 		
		ButterKnife.bind(SpaceDevicesActivity.this);//注解工具声明
	    inflater = getLayoutInflater();  
	    main = (ViewGroup)inflater.inflate(R.layout.activity_space_devices, null); 
	    
		initViews();
		
		
		//从本地数据库加载房间列表,房间只加载一次
		spacelist=WebPacketUtil.findSpaceFromLocalByWgidAndPhonenum();
		//根据内网的列表从本地数据库加载设备
		initDeviceByIntranetSpaceList();
	
        setContentView(main);
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());  
      
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
									ToastUtils.showToast(SpaceDevicesActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
								}
								
								NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("远程");
							}else if(NetValue.INTRANET == netstatue){
								tvbttomNetwork.setText("本地");
							}
							break;	
	    				case DEVMSG:
	    				
	    				SocketPacket socketPacket=(SocketPacket) tranObject.getObject();
	    				String devid=socketPacket.getDevId();
	    				String devstate=(String)socketPacket.getData();
	    				
	    				Device devDTO=null;
	    				  int vposition=-1;
	    			        // 进行数据对比获取对应数据在list中的位置
	    			      for(int j=0;j<devlist.size();j++)
	    			        { 
	    			    	    String strdevid=devlist.get(j).getDeviceNo();
	    			            if (devid.equalsIgnoreCase(strdevid))
	    			            { 
	    			                devDTO=devlist.get(j);
	    			                devDTO.setDeviceStateCmd(devstate);
	    			                vposition=j;
	    			                //  异步进程更新界面
	    	    		 		    Message msg=new Message();
	    	    		            msg.what=0x009;
	    	    		            msg.arg1=vposition;
	    	    		            msg.obj=devDTO;
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
	

	
	/*************根据内网的列表从本地数据库加载设备*****************/
	private void initDeviceByIntranetSpaceList() {
		if (!(spacelist.isEmpty())) {
    	    viewsNum=spacelist.size();
	  		//初始化指引小圆点图片数组
	  	    initGuidImageView(viewsNum);
	  	    //初始化SpaceDeviceViewPages
	        initSpaceDeviceView(inflater,viewsNum);
        	 
        	
             //根据网关号从数据库加载相应设备
		   String firSpaceId=spacelist.get(0).getSpaceNo();
		   pSpaceid=firSpaceId;
		   
          selectIndex=0;    //初始化第一个view的序号
          tvTitle.setText(spacelist.get(selectIndex).getSpaceName());//标题栏显示当前位置 
          
          initDatas();	
          
 		}else {
 			ToastUtils.showToast(SpaceDevicesActivity.this,"空间列表为空，请先在设置页面添加空间！", 1000);
 		}
	}


    /***************初始化指引小圆点图片数组****************/
	private void initGuidImageView(int viewsnum) {
		imageViews = new ImageView[viewsnum];  
    	 for (int i = 0; i < viewsnum; i++) {  
             imageView = new ImageView(SpaceDevicesActivity.this);  
             imageView.setLayoutParams(new LayoutParams(20,20));
             imageView.setPadding(10, 0, 10, 0);  
             imageViews[i] = imageView;  
             
             if (i == 0) {  
                 //默认选中第一张图片
                 imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);  
             } else {  
                 imageViews[i].setBackgroundResource(R.drawable.page_indicator);  
             }  
             group.addView(imageViews[i]);  
         }  
		
	}



	/*********************指引页面更改事件监听器*****************************/ 
    class GuidePageChangeListener implements OnPageChangeListener {  
    	  
		@Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
        }  
  
        @Override  
        public void onPageSelected(int arg0) {  
            for (int i = 0; i < imageViews.length; i++) {  
                imageViews[arg0].setBackgroundResource(R.drawable.page_indicator_focused);
                
                if (arg0 != i) {  
                    imageViews[i].setBackgroundResource(R.drawable.page_indicator);  
                }  
            }
            pSpaceid = spacelist.get(arg0).getSpaceNo();
            vSpaceid=pSpaceid;
            selectIndex=arg0;   //全局化当前的paperView序号
            tvTitle.setText(spacelist.get(selectIndex).getSpaceName());//标题栏显示当前位置 
             //从本地数据库加载房间列表
       	     devlist.clear();
  		     gvlistAll.clear();
  		     
             initDatas();
       		  
        }  
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
	
			device.setDeviceStateCmd(sqlCmd);
			// 更新device最新状态到本地设备数据库（3）
			new DevdtoDao(SpaceDevicesActivity.this).updateDevStateByDeviceNo(device);
	
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
		
		
		 /****************初始化SpaceDeviceViewPages************************/
	    private void initSpaceDeviceView(LayoutInflater inflater,int viewsnum) {
	        pageViews = new ArrayList<View>();  
	        spaceViews = new View[viewsNum]; 
	        mgvDevices=new GridView[viewsNum];
	        
	      for (int i = 0; i < viewsnum; i++) {
	           spaceViews[i]=inflater.inflate(R.layout.item_space_devices, null);
	           mgvDevices[i]=(GridView)spaceViews[i].findViewById(R.id.gv_space_devices);
	           pageViews.add(spaceViews[i]);
	         //Gridview滑动分页加载列表
	           mgvDevices[i].setOnScrollListener(new MyOnScrollListener());
			}
	      
	      viewPager.setAdapter(new SpaceDeviceViewAdapter(pageViews));  
	      
		}  
	    
	    private final class MyOnScrollListener implements OnScrollListener{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			    final int totalCount=firstVisibleItem+visibleItemCount;  //firstVisibleItem当前页的第一项位置
			                                                             //totalCount  当前页的最后一项位置
				if (totalCount==totalItemCount) {     //当前这一页加载完成，等待加载下一页
					if (loadState==LOAD_STATE_IDLE) {
						loadState=LOAD_STATE_LOADING;
						
						new Thread(){
							public void run(){
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								int count=deviceAdpter.getCount();
								int dataIndex=0;  // 要加载的数据的index
								List<Device>  result=new ArrayList<Device>();
								for (dataIndex=count; dataIndex <Math.min(count+EACH_COUNT,LIST_COUNT);dataIndex++) {
									if (!gvlistAll.isEmpty()) {
										Device devdto=gvlistAll.get(dataIndex);
										result.add(devdto);
									}
								}
									
								if (dataIndex==LIST_COUNT) {
									loadState=LOAD_STATE_FINISH;
								}else {
									loadState=LOAD_STATE_IDLE;   //list未加载完，待续
								}
								
									Message msg=new Message();
									msg.what=0x008;
									msg.obj=result;
									handler.sendMessage(msg);
							};
						}.start();
					}
				}
			}
		}
	    
	    
	    /***************devlist数据适配器*******************/
         public  class DevicesAllAdapter extends BaseAdapter{
			private AdpterOnItemClick myAdpterOnclick;
				
			public void onListener(AdpterOnItemClick listener){
					this.myAdpterOnclick=listener;
			}
			@Override
			public int getCount() {
				return devlist.size();
			}

			@Override
			public Object getItem(int position) {
				return devlist.get(position);
			}

			@Override
			public long getItemId(int position) {
				return -1;
			}

			@Override
			public View getView(final int position, View view, ViewGroup parent) {
				Holder holder;
				if (view != null) {
				      holder = (Holder) view.getTag();
				}else {
				      view = View.inflate(SpaceDevicesActivity.this,R.layout.item_devices_all,null);
				      holder = new Holder(view);
				      view.setTag(holder);
				    }
				Device devdto=devlist.get(position);
				
				holder.tvDevSite.setVisibility(view.GONE);
				holder.tvDevName.setText(devdto.getDeviceName());
				
//				initDeviceNameAndSite(holder.tvDevSite,holder.tvDevName,devdto);//初始化房间名称和位置
				
				 //点击事件的按钮做标记
			    holder.tgBtn1.setTag(position);
			    holder.tgBtn2.setTag(position);
			    holder.tgBtn3.setTag(position);
			    holder.tgBtn4.setTag(position);
			    holder.sbLight.setTag(position);
			    holder.imBtnOn.setTag(position);
			    holder.imBtnPause.setTag(position);
			    holder.imBtnOff.setTag(position);
			    
			    final int fpostion=position;
			    final Holder fHolder=holder;
				
				showViewByDevtype(holder,devdto);  //根据设备类型显示状态

				holder.tgBtn1.setOnClickListener( new ClickEvent() {
						@Override
						public void singleClick(View v) {
						    if (myAdpterOnclick != null) {
								int which=v.getId();
								myAdpterOnclick.onApterClick(which, fpostion);
								if (!fHolder.tgBtn1.isChecked()) { //当前状态没选中，发送off
									cmdControl(position,1,OFF);
								}else {
									cmdControl(position,1,ON);
								}
							}else {
								System.out.println("===myAdpterOnclick为空====");
							}
							
						}
					});
					
				  holder.tgBtn2.setOnClickListener(new ClickEvent(){

		 				@Override
		 				public void singleClick(View v) {
		 				   if (myAdpterOnclick != null) {
		 					  int which=v.getId();
						      myAdpterOnclick.onApterClick(which, fpostion);
		 					if(!fHolder.tgBtn2.isChecked()){
		 						cmdControl(position,2,OFF);
		 					}
		 					else{
		 						cmdControl(position,2,ON);
		 					}
		 				  }
		 			   }
		 			});
				  
				  holder.tgBtn3.setOnClickListener(new ClickEvent() {
						
						@Override
						public void singleClick(View v) {
							   if (myAdpterOnclick != null) {
				 					  int which=v.getId();
								      myAdpterOnclick.onApterClick(which, fpostion);
				 					if(!fHolder.tgBtn3.isChecked()){
				 						cmdControl(position,3,OFF);
				 					}
				 					else{
				 						cmdControl(position,3,ON);
				 					}
				 				  }
						}
					});
				  
				  holder.tgBtn4.setOnClickListener(new ClickEvent() {
						
						@Override
						public void singleClick(View v) {
							   if (myAdpterOnclick != null) {
				 					  int which=v.getId();
								      myAdpterOnclick.onApterClick(which, fpostion);
				 					if(!fHolder.tgBtn4.isChecked()){
				 						cmdControl(position,4,OFF);
				 					}
				 					else{
				 						cmdControl(position,4,ON);
				 					}
				 				  }
						}
					});
				  
				  holder.imBtnOn.setOnClickListener(new OnClickListener() {    //窗帘on
						
						@Override
						public void onClick(View v) {
						  if (myAdpterOnclick != null) {
		 					    int which=v.getId();
						        myAdpterOnclick.onApterClick(which, fpostion);
							      
						        cmdControl(position,6,WinON);
							
								fHolder.imBtnOn.setBackgroundResource(R.drawable.on1);
								fHolder.imBtnPause.setBackgroundResource(R.drawable.pause0);
								fHolder.imBtnOff.setBackgroundResource(R.drawable.off0);
							 }
						}
					});
					holder.imBtnPause.setOnClickListener(new OnClickListener() {  //窗帘pk
								
								@Override
								public void onClick(View v) {
								if (myAdpterOnclick != null) {
			 					    int which=v.getId();
							        myAdpterOnclick.onApterClick(which, fpostion);
							        
							    	cmdControl(position,6,WinPK);
									fHolder.imBtnOn.setBackgroundResource(R.drawable.on0);
									fHolder.imBtnPause.setBackgroundResource(R.drawable.pause1);
									fHolder.imBtnOff.setBackgroundResource(R.drawable.off0);
								}
									
								}
							});
					holder.imBtnOff.setOnClickListener(new OnClickListener() {    //窗帘off
						
						@Override
						public void onClick(View v) {
						if (myAdpterOnclick != null) {
		 					  int which=v.getId();
						      myAdpterOnclick.onApterClick(which, fpostion);
						      
						  	cmdControl(position,6,WinOFF);
							fHolder.imBtnOn.setBackgroundResource(R.drawable.on0);
							fHolder.imBtnPause.setBackgroundResource(R.drawable.pause0);
							fHolder.imBtnOff.setBackgroundResource(R.drawable.off1);
						 }	
						}
					});
					
					holder.sbLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {    //调光灯
						int mprogress=0;
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress,
								boolean fromUser) {
							mprogress=progress;
						}
						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}
						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {     //滑动条停止时触发事件
							 sLightVal=String.valueOf(mprogress);
						     devlist.get(position).setDeviceStateCmd(sLightVal);
						     cmdControl(position,5,ON);
						}
					});
					  
				   return view;
				}

			class Holder {
				@Bind(R.id.tv_switch_devSite)  TextView tvDevSite;
				@Bind(R.id.tv_switch_devtypeName)  TextView tvDevName;
				@Bind(R.id.tg_btn1)  ToggleButton tgBtn1;
				@Bind(R.id.tg_btn2)  ToggleButton tgBtn2;
				@Bind(R.id.tg_btn3)  ToggleButton tgBtn3;
				@Bind(R.id.tg_btn4)  ToggleButton tgBtn4;
				@Bind(R.id.sb_switch_light)  SeekBar sbLight;   //调光灯
				@Bind(R.id.im_btnOn) ImageView imBtnOn;
				@Bind(R.id.im_btnPause) ImageView imBtnPause;
				@Bind(R.id.im_btnOff) ImageView imBtnOff;
				
			    public Holder(View view) {
			    	ButterKnife.bind(this,view);
			    }
			  }

		   }
	    
		     /**
		      * 根据设备类型显示按钮
		      * @param holder
		      * @param devtype
		      */
			  private void showViewByDevtype(Holder holder,Device devdto) {
				   int devtype=devdto.getDeviceTypeId();
				   //设备状态串转换为状态字符数组
					String devState=devdto.getDeviceStateCmd();
					strStaArr=devState.toCharArray();
					switch (devtype) {
					case 1:
						holder.tgBtn1.setVisibility(View.VISIBLE);
						holder.tgBtn2.setVisibility(View.GONE);
						holder.tgBtn3.setVisibility(View.GONE);
						holder.tgBtn4.setVisibility(View.GONE);
						holder.sbLight.setVisibility(View.GONE);
						holder.imBtnOn.setVisibility(View.GONE);
						holder.imBtnPause.setVisibility(View.GONE);
						holder.imBtnOff.setVisibility(View.GONE);
						if(strStaArr[0]==ON){
		            		holder.tgBtn1.setChecked(true);
		    			}else {
		    				holder.tgBtn1.setChecked(false);
						}
						break;
					case 8:    //插座
						holder.tgBtn1.setVisibility(View.VISIBLE);
						holder.tgBtn2.setVisibility(View.GONE);
						holder.tgBtn3.setVisibility(View.GONE);
						holder.tgBtn4.setVisibility(View.GONE);
						holder.sbLight.setVisibility(View.GONE);
						holder.imBtnOn.setVisibility(View.GONE);
						holder.imBtnPause.setVisibility(View.GONE);
						holder.imBtnOff.setVisibility(View.GONE);
						
						if(strStaArr[0]==ON){
		            		holder.tgBtn1.setChecked(true);
		    			}else {
		    				holder.tgBtn1.setChecked(false);
						}
						break;
					case 51:    //风扇
						holder.tgBtn1.setVisibility(View.VISIBLE);
						holder.tgBtn2.setVisibility(View.GONE);
						holder.tgBtn3.setVisibility(View.GONE);
						holder.tgBtn4.setVisibility(View.GONE);
						holder.sbLight.setVisibility(View.GONE);
						holder.imBtnOn.setVisibility(View.GONE);
						holder.imBtnPause.setVisibility(View.GONE);
						holder.imBtnOff.setVisibility(View.GONE);
						
						if(strStaArr[0]==ON){
		            		holder.tgBtn1.setChecked(true);
		    			}else {
		    				holder.tgBtn1.setChecked(false);
						}
						break;
		            case 2:
		            	holder.tgBtn1.setVisibility(View.VISIBLE);
		            	holder.tgBtn2.setVisibility(View.VISIBLE);
						holder.tgBtn3.setVisibility(View.GONE);
						holder.tgBtn4.setVisibility(View.GONE);
						holder.sbLight.setVisibility(View.GONE);
						holder.imBtnOn.setVisibility(View.GONE);
						holder.imBtnPause.setVisibility(View.GONE);
						holder.imBtnOff.setVisibility(View.GONE);
						
		            	if(strStaArr[0]==ON){
		            		holder.tgBtn1.setChecked(true);
		    			}else {
		    				holder.tgBtn1.setChecked(false);
						}
		    			if(strStaArr[1]==ON){
		    				holder.tgBtn2.setChecked(true);
		    			}else {
		    				holder.tgBtn2.setChecked(false);
						}
						break;
		            case 3:
		            	holder.tgBtn1.setVisibility(View.VISIBLE);
		            	holder.tgBtn2.setVisibility(View.VISIBLE);
		            	holder.tgBtn3.setVisibility(View.VISIBLE);
						holder.tgBtn4.setVisibility(View.GONE);
						holder.sbLight.setVisibility(View.GONE);
						holder.imBtnOn.setVisibility(View.GONE);
						holder.imBtnPause.setVisibility(View.GONE);
						holder.imBtnOff.setVisibility(View.GONE);
						
		    			if(strStaArr[0]==ON){
		    				holder.tgBtn1.setChecked(true);
		    			}else {
		    				holder.tgBtn1.setChecked(false);
						}
		    			if(strStaArr[1]==ON){
		    				holder.tgBtn2.setChecked(true);
		    			}else {
		    				holder.tgBtn2.setChecked(false);
						}
		    			if(strStaArr[2]==ON){
		    				holder.tgBtn3.setChecked(true);
		    			}else {
		    				holder.tgBtn3.setChecked(false);
						}
						break;
		            case 4:
		            	holder.tgBtn1.setVisibility(View.VISIBLE);
		            	holder.tgBtn2.setVisibility(View.VISIBLE);
		            	holder.tgBtn3.setVisibility(View.VISIBLE);
		            	holder.tgBtn4.setVisibility(View.VISIBLE);
						holder.sbLight.setVisibility(View.GONE);
						holder.imBtnOn.setVisibility(View.GONE);
						holder.imBtnPause.setVisibility(View.GONE);
						holder.imBtnOff.setVisibility(View.GONE);
						
		    			if(strStaArr[0]==ON){
		    				holder.tgBtn1.setChecked(true);
		    			}else {
		    				holder.tgBtn1.setChecked(false);
						}
		    			if(strStaArr[1]==ON){
		    				holder.tgBtn2.setChecked(true);
		    			}else {
		    				holder.tgBtn2.setChecked(false);
						}
		    			if(strStaArr[2]==ON){
		    				holder.tgBtn3.setChecked(true);
		    			}else {
		    				holder.tgBtn3.setChecked(false);
						}
		    			if(strStaArr[3]==ON){
		    				holder.tgBtn4.setChecked(true);
		    			}else {
		    				holder.tgBtn4.setChecked(false);
						}
						break;
		            case 6:   //窗帘
		            	holder.tgBtn1.setVisibility(View.GONE);
		            	holder.tgBtn2.setVisibility(View.GONE);
		            	holder.tgBtn3.setVisibility(View.GONE);
		            	holder.tgBtn4.setVisibility(View.GONE);
						holder.sbLight.setVisibility(View.GONE);
		            	holder.imBtnOn.setVisibility(View.VISIBLE);
		            	holder.imBtnPause.setVisibility(View.VISIBLE);
		            	holder.imBtnOff.setVisibility(View.VISIBLE);
		            	
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
		            	break;
		            case 11:    //窗户
		            	holder.tgBtn1.setVisibility(View.GONE);
		            	holder.tgBtn2.setVisibility(View.GONE);
		            	holder.tgBtn3.setVisibility(View.GONE);
		            	holder.tgBtn4.setVisibility(View.GONE);
						holder.sbLight.setVisibility(View.GONE);
		            	holder.imBtnOn.setVisibility(View.VISIBLE);
		            	holder.imBtnPause.setVisibility(View.VISIBLE);
		            	holder.imBtnOff.setVisibility(View.VISIBLE);
		            	
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
		            	break;
		            
		            case 5:   //调光灯
		            	holder.tgBtn1.setVisibility(View.GONE);
		            	holder.tgBtn2.setVisibility(View.GONE);
		            	holder.tgBtn3.setVisibility(View.GONE);
		            	holder.tgBtn4.setVisibility(View.GONE);
						holder.sbLight.setVisibility(View.VISIBLE);
		            	holder.imBtnOn.setVisibility(View.GONE);
		            	holder.imBtnPause.setVisibility(View.GONE);
		            	holder.imBtnOff.setVisibility(View.GONE);
		            	
						holder.sbLight.setMax(9);
						int dLight = 0;
						if (devState.endsWith("a")) {
							dLight = 9;
						} else {
							dLight = Integer.valueOf(devState);
						}
						holder.sbLight.setProgress(dLight);
						break;
					}
					
				}
	

	@Override
	protected void initViews() {
	    group = (ViewGroup)main.findViewById(R.id.vg_indicator_img);  
        viewPager = (ViewPager)main.findViewById(R.id.vp_space_device);  
        tvBack=(TextView) main.findViewById(R.id.tv_head_back);
        tvTitle=(TextView) main.findViewById(R.id.tv_head_title);
        tvExit=(TextView) main.findViewById(R.id.tv_head_submit);
        tvExit.setText("退出");
        tvbttomNetwork=(TextView) main.findViewById(R.id.tv_bottom_network);
        
        rg_tab_navi=(RadioGroup) main.findViewById(R.id.rg_tab_navi);
        
		tvBack.setOnClickListener(BackOnClickListener);
		tvExit.setOnClickListener(ExitOnClickListener);
		
		rg_tab_navi.setOnCheckedChangeListener(this);
		
		tvbttomNetwork.setOnClickListener(NetworkOnClickListener);
		
		if (NetValue.netFlag==NetValue.INTRANET) {
			tvbttomNetwork.setText("本地");
		}else if ((NetValue.netFlag==NetValue.OUTERNET)) {
			tvbttomNetwork.setText("远程");
		}
        
	}


	@Override
	protected void initDatas() {
	        List<UserSpaceDevice>  userDevList=new UserSpaceDevDao(SpaceDevicesActivity.this)
	                           .getDeviceListByPhonenumAndSpaceNo(SystemValue.phonenum,pSpaceid);
	   
	    	  gvlistAll=WebPacketUtil.findSpaceDevicesFromDevicesAll(userDevList);  
	        //异步进程更新界面
	   	      Message msg=new Message();
	   	      msg.what=0x129;
	   	      handler.sendMessage(msg);  
	}
	
	private OnClickListener BackOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent=new Intent(SpaceDevicesActivity.this,HomeActivity.class);
			startActivity(intent);		
			finish();
		}
	};
	private OnClickListener ExitOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			initExitDialog();
		}
	};
	

	
	private OnClickListener NetworkOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			netWorkSwitch(devService,tvbttomNetwork);
		}
	};
	
	@Override  
    protected void onDestroy() {  
		unbindService(devconn);
        super.onDestroy(); //注意先后  
    }




	@Override
	public void onApterClick(int which, int postion) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_navi_space:
			Intent intent=new Intent(SpaceDevicesActivity.this,HomeActivity.class);
			startActivity(intent);		
			finish();
			break;
		case R.id.rb_navi_scene:
			Intent intent2=new Intent(SpaceDevicesActivity.this,SceneModelActivity.class);
			startActivity(intent2);		
			finish();		
			break;
		case R.id.rb_navi_alert:
			Intent intent3=new Intent(SpaceDevicesActivity.this,DefenceAreaActivity.class);
			startActivity(intent3);		
			finish();
			break;
		case R.id.rb_navi_set:
			Intent intent4=new Intent(SpaceDevicesActivity.this,SetActivity.class);
			startActivity(intent4);		
			finish();
			break;

		default:
			break;
		}
		
	}  
}
