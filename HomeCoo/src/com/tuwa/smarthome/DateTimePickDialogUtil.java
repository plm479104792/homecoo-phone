package com.tuwa.smarthome;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.DateTimePickDialogUtil.SwitchsAdapter.Holder;
import com.tuwa.smarthome.activity.DeviceTimerSetActivity;
import com.tuwa.smarthome.activity.DeviceTimerSetActivity.InfraTaskCallBack;
import com.tuwa.smarthome.activity.TimeTaskActivity;
import com.tuwa.smarthome.adapter.SpaceDeviceViewAdapter;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.Schedule;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.util.PreferencesUtils;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import et.song.remotestar.ActivityMain;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * 
 *           } });
 * 
 * @author
 */
@SuppressLint("InflateParams")
public class DateTimePickDialogUtil implements OnDateChangedListener,OnTimeChangedListener,
          OnCheckedChangeListener{
	private DatePicker datePicker;
	private TimePicker timePickerSingle,timePickerWeek;
	private AlertDialog ad,updateDialog;
	private String dateTime;
	private String initDateTime;
	private Device device;  //activity传递的device
	private Activity activity;
	private EditText et_addtask;
	
	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private View[] timerViews; // 动态生成的view数组
	private RadioGroup rg_time;
	private List<Device> devlist = new ArrayList<Device>();
	private GridView gvSingleDevice,gvMultiDevice;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	final char ON = 1 + '0'; // 字符开
	final char OFF = 0 + '0'; // 字符关
	final char WinON = 5 + '0'; // 窗帘暂停
	final char WinPK = 6 + '0'; // 窗帘暂停
	final char WinOFF = 7 + '0'; // 窗帘暂停
	public String sLightVal; // 可调关的亮度
	private static char[] strStaArr = new char[4]; // 字符数组代表多路开关状态
	private static char[] strWeekArr = new char[7]; // 字符数组代表定时的周
	private TextView tvScheduleName;
	private TextView tvScheduleName2;
	private CheckBox cbMon;
	private CheckBox cbTue;
	private CheckBox cbWed;
	private CheckBox cbTur;
	private CheckBox cbFri;
	private CheckBox cbSat;
	private CheckBox cbSun;
	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialogUtil(Activity activity, String initDateTime,Device device,int scheduleType) {
		this.activity = activity;
		this.initDateTime = initDateTime;
        this.device=device;
        devlist.add(device);
        
        if(scheduleType==1){    //scheduleType   1:红外   0:其它
            //回调设置红外任务
            ((DeviceTimerSetActivity) activity).callBackName(new InfraTaskCallBack() {
    			
    			@Override
    			public void callBack(String string) {
    				   tvScheduleName.setText("遥控任务:"+string);
    			       tvScheduleName2.setText("遥控任务:"+string);
    			       Log.i("343", "遥控任务:"+string);
    			}
    		});
        }
  
	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar = Calendar.getInstance();
		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + "-"
					+ calendar.get(Calendar.MONTH) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH) + ""
					+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
					+ calendar.get(Calendar.MINUTE);
		}

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
	}
	
	//初始化时钟
	public void initTimePicker(TimePicker timePicker,String strTime) {
		if(strTime.equals("")){
			timePicker.setCurrentHour(Calendar.HOUR_OF_DAY);
			timePicker.setCurrentMinute(Calendar.MINUTE);
		}else{
			System.out.println("=====设定的时间为====="+strTime);
			String timestr[] = null;
		    timestr=strTime.split(":");
		    
		    if(timestr!=null){
				int currentHour = Integer.valueOf(timestr[0].trim()).intValue();
				int currentMinute = Integer.valueOf(timestr[1].trim()).intValue();
				
				timePicker.setCurrentHour(currentHour);
				timePicker.setCurrentMinute(currentMinute);
		    }
		}
	}
	

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	
	public AlertDialog dateTimePicKDialog(final Handler handler ) {
		
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.layout_time_picker, null);
		viewPager=(ViewPager) dateTimeLayout.findViewById(R.id.vp_timer_vpager);
		pageViews = new ArrayList<View>();
		timerViews = new View[2];
		timerViews[0]= activity.getLayoutInflater().inflate(R.layout.item_single_timer, null);
		timerViews[1]=activity.getLayoutInflater().inflate(R.layout.item_cycle_timer, null);
		pageViews.add(timerViews[0]);
		pageViews.add(timerViews[1]);
		viewPager.setAdapter(new SpaceDeviceViewAdapter(pageViews));
		rg_time = (RadioGroup) dateTimeLayout.findViewById(R.id.rg_timer);
		rg_time.setOnCheckedChangeListener(this);
		
		//初始化第一个view
		initScheduleSingleView();
		
		//初始化第二个view
		initScheduleMultiView();
	
