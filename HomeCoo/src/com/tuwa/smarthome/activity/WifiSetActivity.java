package com.tuwa.smarthome.activity;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.BaseDialog;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.entity.MenuSet;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.fragment.WGToRouterFragment;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

public class WifiSetActivity extends BaseActivity implements OnClickListener {  

	@Bind(R.id.tv_head_title)  TextView tvtitle;
	@Bind(R.id.tv_head_submit) TextView tvExit;
	
	private TextView tvBack;
	private ListView lvMenu;
	private List<MenuSet> menuList;
	private BaseDialog mLogoffUserDialog;
	private SocketService devService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_set);
		ButterKnife.bind(WifiSetActivity.this);
		
		// Activity和service绑定2
		Intent service = new Intent(WifiSetActivity.this,SocketService.class);
		bindService(service, devconn, Context.BIND_AUTO_CREATE);
		
		initViews();  //初始化界面
		initDatas();  //初始化点击事件
		
		menuList=SystemValue.getWifiSetList();
		lvMenu.setAdapter(new MenuAdapter());
		
		WGToRouterFragmentShow(1);  //填充默认的第一个fragment
	}
	
	
	ServiceConnection devconn=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			devService = ((SocketService.SocketBinder) service).getService();
			
		}
	};
	
	@Override
	protected void initViews() {

		tvtitle.setText("无线设置");
		tvExit.setVisibility(View.INVISIBLE);
		tvBack = (TextView) findViewById(R.id.tv_head_back);
		lvMenu = (ListView) findViewById(R.id.lv_lf_menu);
	}

	/**
	 * 设置网关无线连到路由器
	 */
	public void WGToRouterFragmentShow(int wifiFlag) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction bt = fm.beginTransaction();
		
		WGToRouterFragment wgfragment=new WGToRouterFragment();
		Bundle bundle = new Bundle();
	    bundle.putInt("wifiFlag",wifiFlag);
	    wgfragment.setArguments(bundle);
	    
		bt.replace(R.id.fl_set_content, wgfragment);
		bt.commit();
	}



	@Override
	protected void initDatas() {
		tvBack.setOnClickListener(this);
        lvMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					//桥接网关到路由器
					WGToRouterFragmentShow(1);  
					break;
				case 1:   
					//设置网关无线
					WGToRouterFragmentShow(2);
					break;
				default:
					break;
				}
				
			}
		});
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_head_back:
			finish();
			break;

		default:
			break;
		}

	}
	
	class MenuAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return menuList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return menuList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(WifiSetActivity.this, R.layout.item_menu, null);
			ImageView imgMenu = (ImageView) view.findViewById(R.id.img_menu);
			TextView tvMenu = (TextView) view.findViewById(R.id.tv_menu_content);
			MenuSet menu=menuList.get(position);
			imgMenu.setImageResource(menu.getImageId());
			tvMenu.setText(menu.getTitle());
			return view;
		}
		
	}
	
	//注销用户确认对话框
	protected void ShowLogoffUserDalog() {
		// TODO Auto-generated method stub
		mLogoffUserDialog = BaseDialog.getDialog(WifiSetActivity.this, "提示",
				"确认要注销账号吗？", "确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
//						logoffUserFromServer();
						
						mLogoffUserDialog.dismiss();
					}
				}, "取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		mLogoffUserDialog.setButton1Background(R.drawable.btn_default_popsubmit);
		mLogoffUserDialog.show();
	}
	
	/**
	 * 无线桥接网关到路由
	 */
	public void linkRouterWifi(){
		devService.socketConnect(NetValue.LOCAL_IP);
		
		new Handler().postDelayed(new Runnable(){    
		    public void run() {  
		    	
				SocketPacket wifiPacket = WebPacketUtil
						   .getWifiSetPacket();
				devService.sentPacket(wifiPacket); // 发送请求所有设备状态
				System.out.println("桥接网关wifi。。。");
		    }    
		 }, 2000);  
		
		new Handler().postDelayed(new Runnable(){    
		    public void run() {    
			
				if(SystemValue.WIFI_SET_FLAG==true){
					ToastUtils.showToast(WifiSetActivity.this,"网关无线连接成功",1000);
				}else{
					ToastUtils.showToast(WifiSetActivity.this,"网关无线连接失败",1000);
				}  
		    }    
		 }, 1000);  
	}
	
	/**
	 * 设置网关无线网
	 */
	public void setGatewayWifi(){
		devService.socketConnect(NetValue.LOCAL_IP);
		
		new Handler().postDelayed(new Runnable(){    
		    public void run() {  
		    	
				SocketPacket wifiPacket = WebPacketUtil
						   .setGatewayWifiPacket();
				devService.sentPacket(wifiPacket); // 发送请求所有设备状态
				System.out.println("设置网关wifi。。。");
		    }    
		 }, 2000);  
	}
	
	@Override
	protected void onDestroy() {
		unbindService(devconn);
		super.onDestroy(); // 注意先后
	}

}
