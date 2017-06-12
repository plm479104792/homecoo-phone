package com.p2p.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import com.p2p.core.P2PInterface.IP2P;
import com.p2p.core.P2PInterface.ISetting;
import com.p2p.core.global.AlarmRecord;
import com.p2p.core.global.Constants;
import com.p2p.core.utils.MyUtils;

public class MediaPlayer {
	private static MediaPlayer manager = null;
	private static IP2P p2pInterface = null;
	private static ISetting settingInterface = null;
	private static Context mContext;

	public MediaPlayer(Context context) {
		native_setup(new WeakReference<MediaPlayer>(this));
		MediaPlayer.mContext = context;
		manager = this;
	};

	public static MediaPlayer getInstance() {
		return manager;
	}

	public static boolean isMute = false;
	public static boolean isSendAudio = false;

	private final static String TAG = "2cu";
	private boolean mScreenOnWhilePlaying;
	private static ICapture mCapture = null;

	private static AudioRecord mAudioRecord = null;
	private static int mCpuVersion = 0;
	private int mNativeContext; // accessed by native methods
	private Surface mSurface;

	static {
		System.loadLibrary("SDL");
		mCpuVersion = MyUtils.getCPUVesion();
		System.loadLibrary("mediaplayer");
		native_init(mCpuVersion);
	}

	public void setCaptureListener(ICapture captureLister) {
		mCapture = captureLister;
	}

	public void setP2PInterface(IP2P p2pInterface) {
		MediaPlayer.p2pInterface = p2pInterface;
	}

	public void setSettingInterface(ISetting settingInterface) {
		MediaPlayer.settingInterface = settingInterface;
	}

	public void setIsSendAudio(boolean bool) {
		MediaPlayer.isSendAudio = bool;
	}

	public static int getConvertAckResult(int result) {
		if (result == 0) {
			return Constants.ACK_RET_TYPE.ACK_SUCCESS;
		} else if (result == 1) {
			return Constants.ACK_RET_TYPE.ACK_PWD_ERROR;
		} else if (result == 2) {
			return Constants.ACK_RET_TYPE.ACK_NET_ERROR;
		} else if (result == 4) {
			return Constants.ACK_RET_TYPE.ACK_INSUFFICIENT_PERMISSIONS;
		} else {
			return result;
		}
	}