//		//显示定时预设的设备状态
//		if(SystemValue.timerAddType.equals(SystemValue.TIMER_DEVICE)){
//			SwitchsAdapter deviceAdpter = new SwitchsAdapter();
//			gvSingleDevice.setAdapter(deviceAdpter);
//			gvMultiDevice.setAdapter(deviceAdpter);
//		}

		if(SystemValue.timerAddType.equals(SystemValue.TIMER_DEVICE)){    //设备定时
			SwitchsAdapter deviceAdpter = new SwitchsAdapter();
			gvSingleDevice.setAdapter(deviceAdpter);
			gvMultiDevice.setAdapter(deviceAdpter);
			
			
			ad = new AlertDialog.Builder(activity)
			.setView(dateTimeLayout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
                    Schedule schedule=new Schedule();
                   int currentItem= viewPager.getCurrentItem();
                   if(currentItem==0){  //一次性定时
                	   schedule.setDeviceNo(device.getDeviceNo());
                	   schedule.setDeviceState(device.getDeviceStateCmd());
                	   schedule.setGatewayNo(device.getGatewayNo());
                	   schedule.setPhoneNum(SystemValue.phonenum);
                	   String strdate=getDateByCalendar();
                	   schedule.setRiqi(strdate);
                	   schedule.setScheduleName(device.getDeviceName());
                	   schedule.setStrategy("1");   //一次性执行完的
                	   schedule.setState("1");  //一次性执行完的
                	   String strtime=getTimeByCalendar(timePickerSingle)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                	   String strPacket=WebPacketUtil.converDevice2PacketStr(device);
                	   schedule.setPacketData(strPacket);
                	   
                   }else{   //周期性定时
                	   schedule.setDeviceNo(device.getDeviceNo());
                	   schedule.setDeviceState(device.getDeviceStateCmd());
                	   schedule.setGatewayNo(device.getGatewayNo());
                	   schedule.setPhoneNum(SystemValue.phonenum);
                	   schedule.setRiqi("");
                	   schedule.setScheduleName(device.getDeviceName());
                	   schedule.setStrategy("2");  //周期重复
                	   schedule.setState("0");  //一次性执行完的
                	   String strWeek=new String(strWeekArr);
                	   System.out.println("定时周期为："+strWeek);
                	   schedule.setXingqi(strWeek);
                	   String strtime=getTimeByCalendar(timePickerWeek)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                	   String strPacket=WebPacketUtil.converDevice2PacketStr(device);
                	   schedule.setPacketData(strPacket);
                   }
                   sendDeviceTimerToServer(schedule);  //发送定时设置到服务器
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			}).show();
			
		}else if(SystemValue.timerAddType.equals(SystemValue.TIMER_SCENE)){  //情景定时
			final Theme theme=SystemValue.stheme;
			tvScheduleName.setText(theme.getThemeName());
			tvScheduleName2.setText(theme.getThemeName());
		
			ad = new AlertDialog.Builder(activity)
			.setView(dateTimeLayout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
                    Schedule schedule=new Schedule();
                   int currentItem= viewPager.getCurrentItem();
                   if(currentItem==0){  //一次性定时
                	   schedule.setThemeNo(theme.getThemeNo());
                	   schedule.setGatewayNo(theme.getGatewayNo());
                	   schedule.setPhoneNum(SystemValue.phonenum);
                	   String strdate=getDateByCalendar();
                	   schedule.setRiqi(strdate);
                	   schedule.setScheduleName(theme.getThemeName());
                	   schedule.setStrategy("1");   //一次性执行完的
                	   schedule.setState("1");    //一次性执行完的
                	   String strtime=getTimeByCalendar(timePickerSingle)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                	   String strPacket=WebPacketUtil.converTheme2PacketStr(theme);
                	   schedule.setPacketData(strPacket);
                	   
                   }else{   //周期性定时
                	   schedule.setThemeNo(theme.getThemeNo());
                	   schedule.setGatewayNo(theme.getGatewayNo());
                	   schedule.setPhoneNum(SystemValue.phonenum);
                	   schedule.setRiqi("");
                	   schedule.setScheduleName(theme.getThemeName());
                	   schedule.setStrategy("2");  //周期重复
                	   schedule.setState("0");  //周期重复
                	   String strWeek=new String(strWeekArr);
                	   System.out.println("定时周期为："+strWeek);
                	   schedule.setXingqi(strWeek);
                	   String strtime=getTimeByCalendar(timePickerWeek)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                	   String strPacket=WebPacketUtil.converTheme2PacketStr(theme);
                	   schedule.setPacketData(strPacket);
                   }
                   sendDeviceTimerToServer(schedule);  //发送定时设置到服务器
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			}).show();
		}else if(SystemValue.timerAddType.equals(SystemValue.TIMER_MUSIC)){   //音乐定时
			final String songName=SystemValue.smusicName;
			tvScheduleName.setText(songName);
			tvScheduleName2.setText(songName);
		
			ad = new AlertDialog.Builder(activity)
			.setView(dateTimeLayout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
                    Schedule schedule=new Schedule();
                   int currentItem= viewPager.getCurrentItem();
                   if(currentItem==0){  //一次性定时
                	   schedule.setThemeNo("");
                	   schedule.setGatewayNo(SystemValue.gatewayid);
                	   schedule.setPhoneNum(SystemValue.phonenum);
                	   String strdate=getDateByCalendar();
                	   schedule.setRiqi(strdate);
                	   schedule.setScheduleName(songName);
                	   schedule.setStrategy("1");   //一次性执行完的
                	   schedule.setState("3");    // 表示音乐
                	   String strtime=getTimeByCalendar(timePickerSingle)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
//                	   String strPacket=WebPacketUtil.converTheme2PacketStr(theme);
                	   schedule.setPacketData("");
                	   
                   }else{   //周期性定时
                	   schedule.setThemeNo("");
                	   schedule.setGatewayNo(SystemValue.gatewayid);
                	   schedule.setPhoneNum(SystemValue.phonenum);
                	   schedule.setRiqi("");
                	   schedule.setScheduleName(songName);
                	   schedule.setStrategy("2");  //周期重复
                	   schedule.setState("3");  //表示音乐
                	   String strWeek=new String(strWeekArr);
                	   System.out.println("定时周期为："+strWeek);
                	   schedule.setXingqi(strWeek);
                	   String strtime=getTimeByCalendar(timePickerWeek)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
//                	   String strPacket=WebPacketUtil.converTheme2PacketStr(theme);
                	   schedule.setPacketData("");
                   }
                   sendDeviceTimerToServer(schedule);  //发送定时设置到服务器
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			}).show();
		}else if(SystemValue.timerAddType.equals(SystemValue.TIMER_INFRA)){  //红外定时设置框
	        tvScheduleName.setText("点击设置遥控任务");
	        tvScheduleName2.setText("点击设置遥控任务");
	        
	        tvScheduleName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					 PreferencesUtils.putString(activity, "OPERATION_TYPE", "SCENE_INFRA_SET");
					
					 Intent yaokongIntent=new Intent(activity,ActivityMain.class);
					 activity.startActivity(yaokongIntent);
					 
				}
			});
	        
	        if(ad==null){
			ad = new AlertDialog.Builder(activity)
			.setView(dateTimeLayout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// 点击确定对话框不消失
					try {
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					if(SystemValue.sInfraDevice!=null){
					   Schedule schedule=new Schedule();
	                   int currentItem= viewPager.getCurrentItem();
	                   if(currentItem==0){  //一次性定时
	                	   schedule.setDeviceNo(NetValue.DEVID_INFRA);
	                	   schedule.setDeviceState(SystemValue.sInfraData);
	                	   schedule.setGatewayNo(SystemValue.gatewayid);
	                	   schedule.setPhoneNum(SystemValue.phonenum);
	                	   String strdate=getDateByCalendar();
	                	   schedule.setRiqi(strdate);
	                	   schedule.setScheduleName(SystemValue.sInfraName);
	                	   schedule.setStrategy("1");   //一次性执行完的
	                	   schedule.setState("4");    // 表示定时遥控
	                	   String strtime=getTimeByCalendar(timePickerSingle)+":00"; //填充为时分秒格式
	                	   schedule.setShij(strtime);
	                	   String strPacket="";
	                	   if(SystemValue.sInfraDevice!=null){
	                		  strPacket=WebPacketUtil.converDevice2PacketStr(SystemValue.sInfraDevice);
	                		  schedule.setPacketData(strPacket);
	                	   }
	                	   
	                   }else{   //周期性定时
	                 	   schedule.setDeviceNo(NetValue.DEVID_INFRA);
	                	   schedule.setDeviceState(SystemValue.sInfraData);
	                	   schedule.setGatewayNo(SystemValue.gatewayid);
	                	   schedule.setPhoneNum(SystemValue.phonenum);
	                	   schedule.setRiqi("");
	                	   schedule.setScheduleName(SystemValue.sInfraName);
	                	   schedule.setStrategy("2");  //周期重复
	                	   schedule.setState("4");  //表示定时遥控
	                	   String strWeek=new String(strWeekArr);
	                	   System.out.println("定时周期为："+strWeek);
	                	   schedule.setXingqi(strWeek);
	                	   String strtime=getTimeByCalendar(timePickerWeek)+":00"; //填充为时分秒格式
	                	   schedule.setShij(strtime);
	                	   
	                	   String strPacket="";
	                	   if(SystemValue.sInfraDevice!=null){
	                		  strPacket=WebPacketUtil.converDevice2PacketStr(SystemValue.sInfraDevice);
	                		  schedule.setPacketData(strPacket);
	                	   }
	                	  
	                   }
	                   sendDeviceTimerToServer(schedule);  //发送定时设置到服务器
	                   
	                   // 点击确定对话框消失
	                   try {
	   					Field field = dialog.getClass().getSuperclass()
	   							.getDeclaredField("mShowing");
	   					field.setAccessible(true);
	   					field.set(dialog, true);
		   				} catch (Exception e) {
		   					e.printStackTrace();
		   				}
					}else {
					   ToastUtils.showToast(activity, "请先设置红外定时任务", 1000);
					}

				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
			        // 点击确定对话框消失
	                   try {
	   					Field field = dialog.getClass().getSuperclass()
	   							.getDeclaredField("mShowing");
	   					field.setAccessible(true);
	   					field.set(dialog, true);
		   				} catch (Exception e) {
		   					e.printStackTrace();
		   				}
				}
			}).show();
			
	      }
		}
		
		onDateChanged(null, 0, 0, 0);
		return ad;
	}
	
	
	/**
	 * 情景及设置修改设置对话框
	 * @param handler
	 * @return
	 */
    public AlertDialog updateDateTimePicKDialog(final Handler handler ) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.layout_time_picker, null);
		viewPager=(ViewPager) dateTimeLayout.findViewById(R.id.vp_timer_vpager);
		pageViews = new ArrayList<View>();
		timerViews = new View[2];
		timerViews[0]= activity.getLayoutInflater().inflate(R.layout.item_single_timer, null);
		timerViews[1]=activity.getLayoutInflater().inflate(R.layout.item_cycle_timer, null);
		pageViews.add(timerViews[0]);
		pageViews.add(timerViews[1]);
		viewPager.setAdapter(new SpaceDeviceViewAdapter(pageViews));
		rg_time = (RadioGroup) dateTimeLayout.findViewById(R.id.rg_timer);
		rg_time.setOnCheckedChangeListener(this);
		
		//初始化第一个view
		initScheduleSingleView();
		
		//初始化第二个view
		initScheduleMultiView();
		
		//显示定时预设的设备状态
		if(SystemValue.timerAddType.equals(SystemValue.TIMER_DEVICE)){
			SwitchsAdapter deviceAdpter = new SwitchsAdapter();
			gvSingleDevice.setAdapter(deviceAdpter);
			gvMultiDevice.setAdapter(deviceAdpter);
		}
	
		//定时情景的修改
		 if(SystemValue.timerUpdateType.equals(SystemValue.TIMER_SCENE)){  //情景定时修改
			final Schedule schedule=SystemValue.schedule;
			tvScheduleName.setText(schedule.getScheduleName());
			tvScheduleName2.setText(schedule.getScheduleName());
			
            //判断并定位到周期设置的页面
			String statestragy=schedule.getStrategy();
             if(statestragy.equals("2")){ //周期性的
             	viewPager.setCurrentItem(1);
        		initTimePicker(timePickerWeek,schedule.getShij());  //初始化已经设置过的时间
             	String strweek=schedule.getXingqi();
    		    initWeekShow(strweek);
             }
		
			updateDialog = new AlertDialog.Builder(activity)
			.setView(dateTimeLayout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
               
                   int currentItem= viewPager.getCurrentItem();
                   if(currentItem==0){  //一次性定时
                	   String strdate=getDateByCalendar();
                	   schedule.setRiqi(strdate);
                	   schedule.setStrategy("1");   //一次性执行完的
                	   schedule.setState("1");    //一次性执行完的
                	   String strtime=getTimeByCalendar(timePickerSingle)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                	   
                   }else{   //周期性定时
                	   schedule.setRiqi("");
                	   schedule.setStrategy("2");  //周期重复
                	   schedule.setState("0");  //周期重复
                	   String strWeek=new String(strWeekArr);
                	   System.out.println("定时周期为："+strWeek);
                	   schedule.setXingqi(strWeek);
                	   String strtime=getTimeByCalendar(timePickerWeek)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                   }
                   
                   updateDeviceTimerToServer(schedule);  //发送定时设置到服务器
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			}).show();
		}else if(SystemValue.timerUpdateType.equals(SystemValue.TIMER_DEVICE)){    //设备定时
			
			final Schedule schedule=SystemValue.schedule;
			
            //判断并定位到周期设置的页面
			String statestragy=schedule.getStrategy();
             if(statestragy.equals("2")){ //周期性的
             	viewPager.setCurrentItem(1);
        		initTimePicker(timePickerWeek,schedule.getShij());  //初始化已经设置过的时间
             	String strweek=schedule.getXingqi();
    		    initWeekShow(strweek);
             }
			
            updateDialog = new AlertDialog.Builder(activity)
			.setView(dateTimeLayout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
                  
                   int currentItem= viewPager.getCurrentItem();
                   if(currentItem==0){  //一次性定时
                	   schedule.setDeviceState(device.getDeviceStateCmd());
                	   String strdate=getDateByCalendar();
                	   schedule.setRiqi(strdate);
                	   schedule.setStrategy("1");   //一次性执行完的
                	   schedule.setState("1");  //一次性执行完的
                	   String strtime=getTimeByCalendar(timePickerSingle)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                	   String strPacket=WebPacketUtil.converDevice2PacketStr(device);
                	   schedule.setPacketData(strPacket);
                	   
                   }else{   //周期性定时
          		    	
                	   schedule.setDeviceState(device.getDeviceStateCmd());
                	   schedule.setRiqi("");
                	   schedule.setStrategy("2");  //周期重复
                	   schedule.setState("0");  //一次性执行完的
                	   String strWeek=new String(strWeekArr);
                	   System.out.println("定时周期为："+strWeek);
                	   schedule.setXingqi(strWeek);
                	   String strtime=getTimeByCalendar(timePickerWeek)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                	   String strPacket=WebPacketUtil.converDevice2PacketStr(device);
                	   schedule.setPacketData(strPacket);
                   }
                   
                   updateDeviceTimerToServer(schedule);  //发送定时设置到服务器
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			}).show();
		}else if (SystemValue.timerUpdateType.equals(SystemValue.TIMER_MUSIC)) {			//音乐定时
			final Schedule schedule=SystemValue.schedule;
			tvScheduleName.setText(schedule.getScheduleName());
			tvScheduleName2.setText(schedule.getScheduleName());
            //判断并定位到周期设置的页面
			String statestragy=schedule.getStrategy();
             if(statestragy.equals("2")){ //周期性的
             	viewPager.setCurrentItem(1);
        		initTimePicker(timePickerWeek,schedule.getShij());  //初始化已经设置过的时间
             	String strweek=schedule.getXingqi();
    		    initWeekShow(strweek);
             }
		
			updateDialog = new AlertDialog.Builder(activity)
			.setView(dateTimeLayout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
               
                   int currentItem= viewPager.getCurrentItem();
                   if(currentItem==0){  //一次性定时
                	   String strdate=getDateByCalendar();
                	   schedule.setRiqi(strdate);
                	   schedule.setStrategy("1");   //一次性执行完的
                	   schedule.setState("3");    //一次性执行完的    3:表示音乐
                	   String strtime=getTimeByCalendar(timePickerSingle)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                	   
                   }else{   //周期性定时
                	   schedule.setRiqi("");
                	   schedule.setStrategy("2");  //周期重复
                	   schedule.setState("3");  //表示音乐
                	   String strWeek=new String(strWeekArr);
                	   System.out.println("定时周期为："+strWeek);
                	   schedule.setXingqi(strWeek);
                	   String strtime=getTimeByCalendar(timePickerWeek)+":00"; //填充为时分秒格式
                	   schedule.setShij(strtime);
                   }
                   
                   updateDeviceTimerToServer(schedule);  //发送定时设置到服务器
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			}).show();
		}

		onDateChanged(null, 0, 0, 0);
		return updateDialog;
	}

	//初始化周期定时对话框内容
	private void initScheduleMultiView() {
		timePickerWeek = (TimePicker) timerViews[1].findViewById(R.id.timepicker_week);
		timePickerWeek.setIs24HourView(true);
		gvMultiDevice = (GridView) timerViews[1].findViewById(R.id.gv_timer_devstate);
		
		initTimePicker(timePickerWeek, "");
		
		cbMon = (CheckBox) timerViews[1].findViewById(R.id.cb_mon);
		cbTue = (CheckBox) timerViews[1].findViewById(R.id.cb_tue);
		cbWed = (CheckBox) timerViews[1].findViewById(R.id.cb_wed);
		cbTur = (CheckBox) timerViews[1].findViewById(R.id.cb_thu);
		cbFri = (CheckBox) timerViews[1].findViewById(R.id.cb_fri);
		cbSat = (CheckBox) timerViews[1].findViewById(R.id.cb_sat);
		cbSun = (CheckBox) timerViews[1].findViewById(R.id.cb_sun);
		
		initOnCheckedlisener();
		
	}

	//初始化单次定时对话框内容
    private void initScheduleSingleView() {
		viewPager.setCurrentItem(0);
		datePicker =(DatePicker) timerViews[0].findViewById(R.id.datepicker);
		tvScheduleName = (TextView) timerViews[0].findViewById(R.id.tv_schedule_name);
		timePickerSingle = (TimePicker) timerViews[0].findViewById(R.id.time_picker_single);
		gvSingleDevice= (GridView) timerViews[0].findViewById(R.id.gv_timer_devstate);
		tvScheduleName2 = (TextView) timerViews[1].findViewById(R.id.tv_schedule_name2);
		et_addtask=(EditText) timerViews[0].findViewById(R.id.et_addtask);
		init(datePicker, timePickerSingle);            //初始化显示日期及时间
		timePickerSingle.setIs24HourView(true);
		
	}


	//星期多选框状态改变进行监听
	private void initOnCheckedlisener() {
		cbMon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					strWeekArr[0]=ON;
				}else{
					strWeekArr[0]=OFF;
				}
			}
		});
		
		 cbTue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						strWeekArr[1]=ON;
					}else{
						strWeekArr[1]=OFF;
					}
				}
			});
		
		cbWed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					strWeekArr[2]=ON;
					  System.out.println("定时周期为："+new String(strWeekArr));
				}else{
					strWeekArr[2]=OFF;
					  System.out.println("定时周期为："+new String(strWeekArr));
				}
			}
		});
		cbTur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					strWeekArr[3]=ON;
				}else{
					strWeekArr[3]=OFF;
				}
			}
		});
		cbFri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					strWeekArr[4]=ON;
				}else{
					strWeekArr[4]=OFF;
				}
			}
		});
		cbSat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					strWeekArr[5]=ON;
				}else{
					strWeekArr[5]=OFF;
				}
			}
		});
		cbSun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					strWeekArr[6]=ON;
				}else{
					strWeekArr[6]=OFF;
				}
			}
		});
		
	}

	//显示之前设置的定时周期
	protected void initWeekShow(String strweek) {
		strWeekArr = strweek.toCharArray();
		if(strWeekArr[0]==ON){
			cbMon.setChecked(true);
		}
		if(strWeekArr[1]==ON){
			cbTue.setChecked(true);
		}
		if(strWeekArr[2]==ON){
			cbWed.setChecked(true);
		}
		if(strWeekArr[3]==ON){
			cbTur.setChecked(true);
		}
		if(strWeekArr[4]==ON){
			cbFri.setChecked(true);
		}
		if(strWeekArr[5]==ON){
			cbSat.setChecked(true);
		}
		if(strWeekArr[6]==ON){
			cbSun.setChecked(true);
		}
	}

	//发送定时设置到服务器
	protected void sendDeviceTimerToServer(final Schedule schedule) {
		Gson gson=new Gson();
		String scheduleJson=gson.toJson(schedule);
		
		RequestParams params = new RequestParams();
  		params.addBodyParameter("scheduleJson",scheduleJson);
  		
  		System.out.println("===发送到服务器的定时串==="+scheduleJson);
  		
  		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
  		utils.send(HttpMethod.POST, NetValue.SCHEDULE_ADD_URL, params,new RequestCallBack<String>() {

  			@Override
  			public void onFailure(HttpException arg0, String arg1) {
  				ToastUtils.showToast(activity, "请检查手机网络连接",SystemValue.MSG_TIME);
  			}

  			@Override
  			public void onSuccess(ResponseInfo<String> arg0) {
  				Gson gson = new Gson();
  				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
  				if (message != null) {
  					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
  						System.out.println("添加定时服务器返回"+ message.getMessageInfo());
  						
  						double scheduleId=(Double) message.getObject();
  						int id=(int) scheduleId;
  						schedule.setScheduleId(id);
 			
  						if(SystemValue.sAddrfreshType==1){
  							 ((DeviceTimerSetActivity) activity).InfraAddScheduleList(schedule);
  						}else {
  						    ((TimeTaskActivity) activity).timetaskAddScheduleList(schedule);
  						}
  						
  					} else {
  						ToastUtils.showToast(activity, message.getMessageInfo(), 1000);
//  				    System.out.println("添加定时服务器返回"+ message.getMessageInfo());
  					}
  				}
  			}
  		});
	}
	
	  //更新定时设置到服务器
		protected void updateDeviceTimerToServer(final Schedule schedule) {
			schedule.setCreateTime(null);
			Gson gson=new Gson();
			String scheduleJson=gson.toJson(schedule);
			
			RequestParams params = new RequestParams();
	  		params.addBodyParameter("scheduleJson",scheduleJson);
	  		
	  		System.out.println("===发送到服务器的定时串==="+scheduleJson);
	  		
	  		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
	  		utils.send(HttpMethod.POST, NetValue.SCHEDULE_UPDATE_URL, params,new RequestCallBack<String>() {

	  			@Override
	  			public void onFailure(HttpException arg0, String arg1) {
	  				ToastUtils.showToast(activity, "请检查手机网络连接",SystemValue.MSG_TIME);
	  			}

	  			@Override
	  			public void onSuccess(ResponseInfo<String> arg0) {
	  				Gson gson = new Gson();
	  				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
	  				if (message != null) {
	  					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
	  						System.out.println("更新定时服务器返回"+ message.getMessageInfo());
	  						
	  						//更新本地的数据库，后期添加
	  						SystemValue.schedule=schedule;
	  					   ((TimeTaskActivity) activity).updateScheduleList();
	  						
	  					} else {
//	  						showCustomToast(message.getMessageInfo());
	  						System.out.println("更新定时服务器返回"+ message.getMessageInfo());
	  					}
	  				}
	  			}
	  		});
		}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePickerSingle.getCurrentHour(),
				timePickerSingle.getCurrentMinute());

		dateTime = sdf.format(calendar.getTime());
