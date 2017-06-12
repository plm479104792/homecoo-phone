package com.tuwa.smarthome.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
import com.tuwa.smarthome.R.id;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.dao.SpaceDao;
import com.tuwa.smarthome.dao.ThemeDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.dao.VersionDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.MD5Security16;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.VerifyUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import et.song.db.ETDB;
import et.song.etclass.ETGroup;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class DeviceManegeActivity extends BaseActivity implements
		OnCheckedChangeListener, OnClickListener {
	// activity绑定service1
	private SocketService devService;

	@Bind(R.id.tv_head_submit)
	TextView tvSubmit;
	@Bind(R.id.tv_head_back)
	TextView tvBack;
	@Bind(R.id.tv_head_title)
	TextView tvtitle;
	@Bind(R.id.gv_devicelist)
	GridView gvDevices;
	// @Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;

	private TextView tvDevFromWg, tvDevToServer, tvDevToLocal;
	private Version pVersion;   //该用户对应的设备版本

	private Drawable drawable,drawLeftServer,drawLeftLocal;
	private BaseDialog mBackDialog;
	private List<Device> devdtoList = new ArrayList<Device>();
	private DeviceAdapter deviceAdpter = null;
	private EditText etdevname;
	private Spinner siteSpinner;
	private List<Space> spacelist = new ArrayList<Space>(); // 房间对象列表
	private ArrayAdapter<String> siteAdapter;

	private Device devset; // 当前设置的dev
	private Space vSpacetypeSet=new Space(); // 选中的房间
	private int versionResult=0;   //版本标识

	private int mdevCategoryId;
	private EditText etScene1, etScene2, etScene3, etScene4;
	private int vSpaceTypeId = 0; // 默认为0不选择防区类型
	private List<UserSpaceDevice> userSpaceList = new ArrayList<UserSpaceDevice>();
	private boolean deviceSaveFlag=true;  //保存设备信息到服务器
	
	public boolean initDataFlag=false;  //加载设备标志
	public String[] sceneNames;
	public int i=0;

	/* 辅线程动态刷新页面 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x129:
				if(devdtoList!=null){  //list不为null后填充适配器
					if(deviceAdpter==null){
						deviceAdpter = new DeviceAdapter();
						gvDevices.setAdapter(deviceAdpter);
					}else{
						deviceAdpter.notifyDataSetChanged();
					}
				}
			 break;

//			case 0x102:
//				initDeviceData();   //显示设备信息
//				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_manege);
		ButterKnife.bind(DeviceManegeActivity.this);// 注解工具声明
		
		// Activity和service绑定2
		Intent service = new Intent(DeviceManegeActivity.this,SocketService.class);
		bindService(service, devconn, Context.BIND_AUTO_CREATE);

		initViews();
		
//    	switch (NetValue.netFlag) {
//		case NetValue.OUTERNET: // 外网
//			checkVersionFromServer();  //从服务器对比版本信息
//			getDeviceFromServer();
//			break;
//		case NetValue.INTRANET: // 内网在认证成功后请求
			getDeviceFromServer();   //加载设备的配置信息
//			initDatas();
//			break;
//		}
			
	}
    
	/**
	 * 从服务器对比版本信息
	 */
	private void checkVersionFromServer() {
		String phonenum=SystemValue.phonenum;
	    int versionType=SystemValue.VERSION_DEVICE;
		pVersion = new VersionDao(DeviceManegeActivity.this)
		        .getVersionByPhonenumAndVersionType(phonenum,versionType);
		if(pVersion==null){
			pVersion=SystemValue.getinitVersion(versionType);  //获取系统的版本时间戳
		}
			Gson gversion = new Gson();
			String strVersion=gversion.toJson(pVersion);
		
			RequestParams params = new RequestParams();
			params.addBodyParameter("versionJson",strVersion);

			HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
			utils.send(HttpMethod.POST, NetValue.CHECK_VERSION_URL, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							initDatas();  //版本相同
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							Gson gson = new Gson();
							ResultMessage message = gson.fromJson(arg0.result,
									ResultMessage.class);
							if (message != null) {
								if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
									System.out.println("检查版本"+ message.getMessageInfo());
									System.out.println("匹配结果"+ message.getObject());
									
									versionResult = Integer.valueOf((String)message.getObject());
								
									//后台在代码中根据版本比对结果，在本地和服务器之间同步
									if(versionResult==1){//服务器配置信息最新
										 getDeviceFromServer();
									}else if(versionResult==2){//本地配置信息最新直接加载
										 initDatas();  //版本相同
										 sysnDeviceToServer();
									}else {
										initDatas();  //版本相同
									}
									
								} else {
								    System.out.println(message.getMessageInfo());
								}
							}
						}
					});
	}

	// 获取SocketService实例对象
	ServiceConnection devconn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 返回一个SocketService对象
			devService = ((SocketService.SocketBinder) service).getService();

			devService.callSocket(new SocketCallBack() {
				@Override
				public void callBack(TranObject tranObject) {

					switch (tranObject.getTranType()) {
					case NETMSG:
						int netstatue = (Integer) tranObject.getObject();
						if ((NetValue.NONET == netstatue)) { // 本地连接失败
							ToastUtils.showToast(DeviceManegeActivity.this,"本地连接失败！", 1000);
						}
						break;
					case DEVMSG:

						// 根据网关号从数据库加载相应设备
						devdtoList = new DevdtoDao(DeviceManegeActivity.this)
								.findDevListByGatewayidAndPhonenum(SystemValue.gatewayid);
						
						if(deviceAdpter==null){
							deviceAdpter = new DeviceAdapter();
							gvDevices.setAdapter(deviceAdpter);
						}
						deviceAdpter.notifyDataSetChanged();

					default:
						break;
					}
				}

			});
		}
	};


	public class DeviceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return devdtoList.size();
		}

		@Override
		public Object getItem(int position) {
			return devdtoList.get(position);
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
				view = View.inflate(DeviceManegeActivity.this,R.layout.item_dev_set, null);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}

			Device devdto = devdtoList.get(position);
			
			initDeviceNameAndSite(holder.tvDevSite,holder.tvDevName,devdto);//初始化自定义房间名称和位置
			
			// 红外设备类型数据添加到红外码库中
			if (devdto.getDeviceTypeId() == 105) { 
				addOrUpdateGroupToInfraRed(devdto);   //同步服务器的设备信息时， 添加红外遥控位置到红外数据库
			}
			
			
			holder.imSetting.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deviceSetDialog(position); // 设置功能对话框
				}
			});

			return view;
		}

		class ViewHolder {        
			@Bind(R.id.tv_set_devSite)
			TextView tvDevSite;
			@Bind(R.id.tv_set_devtypeName)
			TextView tvDevName;
			@Bind(R.id.im_setting)
			ImageView imSetting;

			public ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
		}
	}

	/**
	 * 房间设置对话框
	 */
	private void deviceSetDialog(final int position) {
		int categoryId =devdtoList.get(position).getDeviceCategoryId();
		String[] items = new String[3];
		if (categoryId == 4) { // 硬件情景类开关增加设置情景名称
			items[0] = "更改";
			items[1] = "删除";
			items[2] = "设置情景名称";
		} else {
			items[0] = "更改";
			items[1] = "删除";
			items[2] = "";
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		builder.setTitle("产品设置"); // 设置标题

		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0:
					devUpdateDialog(position);
					break;
				case 1:
					showDeleteDialog(position);
					break;
				case 2:
					showSceneSetDialog(position); // 设置情景名称对话框
					break;
				default:
					break;
				}
			}
		});
		builder.create().show();
	}
    
	/**
	 * 根据deviceNo获取设备的配置信息
	 * @param devdto
	 */
	public Device findDeviceNameAndSpaceNo(Device devdto) {
		String deviceNo=devdto.getDeviceNo();
		for (int i = 0; i < userSpaceList.size(); i++) {
			UserSpaceDevice userSpace=userSpaceList.get(i);
			String mDeviceNo=userSpace.getDeviceNo();
			if (deviceNo.equals(mDeviceNo)) {
				devdto.setDeviceName(userSpace.getDeviceName());
				devdto.setSpaceNo(userSpace.getSpaceNo());
				return devdto;
			}
		}
		return devdto;
	}

	/**
	 * 设置硬件情景名称
	 * 
	 * @param position
	 */
	protected void showSceneSetDialog(final int position) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_scenename_set_dialog,
				(ViewGroup) findViewById(R.id.dialog));
		etScene1 = (EditText) layout.findViewById(R.id.et_scene1);
		etScene2 = (EditText) layout.findViewById(R.id.et_scene2);
		etScene3 = (EditText) layout.findViewById(R.id.et_scene3);
		etScene4 = (EditText) layout.findViewById(R.id.et_scene4);
		
		final String deviceno = devdtoList.get(position).getDeviceNo();//硬件情景开关的deviceNo
		
		initHardThemeNameByState(etScene1,deviceno,"10000000");
		initHardThemeNameByState(etScene2,deviceno,"01000000");
		initHardThemeNameByState(etScene3,deviceno,"00100000");
		initHardThemeNameByState(etScene4,deviceno,"00010000");

		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("情景名称设置").setView(layout);
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

				String scene1 = etScene1.getText().toString();
				String scene2 = etScene2.getText().toString();
				String scene3 = etScene3.getText().toString();
				String scene4 = etScene4.getText().toString();
				sceneNames = new String[] { deviceno, scene1, scene2,
						scene3, scene4,"" };
				
				
				if(VerifyUtils.isEmpty(scene1)){
					ToastUtils.showToast(DeviceManegeActivity.this, "请输入情景名称！",500);
				}else{
					try {// 点击确定对话框消失
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					//周期性生成硬件情景
					generateSceneTask();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {
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

	//根据设备识别码和状态初始化硬件情景名称
	private void initHardThemeNameByState(EditText etName,String deviceno, String state) {
		Theme theme=new ThemeDao(DeviceManegeActivity.this)
                 .findThemeByDeviceNoAndState(deviceno, state);
		if(theme!=null){
			etName.setText(theme.getThemeName());
		}
		
	}
	
	/**
	 * 生成硬件情景
	 */
	private void generateSceneTask (){
		if(VerifyUtils.isEmpty(sceneNames[1])){
			ToastUtils.showToast(DeviceManegeActivity.this, "请输入情景名称！",500);
		}else{
			
			ToastUtils.showToast(DeviceManegeActivity.this, "正在生成第1条情景",500);
			theme = new Theme();
			theme.setDeviceNo(sceneNames[0]);
			theme.setThemeType(1);
			theme.setGatewayNo(SystemValue.gatewayid);
			states = new String[] { "10000000", "01000000", "00100000", "00010000" };
			handler.postDelayed(runnable, 500);// 打开定时器，执行操作 
		}
	}
	
	
	//周期性生成硬件情景
	Runnable runnable = new Runnable(){ 
	@Override 
		public void run() { 
			String sceneName=sceneNames[i + 1];
			if(!VerifyUtils.isEmpty(sceneName)){ //情景名称为空，则不生成情景
				theme.setThemeName(sceneNames[i + 1]);
				theme.setThemeState(states[i]);
				generatorHardTheme(theme);
				i++;
				ToastUtils.showToast(DeviceManegeActivity.this, "正在生成第"+i+"条情景", 500);
				handler.postDelayed(this, 500);// 50是延时时长 
			}else{
				handler.removeCallbacks(runnable);// 关闭定时器处理
			}
		} 
	};

	private String[] states;
	private Theme theme;

	private UserSpaceDevice userSpace; 

	/**
	 * 生成硬件情景
	 * 
	 * @param scenename
	 */
	private void generatorHardTheme(Theme theme) {

		String strTheme = theme.getGatewayNo() + theme.getDeviceNo()
				+theme.getThemeState(); // GatewayId+deviceNo+ThemeState生成themeid

		try {
			String themeNo = MD5Security16.md5_16(strTheme);

			theme.setThemeNo(themeNo); // 生成16位硬件情景编号,ThemeNo作为硬件情景的唯一识别号
			
			new ThemeDao(DeviceManegeActivity.this).addOrUpdateTheme(theme); // 添加theme到本地数据库

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 修改产品名称和位置的对话框
	 * 
	 * @param point
	 */
	private void devUpdateDialog(final int point) {

		spacelist = new SpaceDao(DeviceManegeActivity.this)
				.getSpaceByPhonenum(SystemValue.phonenum);

		List<String> sitelist = getSpaceName(spacelist); // 房间名称列表

		devset = devdtoList.get(point);
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_devset_dialog,
				(ViewGroup) findViewById(R.id.dialog));
		etdevname = (EditText) layout.findViewById(R.id.etdevname);
		
		userSpace = new UserSpaceDevDao(DeviceManegeActivity.this)
		                   .findDeviceSpace(SystemValue.phonenum, devset.getDeviceNo());
        if (userSpace != null) {
        	etdevname.setText(userSpace.getDeviceName());
        }else{
        	etdevname.setText(devset.getDeviceName());
        }
		

		// 防区设置显示
		LinearLayout layout_alertAreaSet = (LinearLayout) layout.findViewById(R.id.ll_alertAreaSet);
		RadioGroup rg_sensorArea = (RadioGroup) layout.findViewById(R.id.rg_sensorSite);
		rg_sensorArea.setOnCheckedChangeListener(this);
		RadioButton rb_indoor = (RadioButton) layout.findViewById(R.id.rb_indoor);
		RadioButton rb_outdoor = (RadioButton) layout.findViewById(R.id.rb_outdoor);
	

		// 传感器类设备显示防区设置
		int devType=devset.getDeviceTypeId();
		int categoryId =devdtoList.get(point).getDeviceCategoryId();
		if (categoryId == SystemValue.SENSOR&&devType!=SystemValue.DEV_FANS) {
			layout_alertAreaSet.setVisibility(View.VISIBLE);
			int spaceTypeId =0;
			
			spaceTypeId=devset.getSpaceTypeId();  //从Device表中获取空间类型20161215
			
			if (spaceTypeId == 1) { // 室内
				rb_indoor.setChecked(true);
			} else if (spaceTypeId == 2) { // 室外
				rb_outdoor.setChecked(true);
			}

		}

		siteSpinner = (Spinner) layout.findViewById(R.id.Spinner_site);
		// 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
		siteAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, sitelist);

		// 第三步：为适配器设置下拉列表下拉时的菜单样式。
		siteAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 第四步：将适配器添加到下拉列表上
		siteSpinner.setAdapter(siteAdapter);
		int selectId = findPointBySpacename(devset.getSpaceNo());
		siteSpinner.setSelection(selectId);
		siteSpinner.setPrompt("选择产品安装位置：");
		siteSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(spacelist!=null){
					vSpacetypeSet = spacelist.get(position);
				}else{
					vSpacetypeSet.setSpaceNo("0");
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}

		});

		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("产品设置").setView(layout);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				deviceSaveFlag=false;  
				String deviceName=etdevname.getText().toString();
				String spaceNo=vSpacetypeSet.getSpaceNo();
				devset.setDeviceName(deviceName);
				devset.setSpaceNo(spaceNo);
				devset.setSpaceTypeId(vSpaceTypeId);  //保存房间类型到Device表中20161215 
			
				
				UserSpaceDevice userSpace=new UserSpaceDevice();
				userSpace.setPhonenum(SystemValue.phonenum);
				userSpace.setDeviceName(deviceName);
				userSpace.setDeviceNo(devset.getDeviceNo());
				userSpace.setSpaceNo(spaceNo);
//				userSpace.setSpaceType(vSpaceTypeId);  //20161215 
				
				devdtoList.get(point).setDeviceName(deviceName);
				
				deviceAdpter.notifyDataSetChanged();
		        
		        // 更新设备信息到数据库
				new DevdtoDao(DeviceManegeActivity.this)
						.updateDeviceNameAndSpaceNo(devset); // 更新本地spacetype
				
				//更新设备信息配置表
				new UserSpaceDevDao(DeviceManegeActivity.this).addorUpdate(userSpace);

				
				// 红外设备类型数据添加到红外码库中
				if (devset.getDeviceTypeId() == 105) { 
					// 添加红外遥控位置到红外数据库
					addOrUpdateGroupToInfraRed(devset);
				}
			}
		});
		builder.setNegativeButton("取消", null).show();
	}

	/**
	 * 提取出房间名称列表
	 * 
	 * @param spacelist
	 * @return
	 */
	private List<String> getSpaceName(List<Space> spacelist) {
		List<String> namelist = new ArrayList<String>();
		for (int i = 0; i < spacelist.size(); i++) {
			namelist.add(spacelist.get(i).getSpaceName());
		}
		return namelist;
	}

	// 添加红外遥控位置到红外数据库
	private void addOrUpdateGroupToInfraRed(Device devset) {
		String devid = devset.getDeviceNo();
		String gatewayid = SystemValue.gatewayid;

		ETGroup group = new ETGroup();
		String spacename = WebPacketUtil.getSpaceName(devset.getSpaceNo()); // 转换设备的位置
		group.SetName(spacename); // 红外转发器的位置
		group.SetType(16777220);
		group.SetRes(1);
		group.setmGatewayid(gatewayid);
		group.setmDevid(devid);

		ETGroup mgroup = group.findGroupByName(ETDB
				.getInstance(DeviceManegeActivity.this));
		if (mgroup == null) { // 本地数据库中查找到的mgroup
			group.Inster(ETDB.getInstance(DeviceManegeActivity.this));
			System.out.println("新添一条数据到红外码库数据库中！");
		} else {
			int mid = mgroup.GetID();
			group.SetID(mid);
			group.Update(ETDB.getInstance(DeviceManegeActivity.this));
			System.out.println("更新数据到红外码库数据库中！");
		}
	}



	/** 删除对话框 **/
	protected void showDeleteDialog(final int position) {
		final Device devdto = devdtoList.get(position);
		final int devCategoryId = devdto.getDeviceCategoryId();
		final String deviceNo = devdto.getDeviceNo();
		final String devname = devdto.getDeviceName();
		mBackDialog = BaseDialog.getDialog(DeviceManegeActivity.this, "提示",
				"确认要删除设备" + devname + "吗？", "确认",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 先发送消息后删除
						devdtoList.remove(position);
						
						deviceAdpter.notifyDataSetChanged();

						// 从数据库中删除设备
						new DevdtoDao(DeviceManegeActivity.this).deleteDeviceByDeviceno(devdto);
						
						new UserSpaceDevDao(DeviceManegeActivity.this).deleteDevSpaceByDeviceno(devdto);
						
						deleteDeviceFromServer(devdto.getDeviceNo());//20160815 从服务器删除设备
						
						//===更新version_device 时间戳===
				    	Version version=SystemValue.getVersion(SystemValue.VERSION_DEVICE);
				    	new VersionDao(null).addorUpdateVerson(version);

						mBackDialog.dismiss();
						ToastUtils.showToast(DeviceManegeActivity.this, "删除设备" + devname + "成功", 1000);

						// 删除硬件对应的情景
						if (devCategoryId == 4 || devCategoryId == 3) { // 硬件情景和安防类情景
							new ThemeDao(DeviceManegeActivity.this)
									.deleteThemeByDeviceNo(deviceNo); // 删除与硬件相关的情景
							new ThemeDeviceDao(DeviceManegeActivity.this)
									.deleteThemeDeviceByThemeDeviceNo(deviceNo); // 删除与硬件相关的情景
						}

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
	 * 根据产品位置的id在spinner中默认加载
	 * 
	 * @param spaceid
	 * @return
	 */
	private int findPointBySpacename(String spaceid) {
		int position = 0;
		for (int i = 0; i < spacelist.size(); i++) {
			String spaceId = spacelist.get(i).getSpaceNo();
			if (spaceid.equals(spaceId)) {
				position = i;
				break;
			}

		}
		return position;
	}

	

	@OnClick(R.id.tv_head_back)
	public void back() {
		if(deviceSaveFlag==true){
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			finish();
		}else{
			ToastUtils.showToast(DeviceManegeActivity.this, "请先点击完成，保存设备信息！", 2000);
		}
	}

	@Override
	protected void onDestroy() {
		unbindService(devconn);
		super.onDestroy(); // 注意先后
	}

	@Override
	protected void initViews() {
		tvtitle.setText("产品管理");
		
		tvDevFromWg = (TextView) findViewById(R.id.tv_dev_refresh);
		tvDevToServer = (TextView) findViewById(R.id.tv_devicetoserver);
		tvDevToLocal = (TextView) findViewById(R.id.tv_devicetolocal);
		
		//动态更新NEW图标
		drawable = getResources().getDrawable(R.drawable.icon_new);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		drawLeftServer = getResources().getDrawable(R.drawable.leftmenu_toserver_sel);
		drawLeftServer.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		drawLeftLocal = getResources().getDrawable(R.drawable.leftmenu_downlocal_sel);
		drawLeftLocal.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		
		tvDevToServer.setCompoundDrawables(drawLeftServer,null,null,null);
		tvDevToLocal.setCompoundDrawables(drawLeftLocal,null,null,null);
		
		tvDevFromWg.setOnClickListener(this);
		tvDevToServer.setOnClickListener(this);
		tvDevToLocal.setOnClickListener(this);
	}

	@Override
	protected void initDatas() {
		String phonenum = SystemValue.phonenum;

		// 加载设备关联的设置信息
		userSpaceList = new UserSpaceDevDao(DeviceManegeActivity.this)
				.getDeviceSpaceByPhonenum(phonenum);
		
		// 根据网关号从数据库加载相应设备
		devdtoList = new DevdtoDao(DeviceManegeActivity.this)
				.findDevListByGatewayidAndPhonenum(SystemValue.gatewayid);
		
//		devdtoList=WebPacketUtil.findSpaceDevicesFromDevicesAll(userSpaceList);  
		
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);

	}

	// 防区设置事件监听
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_indoor:
			vSpaceTypeId = 1; // 室内
			break;
		case R.id.rb_outdoor:
			vSpaceTypeId = 2; // 室外
			break;
		}

	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case id.tv_devicetoserver:
