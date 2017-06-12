package com.tuwa.smarthome.fragment;

import com.tuwa.smarthome.R;
import com.tuwa.smarthome.activity.GatewayManegeActivity;
import com.tuwa.smarthome.activity.TimeTaskActivity;
import com.tuwa.smarthome.activity.WifiSetActivity;
import com.tuwa.smarthome.activity.DeviceManegeActivity.DeviceAdapter;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.util.DataConvertUtil;
import com.tuwa.smarthome.util.PreferencesUtils;
import com.tuwa.smarthome.util.ToastUtils;
import com.tuwa.smarthome.util.WebPacketUtil;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * @类名    HomeFragment
 * @创建者   ppa
 * @创建时间 2016-3-29
 * @描述   TODO
 */
public class WGToRouterFragment extends Fragment implements OnClickListener {
	public Activity mActivity;
	private EditText etGatewayNo,etGatewayPwd,etWifiName,etWifiPwd;
	private TextView tvWifiName,tvWifiPwd;
	private Button btnSubmit;
	private int wifiFlag=0;
	
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x001:
			    String sGatewayNo=etGatewayNo.getText().toString();
			    PreferencesUtils.putString(mActivity, "gatewayNo",sGatewayNo);
				break;
			case 0x002:
				String sGatewayPwd=etGatewayPwd.getText().toString();
			    PreferencesUtils.putString(mActivity, "gatewayPwd",sGatewayPwd);
				break;
			case 0x003:
				String sWifiName=etWifiName.getText().toString();
				if(wifiFlag==1){
					 PreferencesUtils.putString(mActivity, "rouWifiName",sWifiName);
				}else if(wifiFlag==2){
					 PreferencesUtils.putString(mActivity, "gatewayWifiName",sWifiName);
				}
				break;
			case 0x004:
				String sWifiPwd=etWifiPwd.getText().toString();
				if(wifiFlag==1){
					 PreferencesUtils.putString(mActivity, "rouWifiPwd",sWifiPwd);
				}else if(wifiFlag==2){
					 PreferencesUtils.putString(mActivity, "gatewayWifiPwd",sWifiPwd);
				}
				break;
			}
		}
	};
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mActivity =  getActivity();
    	
   
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 Bundle bundle = getArguments();
		 wifiFlag=bundle.getInt("wifiFlag");
		 
		View view = View.inflate(mActivity, R.layout.fragment_wifi_content, null);
		
		etGatewayNo=(EditText) view.findViewById(R.id.et_gatewayNo);
		etGatewayPwd=(EditText) view.findViewById(R.id.et_gatewayPwd);
		tvWifiName=(TextView) view.findViewById(R.id.tv_wifi_name);
		tvWifiPwd=(TextView) view.findViewById(R.id.tv_wifi_pwd);
		
		etWifiName = (EditText) view.findViewById(R.id.et_wifi_name);
		etWifiPwd = (EditText) view.findViewById(R.id.et_wifi_pwd);
		btnSubmit=(Button) view.findViewById(R.id.btn_wifi_submit);
		btnSubmit.setOnClickListener(this);
		
		initView();
		
		initData();
		
		
		return view;
	}
	
	
    private void initView() {
    	if(wifiFlag==1){
    		tvWifiName.setText("路由无线名称");
    		tvWifiPwd.setText("路由无线密码");
    		etWifiName.setHint("请输入路由无线名称");
    		etWifiPwd.setHint("请输入路由无线密码");
    	}else if(wifiFlag==2) {
    		tvWifiName.setText("网关无线名称");
    		tvWifiPwd.setText("网关无线密码");
    		etWifiName.setHint("请输入网关无线名称");
    		etWifiPwd.setHint("请输入网关无线密码");
    	}
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    	initData();
    }
  



	private void initData() {
	     String gatewayNo=PreferencesUtils.getString(mActivity, "gatewayNo");
	        etGatewayNo.setText(gatewayNo);
	     String gatewayPwd=PreferencesUtils.getString(mActivity, "gatewayPwd");
	        etGatewayPwd.setText(gatewayPwd);
		
	     if(wifiFlag==1){
	    	 String rouWifiName=PreferencesUtils.getString(mActivity, "rouWifiName");
	    	 etWifiName.setText(rouWifiName);
	    	 String rouWifiPwd=PreferencesUtils.getString(mActivity, "rouWifiPwd");
	    	 etWifiPwd.setText(rouWifiPwd);
	     }else if(wifiFlag==2){
	    	 String rouWifiName=PreferencesUtils.getString(mActivity, "gatewayWifiName");
	    	 etWifiName.setText(rouWifiName);
	    	 String rouWifiPwd=PreferencesUtils.getString(mActivity, "gatewayWifiPwd");
	    	 etWifiPwd.setText(rouWifiPwd);
	     }    
		
		etGatewayNo.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			    Message msg = new Message();  
	            msg.what = 0x001;  
	            mHandler.sendMessageDelayed(msg,1000);  
				
			}
		});
		
		etGatewayPwd.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				 Message msg = new Message();  
	             msg.what = 0x002;  
	             mHandler.sendMessageDelayed(msg,1000); 
			} 
		});
		
		etWifiName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				 Message msg = new Message();  
	             msg.what = 0x003;  
	             mHandler.sendMessageDelayed(msg,1000); 
			}
		});
		
		etWifiPwd.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					 Message msg = new Message();  
		             msg.what = 0x004;  
		             mHandler.sendMessageDelayed(msg,1000); 
				}
			});
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_wifi_submit:
			
			sendWifiSetToGateway();
		
			break;
		
		default:
			break;
		}
		
	}

	private void sendWifiSetToGateway() {
		NetValue.LOCAL_IP = "192.168.7.1";
		String gatewayNo= etGatewayNo.getText().toString();
		String gatewayPwd=etGatewayPwd.getText().toString();	
		
		if (gatewayNo.length() !=8) {
			ToastUtils.showToast(mActivity,"主机号格式错误", 1000);
		}else if(gatewayPwd.length()!=8){
			ToastUtils.showToast(mActivity,"主机密码格式错误", 1000);
		}else{
			NetValue.authdata = gatewayPwd;	
			byte[] bGateway=gatewayNo.getBytes();
			SystemValue.gatewayid=DataConvertUtil.toHexUpString(bGateway);
			
			String wifiName=etWifiName.getText().toString();
			String wifiPwd=etWifiPwd.getText().toString();
			
			SystemValue.SSID_NAME=wifiName;
			SystemValue.SSID_PWD=wifiPwd;
			
			if(wifiFlag==1){
				((WifiSetActivity) getActivity()).linkRouterWifi();
				
			}else if(wifiFlag==2){
				((WifiSetActivity) getActivity()).setGatewayWifi();
			}
		}
		
	}
	
	
	
	
    
}
