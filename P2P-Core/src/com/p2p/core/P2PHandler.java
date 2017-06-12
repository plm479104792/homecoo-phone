package com.p2p.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.p2p.core.P2PInterface.IP2P;
import com.p2p.core.P2PInterface.ISetting;
import com.p2p.core.global.Constants;
import com.p2p.core.utils.MyUtils;

public class P2PHandler {
	String TAG = "SDK";

	private static int MSG_ID_SETTING_DEVICE_TIME = Constants.MsgSection.MSG_ID_SETTING_DEVICE_TIME;
	private static int MSG_ID_GETTING_DEVICE_TIME = Constants.MsgSection.MSG_ID_GETTING_DEVICE_TIME;

	private static int MSG_ID_GETTING_NPC_SETTINGS = Constants.MsgSection.MSG_ID_GETTING_NPC_SETTINGS;
	private static int MSG_ID_SET_REMOTE_DEFENCE = Constants.MsgSection.MSG_ID_SET_REMOTE_DEFENCE;
	private static int MSG_ID_SET_REMOTE_RECORD = Constants.MsgSection.MSG_ID_SET_REMOTE_RECORD;
	private static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT;
	private static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME;
	private static int MSG_ID_SETTING_NPC_SETTINGS_BUZZER = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER;
	private static int MSG_ID_SETTING_NPC_SETTINGS_MOTION = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION;
	private static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE;
	private static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME;
	private static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME;
	private static int MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE;

	private static int MSG_ID_SETTING_ALARM_EMAIL = Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL;
	private static int MSG_ID_GETTING_ALARM_EMAIL = Constants.MsgSection.MSG_ID_GETTING_ALARM_EMAIL;

	private static int MSG_ID_SETTING_ALARM_BIND_ID = Constants.MsgSection.MSG_ID_SETTING_ALARM_BIND_ID;
	private static int MSG_ID_GETTING_ALARM_BIND_ID = Constants.MsgSection.MSG_ID_GETTING_ALARM_BIND_ID;

	private static int MSG_ID_SETTING_INIT_PASSWORD = Constants.MsgSection.MSG_ID_SETTING_INIT_PASSWORD;
	private static int MSG_ID_SETTING_DEVICE_PASSWORD = Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD;
	private static int MSG_ID_CHECK_DEVICE_PASSWORD = Constants.MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD;

	private static int MSG_ID_SETTING_DEFENCEAREA = Constants.MsgSection.MSG_ID_SETTING_DEFENCEAREA;
	private static int MSG_ID_GETTING_DEFENCEAREA = Constants.MsgSection.MSG_ID_GETTING_DEFENCEAREA;

	private static int MSG_ID_SETTING_WIFI = Constants.MsgSection.MSG_ID_SETTING_WIFI;
	private static int MSG_ID_GETTING_WIFI_LIST = Constants.MsgSection.MSG_ID_GETTING_WIFI_LIST;

	private static int MSG_ID_GETTING_RECORD_FILE_LIST = Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST;
	private static int MSG_ID_SEND_MESSAGE = Constants.MsgSection.MSG_ID_SEND_MESSAGE;
	private static int MSG_ID_SEND_CUSTOM_CMD = Constants.MsgSection.MSG_ID_SEND_CUSTOM_CMD;
	private static int MSG_ID_CHECK_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_CHECK_DEVICE_UPDATE;
	private static int MSG_ID_CANCEL_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE;
	private static int MSG_ID_DO_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_DO_DEVICE_UPDATE;
	private static int MSG_ID_GET_DEFENCE_STATE = Constants.MsgSection.MSG_ID_GET_DEFENCE_STATE;
	private static int MSG_ID_GET_DEVICE_VERSION = Constants.MsgSection.MSG_ID_GET_DEVICE_VERSION;
	private static int MSG_ID_CLEAR_DEFENCE_GROUP = Constants.MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP;
	private static int MESG_ID_STTING_PIC_REVERSE=Constants.MsgSection.MESG_ID_STTING_PIC_REVERSE;
	private static int MESG_ID_STTING_IR_ALARM_EN=Constants.MsgSection.MESG_ID_STTING_IR_ALARM_EN;
	private static int MESG_STTING_ID_EXTLINE_ALARM_IN_EN=Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN;
	private static int MESG_STTING_ID_EXTLINE_ALARM_OUT_EN=Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN;
	private static int MESG_STTING_ID_SECUPGDEV=Constants.MsgSection.MESG_STTING_ID_SECUPGDEV;
    private static int MESG_STTING_ID_GUEST_PASSWD=Constants.MsgSection.MESG_STTING_ID_GUEST_PASSWD;
    private static int MESG_STTING_ID_TIMEZONE=Constants.MsgSection.MESG_STTING_ID_TIMEZONE;
    private static int MESG_GET_SD_CARD_CAPACITY=Constants.MsgSection.MESG_GET_SD_CARD_CAPACITY;
    private static int MESG_SD_CARD_FORMAT=Constants.MsgSection.MESG_SD_CARD_FORMAT;
    private static int MESG_SET_GPIO=Constants.MsgSection.MESG_SET_GPIO;
    private static int MESG_SET_GPI1_0=Constants.MsgSection.MESG_SET_GPI1_0;
    private static int MESG_SET_PRE_RECORD=Constants.MsgSection.MESG_SET_PRE_RECORD;
    private static int MESG_GET_DEFENCE_AREA_SWITCH=Constants.MsgSection.MESG_GET_DEFENCE_AREA_SWITCH;
    private static int MESG_SET_DEFENCE_AREA_SWITCH=Constants.MsgSection.MESG_SET_DEFENCE_AREA_SWITCH;
	private static int MESG_GET_ALARM_RECORD=Constants.MsgSection.MESG_GET_ALARM_RECORD;
    private static P2PHandler manager = null;

	private P2PHandler() {
	};

	public synchronized static P2PHandler getInstance() {
		if (null == manager) {
			synchronized (P2PHandler.class) {
				manager = new P2PHandler();
			}
		}
		return manager;
	}

	/*
	 * 初始化
	 */
	public void p2pInit(Context context, IP2P p2pListener,
			ISetting settingListener) {
		new MediaPlayer(context);
		MediaPlayer.getInstance().setP2PInterface(p2pListener);
		MediaPlayer.getInstance().setSettingInterface(settingListener);
	}

