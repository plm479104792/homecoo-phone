package com.tuwa.smarthome.global;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.tuwa.smarthome.R;
import com.tuwa.smarthome.entity.DevWidetype;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.MenuSet;
import com.tuwa.smarthome.entity.Schedule;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeInfra;
import com.tuwa.smarthome.entity.User;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.network.DatagramSocketPhoneServer;

public class SystemValue {
	public static Activity activity;
	
	
	/***************** 系统每次启动的初始化变量 *********************/
	public static int userid = 1; // 用户id
	public static User user;
	public static String gatewayid = ""; // 网关id

	public static String phonenum = ""; // 注册验证的手机号
	
	public static String userName,passWord;
	
	public static Theme themeSet = null; //情景设置全局变量
	
	// ============WIFI设置================
	public static final int  TIMEOUT=3000;  //访问服务器超时时间
	public static String SSID_NAME="";
	public static String SSID_PWD="";
	public static boolean WIFI_SET_FLAG=false;   //网关wifi设置标识
	
	public static final int  MSG_TIME=1000;  //Toast消息显示
	
	// ============Login=================	
	public static boolean loginFlag=false;  //登录标志位
	public static String cameraPhone="";    //传递给摄像机的手机账号
	public static String cameraPwd="";      //传递给摄像机的密码
	
	public static String city="上海";      //定位的城市
	
	// ============标志位=================	
	public static boolean deviceSysnFlag=false;  //设备同步标志位
	
	// ============广播中Action=================	
	public static String ACTION_FINISH_ACTIVITY="ACTIVITY_FINISH";  //销毁activity
	
	// ============网关结果显示=================	
	public static boolean NETRESULT_SHOW_FLAG=false;  //网络结果显示标志位
	
	// ============Version版本类型=================
	public static final int VERSION_APP = 1; // app版本
	public static final int VERSION_DEVICE = 2; // 设备版本
	public static final int VERSION_SPACE = 3; // 空间版本
	public static final int VERSION_SCENE = 4; // 情景版本
	public static final int VERSION_MUSIC = 5; // 情景音乐版本
	public static final int VERSION_GATEWAY = 6; // 网关版本

	// ============功能对话框操作类型=================
	public static final int add = 2; // 增加
	public static final int update = 0; // 修改
	public static final int delete = 1; // 删除

	// ============HomeActivity产品类别图标=================
	public static final int SWITCH = 1; // 照明
	public static final int SENSOR = 2; // 传感类
	public static final int WINDOW = 3; // 门窗
	public static final int SOCK = 5; // 插座
	public static final int weikong2 = 7; // 微控
	public static final int anfang = 11; // 安防模块
	public static final int yaokong = 12; // 遥控
	public static final int xiaoxi = 13; // 消息
	public static final int yingyue = 15; // 音乐

	// ============设备类型定义=================
	public static final int DEV_SWITCH_ONE = 1; // 一路开关
	public static final int DEV_SWITCH_two = 2; // 二路开关
	public static final int DEV_SWITCH_three = 3; // 三路开关
	public static final int DEV_SWITCH_four = 4; // 四路开关
	public static final int DEV_DIM_LIGHT = 5; // 调光开关
	public static final int DEV_CURTAIN_ONE = 6; // 窗帘
	public static final int DEV_SOCK_ONE = 8; // 插座
	public static final int DEV_WINDOW_ONE = 11; // 窗户
	public static final int DEV_FANS = 51; // 风扇

	public static final int DEV_TEMP_HUMI = 104; // 温湿度
	public static final int DEV_INFRA_CONTROL = 105; // 红外转发控制

	public static final int DEV_PM25 = 109; // PM25
	public static final int DEV_DOOR_LOCK = 110; // 门磁
	public static final int DEV_INFRA_DETECT = 113; // 红外入侵
	public static final int DEV_SENSOR_GAS = 115; // 燃气
	public static final int DEV_SENSOR_SMOKE = 118; // 烟感

	public static final int DEV_SCENE = 202; // 情景开关
	public static final int DEV_DOUBLE_CONTRL = 204; // 双控开关

	// ============情景类型宏定义=================
	public static final int SCENE_HARD = 1; // 硬件情景
	public static final int SCENE_DOUBLE = 2; // 双控情景
	public static final int SCENE_TRIGGER = 3; // 传感器情景
	public static final int SCENE_SOFT = 4; // 自定义情景

