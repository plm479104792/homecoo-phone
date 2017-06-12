package object.p2pipcam.content;

public class ContentCommon {
	public static final String MAIN_KEY_MENU = "key_menu";
	public static final String MAIN_KEY_BACK = "key_back";

	public static final int MSG_GET_ALL_DOORBELL_PARM = 0x3a;
	public static final int  MSG_GET_RESET_USER =0x3b;
	
	public static final String SDCARD_PATH = "DoorBell";
	public static final String STR_CAMERA_INFO_RECEIVER = "object.ipcam.client.camerainforeceiver";
	public static final String STR_CAMERA_ADDR = "camera_addr";
	public static final String STR_CAMERA_PORT = "camera_port";
	public static final String STR_CAMERA_NAME = "camera_name";
	public static final String STR_CAMERA_MAC = "camera_mac";
	public static final String STR_CAMERA_USER = "camera_user";
	public static final String STR_CAMERA_PWD = "camera_pwd";
	public static final String STR_CAMERA_ID = "cameraid";
	public static final String STR_CAMERA_SNAPSHOT = "camera_snapshot";
	public static final String STR_CAMERA_USER_AUTHORITY = "camera_user_authority";

	public static final String STR_CAMERA_OLD_ADDR = "camera_old_addr";
	public static final String STR_CAMERA_OLD_PORT = "camera_old_port";
	public static final String STR_CAMERA_OLD_ID = "camera_old_id";

	public static final String STR_CAMERA_TYPE = "camera_type";
	public static final String STR_STREAM_TYPE = "stream_type";
	public static final String STR_H264_MAIN_STREAM = "h264_main_stream";
	public static final String STR_H264_SUB_STREAM = "h264_sub_stream";
	public static final String STR_MJPEG_SUB_STREAM = "mjpeg_sub_stream";

	public static final int DEFAULT_PORT = 81;
	public static final String DEFAULT_USER_NAME = "admin";
	public static final String DEFAULT_USER_PWD = "";

	public static final String CAMERA_OPTION = "camera_option";
	public static final int ADD_CAMERA = 1;
	public static final int EDIT_CAMERA = 2;
	public static final int CHANGE_CAMERA_USER = 3;
	public static final int DEL_CAMERA = 4;
	public static final int INVALID_OPTION = 0xffff;

	public static final int CAMERA_TYPE_UNKNOW = 0;
	public static final int CAMERA_TYPE_MJPEG = 1;
	public static final int CAMERA_TYPE_H264 = 2;

	public static final int H264_MAIN_STREAM = 0;
	public static final int H264_SUB_STREAM = 1;
	public static final int MJPEG_SUB_STREAM = 3;
	// #define MSG_TYPE_SET_AP 0x34
	// #define MSG_TYPE_GET_AP 0x35
	public static final int MSG_TYPE_SET_AP = 0x34;
	public static final int MSG_TYPE_GET_AP = 0x35;
	// =======pppppp==============================================

	public static final int PPPP_DEV_TYPE_UNKNOWN = 0xffffffff;

	public static final String STR_PPPP_STATUS = "pppp_status";

	public static final int PPPP_STATUS_CONNECTING = 0;/* connecting */
	public static final int PPPP_STATUS_INITIALING = 1;/* initialing */
	public static final int PPPP_STATUS_ON_LINE = 2;/* on line */
	public static final int PPPP_STATUS_CONNECT_FAILED = 3;/* connect failed */
	public static final int PPPP_STATUS_DISCONNECT = 4;/* connect is off */
	public static final int PPPP_STATUS_INVALID_ID = 5;
	public static final int PPPP_STATUS_DEVICE_NOT_ON_LINE = 6;
	public static final int PPPP_STATUS_CONNECT_TIMEOUT = 7;
	public static final int PPPP_STATUS_CONNECT_ERRER = 8;
	public static final int PPPP_STATUS_USER_LOGIN = 9;
	public static final int PPPP_STATUS_PWD_CUO = 10;
	public static final int PPPP_STATUS_UNKNOWN = 0xffffffff;

