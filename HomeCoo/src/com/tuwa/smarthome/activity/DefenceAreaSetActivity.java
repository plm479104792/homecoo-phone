package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.WebService;
import com.tuwa.smarthome.network.WebService.WebServiceCallBack;
import com.tuwa.smarthome.util.WebPacketUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

//public class DefenceAreaSetActivity extends BaseActivity {
//	  @Bind(R.id.tv_head_submit)  TextView tvSubmit;
//	  @Bind(R.id.tv_head_back) TextView tvBack;
//	  @Bind(R.id.tv_head_title) TextView tvtitle;
//	  @Bind(R.id.gv_devsensor_list) GridView gvDevices;
//
//	  protected Context mContext;
//	  private List<Device>  devlist=new ArrayList<Device>();
//	  private DeviceAdapter deviceAdpter=null;
//	  private int aleThemeid;  //安防情景号
//	  
//	 
//	  /*辅线程动态刷新页面*/   
//	   /*辅线程动态刷新页面*/
//	     Handler handler=new Handler(){
//		   	 @Override
//		   	 public void handleMessage(Message msg){
//		   		 switch(msg.what){
//		   		 case 0x129:
//		   			deviceAdpter=new DeviceAdapter();
//		   		    gvDevices.setAdapter( deviceAdpter);
//		   		 }
//		   	 }
//		   };
//	  
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_device_sensor);
//		
//		 ButterKnife.bind(DefenceAreaSetActivity.this);//注解工具声明
//			
//			tvSubmit.setVisibility(View.INVISIBLE);
//			tvtitle.setText("防区管理");
//			
//	 	    
//	 		if (NetValue.netFlag==NetValue.INTRANET) {
//				//根据网关号从数据库加载相应设备
//				devlist=new DevdtoDao(DefenceAreaSetActivity.this)
//	                 .switchListBygwId(SystemValue.gatewayid,SystemValue.weikong2);
//				
//			    //  异步进程更新界面
//	 		    Message msg=new Message();
//	            msg.what=0x129;
//	            handler.sendMessage(msg);
//				
//			}else if (NetValue.netFlag==NetValue.OUTERNET) {
//				/*外网根据设备大类号加载设备信息*/
//		 	    showDevByWideid(SystemValue.weikong2);
//			} 
//			
//	}
//	/**
//	 * 根据设备大类号加载设备信息
//	 * @param devWideId      设备的大类号
//	 */
//	private void showDevByWideid(int devWideId){
//		
//	   	 String methodName = "findDevByDevWideType";   //设备类型的大类
//	  	 String url = WebService.WEB_SERVER_URL;
//    	 HashMap<String,Object> properties=new HashMap<String,Object>();
//    	 properties.put("userid",SystemValue.userid);
//    	 properties.put("devWideType",devWideId);
//    	 
//    	WebService.callWebService(url, methodName, properties,new WebServiceCallBack() {  
//           
//    		 //WebService接口返回的数据回调到这个方法中  
//	          @Override  
//	          public void callBack(TranObject tranObject) { 
//	        	  switch (tranObject.getTranType()) {
//					case NETMSG:   
//						int netstatue=(Integer) tranObject.getObject();
//						if (NetValue.NONET==netstatue) {  //本地连接失败
//							
//							showCustomToast("远程连接失败，请检查网络！");
//						}
//	        	       break;
//					case  DEVMSG:
//						 parseAndSaveDevlist(tranObject);  //解析和保存设备列表
//						break;
//	        	  }
//	          }
//      }); 
//    }
//
//
//    /**
//     * 解析和保存设备列表
//     * @param tranObject
//     */
//	private void parseAndSaveDevlist(TranObject tranObject) {
//		  SoapObject soapObject=(SoapObject) tranObject.getObject();
//		  String rstJsonArray = soapObject.getProperty(0).toString();  //转换为jsonArray串
//		  System.out.println("======devswitch====="+rstJsonArray);
//		  devlist=WebPacketUtil.parseSoaptoDevicelist(rstJsonArray);
//		  
//		  //  异步进程更新界面
//		  Message msg=new Message();
//          msg.what=0x129;
//          handler.sendMessage(msg);
//		  
//	} 
//		
//	 public class DeviceAdapter  extends BaseAdapter {
//			
//			@Override
//			public int getCount() {
//				return devlist.size();
//			}
//
//			@Override
//			public Object getItem(int position) {
//				return devlist.get(position);
//			}
//
//			@Override
//			public long getItemId(int position) {
//				return position;
//			}
//
//			@Override
//			public View getView( final int position, View view, ViewGroup parent) {
//				ViewHolder holder;
//				if (view != null) {
//				      holder = (ViewHolder) view.getTag();
//				    } else {
//				      view = View.inflate(DefenceAreaSetActivity.this,R.layout.item_devarea_set,null);
//				      holder = new ViewHolder(view);
//				      view.setTag(holder);
//				    }
//				
//	            showViewByDevtype(holder,position);  //根据设备类型显示状态
//				
//				switchViewOnClick(holder,position); //列表中开关按键点击事件监听
//				
//				return view;
//			}
//
//			class ViewHolder {
//				@Bind(R.id.tv_set_devSite)  TextView tvDevSite;
//				@Bind(R.id.tv_set_devtypeName)  TextView tvDevName;
//			    @Bind(R.id.rbtn_indoor) RadioButton rbtnIndoor;
//			    @Bind(R.id.rbtn_outdoor) RadioButton rbtnOutdoor;
//				
//			    public ViewHolder(View view) {
//			    	ButterKnife.bind(this,view);
//			    }
//			  }
//			
//			
//			private void showViewByDevtype(ViewHolder holder, int position) {
//				 Device devdto=devlist.get(position);
//				 String devname=devdto.getDevname();
//				 String devid=devdto.getDevId();
//				 
//				 holder.tvDevSite.setText(devdto.getSpacetypnename());
//			     holder.tvDevName.setText("/"+devname);
//				 
//				  Theme theme=new ThemeDao(DefenceAreaSetActivity.this).getThemeByUseridAnddevstrid(SystemValue.userid,devid);
//				     if (theme!=null) {
//				    	 String themestate=theme.getThemestate();
//				    	 if (themestate.equals("1")) {
//				    		 holder.rbtnIndoor.setChecked(true);
//						 }else if (themestate.equals("2")) {
//							 holder.rbtnOutdoor.setChecked(true);
//						}
//						
//					 }
//			}
//
//		private void switchViewOnClick(ViewHolder holder, final int position) {
//			holder.rbtnIndoor.setOnClickListener( new ClickEvent() {
//					
//				@Override
//				public void singleClick(View v) {
////					if (isChecked) {
//						
//						//外网防区设置
//						insertOrUpdateAletTheme(1,position);   //alerttype 1室内
//						 
////					}else {
////						System.out.println("发送指令关闭传感器");
////					}
//					
//				}
//			});
//			holder.rbtnOutdoor.setOnClickListener( new ClickEvent() {
//				
//				@Override
//				public void singleClick(View v){
////					if (isChecked) {
//						insertOrUpdateAletTheme(2,position);   //alerttype 2室外
////					}else {
////						System.out.println("发送指令关闭传感器");
////					}
//				}
//			});
//			
//		 }
//
//		}
//	    
//	    /**
//	     * 添加管理情景到本地theme表
//	     * @param position
//	     */
//		private void addOrUpdateThemeToLocalSql(Theme alertTheme) {
//			String devstrid=alertTheme.getDevstrid();
//		
//			   //判断是否是新紧急情景
//		    Theme theme=new ThemeDao(DefenceAreaSetActivity.this).getThemeByUseridAnddevstrid(SystemValue.userid,devstrid);
//		     if (theme==null) {
//		    	 /**************本地数据库theme表中添加新情景*******************/
//				 new ThemeDao(DefenceAreaSetActivity.this).addOrUpdate(alertTheme);
//			 }else {
//				 new ThemeDao(DefenceAreaSetActivity.this).updateThemeByUserIdAndDevstrid(alertTheme);
//			 }
//			
//		}
//	    
//	    /**
//		   * 将安防设备加入情景
//		 * @param position
//		 * @param controlcmd
//		 */
//		private void insertOrUpdateAletTheme(int alerttype,int position){
//			
//			Device devAlert=new Device();
//			devAlert=devlist.get(position);
//		    final String devname=devAlert.getDevname();
//		    String themestate=String.valueOf(alerttype);
//			
//		    String themename=devAlert.getSpacetypnename()+"/"+devname;
//		    final Theme alertTheme=new Theme();
//		    alertTheme.setThemeid(0);
//		    alertTheme.setDevid(devAlert.getId());
//		    alertTheme.setDevstrid(devAlert.getDevId());
//		    alertTheme.setThemename(themename);
//		    alertTheme.setUserid(SystemValue.userid);
//		    alertTheme.setThemestate(themestate);
//		     
//		     Gson themeGson=new Gson();
//		     String strTheme=themeGson.toJson(alertTheme);
//		            	
//		   	String methodName = "insertOrUpdateAlertTheme";   
//	       	String url = WebService.WEB_SCENESER_URL;
//
//	    	 HashMap<String,Object> properties=new HashMap<String,Object>();
//	    	 properties.put("themee",strTheme);
//	    	 properties.put("AlertType",alerttype);  //【注意】联动设置调用以前的紧急情景接口
//	    	 
//	    	 System.out.println("===安防设备所处区域====="+alerttype);
//	    	 
//	    	 WebService.callWebService(url, methodName, properties,new WebServiceCallBack() {  
//		            
//	    	     //WebService接口返回的数据回调到这个方法中  
//		          @Override  
//		          public void callBack(TranObject tranObject) { 
//		        	  switch (tranObject.getTranType()) {
//						case NETMSG:   
//							int netstatue=(Integer) tranObject.getObject();
//							if (NetValue.NONET==netstatue) { 
//								showCustomToast("远程连接失败，请检查网络！");
//							}
//		        	       break;
//						case  DEVMSG:
//							 SoapObject soapObject=(SoapObject) tranObject.getObject();
//							 String rstJsonArray = soapObject.getProperty(0).toString();  //转换为jsonArray串
//							 System.out.println("===服务器端返回的安防情景id===="+rstJsonArray);
//							 aleThemeid=Integer.valueOf(rstJsonArray);
//							 
//						    //更新本地数据库的themeid
//							 alertTheme.setThemeid(aleThemeid);
//							 alertTheme.setThemetype(3);  //安防类情景类型3
//							 addOrUpdateThemeToLocalSql(alertTheme);   //外网添加成功后同步到本地
//							 
//							break;
//		        	  }
//		          }
//	    	    });
//	    }
//		
//		
//		
//		@OnClick(R.id.tv_head_back)
//		public void back(){
//			finish();
//		}
//		@Override
//		protected void initViews() {
//			// TODO Auto-generated method stub
//			
//		}
//		@Override
//		protected void initEvents() {
//			// TODO Auto-generated method stub
//			
//		}
//	
//}
