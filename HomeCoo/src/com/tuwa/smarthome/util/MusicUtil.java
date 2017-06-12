package com.tuwa.smarthome.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tuwa.smarthome.entity.APPMusic;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.AppThemeMusicSocket;
import com.tuwa.smarthome.entity.Music;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.MusicSocket;
import com.tuwa.smarthome.entity.MusicSocketByte;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeMusic;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.DatagramSocketPhoneServer;

/**
 * 工具类
 * */
public class MusicUtil {

	/**
	 * @Description:musci 转 musicOrder
	 * @param:songName 歌曲名称
	 * @param:type 控制类型
	 * @param:style 循环模式
	 * @return:MusicOrder
	 * */
	public static MusicOrder ToMusicOrder(String songName, String type,
			String style) {
		MusicOrder musicOrder = new MusicOrder();
		musicOrder.setOrder(type);
		musicOrder.setSongName(songName);
		musicOrder.setWgid(SystemValue.gatewayid);
		musicOrder.setStyle(style);
		musicOrder.setBz(style);
		return musicOrder;

	}

	/**
	 * @Description:MusicList 转 MusicThemeList
	 * @param:Musiclist
	 * @param:ThemeMusiclist
	 * @return:themeMusicList
	 * */
	public static List<ThemeMusic> ToThemeMusicList(List<Music> list) {

		Iterator<Music> iterator = list.iterator();
		List<ThemeMusic> themeMusicList = new ArrayList<ThemeMusic>();
		while (iterator.hasNext()) {
			Music music = iterator.next();
			ThemeMusic themeMusic = new ThemeMusic();
			themeMusic.setBz(music.getBz());
			themeMusic.setSpace(music.getSpace());
			themeMusic.setSongName(music.getSongName());
			// 默认情景模式下 音乐是列表循环 之后的再搞
			themeMusic.setStyle(SystemValue.MUSIC_STYLE_LIST);
			themeMusic.setThemeNo(null);
			themeMusic.setGatewayNo(SystemValue.gatewayid);
			themeMusicList.add(themeMusic);
		}
		return themeMusicList;

	}

	/**
	 * @Description:收到socket未解码的 incodemusiclist 转码为 MusicList
	 * @param String
	 * @return List<MusicSocket>
	 * */
	public static List<MusicSocket> ToMusicSocketList(String incodemusiclist) {
		MusicSocketByte byte1 = JSONObject.parseObject(incodemusiclist,
				MusicSocketByte.class);
		List<MusicSocket> musicList = new ArrayList<MusicSocket>();
		musicList = JSONArray.parseArray(byte1.getStyle(), MusicSocket.class);
		return musicList;
	}

	/**
	 * @Description:List<MusicSocket> 转 List<Music>
	 * @retuen List<Music>
	 * */
	public static List<Music> ToMusicList(List<MusicSocket> list) {
		List<Music> musiclist = new ArrayList<Music>();
		Iterator<MusicSocket> iterator = list.iterator();
		while (iterator.hasNext()) {
			MusicSocket musicSocket = iterator.next();
			Music music = new Music();
			music.setBz("");
			music.setFamilyName("");
			String songName = new String(musicSocket.getSongName());
			music.setSongName(songName);
			music.setSpace("");
			music.setUuid("");
			music.setWgid(SystemValue.gatewayid);
			musiclist.add(music);
		}
		return musiclist;
	}

	/**
	 * @Description:List<Music> 转 List<APPMusic>
	 * */
	public static List<APPMusic> GetAppMusicList(List<Music> musics) {
		Iterator<Music> iterator = musics.iterator();
		List<APPMusic> list = new ArrayList<APPMusic>();
		while (iterator.hasNext()) {
			APPMusic appMusic = new APPMusic();
			Music music = (Music) iterator.next();
			appMusic.setGatewayNo(music.getWgid());
			appMusic.setSongName(music.getSongName());
			list.add(appMusic);
		}

		return list;
	}