	// ============CameraKindActivity摄像机=================
	public static final int yuntai = 100; // 云台
	public static final int qiangji = 101; // 枪机
	public static final int duijiang = 102; // 对讲
	
	
	// ============情景联动设置中遥控的操作类型操作类型=================
	public static  int InfraSetType = 0; //遥控联动操作类型
	public static final int InfraAdd = 1; // 添加遥控
	public static final int InfraUpdate = 2; // 修改遥控
	public static  ThemeInfra formerInfra = null; // 修改前遥控对象
	public static  ThemeInfra formerAddInfra = null; // 添加遥控对象
	public static int infraDevType=0;   //遥控对象的设备类型
	public static String sceneSetThemeNo; // 全局情景设置情景号
	
	public static boolean themeClean=false;   //情景清空全局标志位

	// ============定时设置系统变量=================
	public static String timerAddType = "0"; // 添加定时标志位              1:设备 2:情景
	public static String timerUpdateType = "0"; //更新定时标志位        1:设备 2:情景
	public static boolean timerSetFlag = false; // 1:设备 2:情景
	public static String TIMER_DEVICE ="1"; // 1:设备
	public static String TIMER_SCENE = "2"; // 2:情景
	public static String TIMER_MUSIC = "3"; // 3:音乐
	public static String TIMER_INFRA = "4"; // 4:红外
	
	public static Device sdevice;
	public static Theme stheme;
	public static String smusicName; // 定时音乐名称
	public static String sInfraName; // 定时红外名称
	public static String sInfraData; // 定时红外码
	public static Device sInfraDevice=null;  //硬件红外转发器
	public static Schedule schedule;
	public static int sAddrfreshType=2;   //区分红外大类标志位   1:红外   2:其它

	// ============HomeActivity产品类别列表初始化=================
	public static List<DevWidetype> getDevWideList() {
		List<DevWidetype> list = new ArrayList<DevWidetype>();
		DevWidetype devwide1 = new DevWidetype(R.drawable.ha_anfang, "监控", 11);
		DevWidetype devwide2 = new DevWidetype(R.drawable.ha_yaokong, "遥控", 12);
		DevWidetype devwide3 = new DevWidetype(R.drawable.ha_zhaoming, "照明", 1);
		DevWidetype devwide4 = new DevWidetype(R.drawable.ha_xiaoxi, "消息", 13);
		DevWidetype devwide5 = new DevWidetype(R.drawable.ha_menchuan, "门窗", 3);
		DevWidetype devwide6 = new DevWidetype(R.drawable.ha_chazuo, "插座", 5);
		DevWidetype devwide7 = new DevWidetype(R.drawable.ha_weikong, "微控", 2);
		DevWidetype devwide8 = new DevWidetype(R.drawable.ha_yingyue, "音乐", 15);

		list.add(devwide1);
		list.add(devwide2);
		list.add(devwide3);
		list.add(devwide4);
		list.add(devwide5);
		list.add(devwide6);
		list.add(devwide7);
		list.add(devwide8);
		return list;
	}

	// ============填充系统设置的菜单栏=================
	public static List<MenuSet> getMenuSetList() {
		List<MenuSet> list = new ArrayList<MenuSet>();
		MenuSet mUpdateSystem = new MenuSet(R.drawable.update, "版本信息");
		MenuSet mLogoffUser = new MenuSet(R.drawable.deleteuser, "注销账号");
		list.add(mUpdateSystem);
		list.add(mLogoffUser);
		return list;
	}
	
	// ============填充Wifi设置的菜单栏=================
	public static List<MenuSet> getWifiSetList() {
		List<MenuSet> list = new ArrayList<MenuSet>();
		MenuSet mUpdateSystem = new MenuSet(R.drawable.wifi_router, "网关路由器无线连接");
//		MenuSet mLogoffUser = new MenuSet(R.drawable.wifi_ap, "网关无线网设置");
		list.add(mUpdateSystem);
//		list.add(mLogoffUser);
		return list;
	}
	
	
	/**
	 * 封装版本时间戳
	 * 
	 * @param versionType
	 * @return
	 */
	public static Version getVersion(int versionType) {
		Version version = new Version();
		version.setPhonenum(SystemValue.phonenum);
		version.setGatewayNo(SystemValue.gatewayid);
		version.setVersionType(versionType);
		long date = System.currentTimeMillis();// 获取当前时间
		version.setUpdateTime(date);
		return version;
	}

	/**
	 * 封装初始化版本
	 * 
	 * @param versionType
	 * @return
	 */
	public static Version getinitVersion(int versionType) {
		Version version = new Version();
		version.setPhonenum(SystemValue.phonenum);
		version.setGatewayNo(SystemValue.gatewayid);
		version.setVersionType(versionType);
		version.setUpdateTime(1);  //无版本信息，初始化为最小的时间
		return version;
	}

