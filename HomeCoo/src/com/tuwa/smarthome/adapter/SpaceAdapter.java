package com.tuwa.smarthome.adapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.tuwa.smarthome.R;
import com.tuwa.smarthome.entity.Space;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpaceAdapter  extends BaseAdapter {
	private List<Space> spaceList;
	private LayoutInflater mInflater;
	
	public SpaceAdapter(Context context, List<Space> vector) {
		this.spaceList = vector;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return spaceList.size();
	}

	@Override
	public Object getItem(int position) {
		return spaceList.get(position);
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
		      view = mInflater.inflate(R.layout.item_space,parent,false);
		      holder = new ViewHolder(view);
		      view.setTag(holder);
		    }
		
		Space space=spaceList.get(position);
		holder.tvSpacename.setText(space.getSpaceName());
		
		
	  
		
		return view;
	}

	static class ViewHolder {
		@Bind(R.id.tv_list_spacename)  TextView tvSpacename;
		@Bind(R.id.im_setting)  ImageView imSetting;
		
	    public ViewHolder(View view) {
	    	ButterKnife.bind(this,view);
	    }
	  }


	
	
}