	public static final int PPPP_MSG_TYPE_PPPP_STATUS = 0;
	public static final int PPPP_MSG_TYPE_PPPP_MODE = 1;
	public static final int PPPP_MSG_TYPE_STREAM = 2;
	public static final int PPPP_MSG_TYPE_INVALID_MSG = 0xffffffff;

	public static final int PPPP_STREAM_TYPE_H264 = 0;
	public static final int PPPP_STREAM_TYPE_JPEG = 1;

	public static final int PPPP_MODE_UNKNOWN = 0xffffffff;
	public static final int PPPP_MODE_P2P_NORMAL = 1;
	public static final int PPPP_MODE_P2P_RELAY = 2;

	// ptz control command ---------------------------------

	public static final int CMD_PTZ_UP = 0;
	public static final int CMD_PTZ_UP_STOP = 1;
	public static final int CMD_PTZ_DOWN = 2;
	public static final int CMD_PTZ_DOWN_STOP = 3;
	public static final int CMD_PTZ_LEFT = 4;
	public static final int CMD_PTZ_LEFT_STOP = 5;
	public static final int CMD_PTZ_RIGHT = 6;
	public static final int CMD_PTZ_RIGHT_STOP = 7;
	// new add
	public static final int CMD_PTZ_CENTER = 25;// 锟斤拷锟斤拷
	public static final int CMD_PTZ_UP_DOWN = 26;// 锟斤拷直巡锟斤拷
	public static final int CMD_PTZ_UP_DOWN_STOP = 27;// 锟斤拷直巡锟斤拷停止
	public static final int CMD_PTZ_LEFT_RIGHT = 28;// 水平巡锟斤拷
	public static final int CMD_PTZ_LEFT_RIGHT_STOP = 29;// 水平巡锟斤拷停止
	public static final int CMD_PTZ_ORIGINAL = 0;// 原始位锟斤拷
	public static final int CMD_PTZ_VERTICAL_MIRROR = 1;// 锟斤拷直锟斤拷锟斤拷
	public static final int CMD_PTZ_HORIZONAL_MIRROR = 2;// 水平锟斤拷锟斤拷
	public static final int CMD_PTZ_VERHOR_MIRROR = 3;// 水平锟斤拷直锟斤拷转

	// 预锟斤拷位锟斤拷锟斤拷
	public static final int CMD_PTZ_PREFAB_BIT_SET0 = 30;
	public static final int CMD_PTZ_PREFAB_BIT_SET1 = 32;
	public static final int CMD_PTZ_PREFAB_BIT_SET2 = 34;
	public static final int CMD_PTZ_PREFAB_BIT_SET3 = 36;
	public static final int CMD_PTZ_PREFAB_BIT_SET4 = 38;
	public static final int CMD_PTZ_PREFAB_BIT_SET5 = 40;
	public static final int CMD_PTZ_PREFAB_BIT_SET6 = 42;
	public static final int CMD_PTZ_PREFAB_BIT_SET7 = 44;
	public static final int CMD_PTZ_PREFAB_BIT_SET8 = 46;
	public static final int CMD_PTZ_PREFAB_BIT_SET9 = 48;
	public static final int CMD_PTZ_PREFAB_BIT_SETA = 50;
	public static final int CMD_PTZ_PREFAB_BIT_SETB = 52;
	public static final int CMD_PTZ_PREFAB_BIT_SETC = 54;
	public static final int CMD_PTZ_PREFAB_BIT_SETD = 56;
	public static final int CMD_PTZ_PREFAB_BIT_SETE = 58;
	public static final int CMD_PTZ_PREFAB_BIT_SETF = 60;

