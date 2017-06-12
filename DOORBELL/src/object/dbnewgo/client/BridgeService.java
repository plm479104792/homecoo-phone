package object.dbnewgo.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.DataBaseHelper;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.RemoteViews;

@SuppressLint("NewApi")
public class BridgeService extends Service {
	private String TAG = BridgeService.class.getSimpleName();
	private DataBaseHelper baseHelper = null;
	private Notification mNotify2;
	private NotificationManager mCustomMgr;
	WakeLock wakeLock = null;

	// ��ȡ��Դ���ָ÷�������ĻϨ��ʱ��Ȼ��ȡCPUʱ����������
	private void acquireWakeLock() {
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "PostLocationService");
			if (null != wakeLock) {
				wakeLock.acquire();
			}
		}
	}

	// �ͷ��豸��Դ��
	private void releaseWakeLock() {
		if (null != wakeLock) {
			wakeLock.release();
			wakeLock = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("tag", "BridgeService onBind()");
		return new ControllerBinder();
	}

	/**
     * 
     * **/
	class ControllerBinder extends Binder {
		public BridgeService getBridgeService() {
			return BridgeService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("tag", "BridgeService onCreate()");
		baseHelper = DataBaseHelper.getInstance(this);
		mCustomMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		SystemValue.ISRUN = true;
		Log.d("tag", "BridgeService PPPPSetCallbackContext0");
		NativeCaller.PPPPSetCallbackContext(this);   
		Log.d("tag", "BridgeService PPPPSetCallbackContext1");
		acquireWakeLock();
		System.out.println("==***====ipdoorbell==BridgeService onCreate()==");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startForeground(
				R.drawable.homecoo_mini_logo,
				getNotification(getResources().getString(R.string.app_running),
						"", false));// set up service
		return super.onStartCommand(intent, flags, startId);
	}

	private Notification getNotification(String content, String did,
			boolean isAlarm) {
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = f.format(date);
		String titlePrompt = "";
		String title = "";
		PendingIntent pendingIntent = null;
		Intent intent = null;

		titlePrompt = getResources().getString(R.string.app_name) + " "
				+ content;
		title = getResources().getString(R.string.app_name);
		intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(this, MainActivity.class);
		mNotify2 = new Notification(R.drawable.homecoo_mini_logo, titlePrompt,
				System.currentTimeMillis());
		mNotify2.flags = Notification.FLAG_ONGOING_EVENT;
		pendingIntent = PendingIntent.getActivity(this, R.drawable.homecoo_mini_logo, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		mNotify2.contentIntent = pendingIntent;
		mNotify2.contentView = views;
		mNotify2.contentView.setTextViewText(R.id.no_title, title);
		mNotify2.contentView.setTextViewText(R.id.no_content, content);
		mNotify2.contentView.setTextViewText(R.id.no_time, strDate);
		mNotify2.contentView.setImageViewResource(R.id.no_img, R.drawable.homecoo_mini_logo);

		return mNotify2;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForeground(true);
		mCustomMgr.cancel(R.drawable.homecoo_mini_logo);
		SystemValue.ISRUN = false;
		if (baseHelper != null) {
			baseHelper.close();
		}
		releaseWakeLock();
		NativeCaller.Free();
	}

	public void CallBack_GetDBParm(String did, int type, int on1, int on2,
			int on3, int on4, int on5, int on6, int on7, int on8, int mode,
			int wind, int temperature, int indoor_temperature,
			int freezing_temperature, int freezing_enable, int keypad_locked) {

	}

	/**
	 * 
	 * PlayActivity feedback method
	 * 
	 * jni
	 * 
	 * @param videobuf
	 * 
	 * @param h264Data
	 * 
	 * @param len
	 * 
	 * @param width
	 * 
	 * @param height
	 * 
	 */
	@SuppressWarnings("unused")
	private void VideoData(String did, byte[] videobuf, int h264Data, int len,
			int width, int height, int time) {
		// Log.d(TAG, "Call VideoData...h264Data: " + h264Data + " len: " + len
		// + " videobuf len: " + videobuf.length + "  time==" + time);
		if (ipPlayInterface != null) {
			ipPlayInterface.callBaceVideoData(did, videobuf, h264Data, len,
					width, height);
		}
		if (playInterface != null) {
			playInterface.callBaceVideoData(did, videobuf, h264Data, len,
					width, height, time);
		}
	}

	public void DoorBellController(String did, int index1, int index2,
			int index3, int index4, int index5, int index6) {
		Log.d("test", "shix-doorbell did:" + did + "  index1=" + index1);
		if (ipcamClientInterface != null) {
			ipcamClientInterface.callBackDoorBellController(did, index1);
		}
		if (doorBellTimeDeLay != null) {
			doorBellTimeDeLay.CallBackTimeDelay(index4);
		}
	}

	public void DoorBellSetUp(String did, int result, int motion_enable,
			int motion_level, int pir_enable, int record_enable,
			int record_size, int on_delay_time, int alarm_delay_time,
			int record_cover) {
		Log.d("test", "shix-doorbell did:" + did + "  result=" + result
				+ "  motion_enable=" + motion_enable + "  motion_level="
				+ motion_level + "  pir_enable=" + pir_enable
				+ "  record_enable=" + record_enable + "  record_size="
				+ record_size + "  on_delay_time=" + on_delay_time
				+ "  alarm_delay_time=" + alarm_delay_time + "  record_cover="
				+ record_cover);

		if (doorBellAlerm != null) {
			doorBellAlerm.DoorBeelAlerm(did, result, motion_enable,
					motion_level, pir_enable, record_enable, record_size,
					on_delay_time, alarm_delay_time, record_cover);
		}
	}

	@SuppressWarnings("unused")
	/**
	 * PlayActivity feedback method
	 * 
	 * PPPP
	 * @param did
	 * @param msgType
	 * @param param
	 */
	private void MessageNotify(String did, int msgType, int param) {
		Log.d("test_four_2", "MessageNotify did: " + did + " msgType: "
				+ msgType + " param: " + param);
		if (playInterface != null) {
			playInterface.callBackMessageNotify(did, msgType, param);
		}

	}

	/**
	 * PlayActivity feedback method
	 * 
	 * AudioData
	 * 
	 * @param pcm
	 * @param len
	 */
	@SuppressWarnings("unused")
	private void AudioData(byte[] pcm, int len) {
		Log.d(TAG, "AudioData: len :+ " + len);
		if (playInterface != null) {
			playInterface.callBackAudioData(pcm, len);
		}
	}

	/**
	 * IpcamClientActivity feedback method
	 * 
	 * p2p statu
	 * 
	 * @param msgtype
	 * @param param
	 */
	@SuppressWarnings("unused")
	private void PPPPMsgNotify(String did, int type, int param) {
		Log.d(TAG, "PPPPMsgNotify  did:" + did + " type:" + type + " param:"
				+ param);
		if (playInterface != null) {
			playInterface.callBackMessageNotify(did, type, param);
		}
		if (ipPlayInterface != null) {
			ipPlayInterface.callBackMessageNotify(did, type, param);
		}
		if (ipcamClientInterface != null) {
			ipcamClientInterface.BSMsgNotifyData(did, type, param);
		}
		if (wifiInterface != null) {
			wifiInterface.callBackPPPPMsgNotifyData(did, type, param);
		}

		if (userInterface != null) {
			userInterface.callBackPPPPMsgNotifyData(did, type, param);

		}
		if (doorBellOneKey != null) {
			doorBellOneKey.BSMsgNotifyData(did, type, param);
		}
	}

	public void SearchResult(int cameraType, String strMac, String strName,
			String strDeviceID, String strIpAddr, int port) {
		Log.d(TAG, "SearchResult: " + strIpAddr + " " + port);
		if (strDeviceID.length() == 0) {
			return;
		}
		if (addCameraInterface != null) {
			addCameraInterface.callBackSearchResultData(cameraType, strMac,
					strName, strDeviceID, strIpAddr, port);
		}

	}

	// ======================callback==================================================

	public void CallBack_SetSystemParamsResult(String did, int paramType,
			int result) {
		switch (paramType) {
		case ContentCommon.MSG_TYPE_DOOR_BELL_SETUP_PARM:
			if (doorBellAlerm != null) {
				doorBellAlerm.callBackSetSystemParamsResult(did, paramType,
						result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_WIFI:
			if (wifiInterface != null) {
				wifiInterface.callBackSetSystemParamsResult(did, paramType,
						result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_USER:
			if (userInterface != null) {
				userInterface.callBackSetSystemParamsResult(did, paramType,
						result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_ALARM:
			if (alarmInterface != null) {
				// Log.d(TAG,"user result:"+result+" paramType:"+paramType);
				alarmInterface.callBackSetSystemParamsResult(did, paramType,
						result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_MAIL:
			if (mailInterface != null) {
				mailInterface.callBackSetSystemParamsResult(did, paramType,
						result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_FTP:
			if (ftpInterface != null) {
				ftpInterface.callBackSetSystemParamsResult(did, paramType,
						result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_DATETIME:
			if (dateTimeInterface != null) {
				Log.d(TAG, "user result:" + result + " paramType:" + paramType);
				dateTimeInterface.callBackSetSystemParamsResult(did, paramType,
						result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_RECORD_SCH:
			if (sCardInterface != null) {
				sCardInterface.callBackSetSystemParamsResult(did, paramType,
						result);
			}
			break;
		default:
			break;
		}
	}

	public void CallBack_CameraParams(String did, int resolution,
			int brightness, int contrast, int hue, int saturation, int flip,
			int fram, int mode) {
		Log.d("ddd", "CallBack_CameraParams fram==" + fram);
		if (playInterface != null) {
			playInterface.callBackCameraParamNotify(did, resolution,
					brightness, contrast, hue, saturation, flip, fram);
		}
	}

	public void CallBack_WifiParams(String did, int enable, String ssid,
			int channel, int mode, int authtype, int encryp, int keyformat,
			int defkey, String key1, String key2, String key3, String key4,
			int key1_bits, int key2_bits, int key3_bits, int key4_bits,
			String wpa_psk, int statu) {
		Log.d("ddd", "CallBack_WifiParams");
		if (wifiInterface != null) {
			wifiInterface.callBackWifiParams(did, enable, ssid, channel, mode,
					authtype, encryp, keyformat, defkey, key1, key2, key3,
					key4, key1_bits, key2_bits, key3_bits, key4_bits, wpa_psk);
		}
	}

	public void CallBack_UserParams(String did, String user1, String pwd1,
			String user2, String pwd2, String user3, String pwd3) {
		Log.d("ddd", "CallBack_UserParams");
		if (userInterface != null) {
			userInterface.callBackUserParams(did, user1, pwd1, user2, pwd2,
					user3, pwd3);
		}
		if (ipcamClientInterface != null) {
			ipcamClientInterface.callBackUserParams(did, user1, pwd1, user2,
					pwd2, user3, pwd3);
		}
	}

	public void CallBack_FtpParams(String did, String svr_ftp, String user,
			String pwd, String dir, int port, int mode, int upload_interval) {
		if (ftpInterface != null) {
			ftpInterface.callBackFtpParams(did, svr_ftp, user, pwd, dir, port,
					mode, upload_interval);
		}
	}

	public void CallBack_DDNSParams(String did, int service, String user,
			String pwd, String host, String proxy_svr, int ddns_mode,
			int proxy_port) {
		Log.d("ddd", "CallBack_DDNSParams");
	}

	public void CallBack_MailParams(String did, String svr, int port,
			String user, String pwd, int ssl, String sender, String receiver1,
			String receiver2, String receiver3, String receiver4) {
		if (mailInterface != null) {
			mailInterface.callBackMailParams(did, svr, port, user, pwd, ssl,
					sender, receiver1, receiver2, receiver3, receiver4);
		}
	}

	public void CallBack_DatetimeParams(String did, int now, int tz,
			int ntp_enable, String ntp_svr) {
		if (dateTimeInterface != null) {
			dateTimeInterface.callBackDatetimeParams(did, now, tz, ntp_enable,
					ntp_svr);
		}
	}

	/**
	 * IpcamClientActivity feedback method
	 * 
	 * snapshot result
	 * 
	 * @param did
	 * @param bImage
	 * @param len
	 */
	@SuppressWarnings("unused")
	private void PPPPSnapshotNotify(String did, byte[] bImage, int len) {
		Log.d(TAG, "did:" + did + " len:" + len);
		if (ipcamClientInterface != null) {
			ipcamClientInterface.BSSnapshotNotify(did, bImage, len);
		}
	}

	public void CallBack_Snapshot(String did, byte[] data, int len) {
		Log.d("ddd", "CallBack_Snapshot");
		if (ipcamClientInterface != null) {
			ipcamClientInterface.BSSnapshotNotify(did, data, len);
		}
		if (snapShotInterface != null) {
			snapShotInterface.BSSnapshot(did, data, len);
		}
	}

	public void CallBack_APParams(String did, String ssid, String pwd) {
		Log.d("shix", "CallBack_APParams---" + "ssid=" + ssid + "pwd=" + pwd);
	}

	public void CallBack_NetworkParams(String did, String ipaddr,
			String netmask, String gateway, String dns1, String dns2, int dhcp,
			int port, int rtsport) {
		Log.d("ddd", "CallBack_NetworkParams");
	}

	public void CallBack_CameraStatusParams(String did, String sysver,
			String devname, String devid, int alarmstatus, int sdcardstatus,
			int sdcardtotalsize, int sdcardremainsize) {
		Log.d("ddd", "CallBack_CameraStatusParams");
	}

	public void CallBack_PTZParams(String did, int led_mod,
			int ptz_center_onstart, int ptz_run_times, int ptz_patrol_rate,
			int ptz_patrul_up_rate, int ptz_patrol_down_rate,
			int ptz_patrol_left_rate, int ptz_patrol_right_rate,
			int disable_preset) {
		Log.d("ddd", "CallBack_PTZParams");
	}

	public void CallBack_WifiScanResult(String did, String ssid, String mac,
			int security, int dbm0, int dbm1, int mode, int channel, int bEnd) {
		Log.d("tag", "CallBack_WifiScanResult");
		if (wifiInterface != null) {
			wifiInterface.callBackWifiScanResult(did, ssid, mac, security,
					dbm0, dbm1, mode, channel, bEnd);
		}
	}

	public void CallBack_AlarmParams(String did, int motion_armed,
			int motion_sensitivity, int input_armed, int ioin_level,
			int iolinkage, int ioout_level, int alarmpresetsit, int mail,
			int snapshot, int record, int upload_interval, int schedule_enable,
			int schedule_sun_0, int schedule_sun_1, int schedule_sun_2,
			int schedule_mon_0, int schedule_mon_1, int schedule_mon_2,
			int schedule_tue_0, int schedule_tue_1, int schedule_tue_2,
			int schedule_wed_0, int schedule_wed_1, int schedule_wed_2,
			int schedule_thu_0, int schedule_thu_1, int schedule_thu_2,
			int schedule_fri_0, int schedule_fri_1, int schedule_fri_2,
			int schedule_sat_0, int schedule_sat_1, int schedule_sat_2) {
		if (alarmInterface != null) {
			alarmInterface.callBackAlarmParams(did, motion_armed,
					motion_sensitivity, input_armed, ioin_level, iolinkage,
					ioout_level, alarmpresetsit, mail, snapshot, record,
					upload_interval, schedule_enable, schedule_sun_0,
					schedule_sun_1, schedule_sun_2, schedule_mon_0,
					schedule_mon_1, schedule_mon_2, schedule_tue_0,
					schedule_tue_1, schedule_tue_2, schedule_wed_0,
					schedule_wed_1, schedule_wed_2, schedule_thu_0,
					schedule_thu_1, schedule_thu_2, schedule_fri_0,
					schedule_fri_1, schedule_fri_2, schedule_sat_0,
					schedule_sat_1, schedule_sat_2);
		}
	}

	public void CallBack_GetDoorUserParm(String did, String user1, String pwd1,
			String user2, String pwd2, String user3, String pwd3, String user4,
			String pwd4, String user5, String pwd5, String user6, String pwd6,
			String user7, String pwd7, String user8, String pwd8, String alias,
			int admin, int s1, int s2, int s3, int s4, int s5, int s6, int s7,
			int s8) {
		Log.d("test", "CallBack_GetDoorUserParm did:" + did + " user1:" + user1
				+ "  pwd1:" + pwd1 + "   s1:" + s1 + "   alias:" + alias);
		if (doorBellUserInterface != null) {
			doorBellUserInterface.CallBackUserParms(did, user1, pwd1, user2,
					pwd2, user3, pwd3, user4, pwd4, user5, pwd5, user6, pwd6,
					user7, pwd7, user8, pwd8, alias, admin, s1, s2, s3, s4, s5,
					s6, s7, s8);
		}
	}
	public void CallBack_DoorBell_Logs(String uuid, String user, int status,
			int bEnd) {
		// JNI DETECTED ERROR IN APPLICATION: native code passing in reference to invalid stack indirect reference table or invalid reference: 0xbed5e1a0

	}
	private String pushID;
	private String alermType;
	private String dateTime;
	private String Name;

	public void CallBack_AlarmNotifyDoorBell(String did, String dbTime,
			String dbDid, String dbType) {
		Log.e("test", "doorbell:db_did:" + dbDid + "  db_type:" + dbType
				+ "  db_time:" + dbTime);
		// 2014-06-30-Mon-03-34-35
		// CallBack_AlarmNotifyDoorBell dateTime:2014-06-30 3:2:9
		if (dbDid == null || dbDid.length() < 3) {
			return;
		}
		pushID = dbDid.replace("-", "").toUpperCase();
		alermType = dbType;
		if (dbTime != null && dbTime.length() >= 21) {
			dateTime = dbTime.substring(0, 10) + "  "
					+ dbTime.substring(15, 17) + ":" + dbTime.substring(18, 20)
					+ ":" + dbTime.substring(21);
			Log.d("test", "CallBack_AlarmNotifyDoorBell dateTime:" + dateTime);
		} else {
			dateTime = "δ��ȡ��ʱ��";
		}

		if (alermType != null) {
			if (alermType.equalsIgnoreCase("1")
					|| alermType.equalsIgnoreCase("2")) {

				if (SystemValue.isStartRun && doorBellAlermPush != null) {
					doorBellAlermPush.CallBackDoorbellType(dbTime,
							Integer.parseInt(alermType));
				}
				if (!SystemValue.isStartRun && doorBellAlermPush == null) {
					SystemValue.isStartRun = true;
					intentToStart(alermType, pushID, true, dateTime, dbTime);
				} else {
					if (alermType.equalsIgnoreCase("1")) {
						getNotification1(
								this.getResources().getString(
										R.string.doorbell_fangke), pushID, true);
					} else {
						getNotification1(
								this.getResources().getString(
										R.string.doorbell_alerm), pushID, true);
					}
				}
			} else if (alermType.equalsIgnoreCase("3")
					|| alermType.equalsIgnoreCase("6")
					|| alermType.equalsIgnoreCase("7")
					|| alermType.equalsIgnoreCase("5")
					|| alermType.equalsIgnoreCase("4")) {
				if (doorBellAlermPush != null) {
					doorBellAlermPush.CallBackAlermType(Integer
							.parseInt(alermType));
				}
				if (playInterface != null) {
					playInterface.CallBackAlermType(pushID,
							Integer.parseInt(alermType));
				}
			}
		}

	}

	private Notification getNotification1(String content, String did,
			boolean isAlarm) {
		Log.d("tag", "alarm did:" + did);
		int notyTag = (int) (Math.random() * 900) + 100;
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = f.format(date);
		String titlePrompt = "";
		String title = "";
		PendingIntent pendingIntent = null;
		Intent intent = null;
		String user = retrunUser(did);
		String pwd = retrunUserPWD(did);
		String CamName = retrunName(did);
		intent = new Intent(this, MainActivity.class);
		intent.putExtra(ContentCommon.STR_CAMERA_NAME, CamName);
		intent.putExtra(ContentCommon.STR_CAMERA_ID, did);
		intent.putExtra(ContentCommon.STR_CAMERA_USER, user);
		intent.putExtra(ContentCommon.STR_CAMERA_PWD, pwd);
		SystemValue.doorBellAdmin = user;
		SystemValue.doorBellPass = pwd;
		title = CamName;
		titlePrompt = CamName + " " + content;
		mNotify2 = new Notification(R.drawable.homecoo_mini_logo, titlePrompt,
				System.currentTimeMillis());
		mNotify2.flags = Notification.FLAG_ONGOING_EVENT;
		pendingIntent = PendingIntent.getActivity(this, R.drawable.homecoo_mini_logo, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		mNotify2.contentIntent = pendingIntent;
		mNotify2.contentView = views;
		mNotify2.contentView.setTextViewText(R.id.no_title, title);
		mNotify2.contentView.setTextViewText(R.id.no_content, content);
		mNotify2.contentView.setTextViewText(R.id.no_time, strDate);
		mNotify2.contentView.setImageViewResource(R.id.no_img, R.drawable.homecoo_mini_logo);

		if (isAlarm) {
			mNotify2.defaults = Notification.DEFAULT_ALL;
			mCustomMgr.notify(notyTag, mNotify2);
		}
		return mNotify2;
	}

	private int pushType;
	private int i = 0;
	private String Udid;

	private void intentToStart(String content, String did, boolean isAlarm,
			String dateTime, String uuid) {

		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (SystemValue.arrayList.get(i).getDid().equals(did)) {
				Name = SystemValue.arrayList.get(i).getName();
			}
		}
		if (content.equals("1")) {
			content = this.getResources().getString(R.string.doorbell_fangke);
			pushType = 1;
		} else {
			content = this.getResources().getString(R.string.doorbell_alerm);
			pushType = 2;
		}

		Intent intent = null;
		intent = new Intent(this, ListeningActivity.class);
		intent.putExtra(ContentCommon.STR_CAMERA_ID, did);
		intent.putExtra(ContentCommon.STR_CAMERA_NAME, Name);
		intent.putExtra("pushType", content);
		intent.putExtra("pushIsAlerm", pushType);
		intent.putExtra("pushPic", uuid);
		intent.putExtra("pushDate", dateTime);
		intent.putExtra(ContentCommon.STR_CAMERA_USER, retrunUser(did));
		intent.putExtra(ContentCommon.STR_CAMERA_PWD, retrunUserPWD(did));
		intent.putExtra(ContentCommon.STR_CAMERA_TYPE, 0);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}

	private String retrunUser(String did) {

		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (did != null
					&& did.equals(SystemValue.arrayList.get(i).getDid())) {
				Log.d("tag", "testuser:"
						+ SystemValue.arrayList.get(i).getUser());
				return SystemValue.arrayList.get(i).getUser();
			}
		}
		return null;
	}

	private String retrunUserPWD(String did) {

		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (did != null
					&& did.equals(SystemValue.arrayList.get(i).getDid())) {
				Log.d("tag", "testuserpwd:"
						+ SystemValue.arrayList.get(i).getPwd());
				return SystemValue.arrayList.get(i).getPwd();
			}
		}
		return null;
	}

	public void CallBack_AlarmNotify(String did, int alarmtype) {
		Log.d("tag", "callBack_AlarmNotify did:" + did + " alarmtype:"
				+ alarmtype);
		// switch (alarmtype) {
		// case ContentCommon.MOTION_ALARM:
		// String strMotionAlarm = getResources().getString(
		// R.string.alerm_motion_alarm);
		// getNotification(strMotionAlarm, did, true);
		// break;
		// case ContentCommon.GPIO_ALARM:
		// String strGpioAlarm = getResources().getString(
		// R.string.alerm_gpio_alarm);
		// getNotification(strGpioAlarm, did, true);
		// break;
		// default:
		// break;
		// }

	}

	private void CallBack_RecordFileSearchResult(String did, String filename,
			int nFileSize, int nRecordCount, int nPageCount, int nPageIndex,
			int nPageSize, int bEnd) {
		Log.d(TAG, "CallBack_RecordFileSearchResult did: " + did
				+ " filename: " + filename + " size: " + nFileSize);
		if (playBackTFInterface != null) {
			playBackTFInterface.callBackRecordFileSearchResult(did, filename,
					nFileSize, nPageCount, bEnd);
		}
	}

	private void CallBack_PlaybackVideoData(String did, byte[] videobuf,
			int h264Data, int len, int width, int height, int time) {
		Log.d(TAG, "CallBack_PlaybackVideoData  len:" + len + " width:" + width
				+ " height:" + height + "  time:" + time);
		if (playBackInterface != null) {
			playBackInterface.callBackPlaybackVideoData(videobuf, h264Data,
					len, width, height, time);
		}
	}

	private void CallBack_H264Data(String did, byte[] h264, int type, int size) {
		// Log.e("tag", "did=" + did + "  h264=" + h264.length);
		if (playInterface != null) {
			playInterface.callBackH264Data(did, h264, type, size);
		}
	}

	public void CallBack_RecordSchParams(String did, int record_cover_enable,
			int record_timer, int record_size, int record_time_enable,
			int record_schedule_sun_0, int record_schedule_sun_1,
			int record_schedule_sun_2, int record_schedule_mon_0,
			int record_schedule_mon_1, int record_schedule_mon_2,
			int record_schedule_tue_0, int record_schedule_tue_1,
			int record_schedule_tue_2, int record_schedule_wed_0,
			int record_schedule_wed_1, int record_schedule_wed_2,
			int record_schedule_thu_0, int record_schedule_thu_1,
			int record_schedule_thu_2, int record_schedule_fri_0,
			int record_schedule_fri_1, int record_schedule_fri_2,
			int record_schedule_sat_0, int record_schedule_sat_1,
			int record_schedule_sat_2, int record_sd_status, int sdtotal,
			int sdfree) {
		Log.d("shix_sd", "shix_sd:���� record_cover_enable:"
				+ record_cover_enable);
		if (sCardInterface != null) {
			sCardInterface.callBackRecordSchParams(did, record_cover_enable,
					record_timer, record_size, record_time_enable,
					record_schedule_sun_0, record_schedule_sun_1,
					record_schedule_sun_2, record_schedule_mon_0,
					record_schedule_mon_1, record_schedule_mon_2,
					record_schedule_tue_0, record_schedule_tue_1,
					record_schedule_tue_2, record_schedule_wed_0,
					record_schedule_wed_1, record_schedule_wed_2,
					record_schedule_thu_0, record_schedule_thu_1,
					record_schedule_thu_2, record_schedule_fri_0,
					record_schedule_fri_1, record_schedule_fri_2,
					record_schedule_sat_0, record_schedule_sat_1,
					record_schedule_sat_2, record_sd_status, sdtotal, sdfree);
		}
	}

	public void CallBack_GetLockParm(String did, int lock_type, int lock_delay) {
		if (doorBellLockParmInterface != null) {
			doorBellLockParmInterface.callBackDoorBellLockParm(did, lock_type,
					lock_delay);
		}
	}

	public void CallBack_GetAlarmParm(String did, int alarm_on, int alarm_type,
			int alarm_level, int alarm_delay, int alarm_start_hour,
			int alarm_stop_hour, int alarm_start_minute, int alarm_stop_minute) {
		Log.e("test", "did:" + did + "  alarm_on:" + alarm_on + "  alarm_type:"
				+ alarm_type + "  alarm_level:" + alarm_level
				+ "  alarm_delay:" + alarm_delay + "  alarm_start_hour:"
				+ alarm_start_hour + "  alarm_stop_hour:" + alarm_stop_hour
				+ "  alarm_start_minute:" + alarm_start_minute
				+ "  alarm_stop_minute:" + alarm_stop_minute);
		if (doorBellAlarmParmInterface != null) {
			doorBellAlarmParmInterface.callBackDoorBellAlarmParm(did, alarm_on,
					alarm_type, alarm_level, alarm_delay, alarm_start_hour,
					alarm_stop_hour, alarm_start_minute, alarm_stop_minute);
		}
	}

	// int pin = 0;
	// int pin_bind = 0;
	// int pout = 0;
	// int pout_bind = 0;
	public void CallBack_GetInterfaceParm(String did, int pin, int pin_bind,
			int pout, int pout_bind) {
		Log.e("test", "did:" + did + "  pin:" + pin + "  pin_bind:" + pin_bind
				+ "  pout:" + pout + "  pout_bind:" + pout_bind);
		if (doorBellInterfaceParmInterface != null) {
			doorBellInterfaceParmInterface.callBackDoorBellInterfaceParm(did,
					pin, pin_bind, pout, pout_bind);
		}
	}

	// int bell_on = 0;
	// int bell_audio = 0;
	// int bell_mode = 0;
	// int max_watch = 0;
	// int max_talk = 0;
	// int max_wait = 0;
	public void CallBack_GetDoorSystemParm(String did, int bell_on,
			int bell_audio, int bell_mode, int max_watch, int max_talk,
			int max_wait) {
		Log.e("test", "did:" + did + "  bell_on:" + bell_on + "  bell_audio:"
				+ bell_audio + "  bell_mode:" + bell_mode + "  max_watch:"
				+ max_watch + "  max_talk:" + max_talk + "  max_wait:"
				+ max_wait);
		if (doorBellSystemParmInterface != null) {
			doorBellSystemParmInterface.callBackDoorBellSystemParm(did,
					bell_on, bell_audio, bell_mode, max_watch, max_talk,
					max_wait);
			Log.e("test", "2did:" + did + "  bell_on:" + bell_on
					+ "  bell_audio:" + bell_audio + "  bell_mode:" + bell_mode
					+ "  max_watch:" + max_watch + "  max_talk:" + max_talk
					+ "  max_wait:" + max_wait);
		}
	}

	private static DoorBellSystemParmInterface doorBellSystemParmInterface;

	public static void setDoorBellSystemParmInterface(
			DoorBellSystemParmInterface sdoorBellInterface) {
		doorBellSystemParmInterface = sdoorBellInterface;
	}

	public interface DoorBellSystemParmInterface {
		void callBackDoorBellSystemParm(String did, int bell_on,
				int bell_audio, int bell_mode, int max_watch, int max_talk,
				int max_wait);

	}

	private static DoorBellInterfaceParmInterface doorBellInterfaceParmInterface;

	public static void setDoorBellInterfaceParmInterface(
			DoorBellInterfaceParmInterface sdoorBellInterface) {
		doorBellInterfaceParmInterface = sdoorBellInterface;
	}

	public interface DoorBellInterfaceParmInterface {
		void callBackDoorBellInterfaceParm(String did, int pin, int pin_bind,
				int pout, int pout_bind);

	}

	private static DoorBellAlarmParmInterface doorBellAlarmParmInterface;

	public static void setDoorBellAlarmParmInterface(
			DoorBellAlarmParmInterface sdoorBellInterface) {
		doorBellAlarmParmInterface = sdoorBellInterface;
	}

	public interface DoorBellAlarmParmInterface {
		void callBackDoorBellAlarmParm(String did, int alarm_on,
				int alarm_type, int alarm_level, int alarm_delay,
				int alarm_start_hour, int alarm_stop_hour,
				int alarm_start_minute, int alarm_stop_minute);

	}

	private static DoorBellLockParmInterface doorBellLockParmInterface;

	public static void setDoorBellLockParmInterface(
			DoorBellLockParmInterface sdoorBellInterface) {
		doorBellLockParmInterface = sdoorBellInterface;
	}

	public interface DoorBellLockParmInterface {
		void callBackDoorBellLockParm(String did, int lock_type, int lock_delay);

	}

	private static IpcamClientInterface ipcamClientInterface;

	public static void setIpcamClientInterface(IpcamClientInterface ipcInterface) {
		ipcamClientInterface = ipcInterface;
	}

	public interface IpcamClientInterface {
		void BSMsgNotifyData(String did, int type, int param);

		void BSSnapshotNotify(String did, byte[] bImage, int len);

		void callBackDoorBellController(String did, int index1);

		void callBackUserParams(String did, String user1, String pwd1,
				String user2, String pwd2, String user3, String pwd3);
	}

	private static PictureInterface pictureInterface;

	public static void setPictureInterface(PictureInterface pi) {
		pictureInterface = pi;
	}

	public interface PictureInterface {
		void BSMsgNotifyData(String did, int type, int param);
	}

	private static VideoInterface videoInterface;

	public static void setVideoInterface(VideoInterface vi) {
		videoInterface = vi;
	}

	public interface VideoInterface {
		void BSMsgNotifyData(String did, int type, int param);
	}

	private static WifiInterface wifiInterface;

	public static void setWifiInterface(WifiInterface wi) {
		wifiInterface = wi;
	}

	public interface WifiInterface {
		void callBackWifiParams(String did, int enable, String ssid,
				int channel, int mode, int authtype, int encryp, int keyformat,
				int defkey, String key1, String key2, String key3, String key4,
				int key1_bits, int key2_bits, int key3_bits, int key4_bits,
				String wpa_psk);

		void callBackWifiScanResult(String did, String ssid, String mac,
				int security, int dbm0, int dbm1, int mode, int channel,
				int bEnd);

		void callBackSetSystemParamsResult(String did, int paramType, int result);

		void callBackPPPPMsgNotifyData(String did, int type, int param);
	}

	private static UserInterface userInterface;

	public static void setUserInterface(UserInterface ui) {
		userInterface = ui;
	}

	public interface UserInterface {
		void callBackUserParams(String did, String user1, String pwd1,
				String user2, String pwd2, String user3, String pwd3);

		void callBackSetSystemParamsResult(String did, int paramType, int result);

		void callBackPPPPMsgNotifyData(String did, int type, int param);
	}

	private static AlarmInterface alarmInterface;

	public static void setAlarmInterface(AlarmInterface ai) {
		alarmInterface = ai;
	}

	public interface AlarmInterface {
		void callBackAlarmParams(String did, int motion_armed,
				int motion_sensitivity, int input_armed, int ioin_level,
				int iolinkage, int ioout_level, int alermpresetsit, int mail,
				int snapshot, int record, int upload_interval,
				int schedule_enable, int schedule_sun_0, int schedule_sun_1,
				int schedule_sun_2, int schedule_mon_0, int schedule_mon_1,
				int schedule_mon_2, int schedule_tue_0, int schedule_tue_1,
				int schedule_tue_2, int schedule_wed_0, int schedule_wed_1,
				int schedule_wed_2, int schedule_thu_0, int schedule_thu_1,
				int schedule_thu_2, int schedule_fri_0, int schedule_fri_1,
				int schedule_fri_2, int schedule_sat_0, int schedule_sat_1,
				int schedule_sat_2);

		void callBackSetSystemParamsResult(String did, int paramType, int result);
	}

	private static DateTimeInterface dateTimeInterface;

	public static void setDateTimeInterface(DateTimeInterface di) {
		dateTimeInterface = di;
	}

	public interface DateTimeInterface {
		void callBackDatetimeParams(String did, int now, int tz,
				int ntp_enable, String ntp_svr);

		void callBackSetSystemParamsResult(String did, int paramType, int result);
	}

	private static MailInterface mailInterface;

	public static void setMailInterface(MailInterface mi) {
		mailInterface = mi;
	}

	public interface MailInterface {
		void callBackMailParams(String did, String svr, int port, String user,
				String pwd, int ssl, String sender, String receiver1,
				String receiver2, String receiver3, String receiver4);

		void callBackSetSystemParamsResult(String did, int paramType, int result);
	}

	private static FtpInterface ftpInterface;

	public static void setFtpInterface(FtpInterface fi) {
		ftpInterface = fi;
	}

	public interface FtpInterface {
		void callBackFtpParams(String did, String svr_ftp, String user,
				String pwd, String dir, int port, int mode, int upload_interval);

		void callBackSetSystemParamsResult(String did, int paramType, int result);
	}

	private static SDCardInterface sCardInterface;

	public static void setSDCardInterface(SDCardInterface si) {
		sCardInterface = si;
	}

	public interface SDCardInterface {
		void callBackRecordSchParams(String did, int record_cover_enable,
				int record_timer, int record_size, int record_time_enable,
				int record_schedule_sun_0, int record_schedule_sun_1,
				int record_schedule_sun_2, int record_schedule_mon_0,
				int record_schedule_mon_1, int record_schedule_mon_2,
				int record_schedule_tue_0, int record_schedule_tue_1,
				int record_schedule_tue_2, int record_schedule_wed_0,
				int record_schedule_wed_1, int record_schedule_wed_2,
				int record_schedule_thu_0, int record_schedule_thu_1,
				int record_schedule_thu_2, int record_schedule_fri_0,
				int record_schedule_fri_1, int record_schedule_fri_2,
				int record_schedule_sat_0, int record_schedule_sat_1,
				int record_schedule_sat_2, int record_sd_status, int sdtotal,
				int sdfree);

		void callBackSetSystemParamsResult(String did, int paramType, int result);;
	}

	private static PlayInterface playInterface;

	public static void setPlayInterface(PlayInterface pi) {
		playInterface = pi;
	}

	public interface PlayInterface {
		void callBackCameraParamNotify(String did, int resolution,
				int brightness, int contrast, int hue, int saturation,
				int flip, int fram);

		void callBaceVideoData(String did, byte[] videobuf, int h264Data,
				int len, int width, int height, int time);

		void callBackMessageNotify(String did, int msgType, int param);

		void callBackAudioData(byte[] pcm, int len);

		void callBackH264Data(String did, byte[] h264, int type, int size);

		void CallBackAlermType(String did, int type);
	}

	private static PlayBackTFInterface playBackTFInterface;

	public static void setPlayBackTFInterface(PlayBackTFInterface pbtfi) {
		playBackTFInterface = pbtfi;
	}

	public interface PlayBackTFInterface {
		void callBackRecordFileSearchResult(String did, String filename,
				int size, int nPageCount, int bEnd);
	}

	private static PlayBackInterface playBackInterface;

	public static void setPlayBackInterface(PlayBackInterface pbi) {
		playBackInterface = pbi;
	}

	public interface PlayBackInterface {
		void callBackPlaybackVideoData(byte[] videobuf, int h264Data, int len,
				int width, int height, int time);
	}

	private static AddCameraInterface addCameraInterface;

	public static void setAddCameraInterface(AddCameraInterface aci) {
		addCameraInterface = aci;
	}

	public interface AddCameraInterface {
		void callBackSearchResultData(int cameraType, String strMac,
				String strName, String strDeviceID, String strIpAddr, int port);
	}

	private static IPPlayInterface ipPlayInterface;

	public static void setIpPlayInterface(IPPlayInterface pi) {
		ipPlayInterface = pi;
	}

	public interface IPPlayInterface {
		void callBackCameraParamNotify(String did, int resolution,
				int brightness, int contrast, int hue, int saturation, int flip);

		void callBaceVideoData(String did, byte[] videobuf, int h264Data,
				int len, int width, int height);

		void callBackMessageNotify(String did, int msgType, int param);

		void callBackAudioData(byte[] pcm, int len);

		void callBackH264Data(byte[] h264, int type, int size);
	}

	private static SnapShotInterface snapShotInterface;

	public static void setSnapShotInterface(SnapShotInterface snape) {
		snapShotInterface = snape;
	}

	public interface SnapShotInterface {
		void BSSnapshot(String did, byte[] bImage, int len);
	}

	private static DoorBellAlerm doorBellAlerm;

	public static void setDoorBellAlerm(DoorBellAlerm door) {
		doorBellAlerm = door;
	}

	public interface DoorBellAlerm {
		void DoorBeelAlerm(String did, int result, int motion_enable,
				int motion_level, int pir_enable, int record_enable,
				int record_size, int on_delay_time, int alarm_delay_time,
				int record_cover);

		void callBackSetSystemParamsResult(String did, int paramType, int result);
	}

	private static DoorBellOneKey doorBellOneKey;

	public static void setDoorBellOneKey(DoorBellOneKey door) {
		doorBellOneKey = door;
	}

	public interface DoorBellOneKey {
		void BSMsgNotifyData(String did, int type, int param);
	}

	private static DoorBellTimeDeLay doorBellTimeDeLay;

	public static void setDoorBellTimeDeLay(DoorBellTimeDeLay door) {
		doorBellTimeDeLay = door;
	}

	public interface DoorBellTimeDeLay {
		void CallBackTimeDelay(int time);
	}

	private static DoorBellAlermPush doorBellAlermPush;

	public static void setDoorBellAlermPush(DoorBellAlermPush door) {
		doorBellAlermPush = door;
	}
	
    //�����������ӿ�
	public interface DoorBellAlermPush {
		void CallBackAlermType(int type);

		void CallBackDoorbellType(String uuid, int type);
	}

	private static DoorBellUserInterface doorBellUserInterface;

	public static void setDoorBellUserInterface(DoorBellUserInterface door) {
		doorBellUserInterface = door;
	}

	public interface DoorBellUserInterface {
		void CallBackUserParms(String did, String user1, String pwd1,
				String user2, String pwd2, String user3, String pwd3,
				String user4, String pwd4, String user5, String pwd5,
				String user6, String pwd6, String user7, String pwd7,
				String user8, String pwd8, String alias, int admin, int s1,
				int s2, int s3, int s4, int s5, int s6, int s7, int s8);
	}

	private String retrunName(String did) {

		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (did != null
					&& did.equals(SystemValue.arrayList.get(i).getDid())) {

				return SystemValue.arrayList.get(i).getName();
			}
		}
		return null;
	}

}
