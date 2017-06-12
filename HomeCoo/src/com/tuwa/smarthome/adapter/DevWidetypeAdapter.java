package com.tuwa.smarthome.adapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.tuwa.smarthome.R;
import com.tuwa.smarthome.entity.DevWidetype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DevWidetypeAdapter extends BaseAdapter {

	private List<DevWidetype> devwideList;
	private LayoutInflater mInflater;
	
	public DevWidetypeAdapter(Context context, List<DevWidetype> vector) {
		this.devwideList = vector;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return devwideList.size();
	}

	@Override
	public Object getItem(int position) {
		return devwideList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view != null) {
		      holder = (ViewHolder) view.getTag();
		    } else {
		      view = mInflater.inflate(R.layout.item_devwidetype,parent,false);
		      holder = new ViewHolder(view);
		      view.setTag(holder);
		    }
		
		DevWidetype devWidetype=devwideList.get(position);
		holder.devwideImg.setImageResource(devWidetype.getImageId());
		holder.devwideTitle.setText(devWidetype.getTitle());
		
		return view;
	}

	static class ViewHolder {
		@Bind(R.id.img_devwide)  ImageView devwideImg;
		@Bind(R.id.tv_devtitle)  TextView devwideTitle;
		
		
	    public ViewHolder(View view) {
	    	ButterKnife.bind(this,view);
	    }
	  }
	
	
}