	public static List<Schedule> getTimetaskList() {
		List<Schedule> list = new ArrayList<Schedule>();
		// Schedule task1 = new Schedule(1,"1010000","", "09:18", "0",
		// "","18679451786","bcbc3706004b1200","","3030414130304444","100","客厅三路灯","","","");
		// Schedule task2 = new Schedule(2,"","2016年5月10日", "16:28", "1",
		// "","18679451786","bcbc3706004b1200","","3030414130304444","101","客厅三路灯","","","");
		// list.add(task1);
		// list.add(task2);
		return list;
	}

	// ============CameraKindActivity摄像机大类=================
	public static List<DevWidetype> getCameraKindList() {
		List<DevWidetype> list = new ArrayList<DevWidetype>();
		DevWidetype camerakind1 = new DevWidetype(R.drawable.ha_anfang, "摄像机",
				100);
		// DevWidetype camerakind2=new DevWidetype(R.drawable.ha_yaokong,
		// "室外枪机",101);
		DevWidetype camerakind3 = new DevWidetype(R.drawable.ha_zhaoming,
				"可视对讲", 102);

		list.add(camerakind1);
		// list.add(camerakind2);
		list.add(camerakind3);
		return list;
	}

	public static List<String> strips = new ArrayList<String>();

	/**
	 * 将所有可以ping通的ip地址添加到列表中
	 * 
	 * @param ip
	 * @return
	 */
	public static List<String> setIps(String ip) {
		boolean isflag = false;
		if (!ip.equals("")) {
			for (int i = 0; i < strips.size(); i++) {
				String IP = strips.get(i);
				if (ip.equals(IP)) {
					isflag = true;
					break;
				}
			}
			if (!isflag) {
				strips.add(ip);
			}
		}
		return strips;
	}

	public static List<String> getIps() {
		return strips;
	}

	// ==========JPush实体类 messageType 定义==========
	public static final int DEVICE_STATE_UPDATE_JPUSH = 1; // 设备状态更新
	public static final int SECURITY_ALERT_JPUSH = 2; // 安防报警
	public static final int MUSIC_JPUSH = 3; // 音乐
	public static final int THEME_JPUSH = 4; // 情景
	public static final int THEME_MUSIC_JPUSH=5; //情景音乐
	public static final int VOLUME_MUSIC_JOUSH=6; //音量      好像没有用上？？

	/**
	 * 音乐控制模式
	 * */
	public static final String MUSIC_LIST_GET="0";	//内网获取音量列表
	public static final String MUSIC_CTRL_PAUSE = "1"; // 暂停
	public static final String MUSIC_CTRL_PLAY = "2"; // 播放
	public static final String MUSIC_CTRL_LAST_SONG = "3"; // 上一首歌曲
	public static final String MUSIC_CTRL_NEXT_SONG = "4"; // 下一首歌曲
	public static final String MUSIC_STYLE_SINGER = "5"; // 单曲
	public static final String MUSIC_STYLE_LIST = "6"; // 列表
	public static final String MUSIC_STYLE_RANDOM = "7"; // 随机
	public static final String MUSIC_THEME_MUSIC_DELETE = "9"; // 内网删除情景音乐
	public static final String MUSIC_THEME_MUSIC_SET = "10"; // 情景设置音乐
	public static final String MUSIC_STYLE_AA = "11"; // 内网下播放情景音乐
	public static final String MUSIC_THEME_MUSIC_PAUSE = "12"; // 内网设置暂停音乐
	
	public static final String MUSIC_VOLUME="15";	//手机端获取音量控制
	public static final String MUSIC_VOLUME_CTRL="16"; //内网音量控制
	public static final String MUSIC_THEME_GET="17"; //内网APP获取情景音乐 联动音乐设置
	public static final String MUSIC_THEME_SEND="18"; //内网七寸屏发送情景音乐  联动音乐
	public static final String UDP_CLIENT_IP_TRUE="23";  //手机端发送socket请求该IP是不是七寸屏的IP 
	public static final String UDP_SCREEN_IP_TRUE="24";  //手机端收到七寸屏回复，该IP是七寸屏的IP

	
	
	

	/**
	 * @Description:根据多点广播  获取七寸屏上的局域网IP，赋予全局变量，用于内网music的socket连接
	 * 不直接用多点广播连接的原因是    多点广播不够稳定
	 * */
	public static String MUSIC_SCREEN_IP="";
	/**
	 * 2016-09-29 20:06:12
	 * */
	public static DatagramSocketPhoneServer datagramsocketServer;
}
