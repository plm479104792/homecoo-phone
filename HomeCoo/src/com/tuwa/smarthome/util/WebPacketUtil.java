package com.tuwa.smarthome.util;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.R.integer;
import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.activity.DeviceManegeActivity;
import com.tuwa.smarthome.activity.DeviceSwitchActivity;
import com.tuwa.smarthome.activity.SceneManegeActivity;
import com.tuwa.smarthome.activity.SceneSetActivity;
import com.tuwa.smarthome.activity.SpaceDevicesActivity;
import com.tuwa.smarthome.activity.SpaceManegeActivity;
import com.tuwa.smarthome.activity.TimeTaskActivity;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.GateWayDao;
import com.tuwa.smarthome.dao.SpaceDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.dao.VersionDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.Item;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.Schedule;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.ThemeData;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.User;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.entity.ZnodePacket;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.WebService;
import com.tuwa.smarthome.network.WebService.WebServiceCallBack;

public class WebPacketUtil {
	final char ON = 1 + '0'; // 字符开
	final char OFF = 0 + '0'; // 字符关


    /**
     * 网关Hex数据转换到本地数据库
     * @param devtype
     * @param b
     * @return
     */
	public static String wgDataToSqlString(short devtype, byte[] b) {
		String strhex = "";
		if (devtype ==SystemValue.DEV_TEMP_HUMI) {   //温湿度data转码
			byte[] bytetemp = new byte[2];
			byte[] bytehumi = new byte[2];
			System.arraycopy(b, 0, bytetemp, 0, 2);
			System.arraycopy(b, 2, bytehumi, 0, 2);
			int temp = DataConvertUtil.byte2int(bytetemp);
			int humi = DataConvertUtil.byte2int2(bytehumi);
			String strtemp = Integer.toString(temp);
			String strhumi = Integer.toString(humi);
			strhex = strtemp + "p" + strhumi;
		}else if(devtype ==SystemValue.DEV_PM25){
			byte[] bpm25=new byte[2];
			System.arraycopy(b, 0, bpm25, 0, 2);
			
			byte bpm25H=bpm25[0];
			byte bpm25L=bpm25[1];
			
			int ipm25h=bpm25H;
			int ipm25l=bpm25L;
			int pm25=ipm25h*10+ipm25l/10;
			
			String strtemp = Integer.toString(pm25);
			strhex = strtemp;
			System.out.println("底层反馈的pm25值为："+strtemp+"高:"+ipm25h+"低:"+ipm25l);
		} else {
			for (int i = 0; i < b.length; i++) {
				String hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.equals("64")) {
					hex = "1";
				} else if (hex.equals("00")) {
					hex = "0";
				} else if (hex.equals("32")) { // 情景学习
					hex = "2";
				}
				strhex += hex; // 除了0和64其它的原字节返回,调光灯原字节返回
			}
		}
		return strhex;
	}

	/**
	 * 数据库中数据转网关数据
	 * @param devType
	 * @param devstate
	 * @return
	 */
	public static String SqlStr2WgData(int devType,String devstate){
		   	// 添加转码后的Data
			byte[] dataByte = devstate.getBytes();
			int stateLen=dataByte.length;
			int index=0;
			byte[] soc=new byte[stateLen];
		   if (devType == NetValue.DEV_LAMP_LIGHT) { // 调光灯
				  soc[index++] = convertLampStringtoByte(devstate);
			} else { // 普通的开关插座设备
				for (int i = 0; i < dataByte.length; i++) {
					if (dataByte[i] == 0x31) { // 1的字节码为0x31转换为0x64
						soc[index++] = 0x64;
					} else if (dataByte[i] == 0x30) { // 0的字节码为0x30转换为0x00
						soc[index++] = 0x00;
					} else if (dataByte[i] == 0x32) {
						soc[index++] = 0x16; // 情景学习下发确认报文
					} else if (dataByte[i] == 0x38) { // 双控开关 themestate开为8，关为0
						soc[index++] = 0x64;
					} else {
						soc[index++] = dataByte[i]; // 特殊的字节码按原码转换
					}
				}
			}
			String strHex=DataConvertUtil.toHexUpString(soc);
			return strHex;
	 }


	/**
	 * 对象转换为字节数组
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] packetToByteStream(SocketPacket sentPackage) {
		// int dataLength = dataBytes.length; //data数据体长度
		int dataLength = sentPackage.getDataLen();
		int socketLength = 4 + 4 + 8 + 8 + 2 + 2 + 2 + dataLength; // 整个报文的长度
		byte[] soc = new byte[socketLength];
		// 添加报文头
		int index = 0;
		byte[] headerBytes = sentPackage.getHeader().getBytes();
		for (int i = 0; i < 4; i++) {
			soc[index++] = headerBytes[i];
		}

		// 添加stamp
		// byte [] stampBytes = this.stamp.getBytes();
		byte[] stampBytes = DataConvertUtil.toByteArray(sentPackage.getStamp());
		for (int i = 0; i < 4; i++) {
			soc[index++] = stampBytes[i];
		}
		// 添加GW_id
		// byte [] gwIdBytes = sentPackage.getGatewayId().getBytes();
		// //gw_id串string长度为8时使用
		byte[] gwIdBytes = DataConvertUtil.toByteArray(sentPackage
				.getGatewayId());
		for (int i = 0; i < 8; i++) {
			soc[index++] = gwIdBytes[i];
		}
		// 添加Dev_id
		// byte [] devIdBytes = this.devId.getBytes(); //dev_id串string长度为8时使用
		byte[] devIdBytes = DataConvertUtil.toByteArray(sentPackage.getDevId()); // String
														    // 转换成byte[]形式，注意这个不改变实际内容
		for (int i = 0; i < 8; i++) {
			soc[index++] = devIdBytes[i];
		}

		// 添加Dev_type,低字节在前高字节在后0x0200,手动转换
		byte[] devTypeBytes = DataConvertUtil.short2Byte(sentPackage
				.getDevType()); // devType转换为字节数组
		soc[index++] = devTypeBytes[1];
		soc[index++] = devTypeBytes[0];

		// 添加Data_type,低字节在前高字节在后0x0100
		byte[] dataTypeBytes = DataConvertUtil.short2Byte(sentPackage
				.getDataType());
		soc[index++] = dataTypeBytes[1];
		soc[index++] = dataTypeBytes[0];

		// 添加Data_len,高字节在前低字节在后0x0008
		byte[] datalenBytes = DataConvertUtil.short2Byte(sentPackage.getDataLen());
		System.out.println("====报文长度:===="+sentPackage.getDataLen());
		DataConvertUtil.tprintHexString(datalenBytes); // 发送的数据打印
		for (int i = 0; i < 2; i++) {
			soc[index++] = datalenBytes[i];
		}

		short dataType = sentPackage.getDataType();
		
		//============== 向网关设置情景联动信息==============
		if (dataType == NetValue.DATA_SET_SCENE) { 
			ThemeData themedata = (ThemeData) sentPackage.getData();
			byte[] data = sceneSetPacketToByteStream(themedata, dataLength);   //情景设置themedata填充为标准的字节报文格式
			for (int i = 0; i < data.length; i++) {
				soc[index++] = data[i];
			}
		//==============向网关请求所有情景=================
		}else if(dataType == NetValue.DATA_GET_SCENE){
			byte[] data=DataConvertUtil.toByteArray((String)sentPackage.getData());
//			System.out.println("请求所有情景"+(String)sentPackage.getData());
			for (int i = 0; i < data.length; i++) {
				soc[index++] = data[i];
			}
		}
		//===============情景控制data填充==============
		else if(dataType == NetValue.DATA_SCENE_CTRL){ 
			ThemeData themedata = (ThemeData) sentPackage.getData();
			byte[] data = sceneControl2ByteStream(themedata, dataLength);  //情景控制themedata填充为标准的字节报文格式
			for (int i = 0; i < data.length; i++) {
				soc[index++] = data[i];
			}
		}
		//==============情景删除data填充================
		else if(dataType == NetValue.DATA_DELETE_SCENE){
			//填充themeNo
			String delThemeData=(String) sentPackage.getData();
			String themeNo=delThemeData.substring(0, 16);
			byte[] bthemeNo = DataConvertUtil.toByteArray(themeNo);
			for (int i = 0; i < 8; i++) {
				soc[index++] = bthemeNo[i];
			}
			
			// 填充themestate
			String themeState=delThemeData.substring(16, 23);
			byte[] bthemeState = themeState.getBytes();
			for (int i = 0; i < bthemeState.length; i++) {
				if (bthemeState[i] == 0x31) { // 1的字节码为0x31转换为0x64
					soc[index++] = 0x64;
				} else if (bthemeState[i] == 0x30) { // 0的字节码为0x30转换为0x00
					soc[index++] = 0x00;
				} 
			}
			
		}
		//===============网关wifi桥接==============
		else if(dataType == NetValue.DATA_SET_STA ||dataType == NetValue.DATA_SET_AP ){
			String wifidata=(String) sentPackage.getData();
			System.out.println(">>>wifi账号"+wifidata);
			String[] strwifi = wifidata.split(",");
			String ssid=strwifi[0];
			String pwd=strwifi[1];
			byte[] bssid=ssid.getBytes();
			byte[] bpwd=pwd.getBytes();
		
			int ssidlen=ssid.length();
			int pwdlen=pwd.length();
			byte[] bssidlen=DataConvertUtil.intToByte(ssidlen);
			byte[] bpwdlen=DataConvertUtil.intToByte(pwdlen);
			
			soc[index++]= bssidlen[0];  //wifi账号填充
			for(int i=0;i<ssidlen;i++){
				soc[index++]=bssid[i];
			}
			int ssidLeft = 32 - ssidlen; //账号名称不够32个字节补全
			index = index + ssidLeft;
			
			soc[index++]=bpwdlen[0];   //wifi密码填充
			for(int i=0;i<pwdlen;i++){
				soc[index++]=bpwd[i];
			}
			int pwdLeft = 32 - pwdlen; //账号名称不够32个字节补全
			index = index + pwdLeft;
			
		}
		//===============普通设备控制data填充==============
		else {
			// 添加转码后的Data
			byte[] dataByte = ((String) sentPackage.getData()).trim().getBytes();

			short devType = sentPackage.getDevType();
			if (devType == NetValue.DEV_LOCAL_PHONE) { // 手机端认证
				for (int i = 0; i < dataByte.length; i++) {
					soc[index++] = dataByte[i];
				}
			} else if (devType == NetValue.DEV_LAMP_LIGHT) { // 调光灯
				String data = (String) sentPackage.getData();
				soc[index++] = convertLampStringtoByte(data);
			} else if (devType == NetValue.DEV_INFRARE) { // 红外遥控
				String data = (String) sentPackage.getData();
				byte[] infrareBytes = DataConvertUtil.toByteArray(data);
				int length = sentPackage.getDataLen();
				for (int i = 0; i < length; i++) {
					soc[index++] = infrareBytes[i];
				}
			} else { // 普通的开关插座设备
				for (int i = 0; i < dataByte.length; i++) {
					if (dataByte[i] == 0x31) { // 1的字节码为0x31转换为0x64
						soc[index++] = 0x64;
					} else if (dataByte[i] == 0x30) { // 0的字节码为0x30转换为0x00
						soc[index++] = 0x00;
					} else if (dataByte[i] == 0x32) {
						soc[index++] = 0x16; // 情景学习下发确认报文
					} else if (dataByte[i] == 0x38) { // 双控开关 themestate开为8，关为0
						soc[index++] = 0x64;
					} else {
						soc[index++] = dataByte[i]; // 特殊的字节码按原码转换
					}
				}
			}
		}

//		DataConvertUtil.tprintHexString(soc); // 发送的数据打印
		return soc;
	}

	// 调光灯string转byte,1转换为01
	private static byte convertLampStringtoByte(String data) {
		// TODO Auto-generated method stub
		if (data.equals("1")) {
			return 0x01;
		} else if (data.equals("2")) {
			return 0x02;
		} else if (data.equals("3")) {
			return 0x03;
		} else if (data.equals("4")) {
			return 0x04;
		} else if (data.equals("5")) {
			return 0x05;
		} else if (data.equals("6")) {
			return 0x06;
		} else if (data.equals("7")) {
			return 0x07;
		} else if (data.equals("8")) {
			return 0x08;
		} else if (data.equals("9")) {
			return 0x09;
		}
		return 0x00;
	}

	/**
	 * 字节数组转为对象
	 * 
	 * @param bytearr
	 * @return
	 */
	public static SocketPacket byteToSocketPacket(byte[] bytearr) {
	 SocketPacket recivePkt = null;
	  try {
		int index = 0;
		byte[] header = new byte[4]; // 报头4字节
		System.arraycopy(bytearr, index, header, 0, 4);
//		String strHeader = DataConvertUtil.hexToString(header); // 将字节的时间戳以字符串显示
		String strHeader = DataConvertUtil.toHexUpString(header); // 将字节的时间戳以字符串显示
		index += 4;
		byte[] stamp = new byte[4]; // 4位长度
		System.arraycopy(bytearr, index, stamp, 0, 4);
		String strStamp = DataConvertUtil.toHexUpString(stamp); // 将字节的时间戳以字符串显示
																// strStamp:00000000
		index += 4;
		byte[] gwId = new byte[8]; // 8位长度
		System.arraycopy(bytearr, index, gwId, 0, 8);
		String strgwId = DataConvertUtil.toHexUpString(gwId); // 将字节的网关id以字符串显示
		index += 8;
		byte[] devId = new byte[8]; // 8位长度
		System.arraycopy(bytearr, index, devId, 0, 8);
		String strdevId = DataConvertUtil.toHexUpString(devId); // 将字节的devid以字符串显示
		index += 8;
		byte[] devType = new byte[2]; // 2位长度
		System.arraycopy(bytearr, index, devType, 0, 2);
		short sdevType = DataConvertUtil.byteLH2Short(devType);// devType字节转换为short,屏蔽低字节在前高字节在后
		index += 2;
		byte[] dataType = new byte[2]; // 2位长度
		System.arraycopy(bytearr, index, dataType, 0, 2);
		short sdataType = DataConvertUtil.byteLH2Short(dataType);// data字节转换为short,屏蔽低字节在前高字节在后
		index += 2;
		byte[] dataLen = new byte[2]; // 2位长度
		System.arraycopy(bytearr, index, dataLen, 0, 2);
		short sdataLen = DataConvertUtil.byte2Short(dataLen); // dataLen字节转换为short
		index += 2;
		byte[] data = new byte[sdataLen];
		System.arraycopy(bytearr, index, data, 0, sdataLen);

	
			//==============网关返回情景设置信息================
		  if((sdataType == NetValue.DATA_SCENE)||(sdataType == NetValue.DATA_SET_SCENE)){
				//先清空网关本地的情景联动的设置
			  if(SystemValue.themeClean==true){ //第一次同步情景时清空本地情景及其设置
				  new ThemeDao(null).deleteAllByGatewayNo(SystemValue.gatewayid);		
				  new ThemeDeviceDao(null).deleteAllByGatewayNo(SystemValue.gatewayid);
				  SystemValue.themeClean=false;
			  }
				
				byte[] bThemeNo = new byte[8];  
				System.arraycopy(data, 0, bThemeNo, 0, 8);
				String themeNo=DataConvertUtil.toHexUpString(bThemeNo);  //themeNo中的字母大写
				
				byte[] bThemeType = new byte[4];
				System.arraycopy(data, 24, bThemeType, 0, 4);
				int themeType = DataConvertUtil.bytesToInt(bThemeType);
				
				byte[] bThemeState = new byte[8];   //情景开关状态       
				System.arraycopy(data, 8, bThemeState, 0, 8);
				short themeDevType=113;
				String themeStateAll=wgDataToSqlString(themeDevType,bThemeState); 
				String themeState=themeStateAll;
//				if(themeType==SystemValue.SCENE_HARD){  //硬件情景开关
//					 themeState=themeStateAll.substring(0, 4);
//				}else if(themeType==SystemValue.SCENE_DOUBLE){
//					 themeState=themeStateAll.substring(0, 1);
//				}else if(themeType==SystemValue.SCENE_TRIGGER){
//					themeState=themeStateAll.substring(0, 2);
//				}
				
				byte[] bDeviceNo = new byte[8];          //deviceNo中字母小写
				System.arraycopy(data, 16, bDeviceNo, 0, 8);
				String deviceNo=DataConvertUtil.toHexUpString(bDeviceNo);  
				
				byte[] bScenamelength = new byte[4];   //name的前4个字节为scenename的长度
				System.arraycopy(data, 28, bScenamelength, 0, 4);
				int length = DataConvertUtil.bytesToInt(bScenamelength);
				byte[] bSceneName = new byte[length];
				System.arraycopy(data, 32, bSceneName, 0, length);
				
				String strHexChine=new String(bSceneName);
				String sceneName=DataConvertUtil.stringToChinese(strHexChine);
				
				Theme theme=new Theme();
				theme.setThemeNo(themeNo);
				theme.setThemeName(sceneName);
				theme.setThemeType(themeType);
				theme.setDeviceNo(deviceNo);
				theme.setGatewayNo(SystemValue.gatewayid);
				theme.setThemeState(themeState);
				
//				System.out.println("===网关返回的情景==="+theme.toString());
				//添加或者更新theme到本地数据库
				new ThemeDao(null).addOrUpdateTheme(theme);	   //20160525临时屏蔽网关存储红外联动错误
				
				//=========trigger_num============
				byte[] bTri_num = new byte[4];   
				System.arraycopy(data, 88, bTri_num, 0, 4);
				int triggerNum = DataConvertUtil.bytesToInt(bTri_num);
				//========联动设备数量linkDevice_num=======
				byte[] bLinkDev_num = new byte[4];   
				System.arraycopy(data, 92, bLinkDev_num, 0, 4);
				int linkDevNum = DataConvertUtil.bytesToInt(bLinkDev_num);
				//=========安防设备的信息============
				int k=144;  //120
				if(themeType!=4){  //20160904
					byte[] bTriggerItem = new byte[48];
					System.arraycopy(data, 96, bTriggerItem, 0, 48);
				}else{
					//自定义情景无需填充触发器信息
					k=96;
				}
				
				//=========联动设备的信息============
				
				for(int j=0;j<linkDevNum;j++){
					//=========联动deviceNo============
					byte[] bLinkDevNo = new byte[8];  //触发器deviceNo
					System.arraycopy(data,k, bLinkDevNo, 0, 8);
					k=k+8;
					String linkDevNo=DataConvertUtil.toHexUpString(bLinkDevNo);
					//=========联动deviceStateAll============
					byte[] bLinkDevStateAll = new byte[32];  //触发器deviceState
					System.arraycopy(data,k, bLinkDevStateAll, 0, 32);
					k=k+32;
					String linkDevState=DataConvertUtil.toHexUpString(bLinkDevStateAll);
					//=========联动stateLen============
					byte[] bDevStateLen = new byte[4];   
					System.arraycopy(data, k, bDevStateLen, 0, 4);
					k=k+4; 
					int devStateLen = DataConvertUtil.bytesToInt(bDevStateLen);
					if(devStateLen > 32){
						return null;
					}
					System.out.println("WebPacketUtil,devStateLen转换后的长度为"+devStateLen);
					DataConvertUtil.rprintHexString(bDevStateLen);  //接收的数据打印
					
					//=========联动devType============
					byte[] bDevlinkType = new byte[4];   
					System.arraycopy(data, k, bDevlinkType, 0, 4);
					k=k+4; 
					short devlinkType = (short)DataConvertUtil.bytesToInt(bDevlinkType);
					
					//=========联动deviceState============
					byte[] bLinkDevState = new byte[devStateLen];  //触发器deviceState
					System.arraycopy(bLinkDevStateAll,0, bLinkDevState, 0, devStateLen);
					String devLinkState = wgDataToSqlString(devlinkType, bLinkDevState);
					
					ThemeDevice themeDevice=new ThemeDevice();
					themeDevice.setThemeNo(themeNo);
					themeDevice.setThemeDeviceNo(deviceNo); //实体情景设备识别码
					themeDevice.setThemeType(themeType);
					themeDevice.setThemeState(themeState);
					themeDevice.setDeviceNo(linkDevNo);
					themeDevice.setDeviceStateCmd(devLinkState);
					themeDevice.setGatewayNo(SystemValue.gatewayid);
					
//					System.out.println("******联动的设备信息******"+themeDevice.toString());
					
					if(devlinkType==SystemValue.DEV_INFRA_CONTROL){
						boolean infraFlg=new ThemeDeviceDao(null).isEsistInfraThemestate(themeDevice);
						if(!infraFlg){
							new ThemeDeviceDao(null).addInfraThemeDevice(themeDevice);
						}
					}else{
						//添加或者更新情景联动到本地数据库
						new ThemeDeviceDao(null).addOrUpdate(themeDevice);  //20160525临时屏蔽网关存储红外联动错误
					}
				}
				
				recivePkt = new SocketPacket();
				recivePkt.setHeader(strHeader);
				recivePkt.setStamp(strStamp);
				recivePkt.setGatewayId(SystemValue.gatewayid); // 屏蔽网关上报报文中gwid全为"000000000"
				recivePkt.setDevId(strdevId);
				recivePkt.setDevType(sdevType);
				recivePkt.setDataType(sdataType);
				recivePkt.setDataLen(sdataLen);
			}
			//==============网关上报设备状态信息================
			else {
				String strdata = wgDataToSqlString(sdevType, data);
				recivePkt = new SocketPacket();

				recivePkt.setHeader(strHeader);
				recivePkt.setStamp(strStamp);
				recivePkt.setGatewayId(SystemValue.gatewayid); // 屏蔽网关上报报文中gwid全为"000000000"
				recivePkt.setDevId(strdevId);
				recivePkt.setDevType(sdevType);
				recivePkt.setDataType(sdataType);
				recivePkt.setDataLen(sdataLen);
				recivePkt.setData(strdata);

				// 【后台】添加或者更新本地数据库devdto的状态
			    addOrUpdateDeviceToLocalSql(recivePkt);
			}
		} catch (Exception e) {
			System.out.println("捕获到报文解析异常...");
			e.printStackTrace();
		}
		return recivePkt;
	}



	/**
	 * 解析服务器返回的SpaceList
	 * 
	 * @param strSitelist
	 * @return
	 */
	public static List<Space> parseSpacelistFromServer(String strSitelist) {
		
		List<Space> spacelist =JSONArray.parseArray(strSitelist, Space.class);
		for (int i = 0; i < spacelist.size(); i++) {
			Space space = spacelist.get(i);

			if (new SpaceDao(null).isUseableSpace(space.getSpaceNo())) {
				new SpaceDao(null).add(space); // 新添space到数据库
//				System.out.println("插入的房间编号"+i);
			} 
		}
		return spacelist;
	}
	
	/**
	 * 解析服务器返回的设备定时列表
	 */
	public static List<Schedule> parseScheduleListFromServer(String strScheduleList) {
		
		List<Schedule> schedulelist =JSONArray.parseArray(strScheduleList, Schedule.class);
		for (int i = 0; i < schedulelist.size(); i++) {
			Schedule schedule = schedulelist.get(i);

//			if (new SpaceDao(null).isUseableSpace(space.getSpaceNo())) {
//				new SpaceDao(null).add(space); // 新添space到数据库
//			} else {
//			}
		}
		return schedulelist;
	}
	
	/**
	 * 解析服务器返回devicelist
	 * @param strDevicelist
	 * @return
	 */
	public static List<Device> parseDevicelistFromServer(String strDevicelist) {
		List<Device> devicelist =JSONArray.parseArray(strDevicelist, Device.class);
		try {
			for (int i = 0; i < devicelist.size(); i++) {
				Device device = devicelist.get(i);
			    String deviceNo=device.getDeviceNo();
			    String phonenum=device.getPhoneNum();
			    String deviceType=String.valueOf(device.getDeviceTypeId());
			    Short sDeviceType=new Short(deviceType);
			    
			    String stateHex=device.getDeviceStateCmd();
			    if(!VerifyUtils.isEmpty(stateHex)){
				   byte[] bState= DataConvertUtil.toByteArray(stateHex);
			       String stateSql=wgDataToSqlString(sDeviceType,bState);
			       device.setDeviceStateCmd(stateSql);
			          
				   if (new DevdtoDao(null).isNewDevByDeviceNo(deviceNo,phonenum)) {
						new DevdtoDao(null).add(device);   
				   }
			    }
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return devicelist;
	}
	
	
	public static List<Device> parseDevListToServer(List<Device> devicelist) {
		List<Device> sDevList = new ArrayList<Device>();
		try {
			for (int i = 0; i < devicelist.size(); i++) {
				Device device = devicelist.get(i);
				int devType=device.getDeviceTypeId();
				String devState=device.getDeviceStateCmd();
				if(!VerifyUtils.isEmpty(devState)){
					String stateStr=SqlStr2WgData(devType,devState);
					device.setDeviceStateCmd(stateStr);
					sDevList.add(device);	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sDevList;
	}
	
	/**
	 * 解析服务器返回的设备配置表
	 * @param strUserDevicelist
	 * @return
	 */
	public static List<UserSpaceDevice> parseUserDevicelistFromServer(String strUserDevicelist) {
		List<UserSpaceDevice> UserDevicelist =JSONArray.parseArray(strUserDevicelist, UserSpaceDevice.class);
		for (int i = 0; i < UserDevicelist.size(); i++) {
			UserSpaceDevice userSpace = UserDevicelist.get(i);
			new UserSpaceDevDao(null).addorUpdate(userSpace);   
		}
		return UserDevicelist;
	}
	
	/**
	 * 解析服务器返回gatewaylist
	 * @param strDevicelist
	 * @return
	 */
	public static List<Gateway> parseGatewaylistFromServer(String strlist) {
		List<Gateway> gatewaylist =JSONArray.parseArray(strlist, Gateway.class);
		for (int i = 0; i < gatewaylist.size(); i++) {
			Gateway gateway = gatewaylist.get(i);
            String gatewayNo=gateway.getGatewayNo();
            Gateway mgateway=new GateWayDao(null).getGatewayByGatewayNo(gatewayNo);
            if(mgateway==null){
            	new GateWayDao(null).add(gateway);
            }else{
            	new GateWayDao(null).updateGateWayByGatewayNo(gateway);
            }
		}
		return gatewaylist;
	}
	

	/**
	 * 解析服务器返回的情景联动表
	 * @param strThemeDevicelist
	 * @return
	 */
	public static List<ThemeDevice> parseThemeDevicelistFromServer(String strThemeDevicelist) {
		
		List<ThemeDevice> themeDevicelist =JSONArray.parseArray(strThemeDevicelist, ThemeDevice.class);
		for (int i = 0; i < themeDevicelist.size(); i++) {
			ThemeDevice themeDevice = themeDevicelist.get(i);
			new ThemeDeviceDao(null).addOrUpdate(themeDevice);
		}
		return themeDevicelist;
	}
	
	
	/**
	 * 解析从服务器返回的themelist
	 * @param strThemelist
	 * @return
	 */
	public static List<Theme> parseThemelistFromServer(String strThemelist) {
		List<Theme> themelist =JSONArray.parseArray(strThemelist, Theme.class);
		for (int i = 0; i < themelist.size(); i++) {
			Theme theme = themelist.get(i);
            String themeNo=theme.getThemeNo();
//			if (new ThemeDao(null).isUseableTheme(themeNo)) {
				new ThemeDao(null).addOrUpdateTheme(theme);   //添加theme到本地数据库
//			}else {
//			}
		}
		return themelist;
	}
	
	
	
	
	
	/************* 从外网获取空间列表 ****************/
	public static List<Space> findSpaceFromLocalByWgidAndPhonenum() {
		List<Space> spacelist = new ArrayList<Space>();
		// 根据网关号从数据库加载空间列表
		String gatewayId = SystemValue.gatewayid;
		String phonenum = SystemValue.phonenum;
		spacelist = new SpaceDao(null).getSpaceByPhonenum(phonenum);
		return spacelist;
	}

	/**
	 * DevDTO对象转换为SocketPacket报文
	 * 
	 * @param device
	 */
	public static SocketPacket devConvertToPacket(Device device) {
		SocketPacket devPacket = new SocketPacket();
	
		if(device!=null){
			int devtype = device.getDeviceTypeId();
			int datalen = device.getDeviceStateCmd().length(); // 除devtype为1,8,6外，直接根据命令字符串填充长度
		
			devPacket.setHeader(NetValue.header);
			devPacket.setStamp("30303030");
			devPacket.setGatewayId(device.getGatewayNo());
			devPacket.setDevId(device.getDeviceNo());
			devPacket.setDevType((short) devtype);
			devPacket.setDataType(NetValue.localDataControl);
			devPacket.setDataLen((short) datalen);
			devPacket.setData(device.getDeviceStateCmd());
		}
		return devPacket;
	}
	
	/**
	 * 封装情景请求报文，从网关请求所有情景报文
	 * 
	 * @return
	 */
	public static SocketPacket getThemeAllPacket() {
		SocketPacket socketPacket = new SocketPacket();
		socketPacket.setHeader(NetValue.header);
		socketPacket.setStamp(NetValue.stamp);
		socketPacket.setGatewayId(SystemValue.gatewayid);
		socketPacket.setDevId(NetValue.DEVID_NULL);
		socketPacket.setDevType(NetValue.DEV_LOCAL_PHONE);
		socketPacket.setDataType(NetValue.DATA_GET_SCENE);
		socketPacket.setDataLen((short)16);
		socketPacket.setData("01000000000000000000000000000000");
		return socketPacket;
	}

	/**
	 * 完成情景设置，让网关保存报文
	 * 
	 * @return
	 */
	public static SocketPacket finnishThemeSetPacket() {
		SocketPacket socketPacket = new SocketPacket();
		socketPacket.setHeader(NetValue.header);
		socketPacket.setStamp(NetValue.stamp);
		socketPacket.setGatewayId(SystemValue.gatewayid);
		socketPacket.setDevId(NetValue.DEVID_NULL);
		socketPacket.setDevType(NetValue.DEV_LOCAL_PHONE);
		socketPacket.setDataType(NetValue.DATA_FINISH_SCENE);
		socketPacket.setDataLen((short)1);
		socketPacket.setData("0");
		return socketPacket;
	}

	/**
	 * 删除情景设置报文
	 * @return
	 */
	public static SocketPacket deleteThemeSetPacket() {
		SocketPacket socketPacket = new SocketPacket();
		socketPacket.setHeader(NetValue.header);
		socketPacket.setStamp(NetValue.stamp);
		socketPacket.setGatewayId(SystemValue.gatewayid);
		socketPacket.setDevId(NetValue.DEVID_NULL);
		socketPacket.setDevType(NetValue.DEV_LOCAL_PHONE);
		socketPacket.setDataType(NetValue.DATA_DELETE_SCENE);
		socketPacket.setDataLen((short)16);
		socketPacket.setData("0");
		return socketPacket;
	}
	/**
	 * 将情景转换为报文
	 * 
	 * @param device
	 * @return
	 */
	public static SocketPacket sceneSet2Packet(ThemeData themedata) {

		int triggerNum = themedata.getTriggerNum();
		int deviceNum = themedata.getDeviceNum() + triggerNum;  //触发器和联动的设备的总数量
		int datalen = 8 + 8 + 8 + 4 + 60 + 4 + 4 + deviceNum * 48; // 仅仅需要算成data的长度即themedata的长度

		SocketPacket socketPacket = new SocketPacket();
		socketPacket.setHeader(NetValue.header);
		socketPacket.setStamp("30303030");
		socketPacket.setGatewayId(SystemValue.gatewayid);
		socketPacket.setDevId(NetValue.DEVID_NULL);
		socketPacket.setDevType((short) 1);
		socketPacket.setDataType(NetValue.DATA_SET_SCENE);
		socketPacket.setDataLen((short) datalen);
		socketPacket.setData(themedata);

		return socketPacket;
	}
	
	/**
	 * 将情景控制转换为报文
	 * 
	 * @param device
	 * @return
	 */
	public static SocketPacket sceneControl2Packet(ThemeData themedata) {

		int datalen = 8 +8 ; // 情景的major+minor长度

		SocketPacket socketPacket = new SocketPacket();
		socketPacket.setHeader(NetValue.header);
		socketPacket.setStamp("30303030");
		socketPacket.setGatewayId(SystemValue.gatewayid);
		socketPacket.setDevId(NetValue.DEVID_NULL);
		socketPacket.setDevType((short) 1);
		socketPacket.setDataType(NetValue.DATA_SCENE_CTRL);
		socketPacket.setDataLen((short) datalen);   //先填充为标准报文的长度
		socketPacket.setData(themedata);

		return socketPacket;
	}

	// 封装红外报文
	public static SocketPacket infraredConverToPacket(String str) {
		int datalen = str.length() / 2;

		SocketPacket socketPacket = new SocketPacket();
		socketPacket.setHeader(NetValue.header);
		socketPacket.setStamp(NetValue.stamp);
		socketPacket.setGatewayId(SystemValue.gatewayid);
		socketPacket.setDevId(NetValue.DEVID_INFRA);
		socketPacket.setDevType(NetValue.DEV_INFRARE);
		socketPacket.setDataType(NetValue.localDataControl);
		socketPacket.setDataLen((short) datalen);
		socketPacket.setData(str);

		return socketPacket;
	}

	/**
	 * 封装socket请求报文
	 * 
	 * @param gatewayId
	 * @return
	 */
	public static SocketPacket getAuthenticPacket() {
		SocketPacket authPacket = new SocketPacket();
		authPacket.setHeader(NetValue.header);
		authPacket.setStamp(NetValue.stamp);
		authPacket.setGatewayId(SystemValue.gatewayid);
		authPacket.setDevId(NetValue.DEVID_NULL);
		authPacket.setDevType(NetValue.DEV_LOCAL_PHONE);
		authPacket.setDataType(NetValue.localAuthDatareq);
		authPacket.setDataLen((short) 8);
		authPacket.setData(NetValue.authdata);
		return authPacket;
	}

	// 封装情景学习应答报文
	public static SocketPacket getThemeStuAskPacket(SocketPacket sp) {
		SocketPacket authPacket = new SocketPacket();
		authPacket.setHeader(NetValue.header);
		authPacket.setStamp(NetValue.stamp);
		authPacket.setGatewayId(sp.getGatewayId());
		authPacket.setDevId(sp.getDevId());
		authPacket.setDevType(sp.getDevType());
		authPacket.setDataType(NetValue.localDataControl);
		authPacket.setDataLen((short) 4);
		authPacket.setData((String) sp.getData());
		return authPacket;
	}

	// 封装安防设备撤布防报文
	public static SocketPacket getCancelAlertPacket(SocketPacket sp) {
		SocketPacket authPacket = new SocketPacket();
		authPacket.setHeader(NetValue.header);
		authPacket.setStamp(NetValue.stamp);
		authPacket.setGatewayId(sp.getGatewayId());
		authPacket.setDevId(sp.getDevId());
		authPacket.setDevType(sp.getDevType());
		authPacket.setDataType(NetValue.localDataControl);
		authPacket.setDataLen((short) 2);
		authPacket.setData((String) sp.getData());
		return authPacket;
	}

	/**
	 * 修改密码报文
	 * 
	 * @param wgid
	 * @param pwd
	 * @return
	 */
	public static SocketPacket updatePasswordPacket(String wgid, String resetpwd) {

		SocketPacket authPacket = new SocketPacket();
		authPacket.setHeader(NetValue.header);
		authPacket.setStamp(NetValue.stamp);
		authPacket.setGatewayId(wgid);
		authPacket.setDevId(NetValue.DEVID_NULL);
		authPacket.setDevType(NetValue.DEV_LOCAL_PHONE);
		authPacket.setDataType(NetValue.Data_UPDATE_PWD);
		authPacket.setDataLen((short) 16);
		authPacket.setData(resetpwd);
		return authPacket;
	}

	/**
	 * 从网关获取风扇状态
	 * 
	 * @param gatewayId
	 * @return
	 */
	public static SocketPacket getDevFanStatePacket(String gatewayId) {
		SocketPacket authPacket = new SocketPacket();
		authPacket.setHeader(NetValue.header);
		authPacket.setStamp(NetValue.stamp);
		authPacket.setGatewayId(gatewayId);
		authPacket.setDevId(NetValue.DEVID_NULL);
		authPacket.setDevType(NetValue.DEV_FAN);
		authPacket.setDataType(NetValue.localDevDatareq);
		authPacket.setDataLen((short) 1);
		authPacket.setData("0");
		return authPacket;
	}
	
	/**
	 * 网关无线桥接报文
	 * @param gatewayId
	 * @return
	 */
	public static SocketPacket getWifiSetPacket() {

		String wifiData=SystemValue.SSID_NAME+","+SystemValue.SSID_PWD;
		SocketPacket authPacket = new SocketPacket();
		authPacket.setHeader(NetValue.header);
		authPacket.setStamp(NetValue.stamp);
		authPacket.setGatewayId(SystemValue.gatewayid);
		authPacket.setDevId(NetValue.DEVID_NULL);
		authPacket.setDevType(NetValue.DEV_LOCAL_PHONE);
		authPacket.setDataType(NetValue.DATA_SET_STA);
		authPacket.setDataLen((short)66);
		authPacket.setData(wifiData);
		return authPacket;
	}
	
	/**
	 * 设置网关无线
	 * @param gatewayId
	 * @param ssid
	 * @param pwd
	 * @return
	 */
	public static SocketPacket setGatewayWifiPacket() {

		String wifiData=SystemValue.SSID_NAME+","+SystemValue.SSID_PWD;
		SocketPacket authPacket = new SocketPacket();
		authPacket.setHeader(NetValue.header);
		authPacket.setStamp(NetValue.stamp);
		authPacket.setGatewayId(SystemValue.gatewayid);
		authPacket.setDevId(NetValue.DEVID_NULL);
		authPacket.setDevType(NetValue.DEV_LOCAL_PHONE);
		authPacket.setDataType(NetValue.DATA_SET_AP);
		authPacket.setDataLen((short)66);
		authPacket.setData(wifiData);
		return authPacket;
	}

	/**
	 * 从网关获取所有设备状态
	 * 
	 * @param gatewayId
	 * @return
	 */
	public static SocketPacket getDevAllStatePacket(String gatewayId) {
		SocketPacket authPacket = new SocketPacket();
		authPacket.setHeader(NetValue.header);
		authPacket.setStamp(NetValue.stamp);
		authPacket.setGatewayId(gatewayId);
		authPacket.setDevId(NetValue.DEVID_NULL);
		authPacket.setDevType(NetValue.DEV_LOCAL_PHONE);
		authPacket.setDataType(NetValue.localDevDatareq);
		authPacket.setDataLen((short) 1);
		authPacket.setData("0");
		return authPacket;
	}

	/**
	 * 封装定时同步报文
	 * 
	 * @param gatewayId
	 * @return
	 */
	public static SocketPacket getSynchroPacket() {
		SocketPacket synPacket = new SocketPacket();
		synPacket.setHeader(NetValue.header);
		synPacket.setStamp(NetValue.stamp);
		synPacket.setGatewayId(SystemValue.gatewayid);
		synPacket.setDevId(NetValue.DEVID_NULL);
		synPacket.setDevType(NetValue.DEV_LOCAL_PHONE);
		synPacket.setDataType((short) NetValue.SOCKET_PULSE);
		synPacket.setDataLen((short) 8);
		synPacket.setData(NetValue.authdata);
		return synPacket;
	}

	/**
	 * 内网更新设备状态表 添加或者更新本地数据库devdto的状态
	 * 
	 * @param tranObject
	 */
	public static void addOrUpdateDeviceToLocalSql(SocketPacket socketPacket) {
		String devid = socketPacket.getDevId();
		String gatewayid = SystemValue.gatewayid;
		short datatype = socketPacket.getDataType();
		short devtype = socketPacket.getDevType();
		switch (datatype) {
		case 1: // 屏蔽登录反馈的datatype类型
			Device device = new DevdtoDao(null).findDevByDeviceNoAndGatewayNo(devid,gatewayid);
			if (device == null) {
				int devwideType;
				Device devDTO = new Device();
				devDTO.setGatewayNo(socketPacket.getGatewayId());
				devDTO.setDeviceNo(socketPacket.getDevId());
				devDTO.setDeviceTypeId((socketPacket.getDevType()).intValue());
				devDTO.setDeviceStateCmd((String) socketPacket.getData());
				devwideType = findWidetypeByDevtype(socketPacket.getDevType());
				devDTO.setDeviceCategoryId(devwideType);
				String devname = initDevtypeNameByDevtype(socketPacket.getDevType());
				devDTO.setDeviceName(devname);
				devDTO.setSpaceNo("0"); // 默认spaceid为0，房间名称为位置待定
				devDTO.setSpaceTypeId(0); // 默认防区类型为未选择
				devDTO.setPhoneNum(SystemValue.phonenum);

				new DevdtoDao(null).add(devDTO); // 添加新devdto到本地数据
				
			} else {
				device.setDeviceStateCmd((String) socketPacket.getData()); // 从网关更新本地设备状态

				new DevdtoDao(null).updateDevStateByDeviceNo(device); // 更新设备的最新状态到本地数据库

			}
			break;
		case 12: // 删除设备
			 new DevdtoDao(null).deleteByDevid(devid);
			 if ((devtype==202)||(devtype==204)||(devtype==110)||(devtype==113)||(devtype==115)) {
					new ThemeDao(null)
					          .deleteThemeByDeviceNo(devid); // 删除与硬件相关的情景
		         	new ThemeDeviceDao(null)
					          .deleteThemeDeviceByThemeDeviceNo(devid); // 删除与硬件相关的情景
			 } 

			break;

		}
	}



	/**
	 * 从所有设备列表中查找房间内开关，窗帘，插座显示设备
	 * 
	 * @param devslist
	 * @return
	 */
	public static List<Device> findSpaceDevicesFromDevicesAll(List<UserSpaceDevice>  userSpaceList) {
		
		List<Device> devlist = new ArrayList<Device>();
		if(userSpaceList!=null){
			for (int i = 0; i < userSpaceList.size(); i++) {
				UserSpaceDevice userSpaceDev=userSpaceList.get(i);
				String sDeviceNo=userSpaceDev.getDeviceNo();
				Device devdto = new DevdtoDao(null).findDevByDeviceNoAndGatewayNo(sDeviceNo, SystemValue.gatewayid);
			    if(devdto!=null){
			    	int widetype = devdto.getDeviceCategoryId();
					if ((widetype == 1) || (widetype == 3) || (widetype == 5)) {
						String sdevName=userSpaceDev.getDeviceName();
						devdto.setDeviceName(sdevName);
						devlist.add(devdto);
					}
			    }
			}
		}
		return devlist;
	}
	
	/**
	 * 微控设备的联动里面，屏蔽风扇，温湿度，PM25
	 * @param devslist
	 * @return
	 */
	public static List<Device> deleteFansFromDeviceSensor(List<Device> devslist) {
		List<Device> devlist = new ArrayList<Device>();
		for (int i = 0; i < devslist.size(); i++) {
			Device devdto = devslist.get(i);
			int deviceType = devdto.getDeviceTypeId();
			if ((deviceType ==SystemValue.DEV_TEMP_HUMI)
					|| (deviceType == SystemValue.DEV_PM25)
					   || (deviceType == SystemValue.DEV_FANS)) {
				
			}else{
				devlist.add(devdto);
			}
		}
		return devlist;
	}

	/**
	 * 根据phoneSpaceId查找spacename
	 * 
	 * @param phoneSpaceId
	 * @return
	 */
	public static String getSpaceName(String spaceNo) {
		String spacename = "位置待定";
		if (spaceNo.equals("0")) { // 初始的设备的位置待定
			spacename = "位置待定";
		} else {
			spacename = new SpaceDao(null).getSpacenameBySpaceNo(spaceNo);
			
		}
		return spacename;
	}

	/**
	 * 去除温湿度，PM25
	 * 
	 * @param devslist
	 * @return
	 */
	public static List<Device> DeleteSensorFromDevicesAll(List<Device> devslist) {
		List<Device> spacedevlist = new ArrayList<Device>();
		for (int i = 0; i < devslist.size(); i++) {
			Device devdto = devslist.get(i);
			int devtype = devdto.getDeviceTypeId();
			if ((devtype == 104) || (devtype == 109)) { // 温湿度，PM25不在微控中显示

			} else {
				spacedevlist.add(devdto);
			}
		}
		return spacedevlist;
	}

	/**
	 * 去除传感器类情景设备和室内室外情景
	 * 
	 * @param devid
	 *            002 传感器类情景
	 * @return 003 室内室外撤布防 备注： 情景类型 1：硬件四路 2：双控开关 3：安防情景 4：自定义情景
	 */
	public static List<Theme> findCustomThemeFromThemesAll(List<Theme> allLists) {
		List<Theme> thmelist = new ArrayList<Theme>();
		for (int i = 0; i < allLists.size(); i++) {
			Theme theme = allLists.get(i);
			int themetype = theme.getThemeType();
			if ((themetype == 1) || (themetype == 2) || (themetype == 4)) {
				thmelist.add(theme);
			}
		}
		return thmelist;
	}


	/**
	 * 控制命令转码保存到数据库
	 * 
	 * @param devtype
	 * @param ch
	 * @param devstate
	 * @return
	 */
	public static String convertCmdToSql(Device device, int switchid, char ch) {
		final char ON = 1 + '0'; // 字符开
		final char OFF = 0 + '0'; // 字符关
		final char WinON = 5 + '0'; // 窗帘暂停
		final char WinPK = 6 + '0'; // 窗帘暂停
		final char WinOFF = 7 + '0'; // 窗帘暂停
		int devtype = device.getDeviceTypeId();

		String devstate = device.getDeviceStateCmd();
		char[] strStaArr = new char[4]; // 字符数组代表多路开关状态
		String strCmd = null; // 待发送的命令

		if ((devtype == 110) || (devtype == 113) || (devtype == 115)
				|| (devtype == 118)) {// 安防类设备
			if (switchid == 1) {
				if (ch == ON) {
					strCmd = "11"; // 布防
				} else {
					strCmd = "10"; // 撤防
				}
			}
		} else {
			if (switchid == 5) { // 传递灯光的值
				strCmd = devstate;
			} else if ((switchid == 6)) { // 窗帘、窗户类
				if (ch == WinON) {
					strCmd = "10";
				} else if (ch == WinPK) {
					strCmd = "00";
				} else if (ch == WinOFF) {
					strCmd = "01";
				}
			} else { // 开关插座类switchid=2,3,4
				String sDevState = device.getDeviceStateCmd();
				strStaArr = sDevState.toCharArray();
				if (switchid == 1) {
					strStaArr[0] = ch;
				} else if (switchid == 2) {
					strStaArr[1] = ch;
				} else if (switchid == 3) {
					strStaArr[2] = ch;
				} else if (switchid == 4) {
					strStaArr[3] = ch;
				}
				strCmd = new String(strStaArr);
			}
		}

		return strCmd;
	}

	/**
	 * 根据设备类型查找设备大类
	 * 
	 * @param devtype
	 * @return
	 */
	public static Integer findWidetypeByDevtype(short devtype) {
		Integer devwidetype = 0;
		if ((devtype == 1) || (devtype == 2) || (devtype == 3)
				|| (devtype == 4) || (devtype == 5)) {
			devwidetype = 1; // 开关类
			return devwidetype;
		} else if ((devtype == 51) || (devtype == 104) || (devtype == 109)
				|| (devtype == 110) || (devtype == 113) || (devtype == 115)
				|| (devtype == 118)) {
			devwidetype = SystemValue.SENSOR; // 传感器类
			return devwidetype;
		} else if ((devtype == 6) || (devtype == 11)) {
			devwidetype = 3; // 窗帘、窗户类
			return devwidetype;
		} else if ((devtype == 202) || (devtype == 204)) {
			devwidetype = 4; // 情景开关
			return devwidetype;
		} else if (devtype == 8) {
			devwidetype = 5; // 插座类
			return devwidetype;
		}

		return devwidetype;
	}

	/**
	 * 根据设备类型初始化设备名称
	 * 
	 * @param devtype
	 * @return
	 */
	public static String initDevtypeNameByDevtype(short devtype) {
		String devname = null;
		switch (devtype) {
		case 1:
			devname = "一路开关";
			break;
		case 2:
			devname = "二路开关";
			break;
		case 3:
			devname = "三路开关";
			break;
		case 4:
			devname = "四路开关";
			break;
		case 5:
			devname = "调光开关";
			break;
		case 6:
			devname = "窗帘";
			break;
		case 8:
			devname = "插座";
			break;
		case 11:
			devname = "窗户";
			break;
		case 51:
			devname = "空气净化器";
			break;
		case 104:
			devname = "温湿度";
			break;
		case 105:
			devname = "红外转发器";
			break;
		case 109:
			devname = "PM2.5";
			break;
		case 110:
			devname = "门磁";
			break;
		case 113:
			devname = "红外入侵";
			break;
		case 115:
			devname = "燃气";
			break;
		case 118:
			devname = "烟感";
			break;
		case 202:
			devname = "情景开关";
			break;
		case 204:
			devname = "双控开关";
			break;
		case 304:
			devname = "声光报警";
			break;
		default:
			devname = "名称待定";
			break;
		}
		return devname;
	}


	// 从list中加载部分
	public static List<Device> getData(List<Device> list, int start, int offset) {
		List<Device> devlist = new ArrayList<Device>();
		for (int i = start; i < offset; i++) {
			devlist.add(list.get(i));
		}
		return devlist;
	}
	
	
	//情景控制themeData转换为字节
	public static byte[] sceneControl2ByteStream(ThemeData themedata,int length) {
		byte[] soc = new byte[length];
		int index = 0;
		//填充ThemeNo【id_major】
		byte[] bThemeNo = DataConvertUtil.toByteArray(themedata.getThemeNo()); // 长度为16的string串转换为8个字节
		for (int i = 0; i < 8; i++) {
			soc[index++] = bThemeNo[i];
		}
		// 填充情景状态【id_minor】		
		byte[] bThemeState = ( themedata.getThemeState()).getBytes();   //themestate需转换为底层硬件识别的0x64,0x00
			for (int k = 0; k < bThemeState.length; k++) {
				if (bThemeState[k]==0x31) { // 1的字节码为0x31转换为0x64
					soc[index++]=0x64;
				} else if (bThemeState[k]==0x30) { // 0的字节码为0x30转换为0x00
					soc[index++]=0x00;
				}
			}
		return soc;
	}
	
	

	/**
	 * 设备安装信息转换为字节数组
	 * 
	 * @param znodepacket
	 * @return
	 */
	public static byte[] sceneSetPacketToByteStream(ThemeData themedata,int length) {
		int themeType = themedata.getThemeType();

		int socketLength = length; // 整个报文的长度
		byte[] soc = new byte[socketLength];

		int index = 0;
		//填充ThemeNo【id_major】
		byte[] bThemeNo = DataConvertUtil.toByteArray(themedata.getThemeNo()); // 长度为16的string串转换为8个字节
		for (int i = 0; i < 8; i++) {
			soc[index++] = bThemeNo[i];
		}
		// 填充情景状态【id_minor】		
		byte[] bThemeState = ( themedata.getThemeState()).getBytes();   //themestate需转换为底层硬件识别的0x64,0x00
			
			for (int k = 0; k < bThemeState.length; k++) {
				if (bThemeState[k]==0x31) { // 1的字节码为0x31转换为0x64
					soc[index++]=0x64;
				} else if (bThemeState[k]==0x30) { // 0的字节码为0x30转换为0x00
					soc[index++]=0x00;
				}
			}
			
		int stateLeft = 8 - bThemeState.length; // themestate不够8个字节补全
		index = index + stateLeft;
		//填充DeviceNo【host_mac】
		byte[] bDeviceNo = DataConvertUtil.toByteArray(themedata.getDeviceNo()); // 长度为16的string串转换为8个字节
		if (themeType == 4) { // 自定义情景
			for (int i = 0; i < 8; i++) {
				soc[index++] = 0x30;
			}
		} else {//实体情景
			for (int i = 0; i < 8; i++) {
				soc[index++] = bDeviceNo[i];
			}
		}
		// 填充情景类型
		byte[] bThemeType = DataConvertUtil.intToByte(themedata.getThemeType()); 
		for (int i = 0; i < 4; i++) {
			soc[index++] = bThemeType[i];
		}
		// 填充情景名称【名称的前4个字节为名称的长度，后面的56个字节为名称的内容】 
		String strChinese=DataConvertUtil.chineseToString(themedata.getThemeName());
		int nameLength=strChinese.length();
		
		byte[] bNameLength = DataConvertUtil.intToByte(nameLength); 
		for (int i = 0; i < 4; i++) {
			soc[index++] = bNameLength[i];
		}     
		
		byte[] bThemeName =strChinese.getBytes();
		
		for (int i = 0; i < bThemeName.length; i++) {
			soc[index++] = bThemeName[i];
		}
		int nameLeft = 56 - bThemeName.length; //名称的前4个字节为名称的长度，后面的56个字节为名称的内容
		index = index + nameLeft;
		// 填充触发情景设备的数量
		byte[] bTriggerNum = DataConvertUtil.intToByte(themedata
				.getTriggerNum()); // 长度为16的string串转换为8个字节
		for (int i = 0; i < 4; i++) {
			soc[index++] = bTriggerNum[i];
		}
		// 填充情景联动设备的数量
		byte[] bDeviceNum = DataConvertUtil.intToByte(themedata
				.getDeviceNum()); // 长度为16的string串转换为8个字节
		for (int i = 0; i < 4; i++) {
			soc[index++] = bDeviceNum[i];
		}
		/******** 填充触发器信息 *********/
	if(themeType!=4){
	
		Item itemTrigger = themedata.getTriggerList().get(0);   //一个情景对应的触发器只有一个
		// 填充触发器硬件识别号
		byte[] bTriDeviceNo = DataConvertUtil.toByteArray(itemTrigger.getDeviceNo()); // 当为触发器时，item的id代表theme_no
		for (int i = 0; i < 8; i++) {
			soc[index++] = bTriDeviceNo[i];
		}
		
		// 填充触发器状态
		byte[] bTriggerState = ( itemTrigger.getDeviceStateCmd()).getBytes();
//		DataConvertUtil.tprintHexString(bTriggerState);
		for (int k = 0; k < bTriggerState.length; k++) {
			if (bTriggerState[k]==0x31) { // 1的字节码为0x31转换为0x64
				soc[index++]=0x64;
			} else if (bTriggerState[k]==0x30) { // 0的字节码为0x30转换为0x00
				soc[index++]=0x00;
			}
		}
		int triStateLen = 32 - bTriggerState.length; // themestate不够32个字节补全
		index = index + triStateLen;
		
		// 填充触发器datalen
		byte[] bTriDataLen = DataConvertUtil.intToByte(itemTrigger.getDataLen());
		for (int i = 0; i < 4; i++) {
			soc[index++] = bTriDataLen[i];
		}
		
		// 填充触发器devtype
		byte[] bTriDevType = DataConvertUtil.intToByte(itemTrigger.getDeviceType());
		for (int i = 0; i < 4; i++) {
			soc[index++] = bTriDevType[i];
		}
		
	}
		
		/******** 填充情景设备联动表 *********/
		ArrayList<Item> deviceList = themedata.getDeviceList();
		for (int i = 0; i < deviceList.size(); i++) {
			Item itemDevice = deviceList.get(i);
			// 填充联动设备识别号
			byte[] bThemeDeviceNo = DataConvertUtil.toByteArray(itemDevice
					.getDeviceNo()); // 前4个字节代表设备名的长度
			for (int j = 0; j < 8; j++) {
				soc[index++] = bThemeDeviceNo[j];
			}
			// 填充联动设备状态
			String itemDevState=itemDevice.getDeviceStateCmd();
			byte[] bThemeDevState = itemDevState.getBytes();
			
			int devStateLen=0;  //state不够32字节补充的
			if(itemDevice.getDeviceType()==SystemValue.DEV_DIM_LIGHT){//调光灯
				soc[index++] = convertLampStringtoByte(itemDevState);
			    devStateLen = 32 - bThemeDevState.length; // 联动的设备状态，themestate不够8个字节补全
			}else if(itemDevice.getDeviceType()==SystemValue.DEV_INFRA_CONTROL){
				byte[] infrareBytes = DataConvertUtil.toByteArray(itemDevState);
				int datalength = infrareBytes.length;
				for (int j = 0; j < datalength; j++) {
					soc[index++] = infrareBytes[j];
				}
				devStateLen = 32 - datalength; // 联动的设备状态，themestate不够8个字节补全
			}else{  //普通开关设备
				for (int k = 0; k < bThemeDevState.length; k++) {
					if (bThemeDevState[k] == 0x31) { // 1的字节码为0x31转换为0x64
						soc[index++] = 0x64;
					} else if (bThemeDevState[k] == 0x30) { // 0的字节码为0x30转换为0x00
						soc[index++] = 0x00;
					}
				}
				devStateLen = 32 - bThemeDevState.length; // 联动的设备状态，themestate不够8个字节补全
			}
			index = index + devStateLen;
			
			// 填充被触发设备datalen
			byte[] bDevDataLen = DataConvertUtil.intToByte(itemDevice.getDataLen());
			for (int m = 0; m < 4; m++) {
				soc[index++] = bDevDataLen[m];
			}
			
			// 填充被触发器设备devtype
			byte[] bDevType = DataConvertUtil.intToByte(itemDevice.getDeviceType());
			for (int n = 0; n < 4; n++) {
				soc[index++] = bDevType[n];
			}
		}
		return soc;
	}
	
	/**
	 * 获取所有设备的最新状态
	 * @param categoryId
	 */
	public static void getDeviceListAllByPhonenum() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", SystemValue.phonenum);
		
		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.POST, NetValue.DEVICE_CATEGORY_URL, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				System.out.println("定时从服务器刷新设备最新状态失败！");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Gson gson = new Gson();
				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
				if (message != null) {
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
						
						String jDevices=(String) message.getObject();
						
						//更新数据库中的设备表
						WebPacketUtil.parseDevicelistFromServer(jDevices);
							
					} else {
					}
				}
			}
		});
	}
	
	/**
	 * 查找红外遥控器对应的设备类型
	 * @param devtype
	 * @return
	 */
	public static String getInfraDevtypeNameByDevtype(int devtype){
		String devName="";
		switch (devtype) {
		case 8192:
			return devName="电视";
		case 8448:
			return devName="IPTV";
        case 9472:
			return devName="电视盒子";
        case 16384:
			return devName="机顶盒";
        case 24576:
			return devName="DVD";
        case 32768:
			return devName="电风扇";
        case 40960:
        	return devName="投影仪";
        case 49152:
			return devName="空调";
		default:
			Log.i("343", "红外设备类型："+devtype);
			return devName;
		}
	}
	
	/**
	 * 将设备转换为服务器的报文
	 * @param device
	 * @return
	 */
	public static String converDevice2PacketStr(Device device){
		String strPacket="";
		SocketPacket devPacket=null;
		//将命令转换为报文
		int devtype=device.getDeviceTypeId();
		if(devtype==SystemValue.DEV_INFRA_CONTROL){
			 devPacket =infraredConverToPacket(device.getDeviceStateCmd());
		}else{
			 devPacket =devConvertToPacket(device);
		}
		byte[] sentBytes=packetToByteStream(devPacket);
		String  strCmd=DataConvertUtil.toHexUpString(sentBytes);
  		int strLength=strCmd.length();
  		String strSub="42424141"+strCmd.substring(8, strLength);
  		strPacket=strSub;
		
		return strPacket;
	}
	
	/**
	 * 将情景转换为服务器的报文
	 * @param theme
	 * @return
	 */
	public static String converTheme2PacketStr(Theme theme){
		String strPacket="";
		ThemeData themeData=new ThemeData();
   		themeData.setThemeNo(theme.getThemeNo());
   		themeData.setThemeState(theme.getThemeState());
   		SocketPacket socketPacket=WebPacketUtil.sceneControl2Packet(themeData);
   		byte[] sentBytes=WebPacketUtil.packetToByteStream(socketPacket);
   		String  strCmd=DataConvertUtil.toHexUpString(sentBytes);
 		int strLength=strCmd.length();
 		String strSub="42424141"+strCmd.substring(8, strLength);
 		strPacket=strSub;
		
		return strPacket;
	}
	
	/**
	 * 发送红外命令到服务器
	 * @param sentBytes
	 * @param cmdType
	 */
	public static void sendCmdToServer(byte[] sentBytes,final int cmdType) {
  		String  strCmd=DataConvertUtil.toHexUpString(sentBytes);
  		int strLength=strCmd.length();
  		String strSub="42424141"+strCmd.substring(8, strLength);
  		
  		RequestParams params = new RequestParams();
  		params.addBodyParameter("devicePacketJson",strSub);
  		
//  		System.out.println("===发送到服务器的命令==="+strSub);
  		Log.i("343", "===红外》》》服务器的命令==="+strSub);
  		
  		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
  		utils.send(HttpMethod.POST, NetValue.DEVICE_CONTROL_URL, params,new RequestCallBack<String>() {

  			@Override
  			public void onFailure(HttpException arg0, String arg1) {
  				// TODO Auto-generated method stub
  			}

  			@Override
  			public void onSuccess(ResponseInfo<String> arg0) {
  				
  				Gson gson = new Gson();
  				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
  				if (message != null) {
  					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
  					   if(cmdType==1){ //0:控制设备  1:请求同步设备  2:请求同步情景
  						 SystemValue.deviceSysnFlag=true;  //向服务器请求同步设备成功   
  					   }else if(cmdType==2){
  					   }
  					   
  						System.out.println("控制设备服务器返回"+ message.getMessageInfo());
  					} else {
//  						ToastUtils.showToast(BaseActivity.this,message.getMessageInfo(), 1000);
  					}
  				}
  			}
  		});
  	}

	
}