	// 预锟斤拷位锟介看
	public static final int CMD_PTZ_PREFAB_BIT_RUN0 = 31;
	public static final int CMD_PTZ_PREFAB_BIT_RUN1 = 33;
	public static final int CMD_PTZ_PREFAB_BIT_RUN2 = 35;
	public static final int CMD_PTZ_PREFAB_BIT_RUN3 = 37;
	public static final int CMD_PTZ_PREFAB_BIT_RUN4 = 39;
	public static final int CMD_PTZ_PREFAB_BIT_RUN5 = 41;
	public static final int CMD_PTZ_PREFAB_BIT_RUN6 = 43;
	public static final int CMD_PTZ_PREFAB_BIT_RUN7 = 45;
	public static final int CMD_PTZ_PREFAB_BIT_RUN8 = 47;
	public static final int CMD_PTZ_PREFAB_BIT_RUN9 = 49;
	public static final int CMD_PTZ_PREFAB_BIT_RUNA = 51;
	public static final int CMD_PTZ_PREFAB_BIT_RUNB = 53;
	public static final int CMD_PTZ_PREFAB_BIT_RUNC = 55;
	public static final int CMD_PTZ_PREFAB_BIT_RUND = 57;
	public static final int CMD_PTZ_PREFAB_BIT_RUNE = 59;
	public static final int CMD_PTZ_PREFAB_BIT_RUNF = 61;

	public static final int MSG_TYPE_GET_CAMERA_PARAMS = 0x2;// 锟斤拷取锟斤拷频锟斤拷锟斤拷
	public static final int MSG_TYPE_DECODER_CONTROL = 0x3; // 锟斤拷台锟斤拷锟斤拷
	public static final int MSG_TYPE_GET_PARAMS = 0x4; // 锟斤拷取锟斤拷锟界，WIFI,锟矫伙拷锟斤拷息锟斤拷FTP,DNS,MAIL,DATETIME,锟斤拷锟斤拷锟斤拷息锟斤拷锟斤拷
	public static final int MSG_TYPE_SNAPSHOT = 0x5; // 抓图
	public static final int MSG_TYPE_CAMERA_CONTROL = 0x6; // 锟斤拷频锟斤拷锟斤拷锟斤拷锟斤拷,锟斤拷锟斤拷
	public static final int MSG_TYPE_SET_NETWORK = 0x7; // 锟斤拷锟斤拷锟斤拷锟斤拷
	public static final int MSG_TYPE_REBOOT_DEVICE = 0x8; // 锟斤拷锟斤拷锟借备
	public static final int MSG_TYPE_RESTORE_FACTORY = 0x9;// 锟街革拷锟斤拷锟斤拷锟斤拷
	public static final int MSG_TYPE_SET_USER = 0xa;// 锟斤拷锟斤拷锟矫伙拷
	public static final int MSG_TYPE_SET_WIFI = 0xb; // 锟斤拷锟斤拷wifi
	public static final int MSG_TYPE_SET_DATETIME = 0xc;// 时锟斤拷锟斤拷锟斤拷
	public static final int MSG_TYPE_GET_STATUS = 0xd; // 锟斤拷取锟斤拷锟斤拷锟阶刺�
	public static final int MSG_TYPE_GET_PTZ_PARAMS = 0xe;// PTZ锟斤拷锟斤拷
	public static final int MSG_TYPE_SET_DDNS = 0xf; // DDNS锟斤拷锟斤拷
	public static final int MSG_TYPE_SET_MAIL = 0x10;// 锟绞硷拷锟斤拷锟斤拷
	public static final int MSG_TYPE_SET_FTP = 0x11; // FTP锟斤拷锟斤拷
	public static final int MSG_TYPE_SET_ALARM = 0x12; // 锟斤拷锟斤拷锟斤拷息
	public static final int MSG_TYPE_SET_PTZ = 0x13;// PTZ锟斤拷锟斤拷
	public static final int MSG_TYPE_WIFI_SCAN = 0x14; // WIFI扫锟斤拷
	public static final int MSG_TYPE_GET_ALARM_LOG = 0x15;
	public static final int MSG_TYPE_GET_RECORD = 0x16;
	public static final int MSG_TYPE_GET_RECORD_FILE = 0x17;
	public static final int MSG_TYPE_SET_PPPOE = 0x18;
	public static final int MSG_TYPE_SET_UPNP = 0x19;
	public static final int MSG_TYPE_DEL_RECORD_FILE = 0x1a;
	public static final int MSG_TYPE_SET_MEDIA = 0x1b;
	public static final int MSG_TYPE_SET_RECORD_SCH = 0x1c;
	public static final int MSG_TYPE_CLEAR_ALARM_LOG = 0x1d;
	public static final int MSG_TYPE_WIFI_PARAMS = 0x1f;
	public static final int MSG_TYPE_MAIL_PARAMS = 0x20;
	public static final int MSG_TYPE_FTP_PARAMS = 0x21;
	public static final int MSG_TYPE_NETWORK_PARAMS = 0x22;
	public static final int MSG_TYPE_USER_INFO = 0x23;
	public static final int MSG_TYPE_DDNS_PARAMS = 0x24;
	public static final int MSG_TYPE_DATETIME_PARAMS = 0x25;
	public static final int MSG_TYPE_ALARM_PARAMS = 0x26;
	public static final int MSG_TYPE_SET_DEVNAME = 0x27;
	public static final int MSG_TYPE_DOOR_BELL_SETUP_PARM = 0x39;
	// =========鎶ヨ鏃ュ織=================
	public static final int MOTION_ALARM = 0x01;
	public static final int GPIO_ALARM = 0x02;

