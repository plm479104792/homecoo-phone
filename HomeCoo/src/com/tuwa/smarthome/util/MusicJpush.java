package com.tuwa.smarthome.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Jpush;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.ThemeMusic;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;

/**
 * 音乐Jpush工具类
 * */
public class MusicJpush {
	private static ResultMessage msg=new ResultMessage();
	
	/**
	 * 推送自定义消息  
	 * */
	public static void JpushMusicOrder(MusicOrder musicOrder){
		JPushimpl jPushimpl=new JPushimpl();
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(SystemValue.gatewayid);
		jpush.setMesssageType(SystemValue.MUSIC_JPUSH);
		jpush.setObject(JSONObject.toJSONString(musicOrder));
		jPushimpl.sendPush(jpush);
	}
	
	/**
	 * musicOrder 转 jpush
	 * */
	public static Jpush ToJpush(MusicOrder order){
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(SystemValue.gatewayid);
		jpush.setMesssageType(SystemValue.MUSIC_JPUSH);
		jpush.setObject(JSON.toJSONString(order));
		return jpush;
	}
	
	/**
	 * 调用服务器 接口 让服务器JPush推送到七寸屏
	 * */
	public static ResultMessage SendServer(MusicOrder order){
		Jpush jpush=MusicJpush.ToJpush(order);
		RequestParams params=new RequestParams();
		params.addBodyParameter("jpushJson",JSON.toJSONString(jpush));
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST,NetValue.MUSIC_CTRL_MUSIC, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				msg.setMessageInfo("网络错误，请检查网络");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				msg=JSONObject.parseObject(arg0.result,ResultMessage.class);
				
			}
		});
		
		System.out.println(msg.toString());
		return msg;
		
	}
	
	/**
	 * @Description:设置情景音乐
	 * @param:ThemeMusic 情景音乐实体类
	 * @param:type 接口类型
	 * @return:null
	 * */
	public static void SendThemeMusicToServer(ThemeMusic themeMusic,String type){
		RequestParams params=new RequestParams();
		params.addBodyParameter("thememusic", JSON.toJSONString(themeMusic));
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST, type, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				//不做处理
			}

			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//还不知道怎么返回一个数据过去
				msg=JSONObject.parseObject(arg0.result,ResultMessage.class);
			}
		});
	
		
	}
	

	/**
	 * @Description:外网下，点击设置情景音乐  通过JPush推送 情景音乐设置到七寸屏  
	 * 把appthememusic放到jpush.object上  
	 * */
	public static void SendThemeMusicToJpush(List<APPThemeMusic> list){
		
		Jpush jpush=new Jpush();
		jpush.setGatewayNo(SystemValue.gatewayid);
		jpush.setMesssageType(SystemValue.THEME_MUSIC_JPUSH);
		jpush.setObject(JSON.toJSONString(list));
		jpush.setTime(0L);
		
		RequestParams params=new RequestParams();
		params.addBodyParameter("jpushJson", JSON.toJSONString(jpush));
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_CTRL_THEMEMUSIC, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				ResultMessage message=JSONObject.parseObject(arg0.result, ResultMessage.class);
				if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
				}
			}
		});
		
	}
	
	
	/**
	 * 内网同步情景音乐到外网。
	 * */
	public static void SendThemeMusicToServer(List<APPThemeMusic> list){
		
		RequestParams params=new RequestParams();
		params.addBodyParameter("appthemeMusicJson",JSON.toJSONString(list));
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_SEND_THEMEMUSIC_TO_SERVER, params, new RequestCallBack<String>() {
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				ResultMessage message=JSONObject.parseObject(arg0.result, ResultMessage.class);
				if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
				}
			}
		});
		
	}

	
	
	
	
	
	
	
	
}
