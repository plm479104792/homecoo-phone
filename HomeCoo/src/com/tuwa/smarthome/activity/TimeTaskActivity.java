package com.tuwa.smarthome.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.BaseDialog;
import com.tuwa.smarthome.DateTimePickDialogUtil;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.Schedule;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * 加载某个设备的定时任务
 * @author WSN-520
 *
 */
public class TimeTaskActivity extends BaseActivity {
	 @Bind(R.id.tv_head_submit)  TextView tvSubmit;
	 @Bind(R.id.tv_head_back) TextView tvBack;
	 @Bind(R.id.tv_head_title) TextView tvtitle;
	 @Bind(R.id.gv_timetask_list) GridView gvTimetasks;
	 
	 private List<Schedule>  schedulelist=new ArrayList<Schedule>();
	 private TimetaskAdapter timetaskAdpter=null;
	
	 private TextView tvAddTimetask;
	 private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	 private String initDateTime = "2016-5-10 16:38"; // 初始化开始时间
	 private Schedule newTimetask=new Schedule();
	 final char ON = 1 + '0'; // 字符开
	 final char OFF = 0 + '0'; // 字符关
	 final char WinON = 5 + '0'; // 窗帘暂停
	 final char WinPK = 6 + '0'; // 窗帘暂停
	 final char WinOFF = 7 + '0'; // 窗帘暂停
	 public String sLightVal; // 可调关的亮度
	 private static char[] strStaArr = new char[4]; // 字符数组代表多路开关状态
	 private Device pdevdto;   //定时设备全局变量
	 private Theme ptheme;   //定时情景全局变量
	 private String paramNo;
	 private static char[] strWeekArr = new char[7]; // 字符数组代表多路开关状态
     private BaseDialog mBackDialog;
     private int updatePosition;  //修改的shecdule在list中位置
     
     private String songName;		//音乐的全局变量
	 
	 /*辅线程动态刷新页面*/   
	    Handler handler=new Handler(){
		   	 @Override
		   	 public void handleMessage(Message msg){
		   		 switch(msg.what){
		   		 case 0x125:
		   			newTimetask= (Schedule) msg.obj;
		   			schedulelist.add(newTimetask);
		   			 
		   			timetaskAdpter=new TimetaskAdapter(); 
		   			gvTimetasks.setAdapter(timetaskAdpter);
		   		 break;
		   		 case 0x129:
			   			 
		   			timetaskAdpter=new TimetaskAdapter(); 
		   			gvTimetasks.setAdapter(timetaskAdpter);
			      break;
		   		 }
		   	 }
		   };
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_task);
        ButterKnife.bind(TimeTaskActivity.this);//注解工具声明
		
		tvSubmit.setVisibility(View.INVISIBLE);
		tvtitle.setText("定时任务");
		
		initViews();
		
