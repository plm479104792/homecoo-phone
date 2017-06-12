package com.tuwa.smarthome.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.BaseDialog;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.APPThemeMusicDao;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.dao.VersionDao;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Item;
import com.tuwa.smarthome.entity.Packet;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.ThemeData;
import com.tuwa.smarthome.entity.ThemeMusic;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.DataConvertUtil;
import com.tuwa.smarthome.util.MD5Security16;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

public class SceneManegeActivity extends BaseActivity implements OnClickListener {
	private  SocketService socketService ;
	@Bind(R.id.tv_head_submit)
	TextView tvSubmit;
	@Bind(R.id.tv_head_back)
	TextView tvBack;
	@Bind(R.id.tv_head_title)
	TextView tvtitle;
	@Bind(R.id.gv_scenelist)
	GridView gvScene;

	private BaseDialog mBackDialog;
	protected Context mContext;
	private SceneAdapter sceneAdpter = null;
	private TextView tvAddScene;
	private TextView tvSceneToWg;
	private TextView tvSceneToServer;
	private TextView tvSceneToLocal;
	private Drawable drawable,drawLeftServer,drawLeftLocal;
	private int j=0;     //正在同步网关的情景关联设备指针

	private List<Theme> themeList = new ArrayList<Theme>();
	private List<ThemeDevice> themeDeviceList = new ArrayList<ThemeDevice>();

