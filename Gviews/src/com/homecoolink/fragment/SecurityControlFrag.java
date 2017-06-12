package com.homecoolink.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.homecoolink.R;
import com.homecoolink.activity.MainControlActivity;
import com.homecoolink.activity.ModifyNpcPasswordActivity;
import com.homecoolink.activity.ModifyNpcVisitorPasswordActivity;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;


public class SecurityControlFrag extends BaseFragment implements OnClickListener{
	private Context mContext;
	private Contact contact;
	private boolean isRegFilter = false;
	RelativeLayout change_password,change_super_password,automatic_upgrade;
	ImageView img_automatic_upgrade;
	ProgressBar progressBar_automatic_upgrade;
	boolean isOpenAutomaticUpgrade;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = MainControlActivity.mContext;
		contact = (Contact) getArguments().getSerializable("contact");
		View view = inflater.inflate(R.layout.fragment_security_control, container, false);
		initComponent(view);
		regFilter();
		P2PHandler.getInstance().getNpcSettings(contact.contactId, contact.contactPassword);
		return view;
	}
	
	
	public void initComponent(View view){
		change_password = (RelativeLayout) view.findViewById(R.id.change_password);
		change_super_password=(RelativeLayout)view.findViewById(R.id.change_super_password);
		automatic_upgrade=(RelativeLayout)view.findViewById(R.id.automatic_upgrade);
		img_automatic_upgrade=(ImageView)view.findViewById(R.id.img_automatic_upgrade);
		progressBar_automatic_upgrade=(ProgressBar)view.findViewById(R.id.progressBar_automatic_upgrade);
		change_password.setOnClickListener(this);
		change_super_password.setOnClickListener(this);
		automatic_upgrade.setOnClickListener(this);
		if(contact.contactType==P2PValue.DeviceType.IPC){
			change_super_password.setVisibility(View.VISIBLE);
		}
	}
	
	public void regFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.REFRESH_CONTANTS);
		filter.addAction(Constants.P2P.ACK_RET_SET_AUTOMATIC_UPGRADE);
//		filter.addAction(Constants.P2P.RET_GET_AUTOMATIC_UPGRAD);
		
		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getAction().equals(Constants.Action.REFRESH_CONTANTS)){
				contact  = (Contact) intent.getSerializableExtra("contact");
			}else if(intent.getAction().equals(Constants.P2P.RET_GET_AUTOMATIC_UPGRAD)){
				int state=intent.getIntExtra("state", -1);
				if(state==Constants.P2P_SET.AUTOMATIC_UPGRADE.AUTOMATIC_UPGRADE_ON){
					automatic_upgrade.setVisibility(View.VISIBLE);
					isOpenAutomaticUpgrade=false;
					img_automatic_upgrade.setBackgroundResource(R.drawable.ic_checkbox_on);
				}else if(state==Constants.P2P_SET.AUTOMATIC_UPGRADE.AUTOMATIC_UPGRADE_OFF){
					automatic_upgrade.setVisibility(View.VISIBLE);
					isOpenAutomaticUpgrade=true;
			        img_automatic_upgrade.setBackgroundResource(R.drawable.ic_checkbox_off);
				}
				showImagview_automatic_upgrade();
			}else if(intent.getAction().equals(Constants.P2P.ACK_RET_SET_AUTOMATIC_UPGRADE)){
				int state=intent.getIntExtra("state",-1);
				if(state==Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR){
					if(isOpenAutomaticUpgrade==true){
						P2PHandler.getInstance().setAutomaticUpgrade(contact.contactId,contact.contactPassword, 1);
					}else{
						P2PHandler.getInstance().setAutomaticUpgrade(contact.contactId,contact.contactPassword, 0);
					}
					showImagview_automatic_upgrade();
				}else if(state==Constants.P2P_SET.ACK_RESULT.ACK_SUCCESS){
					if(isOpenAutomaticUpgrade==true){
						isOpenAutomaticUpgrade=false;
						img_automatic_upgrade.setBackgroundResource(R.drawable.ic_checkbox_on);
					}else{
						isOpenAutomaticUpgrade=true;
						img_automatic_upgrade.setBackgroundResource(R.drawable.ic_checkbox_off);
					}
					showImagview_automatic_upgrade();
				}
			}
		}
	};
	
	@Override
	public void onClick(View view) {
		int id=view.getId();
		if(id==R.id.change_password){
			Intent modify_password = new Intent(mContext,ModifyNpcPasswordActivity.class);
			modify_password.putExtra("contact", contact);
			mContext.startActivity(modify_password);
		}else if(id==R.id.automatic_upgrade){
			if(isOpenAutomaticUpgrade==true){
				P2PHandler.getInstance().setAutomaticUpgrade(contact.contactId,contact.contactPassword,1);
			}else{
				P2PHandler.getInstance().setAutomaticUpgrade(contact.contactId,contact.contactPassword,0);
			}
		}else if(id== R.id.change_super_password){
			Intent modify_visitor_password=new Intent(mContext, ModifyNpcVisitorPasswordActivity.class);
			modify_visitor_password.putExtra("contact", contact);
			mContext.startActivity(modify_visitor_password);
		}
		
		
		// TODO Auto-generated method stub
//		switch(view.getId()){
//		case R.id.change_password:
//			Intent modify_password = new Intent(mContext,ModifyNpcPasswordActivity.class);
//			modify_password.putExtra("contact", contact);
//			mContext.startActivity(modify_password);
//			break;
//		case R.id.automatic_upgrade:
//			if(isOpenAutomaticUpgrade==true){
//				P2PHandler.getInstance().setAutomaticUpgrade(contact.contactId,contact.contactPassword,1);
//			}else{
//				P2PHandler.getInstance().setAutomaticUpgrade(contact.contactId,contact.contactPassword,0);
//			}
//			break;
//		case R.id.change_super_password:
//			Intent modify_visitor_password=new Intent(mContext, ModifyNpcVisitorPasswordActivity.class);
//			modify_visitor_password.putExtra("contact", contact);
//			mContext.startActivity(modify_visitor_password);
//			break;
//		}
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if(isRegFilter){
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent it=new Intent();
		it.setAction(Constants.Action.CONTROL_BACK);
		mContext.sendBroadcast(it);
	}
   public void showProgress_automatic_upgrade(){
	   progressBar_automatic_upgrade.setVisibility(View.VISIBLE);
	   img_automatic_upgrade.setVisibility(View.GONE);
   }
   public void showImagview_automatic_upgrade(){
	   progressBar_automatic_upgrade.setVisibility(View.GONE);
	   img_automatic_upgrade.setVisibility(View.VISIBLE);
   }
}
