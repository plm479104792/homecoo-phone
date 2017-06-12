package com.tuwa.smarthome.activity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;

import com.homecoolink.global.MyApp;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.adapter.DevWidetypeAdapter;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.DevWidetype;
import com.tuwa.smarthome.entity.Result;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.Weather;
import com.tuwa.smarthome.entity.Weather_data;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.DatagramSocketPhoneServer;
import com.tuwa.smarthome.network.HttpUtils;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.CrashHandler;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.PreferencesUtils;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.VerifyUtils;
import et.song.remotestar.ActivityMain;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends BaseActivity {
	
	private SocketService mService;
	private Handler timerhandler;  
	// 天气模块的控件
	private TextView tvCity; // 城市
	private TextView tvPM25; // pM值
	private TextView tvDate; // 日期
	private ImageView ivpic11; // 白天的天气
	private ImageView ivpic12; // 晚上的天气
	private TextView tvweek1; // 礼拜几
	private TextView tvwea1; // 中文天气
	private TextView tvwind1; // 有风无风还是微风
	private TextView tvtemper1; // 温度
   
	private List<DevWidetype> devwidetypeList;
	@Bind(R.id.tv_head_back)
	TextView tvBack;
	@Bind(R.id.iv_logo)
	ImageView ivLogo;
	@Bind(R.id.tv_head_title)
	TextView tvheadTitle;
	@Bind(R.id.tv_head_submit)
	TextView tvheadExit;
	@Bind(R.id.tv_bottom_network)
	TextView tvbttomNetwork;
	@Bind(R.id.tv_temp)
	TextView tvtemprature; // 温度
	@Bind(R.id.tv_humidity)
	TextView tvhumidity; // 湿度

	@Bind(R.id.gv_dev_widetype)
	GridView gvDevWideType;

	private String dataTemp = "2600";
	private String dataHumi = "45";
	private String dataPM25 = "58";

	/* 辅线程动态刷新页面 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x129:
				 initTemperatureAndHumidity();
				 initPM25();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("HomeActivity重启！！！");
		
		setContentView(R.layout.activity_home);

		ButterKnife.bind(HomeActivity.this);
		
		SystemValue.loginFlag=true;  //标识登录成功
		
		// Activity和service绑定
		Intent service = new Intent(HomeActivity.this, SocketService.class);
		bindService(service, conn, Context.BIND_AUTO_CREATE);
		
	    initViews();
	    initDatas();  //初始化数据

		new WeatherAsyncTask().execute(SystemValue.city); //根据城市获取天气

		// 访问刷新温湿度线程 //【定时线程步骤2】
        HandlerThread tempHumiThread = new HandlerThread("SentHandlerThread");
        tempHumiThread.start();//创建一个HandlerThread并启动它
        timerhandler=new Handler(tempHumiThread.getLooper());  //发送数据线程
		timerhandler.post(timerrunnable);
		
		//设置别名，JPush根据别名广播给别名用户
		setAliasAndTags();
	
		Intent startIntent = new Intent(this, SocketService.class);
		startService(startIntent);
		
		if(VerifyUtils.isEmpty(SystemValue.gatewayid)){
			ToastUtils.showToast(HomeActivity.this, "请先绑定网关！", 2000);
		}
		
		/**
		 * 启动APP获取七寸屏IP service(Datagramsocket) 
		 * */
		if (NetValue.netFlag==NetValue.INTRANET) {
			MusicUtil.StartDatagramSocketServerForIp(this);
		}
		
		Intent startService=new Intent(HomeActivity.this,DatagramSocketPhoneServer.class);
		startService(startService);
		
		CrashHandler crash=new CrashHandler();
		crash.init(HomeActivity.this,MyApp.app);
		
	}

	// 从数据库读取最新的温湿度
	private void initTemperatureAndHumidity() {
		int devtype=SystemValue.DEV_TEMP_HUMI;
		Device devTempHumi = new DevdtoDao(HomeActivity.this)
				.findDevTempHumiByGwId(SystemValue.gatewayid,devtype);
		if (devTempHumi != null) {
			String strdata = devTempHumi.getDeviceStateCmd();
			if (strdata.indexOf("p") != -1) {
				String[] strTempHumi = strdata.split("p");
				double dTemp = Double.valueOf(strTempHumi[0]);
				tvtemprature.setText("室内温度：" + dTemp + "℃");
				tvhumidity.setText("湿度：" + strTempHumi[1] + "%");
			}
		}else{
			System.out.println("=====无温湿度设备======" );
		}
		
	}
	
	// 从数据库读取最新的温湿度
	private void initPM25() {
		int devtype=SystemValue.DEV_PM25;
		Device devPM25 = new DevdtoDao(HomeActivity.this)
				.findDevTempHumiByGwId(SystemValue.gatewayid,devtype);
		if (devPM25 != null) {
			String strdata = devPM25.getDeviceStateCmd();
			tvPM25.setText("PM2.5：" + strdata);
		}else{
			System.out.println("=====无PM25设备======" );
		}
		
	}

	/* 定时访问服务器动态同步数据 */// 【定时线程步骤1】
	Runnable timerrunnable = new Runnable() {
		@Override
		public void run() {
			// 异步进程更新界面
			Message msg = new Message();
			msg.what = 0x129;
			handler.sendMessage(msg);
			timerhandler.postDelayed(this, 60000);
		}
	};
	
	
	// 获取SocketService实例对象
	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 返回一个SocketService对象
			mService = ((SocketService.SocketBinder) service).getService();

			mService.callSocket(new SocketCallBack() {
				@Override
				public void callBack(TranObject tranObject) {

					switch (tranObject.getTranType()) {
					case NETMSG:
						int netstatue = (Integer) tranObject.getObject();
						if (NetValue.NONET == netstatue) { // 本地连接失败
							if(!NetValue.autoFlag){
								ToastUtils.showToast(HomeActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
							}
							
							NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
							tvbttomNetwork.setText("远程");
						}else if(NetValue.INTRANET == netstatue){
							tvbttomNetwork.setText("本地");
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

	@OnItemClick(R.id.gv_dev_widetype)
	public void devwideClick(int position) {
		DevWidetype devWidetype = devwidetypeList.get(position);
		int devwideId = devWidetype.getWidetypeId();
		switch (devwideId) {
		case SystemValue.anfang:
			 Intent cameraIntent=new
			 Intent(HomeActivity.this,CameraKindsActivity.class);
			 startActivity(cameraIntent);
			 finish();
			break;
		case SystemValue.yaokong:
			 //直接发送红外码
			 PreferencesUtils.putString(HomeActivity.this,"OPERATION_TYPE","SCENE_INFRA_SEND");
			 Intent yaokongIntent=new Intent(HomeActivity.this,ActivityMain.class);
			 startActivity(yaokongIntent);
			break;
		case SystemValue.SWITCH:
			Intent switchIntent = new Intent(HomeActivity.this,DeviceSwitchActivity.class);
			startActivity(switchIntent);
			finish();
			break;
		case SystemValue.xiaoxi:
			Intent messageIntent = new Intent(HomeActivity.this,MessageActivity.class);
			startActivity(messageIntent);
			finish();
			break;
		case SystemValue.WINDOW:
			Intent windowIntent = new Intent(HomeActivity.this,DeviceWindowActivity.class);
			startActivity(windowIntent);
			finish();
			break;
		case SystemValue.SOCK:
			Intent sockIntent = new Intent(HomeActivity.this,DeviceSockActivity.class);
			startActivity(sockIntent);
			finish();
			break;
		case SystemValue.SENSOR:
			 Intent weikongIntent=new  Intent(HomeActivity.this,DeviceWeiKongActivity.class);
			 weikongIntent.putExtra("operator_type", "operator");
			 startActivity(weikongIntent);
			 finish();
			break;
		case SystemValue.yingyue:
			 Intent intent=new Intent();
			 intent.setClass(HomeActivity.this, MusicMainActivity.class);
			 startActivity(intent);
			 overridePendingTransition(R.anim.out_to_left,R.anim.in_from_right);
			 finish();
			break;
		default:
			break;
		}
	}

	protected void netWorkSwitch(TextView textview) {
		if (NetValue.netFlag == NetValue.INTRANET) { // 切换为远程模式
			NetValue.netFlag = NetValue.OUTERNET;
			textview.setText("远程");
			ToastUtils.showToast(HomeActivity.this,"系统已切换网络为远程模式！", 1000);
		} else { // 切换为本地模式
			/********** 启动socketservice在后台运行 ******************/
			Intent startIntent = new Intent(this, SocketService.class);
			startService(startIntent);

			if (!NetValue.socketauthen) {
				mService.socketConnect(NetValue.LOCAL_IP); // 建立连接并认证
			}
			textview.setText("本地");
			ToastUtils.showToast(HomeActivity.this,"系统已切换网络为本地模式！", 1000);
			NetValue.netFlag = NetValue.INTRANET;
		}
	}

	/*** 退出系统 ***/
	@OnClick(R.id.tv_head_submit)
	public void systemExit() {
		initExitDialog();
	}

	/*** 空间 ***/
	@OnCheckedChanged(R.id.rb_navi_space)
	public void spaceDeviceShow() {
		Intent intent = new Intent(HomeActivity.this,SpaceDevicesActivity.class);
		startActivity(intent);
		finish();
	}

	/*** 情景模式 ***/
	@OnCheckedChanged(R.id.rb_navi_scene)
	public void sceneMode() {
		 Intent sceneIntent=new
		 Intent(HomeActivity.this,SceneModelActivity.class);
		 startActivity(sceneIntent);
		 finish();
	}

	/*** 网络切换 ***/
	@OnClick(R.id.tv_bottom_network)
	public void networkSwitchClick() {
		netWorkSwitch(mService, tvbttomNetwork);
	}

	/*** 防区管理 ***/
	@OnCheckedChanged(R.id.rb_navi_alert)
	public void defenceArea() {
		Intent intent = new Intent(HomeActivity.this, DefenceAreaActivity.class);
		startActivity(intent);
		finish();
	}

	/*** 系统设置 ***/
	@OnCheckedChanged(R.id.rb_navi_set)
	public void systemSet() {
//		if(!VerifyUtils.isEmpty(SystemValue.gatewayid)){  //只有在网关已经绑定的情况下才同步情景
//			getSceneToLocal(mService);
//		}
		Intent intent = new Intent(HomeActivity.this, SetActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void initViews() {
		tvBack.setVisibility(View.GONE);
		ivLogo.setVisibility(View.VISIBLE);
		ivLogo.setImageResource(R.drawable.ic_homecoo_main);
		
		tvheadTitle.setVisibility(View.INVISIBLE);
		tvheadExit.setText(R.string.header_exit);
		
		// tvCity = (TextView) findViewById(R.id.tvCity);
		tvPM25 = (TextView) findViewById(R.id.tvPM25);
		tvDate = (TextView) findViewById(R.id.tvDate);

		ivpic11 = (ImageView) findViewById(R.id.ivpic11);
		ivpic12 = (ImageView) findViewById(R.id.ivpic12);

		tvweek1 = (TextView) findViewById(R.id.tvweek1);
		tvwea1 = (TextView) findViewById(R.id.tvwea13);
		tvwind1 = (TextView) findViewById(R.id.tvwind1);
		tvtemper1 = (TextView) findViewById(R.id.tvtemper1);
		
		// 初始化显示网络状态
		if (NetValue.netFlag == NetValue.INTRANET) {
			tvbttomNetwork.setText("本地"); // 任务栏显示网络状态
		} else {
			tvbttomNetwork.setText("远程"); // 任务栏显示网络状态
		}

	}

	@Override
	protected void initDatas() {
		devwidetypeList = SystemValue.getDevWideList();
		gvDevWideType.setAdapter(new DevWidetypeAdapter(HomeActivity.this,devwidetypeList));
	}

	@Override
	protected void onDestroy() {
		timerhandler.removeCallbacks(timerrunnable); // 停止定时器线程
		unbindService(conn);
		super.onDestroy(); // 注意先后
	}

	// 异步获取天气
	class WeatherAsyncTask extends AsyncTask<String, Void, Weather> {

		protected Weather doInBackground(String... params) {
			String url = HttpUtils.getURl(params[0]);
			String jsonStr = HttpUtils.getJsonStr(url);
			if (jsonStr != null) {
				Weather weather = HttpUtils.fromJson(jsonStr);
				if(weather!=null){
					Result r = weather.getResults().get(0);
					for (int i = 0; i < 3; i++) {
						Weather_data w = r.getWeather_data().get(i);
						w.setDayPicture(HttpUtils.getImage(w.getDayPictureUrl()));
						w.setNightPicture(HttpUtils.getImage(w.getNightPictureUrl()));
					}
				}
			
				return weather;
			} else {
				return null;
			}
		}

		protected void onPostExecute(Weather result) {
			if (result != null) {
				Result res = result.getResults().get(0);
				Weather_data weather_data = res.getWeather_data().get(0);
//				System.out.println("获取的天气信息： " + weather_data.toString());
				// tvCity.setText("城市:"+res.getCurrentCity());
				// String pm2_5 = "".equals(res.getPm25()) ? "75" :
				// res.getPm25();
				// tvPM25.setText("PM2.5浓度:"+pm2_5);
//				tvPM25.setText("PM2.5: " + res.getPm25());
				tvDate.setText(result.getDate()); // 应该直接从网络上获取
				ivpic11.setImageBitmap(weather_data.getDayPicture());
				ivpic12.setImageBitmap(weather_data.getNightPicture());
				String str = weather_data.getDate();
				tvweek1.setText(str.substring(0, 2));
				tvwea1.setText(weather_data.getWeather());
				tvwind1.setText(weather_data.getWind());
				tvtemper1.setText(weather_data.getTemperature());
				// tvtemper1.setText(str.substring(14, str.length()-1)); //实时温度

			}
		}
	}
	
}
