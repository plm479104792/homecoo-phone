package et.song.global;

import android.content.Intent;
import et.song.device.DeviceType;
import et.song.remotestar.hxd.sdk.R;
import et.song.tg.face.ITg;


public final class ETGlobal  {
	public static int W = 0;
	public static int H = 0;
	public static ITg mTg = null;
	public static boolean mIsWifiWan = false;
	public final static String NETWORK_REMOTE_HOST = "http://www.hxdkj88.com/";
	public final static String WIFI_DEVICE_DEFAULT_PORT = "8695";
	public final static String APPName = "RemoteStar_HXD.apk";
	public final static String APPVer = "version_hxd.json";
	
	public final static String BROADCAST_OPEN_FINISH = "ET.SONG.BROADCAST.APP.HXD.RS.OPEN.FINISH";
	public final static String BROADCAST_START_LEARN = "ET.SONG.BROADCAST.APP.HXD.RS.START.LEARN";
	public final static String BROADCAST_END_LEARN = "ET.SONG.BROADCAST.APP.HXD.RS.END.LEARN";
	public final static String BROADCAST_PASS_LEARN = "ET.SONG.BROADCAST.APP.HXD.RS.PASS.LEARN";
	public final static String BROADCAST_REPEAT_LEARN = "ET.SONG.BROADCAST.APP.HXD.RS.REPEAT.LEARN";	
	public final static String BROADCAST_DATABASE_LOAD = "ET.SONG.BROADCAST.APP.HXD.RS.DATABASE.LOAD";
	public final static String BROADCAST_KEYCODE_VOLUME_DOWN = "ET.SONG.BROADCAST.APP.HXD.RS.VOLUME_DOWN";
	public final static String BROADCAST_KEYCODE_VOLUME_UP = "ET.SONG.BROADCAST.APP.HXD.RS.VOLUME_UP";
	public final static String BROADCAST_DATA_RECV = "ET.SONG.APP.HXD.RS.DATA_RECV";
	public final static String BROADCAST_DATA_SEND = "ET.SONG.APP.HXD.RS.DATA_SEND";
	
	public final static String BROADCAST_APP_UPDATE_LOADING = "ET.SONG.BROADCAST.APP.HXD.RSUPDATE.LOADING";
	public final static String BROADCAST_APP_UPDATE_START = "ET.SONG.BROADCAST.APP.HXD.RSUPDATE.START";
	public final static String BROADCAST_APP_BACK = "ET.SONG.BROADCAST.APP.HXD.RSBACK";
	public final static String BROADCAST_APP_BUY_YES = "ET.SONG.BROADCAST.APP.HXD.RSBUY.YES";
	public final static String BROADCAST_APP_BUY_NO = "ET.SONG.BROADCAST.APP.HXD.RSBUY.NO";
	public final static String BROADCAST_FOUND_COL = "ET.SONG.BROADCAST.APP.HXD.RSFOUND.COL";
	/** group type (strings.xml, ETGlobal.java) */
	public static final int ETGROUP_TYPE = 0x01000000;
	public static final int ETGROUP_TYPE_ADD = ETGROUP_TYPE | 0x00000001;
	public static final int ETGROUP_TYPE_BEDROOM = ETGROUP_TYPE | 0x00000002;
	public static final int ETGROUP_TYPE_LIVINGROOM = ETGROUP_TYPE | 0x00000003;
	public static final int ETGROUP_TYPE_OFFICROOM = ETGROUP_TYPE | 0x00000004;
	public static final int ETGROUP_TYPE_COOKROOM = ETGROUP_TYPE | 0x00000005;
	public static final int ETGROUP_TYPE_BATHROOM = ETGROUP_TYPE | 0x00000006;
	public static final int ETGROUP_TYPE_BABYROOM = ETGROUP_TYPE | 0x00000007;
	public static final int ETGROUP_TYPE_MEETINGROOM = ETGROUP_TYPE | 0x00000008;
	public static final int ETGROUP_TYPE_DININGROOM = ETGROUP_TYPE | 0x00000009;
	public static final int ETGROUP_TYPE_CUSTOM = ETGROUP_TYPE | 0x0000FF00;

	/** device type (strings.xml, ETGlobal.java) */

	public static final int ETWIFIDEVICE_TYPE = 0x03000000;

	public static int[] mGroupTypes = new int[] { ETGROUP_TYPE_BEDROOM,
			ETGROUP_TYPE_LIVINGROOM, ETGROUP_TYPE_OFFICROOM,
			ETGROUP_TYPE_MEETINGROOM, ETGROUP_TYPE_COOKROOM,
			ETGROUP_TYPE_DININGROOM, ETGROUP_TYPE_BATHROOM,
			ETGROUP_TYPE_BABYROOM, ETGROUP_TYPE_ADD };
	public static int[] mGroupImages = new int[] { R.drawable.ic_group_bedroom,
			R.drawable.ic_group_livingroom, R.drawable.ic_group_officeroom,
			R.drawable.ic_group_meetingroom, R.drawable.ic_group_cookroom,
			R.drawable.ic_group_diningroom, R.drawable.ic_group_bathroom,
			R.drawable.ic_group_babyroom, R.drawable.ic_all_add };