//			if(versionResult==2){
//				sysnDeviceToServer();
//			}else{
//				ToastUtils.showToast(DeviceManegeActivity.this,"服务器已经是最新数据！", 1000);
//			}
//			break;
//		case id.tv_devicetolocal:
//			if(versionResult==1){
//			    getDeviceFromServer();
//			}else{
//				ToastUtils.showToast(DeviceManegeActivity.this,"本地已经是最新版本！", 1000);
//			}
//			break;
//		default:
//			break;
//		}

	}

	/**
	 * 从服务器获取device及其配置信息
	 */
	private void getDeviceFromServer() {
		String phonenum=SystemValue.phonenum;
		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum",phonenum);
		params.addBodyParameter("gatewayNo",SystemValue.gatewayid);
		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.DEVICE_FROM_SERVER_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						initDatas();
//						ToastUtils.showToast(DeviceManegeActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
						if (message != null) {
							if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
								//从外网请求所有设备成功，先清空本地设备表
								new DevdtoDao(DeviceManegeActivity.this).deleteAllByGatewayNo(SystemValue.gatewayid);

								
								System.out.println("设备的信息"+ message.getMessageInfo());
								System.out.println("设备配置信息"+ message.getObject());
								
								String jDevice=message.getMessageInfo();
								String jUserSpace=(String) message.getObject();
								
								//更新版本的信息到本地数据库
//								String versionJson=message.getDescription();
//								Gson gVersion=new Gson();
//								Version version=gVersion.fromJson(versionJson,Version.class); 
//								new VersionDao(DeviceManegeActivity.this).addorUpdateVerson(version);
								
								if(!jDevice.equals("[]")){
									//更新数据库中的设备表
									devdtoList=WebPacketUtil.parseDevicelistFromServer(jDevice);
								}
								
								if(!jUserSpace.equals("[]")){
									//更新数据库中设备配置表
									userSpaceList=WebPacketUtil.parseUserDevicelistFromServer(jUserSpace);
								}
//								
//								//更新数据库中的设备表
//								devdtoList=WebPacketUtil.parseDevicelistFromServer(jDevice);
//								//更新数据库中设备配置表
//								userSpaceList=WebPacketUtil.parseUserDevicelistFromServer(jUserSpace);
								
								initDatas();  //远程与本地数据库中设备混合后显示
								
								// 辅助线程更新页面
								initDataFlag=true; 
								Message msg = new Message();
								msg.what = 0x129;
								handler.sendMessage(msg);
								
							} else {
								ToastUtils.showToast(DeviceManegeActivity.this,message.getMessageInfo(), 1000);
								
							}
						}
					}
				});
	}

	/**
	 * 同步设备信息到服务器
	 */
	private void sysnDeviceToServer() {
		 initDatas();   //初始化一下数据
		
		 List<Device> sDevList = new ArrayList<Device>();
		 sDevList=WebPacketUtil.parseDevListToServer(devdtoList);
		
		Gson gson = new Gson();
		String jDeviceList = gson.toJson(sDevList);
		
		System.out.println("同步到网关的设备列表是"+jDeviceList);
		
		Gson gDeviceSpace = new Gson();
		String jDeviceSpace = gDeviceSpace.toJson(userSpaceList);
		
		System.out.println("同步到网关的用户配置列表是"+jDeviceSpace);
		
		Gson gVersion = new Gson();
		String jVersion = gVersion.toJson(pVersion);

		RequestParams params = new RequestParams();
		params.addBodyParameter("deviceJson", jDeviceList);
		params.addBodyParameter("deviceSpaceJson", jDeviceSpace);
		params.addBodyParameter("versionJson", jVersion);   //同步版本信息到服务器

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.DEVICE_TO_SERVER_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						ToastUtils.showToast(DeviceManegeActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,
								ResultMessage.class);
						if (message != null) {
							if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
								//同步服务器成功，将这条记录插入到本地数据库version表中
								System.out.println("同步设备的信息"+ message.getMessageInfo());
							
							} else {
								System.out.println("同步设备的信息"+ message.getMessageInfo());
							}
						}
					}
				});
	}
	
	
	    
		/***对比版本，同步到服务器 ***/
		@OnClick(R.id.tv_head_submit)
		public void systemExit() {
			 ToastUtils.showToast(DeviceManegeActivity.this,"正在保存设备信息...", 1000);
			 deviceSaveFlag=true;
			 sysnDeviceToServer();  //同步设备配置信息到服务器
			
		}
		
		/**
		 * 从服务器删除设备
		 * @param deviceNo
		 */
		private void deleteDeviceFromServer(String deviceNo) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("deviceNo",deviceNo);
			HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
			utils.send(HttpMethod.POST, NetValue.DEVICE_DELETE_URL, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							ToastUtils.showToast(DeviceManegeActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							Gson gson = new Gson();
							ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
							Log.i("343", "删除设备返回"+message.getMessageInfo());
							if (message != null) {
								if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
									ToastUtils.showToast(DeviceManegeActivity.this,message.getMessageInfo(), 1000);
								} else {
									
								}
							}
						}
					});
		}

}