	/* 辅线程动态刷新页面 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x125:
				if(themeList!=null){  ////list不为null后填充适配器
					sceneAdpter = new SceneAdapter();
					gvScene.setAdapter(sceneAdpter);
				}
				 break;

			case 0x101:
				//通过网关或者服务器拉取情景
				getSceneFromGateway(socketService);
				break;
			case 4:
				List<ThemeMusic> themeMusiclist=new ArrayList<ThemeMusic>();
				try {
					themeMusiclist=JSONArray.parseArray(msg.obj.toString(),ThemeMusic.class);
					List<APPThemeMusic> appList=MusicUtil.ThemeMusicListToAppThemeMuiscList(themeMusiclist);
					new APPThemeMusicDao(SceneManegeActivity.this).UpdateOrSaveAppthemeMusic(appList);
				} catch (Exception e) {
					Log.i("outside","外网下没有设置情景联动音乐"+e);
				}
//				List<APPThemeMusic> list=new APPThemeMusicDao(SceneManegeActivity.this).GetAppthemeMusicListByGatewayNo();
//				Log.i("insidemusic",list.toString());
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scene_manege);
		ButterKnife.bind(SceneManegeActivity.this);// 注解工具声明
		
		// Activity和service绑定
	    Intent service = new Intent(SceneManegeActivity.this,SocketService.class);
	    bindService(service, conn, Context.BIND_AUTO_CREATE);

		tvtitle.setText("情景管理");

		initViews();
		
		Toast.makeText(this,"正在初始化情景，请稍后...",3000).show();  
		 
		Message msg=new Message();
		msg.what=0x101;
		handler.sendMessage(msg);
		
		if (NetValue.netFlag==NetValue.OUTERNET) {
			GetAllThemeMusic();          //获取该网关下的所有情景联动音乐
		}
		
	}
	
	//获取SocketService实例对象
	  ServiceConnection conn = new ServiceConnection() {  
	        @Override  
	        public void onServiceDisconnected(ComponentName name) {  
	              
	        }  
	          
	        @Override  
	        public void onServiceConnected(ComponentName name, IBinder service) {  
	            //返回一个SocketService对象  
	            socketService = ((SocketService.SocketBinder)service).getService();  
	            
	        	socketService.callSocket(new SocketCallBack(){
	    			@Override 
	    			public void callBack(TranObject tranObject) {
	    				
	    				switch (tranObject.getTranType()) {
	    				case NETMSG:   
	    					int netstatue=(Integer) tranObject.getObject();
	    					if ((NetValue.NONET==netstatue)) {  //本地连接失败
	    						if(!NetValue.autoFlag){
	    						 ToastUtils.showToast(SceneManegeActivity.this,"本地连接失败！", 1000);
	    						 NetValue.netFlag = NetValue.OUTERNET;
	    						}
	    					}
	    					break;
	    				case DEVMSG:

	    				default:
	    					break;
	    				}
	    			}
	    		});
	        }  
	    }; 


	// 空间适配器
	public class SceneAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return themeList.size();
		}

		@Override
		public Object getItem(int position) {
			return themeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if (view != null) {
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(SceneManegeActivity.this,
						R.layout.item_scene, null);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}

			Theme theme = themeList.get(position);
			int themeType=theme.getThemeType();
			String deviceNo=theme.getDeviceNo();
			if(themeType==SystemValue.SCENE_HARD){  //硬件情景显示硬件情景开关的位置
				UserSpaceDevice userSpace = new UserSpaceDevDao(SceneManegeActivity.this)
				    .findDeviceSpace(SystemValue.phonenum, deviceNo);
				if (userSpace != null) {
					String spacename = WebPacketUtil.getSpaceName(userSpace
							.getSpaceNo()); // 根据phonespaceid获取spacename
					holder.tvScenename.setText(spacename+"/"+theme.getThemeName());
				} else {
					Device devdto=new DevdtoDao(SceneManegeActivity.this).findDevByDeviceNoAndGatewayNo(deviceNo, SystemValue.gatewayid);
					String spacename="";
					if(devdto!=null){
					   spacename = WebPacketUtil.getSpaceName(devdto.getSpaceNo()); // 根据phonespaceid获取spacename
					}
					holder.tvScenename.setText(spacename+"/"+theme.getThemeName());
				}
			}else{
				holder.tvScenename.setText(theme.getThemeName());
			}
			
			holder.imSetting.setVisibility(View.VISIBLE);
			holder.imSetting.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sceneSetDialog(position); // 设置功能对话框
				}
			});

			return view;
		}

		class ViewHolder {        
			@Bind(R.id.tv_list_scenename)
			TextView tvScenename;
			@Bind(R.id.im_setting)
			ImageView imSetting;

			public ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
		}
	}

	private void sceneSetDialog(final int position) {
		final String items[] = { "情景配置", "情景删除" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		builder.setTitle("情景管理"); // 设置标题
		// builder.setIcon(R.drawable.set_tool);//设置图标，图片id即可
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0:
					 Theme theme = themeList.get(position);
					 SystemValue.themeSet=theme;
					 Intent sceneSetIntent=new Intent(SceneManegeActivity.this,SceneSetActivity.class);
					 sceneSetIntent.putExtra("themeNo",theme.getThemeNo());
					 sceneSetIntent.putExtra("themename",theme.getThemeName());
					 sceneSetIntent.putExtra("devstrid",theme.getDeviceNo());
					 sceneSetIntent.putExtra("themetype",theme.getThemeType());
					 sceneSetIntent.putExtra("themestate",theme.getThemeState());
					 startActivity(sceneSetIntent);
					//设置切换动画，从右边进入，左边退出
				     overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
					break;
				case 1:
					Theme deltheme = themeList.get(position);

					showDeleteDialog(position); // 情景删除
					DeleteThemeMusic(deltheme.getThemeNo());	//删除情景音乐

					break;
				case 2:

					break;
				default:
					break;
				}

			}
		});
		builder.create().show();
	}

	/************ 添加情景 **************/
	private void addSceneDialog() {

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_dialog_tvandet,
				(ViewGroup) findViewById(R.id.dialog));
		final TextView tvSpaceName = (TextView) layout.findViewById(R.id.tv_dialog_name);
		tvSpaceName.setText("情景名称:");
		final EditText etSceneName = (EditText) layout.findViewById(R.id.et_dialog_content);
		etSceneName.setHint("请输入情景名称");
		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("添加情景").setView(layout);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 点击确定对话框不消失
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String scenename = etSceneName.getText().toString();
				if (scenename.equals("")) {
					ToastUtils.showToast(SceneManegeActivity.this,"请输入新情景名称", 1000);
				} else if (scenename.length() > 10) {
					ToastUtils.showToast(SceneManegeActivity.this,"情景名称长度应小于5", 1000);
					
				} else {
					addTheme(scenename);

					try {// 对话框消失
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {// 对话框消失
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).show();
	}

	/************ 本地/远程添加情景 *****************/
	private void addTheme(String scenename) {

		Theme theme = new Theme();
		theme.setThemeName(scenename);
		theme.setDeviceNo(NetValue.DEVID_NULL);  //自定义情景填充默认设备
		theme.setThemeState("00000000");   //自定义情景themestate填充8个0，对应转换为8个字节
		theme.setThemeType(4);  //通过对话框添加的都是自定义类型的
		theme.setGatewayNo(SystemValue.gatewayid);
		
		String strTheme = theme.getGatewayNo() + theme.getDeviceNo()
				+ theme.getThemeName() + theme.getThemeState(); // GatewayId+deviceNo+ThemeName+ThemeState生成themeid

		String themeNo="";
		try {
			themeNo=MD5Security16.md5_16(strTheme);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		theme.setThemeNo(themeNo);

		if (new ThemeDao(SceneManegeActivity.this).isUseableTheme(themeNo)) {
			themeList.add(theme);
			 //辅助线程更新页面
			 Message msg=new Message();
			 msg.what=0x125;
			 handler.sendMessage(msg);
			
			new ThemeDao(SceneManegeActivity.this).addOrUpdateTheme(theme);   //添加theme到本地数据库
			
			// ===更新version_scene 时间戳===
			Version version = SystemValue.getVersion(SystemValue.VERSION_SCENE);
			new VersionDao(null).addorUpdateVerson(version);
		}else {
			ToastUtils.showToast(SceneManegeActivity.this,"情景模式"+scenename+"已经存在！", 1000);
		}
	}


	/** 删除对话框 **/
	protected void showDeleteDialog(final int position) {
		final Theme theme = themeList.get(position);
		final String scenename = theme.getThemeName();
		mBackDialog = BaseDialog.getDialog(SceneManegeActivity.this, "提示",
				"确认要删除情景" + scenename + "吗？", "确认",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						 //先发送消息后删除
						 themeList.remove(position);
						 ToastUtils.showToast(SceneManegeActivity.this,"删除情景" + scenename+"成功", 1000);
						 sceneAdpter.notifyDataSetChanged();
						 
						 //发送报文到网关删除情景
						 SocketPacket  deleteTheme=WebPacketUtil.deleteThemeSetPacket();
						 String packtData=theme.getThemeNo()+theme.getThemeState();
						 deleteTheme.setData(packtData);
						 
						 sentCmdByServerOrGateway(deleteTheme);//### 发送删除情景报文到网关
						 
						 //从theme表中删除该情景
						 new ThemeDao(SceneManegeActivity.this).deleteByThemeNo(theme);
						//从themeDevice表中删除该themeno关联的所有设备
						 new ThemeDeviceDao(SceneManegeActivity.this).deleteThemeDeviceAllByThemeNo(theme.getThemeNo());
						//删除该情景设置的音乐
						 new APPThemeMusicDao(SceneManegeActivity.this).DeleteAppThemeMusic(theme.getThemeNo());
						 mBackDialog.dismiss();
						
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
	
	

	/***提示修改情景设置信息到网关***/
	@OnClick(R.id.tv_head_submit)
	public void syncScene2Gateway() {
		showLoadingDialog("正在同步情景到网关！");
		j=0;  //每当新情景，指针重置为0
		timerhandler.post(timerrunnable);
	}

	/*** 返回 ***/
	@OnClick(R.id.tv_head_back)
	public void back() {
		finish();
	}


	@Override
	protected void initViews() {
		tvAddScene = (TextView) findViewById(R.id.tv_addscene);
		tvAddScene.setVisibility(View.VISIBLE);
		tvSubmit.setVisibility(View.INVISIBLE);
		
		tvSceneToWg = (TextView) findViewById(R.id.tv_scenetowg);
		tvSceneToServer = (TextView) findViewById(R.id.tv_scenetoserver);
		tvSceneToLocal = (TextView) findViewById(R.id.tv_scenetolocal);
		
		//动态更新NEW图标
		drawable = getResources().getDrawable(R.drawable.icon_new);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		drawLeftServer = getResources().getDrawable(R.drawable.leftmenu_toserver_sel);
		drawLeftServer.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		drawLeftLocal = getResources().getDrawable(R.drawable.leftmenu_downlocal_sel);
		drawLeftLocal.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		
		tvSceneToServer.setCompoundDrawables(drawLeftServer,null,null,null);
		tvSceneToLocal.setCompoundDrawables(drawLeftLocal,null,null,null);
		
		tvAddScene.setOnClickListener(addSceneOnClickListener);
		tvSceneToWg.setOnClickListener(this);
		tvSceneToServer.setOnClickListener(this);
		tvSceneToLocal.setOnClickListener(this);
	}

	@Override
	protected void initDatas() {
		
		// 根据网关号从数据库加载相应设备
		List<Theme> allThemeList = new ArrayList<Theme>();
		allThemeList = new ThemeDao(SceneManegeActivity.this)
				.themeListByGatewayNo(SystemValue.gatewayid);
		List<Theme> localthemeList = WebPacketUtil
				.findCustomThemeFromThemesAll(allThemeList);
		themeList.addAll(localthemeList);
		
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x125;
		handler.sendMessage(msg);
	}

	private OnClickListener addSceneOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			addSceneDialog(); // 添加新情景
		}
	};


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_scenetowg:
			showLoadingDialog("正在同步情景到网关！");
			j=0;  //每当新情景，指针重置为0
			timerhandler.post(timerrunnable);
		
			break;
		}
		
	}
	
