package com.homecoolink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.fragment.NetControlFrag;
import com.homecoolink.widget.NormalDialog;


public class WifiAdapter extends BaseAdapter{
	Context mContext;
	int iCurrentId;
	int iCount;
	int[] iType;
	int[] iStrength;
	String[] names;
	NetControlFrag ncf;
	
	
	public WifiAdapter(Context context,NetControlFrag ncf){
		this.mContext = context;
		this.iCount = 0;
		this.ncf = ncf;
	}
	
	public WifiAdapter(Context context,int iCount,int[] iType,int[] iStrength,String[] names){
		this.mContext = context;
		this.iCount = iCount;
		this.iType = iType;
		this.iStrength = iStrength;
		this.names = names;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return iCount;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = arg1;
		if(null==view){
			view = LayoutInflater.from(mContext).inflate(R.layout.list_wifi_item, null);
		}
		TextView name = (TextView) view.findViewById(R.id.name);
		ImageView wifi_strength = (ImageView) view.findViewById(R.id.wifi_strength);
		ImageView choose_img = (ImageView) view.findViewById(R.id.choose_img);
		ImageView wifi_type = (ImageView) view.findViewById(R.id.wifi_type);
		if(iType[position]!=2){
			wifi_type.setImageBitmap(null);
		}
		try{
			name.setText(names[position]);
		}catch(Exception e){
			name.setText("");
		}
		
		if(position==iCurrentId){
			choose_img.setVisibility(View.VISIBLE);
			LinearLayout top1 = (LinearLayout) view.findViewById(R.id.list_wifi_item_top1);
			LinearLayout top2 = (LinearLayout) view.findViewById(R.id.list_wifi_item_top2);
			top1.setVisibility(View.VISIBLE);
			top2.setVisibility(View.VISIBLE);
		}else{
			LinearLayout top1 = (LinearLayout) view.findViewById(R.id.list_wifi_item_top1);
			LinearLayout top2 = (LinearLayout) view.findViewById(R.id.list_wifi_item_top2);
			top1.setVisibility(View.GONE);
			top2.setVisibility(View.GONE);
			choose_img.setVisibility(View.GONE);
		}
		switch(iStrength[position]){
		case 0:
			wifi_strength.setImageResource(R.drawable.ic_strength1);
			break;
		case 1:
			wifi_strength.setImageResource(R.drawable.ic_strength2);
			break;
		case 2:
			wifi_strength.setImageResource(R.drawable.ic_strength3);
			break;
		case 3:
			wifi_strength.setImageResource(R.drawable.ic_strength4);
			break;
		case 4:
			wifi_strength.setImageResource(R.drawable.ic_strength5);
			break;
		}
		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				NormalDialog dialog = new NormalDialog(
						mContext,
						mContext.getResources().getString(R.string.warning),
						mContext.getResources().getString(R.string.modify_net_warning),
						mContext.getResources().getString(R.string.change),
						mContext.getResources().getString(R.string.cancel)
						);
				
				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
					
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						ncf.showModfyWifi(iType[position], names[position]);
					}
				});
				
				dialog.showNormalDialog();
				dialog.setCanceledOnTouchOutside(false);
			}
			
		});
		return view;
	}
	
	
	public void updateData (int iCurrentId,int iCount,int[] iType,int[] iStrength,String[] names){
		this.iCurrentId = iCurrentId;
		this.iCount = iCount;
		this.iType = iType;
		this.iStrength = iStrength;
		this.names = names;
		notifyDataSetChanged();
		
	}
}
