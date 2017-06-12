package com.tuwa.smarthome.activity;

import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.tuwa.smarthome.R.layout;
import com.tuwa.smarthome.dao.SpaceDao;
import com.tuwa.smarthome.dao.UserDao;
import com.tuwa.smarthome.dao.UserSpaceDevDao;
import com.tuwa.smarthome.entity.MenuSet;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.fragment.SystemUpdateFragment;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.SocketService;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;

public class SystemSetActivity extends BaseActivity implements OnClickListener {

	@Bind(R.id.tv_head_title)  TextView tvtitle;
	@Bind(R.id.tv_head_submit) TextView tvExit;
	
	private TextView tvBack;
	private ListView lvMenu;
	private List<MenuSet> menuList;
	private BaseDialog mLogoffUserDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_set);
		ButterKnife.bind(SystemSetActivity.this);
		
		
		initViews();  //初始化界面
		initDatas();  //初始化点击事件
		
		menuList=SystemValue.getMenuSetList();
		lvMenu.setAdapter(new MenuAdapter());
		
		initFragment();  //填充默认的第一个fragment
	}
	
	@Override
	protected void initViews() {

		tvtitle.setText("系统设置");
		tvExit.setVisibility(View.INVISIBLE);
		tvBack = (TextView) findViewById(R.id.tv_head_back);
		lvMenu = (ListView) findViewById(R.id.lv_lf_menu);
	}

	/**
	 * 填充设置页面的fragment
	 */
	public void initFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction bt = fm.beginTransaction();
		bt.replace(R.id.fl_set_content, new SystemUpdateFragment());
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
					initFragment();  //更新系统
					break;
				case 1:              //注销用户
					ShowLogoffUserDalog();
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
			View view = View.inflate(SystemSetActivity.this, R.layout.item_menu, null);
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
		mLogoffUserDialog = BaseDialog.getDialog(SystemSetActivity.this, "提示",
				"确认要注销账号吗？", "确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						logoffUserFromServer();
						
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
	 * 从服务器注销用户及配置信息
	 */
	private void logoffUserFromServer() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", SystemValue.phonenum);

		HttpUtils utils = new HttpUtils(SystemValue.TIMEOUT);
		utils.send(HttpMethod.POST, NetValue.CANCEL_USER_URL, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				initDatas();  //连不上服务器从本地加载
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Gson gson = new Gson();
				ResultMessage message = gson.fromJson(arg0.result,ResultMessage.class);
				if (message != null) {
					if (message.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
						ToastUtils.showToast(SystemSetActivity.this,message.getMessageInfo(), 1000);
					
						new UserDao(SystemSetActivity.this).deleteUserByPhoneno(SystemValue.phonenum);  //删除用户信息
						new UserSpaceDevDao(SystemSetActivity.this).deleteDevSpaceByPhoneno(SystemValue.phonenum); //删除设备用户配置信息
						new SpaceDao(SystemSetActivity.this).deleteSpaceByPhoneno(SystemValue.phonenum);//删除用户下的房间信息
						
						new Handler().postDelayed(new Runnable(){    
			    		    public void run() {    
		    		    	     Intent intent = new Intent();  //Itent就是我们要发送的内容
					             intent.setAction(SystemValue.ACTION_FINISH_ACTIVITY);  //销毁所有activity  
					             sendBroadcast(intent);   //发送广播	
			    		       }    
			            }, 2000); 
					 
						
					} else {
						ToastUtils.showToast(SystemSetActivity.this,message.getMessageInfo(), 1000);
					}
				}
			}
		});
	}

}