	private static void postEventFromNative(Object mediaplayer_ref, int what,
			int iDesID, int arg1, int arg2, String msgStr) {
		int reason_code = 0;
		// 截获字串
		if (msgStr.equals("pw_incrrect")) {
			reason_code = 0;
		} else if (msgStr.equals("busy")) {
			reason_code = 1;
		} else if (msgStr.equals("none")) {
			reason_code = 2;
		} else if (msgStr.equals("id_disabled")) {
			reason_code = 3;
		} else if (msgStr.equals("id_overdate")) {
			reason_code = 4;
		} else if (msgStr.equals("id_inactived")) {
			reason_code = 5;
		} else if (msgStr.equals("offline")) {
			reason_code = 6;
		} else if (msgStr.equals("powerdown")) {
			reason_code = 7;
		} else if (msgStr.equals("nohelper")) {
			reason_code = 8;
		} else if (msgStr.equals("hungup")) {
			reason_code = 9;
		} else if (msgStr.equals("timeout")) {
			reason_code = 10;
		} else if (msgStr.equals("nobody")) {
			reason_code = 11;
		} else if (msgStr.equals("internal_error")) {
			reason_code = 12;
		} else if (msgStr.equals("conn_fail")) {
			reason_code = 13;
		} else if (msgStr.equals("not_support")) {
			reason_code = 14;
		}

		switch (what) {
		case 1:
			String threeNumber = "";
			if (arg2 > 0) {
				threeNumber = String.valueOf(arg2);
			} else {
				threeNumber = "0" + String.valueOf((0 - arg2));
			}

			if (arg1 == 1) {
				// 呼入
				p2pInterface.vCalling(false, threeNumber,
						Integer.parseInt(msgStr));
			} else {
				// 呼出
				p2pInterface.vCalling(true, threeNumber,
						Integer.parseInt(msgStr));
			}
			break;
		case 2:
			p2pInterface.vReject(reason_code);
			break;
		case 3:
			p2pInterface.vAccept();
			break;
		case 4:
			p2pInterface.vConnectReady();
			break;
		case 5:
			if (mCapture != null) {
				mCapture.vCaptureResult(arg1);
			}
			break;
		case 6:
			// remote 线程ack回调
			if (arg1 < Constants.MsgSection.MSG_ID_SET_REMOTE_DEFENCE
					&& arg1 >= (Constants.MsgSection.MSG_ID_SET_REMOTE_DEFENCE - 1000)) {
				settingInterface
						.ACK_vRetSetRemoteDefence(String.valueOf(iDesID), arg1,
								getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SET_REMOTE_RECORD
					&& arg1 >= (Constants.MsgSection.MSG_ID_SET_REMOTE_RECORD - 1000)) {
				settingInterface.ACK_vRetSetRemoteRecord(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_DEVICE_TIME
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_DEVICE_TIME - 1000)) {
				settingInterface.ACK_vRetSetDeviceTime(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_GETTING_DEVICE_TIME
					&& arg1 >= (Constants.MsgSection.MSG_ID_GETTING_DEVICE_TIME - 1000)) {
				settingInterface.ACK_vRetGetDeviceTime(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_GETTING_NPC_SETTINGS
					&& arg1 >= (Constants.MsgSection.MSG_ID_GETTING_NPC_SETTINGS - 1000)) {
				settingInterface.ACK_vRetGetNpcSettings(String.valueOf(iDesID),
						arg1, getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT - 1000)) {
				settingInterface.ACK_vRetSetNpcSettingsVideoFormat(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME - 1000)) {
				settingInterface.ACK_vRetSetNpcSettingsVideoVolume(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_BUZZER - 1000)) {
				settingInterface.ACK_vRetSetNpcSettingsBuzzer(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_MOTION - 1000)) {
				settingInterface.ACK_vRetSetNpcSettingsMotion(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE - 1000)) {
				settingInterface.ACK_vRetSetNpcSettingsRecordType(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME - 1000)) {
				settingInterface.ACK_vRetSetNpcSettingsRecordTime(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME - 1000)) {
				settingInterface.ACK_vRetSetNpcSettingsRecordPlanTime(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE - 1000)) {
				settingInterface.ACK_vRetSetNpcSettingsNetType(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_ALARM_EMAIL - 1000)) {
				settingInterface.ACK_vRetSetAlarmEmail(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_GETTING_ALARM_EMAIL
					&& arg1 >= (Constants.MsgSection.MSG_ID_GETTING_ALARM_EMAIL - 1000)) {
				settingInterface.ACK_vRetGetAlarmEmail(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_ALARM_BIND_ID
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_ALARM_BIND_ID - 1000)) {
				settingInterface.ACK_vRetSetAlarmBindId(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_GETTING_ALARM_BIND_ID
					&& arg1 >= (Constants.MsgSection.MSG_ID_GETTING_ALARM_BIND_ID - 1000)) {
				settingInterface.ACK_vRetGetAlarmBindId(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_INIT_PASSWORD
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_INIT_PASSWORD - 1000)) {
				settingInterface.ACK_vRetSetInitPassword(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_DEVICE_PASSWORD - 1000)) {
				settingInterface.ACK_vRetSetDevicePassword(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD
					&& arg1 >= (Constants.MsgSection.MSG_ID_CHECK_DEVICE_PASSWORD - 1000)) {
				settingInterface.ACK_vRetCheckDevicePassword(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_DEFENCEAREA
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_DEFENCEAREA - 1000)) {
				settingInterface.ACK_vRetSetDefenceArea(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_GETTING_DEFENCEAREA
					&& arg1 >= (Constants.MsgSection.MSG_ID_GETTING_DEFENCEAREA - 1000)) {
				settingInterface.ACK_vRetGetDefenceArea(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SETTING_WIFI
					&& arg1 >= (Constants.MsgSection.MSG_ID_SETTING_WIFI - 1000)) {
				settingInterface.ACK_vRetSetWifi(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_GETTING_WIFI_LIST
					&& arg1 >= (Constants.MsgSection.MSG_ID_GETTING_WIFI_LIST - 1000)) {
				settingInterface.ACK_vRetGetWifiList(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST
					&& arg1 >= (Constants.MsgSection.MSG_ID_GETTING_RECORD_FILE_LIST - 1000)) {
				settingInterface.ACK_vRetGetRecordFileList(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SEND_MESSAGE
					&& arg1 >= (Constants.MsgSection.MSG_ID_SEND_MESSAGE - 1000)) {
				settingInterface.ACK_vRetMessage(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_SEND_CUSTOM_CMD
					&& arg1 >= (Constants.MsgSection.MSG_ID_SEND_CUSTOM_CMD - 1000)) {
				settingInterface.ACK_vRetCustomCmd(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_CHECK_DEVICE_UPDATE
					&& arg1 >= (Constants.MsgSection.MSG_ID_CHECK_DEVICE_UPDATE - 1000)) {
				settingInterface.ACK_vRetCheckDeviceUpdate(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE
					&& arg1 >= (Constants.MsgSection.MSG_ID_CANCEL_DEVICE_UPDATE - 1000)) {
				settingInterface.ACK_vRetCancelDeviceUpdate(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_DO_DEVICE_UPDATE
					&& arg1 >= (Constants.MsgSection.MSG_ID_DO_DEVICE_UPDATE - 1000)) {
				settingInterface.ACK_vRetDoDeviceUpdate(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_GET_DEFENCE_STATE
					&& arg1 >= (Constants.MsgSection.MSG_ID_GET_DEFENCE_STATE - 1000)) {
				settingInterface
						.ACK_vRetGetDefenceStates(String.valueOf(iDesID), arg1,
								getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_GET_DEVICE_VERSION
					&& arg1 >= (Constants.MsgSection.MSG_ID_GET_DEVICE_VERSION - 1000)) {
				settingInterface.ACK_vRetGetDeviceVersion(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP
					&& arg1 >= (Constants.MsgSection.MSG_ID_CLEAR_DEFENCE_GROUP - 1000)) {
				settingInterface.ACK_vRetClearDefenceAreaState(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_ID_STTING_PIC_REVERSE
					&& arg1 >= (Constants.MsgSection.MESG_ID_STTING_PIC_REVERSE - 1000)) {
				settingInterface.ACK_vRetSetImageReverse(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_ID_STTING_IR_ALARM_EN
					&& arg1 >= (Constants.MsgSection.MESG_ID_STTING_IR_ALARM_EN - 1000)) {
				settingInterface.ACK_vRetSetInfraredSwitch(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN
					&& arg1 >= (Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_IN_EN - 1000)) {
				settingInterface.ACK_vRetSetWiredAlarmInput(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN
					&& arg1 >= (Constants.MsgSection.MESG_STTING_ID_EXTLINE_ALARM_OUT_EN - 1000)) {
				settingInterface.ACK_vRetSetWiredAlarmOut(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_STTING_ID_SECUPGDEV
					&& arg1 >= (Constants.MsgSection.MESG_STTING_ID_SECUPGDEV - 1000)) {
				settingInterface.ACK_vRetSetAutomaticUpgrade(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_STTING_ID_GUEST_PASSWD
					&& arg1 >= (Constants.MsgSection.MESG_STTING_ID_GUEST_PASSWD - 1000)) {
				settingInterface.ACK_VRetSetVisitorDevicePassword(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_STTING_ID_TIMEZONE
					&& arg1 >= (Constants.MsgSection.MESG_STTING_ID_TIMEZONE - 1000)) {
				settingInterface.ACK_vRetSetTimeZone(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_GET_SD_CARD_CAPACITY
					&& arg1 >= (Constants.MsgSection.MESG_GET_SD_CARD_CAPACITY - 1000)) {
				settingInterface.ACK_vRetGetSDCard(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_SD_CARD_FORMAT
					&& arg1 >= (Constants.MsgSection.MESG_SD_CARD_FORMAT - 1000)) {
				settingInterface.ACK_vRetSdFormat(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_GET_DEFENCE_AREA_SWITCH
					&& arg1 >= (Constants.MsgSection.MESG_GET_DEFENCE_AREA_SWITCH - 1000)) {
				settingInterface.ACK_vRetGetSensorSwitchs(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_SET_DEFENCE_AREA_SWITCH
					&& arg1 >= (Constants.MsgSection.MESG_SET_DEFENCE_AREA_SWITCH - 1000)) {
				settingInterface.ACK_vRetSetSensorSwitchs(arg1,
						getConvertAckResult(arg2));
			} else if (arg1 < Constants.MsgSection.MESG_GET_ALARM_RECORD
					&& arg1 >= (Constants.MsgSection.MESG_GET_ALARM_RECORD - 1000)) {
				settingInterface.ACK_vRetGetAlarmRecord(arg1,
						getConvertAckResult(arg2));
			}

			// //message ack回调
			// if(arg1>=4000&&arg1<6000){
			// if(getConvertAckResult(arg2)==Constants.SettingConfig.ACK_SUCCESS){
			// Log.e("my","msg success");
			// if(MainActivity.msgHeap.get(arg1+"")!=null){
			// Intent i = new Intent();
			// i.setAction(Constants.Action.SUCCESS_MSG);
			// i.putExtra("msgId", arg1);
			// MyApp.app.sendBroadcast(i);
			// }
			// }else
			// if(getConvertAckResult(arg2)==Constants.SettingConfig.ACK_NET_ERROR){
			// Log.e("my","msg fault");
			// if(MainActivity.msgHeap.get(arg1+"")!=null){
			// Intent i = new Intent();
			// i.setAction(Constants.Action.FAULT_MSG);
			// i.putExtra("msgId", arg1);
			// MyApp.app.sendBroadcast(i);
			// }
			// }
			// }
			//
			// //search playback list ack回调
			// if((arg2==2 || arg2==1)&&arg1>=6000&&arg1<8000){
			// Log.e("my","search fault");
			// Intent i = new Intent();
			// i.putExtra("errorCode", getConvertAckResult(arg2));
			// i.setAction(Constants.Action.PLAYBACK_SEARCH_FAULT);
			// MyApp.app.sendBroadcast(i);
			// }
			//

			break;
		case 7:

			break;
		case 8:
			p2pInterface.vChangeVideoMask(arg1);
			break;
		}

	}

	static int iAudioDataInputNs = 0;
	static long AudioTrackPTSBegin = 0;
	static boolean fgdoPlayInit = true;
	static boolean fgdoRecordInit = true;

	public static void openAudioTrack() {
		try {
			int maxjitter = AudioTrack.getMinBufferSize(8000,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
			if (Build.MODEL.equals("HTC One X")) {
				mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
						8000, AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT, maxjitter * 2,
						AudioTrack.MODE_STREAM);
			} else {
				mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
						AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT, maxjitter * 2,
						AudioTrack.MODE_STREAM);
			}

			Log.i(TAG, "Audio Track min buffer size:" + maxjitter); // 870
																	// frames--
																	// 108.75mS
			iAudioDataInputNs = 0;
			AudioTrackPTSBegin = System.currentTimeMillis();
			mAudioTrack.play();
			fgdoPlayInit = true;
		} catch (Exception e) {
			Log.e("test", "error");
		}
	}

	public static void openAudioRecord() {
		try {

			int samp_rate = 8000;
			int min = AudioRecord
					.getMinBufferSize(samp_rate, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);

			mAudioRecord = new AudioRecord(
					MediaRecorder.AudioSource.CAMCORDER,// the recording source
					samp_rate, // 采样频率，一般为8000hz/s
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, min);

			Log.i(TAG, "Audio Record min buffer size:" + (min)); // 4096 frames
																	// --
																	// 256mS*2
			mAudioRecord.startRecording();
			fgdoRecordInit = true;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("343", Log.getStackTraceString(e));
		}
	}

	private static int setAudioBuffer(byte[] buffer, int buffer_size, int[] iPTS) {

		int readNum = 0;
		if (mAudioRecord == null) {
			readNum = 0;
		} else {
			// Set priority, only do once
			if (fgdoRecordInit == true) {
				try {
					android.os.Process
							.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				} catch (Exception e) {
					// DoLog("Set play thread priority failed: " +
				}
				fgdoRecordInit = false;
			}
			readNum = mAudioRecord.read(buffer, 0, buffer_size);
			// Log.e("my",readNum+"");
			iPTS[0] = (int) (System.currentTimeMillis() - AudioTrackPTSBegin - (readNum / 16));
			// iAudioRecordPTS += readNum/16 ; ///time mS
		}
		return readNum;
	}

	private static void getAudioBuffer(byte[] buffer, int buffer_size,
			int[] iPTS) {

		int i;
		int iTime1;

		i = mAudioTrack.getPlaybackHeadPosition();
		iTime1 = (int) (System.currentTimeMillis() - AudioTrackPTSBegin);

		iPTS[0] = iTime1 + (iAudioDataInputNs - i) / 8; // /PTS mS

		if (mAudioTrack != null) {
			if (fgdoPlayInit == true) {
				try {
					android.os.Process
							.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				} catch (Exception e) {

				}
				fgdoPlayInit = false;
			}
			int result = mAudioTrack.write(buffer, 0, 320);

			iAudioDataInputNs += (buffer_size / 2);
		}

	}

	private static void RecvAVData(byte[] AudioBuffer, int AudioLen,
			int AudioFrames, long AudioPTS, byte[] VideoBuffer, int VideoLen,
			long VideoPTS) {

		p2pInterface.vRecvAudioVideoData(AudioBuffer, AudioLen, AudioFrames,
				AudioPTS, VideoBuffer, VideoLen, VideoPTS);
	}

	public void setDisplay(SurfaceView sh) throws IOException {
		_setVideoSurface(sh);
	}

	public void init(int width, int height, int fullScreenSize)
			throws IllegalStateException {
		_InitSession(width, height, fullScreenSize);
	}

	public void start(int iFrameRate) throws IllegalStateException {
		openAudioRecord();
		_StartSending(iFrameRate);
	}

	public void stop() throws IllegalStateException {
		_StopSession();

		if (mAudioTrack != null) {
			mAudioTrack.flush();
			mAudioTrack.stop();

			mAudioTrack.release();
			mAudioTrack = null;
		}

		if (mAudioRecord != null) {
			mAudioRecord.stop();

			mAudioRecord.release();
			mAudioRecord = null;
		}
	}

	public void pause() throws IllegalStateException {
		_PauseSession();
	}

	public void setScreenOnWhilePlaying(boolean screenOn) {
		if (mScreenOnWhilePlaying != screenOn) {
			mScreenOnWhilePlaying = screenOn;
		}
	}

	private native void _setVideoSurface(SurfaceView surface)
			throws IOException;

	public native void _SetMute(boolean isMute) throws IOException;

	public native void _CaptureScreen() throws IOException;

	public native void _SetRecvAVDataEnable(boolean fgRecv);

	private native void _InitSession(int width, int height, int fullScreenSize)
			throws IllegalStateException;

	public native void _StartPlaying(int width, int height) throws IOException,
			IllegalStateException;

	private native void _StartSending(int iFrameRate)
			throws IllegalStateException;

	private native void _PauseSession() throws IllegalStateException;

	private native void _StopSession() throws IllegalStateException;

	/**
	 * Checks whether the MediaPlayer is playing.
	 * 
	 * @return true if currently playing, false otherwise
	 */
	public native boolean _isPlaying();

	/**
	 * H264 ENCODER
	 */
	public native int _FillVideoRawFrame(byte[] in, int insize, int width,
			int height, int isYUV);

	/**
	 * AMR ENCODER
	 */

	/**
	 * P2P connect
	 */

	private static native final void native_init(int cpuVersion)
			throws RuntimeException;

	private native final void native_setup(Object mediaplayer_this);

	public native int native_p2p_connect(int uID, int password, int code1,
			int code2, byte[] szMesg, int[] iCustomerID);

	public native int native_p2p_call(long id, int bMonitor, int password,
			int iFileIndex, byte[] filename, byte[] szMesg);

	public native void native_p2p_accpet();

	public native void native_p2p_hungup();

	public native void native_p2p_control(int control);

	public native void native_p2p_disconnect();

	// SDL function
	public static native void nativeInit(Object classObj);

	public static native void nativeQuit();

	public static native void nativePause();

	public static native void nativeResume();

	public static native void onNativeResize(int x, int y, int format);

	public static native void onNativeKeyDown(int keycode);

	public static native void onNativeKeyUp(int keycode);

	public static native void onNativeTouch(int touchDevId,
			int pointerFingerId, int action, float x, float y, float p);

	public static native void onNativeAccel(float x, float y, float z);

	public static native void nativeRunAudioThread();

	/**
	 * Releases resources associated with this MediaPlayer object. It is
	 * considered good practice to call this method when you're done using the
	 * MediaPlayer.
	 */

	public void release() {
		// stayAwake(false);
		// updateSurfaceScreenOn();
		// _release();
	}

	/**
	 * Resets the MediaPlayer to its uninitialized state. After calling this
	 * method, you will have to initialize it again by setting the data source
	 * and calling prepare().
	 */
	public void reset() {
		// stayAwake(false);
		// _reset();
	}

	// @Override

	public interface IFFMpegPlayer {
		public void onPlay();

		public void onStop();

		public void onRelease();

		public void onError(String msg, Exception e);
	}

	public interface ICapture {
		public void vCaptureResult(int result);
	}

	// yi add
	public static native void nativeInitPlayBack();

	/**
	 * +++++++++++++++++++++++SDL++++++++++++++++++++++*************************
	 * ************88
	 */
	private static EGLContext mEGLContext;
	private static EGLSurface mEGLSurface;
	private static EGLDisplay mEGLDisplay;
	private static EGLConfig mEGLConfig;
	private static int mGLMajor, mGLMinor;
	private static EGL10 mEgl;

	// Audio
	private static Thread mAudioThread;
	private static AudioTrack mAudioTrack;
	private static Object buf;

	/** ++++++++++++++++For native callback function+++++++++++++++++++++++++ */
	public static void testFunction(int sb1, int sb2) {

	}

	public static boolean createGLContext(int majorVersion, int minorVersion) {
		Log.e(TAG, "createGLContext");
		return initEGL(majorVersion, minorVersion);
	}

	public static void flipBuffers() {
		// Log.e(TAG, "flipBuffers");
		flipEGL();
	}

	public static Object audioInit(int sampleRate, boolean is16Bit,
			boolean isStereo, int desiredFrames) {
		return null;
	}

	public static void audioWriteShortBuffer(short[] buffer) {
		// Log.e(TAG, "audioWriteShortBuffer");

	}

	public static void audioWriteByteBuffer(byte[] buffer) {

	}

	public static void audioQuit() {
		Log.i(TAG, "++ audioQuit");
		if (mAudioThread != null) {
			try {
				mAudioThread.join();
			} catch (Exception e) {
				Log.v(TAG, "Problem stopping audio thread: " + e);
			}
			mAudioThread = null;

			// Log.v("SDL", "Finished waiting for audio thread");
		}

		if (mAudioTrack != null) {
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}

		Log.i(TAG, "-- audioQuit");
	}

	/** ----------------For native callback function------------------------- */

	public static void audioStartThread() {

	}

	// EGL functions
	public static boolean initEGL(int majorVersion, int minorVersion) {
		Log.i(TAG, "++ initEGL");
		Log.i("surface", "initEGL");
		if (mEGLDisplay == null) {
			// Log.v("SDL", "Starting up OpenGL ES " + majorVersion + "." +
			// minorVersion);

			try {
				if (mEgl == null) {
					mEgl = (EGL10) EGLContext.getEGL();
				}

				EGLDisplay dpy = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

				int[] version = new int[2];
				mEgl.eglInitialize(dpy, version);

				int EGL_OPENGL_ES_BIT = 1;
				int EGL_OPENGL_ES2_BIT = 4;
				int renderableType = 0;

				if (majorVersion == 2) {
					renderableType = EGL_OPENGL_ES2_BIT;
				} else if (majorVersion == 1) {
					renderableType = EGL_OPENGL_ES_BIT;
				}
				int[] configSpec = {
						// EGL10.EGL_DEPTH_SIZE, 16,
						EGL10.EGL_RENDERABLE_TYPE, renderableType,
						EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] num_config = new int[1];
				if (!mEgl.eglChooseConfig(dpy, configSpec, configs, 1,
						num_config) || num_config[0] == 0) {
					Log.e(TAG, "No EGL config available");
					return false;
				}
				EGLConfig config = configs[0];

				/*
				 * int EGL_CONTEXT_CLIENT_VERSION=0x3098; int contextAttrs[] =
				 * new int[] { EGL_CONTEXT_CLIENT_VERSION, majorVersion,
				 * EGL10.EGL_NONE }; EGLContext ctx = egl.eglCreateContext(dpy,
				 * config, EGL10.EGL_NO_CONTEXT, contextAttrs);
				 * 
				 * if (ctx == EGL10.EGL_NO_CONTEXT) { Log.e("SDL",
				 * "Couldn't create context"); return false; }
				 * SDLActivity.mEGLContext = ctx;
				 */
				mEGLDisplay = dpy;
				mEGLConfig = config;
				mGLMajor = majorVersion;
				mGLMinor = minorVersion;

				Log.i("SDL", "majorVersion " + majorVersion);
				Log.i("SDL", "minorVersion " + minorVersion);

				createEGLSurface();
			} catch (Exception e) {
				Log.v(TAG, e + "");
				for (StackTraceElement s : e.getStackTrace()) {
					Log.v(TAG, s.toString());
				}
			}
		} else
			createEGLSurface();

		Log.i(TAG, "-- initEGL");
		return true;
	}

	static long timeStart = 0;
	static int frame = 0;
	private static Object showView = null;

	public static void setEglView(Object view) {
		showView = view;
	}

	// EGL buffer flip
	public static void flipEGL() {
		if (frame == 0) {
			// timeStart = Calendar.getInstance().getTimeInMillis(); //shengming
		}

		try {
			mEgl.eglWaitNative(EGL10.EGL_CORE_NATIVE_ENGINE, null);
			// drawing here
			mEgl.eglWaitGL();
			mEgl.eglSwapBuffers(mEGLDisplay, mEGLSurface);
		} catch (Exception e) {
			Log.v(TAG, "flipEGL(): " + e);
			for (StackTraceElement s : e.getStackTrace()) {
				Log.v(TAG, s.toString());
			}
		}

		/*
		 * frame++;
		 * 
		 * if (frame == 25) { frame = 0; Log.e(TAG, "duration time is: " +
		 * (Calendar.getInstance().getTimeInMillis() - timeStart)); }
		 */
	}

	public static boolean createEGLContext() {
		int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
		int contextAttrs[] = new int[] { EGL_CONTEXT_CLIENT_VERSION, mGLMajor,
				EGL10.EGL_NONE };
		mEGLContext = mEgl.eglCreateContext(mEGLDisplay, mEGLConfig,
				EGL10.EGL_NO_CONTEXT, contextAttrs);
		if (mEGLContext == EGL10.EGL_NO_CONTEXT) {
			Log.e("SDL", "Couldn't create context");
			return false;
		}
		return true;
	}

	public static boolean createEGLSurface() {
		Log.i(TAG, "createEGLSurface");
		if (mEGLDisplay != null && mEGLConfig != null) {
			if (mEGLContext == null)
				createEGLContext();

			Log.v(TAG, "Creating new EGL Surface");
			EGLSurface surface = mEgl.eglCreateWindowSurface(mEGLDisplay,
					mEGLConfig, showView, null);
			if (surface == EGL10.EGL_NO_SURFACE) {
				Log.e(TAG, "Couldn't create surface");
				return false;
			}

			if (!mEgl
					.eglMakeCurrent(mEGLDisplay, surface, surface, mEGLContext)) {
				Log.e(TAG, "Old EGL Context doesnt work, trying with a new one");
				createEGLContext();
				if (!mEgl.eglMakeCurrent(mEGLDisplay, surface, surface,
						mEGLContext)) {
					Log.e(TAG, "Failed making EGL Context current");
					return false;
				}
			}
			mEGLSurface = surface;
			return true;
		}

		return false;
	}

	public static void ReleaseOpenGL() {
		// 首先解除display,surface等页面和接口的邦定
		mEgl.eglMakeCurrent(EGL10.EGL_NO_DISPLAY, EGL10.EGL_NO_SURFACE,
				EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);

		// 接着删除各个页面
		if (mEGLContext != null) {
			mEgl.eglDestroyContext(mEGLDisplay, mEGLContext);
			mEGLContext = null;
		}

		if (mEGLSurface != null) {
			mEgl.eglDestroySurface(mEGLDisplay, mEGLSurface);
			mEGLSurface = null;
		}

		if (mEGLDisplay != null) {
			mEgl.eglTerminate(mEGLDisplay);
			mEGLDisplay = null;
		}

	}

	/** +++++++++++++++++++++++SDL++++++++++++++++++++++ ************************************************/

	public static native int iSetNPCSettings(int iNPCID, int iPassword,
			int iMsgID, int iSettingID, int iSettingValue);

	public static native int iGetNPCSettings(int iNPCID, int iPassword,
			int iMsgID);

	public static void vRetNPCSettings(int iSrcID, int iCount,
			int[] iSettingID, int[] iValue, int iResult) {
		if (iResult == 1) {
			Log.e("my", "获取");
			for (int i = 0; i < iCount; i++) {
				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_DEFENCE) {
					settingInterface.vRetGetRemoteDefenceResult("" + iSrcID,
							iValue[i]);
				}

				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_BUZZER) {
					settingInterface.vRetGetBuzzerResult(iValue[i]);
				}

				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_RECORD) {
					settingInterface.vRetGetRemoteRecordResult(iValue[i]);
				}

				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_MOTION_DECT) {
					settingInterface.vRetGetMotionResult(iValue[i]);
				}

				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_VIDEO_FORMAT) {
					settingInterface.vRetGetVideoFormatResult(iValue[i]);
				}

				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TYPE) {
					settingInterface.vRetGetRecordTypeResult(iValue[i]);
				}

				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TIME) {
					settingInterface.vRetGetRecordTimeResult(iValue[i]);
				}

				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_NET_TYPE) {
					/*
					 * iValue[i]>>16 有线：1 wifi:2 都有：3
					 */
					settingInterface.vRetGetNetTypeResult(iValue[i] & 0xffff);
				}

				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_VOLUME) {
					settingInterface.vRetGetVideoVolumeResult(iValue[i]);
				}

				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_PLAN_TIME) {
					int time = iValue[i];
					settingInterface.vRetGetRecordPlanTimeResult(MyUtils
							.convertPlanTime(time));
				}
				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_IMAGE_REVERSE) {
					settingInterface.vRetGetImageReverseResult(iValue[i]);
				}
				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_IR_ALARM_EN) {
					settingInterface.vRetGetInfraredSwitch(iValue[i]);
				}
				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_IN_EN) {
					settingInterface.vRetGetWiredAlarmInput(iValue[i]);
				}
				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_OUT_EN) {
					settingInterface.vRetGetWiredAlarmOut(iValue[i]);
				}
				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_SECUPGDEV) {
					settingInterface.vRetGetAutomaticUpgrade(iValue[i]);
				}
				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_TIMEZONE) {
					settingInterface.vRetGetTimeZone(iValue[i]);
				}
				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.STTING_ID_GET_AUDIO_DEVICE_TYPE) {
					settingInterface.vRetGetAudioDeviceType(iValue[i]);
				}
				if (iSettingID[i] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_PRERECORD) {
					settingInterface.vRetGetPreRecord(iValue[i]);
				}
			}
		} else {
			if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_VIDEO_FORMAT) {
				settingInterface.vRetSetVideoFormatResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_VOLUME) {
				settingInterface.vRetSetVolumeResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_BUZZER) {
				settingInterface.vRetSetBuzzerResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TYPE) {
				settingInterface.vRetSetRecordTypeResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_MOTION_DECT) {
				settingInterface.vRetSetMotionResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_TIME) {
				settingInterface.vRetSetRecordTimeResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_RECORD_PLAN_TIME) {
				settingInterface.vRetSetRecordPlanTimeResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_DEFENCE) {
				settingInterface.vRetSetRemoteDefenceResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_DEVICE_PWD) {
				settingInterface.vRetSetDevicePasswordResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_NET_TYPE) {
				settingInterface.vRetSetNetTypeResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_REMOTE_RECORD) {
				settingInterface.vRetSetRemoteRecordResult(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_IMAGE_REVERSE) {
				settingInterface.vRetSetImageReverse(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_IR_ALARM_EN) {
				settingInterface.vRetSetInfraredSwitch(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_IN_EN) {
				settingInterface.vRetSetWiredAlarmInput(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_EXTLINE_ALARM_OUT_EN) {
				settingInterface.vRetSetWiredAlarmOut(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_SECUPGDEV) {
				settingInterface.vRetSetAutomaticUpgrade(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_GUEST_PASSWD) {
				settingInterface.vRetSetVisitorDevicePassword(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_TIMEZONE) {
				settingInterface.vRetSetTimeZone(iResult);
			} else if (iSettingID[0] == Constants.P2P_SETTING.SETTING_TYPE.SETTING_ID_PRERECORD) {
				settingInterface.vRetSetPreRecord(iResult);
			}
		}

	}

	public static void vRetFriendsStatus(final int iFriendsCount,
			final int[] iIDArray, final byte[] bStatus, final byte[] bType) {
		String[] threeNumbers = new String[iFriendsCount];
		int[] status = new int[iFriendsCount];
		int[] types = new int[iFriendsCount];

		for (int i = 0; i < iFriendsCount; i++) {
			int id = iIDArray[i] & 0x7FFFFFFF;
			int state = bStatus[i] & 0x0f;
			int type = bType[i] & 0x0f;

			status[i] = state;
			types[i] = type;
			if ((iIDArray[i] & 0x80000000) != 0) {
				threeNumbers[i] = "0" + id;
			} else {
				threeNumbers[i] = "" + id;
			}

		}

		settingInterface.vRetGetFriendStatus(iFriendsCount, threeNumbers,
				status, types);
	}

	public static void vRetMessage(int srcID, int iLen, byte[] cString) {
		/* ***********************************
		 * ***********************************
		 * ***********************************
		 * ***********************************
		 * ***********************************
		 * **********************************
		 */
		int id = srcID & 0x7FFFFFFF;

		if (id == 10000) {
			settingInterface.vRetSysMessage(new String(cString));
			return;
		}
		settingInterface.vRetMessage("0" + String.valueOf(id), new String(
				cString));
	}

	public static native int iGetFriendsStatus(int[] data, int iFriendNS);

	public static native int iSendMesgToFriend(int iDesID, int iMesgID,
			byte[] data, int datasize);

	public static native int iGetRecFiles(int iNPCID, int iPassword,
			int iMesgID, int iStartDateTime, int iEndDateTime);

	public static void vRetRecordFilesList(int id, int count, byte[] bytes) {
		String name = new String(bytes);
		String[] names = name.split("\\|");
		String[] names_moveEndNull = new String[count];
		System.arraycopy(names, 0, names_moveEndNull, 0, count);
		settingInterface.vRetGetRecordFiles(names_moveEndNull);
	}

	public static native int iRecFilePlayingControl(int iCommand, int iParm);

	public static native int iLocalVideoControl(int iCommand);

	public static void vRetPlayingStatus(int iStatus) {
		p2pInterface.vRetPlayBackStatus(iStatus);
	}

	public static void vRetPlayingPos(int iLength, int iCurrentSec) {
		Log.e("my", iLength + ":" + iCurrentSec);
		p2pInterface.vRetPlayBackPos(iLength, iCurrentSec);
	}

	public static void vRetPlayingSize(int iWidth, int iHeight) {
		p2pInterface.vRetPlaySize(iWidth, iHeight);
	}

	public static void vRetPlayingNumber(int iNumber) {
		p2pInterface.vRetPlayNumber(iNumber);
	}

	public static native int iSetNPCDateTime(int iNpcID, int iPassword,
			int iMesgID, int iTime);

	public static native int iGetNPCDateTime(int iNpcID, int iPassword,
			int iMesgID);

	public static native int iGetNPCEmail(int iNpcID, int iPassword, int iMesgID);

	public static native int iSetNPCEmail(int iNpcID, int iPassword,
			int iMesgID, byte[] data, int iLen);

	public static void vRetEmail(int srcID, int iLen, byte[] cString, int result) {
		if (result == 1) {
			// 获取成功
			String email = new String(cString);
			settingInterface.vRetAlarmEmailResult(result, email);
		} else {
			settingInterface.vRetAlarmEmailResult(result, "");
		}
	}

	public static native int iGetNPCWifiList(int iNpcID, int iPassword,
			int iMesgID);

	/*
	 * @param data1 Wifi Name
	 * 
	 * @param data2 Wifi password
	 */
	public static native int iSetNPCWifi(int iNpcID, int iPassword,
			int iMesgID, int iType, byte[] data1, int iLen1, byte[] data2,
			int iLen2);

	public static void vRetNPCWifiList(int srcID, int iCurrentId, int iCount,
			int[] iType, int[] iStrength, byte[] cString, int iResult) {

		String strbuffer = "--";
		for (int j = 0; j < cString.length; j++) {
			if (cString[j] == 0) {
				Log.e("wifidata", strbuffer);
				strbuffer = "--";
			}
			strbuffer = strbuffer + "  " + cString[j];
		}
		// for(int i=0;i<cString.length;i++){
		// if(cString[i]==0){
		// Log.e("data", "/");
		// Log.e("data", strbuffer);
		// strbuffer = "--";
		// continue;
		// }
		// if(cString[i]/16 == 10 )
		// strbuffer = strbuffer + "A";
		// else if(cString[i]/16 == 11 )
		// strbuffer = strbuffer + "B";
		// else if(cString[i]/16 == 12 )
		// strbuffer = strbuffer + "C";
		// else if(cString[i]/16 == 13 )
		// strbuffer = strbuffer + "D";
		// else if(cString[i]/16 == 14 )
		// strbuffer = strbuffer + "E";
		// else if(cString[i]/16 == 15 )
		// strbuffer = strbuffer + "F";
		// strbuffer = strbuffer + (cString[i]/16);
		//
		// if(cString[i]*16/16 == 10 )
		// strbuffer = strbuffer + "A";
		// else if(cString[i]*16/16 == 11 )
		// strbuffer = strbuffer + "B";
		// else if(cString[i]*16/16 == 12 )
		// strbuffer = strbuffer + "C";
		// else if(cString[i]*16/16 == 13 )
		// strbuffer = strbuffer + "D";
		// else if(cString[i]*16/16 == 14 )
		// strbuffer = strbuffer + "E";
		// else if(cString[i]*16/16 == 15 )
		// strbuffer = strbuffer + "F";
		// strbuffer = strbuffer + (cString[i]*16/16);
		//
		// strbuffer = strbuffer + " ";
		//
		// }
		if (iResult == 1) {
			String[] names;
			try {
				names = new String(cString, "UTF-8").split("\0");
				settingInterface.vRetWifiResult(iResult, iCurrentId, iCount,
						iType, iStrength, names);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			settingInterface.vRetWifiResult(iResult, 0, 0, null, null, null);
		}

	}

	public static native int iGetAlarmCodeStatus(int iNpcID, int iPassword,
			int iMesgID);

	public static native int iSetAlarmCodeStatus(int iNpcID, int iPassword,
			int iMesgID, int iCount, int iType, int[] iGroup, int[] iItem);

	public static void vRetAlarmCodeStatus(int srcID, int iCount, int key,
			byte[] bData, int iResult) {
		if (iResult == 1) {
			ArrayList<int[]> data = new ArrayList<int[]>();
			int[] status_key = new int[8];
			status_key[0] = (key >> 0) & 0x1;
			status_key[1] = (key >> 1) & 0x1;
			status_key[2] = (key >> 2) & 0x1;
			status_key[3] = (key >> 3) & 0x1;
			status_key[4] = (key >> 4) & 0x1;
			status_key[5] = (key >> 5) & 0x1;
			status_key[6] = (key >> 6) & 0x1;
			status_key[7] = (key >> 7) & 0x1;
			Log.e("area", status_key[0] + " " + status_key[1] + " "
					+ status_key[2] + " " + status_key[3] + " " + status_key[4]
					+ " " + status_key[5] + " " + status_key[6] + " "
					+ status_key[7] + " ");
			data.add(0, status_key);
			for (int i = 0; i < iCount; i++) {
				byte b = bData[i];
				int[] status = new int[8];
				status[0] = (b >> 0) & 0x1;
				status[1] = (b >> 1) & 0x1;
				status[2] = (b >> 2) & 0x1;
				status[3] = (b >> 3) & 0x1;
				status[4] = (b >> 4) & 0x1;
				status[5] = (b >> 5) & 0x1;
				status[6] = (b >> 6) & 0x1;
				status[7] = (b >> 7) & 0x1;
				Log.e("area", status[0] + " " + status[1] + " " + status[2]
						+ " " + status[3] + " " + status[4] + " " + status[5]
						+ " " + status[6] + " " + status[7] + " ");
				data.add(i + 1, status);
			}

			settingInterface.vRetDefenceAreaResult(iResult, data, 0, 0);
		} else {
			int group = bData[0];
			int item = bData[4];
			settingInterface.vRetDefenceAreaResult(iResult, null, group, item);
		}

	}

	public static native int iGetBindAlarmId(int iNpcID, int iPassword,
			int iMesgID);

	public static native int iSetBindAlarmId(int iNpcID, int iPassword,
			int iMesgID, int iCount, int[] iData);

	public static void vRetBindAlarmId(int srcID, int iMaxCount, int iCount,
			int[] iData, int iResult) {
		if (iResult == 1) {
			if (iCount == 1 && iData[0] == 0) {
				settingInterface.vRetBindAlarmIdResult(iResult, iMaxCount,
						new String[0]);
			} else {
				String[] new_data = new String[iData.length];
				for (int i = 0; i < iData.length; i++) {
					new_data[i] = "0" + iData[i];
				}
				settingInterface.vRetBindAlarmIdResult(iResult, iMaxCount,
						new_data);
			}
		} else {
			settingInterface.vRetBindAlarmIdResult(iResult, 0, null);
		}

	}

	public static void vRetDeviceNotSupport(int iNpcId) {
		Log.e("my", "device not support:" + iNpcId);
		settingInterface.vRetDeviceNotSupport();
	}

	public static native void ChangeScreenSize(int windowWidth,
			int windowHeight, int isFullScreen);

	public static native int ZoomView(int x, int y, float fScale);

	public static native int MoveView(int DetaX, int DetaY);

	public static native int iSetInitPassword(int iNpcID, int iPassword,
			int iMesgID, int iNewPassword);

	public static void vRetInitPassword(int iNpcId, int iResult) {
		settingInterface.vRetSetInitPasswordResult(iResult);
	}

	public static void vRetAlarm(int iSrcId, int iType,
			int isSupportExternAlarm, int iGroup, int iItem) {
		boolean bool = false;

		if (isSupportExternAlarm == 1) {
			bool = true;
		} else {
			bool = false;
		}

		if (iGroup > 8) {
			// 兼容BUG
			isSupportExternAlarm = 0;
		}

		p2pInterface.vAllarming(String.valueOf(iSrcId), iType, bool, iGroup,
				iItem);
	}

	/* ***********************************************************************
	 * SDK
	 * ***********************************************************************
	 */

	// 设置设备时间回调
	public static void vRetNPCTime(int iTime, int result) {
		if (result == 1) {
			// 获取成功
			settingInterface.vRetGetDeviceTimeResult(MyUtils
					.convertDeviceTime(iTime));
		} else {
			settingInterface.vRetSetDeviceTimeResult(result);
		}
	}

	public static native int iSendCmdToFriend(int iDesID, int iPassword,
			int iMesgID, byte[] data, int datasize);

	// 自定义命令
	public static void vRetCustomCmd(int srcID, int iLen, byte[] cString) {
		int id = srcID & 0x7FFFFFFF;
		settingInterface.vRetCustomCmd("0" + String.valueOf(id), new String(
				cString, 0, cString.length - 1));
	}

	public static native int iSetVideoMode(int type);

	public static native void checkDeviceUpdate(int iNpcID, int iPassword,
			int iMesgID);

	public static void vRetCheckDeviceUpdate(int iSrcID, int result,
			int iCurVersion, int iUpgVersion) {
		int a = iCurVersion & 0xff;
		int b = (iCurVersion >> 8) & 0xff;
		int c = (iCurVersion >> 16) & 0xff;
		int d = (iCurVersion >> 24) & 0xff;

		String cur_version = d + "." + c + "." + b + "." + a;

		int e = iUpgVersion & 0xff;
		int f = (iUpgVersion >> 8) & 0xff;
		int g = (iUpgVersion >> 16) & 0xff;
		int h = (iUpgVersion >> 24) & 0xff;

		String upg_version = h + "." + g + "." + f + "." + e;

		settingInterface
				.vRetCheckDeviceUpdate(result, cur_version, upg_version);
	}

	public static native void doDeviceUpdate(int iNpcID, int iPassword,
			int iMesgID);

	public static void vRetDoDeviceUpdate(int iSrcID, int result, int value) {

		settingInterface.vRetDoDeviceUpdate(result, value);
	}

	public static native void cancelDeviceUpdate(int iNpcID, int iPassword,
			int iMesgID);

	public static void vRetCancelDeviceUpdate(int iSrcID, int result) {

		settingInterface.vRetCancelDeviceUpdate(result);
	}

	public static native int iSendCtlCmd(int iCmd, int iOption);

	public static native void getDeviceVersion(int iNpcID, int iPassword,
			int iMesgID);

	public static void vRetGetDeviceVersion(int iSrcID, int result,
			int iCurVersion, int iUbootVersion, int iKernelVersion,
			int iRootfsVersion) {
		int a = iCurVersion & 0xff;
		int b = (iCurVersion >> 8) & 0xff;
		int c = (iCurVersion >> 16) & 0xff;
		int d = (iCurVersion >> 24) & 0xff;

		String cur_version = d + "." + c + "." + b + "." + a;
		settingInterface.vRetGetDeviceVersion(result, cur_version,
				iUbootVersion, iKernelVersion, iRootfsVersion);
	}

	public static native void setBindFlag(int flag);

	public static void vGXNotifyFlag(int flag) {
		p2pInterface.vGXNotifyFlag(flag);
	}

	public static native int iClearAlarmCodeGroup(int iNpcID, int iPassword,
			int iMesgID, int iGroup);

	public static void vRetClearAlarmCodeGroup(int iSrcID, int result) {
		settingInterface.vRetClearDefenceAreaState(result);
	}

	public static native int iExtendedCmd(int iTargetID, int iPassword,
			int iMesgID, byte[] data, int datasize);

	public static final int MESG_TYPE_GET_DISK_INFO = 80;
	public static final int MESG_TYPE_FORMAT_DISK = 81;
	public static final int MESG_SDCARD_NO_EXIST = 82;
	public static final int MESG_SET_GPIO_INFO = 96;
	public static final int MESG_TYPE_RET_DEFENCE_SWITCH_STATE = 84;
	public static final int MESG_TYPE_GET_ALARM_INFO = 122;

	public static void vRetExtenedCmd(int iSrcID, byte[] data, int datasize) {
		Log.e("sddata", data.toString());
		for (int i = 0; i < data.length; i++) {
			Log.e("sddata", "data" + data[i]);
		}
		if (data[0] == MESG_TYPE_GET_DISK_INFO) {
			if (data[1] == MESG_SDCARD_NO_EXIST) {
				settingInterface.vRetGetSdCard(0, 0, 0, 0);
			} else {
				int DiskCount;
				int DiskID;
				long TotalSpace, FreeSpace;
				DiskCount = data[2] + data[3] * 256;
				Log.e("2cu", "---" + DiskCount);
				DiskID = data[4];
				Log.e("diskid", "DiskID" + DiskID);

				long[] longData = new long[8];
				longData[0] = 0xFF & data[5];
				longData[0] <<= 0;
				longData[1] = 0xFF & data[6];
				longData[1] <<= 8;
				longData[2] = 0xFF & data[7];
				longData[2] <<= 16;
				longData[3] = 0xFF & data[8];
				longData[3] <<= 24;
				longData[4] = 0xFF & data[9];
				longData[4] <<= 32;
				longData[5] = 0xFF & data[10];
				longData[5] <<= 40;
				longData[6] = 0xFF & data[11];
				longData[6] <<= 48;
				longData[7] = 0xFF & data[12];
				longData[7] <<= 56;

				TotalSpace = longData[0] + longData[1] + longData[2]
						+ longData[3] + longData[4] + longData[5] + longData[6]
						+ longData[7];
				TotalSpace = TotalSpace / 1024 / 1024; // MB

				longData[0] = 0xFF & data[13];
				longData[0] <<= 0;
				longData[1] = 0xFF & data[14];
				longData[1] <<= 8;
				longData[2] = 0xFF & data[15];
				longData[2] <<= 16;
				longData[3] = 0xFF & data[16];
				longData[3] <<= 24;
				longData[4] = 0xFF & data[17];
				longData[4] <<= 32;
				longData[5] = 0xFF & data[18];
				longData[5] <<= 40;
				longData[6] = 0xFF & data[19];
				longData[6] <<= 48;
				longData[7] = 0xFF & data[20];
				longData[7] <<= 56;

				FreeSpace = longData[0] + longData[1] + longData[2]
						+ longData[3] + longData[4] + longData[5] + longData[6]
						+ longData[7];
				FreeSpace = FreeSpace / 1024 / 1024; // MB

				Log.e("2cu", "TotalSpace=" + TotalSpace);
				Log.e("2cu", "FreeSpace=" + FreeSpace);

				settingInterface.vRetGetSdCard((int) TotalSpace,
						(int) FreeSpace, DiskID, 1);

				if (DiskCount > 1) {
					DiskID = data[21];
					Log.e("diskid", "DiskID" + DiskID);
					longData[0] = 0xFF & data[22];
					longData[0] <<= 0;
					longData[1] = 0xFF & data[23];
					longData[1] <<= 8;
					longData[2] = 0xFF & data[24];
					longData[2] <<= 16;
					longData[3] = 0xFF & data[25];
					longData[3] <<= 24;
					longData[4] = 0xFF & data[26];
					longData[4] <<= 32;
					longData[5] = 0xFF & data[27];
					longData[5] <<= 40;
					longData[6] = 0xFF & data[28];
					longData[6] <<= 48;
					longData[7] = 0xFF & data[29];
					longData[7] <<= 56;

					TotalSpace = longData[0] + longData[1] + longData[2]
							+ longData[3] + longData[4] + longData[5]
							+ longData[6] + longData[7];
					TotalSpace = TotalSpace / 1024 / 1024; // MB

					longData[0] = 0xFF & data[30];
					longData[0] <<= 0;
					longData[1] = 0xFF & data[31];
					longData[1] <<= 8;
					longData[2] = 0xFF & data[32];
					longData[2] <<= 16;
					longData[3] = 0xFF & data[33];
					longData[3] <<= 24;
					longData[4] = 0xFF & data[34];
					longData[4] <<= 32;
					longData[5] = 0xFF & data[35];
					longData[5] <<= 40;
					longData[6] = 0xFF & data[36];
					longData[6] <<= 48;
					longData[7] = 0xFF & data[37];
					longData[7] <<= 56;

					FreeSpace = longData[0] + longData[1] + longData[2]
							+ longData[3] + longData[4] + longData[5]
							+ longData[6] + longData[7];
					FreeSpace = FreeSpace / 1024 / 1024; // MB

					Log.e("2cu", "TotalSpace=" + TotalSpace);
					Log.e("2cu", "FreeSpace=" + FreeSpace);

					settingInterface.VRetGetUsb((int) TotalSpace,
							(int) FreeSpace, DiskID, 1);

				}

			}

		} else if (data[0] == MESG_TYPE_FORMAT_DISK) {
			settingInterface.vRetSdFormat(data[1]);
		} else if (data[0] == MESG_SET_GPIO_INFO) {
			int result = -1;
			if (data[1] < 0) {
				result = (data[1] + 256);
			} else {
				result = data[1];
			}
			settingInterface.vRetSetGPIO(result);
		} else if (data[0] == MESG_TYPE_RET_DEFENCE_SWITCH_STATE) {
			if (data[1] == 1) {
				ArrayList<int[]> sensors = new ArrayList<int[]>();
				for (int i = 4; i < data.length; i++) {
					if (data[i] < 0) {
						String sensor = Integer.toBinaryString(data[i] + 256);
						int[] sensor_switchs = new int[8];
						ArrayList<Integer> list = new ArrayList<Integer>();
						if (sensor.length() < 8) {
							for (int k = 0; k < 8 - sensor.length(); k++) {
								list.add(0);
							}
						}
						for (int j = 0; j < sensor.length(); j++) {
							list.add(Integer.parseInt(sensor
									.substring(j, j + 1)));
						}
						Log.e("length", "list_size" + list.size());
						String s = "";
						for (int k = 0; k < list.size(); k++) {
							sensor_switchs[k] = list.get(k);
							s = s + sensor_switchs[k];
						}
						Log.e("length", s);
						// Log.e("length",
						// "sensor="+sensor+"length="+sensor.length());
						// int [] sensor_switchs=new int[8];
						// if(sensor.length()<8){
						// for(int k=0;k<8-sensor.length();k++){
						// sensor_switchs[k]=0;
						// }
						// }
						// for(int j=8-sensor.length();j<8;j++){
						// sensor_switchs[j]=Integer.parseInt(sensor.substring(j,
						// j+1));
						// }
						sensors.add(sensor_switchs);
					} else {
						String sensor = Integer.toBinaryString(data[i]);
						int[] sensor_switchs = new int[8];
						ArrayList<Integer> list = new ArrayList<Integer>();
						if (sensor.length() < 8) {
							for (int k = 0; k < 8 - sensor.length(); k++) {
								list.add(0);
							}
						}
						for (int j = 0; j < sensor.length(); j++) {
							list.add(Integer.parseInt(sensor
									.substring(j, j + 1)));
						}
						Log.e("length", "list_size" + list.size());
						String s = "";
						for (int k = 0; k < list.size(); k++) {
							sensor_switchs[k] = list.get(k);
							s = s + sensor_switchs[k];
						}
						Log.e("length", s);
						// Log.e("length",
						// "sensor="+sensor+"length="+sensor.length());
						// int [] sensor_switchs=new int[8];
						// if(sensor.length()<8){
						// for(int k=0;k<8-sensor.length();k++){
						// sensor_switchs[k]=0;
						// }
						// }
						// for(int j=8-sensor.length();j<8;j++){
						// sensor_switchs[j]=Integer.parseInt(sensor.substring(j,
						// j+1));
						// }
						sensors.add(sensor_switchs);
					}
				}
				settingInterface.vRetGetSensorSwitchs(1, sensors);
			} else if (data[1] == 0) {
				settingInterface.vRetSetSensorSwitchs(0);
			} else if (data[1] == 41) {
				settingInterface.vRetGetSensorSwitchs(41,
						new ArrayList<int[]>());
			} else if (data[1] == 88) {
				settingInterface.vRetSetSensorSwitchs(88);
			}
		} else if (data[0] == MESG_TYPE_GET_ALARM_INFO) {
			if (data[1] == 1) {
				if (data.length < 14) {
					return;
				}
				List<byte[]> list = new ArrayList<byte[]>();
				ArrayList<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
				Log.e("alarm_number", "data[2]" + data[2]);
				int alarm_number = data[2];
				if (alarm_number < 0) {
					alarm_number = alarm_number + 256;
				}
				Log.e("alarm_record", "alarm_number=" + alarm_number);
				Log.e("data_length", "lenght=" + data.length);
				byte[] alarm_index = { data[4], data[5], data[6], data[7] };
				int index = bytesToInt(alarm_index, 0);
				Log.e("alarm_record", "index=" + index);
				int record_number = (data.length - 8) / 6;
				for (int i = 0; i < record_number; i++) {
					int record_index = 8 + i * 6;
					byte[] record = new byte[6];
					for (int j = 0; j < 6; j++) {
						record[j] = data[record_index];
						record_index++;
					}
					list.add(record);
				}
				for (int i = 0; i < list.size(); i++) {
					byte[] bt = list.get(i);
					long time = bytesToInt(bt, 0);
					long second = time * 1000L;
					TimeZone timezone_defalt = TimeZone.getDefault();
					String zone_name = timezone_defalt.getDisplayName(false,
							TimeZone.SHORT);
					int timezone = TimeZone.getDefault().getRawOffset();
					String zone = TimeZone.getDefault().getDisplayName(false,
							TimeZone.SHORT);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");// 制定日期的显示格式
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
					String alarm_time = sdf.format(new Date(time * 1000));
					int alarm_type = bt[4];
					String alarm_channel = Integer.toBinaryString(bt[5]);
					int[] channel = new int[8];
					ArrayList<Integer> list_channel = new ArrayList<Integer>();
					if (alarm_channel.length() < 8) {
						for (int k = 0; k < 8 - alarm_channel.length(); k++) {
							list_channel.add(0);
						}
					}
					for (int j = 0; j < alarm_channel.length(); j++) {
						list_channel.add(Integer.parseInt(alarm_channel
								.substring(j, j + 1)));
					}
					String s = "";
					for (int k = 0; k < list_channel.size(); k++) {
						s = s + list_channel.get(k);
					}
					Log.e("alarm_record", "channel=" + s);
					Log.e("alarm_record", "alarm_time=" + alarm_time + "--"
							+ "alarm_type=" + alarm_type + "--"
							+ "alarm_channel=" + alarm_channel + "--" + "time="
							+ time + "bt[5]" + bt[5]);
					AlarmRecord alarm_record = new AlarmRecord();
					alarm_record.alarmTime = alarm_time;
					alarm_record.alarmType = alarm_type;
					alarm_record.alarmChannel = s;
					alarmRecords.add(alarm_record);
				}
				settingInterface.vRetGetAlarmRecord(alarm_number, index,
						alarmRecords);
			}

		}
	}

	// 发送 wifiSSID与 密码到设备 功能未实现 尚未开放
	public static native void vSendWiFiCmd(int iType, byte[] SSID,
			int iSSIDLen, byte[] Password, int iPasswordLen);

	// 收到新的系统消息的回调函数
	// 参数-iSystemMessageType： 系统消息的类型
	// 参数-iSystemMessageIndex： 系统消息的序号标记
	// Java层收到此回调函数的时候需要 使用web接口去服务器获取新的消息内容，获取到后把已经取下来的最新的系统消息的序号标记设回jni层
	// （使用SetSystemMessageIndex()接口）
	// 刚初始化jni的时候，由于还没有设置本地的消息系统消息的序号标记，jni层有可能会产生此回调函数。
	public static void RetNewSystemMessage(int iSystemMessageType,
			int iSystemMessageIndex) {
		Log.e("systemmessage", "type=" + iSystemMessageType + "  index"
				+ iSystemMessageIndex);

	}

	// 设置系统消息的序号标记
	// 参数-iSystemMessageType： 系统消息的类型
	// 参数-iSystemMessageIndex： 系统消息的序号标记
	// 调用此函数的目的是让jni层能够判别是否有新的消息
	public static native void SetSystemMessageIndex(int iSystemMessageType,
			int iSystemMessageIndex);

	public static int bytesToInt(byte[] bytes, int index) {

		int addr = bytes[index] & 0xFF;

		addr |= ((bytes[index + 1] << 8) & 0xFF00);

		addr |= ((bytes[index + 2] << 16) & 0xFF0000);

		addr |= ((bytes[index + 3] << 24) & 0xFF000000);

		return addr;

	}

}
