package com.tuwa.smarthome.activity;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.sparta.xpath.PositionEqualsExpr;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.DeviceSwitchActivity.SwitchsAdapter.Holder;
import com.tuwa.smarthome.dao.DevdtoDao;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

interface AdpterOnItemClick {
	void onApterClick(int which, int postion);
}

public class DeviceSwitchActivity extends BaseActivity implements
		AdpterOnItemClick {
	// activity绑定service1
	private SocketService devService;
	

	@Bind(R.id.tv_head_submit)
	TextView tvExit;
	@Bind(R.id.tv_head_back)
	TextView tvBack;
	@Bind(R.id.tv_head_title)
	TextView tvTitle;
	@Bind(R.id.gv_switch_list)
	GridView gvDevices;
	@Bind(R.id.tv_bottom_network)
	TextView tvbttomNetwork;

	private List<Device> devlist = new ArrayList<Device>();
	private List<Device> gvlistAll = new ArrayList<Device>(); // 在列表中显示的list
	private SwitchsAdapter deviceAdpter = new SwitchsAdapter();

	// 滑动缓存加载Listview
	private final int LOAD_STATE_IDLE = 0; // 没有在加载，并且list中还有数据没加载
	private final int LOAD_STATE_LOADING = 1; // 正在加载
	private final int LOAD_STATE_FINISH = 2; // list中数据全部加载完毕
	private int loadState = LOAD_STATE_IDLE; // 记录加载状态
	private int LIST_COUNT; // list中的总数目
	private static int EACH_COUNT = 10; // 每页加载的数目

	final char ON = 1 + '0'; // 字符开
	final char OFF = 0 + '0'; // 字符关
	public String sLightVal; // 可调关的亮度
	private static char[] strStaArr = new char[4]; // 字符数组代表多路开关状态

	/* 辅线程动态刷新页面 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x129:
				if(devlist!=null){   //list不为空，填充适配器
					gvDevices.setAdapter(deviceAdpter);
				}
				if(gvlistAll!=null){
					LIST_COUNT = gvlistAll.size(); // 实例化所有设备的总数
				}
			
				loadState = LOAD_STATE_IDLE; // 记录加载状态
				deviceAdpter.onListener(DeviceSwitchActivity.this);
				deviceAdpter.notifyDataSetChanged();
				break;
			case 0x008:
				@SuppressWarnings("unchecked")
				ArrayList<Device> result = ((ArrayList<Device>) msg.obj);

				devlist.addAll(result);
				deviceAdpter.notifyDataSetChanged();

				break;
			case 0x001: // 内网局部刷新
				int index = msg.arg1;
				Device devdto = (Device) msg.obj;
				int firstVisible = gvDevices.getFirstVisiblePosition();
				int lastVisible = gvDevices.getLastVisiblePosition();
				if (index >= firstVisible && index <= lastVisible) {
					// 获取到index对应的holder
					Holder holder = (Holder) (gvDevices.getChildAt(index
							- firstVisible).getTag());
					showViewByDevtype(holder, devdto,1); // 根据设备类型显示状态
				}
				break;
			case 0x002: // 外网局部刷新
				// int index = msg.arg1;
				// DevDTO devdto = (DevDTO) msg.obj;
				// int outfirstVisible = gvDevices.getFirstVisiblePosition();
				// int outlastVisible = gvDevices.getLastVisiblePosition();
				// System.out.println("========listview局部刷新====="+index);
				// if (index >= firstVisible && index <= lastVisible) {
				// //获取到index对应的holder
				// Holder holder = (Holder) (gvDevices
				// .getChildAt(index - firstVisible).getTag());
				// showViewByDevtype(holder,devdto); //根据设备类型显示状态
				// }
				break;
			}
		}
	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_switch);
		ButterKnife.bind(DeviceSwitchActivity.this);// 注解工具声明

		// Activity和service绑定2
		Intent service = new Intent(DeviceSwitchActivity.this,SocketService.class);
		bindService(service, devconn, Context.BIND_AUTO_CREATE);

		tvExit.setText("退出");
		tvTitle.setText("照明");
		
		initViews();
		initDatas();
		
		// Gridview滑动分页加载列表
		gvDevices.setOnScrollListener(new MyOnScrollListener());

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
						if (NetValue.NONET == netstatue) { // 本地连接失败
							if(!NetValue.autoFlag){
								ToastUtils.showToast(DeviceSwitchActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
							}
							
							NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
							tvbttomNetwork.setText("远程");
						}else if(NetValue.INTRANET == netstatue){
							tvbttomNetwork.setText("本地");
						}
						break;
					case DEVMSG:
						SocketPacket socketPacket = (SocketPacket) tranObject.getObject();
						String devid = socketPacket.getDevId();
						String devstate =(String)socketPacket.getData();

						Device devDTO = null;
						int vposition = -1;
						// 进行数据对比获取对应数据在list中的位置
						for (int j = 0; j < devlist.size(); j++) {
							String strdevid = devlist.get(j).getDeviceNo();
							if (devid.equalsIgnoreCase(strdevid)) {
								devDTO = devlist.get(j);
								devDTO.setDeviceStateCmd(devstate);
								vposition = j;
								// 异步进程更新界面
								Message msg = new Message();
								msg.what = 0x001;
								msg.arg1 = vposition;
								msg.obj = devDTO;
								handler.sendMessage(msg);
								break;
							}
						}
						break;
					default:
						break;
					}
				}
			});
		}
	};

	// list数据适配器
	public class SwitchsAdapter extends BaseAdapter {
		private AdpterOnItemClick myAdpterOnclick;

		public void onListener(AdpterOnItemClick listener) {
			this.myAdpterOnclick = listener;
		}

		@Override
		public int getCount() {
			return devlist.size();
		}

		@Override
		public Object getItem(int position) {
			return devlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return -1;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			View view = converView;
			Holder holder = null;
			if (view != null) {
				holder = (Holder) view.getTag();
			} else {
				view = View.inflate(DeviceSwitchActivity.this,
						R.layout.item_dev_switch_click, null);
				holder = new Holder(view);
				view.setTag(holder);
			}

			Device devdto = devlist.get(position);
			initDeviceNameAndSite(holder.tvDevSite,holder.tvDevName,devdto); //初始化房间名称和位置
			
			// 点击事件的按钮做标记
			holder.tgBtn1.setTag(position);
			holder.tgBtn2.setTag(position);
			holder.tgBtn3.setTag(position);
			holder.tgBtn4.setTag(position);
			holder.sbLight.setTag(position);
			
			final int fpostion = position;
			final Holder fHolder = holder;

			showViewByDevtype(holder, devdto,position); // 根据设备类型显示状态

			fHolder.tgBtn1.setOnClickListener(new DoubleClickEvent() {
				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						if (!fHolder.tgBtn1.isChecked()) { // 当前状态为开，点击的时候发送off
							cmdControl(fpostion, 1, OFF);
							fHolder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_off);
						} else {
							cmdControl(fpostion, 1, ON);
							fHolder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_on);
						}
					} else {
						System.out.println("===myAdpterOnclick为空====");
					}

				}
			});

			holder.tgBtn2.setOnClickListener(new DoubleClickEvent() {

				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						if (!fHolder.tgBtn2.isChecked()) {
							fHolder.tgBtn2.setBackgroundResource(R.drawable.icon_switch_off);
							cmdControl(fpostion, 2, OFF);
						} else {
							fHolder.tgBtn2.setBackgroundResource(R.drawable.icon_switch_on);
							cmdControl(fpostion, 2, ON);
						}
					}
				}
			});

			holder.tgBtn3.setOnClickListener(new DoubleClickEvent() {

				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						if (!fHolder.tgBtn3.isChecked()) {
							fHolder.tgBtn3.setBackgroundResource(R.drawable.icon_switch_off);
							cmdControl(fpostion, 3, OFF);
						} else {
							fHolder.tgBtn3.setBackgroundResource(R.drawable.icon_switch_on);
							cmdControl(fpostion, 3, ON);
						}
					}
				}
			});

			holder.tgBtn4.setOnClickListener(new DoubleClickEvent() {

				@Override
				public void singleClick(View v) {
					if (myAdpterOnclick != null) {
						int which = v.getId();
						myAdpterOnclick.onApterClick(which, fpostion);
						if (!fHolder.tgBtn4.isChecked()) {
							fHolder.tgBtn4.setBackgroundResource(R.drawable.icon_switch_off);
							cmdControl(fpostion, 4, OFF);
						} else {
							fHolder.tgBtn4.setBackgroundResource(R.drawable.icon_switch_on);
							cmdControl(fpostion, 4, ON);
						}
					}
				}
			});

			holder.sbLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() { // 调光灯
						int mprogress = 0;

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							    mprogress = progress;
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) { // 滑动条停止时触发事件
							sLightVal = String.valueOf(mprogress);
							devlist.get(fpostion).setDeviceStateCmd(sLightVal);
							cmdControl(fpostion, 5, ON);
						}
					});

			return view;
		}
   
		class Holder {  
			@Bind(R.id.tv_switch_devSite)
			TextView tvDevSite;
			@Bind(R.id.tv_switch_devtypeName)
			TextView tvDevName;
			@Bind(R.id.tg_btn1)
			ToggleButton tgBtn1;
			@Bind(R.id.tg_btn2)
			ToggleButton tgBtn2;
			@Bind(R.id.tg_btn3)
			ToggleButton tgBtn3;
			@Bind(R.id.tg_btn4)
			ToggleButton tgBtn4;
			@Bind(R.id.sb_switch_light)
			SeekBar sbLight; // 调光灯

			public Holder(View view) {
				ButterKnife.bind(this, view);
			}
		}
	}

	/**
	 * 根据设备类型显示按钮
	 * 
	 * @param holder
	 * @param devtype
	 */
	private void showViewByDevtype(Holder holder, Device devdto,int position) {
		int devtype = devdto.getDeviceTypeId();
		// 设备状态串转换为状态字符数组
		String mdevState = devdto.getDeviceStateCmd();
		if(mdevState!=""){
			strStaArr = mdevState.toCharArray();
		}

		switch (devtype) {
		case 1:
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.tgBtn2.setVisibility(View.GONE);
				holder.tgBtn3.setVisibility(View.GONE);
				holder.tgBtn4.setVisibility(View.GONE);
				holder.sbLight.setVisibility(View.GONE);
				if (strStaArr[0] == ON) {
					holder.tgBtn1.setChecked(true);
					holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_on);
				}else {
					holder.tgBtn1.setChecked(false);
					holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_off);
				} 
		
			break;
		case 51: // 风扇
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setVisibility(View.GONE);
			if (strStaArr[0] == ON) {
				holder.tgBtn1.setChecked(true);
				holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_on);
			}else {
				holder.tgBtn1.setChecked(false);
				holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_off);
			} 
			break;
		case 2:
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.tgBtn2.setVisibility(View.VISIBLE);
				holder.tgBtn3.setVisibility(View.GONE);
				holder.tgBtn4.setVisibility(View.GONE);
				holder.sbLight.setVisibility(View.GONE);
				if (strStaArr[0] == ON) {
					holder.tgBtn1.setChecked(true);
					holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_on);
				}else {
					holder.tgBtn1.setChecked(false);
					holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_off);
				}
				if (strStaArr[1] == ON) {
					holder.tgBtn2.setChecked(true);
					holder.tgBtn2.setBackgroundResource(R.drawable.icon_switch_on);
				}else {
					holder.tgBtn2.setChecked(false);
					holder.tgBtn2.setBackgroundResource(R.drawable.icon_switch_off);
				}
			
			break;
		case 3:
				holder.tgBtn1.setVisibility(View.VISIBLE);
				holder.tgBtn2.setVisibility(View.VISIBLE);
				holder.tgBtn3.setVisibility(View.VISIBLE);
				holder.tgBtn4.setVisibility(View.GONE);
				holder.sbLight.setVisibility(View.GONE);
				if (strStaArr[0] == ON) {
					holder.tgBtn1.setChecked(true);
					holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_on);
				}else {
					holder.tgBtn1.setChecked(false);
					holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_off);
				} 
				if (strStaArr[1] == ON) {
					holder.tgBtn2.setChecked(true);
					holder.tgBtn2.setBackgroundResource(R.drawable.icon_switch_on);
				}else {
					holder.tgBtn2.setChecked(false);
					holder.tgBtn2.setBackgroundResource(R.drawable.icon_switch_off);
				} 
				if (strStaArr[2] == ON) {
					holder.tgBtn3.setChecked(true);
					holder.tgBtn3.setBackgroundResource(R.drawable.icon_switch_on);
				}else {
					holder.tgBtn3.setChecked(false);
					holder.tgBtn3.setBackgroundResource(R.drawable.icon_switch_off);
				}
			
			break;
		case 4:
			holder.tgBtn1.setVisibility(View.VISIBLE);
			holder.tgBtn2.setVisibility(View.VISIBLE);
			holder.tgBtn3.setVisibility(View.VISIBLE);
			holder.tgBtn4.setVisibility(View.VISIBLE);
			holder.sbLight.setVisibility(View.GONE);
			
			if (strStaArr[0] == ON) {
				holder.tgBtn1.setChecked(true);
				holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_on);
			}else {
				holder.tgBtn1.setChecked(false);
				holder.tgBtn1.setBackgroundResource(R.drawable.icon_switch_off);
			}
			if (strStaArr[1] == ON) {   
				holder.tgBtn2.setChecked(true);
				holder.tgBtn2.setBackgroundResource(R.drawable.icon_switch_on);
			}else {
				holder.tgBtn2.setChecked(false);
				holder.tgBtn2.setBackgroundResource(R.drawable.icon_switch_off);
			}
			if (strStaArr[2] == ON) {
				holder.tgBtn3.setChecked(true);
				holder.tgBtn3.setBackgroundResource(R.drawable.icon_switch_on);
			}else {
				holder.tgBtn3.setChecked(false);
				holder.tgBtn3.setBackgroundResource(R.drawable.icon_switch_off);
			}
			if (strStaArr[3] == ON) {
				holder.tgBtn4.setChecked(true);
				holder.tgBtn4.setBackgroundResource(R.drawable.icon_switch_on);
			}else {
				holder.tgBtn4.setChecked(false);
				holder.tgBtn4.setBackgroundResource(R.drawable.icon_switch_off);
			}
			break;
		case 5:
			holder.sbLight.setVisibility(View.VISIBLE);
			holder.tgBtn1.setVisibility(View.GONE);
			holder.tgBtn2.setVisibility(View.GONE);
			holder.tgBtn3.setVisibility(View.GONE);
			holder.tgBtn4.setVisibility(View.GONE);
			holder.sbLight.setMax(9);
			int dLight = 0;
			try {
				if (mdevState.endsWith("a")) {
					dLight = 9;
				} else {
					dLight = Integer.valueOf(mdevState);
				}
				holder.sbLight.setProgress(dLight);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * 发送命令函数
	 * 
	 * @param position
	 *            当前点击的开关在devlist中位置
	 * @param switchid
	 *            多路开关中当前点击的位置
	 * @param ch
	 *            相应的开关命令
	 */
	public void cmdControl(int position, int switchid, char ch) {
		Device device = devlist.get(position);
		String sqlCmd = WebPacketUtil.convertCmdToSql(device, switchid, ch); // 控制的devstate转码到本地

		device.setDeviceStateCmd(sqlCmd); // 注意更改后的设备状态先加载到本地
		// //更新device最新状态到本地设备数据库（3）
		new DevdtoDao(DeviceSwitchActivity.this).updateDevStateByDeviceNo(device);
		//将命令转换为报文
		SocketPacket devPacket = WebPacketUtil.devConvertToPacket(device);
		
		switch (NetValue.netFlag) {
		case NetValue.OUTERNET: // 外网
			//将命令封装为字符串发送到服务器
			byte[] sentBytes=WebPacketUtil.packetToByteStream(devPacket);
			sendCmdToServer(sentBytes,0); // 发送到服务器的命令串
			break;
		case NetValue.INTRANET: // 内网
			devService.sentPacket(devPacket); // 发送请求认证报文到网关
			break;
		}
	}

	

	/*** 退出系统 ***/
	@OnClick(R.id.tv_head_submit)
	public void systemExit() {
		initExitDialog();
	}

	/*** 返回 ***/
	@OnClick(R.id.tv_head_back)
	public void back() {
		Intent intent = new Intent(DeviceSwitchActivity.this,HomeActivity.class);
		startActivity(intent);
		finish();
	}

	 /***空间***/
	 @OnCheckedChanged(R.id.rb_navi_space)
	 public void spaceDeviceShow(){
		 Intent intent=new Intent(DeviceSwitchActivity.this,SpaceDevicesActivity.class);
		 startActivity(intent);
		 finish();
	 }
	 /***情景模式***/
	 @OnCheckedChanged(R.id.rb_navi_scene)
	 public void sceneMode(){
		 Intent sceneIntent=new  Intent(DeviceSwitchActivity.this,SceneModelActivity.class);
		 startActivity(sceneIntent);
		 finish();
	 }
	
	 /***网络切换***/
	 @OnClick(R.id.tv_bottom_network)
	 public void networkSwitchClick(){
	     netWorkSwitch(devService,tvbttomNetwork);
	 }
	
	 /***防区管理***/
	 @OnCheckedChanged(R.id.rb_navi_alert)
	 public void DefenceAreaClick(){
		 Intent sceneIntent=new Intent(DeviceSwitchActivity.this,DefenceAreaActivity.class);
		 startActivity(sceneIntent);
		 finish();
	 }
	 /***系统设置***/
	 @OnCheckedChanged(R.id.rb_navi_set)
	 public void systemSet(){
		 Intent intent=new Intent(DeviceSwitchActivity.this,SetActivity.class);
		 startActivity(intent);
		 finish();
	 }
	

	@Override
	protected void initViews() {
		if (NetValue.netFlag == NetValue.INTRANET) {
			tvbttomNetwork.setText("本地"); // 任务栏显示网络状态
			
		} else if (NetValue.netFlag == NetValue.OUTERNET) {
			tvbttomNetwork.setText("远程"); // 任务栏显示网络状态
		}
	}

	@Override
	protected void initDatas() {
		// 根据网关号从数据库加载相应设备
		gvlistAll = new DevdtoDao(DeviceSwitchActivity.this).switchListBygwId(SystemValue.gatewayid,SystemValue.SWITCH);
	
		// 异步进程更新界面
		Message msg = new Message();
		msg.what = 0x129;
		handler.sendMessage(msg);
	}

	@Override
	protected void onDestroy() {
		unbindService(devconn);
		super.onDestroy(); // 注意先后
	}

	@Override
	public void onApterClick(int which, int postion) {
		// TODO Auto-generated method stub
	}
	
	private final class MyOnScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			final int totalCount = firstVisibleItem + visibleItemCount; // firstVisibleItem当前页的第一项位置
																		// totalCount
																		// 当前页的最后一项位置
			if (totalCount == totalItemCount) { // 当前这一页加载完成，等待加载下一页
				if (loadState == LOAD_STATE_IDLE) {
					loadState = LOAD_STATE_LOADING;

					new Thread() {
						public void run() {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							int count = deviceAdpter.getCount(); // 获取deviceAdpter中的数据总数

							int dataIndex = 0; // 要加载的数据的index
							List<Device> result = new ArrayList<Device>();
							for (dataIndex = count; dataIndex < Math.min(count
									+ EACH_COUNT, LIST_COUNT); dataIndex++) {
								Device devdto = gvlistAll.get(dataIndex);
								result.add(devdto);
							}

							if (dataIndex <= LIST_COUNT) {
								Message msg = new Message();
								msg.what = 0x008;
								msg.obj = result;
								handler.sendMessage(msg);
							}
							if (dataIndex == LIST_COUNT) {
								loadState = LOAD_STATE_FINISH;
							} else {
								loadState = LOAD_STATE_IDLE; // list未加载完，待续
							}
						};
					}.start();
				}
			}
		}
	}

}
