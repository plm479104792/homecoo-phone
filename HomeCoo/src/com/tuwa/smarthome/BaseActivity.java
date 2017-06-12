package com.tuwa.smarthome;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import object.dbnewgo.client.BridgeService;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.google.gson.Gson;
import com.homecoolink.global.MyApp;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.DatagramSocketPhoneServer;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.util.DataConvertUtil;
import com.tuwa.smarthome.util.MusicJpush;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.VerifyUtils;
import com.tuwa.smarthome.util.WebPacketUtil;
import com.tuwa.smarthome.view.HandyTextView;



public abstract class BaseActivity extends FragmentActivity {
	protected FlippingLoadingDialog mLoadingDialog;
	/**
	 * 屏幕的宽度、高度、密度
	 */
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected float mDensity;
	private BaseDialog mBackDialog;
	// SharedPreferences共享数据
	SharedPreferences preferences; // 保存用户的id
	SharedPreferences.Editor editor;
	
	private ReceiveBroadCast  receiveBroadCast;

	protected List<AsyncTask<Void, Void, Integer>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Integer>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 获取只能被本应用程序读、写的SharedPreferences对象
		preferences = getSharedPreferences("tuwa", Context.MODE_PRIVATE);
		editor = preferences.edit();
		
//		mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
		
		
		//注册广播接收,关闭所有的activity
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SystemValue.ACTION_FINISH_ACTIVITY);    //销毁activity
        registerReceiver(receiveBroadCast, filter);
        
    	SystemValue.activity=BaseActivity.this;
    	
