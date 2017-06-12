package com.tuwa.smarthome.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.tuwa.smarthome.InfraDateTimePickDialogUtil;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.DeviceTimerSetActivity.DevicesAllAdapter.ViewHolder;
import com.tuwa.smarthome.adapter.SpaceDeviceViewAdapter;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Music;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.Schedule;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeMusic;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.DataConvertUtil;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

interface AdpterOnItemClick2 {
	void onApterClick(int which, int postion);
}

/**
 * 按大类加载情景、开关、音乐等
 * @author WSN-520
 *
 */
public class DeviceTimerSetActivity extends BaseActivity implements
		AdpterOnItemClick2, OnCheckedChangeListener {
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
	
	private TextView tvBack, tvTitle, tvExit;
	private TextView tvRefreshThemestate;
	private RadioGroup rb_tab;
	private LayoutInflater inflater;
	private ViewGroup main;
	private List<ThemeMusic> musiclist = new ArrayList<ThemeMusic>();
	private MusicListAdapter musicListAdapter;

	private View[] spaceViews; // 动态生成的view数组
	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private GridView[] mgvDevices;
	private List<Device> gvlistAll = new ArrayList<Device>(); // 在列表中显示的list
	private List<Device> devlist = new ArrayList<Device>();
	private int viewsNum;
	private int selectIndex;

	// 滑动缓存加载Listview
	private final int LOAD_STATE_IDLE = 0; // 没有在加载，并且list中还有数据没加载
	private final int LOAD_STATE_LOADING = 1; // 正在加载
	private final int LOAD_STATE_FINISH = 2; // list中数据全部加载完毕
	private int loadState = LOAD_STATE_IDLE; // 记录加载状态
	private int LIST_COUNT; // list中的总数目
	private static int EACH_COUNT = 10; // 每页加载的数目

	private DevicesAllAdapter deviceAdpter = new DevicesAllAdapter();
	final char ON = 1 + '0'; // 字符开
	final char OFF = 0 + '0'; // 字符关
	final char WinON = 5 + '0'; // 窗帘暂停
	final char WinPK = 6 + '0'; // 窗帘暂停
	final char WinOFF = 7 + '0'; // 窗帘暂停
	public String sLightVal; // 可调关的亮度
	private InfraSetReceiver infraReceiver;  //红外设置的广播接收器
	
	private List<Theme>  themeList=new ArrayList<Theme>();
	private SceneAdapter sceneAdpter=null;
	private InfraTaskAdapter infraAdapter=null;   //紅外定时适配任务
	private GridView gvSceneSchedule;
	private List<Schedule>  schedulelist=new ArrayList<Schedule>();
	private GridView gvInfraSchedule;
	private static char[] strWeekArr = new char[7]; // 字符数组代表多路开关状态
	private String initDateTime = "2016-5-10 16:38"; // 初始化开始时间
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private String devsite="";   //红外转发器的位置
	public TextView tvInFra;
	 private BaseDialog mBackDialog;


	/* 辅线程动态刷新页面 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x121:
				sceneAdpter=new SceneAdapter();
				gvSceneSchedule.setAdapter(sceneAdpter);
				break;
			case 0x122:
				infraAdapter=new InfraTaskAdapter();
				gvInfraSchedule.setAdapter(infraAdapter);
				break;
	
			case 0x129:
				LIST_COUNT = gvlistAll.size(); // 实例化所有设备的总数

				deviceAdpter = new DevicesAllAdapter();
				deviceAdpter.onListener(DeviceTimerSetActivity.this);
				mgvDevices[selectIndex].setAdapter(deviceAdpter);
				break;
			case 0x008:
				@SuppressWarnings("unchecked")
				ArrayList<Device> result = ((ArrayList<Device>) msg.obj);

				devlist.addAll(result);
				deviceAdpter.notifyDataSetChanged();
				break;
			case 0x130:
				musicListAdapter = new MusicListAdapter(DeviceTimerSetActivity.this,
						musiclist);
				mgvDevices[selectIndex].setAdapter(musicListAdapter);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.bind(DeviceTimerSetActivity.this);// 注解工具声明
		
		// 获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getSharedPreferences("tuwa", Context.MODE_PRIVATE);
		editor = preferences.edit();

		inflater = getLayoutInflater();
		main = (ViewGroup) inflater.inflate(R.layout.activity_scene_set, null);

		initViews();
		initDatas();

		tvTitle.setText("定时设置");

		// 每个情景themeid,缓存加载一次对应的themeLink
		viewsNum = 7;
		// 初始化SceneViewPages
		initSpaceDeviceView(inflater, viewsNum);

		setContentView(main);

		//注册红外广播接收器
		infraReceiver = new InfraSetReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("send");
		filter.addAction("ACTION_SCENE_INFRA_SET");
		registerReceiver(infraReceiver, filter);
		
		showSceneFragment();  //初始化时加载第一个情景页面
		
	}



	/**************** 初始化SpaceDeviceViewPages ************************/
	private void initSpaceDeviceView(LayoutInflater inflater, int viewsnum) {
		pageViews = new ArrayList<View>();
		spaceViews = new View[viewsNum];
		mgvDevices = new GridView[5]; // switch,sock,window,weikong,yinyue

		//情景
		spaceViews[0] = inflater.inflate(R.layout.item_link_scene, null);
		gvSceneSchedule = (GridView) spaceViews[0].findViewById(R.id.gv_scene_schedule);
		//红外遥控
		spaceViews[1] = inflater.inflate(R.layout.item_link_yaokong, null);
		gvInfraSchedule=(GridView) spaceViews[1].findViewById(R.id.gv_infratask_list);
		tvInFra = (TextView) spaceViews[1].findViewById(R.id.tv_add_infratask);
		
		//照明     
		spaceViews[2] = inflater.inflate(R.layout.item_space_devices, null);
		mgvDevices[0] = (GridView) spaceViews[2].findViewById(R.id.gv_space_devices);
		//插座
		spaceViews[3] = inflater.inflate(R.layout.item_space_devices, null); 
		mgvDevices[1] = (GridView) spaceViews[3].findViewById(R.id.gv_space_devices);
		// 门窗
		spaceViews[4] = inflater.inflate(R.layout.item_space_devices, null); 
		mgvDevices[2] = (GridView) spaceViews[4].findViewById(R.id.gv_space_devices);
		 // 微控
		spaceViews[5] = inflater.inflate(R.layout.item_space_devices, null);
		mgvDevices[3] = (GridView) spaceViews[5].findViewById(R.id.gv_space_devices);
		
		// spaceViews[6]=inflater.inflate(R.layout.item_space_devices, null);
		// //安防
		// mgvDevices[4]=(GridView)spaceViews[6].findViewById(R.id.gv_space_devices);
		spaceViews[6] = inflater.inflate(R.layout.item_space_devices, null); // 音乐
		mgvDevices[4] = (GridView) spaceViews[6].findViewById(R.id.gv_space_devices);
		
		for (int i = 0; i < viewsnum; i++) {
			pageViews.add(spaceViews[i]);
		}

		viewPager.setAdapter(new SpaceDeviceViewAdapter(pageViews));
	}

	/*************** devlist数据适配器 *******************/
	public class DevicesAllAdapter extends BaseAdapter {
		private AdpterOnItemClick2 myAdpterOnclick;

		public void onListener(AdpterOnItemClick2 listener) {
			this.myAdpterOnclick = listener;
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
			ViewHolder holder;
			if (view != null) {
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(DeviceTimerSetActivity.this,
						R.layout.item_timer_dev_set, null);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}
			Device devdto = devlist.get(position);

			initDeviceNameAndSite(holder.tvDevSite,holder.tvDevName,devdto);//初始化房间名称和位置

			// 点击事件的按钮做标记
			holder.tgBtn1.setTag(position);
			holder.tgBtn2.setTag(position);
			holder.tgBtn3.setTag(position);
			holder.tgBtn4.setTag(position);
			holder.sbLight.setTag(position);
			holder.imBtnOn.setTag(position);
			holder.imBtnPause.setTag(position);
			holder.imBtnOff.setTag(position);
			holder.tvTimerSet.setTag(position);

			final int fpostion = position;

			holder.tvTimerSet.setOnClickListener(new ClickEvent() {

				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						
						Device device = devlist.get(position);
						
						Gson gson=new Gson();
						String deviceJson=gson.toJson(device);
					
						Intent intent=new Intent(DeviceTimerSetActivity.this,TimeTaskActivity.class);
						SystemValue.timerAddType=SystemValue.TIMER_DEVICE;
						SystemValue.sdevice=device;
						intent.putExtra("settype",SystemValue.TIMER_DEVICE);  // 1:表示设备   2： 表示情景
						intent.putExtra("deviceJson", deviceJson);
						startActivity(intent);	
						//设置切换动画，从右边进入，左边退出
				        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
					
					}
				}
			});

			return view;
		}

		class ViewHolder {  
			@Bind(R.id.tv_switch_devSite)
			TextView tvDevSite;
			@Bind(R.id.tv_switch_devtypeName)
			TextView tvDevName;
			@Bind(R.id.tv_timer_set)
			TextView tvTimerSet;
			@Bind(R.id.tg_btn1)
			ToggleButton tgBtn1;
			@Bind(R.id.tg_btn2)
			ToggleButton tgBtn2;
			@Bind(R.id.tg_btn3)
			ToggleButton tgBtn3;
			@Bind(R.id.tg_btn4)
			ToggleButton tgBtn4;
			@Bind(R.id.sb_switch_light)
			SeekBar sbLight; // 调光灯
			@Bind(R.id.im_btnOn)
			ImageView imBtnOn;
			@Bind(R.id.im_btnPause)
			ImageView imBtnPause;
			@Bind(R.id.im_btnOff)
			ImageView imBtnOff;

			public ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
		}
	}
	
	
	
	

	/**************** 音乐adapter ************************/

	public class MusicListAdapter extends BaseAdapter {
		private int[] colors = new int[] { 0x30BCDFE3, 0x30E1EFEF };
		private List<ThemeMusic> musicList;
		private LayoutInflater mInflater;

		public MusicListAdapter(Context context, List<ThemeMusic> vector) {
			this.musicList = vector;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return musicList.size();
		}

		@Override
		public Object getItem(int position) {
			return musicList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if (view != null) {
				holder = (ViewHolder) view.getTag();
			} else {
				view = mInflater.inflate(R.layout.include_music, parent, false);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}

			holder.tvmusicname.setText((CharSequence) musicList.get(position).getSongName());
			int colorPos = position % colors.length;
			view.setBackgroundColor(colors[colorPos]);
			return view;
		}
    
		class ViewHolder {
			@Bind(R.id.tv_music_name)
			TextView tvmusicname;

			public ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
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
	public void cmdControl(ViewHolder holder, int position, int switchid,
			char ch) {

		Device device = devlist.get(position);

		String sqlCmd = WebPacketUtil.convertCmdToSql(device, switchid, ch); // devstate转码到本地

		device.setDeviceStateCmd(sqlCmd); // 注意更改后的设备状态先加载到本地

	}


	@Override
	protected void initViews() {
		viewPager = (ViewPager) main.findViewById(R.id.vp_scene_vpager);
		tvBack = (TextView) main.findViewById(R.id.tv_head_back);
		tvTitle = (TextView) main.findViewById(R.id.tv_head_title);
		tvExit = (TextView) main.findViewById(R.id.tv_head_submit);
		tvExit.setVisibility(View.INVISIBLE);

		rb_tab = (RadioGroup) main.findViewById(R.id.rg_tab);
		RadioButton rb_scene = (RadioButton)main.findViewById(R.id.rb_scene_qingjing);
		rb_scene.setVisibility(View.VISIBLE);

		tvRefreshThemestate = (TextView) main
				.findViewById(R.id.tv_refresh_themestate);
	}

	@Override
	protected void initDatas() {
		tvBack.setOnClickListener(BackOnClickListener);
		rb_tab.setOnCheckedChangeListener(this);
	}

	private OnClickListener BackOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			unregisterReceiverSafe(infraReceiver);
			finish();
		}
	};


	// 联动情景设置
	private void showSceneFragment() {
		viewPager.setCurrentItem(0);
		
	    List<Theme> allThemeList=new ArrayList<Theme>();
	    allThemeList=new ThemeDao(DeviceTimerSetActivity.this)
                               .themeListByGatewayNo(SystemValue.gatewayid);
        themeList=WebPacketUtil.findCustomThemeFromThemesAll(allThemeList);
        
       // 异步进程更新情景界面
 		Message msg = new Message();
 		msg.what = 0x121;
 		handler.sendMessage(msg);
        
	}

	// 联动遥控设置
	private void showRemoteControlFragment() {
		viewPager.setCurrentItem(1);
		 //加载红外定时列表
		getScheduleByDeviceNoFromServer(SystemValue.TIMER_INFRA,""); 
		
		SystemValue.timerAddType=SystemValue.TIMER_INFRA;
		SystemValue.sInfraDevice=null;
		tvInFra.setText("点击此处添加红外遥控定时任务");
		tvInFra.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SystemValue.sAddrfreshType=1;
				
//				Date date = new Date();  
//				initDateTime=sdf.format(date);
//				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
//				DeviceTimerSetActivity.this, initDateTime,null,1);
//				dateTimePicKDialog.dateTimePicKDialog(handler);
//				System.out.println("弹出红外定时对话框...");
				
				
				Date date = new Date();  
				initDateTime=sdf.format(date);
				InfraDateTimePickDialogUtil dateTimePicKDialog = new InfraDateTimePickDialogUtil(
				DeviceTimerSetActivity.this, initDateTime,null,1);
				dateTimePicKDialog.dateTimePicKDialog(handler);
				System.out.println("弹出红外定时对话框...");
			}
		});
	}
	/**
	 * 添加定时任务成功后，刷新列表
	 * @param schedule
	 */
	public  void InfraAddScheduleList(Schedule schedule){
		schedulelist.add(schedule);
		// 辅助线程更新页面
		Message msg = new Message();
		msg.what = 0x122;
		handler.sendMessage(msg);
	}

	// 联动开关设置
	private void showLightFragment() {
		viewPager.setCurrentItem(2);
		selectIndex = 0;

		// =====清空当前list中缓存=====
		gvlistAll.clear();
		devlist.clear();
		loadState = LOAD_STATE_IDLE;


		gvlistAll = new DevdtoDao(DeviceTimerSetActivity.this).switchListBygwId(
				SystemValue.gatewayid, SystemValue.SWITCH);
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);

		mgvDevices[0].setOnScrollListener(new MyOnScrollListener());
	}

	// 联动插座设置
	private void showSocketFragment() {
		viewPager.setCurrentItem(3);
		selectIndex = 1;

		// =====清空当前list中缓存=====
		gvlistAll.clear();
		devlist.clear();
		loadState = LOAD_STATE_IDLE;

		// 根据网关号从数据库加载相应设备
		gvlistAll = new DevdtoDao(DeviceTimerSetActivity.this).switchListBygwId(
				SystemValue.gatewayid, SystemValue.SOCK);
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);

		// gridView滑动监听
		mgvDevices[1].setOnScrollListener(new MyOnScrollListener());
	}

	// 联动门窗设置
	private void showWindowFragment() {
		viewPager.setCurrentItem(4);
		selectIndex = 2;

		// =====清空当前list中缓存=====
		gvlistAll.clear();
		devlist.clear();
		loadState = LOAD_STATE_IDLE;

		// 根据网关号从数据库加载相应设备
		gvlistAll = new DevdtoDao(DeviceTimerSetActivity.this).switchListBygwId(
				SystemValue.gatewayid, SystemValue.WINDOW);
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);

		// gridView滑动监听
		mgvDevices[2].setOnScrollListener(new MyOnScrollListener());
	}

	// 联动微控设置
	private void showMicroControlFragment() {
		viewPager.setCurrentItem(5);
		selectIndex = 3;

		// =====清空当前list中缓存=====
		gvlistAll.clear();
		devlist.clear();
		loadState = LOAD_STATE_IDLE;

		//根据网关号从数据库加载相应设备
		List<Device> allDevList=new ArrayList<Device>();
		allDevList=new DevdtoDao(DeviceTimerSetActivity.this)
              .switchListBygwId(SystemValue.gatewayid,SystemValue.SENSOR);
		gvlistAll=WebPacketUtil.DeleteSensorFromDevicesAll(allDevList);
		
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);

		// gridView滑动监听
		mgvDevices[3].setOnScrollListener(new MyOnScrollListener());
	}

	// 联动音乐设置
	private void showMusicFragment() {
		viewPager.setCurrentItem(7);
		selectIndex = 4; // 音乐是第5个页面，view编号为4
		gvlistAll.clear();
		devlist.clear();
		musiclist.clear();
		loadState = LOAD_STATE_IDLE;
		findmusicByWgid();
		//点击跳转到定时任务  跳转到点击此处添加定时任务
		mgvDevices[4].setOnItemClickListener(ScheduleMusicListener);
		 
	}




	@Override
	public void onApterClick(int which, int postion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_scene_qingjing:
			showSceneFragment();
			break;
		case R.id.rb_scene_yaokong:
			
			showRemoteControlFragment();
			break;
		case R.id.rb_scene_zhaoming:
			showLightFragment();
			break;
		case R.id.rb_scene_menchuang:
			showWindowFragment();
			break;
		case R.id.rb_scene_chazuo:
			showSocketFragment();
			break;
		case R.id.rb_scene_weikong:
			showMicroControlFragment();
			break;
		case R.id.rb_scene_yinyue:
			showMusicFragment();
			break;
		}

	}
	
	/**
	 * 根据网关id load 音乐列表
	 */
	private void findmusicByWgid() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("gatewayNo", SystemValue.gatewayid);
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_GET_MUSIC_FROM_SERVER,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
//						showCustomToast(getResources().getString(
//								R.string.error_network));
						ToastUtils.showToast(DeviceTimerSetActivity.this,"网络连接错误！", 1000);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ResultMessage msg = JSONObject.parseObject(arg0.result,
								ResultMessage.class);
						List<Music> list = JSONArray.parseArray(
								(String) msg.getObject(), Music.class);
						musiclist = MusicUtil.ToThemeMusicList(list);
						Message msg1 = new Message();
						msg1.what = 0x130;
						handler.sendMessage(msg1);
					}
				});

	}
	
	private OnItemClickListener ScheduleMusicListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			PauseOrPlayTimeSet(arg2);