	   //连续执行情景线程同步到网关
		Handler timerhandler=new Handler();  
		Runnable timerrunnable=new Runnable() {  
		    @Override  
		    public void run() {  
		    	sysnSceneToGatewayLoop();
		    	try {
					Thread.sleep(1000); //每隔2秒执行一个情景状态
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    }  
		};
    
	
	/**
	 * 同步情景及其联动设置到网关
	 */
	private void sysnSceneToGatewayLoop() {
		//设备安装信息不需同步到网关2016.04.16
		
        if(j<themeList.size()){
        	 Theme theme=themeList.get(j);
    		 ArrayList<Item> triggerList=new ArrayList<Item>();
    		 Item  trggerItem=new Item();
    		 trggerItem.setDeviceNo(theme.getDeviceNo());      //触发器item的id设置为theme_mac
    		 trggerItem.setDeviceStateCmd(theme.getThemeState());
    		 trggerItem.setDataLen(4);   //触发器的data长度
    		 trggerItem.setDeviceType(202);  //填充默认的情景类型202
    		 triggerList.add(trggerItem);
    		
    		themeDeviceList = new ThemeDeviceDao(SceneManegeActivity.this)
    		              .findDevstateBythemeNo(theme.getThemeNo());
    		int itemSize=themeDeviceList.size();
    		 ArrayList<Item> sceneList=new ArrayList<Item>();
    		 for (int i = 0; i < themeDeviceList.size(); i++) {
    				ThemeDevice themedevice=themeDeviceList.get(i);
    				Item  deviceitem=new Item();
    				String deviceNo= themedevice.getDeviceNo();
    				Device device=new DevdtoDao(SceneManegeActivity.this)
    				       .findDevByDeviceNoAndGatewayNo(deviceNo, SystemValue.gatewayid);
    				
    				if(device!=null){
    					deviceitem.setDeviceNo(themedevice.getDeviceNo());
        				deviceitem.setDeviceStateCmd(themedevice.getDeviceStateCmd());
        				int datalen=themedevice.getDeviceStateCmd().length();
        				deviceitem.setDataLen(datalen);
        				deviceitem.setDeviceType(device.getDeviceTypeId());
        				sceneList.add(deviceitem);
    				}
    		}
    		
    		ThemeData themeData=new ThemeData();
    		themeData.setDeviceNo(theme.getDeviceNo());
    		themeData.setThemeNo(theme.getThemeNo());
    		themeData.setThemeState(theme.getThemeState());
    		themeData.setThemeType(theme.getThemeType());
    		themeData.setThemeName(theme.getThemeName());
    		themeData.setTriggerNum(1);
    		themeData.setDeviceNum(itemSize);    //联动的设备数量
    		themeData.setTriggerList(triggerList);
    		themeData.setDeviceList(sceneList);
    		
    		SocketPacket socketPacket=WebPacketUtil.sceneSet2Packet(themeData);
    		
    		sentCmdByServerOrGateway(socketPacket);
    		
    	     timerhandler.post(timerrunnable);
			 j++;
	   }else {
			dismissLoadingDialog();
			SocketPacket socketPacket=WebPacketUtil.finnishThemeSetPacket();
			
			sentCmdByServerOrGateway(socketPacket); //判断发送到网关还是服务器
			
	   }
	}
	
	private void sentCmdByServerOrGateway(SocketPacket socketPacket) {
		switch (NetValue.netFlag) {
		case NetValue.OUTERNET: // 外网
			//将命令封装为字符串发送到服务器
			byte[] sentBytes=WebPacketUtil.packetToByteStream(socketPacket);
			sendCmdToServer(sentBytes,0); // 发送到服务器的命令串
			break;
		case NetValue.INTRANET: // 内网
			socketService.sentPacket(socketPacket); // 发送请求认证报文到网关
			break;
		}
	}

	@Override  
    protected void onDestroy() {  
		unbindService(conn);
        super.onDestroy(); //注意先后  
    } 
	
	
	    /**
		 * 从服务器获取情景设置报文
		 */
		public void getThemePacketFromServer() {
			RequestParams params = new RequestParams();
			params.addBodyParameter("gatewayNo",SystemValue.gatewayid);
	
			HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
			utils.send(HttpMethod.POST, NetValue.THEMEPAKET_FROM_SERVER_URL, params,
				new RequestCallBack<String>() {
	
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						initDatas();
					}
	
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
					  if(SystemValue.themeClean==true){ //第一次同步情景时清空本地情景及其设置
						  new ThemeDao(null).deleteAllByGatewayNo(SystemValue.gatewayid);		
						  new ThemeDeviceDao(null).deleteAllByGatewayNo(SystemValue.gatewayid);
						  SystemValue.themeClean=false;
					  }
					  
						// 根据返回值校验登录是否成功
						Gson gson=new Gson();
						ResultMessage message=gson.fromJson(arg0.result, ResultMessage.class);
					
						if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
		                    System.out.println("====***情景报文====="+message.getObject());
		                	List<Packet> themePktlist =JSONArray.parseArray((String)message.getObject(), Packet.class);
		                	for(int i=0;i<themePktlist.size();i++){
		                		String strPacket=themePktlist.get(i).getPacket();
		              		    byte[] themePacket = DataConvertUtil.toByteArray(strPacket);
		  				        // 字节数组转换为对象
		  			            WebPacketUtil.byteToSocketPacket(themePacket);
		                	}
		                	
		                	initDatas(); //显示情景到前台
						}
					}
				});
		}
		
		/**
		 * @Description:删除情景音乐
		 * @author xiaobai
		 * @param themeNo
		 * */
		public void DeleteThemeMusic(String themeNo){
			RequestParams params=new RequestParams();
			params.addBodyParameter("themeNo", themeNo);
			params.addBodyParameter("gatewayNo",SystemValue.gatewayid);
			HttpUtils httpUtils=new HttpUtils();
			httpUtils.send(HttpMethod.POST, NetValue.MUSIC_DELETE_THEME_MUSIC, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					ResultMessage msg=JSONObject.parseObject(arg0.result, ResultMessage.class);
					if (msg.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
						System.out.println("删除情景音乐成功!");
					}
				}
			});
		}
		
		
		    
			//设置和情景模式页面判断并同步情景
		public void getSceneFromGateway(SocketService socketService){
		       SystemValue.themeClean=true;
			
				switch (NetValue.netFlag) {
				case NetValue.OUTERNET: // 外网
					getThemePacketFromServer(); //外网从服务器直接拉取情景报文
					break;
				case NetValue.INTRANET: // 内网
	
					if(socketService!=null){
						SocketPacket sceneAllPacket = WebPacketUtil.getThemeAllPacket();
						socketService.sentPacket(sceneAllPacket);
						
						 new Handler().postDelayed(new Runnable(){    
			                    public void run() {    
                                   initDatas();
			                    }    
			            }, 3000);
					
					}else{
						System.out.println("========socketService为空");
					}
					break;
				}
			}
		
		
		/**
		 * 获取所有的情景联动音乐
		 * */
		private void GetAllThemeMusic(){
			RequestParams params=new RequestParams();
			params.addBodyParameter("gatewayNo",SystemValue.gatewayid);
			HttpUtils httpUtils=new HttpUtils();
			httpUtils.send(HttpMethod.POST, NetValue.MUSIC_GET_ALL_THEME_MUSIC, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
//					ToastUtils.showToast(SceneManegeActivity.this, "请检查网络连接！",1000);
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					ResultMessage message=JSON.parseObject(arg0.result, ResultMessage.class);
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
							Message msg = new Message();
							msg.what = 4;
							msg.obj = message.getObject().toString();
							handler.sendMessage(msg);
					} 
				}
			});
		}
}