	public static int[] mDeviceTypes = new int[] { DeviceType.DEVICE_REMOTE_TV,
			DeviceType.DEVICE_REMOTE_IPTV, DeviceType.DEVICE_REMOTE_STB,
			DeviceType.DEVICE_REMOTE_DVD, DeviceType.DEVICE_REMOTE_FANS,
			DeviceType.DEVICE_REMOTE_PJT, DeviceType.DEVICE_REMOTE_LIGHT,
			DeviceType.DEVICE_REMOTE_AIR, DeviceType.DEVICE_REMOTE_DC, DeviceType.DEVICE_REMOTE_CUSTOM,
			DeviceType.DEVICE_REMOTE_POWER,
			DeviceType.DEVICE_ADD };
	public static int[] mDeviceImages = new int[] { R.drawable.ic_device_tv,
			R.drawable.ic_device_iptv, R.drawable.ic_device_stb,
			R.drawable.ic_device_dvd, R.drawable.ic_device_fans,
			R.drawable.ic_device_pjt, R.drawable.ic_device_light,
			R.drawable.ic_device_air, R.drawable.ic_device_dc, R.drawable.ic_device_diy,
			R.drawable.ic_device_power,
			R.drawable.ic_all_add };

	public static int[] mWatchTVImages = new int[] { R.drawable.ic_anhui,
			R.drawable.ic_btv_caijing, R.drawable.ic_btv_kejiao,
			R.drawable.ic_btv_shenghuo, R.drawable.ic_btv_tiyu,
			R.drawable.ic_btv_wenyi, R.drawable.ic_btv_yingshi,
			R.drawable.ic_btv, R.drawable.ic_cctv1, R.drawable.ic_cctv2,
			R.drawable.ic_cctv3, R.drawable.ic_cctv4, R.drawable.ic_cctv5,
			R.drawable.ic_cctv6, R.drawable.ic_cctv7, R.drawable.ic_cctv8,
			R.drawable.ic_cctv9, R.drawable.ic_cctv10, R.drawable.ic_cctv11,
			R.drawable.ic_cctv12, R.drawable.ic_cctv13, R.drawable.ic_cctv14,
			R.drawable.ic_cctv15, R.drawable.ic_chongqing,
			R.drawable.ic_dongfang, R.drawable.ic_dongna, R.drawable.ic_gansu,
			R.drawable.ic_guangdong, R.drawable.ic_guangxi,
			R.drawable.ic_hebei, R.drawable.ic_heilongjiang,
			R.drawable.ic_henan, R.drawable.ic_jiangsu, R.drawable.ic_jiangxi,
			R.drawable.ic_jilin, R.drawable.ic_liaoning,
			R.drawable.ic_neimenggu, R.drawable.ic_ningxia,
			R.drawable.ic_qinghai, R.drawable.ic_shandong,
			R.drawable.ic_shandongjiaoyu, R.drawable.ic_shanxi_1,
			R.drawable.ic_shanxi_3, R.drawable.ic_shenzhen,
			R.drawable.ic_sichuan, R.drawable.ic_tianjing,
			R.drawable.ic_xiamen, R.drawable.ic_xinjiang, R.drawable.ic_xizang,
			R.drawable.ic_yunnan, R.drawable.ic_zhejiang,
			R.drawable.ic_zhongguojiaoyu };

	public static int[] mAddButtonImages = new int[] { R.drawable.ic_add,
			R.drawable.ic_back, R.drawable.ic_brightness_down,
			R.drawable.ic_brightness_up, R.drawable.ic_brightness,
			R.drawable.ic_computer, R.drawable.ic_cool, R.drawable.ic_dir_down,
			R.drawable.ic_dir_left, R.drawable.ic_dir_right,
			R.drawable.ic_dir_up, R.drawable.ic_down, R.drawable.ic_exit,
			R.drawable.ic_fb, R.drawable.ic_ff, R.drawable.ic_high,
			R.drawable.ic_light, R.drawable.ic_low, R.drawable.ic_media_front,
			R.drawable.ic_media_next, R.drawable.ic_mid, R.drawable.ic_mute,
			R.drawable.ic_oc, R.drawable.ic_pause, R.drawable.ic_play,
			R.drawable.ic_power, R.drawable.ic_setting, R.drawable.ic_sign,
			R.drawable.ic_sleep, R.drawable.ic_stop, R.drawable.ic_sub,
			R.drawable.ic_timer, R.drawable.ic_timer1, R.drawable.ic_timer2,
			R.drawable.ic_timer3, R.drawable.ic_timer4, R.drawable.ic_up, };
	public static int[] mWifiTypes = new int[] { ETWIFIDEVICE_TYPE };
	public static int[] mWifiDeviceImages = new int[] { R.drawable.ic_wifidevice };
	
	  public static void rprintHexString( byte[] b)
	    {  
	        for (int i = 0; i < b.length; i++)
	        {
	            String hex = Integer.toHexString(b[i] & 0xFF);
	            if (hex.length() == 1)
	            {
	                hex = '0' + hex;
	            }
	            System.out.print(hex.toUpperCase() + " ");
	        }
	        System.out.println("码库中对应的码值");
	    }


	 
}