//			Intent intent=new Intent();
//			intent.setClass(DeviceTimerSetActivity.this,TimeTaskActivity.class );
//			intent.putExtra("settype",SystemValue.TIMER_MUSIC);
//			intent.putExtra("songName", musiclist.get(arg2).getSongName());
//			SystemValue.timerAddType=SystemValue.TIMER_MUSIC;
//			SystemValue.smusicName=musiclist.get(arg2).getSongName();
//			startActivity(intent);
//			//设置切换动画，从右边进入，左边退出
//	        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
//			finish();
		}
	};

	
	
	private final class MyOnScrollListener implements OnScrollListener {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			final int totalCount = firstVisibleItem + visibleItemCount; // firstVisibleItem当前页的第一项位置
																		// totalCount
																		// 当前页的最后一项位置
			if (totalCount == totalItemCount) { // 当前这一页加载完成，等待加载下一页
				if (loadState == LOAD_STATE_IDLE) {
					loadState = LOAD_STATE_LOADING;

					new Thread() {
						public void run() {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							int count = deviceAdpter.getCount();
							int dataIndex = 0; // 要加载的数据的index
							List<Device> result = new ArrayList<Device>();

							if (gvlistAll.size() > 0) {
								for (dataIndex = count; dataIndex < Math.min(
										count + EACH_COUNT, LIST_COUNT); dataIndex++) {
									Device devdto = gvlistAll.get(dataIndex);
									result.add(devdto);
								}
							}

							if (dataIndex == LIST_COUNT) {
								loadState = LOAD_STATE_FINISH;
							} else {
								loadState = LOAD_STATE_IDLE; // list未加载完，待续
							}

							if (dataIndex <= LIST_COUNT) {
								Message msg = new Message();
								msg.what = 0x008;
								msg.obj = result;
								handler.sendMessage(msg);
							}
						};
					}.start();
				}
			}
		}
	}
	
	
	   /**
     * 接收红外转发器的红外码
     * @author WSN-520
     *
     */
	private class InfraSetReceiver extends BroadcastReceiver {     //收到广播后关闭AddCameraActivity

		

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			 
			    String action=arg1.getAction();
			    if (action.equals("INFRA_DEVID")) {  //接收红外设备id
					arg1.getStringExtra("devid");
				}else if (action.equals("ACTION_SCENE_INFRA_SET")) {  //接收设置红外码
					byte[] infraredbuf=arg1.getByteArrayExtra("infrared");
					String str=DataConvertUtil.toHexUpString(infraredbuf);
					System.out.println("定时接收到的红外码===>>>"+str);
					DataConvertUtil.rprintHexString(infraredbuf);
					int deviceType=arg1.getIntExtra("INFRA_TYPE",0);  //遥控器控制的设备类型
					String deviceName=WebPacketUtil.getInfraDevtypeNameByDevtype(deviceType);
					String model=arg1.getStringExtra("INFRA_MODEL");     //用户设置的模式
					devsite=initDeviceCustomSite(NetValue.DEVID_INFRA);
					
					SystemValue.sInfraData = str;
					SystemValue.sInfraName = deviceName+model;
					
				    if (infraCallBack!=null) {  //任务名称回调
					   infraCallBack.callBack(SystemValue.sInfraName); 
		            }
					
					Device sInfraDevice = new DevdtoDao(DeviceTimerSetActivity.this)
					      .findDevByDeviceNoAndGatewayNo(NetValue.DEVID_INFRA, SystemValue.gatewayid);
					
					if(sInfraDevice!=null){
						sInfraDevice.setDeviceStateCmd(str);
						sInfraDevice.setDeviceTypeId(105);          //红外转发器
						SystemValue.sInfraDevice=sInfraDevice;
					}else{
						System.out.println("没有查找到对应的infraDevice");
					}
					SystemValue.sAddrfreshType=1;
				}
		}

	}
	
	//取消动态注册的广播
	private void unregisterReceiverSafe(BroadcastReceiver receiver) {
	    try {
	       unregisterReceiver(receiver);
	    } catch (IllegalArgumentException e) {
	        // ignore
	    }
	}
	
	
	 //情景列表适配器
	public class SceneAdapter  extends BaseAdapter {
		@Override
		public int getCount() {
			return themeList.size();
		}
		@Override
		public Object getItem(int position) {
			return themeList.get(position);
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
			      view = View.inflate(DeviceTimerSetActivity.this,R.layout.item_scene,null);
			      holder = new ViewHolder(view);
			      view.setTag(holder);
			    }
			
			Theme theme = themeList.get(position);
			int themeType=theme.getThemeType();
			String deviceNo=theme.getDeviceNo();
			if(themeType==SystemValue.SCENE_HARD){  //硬件情景显示硬件情景开关的位置
				UserSpaceDevice userSpace = new UserSpaceDevDao(DeviceTimerSetActivity.this)
				    .findDeviceSpace(SystemValue.phonenum, deviceNo);
				if (userSpace != null) {
					String spacename = WebPacketUtil.getSpaceName(userSpace
							.getSpaceNo()); // 根据phonespaceid获取spacename
					holder.tvScenename.setText(spacename+"/"+theme.getThemeName());
				} else {
					Device devdto=new DevdtoDao(DeviceTimerSetActivity.this).findDevByDeviceNoAndGatewayNo(deviceNo, SystemValue.gatewayid);
					String spacename="";
					if(devdto!=null){
					   spacename = WebPacketUtil.getSpaceName(devdto.getSpaceNo()); // 根据phonespaceid获取spacename
					}
					holder.tvScenename.setText(spacename+"/"+theme.getThemeName());
				}
			}else{
				holder.tvScenename.setText(theme.getThemeName());
			}
			
			holder.imSetting.setVisibility(View.VISIBLE);
			holder.tgSceneSwitch.setVisibility(View.INVISIBLE);
			
			sceneViewOnClick(holder,position); //列表中开关按键点击事件监听
			
			return view;
		}

		class ViewHolder {
			@Bind(R.id.tv_list_scenename)  TextView tvScenename;
			@Bind(R.id.im_setting)  ImageView imSetting;
			@Bind(R.id.tg_scene_switch)  ToggleButton tgSceneSwitch;
			
		    public ViewHolder(View view) {
		    	ButterKnife.bind(this,view);
		    }
		  }
		
		 private void sceneViewOnClick(ViewHolder holder, final int position) {
			 holder.imSetting.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Theme theme = themeList.get(position);
					
					Gson gson=new Gson();
					String themeJson=gson.toJson(theme);
				
					Intent intent=new Intent(DeviceTimerSetActivity.this,TimeTaskActivity.class);
					SystemValue.timerAddType=SystemValue.TIMER_SCENE;
					
					SystemValue.stheme=theme;
					intent.putExtra("settype",SystemValue.TIMER_SCENE);  // 1:表示设备   2： 表示情景
					intent.putExtra("themeJson", themeJson);
					startActivity(intent);	
					//设置切换动画，从右边进入，左边退出
			        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
				}
			});
		 }
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
  				ToastUtils.showToast(DeviceTimerSetActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
  				
  			}

  			@Override
  			public void onSuccess(ResponseInfo<String> arg0) {
  				Gson gson = new Gson();
  				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
  				if (message != null) {
  					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
  						
  						System.out.println("服务器返回设备定时信息"+message.getObject());
						String strScheduleList=(String) message.getObject();
						schedulelist=WebPacketUtil.parseScheduleListFromServer(strScheduleList);
						// 辅助线程更新页面
						Message msg = new Message();
						msg.what = 0x122;
						handler.sendMessage(msg);
						
  						
  					} else {
  						ToastUtils.showToast(DeviceTimerSetActivity.this,message.getMessageInfo(), 1000);
  					}
  				}
  			}
  		});
	}
	
	
	   /**
	    * 显示已经设置的定时任务
	    * @author WSN-520
	    *
	    */
	   public class InfraTaskAdapter  extends BaseAdapter {
			
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
				      view = View.inflate(DeviceTimerSetActivity.this,R.layout.item_device_schedule,null);
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
				
				 
				 holder.imScheduleManager.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						showDeleteDialog(position);
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
	   }
	   
		/** 删除对话框 **/
		protected void showDeleteDialog(final int position) {
			final Schedule schedule=schedulelist.get(position);
		
			mBackDialog = BaseDialog.getDialog(DeviceTimerSetActivity.this, "提示",
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
	  				ToastUtils.showToast(DeviceTimerSetActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
	  			}

	  			@Override
	  			public void onSuccess(ResponseInfo<String> arg0) {
	  				Gson gson = new Gson();
	  				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
	  				if (message != null) {
	  					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
	  						
	  						// 先发送消息后删除
							schedulelist.remove(position);
							
							infraAdapter.notifyDataSetChanged();

	  					} else {
	  						ToastUtils.showToast(DeviceTimerSetActivity.this,message.getMessageInfo(), 1000);
	  					}
	  				}
	  			}
	  		});
			
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
	   
	      /**
	       * 回调函数
	       * @param callBack
	       */
	        public InfraTaskCallBack infraCallBack;

	        public void callBackName( InfraTaskCallBack callBack) {  
	               this.infraCallBack=callBack;    //注意全局变量的使用    
	        } 

	        /** 
	         * socket接收数据回调接口
	         */  
	        public interface InfraTaskCallBack {  
	            public void callBack(String string);  
	        } 
	        
	      /**
	       * TODO 选择是暂停还是播放音乐对话框
	       * */
	   	 private void PauseOrPlayTimeSet(final int arg2){
	   			final String items[] = { "播放音乐", "暂停音乐" };
	   			AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
	   			builder.setTitle("音乐定时设置"); // 设置标题
	   			builder.setItems(items, new DialogInterface.OnClickListener() {
	   				@Override
	   				public void onClick(DialogInterface dialog, int which) {
	   					dialog.dismiss();
	   					switch (which) {
	   					case 0:
	   						Intent intent=new Intent();
	   						intent.setClass(DeviceTimerSetActivity.this,TimeTaskActivity.class );
	   						intent.putExtra("settype",SystemValue.TIMER_MUSIC);
	   						intent.putExtra("songName", musiclist.get(arg2).getSongName());
	   						SystemValue.timerAddType=SystemValue.TIMER_MUSIC;
	   						SystemValue.smusicName=musiclist.get(arg2).getSongName();
	   						startActivity(intent);
	   						//设置切换动画，从右边进入，左边退出
	   				        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
	   						finish();
	   						break;
	   					case 1:
	   						Intent intent1=new Intent();
	   						intent1.setClass(DeviceTimerSetActivity.this,TimeTaskActivity.class );
	   						intent1.putExtra("settype",SystemValue.TIMER_MUSIC);
//	   						intent.putExtra("songName", musiclist.get(arg2).getSongName());
	   						intent1.putExtra("songName", "暂停音乐");
	   						SystemValue.timerAddType=SystemValue.TIMER_MUSIC;
//	   						SystemValue.smusicName=musiclist.get(arg2).getSongName();
	   						SystemValue.smusicName="暂停音乐";
	   						startActivity(intent1);
	   						//设置切换动画，从右边进入，左边退出
	   				        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
	   						finish();
	   						break;
	   					default:
	   						break;
	   					}

	   				}
	   			});
	   			builder.create().show();
	   		}

	   
}