	/*
	 * p2p连接
	 */
	public boolean p2pConnect(String activeUser, int codeStr1, int codeStr2) {
//		final String cHostName="|6sci.com|6sci.com.cn";
		final String cHostName="|cloudlinks.cn|2cu.co|gwelltimes.com|cloud-links.net";
//		if (MediaPlayer.getInstance().native_p2p_connect(
//				Integer.parseInt(activeUser)|0x80000000, 886976412, codeStr1, codeStr2,cHostName.getBytes() ) == 1) {
//			return true;
//		} else {
//			return false;
//		}
        int connect;
        int[] iCustomerID = new int[10];
        iCustomerID[0] = 0;
        iCustomerID[1] = 0;
        iCustomerID[2] = 0;
        iCustomerID[3] = 0;
        iCustomerID[4] = 0;
        iCustomerID[5] = 0;
        iCustomerID[6] = 0;
        iCustomerID[7] = 0;
        iCustomerID[8] = 0;
        iCustomerID[9] = 0;
        
        
		if(activeUser.equals("517400")){
			connect=MediaPlayer.getInstance().native_p2p_connect(Integer.parseInt(activeUser), 886976412, codeStr1, codeStr2,cHostName.getBytes(),iCustomerID);
		}else{
			connect=MediaPlayer.getInstance().native_p2p_connect(Integer.parseInt(activeUser)|0x80000000, 886976412, codeStr1, codeStr2,cHostName.getBytes(),iCustomerID);
		}
		if (connect == 1) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * p2p断开
	 */
	public void p2pDisconnect() {
		MediaPlayer.getInstance().native_p2p_disconnect();
	}

	/*
	 * 获取WIFI列表
	 */
	public void getWifiList(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getWifiList");
		if (P2PHandler.MSG_ID_GETTING_WIFI_LIST >= (Constants.MsgSection.MSG_ID_GETTING_WIFI_LIST)) {
			P2PHandler.MSG_ID_GETTING_WIFI_LIST = Constants.MsgSection.MSG_ID_GETTING_WIFI_LIST - 1000;
		}

		MediaPlayer.iGetNPCWifiList(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_GETTING_WIFI_LIST);
		P2PHandler.MSG_ID_GETTING_WIFI_LIST++;
	}

	/*
	 * 设置WIFI
	 */
	public void setWifi(String contactId, String password, int type,
			String name, String wifiPassword) {
		Log.e(TAG, "P2PHANDLER:setWifi");
		String s = null;
		byte[] bt;
		try {
			bt = name.getBytes("UTF-8");
			for(int i=0;i<bt.length;i++){
				s=s+"  "+bt[i];
			}
			Log.e("setwifiname","--"+s);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (P2PHandler.MSG_ID_SETTING_WIFI >= (Constants.MsgSection.MSG_ID_SETTING_WIFI)) {
			P2PHandler.MSG_ID_SETTING_WIFI = Constants.MsgSection.MSG_ID_SETTING_WIFI - 1000;
		}

		MediaPlayer.iSetNPCWifi(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_SETTING_WIFI, type,
				name.getBytes(), name.length(), wifiPassword.getBytes(),
				wifiPassword.length());
		P2PHandler.MSG_ID_SETTING_WIFI++;
	}

	/*
	 * 获取NPC各种设置
	 */
	public void getNpcSettings(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getNpcSettings");

		int iPassword = Integer.MAX_VALUE;
		try {
			iPassword = Integer.parseInt(password);
		} catch (Exception e) {
			iPassword = Integer.MAX_VALUE;
		}

		if (P2PHandler.MSG_ID_GETTING_NPC_SETTINGS >= (Constants.MsgSection.MSG_ID_GETTING_NPC_SETTINGS)) {
			P2PHandler.MSG_ID_GETTING_NPC_SETTINGS = Constants.MsgSection.MSG_ID_GETTING_NPC_SETTINGS - 1000;
		}

		MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId), iPassword,
				P2PHandler.MSG_ID_GETTING_NPC_SETTINGS);
		P2PHandler.MSG_ID_GETTING_NPC_SETTINGS++;
	}

