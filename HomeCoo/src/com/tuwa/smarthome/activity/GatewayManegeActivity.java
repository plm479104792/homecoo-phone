package com.tuwa.smarthome.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.BaseDialog;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.GateWayDao;
import com.tuwa.smarthome.dao.UserDao;
import com.tuwa.smarthome.dao.VersionDao;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.NetTool;
import com.tuwa.smarthome.network.NetTool.IPCallBack;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.network.SocketService.SocketCallBack;
import com.tuwa.smarthome.util.DataConvertUtil;
import com.tuwa.smarthome.util.PreferencesUtils;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.VerifyUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GatewayManegeActivity extends BaseActivity {
	// activity绑定service
	private SocketService socketService;

	@Bind(R.id.tv_head_submit)
	TextView tvSubmit;
	@Bind(R.id.tv_head_back)
	TextView tvBack;
	@Bind(R.id.tv_head_title)
	TextView tvtitle;
	@Bind(R.id.gv_gateway_list)
	GridView gvGateway;
	@Bind(R.id.tv_bottom_network)
	TextView tvbttomNetwork;
	private EditText etgatewayIP;   //对话框中的IP地址

	private BaseDialog mBackDialog;
	private List<Gateway> gatewayList = new ArrayList<Gateway>();
	private GateWayAdapter gatewayAdpter = null;
	private TextView tvAddGateway;
	private Gateway pGateWay, nPwdGateway; // nPwdGateway修改密码后的主机
	private boolean addgwflag = false; // 是否添加主机到服务器标志
	private Version pVersion;   //该用户对应的设备版本
	private int versionResult=0;   //版本标识
	
	private String strgatewayNo="";  //用户输入的主机id
	private boolean gatewaySaveFlag=true;

	/* 辅线程动态刷新页面 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x129:
				gatewayAdpter = new GateWayAdapter();
				gvGateway.setAdapter(gatewayAdpter);
				break;
			case 0x130:
				if (addgwflag) { // 在addgatewayDailog中置标志为true
					gatewayList.add(pGateWay);
					gatewayAdpter = new GateWayAdapter();
					gvGateway.setAdapter(gatewayAdpter);
					
					addgwflag=false;  //添加网关成功后，不允许再次添加网关
					
					//将GatewayNo绑定到对应的user
					String phonenum=SystemValue.phonenum;
					String gatewayno=SystemValue.gatewayid;
					setAliasAndTags();  //添加主机成功后，立即设置别名
					
					Gateway mGateway = new GateWayDao(GatewayManegeActivity.this).getGatewayByGatewayNo(gatewayno);
                    if(mGateway==null){   //如果数据库中已有主机信息则不添加
                    	new GateWayDao(GatewayManegeActivity.this).add(pGateWay); // 添加到本地数据库
                    }
					
					if(!VerifyUtils.isEmpty(gatewayno)){
						new UserDao(GatewayManegeActivity.this).updateGatewayNoByPhonenum(phonenum,gatewayno);
						
						//===更新version_device 时间戳===
				    	Version version=SystemValue.getVersion(SystemValue.VERSION_GATEWAY);
				    	new VersionDao(null).addorUpdateVerson(version);
					}
				}
				break;
			case 0x008:
				dismissLoadingDialog();
				if(NetValue.IP_CONNECT_FLAG){
					etgatewayIP.setText( NetValue.LOCAL_IP);
				}else{
					ToastUtils.showToast(GatewayManegeActivity.this, "未搜索到主机IP,请重新搜索!", 1000);
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway_manege);

		ButterKnife.bind(GatewayManegeActivity.this);
		// Activity和service绑定
		Intent service = new Intent(GatewayManegeActivity.this,SocketService.class);
		bindService(service, conn, Context.BIND_AUTO_CREATE);

		tvtitle.setText("主机配置");

		initViews();
		initDatas();
		
//		checkVersionFromServer();  //从服务器对比版本信息
		getGatewayFromServer();
		
	}

	// 获取SocketService实例对象
	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 返回一个SocketService对象
			socketService = ((SocketService.SocketBinder) service).getService();

			socketService.callSocket(new SocketCallBack() {
				@Override
				public void callBack(TranObject tranObject) {

					switch (tranObject.getTranType()) {
					case NETMSG:
						int netstatue = (Integer) tranObject.getObject();
						if (NetValue.NONET == netstatue) { // 本地连接失败
							if(!NetValue.autoFlag){
								ToastUtils.showToast(GatewayManegeActivity.this,"本地连接失败,请检查网关是否连接本地网络！", 1000);
							}
							
							NetValue.netFlag = NetValue.OUTERNET; // 【调试】内网失败，自动切换为外网
							tvbttomNetwork.setText("远程");
						}else if(NetValue.INTRANET == netstatue){
							tvbttomNetwork.setText("本地");
						}
						break;	    
					case DEVMSG:
						SocketPacket socketPacket = (SocketPacket) tranObject
								.getObject();
						short datatype = socketPacket.getDataType();
						if (datatype == 104) { // 修改密码成功返回
							gatewaySaveFlag=false;  //主机密码修改
							NetValue.authdata = nPwdGateway.getGatewayPwd();
							 ToastUtils.showToast(GatewayManegeActivity.this,"修改密码成功！", 1000);
							// 同步新密码到本地数据库
							new GateWayDao(GatewayManegeActivity.this)
									.updateGateWayByGatewayNo(nPwdGateway);
						} else if (datatype == 67) { // 认证返回
							ToastUtils.showToast(GatewayManegeActivity.this,"成功匹配的主机ip为" + NetValue.LOCAL_IP, 1000);	
							
							if (socketPacket.getData().equals("0")) { // 验证成功返回0
								// 异步进程更新界面
								if (addgwflag){
									gatewaySaveFlag=false;  //主机密码修改
									Message msg = new Message();
									msg.what = 0x130; // 通过外网验证，添加主机信息到服务器
									handler.sendMessage(msg);
									
								}else{
									Message msg = new Message();
									msg.what = 0x129; // 通过外网验证，添加主机信息到服务器
									handler.sendMessage(msg);
								}
							
								NetValue.netFlag = NetValue.INTRANET; // 【调试】内网失败，自动切换为外网
								tvbttomNetwork.setText("本地");
								
								PreferencesUtils.putString(GatewayManegeActivity.this, "wgid",SystemValue.gatewayid);  //保存网关号为共享变量
								
							} else {
								ToastUtils.showToast(GatewayManegeActivity.this, "本地验证失败，请检查输入的主机信息！", 2000);
							}
						}
					default:
						break;
					}
				}
			});
		}
	};


	// 空间适配器
	public class GateWayAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return gatewayList.size();
		}

		@Override
		public Object getItem(int position) {
			return gatewayList.get(position);
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
				view = View.inflate(GatewayManegeActivity.this,
						R.layout.item_gateway, null);
				holder = new ViewHolder(view);
				view.setTag(holder);
			}

			showViewByDevtype(holder, position); // 根据设备类型显示状态

			switchViewOnClick(holder, position); // 列表中开关按键点击事件监听

			return view;
		}

		class ViewHolder {           
			@Bind(R.id.tv_list_gatewayNO)
			TextView tvGatewayNO;
			@Bind(R.id.tv_list_gatewayIP)
			TextView tvGatewayIP;
			@Bind(R.id.im_setting)
			ImageView imSetting;
			@Bind(R.id.tg_gateway_switch)
			ToggleButton tgBtnSwitch;

			public ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
		}

		private void showViewByDevtype(ViewHolder holder, int position) {
			Gateway gateway = gatewayList.get(position);
			String gatewayNoHex=gateway.getGatewayNo();
			byte[] gwIdBytes = DataConvertUtil.toByteArray(gatewayNoHex);
			String gatewayNo=new String(gwIdBytes);
			
			holder.tvGatewayNO.setText("主机ID:" + gatewayNo);
			holder.tvGatewayIP.setText("主机IP:" + gateway.getGatewayIp());
			if (gateway.getGatewayNo().equals(SystemValue.gatewayid)) {
				holder.tgBtnSwitch.setChecked(true);
			}

		}

		private void switchViewOnClick(ViewHolder holder, final int position) {
			holder.tgBtnSwitch
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Gateway mgateway = gatewayList.get(position);

							// 启用主机id和IP
							SystemValue.gatewayid = mgateway.getGatewayNo();
							NetValue.LOCAL_IP = mgateway.getGatewayIp();

						}
					});
			holder.imSetting.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					gatewaySetDialog(position); // 设置功能对话框
				}
			});
		}
	}

	/**
	 * 主机设置对话框
	 */
	private void gatewaySetDialog(final int position) {

		final String items[] = { "更改主机IP", "更改主机密码", "删除" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		builder.setTitle("主机设置"); // 设置标题
		// builder.setIcon(R.drawable.set_tool);//设置图标，图片id即可
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0:
					// 更改主机IP
					updateGatewayIPDialog(position);
					break;
				case 1:
					// 密码重置
					if (NetValue.socketauthen) {
						resetPasswordToServerDialog(position);
					} else {
						ToastUtils.showToast(GatewayManegeActivity.this,"请将网络切换为本地后，修改主机密码！", 1000);
					}
					break;
				case 2:
					// 删除主机
					showDeleteDialog(position);
					break;

				default:
					break;
				}

			}
		});
		builder.create().show();
	}

	// 重置密码对话框
	public void resetPasswordToServerDialog(int position) {
		final Gateway mgateway = gatewayList.get(position);
		final String gatewayid = mgateway.getGatewayNo();
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_dialog_resetpasswd,
				(ViewGroup) findViewById(R.id.dialog));
		final TextView tvWgID = (TextView) layout
				.findViewById(R.id.tv_logincode);
		final TextView tvOldpwd = (TextView) layout
				.findViewById(R.id.tv_passwd);
		final TextView tvNewpwd = (TextView) layout
				.findViewById(R.id.tv_repasswd);
		final EditText etLoginCode = (EditText) layout
				.findViewById(R.id.et_logincode);
		final EditText etPassword = (EditText) layout
				.findViewById(R.id.et_passwd);
		final EditText etRePassword = (EditText) layout
				.findViewById(R.id.et_repasswd);
		tvWgID.setText("主机ID");
		tvOldpwd.setText("旧密码");
		tvNewpwd.setText("新密码");
		etLoginCode.setText(mgateway.getGatewayNo());
		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("密码重置").setView(layout);
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

				String oldpwd = etPassword.getText().toString().trim();
				String newpwd = etRePassword.getText().toString().trim();

				if (oldpwd.length() != 8) {
					ToastUtils.showToast(GatewayManegeActivity.this,"原密码错误", 1000);
					etLoginCode.requestFocus();
				} else if (newpwd.length() != 8) {
					ToastUtils.showToast(GatewayManegeActivity.this,"密码格式错误", 1000);
					etPassword.requestFocus();
				} else {
					// 修改密码到主机
					String data = oldpwd + newpwd;
					mgateway.setGatewayPwd(newpwd);
					nPwdGateway = mgateway; // 修改密码后的主机

					if (NetValue.socketauthen) { // 认证通过，直接发送修改密码报文
						SocketPacket resetPwdPacket = WebPacketUtil
								.updatePasswordPacket(gatewayid, data);
						socketService.sentPacket(resetPwdPacket); // 发送修改密码报文到主机
					}
				}

				try {// 点击确定对话框消失
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
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

	/************ 添加主机 ***************/
	private void addGatewayDialog() {
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_gwset_dialog,
				(ViewGroup) findViewById(R.id.dialog));
		final EditText etgatewayNO = (EditText) layout
				.findViewById(R.id.tv_gwno);
		etgatewayIP = (EditText) layout
				.findViewById(R.id.et_gwip);
		final EditText etgatewayPWD = (EditText) layout
				.findViewById(R.id.et_wg_passwd);
		
		if(VerifyUtils.isEmpty(strgatewayNo)&&(!VerifyUtils.isEmpty(SystemValue.gatewayid))){
			byte[] gwIdBytes = DataConvertUtil.toByteArray(SystemValue.gatewayid);
			strgatewayNo=new String(gwIdBytes);
		}
		etgatewayNO.setText(strgatewayNo);
		etgatewayIP.setText(NetValue.LOCAL_IP);

		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("添加主机")
		.setView(layout);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			

			public void onClick(final DialogInterface dialog, int whichButton) {
				// 点击确定对话框不消失
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				strgatewayNo = etgatewayNO.getText().toString().trim();
				String gatewayIp = etgatewayIP.getText().toString().trim();
				String gatePWD = etgatewayPWD.getText().toString().trim();
				
				if (VerifyUtils.isEmpty(strgatewayNo)) {
					ToastUtils.showToast(GatewayManegeActivity.this,"请填写主机号", 1000);
				} else if (strgatewayNo.length() !=8) {
					ToastUtils.showToast(GatewayManegeActivity.this,"主机号格式错误", 1000);
				} else if (VerifyUtils.isEmpty(gatewayIp)) {
					ToastUtils.showToast(GatewayManegeActivity.this,"请填写主机IP地址", 1000);
				} else if (!VerifyUtils.matchIpAddress(gatewayIp)) {
					ToastUtils.showToast(GatewayManegeActivity.this,"主机IP地址格式错误", 1000);
				} else if ((VerifyUtils.isEmpty(gatePWD)) || (gatePWD.length() != 8)) {
					ToastUtils.showToast(GatewayManegeActivity.this,"主机密码格式错误", 1000);
				} else {
					//AADDAADD转换为字符串303041413030
					byte[] bGateway=strgatewayNo.getBytes();
					String gatewayNo=DataConvertUtil.toHexUpString(bGateway);
					
						Gateway gateway = new Gateway();
						gateway.setGatewayNo(gatewayNo);
						gateway.setGatewayIp(gatewayIp);
						gateway.setGatewayPwd(gatePWD);
						//
						pGateWay = gateway;
						SystemValue.gatewayid = gatewayNo;
						NetValue.LOCAL_IP = gatewayIp;
						NetValue.authdata = gatePWD;

						addgwflag = true; // 在0x30中使用

						if (NetValue.socketauthen) { // socket已建立连接通过认证，重新认证主机合法性
							SocketPacket authPacket = WebPacketUtil
									.getAuthenticPacket();
							socketService.sentPacket(authPacket); // 通过内网验证主机的合法性
						} else {
							SystemValue.gatewayid = gatewayNo;
							NetValue.LOCAL_IP = gatewayIp;
							NetValue.authdata = gatePWD;

							socketService.socketConnect(NetValue.LOCAL_IP); // 重新建立连接，认证
						}

						try {// 点击确定对话框消失
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

	

	/************ 更新主机IP到本地数据库 ***************/
	private void updateGatewayIPDialog(int point) {
		initDatas();

		final Gateway gateway = gatewayList.get(point);
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.include_gwset_dialog,
				(ViewGroup) findViewById(R.id.dialog));
		EditText tvgatewayNO = (EditText) layout.findViewById(R.id.tv_gwno);
		tvgatewayNO.setFocusable(false);
		
		if(!VerifyUtils.isEmpty(gateway.getGatewayNo())){
			byte[] gwIdBytes = DataConvertUtil.toByteArray(gateway.getGatewayNo());
			strgatewayNo=new String(gwIdBytes);
		}
		tvgatewayNO.setText(strgatewayNo);
		
		final EditText etgatewayIP = (EditText) layout
				.findViewById(R.id.et_gwip);
		etgatewayIP.setText(gateway.getGatewayIp());
		TableRow tr_wgpwd = (TableRow) layout.findViewById(R.id.tr_wgpwd);
		tr_wgpwd.setVisibility(View.GONE);

		// 对话框的外部结构
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("更新主机IP").setView(layout);
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

				String gatewayIP = etgatewayIP.getText().toString();
				if (!VerifyUtils.matchIpAddress(gatewayIP)) {
					ToastUtils.showToast(GatewayManegeActivity.this,"主机IP地址格式错误", 1000);
					etgatewayIP.requestFocus();
				} else {

					gateway.setGatewayIp(gatewayIP);
					NetValue.LOCAL_IP = gatewayIP; // 更新IP时立即启用
					// 异步进程更新界面
					Message msg = new Message();
					msg.what = 0x129;
					handler.sendMessage(msg);
					// 更新设备信息到数据库
					new GateWayDao(GatewayManegeActivity.this).updateGateWayByGatewayNo(gateway);
					ToastUtils.showToast(GatewayManegeActivity.this,"主机IP地址更新成功！", 1000);
					//===更新version_device 时间戳===
			    	Version version=SystemValue.getVersion(SystemValue.VERSION_GATEWAY);
			    	new VersionDao(null).addorUpdateVerson(version);

					try {// 对话框消失
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}

					/********* 下一步位置修改同步更新到服务器 *************/
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

	/** 删除对话框 **/
	protected void showDeleteDialog(final int position) {
		final Gateway gateway = gatewayList.get(position);
		final String gatewayId = gateway.getGatewayNo();
		mBackDialog = BaseDialog.getDialog(GatewayManegeActivity.this, "提示",
				"确认要删除主机" + gatewayId + "吗？", "确认",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 先发送消息后删除
						gatewayList.remove(position);
						gatewayAdpter.notifyDataSetChanged();
						
						//===更新version_device 时间戳===
				    	Version version=SystemValue.getVersion(SystemValue.VERSION_GATEWAY);
				    	new VersionDao(null).addorUpdateVerson(version);
				    	
				    	unbindGatewayAndUser(version);  //解除手机号和主机号绑定

						// 从本地数据库中删除设备
//						new GateWayDao(GatewayManegeActivity.this)
//								.deleteGatewayByGatewayNo(gateway);
						
						//将用户绑定的GatewayNo置空
						new UserDao(GatewayManegeActivity.this).updateGatewayNoByPhonenum(SystemValue.phonenum, "");
                        SystemValue.gatewayid="";
						
						mBackDialog.dismiss();
						ToastUtils.showToast(GatewayManegeActivity.this,"删除主机" + gateway.getGatewayNo() + "成功", 1000);
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

	


	// 刷新主机的ip地址
	@OnClick(R.id.tv_refresh_gateway)
	public void gatewayRefresh() {
		Toast.makeText(GatewayManegeActivity.this, "正在搜索主机...",
				Toast.LENGTH_LONG).show();


	}

	// 返回点击事件
	@OnClick(R.id.tv_head_back)
	public void back() {
		if(gatewaySaveFlag==true){
			Intent intent = new Intent(GatewayManegeActivity.this,SetActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right); 
			finish();
		}else{
			ToastUtils.showToast(GatewayManegeActivity.this, "请先点击完成，保存主机信息！", 2000);
		}
	
	}

	/***对比版本，同步到服务器 ***/
	@OnClick(R.id.tv_head_submit)
	public void systemExit() {
		ToastUtils.showToast(GatewayManegeActivity.this,"正在保存主机信息...", 1000);
		gatewaySaveFlag=true;
		checkVersionFromServer();  //从服务器对比版本信息
		
	}

	/*** 空间 ***/
	@OnCheckedChanged(R.id.rb_navi_space)
	public void spaceDeviceShow() {
		Intent intent = new Intent(GatewayManegeActivity.this,
				SpaceDevicesActivity.class);
		startActivity(intent);
		finish();
	}

	/*** 情景模式 ***/
	@OnCheckedChanged(R.id.rb_navi_scene)
	public void sceneMode() {
		Intent sceneIntent = new Intent(GatewayManegeActivity.this,
				SceneModelActivity.class);
		startActivity(sceneIntent);
		finish();
	}

	/*** 网络切换 ***/
	@OnClick(R.id.tv_bottom_network)
	public void networkSwitchClick() {

		netWorkSwitch(socketService, tvbttomNetwork);
	}

	/*** 防区管理 ***/
	@OnCheckedChanged(R.id.rb_navi_alert)
	public void DefenceAreaClick() {
		Intent sceneIntent = new Intent(GatewayManegeActivity.this,
				DefenceAreaActivity.class);
		startActivity(sceneIntent);
		finish();
	}

	/*** 系统设置 ***/
	@OnCheckedChanged(R.id.rb_navi_set)
	public void systemSet() {
		Intent intent = new Intent(GatewayManegeActivity.this,
				SetActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void initViews() {
		tvAddGateway = (TextView) findViewById(R.id.tv_addgateway);
		tvAddGateway.setOnClickListener(addGatewayOnClickListener);
		
		
		if (NetValue.netFlag == NetValue.INTRANET) { // socket认证通过则为内网
			// 初始化显示网络状态
			tvbttomNetwork.setText("本地"); // 任务栏显示网络状态
		} else if (NetValue.netFlag == NetValue.OUTERNET) {
			tvbttomNetwork.setText("远程"); // 任务栏显示网络状态

		}
	}

	@Override
	protected void initDatas() {
		String gatewayno=SystemValue.gatewayid;
		if (!VerifyUtils.isEmpty(gatewayno)) {
			Gateway mGateway = new GateWayDao(GatewayManegeActivity.this).getGatewayByGatewayNo(gatewayno);
			if (mGateway!=null) {
				gatewayList.clear();
				gatewayList.add(mGateway);
				
				// 异步进程更新界面
				Message msg = new Message();
				msg.what = 0x129;
				handler.sendMessage(msg);
			}
		
		}else {
			ToastUtils.showToast(GatewayManegeActivity.this, "请绑定主机！", 2000);
		}



	}

	// 添加新主机
	private OnClickListener addGatewayOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(gatewayList.size()>0){
				ToastUtils.showToast(GatewayManegeActivity.this, "网关已存在，请勿重复添加", SystemValue.MSG_TIME);
			}else{
				addGatewayDialog();
				
	    		if((!NetValue.IP_CONNECT_FLAG)){
	    			showLoadingDialog("正在搜索主机IP,请稍后...");
	    			NetTool ntNetTool=new NetTool(null);
	    			ntNetTool.scan();
	    			
	    			//采用回调刷新主机IP
	    			ntNetTool.callSocket(new IPCallBack() {
						
						@Override
						public void callBack(String string) {
							Message msg = new Message();
		    				msg.what = 0x008;
		    				handler.sendMessage(msg);
						}
					});
	    		}
	    		
	    
	    		new Handler().postDelayed(new Runnable(){    
	    		    public void run() {    
	    		    	Message msg = new Message();
	    				msg.what = 0x008;
	    				handler.sendMessage(msg);   
	    		    }    
	    		 }, 15000);   
			}
			
		}
	};



	@Override
	protected void onDestroy() {
		unbindService(conn);
		super.onDestroy(); // 注意先后
	}

	
	/**
	 * 从服务器对比版本信息
	 */
	private void checkVersionFromServer() {
		String phonenum=SystemValue.phonenum;
	    int versionType=SystemValue.VERSION_GATEWAY;
		pVersion = new VersionDao(GatewayManegeActivity.this)
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
							ToastUtils.showToast(GatewayManegeActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							
							try {
								Gson gson = new Gson();
								ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
								if (message != null) {
									if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
										System.out.println("检查版本"+ message.getMessageInfo());
										System.out.println("匹配结果"+ message.getObject());
										
										versionResult = Integer.valueOf((String)message.getObject());
									
										//后台在代码中根据版本比对结果，在本地和服务器之间同步
										if(versionResult==1){//提示从服务器同步到本地，刷新右边显示NEW！
											getGatewayFromServer();
										}else if(versionResult==2){//提示同步到服务器，刷新右边显示NEW！
											sysnGatewayToServer();
										}
										
									} else {
									    System.out.println("版本匹配比较");
									}
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						}
					});
	}
	
	/**
	 * 从服务器获取主机信息
	 */
	private void getGatewayFromServer() {
		String phonenum=SystemValue.phonenum;
		RequestParams params = new RequestParams();
		params.addBodyParameter("phoneNum",phonenum);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.GATEWAY_FROM_SERVER_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						initDatas();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
						if (message != null) {
							if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
								try {
									String jGatewayList=(String) message.getObject();
									
//									//更新版本的信息到本地数据库
//									String versionJson=message.getDescription();
//									Gson gVersion=new Gson();
//									Version version=gVersion.fromJson(versionJson,Version.class); 
//									new VersionDao(GatewayManegeActivity.this).addorUpdateVerson(version);
									
									System.out.println("网关返回的数据："+jGatewayList);
									
									
									if(!jGatewayList.equals("[]")){   //临时屏蔽20161106
										//更新数据库中的设备表
										gatewayList=WebPacketUtil.parseGatewaylistFromServer(jGatewayList);
									}else{
										System.out.println("网关列表为空"+jGatewayList);
									}							
								
									
									if(gatewayList.size()>0){
										Gateway gateway=gatewayList.get(0);									//将GatewayNo绑定到对应的user
										String phonenum=SystemValue.phonenum;
										String gatewayno=gateway.getGatewayNo();
										SystemValue.gatewayid=gatewayno;
										NetValue.LOCAL_IP = gateway.getGatewayIp();
										
//										gatewayAdpter.notifyDataSetChanged();
										
										// 异步进程更新界面
										Message msg = new Message();
										msg.what = 0x129;
										handler.sendMessage(msg);
										
										
										
										
										new UserDao(GatewayManegeActivity.this).updateGatewayNoByPhonenum(phonenum,gatewayno);
									}
								} catch (JsonSyntaxException e) {
									e.printStackTrace();
								}
								
							} else {
								ToastUtils.showToast(GatewayManegeActivity.this,message.getMessageInfo(), 1000);
							}
						}
					}
				});
	}
	
	/**
	 * 同步主机信息到服务器
	 */
	private void sysnGatewayToServer() {
		Gson gson = new Gson();
		String jGatewayList = gson.toJson(gatewayList);
		
		Gson gVersion = new Gson();
		String jVersion = gVersion.toJson(pVersion);
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("gatewayJson", jGatewayList);
		params.addBodyParameter("versionJson", jVersion);   //同步版本信息到服务器
		params.addBodyParameter("phonenum", SystemValue.phonenum);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.GATEWAY_TO_SERVER_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ToastUtils.showToast(GatewayManegeActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,
								ResultMessage.class);
						if (message != null) {
							if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
								//同步服务器成功，将这条记录插入到本地数据库version表中
//								new VersionDao(GatewayManegeActivity.this).addorUpdateVerson(pVersion);
							} else {
							}
						}
					}
				});
	}
	
	
	/**
	 * 同步主机信息到服务器
	 */
	private void unbindGatewayAndUser(Version version) {
		
		Gson gVersion = new Gson();
		String jVersion = gVersion.toJson(version);
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("versionJson", jVersion);   //同步版本信息到服务器
		params.addBodyParameter("phoneNum", SystemValue.phonenum);
		
		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.GATEWAY_UNBIND_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ToastUtils.showToast(GatewayManegeActivity.this, "请检查手机网络连接",SystemValue.MSG_TIME);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Gson gson = new Gson();
						ResultMessage message = gson.fromJson(arg0.result,
								ResultMessage.class);
						if (message != null) {
							if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
							} else {
							}
						}
					}
				});
	}

}