//		setType = getIntent().getStringExtra("settype");
		if(SystemValue.timerAddType.equals(SystemValue.TIMER_DEVICE)){  //加载显示设备状态
			 String deviceJson=getIntent().getStringExtra("deviceJson");
			 Gson gson=new Gson();
			 pdevdto = gson.fromJson(deviceJson, Device.class);
			 paramNo=pdevdto.getDeviceNo();
		}else if(SystemValue.timerAddType.equals(SystemValue.TIMER_SCENE)){  //加载显示情景
			 String themeJson=getIntent().getStringExtra("themeJson");
			 Gson gson=new Gson();
			 ptheme = gson.fromJson(themeJson, Theme.class);
			 paramNo=ptheme.getThemeNo();
		}else if (SystemValue.timerAddType.equals(SystemValue.TIMER_MUSIC)) { //加载显示音乐
			 paramNo=getIntent().getStringExtra("songName");
		}
	
		  getScheduleByDeviceNoFromServer(SystemValue.timerAddType,paramNo); //从服务器获取该设备对应的定时列表
	 
	}
	  
	   /**
	    * 显示已经设置的定时任务
	    * @author WSN-520
	    *
	    */
	   public class TimetaskAdapter  extends BaseAdapter {
			
			@Override
			public int getCount() {
				return schedulelist.size();
			}

			@Override
			public Object getItem(int position) {
				return schedulelist.get(position);
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
				      view = View.inflate(TimeTaskActivity.this,R.layout.item_device_schedule,null);
				      holder = new ViewHolder(view);
				      view.setTag(holder);
				    }
				
				 Schedule schedule=schedulelist.get(position);
				 String time=schedule.getShij();
				 //定时设备的位置显示
				 String deviceNo=schedule.getDeviceNo();
				 String devsite=initDeviceCustomSite(deviceNo);
				 
			     String scheduleName=schedule.getScheduleName();
				 holder.tvTime.setText(time);
				 holder.tvScheduleName.setText(devsite+scheduleName);
			
			     String strategy=schedule.getStrategy();  //1 代表一次执行的 2 代表周期性的
			     
			     //定时的日期显示
				 if(strategy.equals("2")){
					 String week=schedule.getXingqi();
					 strWeekArr = week.toCharArray();
					 String weekName=getWeekName(strWeekArr);
					 holder.tvWeek.setText(weekName);
				 }else if(strategy.equals("1")){
					 String date=schedule.getRiqi();
					 holder.tvWeek.setText(date.toString());
				 }
				 //定时的设备状态显示
				 if(SystemValue.timerAddType.equals(SystemValue.TIMER_DEVICE)){ //设备定时设置
					 pdevdto.setDeviceStateCmd(schedule.getDeviceState()); //获取定时设置的设备状态
			         showViewByDevtype(holder,pdevdto);  //根据设备类型显示状态
//				     switchViewOnClick(holder,position); //列表中开关按键点击事件监听
				 }
				 
				 holder.imScheduleManager.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						scheduleManagerDialog(position);
					}
				});
				 
				
				return view;
			}
			  
			class ViewHolder {
				@Bind(R.id.tv_time)                 TextView tvTime;
				@Bind(R.id.tv_schedule_name)	    TextView tvScheduleName;
				@Bind(R.id.tv_week)                 TextView tvWeek;
				@Bind(R.id.tg_btn1)				    ToggleButton tgBtn1;
				@Bind(R.id.tg_btn2)				    ToggleButton tgBtn2;
				@Bind(R.id.tg_btn3)  				ToggleButton tgBtn3;
				@Bind(R.id.tg_btn4)				    ToggleButton tgBtn4;
				@Bind(R.id.sb_switch_light)			SeekBar sbLight; // 调光灯
				@Bind(R.id.im_btnOn)				ImageView imBtnOn;
				@Bind(R.id.im_btnPause)				ImageView imBtnPause;
				@Bind(R.id.im_btnOff)				ImageView imBtnOff;
				@Bind(R.id.im_schedule_manager)		ImageView imScheduleManager;
				
				
			    public ViewHolder(View view) {
			    	ButterKnife.bind(this,view);
			    }
			  }

			private void showViewByDevtype(ViewHolder holder,Device devdto) {
				 
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
					} else {
						holder.tgBtn1.setChecked(false);
					}
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
				case 110: // 门磁报警器
					holder.tgBtn1.setVisibility(View.VISIBLE);
					holder.sbLight.setVisibility(View.GONE);
					if (mdevState.equals("00") || mdevState.equals("11")) { // zigbee报00代表部分，手动布防11
						holder.tgBtn1.setChecked(true);
					} else { // 显示布防状态为11
						holder.tgBtn1.setChecked(false);
					}
					break;
				case 113: // 红外报警器
					holder.tgBtn1.setVisibility(View.VISIBLE);
					holder.sbLight.setVisibility(View.GONE);
					if (mdevState.equals("00") || mdevState.equals("11")) { // zigbee报00代表部分，手动布防11
						holder.tgBtn1.setChecked(true);
					} else { // 显示布防状态为11
						holder.tgBtn1.setChecked(false);
					}
					break;
				case 115: // 燃气报警器
					holder.tgBtn1.setVisibility(View.VISIBLE);
					holder.sbLight.setVisibility(View.GONE);
					if (mdevState.equals("00") || mdevState.equals("11")) { // zigbee报00代表部分，手动布防11
						holder.tgBtn1.setChecked(true);
					} else { // 显示布防状态为11
						holder.tgBtn1.setChecked(false);
					}
					break;
				case 118: // 烟感报警器
					holder.tgBtn1.setVisibility(View.VISIBLE);
					holder.sbLight.setVisibility(View.GONE);
					if (mdevState.equals("00") || mdevState.equals("11")) { // zigbee报00代表部分，手动布防11
						holder.tgBtn1.setChecked(true);
					} else { // 显示布防状态为11
						holder.tgBtn1.setChecked(false);
					}
					break;

				case 51: // 风扇 51
					holder.tgBtn1.setVisibility(View.VISIBLE);
					holder.sbLight.setVisibility(View.GONE);
					if (strStaArr[0] == ON) {
						holder.tgBtn1.setChecked(true);
					} else {
						holder.tgBtn1.setChecked(false);
					}
					break;
				case 2:
					holder.tgBtn1.setVisibility(View.VISIBLE);
					holder.tgBtn2.setVisibility(View.VISIBLE);
					holder.sbLight.setVisibility(View.GONE);
					if (strStaArr[0] == ON) {
						holder.tgBtn1.setChecked(true);
					} else {
						holder.tgBtn1.setChecked(false);
					}
					if (strStaArr[1] == ON) {
						holder.tgBtn2.setChecked(true);
					} else {
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
					} else {
						holder.tgBtn1.setChecked(false);
					}
					if (strStaArr[1] == ON) {
						holder.tgBtn2.setChecked(true);
					} else {
						holder.tgBtn2.setChecked(false);
					}
					if (strStaArr[2] == ON) {
						holder.tgBtn3.setChecked(true);
					} else {
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
					} else {
						holder.tgBtn1.setChecked(false);
					}
					if (strStaArr[1] == ON) {
						holder.tgBtn2.setChecked(true);
					} else {
						holder.tgBtn2.setChecked(false);
					}
					if (strStaArr[2] == ON) {
						holder.tgBtn3.setChecked(true);
					} else {
						holder.tgBtn3.setChecked(false);
					}
					if (strStaArr[3] == ON) {
						holder.tgBtn4.setChecked(true);
					} else {
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

    }
		

		
	   /**
	    * 将星期编码转换为星期名称
	    * @param strWeekArr
	    * @return
	    */
	   public String getWeekName(char[] strWeekArr) {
			String weekName="";
			if(strWeekArr[0]==ON){
				weekName+="星期一  ";
			}
			if(strWeekArr[1]==ON){
				weekName+="星期二  ";
			}
			if(strWeekArr[2]==ON){
				weekName+="星期三  ";
			}
			if(strWeekArr[3]==ON){
				weekName+="星期四  ";
			}
			if(strWeekArr[4]==ON){
				weekName+="星期五  ";
			}
			if(strWeekArr[5]==ON){
				weekName+="星期六  ";
			}
			if(strWeekArr[6]==ON){
				weekName+="星期日  ";
			}
			return weekName;
		}

	@OnClick(R.id.tv_head_back)
		public void back(){
			finish();
		}


	   @Override
		protected void initViews() {
		   tvAddTimetask=(TextView) findViewById(R.id.tv_addtimetask);
		   tvAddTimetask.setVisibility(View.VISIBLE);
		   tvAddTimetask.setOnClickListener(addTimerOnClickListener);
		}

		@Override
		protected void initDatas() {
			
//		    getScheduleByDeviceNoFromServer(); //从服务器获取该设备对应的定时列表
			
		}
		
		/**
		 * 从服务器获取该设备对应的定时列表
		 * @param paramNo   1设备为deviceNo    2 情景为themeNo
		 * @param setType 
		 */
		private void getScheduleByDeviceNoFromServer(String setType, String paramNo) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("type",setType);
	  		params.addBodyParameter("paramNo",paramNo);
	  		params.addBodyParameter("phoneNum",SystemValue.phonenum);
	  		
	  		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
	  		utils.send(HttpMethod.POST, NetValue.SCHEDULE_GET_URL, params,new RequestCallBack<String>() {

	  			@Override
	  			public void onFailure(HttpException arg0, String arg1) {
	  				// TODO Auto-generated method stub
	  				ToastUtils.showToast(TimeTaskActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
	  			}

	  			@Override
	  			public void onSuccess(ResponseInfo<String> arg0) {
	  				Gson gson = new Gson();
	  				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
	  				if (message != null) {
	  					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
	  						
							String strScheduleList=(String) message.getObject();
							schedulelist=WebPacketUtil.parseScheduleListFromServer(strScheduleList);
							// 辅助线程更新页面
							Message msg = new Message();
							msg.what = 0x129;
							handler.sendMessage(msg);
							
	  					} else {
	  						ToastUtils.showToast(TimeTaskActivity.this,message.getMessageInfo(), 1000);
	  					}
	  				}
	  			}
	  		});
		}

		private OnClickListener addTimerOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				SystemValue.sAddrfreshType=2;  //非红外类设备
				
				Date date = new Date();  
				initDateTime=sdf.format(date);
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
			    TimeTaskActivity.this, initDateTime,pdevdto,0);
				dateTimePicKDialog.dateTimePicKDialog(handler);
			}
		};
		
		//此方法由对话框回调

			public  void timetaskAddScheduleList(Schedule schedule){
			schedulelist.add(schedule);
			// 辅助线程更新页面
			Message msg = new Message();
			msg.what = 0x129;
			handler.sendMessage(msg);
		}
		
		
		public  void updateScheduleList(){
			schedulelist.set(updatePosition, SystemValue.schedule);
			// 辅助线程更新页面
			Message msg = new Message();
			msg.what = 0x129;
			handler.sendMessage(msg);
		}

		
		private void scheduleManagerDialog(final int position) {
			String[] items = new String[2];
				items[0] = "定时设置";
				items[1] = "定时删除";
				
		    final Schedule schedule=schedulelist.get(position);	
				
			AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
			builder.setTitle("定时管理"); // 设置标题

			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					switch (which) {
					case 0:
						updatePosition=position;
						if(SystemValue.timerAddType.equals(SystemValue.TIMER_SCENE)){ //表明是情景
							SystemValue.timerUpdateType=SystemValue.TIMER_SCENE;
						}else if(SystemValue.timerAddType.equals(SystemValue.TIMER_DEVICE)){  //表明是设备
							SystemValue.timerUpdateType=SystemValue.TIMER_DEVICE;   
						}else if (SystemValue.timerAddType.equals(SystemValue.TIMER_MUSIC)) {	//表明是定时音乐
							SystemValue.timerUpdateType=SystemValue.TIMER_MUSIC;
						}
					
						SystemValue.schedule=schedule;
						initDateTime=schedule.getRiqi()+" "+schedule.getShij();
						DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					    TimeTaskActivity.this, initDateTime,pdevdto,0);
						dateTimePicKDialog.updateDateTimePicKDialog(handler);
						break;
					case 1:
						showDeleteDialog(position);
						break;
					default:
						break;
					}
				}
			});
			builder.create().show();
		}
		
		/** 删除对话框 **/
		protected void showDeleteDialog(final int position) {
			final Schedule schedule=schedulelist.get(position);
		
			mBackDialog = BaseDialog.getDialog(TimeTaskActivity.this, "提示",
					"确认要删除设备" + schedule.getScheduleName() + "吗？", "确认",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							deleteScheduleFormServer(schedule,position);	
							dialog.cancel();
						}
					}, "取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			mBackDialog.setButton1Background(R.drawable.btn_default_popsubmit);
			mBackDialog.show();
		}



        /**
         * 从服务器删除定时
         * @param schedule
         * @param position
         */
		protected void deleteScheduleFormServer(Schedule schedule,final int position) {
			int scheduleId=schedule.getScheduleId();
			RequestParams params = new RequestParams();
			params.addBodyParameter("scheduleId",String.valueOf(scheduleId));
			
	  		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
	  		utils.send(HttpMethod.POST, NetValue.SCHEDULE_DELETE_URL, params,new RequestCallBack<String>() {

	  			@Override
	  			public void onFailure(HttpException arg0, String arg1) {
	  				// TODO Auto-generated method stub
	  				ToastUtils.showToast(TimeTaskActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
	  			}

	  			@Override
	  			public void onSuccess(ResponseInfo<String> arg0) {
	  				Gson gson = new Gson();
	  				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
	  				if (message != null) {
	  					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
	  						
	  						// 先发送消息后删除
							schedulelist.remove(position);

							// 辅助线程更新页面
							Message msg = new Message();
							msg.what = 0x129;
							handler.sendMessage(msg);
	  						
	  					} else {
	  						ToastUtils.showToast(TimeTaskActivity.this,message.getMessageInfo(), 1000);
	  					}
	  				}
	  			}
	  		});
			
		}

		
}