	/*
	 * 获取布放状态
	 */
	public void getDefenceStates(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getDefenceStates");

		int iPassword = Integer.MAX_VALUE;
		try {
			iPassword = Integer.parseInt(password);
		} catch (Exception e) {
			iPassword = Integer.MAX_VALUE;
		}

		if (P2PHandler.MSG_ID_GET_DEFENCE_STATE >= (Constants.MsgSection.MSG_ID_GET_DEFENCE_STATE)) {
			P2PHandler.MSG_ID_GET_DEFENCE_STATE = Constants.MsgSection.MSG_ID_GET_DEFENCE_STATE - 1000;
		}

		MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId), iPassword,
				P2PHandler.MSG_ID_GET_DEFENCE_STATE);
		P2PHandler.MSG_ID_GET_DEFENCE_STATE++;
	}

	/*
	 * 检查密码
	 */
	public void checkPassword(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:checkPassword");
		if (P2PHandler.MSG_ID_CHECK_DEVICE_PASSWORD >= (Constants.MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD)) {
			P2PHandler.MSG_ID_CHECK_DEVICE_PASSWORD = Constants.MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD - 1000;
		}

		MediaPlayer.iGetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_CHECK_DEVICE_PASSWORD);
		P2PHandler.MSG_ID_CHECK_DEVICE_PASSWORD++;
	}

	/*
	 * 获取防区设置
	 */
	public void getDefenceArea(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getDefenceArea");
		if (P2PHandler.MSG_ID_GETTING_DEFENCEAREA >= (Constants.MsgSection.MSG_ID_GETTING_DEFENCEAREA)) {
			P2PHandler.MSG_ID_GETTING_DEFENCEAREA = Constants.MsgSection.MSG_ID_GETTING_DEFENCEAREA - 1000;
		}

		MediaPlayer.iGetAlarmCodeStatus(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_GETTING_DEFENCEAREA);
		P2PHandler.MSG_ID_GETTING_DEFENCEAREA++;
	}

	/*
	 * 设置远程布防
	 */
	public void setRemoteDefence(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setRemoteDefence");
		if (P2PHandler.MSG_ID_SET_REMOTE_DEFENCE >= (Constants.MsgSection.MSG_ID_SET_REMOTE_DEFENCE)) {
			P2PHandler.MSG_ID_SET_REMOTE_DEFENCE = Constants.MsgSection.MSG_ID_SET_REMOTE_DEFENCE - 1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_SET_REMOTE_DEFENCE,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_DEFENCE,
				value);
		P2PHandler.MSG_ID_SET_REMOTE_DEFENCE++;
	}

	public  void SetSystemMessageIndex(int iSystemMessageType ,  int iSystemMessageIndex)
	{
		MediaPlayer.SetSystemMessageIndex(iSystemMessageType ,  iSystemMessageIndex);
	}
	
	public  void vSendWiFiCmd(int iType , byte[] SSID, int iSSIDLen,byte[] Password, int iPasswordLen)
	{
		MediaPlayer.vSendWiFiCmd( iType ,  SSID,  iSSIDLen, Password,  iPasswordLen);
	}
	
	/*
	 * 设置远程录像
	 */
	public void setRemoteRecord(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setRemoteRecord");
		if (P2PHandler.MSG_ID_SET_REMOTE_RECORD >= (Constants.MsgSection.MSG_ID_SET_REMOTE_RECORD)) {
			P2PHandler.MSG_ID_SET_REMOTE_RECORD = Constants.MsgSection.MSG_ID_SET_REMOTE_RECORD - 1000;
		}
		MediaPlayer
				.iSetNPCSettings(
						Integer.parseInt(contactId),
						Integer.parseInt(password),
						P2PHandler.MSG_ID_SET_REMOTE_RECORD,
						Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_RECORD,
						value);
		P2PHandler.MSG_ID_SET_REMOTE_RECORD++;
	}

	/*
	 * 设置设备时间
	 */
	public void setDeviceTime(String contactId, String password, String time) {
		Log.e(TAG, "P2PHANDLER:setDeviceTime");
		if (P2PHandler.MSG_ID_SETTING_DEVICE_TIME >= (Constants.MsgSection.MSG_ID_SETTING_DEVICE_TIME)) {
			P2PHandler.MSG_ID_SETTING_DEVICE_TIME = Constants.MsgSection.MSG_ID_SETTING_DEVICE_TIME - 1000;
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date date = null;
		try {
			date = df.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int parseTime = 0;
		
		if (null != date) {

			if(time.substring(11, 13).equals("12")){
				parseTime = ((calendar.get(Calendar.YEAR) - 2000) << 24)
						| (((calendar.get(Calendar.MONTH) + 1) << 18))
						| (calendar.get(Calendar.DAY_OF_MONTH) << 12)
						| (12 << 6)
						| (calendar.get(Calendar.MINUTE) << 0);
			}else{
				parseTime = ((calendar.get(Calendar.YEAR) - 2000) << 24)
						| (((calendar.get(Calendar.MONTH) + 1) << 18))
						| (calendar.get(Calendar.DAY_OF_MONTH) << 12)
						| (calendar.get(Calendar.HOUR_OF_DAY) << 6)
						| (calendar.get(Calendar.MINUTE) << 0);
			}
			

		}
		MediaPlayer.iSetNPCDateTime(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_SETTING_DEVICE_TIME,
				parseTime);
		P2PHandler.MSG_ID_SETTING_DEVICE_TIME++;
	}

	/*
	 * 获取设备时间
	 */
	public void getDeviceTime(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getDeviceTime");
		if (P2PHandler.MSG_ID_GETTING_DEVICE_TIME >= (Constants.MsgSection.MSG_ID_GETTING_DEVICE_TIME)) {
			P2PHandler.MSG_ID_GETTING_DEVICE_TIME = Constants.MsgSection.MSG_ID_GETTING_DEVICE_TIME - 1000;
		}

		MediaPlayer.iGetNPCDateTime(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_GETTING_DEVICE_TIME);
		P2PHandler.MSG_ID_GETTING_DEVICE_TIME++;
	}

	/*
	 * 设置设备音量
	 */
	public void setVideoVolume(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setVideoVolume");
		if (P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME)) {
			P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_VOLUME, value);
		P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME++;
	}

	/*
	 * 设置视频格式
	 */
	public void setVideoFormat(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setVideoFormat");
		if (P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT)) {
			P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_VIDEO_FORMAT, value);
		P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT++;
	}

	/*
	 * 设置录像类型
	 */
	public void setRecordType(String contactId, String password, int type) {
		Log.e(TAG, "P2PHANDLER:setRecordType");
		if (P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE)) {
			P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TYPE, type);
		P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE++;
	}

	/*
	 * 设置录像时间
	 */
	public void setRecordTime(String contactId, String password, int time) {
		Log.e(TAG, "P2PHANDLER:setRecordTime");
		if (P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME)) {
			P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TIME, time);
		P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME++;
	}

	/*
	 * 设置录像计划时间
	 */
	public void setRecordPlanTime(String contactId, String password, String time) {
		Log.e(TAG, "P2PHANDLER:setRecordPlanTime");
		if (P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME)) {
			P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME - 1000;
		}

		int iTime = MyUtils.convertPlanTime(time);
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_PLAN_TIME,
				iTime);
		P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME++;
	}

	/*
	 * 设置防区状态
	 */
	public void setDefenceAreaState(String contactId, String password,
			int group, int item, int type) {
		Log.e(TAG, "P2PHANDLER:setDefenceAreaState");
		if (P2PHandler.MSG_ID_SETTING_DEFENCEAREA >= (Constants.MsgSection.MSG_ID_SETTING_DEFENCEAREA)) {
			P2PHandler.MSG_ID_SETTING_DEFENCEAREA = Constants.MsgSection.MSG_ID_SETTING_DEFENCEAREA - 1000;
		}

		MediaPlayer.iSetAlarmCodeStatus(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_SETTING_DEFENCEAREA, 1,
				type, new int[] { group }, new int[] { item });
		P2PHandler.MSG_ID_SETTING_DEFENCEAREA++;
	}
	
	/*
	 * 清空防区状态
	 */
	public void clearDefenceAreaState(String contactId, String password,
			int group) {
		Log.e(TAG, "P2PHANDLER:setDefenceAreaState");
		if (P2PHandler.MSG_ID_CLEAR_DEFENCE_GROUP >= (Constants.MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP)) {
			P2PHandler.MSG_ID_CLEAR_DEFENCE_GROUP = Constants.MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP - 1000;
		}

		MediaPlayer.iClearAlarmCodeGroup(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_CLEAR_DEFENCE_GROUP,
			    group);
		
		P2PHandler.MSG_ID_CLEAR_DEFENCE_GROUP++;
	}

	
	/*
	 * 设置网络类型
	 */
	public void setNetType(String contactId, String password, int type) {
		Log.e(TAG, "P2PHANDLER:setNetType");
		if (P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE)) {
			P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_NET_TYPE, type);
		P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE++;
	}

	/*
	 * 设置绑定报警ID
	 */
	public void setBindAlarmId(String contactId, String password, int count,
			String[] datas) {
		Log.e(TAG, "P2PHANDLER:setBindAlarmId");
		if (P2PHandler.MSG_ID_SETTING_ALARM_BIND_ID >= (Constants.MsgSection.MSG_ID_SETTING_ALARM_BIND_ID)) {
			P2PHandler.MSG_ID_SETTING_ALARM_BIND_ID = Constants.MsgSection.MSG_ID_SETTING_ALARM_BIND_ID - 1000;
		}
		int[] iData = new int[datas.length];
		try {

			for (int i = 0; i < datas.length; i++) {
				iData[i] = Integer.parseInt(datas[i]);
			}
		} catch (Exception e) {
			iData = new int[] { 0 };
			count = 1;
		}
		MediaPlayer.iSetBindAlarmId(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_SETTING_ALARM_BIND_ID,
				count, iData);
		P2PHandler.MSG_ID_SETTING_ALARM_BIND_ID++;
	}

	/*
	 * 获取绑定报警ID
	 */
	public void getBindAlarmId(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getBindAlarmId");
		if (P2PHandler.MSG_ID_GETTING_ALARM_BIND_ID >= (Constants.MsgSection.MSG_ID_GETTING_ALARM_BIND_ID)) {
			P2PHandler.MSG_ID_GETTING_ALARM_BIND_ID = Constants.MsgSection.MSG_ID_GETTING_ALARM_BIND_ID - 1000;
		}

		MediaPlayer.iGetBindAlarmId(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_GETTING_ALARM_BIND_ID);
		P2PHandler.MSG_ID_GETTING_ALARM_BIND_ID++;
	}

	/*
	 * 设置报警邮箱
	 */
	public void setAlarmEmail(String contactId, String password, String email) {
		Log.e(TAG, "P2PHANDLER:setAlarmEmail");
		if (P2PHandler.MSG_ID_SETTING_ALARM_EMAIL >= (Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL)) {
			P2PHandler.MSG_ID_SETTING_ALARM_EMAIL = Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL - 1000;
		}

		MediaPlayer.iSetNPCEmail(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_SETTING_ALARM_EMAIL,
				email.getBytes(), email.length());
		P2PHandler.MSG_ID_SETTING_ALARM_EMAIL++;
	}

	/*
	 * 获取报警邮箱
	 */
	public void getAlarmEmail(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:getAlarmEmail");
		if (P2PHandler.MSG_ID_GETTING_ALARM_EMAIL >= (Constants.MsgSection.MSG_ID_GETTING_ALARM_EMAIL)) {
			P2PHandler.MSG_ID_GETTING_ALARM_EMAIL = Constants.MsgSection.MSG_ID_GETTING_ALARM_EMAIL - 1000;
		}

		MediaPlayer.iGetNPCEmail(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_GETTING_ALARM_EMAIL);
		P2PHandler.MSG_ID_GETTING_ALARM_EMAIL++;
	}

	/*
	 * 设置蜂鸣器
	 */
	public void setBuzzer(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setBuzzer");
		if (P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_BUZZER >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER)) {
			P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_BUZZER = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_BUZZER,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_BUZZER, value);
		P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_BUZZER++;
	}

	/*
	 * 设置移动侦测
	 */
	public void setMotion(String contactId, String password, int value) {
		Log.e(TAG, "P2PHANDLER:setMotion");
		if (P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_MOTION >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION)) {
			P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_MOTION = Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_MOTION,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_MOTION_DECT, value);
		P2PHandler.MSG_ID_SETTING_NPC_SETTINGS_MOTION++;
	}

	/*
	 * 设置初始密码
	 */
	public void setInitPassword(String contactId, String password) {
		Log.e(TAG, "P2PHANDLER:setInitPassword");
		if (P2PHandler.MSG_ID_SETTING_INIT_PASSWORD >= (Constants.MsgSection.MSG_ID_SETTING_INIT_PASSWORD)) {
			P2PHandler.MSG_ID_SETTING_INIT_PASSWORD = Constants.MsgSection.MSG_ID_SETTING_INIT_PASSWORD - 1000;
		}

		MediaPlayer.iSetInitPassword(Integer.parseInt(contactId), 0,
				P2PHandler.MSG_ID_SETTING_INIT_PASSWORD, Integer.parseInt(password));
		P2PHandler.MSG_ID_SETTING_INIT_PASSWORD++;
	}

	/*
	 * 设置设备密码
	 */
	public void setDevicePassword(String contactId, String oldPassword,
			String newPassword) {
		Log.e(TAG, "P2PHANDLER:setDevicePassword");
		if (P2PHandler.MSG_ID_SETTING_DEVICE_PASSWORD >= (Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD)) {
			P2PHandler.MSG_ID_SETTING_DEVICE_PASSWORD = Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(oldPassword),
				P2PHandler.MSG_ID_SETTING_DEVICE_PASSWORD,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_DEVICE_PWD,
				Integer.parseInt(newPassword));
		P2PHandler.MSG_ID_SETTING_DEVICE_PASSWORD++;
	}
	/*
	 * 设置访客密码
	 */
	public void setDeviceVisitorPassword(String contactId, String oldPassword,
			String visitorPassword) {
		Log.e(TAG, "P2PHANDLER:setDevicePassword");
		if (P2PHandler.MESG_STTING_ID_GUEST_PASSWD >= (Constants.MsgSection.MESG_STTING_ID_GUEST_PASSWD)) {
			P2PHandler.MESG_STTING_ID_GUEST_PASSWD = Constants.MsgSection.MESG_STTING_ID_GUEST_PASSWD - 1000;
		}

		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(oldPassword),
				P2PHandler.MESG_STTING_ID_GUEST_PASSWD,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_GUEST_PASSWD,
				Integer.parseInt(visitorPassword));
		P2PHandler.MESG_STTING_ID_GUEST_PASSWD++;
	}
	/*
	 * 获取好友状态
	 */

	public void getFriendStatus(String[] contactIds) {
		Log.e(TAG, "P2PHANDLER:getFriendStatus");
		int[] friends = new int[contactIds.length];
		for (int i = 0; i < contactIds.length; i++) {
			if (contactIds[i].substring(0, 1).equals("0")) {
				friends[i] = Integer.parseInt(contactIds[i]) | 0x80000000;
			} else {
				friends[i] = Integer.parseInt(contactIds[i]);
			}
		}
		MediaPlayer.iGetFriendsStatus(friends, friends.length);
	}

	/*
	 * 获取录像列表
	 */

	public void getRecordFiles(String contactId, String password,
			int timeInterval) {
		Log.e(TAG, "P2PHANDLER:getRecordFiles");
		if (P2PHandler.MSG_ID_GETTING_RECORD_FILE_LIST >= (Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST)) {
			P2PHandler.MSG_ID_GETTING_RECORD_FILE_LIST = Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST - 1000;
		}
		Timestamp now = new Timestamp(System.currentTimeMillis());
		int i_start;
		if (now.getDate() < timeInterval) {
			i_start = ((now.getYear() - 100) << 24)
					| (((now.getMonth()) << 18))
					| ((timeInterval - now.getDate()) << 12)
					| ((now.getHours()) << 6) | ((now.getMinutes()) << 0);
		} else {
			i_start = ((now.getYear() - 100) << 24)
					| (((now.getMonth() + 1) << 18))
					| ((now.getDate() - timeInterval) << 12)
					| ((now.getHours()) << 6) | ((now.getMinutes()) << 0);
		}
		int i_end = ((now.getYear() - 100) << 24)
				| (((now.getMonth() + 1) << 18)) | ((now.getDate()) << 12)
				| ((now.getHours()) << 6) | ((now.getMinutes()) << 0);
		Log.e("timestamp","year"+now.getYear()+"month"+now.getMonth()+"hour"+now.getHours());
		Log.e("timestamp","i_start="+i_start+"i_end="+i_end);
		MediaPlayer.iGetRecFiles(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_GETTING_RECORD_FILE_LIST, i_start, i_end);
		P2PHandler.MSG_ID_GETTING_RECORD_FILE_LIST++;
	}

	/*
	 * 获取录像列表
	 */

	public void getRecordFiles(String contactId, String password, Date start,
			Date end) {
		Log.e(TAG, "P2PHANDLER:getRecordFiles");
		if (P2PHandler.MSG_ID_GETTING_RECORD_FILE_LIST >= (Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST)) {
			P2PHandler.MSG_ID_GETTING_RECORD_FILE_LIST = Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST - 1000;
		}

		int i_start = ((start.getYear() - 100) << 24)
				| (((start.getMonth() + 1) << 18)) | ((start.getDate()) << 12)
				| ((start.getHours()) << 6) | ((start.getMinutes()) << 0);
		int i_end = ((end.getYear() - 100) << 24)
				| (((end.getMonth() + 1) << 18)) | ((end.getDate()) << 12)
				| ((end.getHours()) << 6) | ((end.getMinutes()) << 0);
		Log.e("timestamp","i_start="+i_start+"i_end="+i_end);
		MediaPlayer.iGetRecFiles(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MSG_ID_GETTING_RECORD_FILE_LIST, i_start, i_end);
		P2PHandler.MSG_ID_GETTING_RECORD_FILE_LIST++;
	}

	/*
	 * 发送端消息
	 */
	public String sendMessage(String contactId, String msg) {
		Log.e(TAG, "P2PHANDLER:sendMessage");
		if (P2PHandler.MSG_ID_SEND_MESSAGE >= (Constants.MsgSection.MSG_ID_SEND_MESSAGE)) {
			P2PHandler.MSG_ID_SEND_MESSAGE = Constants.MsgSection.MSG_ID_SEND_MESSAGE - 1000;
		}
		int iId = Integer.parseInt(contactId) | 0x80000000;
		MediaPlayer.iSendMesgToFriend(iId, P2PHandler.MSG_ID_SEND_MESSAGE,
				msg.getBytes(), msg.getBytes().length);
		P2PHandler.MSG_ID_SEND_MESSAGE++;
		return String.valueOf(P2PHandler.MSG_ID_SEND_MESSAGE - 1);
	}

	/*
	 * 发送自定义命令
	 */
	public String sendCustomCmd(String contactId, String password, String msg) {
		Log.e(TAG, "P2PHANDLER:sendCustomCmd");
		if (P2PHandler.MSG_ID_SEND_CUSTOM_CMD >= (Constants.MsgSection.MSG_ID_SEND_CUSTOM_CMD)) {
			P2PHandler.MSG_ID_SEND_CUSTOM_CMD = Constants.MsgSection.MSG_ID_SEND_CUSTOM_CMD - 1000;
		}
		int iId = Integer.parseInt(contactId);
		MediaPlayer.iSendCmdToFriend(iId, Integer.parseInt(password),
				P2PHandler.MSG_ID_SEND_CUSTOM_CMD, msg.getBytes(),
				msg.getBytes().length);
		P2PHandler.MSG_ID_SEND_CUSTOM_CMD++;
		return String.valueOf(P2PHandler.MSG_ID_SEND_CUSTOM_CMD - 1);
	}

	/*
	 * 打开音频设备
	 */
	public void openAudioAndStartPlaying() {
		try {
			MediaPlayer.getInstance();
			MediaPlayer.openAudioTrack();
			MediaPlayer.getInstance()._StartPlaying(
					Constants.P2P_WINDOW.P2P_SURFACE_START_PLAYING_WIDTH,
					Constants.P2P_WINDOW.P2P_SURFACE_START_PLAYING_HEIGHT);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 挂断p2p
	 */
	public synchronized void reject() {
		synchronized (P2PHandler.this) {
			MediaPlayer.getInstance().native_p2p_hungup();
		}

	}

	/*
	 * 接听
	 */
	public void accept() {
		MediaPlayer.getInstance().native_p2p_accpet();
	}

	/*
	 * 呼叫
	 */

	public boolean call(final String contactId, final String password,
			final boolean isOutCall, final int callType, final String callId,
			final String ipFlag, final String pushMsg) {
		boolean result = false;
		byte[] byt=new byte[8];
		try {
			if (isOutCall) {
				String parseNum = callId;

				if (parseNum.contains("+")) {
					boolean isPhone = false;
					for (int i = 0; i < regionCode.length; i++) {
						int cLength = String.valueOf(regionCode[i]).length();
						parseNum = parseNum.replace("+", "");
						String hight = parseNum.substring(0, cLength);
						String low = parseNum.substring(cLength,
								parseNum.length());
						if (Integer.parseInt(hight) == regionCode[i]) {
							long num = (Long.parseLong(hight) << 48 | Long
									.parseLong(low));
							parseNum = String.valueOf(num);
							isPhone = true;
							break;
						}
					}

					if (!isPhone) {
						return result;
					}
				}

				long id = Long.parseLong(parseNum);
				if (parseNum.charAt(0) == '0') {
					id = 0 - id;
				}

				int pwd = 0;

				int isMonitor = 0;
				if (callType == Constants.P2P_TYPE.P2P_TYPE_MONITOR) {
					isMonitor = 1;
					pwd = Integer.parseInt(password);
				}

				int x = 0;
				if (null != ipFlag && !ipFlag.equals("")
						&& MyUtils.isNumeric(ipFlag)) {
					x = MediaPlayer.getInstance().native_p2p_call(
							Integer.parseInt(ipFlag), isMonitor, pwd, -1,byt,
							pushMsg.getBytes("utf-8"));
				} else {
					x = MediaPlayer.getInstance().native_p2p_call(id,
							isMonitor, pwd, -1,byt, pushMsg.getBytes("utf-8"));
				}

				if (x == 1) {
					result = true;
					Log.i("tag", "p2p call success");
				} else {
					Log.e("tag", "p2p call fail");
				}

			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;

		// new DelayThread(1000, new DelayThread.OnRunListener() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// try {
		// synchronized (P2PHandler.this) {
		// call_alter(contactId, password, isOutCall, callType,
		// callId, ipFlag, pushMsg);
		// }
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// }).start();
	}

	private static int[] regionCode = { 1264, 1268, 1242, 1246, 1441, 1284,
			1345, 1767, 1809, 1473, 1876, 1664, 1787, 1869, 1758, 1784, 1868,
			1649, 1340, 1671, 1670, 210, 211, 212, 213, 214, 215, 216, 217,
			218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230,
			231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243,
			244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256,
			257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269,
			290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 350, 351, 352,
			353, 354, 355, 356, 357, 358, 359, 370, 371, 372, 373, 374, 375,
			376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388,
			389, 420, 421, 422, 423, 424, 425, 426, 427, 428, 429, 500, 501,
			502, 503, 504, 505, 506, 507, 508, 509, 590, 591, 592, 593, 594,
			595, 596, 597, 598, 599, 670, 671, 672, 673, 674, 675, 676, 677,
			678, 679, 680, 681, 682, 683, 684, 685, 686, 687, 688, 689, 690,
			691, 692, 693, 694, 695, 696, 697, 698, 699, 800, 801, 802, 803,
			804, 805, 806, 807, 808, 809, 850, 851, 852, 853, 854, 855, 856,
			857, 858, 859, 870, 871, 872, 873, 874, 875, 876, 877, 878, 879,
			880, 881, 882, 883, 884, 885, 886, 887, 888, 889, 960, 961, 962,
			963, 964, 965, 966, 967, 968, 969, 970, 971, 972, 973, 974, 975,
			976, 977, 978, 979, 990, 991, 992, 993, 994, 995, 996, 997, 998,
			999, 20, 27, 28, 30, 31, 32, 33, 34, 36, 37, 38, 39, 40, 41, 42,
			43, 44, 45, 46, 47, 48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 60, 61,
			62, 63, 64, 65, 66, 81, 82, 83, 84, 86, 90, 91, 92, 93, 94, 95, 98,
			1, 7 };

	private void call_alter(String threeNumber, String password,
			boolean isOutCall, int callType, String callId, String ipFlag,
			String pushMsg) throws UnsupportedEncodingException {
		byte[] byt=new byte[8];
		if (isOutCall) {
			String parseNum = callId;
			if (parseNum.contains("+")) {
				boolean isPhone = false;
				try {
					for (int i = 0; i < regionCode.length; i++) {
						int cLength = String.valueOf(regionCode[i]).length();
						parseNum = parseNum.replace("+", "");
						String hight = parseNum.substring(0, cLength);
						String low = parseNum.substring(cLength,
								parseNum.length());
						if (Integer.parseInt(hight) == regionCode[i]) {
							long num = (Long.parseLong(hight) << 48 | Long
									.parseLong(low));
							parseNum = String.valueOf(num);
							isPhone = true;
							break;
						}
					}
				} catch (Exception e) {
					// onCallResult(Constants.P2P_CALL.CALL_RESULT.CALL_PHONE_FORMAT_ERROR);
				}

				if (!isPhone) {
					// onCallResult(Constants.P2P_CALL.CALL_RESULT.CALL_PHONE_FORMAT_ERROR);
					return;
				}
			}

			long id = Long.parseLong(parseNum);
			if (parseNum.charAt(0) == '0') {
				id = 0 - id;
			}

			int pwd = 0;

			int isMonitor = 0;
			if (callType == Constants.P2P_TYPE.P2P_TYPE_MONITOR) {
				isMonitor = 1;
				if (MyUtils.isNumeric(password)) {
					pwd = Integer.parseInt(password);
				}
			}

			int result = 0;
			if (null != ipFlag && !ipFlag.equals("")
					&& MyUtils.isNumeric(ipFlag)) {
				result = MediaPlayer.getInstance().native_p2p_call(
						Integer.parseInt(ipFlag), isMonitor, pwd, -1,byt,
						pushMsg.getBytes("utf-8"));
			} else {
				result = MediaPlayer.getInstance().native_p2p_call(id,
						isMonitor, pwd, -1,byt, pushMsg.getBytes("utf-8"));
			}

			if (result == 1) {
				Log.i("tag", "p2p call success");
			} else {
				Log.e("tag", "p2p call fail");
			}
		}
	}

	/*
	 * 录像回放连接
	 */
	public void playbackConnect(String contactId, String password,String filename,
			int recordFilePosition) {
		byte[] byt=filename.getBytes();
		MediaPlayer.getInstance().native_p2p_call(Integer.parseInt(contactId),
				Constants.P2P_TYPE.P2P_TYPE_PLAYBACK,
				Integer.parseInt(password), recordFilePosition,byt, "".getBytes());
	}

	/*
	 * 设置视频模式
	 */

	public int setVideoMode(int type) {
		MediaPlayer.getInstance();
		return MediaPlayer.iSetVideoMode(type);
	}

	public void checkDeviceUpdate(String contactId, String password) {

		Log.e(TAG, "P2PHANDLER:checkDeviceUpdate");
		if (P2PHandler.MSG_ID_CHECK_DEVICE_UPDATE >= (Constants.MsgSection.MSG_ID_CHECK_DEVICE_UPDATE)) {
			P2PHandler.MSG_ID_CHECK_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_CHECK_DEVICE_UPDATE - 1000;
		}

		MediaPlayer.getInstance();
		MediaPlayer.checkDeviceUpdate(
				Integer.parseInt(contactId), Integer.parseInt(password),
				P2PHandler.MSG_ID_CHECK_DEVICE_UPDATE);
		P2PHandler.MSG_ID_CHECK_DEVICE_UPDATE++;

	}

	public void doDeviceUpdate(String contactId, String password) {

		Log.e(TAG, "P2PHANDLER:doDeviceUpdate");
		if (P2PHandler.MSG_ID_DO_DEVICE_UPDATE >= (Constants.MsgSection.MSG_ID_DO_DEVICE_UPDATE)) {
			P2PHandler.MSG_ID_DO_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_DO_DEVICE_UPDATE - 1000;
		}

		MediaPlayer.getInstance();
		MediaPlayer.doDeviceUpdate(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_DO_DEVICE_UPDATE);
		P2PHandler.MSG_ID_DO_DEVICE_UPDATE++;

	}

	public void cancelDeviceUpdate(String contactId, String password) {

		Log.e(TAG, "P2PHANDLER:cancelDeviceUpdate");
		if (P2PHandler.MSG_ID_CANCEL_DEVICE_UPDATE >= (Constants.MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE)) {
			P2PHandler.MSG_ID_CANCEL_DEVICE_UPDATE = Constants.MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE - 1000;
		}

		MediaPlayer.getInstance();
		MediaPlayer.cancelDeviceUpdate(
				Integer.parseInt(contactId), Integer.parseInt(password),
				P2PHandler.MSG_ID_CANCEL_DEVICE_UPDATE);
		P2PHandler.MSG_ID_CANCEL_DEVICE_UPDATE++;

	}

	public void getDeviceVersion(String contactId, String password) {

		Log.e(TAG, "P2PHANDLER:getDeviceVersion");
		if (P2PHandler.MSG_ID_GET_DEVICE_VERSION >= (Constants.MsgSection.MSG_ID_GET_DEVICE_VERSION)) {
			P2PHandler.MSG_ID_GET_DEVICE_VERSION = Constants.MsgSection.MSG_ID_GET_DEVICE_VERSION - 1000;
		}

		MediaPlayer.getInstance();
		MediaPlayer.getDeviceVersion(Integer.parseInt(contactId),
				Integer.parseInt(password), P2PHandler.MSG_ID_GET_DEVICE_VERSION);
		P2PHandler.MSG_ID_GET_DEVICE_VERSION++;
	}

	public boolean sendCtlCmd(int cmd, int option) {
		if (MediaPlayer.iSendCtlCmd(cmd, option) == 1) {
			return true;
		} else {
			return false;
		}
	}

	public void setBindFlag(int flag) {
		MediaPlayer.setBindFlag(flag);
	}
	
		public void setRecvAVDataEnable(boolean fgEn){
		
		MediaPlayer.getInstance()._SetRecvAVDataEnable(fgEn);
	}
	
	public void setImageReverse(String contactId,String password,int value){
		Log.e(TAG, "P2PHANDLER:setImageReverse");
		if((this.MESG_ID_STTING_PIC_REVERSE)>=(Constants.MsgSection.MESG_ID_STTING_PIC_REVERSE)){
			P2PHandler.MESG_ID_STTING_PIC_REVERSE=Constants.MsgSection.MESG_ID_STTING_PIC_REVERSE-1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
				Integer.parseInt(password), 
				P2PHandler.MESG_ID_STTING_PIC_REVERSE,
				Constants.P2P_SETTING.SETTING_TYPE.SETTING_IMAGE_REVERSE,
				value);
		P2PHandler.MESG_ID_STTING_PIC_REVERSE++;
	}
	public void setInfraredSwitch(String contactId,String password,int value){
		if((this.MESG_ID_STTING_IR_ALARM_EN)>=(Constants.MsgSection.MESG_ID_STTING_IR_ALARM_EN)){
			P2PHandler.MESG_ID_STTING_IR_ALARM_EN=Constants.MsgSection.MESG_ID_STTING_IR_ALARM_EN-1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
					Integer.parseInt(password), 
					P2PHandler.MESG_ID_STTING_IR_ALARM_EN,
					Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_IR_ALARM_EN,
					value);
		P2PHandler.MESG_ID_STTING_IR_ALARM_EN++;
	}
	public void setWiredAlarmInput(String contactId,String password,int value){
		if((this.MESG_STTING_ID_EXTLINE_ALARM_IN_EN)>=(Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN)){
			P2PHandler.MESG_STTING_ID_EXTLINE_ALARM_IN_EN=Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN-1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
					Integer.parseInt(password), 
					P2PHandler.MESG_STTING_ID_EXTLINE_ALARM_IN_EN,
					Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_IN_EN,
					value);
		P2PHandler.MESG_STTING_ID_EXTLINE_ALARM_IN_EN++;
	}
	public void setWiredAlarmOut(String contactId,String password,int value){
		if((this.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN)>=(Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN)){
			P2PHandler.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN=Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN-1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
					Integer.parseInt(password), 
					P2PHandler.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN,
					Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_OUT_EN,
					value);
		P2PHandler.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN++;
	}
	public void setAutomaticUpgrade(String contactId,String password,int value){
		if((this.MESG_STTING_ID_SECUPGDEV)>=(Constants.MsgSection.MESG_STTING_ID_SECUPGDEV)){
			P2PHandler.MESG_STTING_ID_SECUPGDEV=Constants.MsgSection.MESG_STTING_ID_SECUPGDEV-1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
					Integer.parseInt(password), 
					P2PHandler.MESG_STTING_ID_SECUPGDEV,
					Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_SECUPGDEV,
					value);
		P2PHandler.MESG_STTING_ID_SECUPGDEV++;
	}
	public void setTimeZone(String contactId,String password,int value){
		if((this.MESG_STTING_ID_TIMEZONE)>=(Constants.MsgSection.MESG_STTING_ID_TIMEZONE)){
			P2PHandler.MESG_STTING_ID_TIMEZONE=Constants.MsgSection.MESG_STTING_ID_TIMEZONE-1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
					Integer.parseInt(password), 
					P2PHandler.MESG_STTING_ID_TIMEZONE,
					Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_TIMEZONE,
					value);
		P2PHandler.MESG_STTING_ID_TIMEZONE++;
	}
	public void getSdCardCapacity(String contactId,String password,String data){
		if((this.MESG_GET_SD_CARD_CAPACITY)>=(Constants.MsgSection.MESG_GET_SD_CARD_CAPACITY)){
			P2PHandler.MESG_GET_SD_CARD_CAPACITY=Constants.MsgSection.MESG_GET_SD_CARD_CAPACITY-1000;
		}
		byte[] datas= new byte[16];
		datas[0] = 80;
		datas[1] = 0;
		datas[2] = 0;
		datas[3] = 0;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MESG_GET_SD_CARD_CAPACITY,
				 datas, 4);
		P2PHandler.MESG_GET_SD_CARD_CAPACITY++;
	}
	public void setSdFormat(String contactId,String password,int SDcardID){
		if((this.MESG_SD_CARD_FORMAT)>=(Constants.MsgSection.MESG_SD_CARD_FORMAT)){
			P2PHandler.MESG_SD_CARD_FORMAT=Constants.MsgSection.MESG_SD_CARD_FORMAT-1000;
		}
		byte[] datas= new byte[16];
		datas[0] = 81;
		datas[1] = 0;
		datas[2] = 0;
		datas[3]=0;
		datas[4]=(byte)SDcardID;
		Log.e("id", "id:"+datas[4]);
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password),
				P2PHandler.MESG_SD_CARD_FORMAT,
				 datas, 5);
		P2PHandler.MESG_SD_CARD_FORMAT++;
	}
	public void setGPIO(String contactId,String password,int group,int pin){
		if((this.MESG_SET_GPIO)>=(Constants.MsgSection.MESG_SET_GPIO)){
			P2PHandler.MESG_SET_GPIO=Constants.MsgSection.MESG_SET_GPIO-1000;
		}
		byte[] datas=new byte[37];
		datas[0]=95;
		datas[1]=0;
	    datas[2]=(byte)group;
	    datas[3]=(byte)pin;
	    datas[4]=5;
	    datas[5]=(byte)(-15&0xFF);
	    datas[6]=(byte) (-15>>8&0xFF);
	    datas[7]=(byte) (-15>>16&0xFF);
	    datas[8]=(byte) (-15>>24&0xFF);
	    datas[9]=(byte)  (1000&0xFF);
	    datas[10]=(byte) (1000>>8&0xFF);
	    datas[11]=(byte) (1000>>16&0xFF);
	    datas[12]=(byte) (1000>>24&0xFF);
	    datas[13]=(byte) (-1000&0xFF);
	    datas[14]=(byte) (-1000>>8&0xFF);
	    datas[15]=(byte) (-1000>>16&0xFF);
	    datas[16]=(byte) (-1000>>24&0xFF);
	    datas[17]=(byte) (1000&0xFF);
	    datas[18]=(byte) (1000>>8&0xFF);
	    datas[19]=(byte) (1000>>16&0xFF);
	    datas[20]=(byte) (1000>>24&0xFF);
	    datas[21]=(byte) (-1000&0xFF);
	    datas[22]=(byte) (-1000>>8&0xFF);
	    datas[23]=(byte) (-1000>>16&0xFF);
	    datas[24]=(byte) (-1000>>24&0xFF);
	    for(int i=25;i<datas.length;i++){
	    	datas[i]=0;
	    }
	    String s="";
	    for(int j=0;j<datas.length;j++){
	    	s=s+" "+datas[j];
	    }
	    Log.e("GPIO","GPIO"+s+" "+"length="+datas.length);
	    MediaPlayer.iExtendedCmd(Integer.parseInt(contactId), 
	    		Integer.parseInt(password),
	    		P2PHandler.MESG_SET_GPIO, datas, datas.length);
		P2PHandler.MESG_SET_GPIO++;
	}
	public void setGPIO1_0(String contactId,String password){
		if((this.MESG_SET_GPI1_0)>=(Constants.MsgSection.MESG_SET_GPI1_0)){
			P2PHandler.MESG_SET_GPI1_0=Constants.MsgSection.MESG_SET_GPI1_0-1000;
		}
		byte[] datas=new byte[37];
		datas[0]=95;
		datas[1]=0;
	    datas[2]=1;
	    datas[3]=0;
	    datas[4]=3;
	    datas[5]=(byte)(-15&0xFF);
	    datas[6]=(byte) (-15>>8&0xFF);
	    datas[7]=(byte) (-15>>16&0xFF);
	    datas[8]=(byte) (-15>>24&0xFF);
	    datas[9]=(byte)  (6000&0xFF);
	    datas[10]=(byte) (6000>>8&0xFF);
	    datas[11]=(byte) (6000>>16&0xFF);
	    datas[12]=(byte) (6000>>24&0xFF);
	    datas[13]=(byte) (-6000&0xFF);
	    datas[14]=(byte) (-6000>>8&0xFF);
	    datas[15]=(byte) (-6000>>16&0xFF);
	    datas[16]=(byte) (-6000>>24&0xFF);
	    for(int i=17;i<datas.length;i++){
	    	datas[i]=0;
	    }
	    String s="";
	    for(int j=0;j<datas.length;j++){
	    	s=s+" "+datas[j];
	    }
	    Log.e("GPIO","GPIO"+s+" "+"length="+datas.length);
	    MediaPlayer.iExtendedCmd(Integer.parseInt(contactId), 
	    		Integer.parseInt(password),
	    		P2PHandler.MESG_SET_GPI1_0, datas, datas.length);
		P2PHandler.MESG_SET_GPI1_0++;
	}
	public void setPreRecord(String contactId,String password,int value){
		if((this.MESG_SET_PRE_RECORD)>=(Constants.MsgSection.MESG_SET_PRE_RECORD)){
			P2PHandler.MESG_SET_PRE_RECORD=Constants.MsgSection.MESG_SET_PRE_RECORD-1000;
		}
		MediaPlayer.iSetNPCSettings(Integer.parseInt(contactId),
					Integer.parseInt(password), 
					P2PHandler.MESG_SET_PRE_RECORD,
					Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_PRERECORD,
					value);
		P2PHandler.MESG_SET_PRE_RECORD++;
	}
	public void getDefenceAreaAlarmSwitch(String contactId,String password){
		if((this.MESG_GET_DEFENCE_AREA_SWITCH)>=(Constants.MsgSection.MESG_GET_DEFENCE_AREA_SWITCH)){
			P2PHandler.MESG_GET_DEFENCE_AREA_SWITCH=Constants.MsgSection.MESG_GET_DEFENCE_AREA_SWITCH-1000;
		}
		byte[] datas=new byte[12];
		datas[0]=82;
		datas[1]=0;
		datas[2]=8;
		datas[3]=0;
		datas[4]=0;
		datas[5]=0;
		datas[6]=0;
		datas[7]=0;
		datas[8]=0;
		datas[9]=0;
		datas[10]=0;
		datas[11]=0;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), 
				P2PHandler.MESG_GET_DEFENCE_AREA_SWITCH,
				datas, datas.length);
		P2PHandler.MESG_GET_DEFENCE_AREA_SWITCH++;
	}
	public void setDefenceAreaAlarmSwitch(String contactId,String password,int state,int group,int item){
		if((this.MESG_SET_DEFENCE_AREA_SWITCH)>=(Constants.MsgSection.MESG_SET_DEFENCE_AREA_SWITCH)){
			P2PHandler.MESG_SET_DEFENCE_AREA_SWITCH=Constants.MsgSection.MESG_SET_DEFENCE_AREA_SWITCH-1000;
		}
		byte[] datas=new byte[12];
		datas[0]=83;
		datas[1]=0;
		datas[2]=(byte)state;
		datas[3]=1;
		datas[4]=(byte) (group&0xFF);
		datas[5]=(byte)(group>>8&0xFF);
		datas[6]=(byte)(group>>16&0xFF);
		datas[7]=(byte)(group>>24&0xFF);
		datas[8]=(byte) (item&0xFF);
		datas[9]=(byte) (item>>8&0xFF);
		datas[10]=(byte) (item>>16&0xFF);
		datas[11]=(byte) (item>>24&0xFF);
		for(int i=0;i<datas.length;i++){
			Log.e("data", "datas[i]="+datas[i]);
		}
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), 
				P2PHandler.MESG_SET_DEFENCE_AREA_SWITCH,
				datas, datas.length);
		P2PHandler.MESG_SET_DEFENCE_AREA_SWITCH++;
	}
	public void getAlarmRecord(String contactId,String password){
		if((this.MESG_GET_ALARM_RECORD)>=(Constants.MsgSection.MESG_GET_ALARM_RECORD)){
			P2PHandler.MESG_GET_ALARM_RECORD=Constants.MsgSection.MESG_GET_ALARM_RECORD-1000;
		}
		byte[]datas=new byte[3];
		datas[0]=121;
		datas[1]=0;
	    datas[2]=0;
		MediaPlayer.iExtendedCmd(Integer.parseInt(contactId),
				Integer.parseInt(password), 
				P2PHandler.MESG_GET_ALARM_RECORD, 
				datas, datas.length);
		P2PHandler.MESG_GET_ALARM_RECORD++;
	}
}