//    	MyApp.app.addActivity(this);
	}
	
	

	@Override
	protected void onDestroy() {
		clearAsyncTask();
		unregisterReceiver(receiveBroadCast);  //取消广播注册
		super.onDestroy();
	}

	/** 初始化视图 **/
	protected abstract void initViews();

	/** 初始化事件 **/
	protected abstract void initDatas();

	protected void putAsyncTask(AsyncTask<Void, Void, Integer> asyncTask) {
		mAsyncTasks.add(asyncTask.execute());
	}

	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Integer>> iterator = mAsyncTasks
				.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Integer> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
	}

	protected void showLoadingDialog(String text) {
		if(mLoadingDialog==null){
			mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");
			mLoadingDialog.setText(text);
		}else{
			mLoadingDialog.setText(text);
		}
		mLoadingDialog.show();
	}

	protected void dismissLoadingDialog() {
	  if(mLoadingDialog!=null){
		  if (mLoadingDialog.isShowing()) {
				mLoadingDialog.dismiss();
			}
	  }
		
	}

	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** 显示自定义Toast提示(来自res) **/
	protected void showCustomToast(int resId) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text))
				.setText(getString(resId));
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast1(String text) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastRoot);
		toast.show();
	}

	/** Debug输出Log日志 **/
	protected void showLogDebug(String tag, String msg) {
		Log.d(tag, msg);
	}

	/** Error输出Log日志 **/
	protected void showLogError(String tag, String msg) {
		Log.e(tag, msg);
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 通过Action跳转界面 **/
	protected void startActivity(String action) {
		startActivity(action, null);
	}

	/** 含有Bundle通过Action跳转界面 **/
	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 含有标题和内容的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).show();
		return alertDialog;
	}

	/** 含有标题、内容、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	/** 含有标题、内容、图标、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			int icon, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setIcon(icon)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}
	
	/**退出对话框**/
	protected void initExitDialog() {
		mBackDialog = BaseDialog.getDialog(BaseActivity.this, "提示",
				"确认要退出HomeCoo系统吗？", "确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SystemValue.loginFlag=false; //标识退出登录
						NetValue.sIsConneted=false;
						//退出系统前关闭后台socketservice
						Intent startIntent = new Intent(BaseActivity.this, SocketService.class);  
			            stopService(startIntent); 
			            
//			    		Intent intent = new Intent();
//			    		intent.setClass(BaseActivity.this, BridgeService.class);
//			    		stopService(intent);
			    		
						 //发送广播清空当前的activity栈
						Intent finishIntent = new Intent();  //Itent就是我们要发送的内容
						finishIntent.setAction("ACTIVITY_FINISH");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
			            sendBroadcast(finishIntent);   //发送广播
			    		
			    		SystemValue.deviceSysnFlag=false;
			        	
			        	NetValue.IP_SCAN_FLAG=false;
			        	NetValue.IP_CONNECT_FLAG=false;
			            
			        	//退出系统的时候，需要关闭UDP广播
			        	MusicUtil.StopDatagramSocketServer(BaseActivity.this);
			        	
			        	
						dialog.dismiss();
						finish();
					}
				}, "取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		mBackDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mBackDialog.show();
	}
	
	   /**
	    * 网络切换通用回调
	    * @param socketService
	    * @param textview
	    */
	 protected  void netWorkSwitch(SocketService socketService,TextView textview){
		 if(VerifyUtils.isEmpty(SystemValue.gatewayid)){
			 ToastUtils.showToast(BaseActivity.this, "请先添加网关！", 2000);
		 }else{
			 switch (NetValue.netFlag) {
				case NetValue.OUTERNET: // 当前为外网，切换到内网
					NetValue.netFlag=NetValue.INTRANET;   //本地网络
			        SystemValue.NETRESULT_SHOW_FLAG=true;  //显示网络结果标识
					 if (!NetValue.socketauthen) {  
						socketService.socketConnect(NetValue.LOCAL_IP);  //建立连接并认证，认证通过请求所有设备状态
						System.out.println("==网络切换建立建立socket连接===");
					  }   
					    NetValue.inputflag=true;
						textview.setText("本地");
						ToastUtils.showToast(BaseActivity.this, "系统已切换网络为本地模式！", 1000);
						NetValue.netFlag=NetValue.INTRANET;
						
						socketService.startVisitGatewayThread();////开启定时访问网关心跳包
						
					break;
				case NetValue.INTRANET: // 当前为内网，切换到外网
					
					NetValue.inputflag=false;
			 		NetValue.netFlag=NetValue.OUTERNET;   //远程网络
			 		textview.setText("远程");
			 		socketService.stopVisitGatewayThread();  //停止定时访问网关心跳包
			 		socketService.socketClose();
			 		ToastUtils.showToast(BaseActivity.this, "系统已切换网络为远程模式！", 1000);
			 		
			 		//TODO 当前内网，切换到外网
			 		/**
			 		 * 内网设置的情景联动音乐，外网下通过Jpush 推送到七寸屏上。七寸屏保存在本地sqlite上。
			 		 * 这里需要同步下
			 		 * */
			 		List<APPThemeMusic> list=new APPThemeMusicDao(BaseActivity.this).GetAppthemeMusicListByGatewayNo();
			 		if (list.size()>0) {
			 			Log.i("outside",list.toString());
						MusicJpush.SendThemeMusicToServer(list);
					}
			 		
			 	
					break;
				}
			 
		
		 }
		 
	 } 
	
		
	/** 默认退出 **/
	protected void defaultFinish() {
		super.finish();
	}
	
	/**
	 * 连续点击
	 */
	public static long lastTime; //最后的点击时间
	public abstract class ClickEvent implements OnClickListener {
	public boolean doubclickflag=false;	
	@SuppressWarnings("unused")
	private int position;
	
		public abstract void singleClick(View v); 
	
		@Override
		public void onClick(View v) {
			 
			if (IsDoubClick()) { //判断间隔
				if (!doubclickflag) {
					ToastUtils.showToast(BaseActivity.this, "请勿连续频繁操作！", 1000);
					doubclickflag=true;
				}
			}else {
				singleClick(v);
				doubclickflag=false;
			}
		}

		public boolean IsDoubClick() {
		    long time = System.currentTimeMillis() - lastTime;
		    if(time <50) {
		     lastTime = System.currentTimeMillis();
		     return true;
		    }
		    lastTime = System.currentTimeMillis();
	    	return false;
		  }
   }
	
	
	
	/**
	 * 防暴力点击
	 */
	public static long lastTimeClick; //最后的点击时间
	public abstract class DoubleClickEvent implements OnClickListener {
	public boolean doubleflag=false;	
	
		public abstract void singleClick(View v); 
	
		@Override
		public void onClick(View v) {
			 
			if (IsDoubClick()) { //判断间隔
				if (!doubleflag) {
					ToastUtils.showToast(BaseActivity.this, "请勿连续频繁操作！", 1000);
					doubleflag=true;
				}
			}else {
				singleClick(v);
				doubleflag=false;
			}
		}

		public boolean IsDoubClick() {
		    long time = System.currentTimeMillis() - lastTimeClick;
		    if(time <500) {
		    lastTimeClick = System.currentTimeMillis();
		     return true;
		    }
		    lastTimeClick = System.currentTimeMillis();
	    	return false;
		  }
   }