//		ad.setTitle(dateTime);
		System.out.println("===时间改变事件，当前的日期为：==="+dateTime);
	}
	
	/**
	 * 获取当前的日历的时间
	 * @return
	 */
	private String getDateByCalendar(){
		String strdate="";
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(),
				datePicker.getMonth(),
				datePicker.getDayOfMonth());

		strdate = dateFormat.format(calendar.getTime());
		System.out.println("===获取的日历的日期为：==="+strdate);
		return strdate;
	}
    
	
	private String getTimeByCalendar(TimePicker timePicker){
	    String strHour=String.valueOf(timePicker.getCurrentHour());
	    String strMinute=String.valueOf(timePicker.getCurrentMinute());
	    
	    System.out.println("dingshi小时"+strHour.length());
	    
	    if(strHour.length()==1){
	    	strHour="0"+strHour;
	    }
	    if(strMinute.length()==1){
	    	strMinute="0"+strMinute;
	    }
	    
		String strtime=strHour+":"+strMinute;
		System.out.println("===获取的日历的时间为：==="+strtime);
		return strtime;
	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_single_timer:
			System.out.println("定时_单次");
			viewPager.setCurrentItem(0);
			break;
		case R.id.rb_cycle_timer:
			String strweek="0000000";
			strWeekArr = strweek.toCharArray();
			
			System.out.println("定时_周期");
			viewPager.setCurrentItem(1);

			break;
		}
		
	}
	
	
	
    /**
	 * 调整FrameLayout大小
	 * @param tp
	 */
	private void resizePikcer(FrameLayout tp){
		List<NumberPicker> npList = findNumberPicker(tp);
		for(NumberPicker np:npList){
			resizeNumberPicker(np);
		}
	}
	
	/**
	 * 得到viewGroup里面的numberpicker组件
	 * @param viewGroup
	 * @return
	 */
	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup){
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;
		if(null != viewGroup){
			for(int i = 0;i<viewGroup.getChildCount();i++){
				child = viewGroup.getChildAt(i);
				if(child instanceof NumberPicker){
					npList.add((NumberPicker)child);
				}
				else if(child instanceof LinearLayout){
					List<NumberPicker> result = findNumberPicker((ViewGroup)child);
					if(result.size()>0){
						return result;
					}
				}
			}
		}
		return npList;
	}

	
	/*
	 * 调整numberpicker大小
	 */
	private void resizeNumberPicker(NumberPicker np){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50,50);
		params.setMargins(5, 0, 5, 0);
		np.setLayoutParams(params);
	}

	// list数据适配器
		public class SwitchsAdapter extends BaseAdapter {

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
			public View getView(int position, View converView, ViewGroup parent) {
				View view = converView;
				Holder holder = null;
				if (view != null) {
					holder = (Holder) view.getTag();
				} else {
					
				   LayoutInflater inflater = activity.getLayoutInflater();
				   view= inflater.inflate(R.layout.item_devices_all, null);
					
					holder = new Holder(view);
					view.setTag(holder);
				}

				Device devdto = devlist.get(position);
				String spacename = WebPacketUtil.getSpaceName(devdto.getSpaceNo());   //根据phonespaceid获取spacename
				holder.tvDevSite.setText(spacename);
				holder.tvDevName.setText("/" + devdto.getDeviceName());

				// 点击事件的按钮做标记
				holder.tgBtn1.setTag(position);
				holder.tgBtn2.setTag(position);
				holder.tgBtn3.setTag(position);
				holder.tgBtn4.setTag(position);
				holder.sbLight.setTag(position);
				holder.imBtnOn.setTag(position);
				holder.imBtnPause.setTag(position);
				holder.imBtnOff.setTag(position);

				final int fpostion = position;
				final Holder fHolder = holder;

				showViewByDevtype(holder, devdto); // 根据设备类型显示状态
				
				fHolder.tgBtn1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (!fHolder.tgBtn1.isChecked()) { // 当前状态为开，点击的时候发送off
							cmdControl(fpostion, 1, OFF);
						} else {
							cmdControl(fpostion, 1, ON);
						}
					}
				});

				holder.tgBtn2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (!fHolder.tgBtn2.isChecked()) {
							cmdControl(fpostion, 2, OFF);
						} else {
							cmdControl(fpostion, 2, ON);
						}
					}
				});
				
				holder.tgBtn3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (!fHolder.tgBtn3.isChecked()) {
							cmdControl(fpostion, 3, OFF);
						} else {
							cmdControl(fpostion, 3, ON);
						}
					}
				});
				
				holder.tgBtn4.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!fHolder.tgBtn4.isChecked()) {
							cmdControl(fpostion, 4, OFF);
						} else {
							cmdControl(fpostion, 4, ON);
						}
					}
				});
				
				holder.imBtnOn.setOnClickListener(new OnClickListener() { // 窗帘on
							@Override
							public void onClick(View v) {
								cmdControl(fpostion, 6, WinON);
	
								fHolder.imBtnOn.setBackgroundResource(R.drawable.on1);
								fHolder.imBtnPause.setBackgroundResource(R.drawable.pause0);
								fHolder.imBtnOff.setBackgroundResource(R.drawable.off0);
							}
						});
				holder.imBtnPause.setOnClickListener(new OnClickListener() { // 窗帘pk
							@Override
							public void onClick(View v) {
								cmdControl(fpostion, 6, WinPK);
								fHolder.imBtnOn.setBackgroundResource(R.drawable.on0);
								fHolder.imBtnPause.setBackgroundResource(R.drawable.pause1);
								fHolder.imBtnOff.setBackgroundResource(R.drawable.off0);
							}
	
						});
				holder.imBtnOff.setOnClickListener(new OnClickListener() { // 窗帘off
	
							@Override
							public void onClick(View v) {
								cmdControl(fpostion, 6, WinOFF);
								fHolder.imBtnOn.setBackgroundResource(R.drawable.on0);
								fHolder.imBtnPause.setBackgroundResource(R.drawable.pause0);
								fHolder.imBtnOff.setBackgroundResource(R.drawable.off1);
							}
						});
				

				holder.sbLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() { // 调光灯
							int mprogress = 0;

							@Override
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								mprogress = progress;
							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
							}

							@Override
							public void onStopTrackingTouch(SeekBar seekBar) { // 滑动条停止时触发事件
								sLightVal = String.valueOf(mprogress);
								devlist.get(fpostion).setDeviceStateCmd(sLightVal);
								cmdControl(fpostion, 5, ON);
							}
						});

				return view;
			}
	   
			class Holder {    
				@Bind(R.id.tv_switch_devSite)       TextView tvDevSite;
				@Bind(R.id.tv_switch_devtypeName)   TextView tvDevName;
				@Bind(R.id.tg_btn1)                 ToggleButton tgBtn1;
				@Bind(R.id.tg_btn2)                 ToggleButton tgBtn2;
				@Bind(R.id.tg_btn3)                 ToggleButton tgBtn3;
				@Bind(R.id.tg_btn4)                 ToggleButton tgBtn4;
				@Bind(R.id.sb_switch_light)         SeekBar sbLight; // 调光灯
				@Bind(R.id.im_btnOn) ImageView imBtnOn;
				@Bind(R.id.im_btnPause) ImageView imBtnPause;
				@Bind(R.id.im_btnOff) ImageView imBtnOff;

				public Holder(View view) {
					ButterKnife.bind(this, view);
				}
			}
		}
		
		/**
		 * 根据设备类型显示按钮
		 * 
		 * @param holder
		 * @param devtype
		 */
		private void showViewByDevtype(Holder holder, Device devdto) {

			int devtype = devdto.getDeviceTypeId();
			// 设备状态串转换为状态字符数组
			String mdevState = devdto.getDeviceStateCmd();
			strStaArr = mdevState.toCharArray();

			switch (devtype) {
			case 1:
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.sbLight.setVisibility(View.GONE);
				if (strStaArr[0] == ON) {
					holder.tgBtn1.setChecked(true);
				}else {
					holder.tgBtn1.setChecked(false);
				} 
				break;
		
			case 2:
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.tgBtn2.setVisibility(View.VISIBLE);
				holder.sbLight.setVisibility(View.GONE);
				if (strStaArr[0] == ON) {
					holder.tgBtn1.setChecked(true);
				}else {
					holder.tgBtn1.setChecked(false);
				}
				if (strStaArr[1] == ON) {
					holder.tgBtn2.setChecked(true);
				}else {
					holder.tgBtn2.setChecked(false);
				}
				break;
			case 3:
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.tgBtn2.setVisibility(View.VISIBLE);
				holder.tgBtn3.setVisibility(View.VISIBLE);

				holder.sbLight.setVisibility(View.GONE);
				if (strStaArr[0] == ON) {
					holder.tgBtn1.setChecked(true);
				}else {
					holder.tgBtn1.setChecked(false);
				} 
				if (strStaArr[1] == ON) {
					holder.tgBtn2.setChecked(true);
				}else {
					holder.tgBtn2.setChecked(false);
				} 
				if (strStaArr[2] == ON) {
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
				if (strStaArr[0] == ON) {
					holder.tgBtn1.setChecked(true);
				}else {
					holder.tgBtn1.setChecked(false);
				}
				if (strStaArr[1] == ON) {   
					holder.tgBtn2.setChecked(true);
				}else {
					holder.tgBtn2.setChecked(false);
				}
				if (strStaArr[2] == ON) {
					holder.tgBtn3.setChecked(true);
				}else {
					holder.tgBtn3.setChecked(false);
				}
				if (strStaArr[3] == ON) {
					holder.tgBtn4.setChecked(true);
				}else {
					holder.tgBtn4.setChecked(false);
				}
				break;
			case 5:
				holder.sbLight.setVisibility(View.VISIBLE);

				holder.tgBtn1.setVisibility(View.GONE);
				holder.tgBtn2.setVisibility(View.GONE);
				holder.tgBtn3.setVisibility(View.GONE);
				holder.tgBtn4.setVisibility(View.GONE);
				holder.sbLight.setMax(9);
				int dLight = 0;
				if (mdevState.endsWith("a")) {
					dLight = 9;
				} else {
					dLight = Integer.valueOf(mdevState);
				}
				holder.sbLight.setProgress(dLight);
				break;
				
			case 8: // 插座
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.sbLight.setVisibility(View.GONE);
				if (strStaArr[0] == ON) {
					holder.tgBtn1.setChecked(true);
				} else {
					holder.tgBtn1.setChecked(false);
				}
				break;
			case 51: // 风扇
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.sbLight.setVisibility(View.GONE);
				if (strStaArr[0] == ON) {
					holder.tgBtn1.setChecked(true);
				}else {
					holder.tgBtn1.setChecked(false);
				} 
				break;
			case 110: // 门磁报警器
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.sbLight.setVisibility(View.GONE);
				if (mdevState.equals("11000000")||mdevState.equals("11")
						||mdevState.equals("01000000")) {  //入网，布防，报警
					holder.tgBtn1.setChecked(true);
				} else { // 显示布防状态为11
					holder.tgBtn1.setChecked(false);
				}
				break;
			case 113: // 红外报警器
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.sbLight.setVisibility(View.GONE);
				if (mdevState.equals("11000000")||mdevState.equals("11")
						||mdevState.equals("01000000")) {  //入网，布防，报警
					holder.tgBtn1.setChecked(true);
				} else { // 显示布防状态为11
					holder.tgBtn1.setChecked(false);
				}
				break;
			case 115: // 燃气报警器
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.sbLight.setVisibility(View.GONE);
				if (mdevState.equals("11000000")||mdevState.equals("11")
						||mdevState.equals("01000000")) {  //入网，布防，报警
					holder.tgBtn1.setChecked(true);
				} else { // 显示布防状态为11
					holder.tgBtn1.setChecked(false);
				}
				break;
			case 118: // 烟感报警器
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.sbLight.setVisibility(View.GONE);
				if (mdevState.equals("11000000")||mdevState.equals("11")
						||mdevState.equals("01000000")) {  //入网，布防，报警
					holder.tgBtn1.setChecked(true);
				} else { // 显示布防状态为11
					holder.tgBtn1.setChecked(false);
				}
				break;
			case 6: // 窗帘
				holder.imBtnOn.setVisibility(View.VISIBLE);
				holder.imBtnPause.setVisibility(View.VISIBLE);
				holder.imBtnOff.setVisibility(View.VISIBLE);
				if (mdevState.equals("10")) {

					holder.imBtnOn.setBackgroundResource(R.drawable.on1);
					holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
					holder.imBtnOff.setBackgroundResource(R.drawable.off0);
				} else if (mdevState.equals("01")) {
					holder.imBtnOn.setBackgroundResource(R.drawable.on0);
					holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
					holder.imBtnOff.setBackgroundResource(R.drawable.off1);
				} else {
					holder.imBtnOn.setBackgroundResource(R.drawable.on0);
					holder.imBtnPause.setBackgroundResource(R.drawable.pause1);
					holder.imBtnOff.setBackgroundResource(R.drawable.off0);
				}
				break;
			case 11: // 窗户
				holder.imBtnOn.setVisibility(View.VISIBLE);
				holder.imBtnPause.setVisibility(View.VISIBLE);
				holder.imBtnOff.setVisibility(View.VISIBLE);
				if (mdevState.equals("10")) {
					holder.imBtnOn.setBackgroundResource(R.drawable.on1);
					holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
					holder.imBtnOff.setBackgroundResource(R.drawable.off0);
				} else if (mdevState.equals("01")) {
					holder.imBtnOn.setBackgroundResource(R.drawable.on0);
					holder.imBtnPause.setBackgroundResource(R.drawable.pause0);
					holder.imBtnOff.setBackgroundResource(R.drawable.off1);
				} else {
					holder.imBtnOn.setBackgroundResource(R.drawable.on0);
					holder.imBtnPause.setBackgroundResource(R.drawable.pause1);
					holder.imBtnOff.setBackgroundResource(R.drawable.off0);
				}
				break;
				
			}
		}
		
		public void cmdControl(int position, int switchid, char ch) {
		
			String sqlCmd = WebPacketUtil.convertCmdToSql(device, switchid, ch); // 控制的devstate转码到本地

			System.out.println("===发送的控制指令存入本地数据库===" + sqlCmd);
			device.setDeviceStateCmd(sqlCmd); // 注意更改后的设备状态先加载到本地
			// //更新device最新状态到本地设备数据库（3）
		}

		
		/**
		 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
		 * 2012-07-02 16:45
		 * @param initDateTime
		 *            初始日期时间值 字符串型
		 * @return Calendar
		 */
		private Calendar getCalendarByInintData(String initDateTime) {
			Calendar calendar = Calendar.getInstance();

			// 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
//			String date = spliteString(initDateTime, "日", "index", "front"); // 日期
//			String time = spliteString(initDateTime, "日", "index", "back"); // 时间
//
//			String yearStr = spliteString(date, "年", "index", "front"); // 年份
//			String monthAndDay = spliteString(date, "年", "index", "back"); // 月日
//
//			String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
//			String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日
//
//			String hourStr = spliteString(time, ":", "index", "front"); // 时
//			String minuteStr = spliteString(time, ":", "index", "back"); // 分
			
			System.out.println("initDateTime"+initDateTime);
			
			String date = spliteString(initDateTime, " ", "index", "front"); // 日期
			String time = spliteString(initDateTime, " ", "index", "back"); // 时间
			
			int currentYear=2016;
			int currentMonth=1;
			int currentDay=1;
			
			if(!date.equals("")){
				String strDate[]=date.split("-");
				 currentYear = Integer.valueOf(strDate[0].trim()).intValue();
				 currentMonth = Integer.valueOf(strDate[1].trim()).intValue() - 1;
				 currentDay = Integer.valueOf(strDate[2].trim()).intValue();
			}
			
			String strTime[]=time.split(":");
			int currentHour = Integer.valueOf(strTime[0].trim()).intValue();
			int currentMinute = Integer.valueOf(strTime[1].trim()).intValue();

//			int currentYear = Integer.valueOf(yearStr.trim()).intValue();
//			int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
//			int currentDay = Integer.valueOf(dayStr.trim()).intValue();
//			int currentHour = Integer.valueOf(hourStr.trim()).intValue();
//			int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

			calendar.set(currentYear, currentMonth, currentDay, currentHour,
					currentMinute);
			return calendar;
		}

		/**
		 * 截取子串
		 * 
		 * @param srcStr
		 *            源串
		 * @param pattern
		 *            匹配模式
		 * @param indexOrLast
		 * @param frontOrBack
		 * @return
		 */
		public static String spliteString(String srcStr, String pattern,
				String indexOrLast, String frontOrBack) {
			String result = "";
			int loc = -1;
			if (indexOrLast.equalsIgnoreCase("index")) {
				loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
			} else {
				loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
			}
			if (frontOrBack.equalsIgnoreCase("front")) {
				if (loc != -1)
					result = srcStr.substring(0, loc); // 截取子串
			} else {
				if (loc != -1)
					result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
			}
			return result;
		}

		@Override
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			System.out.println("时间改变啦。。。");
			
		}


	
}