	/**
	 * @Description:List<Music> 转 List<Map<String, Object>>
	 * @return List<Map<String, Object>>
	 * */
	public static List<Map<String, Object>> TolistMap(List<Music> list) {
		List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
		Iterator<Music> iterator = list.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			Music music = iterator.next();
			map.put("songName", music.getSongName());
			listems.add(map);
		}
		return listems;

	}

	/**
	 * @Description:MusicOrder 转 MusicSocketByteJson
	 * @return MusicSocketByteJson
	 * */
	public static String ToMusicOrderSocketJson(MusicOrder musicOrder) {

		MusicSocketByte socketByte = new MusicSocketByte();
		socketByte.setBz(musicOrder.getBz());
		socketByte.setOrder(musicOrder.getOrder());
		if (!musicOrder.getOrder().equals("0")
				&& !musicOrder.getOrder().equals("11")) { // 0表示 获取歌曲列表 11表示播放
			try {
				byte[] songName = musicOrder.getSongName().getBytes();
				socketByte.setSongName(songName);
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		socketByte.setStyle(musicOrder.getStyle());
		socketByte.setWgid(SystemValue.gatewayid);
		String respjson = JSONObject.toJSONString(socketByte) + '\0';
		return respjson;

	}

	/**
	 * @Description:每次一连接到socket就发送一条命令 获取歌曲列表
	 * */
	public static MusicOrder GetMusicList() {
		MusicOrder musicOrder = new MusicOrder();
		musicOrder.setBz("");
		musicOrder.setOrder("0");
		musicOrder.setSongName("");
		musicOrder.setStyle("");
		musicOrder.setWgid(SystemValue.gatewayid);
		return musicOrder;

	}

	/**
	 * @Description:获取当前的音量数值
	 * */
	public static MusicOrder GetVolume() {
		MusicOrder order = new MusicOrder();
		order.setBz("");
		order.setOrder("15");
		order.setSongName("");
		order.setStyle("");
		order.setWgid(SystemValue.gatewayid);
		return order;

	}

	/**
	 * @Description:获取七寸屏上的情景联动音乐
	 * */
	public static MusicOrder GetThemeMusicInsideFromScreen() {
		MusicOrder order = new MusicOrder();
		order.setBz("");
		order.setOrder(SystemValue.MUSIC_THEME_GET); // 内网下获取七寸屏上的联动音乐
		order.setSongName("");
		order.setStyle("");
		order.setWgid(SystemValue.gatewayid);
		return order;

	}

	/**
	 * @Description:内网控制音量
	 * */
	public static MusicOrder SetVolume(String volume) {
		MusicOrder order = new MusicOrder();
		order.setBz("");
		order.setOrder(SystemValue.MUSIC_VOLUME_CTRL);
		order.setSongName("");
		order.setStyle(volume);
		order.setWgid(SystemValue.gatewayid);
		return order;

	}

	/**
	 * @Description:List<APPMusic> 转 list<ThemeMusic>
	 * */
	public static List<ThemeMusic> TothememusicList(List<APPMusic> appMusics) {
		List<ThemeMusic> list = new ArrayList<ThemeMusic>();
		Iterator<APPMusic> iterator = appMusics.iterator();
		while (iterator.hasNext()) {
			APPMusic appMusic = (APPMusic) iterator.next();
			ThemeMusic themeMusic = new ThemeMusic();
			themeMusic.setGatewayNo(SystemValue.gatewayid);
			themeMusic.setSongName(appMusic.getSongName());
			themeMusic.setStyle(SystemValue.MUSIC_STYLE_LIST);
			list.add(themeMusic);

		}

		return list;

	}

	/**
	 * @Description:内网删除情景音乐
	 * */
	public static MusicOrder getMusicOrder(String themeid) {

		MusicOrder order = new MusicOrder();
		order.setOrder("9");
		order.setStyle("6");
		order.setBz(themeid);
		order.setSongName("");
		order.setWgid(SystemValue.gatewayid);
		return order;

	}

	// /**
	// * @Description:内网设置情景音乐为 暂停音乐播放
	// * */
	// public static MusicOrder getMusicOrderofpauseMusic(String themeid){
	//
	// MusicOrder order=new MusicOrder();
	// order.setOrder("12");
	// order.setStyle("1");
	// order.setBz(themeid);
	// order.setSongName("00");
	// order.setWgid(SystemValue.gatewayid);
	// return order;
	//
	// }

	/**
	 * @Description:内网设置情景音乐为 暂停音乐播放
	 * */
	public static MusicOrder getMusicOrderofpauseMusic(
			APPThemeMusic appThemeMusic) {

		MusicOrder order = new MusicOrder();
		AppThemeMusicSocket musicSocket = Getappthememusicsocket(appThemeMusic);
		order.setBz(JSONObject.toJSONString(musicSocket));
		order.setOrder("12"); // 内网暂停情景联动音乐
		order.setStyle(appThemeMusic.getStyle()); // 暂停音乐
		order.setSongName("00");
		order.setWgid(SystemValue.gatewayid);
		return order;

	}

	/**
	 * @Description:thememusic theme 得到 appthememusic
	 * @param type
	 *            : 2 播放情景音乐 1 暂停情景音乐
	 * */
	public static APPThemeMusic GetAppThemeMusic(ThemeMusic themeMusic,
			Theme theme, String type) {
		APPThemeMusic appThemeMusic = new APPThemeMusic();
		appThemeMusic.setBz("");
		appThemeMusic.setDeviceNo(theme.getDeviceNo());
		appThemeMusic.setDeviceState(theme.getThemeState());
		if (theme.getThemeState().length() < 5) {
			String themestate = theme.getThemeState() + "0000";
			appThemeMusic.setDeviceState(themestate);
		}
		appThemeMusic.setGatewayNo(themeMusic.getGatewayNo());
		if (type.equals("2")) {
			appThemeMusic.setSongName(themeMusic.getSongName());
			appThemeMusic.setStyle("5");
		} else if (type.equals("1")) {
//			appThemeMusic.setSongName("00");
			appThemeMusic.setSongName(themeMusic.getSongName());
			appThemeMusic.setStyle("1");
		}
		appThemeMusic.setSpace("");
		appThemeMusic.setThemeName(theme.getThemeName());
		appThemeMusic.setThemeNo(theme.getThemeNo());
		return appThemeMusic;

	}

	/**
	 * @Description:情景设置音乐
	 * */
	public static MusicOrder SetthememusicOnInside(APPThemeMusic appThemeMusic) {

		MusicOrder order = new MusicOrder();
		AppThemeMusicSocket musicSocket = MusicUtil
				.Getappthememusicsocket(appThemeMusic);
		String bz = JSONObject.toJSONString(musicSocket);
		order.setBz(bz);
		order.setOrder("10");
		order.setSongName(appThemeMusic.getSongName());
		order.setStyle(appThemeMusic.getStyle());
		order.setWgid(appThemeMusic.getGatewayNo());
		return order;

	}

	/**
	 * @Description:appthememusic 转 appthememusicsocket
	 * */
	public static AppThemeMusicSocket Getappthememusicsocket(
			APPThemeMusic appThemeMusic) {
		AppThemeMusicSocket musicSocket = new AppThemeMusicSocket();
		musicSocket.setBz(appThemeMusic.getBz());
		musicSocket.setDeviceNo(appThemeMusic.getDeviceNo());
		musicSocket.setDeviceState(appThemeMusic.getDeviceState());
		musicSocket.setGatewayNo(appThemeMusic.getGatewayNo());
		byte[] songName = appThemeMusic.getSongName().getBytes();
		musicSocket.setSongName(songName);
		musicSocket.setSpace(appThemeMusic.getSpace());
		musicSocket.setStyle(appThemeMusic.getStyle());
		byte[] themeName = appThemeMusic.getThemeName().getBytes();
		musicSocket.setThemeName(themeName);
		musicSocket.setThemeNo(appThemeMusic.getThemeNo());

		return musicSocket;

	}

	/**
	 * @Description:设置情景音乐为暂停音乐
	 * */
	public static APPThemeMusic PauseAppthemeMusic(Theme theme,ThemeMusic themeMusic) {
		APPThemeMusic appThemeMusic = new APPThemeMusic();
		appThemeMusic.setBz("");
		appThemeMusic.setDeviceNo(theme.getDeviceNo());
		appThemeMusic.setDeviceState(theme.getThemeState());
		if (theme.getThemeState().length() < 5) {
			String themestate = theme.getThemeState() + "0000";
			appThemeMusic.setDeviceState(themestate);
		}
		appThemeMusic.setGatewayNo(SystemValue.gatewayid);
//		appThemeMusic.setSongName("00");
		appThemeMusic.setSongName(themeMusic.getSongName());
		appThemeMusic.setSpace("");
		appThemeMusic.setStyle(SystemValue.MUSIC_CTRL_PAUSE);
		appThemeMusic.setThemeName(theme.getThemeName());
		appThemeMusic.setThemeNo(theme.getThemeNo());

		return appThemeMusic;

	}

	/**
	 * @Description:appthememusic 转 thememusic 只用于情景音乐 为 暂停播放音乐 情况
	 * update by xiaobai  2017-01-07
	 * */
	public static ThemeMusic PauseThememusicMusic(Theme theme,String songName) {

		ThemeMusic themeMusic = new ThemeMusic();
		themeMusic.setBz("");
		themeMusic.setDeviceNo(theme.getDeviceNo());
		themeMusic.setDeviceState(theme.getThemeState());
		if (theme.getThemeState().length() < 5) {
			String themestate = theme.getThemeState() + "0000";
			themeMusic.setDeviceState(themestate);
		}
		themeMusic.setGatewayNo(SystemValue.gatewayid);
//		themeMusic.setSongName("00");
		themeMusic.setSongName(songName);
		themeMusic.setSpace("");
		themeMusic.setStyle(SystemValue.MUSIC_CTRL_PAUSE);
		themeMusic.setThemeName(theme.getThemeName());
		themeMusic.setThemeNo(theme.getThemeNo());

		return themeMusic;

	}

	/**
	 * @Description:情景音乐设置 生成 themeMusic
	 * 
	 * */
	public static ThemeMusic GetThemeMusic(Theme theme, ThemeMusic themeMusic) {
		themeMusic.setThemeNo(theme.getThemeNo());
		themeMusic.setThemeName(theme.getThemeName());
		themeMusic.setDeviceNo(theme.getDeviceNo());
		themeMusic.setDeviceState(theme.getThemeState());
		if (theme.getThemeState().length() < 5) {
			String themestate = theme.getThemeState() + "0000";
			themeMusic.setDeviceState(themestate);
		}
		themeMusic.setSpace("");
		themeMusic.setStyle("5");

		return themeMusic;
	}

	/**
	 * 获取设置的 情景音乐 在歌曲列表的下标
	 * */
	public static int GetMusicListIndex(String themeMusicName,
			List<ThemeMusic> list) {
		int a = -1;
		for (int i = 0; i < list.size(); i++) {
			if (themeMusicName.equals(list.get(i).getSongName())) {
				a = i;
				break;
			}
		}
		return a;

	}

	/**
	 * 外网下删除情景联动音乐
	 * */
	public static APPThemeMusic deletetoJpush(String themeNo) {
		APPThemeMusic appThemeMusic = new APPThemeMusic();
		appThemeMusic.setGatewayNo(SystemValue.gatewayid);
		appThemeMusic.setSongName("00");
		appThemeMusic.setStyle(SystemValue.MUSIC_THEME_MUSIC_DELETE);
		appThemeMusic.setThemeNo(themeNo);
		return appThemeMusic;
	}

	/**
	 * 启动Datagramsocket server
	 * 手机端   2016-09-29 20:03:23
	 * */
	public static void StartDatagramSocketServerForIp(Context context){
//		SystemValue.datagramsocketServer=new DatagramSocketPhoneServer();
//		DatagramSocketPhoneServer.datasocketserverReceiveFlag=true;
//		DatagramSocketPhoneServer.datasocketserverSendFlag=true;
//		SystemValue.datagramsocketServer.onCreate();
		
		DatagramSocketPhoneServer.datasocketserverSendFlag = true;      
		DatagramSocketPhoneServer.datasocketserverReceiveFlag = true; 
		DatagramSocketPhoneServer.datasocketslientReceive = true;   
		DatagramSocketPhoneServer.socketStatus=false;				
		DatagramSocketPhoneServer.SendSocketFlag=true;
		
		Intent service=new Intent(context, DatagramSocketPhoneServer.class);
		context.startService(service);
	}
	
	
	/**
	 * 手机 收到七寸屏的IP，关闭UDP广播
	 * */
	public static void StopDatagramSocketServer(Context context){
		Intent intent1=new Intent(context,DatagramSocketPhoneServer.class);
//		DatagramSocketPhoneServer.datasocketserverReceiveFlag=false;
//		DatagramSocketPhoneServer.datasocketserverSendFlag=false;
		
		DatagramSocketPhoneServer.datasocketserverSendFlag=false;     //111
		DatagramSocketPhoneServer.datasocketserverReceiveFlag=false;   //444
		DatagramSocketPhoneServer.datasocketslientReceive=false;    //222
		DatagramSocketPhoneServer.socketStatus=true;   //333
		
		context.stopService(intent1);
	}
	/**
	 * List<ThemeMusic> 转 List<AppThemeMusic>
	 * @param List<ThemeMusic>
	 * @return List<APPThemeMusic>
	 * */
	public static List<APPThemeMusic> ThemeMusicListToAppThemeMuiscList(List<ThemeMusic> list){
		List<APPThemeMusic> list2=new ArrayList<APPThemeMusic>();
		Iterator<ThemeMusic> iterator=list.iterator();
		while (iterator.hasNext()) {
			APPThemeMusic appThemeMusic=new APPThemeMusic();
			ThemeMusic themeMusic=iterator.next();
			appThemeMusic.setBz(themeMusic.getBz());
			appThemeMusic.setDeviceNo(themeMusic.getDeviceNo());
			appThemeMusic.setDeviceState(themeMusic.getDeviceState());
			appThemeMusic.setGatewayNo(themeMusic.getGatewayNo());
			appThemeMusic.setSongName(themeMusic.getSongName());
			appThemeMusic.setSpace(themeMusic.getSpace());
			appThemeMusic.setStyle(themeMusic.getStyle());
			appThemeMusic.setThemeName(themeMusic.getThemeName());
			appThemeMusic.setThemeNo(themeMusic.getThemeNo());
			list2.add(appThemeMusic);
		}
		
		return list2;
		
	}
	
	
	/**
	 * APPThemeMusic 转 ThemeMusic
	 * @param AppThemeMusic
	 * @return ThemeMusic
	 * */
	public static ThemeMusic AppThemeMusicToThemeMusic(APPThemeMusic appThemeMusic){
		ThemeMusic themeMusic=new ThemeMusic();
		themeMusic.setBz(appThemeMusic.getBz());
		themeMusic.setDeviceNo(appThemeMusic.getDeviceNo());
		themeMusic.setDeviceState(appThemeMusic.getDeviceState());
		themeMusic.setGatewayNo(appThemeMusic.getGatewayNo());
		themeMusic.setSongName(appThemeMusic.getSongName());
		themeMusic.setSpace(appThemeMusic.getSpace());
		themeMusic.setStyle(appThemeMusic.getStyle());
		themeMusic.setThemeName(appThemeMusic.getThemeName());
		themeMusic.setThemeNo(appThemeMusic.getThemeNo());
		
		return themeMusic;
		
	}


	/**
	 * List<APPThemeMusicSocket> 转 List<APPThemeMusic>
	 * @param List<APPThemeMusicSocket>
	 * @return List<APPThemeMusic>
	 * */
	public static List<APPThemeMusic> APPThemeMusicSocketListToAppThemeMusicList(List<AppThemeMusicSocket> list){
		List<APPThemeMusic> themeMusics=new ArrayList<APPThemeMusic>();
		Iterator<AppThemeMusicSocket> iterator=list.iterator();
		while (iterator.hasNext()) {
			AppThemeMusicSocket socket=iterator.next();
			APPThemeMusic music=new APPThemeMusic();
			music.setBz(socket.getBz());
			music.setDeviceNo(socket.getDeviceNo());
			music.setDeviceState(socket.getDeviceState());
			music.setGatewayNo(socket.getGatewayNo());
			String songName=new String(socket.getSongName());
			music.setSongName(songName);
			music.setSpace(socket.getSpace());
			music.setStyle(socket.getStyle());
			String themeName=new String(socket.getThemeName());
			music.setThemeName(themeName);
			music.setThemeNo(socket.getThemeNo());
			themeMusics.add(music);
		}
		return themeMusics;
		
	}

	

	/**
	 * 发送七寸屏一个23     手机端发送socket请求该IP是不是七寸屏的IP 
	 * */
	public static String SendIPFlagToScreen(){
		MusicSocketByte socketByte=new MusicSocketByte();
		socketByte.setOrder(SystemValue.UDP_CLIENT_IP_TRUE);
		socketByte.setWgid(SystemValue.gatewayid);
		String resp=JSON.toJSONString(socketByte)+'\0';
		return resp;
		
	}
	
	/**
	 * 退出APK的时候,停掉当前UDP广播
	 * 停掉Udp广播，需要关闭该service中的线程
	 * */
	public static void StopDatagramSocketphoneServer(Context context){
		Intent intent=new Intent(context, DatagramSocketPhoneServer.class);
		DatagramSocketPhoneServer.datasocketserverSendFlag=false;     //111
		DatagramSocketPhoneServer.datasocketserverReceiveFlag=false;   //444
		DatagramSocketPhoneServer.datasocketslientReceive=false;    //222
		DatagramSocketPhoneServer.socketStatus=true;   //333
//		Log.i("X-2016-Udp","helolo\\");
		context.stopService(intent);
		
	}
	
	
}
