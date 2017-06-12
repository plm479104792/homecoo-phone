/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  This file and this file only is also released under Apache license as an API file
 */

package et.song.db;

public class DBProfile {
	public final static String DBNAME = "com.hxd.remotestat.db";

	public final static String GROUP_TABLE_NAME = "ETGroup";
	public static final String TABLE_GROUP_FIELD_ID = "id";
	public static final String TABLE_GROUP_FIELD_NAME = "group_name";
	public static final String TABLE_GROUP_FIELD_TYPE = "group_type";
	public static final String TABLE_GROUP_FIELD_RES = "group_res";
	public static final String TABLE_GROUP_FIELD_DEVID = "group_devid";    //设备id
	public static final String TABLE_GROUP_FIELD_GATEWAYID = "group_wgid";  //网关id
	
	public final static String DEVICE_TABLE_NAME = "ETDevice";
	public static final String TABLE_DEVICE_FIELD_ID = "id";
	public static final String TABLE_DEVICE_FIELD_GROUP_ID = "gid";
	public static final String TABLE_DEVICE_FIELD_NAME = "device_name";
	public static final String TABLE_DEVICE_FIELD_TYPE = "device_type";
	public static final String TABLE_DEVICE_FIELD_RES = "device_res";
//	public static final String TABLE_DEVICE_FIELD_INDEX = "device_index";
//	public static final String TABLE_DEVICE_FIELD_POS	= "device_pos";
	
	
	
	public final static String KEY_TABLE_NAME = "ETKEY";
	public static final String TABLE_KEY_FIELD_ID = "id";
	public static final String TABLE_KEY_FIELD_DEVICE_ID = "did";
	public static final String TABLE_KEY_FIELD_NAME = "key_name";
	public static final String TABLE_KEY_FIELD_RES = "key_res";
	public static final String TABLE_KEY_FIELD_X = "key_x";
	public static final String TABLE_KEY_FIELD_Y = "key_y";
	public static final String TABLE_KEY_FIELD_KEYVALUE = "key_value";
	public static final String TABLE_KEY_FIELD_KEY = "key_key";
	public static final String TABLE_KEY_FIELD_BRANDINDEX = "key_brandindex";
	public static final String TABLE_KEY_FIELD_BRANDPOS = "key_brandpos";
	public static final String TABLE_KEY_FIELD_ROW = "key_row";
	public static final String TABLE_KEY_FIELD_STATE = "key_state";
	
	
	public final static String KEYEX_TABLE_NAME = "ETKEYEX";
	public static final String TABLE_KEYEX_FIELD_ID = "id";
	public static final String TABLE_KEYEX_FIELD_DEVICE_ID = "did";
	public static final String TABLE_KEYEX_FIELD_NAME = "key_name";
	public static final String TABLE_KEYEX_FIELD_KEYVALUE = "key_value";
	public static final String TABLE_KEYEX_FIELD_KEY = "key_key";
	
	
	public final static String WIFIDEVICE_TABLE_NAME = "ETWifiDevice";
	public static final String TABLE_WIFIDEVICE_FIELD_ID = "id";
	public static final String TABLE_WIFIDEVICE_FIELD_UID = "wifidevice_uid";
	public static final String TABLE_WIFIDEVICE_FIELD_SSID = "wifidevice_ssid";
	public static final String TABLE_WIFIDEVICE_FIELD_PWD = "wifidevice_pwd";
	public static final String TABLE_WIFIDEVICE_FIELD_NAME = "wifidevice_name";
	public static final String TABLE_WIFIDEVICE_FIELD_TYPE = "wifidevice_type";
	public static final String TABLE_WIFIDEVICE_FIELD_RES =  "wifidevice_res";
	public static final String TABLE_WIFIDEVICE_FIELD_WAN =  "wifidevice_wan";
	public static final String TABLE_WIFIDEVICE_FIELD_IP  =  "wifidevice_ip";
	public static final String TABLE_WIFIDEVICE_FIELD_PORT = "wifidevice_port";
	
	
	public final static String WIFIDIRECT_TABLE_NAME = "ETWifiDirect";
	public static final String TABLE_WIFIDIRECT_FIELD_ID = "id";
	public static final String TABLE_WIFIDIRECT_FIELD_RES =  "wifidirect_res";
	public static final String TABLE_WIFIDIRECT_FIELD_IP  =  "wifidirect_ip";
	public static final String TABLE_WIFIDIRECT_FIELD_PORT = "wifidirect_port";
	
	
	//AIR
	public final static String AIRDEVICE_TABLE_NAME = "ETAirDevice";
	public static final String TABLE_AIRDEVICE_FIELD_ID = "id";
	public static final String TABLE_AIRDEVICE_FIELD_DEVICE_ID = "did";
	public static final String TABLE_AIRDEVICE_FIELD_TEMP = "air_temp";
	public static final String TABLE_AIRDEVICE_FIELD_RATE = "air_rate";
	public static final String TABLE_AIRDEVICE_FIELD_DIR =  "air_dir";
	public static final String TABLE_AIRDEVICE_FIELD_AUTO_DIR =  "air_auto_dir";
	public static final String TABLE_AIRDEVICE_FIELD_MODE  =  "air_mode";
	public static final String TABLE_AIRDEVICE_FIELD_POWER = "air_power";
	
	public final static String WATCHTV_TABLE_NAME = "WATCHTV";
	public static final String TABLE_WATCHTV_FIELD_ID = "id";
	public static final String TABLE_WATCHTV_FIELD_DEVICE_ID = "did";
	public static final String TABLE_WATCHTV_FIELD_NAME = "watchtv_name";
	public static final String TABLE_WATCHTV_FIELD_CONTEXT = "watchtv_context";
	public static final String TABLE_WATCHTV_FIELD_RES =  "watchtv_res";
	public static final String TABLE_WATCHTV_FIELD_VALUE =  "watchtv_value";
	public static final String TABLE_WATCHTV_FIELD_ISOK  =  "watchtv_ok";
	public static final String TABLE_WATCHTV_FIELD_ISSELECT  =  "watchtv_select";
	public static final String TABLE_WATCHTV_FIELD_VALUE_EX  =  "watchtv_value_ex";
}
