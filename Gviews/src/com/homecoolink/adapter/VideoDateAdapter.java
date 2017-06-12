package com.homecoolink.adapter;

import com.homecoolink.R;
import com.homecoolink.activity.AlarmReordVideo;
import com.homecoolink.fragment.ImageFrag;
import com.homecoolink.global.FList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VideoDateAdapter extends BaseAdapter {

	private Context mcontext;
	private LayoutInflater mlif;
	private String[] datesStrings;
	ImageFrag ifFrag = null;
	AlarmReordVideo alarmv;
	View footerView = null;
	public View getFooterView() {
		return footerView;
	}

	public void setFooterView(View footerView) {
		this.footerView = footerView;
	}

	public VideoDateAdapter(Context context, String[] datesstrings,
			ImageFrag ifrag) {
		this.mcontext = context;
		this.mlif = LayoutInflater.from(mcontext);
		ifFrag = ifrag;
		datesStrings = datesstrings;
	}

	public VideoDateAdapter(Context context, String[] datesstrings,
			AlarmReordVideo alarm) {
		this.mcontext = context;
		this.mlif = LayoutInflater.from(mcontext);
		alarmv = alarm;
		datesStrings = datesstrings;
	}

	public void ChangeData(String[] datesstrings) {
		datesStrings = datesstrings;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (datesStrings.length>0) {
			if (getFooterView()!=null) {				
				getFooterView().setVisibility(View.VISIBLE);
			}
		}else if (datesStrings.length==0) {
			if (getFooterView()!=null) {				
				getFooterView().setVisibility(View.GONE);
			}
		}
		return datesStrings.length;
	}

	@Override
	public String getItem(int position) {
		return datesStrings[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (position >= FList.getInstance().size()) {
			return 0;
		} else {
			return 1;
		}

	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e("343", "====");

		final String dateString = datesStrings[position];
		View view = convertView;
		ViewHolder holder = null;
		if (null == view) {
			holder = new ViewHolder();
			view = mlif.inflate(R.layout.video_spinner_deviceitem, null);
			holder.setTName((TextView) view.findViewById(R.id.time_group_title));
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();

		}
		TextView tView = holder.getTName();
		tView.setText(dateString);
		tView.setCompoundDrawablePadding(0);
		tView.setCompoundDrawables(null, null, null, null);
		tView.setTextColor(mcontext.getResources().getColor(R.color.black));
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ifFrag != null) {
					ifFrag.SetSelectedDate(dateString);
				} else {
					alarmv.SetSelectedDate(dateString);
				}
			}
		});
		return view;
	}

	class ViewHolder {

		private TextView tName;

		public TextView getTName() {
			return tName;
		}

		public void setTName(TextView tName) {
			this.tName = tName;
		}
	}

	public String[] getDatesStrings() {
		return datesStrings;
	}

	public void setDatesStrings(String[] datesStrings) {
		this.datesStrings = datesStrings;
	}

	

}
