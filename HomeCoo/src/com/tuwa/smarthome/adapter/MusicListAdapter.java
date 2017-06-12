package com.tuwa.smarthome.adapter;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.tuwa.smarthome.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MusicListAdapter extends BaseAdapter {
	//private int[] colors=new int[]{0x30FF0000, 0x300000FF};
	private int[] colors=new int[]{0x30BCDFE3,0x30E1EFEF};
	private List<Map<String, Object>> musicList;
	private LayoutInflater mInflater;
	
	public MusicListAdapter(Context context, List<Map<String, Object>> vector) {
		this.musicList = vector;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return musicList.size();
	}

	@Override
	public Object getItem(int position) {
		return musicList.get(position);
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
		      view = mInflater.inflate(R.layout.include_music,parent,false);
		      holder = new ViewHolder(view);
		      view.setTag(holder);
		    }
		
		Map<String, Object> map=musicList.get(position);
		holder.tvmusicname.setText((CharSequence) map.get("songName"));
		
		int colorPos = position % colors.length;  
		view.setBackgroundColor(colors[colorPos]);  
		return view;
	}

	static class ViewHolder {
		@Bind(R.id.tv_music_name)  TextView tvmusicname;
		
		
	    public ViewHolder(View view) {
	    	ButterKnife.bind(this,view);
	    }
	  }
	
	
}
