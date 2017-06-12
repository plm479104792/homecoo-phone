package com.homecoolink.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.data.Contact;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.p2p.core.P2PValue;


public class AddContactActivity extends BaseActivity implements OnClickListener{
	private TextView mNext;
	private ImageView mBack;
	Context mContext;
	EditText contactId;
	Contact mContact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		mContact = (Contact) getIntent().getSerializableExtra("contact");
		mContext = this;
		initCompent();	
	}

	public void initCompent() {
		contactId = (EditText) findViewById(R.id.contactId);
		mBack=(ImageView)findViewById(R.id.back_btn);
		mNext=(TextView)findViewById(R.id.next);
		
		if(null!=mContact){
			contactId.setText(mContact.contactId);
		}
		
		mBack.setOnClickListener(this);
		mNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.back_btn){
			this.finish();
		}else if(id==R.id.next){
			next();
		}
		
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.back_btn:
//			this.finish();
//			break;
//		case R.id.next:
//			next();
//			break;
//		default:
//			break;
//		}
	}
	
	public void next(){
		String input_id = contactId.getText().toString();
		
		if(input_id!=null&&input_id.trim().equals("")){
			T.showShort(mContext,  R.string.input_contact_id);
			return;
		}
		
		if(input_id.charAt(0)=='0'){
			T.showShort(mContext,  R.string.robot_id_not_first_with_zero);
			return;
		}
		
		if(input_id.length()>9){
			T.showShort(mContext,  R.string.contact_id_too_long);
			return;
		}
		
		if(!Utils.isNumeric(input_id)){
			T.showShort(mContext,  R.string.contact_id_must_digit);
			return;
		}
		
		if(null!=FList.getInstance().isContact(input_id)){
			T.showShort(mContext,  R.string.contact_already_exist);
			return;
		}
		
		int type;
		if(input_id.charAt(0)=='0'){
			type = P2PValue.DeviceType.PHONE;
		}else{
			type = P2PValue.DeviceType.UNKNOWN;
		}
		
		Contact saveContact = new Contact();
		saveContact.contactId = input_id;
		saveContact.contactType = type;
		saveContact.activeUser = NpcCommon.mThreeNum;
		saveContact.messageCount = 0;
		Intent add_next = new Intent(mContext,AddContactNextActivity.class);
		add_next.putExtra("contact", saveContact);
		mContext.startActivity(add_next);
		finish();
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_ADDCONTACTACTIVITY;
	}
	
}