	// end
	// cgi cmd ======
	// IE CGI CMD
	// #define CGI_IEGET_STATUS 0x6001
	// #define CGI_IEGET_PARAM 0x6002
	public static final int CGI_IEGET_CAM_PARAMS = 0x6003;
	// #define CGI_IEGET_LOG 0x6004
	// #define CGI_IEGET_MISC 0x6005
	// #define CGI_IEGET_RECORD 0x6006
	// #define CGI_IEGET_RECORD_FILE 0x6007
	// #define CGI_IEGET_WIFI_SCAN 0x6008
	// #define CGI_IEGET_FACTORY 0x6009
	// #define CGI_IESET_IR 0x600a
	// #define CGI_IESET_UPNP 0x600b
	// #define CGI_IESET_ALARM 0x600c
	// #define CGI_IESET_LOG 0x600d
	// #define CGI_IESET_USER 0x600e
	// #define CGI_IESET_ALIAS 0x600f
	// #define CGI_IESET_MAIL 0x6010
	// #define CGI_IESET_WIFI 0x6011
	// #define CGI_CAM_CONTROL 0x6012
	// #define CGI_IESET_DATE 0x6013
	// #define CGI_IESET_MEDIA 0x6014
	// #define CGI_IESET_SNAPSHOT 0x6015
	// #define CGI_IESET_DDNS 0x6016
	// #define CGI_IESET_MISC 0x6017
	// #define CGI_IEGET_FTPTEST 0x6018
	// #define CGI_DECODER_CONTROL 0x6019
	// #define CGI_IESET_DEFAULT 0x601a
	// #define CGI_IESET_MOTO 0x601b
	// #define CGI_IEGET_MAILTEST 0x601c
	// #define CGI_IESET_MAILTEST 0x601d
	// #define CGI_IEDEL_FILE 0x601e
	// #define CGI_IELOGIN 0x601f
	// #define CGI_IESET_DEVICE 0x6020
	// #define CGI_IESET_NETWORK 0x6021
	// #define CGI_IESET_FTPTEST 0x6022
	// #define CGI_IESET_DNS 0x6023
	// #define CGI_IESET_OSD 0x6024
	// #define CGI_IESET_FACTORY 0x6025
	// #define CGI_IESET_PPPOE 0x6026
	// #define CGI_IEREBOOT 0x6027
	// #define CGI_IEFORMATSD 0x6028
	// #define CGI_IESET_RECORDSCH 0x6029
	// #define CGI_IESET_WIFISCAN 0x602a
	// #define CGI_IERESTORE 0x602b
	// #define CGI_IESET_FTP 0x602c
	// #define CGI_IESET_RTSP 0x602d
	// #define CGI_IEGET_VIDEOSTREAM 0x602e
	// #define CGI_UPGRADE_APP 0x602f
	// #define CGI_UPGRADE_SYS 0x6030
	//
	// #define CGI_SET_IIC 0x6031
	// #define CGI_GET_IIC 0x6032
	//
	// #define CGI_IEGET_ALARMLOG 0x6033
	// #define CGI_IESET_ALARMLOGCLR 0X6034
	//
	// #define CGI_IEGET_SYSWIFI 0x6035
	// #define CGI_IESET_SYSWIFI 0X6036
	//
	// #define CGI_IEGET_LIVESTREAM 0X6037

}