//	public abstract class CheckClickEvent implements OnCheckedChangeListener{
//		public boolean doubclickflag=false;	
//		private int position;
//		
//			public abstract void singleClick(View v,boolean isChecked); 
//		
//			@Override
//			public void onCheckedChanged(CompoundButton v, boolean isChecked) {
//				if (IsDoubClick()) { //判断间隔
//					if (!doubclickflag) {
//						Toast.makeText(BaseActivity.this,"请勿连续频繁操作！",1000).show();
//						doubclickflag=true;
//					}
//				}else {
//					singleClick(v,isChecked);
//					doubclickflag=false;
//				}
//			}
//		
//
//			public boolean IsDoubClick() {
//			    long time = System.currentTimeMillis() - lastTime;
//			    System.out.println("===系统当前时间==="+System.currentTimeMillis());
////			    System.out.println("连续点击的时间间隔"+time);
//			    if(time <50) {
//			     lastTime = System.currentTimeMillis();
//			     return true;
//			    }
//			    lastTime = System.currentTimeMillis();
//		    	return false;
//			  }
//	}
	
		//android键返回
      @Override 
	  public boolean onKeyDown(int keyCode, KeyEvent event) { 
	     if ((keyCode == KeyEvent.KEYCODE_BACK)) { 
	         System.out.println("按下了back键   onKeyDown()");  
	         if (isTaskRoot()) {  //最后一个activity
	        	 initExitDialog(); 
	        	  return false; 
			   }else {
				    super.finish();
				  return false;
			   }
	     }else { 
	         return super.onKeyDown(keyCode, event); 
	     } 
     } 
      
      
      /**
  	 * 发送字节字符串到服务器
  	 */
  	public void sendCmdToServer(byte[] sentBytes,final int cmdType) {
  		String  strCmd=DataConvertUtil.toHexUpString(sentBytes);
  		int strLength=strCmd.length();
  		String strSub="42424141"+strCmd.substring(8, strLength);
  		
  		RequestParams params = new RequestParams();
  		params.addBodyParameter("devicePacketJson",strSub);
  		
//  	System.out.println("===发送到服务器的命令==="+strSub);
  		Log.i("343", "===发送到服务器的命令==="+strSub);
  		
  		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
  		utils.send(HttpMethod.POST, NetValue.DEVICE_CONTROL_URL, params,new RequestCallBack<String>() {

  			@Override
  			public void onFailure(HttpException arg0, String arg1) {
  				ToastUtils.showToast(BaseActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
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
//  						 SystemValue.sceneSysnFlag=true;  //已经同步过情景 
  					   }
  					   
  						System.out.println("控制设备服务器返回"+ message.getMessageInfo());
  					} else {
  						ToastUtils.showToast(BaseActivity.this,message.getMessageInfo(), 1000);
  					}
  				}
  			}
  		});
  	}

  	
  	 public void setAliasAndTags() {
	    	//设置别名，JPush根据别名广播给别名用户
			JPushInterface.setAliasAndTags(getApplicationContext(), SystemValue.gatewayid, null, new TagAliasCallback() {
				
				@SuppressWarnings("unused")
				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					String logs ;
			        switch (arg0) {
			        case 0:
			            logs = "Set tag and alias success";
			      //      Log.i(TAG, logs);
			            System.out.println("===设置别名成功==="+SystemValue.gatewayid);
			            // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
			            break;
			        case 6002:
			            logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
			     //       Log.i(TAG, logs);
			            // 延迟 60 秒来调用 Handler 设置别名
			            System.out.println("===设置别名失败！！！===");
			      //      mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, String.valueOf(wgid)), 1000 * 60);
			            break;
			        default:
			            logs = "Failed with errorCode = " + arg0;
			            System.out.println("===设置别名失败！未知原因===");
			//            Log.e(TAG, logs);
			        }
			         
			//        ExampleUtil.showToast(logs, getApplicationContext());
				}
			});
			
		}
  	 
  	    /**
  	     * 向服务器发送请求所有设备状态报文
  	     */
  	    public void sysnDeviceFromServer(SocketService service){
  	    	SocketPacket devAllPacket = WebPacketUtil
					   .getDevAllStatePacket(SystemValue.gatewayid);
  	    	  
  	    	switch (NetValue.netFlag) {
  			case NetValue.OUTERNET: // 外网
  				//将命令封装为字符串发送到服务器
  				byte[] sentBytes=WebPacketUtil.packetToByteStream(devAllPacket);
  				sendCmdToServer(sentBytes,1);   //发送到服务器的命令串
  				break;
//  			case NetValue.INTRANET: // 内网在认证成功后请求
//  			
//  				service.sentPacket(devAllPacket); // 发送请求所有设备到网关
//  				System.out.println("========内网发送");
//  				break;
  			}
  	    }
  	    
  	    
  	    /**
  	     * 加载设备的名称和位置
  	     * @param tvSite
  	     * @param tvName
  	     * @param devdto
  	     */
		public void initDeviceNameAndSite(TextView tvSite, TextView tvName,
				Device devdto) {
		
			UserSpaceDevice userSpace = new UserSpaceDevDao(BaseActivity.this)
					.findDeviceSpace(SystemValue.phonenum, devdto.getDeviceNo());
			if (userSpace != null) {
				String spacename = WebPacketUtil.getSpaceName(userSpace
						.getSpaceNo()); // 根据phonespaceid获取spacename
				tvSite.setText(spacename+"/" );
				tvName.setText(userSpace.getDeviceName());
				devdto.setSpaceNo(userSpace.getSpaceNo());
			} else {
//				String spaceno=devdto.getSpaceNo();
//				if(spaceno==null){
//					devdto.setSpaceNo("0");
//				}
//				String spacename = WebPacketUtil.getSpaceName(devdto.getSpaceNo()); // 根据phonespaceid获取spacename
//				tvSite.setText(spacename+"/" );
//				tvName.setText(devdto.getDeviceName());
				
				tvSite.setText("位置待定"+"/" );
				tvName.setText(devdto.getDeviceName());

			}
		}
	
		
	
		
	    /**
	     * 加载自定义设备位置
	     * @param schedule
	     * @return
	     */
		public String initDeviceCustomSite(String deviceno) {
			String spacename="";
			if(!VerifyUtils.isEmpty(deviceno)){
				UserSpaceDevice userSpace = new UserSpaceDevDao(BaseActivity.this)
				     .findDeviceSpace(SystemValue.phonenum, deviceno);
				if (userSpace != null) {
					 spacename = WebPacketUtil.getSpaceName(userSpace.getSpaceNo()); // 根据phonespaceid获取spacename
				} else {
					 spacename = WebPacketUtil.getSpaceName(deviceno); // 根据phonespaceid获取spacename
				}
			}
			return spacename;
		}
		
		
		/**
		 * 基类中放置广播接收器，关闭所有的activity
		 * @author WSN-520
		 *
		 */
		public class ReceiveBroadCast extends BroadcastReceiver
		{
		        @Override
		        public void onReceive(Context context, Intent intent)
		        {
		            //得到广播中得到的数据，并显示出来
		        	System.out.println("广播关闭当前activity。。。");
		            finish();
		        }
		}

		
}
